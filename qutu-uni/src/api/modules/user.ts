/**
 * 用户模块 API
 */

import { get, post, put, request, BASE_URL } from '../request'
import CryptoJS from 'crypto-js'
import {
  mockUserInfo, 
  mockVipBenefits, 
  mockVipPackages,
  mockUserSettings,
  mockFrequentLocations,
  mockFavoriteLocations 
} from '../mock/user'

// 类型定义
export interface UserInfo {
  id: number
  username: string
  nickname: string
  name: string
  avatar: string
  phone: string
  email: string
  wechatBound: boolean
  vipLevel: number
  vipExpireDate: string
  createTime: string
  permissions: string[]
  roles: number[]
  stats: {
    trips: number
    spots: number
    guides: number
    footprints: number
  }
}

export interface VipBenefit {
  id: number
  icon: string
  name: string
  desc: string
}

export interface VipPackage {
  id: number
  name: string
  price: number
  originalPrice: number
  duration: number
  tag?: string
}

export interface UserSettings {
  language: string
  notifications: {
    tripReminder: boolean
    newGuides: boolean
    systemMessages: boolean
  }
  privacy: {
    showFootprints: boolean
    showTrips: boolean
  }
  mapSettings: {
    defaultZoom: number
    showTraffic: boolean
    mapStyle: string
  }
}

export interface Location {
  id: number
  name: string
  address: string
  latitude: number
  longitude: number
  type?: string
  image?: string
  addTime?: string
}

interface AuthConfig {
  tenantId?: string
  scope?: string
  passwordTokenUrl?: string
  passwordGrantType?: string
  smsTokenUrl?: string
  smsSendUrl?: string
  smsGrantType?: string
  socialTokenUrl?: string
  socialGrantType?: string
  socialState?: string
  logoutUrl?: string
  captchaEnabled?: boolean // 验证码是否启用
}

// API方法

/**
 * 解析后端返回的文件/图片URL
 * 
 * 后台管理端上传文件后，存入数据库的URL格式可能是：
 * 1. /api/admin/sys-file/local/xxx.jpg  （后台VITE_API_URL=/api，存的是带/api前缀的路径）
 * 2. /admin/sys-file/local/xxx.jpg       （相对路径，不含代理前缀）
 * 3. http://xxx/admin/sys-file/xxx.jpg   （完整URL）
 * 
 * 移动端不走 /api 代理，需要：
 * - 去掉 /api 前缀（这是后台管理端的 vite proxy 前缀，后端实际不识别）
 * - H5环境：BASE_URL=''，相对路径 /admin/... 通过 vite proxy 正工作
 * - 小程序环境：BASE_URL='http://localhost:9999'，需拼接完整URL
 */
