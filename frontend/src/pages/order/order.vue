<template>
  <view class="order-page">
    <!-- 订单类型切换 -->
    <view class="type-tabs" v-if="showTypeTabs">
      <view class="type-tab" :class="{ active: orderType === 'buy' }" @click="switchType('buy')">
        <text>我买到的</text>
      </view>
      <view class="type-tab" :class="{ active: orderType === 'sell' }" @click="switchType('sell')">
        <text>我卖出的</text>
      </view>
    </view>
    
    <!-- 状态筛选 -->
    <scroll-view scroll-x class="status-tabs">
      <view 
        class="status-tab" 
        :class="{ active: currentStatus === '' }" 
        @click="filterByStatus('')"
      >
        <text>全部</text>
      </view>
      <view 
        class="status-tab" 
        :class="{ active: currentStatus === 'PENDING' }" 
        @click="filterByStatus('PENDING')"
      >
        <text>待付款</text>
      </view>
      <view 
        class="status-tab" 
        :class="{ active: currentStatus === 'PAID' }" 
        @click="filterByStatus('PAID')"
        v-if="orderType === 'sell'"
      >
        <text>待发货</text>
      </view>
      <view 
        class="status-tab" 
        :class="{ active: currentStatus === 'SHIPPED' }" 
        @click="filterByStatus('SHIPPED')"
      >
        <text>待收货</text>
      </view>
      <view 
        class="status-tab" 
        :class="{ active: currentStatus === 'COMPLETED' }" 
        @click="filterByStatus('COMPLETED')"
      >
        <text>已完成</text>
      </view>
    </scroll-view>
    
    <!-- 订单列表 -->
    <view class="order-list">
      <view class="order-item" v-for="order in orders" :key="order.id" @click="goOrderDetail(order.id)">
        <view class="order-header">
          <text class="order-no">订单号：{{ order.orderNo }}</text>
          <text class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</text>
        </view>
        <!-- 待支付倒计时 -->
        <view class="countdown-bar" v-if="order.status === 'PENDING' && countdowns[order.id] !== undefined">
          <text class="countdown-label">剩余支付时间</text>
          <text class="countdown-time" :class="{ urgent: countdowns[order.id] <= 300 }">
            {{ formatCountdown(countdowns[order.id]) }}
          </text>
        </view>
        <view class="order-content">
          <image class="product-image" :src="getImageUrl(order.productImage)" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name text-ellipsis-2">{{ order.productName }}</text>
            <text class="product-price">¥{{ order.price }}</text>
          </view>
        </view>
        <view class="order-footer">
          <view class="user-info">
            <text v-if="orderType === 'buy'">卖家：{{ order.seller?.nickname || order.seller?.username }}</text>
            <text v-else>买家：{{ order.buyer?.nickname || order.buyer?.username }}</text>
          </view>
          <view class="order-actions">
            <button 
              class="action-btn primary" 
              v-if="order.status === 'PENDING' && orderType === 'buy'"
              @click.stop="payOrder(order.id)"
            >
              去支付
            </button>
            <button 
              class="action-btn" 
              v-if="order.status === 'PENDING'"
              @click.stop="cancelOrder(order.id)"
            >
              取消订单
            </button>
            <button 
              class="action-btn primary" 
              v-if="order.status === 'PAID' && orderType === 'sell'"
              @click.stop="shipOrder(order.id)"
            >
              发货
            </button>
            <button 
              class="action-btn primary" 
              v-if="order.status === 'SHIPPED' && orderType === 'buy'"
              @click.stop="confirmReceive(order.id)"
            >
              确认收货
            </button>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="empty-tip" v-if="orders.length === 0 && !loading">
      <text>暂无订单</text>
    </view>
    
    <!-- 加载状态 -->
    <view class="loading-status" v-if="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script>
