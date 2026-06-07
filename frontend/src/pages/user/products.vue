<template>
  <view class="my-products-page">
    <!-- 标签栏 -->
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: currentTab === 'selling' }" @click="switchTab('selling')">
        <text>在售</text>
      </view>
      <view class="tab-item" :class="{ active: currentTab === 'pending' }" @click="switchTab('pending')">
        <text>待审核</text>
      </view>
      <view class="tab-item" :class="{ active: currentTab === 'rejected' }" @click="switchTab('rejected')">
        <text>已拒绝</text>
      </view>
      <view class="tab-item" :class="{ active: currentTab === 'sold' }" @click="switchTab('sold')">
        <text>已售</text>
      </view>
    </view>

    <!-- 操作栏 -->
    <view class="action-bar" v-if="!editMode">
      <button class="edit-btn" @click="enterEditMode">管理</button>
    </view>
    <view class="action-bar edit-action-bar" v-else>
      <view class="left">
        <checkbox-group @change="handleSelectAll">
          <label>
            <checkbox :checked="isAllSelected" color="#ff6b35" />
            <text class="checkbox-text">全选</text>
          </label>
        </checkbox-group>
      </view>
      <view class="right">
        <button class="cancel-btn" @click="exitEditMode">取消</button>
        <button class="delete-btn" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
          删除({{ selectedIds.length }})
        </button>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="product-list">
      <view 
        class="product-item" 
        v-for="item in products" 
        :key="item.id"
      >
        <!-- 编辑模式：显示复选框 -->
        <view class="checkbox-wrapper" v-if="editMode" @click.stop="toggleSelect(item.id)">
          <checkbox-group>
            <label>
              <checkbox :checked="selectedIds.includes(item.id)" color="#ff6b35" />
            </label>
          </checkbox-group>
        </view>

        <!-- 商品内容 -->
        <view class="product-content" @click="!editMode && goDetail(item.id)">
          <image class="product-image" :src="getImageUrl(item.imageUrl)" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-price">¥{{ item.price }}</text>
            <view class="product-meta">
              <text class="view-count">{{ item.viewCount || 0 }}次浏览</text>
              <text class="product-time">{{ item.createdAt }}</text>
            </view>
            <!-- 待审核提示 -->
            <view v-if="item.status === 3" class="status-tag pending">待审核</view>
            <!-- 拒绝原因提示 -->
            <view v-if="item.status === 4" class="status-tag rejected">
              已拒绝：{{ item.rejectReason || '内容违规' }}
            </view>
          </view>
        </view>

        <!-- 非编辑模式：操作按钮 -->
        <view class="item-actions" v-if="!editMode">
          <button
            class="action-btn edit"
            v-if="item.status === 3 || item.status === 4"
            @click.stop="goEdit(item.id)"
          >编辑</button>
          <button class="action-btn delete" @click.stop="handleSingleDelete(item.id)">删除</button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="products.length === 0 && !loading" class="empty">
      <text class="empty-text">{{ currentTab === 'selling' ? '暂无在售商品' : currentTab === 'pending' ? '暂无待审核商品' : currentTab === 'rejected' ? '暂无被拒绝商品' : '暂无已售商品' }}</text>
      <button class="go-publish-btn" @click="goPublish" v-if="currentTab === 'selling'">去发布</button>
    </view>

    <!-- 加载 -->
    <view class="loading-status" v-if="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script>
