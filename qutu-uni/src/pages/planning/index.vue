<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-back" @tap="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <text class="nav-title">智能规划</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 蓝色渐变头部 -->
    <view class="header-banner">
      <view class="banner-content">
        <text class="banner-title">智能行程规划</text>
        <text class="banner-subtitle">AI为您定制专属自驾路线</text>
      </view>
      <view class="banner-graphic">
        <view class="route-line">
          <view class="route-point start"></view>
          <view class="route-segment"></view>
          <view class="route-point mid"></view>
          <view class="route-segment"></view>
          <view class="route-point mid"></view>
          <view class="route-segment up"></view>
          <view class="route-point end"></view>
        </view>
      </view>
    </view>

    <!-- 地点选择卡片 -->
    <view class="location-card">
      <!-- 起点 -->
      <view class="location-item" @tap="selectLocation('start')">
        <view class="location-tag start">
          <text class="tag-text">起</text>
        </view>
        <text class="location-name">{{ startLocation || '选择出发地' }}</text>
        <view class="location-action" v-if="startLocation" @tap.stop="clearStart">
          <text class="action-icon">✕</text>
        </view>
        <view class="location-action" v-else>
          <text class="action-icon">›</text>
        </view>
      </view>

      <!-- 添加途经地 -->
      <view class="location-item add" @tap="addWaypoint">
        <view class="location-tag add">
          <text class="tag-text">+</text>
        </view>
        <text class="location-name add">添加途经地</text>
        <text class="location-hint">（建议按照游玩顺序添加）</text>
        <view class="location-action">
          <text class="action-icon">›</text>
        </view>
      </view>

      <!-- 途经地列表 -->
      <view 
        class="location-item waypoint" 
        v-for="(point, index) in waypoints" 
        :key="index"
      >
        <view class="location-tag waypoint">
          <text class="tag-text">{{ index + 1 }}</text>
        </view>
        <text class="location-name">{{ point }}</text>
        <view class="location-action" @tap="removeWaypoint(index)">
          <text class="action-icon">✕</text>
        </view>
      </view>

      <!-- 终点 -->
      <view class="location-item" @tap="selectLocation('end')">
        <view class="location-tag end">
          <text class="tag-text">终</text>
        </view>
        <text class="location-name">{{ endLocation || '选择目的地' }}</text>
        <view class="location-action" v-if="endLocation" @tap.stop="clearEnd">
          <text class="action-icon">✕</text>
        </view>
        <view class="location-action" v-else>
          <text class="action-icon">›</text>
        </view>
      </view>
    </view>

    <!-- 行程设置卡片 -->
    <view class="settings-card">
      <view class="settings-header">
        <view class="header-indicator"></view>
        <text class="header-title">行程设置</text>
      </view>

      <!-- 行程天数 -->
      <view class="setting-item">
        <text class="setting-label">行程天数</text>
        <view class="day-selector">
          <view class="day-btn minus" :class="{ disabled: tripDays <= 1 }" @tap="decreaseDays">
            <SFIcon name="minus" :size="28" :color="tripDays <= 1 ? '#D1D1D6' : '#00C9A7'" />
          </view>
          <text class="day-value">{{ tripDays }}</text>
          <text class="day-unit">天</text>
          <view class="day-btn plus" @tap="increaseDays">
            <SFIcon name="plus" :size="28" color="#FFFFFF" />
          </view>
        </view>
      </view>

      <!-- 规划偏好 -->
      <view class="setting-item column">
        <text class="setting-label">规划偏好</text>
        <view class="preference-options">
          <view 
            class="preference-btn" 
            :class="{ active: preference === 'auto' }"
            @tap="preference = 'auto'"
          >
            <text class="preference-text">自动推荐</text>
          </view>
          <view 
            class="preference-btn" 
            :class="{ active: preference === 'spots' }"
            @tap="preference = 'spots'"
          >
            <text class="preference-text">只规划景点</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-area">
      <view class="generate-btn" @tap="generateRoute">
        <text class="btn-text">智能生成路线</text>
      </view>
    </view>

    <!-- 城市选择弹窗 -->
    <view class="popup-mask" v-if="showCityPicker" @tap="showCityPicker = false"></view>
    <view class="city-popup" :class="{ 'popup-show': showCityPicker }">
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <text class="popup-title">选择{{ currentSelectType === 'start' ? '出发地' : currentSelectType === 'end' ? '目的地' : '途经地' }}</text>
        <view class="popup-close" @tap="showCityPicker = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 搜索框 -->
      <view class="popup-search">
        <text class="popup-search-icon">🔍</text>
        <input class="popup-search-input" placeholder="搜索城市" v-model="citySearchText" />
      </view>

      <!-- 面包屑导航 -->
      <view class="breadcrumb">
        <text class="breadcrumb-item active">中国</text>
      </view>

      <!-- 地区列表 -->
      <scroll-view class="region-scroll" scroll-y>
        <!-- 热门城市 -->
        <view class="region-group">
          <text class="region-group-title">热门城市</text>
          <view class="region-tags">
            <view 
              class="region-tag" 
              :class="{ selected: tempSelectedCity === city }"
              v-for="city in filteredHotCities" 
              :key="city"
              @tap="tempSelectedCity = city"
            >
              <text class="region-tag-text">{{ city }}</text>
            </view>
          </view>
        </view>

        <!-- 按地区分组 -->
        <view class="region-group" v-for="group in regionGroups" :key="group.name">
          <text class="region-group-title">{{ group.name }}</text>
          <view class="region-tags">
            <view 
              class="region-tag" 
              :class="{ selected: tempSelectedCity === province }"
              v-for="province in group.provinces" 
              :key="province"
              @tap="tempSelectedCity = province"
            >
              <text class="region-tag-text">{{ province }}</text>
            </view>
          </view>
        </view>
      </scroll-view>

      <!-- 底部操作栏 -->
      <view class="popup-footer">
        <view class="footer-back" @tap="showCityPicker = false">
          <text class="back-arrow">‹</text>
          <text class="back-text">取消</text>
        </view>
        <view class="footer-confirm" @tap="confirmCitySelection">
          <text class="confirm-text">确定{{ tempSelectedCity ? ' ' + tempSelectedCity : '' }}</text>
        </view>
      </view>
    </view>

    <!-- AI生成中遮罩 -->
    <view class="generating-overlay" v-if="generating">
      <view class="generating-card">
        <view class="ai-brain">
          <view class="brain-core"></view>
          <view class="brain-wave wave1"></view>
          <view class="brain-wave wave2"></view>
          <view class="brain-wave wave3"></view>
        </view>
        <view class="generating-title">AI正在为您规划路线</view>
        <view class="generating-step">{{ generateSteps[currentStep] }}</view>
        <view class="progress-bar">
          <view class="progress-fill" :style="{ width: generateProgress + '%' }"></view>
        </view>
        <view class="progress-text">{{ Math.min(Math.floor(generateProgress), 100) }}%</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getPlanningSettings, generatePlanningRoute, getCityList } from '@/api'
