<template>
  <view class="audit-page">

    <!-- Tab 栏 -->
    <view class="tab-bar">
      <view
        v-for="tab in tabs" :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="switchTab(tab.key)"
      >
        <text>{{ tab.label }}</text>
        <view v-if="badges[tab.key] > 0" class="badge">
          <text class="badge-text">{{ badges[tab.key] > 99 ? '99+' : badges[tab.key] }}</text>
        </view>
      </view>
    </view>

    <!-- ===================== 注册审核 ===================== -->
    <view v-if="activeTab === 'user'">
      <view v-if="!userLoading && userList.length === 0" class="empty-wrap">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无待审核的注册申请</text>
      </view>
      <view v-if="userLoading" class="loading-wrap">
        <text class="loading-text">加载中...</text>
      </view>

      <view v-for="item in userList" :key="item.id" class="card">
        <view class="card-header">
          <text class="card-title">{{ item.nickname || item.username }}</text>
          <text class="card-time">{{ item.createdAt }}</text>
        </view>
        <view class="info-row"><text class="info-label">用户名</text><text class="info-value">{{ item.username }}</text></view>
        <view class="info-row" v-if="item.phoneNumber"><text class="info-label">手机号</text><text class="info-value">{{ item.phoneNumber }}</text></view>
        <view class="info-row" v-if="item.email"><text class="info-label">邮箱</text><text class="info-value">{{ item.email }}</text></view>
        <view class="info-row">
          <text class="info-label">身份证号</text>
          <text class="info-value id-card">{{ item.idCard }}</text>
        </view>
        <view class="action-row">
          <button class="btn-reject" :disabled="item.auditing" @click="rejectUser(item)">拒绝</button>
          <button class="btn-approve" :disabled="item.auditing" @click="approveUser(item)">通过注册</button>
        </view>
      </view>

      <view v-if="userHasMore && !userLoading" class="load-more" @click="loadUsers(true)">
        <text class="load-more-text">加载更多</text>
      </view>
    </view>

    <!-- ===================== 商品审核 ===================== -->
    <view v-if="activeTab === 'product'">
      <view v-if="!productLoading && productList.length === 0" class="empty-wrap">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无待审核的商品</text>
      </view>
      <view v-if="productLoading" class="loading-wrap">
        <text class="loading-text">加载中...</text>
      </view>

      <view v-for="item in productList" :key="item.id" class="card">
        <view class="card-header">
          <text class="card-title">{{ item.name }}</text>
          <text class="card-time">{{ item.createdAt }}</text>
        </view>
        <scroll-view v-if="productImages(item).length > 0" scroll-x class="product-img-scroll">
          <view class="product-img-row">
            <image
              v-for="(url, idx) in productImages(item)"
              :key="idx"
              :src="getImageUrl(url)"
              mode="aspectFill"
              class="product-img"
              @click="previewProductImages(item, idx)"
            />
          </view>
        </scroll-view>
        <view class="info-row"><text class="info-label">价格</text><text class="info-value price">¥{{ item.price }}</text></view>
        <view class="info-row"><text class="info-label">卖家</text><text class="info-value">{{ item.seller && (item.seller.nickname || item.seller.username) }}</text></view>
        <view class="action-row">
          <button class="btn-reject" :disabled="item.auditing" @click="openProductRejectModal(item)">拒绝</button>
          <button class="btn-approve" :disabled="item.auditing" @click="approveProduct(item)">通过上架</button>
        </view>
      </view>

      <view v-if="productHasMore && !productLoading" class="load-more" @click="loadProducts(true)">
        <text class="load-more-text">加载更多</text>
      </view>
    </view>

    <!-- ===================== 评价审核 ===================== -->
    <view v-if="activeTab === 'review'">
      <view v-if="!reviewLoading && reviewList.length === 0" class="empty-wrap">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无待审核的差评</text>
      </view>
      <view v-if="reviewLoading" class="loading-wrap">
        <text class="loading-text">加载中...</text>
      </view>

      <view v-for="item in reviewList" :key="item.id" class="card">
        <view class="card-header">
          <text class="card-title">{{ item.productName }}</text>
          <view class="credit-badge"><text class="credit-text">{{ item.creditDelta }}分</text></view>
        </view>
        <text class="sub-text">买家：{{ item.user && (item.user.nickname || item.user.username) }} · {{ item.createdAt }}</text>
        <view class="rating-row">
          <text v-for="i in 5" :key="i" class="star" :class="{ active: i <= item.rating }">★</text>
          <text class="rating-num">{{ item.rating }}星</text>
        </view>
        <text v-if="item.comment" class="comment">{{ item.comment }}</text>
        <view v-if="parsedImages(item).length > 0" class="img-row">
          <image
            v-for="(url, idx) in parsedImages(item)" :key="idx"
            :src="url" mode="aspectFill" class="review-img"
            @click="previewImage(parsedImages(item), idx)"
          />
        </view>
        <view class="action-row">
          <button class="btn-reject" :disabled="item.auditing" @click="openReviewRejectModal(item)">拒绝（不扣分）</button>
          <button class="btn-approve" :disabled="item.auditing" @click="approveReview(item)">
            通过（扣 {{ Math.abs(item.creditDelta) }} 分）
          </button>
        </view>
      </view>

      <view v-if="reviewHasMore && !reviewLoading" class="load-more" @click="loadReviews(true)">
        <text class="load-more-text">加载更多</text>
      </view>
    </view>

    <!-- ===================== 拒绝原因弹窗 ===================== -->
    <view v-if="rejectModal.visible" class="modal-mask" @click.self="rejectModal.visible = false">
      <view class="modal-box">
        <text class="modal-title">{{ rejectModal.title }}</text>
        <textarea
          class="remark-input"
          v-model="rejectModal.remark"
          placeholder="请输入拒绝原因（选填）"
          maxlength="200"
        />
        <view class="modal-btns">
          <button class="modal-cancel" @click="rejectModal.visible = false">取消</button>
          <button class="modal-confirm" :disabled="rejectModal.submitting" @click="confirmReject">
            {{ rejectModal.submitting ? '提交中...' : '确认拒绝' }}
          </button>
        </view>
      </view>
    </view>

  </view>
