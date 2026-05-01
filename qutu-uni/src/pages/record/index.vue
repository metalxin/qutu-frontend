<template>
  <view class="record-page">
    <!-- 地图区域 -->
    <view class="map-container">
      <map
        id="recordMap"
        class="record-map"
        :latitude="latitude"
        :longitude="longitude"
        :markers="markers"
        :polyline="polyline"
        :scale="16"
        :show-location="true"
        :enable-3D="true"
        :show-compass="true"
      />
      
      <!-- 顶部状态栏 -->
      <view class="map-top-bar">
        <view class="activity-dropdown" @tap="showActivityPicker = !showActivityPicker">
          <text class="dropdown-icon">{{ currentActivity.icon }}</text>
          <text class="dropdown-name">{{ currentActivity.name }}</text>
          <text class="dropdown-arrow">▼</text>
        </view>
        
        <!-- 活动类型选择器 -->
        <view class="activity-picker" :class="{ show: showActivityPicker }">
          <view 
            class="activity-option" 
            v-for="(activity, index) in activityTypes" 
            :key="activity.id"
            :class="{ show: showActivityPicker }"
            :style="{ transitionDelay: showActivityPicker ? (index * 0.04) + 's' : '0s' }"
            @tap="selectActivity(activity)"
          >
            <text class="activity-option-icon">{{ activity.icon }}</text>
            <text class="activity-option-name">{{ activity.name }}</text>
          </view>
        </view>
      </view>
      
      <!-- 右侧工具栏 -->
      <view class="map-tools">
        <view class="tool-btn" @tap="locateMe">
          <SFIcon name="crosshair" :size="36" color="#007AFF" />
        </view>
        <view class="tool-btn" @tap="toggleMapType">
          <SFIcon name="layers" :size="36" color="#007AFF" />
        </view>
      </view>
    </view>
    
    <!-- 底部控制面板 -->
    <view class="control-panel">
      <!-- 数据展示区 -->
      <view class="data-display">
        <view class="data-item timer">
          <text class="data-value">{{ formatTime(recordTime) }}</text>
          <text class="data-label">时间</text>
        </view>
        <view class="data-divider"></view>
        <view class="data-item">
          <text class="data-value">{{ formatDistance(distance) }}</text>
          <text class="data-label">距离</text>
        </view>
        <view class="data-divider"></view>
        <view class="data-item">
          <text class="data-value">{{ speed.toFixed(1) }}<text class="data-unit">km/h</text></text>
          <text class="data-label">速度</text>
        </view>
      </view>
      
      <!-- 控制按钮区 -->
      <view class="control-buttons">
        <view class="side-btn camera-btn" @tap="takePhoto">
          <SFIcon name="camera" :size="40" color="#007AFF" />
        </view>
        
        <view class="main-btn-wrapper">
          <view 
            class="main-btn" 
            :class="{ 'is-paused': isPaused, 'is-recording': isRecording && !isPaused }"
            @tap="toggleRecord"
          >
            <view class="btn-inner" v-if="!isRecording">
              <SFIcon name="play" :size="48" color="#FFFFFF" />
            </view>
            <view class="btn-inner" v-else-if="isPaused">
              <SFIcon name="play" :size="48" color="#FFFFFF" />
            </view>
            <view class="btn-inner" v-else>
              <SFIcon name="pause" :size="48" color="#FFFFFF" />
            </view>
          </view>
        </view>
        
        <view class="side-btn stop-btn" :class="{ 'visible': isRecording }" @tap="stopRecord">
          <SFIcon name="stop" :size="40" color="#FF3B30" />
        </view>
      </view>
      
      <!-- 底部提示 -->
      <view class="control-hint">
        <text v-if="!isRecording">点击开始记录</text>
        <text v-else-if="isPaused">已暂停，点击继续</text>
        <text v-else>记录中...</text>
      </view>
    </view>
    
    <!-- 返回按钮 -->
    <view class="back-btn" @tap="goBack">
      <SFIcon name="back" :size="36" color="#1D1D1F" />
    </view>
    
    <!-- 结束确认弹窗 -->
    <view class="popup-overlay" :class="{ 'show': showStopConfirm }" @tap="showStopConfirm = false">
      <view class="confirm-popup" @tap.stop>
        <view class="confirm-title">结束记录？</view>
        <view class="confirm-content">
          <view class="confirm-data">
            <view class="confirm-item">
              <text class="confirm-label">时间</text>
              <text class="confirm-value">{{ formatTime(recordTime) }}</text>
            </view>
            <view class="confirm-item">
              <text class="confirm-label">距离</text>
              <text class="confirm-value">{{ formatDistance(distance) }}</text>
            </view>
            <view class="confirm-item">
              <text class="confirm-label">平均速度</text>
              <text class="confirm-value">{{ avgSpeed.toFixed(1) }} km/h</text>
            </view>
          </view>
        </view>
        <view class="confirm-actions">
          <view class="confirm-btn cancel" @tap="showStopConfirm = false">继续记录</view>
          <view class="confirm-btn save" @tap="saveRecord">保存</view>
          <view class="confirm-btn discard" @tap="discardRecord">放弃</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