import { getRegionGroups } from '@/api'

// 状态栏高度
const statusBarHeight = ref(44)
const loading = ref(false)

// 地点数据
const startLocation = ref('合肥市')
const endLocation = ref('合肥市')
const waypoints = ref<string[]>([])

// 行程设置
const tripDays = ref(3)
const preference = ref('auto')

// 城市选择弹窗
const showCityPicker = ref(false)
const citySearchText = ref('')
const currentSelectType = ref('start') // start | end | waypoint
const tempSelectedCity = ref('')

// 热门城市
const hotCities = ref<string[]>([
  '北京', '上海', '广州', '深圳', '杭州', '南京',
  '成都', '重庆', '西安', '武汉', '长沙', '苏州',
  '厦门', '青岛', '大连', '三亚', '丽江', '拉萨'
])

// 地区分组
const regionGroups = ref<Array<{ name: string; provinces: string[] }>>([])

// 过滤后的热门城市
const filteredHotCities = computed(() => {
  if (!citySearchText.value) return hotCities.value
  return hotCities.value.filter(city => city.includes(citySearchText.value))
})

// 加载规划设置
const loadPlanningSettings = async () => {
  try {
    const res = await getPlanningSettings()
    if (res) {
      if (res.defaultStartLocation) startLocation.value = res.defaultStartLocation
      if (res.defaultEndLocation) endLocation.value = res.defaultEndLocation
      if (res.defaultDays) tripDays.value = res.defaultDays
      if (res.defaultPreference) preference.value = res.defaultPreference
    }
  } catch (error) {
    console.error('加载规划设置失败:', error)
  }
}

