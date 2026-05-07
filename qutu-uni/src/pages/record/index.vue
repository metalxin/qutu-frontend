<template>
  <view class="record-page">
    <view class="map-container">
      <map
        id="recordMap"
        class="record-map"
        :latitude="latitude"
        :longitude="longitude"
        :markers="allMarkers"
        :polyline="polyline"
        :scale="mapScale"
        :show-location="true"
        :enable-3D="true"
        :show-compass="true"
        :enable-satellite="mapStyleType === 'satellite'"
        :enable-traffic="showTraffic"
      />

      <view class="map-top-bar">
        <view class="activity-dropdown" @tap="showActivityPicker = !showActivityPicker">
          <text class="dropdown-icon">{{ currentActivity.icon }}</text>
          <text class="dropdown-name">{{ currentActivity.name }}</text>
          <text class="dropdown-arrow">▼</text>
        </view>

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

      <view class="map-tools">
        <view class="tool-btn" @tap="locateMe">
          <SFIcon name="crosshair" :size="36" color="#007AFF" />
        </view>
        <view class="tool-btn" @tap="showMapStylePicker = true">
          <SFIcon name="layers" :size="36" color="#007AFF" />
        </view>
        <view class="tool-btn photo-count-btn" v-if="photos.length > 0" @tap="showPhotoList = true">
          <SFIcon name="camera" :size="32" color="#007AFF" />
          <text class="photo-count-badge">{{ photos.length }}</text>
        </view>
      </view>
    </view>

    <view class="control-panel">
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

      <view class="control-hint">
        <text v-if="!isRecording">点击开始记录</text>
        <text v-else-if="isPaused">已暂停，点击继续</text>
        <text v-else>记录中...</text>
      </view>
    </view>

    <view class="back-btn" @tap="goBack">
      <SFIcon name="back" :size="36" color="#1D1D1F" />
    </view>

    <view class="history-btn" @tap="goToHistory">
      <SFIcon name="clock" :size="32" color="#1D1D1F" />
    </view>

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
            <view class="confirm-item" v-if="photos.length > 0">
              <text class="confirm-label">照片</text>
              <text class="confirm-value">{{ photos.length }} 张</text>
            </view>
          </view>
        </view>
        <view class="confirm-input" v-if="showStopConfirm">
          <input
            class="route-title-input"
            v-model="routeTitle"
            placeholder="为这条路线命名（选填）"
          />
        </view>
        <view class="confirm-actions">
          <view class="confirm-btn cancel" @tap="showStopConfirm = false">继续记录</view>
          <view class="confirm-btn save" @tap="saveRecord">保存</view>
          <view class="confirm-btn discard" @tap="discardRecord">放弃</view>
        </view>
      </view>
    </view>

    <view class="popup-overlay" :class="{ 'show': showMapStylePicker }" @tap="showMapStylePicker = false">
      <view class="style-popup" @tap.stop>
        <view class="style-popup-header">
          <text class="style-popup-title">地图样式</text>
          <view class="style-popup-close" @tap="showMapStylePicker = false">
            <SFIcon name="close" :size="32" color="#86868B" />
          </view>
        </view>
        <view class="style-list">
          <view
            class="style-item"
            v-for="style in mapStyleOptions"
            :key="style.value"
            :class="{ active: mapStyleType === style.value }"
            @tap="selectMapStyle(style.value)"
          >
            <view class="style-preview" :class="style.value">
              <text class="style-preview-icon">{{ style.icon }}</text>
            </view>
            <view class="style-info">
              <text class="style-name">{{ style.label }}</text>
              <text class="style-desc">{{ style.desc }}</text>
            </view>
            <view class="style-check" v-if="mapStyleType === style.value">
              <text class="check-icon">✓</text>
            </view>
          </view>
        </view>
        <view class="style-option-row">
          <view class="style-option-item">
            <text class="style-option-label">路况信息</text>
            <switch
              :checked="showTraffic"
              @change="showTraffic = ($event as any).detail.value"
              color="#007AFF"
              style="transform: scale(0.8)"
            />
          </view>
        </view>
      </view>
    </view>

    <view class="popup-overlay" :class="{ 'show': showPhotoList }" @tap="showPhotoList = false">
      <view class="photo-popup" @tap.stop>
        <view class="photo-popup-header">
          <text class="photo-popup-title">路线照片 ({{ photos.length }})</text>
          <view class="photo-popup-close" @tap="showPhotoList = false">
            <SFIcon name="close" :size="32" color="#86868B" />
          </view>
        </view>
        <scroll-view class="photo-scroll" scroll-y :show-scrollbar="false">
          <view class="photo-grid" v-if="photos.length > 0">
            <view
              class="photo-item"
              v-for="(photo, index) in photos"
              :key="photo.id"
              @tap="previewPhoto(index)"
            >
              <image :src="photo.url" mode="aspectFill" class="photo-image" />
              <view class="photo-location">
                <SFIcon name="location" :size="20" color="#FFFFFF" />
              </view>
            </view>
          </view>
          <view class="photo-empty" v-else>
            <text class="photo-empty-text">还没有照片，点击📷按钮添加</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { saveRouteRecord, uploadRoutePhoto } from '@/api'
