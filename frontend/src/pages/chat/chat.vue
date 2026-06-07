<template>
  <view class="chat-page">
    <!-- 消息列表 -->
    <scroll-view 
      class="message-list" 
      scroll-y 
      :scroll-top="scrollTop"
      scroll-into-view="{{scrollIntoView}}"
      @scrolltoupper="loadMoreMessages"
    >
      <view class="loading-more" v-if="loading">加载中...</view>
      
      <view 
        class="message-item"
        :class="{ 'self': msg.senderId === currentUserId }"
        v-for="(msg, index) in messages" 
        :key="msg.id"
        :id="'msg-' + msg.id"
      >
        <image 
          class="avatar" 
          :src="getImageUrl(msg.senderId === currentUserId ? currentUserAvatar : otherUserAvatar)" 
        />
        <view class="message-content">
          <view class="message-bubble">
            <text>{{ msg.content }}</text>
          </view>
          <text class="message-time">{{ formatTime(msg.createdAt) }}</text>
        </view>
      </view>
    </scroll-view>
    
    <!-- 输入区域 -->
    <view class="input-bar">
      <input 
        class="message-input" 
        v-model="inputText" 
        placeholder="输入消息..."
        confirm-type="send"
        @confirm="sendMessage"
      />
      <button class="send-btn" @click="sendMessage" :disabled="!inputText.trim()">发送</button>
    </view>
  </view>
</template>

<script>
import { messageApi, userApi } from '../../api/index.js'
import { getAvatarUrl } from '../../utils/image.js'
import http from '../../utils/request.js'

export default {
  data() {
    return {
      otherUserId: null,
      productId: null,
      messages: [],
      inputText: '',
      currentUserId: null,
      currentUserAvatar: '',
      otherUserAvatar: '',
      otherUserName: '',
      page: 0,
      loading: false,
      hasMore: true,
      scrollTop: 0,
      timer: null
    }
  },
  onLoad(options) {
    this.otherUserId = parseInt(options.userId)
    this.productId = options.productId ? parseInt(options.productId) : null
    
    this.loadCurrentUser()
    this.loadOtherUser()
    this.loadMessages()
    
    // 定时刷新消息
    this.timer = setInterval(() => {
      this.refreshMessages()
    }, 5000)
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    getImageUrl: getAvatarUrl,
    
    async loadCurrentUser() {
      try {
        const userInfo = uni.getStorageSync('userInfo')
        if (userInfo) {
          this.currentUserId = userInfo.id
          this.currentUserAvatar = userInfo.avatar
        } else {
          const res = await userApi.getCurrentUser()
          this.currentUserId = res.data.id
          this.currentUserAvatar = res.data.avatar
        }
      } catch (e) {
        console.error('获取当前用户失败', e)
      }
    },
    
    async loadOtherUser() {
      try {
        const res = await userApi.getUserInfo(this.otherUserId)
        this.otherUserAvatar = res.data.avatar
        this.otherUserName = res.data.nickname || res.data.username
        uni.setNavigationBarTitle({
          title: this.otherUserName
        })
      } catch (e) {
        console.error('获取对方用户信息失败', e)
      }
    },
    
    async loadMessages() {
      if (this.loading) return
      this.loading = true
      
      try {
        const res = await messageApi.getConversation(this.otherUserId, {
          page: this.page,
          size: 20
        })
        const newMessages = res.data.content || []
        
        if (this.page === 0) {
          // 后端返回降序（新消息在前），直接使用
          this.messages = newMessages
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        } else {
          // 加载更多旧消息，插入到数组末尾
          this.messages = [...this.messages, ...newMessages]
        }
        
        this.hasMore = !res.data.last
        this.page++
      } catch (e) {
        console.error('加载消息失败', e)
      } finally {
        this.loading = false
      }
    },
    
    async refreshMessages() {
      try {
        const res = await messageApi.getConversation(this.otherUserId, {
          page: 0,
          size: 20
        })
        const newMessages = res.data.content || []
        
        // 检查是否有新消息
        if (newMessages.length > 0) {
          const latestId = this.messages.length > 0 ? this.messages[0].id : 0 // 数组第一个是最新的
          const hasNew = newMessages.some(m => m.id > latestId)
          
          if (hasNew) {
            // 重新加载所有消息，后端返回降序（新消息在前）
            this.page = 0
            this.messages = newMessages
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          }
        }
      } catch (e) {
        console.error('刷新消息失败', e)
      }
    },
    
    loadMoreMessages() {
      if (this.hasMore && !this.loading) {
        this.loadMessages()
      }
    },
    
    async sendMessage() {
      const content = this.inputText.trim()
      if (!content) return
      
      try {
        const data = {
          receiverId: this.otherUserId,
          content: content,
          messageType: 1
        }
        
        if (this.productId) {
          data.productId = this.productId
        }
        
        const res = await messageApi.sendMessage(data)
        
        // 新消息插入到数组开头（因为数组是降序的）
        this.messages.unshift(res.data)
        this.inputText = ''
        
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      } catch (e) {
        console.error('发送消息失败', e)
      }
    },
    
    scrollToBottom() {
      this.scrollTop = 999999
    },
    
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr.replace(/-/g, '/'))
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      
      return timeStr.substring(5, 16)
    }
  }
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

/* 消息列表 */
.message-list {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column-reverse; /* 反转显示顺序，让最新消息在下面 */
}

.loading-more {
  text-align: center;
  font-size: 24rpx;
  color: #999999;
  padding: 20rpx;
}

.message-item {
  display: flex;
  margin-bottom: 30rpx;
}

.message-item.self {
  flex-direction: row-reverse;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  margin: 0 16rpx;
}

.message-item.self .message-content {
  align-items: flex-end;
}

.message-bubble {
  background-color: #ffffff;
  padding: 20rpx 24rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.message-item.self .message-bubble {
  background-color: #ff6b35;
}

.message-bubble text {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.5;
}

.message-item.self .message-bubble text {
  color: #ffffff;
}

.message-time {
  font-size: 22rpx;
  color: #999999;
  margin-top: 8rpx;
  display: block;
}

.message-item.self .message-time {
  text-align: right;
}

/* 输入区域 */
.input-bar {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #eeeeee;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
}

.message-input {
  flex: 1;
  height: 72rpx;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.send-btn {
  width: 120rpx;
  height: 72rpx;
  background-color: #ff6b35;
  color: #ffffff;
  border: none;
  border-radius: 36rpx;
  font-size: 28rpx;
  margin-left: 16rpx;
  line-height: 72rpx;
  padding: 0;
}

.send-btn[disabled] {
  background-color: #cccccc;
}
</style>