// 加载热门城市
const loadHotCities = async () => {
  try {
    const res = await getCityList()
    if (res && res.hotCities && res.hotCities.length > 0) {
      hotCities.value = res.hotCities
    }
  } catch (error) {
    console.error('加载热门城市失败:', error)
  }
}

// 加载地区分组
const loadRegionGroups = async () => {
  try {
    const res = await getRegionGroups()
    regionGroups.value = res
  } catch (error) {
    console.error('加载地区数据失败:', error)
  }
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  
  loadPlanningSettings()
  loadHotCities()
  loadRegionGroups()
})

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 选择地点
const selectLocation = (type: string) => {
  currentSelectType.value = type
  tempSelectedCity.value = ''
  citySearchText.value = ''
  showCityPicker.value = true
}

// 确认城市选择
const confirmCitySelection = () => {
  if (!tempSelectedCity.value) {
    uni.showToast({
      title: '请选择城市',
      icon: 'none'
    })
    return
  }
  
  if (currentSelectType.value === 'start') {
    startLocation.value = tempSelectedCity.value
  } else if (currentSelectType.value === 'end') {
    endLocation.value = tempSelectedCity.value
  } else if (currentSelectType.value === 'waypoint') {
    waypoints.value.push(tempSelectedCity.value)
  }
  showCityPicker.value = false
}

// 选择城市（直接点击选中）
const selectCity = (city: string) => {
  if (currentSelectType.value === 'start') {
    startLocation.value = city
  } else if (currentSelectType.value === 'end') {
    endLocation.value = city
  } else if (currentSelectType.value === 'waypoint') {
    waypoints.value.push(city)
  }
  showCityPicker.value = false
}

// 清除起点
const clearStart = () => {
  startLocation.value = ''
}

// 清除终点
const clearEnd = () => {
  endLocation.value = ''
}

// 添加途经地
const addWaypoint = () => {
  currentSelectType.value = 'waypoint'
  showCityPicker.value = true
}

// 移除途经地
const removeWaypoint = (index: number) => {
  waypoints.value.splice(index, 1)
}

// 减少天数
const decreaseDays = () => {
  if (tripDays.value > 1) {
    tripDays.value--
  }
}

// 增加天数
const increaseDays = () => {
  tripDays.value++
}

// AI生成中状态
const generating = ref(false)
const generateProgress = ref(0)
const generateSteps = [
  '正在分析您的旅行偏好...',
  '正在搜索目的地景点...',
  '正在优化游览顺序...',
  '正在规划每日行程...',
  '正在匹配餐厅和住宿...',
  '正在生成最优路线...'
]
const currentStep = ref(0)

