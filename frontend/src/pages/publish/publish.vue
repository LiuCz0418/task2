<template>
  <view class="publish-page">
    <!-- 已拒绝原因提示 -->
    <view class="reject-tip card" v-if="rejectReason">
      <text class="reject-tip-title">📋 审核未通过原因</text>
      <text class="reject-tip-content">{{ rejectReason }}</text>
    </view>

    <!-- 图片上传区域 -->
    <view class="image-section card">
      <text class="section-title">商品图片（最多9张）</text>
      <view class="image-list">
        <view class="image-item" v-for="(img, index) in images" :key="index">
          <image class="uploaded-image" :src="img" mode="aspectFill" />
          <view class="delete-btn" @click="deleteImage(index)">×</view>
        </view>
        <view class="add-image" @click="chooseImage" v-if="images.length < 9">
          <text class="add-icon">+</text>
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>
    
    <!-- 商品信息表单 -->
    <view class="form-section card">
      <view class="form-item">
        <text class="form-label">商品名称</text>
        <input class="form-input" v-model="form.name" placeholder="请输入商品名称" maxlength="100" />
      </view>
      
      <view class="form-item">
        <text class="form-label">商品分类</text>
        <picker :range="categoryNames" @change="onCategoryChange">
          <view class="picker-content">
            <text :class="{ placeholder: !form.categoryId }">
              {{ selectedCategoryName || '请选择分类' }}
            </text>
            <text class="picker-arrow">›</text>
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="form-label">商品价格</text>
        <view class="price-input-wrapper">
          <text class="price-prefix">¥</text>
          <input class="form-input price-input" v-model="form.price" type="digit" placeholder="0.00" />
        </view>
      </view>
      
      <view class="form-item">
        <text class="form-label">原价（选填）</text>
        <view class="price-input-wrapper">
          <text class="price-prefix">¥</text>
          <input class="form-input price-input" v-model="form.originalPrice" type="digit" placeholder="0.00" />
        </view>
      </view>
      
      <view class="form-item">
        <text class="form-label">商品成色</text>
        <picker :range="conditionOptions" range-key="label" @change="onConditionChange">
          <view class="picker-content">
            <text>{{ selectedConditionLabel || '请选择成色' }}</text>
            <text class="picker-arrow">›</text>
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="form-label">所在地区</text>
        <input class="form-input" v-model="form.location" placeholder="请输入所在地区" />
      </view>
      
      <view class="form-item textarea-item">
        <text class="form-label">商品描述</text>
        <textarea class="form-textarea" v-model="form.description" placeholder="请详细描述商品的品牌、型号、使用情况等信息..." maxlength="1000" />
        <text class="char-count">{{ form.description?.length || 0 }}/1000</text>
      </view>
    </view>
    
    <!-- 发布按钮 -->
    <view class="publish-btn-wrapper">
      <button class="publish-btn" :disabled="publishing" @click="publishProduct">
        {{ publishing ? (editMode ? '提交中...' : '发布中...') : (editMode ? '重新提交审核' : '立即发布') }}
      </button>
    </view>
  </view>
</template>

<script>
import { productApi, uploadImage } from '../../api/index.js'

