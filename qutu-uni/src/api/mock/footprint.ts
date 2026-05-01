/**
 * Mock数据 - 足迹模块
 */

// 足迹记录
export const mockFootprintRecords = [
  {
    id: 1,
    name: '北京',
    type: 'city',
    date: '2025-10-15',
    image: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=400&q=80',
    spots: ['故宫', '天坛', '颐和园']
  },
  {
    id: 2,
    name: '上海',
    type: 'city',
    date: '2025-08-20',
    image: 'https://images.unsplash.com/photo-1538428494232-9c0d8a3ab403?w=400&q=80',
    spots: ['外滩', '迪士尼', '南京路']
  },
  {
    id: 3,
    name: '杭州',
    type: 'city',
    date: '2025-06-10',
    image: 'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=400&q=80',
    spots: ['西湖', '灵隐寺', '宋城']
  }
]

// 点亮统计
export const mockFootprintStats = {
  totalProvinces: 12,
  totalCities: 28,
  totalSpots: 156,
  totalCountries: 3,
  ranking: 2580
}

// 活动地图数据
export const mockActivityMaps = [
  {
    id: 'spring',
    type: 'spring',
    name: '春节玩乐地图',
    title: '春节玩乐地图',
    subtitle: '年味在路上，点亮你的新春足迹！',
    count: '3.05万人已点亮',
    bgType: 'red',
    items: [
      { emoji: '🐲', text: '新春看神仙地图' },
      { emoji: '😋', text: '新春贪吃地图' },
      { emoji: '🧧', text: '新春玩乐地图' }
    ]
  },
  {
    id: 'china',
    type: 'china',
    name: '中国漫游者指南',
    title: '中国漫游者指南',
    subtitle: '点亮你去过的省/市/自治区/特别行政区',
    count: '28万人已点亮',
    bgType: 'purple'
  },
  {
    id: 'world',
    type: 'world',
    name: '世界探险家手册',
    title: '世界探险家手册',
    subtitle: '点亮你在世界上去过的国家/地区',
    count: '13.02万人已点亮',
    bgType: 'blue'
  }
]

// 省份数据
export const mockProvinces = [
  { id: 1, name: '北京', visited: true, citiesTotal: 1, citiesVisited: 1 },
  { id: 2, name: '天津', visited: false, citiesTotal: 1, citiesVisited: 0 },
  { id: 3, name: '河北', visited: true, citiesTotal: 11, citiesVisited: 3 },
  { id: 4, name: '山西', visited: false, citiesTotal: 11, citiesVisited: 0 },
  { id: 5, name: '内蒙古', visited: false, citiesTotal: 12, citiesVisited: 0 },
  { id: 6, name: '辽宁', visited: true, citiesTotal: 14, citiesVisited: 2 },
  { id: 7, name: '吉林', visited: false, citiesTotal: 9, citiesVisited: 0 },
  { id: 8, name: '黑龙江', visited: false, citiesTotal: 13, citiesVisited: 0 },
  { id: 9, name: '上海', visited: true, citiesTotal: 1, citiesVisited: 1 },
  { id: 10, name: '江苏', visited: true, citiesTotal: 13, citiesVisited: 5 },
  { id: 11, name: '浙江', visited: true, citiesTotal: 11, citiesVisited: 4 },
  { id: 12, name: '安徽', visited: true, citiesTotal: 16, citiesVisited: 2 },
  { id: 13, name: '福建', visited: true, citiesTotal: 9, citiesVisited: 3 },
  { id: 14, name: '江西', visited: false, citiesTotal: 11, citiesVisited: 0 },
  { id: 15, name: '山东', visited: true, citiesTotal: 16, citiesVisited: 4 },
  { id: 16, name: '河南', visited: false, citiesTotal: 17, citiesVisited: 0 },
  { id: 17, name: '湖北', visited: true, citiesTotal: 13, citiesVisited: 2 },
  { id: 18, name: '湖南', visited: false, citiesTotal: 14, citiesVisited: 0 },
  { id: 19, name: '广东', visited: true, citiesTotal: 21, citiesVisited: 6 },
  { id: 20, name: '广西', visited: false, citiesTotal: 14, citiesVisited: 0 },
  { id: 21, name: '海南', visited: true, citiesTotal: 4, citiesVisited: 2 },
  { id: 22, name: '重庆', visited: false, citiesTotal: 1, citiesVisited: 0 },
  { id: 23, name: '四川', visited: true, citiesTotal: 21, citiesVisited: 3 },
  { id: 24, name: '贵州', visited: false, citiesTotal: 9, citiesVisited: 0 },
  { id: 25, name: '云南', visited: true, citiesTotal: 16, citiesVisited: 4 },
  { id: 26, name: '西藏', visited: false, citiesTotal: 7, citiesVisited: 0 },
  { id: 27, name: '陕西', visited: false, citiesTotal: 10, citiesVisited: 0 },
  { id: 28, name: '甘肃', visited: false, citiesTotal: 14, citiesVisited: 0 },
  { id: 29, name: '青海', visited: false, citiesTotal: 8, citiesVisited: 0 },
  { id: 30, name: '宁夏', visited: false, citiesTotal: 5, citiesVisited: 0 },
  { id: 31, name: '新疆', visited: false, citiesTotal: 14, citiesVisited: 0 },
  { id: 32, name: '香港', visited: false, citiesTotal: 1, citiesVisited: 0 },
  { id: 33, name: '澳门', visited: false, citiesTotal: 1, citiesVisited: 0 },
  { id: 34, name: '台湾', visited: false, citiesTotal: 1, citiesVisited: 0 }
]

// 国家数据
export const mockCountries = [
  { id: 1, name: '中国', code: 'CN', visited: true, continent: 'Asia' },
  { id: 2, name: '日本', code: 'JP', visited: true, continent: 'Asia' },
  { id: 3, name: '韩国', code: 'KR', visited: true, continent: 'Asia' },
  { id: 4, name: '泰国', code: 'TH', visited: false, continent: 'Asia' },
  { id: 5, name: '新加坡', code: 'SG', visited: false, continent: 'Asia' },
  { id: 6, name: '美国', code: 'US', visited: false, continent: 'America' },
  { id: 7, name: '英国', code: 'GB', visited: false, continent: 'Europe' },
  { id: 8, name: '法国', code: 'FR', visited: false, continent: 'Europe' },
  { id: 9, name: '德国', code: 'DE', visited: false, continent: 'Europe' },
  { id: 10, name: '澳大利亚', code: 'AU', visited: false, continent: 'Oceania' }
]