// 创建模拟路线数据
const createMockRoute = () => {
  const days = tripDays.value
  const schedule = []
  
  for (let i = 0; i < days; i++) {
    const daySchedule = {
      date: `第${i + 1}天`,
      totalDuration: '6小时',
      totalDistance: '80公里',
      spots: [
        {
          id: `spot-${i}-1`,
          name: `${startLocation.value}景点${i + 1}`,
          startTime: '09:00',
          endTime: '12:00',
          duration: '3小时',
          spotInfo: {
            image: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=400',
            rating: '4.5',
            category: '自然景观',
            openTime: '08:00 - 18:00',
            address: `${startLocation.value}市XX路XX号`,
            price: '免费',
            tips: '建议早上去，人少光线好',
            description: '这是一个美丽的景点，风景优美，非常适合拍照留念。'
          }
        },
        {
          id: `spot-${i}-2`,
          name: `${endLocation.value}景点${i + 2}`,
          startTime: '14:00',
          endTime: '17:00',
          duration: '3小时',
          spotInfo: {
            image: 'https://images.unsplash.com/photo-1519677100203-a0e668c92439?w=400',
            rating: '4.8',
            category: '历史文化',
            openTime: '09:00 - 17:30',
            address: `${endLocation.value}市YY路YY号`,
            price: '50元',
            tips: '可以请导游讲解',
            description: '这里有着悠久的历史文化，是了解当地文化的好地方。'
          }
        }
      ],
      meals: {
        lunch: {
          name: '特色餐厅',
          address: '市中心美食街88号'
        },
        dinner: {
          name: '当地风味馆',
          address: '旅游区美食广场'
        }
      },
      hotel: {
        name: '舒适酒店',
        price: '¥299'
      }
    }
    schedule.push(daySchedule)
  }
  
  return {
    name: `${startLocation.value} → ${endLocation.value} ${days}日游`,
    startCity: startLocation.value,
    endCity: endLocation.value,
    days: days,
    summary: {
      totalSpots: days * 2,
      totalDistance: `${days * 200}公里`,
      estimatedCost: `¥${days * 500}`,
      bestSeason: '春秋季'
    },
    schedule: schedule
  }
}

// 生成路线
const generateRoute = async () => {
  if (!startLocation.value) {
    uni.showToast({
      title: '请选择出发地',
      icon: 'none'
    })
    return
  }
  if (!endLocation.value) {
    uni.showToast({
      title: '请选择目的地',
      icon: 'none'
    })
    return
  }
  
  // 开始AI生成动画
  generating.value = true
  generateProgress.value = 0
  currentStep.value = 0
  loading.value = true
  
  // 模拟AI思考过程
  const progressInterval = setInterval(() => {
    if (generateProgress.value < 90) {
      generateProgress.value += Math.random() * 5
      currentStep.value = Math.min(
        Math.floor(generateProgress.value / 100 * generateSteps.length),
        generateSteps.length - 1
      )
    }
  }, 500)
  
  try {
    const res = await generatePlanningRoute({
      startLocation: startLocation.value,
      endLocation: endLocation.value,
      waypoints: waypoints.value,
      days: tripDays.value,
      preference: preference.value
    })
    
    // 完成动画
    clearInterval(progressInterval)
    generateProgress.value = 100
    currentStep.value = generateSteps.length - 1
    
    await new Promise(resolve => setTimeout(resolve, 500))
    
    generating.value = false
    loading.value = false
    
    let routeData = null
    
    if (res && res.route) {
      routeData = res.route
    } else {
      // API返回数据格式不对时，使用模拟数据
      routeData = createMockRoute()
    }
    
    // 保存路线数据到本地存储以便详情页获取
    uni.setStorageSync('currentRoute', JSON.stringify(routeData))
    
    // 跳转到详情页
    uni.navigateTo({
      url: '/pages/planning/detail'
    })
  } catch (error) {
    clearInterval(progressInterval)
    generateProgress.value = 100
    currentStep.value = generateSteps.length - 1
    
    await new Promise(resolve => setTimeout(resolve, 500))
    
    generating.value = false
    loading.value = false
    
    // API调用失败时，使用模拟数据
    const routeData = createMockRoute()
    uni.setStorageSync('currentRoute', JSON.stringify(routeData))
    
    // 跳转到详情页
    uni.navigateTo({
      url: '/pages/planning/detail'
    })
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #00C9A7;
$primary-light: #E3F9F5;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;

.page {
  min-height: 100vh;
  background: $bg-color;
}

// 导航栏
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 32rpx;
  background: $card-bg;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: $text-primary;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 60rpx;
}

// 头部横幅
.header-banner {
  background: linear-gradient(135deg, #007AFF 0%, #5AC8FA 50%, #34C759 100%);
  padding: 56rpx 32rpx 72rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-content {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.banner-title {
  font-size: 44rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.banner-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
}

.banner-graphic {
  width: 200rpx;
  height: 100rpx;
  display: flex;
  align-items: flex-end;
}

.route-line {
  display: flex;
  align-items: flex-end;
  gap: 4rpx;
}

.route-point {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: 3rpx solid #FFFFFF;

  &.start {
    background: transparent;
  }
}

.route-segment {
  width: 40rpx;
  height: 4rpx;
  background: rgba(255, 255, 255, 0.6);
  margin-bottom: 6rpx;

  &.up {
    transform: rotate(-30deg);
    transform-origin: left center;
  }
}

// 地点选择卡片
.location-card {
  margin: -48rpx 24rpx 28rpx;
  background: $card-bg;
  border-radius: 32rpx;
  padding: 12rpx 0;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.12);
  position: relative;
  z-index: 10;
}

.location-item {
  display: flex;
  align-items: center;
  padding: 32rpx 36rpx;
  border-bottom: 1rpx solid #F0F0F5;
  position: relative;
  transition: background-color 0.2s ease;

  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    background-color: rgba(0, 0, 0, 0.04);
  }

  &.add {
    .location-name {
      color: $primary-color;
      font-weight: 500;
    }
  }
}

.location-tag {
  width: 48rpx;
  height: 48rpx;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;

  &.start {
    background: $primary-color;
  }

  &.end {
    background: #FF6B6B;
  }

  &.add {
    background: $primary-light;
    border: 2rpx dashed $primary-color;
  }

  &.waypoint {
    background: #FFB800;
  }
}

.tag-text {
  font-size: 24rpx;
  font-weight: 600;
  color: #FFFFFF;

  .location-tag.add & {
    color: $primary-color;
    font-size: 32rpx;
  }
}

.location-name {
  flex: 1;
  font-size: 30rpx;
  color: $text-primary;
  font-weight: 500;

  &.add {
    flex: none;
  }
}

.location-hint {
  font-size: 24rpx;
  color: $text-secondary;
  margin-left: 8rpx;
}

.location-action {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon {
  font-size: 32rpx;
  color: #C7C7CC;
}

// 行程设置卡片
.settings-card {
  margin: 0 24rpx 28rpx;
  background: $card-bg;
  border-radius: 32rpx;
  padding: 36rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.12);
}

.settings-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 32rpx;
}

.header-indicator {
  width: 6rpx;
  height: 32rpx;
  background: $primary-color;
  border-radius: 3rpx;
}

.header-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;

  &:last-child {
    margin-bottom: 0;
  }

  &.column {
    flex-direction: column;
    align-items: flex-start;
    gap: 20rpx;
  }
}

