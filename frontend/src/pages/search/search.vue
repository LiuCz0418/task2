<template>
  <view class="search-page">
    <!-- 搜索框 -->
    <view class="search-header">
      <view class="search-input-wrapper">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          class="search-input" 
          type="text" 
          v-model="keyword"
          placeholder="搜索商品"
          focus
          confirm-type="search"
          @confirm="doSearch"
        />
        <uni-icons 
          v-if="keyword" 
          type="clear" 
          size="18" 
          color="#999" 
          @click="clearKeyword"
        ></uni-icons>
      </view>
      <text class="cancel-btn" @click="goBack">取消</text>
    </view>

    <!-- 搜索历史 -->
    <view v-if="!searched && searchHistory.length > 0" class="history-section">
      <view class="section-header">
        <text class="section-title">搜索历史</text>
        <uni-icons type="trash" size="18" color="#999" @click="clearHistory"></uni-icons>
      </view>
      <view class="history-tags">
        <text 
          v-for="(item, index) in searchHistory" 
          :key="index" 
          class="history-tag"
          @click="searchByHistory(item)"
        >{{ item }}</text>
      </view>
    </view>

    <!-- 热门搜索 -->
    <view v-if="!searched" class="hot-section">
      <view class="section-header">
        <text class="section-title">热门搜索</text>
      </view>
      <view class="hot-tags">
        <text 
          v-for="(item, index) in hotKeywords" 
          :key="index" 
          class="hot-tag"
          @click="searchByHistory(item)"
        >{{ item }}</text>
      </view>
    </view>

    <!-- 搜索结果 -->
    <view v-if="searched" class="search-result">
      <view class="result-header">
        <text class="result-count">共找到 {{ total }} 件商品</text>
        <view class="sort-options">
          <text 
            class="sort-item" 
            :class="{ active: sortType === 'default' }"
            @click="changeSort('default')"
          >综合</text>
          <text 
            class="sort-item" 
            :class="{ active: sortType === 'price_asc' }"
            @click="changeSort('price_asc')"
          >价格↑</text>
          <text 
            class="sort-item" 
            :class="{ active: sortType === 'price_desc' }"
            @click="changeSort('price_desc')"
          >价格↓</text>
          <text 
            class="sort-item" 
            :class="{ active: sortType === 'newest' }"
            @click="changeSort('newest')"
          >最新</text>
        </view>
      </view>

      <!-- 商品列表 -->
      <view class="product-list">
        <view 
          v-for="item in products" 
          :key="item.id" 
          class="product-item"
          @click="goDetail(item.id)"
        >
          <image class="product-image" :src="item.mainImage" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-title">{{ item.title }}</text>
            <view class="product-meta">
              <text class="product-price">¥{{ item.price }}</text>
              <text class="product-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
            </view>
            <view class="product-seller">
              <image class="seller-avatar" :src="item.sellerAvatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
              <text class="seller-name">{{ item.sellerNickname }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 无结果 -->
      <view v-if="products.length === 0 && !loading" class="empty-result">
        <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
        <text class="empty-text">未找到相关商品</text>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="products.length > 0">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi } from '@/api'

const keyword = ref('')
const searched = ref(false)
const loading = ref(false)
const products = ref([])
const total = ref(0)
const page = ref(0)
const noMore = ref(false)
const sortType = ref('default')

// 搜索历史
const searchHistory = ref([])
// 热门搜索
const hotKeywords = ref(['iPhone', '笔记本电脑', '二手书', '自行车', '相机', '耳机', '平板', '手表'])

onMounted(() => {
  loadHistory()
})

const loadHistory = () => {
  try {
    const history = uni.getStorageSync('searchHistory')
    if (history) {
      searchHistory.value = JSON.parse(history)
    }
  } catch (e) {
    console.error('加载搜索历史失败', e)
  }
}

