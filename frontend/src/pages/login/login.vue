<template>
  <view class="login-page">
    <view class="logo-section">
      <text class="app-name">二手集市</text>
      <text class="app-slogan">让闲置物品找到新主人</text>
    </view>
    
    <view class="form-section">
      <!-- 登录/注册切换 -->
      <view class="tab-bar">
        <view class="tab-item" :class="{ active: !isRegister }" @click="isRegister = false">
          <text>登录</text>
        </view>
        <view class="tab-item" :class="{ active: isRegister }" @click="isRegister = true">
          <text>注册</text>
        </view>
      </view>
      
      <!-- 表单 -->
      <view class="form-content">
        <view class="form-item">
          <text class="form-icon">&#x1F464;</text>
          <input 
            class="form-input" 
            v-model="form.username" 
            placeholder="请输入用户名" 
            maxlength="50"
          />
        </view>
        
        <view class="form-item">
          <text class="form-icon">&#x1F512;</text>
          <input 
            class="form-input" 
            v-model="form.password" 
            :password="!showPassword"
            placeholder="请输入密码" 
            maxlength="20"
          />
          <text class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? '&#x1F441;' : '&#x1F576;' }}
          </text>
        </view>
        
        <view class="form-item" v-if="isRegister">
          <text class="form-icon">&#x1F4C4;</text>
          <input
            class="form-input"
            v-model="form.idCard"
            placeholder="请输入身份证号（必填）"
            maxlength="18"
          />
        </view>

        <view class="form-item" v-if="isRegister">
          <text class="form-icon">&#x1F4F1;</text>
          <input 
            class="form-input" 
            v-model="form.phoneNumber" 
            placeholder="请输入手机号（选填）" 
            type="number"
            maxlength="11"
          />
        </view>
        
        <view class="form-item" v-if="isRegister">
          <text class="form-icon">&#x2709;</text>
          <input 
            class="form-input" 
            v-model="form.email" 
            placeholder="请输入邮箱（选填）" 
          />
        </view>
        
        <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '处理中...' : (isRegister ? '注册' : '登录') }}
        </button>
      </view>
    </view>
    
    <view class="agreement">
      <text>登录/注册即表示同意</text>
      <text class="link">《用户协议》</text>
      <text>和</text>
      <text class="link">《隐私政策》</text>
    </view>
  </view>
</template>

<script>
import { userApi } from '../../api/index.js'

export default {
  data() {
    return {
      isRegister: false,
      showPassword: false,
      form: {
        username: '',
        password: '',
        idCard: '',
        phoneNumber: '',
        email: ''
      },
      submitting: false
    }
  },
  methods: {
    validateForm() {
      if (!this.form.username?.trim()) {
        uni.showToast({ title: '请输入用户名', icon: 'none' })
        return false
      }
      if (this.form.username.length < 3) {
        uni.showToast({ title: '用户名至少3个字符', icon: 'none' })
        return false
      }
      if (!this.form.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return false
      }
      if (this.form.password.length < 6) {
        uni.showToast({ title: '密码至少6个字符', icon: 'none' })
        return false
      }
      if (this.isRegister) {
        if (!this.form.idCard?.trim()) {
          uni.showToast({ title: '请输入身份证号', icon: 'none' })
          return false
        }
        const idCardReg = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dX]$/
        if (!idCardReg.test(this.form.idCard.toUpperCase())) {
          uni.showToast({ title: '身份证号格式不正确', icon: 'none' })
          return false
        }
      }
      return true
    },

    async handleSubmit() {
      if (!this.validateForm()) return

      this.submitting = true
      try {
        let res
        if (this.isRegister) {
          res = await userApi.register({
            username: this.form.username.trim(),
            password: this.form.password,
            idCard: this.form.idCard.trim().toUpperCase(),
            phoneNumber: this.form.phoneNumber || null,
            email: this.form.email || null
          })
        } else {
          res = await userApi.login({
            username: this.form.username.trim(),
            password: this.form.password
          })
        }
        
        // 保存登录信息
        uni.setStorageSync('token', res.data.token)
        uni.setStorageSync('userInfo', {
          id: res.data.id,
          username: res.data.username,
          nickname: res.data.nickname,
          avatar: res.data.avatar,
          role: res.data.role
        })
        
        uni.showToast({
          title: this.isRegister ? '注册成功' : '登录成功',
          icon: 'success'
        })
        
        // 启动未读消息轮询
        const app = getApp()
        app.globalData.isLogin = true
        app.startUnreadPolling()
        
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      } catch (e) {
        console.error('提交失败', e)
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8f5a 100%);
  padding: 80rpx 40rpx;
}

/* Logo 区域 */
.logo-section {
  text-align: center;
  margin-bottom: 80rpx;
}

.app-name {
  display: block;
  font-size: 64rpx;
  color: #ffffff;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.app-slogan {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 表单区域 */
.form-section {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.1);
}

.tab-bar {
  display: flex;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx;
  font-size: 32rpx;
  color: #999999;
  border-bottom: 4rpx solid transparent;
}

.tab-item.active {
  color: #ff6b35;
  border-bottom-color: #ff6b35;
  font-weight: bold;
}

.form-item {
  display: flex;
  align-items: center;
  height: 100rpx;
  border-bottom: 1rpx solid #f0f0f0;
  margin-bottom: 20rpx;
}

.form-icon {
  font-size: 40rpx;
  width: 60rpx;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
}

.toggle-password {
  font-size: 40rpx;
  padding: 10rpx;
}

.submit-btn {
  width: 100%;
  height: 96rpx;
  background-color: #ff6b35;
  color: #ffffff;
  border: none;
  border-radius: 48rpx;
  font-size: 32rpx;
  margin-top: 40rpx;
}

.submit-btn[disabled] {
  background-color: #ffb399;
}

/* 协议 */
.agreement {
  text-align: center;
  margin-top: 60rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.agreement .link {
  color: #ffffff;
}
</style>
