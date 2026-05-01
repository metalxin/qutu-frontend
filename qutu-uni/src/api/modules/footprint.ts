/**
 * 足迹模块 API
 */

import { get, post } from '../request'
import { 
  mockFootprintRecords, 
  mockFootprintStats, 
  mockActivityMaps,
  mockProvinces,
  mockCountries 
} from '../mock/footprint'

// 类型定义
export interface FootprintRecord {
  id: number
  name: string
  type: string
  date: string
  image: string
  spots: string[]
}

export interface FootprintStats {
  totalProvinces: number
  totalCities: number
  totalSpots: number
  totalCountries: number
  ranking: number
}

export interface ActivityMap {
  id: string
  type: string
  name: string
  title: string
  subtitle: string
  count: string
  bgType: string
  items?: Array<{ emoji: string; text: string }>
}

export interface Province {
  id: number
  name: string
  visited: boolean
  citiesTotal: number
  citiesVisited: number
}

export interface Country {
  id: number
  name: string
  code: string
  visited: boolean
  continent: string
}

// API方法

/**
 * 获取足迹记录列表
 */
export function getFootprintRecords(params?: { page?: number; pageSize?: number }) {
  const mockResult = {
    list: mockFootprintRecords,
    total: mockFootprintRecords.length
  }
  return get<{ list: FootprintRecord[]; total: number }>('/api/footprints', params, mockResult)
}

/**
 * 获取足迹活动列表
 */
export function getFootprintActivities() {
  return get<ActivityMap[]>('/api/footprints/activities', undefined, mockActivityMaps)
}

/**
 * 获取足迹统计
 */
export function getFootprintStats() {
  return get<FootprintStats>('/api/footprints/stats', undefined, mockFootprintStats)
}

/**
 * 获取活动地图列表
 */
export function getActivityMaps() {
  return get<ActivityMap[]>('/api/footprints/activities', undefined, mockActivityMaps)
}

/**
 * 获取省份列表（中国漫游者）
 */
export function getProvinces() {
  return get<Province[]>('/api/footprints/provinces', undefined, mockProvinces)
}

/**
 * 获取国家列表（世界探险家）
 */
export function getCountries() {
  return get<Country[]>('/api/footprints/countries', undefined, mockCountries)
}

/**
 * 点亮省份
 */
export function lightUpProvince(provinceId: number) {
  return post<{ success: boolean }>(`/api/footprints/provinces/${provinceId}/light`, undefined, { success: true })
}

/**
 * 点亮城市
 */
export function lightUpCity(cityId: number) {
  return post<{ success: boolean }>(`/api/footprints/cities/${cityId}/light`, undefined, { success: true })
}

/**
 * 点亮国家
 */
export function lightUpCountry(countryId: number) {
  return post<{ success: boolean }>(`/api/footprints/countries/${countryId}/light`, undefined, { success: true })
}

/**
 * 添加足迹记录
 */
export function addFootprintRecord(data: { name: string; type: string; spots: string[]; image?: string }) {
  const newRecord: FootprintRecord = {
    id: Date.now(),
    name: data.name,
    type: data.type,
    date: new Date().toISOString().split('T')[0],
    image: data.image || '',
    spots: data.spots
  }
  return post<FootprintRecord>('/api/footprints', data, newRecord)
}

/**
 * 删除足迹记录
 */
export function deleteFootprintRecord(recordId: number) {
  return post<{ success: boolean }>(`/api/footprints/${recordId}/delete`, undefined, { success: true })
}

/**
 * 生成足迹分享图
 */
export function generateShareImage(type: 'china' | 'world') {
  const mockShareImage = {
    imageUrl: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&q=80',
    stats: type === 'china' ? mockProvinces.filter(p => p.visited).length : mockCountries.filter(c => c.visited).length
  }
  return post('/api/footprints/share-image', { type }, mockShareImage)
}
