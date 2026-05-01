/**
 * Mock数据 - 目的地模块
 */

// 目的地列表
export const mockDestinations = [
  { 
    id: 1, 
    name: '北京', 
    subtitle: '古都文化之旅', 
    image: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #E53935 0%, #FF7043 100%)', 
    spots: 128, 
    rating: 4.9, 
    tag: '热门' 
  },
  { 
    id: 2, 
    name: '南京', 
    subtitle: '六朝古都', 
    image: 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #5C6BC0 0%, #7986CB 100%)', 
    spots: 86, 
    rating: 4.8, 
    tag: '' 
  },
  { 
    id: 3, 
    name: '上海', 
    subtitle: '魔都风情', 
    image: 'https://images.unsplash.com/photo-1538428494232-9c0d8a3ab403?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #1E88E5 0%, #42A5F5 100%)', 
    spots: 156, 
    rating: 4.8, 
    tag: '热门' 
  },
  { 
    id: 4, 
    name: '成都', 
    subtitle: '天府之国', 
    image: 'https://images.unsplash.com/photo-1590736969955-71cc94901144?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #43A047 0%, #66BB6A 100%)', 
    spots: 92, 
    rating: 4.9, 
    tag: '美食' 
  },
  { 
    id: 5, 
    name: '杭州', 
    subtitle: '人间天堂', 
    image: 'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #00ACC1 0%, #26C6DA 100%)', 
    spots: 78, 
    rating: 4.7, 
    tag: '风景' 
  },
  { 
    id: 6, 
    name: '西安', 
    subtitle: '千年古都', 
    image: 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #8D6E63 0%, #A1887F 100%)', 
    spots: 95, 
    rating: 4.8, 
    tag: '历史' 
  },
  { 
    id: 7, 
    name: '重庆', 
    subtitle: '山城夜景', 
    image: 'https://images.unsplash.com/photo-1623448926744-0e5c0e4c6e8d?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #F4511E 0%, #FF7043 100%)', 
    spots: 67, 
    rating: 4.7, 
    tag: '美食' 
  },
  { 
    id: 8, 
    name: '丽江', 
    subtitle: '纳西古城', 
    image: 'https://images.unsplash.com/photo-1553954197-bba0d8db0f76?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #7E57C2 0%, #9575CD 100%)', 
    spots: 45, 
    rating: 4.9, 
    tag: '古镇' 
  },
  { 
    id: 9, 
    name: '三亚', 
    subtitle: '海天一色', 
    image: 'https://images.unsplash.com/photo-1559628233-100c798642d4?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #039BE5 0%, #4FC3F7 100%)', 
    spots: 58, 
    rating: 4.6, 
    tag: '海滨' 
  },
  { 
    id: 10, 
    name: '拉萨', 
    subtitle: '圣城之光', 
    image: 'https://images.unsplash.com/photo-1461823385004-d7660947a7c0?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #FFB300 0%, #FFD54F 100%)', 
    spots: 42, 
    rating: 4.9, 
    tag: '文化' 
  },
  { 
    id: 11, 
    name: '厦门', 
    subtitle: '鼓浪屿之恋', 
    image: 'https://images.unsplash.com/photo-1565967511849-76a60a516170?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #00897B 0%, #4DB6AC 100%)', 
    spots: 52, 
    rating: 4.7, 
    tag: '海滨' 
  },
  { 
    id: 12, 
    name: '苏州', 
    subtitle: '园林典范', 
    image: 'https://images.unsplash.com/photo-1578469550956-0e16b69c6a3d?w=800&q=80', 
    gradient: 'linear-gradient(135deg, #546E7A 0%, #78909C 100%)', 
    spots: 68, 
    rating: 4.8, 
    tag: '古镇' 
  }
]

