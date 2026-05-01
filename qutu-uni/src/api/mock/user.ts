/**
 * Mock数据 - 用户模块
 */

// 用户信息
export const mockUserInfo = {
  id: 1,
  username: 'traveler',
  nickname: '旅行者小明',
  avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&q=80',
  phone: '138****8888',
  email: 'traveler@example.com',
  wechatBound: false,
  vipLevel: 0, // 0: 普通用户, 1: 月度会员, 2: 年度会员
  vipExpireDate: '',
  createTime: '2025-06-15',
  permissions: [] as string[],
  roles: [] as number[],
  stats: {
    trips: 12,
    spots: 86,
    guides: 5,
    footprints: 156
  }
}

// VIP权益
export const mockVipBenefits = [
  { id: 1, icon: '🎨', name: '专属主题', desc: '多款精美主题随心换' },
  { id: 2, icon: '📥', name: '无限导入', desc: '无限次智能导入攻略' },
  { id: 3, icon: '☁️', name: '云端备份', desc: '数据自动云端同步备份' },
  { id: 4, icon: '🎁', name: '专属福利', desc: '不定期发放会员专属福利' },
  { id: 5, icon: '🚀', name: 'AI规划', desc: '无限次AI智能行程规划' },
  { id: 6, icon: '📊', name: '数据报告', desc: '专属年度旅行数据报告' }
]

// VIP套餐
export const mockVipPackages = [
  { id: 1, name: '月度会员', price: 18, originalPrice: 28, duration: 30 },
  { id: 2, name: '季度会员', price: 48, originalPrice: 84, duration: 90 },
  { id: 3, name: '年度会员', price: 128, originalPrice: 336, duration: 365, tag: '最划算' }
]

// 用户设置
export const mockUserSettings = {
  language: 'zh-CN',
  notifications: {
    tripReminder: true,
    newGuides: true,
    systemMessages: true
  },
  privacy: {
    showFootprints: true,
    showTrips: false
  },
  mapSettings: {
    defaultZoom: 14,
    showTraffic: false,
    mapStyle: 'normal'
  }
}

// 常用地点
export const mockFrequentLocations = [
  { id: 1, name: '家', address: '北京市朝阳区xxx小区', latitude: 39.9042, longitude: 116.4074, type: 'home' },
  { id: 2, name: '公司', address: '北京市海淀区xxx大厦', latitude: 39.9842, longitude: 116.3074, type: 'work' }
]

// 收藏地点
export const mockFavoriteLocations = [
  { 
    id: 1, 
    name: '颐和园', 
    address: '北京市海淀区新建宫门路19号',
    image: 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=400&q=80',
    latitude: 39.9987,
    longitude: 116.2758,
    addTime: '2026-01-20'
  },
  { 
    id: 2, 
    name: '西湖', 
    address: '杭州市西湖区龙井路1号',
    image: 'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=400&q=80',
    latitude: 30.2590,
    longitude: 120.1489,
    addTime: '2026-01-15'
  },
  { 
    id: 3, 
    name: '外滩', 
    address: '上海市黄浦区中山东一路',
    image: 'https://images.unsplash.com/photo-1538428494232-9c0d8a3ab403?w=400&q=80',
    latitude: 31.2397,
    longitude: 121.4919,
    addTime: '2026-01-10'
  }
]
