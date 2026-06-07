<template>
  <view class="order-detail-page">
    <!-- 订单状态 -->
    <view class="status-card">
      <text class="status-icon">{{ getStatusIcon(order.status) }}</text>
      <text class="status-text">{{ getStatusText(order.status) }}</text>
      <text class="status-desc">{{ getStatusDesc(order.status) }}</text>
      <!-- 待支付倒计时 -->
      <view class="countdown-wrap" v-if="order.status === 'PENDING'">
        <text class="countdown-label">支付剩余时间</text>
        <text class="countdown-clock" :class="{ urgent: countdown <= 300 }">
          {{ formatCountdown(countdown) }}
        </text>
      </view>
    </view>
    
    <!-- 商品信息 -->
    <view class="product-card card" @click="goProductDetail">
      <image class="product-image" :src="getImageUrl(order.productImage)" mode="aspectFill" />
      <view class="product-info">
        <text class="product-name text-ellipsis-2">{{ order.productName }}</text>
        <text class="product-price">¥{{ order.price }}</text>
      </view>
    </view>
    
    <!-- 收货信息 -->
    <view class="address-card card" v-if="order.shippingAddress">
      <text class="card-title">收货信息</text>
      <view class="address-content">
        <text class="address-name">{{ order.shippingName }} {{ order.shippingPhone }}</text>
        <text class="address-detail">{{ order.shippingAddress }}</text>
      </view>
    </view>
    
    <!-- 物流时间线 -->
    <view class="tracking-card card">
      <text class="card-title">物流进度</text>
      <view class="timeline">
        <view
          class="timeline-item"
          v-for="step in timelineSteps"
          :key="step.key"
          :class="{ active: step.active, done: step.done }"
        >
          <view class="tl-dot"></view>
          <view class="tl-line" v-if="step.key !== 'completed'"></view>
          <view class="tl-content">
            <text class="tl-title">{{ step.title }}</text>
            <text class="tl-time" v-if="step.time">{{ step.time }}</text>
            <text class="tl-sub" v-if="step.sub">{{ step.sub }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 订单信息 -->
    <view class="info-card card">
      <text class="card-title">订单信息</text>
      <view class="info-row">
        <text class="info-label">订单编号</text>
        <text class="info-value">{{ order.orderNo }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">创建时间</text>
        <text class="info-value">{{ order.createdAt }}</text>
      </view>
      <view class="info-row" v-if="order.paymentTime">
        <text class="info-label">支付时间</text>
        <text class="info-value">{{ order.paymentTime }}</text>
      </view>
      <view class="info-row" v-if="order.shipTime">
        <text class="info-label">发货时间</text>
        <text class="info-value">{{ order.shipTime }}</text>
      </view>
      <view class="info-row" v-if="order.receiveTime">
        <text class="info-label">收货时间</text>
        <text class="info-value">{{ order.receiveTime }}</text>
      </view>
    </view>
    
    <!-- 买卖双方信息 -->
    <view class="user-card card">
      <view class="user-row">
        <text class="user-label">卖家</text>
        <view class="user-info" @click="contactSeller">
          <image class="user-avatar" :src="getImageUrl(order.seller?.avatar)" />
          <text class="user-name">{{ order.seller?.nickname || order.seller?.username }}</text>
        </view>
      </view>
      <view class="user-row">
        <text class="user-label">买家</text>
        <view class="user-info">
          <image class="user-avatar" :src="getImageUrl(order.buyer?.avatar)" />
          <text class="user-name">{{ order.buyer?.nickname || order.buyer?.username }}</text>
        </view>
      </view>
    </view>
    
    <!-- 评价被拒绝提示卡片 -->
    <view class="review-rejected-card card" v-if="isReviewRejected">
      <text class="card-title">⚠️ 评价审核未通过</text>
      <text class="rejected-reason">{{ orderReview.auditRemark || '请补充更具体的评价内容' }}</text>
      <text class="rejected-tip">你可以修改评价内容后重新提交审核</text>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar" v-if="showBottomBar">
      <button 
        class="action-btn" 
        v-if="order.status === 'PENDING' && isBuyer"
        @click="cancelOrder"
      >
        取消订单
      </button>
      <button 
        class="action-btn primary" 
        v-if="order.status === 'PENDING' && isBuyer"
        @click="payOrder"
      >
        去支付
      </button>
      <button 
        class="action-btn primary" 
        v-if="order.status === 'PAID' && isSeller"
        @click="shipOrder"
      >
        发货
      </button>
      <button 
        class="action-btn primary" 
        v-if="order.status === 'SHIPPED' && isBuyer"
        @click="confirmReceive"
      >
        确认收货
      </button>
      <!-- 未评价：去评价 -->
      <button
        class="action-btn primary"
        v-if="order.status === 'COMPLETED' && isBuyer && !isReviewed"
        @click="goReview"
      >
        去评价
      </button>
      <!-- 评价待审核中 -->
      <button
        class="action-btn"
        v-if="order.status === 'COMPLETED' && isBuyer && isReviewPending"
        disabled
      >
        审核中
      </button>
      <!-- 评价被拒绝：可修改重提 -->
      <button
        class="action-btn primary"
        v-if="order.status === 'COMPLETED' && isBuyer && isReviewRejected"
        @click="goEditReview"
      >
        修改评价
      </button>
      <button 
        class="action-btn" 
        @click="contactOther"
      >
        联系{{ isBuyer ? '卖家' : '买家' }}
      </button>
    </view>
  </view>
</template>

<script>
import { orderApi, reviewApi } from '../../api/index.js'
import { getImageUrl } from '../../utils/image.js'

const SHIP_COMPANIES = ['顺丰速运', '圆通快递', '中通快递', '韵达快递', '申通快递', '邮政EMS']

export default {
  data() {
    return {
      orderId: null,
      order: {},
      currentUserId: null,
      orderReview: null,
      selectedCompanyIndex: 0,
      countdown: 0,   // 剩余秒数
      _timer: null
    }
  },
  computed: {
    isBuyer() {
      return this.order.buyer?.id === this.currentUserId
    },
    isSeller() {
      return this.order.seller?.id === this.currentUserId
    },
    isReviewed() {
      return this.orderReview !== null
    },
    isReviewRejected() {
      return this.orderReview?.auditStatus === 2
    },
    isReviewPending() {
      return this.orderReview?.auditStatus === 0
    },
    showBottomBar() {
      return ['PENDING', 'PAID', 'SHIPPED', 'COMPLETED'].includes(this.order.status)
    },
    timelineSteps() {
      const statusOrder = ['PENDING', 'PAID', 'SHIPPED', 'COMPLETED']
      const currentIdx = statusOrder.indexOf(this.order.status)
      return [
        {
          key: 'pending',
          title: '下单',
          time: this.order.createdAt,
          sub: '',
          done: currentIdx > 0,
          active: currentIdx === 0
        },
        {
          key: 'paid',
          title: '已付款',
          time: this.order.paymentTime,
          sub: '',
          done: currentIdx > 1,
          active: currentIdx === 1
        },
        {
          key: 'shipped',
          title: '已发货',
          time: this.order.shipTime,
          sub: this.order.trackingNumber
            ? `${this.order.trackingCompany || ''} ${this.order.trackingNumber}`
            : '',
          done: currentIdx > 2,
          active: currentIdx === 2
        },
        {
          key: 'completed',
          title: '已收货',
          time: this.order.receiveTime,
          sub: '',
          done: currentIdx > 3,
          active: currentIdx === 3
        }
      ]
    }
  },
  onLoad(options) {
    this.orderId = options.id
    const userInfo = uni.getStorageSync('userInfo')
    this.currentUserId = userInfo?.id
    this.loadOrderDetail()
  },
  onShow() {
    if (this.order.status === 'PENDING') this.startTimer()
  },
  onHide() {
    this.stopTimer()
  },
  onUnload() {
    this.stopTimer()
  },
  methods: {
    getImageUrl,

    async loadOrderDetail() {
      try {
        const res = await orderApi.getOrderDetail(this.orderId)
        this.order = res.data
        if (this.order.status === 'COMPLETED') {
          this.loadOrderReview()
        }
        if (this.order.status === 'PENDING') {
          this.countdown = this.calcRemaining(this.order.createdAt)
          this.startTimer()
        }
      } catch (e) {
        console.error('加载订单详情失败', e)
      }
    },

    calcRemaining(createdAt) {
      const created = new Date(createdAt.replace(' ', 'T'))
      const expireAt = created.getTime() + 30 * 60 * 1000
      return Math.max(0, Math.floor((expireAt - Date.now()) / 1000))
    },

    formatCountdown(seconds) {
      if (seconds <= 0) return '00:00'
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
    },

    startTimer() {
      this.stopTimer()
      if (this.countdown <= 0) return
      this._timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          this.stopTimer()
          // 倒计时归零，刷新订单状态
          setTimeout(() => this.loadOrderDetail(), 1000)
        }
      }, 1000)
    },

    stopTimer() {
      if (this._timer) {
        clearInterval(this._timer)
        this._timer = null
      }
    },

    async loadOrderReview() {
      try {
        const res = await reviewApi.getReviewByOrderId(this.orderId)
        this.orderReview = res.data || null
      } catch (e) {
        this.orderReview = null
      }
    },
    
    getStatusText(status) {
      const map = {
        PENDING: '待付款',
        PAID: '待发货',
        SHIPPED: '待收货',
        COMPLETED: '交易完成',
        CANCELLED: '已取消'
      }
      return map[status] || status
    },
    
    getStatusIcon(status) {
      const map = {
        PENDING: '&#x1F4B3;',
        PAID: '&#x1F4E6;',
        SHIPPED: '&#x1F69A;',
        COMPLETED: '&#x2705;',
        CANCELLED: '&#x274C;'
      }
      return map[status] || ''
    },
    
    getStatusDesc(status) {
      const map = {
        PENDING: '请尽快完成支付',
        PAID: '等待卖家发货',
        SHIPPED: '商品已发出，请注意查收',
        COMPLETED: '感谢您的购买',
        CANCELLED: '订单已取消'
      }
      return map[status] || ''
    },
    
    goProductDetail() {
      uni.navigateTo({
        url: `/pages/detail/detail?id=${this.order.productId}`
      })
    },
    
    async payOrder() {
      uni.showModal({
        title: '确认支付',
        content: `确定支付 ¥${this.order.price} 吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.payOrder(this.orderId)
              uni.showToast({ title: '支付成功', icon: 'success' })
              this.loadOrderDetail()
            } catch (e) {
              console.error('支付失败', e)
            }
          }
        }
      })
    },
    
    async cancelOrder() {
      uni.showModal({
        title: '取消订单',
        content: '确定要取消此订单吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.cancelOrder(this.orderId)
              uni.showToast({ title: '订单已取消', icon: 'success' })
              this.loadOrderDetail()
            } catch (e) {
              console.error('取消失败', e)
            }
          }
        }
      })
    },
    
    shipOrder() {
      uni.showActionSheet({
        title: '选择快递公司',
        itemList: SHIP_COMPANIES,
        success: async (res) => {
          const company = SHIP_COMPANIES[res.tapIndex]
          try {
            await orderApi.shipOrder(this.orderId, { trackingCompany: company })
            uni.showToast({ title: '发货成功', icon: 'success' })
            this.loadOrderDetail()
          } catch (e) {
            uni.showToast({ title: e.message || '发货失败', icon: 'none' })
          }
        }
      })
    },
    
    async confirmReceive() {
      uni.showModal({
        title: '确认收货',
        content: '确定已收到商品吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.confirmReceive(this.orderId)
              uni.showToast({ title: '收货成功', icon: 'success' })
              this.loadOrderDetail()
            } catch (e) {
              console.error('确认收货失败', e)
            }
          }
        }
      })
    },
    
    goReview() {
      uni.navigateTo({
        url: `/pages/review/review?orderId=${this.orderId}&productId=${this.order.productId}&productName=${encodeURIComponent(this.order.productName)}&productImage=${encodeURIComponent(this.order.productImage || '')}`
      })
    },

    goEditReview() {
      const review = this.orderReview
      uni.navigateTo({
        url: `/pages/review/review?reviewId=${review.id}&orderId=${this.orderId}&productId=${this.order.productId}&productName=${encodeURIComponent(this.order.productName)}&productImage=${encodeURIComponent(this.order.productImage || '')}&auditRemark=${encodeURIComponent(review.auditRemark || '')}`
      })
    },
    
    contactOther() {
      const userId = this.isBuyer ? this.order.seller?.id : this.order.buyer?.id
      uni.navigateTo({
        url: `/pages/chat/chat?userId=${userId}&productId=${this.order.productId}`
      })
    },
    
    contactSeller() {
      uni.navigateTo({
        url: `/pages/chat/chat?userId=${this.order.seller?.id}&productId=${this.order.productId}`
      })
    }
  }
}
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

/* 状态卡片 */
.status-card {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8f5a 100%);
  padding: 60rpx 30rpx;
  text-align: center;
}

.status-icon {
  font-size: 80rpx;
  display: block;
  margin-bottom: 16rpx;
}

.status-text {
  font-size: 36rpx;
  color: #ffffff;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.status-desc {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 待支付倒计时 */
.countdown-wrap {
  margin-top: 24rpx;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 40rpx;
  padding: 12rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.countdown-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
}
.countdown-clock {
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  font-variant-numeric: tabular-nums;
  letter-spacing: 4rpx;
}
.countdown-clock.urgent {
  color: #ffe082;
}

/* 通用卡片 */
.card {
  background-color: #ffffff;
  margin: 20rpx;
  padding: 24rpx;
  border-radius: 16rpx;
}

.card-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 20rpx;
  display: block;
}

/* 商品卡片 */
.product-card {
  display: flex;
  align-items: center;
}

.product-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  margin-left: 20rpx;
}

.product-name {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.4;
  margin-bottom: 16rpx;
  display: block;
}

.product-price {
  font-size: 36rpx;
  color: #ff6b35;
  font-weight: bold;
}

/* 地址卡片 */
.address-content {
  padding-top: 10rpx;
}

.address-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.address-detail {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
}

/* 物流卡片 */
.tracking-content text {
  font-size: 26rpx;
  color: #666666;
  display: block;
  margin-bottom: 10rpx;
}

/* 物流时间线 */
.timeline {
  padding-top: 10rpx;
}
.timeline-item {
  display: flex;
  position: relative;
  padding-bottom: 32rpx;
}
.timeline-item:last-child {
  padding-bottom: 0;
}
.tl-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  background: #e0e0e0;
  flex-shrink: 0;
  margin-top: 4rpx;
  z-index: 1;
}
.timeline-item.active .tl-dot {
  background: #ff6b35;
  box-shadow: 0 0 0 6rpx rgba(255,107,53,0.2);
}
.timeline-item.done .tl-dot {
  background: #4caf50;
}
.tl-line {
  position: absolute;
  left: 11rpx;
  top: 28rpx;
  bottom: -4rpx;
  width: 2rpx;
  background: #e0e0e0;
}
.timeline-item.done .tl-line {
  background: #4caf50;
}
.tl-content {
  margin-left: 20rpx;
  flex: 1;
}
.tl-title {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  display: block;
}
.timeline-item.active .tl-title {
  color: #ff6b35;
  font-weight: bold;
}
.timeline-item.done .tl-title {
  color: #4caf50;
}
.tl-time {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 6rpx;
}
.tl-sub {
  font-size: 22rpx;
  color: #666;
  display: block;
  margin-top: 4rpx;
}

/* 信息卡片 */
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 26rpx;
  color: #999999;
}

.info-value {
  font-size: 26rpx;
  color: #333333;
}

/* 用户卡片 */
.user-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}

.user-label {
  font-size: 26rpx;
  color: #999999;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  margin-right: 12rpx;
}

.user-name {
  font-size: 26rpx;
  color: #333333;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
  padding: 20rpx;
  background-color: #ffffff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.action-btn {
  padding: 20rpx 40rpx;
  font-size: 28rpx;
  border-radius: 40rpx;
  background-color: #f5f5f5;
  color: #666666;
  border: none;
}

.action-btn.primary {
  background-color: #ff6b35;
  color: #ffffff;
}

.action-btn[disabled] {
  background-color: #ffe0cc;
  color: #ff9966;
}

/* 评价被拒绝提示卡片 */
.review-rejected-card {
  border-left: 6rpx solid #ff6b35;
}

.rejected-reason {
  font-size: 26rpx;
  color: #c0392b;
  display: block;
  margin-bottom: 12rpx;
  line-height: 1.5;
}

.rejected-tip {
  font-size: 24rpx;
  color: #999;
  display: block;
}
</style>
