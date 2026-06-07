<template>
  <view class="review-page">
    <!-- 商品信息 -->
    <view class="product-card">
      <image class="product-image" :src="getImageUrl(productImage)" mode="aspectFill" />
      <text class="product-name">{{ productName }}</text>
    </view>

    <!-- 拒绝原因提示（编辑模式才显示） -->
    <view class="reject-tip-card" v-if="isEditMode && auditRemark">
      <text class="reject-tip-title">⚠️ 上次审核未通过原因</text>
      <text class="reject-tip-content">{{ auditRemark }}</text>
      <text class="reject-tip-hint">请修改评价内容后重新提交</text>
    </view>

    <!-- 总体评分 -->
    <view class="section-card">
      <text class="section-title">总体评分</text>
      <view class="star-row">
        <text v-for="i in 5" :key="i" class="star" :class="{ active: i <= form.rating }" @click="form.rating = i">★</text>
        <text class="rating-label">{{ ratingLabel }}</text>
      </view>
      <view v-if="form.rating <= 2" class="low-rating-tip">
        <text class="tip-icon">⚠️</text>
        <text class="tip-text">{{ form.rating === 1 ? '1星评价将扣除卖家5分信用分' : '2星评价将扣除卖家3分信用分' }}，需管理员审核后生效</text>
      </view>
    </view>

    <!-- 多维度评分 -->
    <view class="section-card">
      <text class="section-title">详细评分（选填）</text>
      <view class="score-row" v-for="item in scoreItems" :key="item.key">
        <text class="score-label">{{ item.label }}</text>
        <view class="mini-stars">
          <text v-for="i in 5" :key="i" class="mini-star" :class="{ active: i <= form[item.key] }" @click="form[item.key] = i">★</text>
        </view>
      </view>
    </view>

    <!-- 文字评价 -->
    <view class="section-card">
      <text class="section-title">文字评价</text>
      <textarea class="comment-input" v-model="form.comment" placeholder="说说你对这件商品的感受，帮助买家做出更好的选择..." maxlength="500" :auto-height="true" />
      <text class="char-count">{{ form.comment.length }}/500</text>
    </view>

    <!-- 图片上传 -->
    <view class="section-card">
      <text class="section-title">上传图片（选填，最多4张）</text>
      <view class="image-grid">
        <view v-for="(img, idx) in imageList" :key="idx" class="image-item">
          <image :src="img.path" mode="aspectFill" class="thumb" />
          <view class="del-btn" @click="removeImage(idx)"><text class="del-icon">✕</text></view>
          <view v-if="img.uploading" class="upload-mask"><text class="upload-text">上传中...</text></view>
          <view v-if="img.failed" class="fail-mask" @click="retryUpload(idx)"><text class="fail-text">失败重试</text></view>
        </view>
        <view v-if="imageList.length < 4" class="add-image-btn" @click="chooseImage">
          <text class="add-icon">📷</text>
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-wrap">
      <button class="submit-btn" :disabled="submitting || hasUploading" @click="submitReview">
        {{ submitting ? '提交中...' : (hasUploading ? '图片上传中...' : (isEditMode ? '重新提交审核' : '提交评价')) }}
      </button>
    </view>
  </view>
</template>