// 景点详情
export const mockSpotDetail = {
  id: 1,
  name: '颐和园',
  subtitle: '皇家园林',
  cover: 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=800&q=80',
  rating: 4.8,
  reviewSource: '来自携程',
  distance: '12.5km',
  description: '颐和园，中国清朝时期皇家园林，前身为清漪园，坐落在北京西郊，距城区十五公里，占地约二百九十公顷，与圆明园毗邻。它是以昆明湖、万寿山为基址，以杭州西湖为蓝本，汲取江南园林的设计手法而建成的一座大型山水园林，也是保存最完整的一座皇家行宫御苑，被誉为"皇家园林博物馆"。',
  locationTitle: '北京市海淀区',
  locationDetail: '新建宫门路19号',
  price: '旺季¥30 / 淡季¥20',
  priceNote: '联票¥60',
  gallery: [
    'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=800&q=80',
    'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&q=80',
    'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=800&q=80',
    'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=800&q=80'
  ],
  tips: [
    { icon: '🕐', title: '开放时间', desc: '旺季6:30-18:00，淡季7:00-17:00' },
    { icon: '🚇', title: '交通方式', desc: '地铁4号线北宫门站D口出' },
    { icon: '⏱️', title: '建议游玩', desc: '3-4小时' },
    { icon: '📅', title: '最佳季节', desc: '春秋两季，天气宜人' }
  ]
}

// 地区分组数据
export const mockRegionGroups = [
  {
    name: '北方地区',
    provinces: ['山西', '陕西', '内蒙古', '北京', '吉林', '天津', '黑龙江', '河北', '辽宁', '山东']
  },
  {
    name: '南方地区',
    provinces: ['湖南', '广西', '海南', '福建', '广东']
  },
  {
    name: '西部地区',
    provinces: ['四川', '云南', '贵州', '西藏', '新疆', '青海', '甘肃', '宁夏']
  },
  {
    name: '东部地区',
    provinces: ['上海', '江苏', '浙江', '安徽', '江西', '湖北', '河南']
  }
]

// 热门城市
export const mockHotCities = [
  '北京', '上海', '广州', '深圳', '杭州', '南京',
  '成都', '重庆', '西安', '武汉', '长沙', '苏州',
  '厦门', '青岛', '大连', '三亚', '丽江', '拉萨'
]

// 评论数据
export const mockComments = [
  {
    id: 1,
    userId: 101,
    userName: '旅行者小王',
    userAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80',
    content: '颐和园真的太美了！建议大家早点来，人少景美。昆明湖的日落特别壮观，一定要等到傍晚再走。',
    images: [
      'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=400&q=80'
    ],
    likes: 128,
    isLiked: false,
    createTime: '2026-01-28 14:30',
    replies: [
      {
        id: 101,
        userId: 102,
        userName: '摄影爱好者',
        userAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80',
        content: '同意！我上次去拍了很多照片，真的很出片',
        likes: 23,
        isLiked: false,
        createTime: '2026-01-28 15:20',
        replyTo: '旅行者小王'
      },
      {
        id: 102,
        userId: 103,
        userName: '北京土著',
        userAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80',
        content: '推荐从北宫门进去，可以先逛苏州街',
        likes: 45,
        isLiked: false,
        createTime: '2026-01-28 16:45',
        replyTo: '旅行者小王'
      }
    ],
    showAllReplies: false
  },
  {
    id: 2,
    userId: 104,
    userName: '历史迷小李',
    userAvatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=100&q=80',
    content: '作为一个历史爱好者，颐和园的每一处建筑都让我着迷。强烈建议租一个讲解器，能了解到很多有趣的历史故事。',
    images: [],
    likes: 86,
    isLiked: false,
    createTime: '2026-01-27 10:15',
    replies: [
      {
        id: 201,
        userId: 105,
        userName: '学生党',
        userAvatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80',
        content: '请问讲解器多少钱呀？',
        likes: 5,
        isLiked: false,
        createTime: '2026-01-27 11:30',
        replyTo: '历史迷小李'
      }
    ],
    showAllReplies: false
  },
  {
    id: 3,
    userId: 106,
    userName: '带娃达人',
    userAvatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&q=80',
    content: '带孩子来玩非常合适，可以坐船游湖，孩子特别开心。不过夏天记得带防晒和水，园区很大走路较多。',
    images: [
      'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=400&q=80',
      'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=400&q=80'
    ],
    likes: 234,
    isLiked: true,
    createTime: '2026-01-25 09:00',
    replies: [],
    showAllReplies: false
  }
]