export default {
  data() {
    return {
      images: [],
      imageUrls: [],
      categories: [],
      form: {
        name: '',
        categoryId: null,
        price: '',
        originalPrice: '',
        conditionLevel: 9,
        location: '',
        description: ''
      },
      conditionOptions: [
        { value: 10, label: '全新' },
        { value: 9, label: '几乎全新' },
        { value: 8, label: '轻微使用痕迹' },
        { value: 7, label: '有使用痕迹' },
        { value: 5, label: '明显使用痕迹' }
      ],
      publishing: false,
      editMode: false,
      productId: null,
      rejectReason: '',
      originalData: null   // 编辑模式下保存原始数据，用于检测是否有改动
    }
  },
  computed: {
    categoryNames() {
      return this.categories.map(c => c.name)
    },
    selectedCategoryName() {
      const category = this.categories.find(c => c.id === this.form.categoryId)
      return category?.name
    },
    selectedConditionLabel() {
      const option = this.conditionOptions.find(o => o.value === this.form.conditionLevel)
      return option?.label
    }
  },
  onLoad() {
    this.checkLogin()
    this.loadCategories()
  },

  onShow() {
    // 从 storage 读取编辑商品 ID（products 页面点编辑时写入）
    const editId = uni.getStorageSync('editProductId')
    if (editId) {
      uni.removeStorageSync('editProductId')
      this.editMode = true
      this.productId = parseInt(editId)
      uni.setNavigationBarTitle({ title: '编辑商品' })
      this.loadProductData(editId)
    } else if (!this.editMode) {
      // 正常进入发布页时重置表单
      this.resetForm()
    }
  },
  methods: {
    async loadProductData(id) {
      uni.showLoading({ title: '加载中...' })
      try {
        const res = await productApi.getProductDetail(id)
        const p = res.data
        // 填充表单
        this.form.name = p.name || ''
        this.form.description = p.description || ''
        this.form.price = p.price ? String(p.price) : ''
        this.form.originalPrice = p.originalPrice ? String(p.originalPrice) : ''
        this.form.categoryId = p.categoryId || null
        this.form.conditionLevel = p.conditionLevel || 9
        this.form.location = p.location || ''
        this.rejectReason = p.rejectReason || ''
        // 填充已有图片（直接用图片 URL，不需要重新上传）
        if (p.images && p.images.length > 0) {
          this.images = p.images.map(url => this.$http ? url : url)
          this.imageUrls = [...p.images]
        } else if (p.imageUrl) {
          this.images = [p.imageUrl]
          this.imageUrls = [p.imageUrl]
        }
        // 保存原始数据快照，用于提交前判断是否有改动
        this.originalData = {
          name: this.form.name,
          description: this.form.description,
          price: this.form.price,
          originalPrice: this.form.originalPrice,
          categoryId: this.form.categoryId,
          conditionLevel: this.form.conditionLevel,
          location: this.form.location,
          imageUrls: [...this.imageUrls]
        }
      } catch (e) {
        uni.showToast({ title: '加载商品信息失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    checkLogin() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/login/login'
          })
        }, 1500)
      }
    },
    
    async loadCategories() {
      try {
        const res = await productApi.getCategories()
        this.categories = res.data || []
      } catch (e) {
        console.error('加载分类失败', e)
      }
    },
    
    chooseImage() {
      uni.chooseImage({
        count: 9 - this.images.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          res.tempFilePaths.forEach(path => {
            this.uploadSingleImage(path)
          })
        }
      })
    },
    
    async uploadSingleImage(filePath) {
      uni.showLoading({ title: '上传中...' })
      try {
        const url = await uploadImage(filePath)
        this.images.push(filePath)
        this.imageUrls.push(url)
      } catch (e) {
        uni.showToast({
          title: '图片上传失败',
          icon: 'none'
        })
      } finally {
        uni.hideLoading()
      }
    },
    
    deleteImage(index) {
      this.images.splice(index, 1)
      this.imageUrls.splice(index, 1)
    },
    
    onCategoryChange(e) {
      const index = e.detail.value
      this.form.categoryId = this.categories[index]?.id
    },
    
    onConditionChange(e) {
      const index = e.detail.value
      this.form.conditionLevel = this.conditionOptions[index]?.value
    },
    
    validateForm() {
      if (this.imageUrls.length === 0) {
        uni.showToast({ title: '请上传商品图片', icon: 'none' })
        return false
      }
      if (!this.form.name?.trim()) {
        uni.showToast({ title: '请输入商品名称', icon: 'none' })
        return false
      }
      if (!this.form.price || parseFloat(this.form.price) <= 0) {
        uni.showToast({ title: '请输入有效价格', icon: 'none' })
        return false
      }
      return true
    },
    
    hasChanges(currentData) {
      if (!this.originalData) return true
      const orig = this.originalData
      // 比较各字段
      if (currentData.name !== orig.name) return true
      if (currentData.description !== orig.description) return true
      if (String(currentData.price) !== String(orig.price)) return true
      if (String(currentData.originalPrice || '') !== String(orig.originalPrice || '')) return true
      if (currentData.categoryId !== orig.categoryId) return true
      if (currentData.conditionLevel !== orig.conditionLevel) return true
      if ((currentData.location || '') !== (orig.location || '')) return true
      // 比较图片列表
      const currImgs = JSON.stringify([...this.imageUrls].sort())
      const origImgs = JSON.stringify([...orig.imageUrls].sort())
      if (currImgs !== origImgs) return true
      return false
    },

    resetForm() {
      this.images = []
      this.imageUrls = []
      this.editMode = false
      this.productId = null
      this.rejectReason = ''
      this.originalData = null
      this.form = {
        name: '',
        categoryId: null,
        price: '',
        originalPrice: '',
        conditionLevel: 9,
        location: '',
        description: ''
      }
      uni.setNavigationBarTitle({ title: '发布商品' })
    },

    async publishProduct() {
      if (!this.validateForm()) return

      this.publishing = true
      try {
        const data = {
          name: this.form.name.trim(),
          description: this.form.description,
          price: parseFloat(this.form.price),
          originalPrice: this.form.originalPrice ? parseFloat(this.form.originalPrice) : null,
          categoryId: this.form.categoryId,
          conditionLevel: this.form.conditionLevel,
          location: this.form.location,
          images: this.imageUrls
        }

        if (this.editMode) {
          // 检查是否有任何改动
          if (!this.hasChanges(data)) {
            uni.showToast({ title: '无修改，不可重新提交', icon: 'none' })
            return
          }
          // 编辑模式：更新商品，同时将状态重置为"待审核"
          await productApi.updateProduct(this.productId, {
            ...data,
            status: 3
          })
          uni.showToast({ title: '已重新提交审核', icon: 'none' })
        } else {
          // 发布模式：创建新商品
          await productApi.createProduct(data)
          uni.showToast({ title: '提交成功，等待审核', icon: 'none' })
        }

        setTimeout(() => {
          if (this.editMode) {
            // publish 是 tabBar 页，无回退栈，直接跳到"我发布的"列表
            uni.reLaunch({ url: '/pages/user/products' })
          } else {
            uni.switchTab({ url: '/pages/index/index' })
          }
        }, 1500)
      } catch (e) {
        console.error(this.editMode ? '更新失败' : '发布失败', e)
      } finally {
        this.publishing = false
      }
    }
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 150rpx;
}