<script>
import { reviewApi, uploadImage } from '../../api/index.js'
import { getImageUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      reviewId: null,      // 有值时为编辑模式
      orderId: null,
      productId: null,
      productName: '',
      productImage: '',
      auditRemark: '',     // 上次被拒绝的原因
      submitting: false,
      imageList: [],
      form: {
        rating: 5,
        serviceScore: 5,
        descScore: 5,
        shipScore: 5,
        comment: ''
      },
      scoreItems: [
        { key: 'serviceScore', label: '服务质量' },
        { key: 'descScore',    label: '描述准确' },
        { key: 'shipScore',    label: '发货速度' }
      ]
    }
  },
  computed: {
    isEditMode() {
      return !!this.reviewId
    },
    ratingLabel() {
      const labels = ['', '非常差', '较差', '一般', '满意', '非常满意']
      return labels[this.form.rating] || ''
    },
    hasUploading() {
      return this.imageList.some(img => img.uploading)
    }
  },
  onLoad(options) {
    this.reviewId     = options.reviewId   ? parseInt(options.reviewId)   : null
    this.orderId      = options.orderId    ? parseInt(options.orderId)    : null
    this.productId    = options.productId  ? parseInt(options.productId)  : null
    this.productName  = decodeURIComponent(options.productName  || '')
    this.productImage = decodeURIComponent(options.productImage || '')
    this.auditRemark  = decodeURIComponent(options.auditRemark  || '')

    // 编辑模式：加载已有评价内容预填表单
    if (this.reviewId && this.orderId) {
      this.loadExistingReview()
    }
  },
  methods: {
    getImageUrl,

    async loadExistingReview() {
      try {
        const res = await reviewApi.getReviewByOrderId(this.orderId)
        const review = res.data
        if (!review) return
        this.form.rating       = review.rating       || 5
        this.form.serviceScore = review.serviceScore || 5
        this.form.descScore    = review.descScore    || 5
        this.form.shipScore    = review.shipScore    || 5
        this.form.comment      = review.comment      || ''
        // 预填已上传的图片
        if (review.images) {
          try {
            const urls = JSON.parse(review.images)
            this.imageList = urls.map(url => ({ path: url, url, uploading: false, failed: false }))
          } catch (e) { /* 忽略解析失败 */ }
        }
      } catch (e) {
        console.error('加载评价失败', e)
      }
    },

    chooseImage() {
      const remaining = 4 - this.imageList.length
      uni.chooseImage({
        count: remaining,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          res.tempFilePaths.forEach(path => {
            const item = { path, url: null, uploading: true, failed: false }
            this.imageList.push(item)
            const idx = this.imageList.length - 1
            this.uploadOneImage(idx, path)
          })
        }
      })
    },

    async uploadOneImage(idx, path) {
      try {
        const url = await uploadImage(path)
        this.imageList[idx].url = url
        this.imageList[idx].uploading = false
        this.imageList[idx].failed = false
      } catch (e) {
        this.imageList[idx].uploading = false
        this.imageList[idx].failed = true
        uni.showToast({ title: '图片上传失败', icon: 'none' })
      }
    },

    retryUpload(idx) {
      this.imageList[idx].failed = false
      this.imageList[idx].uploading = true
      this.uploadOneImage(idx, this.imageList[idx].path)
    },

    removeImage(idx) {
      this.imageList.splice(idx, 1)
    },

    async submitReview() {
      if (!this.form.rating) {
        uni.showToast({ title: '请选择评分', icon: 'none' })
        return
      }
      if (this.imageList.some(img => img.failed)) {
        uni.showToast({ title: '有图片上传失败，请重试或删除', icon: 'none' })
        return
      }
      // 只把已上传成功的图片（有 url）提交；预填的旧图 url 就是 path，直接使用
      const uploadedUrls = this.imageList
        .map(img => img.url || (img.path && !img.path.startsWith('_doc') ? img.path : null))
        .filter(Boolean)
      const imagesJson = uploadedUrls.length > 0 ? JSON.stringify(uploadedUrls) : null

      this.submitting = true
      try {
        let res
        if (this.isEditMode) {
          // 编辑模式：修改被拒绝的评价
          res = await reviewApi.updateReview(this.reviewId, {
            rating:       this.form.rating,
            serviceScore: this.form.serviceScore,
            descScore:    this.form.descScore,
            shipScore:    this.form.shipScore,
            comment:      this.form.comment,
            images:       imagesJson
          })
        } else {
          // 新建评价
          res = await reviewApi.createReview({
            orderId:      this.orderId,
            productId:    this.productId,
            rating:       this.form.rating,
            serviceScore: this.form.serviceScore,
            descScore:    this.form.descScore,
            shipScore:    this.form.shipScore,
            comment:      this.form.comment,
            images:       imagesJson
          })
        }

        const auditStatus = res.data && res.data.auditStatus
        if (auditStatus === 0) {
          uni.showModal({
            title: this.isEditMode ? '已重新提交审核' : '评价已提交',
            content: '您的差评将在管理员审核通过后扣除卖家信用分',
            showCancel: false,
            success: () => uni.navigateBack()
          })
        } else {
          uni.showToast({ title: this.isEditMode ? '评价已更新' : '评价成功', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1500)
        }
      } catch (e) {
        uni.showToast({ title: e.message || '提交失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.review-page { min-height: 100vh; background-color: #f5f5f5; padding-bottom: 160rpx; }
.product-card { display: flex; align-items: center; background: #fff; padding: 24rpx 30rpx; margin-bottom: 20rpx; }
.product-image { width: 100rpx; height: 100rpx; border-radius: 12rpx; flex-shrink: 0; }
.product-name { margin-left: 20rpx; font-size: 28rpx; color: #333; flex: 1; }

/* 拒绝原因提示卡片 */
.reject-tip-card { background: #fff8f6; border-left: 6rpx solid #ff6b35; margin: 0 0 20rpx; padding: 24rpx 30rpx; }
.reject-tip-title { font-size: 28rpx; font-weight: bold; color: #c0392b; display: block; margin-bottom: 14rpx; }
.reject-tip-content { font-size: 26rpx; color: #333; display: block; line-height: 1.6; margin-bottom: 10rpx; }
.reject-tip-hint { font-size: 24rpx; color: #999; display: block; }
.section-card { background: #fff; margin: 0 0 20rpx; padding: 30rpx; }
.section-title { font-size: 28rpx; font-weight: bold; color: #333; display: block; margin-bottom: 20rpx; }
.star-row { display: flex; align-items: center; gap: 16rpx; }
.star { font-size: 60rpx; color: #e0e0e0; line-height: 1; }
.star.active { color: #ffb300; }
.rating-label { font-size: 26rpx; color: #ff6b35; margin-left: 10rpx; }
.low-rating-tip { display: flex; align-items: flex-start; background: #fff8e1; border: 1rpx solid #ffe082; border-radius: 12rpx; padding: 16rpx 20rpx; margin-top: 20rpx; gap: 10rpx; }
.tip-icon { font-size: 28rpx; flex-shrink: 0; }
.tip-text { font-size: 24rpx; color: #e65100; line-height: 1.5; flex: 1; }
.score-row { display: flex; align-items: center; justify-content: space-between; padding: 12rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.score-row:last-child { border-bottom: none; }
.score-label { font-size: 26rpx; color: #666; }
.mini-stars { display: flex; gap: 8rpx; }
.mini-star { font-size: 40rpx; color: #e0e0e0; }
.mini-star.active { color: #ffb300; }
.comment-input { width: 100%; min-height: 200rpx; font-size: 26rpx; color: #333; line-height: 1.6; }
.char-count { font-size: 22rpx; color: #bbb; text-align: right; display: block; margin-top: 10rpx; }
.image-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
.image-item { position: relative; width: 160rpx; height: 160rpx; border-radius: 12rpx; overflow: hidden; }
.thumb { width: 100%; height: 100%; }
.del-btn { position: absolute; top: 6rpx; right: 6rpx; width: 40rpx; height: 40rpx; background: rgba(0,0,0,0.55); border-radius: 50%; display: flex; align-items: center; justify-content: center; z-index: 10; }
.del-icon { color: #fff; font-size: 22rpx; line-height: 1; }
.upload-mask, .fail-mask { position: absolute; inset: 0; background: rgba(0,0,0,0.45); display: flex; align-items: center; justify-content: center; border-radius: 12rpx; }
.upload-text { color: #fff; font-size: 22rpx; }
.fail-text { color: #ffccbc; font-size: 22rpx; }
.add-image-btn { width: 160rpx; height: 160rpx; border: 2rpx dashed #ccc; border-radius: 12rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8rpx; background: #fafafa; }
.add-icon { font-size: 48rpx; }
.add-text { font-size: 22rpx; color: #999; }
.submit-wrap { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05); }
.submit-btn { width: 100%; background: #ff6b35; color: #fff; font-size: 32rpx; border-radius: 48rpx; padding: 24rpx 0; border: none; }
.submit-btn[disabled] { background: #ffb99a; }
</style>