import type { RoutePhoto, RoutePoint } from '@/api'
import { getUserSettings } from '@/api'

interface ActivityType {
  id: number
  icon: string
  name: string
  type: string
}

interface LocalPhoto {
  id: number
  url: string
  thumbnailUrl: string
  latitude: number
  longitude: number
  timestamp: number
  description?: string
}

const activityTypes = ref<ActivityType[]>([
  { id: 1, icon: '🚴', name: '山地自行车', type: 'bike' },
  { id: 2, icon: '🥾', name: '徒步', type: 'hiking' },
  { id: 3, icon: '🚶', name: '步行', type: 'walk' },
  { id: 4, icon: '🏃', name: '跑步', type: 'running' },
  { id: 5, icon: '🚗', name: '自驾', type: 'driving' },
  { id: 6, icon: '🏍️', name: '摩托车', type: 'motorcycle' },
  { id: 7, icon: '⛷️', name: '滑雪', type: 'skiing' }
])

const currentActivity = ref<ActivityType>(activityTypes.value[0])
const showActivityPicker = ref(false)

const latitude = ref(39.9042)
const longitude = ref(116.4074)
const mapScale = ref(16)
const markers = ref<any[]>([])
const polyline = ref<any[]>([])
const routePoints = ref<RoutePoint[]>([])
const lastPointTime = ref<number | null>(null)
const maxSpeed = ref(0)

const photos = ref<LocalPhoto[]>([])
const photoMarkerBaseId = 1000

const mapStyleType = ref('normal')
const showTraffic = ref(false)
const showMapStylePicker = ref(false)
const showPhotoList = ref(false)

const mapStyleOptions = [
  { value: 'normal', label: '标准地图', desc: '默认地图样式', icon: '🗺️' },
  { value: 'satellite', label: '卫星地图', desc: '卫星影像视图', icon: '🛰️' }
]

const isRecording = ref(false)
const isPaused = ref(false)
const recordTime = ref(0)
const distance = ref(0)
const speed = ref(0)
const showStopConfirm = ref(false)
const routeTitle = ref('')
const isSaving = ref(false)
const recordStartTime = ref<number>(0)

let timer: any = null
let locationWatcher: any = null

const avgSpeed = computed(() => {
  if (recordTime.value === 0) return 0
  return (distance.value / recordTime.value) * 3.6
})

const allMarkers = computed(() => {
  const photoMarkers = photos.value.map((photo, index) => ({
    id: photoMarkerBaseId + index,
    latitude: photo.latitude,
    longitude: photo.longitude,
    iconPath: '/static/marker-photo.png',
    width: 32,
    height: 32,
    callout: {
      content: '📷',
      display: 'ALWAYS',
      fontSize: 14,
      borderRadius: 8,
      bgColor: '#FFFFFF',
      padding: 4
    }
  }))
  return [...markers.value, ...photoMarkers]
})

const formatTime = (seconds: number) => {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
  }
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const formatDistance = (meters: number) => {
  if (meters < 1000) {
    return `${Math.round(meters)}m`
  }
  return `${(meters / 1000).toFixed(2)}km`
}

const selectActivity = (activity: ActivityType) => {
  currentActivity.value = activity
  showActivityPicker.value = false
}

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

