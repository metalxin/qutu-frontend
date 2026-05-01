/**
 * Mock数据 - 附近模块
 */

// 分类数据
export const mockNearbyCategories = [
  { id: 'all', name: '综合', emoji: '🌟' },
  { id: 'food', name: '美食', emoji: '🍜' },
  { id: 'scenic', name: '景点', emoji: '🏞️' },
  { id: 'hotel', name: '住宿', emoji: '🏨' },
  { id: 'shopping', name: '购物', emoji: '🛍️' },
  { id: 'entertainment', name: '娱乐', emoji: '🎮' }
]

// POI数据
export const mockNearbyPois = [
  {
    id: 1,
    name: '西湖景区',
    title: '西湖',
    subtitle: 'AAAAA级景区',
    category: '景点',
    categoryId: 'scenic',
    categoryType: 'place',
    distance: '2.3km',
    rating: 4.9,
    address: '杭州市西湖区龙井路1号',
    phone: '0571-87179539',
    openTime: '全天开放',
    price: '免费',
    image: 'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=400&q=80',
    images: [
      'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=800&q=80',
      'https://images.unsplash.com/photo-1559628233-100c798642d4?w=800&q=80',
      'https://images.unsplash.com/photo-1553954197-bba0d8db0f76?w=800&q=80'
    ],
    latitude: 30.2590,
    longitude: 120.1489
  },
  {
    id: 2,
    name: '外婆家(西湖店)',
    title: '外婆家',
    subtitle: '杭帮菜代表',
    category: '美食',
    categoryId: 'food',
    categoryType: 'food',
    distance: '1.8km',
    rating: 4.6,
    address: '杭州市西湖区湖滨路88号',
    phone: '0571-87012345',
    openTime: '10:30-21:00',
    price: '人均¥68',
    image: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400&q=80',
    images: [
      'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80',
      'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800&q=80'
    ],
    latitude: 30.2456,
    longitude: 120.1567
  },
  {
    id: 3,
    name: '灵隐寺',
    title: '灵隐寺',
    subtitle: '千年古刹',
    category: '景点',
    categoryId: 'scenic',
    categoryType: 'place',
    distance: '5.6km',
    rating: 4.8,
    address: '杭州市西湖区灵隐路法云弄1号',
    phone: '0571-87968665',
    openTime: '07:00-18:00',
    price: '¥75',
    image: 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=400&q=80',
    images: [
      'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=800&q=80',
      'https://images.unsplash.com/photo-1518623489648-a173ef7824f3?w=800&q=80'
    ],
    latitude: 30.2392,
    longitude: 120.0958
  },
  {
    id: 4,
    name: '杭州大厦',
    title: '杭州大厦',
    subtitle: '高端购物中心',
    category: '购物',
    categoryId: 'shopping',
    categoryType: 'place',
    distance: '3.2km',
    rating: 4.5,
    address: '杭州市下城区武林广场21号',
    phone: '0571-85151234',
    openTime: '10:00-22:00',
    price: '',
    image: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400&q=80',
    images: [
      'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800&q=80'
    ],
    latitude: 30.2789,
    longitude: 120.1678
  },
  {
    id: 5,
    name: '西溪湿地',
    title: '西溪湿地',
    subtitle: '城市绿肺',
    category: '景点',
    categoryId: 'scenic',
    categoryType: 'place',
    distance: '8.9km',
    rating: 4.7,
    address: '杭州市西湖区天目山路518号',
    phone: '0571-88106688',
    openTime: '08:00-17:30',
    price: '¥80',
    image: 'https://images.unsplash.com/photo-1565967511849-76a60a516170?w=400&q=80',
    images: [
      'https://images.unsplash.com/photo-1565967511849-76a60a516170?w=800&q=80',
      'https://images.unsplash.com/photo-1559628233-100c798642d4?w=800&q=80'
    ],
    latitude: 30.2678,
    longitude: 120.0567
  },
  {
    id: 6,
    name: '君悦酒店',
    title: '君悦酒店',
    subtitle: '五星级酒店',
    category: '住宿',
    categoryId: 'hotel',
    categoryType: 'place',
    distance: '2.1km',
    rating: 4.8,
    address: '杭州市上城区湖滨路28号',
    phone: '0571-87791234',
    openTime: '24小时',
    price: '¥1288起',
    image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400&q=80',
    images: [
      'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80',
      'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80'
    ],
    latitude: 30.2534,
    longitude: 120.1623
  }
]