const saveHistory = (kw) => {
  if (!kw.trim()) return
  let history = searchHistory.value.filter(item => item !== kw)
  history.unshift(kw)
  if (history.length > 10) {
    history = history.slice(0, 10)
  }
  searchHistory.value = history
  uni.setStorageSync('searchHistory', JSON.stringify(history))
}

const clearHistory = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空搜索历史吗？',
    success: (res) => {
      if (res.confirm) {
        searchHistory.value = []
        uni.removeStorageSync('searchHistory')
      }
    }
  })
}

const clearKeyword = () => {
  keyword.value = ''
}

const doSearch = () => {
  if (!keyword.value.trim()) {
    uni.showToast({ title: '请输入搜索关键词', icon: 'none' })
    return
  }
  saveHistory(keyword.value)
  searched.value = true
  page.value = 0
  products.value = []
  noMore.value = false
  loadProducts()
}

const searchByHistory = (kw) => {
  keyword.value = kw
  doSearch()
}

const changeSort = (type) => {
  sortType.value = type
  page.value = 0
  products.value = []
  noMore.value = false
  loadProducts()
}

const loadProducts = async () => {
  if (loading.value || noMore.value) return
  loading.value = true
  
  try {
    const params = {
      keyword: keyword.value,
      page: page.value,
      size: 10
    }
    
    // 添加排序参数
    if (sortType.value === 'price_asc') {
      params.sort = 'price,asc'
    } else if (sortType.value === 'price_desc') {
      params.sort = 'price,desc'
    } else if (sortType.value === 'newest') {
      params.sort = 'createdAt,desc'
    }
    
    const res = await productApi.getProducts(params)
    if (res.data && res.data.content) {
      products.value = [...products.value, ...res.data.content]
      total.value = res.data.totalElements
      page.value++
      if (res.data.content.length < 10) {
        noMore.value = true
      }
    }
  } catch (e) {
    console.error('搜索失败', e)
    uni.showToast({ title: '搜索失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  uni.navigateTo({
    url: `/pages/detail/detail?id=${id}`
  })
}

const goBack = () => {
  uni.navigateBack()
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-header {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
}

.search-input {
  flex: 1;
  margin: 0 16rpx;
  font-size: 28rpx;
}

.cancel-btn {
  margin-left: 20rpx;
  font-size: 28rpx;
  color: #666;
}

.history-section,
.hot-section {
  background-color: #fff;
  margin-top: 20rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.history-tags,
.hot-tags {
  display: flex;
  flex-wrap: wrap;
}

.history-tag,
.hot-tag {
  padding: 12rpx 24rpx;
  background-color: #f5f5f5;
  border-radius: 24rpx;
  font-size: 26rpx;
  color: #666;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
}

.search-result {
  padding-top: 20rpx;
}

.result-header {
  background-color: #fff;
  padding: 20rpx 30rpx;
}

.result-count {
  font-size: 24rpx;
  color: #999;
}

.sort-options {
  display: flex;
  margin-top: 20rpx;
}

.sort-item {
  flex: 1;
  text-align: center;
  font-size: 26rpx;
  color: #666;
  padding: 16rpx 0;
}

.sort-item.active {
  color: #ff6b35;
  font-weight: bold;
}

.product-list {
  padding: 20rpx;
}

.product-item {
  display: flex;
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.product-image {
  width: 240rpx;
  height: 240rpx;
}

.product-info {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-title {
  font-size: 28rpx;
  color: #333;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.product-meta {
  display: flex;
  align-items: baseline;
}

.product-price {
  font-size: 36rpx;
  color: #ff6b35;
  font-weight: bold;
}

.product-original {
  font-size: 24rpx;
  color: #999;
  text-decoration: line-through;
  margin-left: 12rpx;
}

.product-seller {
  display: flex;
  align-items: center;
}

.seller-avatar {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  margin-right: 12rpx;
}

.seller-name {
  font-size: 24rpx;
  color: #999;
}

.empty-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.load-more {
  text-align: center;
  padding: 30rpx;
  font-size: 26rpx;
  color: #999;
}
</style>
