<template>
  <view class="detail-page">
    <!-- 图片轮播 -->
    <swiper
      class="image-swiper"
      indicator-dots
      circular
      :autoplay="productImages.length > 1"
      :interval="3000"
      :duration="500"
    >
      <swiper-item v-for="(img, index) in productImages" :key="index">
        <image class="swiper-image" :src="getImageUrl(img)" mode="aspectFill" @click="previewImage(index)" />
      </swiper-item>
    </swiper>
    
    <!-- 商品信息 -->
    <view class="product-info card">
      <view class="price-row">
        <text class="price">¥{{ product.price }}</text>
        <text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
      </view>
      <text class="product-name">{{ product.name }}</text>
      <view class="meta-row">
        <text class="condition">{{ conditionText }}</text>
        <text class="location">{{ product.location || '未知位置' }}</text>
        <text class="views">{{ product.viewCount }}次浏览</text>
      </view>
    </view>
    
    <!-- 商品描述 -->
    <view class="description card">
      <text class="section-title">商品描述</text>
      <text class="desc-content">{{ product.description || '暂无描述' }}</text>
    </view>
    
    <!-- 卖家信息 -->
    <view class="seller-info card" @click="goSellerProfile">
      <view class="seller-detail">
        <text class="seller-name">{{ product.seller?.nickname || product.seller?.username }}</text>
        <view class="credit-row">
          <text class="credit-badge" v-if="product.seller?.creditLevel">{{ product.seller.creditLevel }}</text>
          <text class="credit-score-text">信用分 {{ product.seller?.creditScore || 0 }}</text>
        </view>
      </view>
      <text class="arrow">›</text>
    </view>
    
    <!-- 评价区域 -->
    <view class="reviews card">
      <view class="section-header">
        <text class="section-title">商品评价</text>
        <text class="review-count" v-if="product.reviewCount">{{ product.reviewCount }}条评价</text>
      </view>
      <view class="rating-row" v-if="product.avgRating">
        <text class="rating-label">平均评分：</text>
        <text class="rating-stars">{{ '★'.repeat(Math.round(product.avgRating)) }}{{ '☆'.repeat(5 - Math.round(product.avgRating)) }}</text>
        <text class="rating-value">{{ product.avgRating?.toFixed(1) }}</text>
      </view>
      <view class="review-list" v-if="reviews.length > 0">
        <view class="review-item" v-for="review in reviews" :key="review.id">
          <view class="review-header">
            <text class="reviewer-name">{{ review.user?.nickname || review.user?.username }}</text>
            <text class="review-rating">{{ '★'.repeat(review.rating) }}</text>
          </view>
          <text class="review-content">{{ review.comment }}</text>
          <text class="review-time">{{ review.createdAt }}</text>
        </view>
      </view>
      <view class="no-review" v-else>
        <text>暂无评价</text>
      </view>
    </view>
    
    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-btn" @click="contactSeller">
        <text class="action-icon">&#x1F4AC;</text>
        <text class="action-text">联系卖家</text>
      </view>
      <view class="action-btn" @click="collectProduct">
        <text class="action-icon">{{ isCollected ? '&#x2764;' : '&#x1F90D;' }}</text>
        <text class="action-text">{{ isCollected ? '已收藏' : '收藏' }}</text>
      </view>
      <button class="buy-btn" @click="buyNow">立即购买</button>
    </view>
  </view>
</template>

<script>
import { productApi, reviewApi, orderApi } from '../../api/index.js'
import RemoteImage from '../../components/RemoteImage.vue'
import { getImageUrl } from '../../utils/image.js'

