/**
 * Mock数据 - 规划模块
 */

// 热门城市
export const mockPlanningCities = [
  '北京', '上海', '广州', '深圳', '杭州', '南京',
  '成都', '重庆', '西安', '武汉', '长沙', '苏州',
  '厦门', '青岛', '大连', '三亚', '丽江', '拉萨',
  '合肥', '黄山', '桂林', '张家界', '九寨沟', '敦煌'
]

// 景点详情数据库
export const mockSpotDatabase: Record<string, Array<{
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
}>> = {
  '北京': [
    { id: 101, name: '故宫博物院', rating: 4.9, duration: '4小时', price: '¥60', image: '/static/spots/gugong.jpg', category: '历史古迹', description: '世界上现存规模最大、保存最为完整的木质结构古建筑之一', tips: '建议提前网上预约门票', openTime: '08:30-17:00', address: '北京市东城区景山前街4号', latitude: 39.9163, longitude: 116.3972 },
    { id: 102, name: '天安门广场', rating: 4.8, duration: '1.5小时', price: '免费', image: '/static/spots/tiananmen.jpg', category: '地标建筑', description: '世界上最大的城市广场之一', tips: '观看升旗仪式需要早起', openTime: '全天', address: '北京市东城区天安门广场', latitude: 39.9055, longitude: 116.3976 },
    { id: 103, name: '天坛公园', rating: 4.7, duration: '2.5小时', price: '¥15', image: '/static/spots/tiantan.jpg', category: '历史古迹', description: '明清两代帝王祭祀皇天的神圣场所', tips: '建议游览祈年殿和回音壁', openTime: '06:00-21:00', address: '北京市东城区天坛路甲1号', latitude: 39.8822, longitude: 116.4066 },
    { id: 104, name: '颐和园', rating: 4.8, duration: '4小时', price: '¥30', image: '/static/spots/yiheyuan.jpg', category: '皇家园林', description: '中国现存规模最大、保存最完整的皇家园林', tips: '可乘船游览昆明湖', openTime: '06:30-18:00', address: '北京市海淀区新建宫门路19号', latitude: 39.9999, longitude: 116.2755 },
    { id: 105, name: '八达岭长城', rating: 4.9, duration: '5小时', price: '¥40', image: '/static/spots/changcheng.jpg', category: '世界遗产', description: '万里长城最具代表性的段落', tips: '建议乘缆车上山节省体力', openTime: '06:30-19:00', address: '北京市延庆区八达岭镇', latitude: 40.3598, longitude: 116.0204 },
    { id: 106, name: '圆明园', rating: 4.6, duration: '3小时', price: '¥10', image: '/static/spots/yuanmingyuan.jpg', category: '历史遗址', description: '清代著名的皇家园林，被誉为"万园之园"', tips: '西洋楼遗址区需另购票', openTime: '07:00-19:00', address: '北京市海淀区清华西路28号', latitude: 40.0089, longitude: 116.2983 },
    { id: 107, name: '鸟巢水立方', rating: 4.5, duration: '2小时', price: '¥50', image: '/static/spots/niaochao.jpg', category: '现代建筑', description: '2008年北京奥运会主场馆', tips: '晚上灯光效果更佳', openTime: '09:00-21:00', address: '北京市朝阳区国家体育场南路1号', latitude: 39.9929, longitude: 116.3964 },
    { id: 108, name: '南锣鼓巷', rating: 4.4, duration: '2小时', price: '免费', image: '/static/spots/nanluoguxiang.jpg', category: '文化街区', description: '北京最古老的街区之一，充满文艺气息', tips: '工作日人少，体验更好', openTime: '全天', address: '北京市东城区南锣鼓巷', latitude: 39.9376, longitude: 116.4034 }
  ],
  '杭州': [
    { id: 201, name: '西湖景区', rating: 4.9, duration: '4小时', price: '免费', image: '/static/spots/xihu.jpg', category: '自然风光', description: '中国著名的风景旅游胜地，世界文化遗产', tips: '建议租自行车环湖骑行', openTime: '全天', address: '杭州市西湖区龙井路1号', latitude: 30.2590, longitude: 120.1551 },
    { id: 202, name: '雷峰塔', rating: 4.7, duration: '1.5小时', price: '¥40', image: '/static/spots/leifengta.jpg', category: '历史古迹', description: '西湖十景之一，白蛇传说的发生地', tips: '塔顶可俯瞰西湖全景', openTime: '08:00-20:30', address: '杭州市西湖区南山路15号', latitude: 30.2318, longitude: 120.1489 },
    { id: 203, name: '灵隐寺', rating: 4.8, duration: '3小时', price: '¥30', image: '/static/spots/lingyinsi.jpg', category: '宗教文化', description: '中国佛教著名寺院，江南禅宗"五山"之一', tips: '需另购飞来峰门票', openTime: '07:00-18:00', address: '杭州市西湖区灵隐路法云弄1号', latitude: 30.2410, longitude: 120.1015 },
    { id: 204, name: '龙井村', rating: 4.6, duration: '2.5小时', price: '免费', image: '/static/spots/longjing.jpg', category: '田园风光', description: '西湖龙井茶原产地，风景秀丽', tips: '可以体验采茶和品茶', openTime: '全天', address: '杭州市西湖区龙井路', latitude: 30.2276, longitude: 120.1087 },
    { id: 205, name: '西溪湿地', rating: 4.7, duration: '4小时', price: '¥80', image: '/static/spots/xixi.jpg', category: '湿地公园', description: '国内第一个集城市湿地、农耕湿地、文化湿地于一体的国家湿地公园', tips: '可乘坐摇橹船', openTime: '08:30-17:30', address: '杭州市西湖区天目山路518号', latitude: 30.2686, longitude: 120.0629 },
    { id: 206, name: '河坊街', rating: 4.5, duration: '2小时', price: '免费', image: '/static/spots/hefangjie.jpg', category: '历史街区', description: '杭州历史文化街区，汇集各种传统小吃', tips: '晚上更热闹', openTime: '全天', address: '杭州市上城区河坊街', latitude: 30.2405, longitude: 120.1689 }
  ],
  '成都': [
    { id: 301, name: '大熊猫繁育研究基地', rating: 4.9, duration: '4小时', price: '¥55', image: '/static/spots/panda.jpg', category: '动物园', description: '世界最大的大熊猫繁育研究机构', tips: '建议早上9点前入园看熊猫活动', openTime: '07:30-18:00', address: '成都市成华区熊猫大道1375号', latitude: 30.7348, longitude: 104.1456 },
    { id: 302, name: '锦里古街', rating: 4.7, duration: '2.5小时', price: '免费', image: '/static/spots/jinli.jpg', category: '历史街区', description: '成都著名的步行商业街，传统川西古镇风格', tips: '晚上灯笼亮起时最美', openTime: '全天', address: '成都市武侯区武侯祠大街231号', latitude: 30.6456, longitude: 104.0478 },
    { id: 303, name: '宽窄巷子', rating: 4.8, duration: '2.5小时', price: '免费', image: '/static/spots/kuanzhai.jpg', category: '历史街区', description: '成都著名的历史文化保护区', tips: '有很多特色小店和茶馆', openTime: '全天', address: '成都市青羊区同仁路以东', latitude: 30.6699, longitude: 104.0547 },
    { id: 304, name: '武侯祠', rating: 4.7, duration: '2小时', price: '¥50', image: '/static/spots/wuhouci.jpg', category: '历史古迹', description: '纪念诸葛亮的祠堂，三国文化圣地', tips: '可与锦里一起游览', openTime: '08:00-18:00', address: '成都市武侯区武侯祠大街231号', latitude: 30.6454, longitude: 104.0487 },
    { id: 305, name: '都江堰', rating: 4.9, duration: '5小时', price: '¥80', image: '/static/spots/dujiangyan.jpg', category: '世界遗产', description: '世界文化遗产，古代水利工程杰作', tips: '可顺路游览青城山', openTime: '08:00-18:00', address: '成都市都江堰市公园路', latitude: 31.0044, longitude: 103.6196 },
    { id: 306, name: '青城山', rating: 4.8, duration: '6小时', price: '¥90', image: '/static/spots/qingchengshan.jpg', category: '自然风光', description: '中国道教发源地之一，空气清新', tips: '前山和后山可选择一个', openTime: '08:00-17:00', address: '成都市都江堰市青城山镇', latitude: 30.8987, longitude: 103.5725 }
  ],
  '西安': [
    { id: 401, name: '秦始皇兵马俑', rating: 4.9, duration: '4小时', price: '¥120', image: '/static/spots/bingmayong.jpg', category: '世界遗产', description: '世界第八大奇迹，秦始皇陵的陪葬坑', tips: '建议请讲解员', openTime: '08:30-18:00', address: '西安市临潼区秦陵北路', latitude: 34.3847, longitude: 109.2785 },
    { id: 402, name: '大雁塔', rating: 4.7, duration: '2小时', price: '¥40', image: '/static/spots/dayanta.jpg', category: '历史古迹', description: '唐代著名的佛教建筑，玄奘藏经处', tips: '北广场音乐喷泉值得一看', openTime: '08:00-18:30', address: '西安市雁塔区慈恩路', latitude: 34.2185, longitude: 108.9590 },
    { id: 403, name: '古城墙', rating: 4.8, duration: '3小时', price: '¥54', image: '/static/spots/chengqiang.jpg', category: '历史古迹', description: '中国现存规模最大、保存最完整的古代城垣', tips: '可租自行车骑行城墙', openTime: '08:00-22:00', address: '西安市碑林区南大街', latitude: 34.2611, longitude: 108.9458 },
    { id: 404, name: '回民街', rating: 4.6, duration: '2.5小时', price: '免费', image: '/static/spots/huiminjie.jpg', category: '美食街区', description: '西安著名的美食一条街', tips: '羊肉泡馍和肉夹馍必吃', openTime: '全天', address: '西安市莲湖区北院门', latitude: 34.2656, longitude: 108.9378 },
    { id: 405, name: '华清宫', rating: 4.7, duration: '3小时', price: '¥120', image: '/static/spots/huaqinggong.jpg', category: '历史古迹', description: '唐代皇家行宫，杨贵妃沐浴处', tips: '晚上有《长恨歌》演出', openTime: '07:00-18:00', address: '西安市临潼区华清路38号', latitude: 34.3695, longitude: 109.2131 },
    { id: 406, name: '陕西历史博物馆', rating: 4.9, duration: '3.5小时', price: '免费', image: '/static/spots/shanximuseum.jpg', category: '博物馆', description: '中国第一座大型现代化国家级博物馆', tips: '免费门票需提前预约', openTime: '08:30-18:00', address: '西安市雁塔区小寨东路91号', latitude: 34.2268, longitude: 108.9545 }
  ],
  '合肥': [
    { id: 501, name: '三河古镇', rating: 4.6, duration: '3小时', price: '免费', image: '/static/spots/sanhe.jpg', category: '古镇', description: '具有2500多年历史的江南水乡古镇', tips: '可以品尝三河米饺', openTime: '全天', address: '合肥市肥西县三河镇', latitude: 31.5168, longitude: 117.1659 },
    { id: 502, name: '包公园', rating: 4.5, duration: '2小时', price: '¥35', image: '/static/spots/baogongyuan.jpg', category: '历史古迹', description: '纪念包拯的主题公园', tips: '包公祠和包公墓在园内', openTime: '08:00-18:00', address: '合肥市包河区芜湖路72号', latitude: 31.8503, longitude: 117.3011 },
    { id: 503, name: '逍遥津公园', rating: 4.4, duration: '1.5小时', price: '免费', image: '/static/spots/xiaoyaojin.jpg', category: '城市公园', description: '三国时期古战场遗址', tips: '张辽威震逍遥津的故事发生地', openTime: '06:00-22:00', address: '合肥市庐阳区寿春路16号', latitude: 31.8706, longitude: 117.2830 },
    { id: 504, name: '安徽省博物馆', rating: 4.7, duration: '3小时', price: '免费', image: '/static/spots/anhuimuseum.jpg', category: '博物馆', description: '安徽省最大的综合性博物馆', tips: '周一闭馆', openTime: '09:00-17:00', address: '合肥市怀宁路268号', latitude: 31.8472, longitude: 117.2439 },
    { id: 505, name: '大蜀山森林公园', rating: 4.5, duration: '4小时', price: '免费', image: '/static/spots/dashushan.jpg', category: '自然风光', description: '合肥市区最高的山峰', tips: '适合登山健身', openTime: '全天', address: '合肥市蜀山区', latitude: 31.8483, longitude: 117.1687 },
    { id: 506, name: '淮河路步行街', rating: 4.3, duration: '2小时', price: '免费', image: '/static/spots/huaihelu.jpg', category: '商业街区', description: '合肥最繁华的商业步行街', tips: '可以逛街购物吃小吃', openTime: '全天', address: '合肥市庐阳区淮河路', latitude: 31.8662, longitude: 117.2810 }
  ]
}

