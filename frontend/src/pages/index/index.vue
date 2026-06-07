<template>
  <view class="index-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input" @click="goSearch">
        <text class="search-icon">&#x1F50D;</text>
        <text class="search-placeholder">搜索商品、类别、关键词</text>
      </view>
    </view>
    
    <!-- 分类导航 -->
    <view class="category-nav">
      <scroll-view scroll-x class="category-scroll">
        <view 
          class="category-item" 
          v-for="item in categories" 
          :key="item.id"
          :class="{ active: currentCategory === item.id }"
          @click="selectCategory(item.id)"
        >
          <text>{{ item.name }}</text>
        </view>
      </scroll-view>
    </view>
    
    <!-- 商品网格列表 -->
    <view class="product-grid">
      <view 
        class="product-card" 
        v-for="item in products" 
        :key="item.id"
        @click="goDetail(item.id)"
      >
        <remote-image custom-class="product-image" :src="item.imageUrl" mode="aspectFill" />
        <view class="product-info">
          <text class="product-name">{{ item.name }}</text>
          <view class="product-price-row">
            <text class="product-price">¥{{ item.price }}</text>
            <text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
          </view>
          <view class="product-footer">
            <text class="seller-name">{{ item.seller.nickname || item.seller.username }}</text>
            <text class="location" v-if="item.location">{{ item.location }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view class="loading-status" v-if="products.length === 0">
      <text v-if="loading">加载中...</text>
      <text v-else-if="!loading && products.length === 0">暂无商品</text>
    </view>
    
    <!-- 底部加载更多状态 -->
    <view class="loading-status" v-if="products.length > 0">
      <text v-if="loading">加载中...</text>
      <text v-else-if="!hasMore">没有更多了</text>
    </view>
  </view>
</template>

<script>
import { productApi } from '../../api/index.js'
import RemoteImage from '../../components/RemoteImage.vue'

export default {
  components: {
    RemoteImage
  },
  data() {
    return {
      categories: [
        { id: null, name: '全部' }
      ],
      currentCategory: null,
      products: [],
      page: 0,
      size: 20,
      loading: false,
      hasMore: true,
      keyword: ''
    }
  },
  onLoad() {
    this.loadCategories()
    this.loadProducts()
  },
  onPullDownRefresh() {
    this.page = 0
    this.products = []
    this.hasMore = true
    this.loadProducts().then(() => {
      uni.stopPullDownRefresh()
    })
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadProducts()
    }
  },
  methods: {
    async loadCategories() {
      try {
        const res = await productApi.getCategories()
        this.categories = [{ id: null, name: '全部' }, ...res.data]
      } catch (e) {
        console.error('加载分类失败', e)
      }
    },
    
    async loadProducts() {
      if (this.loading) return
      
      console.log('[开始加载商品] page:', this.page, 'categoryId:', this.currentCategory)
      
      this.loading = true
      try {
        // 构建请求参数，过滤掉null值
        const params = {
          page: this.page,
          size: this.size
        }
        
        // 只有当categoryId不为null时才添加
        if (this.currentCategory !== null) {
          params.categoryId = this.currentCategory
        }
        
        // 只有当keyword不为空时才添加
        if (this.keyword) {
          params.keyword = this.keyword
        }
        
        console.log('[请求参数]', params)
        
        const res = await productApi.getProducts(params)
        
        console.log('[加载成功] 商品数量:', res.data.content.length)
        
        if (this.page === 0) {
          this.products = res.data.content
        } else {
          this.products = [...this.products, ...res.data.content]
        }
        
        this.hasMore = !res.data.last
        this.page++
      } catch (e) {
        console.error('加载商品失败', e)
        console.error('错误详情:', JSON.stringify(e))
        
        // 显示更详细的错误信息
        let errorMsg = '加载失败'
        if (e.data && e.data.message) {
          errorMsg = e.data.message
        } else if (e.errMsg) {
          errorMsg = e.errMsg
        }
        
        uni.showToast({
          title: errorMsg,
          icon: 'none',
          duration: 3000
        })
      } finally {
        this.loading = false
      }
    },
    
    selectCategory(categoryId) {
      this.currentCategory = categoryId
      this.page = 0
      this.products = []
      this.hasMore = true
      this.loadProducts()
    },
    
    goSearch() {
      uni.navigateTo({
        url: '/pages/search/search'
      })
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
.index-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 搜索栏 */
.search-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 20rpx;
  background-color: #ffffff;
}

.search-input {
  display: flex;
  align-items: center;
  height: 72rpx;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 30rpx;
}

.search-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
}

.search-placeholder {
  color: #999999;
  font-size: 28rpx;
}

/* 分类导航 */
.category-nav {
  background-color: #ffffff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
}

.category-scroll {
  white-space: nowrap;
  padding: 0 20rpx;
}

.category-item {
  display: inline-block;
  padding: 12rpx 30rpx;
  margin-right: 20rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #333333;
}

.category-item.active {
  background-color: #ff6b35;
  color: #ffffff;
}

/* 商品网格布局 - 固定两列 */
.product-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 8rpx;
}

.product-card {
  width: calc(50% - 8rpx);
  margin: 4rpx;
  background-color: #ffffff;
  border-radius: 8rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.05);
}

/* 图片固定高度 - 严格控制 */
.product-image {
  width: 100%;
  height: 352rpx;
  display: block;
  background-color: #f8f8f8;
}

.product-info {
  padding: 12rpx;
}

.product-name {
  font-size: 26rpx;
  color: #333333;
  line-height: 36rpx;
  height: 72rpx;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 8rpx;
}

.product-price {
  font-size: 28rpx;
  color: #ff6b35;
  font-weight: bold;
}

.original-price {
  font-size: 20rpx;
  color: #999999;
  text-decoration: line-through;
  margin-left: 6rpx;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seller-name {
  font-size: 20rpx;
  color: #999999;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12rpx;
}

.location {
  font-size: 20rpx;
  color: #999999;
  flex-shrink: 0;
}

/* 加载状态 */
.loading-status {
  text-align: center;
  padding: 30rpx;
  color: #999999;
  font-size: 26rpx;
}
</style>
