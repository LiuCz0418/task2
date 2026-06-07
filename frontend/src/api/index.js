import http from '../utils/request.js'

// 用户相关 API
export const userApi = {
  // 注册
  register(data) {
    return http.request({
      url: '/api/users/register',
      method: 'POST',
      data
    })
  },
  
  // 登录
  login(data) {
    return http.request({
      url: '/api/users/login',
      method: 'POST',
      data
    })
  },
  
  // 获取当前用户信息
  getCurrentUser() {
    return http.request({
      url: '/api/users/me',
      method: 'GET'
    })
  },
  
  // 获取用户信息
  getUserInfo(id) {
    return http.request({
      url: `/api/users/${id}`,
      method: 'GET'
    })
  },
  
  // 更新用户信息
  updateUser(data) {
    return http.request({
      url: '/api/users/me',
      method: 'PUT',
      data
    })
  }
}

// 商品相关 API
export const productApi = {
  // 获取分类列表
  getCategories() {
    return http.request({
      url: '/api/categories',
      method: 'GET'
    })
  },
  
  // 添加收藏
  addFavorite(productId) {
    return http.request({
      url: `/api/products/${productId}/favorite`,
      method: 'POST'
    })
  },
  
  // 取消收藏
  removeFavorite(productId) {
    return http.request({
      url: `/api/products/${productId}/favorite`,
      method: 'DELETE'
    })
  },
  
  // 检查是否已收藏
  isFavorite(productId) {
    return http.request({
      url: `/api/products/${productId}/favorite`,
      method: 'GET'
    })
  },
  
  // 获取收藏列表
  getFavorites(params) {
    return http.request({
      url: '/api/favorites',
      method: 'GET',
      data: params
    })
  },
  
  // 获取商品列表
  getProducts(params) {
    return http.request({
      url: '/api/products',
      method: 'GET',
      data: params
    })
  },
  
  // 获取商品详情
  getProductDetail(id) {
    return http.request({
      url: `/api/products/${id}`,
      method: 'GET'
    })
  },
  
  // 发布商品
  createProduct(data) {
    return http.request({
      url: '/api/products',
      method: 'POST',
      data
    })
  },
  
  // 更新商品
  updateProduct(id, data) {
    return http.request({
      url: `/api/products/${id}`,
      method: 'PUT',
      data
    })
  },
  
  // 删除商品
  deleteProduct(id) {
    return http.request({
      url: `/api/products/${id}`,
      method: 'DELETE'
    })
  },
  
  // 批量删除商品
  batchDeleteProducts(productIds) {
    return http.request({
      url: '/api/products/batch-delete',
      method: 'POST',
      data: productIds
    })
  },
  
  // 获取我发布的商品
  getMyProducts(params) {
    return http.request({
      url: '/api/products/my',
      method: 'GET',
      data: params
    })
  },
  
  // 获取热门商品
  getHotProducts() {
    return http.request({
      url: '/api/products/hot',
      method: 'GET'
    })
  },
  
  // 获取最新商品
  getLatestProducts() {
    return http.request({
      url: '/api/products/latest',
      method: 'GET'
    })
  }
}

// 订单相关 API
export const orderApi = {
  // 创建订单
  createOrder(data) {
    return http.request({
      url: '/api/orders',
      method: 'POST',
      data
    })
  },
  
  // 支付订单
  payOrder(id) {
    return http.request({
      url: `/api/orders/${id}/pay`,
      method: 'POST'
    })
  },
  
  // 发货
  shipOrder(id, data) {
    return http.request({
      url: `/api/orders/${id}/ship`,
      method: 'POST',
      data
    })
  },
  
  // 确认收货
  confirmReceive(id) {
    return http.request({
      url: `/api/orders/${id}/receive`,
      method: 'POST'
    })
  },
  
  // 取消订单
  cancelOrder(id) {
    return http.request({
      url: `/api/orders/${id}/cancel`,
      method: 'POST'
    })
  },
  
  // 获取订单详情
  getOrderDetail(id) {
    return http.request({
      url: `/api/orders/${id}`,
      method: 'GET'
    })
  },
  
  // 获取买家订单列表
  getBuyerOrders(params) {
    return http.request({
      url: '/api/orders',
      method: 'GET',
      data: params
    })
  },
  
  // 获取卖家订单列表
  getSellerOrders(params) {
    return http.request({
      url: '/api/orders/sold',
      method: 'GET',
      data: params
    })
  }
}

// 评价相关 API
export const reviewApi = {
  // 提交评价
  createReview(data) {
    return http.request({
      url: '/api/reviews',
      method: 'POST',
      data
    })
  },
  
  // 获取商品评价
  getProductReviews(productId, params) {
    return http.request({
      url: `/api/reviews/${productId}`,
      method: 'GET',
      data: params
    })
  },
  
  // 获取卖家收到的评价
  getSellerReviews(userId, params) {
    return http.request({
      url: `/api/reviews/seller/${userId}`,
      method: 'GET',
      data: params
    })
  },
  
  // 检查订单是否已评价
  checkOrderReviewed(orderId) {
    return http.request({
      url: `/api/reviews/order/${orderId}/exists`,
      method: 'GET'
    })
  },

  // 获取订单的评价详情（含 auditStatus）
  getReviewByOrderId(orderId) {
    return http.request({
      url: `/api/reviews/order/${orderId}`,
      method: 'GET'
    })
  },

  // 修改被拒绝的评价，重新提交审核
  updateReview(reviewId, data) {
    return http.request({
      url: `/api/reviews/${reviewId}`,
      method: 'PUT',
      data
    })
  }
}

// 消息相关 API
export const messageApi = {
  // 发送消息
  sendMessage(data) {
    return http.request({
      url: '/api/messages',
      method: 'POST',
      data
    })
  },
  
  // 获取聊天记录
  getConversation(userId, params) {
    return http.request({
      url: `/api/messages/conversation/${userId}`,
      method: 'GET',
      data: params
    })
  },
  
  // 获取会话列表
  getConversationList() {
    return http.request({
      url: '/api/messages/conversations',
      method: 'GET'
    })
  },
  
  // 获取未读消息数
  getUnreadCount() {
    return http.request({
      url: '/api/messages/unread',
      method: 'GET'
    })
  }
}

// 上传图片
export const uploadImage = (filePath) => {
  return http.uploadFile(filePath)
}
