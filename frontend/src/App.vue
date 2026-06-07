<template>
  <view class="app">
    <slot />
  </view>
</template>

<script>
import { messageApi } from './api/index.js'

export default {
  onLaunch: function() {
    console.log('App Launch')
    // 检查登录状态
    const token = uni.getStorageSync('token')
    if (token) {
      this.globalData.isLogin = true
      this.globalData.token = token
      this.globalData.userInfo = uni.getStorageSync('userInfo') || {}
      // 启动未读消息轮询
      this.startUnreadPolling()
    }
  },
  onShow: function() {
    console.log('App Show')
    // 每次切回App时刷新未读数
    if (this.globalData.isLogin) {
      this.updateUnreadBadge()
    }
  },
  onHide: function() {
    console.log('App Hide')
  },
  globalData: {
    isLogin: false,
    token: '',
    userInfo: {},
    unreadTimer: null
  },
  methods: {
    // 获取未读消息数并更新TabBar角标
    async updateUnreadBadge() {
      try {
        // 只在 TabBar 页面设置角标
        const pages = getCurrentPages()
        const currentPage = pages[pages.length - 1]
        if (!currentPage) return
        
        // 检查当前页面是否是 TabBar 页面
        const tabBarPages = ['/pages/index/index', '/pages/publish/publish', '/pages/chat/list', '/pages/user/user']
        const currentRoute = currentPage.route
        if (!tabBarPages.includes('/' + currentRoute)) return
        
        const token = uni.getStorageSync('token')
        if (!token) {
          uni.removeTabBarBadge({ index: 2 })
          return
        }
        const res = await messageApi.getUnreadCount()
        const count = res.data || 0
        if (count > 0) {
          uni.setTabBarBadge({
            index: 2,
            text: count > 99 ? '99+' : String(count)
          })
        } else {
          uni.removeTabBarBadge({ index: 2 })
        }
      } catch (e) {
        // 静默处理（可能未登录）
      }
    },
    // 定时轮询未读消息
    startUnreadPolling() {
      this.updateUnreadBadge()
      if (this.globalData.unreadTimer) {
        clearInterval(this.globalData.unreadTimer)
      }
      this.globalData.unreadTimer = setInterval(() => {
        this.updateUnreadBadge()
      }, 15000)  // 每15秒轮询
    },
    // 停止轮询
    stopUnreadPolling() {
      if (this.globalData.unreadTimer) {
        clearInterval(this.globalData.unreadTimer)
        this.globalData.unreadTimer = null
      }
    }
  }
}
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.container {
  padding: 20rpx;
}

/* 主题色 */
.text-primary {
  color: #ff6b35;
}

.bg-primary {
  background-color: #ff6b35;
}

/* 按钮样式 */
.btn-primary {
  background-color: #ff6b35;
  color: #ffffff;
  border: none;
  border-radius: 8rpx;
  padding: 20rpx 40rpx;
  font-size: 28rpx;
}

.btn-primary:active {
  background-color: #e55a2b;
}

/* 卡片样式 */
.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

/* 价格样式 */
.price {
  color: #ff6b35;
  font-weight: bold;
}

/* 文本省略 */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
