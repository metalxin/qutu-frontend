/**
 * 附近模块 API - 独立接口，与景点体系完全解耦
 * 后端路径: /admin/nearby/...
 * 前缀 /admin 通过 Vite 代理到 http://localhost:9999
 */

import { request } from '../request'
import { resolveFileUrl } from './user'

// ==================== 类型定义 ====================

export interface NearbyCategory {
  id: number
  name: string
  icon?: string
  sortOrder?: number
}

export interface NearbyPoi {
  id: number
  name: string
  title: string
  subtitle?: string
  coverUrl?: string
  rating: number
  ratingCount?: number
  priceText?: string
  distance: string
  address?: string
  cityName?: string
  categoryId?: number
  categoryName?: string
  categoryIcon?: string
  categoryType: string
  latitude: number
  longitude: number
  favoriteCount?: number
  checkinCount?: number
  viewCount?: number
  isFavorite?: boolean
  image?: string
  images: string[]
}

export interface NearbyPoiDetail {
  id: number
  name: string
  subtitle?: string
  coverUrl?: string
  rating: number
  ratingCount?: number
  priceText?: string
  distance?: string
  address?: string
  cityName?: string
  categoryId?: number
  categoryName?: string
  categoryIcon?: string
  latitude?: number
  longitude?: number
  phone?: string
  openTime?: string
  bestSeason?: string
  suitableFor?: string
  visitMinutes?: number
  description?: string
  gallery: string[]
  tips: Array<{ icon: string; title: string; desc: string; orderIndex?: number }>
  isFavorite?: boolean
  favoriteCount?: number
  checkinCount?: number
  viewCount?: number
}

export interface CityInfo {
  id: number
  name: string
  lat: number
  lng: number
}

// ==================== 公开接口 ====================

/** 获取附近分类 */
export async function getNearbyCategories(): Promise<NearbyCategory[]> {
  try {
    const raw = await request<any[]>({
      url: '/admin/nearby/categories',
      method: 'GET',
      useMock: false
    })
    return Array.isArray(raw) ? raw : []
  } catch {
    return [
      { id: 1, name: '美食', icon: '🍴' },
      { id: 2, name: '景点', icon: '🏞️' },
      { id: 3, name: '购物', icon: '🛍️' },
      { id: 4, name: '娱乐', icon: '🎮' },
      { id: 5, name: '住宿', icon: '🏨' }
    ]
  }
}

/** 获取附近POI列表 */
export async function getNearbyPois(params: {
  longitude: number
  latitude: number
  radius?: number
  limit?: number
  categoryId?: number | null
  cityName?: string
}): Promise<NearbyPoi[]> {
  try {
    const queryParams: Record<string, any> = {
      lng: params.longitude,
      lat: params.latitude,
      radius: params.radius ?? 50,
      limit: params.limit ?? 20
    }
    if (params.categoryId) queryParams.categoryId = params.categoryId
    if (params.cityName) queryParams.cityName = params.cityName

    const raw = await request<any[]>({
      url: '/admin/nearby/pois',
      method: 'GET',
      data: queryParams,
      useMock: false
    })
    if (!Array.isArray(raw)) return []
    return raw.map(mapPoi)
  } catch { return [] }
}

/** 获取POI详情 */
export async function getPoiDetail(id: number): Promise<NearbyPoiDetail | null> {
  try {
    const raw = await request<any>({
      url: `/admin/nearby/pois/${id}`,
      method: 'GET',
      useMock: false
    })
    if (!raw) return null
    return mapDetail(raw)
  } catch { return null }
}

// ==================== 需登录接口 ====================

/** 收藏POI */
export async function favoritePoi(poiId: number): Promise<boolean> {
  try {
    await request({ url: `/admin/nearby/pois/${poiId}/favorite`, method: 'POST', useMock: false })
    return true
  } catch { return false }
}

/** 取消收藏POI */
export async function unfavoritePoi(poiId: number): Promise<boolean> {
  try {
    await request({ url: `/admin/nearby/pois/${poiId}/unfavorite`, method: 'POST', useMock: false })
    return true
  } catch { return false }
}

