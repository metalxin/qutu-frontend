<template>
  <view class="route-detail-page">
    <!-- 加载状态 -->
    <view class="loading-wrap" v-if="loading">
      <text class="loading-text">加载中...</text>
    </view>

    <template v-else-if="routeDetail">
      <!-- 地图区域 -->
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

        <view class="back-btn" :style="backBtnStyle" @click="goBack">
          <text class="back-btn-text">‹</text>
        </view>

        <view class="map-style-btn" @click="showMapStylePicker = true">
          <text class="map-style-icon">☊</text>
        </view>

        <!-- 地图上的路线摘要 -->
        <view class="map-summary">
          <view class="summary-item">
            <text class="summary-value">{{ formatDuration(routeDetail.duration) }}</text>
            <text class="summary-label">时长</text>
          </view>
          <view class="summary-divider"></view>
          <view class="summary-item">
            <text class="summary-value">{{ formatDistance(routeDetail.distance) }}</text>
            <text class="summary-label">距离</text>
          </view>
          <view class="summary-divider"></view>
          <view class="summary-item">
            <text class="summary-value">{{ formatSpeed(routeDetail.avgSpeed) }}</text>
            <text class="summary-label">均速</text>
          </view>
        </view>
      </view>

      <!-- 详情面板 -->
      <scroll-view class="detail-panel" scroll-y :show-scrollbar="false">
        <!-- 标题区 -->
        <view class="panel-header">
          <view class="header-main">
            <text class="header-icon">{{ routeDetail.activityIcon || '🚶' }}</text>
            <view class="header-text">
              <text class="header-title">{{ routeDetail.title || '未命名路线' }}</text>
              <text class="header-sub">{{ routeDetail.activityType || '徒步' }} · {{ formatDate(routeDetail.startTime) }}</text>
            </view>
          </view>
        </view>

        <!-- 数据卡片 -->
        <view class="data-card">
          <view class="data-row">
            <view class="data-cell">
              <text class="data-value">{{ formatDuration(routeDetail.duration) }}</text>
              <text class="data-label">总时长</text>
            </view>
            <view class="data-cell">
              <text class="data-value">{{ formatDistance(routeDetail.distance) }}</text>
              <text class="data-label">总距离</text>
            </view>
            <view class="data-cell">
              <text class="data-value">{{ formatSpeed(routeDetail.avgSpeed) }}</text>
              <text class="data-label">平均速度</text>
            </view>
            <view class="data-cell">
              <text class="data-value">{{ formatSpeed(routeDetail.maxSpeed) }}</text>
              <text class="data-label">最高速度</text>
            </view>
          </view>
        </view>

        <!-- 时间信息 -->
        <view class="info-card">
          <view class="info-row">
            <text class="info-label">开始时间</text>
            <text class="info-value">{{ routeDetail.startTime || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">结束时间</text>
            <text class="info-value">{{ routeDetail.endTime || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">轨迹点数</text>
            <text class="info-value">{{ routeDetail.pointCount || (routeDetail.points ? routeDetail.points.length : 0) }}个</text>
          </view>
          <view class="info-row" v-if="routeDetail.photoCount > 0">
            <text class="info-label">照片数量</text>
            <text class="info-value">{{ routeDetail.photoCount }}张</text>
          </view>
          <view class="info-row" v-if="routeDetail.remark">
            <text class="info-label">备注</text>
            <text class="info-value">{{ routeDetail.remark }}</text>
          </view>
        </view>

        <!-- 路线轨迹起终点 -->
        <view class="info-card">
          <text class="card-title">路线轨迹</text>
          <view class="track-points">
            <view class="track-point">
              <view class="track-dot start"></view>
              <view class="track-info">
                <text class="track-label">起点</text>
                <text class="track-coord">{{ formatCoord(routeDetail.startLat) }}, {{ formatCoord(routeDetail.startLng) }}</text>
              </view>
            </view>
            <view class="track-line-wrap">
              <view class="track-line"></view>
              <text class="track-line-label" v-if="routeDetail.points && routeDetail.points.length > 0">
                {{ routeDetail.points.length }}个轨迹点
              </text>
            </view>
            <view class="track-point">
              <view class="track-dot end"></view>
              <view class="track-info">
                <text class="track-label">终点</text>
                <text class="track-coord">{{ formatCoord(routeDetail.endLat) }}, {{ formatCoord(routeDetail.endLng) }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 照片区域 -->
        <view class="info-card" v-if="routeDetail.photos && routeDetail.photos.length > 0">
          <text class="card-title">路线照片 ({{ routeDetail.photos.length }})</text>
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
                  <text class="photo-loc-text">{{ photo.description }}</text>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>

        <!-- 创建时间 -->
        <view class="info-card">
          <view class="info-row last">
            <text class="info-label">记录时间</text>
            <text class="info-value">{{ routeDetail.createTime || '-' }}</text>
          </view>
        </view>

        <view class="bottom-space"></view>
      </scroll-view>
    </template>

    <!-- 加载失败 -->
    <view class="error-wrap" v-else>
      <text class="error-text">路线数据加载失败</text>
      <view class="error-btn" @click="retryLoad">
        <text class="error-btn-text">重试</text>
      </view>
    </view>

    <!-- 地图样式弹窗 -->
    <view class="popup-mask" v-if="showMapStylePicker" @click="showMapStylePicker = false">
      <view class="style-popup" @click.stop>
        <view class="style-popup-header">
          <text class="style-popup-title">地图样式</text>
          <view class="style-popup-close" @click="showMapStylePicker = false">
            <text class="close-icon">✕</text>
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
import { getRouteDetail } from '@/api'
import type { RouteDetail } from '@/api'

const routeDetail = ref<RouteDetail | null>(null)
const loading = ref(true)
const showMapStylePicker = ref(false)
const mapStyleType = ref('normal')
const showTraffic = ref(false)
const mapScale = ref(14)

// 胶囊按钮位置（微信小程序）
const menuButtonTop = ref(0)
const menuButtonHeight = ref(32)
const statusBarHeight = ref(20)

// 返回按钮动态样式，与胶囊按钮垂直对齐
const backBtnStyle = computed(() => {
  // #ifdef MP-WEIXIN
  if (menuButtonTop.value > 0) {
    const top = menuButtonTop.value
    return { top: top + 'px' }
  }
  // #endif
  return { top: '48rpx' }
})

const mapStyleOptions = [
  { value: 'normal', label: '标准地图', desc: '默认地图样式', icon: '🗺️' },
  { value: 'satellite', label: '卫星地图', desc: '卫星影像视图', icon: '🛰️' }
]

const centerLat = computed(() => {
  if (!routeDetail.value) return 39.9042
  const pts = routeDetail.value.points
  if (pts && pts.length > 0) {
    const lats = pts.map(p => p.latitude).filter(v => v)
    return lats.length ? lats.reduce((a, b) => a + b, 0) / lats.length : 39.9042
  }
  const start = routeDetail.value.startLat || 0
  const end = routeDetail.value.endLat || 0
  if (!start && !end) return 39.9042
  return (start + end) / 2
})

const centerLng = computed(() => {
  if (!routeDetail.value) return 116.4074
  const pts = routeDetail.value.points
  if (pts && pts.length > 0) {
    const lngs = pts.map(p => p.longitude).filter(v => v)
    return lngs.length ? lngs.reduce((a, b) => a + b, 0) / lngs.length : 116.4074
  }
  const start = routeDetail.value.startLng || 0
  const end = routeDetail.value.endLng || 0
  if (!start && !end) return 116.4074
  return (start + end) / 2
})

const mapMarkers = computed(() => {
  if (!routeDetail.value) return []
  const markers: any[] = []

  // 起点
  if (routeDetail.value.startLat && routeDetail.value.startLng) {
    markers.push({
      id: 0,
      latitude: routeDetail.value.startLat,
      longitude: routeDetail.value.startLng,
      width: 0,
      height: 0,
      callout: {
        content: '🟢 起点',
        display: 'ALWAYS',
        color: '#333333',
        fontSize: 14,
        borderRadius: 8,
        bgColor: '#FFFFFF',
        padding: 8,
        textAlign: 'center'
      }
    })
  }

  // 终点
  if (routeDetail.value.endLat && routeDetail.value.endLng) {
    markers.push({
      id: 1,
      latitude: routeDetail.value.endLat,
      longitude: routeDetail.value.endLng,
      width: 0,
      height: 0,
      callout: {
        content: '🔴 终点',
        display: 'ALWAYS',
        color: '#333333',
        fontSize: 14,
        borderRadius: 8,
        bgColor: '#FFFFFF',
        padding: 8,
        textAlign: 'center'
      }
    })
  }

  // 照片标记
  const photos = routeDetail.value.photos || []
  photos.forEach((photo, index) => {
    if (photo.latitude && photo.longitude) {
      markers.push({
        id: 100 + index,
        latitude: photo.latitude,
        longitude: photo.longitude,
        width: 0,
        height: 0,
        callout: {
          content: photo.description || '📷',
          display: 'BYCLICK',
          color: '#333333',
          fontSize: 13,
          borderRadius: 8,
          bgColor: '#FFFFFF',
          padding: 6
        }
      })
    }
  })

  return markers
})

const mapPolyline = computed(() => {
  if (!routeDetail.value) return []
  const pts = routeDetail.value.points
  if (!pts || pts.length === 0) return []
  return [{
    points: pts.map(p => ({ latitude: p.latitude, longitude: p.longitude })),
    color: '#FF6D00',
    width: 6,
    arrowLine: true,
    borderColor: '#E65100',
    borderWidth: 2
  }]
})

const formatDate = (dateStr?: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const formatDuration = (seconds?: number) => {
  if (!seconds && seconds !== 0) return '0min'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) return `${h}h${m}m${s}s`
  if (m > 0) return `${m}m${s}s`
  return `${s}s`
}

const formatDistance = (meters?: number) => {
  if (!meters && meters !== 0) return '0m'
  if (meters < 1000) return `${Math.round(meters)}m`
  return `${(meters / 1000).toFixed(2)}km`
}

const formatSpeed = (speedMs?: number) => {
  if (!speedMs && speedMs !== 0) return '0'
  return `${(speedMs * 3.6).toFixed(1)}km/h`
}

const formatCoord = (val?: number) => {
  if (!val && val !== 0) return '-'
  return val.toFixed(6)
}

const selectMapStyle = (value: string) => {
  mapStyleType.value = value
  showMapStylePicker.value = false
}

const previewPhoto = (index: number) => {
  if (!routeDetail.value || !routeDetail.value.photos) return
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
    if (res) {
      res.photos = res.photos || []
      res.points = res.points || []
    }
    routeDetail.value = res
    if (res?.mapStyle) {
      mapStyleType.value = res.mapStyle
    }
    // 自动调整缩放级别
    if (res?.points && res.points.length > 0) {
      mapScale.value = res.points.length > 50 ? 12 : 14
    }
  } catch (error) {
    console.error('加载路线详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const retryLoad = () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const id = parseInt(currentPage?.options?.id || '0')
  if (id > 0) {
    loadDetail(id)
  }
}

onMounted(() => {
  // 获取系统信息
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonTop.value = menuButton.top
      menuButtonHeight.value = menuButton.height
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const id = parseInt(currentPage?.options?.id || '0')
  if (id > 0) {
    loadDetail(id)
  } else {
    loading.value = false
    uni.showToast({ title: '路线ID无效', icon: 'none' })
  }
})
</script>

<style lang="scss" scoped>
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$accent: #FF6D00;

.route-detail-page {
  width: 100%;
  min-height: 100vh;
  background: $bg-color;
}

.loading-wrap,
.error-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 24rpx;
}

.loading-text {
  font-size: 28rpx;
  color: $text-secondary;
}

.error-text {
  font-size: 28rpx;
  color: $text-secondary;
}

.error-btn {
  padding: 16rpx 48rpx;
  background: $accent;
  border-radius: 32rpx;
}

.error-btn-text {
  font-size: 26rpx;
  color: #FFFFFF;
  font-weight: 500;
}

/* 地图区域 */
.map-container {
  width: 100%;
  height: 480rpx;
  position: relative;
}

.route-map {
  width: 100%;
  height: 100%;
}

.back-btn {
  position: absolute;
  left: 24rpx;
  width: 72rpx;
  height: 72rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.12);
  z-index: 10;
}

.back-btn-text {
  font-size: 44rpx;
  color: $text-primary;
  line-height: 1;
  margin-top: -4rpx;
}

.map-style-btn {
  position: absolute;
  top: calc(48rpx + env(safe-area-inset-top));
  right: 24rpx;
  width: 72rpx;
  height: 72rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.12);
  z-index: 10;
}

.map-style-icon {
  font-size: 32rpx;
  color: $text-primary;
}

/* 地图上的摘要栏 */
.map-summary {
  position: absolute;
  bottom: 24rpx;
  left: 24rpx;
  right: 24rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16rpx;
  padding: 16rpx 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-around;
  z-index: 10;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rpx;
}

.summary-value {
  font-size: 28rpx;
  font-weight: 700;
  color: $text-primary;
}

.summary-label {
  font-size: 20rpx;
  color: $text-secondary;
}

.summary-divider {
  width: 1rpx;
  height: 40rpx;
  background: #E5E5EA;
}

/* 详情面板 */
.detail-panel {
  height: calc(100vh - 480rpx);
  padding: 0 32rpx;
  box-sizing: border-box;
}

.panel-header {
  padding: 32rpx 0 24rpx;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.header-icon {
  font-size: 52rpx;
}

.header-text {
  flex: 1;
  min-width: 0;
}

.header-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-sub {
  display: block;
  font-size: 24rpx;
  color: $text-secondary;
  margin-top: 4rpx;
}

/* 数据卡片 */
.data-card {
  background: $card-bg;
  border-radius: 20rpx;
  padding: 28rpx 16rpx;
  margin-bottom: 16rpx;
}

.data-row {
  display: flex;
  align-items: center;
}

.data-cell {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.data-value {
  font-size: 28rpx;
  font-weight: 700;
  color: $accent;
}

.data-label {
  font-size: 22rpx;
  color: $text-secondary;
}

/* 信息卡片 */
.info-card {
  background: $card-bg;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.card-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #F2F2F7;

  &.last {
    border-bottom: none;
  }
}

.info-label {
  font-size: 26rpx;
  color: $text-secondary;
}

.info-value {
  font-size: 26rpx;
  color: $text-primary;
  font-weight: 500;
  max-width: 60%;
  text-align: right;
}

/* 轨迹起终点 */
.track-points {
  padding: 0 8rpx;
}

.track-point {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.track-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  flex-shrink: 0;

  &.start {
    background: #4CAF50;
    box-shadow: 0 0 0 6rpx rgba(76, 175, 80, 0.2);
  }

  &.end {
    background: #FF3B30;
    box-shadow: 0 0 0 6rpx rgba(255, 59, 48, 0.2);
  }
}

.track-info {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.track-label {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-primary;
}

.track-coord {
  font-size: 22rpx;
  color: $text-secondary;
  font-family: 'SF Mono', 'Menlo', monospace;
}

.track-line-wrap {
  display: flex;
  align-items: center;
  margin-left: 11rpx;
  padding: 8rpx 0;
  gap: 12rpx;
}

.track-line {
  width: 3rpx;
  height: 40rpx;
  background: linear-gradient(to bottom, #4CAF50, $accent);
  border-radius: 2rpx;
}

.track-line-label {
  font-size: 20rpx;
  color: $accent;
  font-weight: 500;
  background: #FFF3E0;
  padding: 2rpx 12rpx;
  border-radius: 8rpx;
}

/* 照片区域 */
.photos-scroll {
  white-space: nowrap;
  margin: 0 -8rpx;
}

.photos-list {
  display: inline-flex;
  gap: 12rpx;
  padding: 0 8rpx;
}

.photo-card {
  width: 200rpx;
  height: 150rpx;
  border-radius: 12rpx;
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
  padding: 6rpx 10rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
}

.photo-loc-text {
  font-size: 18rpx;
  color: #FFFFFF;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bottom-space {
  height: 80rpx;
}

/* 弹窗 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
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

.close-icon {
  font-size: 28rpx;
  color: $text-secondary;
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
