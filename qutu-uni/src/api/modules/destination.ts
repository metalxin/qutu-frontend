/**
 * 目的地模块 API
 */

import { get, post, request } from '../request'
import { resolveFileUrl } from './user'
import {
  mockDestinations, 
  mockSpotDetail, 
  mockRegionGroups, 
  mockHotCities,
  mockComments 
} from '../mock/destination'
import { mockSpotDatabase } from '../mock/planning'

// 类型定义
export interface Destination {
  id: number
  name: string
  subtitle: string
  image: string
  gradient: string
  spots: number
  rating: number
  tag: string
  province?: string
  intro?: string
  isFavorite?: boolean
}

export interface SpotDetail {
  id: number
  name: string
  subtitle: string
  cover: string
  rating: number
  reviewCount?: number
  reviewSource: string
  distance: string
  description: string
  locationTitle: string
  locationDetail: string
  price: string
  priceNote: string
  gallery: string[]
  tips: Array<{ icon: string; title: string; desc: string }>
  isFavorite?: boolean
  favoriteCount?: number
  viewCount?: number
  tags?: string[]
  provinceName?: string
  provinceCode?: string
  countryName?: string
  countryCode?: string
  lat?: number
  lng?: number
}

export interface SpotListItem {
  id: number
  name: string
  rating: number
  duration: string
  price: string
  image: string
  category: string
  description: string
  tips: string
  openTime: string
  address: string
  latitude: number
  longitude: number
}

export interface Comment {
  id: number
  userId: number
  userName: string
  userAvatar: string
  content: string
  images: string[]
  likes: number
  isLiked: boolean
  createTime: string
  replies: Reply[]
  showAllReplies: boolean
}

export interface Reply {
  id: number
  userId: number
  userName: string
  userAvatar: string
  content: string
  likes: number
  isLiked: boolean
  createTime: string
  replyTo: string
}

interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// API方法

/**
 * 获取目的地列表
 */
export async function getDestinations(params?: { region?: string; page?: number; pageSize?: number }) {
  const current = params?.page ?? 1
  const size = params?.pageSize ?? 1000
  try {
    const page = await request<PageResponse<Destination>>({
      url: '/admin/destinations/page',
      method: 'GET',
      useMock: false,
      mockData: {
        records: mockDestinations,
        total: mockDestinations.length,
        size,
        current,
        pages: Math.ceil(mockDestinations.length / size)
      },
      data: {
        current,
        size,
        region: params?.region
      }
    })
    // 处理后端返回的图片URL
    const records = (page.records || []).map((item: any) => ({
      ...item,
      image: resolveFileUrl(item.image || item.coverUrl || '')
    }))
    return records
  } catch {
    return mockDestinations
  }
}

/**
 * 获取目的地详情
 */
export function getDestinationDetail(id: number) {
  // 根据id返回不同的mock数据（实际项目中会从服务器获取）
  const detail = { ...mockSpotDetail, id }
  return get<SpotDetail>(`/api/destinations/${id}`, undefined, detail)
}

/**
 * 获取景点详情
 */
export async function getSpotDetail(id: number): Promise<SpotDetail> {
  try {
    const raw = await request<any>({
      url: `/admin/spots/${id}`,
      method: 'GET',
      useMock: false,
      mockData: mockSpotDetail
    })
    const data = raw && (raw.data || raw) // 兼容 {code, data} 或直接对象
    const detail: SpotDetail = {
      id: data.id,
      name: data.name,
      subtitle: data.subtitle ?? '',
      cover: resolveFileUrl(data.cover ?? data.coverUrl ?? ''),
      rating: data.rating ?? 0,
      reviewCount: data.reviewCount ?? data.ratingCount ?? 0,
      reviewSource: data.reviewSource ?? '',
      distance: data.distance ?? '',
      description: data.description ?? '',
      locationTitle: data.locationTitle ?? data.cityName ?? '',
      locationDetail: data.locationDetail ?? data.address ?? '',
      price: data.price ?? data.priceText ?? '',
      priceNote: data.priceNote ?? '',
      gallery: Array.isArray(data.gallery) ? data.gallery.map(resolveFileUrl) : [],
      tips: Array.isArray(data.tips) ? data.tips : [],
      isFavorite: !!data.isFavorite,
      favoriteCount: data.favoriteCount ?? 0,
      viewCount: data.viewCount ?? 0,
      tags: data.tags ?? []
    }
    return detail
  } catch {
    return mockSpotDetail
  }
}

