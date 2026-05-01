// 采集模块 API
import { get, post, put, del } from '../request'
import {
  mockCollections,
  mockCollectionGroups,
  mockCollectionDetail,
  mockCollectionStats
} from '../mock/collect'

// 类型定义
export interface Collection {
  id: number
  title: string
  image: string
  date: string
  year: string
  location?: string
  city?: string
  country?: string
  description?: string
  aiGenerated?: boolean
  hasVideo?: boolean
  createdAt?: string
}

export interface CollectionGroup {
  id: number
  name: string
  cover?: string
  count: number
}

export interface CollectionDetail extends Collection {
  originalImage?: string
  latitude?: number
  longitude?: number
  videoUrl?: string
  tags?: string[]
  groupId?: number
  groupName?: string
}

export interface CollectionStats {
  totalCount: number
  cityCount: number
  countryCount: number
  locations: Array<{ type: string; name: string; count: number }>
}

// 获取采集列表
export const getCollections = async (params?: {
  groupId?: number
  location?: string
  page?: number
  pageSize?: number
}): Promise<Collection[]> => {
  try {
    return await get<Collection[]>('/collect/list', params)
  } catch (e) {
    console.log('使用 mock 采集列表数据')
    return mockCollections
  }
}

// 获取采集分组列表
export const getCollectionGroups = async (): Promise<CollectionGroup[]> => {
  try {
    return await get<CollectionGroup[]>('/collect/groups')
  } catch (e) {
    console.log('使用 mock 分组数据')
    return mockCollectionGroups
  }
}

// 获取采集详情
export const getCollectionDetail = async (id: number): Promise<CollectionDetail | null> => {
  try {
    return await get<CollectionDetail>(`/collect/detail/${id}`)
  } catch (e) {
    console.log('使用 mock 采集详情数据')
    return mockCollectionDetail
  }
}

// 获取采集统计
export const getCollectionStats = async (): Promise<CollectionStats> => {
  try {
    return await get<CollectionStats>('/collect/stats')
  } catch (e) {
    console.log('使用 mock 统计数据')
    return mockCollectionStats
  }
}

// 创建采集
export const createCollection = async (data: {
  image: string
  title?: string
  description?: string
  location?: string
  latitude?: number
  longitude?: number
  groupId?: number
}): Promise<Collection> => {
  try {
    return await post<Collection>('/collect/create', data)
  } catch (e) {
    console.log('使用 mock 创建采集')
    const newCollection: Collection = {
      id: Date.now(),
      title: data.title || '新采集',
      image: data.image,
      date: new Date().toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }).replace('/', '/'),
      year: new Date().getFullYear().toString(),
      location: data.location,
      createdAt: new Date().toISOString()
    }
    return newCollection
  }
}

// 更新采集
export const updateCollection = async (id: number, data: Partial<CollectionDetail>): Promise<boolean> => {
  try {
    await put(`/collect/update/${id}`, data)
    return true
  } catch (e) {
    console.log('使用 mock 更新采集')
    return true
  }
}

// 删除采集
export const deleteCollection = async (id: number): Promise<boolean> => {
  try {
    await del(`/collect/delete/${id}`)
    return true
  } catch (e) {
    console.log('使用 mock 删除采集')
    return true
  }
}

// 创建分组
export const createCollectionGroup = async (name: string): Promise<CollectionGroup> => {
  try {
    return await post<CollectionGroup>('/collect/group/create', { name })
  } catch (e) {
    console.log('使用 mock 创建分组')
    return {
      id: Date.now(),
      name,
      count: 0
    }
  }
}

// 删除分组
export const deleteCollectionGroup = async (id: number): Promise<boolean> => {
  try {
    await del(`/collect/group/delete/${id}`)
    return true
  } catch (e) {
    console.log('使用 mock 删除分组')
    return true
  }
}

// AI 识别图片
export const recognizeImage = async (imagePath: string): Promise<{
  title: string
  description: string
  tags: string[]
}> => {
  try {
    return await post('/collect/recognize', { image: imagePath })
  } catch (e) {
    console.log('使用 mock AI识别')
    return {
      title: '图片内容',
      description: '这是一张有趣的图片。',
      tags: ['图片']
    }
  }
}