/** 打卡POI */
export async function checkinPoi(poiId: number, data?: { lng?: number; lat?: number; remark?: string }): Promise<boolean> {
  try {
    await request({ url: `/admin/nearby/pois/${poiId}/checkin`, method: 'POST', data, useMock: false })
    return true
  } catch { return false }
}

/** 检查是否已收藏 */
export async function checkFavorite(poiId: number): Promise<boolean> {
  try {
    const res = await request<boolean>({ url: `/admin/nearby/pois/${poiId}/favorite`, method: 'GET', useMock: false })
    return !!res
  } catch { return false }
}

// ==================== 数据映射 ====================

const categoryTypeMap: Record<number, string> = {
  1: 'food',
  2: 'scenic',
  3: 'shopping',
  4: 'entertainment',
  5: 'hotel'
}

function getCategoryType(categoryId?: number, categoryName?: string): string {
  if (categoryId && categoryTypeMap[categoryId]) return categoryTypeMap[categoryId]
  if (categoryName) {
    const nameMap: Record<string, string> = { '美食': 'food', '景点': 'scenic', '购物': 'shopping', '娱乐': 'entertainment', '住宿': 'hotel' }
    if (nameMap[categoryName]) return nameMap[categoryName]
  }
  return 'place'
}

function mapPoi(item: any): NearbyPoi {
  const catType = getCategoryType(item.categoryId, item.categoryName)
  return {
    id: item.id,
    name: item.name,
    title: item.name,
    subtitle: item.subtitle || '',
    coverUrl: item.coverUrl || '',
    rating: Number(item.rating) || 0,
    ratingCount: item.ratingCount || 0,
    priceText: item.priceText || '',
    distance: item.distance ? `${item.distance}km` : '',
    address: item.address || '',
    cityName: item.cityName || '',
    categoryId: item.categoryId || null,
    categoryName: item.categoryName || '',
    categoryIcon: item.categoryIcon || '',
    categoryType: catType,
    latitude: Number(item.lat) || 0,
    longitude: Number(item.lng) || 0,
    favoriteCount: item.favoriteCount || 0,
    checkinCount: item.checkinCount || 0,
    viewCount: item.viewCount || 0,
    isFavorite: !!item.isFavorite,
    image: resolveFileUrl(item.coverUrl || ''),
    images: item.coverUrl ? [resolveFileUrl(item.coverUrl)] : []
  }
}

function mapDetail(data: any): NearbyPoiDetail {
  return {
    id: data.id,
    name: data.name || '',
    subtitle: data.subtitle ?? '',
    coverUrl: resolveFileUrl(data.coverUrl ?? ''),
    rating: Number(data.rating) ?? 0,
    ratingCount: data.ratingCount ?? 0,
    priceText: data.priceText ?? '',
    distance: data.distance ?? '',
    address: data.address ?? '',
    cityName: data.cityName ?? '',
    categoryId: data.categoryId,
    categoryName: data.categoryName ?? '',
    categoryIcon: data.categoryIcon ?? '',
    latitude: data.lat ? Number(data.lat) : undefined,
    longitude: data.lng ? Number(data.lng) : undefined,
    phone: data.phone ?? '',
    openTime: data.openTime ?? '',
    bestSeason: data.bestSeason ?? '',
    suitableFor: data.suitableFor ?? '',
    visitMinutes: data.visitMinutes,
    description: data.description ?? '',
    gallery: Array.isArray(data.gallery) ? data.gallery.map(resolveFileUrl) : [],
    tips: Array.isArray(data.tips) ? data.tips.map((t: any) => ({
      icon: t.icon || '💡', title: t.title || '', desc: t.desc || t.description || '', orderIndex: t.orderIndex ?? 0
    })) : [],
    isFavorite: !!data.isFavorite,
    favoriteCount: data.favoriteCount ?? 0,
    checkinCount: data.checkinCount ?? 0,
    viewCount: data.viewCount ?? 0
  }
}

// ==================== 兼容旧导出 ====================
export function getCityList() { return Promise.resolve([]) }
export function getMapMarkers() { return Promise.resolve([]) }
export function getSpotImages() { return Promise.resolve([]) }
export function getCityListLegacy() { return Promise.resolve([]) }
