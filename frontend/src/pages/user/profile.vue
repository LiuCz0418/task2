<template>
  <view class="profile-page">
    <!-- 用户信息 -->
    <view class="user-header">
      <view class="avatar-wrap">
        <text class="avatar-text">{{ (userInfo.nickname || userInfo.username || '用')[0] }}</text>
      </view>
      <view class="user-detail">
        <text class="nickname">{{ userInfo.nickname || userInfo.username || '用户' }}</text>
        <view class="credit-row">
          <text class="credit-badge" v-if="userInfo.creditLevel">{{ userInfo.creditLevel }}</text>
          <text class="credit-score">信用分 {{ userInfo.creditScore || 100 }}</text>
        </view>
        <text class="join-time" v-if="userInfo.createdAt">注册时间：{{ userInfo.createdAt }}</text>
      </view>
    </view>

    <!-- Tab 切换 -->
    <view class="tab-bar">
      <text class="tab-item" :class="{ active: activeTab === 'products' }" @click="activeTab = 'products'">TA发布的商品</text>
      <text class="tab-item" :class="{ active: activeTab === 'reviews' }" @click="switchToReviews">收到的评价</text>
    </view>

    <!-- 商品列表 -->
    <view class="section" v-if="activeTab === 'products'">
      <view class="product-list">
        <view 
          class="product-item" 
          v-for="item in products" 
          :key="item.id"
          @click="goDetail(item.id)"
        >
          <image class="product-image" :src="getImageUrl(item.imageUrl)" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-price">¥{{ item.price }}</text>
          </view>
        </view>
      </view>
      <view v-if="products.length === 0 && !loading" class="empty">
        <text class="empty-text">该用户暂未发布商品</text>
      </view>
    </view>

    <!-- 评价列表 -->
    <view class="section" v-if="activeTab === 'reviews'">
      <view class="review-list">
        <view class="review-item" v-for="review in reviews" :key="review.id">
          <view class="review-header">
            <text class="reviewer-name">{{ review.user?.nickname || review.user?.username }}</text>
            <text class="review-stars">{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</text>
          </view>
          <view class="score-row" v-if="review.serviceScore">
            <text class="score-item">服务 {{ review.serviceScore }}分</text>
            <text class="score-item">描述 {{ review.descScore }}分</text>
            <text class="score-item">发货 {{ review.shipScore }}分</text>
          </view>
          <text class="review-product">商品：{{ review.productName }}</text>
          <text class="review-comment">{{ review.comment }}</text>
          <text class="review-time">{{ review.createdAt }}</text>
        </view>
      </view>
      <view v-if="reviews.length === 0 && !reviewLoading" class="empty">
        <text class="empty-text">暂无评价</text>
      </view>
    </view>
  </view>
</template>

<script>
import { userApi, productApi, reviewApi } from '../../api/index.js'
import { getImageUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      userId: null,
      userInfo: {},
      products: [],
      reviews: [],
      loading: false,
      reviewLoading: false,
      activeTab: 'products'
    }
  },
  onLoad(options) {
    this.userId = options.id
    if (this.userId) {
      this.loadUserInfo()
      this.loadUserProducts()
    }
  },
  methods: {
    getImageUrl,

    async loadUserInfo() {
      try {
        const res = await userApi.getUserInfo(this.userId)
        this.userInfo = res.data
        uni.setNavigationBarTitle({
          title: this.userInfo.nickname || this.userInfo.username || '用户主页'
        })
      } catch (e) {
        console.error('获取用户信息失败', e)
      }
    },

    async loadUserProducts() {
      this.loading = true
      try {
        const res = await productApi.getProducts({
          sellerId: this.userId,
          page: 0,
          size: 20
        })
        if (res.data && res.data.content) {
          this.products = res.data.content
        }
      } catch (e) {
        console.error('加载商品失败', e)
      } finally {
        this.loading = false
      }
    },

    async loadSellerReviews() {
      this.reviewLoading = true
      try {
        const res = await reviewApi.getSellerReviews(this.userId, { page: 0, size: 20 })
        if (res.data && res.data.content) {
          this.reviews = res.data.content
        }
      } catch (e) {
        console.error('加载评价失败', e)
      } finally {
        this.reviewLoading = false
      }
    },

    switchToReviews() {
      this.activeTab = 'reviews'
      if (this.reviews.length === 0 && !this.reviewLoading) {
        this.loadSellerReviews()
      }
    },

    goDetail(id) {
      uni.navigateTo({
        url: `/pages/detail/detail?id=${id}`
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.user-header {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8f5a 100%);
}

.avatar-wrap {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 4rpx solid rgba(255,255,255,0.5);
}

.avatar-text {
  font-size: 56rpx;
  color: #fff;
  font-weight: bold;
}

.user-detail {
  margin-left: 30rpx;
  flex: 1;
}

.nickname {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.credit-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.credit-badge {
  font-size: 20rpx;
  color: #ff6b35;
  background: #fff;
  padding: 4rpx 14rpx;
  border-radius: 20rpx;
  font-weight: bold;
}

.credit-score {
  font-size: 24rpx;
  color: rgba(255,255,255,0.9);
}

.join-time {
  font-size: 22rpx;
  color: rgba(255,255,255,0.7);
}

/* Tab 栏 */
.tab-bar {
  display: flex;
  background: #fff;
  border-bottom: 2rpx solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 28rpx;
  color: #999;
  padding: 24rpx 0;
}

.tab-item.active {
  color: #ff6b35;
  border-bottom: 4rpx solid #ff6b35;
  font-weight: bold;
}

/* 通用区域 */
.section {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.product-item {
  width: calc(50% - 8rpx);
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.product-image {
  width: 100%;
  height: 320rpx;
  display: block;
  background-color: #f5f5f5;
}

.product-info {
  padding: 16rpx;
}

.product-name {
  font-size: 26rpx;
  color: #333;
  line-height: 36rpx;
  height: 36rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8rpx;
}

.product-price {
  font-size: 28rpx;
  color: #ff6b35;
  font-weight: bold;
}

/* 评价列表 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.review-item {
  border-bottom: 1rpx solid #f0f0f0;
  padding-bottom: 24rpx;
}

.review-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.reviewer-name {
  font-size: 26rpx;
  color: #333;
  font-weight: bold;
}

.review-stars {
  font-size: 24rpx;
  color: #ffb300;
}

.score-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 8rpx;
}

.score-item {
  font-size: 22rpx;
  color: #ff6b35;
  background: #fff5f0;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.review-product {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-bottom: 8rpx;
}

.review-comment {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  display: block;
  margin-bottom: 10rpx;
}

.review-time {
  font-size: 22rpx;
  color: #bbb;
  display: block;
}

.empty {
  text-align: center;
  padding: 60rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
