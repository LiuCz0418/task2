// API 基础配置
// 开发环境使用 localhost，真机调试时需要改为电脑的局域网 IP
// 例如：'http://192.168.1.100:8080'
const BASE_URL = 'http://localhost:8080'

// 获取当前环境的基础 URL
const getBaseUrl = () => {
  // #ifdef H5
  // H5 环境使用相对路径或 localhost
  return BASE_URL
  // #endif
  
  // #ifdef MP-WEIXIN
  // 微信小程序环境
  const systemInfo = uni.getSystemInfoSync()
  const platform = systemInfo.platform
  
  // 如果是开发者工具，使用 localhost
  if (platform === 'devtools') {
    return BASE_URL
  }
  
  // 真机调试时，从 storage 获取配置的 IP 和端口
  const serverIp = uni.getStorageSync('serverIp')
  const serverPort = uni.getStorageSync('serverPort') || '8080'
  if (serverIp) {
    return `http://${serverIp}:${serverPort}`
  }
  
  // 默认返回 localhost（真机调试时会失败，提示用户配置）
  return BASE_URL
  // #endif
  
  return BASE_URL
}

// 请求拦截
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const baseUrl = getBaseUrl()
    
    uni.request({
      url: baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else if (res.data.code === 401) {
            // 未登录、token过期或登录失败
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.showToast({
              title: res.data.message || '请先登录',
              icon: 'none'
            })
            setTimeout(() => {
              uni.navigateTo({
                url: '/pages/login/login'
              })
            }, 1500)
            reject(res.data)
          } else {
            uni.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else {
          uni.showToast({
            title: '网络请求失败',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// 上传文件
const uploadFile = (filePath) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const baseUrl = getBaseUrl()
    
    uni.uploadFile({
      url: baseUrl + '/api/upload/image',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(data)
          }
        } else {
          reject(res)
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

export default {
  request,
  uploadFile,
  get BASE_URL() {
    return getBaseUrl()
  }
}
