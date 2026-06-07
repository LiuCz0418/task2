<template>
  <view class="settings-page">
    <!-- 真机调试配置 -->
    <view class="section">
      <view class="section-title">真机调试配置</view>
      <view class="setting-item">
        <view class="item-label">当前环境</view>
        <view class="item-value">{{ platform }}</view>
      </view>
      <view class="setting-item" v-if="platform !== 'devtools'">
        <view class="item-label">服务器IP</view>
        <input 
          class="item-input" 
          v-model="serverIp" 
          placeholder="请输入电脑的局域网IP"
          @blur="saveConfig"
        />
      </view>
      <view class="setting-item" v-if="platform !== 'devtools'">
        <view class="item-label">服务器端口</view>
        <input 
          class="item-input" 
          v-model="serverPort" 
          placeholder="默认8080"
          type="number"
          @blur="saveConfig"
        />
      </view>
      <view class="setting-item" v-if="platform !== 'devtools'">
        <view class="item-label">当前地址</view>
        <view class="item-value">{{ currentUrl }}</view>
      </view>
      <view class="tip-box" v-if="platform !== 'devtools'">
        <text class="tip-title">💡 如何获取电脑IP？</text>
        <text class="tip-text">1. Windows: 打开cmd，输入 ipconfig</text>
        <text class="tip-text">2. Mac: 系统偏好设置 → 网络 → 查看IP地址</text>
        <text class="tip-text">3. 确保手机和电脑在同一WiFi网络下</text>
        <text class="tip-text">4. 后端需要配置允许跨域访问</text>
      </view>
      <button class="test-btn" @click="testConnection" v-if="platform !== 'devtools'">
        测试连接
      </button>
      
      <!-- 图片测试区域 -->
      <view class="image-test-section" v-if="platform !== 'devtools' && showImageTest">
        <view class="test-title">图片加载测试</view>
        <view class="test-url">URL: {{ testImageUrl }}</view>
        <image 
          class="test-image" 
          :src="testImageUrl" 
          mode="aspectFit"
          @error="handleImageError"
          @load="handleImageLoad"
        />
        <view class="test-result" :class="imageLoadStatus">
          {{ imageLoadMessage }}
        </view>
      </view>
    </view>

    <!-- 应用信息 -->
    <view class="section">
      <view class="section-title">应用信息</view>
      <view class="setting-item">
        <view class="item-label">版本号</view>
        <view class="item-value">1.0.0</view>
      </view>
      <view class="setting-item">
        <view class="item-label">当前平台</view>
        <view class="item-value">{{ systemInfo.platform }}</view>
      </view>
      <view class="setting-item">
        <view class="item-label">系统版本</view>
        <view class="item-value">{{ systemInfo.system }}</view>
      </view>
    </view>

    <!-- 缓存管理 -->
    <view class="section">
      <view class="section-title">缓存管理</view>
      <button class="action-btn" @click="clearCache">清除缓存</button>
    </view>
  </view>
</template>

<script>
import http from '../../utils/request.js'

