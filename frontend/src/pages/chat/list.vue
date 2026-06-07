<template>
  <view class="message-list-page">
    <view v-if="!isLogin" class="login-tip">
      <text>请先登录查看消息</text>
      <button class="login-btn" @click="goLogin">去登录</button>
    </view>
    
    <view v-else-if="conversations.length === 0" class="empty-tip">
      <text>暂无消息</text>
    </view>
    
    <view v-else class="conversation-list">
      <view 
        class="conversation-item" 
        v-for="item in conversations" 
        :key="item.userId"
        @click="goChat(item.userId)"
      >
        <image class="avatar" :src="getImageUrl(item.avatar)" />
        <view class="conversation-info">
          <view class="info-top">
            <text class="nickname">{{ item.nickname || item.username }}</text>
            <text class="time">{{ formatTime(item.lastTime) }}</text>
          </view>
          <text class="last-message text-ellipsis">{{ item.lastMessage }}</text>
        </view>
        <view class="unread-badge" v-if="item.unreadCount > 0">
          <text>{{ item.unreadCount > 99 ? '99+' : item.unreadCount }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { messageApi } from '../../api/index.js'
import { getAvatarUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      conversations: [],
      isLogin: false
    }
  },
  onShow() {
    this.checkLogin()
    if (this.isLogin) {
      this.loadConversations()
    }
  },
  methods: {
    getImageUrl: getAvatarUrl,
    
    checkLogin() {
      const token = uni.getStorageSync('token')
      this.isLogin = !!token
    },
    
    async loadConversations() {
      try {
        const res = await messageApi.getConversationList()
        this.conversations = res.data || []
        // 进入消息列表后刷新角标
        const app = getApp()
        if (app.updateUnreadBadge) {
          app.updateUnreadBadge()
        }
      } catch (e) {
        console.error('加载会话列表失败', e)
      }
    },
    
    goChat(userId) {
      uni.navigateTo({
        url: `/pages/chat/chat?userId=${userId}`
      })
    },
    
    goLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },
    
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr.replace(/-/g, '/'))
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      
      // 判断是否是今年
      if (date.getFullYear() === now.getFullYear()) {
        return timeStr.substring(5, 10)
      }
      return timeStr.substring(0, 10)
    }
  }
}
</script>

<style scoped>
.message-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-tip, .empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.login-tip text, .empty-tip text {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 30rpx;
}

.login-btn {
  width: 240rpx;
  height: 72rpx;
  background-color: #ff6b35;
  color: #ffffff;
  border: none;
  border-radius: 36rpx;
  font-size: 28rpx;
}

/* 会话列表 */
.conversation-list {
  background-color: #ffffff;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.conversation-item:active {
  background-color: #f5f5f5;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.conversation-info {
  flex: 1;
  margin-left: 20rpx;
  overflow: hidden;
}

.info-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.nickname {
  font-size: 30rpx;
  color: #333333;
  font-weight: 500;
}

.time {
  font-size: 24rpx;
  color: #999999;
}

.last-message {
  font-size: 26rpx;
  color: #999999;
}

.unread-badge {
  min-width: 36rpx;
  height: 36rpx;
  background-color: #ff4d4f;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10rpx;
  margin-left: 16rpx;
}

.unread-badge text {
  font-size: 22rpx;
  color: #ffffff;
}
</style>