.setting-label {
  font-size: 30rpx;
  color: $text-primary;
}

// 天数选择器
.day-selector {
  display: flex;
  align-items: center;
  gap: 24rpx;
  background: $bg-color;
  padding: 12rpx 24rpx;
  border-radius: 24rpx;
}

.day-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &.minus {
    background: #FFFFFF;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
    
    .btn-icon {
      color: $text-primary;
    }
  }

  &.plus {
    background: $primary-color;
    box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.25);
    
    .btn-icon {
      color: #FFFFFF;
    }
  }

  &.disabled {
    opacity: 0.4;
  }

  &:active {
    transform: scale(0.93);
  }
}

.btn-icon {
  font-size: 32rpx;
  color: $primary-color;
  font-weight: 500;
}

.day-value {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
  min-width: 40rpx;
  text-align: center;
}

.day-unit {
  font-size: 28rpx;
  color: $text-secondary;
}

// 偏好选项
.preference-options {
  display: flex;
  gap: 16rpx;
}

.preference-btn {
  flex: 1;
  padding: 24rpx 32rpx;
  border-radius: 20rpx;
  border: 2rpx solid #E5E5EA;
  background: $card-bg;
  transition: all 0.2s ease;
  text-align: center;

  &.active {
    border-color: $primary-color;
    background: $primary-light;
    box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.15);
  }

  &:active {
    transform: scale(0.96);
  }
}

.preference-text {
  font-size: 28rpx;
  color: $text-primary;

  .preference-btn.active & {
    color: $primary-color;
    font-weight: 500;
  }
}

// 底部区域
.bottom-area {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: $bg-color;
}