// 活动类型
const activityTypes = ref([
  { id: 1, icon: '🚴', name: '山地自行车' },
  { id: 2, icon: '🥾', name: '徒步' },
  { id: 3, icon: '🚶', name: '步行' },
  { id: 4, icon: '🏃', name: '跑步' },
  { id: 5, icon: '🚗', name: '自驾' },
  { id: 6, icon: '🏍️', name: '摩托车' },
  { id: 7, icon: '⛷️', name: '滑雪' }
])

const currentActivity = ref(activityTypes.value[0])
const showActivityPicker = ref(false)

// 地图相关
const latitude = ref(39.9042)
const longitude = ref(116.4074)
const markers = ref<any[]>([])
const polyline = ref<any[]>([])
const routePoints = ref<{ latitude: number; longitude: number }[]>([])
const lastPointTime = ref<number | null>(null)

// 记录状态
const isRecording = ref(false)
const isPaused = ref(false)
const recordTime = ref(0)
const distance = ref(0)
const speed = ref(0)
const showStopConfirm = ref(false)

let timer: any = null
let locationWatcher: any = null

// 计算平均速度
const avgSpeed = computed(() => {
  if (recordTime.value === 0) return 0
  // 距离(米) / 时间(秒) * 3.6 = km/h
  return (distance.value / recordTime.value) * 3.6
})

// 格式化时间
const formatTime = (seconds: number) => {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
  }
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// 格式化距离
const formatDistance = (meters: number) => {
  if (meters < 1000) {
    return `${Math.round(meters)}m`
  }
  return `${(meters / 1000).toFixed(2)}km`
}

// 选择活动类型
const selectActivity = (activity: any) => {
  currentActivity.value = activity
  showActivityPicker.value = false
}

// 定位自己
const ensureLocationAuth = async () => {
  return await new Promise<boolean>((resolve) => {
    uni.getSetting({
      success: (settingRes) => {
        const hasAuth = !!settingRes.authSetting?.['scope.userLocation']
        if (hasAuth) {
          resolve(true)
          return
        }
        uni.authorize({
          scope: 'scope.userLocation',
          success: () => resolve(true),
          fail: () => {
            uni.showModal({
              title: '需要定位权限',
              content: '请开启定位权限以记录路线',
              success: (res) => {
                if (res.confirm) {
                  uni.openSetting({
                    success: (openRes) => {
                      resolve(!!openRes.authSetting?.['scope.userLocation'])
                    },
                    fail: () => resolve(false)
                  })
                } else {
                  resolve(false)
                }
              }
            })
          }
        })
      },
      fail: () => resolve(false)
    })
  })
}

const locateMe = async () => {
  const ok = await ensureLocationAuth()
  if (!ok) return
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      latitude.value = res.latitude
      longitude.value = res.longitude
    }
  })
}

// 切换地图类型
const toggleMapType = () => {
  uni.showToast({
    title: '切换地图样式',
    icon: 'none'
  })
}

// 开始/暂停记录
const toggleRecord = () => {
  if (!isRecording.value) {
    // 开始记录
    startRecord()
  } else if (isPaused.value) {
    // 继续记录
    resumeRecord()
  } else {
    // 暂停记录
    pauseRecord()
  }
}

// 开始记录
const startRecord = async () => {
  const ok = await ensureLocationAuth()
  if (!ok) return
  isRecording.value = true
  isPaused.value = false
  
  // 获取当前位置作为起点
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      const startPoint = {
        latitude: res.latitude,
        longitude: res.longitude
      }
      routePoints.value = [startPoint]
      
      // 添加起点标记
      markers.value = [{
        id: 0,
        latitude: res.latitude,
        longitude: res.longitude,
        iconPath: '/static/marker-start.png',
        width: 30,
        height: 30,
        callout: {
          content: '起点',
          display: 'ALWAYS'
        }
      }, {
        id: 1,
        latitude: res.latitude,
        longitude: res.longitude,
        iconPath: '/static/marker-end.png',
        width: 30,
        height: 30,
        callout: {
          content: '终点',
          display: 'ALWAYS'
        }
      }]
      polyline.value = [{
        points: routePoints.value,
        color: '#FF6D00',
        width: 6
      }]
    },
    fail: () => {
      isRecording.value = false
      isPaused.value = false
      uni.showToast({ title: '获取定位失败', icon: 'none' })
    }
  })
  
  // 开始计时
  timer = setInterval(() => {
    recordTime.value++
  }, 1000)
  
  // 开始监听位置变化
  startLocationWatch()
}

