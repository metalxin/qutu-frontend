<template>
  <view class="route-detail-page">
    <view class="map-container">
      <map
        id="routeMap"
        class="route-map"
        :latitude="centerLat"
        :longitude="centerLng"
        :markers="mapMarkers"
        :polyline="mapPolyline"
        :scale="mapScale"
        :enable-3D="true"
        :show-compass="true"
        :enable-satellite="mapStyleType === 'satellite'"
        :enable-traffic="showTraffic"
      />

      <view class="back-btn" @click="goBack">
        <SFIcon name="back" :size="36" color="#1D1D1F" />
      </view>

      <view class="map-style-btn" @click="showMapStylePicker = true">
        <SFIcon name="layers" :size="32" color="#1D1D1F" />
      </view>
    </view>

    <view class="detail-panel">
      <view class="panel-handle" @click="togglePanel">
        <view class="handle-bar"></view>
      </view>

      <scroll-view class="panel-content" scroll-y :show-scrollbar="false">
        <view class="detail-header">
          <view class="header-left">
            <text class="detail-icon">{{ routeDetail?.activityIcon || '🚶' }}</text>
            <view class="header-info">
              <text class="detail-title">{{ routeDetail?.title || '路线详情' }}</text>
              <text class="detail-date">{{ routeDetail ? formatDate(routeDetail.startTime) : '' }}</text>
            </view>
          </view>
        </view>

        <view class="detail-stats" v-if="routeDetail">
          <view class="detail-stat">
            <text class="stat-value">{{ formatDuration(routeDetail.duration) }}</text>
            <text class="stat-label">时长</text>
          </view>
          <view class="stat-divider"></view>
          <view class="detail-stat">
            <text class="stat-value">{{ formatDistance(routeDetail.distance) }}</text>
            <text class="stat-label">距离</text>
          </view>
          <view class="stat-divider"></view>
          <view class="detail-stat">
            <text class="stat-value">{{ routeDetail.avgSpeed.toFixed(1) }}</text>
            <text class="stat-label">均速km/h</text>
          </view>
          <view class="stat-divider"></view>
          <view class="detail-stat">
            <text class="stat-value">{{ routeDetail.maxSpeed.toFixed(1) }}</text>
            <text class="stat-label">最高km/h</text>
          </view>
        </view>

        <view class="photos-section" v-if="routeDetail && routeDetail.photos.length > 0">
          <view class="section-header">
            <text class="section-title">路线照片</text>
            <text class="section-count">{{ routeDetail.photos.length }}张</text>
          </view>
          <scroll-view class="photos-scroll" scroll-x :show-scrollbar="false">
            <view class="photos-list">
              <view
                class="photo-card"
                v-for="(photo, index) in routeDetail.photos"
                :key="photo.id"
                @click="previewPhoto(index)"
              >
                <image :src="photo.thumbnailUrl || photo.url" mode="aspectFill" class="photo-img" />
                <view class="photo-loc" v-if="photo.description">
                  <SFIcon name="location" :size="16" color="#FFFFFF" />
                  <text class="photo-loc-text">{{ photo.description }}</text>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>

        <view class="points-section" v-if="routeDetail">
          <view class="section-header">
            <text class="section-title">路线轨迹</text>
            <text class="section-count">{{ routeDetail.pointCount }}个点</text>
          </view>
          <view class="points-info">
            <view class="point-item">
              <view class="point-dot start"></view>
              <view class="point-detail">
                <text class="point-label">起点</text>
                <text class="point-coord">{{ formatCoord(routeDetail.startLat) }}, {{ formatCoord(routeDetail.startLng) }}</text>
              </view>
            </view>
            <view class="point-line"></view>
            <view class="point-item">
              <view class="point-dot end"></view>
              <view class="point-detail">
                <text class="point-label">终点</text>
                <text class="point-coord">{{ formatCoord(routeDetail.endLat) }}, {{ formatCoord(routeDetail.endLng) }}</text>
              </view>
            </view>
          </view>
        </view>

        <view class="bottom-space"></view>
      </scroll-view>
    </view>

    <view class="popup-overlay" :class="{ 'show': showMapStylePicker }" @click="showMapStylePicker = false">
      <view class="style-popup" @click.stop>
        <view class="style-popup-header">
          <text class="style-popup-title">地图样式</text>
          <view class="style-popup-close" @click="showMapStylePicker = false">
            <SFIcon name="close" :size="32" color="#86868B" />
          </view>
        </view>
        <view class="style-list">
          <view
            class="style-item"
            v-for="style in mapStyleOptions"
            :key="style.value"
            :class="{ active: mapStyleType === style.value }"
            @click="selectMapStyle(style.value)"
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
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getRouteDetail } from '@/api'
import type { RouteDetail } from '@/api'

const routeDetail = ref<RouteDetail | null>(null)
const loading = ref(true)
const showMapStylePicker = ref(false)
const mapStyleType = ref('normal')
const showTraffic = ref(false)
const mapScale = ref(14)

const mapStyleOptions = [
  { value: 'normal', label: '标准地图', desc: '默认地图样式', icon: '🗺️' },
  { value: 'satellite', label: '卫星地图', desc: '卫星影像视图', icon: '🛰️' }
]

const centerLat = computed(() => {
  if (!routeDetail.value) return 39.9042
  return (routeDetail.value.startLat + routeDetail.value.endLat) / 2
})