// AI推荐路线
export const mockAIRoutes = [
  {
    id: 1,
    name: '北京经典三日游',
    startCity: '北京',
    endCity: '北京',
    days: 3,
    distance: '48km',
    totalTime: '约6小时',
    spots: [
      { id: 101, name: '故宫博物院', day: 1, order: 1, duration: '4小时', startTime: '08:30', endTime: '12:30' },
      { id: 102, name: '天安门广场', day: 1, order: 2, duration: '1.5小时', startTime: '13:30', endTime: '15:00' },
      { id: 103, name: '天坛公园', day: 1, order: 3, duration: '2.5小时', startTime: '15:30', endTime: '18:00' },
      { id: 104, name: '颐和园', day: 2, order: 1, duration: '4小时', startTime: '09:00', endTime: '13:00' },
      { id: 106, name: '圆明园', day: 2, order: 2, duration: '3小时', startTime: '14:00', endTime: '17:00' },
      { id: 105, name: '八达岭长城', day: 3, order: 1, duration: '5小时', startTime: '08:00', endTime: '13:00' },
      { id: 107, name: '鸟巢水立方', day: 3, order: 2, duration: '2小时', startTime: '15:00', endTime: '17:00' }
    ]
  },
  {
    id: 2,
    name: '杭州西湖二日游',
    startCity: '杭州',
    endCity: '杭州',
    days: 2,
    distance: '25km',
    totalTime: '约4小时',
    spots: [
      { id: 201, name: '西湖景区', day: 1, order: 1, duration: '4小时', startTime: '09:00', endTime: '13:00' },
      { id: 202, name: '雷峰塔', day: 1, order: 2, duration: '1.5小时', startTime: '14:00', endTime: '15:30' },
      { id: 206, name: '河坊街', day: 1, order: 3, duration: '2小时', startTime: '16:00', endTime: '18:00' },
      { id: 203, name: '灵隐寺', day: 2, order: 1, duration: '3小时', startTime: '09:00', endTime: '12:00' },
      { id: 204, name: '龙井村', day: 2, order: 2, duration: '2.5小时', startTime: '13:30', endTime: '16:00' }
    ]
  }
]

