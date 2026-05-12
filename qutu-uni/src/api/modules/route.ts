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
    // request() 已自动解包 R.data，返回的就是 Page<RouteRecordVO>
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
    // res 已经是 Page 对象：{ records: [...], total: N }
    const records = res?.records || res?.list || (Array.isArray(res) ? res : [])
    const total = res?.total ?? records.length
    return { list: records as RouteRecord[], total }
  } catch {
    return { list: mockRouteRecords as RouteRecord[], total: mockRouteRecords.length }
  }
}

export async function getRouteDetail(id: number): Promise<RouteDetail> {
  try {
    // request() 已自动解包 R.data，返回的就是 RouteDetailVO 本身
    const res = await request<any>({
      url: `/admin/route/records/${id}`,
      method: 'GET',
      useMock: false,
      mockData: mockRouteDetail
    })
    return res || mockRouteDetail
  } catch {
    return mockRouteDetail as RouteDetail
  }
}

export async function saveRouteRecord(data: {
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
}): Promise<RouteRecord> {
  // 后端时间格式要求：yyyy-MM-dd HH:mm:ss
  const formatTime = (isoStr: string) => {
    if (!isoStr) return isoStr
    // 已经是目标格式
    if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(isoStr)) return isoStr
    const d = new Date(isoStr)
    if (isNaN(d.getTime())) return isoStr
    const pad = (n: number) => n.toString().padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  }

  const requestData = {
    ...data,
    startTime: formatTime(data.startTime),
    endTime: formatTime(data.endTime),
    points: data.points.map(p => ({
      ...p,
      recordTime: p.recordTime || (p.timestamp ? formatTime(new Date(p.timestamp).toISOString()) : undefined)
    }))
  }

  try {
    // request() 已自动解包 R.data
    const res = await request<any>({
      url: '/admin/route/records',
      method: 'POST',
      data: requestData,
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
    return res || { id: Date.now(), ...requestData }
  } catch {
    throw new Error('保存路线失败')
  }
}

export async function deleteRouteRecord(id: number): Promise<boolean> {
  try {
    // request() 已自动解包 R.data，返回 true/false
    const res = await request<any>({
      url: `/admin/route/records/${id}`,
      method: 'DELETE',
      useMock: false,
      mockData: true
    })
    return res !== false
  } catch {
    return true
  }
}

export async function deleteRouteRecordPost(id: number): Promise<boolean> {
  try {
    const res = await request<any>({
      url: `/admin/route/records/${id}/delete`,
      method: 'POST',
      useMock: false,
      mockData: true
    })
    return res !== false
  } catch {
    return true
  }
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