const centerLng = computed(() => {
  if (!routeDetail.value) return 116.4074
  return (routeDetail.value.startLng + routeDetail.value.endLng) / 2
})

const mapMarkers = computed(() => {
  if (!routeDetail.value) return []
  const markers: any[] = []

  markers.push({
    id: 0,
    latitude: routeDetail.value.startLat,
    longitude: routeDetail.value.startLng,
    iconPath: '/static/marker-start.png',
    width: 30,
    height: 30,
    callout: { content: '起点', display: 'ALWAYS' }
  })

  markers.push({
    id: 1,
    latitude: routeDetail.value.endLat,
    longitude: routeDetail.value.endLng,
    iconPath: '/static/marker-end.png',
    width: 30,
    height: 30,
    callout: { content: '终点', display: 'ALWAYS' }
  })

  routeDetail.value.photos.forEach((photo, index) => {
    markers.push({
      id: 100 + index,
      latitude: photo.latitude,
      longitude: photo.longitude,
      iconPath: '/static/marker-photo.png',
      width: 32,
      height: 32,
      callout: {
        content: photo.description || '📷',
        display: 'BYCLICK',
        fontSize: 14,
        borderRadius: 8,
        bgColor: '#FFFFFF',
        padding: 6
      }
    })
  })

  return markers
})

const mapPolyline = computed(() => {
  if (!routeDetail.value || routeDetail.value.points.length === 0) return []
  return [{
    points: routeDetail.value.points.map(p => ({ latitude: p.latitude, longitude: p.longitude })),
    color: '#FF6D00',
    width: 6,
    arrowLine: true
  }]
})

const formatDate = (dateStr: string) => {
  const d = new Date(dateStr)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const formatDuration = (seconds: number) => {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) return `${h}h${m}m`
  return `${m}min`
}

const formatDistance = (meters: number) => {
  if (meters < 1000) return `${Math.round(meters)}m`
  return `${(meters / 1000).toFixed(1)}km`
}

const formatCoord = (val: number) => {
  return val.toFixed(4)
}

const selectMapStyle = (value: string) => {
  mapStyleType.value = value
  showMapStylePicker.value = false
}

const togglePanel = () => {}

const previewPhoto = (index: number) => {
  if (!routeDetail.value) return
  const urls = routeDetail.value.photos.map(p => p.url)
  uni.previewImage({
    current: urls[index],
    urls
  })
}

const goBack = () => {
  uni.navigateBack()
}

const loadDetail = async (id: number) => {
  loading.value = true
  try {
    const res = await getRouteDetail(id)
    routeDetail.value = res
    if (res.mapStyle) {
      mapStyleType.value = res.mapStyle
    }
  } catch (error) {
    console.error('加载路线详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const id = parseInt(currentPage?.options?.id || '0')
  if (id > 0) {
    loadDetail(id)
  }
})
</script>

<style lang="scss" scoped>
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;

.route-detail-page {
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 55vh;
  position: relative;
}

.route-map {
  width: 100%;
  height: 100%;
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
}

.map-style-btn {
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
}

.detail-panel {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 50vh;
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.panel-handle {
  display: flex;
  justify-content: center;
  padding: 16rpx 0 8rpx;
}

.handle-bar {
  width: 80rpx;
  height: 8rpx;
  background: #D1D1D6;
  border-radius: 4rpx;
}

.panel-content {
  flex: 1;
  padding: 0 32rpx;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex: 1;
  min-width: 0;
}

.detail-icon {
  font-size: 48rpx;
}

.header-info {
  flex: 1;
  min-width: 0;
}

.detail-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: $text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-date {
  display: block;
  font-size: 24rpx;
  color: $text-secondary;
  margin-top: 4rpx;
}

.detail-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 24rpx;
  background: $bg-color;
  border-radius: 16rpx;
  margin-bottom: 32rpx;
}

.detail-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.stat-value {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-primary;
}

.stat-label {
  font-size: 22rpx;
  color: $text-secondary;
}

.stat-divider {
  width: 1rpx;
  height: 50rpx;
  background: #D1D1D6;
}

.photos-section {
  margin-bottom: 32rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.section-count {
  font-size: 24rpx;
  color: $text-secondary;
}

.photos-scroll {
  white-space: nowrap;
}

.photos-list {
  display: inline-flex;
  gap: 16rpx;
}

.photo-card {
  width: 240rpx;
  height: 180rpx;
  border-radius: 16rpx;
  overflow: hidden;
  position: relative;
  display: inline-block;
}

.photo-img {
  width: 100%;
  height: 100%;
}

.photo-loc {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8rpx 12rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.photo-loc-text {
  font-size: 20rpx;
  color: #FFFFFF;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.points-section {
  margin-bottom: 32rpx;
}

.points-info {
  padding: 24rpx;
  background: $bg-color;
  border-radius: 16rpx;
}

.point-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.point-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  flex-shrink: 0;

  &.start {
    background: #4CAF50;
  }

  &.end {
    background: #FF3B30;
  }
}

.point-detail {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.point-label {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-primary;
}

.point-coord {
  font-size: 22rpx;
  color: $text-secondary;
  font-family: monospace;
}

.point-line {
  width: 2rpx;
  height: 32rpx;
  background: #D1D1D6;
  margin-left: 11rpx;
}

.bottom-space {
  height: 60rpx;
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;

  &.show {
    opacity: 1;
    visibility: visible;
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
</style>
