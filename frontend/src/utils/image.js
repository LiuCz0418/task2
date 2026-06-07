/**
 * 图片URL处理工具
 * 统一处理图片路径，支持真机调试动态IP配置
 */

// 图片缓存对象，避免重复下载
const imageCache = {}

/**
 * 获取服务器基础URL
 */
function getServerUrl() {
  // H5 浏览器环境：跟随当前页面的主机名。
  // 例如页面从 http://192.168.5.32:8081 打开时，上传图片应访问 http://192.168.5.32:8080。
  // #ifdef H5
  if (typeof window !== 'undefined' && window.location && window.location.hostname) {
    return `${window.location.protocol}//${window.location.hostname}:8080`
  }
  // #endif

  const systemInfo = uni.getSystemInfoSync()
  const platform = systemInfo.platform
  
  // 开发工具环境使用localhost
  if (platform === 'devtools') {
    return 'http://localhost:8080'
  }
  
  // 真机环境从 storage读取配置
  const serverIp = uni.getStorageSync('serverIp')
  const serverPort = uni.getStorageSync('serverPort') || '8080'
  
  if (serverIp) {
    return `http://${serverIp}:${serverPort}`
  }
  
  // 默认返回localhost（真机会失败，提示配置）
  return 'http://localhost:8080'
}

/**
 * 判断是否为真机环境
 */
function isRealDevice() {
  const systemInfo = uni.getSystemInfoSync()
  return systemInfo.platform !== 'devtools'
}

/**
 * 真机环境下下载图片到本地
 */
async function downloadImageToLocal(url) {
  return new Promise((resolve, reject) => {
    // 检查缓存
    if (imageCache[url]) {
      resolve(imageCache[url])
      return
    }
    
    const baseUrl = getServerUrl()
    const fullUrl = `${baseUrl}${url}`
    
    uni.downloadFile({
      url: fullUrl,
      success: (res) => {
        if (res.statusCode === 200) {
          // 保存到缓存
          imageCache[url] = res.tempFilePath
          resolve(res.tempFilePath)
        } else {
          reject(new Error(`下载失败: ${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 处理图片URL
 * @param {string} url - 图片URL
 * @param {string} defaultImage - 默认图片路径
 * @returns {string|Promise} 处理后的完整URL或Promise
 */
export function getImageUrl(url, defaultImage = '/static/images/placeholder.png') {
  // 空值返回默认图片
  if (!url) {
    return defaultImage
  }
  
  // HTTP/HTTPS开头的外部链接，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  
  // /static/ 开头的是小程序本地静态资源，直接返回
  if (url.startsWith('/static/')) {
    return url
  }
  
  const baseUrl = getServerUrl()
  
  // 真机环境下，返回代理URL（虽然不能直接用于image标签，但可以用于downloadFile）
  if (isRealDevice()) {
    // 返回完整的HTTP URL，用于uni.downloadFile
    const fullUrl = baseUrl + url
    console.log('[图片URL-真机]', url, '->', fullUrl)
    return fullUrl
  }
  
  // 开发工具环境直接返回完整URL
  const fullUrl = baseUrl + url
  console.log('[图片URL-开发]', url, '->', fullUrl)
  return fullUrl
}

/**
 * 获取头像URL（带默认头像）
 */
export function getAvatarUrl(url) {
  return getImageUrl(url, 'https://api.dicebear.com/7.x/avataaars/svg?seed=default')
}

export default {
  getImageUrl,
  getAvatarUrl,
  getServerUrl
}
