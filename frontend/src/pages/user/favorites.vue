<template>
  <view class="favorites-page">
    <!-- 空状态 -->
    <view v-if="!loading && favorites.length === 0" class="empty-state">
      <text class="empty-icon">&#x1F4E6;</text>
      <text class="empty-text">暂无收藏商品</text>
      <text class="empty-tip">去首页逛逛，发现心仪的商品吧</text>
      <button class="go-home-btn" @click="goHome">去逛逛</button>
    </view>
    
    <!-- 收藏列表 -->
    <view v-else class="product-list">
      <view 
        v-for="item in favorites" 
        :key="item.id"
        class="product-item"
        @click="goDetail(item.id)"
      >
        <image class="product-image" :src="getImageUrl(item.imageUrl)" mode="aspectFill" />
        <view class="product-info">
          <text class="product-name">{{ item.name }}</text>
          <text class="product-price">¥{{ item.price }}</text>
          <view class="product-meta">
            <text class="product-location">{{ item.location || '未知位置' }}</text>
            <text class="view-count">浏览 {{ item.viewCount }}</text>
          </view>
        </view>
        <view class="product-actions">
          <text class="cancel-btn" @click.stop="cancelFavorite(item.id)">取消收藏</text>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view v-if="loading" class="loading-more">加载中...</view>
      <view v-if="!hasMore && favorites.length > 0" class="no-more">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { productApi } from '../../api/index.js'
import { getImageUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      favorites: [],
      loading: false,
      page: 0,
      size: 10,
      hasMore: true
    }
  },
  onLoad() {
    this.loadFavorites()
  },
  onPullDownRefresh() {
    this.page = 0
    this.favorites = []
    this.hasMore = true
    this.loadFavorites().finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadFavorites()
    }
  },
  methods: {
    getImageUrl,
    
    async loadFavorites() {
      if (this.loading) return
      this.loading = true
      
      try {
        const res = await productApi.getFavorites({
          page: this.page,
          size: this.size
        })
        
        const data = res.data
        if (this.page === 0) {
          this.favorites = data.content || []
        } else {
          this.favorites = [...this.favorites, ...(data.content || [])]
        }
        
        this.hasMore = !data.last
        if (this.hasMore) {
          this.page++
        }
      } catch (e) {
        console.error('加载收藏失败', e)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    async cancelFavorite(productId) {
      try {
        await productApi.removeFavorite(productId)
        uni.showToast({
          title: '已取消收藏',
          icon: 'success'
        })
        // 从列表中移除
        this.favorites = this.favorites.filter(item => item.id !== productId)
      } catch (e) {
        console.error('取消收藏失败', e)
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        })
      }
    },
    
    goDetail(productId) {
      uni.navigateTo({
        url: `/pages/detail/detail?id=${productId}`
      })
    },
    
    goHome() {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }
  }
}
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 15rpx;
}

.empty-tip {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.go-home-btn {
  width: 240rpx;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #ff6b35;
  color: #fff;
  border-radius: 40rpx;
  font-size: 30rpx;
}

/* 商品列表 */
.product-list {
  padding: 20rpx;
}

.product-item {
  display: flex;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.product-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: 34rpx;
  color: #ff6b35;
  font-weight: bold;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
  color: #999;
}

.product-actions {
  display: flex;
  align-items: flex-end;
  padding-left: 20rpx;
}

.cancel-btn {
  font-size: 26rpx;
  color: #999;
  padding: 10rpx 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 30rpx;
}

.loading-more, .no-more {
  text-align: center;
  padding: 30rpx;
  font-size: 26rpx;
  color: #999;
}
</style>