const selectMapStyle = (value: string) => {
  mapStyleType.value = value
  showMapStylePicker.value = false
}

const toggleRecord = () => {
  if (!isRecording.value) {
    startRecord()
  } else if (isPaused.value) {
    resumeRecord()
  } else {
    pauseRecord()
  }
}

const startRecord = async () => {
  const ok = await ensureLocationAuth()
  if (!ok) return
  isRecording.value = true
  isPaused.value = false
  recordStartTime.value = Date.now()

  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      const startPoint: RoutePoint = {
        latitude: res.latitude,
        longitude: res.longitude,
        timestamp: Date.now(),
        speed: 0
      }
      routePoints.value = [startPoint]

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
        points: routePoints.value.map(p => ({ latitude: p.latitude, longitude: p.longitude })),
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

  timer = setInterval(() => {
    recordTime.value++
  }, 1000)

  startLocationWatch()
}

const pauseRecord = () => {
  isPaused.value = true
  speed.value = 0
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  stopLocationWatch()
}

const resumeRecord = () => {
  isPaused.value = false
  timer = setInterval(() => {
    recordTime.value++
  }, 1000)
  startLocationWatch()
}

const stopRecord = () => {
  if (isRecording.value) {
    pauseRecord()
    showStopConfirm.value = true
  }
}