// 城市列表
export const mockCityList = {
  hot: ['北京', '上海', '广州', '深圳', '杭州', '南京', '成都', '重庆', '西安', '武汉'],
  hotCities: ['北京', '上海', '广州', '深圳', '杭州', '南京', '成都', '重庆', '西安', '武汉', '苏州', '天津'],
  groups: [
    { letter: 'A', cities: ['安庆', '鞍山', '安阳'] },
    { letter: 'B', cities: ['北京', '保定', '包头', '蚌埠'] },
    { letter: 'C', cities: ['成都', '重庆', '长沙', '长春', '常州'] },
    { letter: 'D', cities: ['大连', '东莞', '大同'] },
    { letter: 'F', cities: ['福州', '佛山'] },
    { letter: 'G', cities: ['广州', '贵阳', '桂林'] },
    { letter: 'H', cities: ['杭州', '合肥', '哈尔滨', '海口', '惠州'] },
    { letter: 'J', cities: ['济南', '嘉兴', '金华'] },
    { letter: 'K', cities: ['昆明', '开封'] },
    { letter: 'L', cities: ['兰州', '拉萨', '丽江', '洛阳'] },
    { letter: 'N', cities: ['南京', '南昌', '南宁', '宁波'] },
    { letter: 'Q', cities: ['青岛', '泉州'] },
    { letter: 'S', cities: ['上海', '深圳', '苏州', '沈阳', '石家庄', '三亚'] },
    { letter: 'T', cities: ['天津', '太原', '台州'] },
    { letter: 'W', cities: ['武汉', '无锡', '温州', '乌鲁木齐'] },
    { letter: 'X', cities: ['西安', '厦门', '徐州'] },
    { letter: 'Y', cities: ['扬州', '银川', '烟台'] },
    { letter: 'Z', cities: ['郑州', '珠海', '中山'] }
  ],
  cityGroups: [
    { letter: 'A', cities: ['安庆', '安阳', '鞍山'] },
    { letter: 'B', cities: ['北京', '保定', '包头', '蚌埠', '滨州'] },
    { letter: 'C', cities: ['成都', '重庆', '长沙', '长春', '常州', '沧州'] },
    { letter: 'D', cities: ['大连', '东莞', '大同', '德州'] },
    { letter: 'F', cities: ['福州', '佛山', '阜阳'] },
    { letter: 'G', cities: ['广州', '贵阳', '桂林', '赣州'] },
    { letter: 'H', cities: ['杭州', '合肥', '哈尔滨', '海口', '惠州', '湖州', '呼和浩特'] },
    { letter: 'J', cities: ['济南', '嘉兴', '金华', '江门', '吉林'] },
    { letter: 'K', cities: ['昆明', '开封'] },
    { letter: 'L', cities: ['兰州', '洛阳', '临沂', '柳州', '连云港'] },
    { letter: 'M', cities: ['绵阳', '马鞍山', '茂名'] },
    { letter: 'N', cities: ['南京', '宁波', '南昌', '南宁', '南通'] },
    { letter: 'Q', cities: ['青岛', '泉州', '秦皇岛', '衢州'] },
    { letter: 'S', cities: ['上海', '深圳', '苏州', '沈阳', '石家庄', '绍兴', '汕头', '三亚'] },
    { letter: 'T', cities: ['天津', '太原', '台州', '唐山', '泰州'] },
    { letter: 'W', cities: ['武汉', '无锡', '温州', '乌鲁木齐', '芜湖', '潍坊'] },
    { letter: 'X', cities: ['西安', '厦门', '徐州', '西宁', '襄阳', '新乡'] },
    { letter: 'Y', cities: ['烟台', '扬州', '宜昌', '银川', '盐城', '岳阳'] },
    { letter: 'Z', cities: ['郑州', '珠海', '中山', '镇江', '漳州', '淄博', '株洲'] }
  ]
}

// 地图标记数据
export const mockMapMarkers = [
  {
    id: 1,
    latitude: 30.2590,
    longitude: 120.1489,
    iconPath: '/static/icons/marker-scenic.png',
    width: 30,
    height: 30,
    callout: {
      content: '西湖',
      display: 'ALWAYS',
      fontSize: 12,
      borderRadius: 4,
      padding: 4
    }
  },
  {
    id: 2,
    latitude: 30.2456,
    longitude: 120.1567,
    iconPath: '/static/icons/marker-food.png',
    width: 30,
    height: 30
  },
  {
    id: 3,
    latitude: 30.2392,
    longitude: 120.0958,
    iconPath: '/static/icons/marker-scenic.png',
    width: 30,
    height: 30
  }
]