export async function getDestinationSpots(cityId: number, params?: { limit?: number }) {
  const limit = params?.limit ?? 20
  try {
    const raw = await request<Array<{
      id: number
      name: string
      subtitle?: string
      coverUrl?: string
      rating?: number
      ratingCount?: number
      priceText?: string
      distance?: string | null
      address?: string
      tags?: string[]
      favoriteCount?: number
      commentCount?: number
    }>>({
      url: `/admin/spots/city/${cityId}`,
      method: 'GET',
      useMock: false,
      mockData: [],
      data: { limit }
    })
    const mapped: SpotListItem[] = (raw || []).map(item => ({
      id: item.id,
      name: item.name,
      rating: item.rating ?? 0,
      duration: '',
      price: item.priceText ?? '',
      image: resolveFileUrl(item.coverUrl ?? ''),
      category: item.tags && item.tags.length ? item.tags[0] : (item.subtitle ?? ''),
      description: item.subtitle ?? '',
      tips: '',
      openTime: '',
      address: item.address ?? '',
      latitude: 0,
      longitude: 0
    }))
    return mapped
  } catch {
    // 回退到内置 mock
    const availableCities = Object.keys(mockSpotDatabase)
    const targetCity = availableCities[0] || '北京'
    const spots = mockSpotDatabase[targetCity] || []
    return spots
  }
}

/**
 * 获取地区分组
 */
export function getRegionGroups() {
  return request<Array<{ name: string; provinces: string[] }>>({
    url: '/admin/destinations/regions',
    method: 'GET',
    useMock: false,
    mockData: mockRegionGroups
  })
}

/**
 * 获取热门城市
 */
export function getHotCities() {
  return get<string[]>('/api/cities/hot', undefined, mockHotCities)
}

/**
 * 获取景点评论
 */
export async function getSpotComments(spotId: number, params?: { current?: number; size?: number }) {
  try {
    const res = await request<{ records: Comment[]; total: number; size: number; current: number; pages: number }>({
      url: `/admin/spots/${spotId}/comments`,
      method: 'GET',
      useMock: false,
      data: {
        current: params?.current ?? 1,
        size: params?.size ?? 20
      }
    })
    const records = (res?.records || []).map((item: any) => ({
      ...item,
      userAvatar: resolveFileUrl(item.userAvatar || ''),
      showAllReplies: false,
      replyCount: item.replyCount ?? (item.replies?.length || 0)
    }))
    return { records, total: res?.total || 0 }
  } catch {
    return { records: [], total: 0 }
  }
}

/**
 * 发表评论
 */
export async function postComment(spotId: number, data: { content: string; images?: string[] }) {
  return request<Comment>({
    url: `/admin/spots/${spotId}/comments`,
    method: 'POST',
    useMock: false,
    data
  })
}

/**
 * 回复评论
 */
export async function replyComment(commentId: number, data: { content: string; replyTo: string }) {
  return request<Reply>({
    url: `/admin/comments/${commentId}/replies`,
    method: 'POST',
    useMock: false,
    data
  })
}

/**
 * 点赞评论
 */
export function likeComment(commentId: number) {
  return request<boolean>({
    url: `/admin/comments/${commentId}/like`,
    method: 'POST',
    useMock: false
  })
}

/**
 * 取消点赞评论
 */
export function unlikeComment(commentId: number) {
  return request<boolean>({
    url: `/admin/comments/${commentId}/like`,
    method: 'DELETE',
    useMock: false
  })
}

/**
 * 删除评论
 */
export function deleteComment(commentId: number) {
  return request<boolean>({
    url: `/admin/comments/${commentId}`,
    method: 'DELETE',
    useMock: false
  })
}

/**
 * 收藏景点
 */
export interface FavoriteSpotItem {
  id: number
  name: string
  subtitle?: string
  coverUrl: string
  rating: number
  ratingCount?: number
  priceText?: string
  distance?: string
  address?: string
  cityName?: string
  tags?: string[]
  favoriteCount?: number
  commentCount?: number
  viewCount?: number
  favoriteTime?: string
}

export interface FavoriteSpotPage {
  records: FavoriteSpotItem[]
  total: number
  size: number
  current: number
  pages: number
}

export function favoriteSpot(spotId: number) {
  return request<boolean>({
    url: `/admin/spots/${spotId}/favorite`,
    method: 'POST',
    useMock: false
  })
}

export function unfavoriteSpot(spotId: number) {
  return request<boolean>({
    url: `/admin/spots/${spotId}/unfavorite`,
    method: 'POST',
    useMock: false
  })
}

export function getUserFavoriteSpots(params?: { current?: number; size?: number }) {
  return request<FavoriteSpotPage>({
    url: '/admin/spots/user/favorites',
    method: 'GET',
    data: {
      current: params?.current ?? 1,
      size: params?.size ?? 20
    },
    useMock: false
  })
}

export async function searchDestinations(keyword: string, limit: number = 20) {
  try {
    const list = await request<Destination[]>({
      url: '/admin/destinations/search',
      method: 'GET',
      useMock: false,
      data: { keyword, limit }
    })
    const records = (Array.isArray(list) ? list : []).map((item: any) => ({
      ...item,
      image: resolveFileUrl(item.image || item.coverUrl || '')
    }))
    return records
  } catch {
    return []
  }
}