export function resolveFileUrl(url: string): string {
  if (!url) return ''
  // 已经是完整URL，直接返回
  if (/^https?:\/\//i.test(url)) return url
  // 去掉后台管理端的代理前缀 /api（后端 context-path 是 /admin，不是 /api/admin）
  let cleanUrl = url.replace(/^\/api(\/)/, '/')
  // 拼接 BASE_URL
  return `${BASE_URL}${cleanUrl.startsWith('/') ? '' : '/'}${cleanUrl}`
}

/**
 * 获取验证码图片URL
 * 与后台管理端验证码实现方式完全一致：直接用image的src指向后端图片流接口
 * 后端接口: GET /code/image?randomStr=xxx
 * 
 * 单体模式（默认）: context-path=/admin，完整路径 /admin/code/image
 * 微服务模式: 通过网关路由 /auth/code/image
 * 
 * H5环境：由vite代理到后端
 * 小程序环境：BASE_URL + 路径 直连后端
 */
export function getCaptchaImageUrl(randomStr: string): string {
  // 单体模式下 context-path 为 /admin，验证码路径为 /admin/code/image
  return `${BASE_URL}/admin/code/image?randomStr=${randomStr}`
}

/**
 * 获取用户信息
 * 后端返回: { sysUser: { userId, username, nickname, avatar, phone, email, ... }, permissions: [], roles: [] }
 * request.ts 会自动解包 data，所以这里拿到的是 { sysUser, permissions, roles }
 * 需要映射为前端扁平的 UserInfo 结构
 */
export async function getUserInfo(): Promise<UserInfo> {
  const res: any = await get('/admin/user/info', undefined, mockUserInfo)
  const sysUser = res?.sysUser || res || {}
  return {
    id: sysUser.userId || 0,
    username: sysUser.username || '',
    nickname: sysUser.nickname || '',
    name: sysUser.name || '',
    avatar: resolveFileUrl(sysUser.avatar || ''),
    phone: sysUser.phone || '',
    email: sysUser.email || '',
    wechatBound: !!(sysUser.wxOpenid || sysUser.miniOpenid),
    vipLevel: 0,
    vipExpireDate: '',
    createTime: sysUser.createTime || '',
    permissions: res?.permissions || [],
    roles: res?.roles || [],
    stats: {
      trips: 0,
      spots: 0,
      guides: 0,
      footprints: 0
    }
  }
}

/**
 * 编辑用户信息（对应后端 PUT /admin/user/edit，接收 UserDTO）
 */
export function editUserInfo(data: Record<string, any>) {
  return request({
    url: '/admin/user/edit',
    method: 'PUT',
    header: { 'Content-Type': 'application/json' },
    data,
    useMock: false
  })
}

/**
 * 发送邮箱验证码
 */
export function sendEmailVerifyCode(email: string) {
  return request<{ success: boolean }>({
    url: '/admin/email/code',
    method: 'POST',
    header: { 'Content-Type': 'application/x-www-form-urlencoded' },
    data: { email },
    useMock: false
  })
}

/**
 * 绑定/更换邮箱（通过 editUserInfo 传递 email + code）
 */
export function bindEmail(data: { email: string; code: string }) {
  return request({
    url: '/admin/user/edit',
    method: 'PUT',
    header: { 'Content-Type': 'application/json' },
    data,
    useMock: false
  })
}

/**
 * 邮箱验证码登录（OAuth2 grant_type=email_code）
 */
export async function loginByEmailCode(email: string, code: string, imgCode?: string, randomStr?: string) {
  setOauthClient('app', 'app')
  const tokenResp: any = await request({
    url: '/admin/oauth2/token',
    method: 'POST',
    header: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: getBasicAuth()
    },
    data: {
      email,
      code,
      grant_type: 'email_code',
      scope: 'server',
      ...(imgCode ? { imgCode } : {}),
      ...(randomStr ? { randomStr } : {})
    },
    useMock: false
  })
  saveToken(tokenResp)
  return tokenResp
}

/**
 * 重置密码（使用邮箱验证码）
 */
export function resetPassword(data: { email: string; code: string; password: string }) {
  return request<{ success: boolean }>({
    url: '/admin/user/reset-password',
    method: 'PUT',
    header: { 'Content-Type': 'application/json' },
    data: { ...data, password: encryptPassword(data.password) },
    useMock: false
  })
}

/**
 * 更新用户信息（旧接口，保留兼容）
 */
export function updateUserInfo(data: Partial<UserInfo>) {
  return put<UserInfo>('/admin/user/info', data, { ...mockUserInfo, ...data })
}

/**
 * 登录
 */
export function login(data: { phone: string; code: string }) {
  return post<{ token: string; userInfo: UserInfo }>('/admin/user/login', data, { 
    token: 'mock_token_' + Date.now(), 
    userInfo: mockUserInfo 
  })
}

/**
 * 注册用户
 */
export function registerUser(data: { username: string; password: string; email: string; code?: string; randomStr?: string }) {
  const { username, password, email, code, randomStr } = data
  return request<{ success: boolean }>({
    url: '/admin/register/user',
    method: 'POST',
    header: {
      'Content-Type': 'application/json'
    },
    data: {
      username,
      password,
      email,
      ...(code ? { code } : {}),
      ...(randomStr ? { randomStr } : {})
    },
    useMock: false
  })
}

/**
 * 发送邮箱验证码
 */
export function sendEmailCode(email: string) {
  return request<{ code: number; data: boolean; msg: string }>({
    url: '/admin/email/code',
    method: 'POST',
    header: { 'Content-Type': 'application/x-www-form-urlencoded' },
    data: { email },
    useMock: false
  })
}

/**
 * 密码加密（与后台管理端一致：AES-CFB模式，key和iv相同）
 * 后端 PasswordDecoderFilter 会用同样的 key 解密
 */
const PWD_ENC_KEY = 'thanks,pig4cloud'

function encryptPassword(password: string): string {
  const key = CryptoJS.enc.Utf8.parse(PWD_ENC_KEY)
  const encrypted = CryptoJS.AES.encrypt(password, key, {
    iv: key,
    mode: CryptoJS.mode.CFB,
    padding: CryptoJS.pad.NoPadding
  })
  return encrypted.toString()
}

