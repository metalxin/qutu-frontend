/**
 * 附近模块 API
 */

import { get, post } from '../request'
import { 
  mockNearbyCategories, 
  mockNearbyPois, 
  mockCityList,
  mockMapMarkers 
} from '../mock/nearby'

// 类型定义
export interface NearbyCategory {
  id: string
  name: string
  emoji: string
}

export interface NearbyPoi {
  id: number
  name: string
  title: string
  subtitle?: string
  category: string
  categoryId: string
  categoryType: string
  distance: string
  rating: number
  address: string
  phone: string
  openTime: string
  price: string
  description?: string
  image?: string
  images: string[]
  latitude: number
  longitude: number
  screenX?: number
  screenY?: number
}

export interface CityList {
  hot: string[]
  hotCities: string[]
  groups: Array<{ letter: string; cities: string[] }>
  cityGroups: Array<{ letter: string; cities: string[] }>
}

export interface MapMarker {
  id: number
  latitude: number
  longitude: number
  iconPath: string
  width: number
  height: number
  callout?: {
    content: string
    display: string
    fontSize: number
    borderRadius: number
    padding: number
  }
}

// API方法

/**
 * 获取附近分类
 */
export function getNearbyCategories() {
  return get<NearbyCategory[]>('/api/destination/categories/tree', undefined, mockNearbyCategories)
}

/**
 * 获取附近POI列表
 */
export function getNearbyPois(params?: { 
  categoryId?: string
  latitude?: number
  longitude?: number
  radius?: number
  page?: number
  pageSize?: number 
}) {
  let filteredPois = [...mockNearbyPois]
  
  // 根据分类筛选
  if (params?.categoryId && params.categoryId !== 'all') {
    filteredPois = filteredPois.filter(p => p.categoryId === params.categoryId)
  }
  
  return get<NearbyPoi[]>('/api/destination/spots/nearby', params, filteredPois)
}

/**
 * 获取POI详情
 */
export function getPoiDetail(id: number) {
  const poi = mockNearbyPois.find(p => p.id === id) || mockNearbyPois[0]
  return get<NearbyPoi>(`/api/destination/spots/${id}`, undefined, poi)
}

/**
 * 获取城市列表
 */
export function getCityList() {
  return get<CityList>('/api/cities', undefined, mockCityList)
}

/**
 * 搜索城市
 */
export function searchCities(keyword: string) {
  const allCities = mockCityList.groups.flatMap(g => g.cities)
  const results = allCities.filter(city => city.includes(keyword))
  return get<string[]>('/api/cities/search', { keyword }, results)
}

/**
 * 获取地图标记
 */
export function getMapMarkers(params?: { latitude?: number; longitude?: number; categoryId?: string }) {
  return get<MapMarker[]>('/api/nearby/markers', params, mockMapMarkers)
}

/**
 * 获取当前位置天气
 */
export function getWeather(params: { latitude: number; longitude: number }) {
  const mockWeather = {
    city: '杭州',
    temperature: 15,
    weather: '晴',
    humidity: 65,
    wind: '东南风3级'
  }
  return get('/api/weather', params, mockWeather)
}

/**
 * 添加地点到收藏
 */
export function addPoiToFavorite(poiId: number) {
  return post<{ success: boolean }>(`/api/nearby/pois/${poiId}/favorite`, undefined, { success: true })
}

/**
 * 添加地点到行程
 */
export function addPoiToTrip(poiId: number, tripId: number) {
  return post<{ success: boolean }>('/api/trip/add-poi', { poiId, tripId }, { success: true })
}
