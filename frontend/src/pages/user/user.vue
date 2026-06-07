<template>
  <view class="user-page">
    <!-- 用户信息卡片 -->
    <view class="user-card" @click="goProfile">
      <view v-if="isLogin" class="user-info">
        <image class="avatar" :src="getImageUrl(userInfo.avatar)" />
        <view class="info-detail">
          <text class="nickname">{{ userInfo.nickname || userInfo.username }}</text>
          <view class="credit-row">
            <text class="credit-label">信用分：</text>
            <text class="credit-score">{{ userInfo.creditScore || 100 }}</text>
          </view>
        </view>
        <text class="arrow">›</text>
      </view>
      <view v-else class="login-tip" @click="goLogin">
        <image class="default-avatar" src="https://api.dicebear.com/7.x/avataaars/svg?seed=guest" />
        <text class="login-text">点击登录</text>
      </view>
    </view>
    
    <!-- 订单区域 -->
    <view class="order-section card">
      <view class="section-header">
        <text class="section-title">我的订单</text>
        <view class="view-all" @click="goOrderList()">
          <text>全部订单</text>
          <text class="arrow">›</text>
        </view>
      </view>
      <view class="order-tabs">
        <view class="order-tab" @click="goOrderList('PENDING')">
          <text class="tab-icon">&#x1F4B3;</text>
          <text class="tab-text">待付款</text>
        </view>
        <view class="order-tab" @click="goOrderList('PAID')">
          <text class="tab-icon">&#x1F4E6;</text>
          <text class="tab-text">待发货</text>
        </view>
        <view class="order-tab" @click="goOrderList('SHIPPED')">
          <text class="tab-icon">&#x1F69A;</text>
          <text class="tab-text">待收货</text>
        </view>
        <view class="order-tab" @click="goOrderList('COMPLETED')">
          <text class="tab-icon">&#x2705;</text>
          <text class="tab-text">已完成</text>
        </view>
      </view>
    </view>
    
    <!-- 卖出区域 -->
    <view class="sell-section card">
      <view class="section-header">
        <text class="section-title">我卖出的</text>
        <view class="view-all" @click="goSoldList()">
          <text>查看全部</text>
          <text class="arrow">›</text>
        </view>
      </view>
      <view class="order-tabs">
        <view class="order-tab" @click="goSoldList('PAID')">
          <text class="tab-icon">&#x1F4E6;</text>
          <text class="tab-text">待发货</text>
        </view>
        <view class="order-tab" @click="goSoldList('SHIPPED')">
          <text class="tab-icon">&#x1F69A;</text>
          <text class="tab-text">待收货</text>
        </view>
        <view class="order-tab" @click="goSoldList('COMPLETED')">
          <text class="tab-icon">&#x2705;</text>
          <text class="tab-text">已完成</text>
        </view>
      </view>
    </view>
    
    <!-- 功能菜单 -->
    <view class="menu-section card">
      <view class="menu-item" @click="goMyProducts">
        <text class="menu-icon">&#x1F4E6;</text>
        <text class="menu-text">我发布的</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goCollections">
        <text class="menu-icon">&#x2764;</text>
        <text class="menu-text">我的收藏</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goSettings">
        <text class="menu-icon">&#x2699;</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
    
    <!-- 管理员工具（仅 ADMIN 可见） -->
    <view v-if="isLogin && isAdmin" class="menu-section card admin-section">
      <view class="admin-header">
        <text class="admin-tag">管理员</text>
        <text class="section-title">管理工具</text>
      </view>
      <view class="menu-item" @click="goReviewAudit">
        <text class="menu-icon">🛡️</text>
        <view class="menu-main">
          <text class="menu-text">审核管理</text>
          <text class="menu-sub">注册审核 · 商品上架审核 · 差评信用分审核</text>
        </view>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section" v-if="isLogin">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import { userApi } from '../../api/index.js'
import { getAvatarUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      isLogin: false,
      userInfo: {}
    }
  },
  computed: {
    isAdmin() {
      return this.userInfo && this.userInfo.role === 'ADMIN'
    }
  },
  onShow() {
    this.checkLogin()
    if (this.isLogin) {
      this.loadUserInfo()
    }
  },
  methods: {
    getImageUrl: getAvatarUrl,
    
    checkLogin() {
      const token = uni.getStorageSync('token')
      this.isLogin = !!token
      if (this.isLogin) {
        this.userInfo = uni.getStorageSync('userInfo') || {}
      }
    },
    
    async loadUserInfo() {
      try {
        const res = await userApi.getCurrentUser()
        this.userInfo = res.data
        uni.setStorageSync('userInfo', res.data)
      } catch (e) {
        console.error('获取用户信息失败', e)
      }
    },
    
    goLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },
    
    goProfile() {
      if (!this.isLogin) {
        this.goLogin()
        return
      }
      // 可以跳转到个人资料编辑页
    },
    
    goOrderList(status) {
      if (!this.isLogin) {
        this.goLogin()
        return
      }
      let url = '/pages/order/order?type=buy'
      if (status) {
        url += `&status=${status}`
      }
      uni.navigateTo({ url })
    },
    
    goSoldList(status) {
      if (!this.isLogin) {
        this.goLogin()
        return
      }
      let url = '/pages/order/order?type=sell'
      if (status) {
        url += `&status=${status}`
      }
      uni.navigateTo({ url })
    },
    
    goMyProducts() {
      if (!this.isLogin) {
        this.goLogin()
        return
      }
      uni.navigateTo({
        url: '/pages/user/products'
      })
    },
    
    goCollections() {
      uni.navigateTo({
        url: '/pages/user/favorites'
      })
    },
    
    goSettings() {
      uni.navigateTo({
        url: '/pages/user/settings'
      })
    },

    goReviewAudit() {
      uni.navigateTo({
        url: '/pages/admin/review-audit'
      })
    },
    
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            this.isLogin = false
            this.userInfo = {}
            // 停止轮询并清除角标
            const app = getApp()
            app.globalData.isLogin = false
            app.stopUnreadPolling()
            uni.removeTabBarBadge({ index: 2 })
            uni.showToast({
              title: '已退出登录',
              icon: 'none'
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 30rpx;
}

/* 用户卡片 */
.user-card {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8f5a 100%);
  padding: 60rpx 30rpx;
  padding-top: 100rpx;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
}

.info-detail {
  flex: 1;
  margin-left: 24rpx;
}

.nickname {
  font-size: 36rpx;
  color: #ffffff;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.credit-row {
  display: flex;
  align-items: center;
}

.credit-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.credit-score {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: bold;
}

.arrow {
  font-size: 40rpx;
  color: rgba(255, 255, 255, 0.8);
}

.login-tip {
  display: flex;
  align-items: center;
}

.default-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  opacity: 0.8;
}

.login-text {
  font-size: 32rpx;
  color: #ffffff;
  margin-left: 24rpx;
}

/* 通用卡片 */
.card {
  background-color: #ffffff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 30rpx;
  color: #333333;
  font-weight: bold;
}

.view-all {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: #999999;
}

/* 订单标签 */
.order-tabs {
  display: flex;
  justify-content: space-around;
}

.order-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tab-icon {
  font-size: 48rpx;
  margin-bottom: 10rpx;
}

.tab-text {
  font-size: 24rpx;
  color: #666666;
}

/* 菜单 */
.menu-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 40rpx;
  width: 60rpx;
}

.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.menu-arrow {
  font-size: 32rpx;
  color: #cccccc;
}

/* 退出登录 */
.logout-section {
  padding: 40rpx 20rpx;
}

.logout-btn {
  width: 100%;
  height: 88rpx;
  background-color: #ffffff;
  color: #ff4d4f;
  border: 1rpx solid #ff4d4f;
  border-radius: 44rpx;
  font-size: 30rpx;
}

/* 管理员区块 */
.admin-section {
  border: 2rpx solid #e8f5e9;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
}

.admin-header {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 20rpx;
}

.admin-tag {
  background: #43a047;
  color: #fff;
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  border-radius: 20rpx;
  font-weight: bold;
  letter-spacing: 2rpx;
}

.menu-main {
  flex: 1;
}

.menu-sub {
  font-size: 22rpx;
  color: #aaa;
  display: block;
  margin-top: 4rpx;
}
</style>