export function setOauthClient(clientId: string, clientSecret: string) {
  try {
    const raw = `${clientId}:${clientSecret}`
    let basic = ''
    if (typeof btoa !== 'undefined') {
      basic = btoa(raw)
    } else {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/='
      let str = raw
      let output = ''
      for (let block = 0, charCode, i = 0, map = chars;
        str.charAt(i | 0) || (map = '=', i % 1);
        output += map.charAt(63 & (block >> (8 - (i % 1) * 8)))
      ) {
        charCode = str.charCodeAt(i += 3 / 4)
        if (charCode > 0xFF) break
        block = (block << 8) | charCode
      }
      basic = output
    }
    uni.setStorageSync('basicAuth', `Basic ${basic}`)
  } catch (e) {}
}

function getBasicAuth() {
  try {
    return uni.getStorageSync('basicAuth') || ''
  } catch (e) {
    return ''
  }
}

export function setAuthConfig(config: AuthConfig) {
  try {
    uni.setStorageSync('authConfig', config)
  } catch (e) {}
}

export function getAuthConfig(): AuthConfig {
  try {
    return uni.getStorageSync('authConfig') || {}
  } catch (e) {
    return {}
  }
}

function saveToken(tokenResp: any) {
  const token = tokenResp?.access_token || ''
  if (token) {
    uni.setStorageSync('token', `${tokenResp?.token_type || 'Bearer'} ${token}`)
  }
  if (tokenResp?.refresh_token) {
    uni.setStorageSync('refresh_token', tokenResp.refresh_token)
  }
}

export async function loginBySms(mobile: string, code: string) {
  // 与后台管理端一致：设置Basic Auth
  setOauthClient('app', 'app')
  const tokenResp: any = await request({
    url: '/admin/oauth2/token',
    method: 'POST',
    header: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: getBasicAuth()
    },
    data: {
      mobile,
      code,
      grant_type: 'mobile',
      scope: 'server'
    },
    useMock: false
  })
  saveToken(tokenResp)
  return tokenResp
}

export async function loginByPassword(username: string, password: string, code?: string, randomStr?: string) {
  // 与后台管理端一致：设置Basic Auth（客户端ID:密钥）
  setOauthClient('app', 'app')
  const authConfig = getAuthConfig()
  const tokenResp: any = await request({
    url: authConfig.passwordTokenUrl || '/admin/oauth2/token',
    method: 'POST',
    header: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: getBasicAuth()
    },
    data: {
      username,
      password: encryptPassword(password),
      grant_type: 'password',
      scope: 'server',
      ...(code ? { code } : {}),
      ...(randomStr ? { randomStr } : {})
    },
    useMock: false
  })
  saveToken(tokenResp)
  return tokenResp
}

export async function wechatMpLogin() {
  const authConfig = getAuthConfig()
  // #ifdef MP-WEIXIN
  // 1. 调用wx.login获取code
  const loginRes = await new Promise<any>((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: resolve,
      fail: reject
    })
  })
  const wxCode = loginRes?.code

  // 2. 用code换取openid
  const sessionResp: any = await request({
    url: `/admin/wechat/mini/code2session?code=${wxCode}`,
    method: 'GET',
    useMock: false
  })
  const openid = sessionResp?.openid || sessionResp?.data?.openid

  if (!openid) {
    throw new Error('获取微信openid失败')
  }

  // 3. 用social grant_type + openid进行登录
  // 与后台管理端一致：设置Basic Auth（social客户端）
  setOauthClient('social', 'social')
  const tokenResp: any = await request({
    url: '/admin/oauth2/token',
    method: 'POST',
    header: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: getBasicAuth()
    },
    data: {
      code: openid,
      state: 'MINI',
      grant_type: 'social',
      scope: 'server'
    },
    useMock: false
  })
  saveToken(tokenResp)
  return tokenResp
  // #endif
  return { access_token: '', token_type: 'Bearer' }
}

/**
 * 获取微信小程序openid（不登录，仅获取openid用于绑定）
 */
export async function getWechatOpenid(): Promise<string> {
  // #ifdef MP-WEIXIN
  const loginRes = await new Promise<any>((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: resolve,
      fail: reject
    })
  })
  const wxCode = loginRes?.code
  const sessionResp: any = await request({
    url: `/admin/wechat/mini/code2session?code=${wxCode}`,
    method: 'GET',
    useMock: false
  })
  const openid = sessionResp?.openid || sessionResp?.data?.openid
  if (!openid) throw new Error('获取微信openid失败')
  return openid
  // #endif
  return ''
}