// 暂停记录
const pauseRecord = () => {
  isPaused.value = true
  speed.value = 0
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  stopLocationWatch()
}

// 继续记录
const resumeRecord = () => {
  isPaused.value = false
  timer = setInterval(() => {
    recordTime.value++
  }, 1000)
  startLocationWatch()
}

// 停止记录
const stopRecord = () => {
  if (isRecording.value) {
    pauseRecord()
    showStopConfirm.value = true
  }
}

// 保存记录
const saveRecord = () => {
  showStopConfirm.value = false
  uni.showToast({
    title: '记录已保存',
    icon: 'success'
  })
  setTimeout(() => {
    goBack()
  }, 1500)
}

// 放弃记录
const discardRecord = () => {
  showStopConfirm.value = false
  resetRecord()
}

// 重置记录
const resetRecord = () => {
  isRecording.value = false
  isPaused.value = false
  recordTime.value = 0
  distance.value = 0
  speed.value = 0
  routePoints.value = []
  markers.value = []
  polyline.value = []
  lastPointTime.value = null
  
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  
  stopLocationWatch()
}

// 开始位置监听
const calcDistance = (p1: { latitude: number; longitude: number }, p2: { latitude: number; longitude: number }) => {
  const rad = (d: number) => (d * Math.PI) / 180
  const R = 6378137
  const dLat = rad(p2.latitude - p1.latitude)
  const dLon = rad(p2.longitude - p1.longitude)
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(rad(p1.latitude)) * Math.cos(rad(p2.latitude)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

const onLocationChange = (res: any) => {
  if (!isRecording.value || isPaused.value) return
  const nextPoint = { latitude: res.latitude, longitude: res.longitude }
  const lastPoint = routePoints.value[routePoints.value.length - 1]
  const nowTime = Date.now()
  if (lastPoint) {
    const add = calcDistance(lastPoint, nextPoint)
    distance.value += add
    if (typeof res.speed === 'number') {
      speed.value = Math.max(0, res.speed * 3.6)
    } else if (lastPointTime.value) {
      const dt = (nowTime - lastPointTime.value) / 1000
      if (dt > 0) {
        speed.value = Math.max(0, (add / dt) * 3.6)
      }
    }
  }
  lastPointTime.value = nowTime
  routePoints.value.push(nextPoint)
  latitude.value = res.latitude
  longitude.value = res.longitude
  if (markers.value.length > 0) {
    const startMarker = markers.value[0]
    markers.value = [
      startMarker,
      {
        id: 1,
        latitude: res.latitude,
        longitude: res.longitude,
        iconPath: '/static/marker-end.png',
        width: 30,
        height: 30,
        callout: {
          content: '终点',
          display: 'ALWAYS'
        }
      }
    ]
  }
  polyline.value = [{
    points: routePoints.value,
    color: '#FF6D00',
    width: 6
  }]
}

const startLocationWatch = () => {
  if (locationWatcher) return
  // #ifdef MP-WEIXIN
  uni.startLocationUpdate({
    success: () => {
      uni.onLocationChange(onLocationChange)
      locationWatcher = true
    },
    fail: () => {
      uni.showToast({ title: '定位失败', icon: 'none' })
    }
  })
  // #endif
  // #ifndef MP-WEIXIN
  uni.onLocationChange(onLocationChange)
  locationWatcher = true
  // #endif
}

// 停止位置监听
const stopLocationWatch = () => {
  if (!locationWatcher) return
  // #ifdef MP-WEIXIN
  uni.offLocationChange(onLocationChange)
  uni.stopLocationUpdate()
  // #endif
  // #ifndef MP-WEIXIN
  uni.offLocationChange(onLocationChange)
  // #endif
  locationWatcher = null
}

// 拍照
const takePhoto = () => {
  uni.chooseImage({
    count: 1,
    sourceType: ['camera'],
    success: () => {
      uni.showToast({
        title: '照片已添加',
        icon: 'success'
      })
    }
  })
}

// 返回
const goBack = () => {
  if (isRecording.value && !isPaused.value) {
    pauseRecord()
    showStopConfirm.value = true
  } else if (isRecording.value && isPaused.value) {
    showStopConfirm.value = true
  } else {
    uni.navigateBack()
  }
}

onMounted(() => {
  locateMe()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  stopLocationWatch()
})
</script>

<style lang="scss" scoped>
$primary-color: #FF6D00;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;

.record-page {
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
}

// 地图区域
.map-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.record-map {
  width: 100%;
  height: 100%;
}

// 顶部状态栏
.map-top-bar {
  position: absolute;
  top: calc(44px + env(safe-area-inset-top));
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.activity-dropdown {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 28rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 100rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.dropdown-icon {
  font-size: 32rpx;
}

.dropdown-name {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 600;
}

.dropdown-arrow {
  font-size: 20rpx;
  color: $text-secondary;
}

// 活动选择器
.activity-picker {
  position: absolute;
  top: 80rpx;
  left: 50%;
  transform: translateX(-50%) scale(0.8) translateY(-20rpx);
  background: $card-bg;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
  overflow: hidden;
  min-width: 240rpx;
  opacity: 0;
  transform-origin: top center;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.show {
    opacity: 1;
    transform: translateX(-50%) scale(1) translateY(0);
    pointer-events: auto;
  }
}

.activity-option {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #E5E5EA;
  opacity: 0;
  transform: translateY(-10rpx);
  transition: all 0.25s ease-out;

  &.show {
    opacity: 1;
    transform: translateY(0);
  }

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: $bg-color;
  }
}

.activity-option-icon {
  font-size: 32rpx;
}

.activity-option-name {
  font-size: 28rpx;
  color: $text-primary;
}

// 右侧工具栏
.map-tools {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.tool-btn {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);

  &:active {
    transform: scale(0.95);
  }
}

.tool-icon {
  font-size: 36rpx;
}

// 返回按钮
.back-btn {
  position: absolute;
  top: calc(44px + env(safe-area-inset-top));
  left: 24rpx;
  width: 72rpx;
  height: 72rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
  z-index: 10;

  &:active {
    transform: scale(0.95);
  }
}

.back-icon {
  font-size: 36rpx;
  color: $text-primary;
}

// 底部控制面板
.control-panel {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx 40rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.1);
}

// 数据展示
.data-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32rpx;
  margin-bottom: 40rpx;
}

.data-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex: 1;

  &.timer {
    .data-value {
      font-size: 56rpx;
      font-weight: 700;
    }
  }
}

