/**
 * 足迹模块 API
 */

import { request } from '../request'

export interface FootprintRecord {
  id: number
  spotId?: number
  spotName: string
  cityName?: string
  provinceName?: string
  provinceCode?: string
  countryName?: string
  countryCode?: string
  lat?: number
  lng?: number
  type?: number
  remark?: string
  imageUrl?: string
  createdAt?: string
}

export interface FootprintRecordPage {
  records: FootprintRecord[]
  total: number
  size: number
  current: number
  pages: number
}

export interface FootprintStats {
  totalFootprints: number
  domesticFootprints: number
  overseasFootprints: number
  provinceCount: number
  countryCount: number
  collectCount: number
  provinces: string[]
  countries: string[]
}

export interface FootprintProvince {
  id: number
  provinceCode: string
  provinceName: string
  checkinCount: number
  firstCheckinAt?: string
  lastCheckinAt?: string
}

export interface FootprintCountry {
  id: number
  countryCode: string
  countryName: string
  checkinCount: number
  firstCheckinAt?: string
  lastCheckinAt?: string
}

export interface FootprintCreateDTO {
  spotId?: number
  spotName: string
  cityName?: string
  provinceName?: string
  provinceCode?: string
  countryName?: string
  countryCode?: string
  lat?: number
  lng?: number
  type?: number
  remark?: string
  imageUrl?: string
}

export function getFootprintStats() {
  return request<FootprintStats>({
    url: '/admin/footprint/stats',
    method: 'GET',
    useMock: false
  })
}

export function getFootprintRecords(params?: { current?: number; size?: number }) {
  return request<FootprintRecordPage>({
    url: '/admin/footprint/records',
    method: 'GET',
    data: {
      current: params?.current ?? 1,
      size: params?.size ?? 20
    },
    useMock: false
  })
}

export function createFootprintRecord(data: FootprintCreateDTO) {
  return request<number>({
    url: '/admin/footprint/records',
    method: 'POST',
    data,
    useMock: false
  })
}

export function deleteFootprintRecord(id: number) {
  return request<boolean>({
    url: `/admin/footprint/records/${id}`,
    method: 'DELETE',
    useMock: false
  })
}

export function getUserProvinces() {
  return request<FootprintProvince[]>({
    url: '/admin/footprint/provinces',
    method: 'GET',
    useMock: false
  })
}

export function lightUpProvince(provinceCode: string, provinceName?: string) {
  return request<boolean>({
    url: `/admin/footprint/provinces/${provinceCode}/light${provinceName ? '?provinceName=' + encodeURIComponent(provinceName) : ''}`,
    method: 'POST',
    useMock: false
  })
}

export function getUserCountries() {
  return request<FootprintCountry[]>({
    url: '/admin/footprint/countries',
    method: 'GET',
    useMock: false
  })
}

export function lightUpCountry(countryCode: string, countryName?: string) {
  return request<boolean>({
    url: `/admin/footprint/countries/${countryCode}/light${countryName ? '?countryName=' + encodeURIComponent(countryName) : ''}`,
    method: 'POST',
    useMock: false
  })
}