import { orderApi } from '../../api/index.js'
import { getImageUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      orderType: 'buy',
      currentStatus: '',
      orders: [],
      page: 0,
      loading: false,
      hasMore: true,
      showTypeTabs: true,
      countdowns: {},  // { orderId: remainingSeconds }
      _timer: null
    }
  },
  onLoad(options) {
    if (options.type) {
      this.orderType = options.type
      this.showTypeTabs = false
    }
    if (options.status) {
      this.currentStatus = options.status
    }
    this.loadOrders()
  },
  onShow() {
    this.startTimer()
  },
  onHide() {
    this.stopTimer()
  },
  onUnload() {
    this.stopTimer()
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadOrders()
    }
  },
  methods: {
    getImageUrl,
    
    async loadOrders() {
      this.loading = true
      try {
        const params = { page: this.page, size: 10 }
        if (this.currentStatus) params.status = this.currentStatus

        let res
        if (this.orderType === 'buy') {
          res = await orderApi.getBuyerOrders(params)
        } else {
          res = await orderApi.getSellerOrders(params)
        }

        const newOrders = res.data.content || []
        if (this.page === 0) {
          this.orders = newOrders
        } else {
          this.orders = [...this.orders, ...newOrders]
        }

        this.hasMore = !res.data.last
        this.page++
        this.initCountdowns()
        this.startTimer()
      } catch (e) {
        console.error('加载订单失败', e)
      } finally {
        this.loading = false
      }
    },

    // 初始化所有待支付订单的剩余秒数
    initCountdowns() {
      const map = {}
      this.orders.forEach(order => {
        if (order.status === 'PENDING') {
          map[order.id] = this.calcRemaining(order.createdAt)
        }
      })
      this.countdowns = map
    },

    // 计算距超时的剩余秒数（30分钟超时）
    calcRemaining(createdAt) {
      const created = new Date(createdAt.replace(' ', 'T'))
      const expireAt = created.getTime() + 30 * 60 * 1000
      return Math.max(0, Math.floor((expireAt - Date.now()) / 1000))
    },

    // 格式化为 MM:SS
    formatCountdown(seconds) {
      if (seconds <= 0) return '00:00'
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
    },

    startTimer() {
      this.stopTimer()
      const hasPending = this.orders.some(o => o.status === 'PENDING')
      if (!hasPending) return
      this._timer = setInterval(() => {
        let needRefresh = false
        const map = { ...this.countdowns }
        Object.keys(map).forEach(id => {
          if (map[id] > 0) {
            map[id]--
          } else if (map[id] === 0) {
            // 倒计时归零，稍后刷新列表
            needRefresh = true
          }
        })
        this.countdowns = map
        if (needRefresh) {
          this.stopTimer()
          setTimeout(() => this.refreshList(), 1500)
        }
      }, 1000)
    },

    stopTimer() {
      if (this._timer) {
        clearInterval(this._timer)
        this._timer = null
      }
    },
    
    switchType(type) {
      this.orderType = type
      this.page = 0
      this.orders = []
      this.hasMore = true
      this.loadOrders()
    },
    
    filterByStatus(status) {
      this.currentStatus = status
      this.page = 0
      this.orders = []
      this.hasMore = true
      this.loadOrders()
    },
    
    getStatusText(status) {
      const map = {
        PENDING: '待付款',
        PAID: '已付款',
        SHIPPED: '已发货',
        DELIVERED: '已送达',
        COMPLETED: '已完成',
        CANCELLED: '已取消'
      }
      return map[status] || status
    },
    
    getStatusClass(status) {
      const map = {
        PENDING: 'pending',
        PAID: 'paid',
        SHIPPED: 'shipped',
        COMPLETED: 'completed',
        CANCELLED: 'cancelled'
      }
      return map[status] || ''
    },
    
    goOrderDetail(id) {
      uni.navigateTo({
        url: `/pages/order/detail?id=${id}`
      })
    },
    
    async payOrder(id) {
      uni.showModal({
        title: '确认支付',
        content: '确定要支付此订单吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.payOrder(id)
              uni.showToast({ title: '支付成功', icon: 'success' })
              this.refreshList()
            } catch (e) {
              console.error('支付失败', e)
            }
          }
        }
      })
    },
    
    async cancelOrder(id) {
      uni.showModal({
        title: '取消订单',
        content: '确定要取消此订单吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.cancelOrder(id)
              uni.showToast({ title: '订单已取消', icon: 'success' })
              this.refreshList()
            } catch (e) {
              console.error('取消失败', e)
            }
          }
        }
      })
    },
    
    shipOrder(id) {
      uni.showModal({
        title: '发货',
        content: '确定要发货吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.shipOrder(id, {
                trackingNumber: '',
                trackingCompany: ''
              })
              uni.showToast({ title: '发货成功', icon: 'success' })
              this.refreshList()
            } catch (e) {
              console.error('发货失败', e)
            }
          }
        }
      })
    },
    
    async confirmReceive(id) {
      uni.showModal({
        title: '确认收货',
        content: '确定已收到商品吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.confirmReceive(id)
              uni.showToast({ title: '收货成功', icon: 'success' })
              this.refreshList()
            } catch (e) {
              console.error('确认收货失败', e)
            }
          }
        }
      })
    },
    
    refreshList() {
      this.page = 0
      this.orders = []
      this.hasMore = true
      this.countdowns = {}
      this.loadOrders()
    }
  }
}
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 类型切换 */
.type-tabs {
  display: flex;
  background-color: #ffffff;
}

