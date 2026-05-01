/**
 * 故事/日记模块 API
 * 对接后端 qutu-story 模块
 */

import { get, post, put, del, request } from '../request'
import { resolveFileUrl } from './user'

// 类型定义
export interface Diary {
  id: number
  content: string
  image: string
  images?: string[]
  imagesCount?: number
  location: string
  locationName?: string
  date: string
  recordDate?: string
  day?: string
  weekday?: string
  mood?: string
  moodIcon?: string
  weather?: string
  weatherIcon?: string
  lng?: number
  lat?: number
  spotId?: number
}

export interface StoryTemplate {
  id: number
  name: string
  icon: string
  desc: string
}

// 分页响应
interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 获取日记列表（我的故事分页）
 * 后端：GET /stories/page
 */
export async function getDiaryList(params?: { current?: number; size?: number; date?: string }) {
  const current = params?.current ?? 1
  const size = params?.size ?? 20
  try {
    const page = await get<PageResponse<any>>('/admin/stories/page', {
      current,
      size,
      ...(params?.date ? { date: params.date } : {})
    })
    const records = page?.records || []
    return records.map((item: any) => mapStoryListVO(item))
  } catch {
    return []
  }
}

/**
 * 获取日记详情
 * 后端：GET /stories/{id}
 */
export async function getDiaryDetail(id: number) {
  try {
    const data = await request<any>({
      url: `/admin/stories/${id}`,
      method: 'GET',
    })
    const raw = data?.data || data
    return mapStoryDetailVO(raw)
  } catch {
    return null
  }
}

/**
 * 创建日记
 * 后端：POST /stories
 */
export async function createDiary(data: {
  content: string
  image?: string
  images?: string[]
  location?: string
  date: string
  mood?: string
  weather?: string
}) {
  try {
    const dto: any = {
      content: data.content,
      locationName: data.location || '',
      recordDate: data.date?.replace(/\//g, '-') || new Date().toISOString().slice(0, 10),
      mood: data.mood || '',
      weather: data.weather || '',
      images: data.images || (data.image ? [data.image] : []),
    }
    const res = await request<any>({
      url: '/admin/stories',
      method: 'POST',
      data: dto,
    })
    const id = res?.data || res
    return { id: typeof id === 'number' ? id : Date.now() }
  } catch (error) {
    console.error('创建日记失败:', error)
    return { id: Date.now() }
  }
}

/**
 * 更新日记
 * 后端：PUT /stories/{id}
 */
export async function updateDiary(id: number, data: Partial<Diary>) {
  try {
    const dto: any = {}
    if (data.content !== undefined) dto.content = data.content
    if (data.location !== undefined) dto.locationName = data.location
    if (data.mood !== undefined) dto.mood = data.mood
    if (data.weather !== undefined) dto.weather = data.weather
    if (data.images !== undefined) dto.images = data.images
    else if (data.image !== undefined) dto.images = data.image ? [data.image] : []
    await request<any>({
      url: `/admin/stories/${id}`,
      method: 'PUT',
      data: dto,
    })
    return true
  } catch (error) {
    console.error('更新日记失败:', error)
    return false
  }
}

/**
 * 删除日记
 * 后端：DELETE /stories/{id}
 */
export async function deleteDiary(id: number) {
  try {
    await request<any>({
      url: `/admin/stories/${id}`,
      method: 'DELETE',
    })
    return { success: true }
  } catch (error) {
    console.error('删除日记失败:', error)
    return { success: false }
  }
}

/**
 * 获取故事模板（本地静态数据）
 */
export function getStoryTemplates() {
  return [
    { id: 1, name: '旅途日记', icon: '📝', desc: '记录每一天的旅行点滴' },
    { id: 2, name: '美食记录', icon: '🍜', desc: '发现旅途中的美味' },
    { id: 3, name: '风景随拍', icon: '📷', desc: '用镜头捕捉美丽瞬间' },
    { id: 4, name: '心情感悟', icon: '💭', desc: '旅行中的所思所想' }
  ]
}

/**
 * 获取热门标签
 */
export function getStoryTags() {
  return ['旅行', '美食', '风景', '城市', '海边', '山川', '古镇', '夜景']
}

/**
 * 上传日记图片
 */
export function uploadDiaryImage(filePath: string) {
  return new Promise<{ url: string; path: string }>((resolve, reject) => {
    // 构建上传URL（uni.uploadFile 需要完整URL）
    let uploadUrl = '/admin/sys-file/upload'
    // #ifdef MP-WEIXIN
    uploadUrl = 'http://localhost:9999/admin/sys-file/upload'
    // #endif
    // #ifdef H5
    uploadUrl = '/admin/sys-file/upload'
    // #endif

    // 获取 token 注入 header
    const header: Record<string, string> = {}
    try {
      const token = uni.getStorageSync('token')
      if (token) {
        header['Authorization'] = /^Bearer\s/i.test(String(token)) ? String(token) : `Bearer ${token}`
      }
    } catch (e) {}

    uni.uploadFile({
      url: uploadUrl,
      filePath,
      name: 'file',
      header,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 0 && data.data?.url) {
            resolve({ url: resolveFileUrl(data.data.url), path: filePath })
          } else {
            reject(new Error(data.msg || '上传失败'))
          }
        } catch {
          reject(new Error('解析上传结果失败'))
        }
      },
      fail: (err) => reject(err),
    })
  })
}