</template>

<script>
import http from '../../utils/request.js'
import { getImageUrl } from '../../utils/image.js'

export default {
  data() {
    return {
      activeTab: 'user',
      tabs: [
        { key: 'user',    label: '注册审核' },
        { key: 'product', label: '商品审核' },
        { key: 'review',  label: '评价审核' }
      ],
      badges: { user: 0, product: 0, review: 0 },

      userList: [], userLoading: false, userPage: 0, userHasMore: true,
      productList: [], productLoading: false, productPage: 0, productHasMore: true,
      reviewList: [], reviewLoading: false, reviewPage: 0, reviewHasMore: true,

      rejectModal: {
        visible: false, type: '', title: '', item: null, remark: '', submitting: false
      }
    }
  },

  onLoad() {
    this.checkAdmin()
    this.loadAll()
  },

  onPullDownRefresh() {
    this.loadAll().then(() => uni.stopPullDownRefresh())
  },

  methods: {
    getImageUrl,

    checkAdmin() {
      const userInfo = uni.getStorageSync('userInfo')
      if (!userInfo || userInfo.role !== 'ADMIN') {
        uni.showToast({ title: '需要管理员权限', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 1500)
      }
    },

    switchTab(key) {
      this.activeTab = key
      if (key === 'user'    && this.userList.length === 0)    this.loadUsers()
      if (key === 'product' && this.productList.length === 0) this.loadProducts()
      if (key === 'review'  && this.reviewList.length === 0)  this.loadReviews()
    },

    async loadAll() {
      await Promise.all([this.loadUsers(), this.loadProducts(), this.loadReviews()])
    },

    // ---------- 注册审核 ----------
    async loadUsers(more = false) {
      if (this.userLoading) return
      if (!more) { this.userPage = 0; this.userList = []; this.userHasMore = true }
      this.userLoading = true
      try {
        const res = await http.request({ url: `/api/admin/users/pending?page=${this.userPage}&size=10`, method: 'GET' })
        const data = res.data
        const items = (data.content || []).map(u => ({ ...u, auditing: false }))
        this.userList = more ? [...this.userList, ...items] : items
        this.userHasMore = !data.last
        this.userPage++
        this.badges.user = data.totalElements || 0
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.userLoading = false
      }
    },

    async approveUser(item) {
      uni.showModal({
        title: '确认通过',
        content: `确认通过「${item.username}」的注册申请？`,
        success: async (res) => {
          if (!res.confirm) return
          item.auditing = true
          try {
            await http.request({ url: `/api/admin/users/${item.id}/audit`, method: 'POST', data: { action: 'APPROVE' } })
            uni.showToast({ title: '已通过，用户可正常登录', icon: 'success' })
            this.userList = this.userList.filter(u => u.id !== item.id)
            this.badges.user = Math.max(0, this.badges.user - 1)
          } catch (e) {
            uni.showToast({ title: e.message || '操作失败', icon: 'none' })
          } finally { item.auditing = false }
        }
      })
    },

    rejectUser(item) {
      this.rejectModal = { visible: true, type: 'user', title: '拒绝注册申请', item, remark: '', submitting: false }
    },

    // ---------- 商品审核 ----------
    async loadProducts(more = false) {
      if (this.productLoading) return
      if (!more) { this.productPage = 0; this.productList = []; this.productHasMore = true }
      this.productLoading = true
      try {
        const res = await http.request({ url: `/api/admin/products/pending?page=${this.productPage}&size=10`, method: 'GET' })
        const data = res.data
        const items = (data.content || []).map(p => ({ ...p, auditing: false }))
        this.productList = more ? [...this.productList, ...items] : items
        this.productHasMore = !data.last
        this.productPage++
        this.badges.product = data.totalElements || 0
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.productLoading = false
      }
    },

    async approveProduct(item) {
      uni.showModal({
        title: '确认通过',
        content: `确认通过商品「${item.name}」的上架申请？`,
        success: async (res) => {
          if (!res.confirm) return
          item.auditing = true
          try {
            await http.request({ url: `/api/admin/products/${item.id}/review`, method: 'POST', data: { approved: true } })
            uni.showToast({ title: '商品已通过上架', icon: 'success' })
            this.productList = this.productList.filter(p => p.id !== item.id)
            this.badges.product = Math.max(0, this.badges.product - 1)
          } catch (e) {
            uni.showToast({ title: e.message || '操作失败', icon: 'none' })
          } finally { item.auditing = false }
        }
      })
    },

    openProductRejectModal(item) {
      this.rejectModal = { visible: true, type: 'product', title: '拒绝商品上架', item, remark: '', submitting: false }
    },

    // ---------- 评价审核 ----------
    async loadReviews(more = false) {
      if (this.reviewLoading) return
      if (!more) { this.reviewPage = 0; this.reviewList = []; this.reviewHasMore = true }
      this.reviewLoading = true
      try {
        const res = await http.request({ url: `/api/admin/reviews/pending?page=${this.reviewPage}&size=10`, method: 'GET' })
        const data = res.data
        const items = (data.content || []).map(r => ({ ...r, auditing: false }))
        this.reviewList = more ? [...this.reviewList, ...items] : items
        this.reviewHasMore = !data.last
        this.reviewPage++
        this.badges.review = data.totalElements || 0
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.reviewLoading = false
      }
    },

    async approveReview(item) {
      uni.showModal({
        title: '确认通过',
        content: `确认通过该差评？将扣除卖家 ${Math.abs(item.creditDelta)} 分信用分`,
        confirmText: '确认扣分', confirmColor: '#e53935',
        success: async (res) => {
          if (!res.confirm) return
          item.auditing = true
          try {
            await http.request({ url: `/api/admin/reviews/${item.id}/audit`, method: 'POST', data: { approved: true } })
            uni.showToast({ title: '已通过，信用分已扣除', icon: 'success' })
            this.reviewList = this.reviewList.filter(r => r.id !== item.id)
            this.badges.review = Math.max(0, this.badges.review - 1)
          } catch (e) {
            uni.showToast({ title: e.message || '操作失败', icon: 'none' })
          } finally { item.auditing = false }
        }
      })
    },

    openReviewRejectModal(item) {
      this.rejectModal = { visible: true, type: 'review', title: '拒绝差评（不扣分）', item, remark: '', submitting: false }
    },

    // ---------- 拒绝弹窗统一确认 ----------
    async confirmReject() {
      const { type, item, remark } = this.rejectModal
      this.rejectModal.submitting = true
      try {
        if (type === 'user') {
          await http.request({ url: `/api/admin/users/${item.id}/audit`, method: 'POST', data: { action: 'REJECT', reason: remark || null } })
          uni.showToast({ title: '已拒绝注册申请', icon: 'success' })
          this.userList = this.userList.filter(u => u.id !== item.id)
          this.badges.user = Math.max(0, this.badges.user - 1)

        } else if (type === 'product') {
          await http.request({ url: `/api/admin/products/${item.id}/review`, method: 'POST', data: { approved: false, rejectReason: remark || null } })
          uni.showToast({ title: '已拒绝，商品未上架', icon: 'success' })
          this.productList = this.productList.filter(p => p.id !== item.id)
          this.badges.product = Math.max(0, this.badges.product - 1)

        } else if (type === 'review') {
          await http.request({ url: `/api/admin/reviews/${item.id}/audit`, method: 'POST', data: { approved: false, remark: remark || null } })
          uni.showToast({ title: '已拒绝，信用分不变', icon: 'success' })
          this.reviewList = this.reviewList.filter(r => r.id !== item.id)
          this.badges.review = Math.max(0, this.badges.review - 1)
        }
        this.rejectModal.visible = false
      } catch (e) {
        uni.showToast({ title: e.message || '操作失败', icon: 'none' })
      } finally {
        this.rejectModal.submitting = false
      }
    },

    parsedImages(item) {
      if (!item.images) return []
      try {
        const arr = JSON.parse(item.images)
        return Array.isArray(arr) ? arr : []
      } catch { return [] }
    },

    productImages(item) {
      if (Array.isArray(item.images) && item.images.length > 0) {
        return item.images
      }
      return item.imageUrl ? [item.imageUrl] : []
    },

    previewProductImages(item, current) {
      const urls = this.productImages(item).map(url => getImageUrl(url))
      uni.previewImage({ urls, current: urls[current] })
    },

    previewImage(urls, current) {
      uni.previewImage({ urls, current: urls[current] })
    }
  }
}
</script>

<style scoped>
.audit-page { min-height: 100vh; background: #f5f5f5; }

.tab-bar { display: flex; background: #fff; border-bottom: 1rpx solid #eee; position: sticky; top: 0; z-index: 10; }
.tab-item { flex: 1; display: flex; align-items: center; justify-content: center; padding: 28rpx 0; font-size: 28rpx; color: #666; border-bottom: 4rpx solid transparent; gap: 8rpx; }
.tab-item.active { color: #ff6b35; border-bottom-color: #ff6b35; font-weight: bold; }
.badge { background: #e53935; border-radius: 20rpx; min-width: 32rpx; height: 32rpx; display: flex; align-items: center; justify-content: center; padding: 0 8rpx; }
.badge-text { font-size: 18rpx; color: #fff; font-weight: bold; }

.empty-wrap { display: flex; flex-direction: column; align-items: center; padding: 120rpx 0; }
.empty-icon { font-size: 80rpx; }
.empty-text { font-size: 28rpx; color: #999; margin-top: 20rpx; }
.loading-wrap { display: flex; justify-content: center; padding: 60rpx 0; }
.loading-text { font-size: 26rpx; color: #999; }

.card { background: #fff; border-radius: 16rpx; padding: 28rpx; margin: 20rpx 20rpx 0; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16rpx; }
.card-title { font-size: 28rpx; font-weight: bold; color: #333; flex: 1; margin-right: 16rpx; }
.card-time { font-size: 22rpx; color: #bbb; flex-shrink: 0; }

.info-row { display: flex; align-items: center; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.info-label { font-size: 24rpx; color: #999; width: 120rpx; flex-shrink: 0; }
.info-value { font-size: 26rpx; color: #333; flex: 1; }
.info-value.price { color: #ff6b35; font-weight: bold; }
.info-value.id-card { font-family: monospace; letter-spacing: 2rpx; }

.product-img-scroll { width: 100%; margin: 16rpx 0; white-space: nowrap; }
.product-img-row { display: flex; gap: 16rpx; }
.product-img { width: 220rpx; height: 220rpx; border-radius: 12rpx; flex-shrink: 0; background: #f5f5f5; }

.sub-text { font-size: 24rpx; color: #999; display: block; margin-bottom: 12rpx; }
.rating-row { display: flex; align-items: center; gap: 6rpx; margin-bottom: 14rpx; }
.star { font-size: 36rpx; color: #e0e0e0; }
.star.active { color: #ffb300; }
.rating-num { font-size: 24rpx; color: #ff6b35; margin-left: 6rpx; }
.comment { font-size: 26rpx; color: #333; line-height: 1.6; display: block; margin-bottom: 16rpx; }
.img-row { display: flex; flex-wrap: wrap; gap: 12rpx; margin-bottom: 16rpx; }
.review-img { width: 140rpx; height: 140rpx; border-radius: 10rpx; }
.credit-badge { background: #fce4ec; border-radius: 20rpx; padding: 4rpx 16rpx; }
.credit-text { font-size: 22rpx; color: #e53935; font-weight: bold; }

.action-row { display: flex; gap: 16rpx; margin-top: 20rpx; padding-top: 16rpx; border-top: 1rpx solid #f0f0f0; }
.btn-reject { flex: 1; font-size: 26rpx; color: #666; background: #f5f5f5; border: none; border-radius: 40rpx; padding: 16rpx 0; }
.btn-approve { flex: 1; font-size: 26rpx; color: #fff; background: #ff6b35; border: none; border-radius: 40rpx; padding: 16rpx 0; }
.btn-reject[disabled], .btn-approve[disabled] { opacity: 0.5; }

.load-more { text-align: center; padding: 30rpx 0 20rpx; }
.load-more-text { font-size: 26rpx; color: #999; }

.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 999; }
.modal-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 620rpx; }
.modal-title { font-size: 30rpx; font-weight: bold; color: #333; display: block; margin-bottom: 24rpx; }
.remark-input { width: 100%; min-height: 160rpx; border: 1rpx solid #e0e0e0; border-radius: 12rpx; padding: 16rpx; font-size: 26rpx; color: #333; box-sizing: border-box; }
.modal-btns { display: flex; gap: 20rpx; margin-top: 30rpx; }
.modal-cancel { flex: 1; font-size: 28rpx; color: #666; background: #f5f5f5; border: none; border-radius: 40rpx; padding: 20rpx 0; }
.modal-confirm { flex: 1; font-size: 28rpx; color: #fff; background: #e53935; border: none; border-radius: 40rpx; padding: 20rpx 0; }
.modal-confirm[disabled] { opacity: 0.5; }
</style>