.generate-btn {
  width: 100%;
  height: 100rpx;
  background: $primary-color;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 122, 255, 0.3);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 16rpx rgba(0, 122, 255, 0.3);
  }
}

.btn-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

// 城市选择弹窗
.popup-mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

.city-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 85vh;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx 40rpx 24rpx;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
}

.popup-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  border-radius: 50%;
}

.close-icon {
  font-size: 40rpx;
  color: $text-secondary;
  line-height: 1;
}

// 弹窗搜索框
.popup-search {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin: 0 40rpx 32rpx;
  padding: 24rpx 32rpx;
  background: $bg-color;
  border-radius: 20rpx;
  border: 2rpx solid #E5E5EA;
}

.popup-search-icon {
  font-size: 32rpx;
  color: $text-secondary;
}

.popup-search-input {
  flex: 1;
  font-size: 30rpx;
  color: $text-primary;
  background: transparent;
}

// 面包屑导航
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 0 40rpx 24rpx;
}

.breadcrumb-item {
  font-size: 28rpx;
  color: $text-secondary;
  
  &.active {
    color: $primary-color;
    font-weight: 500;
  }
}

// 地区滚动区域
.region-scroll {
  flex: 1;
  padding: 0 40rpx;
  overflow-y: auto;
}

.region-group {
  margin-bottom: 40rpx;
}

.region-group-title {
  font-size: 26rpx;
  color: $text-secondary;
  margin-bottom: 20rpx;
}

.region-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.region-tag {
  padding: 20rpx 36rpx;
  background: $bg-color;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: rgba(0, 122, 255, 0.1);
    border-color: $primary-color;
  }

  &:active {
    transform: scale(0.96);
  }
}

.region-tag-text {
  font-size: 28rpx;
  color: $text-primary;
}

.region-tag.selected .region-tag-text {
  color: $primary-color;
  font-weight: 500;
}

// 弹窗底部操作栏
.popup-footer {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx 40rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: $card-bg;
  border-top: 1rpx solid #E5E5EA;
}

.footer-back {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 24rpx 32rpx;
  background: $bg-color;
  border-radius: 100rpx;
}

.back-arrow {
  font-size: 32rpx;
  color: $text-primary;
}

.back-text {
  font-size: 28rpx;
  color: $text-primary;
}

.footer-confirm {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28rpx 40rpx;
  background: $primary-color;
  border-radius: 100rpx;
}

.confirm-text {
  font-size: 30rpx;
  color: #FFFFFF;
  font-weight: 600;
}

// AI生成中遮罩
.generating-overlay {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.generating-card {
  width: 560rpx;
  padding: 60rpx 40rpx;
  background: #fff;
  border-radius: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ai-brain {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 40rpx;
  
  .brain-core {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 60rpx;
    height: 60rpx;
    background: linear-gradient(135deg, $primary-color, #5AC8FA);
    border-radius: 50%;
    animation: pulse 1.5s ease-in-out infinite;
  }
  
  .brain-wave {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    border-radius: 50%;
    border: 4rpx solid $primary-color;
    opacity: 0;
    
    &.wave1 {
      width: 80rpx;
      height: 80rpx;
      animation: wave 2s ease-out infinite 0s;
    }
    
    &.wave2 {
      width: 100rpx;
      height: 100rpx;
      animation: wave 2s ease-out infinite 0.5s;
    }
    
    &.wave3 {
      width: 120rpx;
      height: 120rpx;
      animation: wave 2s ease-out infinite 1s;
    }
  }
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
  }
}

@keyframes wave {
  0% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(0.8);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(2);
  }
}

.generating-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 16rpx;
}

.generating-step {
  font-size: 28rpx;
  color: $text-secondary;
  margin-bottom: 32rpx;
  min-height: 40rpx;
}

.progress-bar {
  width: 100%;
  height: 12rpx;
  background: #E5E5EA;
  border-radius: 6rpx;
  overflow: hidden;
  
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, $primary-color, #5AC8FA);
    border-radius: 6rpx;
    transition: width 0.3s ease;
  }
}

.progress-text {
  font-size: 24rpx;
  color: $primary-color;
  margin-top: 16rpx;
  font-weight: 500;
}
</style>
