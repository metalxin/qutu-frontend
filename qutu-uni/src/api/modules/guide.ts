/**
 * 攻略模块 API
 * 
 * 公开接口路径（后端 context-path=/admin）：
 *   GET /admin/guides/page       - 攻略分页
 *   GET /admin/guides/tags       - 攻略标签
 *   GET /admin/guides/{id}       - 攻略详情
 *   GET /admin/guides/related    - 相关攻略（?spotId=&limit=）
 * 
 * 后台管理接口路径：
 *   GET /admin/guide-admin/page  - 后台攻略分页
 *   GET /admin/guide-admin/{id}  - 后台攻略详情
 *   POST /admin/guide-admin      - 新增
 *   PUT /admin/guide-admin       - 修改
 *   DELETE /admin/guide-admin/{id} - 删除
 */

import { get, post, request } from '../request'
import { resolveFileUrl } from './user'
import { 
  mockGuideCategories, 
  mockGuideList, 
  mockGuideDetail,
  mockRelatedGuides 
} from '../mock/guide'

// 类型定义
export interface GuideCategory {
  id: number
  name: string
  icon: string
}

export interface GuideListItem {
  id: number
  title: string
  description: string
  cover: string
  author: string
  authorAvatar: string
  days: string
  likes: number
  views: number
  categoryId: number
}

export interface GuideDayItem {
  type: string
  title: string
  content: string
  spotId: number | null
  orderNum: number
}

export interface GuideDay {
  dayIndex: number
  title: string
  summary: string
  items: GuideDayItem[]
}

export interface GuideDetail {
  id: number
  title: string
  description: string
  cover: string
  city?: string
  days?: string
  tag?: string
  likes: number
  views: number
  overview: {
    summary: string
    transport: string
  }
  schedule: Array<{ day: string; route: string }>
  itinerary: GuideDay[]
}

export interface RelatedGuide {
  id: number
  title: string
  description: string
  cover: string
  days: string
  likes: number
  views: number
}

export interface GuideTag {
  name: string
  count: number
}

// 分页响应
interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// API方法

/**
 * 获取攻略分类
 */
export function getGuideCategories() {
  return get<GuideCategory[]>('/api/guide/categories', undefined, mockGuideCategories)
}

/**
 * 获取攻略标签（公开接口 /guides/tags）
 */
export async function getGuideTags(limit: number = 12) {
  try {
    const list = await get<GuideTag[]>('/admin/guides/tags', { limit })
    return Array.isArray(list) ? list : []
  } catch {
    const tags = mockGuideCategories.map(c => ({ name: c.name, count: 0 }))
    return tags.slice(0, limit)
  }
}

/**
 * 获取攻略列表
 */
export function getGuideList(params?: { categoryId?: number; keyword?: string; page?: number; pageSize?: number }) {
  let filteredList = [...mockGuideList]
  
  if (params?.categoryId) {
    filteredList = filteredList.filter(g => g.categoryId === params.categoryId)
  }
  
  if (params?.keyword) {
    const keyword = params.keyword.toLowerCase()
    filteredList = filteredList.filter(g => 
      g.title.toLowerCase().includes(keyword) || 
      g.description.toLowerCase().includes(keyword)
    )
  }
  
  return get<GuideListItem[]>('/api/guides', params, filteredList)
}

/**
 * 获取攻略详情（公开接口 /guides/{id}）
 * 后端返回 GuideDetailVO: { id, title, cover, summary, days, nights, tag, likeCount, viewCount, cityName, itinerary: [{dayIndex, title, summary, items: [{type, title, content, spotId, orderNum}]}] }
 */
