/**
 * Mock数据 - 故事/日记模块
 */

// 日记列表
export const mockDiaryList = [
  {
    id: 1,
    day: '01/28',
    weekday: '周三',
    date: '2026-01-28',
    image: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&q=80',
    content: '今天去了故宫，红墙金瓦，气势恢宏。虽然天气有点冷，但是阳光很好，拍了很多照片。',
    location: '北京故宫'
  },
  {
    id: 2,
    day: '01/27',
    weekday: '周二',
    date: '2026-01-27',
    image: 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=800&q=80',
    content: '登上了景山公园的最高点，俯瞰故宫全景，真的太震撼了。夕阳西下的时候，整个紫禁城都被金色的光芒笼罩。',
    location: '景山公园'
  },
  {
    id: 3,
    day: '01/26',
    weekday: '周一',
    date: '2026-01-26',
    image: '',
    content: '今天在南锣鼓巷逛了一下午，吃了很多小吃，买了一些文创纪念品。胡同里的生活气息很浓。',
    location: '南锣鼓巷'
  },
  {
    id: 4,
    day: '01/25',
    weekday: '周日',
    date: '2026-01-25',
    image: 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=800&q=80',
    content: '凌晨四点起床看升旗仪式，虽然很冷很困，但是看到五星红旗冉冉升起的那一刻，觉得一切都值得了。',
    location: '天安门广场'
  }
]

// 故事模板
export const mockStoryTemplates = [
  { id: 1, name: '旅途日记', icon: '📝', desc: '记录每一天的旅行点滴' },
  { id: 2, name: '美食记录', icon: '🍜', desc: '发现旅途中的美味' },
  { id: 3, name: '风景随拍', icon: '📷', desc: '用镜头捕捉美丽瞬间' },
  { id: 4, name: '心情感悟', icon: '💭', desc: '旅行中的所思所想' }
]

// 热门标签
export const mockStoryTags = [
  '#旅行日记', '#在路上', '#说走就走', '#旅行的意义',
  '#美景推荐', '#旅行攻略', '#打卡圣地', '#独自旅行',
  '#情侣旅行', '#亲子游', '#美食探店', '#古镇漫游'
]
