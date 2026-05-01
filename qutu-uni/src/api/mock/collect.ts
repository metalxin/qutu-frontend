// 采集 Mock 数据

export const mockCollections = [
  {
    id: 1,
    title: '代码编辑界面',
    image: '/static/temp/code.jpg',
    date: '11/30',
    year: '2025',
    location: '神秘的采集地',
    city: '',
    country: '',
    description: '屏幕上显示着结构化的代码文本，可能用于软件开发或网页设计。',
    aiGenerated: true,
    hasVideo: false,
    createdAt: '2025-11-30 14:30:00'
  }
]

export const mockCollectionGroups = [
  { id: 1, name: '默认分组', cover: '', count: 1 },
  { id: 2, name: '旅行', cover: '', count: 0 },
  { id: 3, name: '美食', cover: '', count: 0 },
  { id: 4, name: '风景', cover: '', count: 0 }
]

export const mockCollectionDetail = {
  id: 1,
  title: '代码编辑界面',
  image: '/static/temp/code.jpg',
  originalImage: '/static/temp/code_original.jpg',
  date: '11/30',
  year: '2025',
  location: '神秘的采集地',
  latitude: 0,
  longitude: 0,
  city: '',
  country: '',
  description: '屏幕上显示着结构化的代码文本，可能用于软件开发或网页设计。',
  aiGenerated: true,
  hasVideo: false,
  videoUrl: '',
  tags: ['代码', '开发'],
  groupId: 1,
  groupName: '默认分组',
  createdAt: '2025-11-30 14:30:00'
}

export const mockCollectionStats = {
  totalCount: 1,
  cityCount: 0,
  countryCount: 0,
  locations: [
    { type: 'mystery', name: '神秘的采集地', count: 1 }
  ]
}