export async function getGuideDetail(id: number) {
  try {
    const data = await request<any>({
      url: `/admin/guides/${id}`,
      method: 'GET',
      useMock: false,
      mockData: mockGuideDetail
    })
    const d = data && (data.data || data)
    const rawCover = typeof d.cover === 'string' ? d.cover.trim() : ''
    const cover = resolveFileUrl(rawCover.replace(/`/g, ''))
    const itinerary: GuideDay[] = Array.isArray(d.itinerary) ? d.itinerary : []

    const result: GuideDetail = {
      id: d.id,
      title: d.title ?? '',
      description: d.summary ?? '',
      cover,
      city: d.cityName ?? '',
      days: (typeof d.days === 'number' && typeof d.nights === 'number')
        ? `${d.days}天${d.nights}晚`
        : '',
      tag: d.tag ?? '',
      likes: d.likeCount ?? 0,
      views: d.viewCount ?? 0,
      overview: {
        summary: d.summary ?? '',
        transport: ''
      },
      schedule: itinerary.map(day => ({
        day: `Day${day.dayIndex}`,
        route: day.summary || day.title || ''
      })),
      itinerary
    }
    return result
  } catch {
    return { ...mockGuideDetail, id } as any
  }
}

/**
 * 分页获取精选攻略（公开接口 /guides/page）
 * 后端返回 MyBatis-Plus 分页: { records, total, size, current, pages }
 */
export async function getGuidesPage(params?: { current?: number; size?: number; keyword?: string; tag?: string; orderBy?: string }) {
  const current = params?.current ?? 1
  const size = params?.size ?? 10
  try {
    const page = await get<PageResponse<any>>('/admin/guides/page', {
      current,
      size,
      keyword: params?.keyword,
      tag: params?.tag,
      orderBy: params?.orderBy || 'viewCount'
    })
    const mappedRecords: GuideListItem[] = (page.records || []).map(item => {
      const rawCover = typeof item.cover === 'string' ? item.cover.trim() : ''
      const cover = resolveFileUrl(rawCover.replace(/`/g, ''))
      return {
        id: item.id,
        title: item.title,
        description: item.summary ?? '',
        cover,
        author: '',
        authorAvatar: '',
        days: item.durationText ?? '',
        likes: item.likeCount ?? 0,
        views: item.viewCount ?? 0,
        categoryId: 0
      }
    })
    return {
      ...page,
      records: mappedRecords
    } as PageResponse<GuideListItem>
  } catch {
    const fallback = mockGuideList.slice(0, size)
    return {
      records: fallback,
      total: mockGuideList.length,
      size,
      current,
      pages: Math.ceil(mockGuideList.length / size)
    } as PageResponse<GuideListItem>
  }
}

/**
 * 获取相关攻略（公开接口 /guides/related?spotId=&limit=）
 */