.data-value {
  font-size: 44rpx;
  font-weight: 600;
  color: $text-primary;
  font-family: 'SF Pro Display', -apple-system, sans-serif;
}

.data-unit {
  font-size: 24rpx;
  font-weight: 400;
  color: $text-secondary;
  margin-left: 4rpx;
}

.data-label {
  font-size: 24rpx;
  color: $text-secondary;
}

.data-divider {
  width: 1rpx;
  height: 60rpx;
  background: #E5E5EA;
}

// 控制按钮
.control-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48rpx;
  margin-bottom: 24rpx;
}

.side-btn {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  transition: all 0.3s ease;

  &.camera-btn {
    background: #E3F2FD;
  }

  &.stop-btn {
    background: #FFEBEE;
    opacity: 0;
    pointer-events: none;

    &.visible {
      opacity: 1;
      pointer-events: auto;
    }
  }

  &:active {
    transform: scale(0.95);
  }
}

.btn-icon {
  font-size: 40rpx;
}

.main-btn-wrapper {
  position: relative;
}

.main-btn {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF8A00, #FF6D00);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255, 109, 0, 0.4);
  transition: all 0.3s ease;

  &.is-recording {
    background: linear-gradient(135deg, #FF6D00, #E65100);
    animation: pulse 1.5s infinite;
  }

  &.is-paused {
    background: linear-gradient(135deg, #4CAF50, #388E3C);
    box-shadow: 0 8rpx 24rpx rgba(76, 175, 80, 0.4);
  }

  &:active {
    transform: scale(0.95);
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 8rpx 24rpx rgba(255, 109, 0, 0.4);
  }
  50% {
    box-shadow: 0 8rpx 40rpx rgba(255, 109, 0, 0.6);
  }
}

.btn-inner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon-large {
  font-size: 48rpx;
  color: white;
}

// 提示文字
.control-hint {
  text-align: center;
  font-size: 24rpx;
  color: $text-secondary;
}

// 弹窗
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.confirm-popup {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 580rpx;
  background: $card-bg;
  border-radius: 24rpx;
  overflow: hidden;
}

.confirm-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
  text-align: center;
  padding: 40rpx;
  border-bottom: 1rpx solid #E5E5EA;
}

.confirm-content {
  padding: 32rpx 40rpx;
}

.confirm-data {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.confirm-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.confirm-label {
  font-size: 28rpx;
  color: $text-secondary;
}

.confirm-value {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.confirm-actions {
  display: flex;
  flex-direction: column;
  border-top: 1rpx solid #E5E5EA;
}

.confirm-btn {
  padding: 28rpx;
  text-align: center;
  font-size: 32rpx;
  border-bottom: 1rpx solid #E5E5EA;

  &:last-child {
    border-bottom: none;
  }

  &.cancel {
    color: $primary-color;
    font-weight: 600;
  }

  &.save {
    color: #4CAF50;
    font-weight: 600;
  }

  &.discard {
    color: #FF3B30;
  }

  &:active {
    background: $bg-color;
  }
}
</style>
