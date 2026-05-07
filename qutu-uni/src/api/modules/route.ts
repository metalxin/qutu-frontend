import { request, get, post, put, del } from '../request'
import { mockRouteRecords, mockRouteDetail } from '../mock/route'

export interface RoutePoint {
  latitude: number
  longitude: number
  timestamp: number
  speed?: number
  recordTime?: string
}

export interface RoutePhoto {
  id: number
  url: string
  thumbnailUrl: string
  latitude: number
  longitude: number
  timestamp: number
  description?: string
}

export interface RouteRecord {
  id: number
  title: string
  activityType: string
  activityIcon: string
  startTime: string
  endTime: string
  duration: number
  distance: number
  avgSpeed: number
  maxSpeed: number
  pointCount: number
  photoCount: number
  coverPhoto: string
  startLat: number
  startLng: number
  endLat: number
  endLng: number
  mapStyle: string
  createTime: string
}

export interface RouteDetail extends RouteRecord {
  points: RoutePoint[]
  photos: RoutePhoto[]
}

export async function getRouteRecords(params?: { page?: number; pageSize?: number }) {
  try {
    const res = await request<any>({
      url: '/admin/route/records',
      method: 'GET',
      data: {
        current: params?.page ?? 1,
        size: params?.pageSize ?? 20
      },
      useMock: false,
      mockData: {
        records: mockRouteRecords,
        total: mockRouteRecords.length
      }
    })
    const records = res?.records || res?.list || (Array.isArray(res) ? res : [])
    const total = res?.total ?? records.length
    return { list: records as RouteRecord[], total }
  } catch {
    return { list: mockRouteRecords as RouteRecord[], total: mockRouteRecords.length }
  }
}

export function getRouteDetail(id: number) {
  return request<RouteDetail>({
    url: `/admin/route/records/${id}`,
    method: 'GET',
    useMock: false,
    mockData: mockRouteDetail
  })
}

export function saveRouteRecord(data: {
  title: string
  activityType: string
  activityIcon: string
  startTime: string
  endTime: string
  duration: number
  distance: number
  avgSpeed: number
  maxSpeed: number
  points: RoutePoint[]
  photos?: RoutePhoto[]
  mapStyle: string
  remark?: string
}) {
  return request<RouteRecord>({
    url: '/admin/route/records',
    method: 'POST',
    data,
    useMock: false,
    mockData: {
      id: Date.now(),
      title: data.title,
      activityType: data.activityType,
      activityIcon: data.activityIcon,
      startTime: data.startTime,
      endTime: data.endTime,
      duration: data.duration,
      distance: data.distance,
      avgSpeed: data.avgSpeed,
      maxSpeed: data.maxSpeed,
      pointCount: data.points.length,
      photoCount: data.photos?.length ?? 0,
      coverPhoto: '',
      startLat: data.points.length > 0 ? data.points[0].latitude : 0,
      startLng: data.points.length > 0 ? data.points[0].longitude : 0,
      endLat: data.points.length > 0 ? data.points[data.points.length - 1].latitude : 0,
      endLng: data.points.length > 0 ? data.points[data.points.length - 1].longitude : 0,
      mapStyle: data.mapStyle,
      createTime: new Date().toISOString()
    }
  })
}

export function deleteRouteRecord(id: number) {
  return request<{ success: boolean }>({
    url: `/admin/route/records/${id}`,
    method: 'DELETE',
    useMock: false,
    mockData: { success: true }
  })
}

export function deleteRouteRecordPost(id: number) {
  return request<{ success: boolean }>({
    url: `/admin/route/records/${id}/delete`,
    method: 'POST',
    useMock: false,
    mockData: { success: true }
  })
}

export function updateRouteRecord(id: number, data: Partial<RouteRecord>) {
  return put<RouteRecord>(`/admin/route/records/${id}`, data, { ...mockRouteDetail, ...data })
}

export function uploadRoutePhoto(data: { file: string; routeId?: number; latitude: number; longitude: number; timestamp: number }) {
  return post<RoutePhoto>('/admin/route/photos', data, {
    id: Date.now(),
    url: data.file,
    thumbnailUrl: data.file,
    latitude: data.latitude,
    longitude: data.longitude,
    timestamp: data.timestamp
  })
}