export async function getRelatedGuides(spotId: number, limit: number = 3) {
  try {
    const list = await get<Array<any>>('/admin/guides/related', { spotId, limit })
    return (list || []).map(item => {
      const rawCover = typeof item.cover === 'string' ? item.cover.trim() : ''
      const cover = resolveFileUrl(rawCover.replace(/`/g, ''))
      return {
        id: item.id,
        title: item.title,
        description: item.summary ?? '',
        cover,
        days: item.durationText ?? '',
        likes: item.likeCount ?? 0,
        views: item.viewCount ?? 0
      } as RelatedGuide
    })
  } catch {
    return mockRelatedGuides.slice(0, limit)
  }
}

/**
 * 点赞攻略
 */
export function likeGuide(guideId: number) {
  return post<{ success: boolean; likes: number }>(`/api/guides/${guideId}/like`, undefined, { 
    success: true, 
    likes: mockGuideDetail.likes + 1 
  })
}

/**
 * 收藏攻略
 */
export function collectGuide(guideId: number) {
  return request<boolean>({
    url: `/admin/guides/${guideId}/favorite`,
    method: 'POST',
    useMock: false
  })
}

/**
 * 取消收藏攻略
 */
export function uncollectGuide(guideId: number) {
  return request<boolean>({
    url: `/admin/guides/${guideId}/unfavorite`,
    method: 'POST',
    useMock: false
  })
}

/**
 * 切换收藏状态
 */
export async function toggleGuideCollect(guideId: number, isCollect: boolean) {
  if (isCollect) {
    await collectGuide(guideId)
    return { success: true, isCollected: true }
  } else {
    await uncollectGuide(guideId)
    return { success: true, isCollected: false }
  }
}

/**
 * 检查攻略收藏状态
 */
export function checkGuideFavorite(guideId: number) {
  return request<boolean>({
    url: `/admin/guides/${guideId}/favorite`,
    method: 'GET',
    useMock: false
  })
}

/**
 * 获取用户收藏的攻略列表
 */
export interface FavoriteGuideItem {
  id: number
  title: string
  cover: string
  summary: string
  durationText: string
  likeCount: number
  viewCount: number
  tag: string
  cityName: string
  favoriteTime: string
}

export interface FavoriteGuidePage {
  records: FavoriteGuideItem[]
  total: number
  size: number
  current: number
  pages: number
}

export function getUserFavoriteGuides(params?: { current?: number; size?: number }) {
  return request<FavoriteGuidePage>({
    url: '/admin/guides/user/favorites',
    method: 'GET',
    data: {
      current: params?.current ?? 1,
      size: params?.size ?? 20
    },
    useMock: false
  })
}

/**
 * 生成行程
 */
export function generateGuideTrip(guideId: number) {
  return post<{ success: boolean; tripId: number }>(`/api/guides/${guideId}/generate-trip`, undefined, { 
    success: true, 
    tripId: Date.now() 
  })
}

/**
 * 关注作者
 */
export function followAuthor(authorId: number) {
  return post<{ success: boolean }>(`/api/authors/${authorId}/follow`, undefined, { success: true })
}

/**
 * 搜索攻略
 */
export function searchGuides(keyword: string) {
  const results = mockGuideList.filter(g => 
    g.title.includes(keyword) || g.description.includes(keyword)
  )
  return get<GuideListItem[]>('/api/guides/search', { keyword }, results)
}

export interface InspirationItem {
  id: number
  title: string
  cover: string
  location: string
}

export async function getInspirations(limit: number = 6) {
  try {
    const list = await get<InspirationItem[]>('/admin/guides/inspirations', { limit })
    return Array.isArray(list) ? list.map(item => ({
      ...item,
      cover: resolveFileUrl(item.cover || '')
    })) : []
  } catch {
    return [
      { id: 1, title: '杭州西湖一日游攻略', location: '杭州 · Citywalk', cover: '' },
      { id: 2, title: '黄山日出观景指南', location: '安徽 · 自然风光', cover: '' },
      { id: 3, title: '成都美食探店之旅', location: '四川 · 美食', cover: '' },
      { id: 4, title: '厦门鼓浪屿漫步', location: '福建 · 海边', cover: '' }
    ]
  }
}

export async function searchInspirations(keyword: string, limit: number = 20) {
  try {
    const list = await get<InspirationItem[]>('/admin/guides/inspirations/search', { keyword, limit })
    return Array.isArray(list) ? list.map(item => ({
      ...item,
      cover: resolveFileUrl(item.cover || '')
    })) : []
  } catch {
    return []
  }
}

// 用户创建攻略相关类型
export interface GuideUserCreateDTO {
  title: string
  coverUrl?: string
  summary?: string
  days?: number
  nights?: number
  tag?: string
  cityId?: number
  cityName?: string
  itinerary?: GuideDayCreateDTO[]
}

export interface GuideDayCreateDTO {
  dayIndex?: number
  title?: string
  summary?: string
  items?: GuideDayItemCreateDTO[]
}

export interface GuideDayItemCreateDTO {
  type?: string
  title?: string
  content?: string
  spotId?: number
  orderNum?: number
}

export interface GuideShareCodeImportVO {
  guideId: number
  title: string
  cover: string
  summary: string
  days: number
  nights: number
  tag: string
  cityName: string
  itinerary: GuideDay[]
}

/**
 * 用户创建攻略
 */
export function createUserGuide(dto: GuideUserCreateDTO) {
  return request<number>({
    url: '/admin/guides/user/create',
    method: 'POST',
    data: dto,
    useMock: false
  })
}

/**
 * 获取用户创建的攻略列表
 */
export function getUserGuides(params?: { current?: number; size?: number }) {
  return request<FavoriteGuidePage>({
    url: '/admin/guides/user/guides',
    method: 'GET',
    data: {
      current: params?.current ?? 1,
      size: params?.size ?? 20
    },
    useMock: false
  })
}

/**
 * 生成攻略分享口令
 */
export function generateGuideShareCode(guideId: number, maxUseCount?: number) {
  return request<string>({
    url: `/admin/guides/share-code/${guideId}`,
    method: 'POST',
    data: maxUseCount ? { maxUseCount } : {},
    useMock: false
  })
}

/**
 * 通过口令导入攻略
 */
export function importGuideByShareCode(shareCode: string) {
  return request<GuideShareCodeImportVO>({
    url: '/admin/guides/share-code/import',
    method: 'POST',
    data: { shareCode },
    useMock: false
  })
}
