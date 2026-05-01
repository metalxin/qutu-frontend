/**
 * 规划模块 API
 */

import { get, post } from '../request'
import {
  mockPlanningCities,
  mockPreferenceOptions,
  mockTransportModes,
  mockSpotDatabase,
  mockRestaurants,
  mockHotels
} from '../mock/planning'

export interface SpotInfo {
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

export interface RouteSpot {
  id: number
  name: string
  day: number
  order: number
  duration: string
  startTime?: string
  endTime?: string
  spotInfo?: SpotInfo
}

export interface DaySchedule {
  day: number
  date: string
  spots: RouteSpot[]
  totalDuration: string
  totalDistance: string
  meals?: {
    lunch?: { name: string; address: string }
    dinner?: { name: string; address: string }
  }
  hotel?: { name: string; price: string }
}

export interface AIRoute {
  id: number
  name: string
  startCity: string
  endCity: string
  days: number
  distance: string
  totalTime: string
  spots: RouteSpot[]
  schedule?: DaySchedule[]
  summary?: {
    totalSpots: number
    totalDistance: string
    estimatedCost: string
    bestSeason: string
  }
}

export interface PreferenceOption {
  id: string
  name: string
  desc: string
}

export interface TransportMode {
  id: string
  name: string
  icon: string
}

function generateFallbackRoute(params: {
  startCity: string
  endCity: string
  waypoints?: string[]
  days: number
  preference: string
}): AIRoute {
  const { startCity, endCity, days, preference } = params

  const allCities = [startCity, ...(params.waypoints || []), endCity].filter((c, i, arr) => arr.indexOf(c) === i)
  let allSpots: SpotInfo[] = []

  allCities.forEach(city => {
    const citySpots = mockSpotDatabase[city] || []
    allSpots = allSpots.concat(citySpots)
  })

  if (allSpots.length === 0) {
    allSpots = mockSpotDatabase['北京'] || []
  }

  let spotsPerDay = 3
  switch (preference) {
    case 'relax':
      spotsPerDay = 2
      break
    case 'intensive':
      spotsPerDay = 4
      break
    default:
      spotsPerDay = 3
  }

  allSpots.sort((a, b) => b.rating - a.rating)

  const schedule: DaySchedule[] = []
  const routeSpots: RouteSpot[] = []
  let spotIndex = 0
  const startDate = new Date()

  for (let day = 1; day <= days; day++) {
    const daySpots: RouteSpot[] = []
    const currentDate = new Date(startDate)
    currentDate.setDate(currentDate.getDate() + day - 1)
    const dateStr = `${currentDate.getMonth() + 1}月${currentDate.getDate()}日`

    let currentHour = 9
    let currentMinute = 0

    for (let order = 1; order <= spotsPerDay && spotIndex < allSpots.length; order++) {
      const spot = allSpots[spotIndex]
      const durationHours = parseFloat(spot.duration) || 2

      const startTime = `${currentHour.toString().padStart(2, '0')}:${currentMinute.toString().padStart(2, '0')}`
      currentMinute += durationHours * 60
      currentHour += Math.floor(currentMinute / 60)
      currentMinute = currentMinute % 60
      const endTime = `${currentHour.toString().padStart(2, '0')}:${currentMinute.toString().padStart(2, '0')}`

      currentMinute += 30
      currentHour += Math.floor(currentMinute / 60)
      currentMinute = currentMinute % 60

      if (currentHour >= 12 && currentHour < 13 && order < spotsPerDay) {
        currentHour = 13
        currentMinute = 30
      }

      const routeSpot: RouteSpot = {
        id: spot.id,
        name: spot.name,
        day,
        order,
        duration: spot.duration,
        startTime,
        endTime,
        spotInfo: spot
      }

      daySpots.push(routeSpot)
      routeSpots.push(routeSpot)
      spotIndex++
    }

    const cityRestaurants = mockRestaurants[startCity] || []
    const meals = preference !== 'spots' && cityRestaurants.length > 0 ? {
      lunch: cityRestaurants[0] ? { name: cityRestaurants[0].name, address: cityRestaurants[0].address } : undefined,
      dinner: cityRestaurants[1] ? { name: cityRestaurants[1].name, address: cityRestaurants[1].address } : undefined
    } : undefined

    const cityHotels = mockHotels[startCity] || []
    const hotel = preference !== 'spots' && day < days && cityHotels.length > 0
      ? { name: cityHotels[0].name, price: cityHotels[0].price }
      : undefined

    schedule.push({
      day,
      date: dateStr,
      spots: daySpots,
      totalDuration: `${daySpots.length * 2 + 2}小时`,
      totalDistance: `${Math.floor(daySpots.length * 5 + Math.random() * 10)}km`,
      meals,
      hotel
    })
  }

  const totalSpots = routeSpots.length
  const totalDistance = `${Math.floor(totalSpots * 8 + Math.random() * 20)}km`
  const estimatedCost = `¥${(totalSpots * 50 + days * 300).toLocaleString()}`

  return {
    id: Date.now(),
    name: `${startCity}${days}日${preference === 'intensive' ? '特种兵' : preference === 'relax' ? '休闲' : '精华'}游`,
    startCity,
    endCity,
    days,
    distance: totalDistance,
    totalTime: `约${days * 8}小时`,
    spots: routeSpots,
    schedule,
    summary: {
      totalSpots,
      totalDistance,
      estimatedCost,
      bestSeason: '春秋季节'
    }
  }
}

export function getPlanningSettings() {
  return get<{ defaultStartLocation: string; defaultEndLocation: string; defaultDays: number; defaultPreference: string }>(
    '/admin/planning/settings',
    undefined,
    {
      defaultStartLocation: '合肥市',
      defaultEndLocation: '合肥市',
      defaultDays: 3,
      defaultPreference: 'auto'
    }
  )
}

export function getCityList() {
  return get<{ hotCities: string[] }>('/admin/planning/cities', undefined, { hotCities: mockPlanningCities })
}

export async function generatePlanningRoute(params: {
  startLocation: string
  endLocation: string
  waypoints?: string[]
  days: number
  preference: string
}): Promise<{ success: boolean; routeId: number; route: AIRoute }> {
  try {
    const result = await post<AIRoute>('/admin/planning/generate', {
      startLocation: params.startLocation,
      endLocation: params.endLocation,
      waypoints: params.waypoints,
      days: params.days,
      preference: params.preference
    }, undefined, 120000)

    if (result) {
      return {
        success: true,
        routeId: result.id || Date.now(),
        route: result
      }
    }
  } catch (error) {
    console.warn('[Planning] 后端API调用失败，使用本地降级方案:', error)
  }

  return new Promise((resolve) => {
    setTimeout(() => {
      const route = generateFallbackRoute({
        startCity: params.startLocation.replace('市', ''),
        endCity: params.endLocation.replace('市', ''),
        waypoints: params.waypoints?.map(w => w.replace('市', '')),
        days: params.days,
        preference: params.preference
      })

      resolve({
        success: true,
        routeId: route.id,
        route
      })
    }, 1500 + Math.random() * 1000)
  })
}

export function getPlanningCities() {
  return get<string[]>('/admin/planning/cities', undefined, mockPlanningCities)
}

export function getPreferenceOptions() {
  return get<PreferenceOption[]>('/admin/planning/preferences', undefined, mockPreferenceOptions)
}

export function getTransportModes() {
  return get<TransportMode[]>('/admin/planning/transports', undefined, mockTransportModes)
}

export async function generateAIRoute(params: {
  startCity: string
  endCity: string
  waypoints?: string[]
  days: number
  preference: string
  transport?: string
}): Promise<AIRoute> {
  try {
    const result = await post<AIRoute>('/admin/planning/generate', {
      startLocation: params.startCity,
      endLocation: params.endCity,
      waypoints: params.waypoints,
      days: params.days,
      preference: params.preference,
      transport: params.transport
    }, undefined, 120000)

    if (result) {
      return result
    }
  } catch (error) {
    console.warn('[Planning] 后端API调用失败，使用本地降级方案:', error)
  }

  return new Promise((resolve) => {
    setTimeout(() => {
      const route = generateFallbackRoute(params)
      resolve(route)
    }, 1500 + Math.random() * 1000)
  })
}

export function getSavedRoutes() {
  return get<AIRoute[]>('/admin/planning/routes', undefined, [])
}

export function saveRoute(route: AIRoute) {
  return post<{ success: boolean; id: number }>('/admin/planning/routes', route, { success: true, id: route.id })
}

export function getSpotInfo(city: string, spotId: number): SpotInfo | undefined {
  const citySpots = mockSpotDatabase[city] || []
  return citySpots.find(s => s.id === spotId)
}

export function getCitySpots(city: string): SpotInfo[] {
  return mockSpotDatabase[city] || []
}

export function deleteRoute(routeId: number) {
  return post<{ success: boolean }>(`/admin/planning/routes/${routeId}/delete`, undefined, { success: true })
}

export function reorderRouteSpots(routeId: number, spotIds: number[]) {
  return post<{ success: boolean }>(`/admin/planning/routes/${routeId}/reorder`, { spotIds }, { success: true })
}

export function addWaypoint(routeId: number, waypoint: string, position: number) {
  const newSpot: RouteSpot = {
    id: Date.now(),
    name: waypoint,
    day: 1,
    order: position,
    duration: '2小时'
  }
  return post<RouteSpot>(`/admin/planning/routes/${routeId}/waypoints`, { waypoint, position }, newSpot)
}

export function removeWaypoint(routeId: number, spotId: number) {
  return post<{ success: boolean }>(`/admin/planning/routes/${routeId}/waypoints/${spotId}/delete`, undefined, { success: true })
}

export function getRouteNavigation(routeId: number) {
  const mockNavigation = {
    routeId,
    totalDistance: '48km',
    totalTime: '2小时15分钟',
    steps: [
      { instruction: '从起点出发', distance: '0km', duration: '0分钟' },
      { instruction: '沿东三环向北行驶', distance: '5km', duration: '15分钟' },
      { instruction: '到达第一个景点', distance: '5km', duration: '15分钟' }
    ]
  }
  return get(`/admin/planning/routes/${routeId}/navigation`, undefined, mockNavigation)
}