// 规划偏好选项
export const mockPreferenceOptions = [
  { id: 'auto', name: '自动推荐', desc: 'AI根据景点评分和距离智能规划' },
  { id: 'spots', name: '只规划景点', desc: '只包含景点，不包含餐厅住宿' },
  { id: 'relax', name: '休闲模式', desc: '每天安排2-3个景点，节奏较慢' },
  { id: 'intensive', name: '特种兵模式', desc: '紧凑安排，一天玩遍更多景点' }
]

// 交通方式
export const mockTransportModes = [
  { id: 'driving', name: '自驾', icon: '🚗' },
  { id: 'public', name: '公共交通', icon: '🚇' },
  { id: 'walking', name: '步行', icon: '🚶' },
  { id: 'cycling', name: '骑行', icon: '🚲' }
]

// 餐厅推荐
export const mockRestaurants: Record<string, Array<{
  id: number
  name: string
  rating: number
  category: string
  price: string
  address: string
}>> = {
  '北京': [
    { id: 1001, name: '全聚德烤鸭店', rating: 4.6, category: '北京菜', price: '¥180/人', address: '前门大街32号' },
    { id: 1002, name: '四季民福烤鸭', rating: 4.8, category: '北京菜', price: '¥150/人', address: '王府井大街东安门' },
    { id: 1003, name: '东来顺涮羊肉', rating: 4.5, category: '火锅', price: '¥120/人', address: '王府井大街198号' }
  ],
  '杭州': [
    { id: 2001, name: '楼外楼', rating: 4.7, category: '杭帮菜', price: '¥150/人', address: '孤山路30号' },
    { id: 2002, name: '知味观', rating: 4.6, category: '杭帮菜', price: '¥80/人', address: '延安路147号' },
    { id: 2003, name: '外婆家', rating: 4.5, category: '杭帮菜', price: '¥60/人', address: '湖滨路步行街' }
  ]
}

// 住宿推荐
export const mockHotels: Record<string, Array<{
  id: number
  name: string
  rating: number
  price: string
  address: string
  tags: string[]
}>> = {
  '北京': [
    { id: 3001, name: '北京饭店', rating: 4.8, price: '¥800', address: '东长安街33号', tags: ['五星级', '地理位置优越'] },
    { id: 3002, name: '王府井希尔顿', rating: 4.7, price: '¥650', address: '王府井东街8号', tags: ['国际品牌', '商务'] }
  ],
  '杭州': [
    { id: 4001, name: '西湖国宾馆', rating: 4.9, price: '¥1200', address: '杨公堤18号', tags: ['园林酒店', '西湖边'] },
    { id: 4002, name: '杭州香格里拉', rating: 4.8, price: '¥800', address: '北山路78号', tags: ['五星级', '湖景房'] }
  ]
}