export default {
  components: {
    RemoteImage
  },
  data() {
    return {
      productId: null,
      product: {},
      reviews: [],
      isCollected: false
    }
  },
  computed: {
    conditionText() {
      const level = this.product.conditionLevel || 9
      if (level >= 9) return '几乎全新'
      if (level >= 7) return '成色较新'
      if (level >= 5) return '有使用痕迹'
      return '明显使用痕迹'
    },
    productImages() {
      if (Array.isArray(this.product.images) && this.product.images.length > 0) {
        return this.product.images
      }
      return this.product.imageUrl ? [this.product.imageUrl] : ['/static/images/placeholder.png']
    }
  },
  onLoad(options) {
    this.productId = options.id
    this.loadProductDetail()
    this.loadReviews()
    this.checkFavorite()
  },
  methods: {
    getImageUrl,
    
    async loadProductDetail() {
      try {
        const res = await productApi.getProductDetail(this.productId)
        this.product = res.data
      } catch (e) {
        console.error('加载商品详情失败', e)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    },
    
    async loadReviews() {
      try {
        const res = await reviewApi.getProductReviews(this.productId, { page: 0, size: 5 })
        this.reviews = res.data.content || []
      } catch (e) {
        console.error('加载评价失败', e)
      }
    },
    
    previewImage(index) {
      // 真机环境下使用本地路径，开发工具直接使用HTTP URL
      const systemInfo = uni.getSystemInfoSync()
      let urls
      
      if (systemInfo.platform === 'devtools') {
        // 开发工具环境，直接使用可访问 URL
        urls = this.productImages.map(img => getImageUrl(img))
      } else {
        urls = this.productImages.map(img => getImageUrl(img))
      }
      
      uni.previewImage({
        urls: urls,
        current: index
      })
    },
    
    contactSeller() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.navigateTo({
          url: '/pages/login/login'
        })
        return
      }
      
      uni.navigateTo({
        url: `/pages/chat/chat?userId=${this.product.seller?.id}&productId=${this.productId}`
      })
    },
    
    async checkFavorite() {
      const token = uni.getStorageSync('token')
      if (!token) return
      try {
        const res = await productApi.isFavorite(this.productId)
        this.isCollected = res.data
      } catch (e) {
        console.error('检查收藏状态失败', e)
      }
    },
    
    async collectProduct() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.navigateTo({
          url: '/pages/login/login'
        })
        return
      }
      
      try {
        if (this.isCollected) {
          await productApi.removeFavorite(this.productId)
          this.isCollected = false
          uni.showToast({ title: '已取消收藏', icon: 'none' })
        } else {
          await productApi.addFavorite(this.productId)
          this.isCollected = true
          uni.showToast({ title: '收藏成功', icon: 'success' })
        }
      } catch (e) {
        console.error('收藏操作失败', e)
        uni.showToast({ title: '操作失败', icon: 'none' })
      }
    },
    
    async buyNow() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.navigateTo({
          url: '/pages/login/login'
        })
        return
      }
      
      uni.showModal({
        title: '确认购买',
        content: `确定要购买 ${this.product.name} 吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              const orderRes = await orderApi.createOrder({
                productId: this.productId,
                price: this.product.price
              })
              uni.showToast({
                title: '下单成功',
                icon: 'success'
              })
              setTimeout(() => {
                uni.navigateTo({
                  url: `/pages/order/detail?id=${orderRes.data.id}`
                })
              }, 1500)
            } catch (e) {
              console.error('下单失败', e)
            }
          }
        }
      })
    },
    
    goSellerProfile() {
      if (this.product.seller?.id) {
        uni.navigateTo({
          url: `/pages/user/profile?id=${this.product.seller.id}`
        })
      }
    }
  }
}
</script>

<style scoped>
.detail-page {
  padding-bottom: 140rpx;
  background-color: #f5f5f5;
}

/* 图片轮播 - 优化尺寸 */
.image-swiper {
  width: 100%;
  height: 750rpx;
  background-color: #f5f5f5;
}

.swiper-image {
  width: 100%;
  height: 100%;
  display: block;
  background-color: #f5f5f5;
}

/* 商品信息 */
.product-info {
  margin-top: 20rpx;
}

.price-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 16rpx;
}

.price {
  font-size: 48rpx;
  color: #ff6b35;
  font-weight: bold;
}

.original-price {
  font-size: 28rpx;
  color: #999999;
  text-decoration: line-through;
  margin-left: 16rpx;
}

.product-name {
  font-size: 32rpx;
  color: #333333;
  line-height: 1.5;
  margin-bottom: 16rpx;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.condition, .location, .views {
  font-size: 24rpx;
  color: #999999;
  background-color: #f5f5f5;
  padding: 6rpx 16rpx;
  border-radius: 6rpx;
}

/* 商品描述 */
.description {
  margin-top: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 16rpx;
  display: block;
}

.desc-content {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
}

/* 卖家信息 */
.seller-info {
  margin-top: 20rpx;
  display: flex;
  align-items: center;
}

.seller-detail {
  flex: 1;
}

.seller-name {
  font-size: 30rpx;
  color: #333333;
  margin-bottom: 8rpx;
  display: block;
}

.credit-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.credit-badge {
  font-size: 20rpx;
  color: #fff;
  background: #ff6b35;
  padding: 4rpx 14rpx;
  border-radius: 20rpx;
  flex-shrink: 0;
}

.credit-score-text {
  font-size: 24rpx;
  color: #999999;
}

.arrow {
  font-size: 40rpx;
  color: #cccccc;
}

/* 评价区域 */
.reviews {
  margin-top: 20rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.review-count {
  font-size: 24rpx;
  color: #999999;
}

.rating-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.rating-label {
  font-size: 26rpx;
  color: #666666;
}

.rating-stars {
  color: #ffb800;
  margin: 0 10rpx;
}

.rating-value {
  font-size: 28rpx;
  color: #ff6b35;
  font-weight: bold;
}

.review-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.reviewer-name {
  font-size: 26rpx;
  color: #333333;
  flex: 1;
}

.review-rating {
  font-size: 24rpx;
  color: #ffb800;
}

.review-content {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
  margin-bottom: 8rpx;
}

.review-time {
  font-size: 22rpx;
  color: #999999;
}

.no-review {
  text-align: center;
  padding: 40rpx;
  color: #999999;
  font-size: 26rpx;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding-bottom: env(safe-area-inset-bottom);
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 120rpx;
}

.action-icon {
  font-size: 40rpx;
  margin-bottom: 4rpx;
}

.action-text {
  font-size: 22rpx;
  color: #666666;
}

.buy-btn {
  flex: 1;
  height: 80rpx;
  background-color: #ff6b35;
  color: #ffffff;
  border: none;
  border-radius: 40rpx;
  font-size: 30rpx;
  margin-left: 30rpx;
}

/* 通用卡片 */
.card {
  background-color: #ffffff;
  padding: 24rpx;
  margin: 0 20rpx;
  border-radius: 16rpx;
}
</style>
