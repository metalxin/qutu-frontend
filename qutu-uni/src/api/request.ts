/**
 * 统一请求封装
 * 支持接口调用失败时自动降级到mock数据
 */

// API基础配置
export let BASE_URL = 'https://api.qutuyunji.com' // 默认生产环境API地址
// #ifdef H5
BASE_URL = '' // H5 走同源代理，留空以使用相对路径
// #endif
// #ifdef MP-WEIXIN
BASE_URL = 'http://localhost:9999' // 小程序开发环境本地服务（需在开发者工具允许或配置合法域名）
// #endif
const TIMEOUT = 10000 // 请求超时时间

// 是否启用mock模式（开发环境默认关闭，对接真实API）
export const USE_MOCK = false

// 请求配置接口
interface RequestConfig {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  useMock?: boolean
  mockData?: any
  timeout?: number
  __retry?: boolean
}

// 响应数据接口（用于真实API）
interface ApiResponse<T = any> {
  code?: number
  msg?: string
  message?: string
  error_description?: string
  data?: T
  [key: string]: any
}

/**
 * 发起请求
 * @param config 请求配置
 * @returns Promise<T> 直接返回数据
 */
export async function request<T = any>(config: RequestConfig): Promise<T> {
  const { url, method = 'GET', data, header = {}, useMock = USE_MOCK, mockData } = config

  // 如果强制使用mock或开发环境，直接返回mock数据
  if (useMock && mockData !== undefined) {
    console.log(`[Mock] ${method} ${url}`, mockData)
    return mockData as T
  }

  try {
    const finalUrl = /^https?:\/\//i.test(url) ? url : `${BASE_URL}${url}`
    let injectedAuth = false
    const headerPayload: Record<string, string> = {
      'Content-Type': 'application/json',
      ...header
    }
    if (!headerPayload['Authorization']) {
      try {
        const token = uni.getStorageSync('token')
        if (token) {
          headerPayload['Authorization'] = /^Bearer\s/i.test(String(token)) ? String(token) : `Bearer ${token}`
          injectedAuth = true
        }
      } catch (e) {}
    }
    const response = await new Promise<ApiResponse<T>>((resolve, reject) => {
      uni.request({
        url: finalUrl,
        method,
        data,
        timeout: config.timeout || TIMEOUT,
        header: headerPayload,
        success: (res: any) => {
          if (res.statusCode === 200) {
            resolve(res.data as ApiResponse<T>)
          } else {
            const err: any = new Error(`HTTP Error: ${res.statusCode}`)
            err.statusCode = res.statusCode
            err.response = res.data
            err.__injectedAuth = injectedAuth
            reject(err)
          }
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
    
    const anyResp: any = response as any
    // 后端统一返回结构：{ code, msg, data }，code !== 0 表示业务失败
    if (anyResp && typeof anyResp === 'object' && Object.prototype.hasOwnProperty.call(anyResp, 'code')) {
      const code = Number(anyResp.code)
      if (!Number.isNaN(code) && code !== 0) {
        const err: any = new Error(anyResp.msg || anyResp.message || anyResp.error_description || '请求失败')
        err.code = code
        err.response = anyResp
        throw err
      }
      return Object.prototype.hasOwnProperty.call(anyResp, 'data') ? anyResp.data as T : (anyResp as T)
    }
    return (anyResp && Object.prototype.hasOwnProperty.call(anyResp, 'data')) ? anyResp.data as T : (anyResp as T)
  } catch (error: any) {
    if (!config.__retry && error?.__injectedAuth && [401, 403, 424].includes(error?.statusCode)) {
      try {
        uni.removeStorageSync('token')
        uni.removeStorageSync('refresh_token')
      } catch (e) {}
      // 401时跳转到登录页
      if (error?.statusCode === 401) {
        const pages = getCurrentPages()
        const currentPage = pages[pages.length - 1]
        const currentPath = currentPage ? `/${currentPage.route}` : ''
        if (currentPath !== '/pages/user/login') {
          uni.showToast({ title: '请先登录', icon: 'none' })
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/user/login' })
          }, 500)
        }
        throw error
      }
      // 403/424重试时，不再注入Bearer token，但保留原有header（包括Basic Auth）
      const retryHeader: Record<string, string> = { ...header }
      delete retryHeader['Authorization']
      return request<T>({
        ...config,
        __retry: true,
        header: retryHeader
      })
    }
    console.warn(`[Request Failed] ${method} ${url}`, error)
    
    // 请求失败时降级到mock数据
    if (mockData !== undefined) {
      console.log(`[Fallback to Mock] ${method} ${url}`)
      return mockData as T
    }

    // 没有mock数据时抛出错误
    throw error
  }
}

/**
 * GET请求
 */
export function get<T = any>(url: string, params?: any, mockData?: T): Promise<T> {
  return request<T>({
    url,
    method: 'GET',
    data: params,
    mockData
  })
}

/**
 * POST请求
 */
export function post<T = any>(url: string, data?: any, mockData?: T, timeout?: number): Promise<T> {
  return request<T>({
    url,
    method: 'POST',
    data,
    mockData,
    timeout
  })
}

/**
 * PUT请求
 */
export function put<T = any>(url: string, data?: any, mockData?: T): Promise<T> {
  return request<T>({
    url,
    method: 'PUT',
    data,
    mockData
  })
}

/**
 * DELETE请求
 */
export function del<T = any>(url: string, params?: any, mockData?: T): Promise<T> {
  return request<T>({
    url,
    method: 'DELETE',
    data: params,
    mockData
  })
}

export default {
  request,
  get,
  post,
  put,
  del
}
