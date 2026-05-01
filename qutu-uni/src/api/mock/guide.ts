/**
 * Mock数据 - 攻略模块
 */

// 攻略分类
export const mockGuideCategories = [
  { id: 1, name: 'Citywalk', icon: '🚶' },
  { id: 2, name: '海边', icon: '🏖️' },
  { id: 3, name: '美食指南', icon: '🍜' },
  { id: 4, name: '去除班味', icon: '🖥️' },
  { id: 5, name: '古镇', icon: '🏯' },
  { id: 6, name: '自然风光', icon: '🏔️' },
  { id: 7, name: '亲子游', icon: '👨‍👩‍👧' },
  { id: 8, name: '情侣约会', icon: '💑' }
]

// 攻略列表
export const mockGuideList = [
  {
    id: 1,
    title: '北京四天三夜自由行',
    description: '超级丝滑的北京旅游攻略！四天三夜带你探索千年古都，领略北京的经典魅力与文化底蕴。',
    cover: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&q=80',
    author: '旅行达人小明',
    authorAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80',
    days: '4天3晚',
    likes: 2345,
    views: 18900,
    categoryId: 1
  },
  {
    id: 2,
    title: '北京两天一夜特种兵经典游',
    description: '两天玩遍北京最具历史气息的景点，适合时间紧凑的朋友。',
    cover: 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=800&q=80',
    author: '特种兵旅游家',
    authorAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80',
    days: '2天1晚',
    likes: 1890,
    views: 15600,
    categoryId: 1
  },
  {
    id: 3,
    title: '上海迪士尼完美攻略',
    description: '手把手教你如何高效游玩迪士尼，不踩坑不排队！',
    cover: 'https://images.unsplash.com/photo-1569959220744-ff553533f492?w=800&q=80',
    author: '迪士尼公主',
    authorAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80',
    days: '1天',
    likes: 5678,
    views: 45000,
    categoryId: 7
  },
  {
    id: 4,
    title: '成都五日慢生活之旅',
    description: '感受成都的悠闲与美食，体验地道的川西风情。',
    cover: 'https://images.unsplash.com/photo-1590736969955-71cc94901144?w=800&q=80',
    author: '成都吃货王',
    authorAvatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=100&q=80',
    days: '5天4晚',
    likes: 3456,
    views: 28000,
    categoryId: 3
  },
  {
    id: 5,
    title: '三亚浪漫海岛蜜月游',
    description: '最美的海岛度假攻略，给你一个完美的蜜月旅行。',
    cover: 'https://images.unsplash.com/photo-1559628233-100c798642d4?w=800&q=80',
    author: '蜜月策划师',
    authorAvatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&q=80',
    days: '4天3晚',
    likes: 4567,
    views: 38000,
    categoryId: 2
  },
  {
    id: 6,
    title: '丽江大理古镇深度游',
    description: '云南古镇慢时光，感受纳西族和白族的独特文化。',
    cover: 'https://images.unsplash.com/photo-1553954197-bba0d8db0f76?w=800&q=80',
    author: '古镇探索者',
    authorAvatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80',
    days: '6天5晚',
    likes: 2890,
    views: 22000,
    categoryId: 5
  },
  {
    id: 7,
    title: '西安历史文化三日游',
    description: '探访千年古都，品味历史与美食的完美融合。',
    cover: 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=800&q=80',
    author: '历史爱好者',
    authorAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&q=80',
    days: '3天2晚',
    likes: 1567,
    views: 12000,
    categoryId: 1
  },
  {
    id: 8,
    title: '张家界仙境徒步攻略',
    description: '阿凡达取景地，感受大自然的鬼斧神工。',
    cover: 'https://images.unsplash.com/photo-1518623489648-a173ef7824f3?w=800&q=80',
    author: '户外达人',
    authorAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&q=80',
    days: '4天3晚',
    likes: 2134,
    views: 18500,
    categoryId: 6
  }
]