/**
 * 根据日期获取日记
 */
export async function getDiaryByDate(date: string) {
  try {
    const list = await getDiaryList({ date: date.replace(/\//g, '-') })
    return list.length > 0 ? list[0] : null
  } catch {
    return null
  }
}

/**
 * 获取日记统计
 */
export async function getDiaryStats() {
  try {
    const list = await getDiaryList({ current: 1, size: 1 })
    // 简单统计，后续可增加专用接口
    return {
      totalDiaries: 0,
      totalWords: 0,
      totalLocations: 0,
      streak: 0,
    }
  } catch {
    return { totalDiaries: 0, totalWords: 0, totalLocations: 0, streak: 0 }
  }
}

// ========== 数据映射工具 ==========

const WEEKDAY_MAP: Record<string, string> = {
  'Monday': '周一', 'Tuesday': '周二', 'Wednesday': '周三',
  'Thursday': '周四', 'Friday': '周五', 'Saturday': '周六', 'Sunday': '周日',
}

const MOOD_ICON_MAP: Record<string, string> = {
  '开心': '😊', '愉快': '😄', '幸福': '🥰', '平静': '😌', '思考': '🤔', '感动': '😢',
}

const WEATHER_ICON_MAP: Record<string, string> = {
  '晴': '☀️', '多云': '⛅', '阴': '☁️', '雨': '🌧️', '雪': '❄️', '大风': '🌪️',
}

function mapStoryListVO(item: any): Diary {
  const recordDate = item.recordDate || ''
  const dateParts = recordDate.split('-')
  const day = dateParts.length >= 3 ? `${dateParts[1]}/${dateParts[2]}` : ''
  const weekday = item.weekday || WEEKDAY_MAP[item.weekday] || ''

  return {
    id: item.id,
    content: item.content || '',
    image: item.firstImage ? resolveFileUrl(item.firstImage) : '',
    imagesCount: item.imagesCount || 0,
    location: item.locationName || '',
    date: recordDate,
    day,
    weekday,
    mood: item.mood || '',
    moodIcon: MOOD_ICON_MAP[item.mood] || (item.mood ? '😊' : ''),
    weather: item.weather || '',
    weatherIcon: WEATHER_ICON_MAP[item.weather] || (item.weather ? '☀️' : ''),
  }
}

function mapStoryDetailVO(item: any): Diary {
  const recordDate = item.recordDate || ''
  const dateParts = recordDate.split('-')
  const day = dateParts.length >= 3 ? `${dateParts[1]}/${dateParts[2]}` : ''
  const weekday = item.weekday || WEEKDAY_MAP[item.weekday] || ''

  const images = (item.images || []).map((url: string) => resolveFileUrl(url))
  const firstImage = images.length > 0 ? images[0] : ''

  return {
    id: item.id,
    content: item.content || '',
    image: firstImage,
    images,
    imagesCount: images.length,
    location: item.locationName || '',
    date: recordDate,
    day,
    weekday,
    mood: item.mood || '',
    moodIcon: MOOD_ICON_MAP[item.mood] || (item.mood ? '😊' : ''),
    weather: item.weather || '',
    weatherIcon: WEATHER_ICON_MAP[item.weather] || (item.weather ? '☀️' : ''),
    lng: item.lng,
    lat: item.lat,
    spotId: item.spotId,
  }
}