/**
 * 绑定微信（将微信openid绑定到当前登录用户）
 */
export function bindWechat(miniOpenid: string) {
  return request({
    url: '/admin/user/bindWechat',
    method: 'PUT',
    header: { 'Content-Type': 'application/x-www-form-urlencoded' },
    data: { miniOpenid },
    useMock: false
  })
}

/**
 * 解绑微信（清空当前用户的微信openid）
 */
export function unbindWechat() {
  return request({
    url: '/admin/user/unbindWechat',
    method: 'PUT',
    useMock: false
  })
}

/**
 * 登出
 */
export function logout() {
  const authConfig = getAuthConfig()
  return request<{ success: boolean }>({
    url: authConfig.logoutUrl || '/admin/token/logout',
    method: 'DELETE',
    useMock: false
  })
}

/**
 * 获取VIP权益列表
 */
export function getVipBenefits() {
  return get<VipBenefit[]>('/admin/vip/benefits', undefined, mockVipBenefits)
}

/**
 * 获取VIP套餐
 */
export function getVipPackages() {
  return get<VipPackage[]>('/admin/vip/packages', undefined, mockVipPackages)
}

/**
 * 购买VIP
 */
export function purchaseVip(packageId: number) {
  return post<{ success: boolean; orderId: string }>('/admin/vip/purchase', { packageId }, { 
    success: true, 
    orderId: 'ORDER_' + Date.now() 
  })
}

/**
 * 获取用户设置
 */
export function getUserSettings() {
  return get<UserSettings>('/admin/user/settings', undefined, mockUserSettings)
}

/**
 * 更新用户设置
 */
export function updateUserSettings(settings: Partial<UserSettings>) {
  return put<UserSettings>('/admin/user/settings', settings, { ...mockUserSettings, ...settings })
}

/**
 * 获取常用地点
 */
export function getFrequentLocations() {
  return get<Location[]>('/admin/user/frequent-locations', undefined, mockFrequentLocations)
}

/**
 * 添加常用地点
 */
export function addFrequentLocation(data: Omit<Location, 'id'>) {
  const newLocation: Location = { id: Date.now(), ...data }
  return post<Location>('/admin/user/frequent-locations', data, newLocation)
}

/**
 * 删除常用地点
 */
export function deleteFrequentLocation(id: number) {
  return post<{ success: boolean }>(`/admin/user/frequent-locations/${id}/delete`, undefined, { success: true })
}

/**
 * 获取收藏地点
 */
export function getFavoriteLocations() {
  return get<Location[]>('/admin/user/favorite-locations', undefined, mockFavoriteLocations)
}

/**
 * 删除收藏地点
 */
export function deleteFavoriteLocation(id: number) {
  return post<{ success: boolean }>(`/admin/user/favorite-locations/${id}/delete`, undefined, { success: true })
}

/**
 * 发送验证码
 */
export function sendVerifyCode(phone: string) {
  const authConfig = getAuthConfig()
  return request<{ success: boolean }>({
    url: authConfig.smsSendUrl || '/admin/mobile/sms',
    method: 'POST',
    header: {
      'Content-Type': 'application/x-www-form-urlencoded',
      ...(getBasicAuth() ? { Authorization: getBasicAuth() } : {})
    },
    data: {
      mobile: phone,
      ...(authConfig.tenantId ? { tenant_id: authConfig.tenantId } : {})
    },
    useMock: false
  })
}

/**
 * 获取用户统计数据
 */
export function getUserStats() {
  return get('/admin/user/stats', undefined, mockUserInfo.stats)
}

/**
 * 更新头像
 */
export function updateAvatar(avatarUrl: string) {
  return post<{ success: boolean; avatarUrl: string }>('/admin/user/avatar', { avatarUrl }, { 
    success: true, 
    avatarUrl 
  })
}

/**
 * 数据备份
 */
export function backupData() {
  return post<{ success: boolean; backupUrl: string }>('/admin/user/backup', undefined, { 
    success: true, 
    backupUrl: 'https://backup.qutuyunji.com/user_1_backup.zip' 
  })
}

/**
 * 数据恢复
 */
export function restoreData(backupUrl: string) {
  return post<{ success: boolean }>('/admin/user/restore', { backupUrl }, { success: true })
}