.card {
  background-color: #ffffff;
  margin: 20rpx;
  padding: 24rpx;
  border-radius: 16rpx;
}

.section-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 20rpx;
  display: block;
}

/* 图片上传区域 */
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: #ff4d4f;
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.add-image {
  width: 200rpx;
  height: 200rpx;
  border: 2rpx dashed #cccccc;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.add-icon {
  font-size: 60rpx;
  color: #cccccc;
}

.add-text {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
}

/* 表单 */
.form-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.form-item:last-child {
  border-bottom: none;
}

.textarea-item {
  flex-direction: column;
  align-items: flex-start;
}

.form-label {
  font-size: 28rpx;
  color: #333333;
  width: 180rpx;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  text-align: right;
}

.price-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.price-prefix {
  font-size: 28rpx;
  color: #ff6b35;
  margin-right: 8rpx;
}

.price-input {
  width: 200rpx;
}

.picker-content {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.picker-content text {
  font-size: 28rpx;
  color: #333333;
}

.picker-content .placeholder {
  color: #999999;
}

.picker-arrow {
  font-size: 32rpx;
  color: #cccccc;
  margin-left: 10rpx;
}

.form-textarea {
  width: 100%;
  height: 200rpx;
  font-size: 28rpx;
  margin-top: 16rpx;
  padding: 16rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
}

.char-count {
  font-size: 24rpx;
  color: #999999;
  text-align: right;
  width: 100%;
  margin-top: 8rpx;
}

/* 发布按钮 */
.publish-btn-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background-color: #ffffff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.publish-btn {
  width: 100%;
  height: 88rpx;
  background-color: #ff6b35;
  color: #ffffff;
  border: none;
  border-radius: 44rpx;
  font-size: 32rpx;
}

.publish-btn[disabled] {
  background-color: #ffb399;
}

/* 拒绝原因提示 */
.reject-tip {
  border-left: 6rpx solid #ff4d4f;
  background-color: #fff1f0;
  padding: 24rpx;
}
.reject-tip-title {
  display: block;
  font-size: 26rpx;
  font-weight: bold;
  color: #ff4d4f;
  margin-bottom: 10rpx;
}
.reject-tip-content {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}
</style>