export default {
  data() {
    return {
      serverIp: '',
      serverPort: '8080',
      platform: '',
      systemInfo: {},
      showImageTest: false,
      testImageUrl: '',
      imageLoadStatus: '',
      imageLoadMessage: '加载中...'
    }
  },
  computed: {
    currentUrl() {
      if (this.serverIp) {
        return `http://${this.serverIp}:${this.serverPort}`
      }
      return 'http://localhost:8080'
    }
  },
  onLoad() {
    this.loadConfig()
    this.getSystemInfo()
  },
  methods: {
    loadConfig() {
      const serverIp = uni.getStorageSync('serverIp')
      const serverPort = uni.getStorageSync('serverPort')
      if (serverIp) {
        this.serverIp = serverIp
      }
      if (serverPort) {
        this.serverPort = serverPort
      }
    },

    getSystemInfo() {
      this.systemInfo = uni.getSystemInfoSync()
      this.platform = this.systemInfo.platform
    },

    saveConfig() {
      if (this.serverIp) {
        uni.setStorageSync('serverIp', this.serverIp.trim())
      }
      if (this.serverPort) {
        uni.setStorageSync('serverPort', this.serverPort.trim())
      }
      uni.showToast({
        title: '配置已保存',
        icon: 'success'
      })
    },

    async testConnection() {
      if (!this.serverIp) {
        uni.showToast({
          title: '请先输入服务器IP',
          icon: 'none'
        })
        return
      }

      uni.showLoading({
        title: '测试连接中...'
      })

      try {
        // 保存配置
        this.saveConfig()
        
        // 延迟一下确保配置生效
        await new Promise(resolve => setTimeout(resolve, 300))
        
        // 测试连接
        const response = await http.request({
          url: '/api/categories',
          method: 'GET'
        })

        uni.hideLoading()
        uni.showModal({
          title: '连接成功',
          content: `成功连接到服务器：${this.currentUrl}`,
          showCancel: false
        })
        
        // 连接成功后测试图片加载
        this.testImageLoad()
      } catch (e) {
        uni.hideLoading()
        console.error('连接测试失败', e)
        uni.showModal({
          title: '连接失败',
          content: `无法连接到服务器：${this.currentUrl}\n\n请检查：\n1. IP地址是否正确\n2. 后端服务是否启动\n3. 手机和电脑是否在同一网络\n4. 防火墙是否允许访问`,
          showCancel: false
        })
      }
    },

    testImageLoad() {
      // 显示图片测试区域
      this.showImageTest = true
      // 使用图片代理API
      this.testImageUrl = `${this.currentUrl}/api/upload/image-proxy?path=${encodeURIComponent('/uploads/product_1.jpg')}`
      this.imageLoadStatus = 'loading'
      this.imageLoadMessage = '图片加载中...'
      console.log('[图片测试] URL:', this.testImageUrl)
    },

    handleImageLoad() {
      this.imageLoadStatus = 'success'
      this.imageLoadMessage = '✅ 图片加载成功！'
      console.log('[图片测试] 加载成功')
      uni.showToast({
        title: '图片加载成功',
        icon: 'success'
      })
    },

    handleImageError(e) {
      this.imageLoadStatus = 'error'
      this.imageLoadMessage = '❌ 图片加载失败！'
      console.error('[图片测试] 加载失败', e)
      uni.showModal({
        title: '图片加载失败',
        content: '无法加载服务器图片。\n\n可能原因：\n1. 微信小程序需要HTTPS域名\n2. 后端跨域配置问题\n3. 防火墙阻止了请求',
        showCancel: false
      })
    },

    clearCache() {
      uni.showModal({
        title: '清除缓存',
        content: '确定要清除所有缓存数据吗？（不包括登录信息）',
        success: (res) => {
          if (res.confirm) {
            try {
              // 保留token和userInfo
              const token = uni.getStorageSync('token')
              const userInfo = uni.getStorageSync('userInfo')
              const serverIp = uni.getStorageSync('serverIp')
              const serverPort = uni.getStorageSync('serverPort')
              
              uni.clearStorageSync()
              
              // 恢复重要数据
              if (token) uni.setStorageSync('token', token)
              if (userInfo) uni.setStorageSync('userInfo', userInfo)
              if (serverIp) uni.setStorageSync('serverIp', serverIp)
              if (serverPort) uni.setStorageSync('serverPort', serverPort)
              
              uni.showToast({
                title: '缓存已清除',
                icon: 'success'
              })
            } catch (e) {
              uni.showToast({
                title: '清除失败',
                icon: 'none'
              })
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.section {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.item-label {
  font-size: 28rpx;
  color: #333;
}

.item-value {
  font-size: 26rpx;
  color: #999;
  max-width: 400rpx;
  text-align: right;
  word-break: break-all;
}

.item-input {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #333;
  padding: 10rpx 20rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 8rpx;
  margin-left: 20rpx;
}

.tip-box {
  background-color: #fff8e1;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-top: 30rpx;
}

.tip-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #ff6b35;
  margin-bottom: 16rpx;
}

.tip-text {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 40rpx;
  margin-bottom: 8rpx;
}

.test-btn, .action-btn {
  width: 100%;
  background-color: #ff6b35;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 28rpx;
  margin-top: 30rpx;
}

.action-btn {
  background-color: #fff;
  color: #ff6b35;
  border: 2rpx solid #ff6b35;
}

/* 图片测试区域 */
.image-test-section {
  margin-top: 40rpx;
  padding: 30rpx;
  background-color: #f8f9fa;
  border-radius: 12rpx;
}

.test-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.test-url {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 20rpx;
  word-break: break-all;
}

.test-image {
  width: 100%;
  height: 400rpx;
  background-color: #fff;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
}

.test-result {
  text-align: center;
  font-size: 28rpx;
  padding: 20rpx;
  border-radius: 8rpx;
}

.test-result.loading {
  background-color: #fff3cd;
  color: #856404;
}

.test-result.success {
  background-color: #d4edda;
  color: #155724;
}

.test-result.error {
  background-color: #f8d7da;
  color: #721c24;
}
</style>