const saveRecord = async () => {
  if (isSaving.value) return
  isSaving.value = true
  showStopConfirm.value = false

  try {
    const now = new Date()
    const startTime = new Date(recordStartTime.value)
    const title = routeTitle.value || `${currentActivity.value.name} - ${startTime.getMonth() + 1}月${startTime.getDate()}日`

    const routePhotos: RoutePhoto[] = photos.value.map(p => ({
      id: p.id,
      url: p.url,
      thumbnailUrl: p.thumbnailUrl,
      latitude: p.latitude,
      longitude: p.longitude,
      timestamp: p.timestamp,
      description: p.description
    }))

    await saveRouteRecord({
      title,
      activityType: currentActivity.value.type,
      activityIcon: currentActivity.value.icon,
      startTime: startTime.toISOString(),
      endTime: now.toISOString(),
      duration: recordTime.value,
      distance: distance.value,
      avgSpeed: avgSpeed.value,
      maxSpeed: maxSpeed.value,
      points: routePoints.value,
      photos: routePhotos,
      mapStyle: mapStyleType.value
    })

    uni.showToast({ title: '路线已保存', icon: 'success' })
    setTimeout(() => {
      resetRecord()
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存路线失败:', error)
    uni.showToast({ title: '保存失败，请重试', icon: 'none' })
  } finally {
    isSaving.value = false
  }
}

const discardRecord = () => {
  showStopConfirm.value = false
  resetRecord()
}

const resetRecord = () => {
  isRecording.value = false
  isPaused.value = false
  recordTime.value = 0
  distance.value = 0
  speed.value = 0
  maxSpeed.value = 0
  routePoints.value = []
  markers.value = []
  polyline.value = []
  photos.value = []
  lastPointTime.value = null
  routeTitle.value = ''

  if (timer) {
    clearInterval(timer)
    timer = null
  }

  stopLocationWatch()
}

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
  const nextPoint: RoutePoint = {
    latitude: res.latitude,
    longitude: res.longitude,
    timestamp: Date.now(),
    speed: typeof res.speed === 'number' ? res.speed * 3.6 : 0
  }
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
    if (speed.value > maxSpeed.value) {
      maxSpeed.value = speed.value
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
    points: routePoints.value.map(p => ({ latitude: p.latitude, longitude: p.longitude })),
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

const takePhoto = () => {
  uni.chooseImage({
    count: 1,
    sourceType: ['camera', 'album'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0]
      const currentLat = latitude.value
      const currentLng = longitude.value
      const now = Date.now()

      const newPhoto: LocalPhoto = {
        id: now,
        url: tempFilePath,
        thumbnailUrl: tempFilePath,
        latitude: currentLat,
        longitude: currentLng,
        timestamp: now
      }
      photos.value.push(newPhoto)

      uploadRoutePhoto({
        file: tempFilePath,
        latitude: currentLat,
        longitude: currentLng,
        timestamp: now
      }).catch(err => {
        console.warn('照片上传失败，已本地保存:', err)
      })

      uni.showToast({ title: '照片已添加', icon: 'success' })
    }
  })
}

const previewPhoto = (index: number) => {
  const urls = photos.value.map(p => p.url)
  uni.previewImage({
    current: urls[index],
    urls
  })
}

const goToHistory = () => {
  uni.navigateTo({ url: '/pages/route/list' })
}

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

const loadMapSettings = async () => {
  try {
    const settings = await getUserSettings()
    if (settings?.mapSettings) {
      mapScale.value = settings.mapSettings.defaultZoom || 16
      showTraffic.value = settings.mapSettings.showTraffic || false
      if (settings.mapSettings.mapStyle === 'dark') {
        mapStyleType.value = 'normal'
      } else {
        mapStyleType.value = settings.mapSettings.mapStyle || 'normal'
      }
    }
  } catch (e) {
    console.warn('加载地图设置失败:', e)
  }
}

onMounted(() => {
  locateMe()
  loadMapSettings()
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

.map-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.record-map {
  width: 100%;
  height: 100%;
}

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

.photo-count-btn {
  position: relative;
}

.photo-count-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  min-width: 32rpx;
  height: 32rpx;
  background: #FF3B30;
  color: #FFFFFF;
  font-size: 20rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}

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

.history-btn {
  position: absolute;
  top: calc(44px + env(safe-area-inset-top));
  right: 24rpx;
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

.control-hint {
  text-align: center;
  font-size: 24rpx;
  color: $text-secondary;
}

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
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600rpx;
  background: $card-bg;
  border-radius: 24rpx;
  padding: 40rpx;
}

.confirm-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
  text-align: center;
  margin-bottom: 32rpx;
}

.confirm-data {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
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

.confirm-input {
  margin-top: 24rpx;
}

.route-title-input {
  width: 100%;
  height: 80rpx;
  background: $bg-color;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.confirm-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 32rpx;
}

.confirm-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 600;

  &.cancel {
    background: $bg-color;
    color: $text-primary;
  }

  &.save {
    background: #007AFF;
    color: #FFFFFF;
  }

  &.discard {
    background: #FFEBEE;
    color: #FF3B30;
  }
}

.style-popup {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

.style-popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;
}

.style-popup-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $text-primary;
}

.style-popup-close {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.style-list {
  display: flex;
  gap: 20rpx;
  margin-bottom: 32rpx;
}

.style-item {
  flex: 1;
  padding: 24rpx;
  border-radius: 20rpx;
  background: $bg-color;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  border: 3rpx solid transparent;
  transition: all 0.2s ease;

  &.active {
    border-color: #007AFF;
    background: #E3F2FD;
  }
}

.style-preview {
  width: 100rpx;
  height: 80rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.normal {
    background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  }

  &.satellite {
    background: linear-gradient(135deg, #1B5E20, #2E7D32);
  }
}

.style-preview-icon {
  font-size: 40rpx;
}

.style-info {
  text-align: center;
}

.style-name {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: $text-primary;
}

.style-desc {
  display: block;
  font-size: 22rpx;
  color: $text-secondary;
}

.style-check {
  width: 36rpx;
  height: 36rpx;
  background: #007AFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-icon {
  font-size: 22rpx;
  color: #FFFFFF;
  font-weight: 700;
}

.style-option-row {
  border-top: 1rpx solid #E5E5EA;
  padding-top: 24rpx;
}

.style-option-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.style-option-label {
  font-size: 28rpx;
  color: $text-primary;
}

.photo-popup {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  max-height: 70vh;
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

.photo-popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.photo-popup-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $text-primary;
}

.photo-popup-close {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.photo-scroll {
  max-height: 50vh;
}

.photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.photo-item {
  width: calc(33.33% - 12rpx);
  aspect-ratio: 1;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}

.photo-image {
  width: 100%;
  height: 100%;
}

.photo-location {
  position: absolute;
  bottom: 8rpx;
  right: 8rpx;
  width: 36rpx;
  height: 36rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.photo-empty {
  padding: 80rpx 0;
  text-align: center;
}

.photo-empty-text {
  font-size: 28rpx;
  color: $text-secondary;
}
</style>