// 攻略详情
export const mockGuideDetail = {
  id: 1,
  title: '北京两天一夜特种兵经典游',
  description: '两天玩遍北京最具历史气息的景点',
  cover: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&q=80',
  author: {
    id: 1,
    name: '旅行达人小明',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80',
    followers: 12580
  },
  likes: 2345,
  views: 18900,
  collects: 890,
  createTime: '2026-01-15',
  overview: {
    summary: '两天一夜的北京经典攻略。去北京一定要提前预约，因为好多景点都需要提前预约！',
    transport: '选择自己方便的火车站就可以。地铁都比较方便,基本上景点又可以到达。下载"亿通行"app，公交地铁都能刷。'
  },
  schedule: [
    { day: 'Day1', route: '天坛-天安门-前门大街-国家博物馆-王府井-什刹海-南锣鼓巷' },
    { day: 'Day2', route: '天安门升旗-故宫-鼓楼-景山公园' }
  ],
  day1Spots: [
    {
      name: '天坛公园',
      location: '北京市东城区天坛东路甲1号',
      subway: '5号线天坛东门站A1出口',
      tip: '建议早上7点前到达，人少且光线好',
      warning: '周一闭馆，提前在公众号预约'
    },
    {
      name: '天安门广场',
      location: '北京市东城区长安街',
      subway: '1号线天安门东站',
      tip: '可以看降旗仪式，非常震撼',
      warning: '需要安检，不能带自拍杆'
    },
    {
      name: '前门大街',
      location: '北京市东城区前门大街',
      subway: '2号线前门站',
      tip: '有很多老字号美食，推荐全聚德烤鸭',
      warning: ''
    },
    {
      name: '国家博物馆',
      location: '北京市东城区东长安街16号',
      subway: '1号线天安门东站',
      tip: '免费参观，提前在官网预约',
      warning: '周一闭馆'
    }
  ],
  day2Spots: [
    {
      name: '天安门升旗仪式',
      location: '北京市东城区天安门广场',
      subway: '1号线天安门东站',
      tip: '升旗时间根据日出时间变化，提前查询',
      warning: '需要凌晨排队，带好保暖衣物'
    },
    {
      name: '故宫博物院',
      location: '北京市东城区景山前街4号',
      subway: '1号线天安门东站',
      tip: '建议游玩4-5小时，从午门进神武门出',
      warning: '周一闭馆，必须提前预约，限流8万人'
    },
    {
      name: '景山公园',
      location: '北京市西城区景山西街44号',
      subway: '5号线东四站',
      tip: '登顶可俯瞰故宫全景，日落时分最美',
      warning: '门票2元，性价比超高'
    }
  ],
  tips: [
    { icon: '🎫', title: '门票预约', content: '所有景点都需要提前在官方公众号预约，建议提前3-7天' },
    { icon: '🚇', title: '交通出行', content: '下载亿通行APP，刷码乘坐地铁公交非常方便' },
    { icon: '🍜', title: '美食推荐', content: '烤鸭、炸酱面、豆汁、卤煮是必尝美食' },
    { icon: '🏨', title: '住宿建议', content: '建议住在王府井或前门附近，出行方便' }
  ]
}

// 相关攻略
export const mockRelatedGuides = [
  {
    id: 2,
    title: '北京四天三夜深度游',
    description: '带你深度探索北京的每一个角落，感受古都的魅力',
    cover: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=400&q=80',
    days: '4天3晚',
    likes: 1256,
    views: 9800
  },
  {
    id: 3,
    title: '北京周末亲子游',
    description: '带娃玩转北京，科技馆、动物园、游乐园一网打尽',
    cover: 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=400&q=80',
    days: '2天1晚',
    likes: 890,
    views: 6500
  },
  {
    id: 4,
    title: '北京美食探店攻略',
    description: '从老字号到网红店，一篇攻略吃遍北京',
    cover: 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=400&q=80',
    days: '3天2晚',
    likes: 2340,
    views: 18000
  }
]