import { productApi } from '../../api/index.js'
import { getImageUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      currentTab: 'selling',
      products: [],
      loading: false,
      page: 0,
      editMode: false,
      selectedIds: []
    }
  },
  computed: {
    isAllSelected() {
      return this.products.length > 0 && this.selectedIds.length === this.products.length
    }
  },
  onLoad() {
    this.loadProducts()
  },
  methods: {
    getImageUrl,

    switchTab(tab) {
      this.currentTab = tab
      this.page = 0
      this.products = []
      this.exitEditMode()
      this.loadProducts()
    },

    async loadProducts() {
      this.loading = true
      try {
        const res = await productApi.getMyProducts({
          page: this.page,
          size: 20,
          status: { selling: 1, pending: 3, rejected: 4, sold: 2 }[this.currentTab]
        })
        if (res.data && res.data.content) {
          this.products = res.data.content
        } else if (Array.isArray(res.data)) {
          this.products = res.data
        }
      } catch (e) {
        console.error('加载商品失败', e)
      } finally {
        this.loading = false
      }
    },

    enterEditMode() {
      this.editMode = true
      this.selectedIds = []
    },

    exitEditMode() {
      this.editMode = false
      this.selectedIds = []
    },

    toggleSelect(id) {
      const index = this.selectedIds.indexOf(id)
      if (index > -1) {
        this.selectedIds.splice(index, 1)
      } else {
        this.selectedIds.push(id)
      }
    },

    handleSelectAll(e) {
      if (e.detail.value.length > 0) {
        this.selectedIds = this.products.map(item => item.id)
      } else {
        this.selectedIds = []
      }
    },

    handleSingleDelete(id) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除该商品吗？',
        success: async (res) => {
          if (res.confirm) {
            await this.deleteProducts([id])
          }
        }
      })
    },

    handleBatchDelete() {
      if (this.selectedIds.length === 0) {
        uni.showToast({
          title: '请选择要删除的商品',
          icon: 'none'
        })
        return
      }

      uni.showModal({
        title: '确认删除',
        content: `确定要删除选中的 ${this.selectedIds.length} 个商品吗？`,
        success: async (res) => {
          if (res.confirm) {
            await this.deleteProducts(this.selectedIds)
          }
        }
      })
    },

    async deleteProducts(ids) {
      uni.showLoading({
        title: '删除中...'
      })

      try {
        if (ids.length === 1) {
          // 单个删除
          await productApi.deleteProduct(ids[0])
        } else {
          // 批量删除
          await productApi.batchDeleteProducts(ids)
        }

        uni.showToast({
          title: '删除成功',
          icon: 'success'
        })

        // 重新加载列表
        this.exitEditMode()
        this.loadProducts()
      } catch (e) {
        console.error('删除失败', e)
        uni.showToast({
          title: e.message || '删除失败',
          icon: 'none',
          duration: 2000
        })
      } finally {
        uni.hideLoading()
      }
    },

    goDetail(id) {
      uni.navigateTo({
        url: `/pages/detail/detail?id=${id}`
      })
    },

    goEdit(id) {
      // publish 是 tabBar 页面，不能用 navigateTo，改用 storage 传参
      uni.setStorageSync('editProductId', id)
      uni.switchTab({
        url: '/pages/publish/publish'
      })
    },

    goPublish() {
      uni.switchTab({
        url: '/pages/publish/publish'
      })
    }
  }
}
</script>

<style scoped>
.my-products-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.tab-bar {
  display: flex;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #666;
  border-bottom: 4rpx solid transparent;
}

.tab-item.active {
  color: #ff6b35;
  font-weight: bold;
  border-bottom-color: #ff6b35;
}

/* 操作栏样式 */
.action-bar {
  background-color: #fff;
  padding: 20rpx;
  display: flex;
  justify-content: flex-end;
  border-bottom: 1rpx solid #f0f0f0;
}

.edit-btn {
  background-color: #fff;
  color: #ff6b35;
  border: 2rpx solid #ff6b35;
  border-radius: 8rpx;
  padding: 12rpx 32rpx;
  font-size: 26rpx;
  line-height: normal;
}

.edit-action-bar {
  justify-content: space-between;
  align-items: center;
}

.edit-action-bar .left {
  display: flex;
  align-items: center;
}

.checkbox-text {
  margin-left: 10rpx;
  font-size: 28rpx;
  color: #333;
}

.edit-action-bar .right {
  display: flex;
  gap: 20rpx;
}

.cancel-btn {
  background-color: #fff;
  color: #666;
  border: 2rpx solid #ddd;
  border-radius: 8rpx;
  padding: 12rpx 32rpx;
  font-size: 26rpx;
  line-height: normal;
}

.delete-btn {
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  padding: 12rpx 32rpx;
  font-size: 26rpx;
  line-height: normal;
}

.delete-btn[disabled] {
  background-color: #ffcccc;
  color: #fff;
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
  position: relative;
}

/* 复选框样式 */
.checkbox-wrapper {
  padding: 30rpx 20rpx;
  display: flex;
  align-items: center;
}

/* 商品内容 */
.product-content {
  flex: 1;
  display: flex;
}

.product-image {
  width: 220rpx;
  height: 220rpx;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 28rpx;
  color: #333;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
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
}

.view-count, .product-time {
  font-size: 22rpx;
  color: #999;
}

/* 操作按钮 */
.item-actions {
  padding: 20rpx 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.action-btn {
  padding: 10rpx 24rpx;
  font-size: 24rpx;
  border-radius: 8rpx;
  line-height: normal;
}

.action-btn.delete {
  background-color: #fff;
  color: #ff4444;
  border: 2rpx solid #ff4444;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 30rpx;
}

.go-publish-btn {
  background-color: #ff6b35;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  padding: 16rpx 60rpx;
  font-size: 28rpx;
}

.loading-status {
  text-align: center;
  padding: 30rpx;
  font-size: 26rpx;
  color: #999;
}

.status-tag {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  margin-top: 8rpx;
}
.status-tag.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}
.status-tag.rejected {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.action-btn.edit {
  background-color: #fff;
  color: #ff6b35;
  border: 2rpx solid #ff6b35;
  margin-bottom: 12rpx;
}
</style>