.type-tab {
  flex: 1;
  text-align: center;
  padding: 28rpx;
  font-size: 28rpx;
  color: #666666;
  border-bottom: 4rpx solid transparent;
}

.type-tab.active {
  color: #ff6b35;
  border-bottom-color: #ff6b35;
  font-weight: bold;
}

/* 状态筛选 */
.status-tabs {
  white-space: nowrap;
  background-color: #ffffff;
  padding: 16rpx 0;
  margin-bottom: 20rpx;
}

.status-tab {
  display: inline-block;
  padding: 12rpx 30rpx;
  margin: 0 10rpx;
  font-size: 26rpx;
  color: #666666;
  background-color: #f5f5f5;
  border-radius: 30rpx;
}

.status-tab.active {
  background-color: #ff6b35;
  color: #ffffff;
}

/* 订单列表 */
.order-list {
  padding: 0 20rpx;
}

.order-item {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
  font-size: 24rpx;
  color: #999999;
}

.order-status {
  font-size: 26rpx;
  font-weight: bold;
}

.order-status.pending { color: #ff9800; }
.order-status.paid { color: #2196f3; }
.order-status.shipped { color: #9c27b0; }
.order-status.completed { color: #4caf50; }
.order-status.cancelled { color: #999999; }

/* 倒计时条 */
.countdown-bar {
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #fff8f0;
  border-radius: 8rpx;
  padding: 10rpx 16rpx;
  margin-bottom: 16rpx;
}
.countdown-label {
  font-size: 22rpx;
  color: #999;
}
.countdown-time {
  font-size: 28rpx;
  font-weight: bold;
  color: #ff9800;
  font-variant-numeric: tabular-nums;
  letter-spacing: 2rpx;
}
.countdown-time.urgent {
  color: #f44336;
}

.order-content {
  display: flex;
  margin-bottom: 20rpx;
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
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.4;
}

.product-price {
  font-size: 32rpx;
  color: #ff6b35;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.user-info {
  font-size: 24rpx;
  color: #999999;
}

.order-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  padding: 12rpx 24rpx;
  font-size: 24rpx;
  border-radius: 30rpx;
  background-color: #f5f5f5;
  color: #666666;
  border: none;
  line-height: 1.5;
}

.action-btn.primary {
  background-color: #ff6b35;
  color: #ffffff;
}

/* 空状态 */
.empty-tip {
  text-align: center;
  padding: 100rpx;
  color: #999999;
  font-size: 28rpx;
}

/* 加载状态 */
.loading-status {
  text-align: center;
  padding: 30rpx;
  color: #999999;
  font-size: 26rpx;
}
</style>
