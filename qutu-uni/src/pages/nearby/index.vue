<template>
  <view class="page">
    <!-- 顶部信息栏 -->
    <view class="top-bar" :style="topBarStyle">
      <view class="city-info" @click="showCityPicker">
        <text class="city-name">{{ currentCityName }}</text>
        <text class="city-arrow">▼</text>
        <view class="weather-info"><text class="weather-text">{{ weather }}</text></view>
      </view>
      <!-- #ifdef MP-WEIXIN -->
      <view class="top-mp-right" :style="{ height: menuButtonHeight + 'px', top: menuButtonTop + 'px', right: (windowWidth - menuButtonLeft + 8) + 'px' }">
        <view class="avatar-btn" @click="goToHome">
          <image v-if="displayAvatar" :src="displayAvatar" class="header-avatar" mode="aspectFill" />
          <SFIcon v-else name="user" :size="36" color="#1D1D1F" />
        </view>
      </view>
      <!-- #endif -->
      <view class="top-right" :style="{ marginRight: menuButtonSpace + 'px' }">
        <!-- #ifndef MP-WEIXIN -->
        <view class="icon-btn" @click="goToHome">
          <view class="avatar-wrapper">
            <image v-if="displayAvatar" :src="displayAvatar" class="header-avatar" mode="aspectFill" />
            <SFIcon v-else name="user" :size="36" color="#1D1D1F" />
          </view>
        </view>
        <!-- #endif -->
      </view>
    </view>

    <!-- 分类筛选 -->
    <view class="filter-bar" :style="{ top: navBarHeight + 'px' }">
      <scroll-view class="filter-scroll" scroll-x :show-scrollbar="false">
        <view class="filter-wrapper">
          <view class="filter-item" v-for="cat in categories" :key="cat.id" :class="{ active: currentCategoryId === cat.id }" @click="selectCategory(cat.id)">
            <text class="filter-emoji">{{ cat.icon || '🔥' }}</text>
            <text class="filter-name">{{ cat.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 地图区域 -->
    <view class="map-container" :style="{ top: (navBarHeight + 80) + 'px' }">
      <map id="nearbyMap" class="nearby-map" :latitude="mapCenter.latitude" :longitude="mapCenter.longitude" :scale="mapScale" :markers="mapMarkers" :show-location="true" @markertap="onMarkerTap" />
      <cover-view class="map-controls">
        <cover-view class="control-btn" @click="showMapStylePicker = true">
          <cover-image class="control-icon" :src="mapStyleIconSrc" />
        </cover-view>
        <cover-view class="control-btn" @click="locateMe">
          <cover-image class="control-icon" :src="locateIconSrc" />
        </cover-view>
      </cover-view>
      <view class="loading-overlay" v-if="loading">
        <view class="loading-spinner"></view>
        <text class="loading-text">搜索附近...</text>
      </view>
      <view class="empty-overlay" v-if="!loading && allPois.length === 0 && hasLoaded">
        <text class="empty-icon">📍</text>
        <text class="empty-text">附近暂无地点</text>
        <text class="empty-hint">试试调整位置或切换分类</text>
      </view>
    </view>

    <!-- POI详情弹窗 -->
    <view class="poi-popup-overlay" v-if="showPoiPopup" @click="closePoiPopup">
      <view class="poi-popup" @click.stop>
        <view class="popup-handle"></view>
        <scroll-view class="popup-scroll" scroll-y :show-scrollbar="false">
          <view class="popup-header">
            <view class="popup-title-row">
              <text class="popup-title">{{ selectedPoi?.name }}</text>
              <view class="popup-close" @click="closePoiPopup"><SFIcon name="close" :size="36" color="#666" /></view>
            </view>
            <view class="popup-subtitle" v-if="selectedPoi?.subtitle"><text class="subtitle-text">{{ selectedPoi.subtitle }}</text></view>
            <view class="popup-meta">
              <text class="meta-distance" v-if="selectedPoi?.distance">距我直线 {{ selectedPoi.distance }}</text>
              <view class="meta-rating" v-if="selectedPoi?.rating">
                <text class="rating-score">{{ selectedPoi.rating }}分</text>
                <SFIcon name="star" :size="24" color="#FFB800" filled />
              </view>
              <text class="meta-category" v-if="selectedPoi?.categoryName">{{ selectedPoi.categoryName }}</text>
            </view>
          </view>
          <scroll-view class="popup-gallery" scroll-x :show-scrollbar="false" v-if="selectedPoi?.gallery?.length">
            <view class="gallery-list">
              <image class="gallery-image" v-for="(img, idx) in selectedPoi.gallery" :key="idx" :src="img" mode="aspectFill" @click="previewImage(idx)" />
            </view>
          </scroll-view>
          <view class="popup-description" v-if="selectedPoi?.description">
            <text class="desc-title">地点介绍</text>
            <text class="desc-text">{{ selectedPoi.description }}</text>
          </view>
          <view class="popup-info">
            <view class="info-item" v-if="selectedPoi?.address"><SFIcon name="location" :size="28" color="#86868B" /><text class="info-text">{{ selectedPoi.address }}</text></view>
            <view class="info-item" v-if="selectedPoi?.openTime"><SFIcon name="clock" :size="28" color="#86868B" /><text class="info-text">{{ selectedPoi.openTime }}</text></view>
            <view class="info-item" v-if="selectedPoi?.phone"><SFIcon name="phone" :size="28" color="#86868B" /><text class="info-text">{{ selectedPoi.phone }}</text></view>
            <view class="info-item" v-if="selectedPoi?.priceText"><SFIcon name="wallet" :size="28" color="#86868B" /><text class="info-text">{{ selectedPoi.priceText }}</text></view>
            <view class="info-item" v-if="selectedPoi?.bestSeason"><SFIcon name="clock" :size="28" color="#86868B" /><text class="info-text">最佳季节: {{ selectedPoi.bestSeason }}</text></view>
            <view class="info-item" v-if="selectedPoi?.suitableFor"><SFIcon name="user" :size="28" color="#86868B" /><text class="info-text">适合: {{ selectedPoi.suitableFor }}</text></view>
          </view>
          <view class="popup-tips" v-if="selectedPoi?.tips?.length">
            <text class="desc-title">旅行贴士</text>
            <view class="tip-item" v-for="(tip, idx) in selectedPoi.tips" :key="idx">
              <text class="tip-icon">{{ tip.icon }}</text>
              <view class="tip-content"><text class="tip-title">{{ tip.title }}</text><text class="tip-desc">{{ tip.desc }}</text></view>
            </view>
          </view>
        </scroll-view>
        <view class="popup-actions">
          <view class="action-btn" @click="addToItinerary"><SFIcon name="plus" :size="32" color="#007AFF" /><text class="action-text">添加行程</text></view>
          <view class="action-btn" @click="toggleFavorite"><SFIcon name="heart" :size="32" :color="isFavorited ? '#FF2D55' : '#1D1D1F'" :filled="isFavorited" /><text class="action-text">{{ isFavorited ? '已收藏' : '收藏' }}</text></view>
          <view class="action-btn" @click="checkIn"><SFIcon name="checkmark" :size="32" color="#34C759" /><text class="action-text">打卡</text></view>
          <view class="action-btn" @click="navigateToSpot"><SFIcon name="navigation" :size="32" color="#007AFF" /><text class="action-text">导航</text></view>
        </view>
      </view>
    </view>

    <!-- 底部TabBar -->
    <view class="tabbar">
      <view class="tabbar-item" @click="goToHome"><SFIcon name="home" :size="44" /><text class="tabbar-text">主页</text></view>
      <view class="tabbar-center"><view class="add-btn" @click="showAddMenuPopup = true"><SFIcon name="plus" :size="48" color="#FFFFFF" /></view></view>
      <view class="tabbar-item active"><SFIcon name="location" :size="44" /><text class="tabbar-text">附近</text></view>
    </view>

    <!-- 添加菜单弹窗 -->
    <view class="add-menu-mask" :class="{ show: showAddMenuPopup }" @click="showAddMenuPopup = false"></view>
    <view class="add-menu-popup" :class="{ show: showAddMenuPopup }">
      <view class="menu-options">
        <view class="menu-option dark animate-item item-1" :class="{ show: showAddMenuPopup }" @click="handleAddLocation"><text class="option-title">添加地点</text><view class="option-icon-wrapper"><SFIcon name="location" :size="40" color="#FFFFFF" /></view></view>
        <view class="menu-option light animate-item item-2" :class="{ show: showAddMenuPopup }" @click="handleRecordTrip"><view class="option-content"><text class="option-title">记录行程</text><text class="option-desc">记录你的出行路线和足迹</text></view><view class="option-icon-wrapper light"><SFIcon name="route" :size="40" color="#FF9500" /></view></view>
        <view class="menu-option light animate-item item-3" :class="{ show: showAddMenuPopup }" @click="handleTakePhoto"><view class="option-content"><text class="option-title">拍照打卡</text><text class="option-desc">拍照记录当前位置</text></view><view class="option-icon-wrapper light"><SFIcon name="camera" :size="40" color="#AF52DE" /></view></view>
      </view>
      <view class="menu-close animate-item item-4" :class="{ show: showAddMenuPopup }" @click="showAddMenuPopup = false"><SFIcon name="close" :size="40" color="#FFFFFF" /></view>
    </view>

    <!-- 城市选择弹窗 -->
    <view class="popup-mask" v-if="showCityPopup" @click="showCityPopup = false"></view>
    <view class="city-popup" :class="{ 'popup-show': showCityPopup }">
      <view class="city-popup-header"><text class="city-popup-title">选择城市</text><view class="city-popup-close" @click="showCityPopup = false"><text class="close-icon">×</text></view></view>
      <view class="popup-search"><text class="popup-search-icon">🔍</text><input class="popup-search-input" placeholder="搜索城市" v-model="citySearchText" /></view>
      <view class="current-location-section"><text class="section-label">当前定位</text><view class="location-city" @click="selectCity(currentCityName)"><SFIcon name="location" :size="28" color="#007AFF" /><text class="location-city-text">{{ currentCityName }}</text></view></view>
      <view class="hot-cities-section"><text class="section-label">热门城市</text>
        <view class="city-tags">
          <view class="city-tag" v-for="city in hotCities" :key="city" :class="{ selected: selectedCityTemp === city }" @click="selectCityTemp(city)"><text class="city-tag-text">{{ city }}</text></view>
        </view>
      </view>
      <view class="popup-footer"><view class="footer-cancel" @click="showCityPopup = false"><text class="cancel-text">取消</text></view><view class="footer-confirm" @click="confirmCitySelection"><text class="confirm-text">确认选择</text></view></view>
    </view>

    <canvas canvas-id="markerCanvas" class="marker-canvas" :style="{ width: markerCanvasSize + 'px', height: markerCanvasSize + 'px' }" />

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
import { onShow } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getNearbyCategories, getNearbyPois, getPoiDetail, favoritePoi, unfavoritePoi, checkinPoi } from '@/api'
import type { NearbyCategory, NearbyPoi, NearbyPoiDetail } from '@/api'

interface POI extends NearbyPoi {
  gallery?: string[]
  tips?: Array<{ icon: string; title: string; desc: string }>
  phone?: string; openTime?: string; bestSeason?: string; suitableFor?: string; visitMinutes?: number
}

const loading = ref(false)
const hasLoaded = ref(false)
const currentCityName = ref('合肥市')
const weather = ref('多云 2°-5° ☁')
const mapCenter = ref({ latitude: 31.8206, longitude: 117.2272 })
const mapScale = ref(12)
const categories = ref<NearbyCategory[]>([{ id: 0, name: '全部', icon: '🔥' }])
const currentCategoryId = ref<number>(0)
const allPois = ref<POI[]>([])
const selectedPoi = ref<POI | null>(null)
const showPoiPopup = ref(false)
const isFavorited = ref(false)
const favoriting = ref(false)
const showCityPopup = ref(false)
const citySearchText = ref('')
const selectedCityTemp = ref('')
const hotCities = ref(['北京','上海','广州','深圳','杭州','合肥','马鞍山','成都','重庆','西安','南京','武汉'])
const showAddMenuPopup = ref(false)
const showMapStylePicker = ref(false)
const mapStyleType = ref('normal')
const showTraffic = ref(false)

// 城市坐标映射
const cityCoords: Record<string, { lat: number; lng: number }> = {
  '合肥市': { lat: 31.8206, lng: 117.2272 },
  '马鞍山市': { lat: 31.6705, lng: 118.5068 },
  '杭州市': { lat: 30.2741, lng: 120.1551 },
  '南京市': { lat: 32.0603, lng: 118.7969 },
  '北京市': { lat: 39.9042, lng: 116.4074 },
  '上海市': { lat: 31.2304, lng: 121.4737 },
}

const statusBarHeight = ref(20); const menuButtonSpace = ref(0); const menuButtonLeft = ref(0)
const menuButtonTop = ref(0); const menuButtonHeight = ref(32); const windowWidth = ref(375); const navBarHeight = ref(88)

const topBarStyle = computed(() => ({ paddingTop: statusBarHeight.value + 'px', height: navBarHeight.value + 'px' }))
const displayAvatar = computed(() => { try { const t = uni.getStorageSync('token'); if (!t) return ''; const u = uni.getStorageSync('userInfo'); return u?.avatar || '' } catch { return '' } })

const locateIconSrc = computed(() => {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#1D1D1F" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="22" y1="12" x2="18" y2="12"/><line x1="6" y1="12" x2="2" y2="12"/><line x1="12" y1="6" x2="12" y2="2"/><line x1="12" y1="22" x2="12" y2="18"/></svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
})

const refreshIconSrc = computed(() => {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#1D1D1F" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
})

const mapStyleIconSrc = computed(() => {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#1D1D1F" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"/><line x1="8" y1="2" x2="8" y2="18"/><line x1="16" y1="6" x2="16" y2="22"/></svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
})

const mapStyleOptions = [
  { value: 'normal', label: '标准地图', desc: '默认地图样式', icon: '🗺️' },
  { value: 'satellite', label: '卫星地图', desc: '卫星影像视图', icon: '🛰️' }
]

const selectMapStyle = (value: string) => {
  mapStyleType.value = value
  showMapStylePicker.value = false
}

const mapMarkers = computed(() => allPois.value.map(poi => ({
  id: poi.id, latitude: poi.latitude, longitude: poi.longitude,
  iconPath: markerIconMap.value[poi.categoryType] || markerIconMap.value['place'] || '',
  width: 44, height: 52,
  callout: { content: poi.title, display: 'ALWAYS' as const, padding: 8, borderRadius: 8, bgColor: '#FFFFFF', fontSize: 12, color: '#1D1D1F' },
  anchor: { x: 0.5, y: 1 }
})))

const markerCanvasSize = ref(52)

const categoryMarkerConfig: Record<string, { color: string; bg: string; emoji: string; label: string }> = {
  food: { color: '#FF6B35', bg: '#FFF0EB', emoji: '🍴', label: '美食' },
  scenic: { color: '#4CAF50', bg: '#E8F5E9', emoji: '🏞️', label: '景点' },
  shopping: { color: '#9C27B0', bg: '#F3E5F5', emoji: '🛍️', label: '购物' },
  entertainment: { color: '#FF9800', bg: '#FFF3E0', emoji: '🎮', label: '娱乐' },
  hotel: { color: '#2196F3', bg: '#E3F2FD', emoji: '🏨', label: '住宿' },
  place: { color: '#8E8E93', bg: '#F2F2F7', emoji: '📍', label: '地点' }
}

const markerIconMap = ref<Record<string, string>>({})

const generateMarkerIcons = () => {
  const ctx = uni.createCanvasContext('markerCanvas')
  const size = markerCanvasSize.value
  const entries = Object.entries(categoryMarkerConfig)

  const drawOne = (index: number): Promise<void> => {
    return new Promise(resolve => {
      const cfg = entries[index]
      if (!cfg) { resolve(); return }
      const [key, config] = cfg
      const cx = size / 2
      const pinRadius = 18
      const pinBottom = size - 4

      ctx.clearRect(0, 0, size, size)

      ctx.setShadow(0, 2, 8, 'rgba(0,0,0,0.2)')
      ctx.beginPath()
      ctx.arc(cx, cx - 2, pinRadius, 0, Math.PI * 2)
      ctx.setFillStyle(config.color)
      ctx.fill()
      ctx.setShadow(0, 0, 0, 'rgba(0,0,0,0)')

      ctx.beginPath()
      ctx.moveTo(cx - 8, cx + pinRadius - 6)
      ctx.lineTo(cx, pinBottom)
      ctx.lineTo(cx + 8, cx + pinRadius - 6)
      ctx.closePath()
      ctx.setFillStyle(config.color)
      ctx.fill()

      ctx.beginPath()
      ctx.arc(cx, cx - 2, pinRadius - 3, 0, Math.PI * 2)
      ctx.setFillStyle('#FFFFFF')
      ctx.fill()

      ctx.setFontSize(18)
      ctx.setTextAlign('center')
      ctx.setTextBaseline('middle')
      ctx.fillText(config.emoji, cx, cx - 2)

      ctx.draw(false, () => {
        uni.canvasToTempFilePath({
          canvasId: 'markerCanvas',
          width: size,
          height: size,
          destWidth: size * 3,
          destHeight: size * 3,
          success: (res: any) => {
            markerIconMap.value[key] = res.tempFilePath
            resolve()
          },
          fail: () => {
            markerIconMap.value[key] = ''
            resolve()
          }
        })
      })
    })
  }

  const run = async () => {
    for (let i = 0; i < entries.length; i++) {
      await drawOne(i)
    }
  }
  run()
}

// ========== 数据加载 ==========
const loadCategories = async () => {
  try {
    const res = await getNearbyCategories()
    if (res?.length) categories.value = [{ id: 0, name: '全部', icon: '🔥' }, ...res]
  } catch { categories.value = [{ id: 0, name: '全部', icon: '🔥' },{ id: 1, name: '美食', icon: '🍴' },{ id: 2, name: '景点', icon: '🏞️' },{ id: 3, name: '购物', icon: '🛍️' },{ id: 4, name: '娱乐', icon: '🎮' },{ id: 5, name: '住宿', icon: '🏨' }] }
}

const loadPois = async () => {
  loading.value = true
  try {
    const params: any = { longitude: mapCenter.value.longitude, latitude: mapCenter.value.latitude, radius: 50, limit: 30, cityName: currentCityName.value.replace('市','') + '市' }
    if (currentCategoryId.value !== 0) params.categoryId = currentCategoryId.value
    const res = await getNearbyPois(params)
    allPois.value = res?.length ? res.map(p => ({ ...p })) : []
  } catch { allPois.value = [] }
  finally { loading.value = false; hasLoaded.value = true }
}

// ========== 交互 ==========
const selectCategory = (id: number) => { currentCategoryId.value = id; loadPois() }

const onMarkerTap = async (e: any) => {
  const markerId = e.detail?.markerId ?? e.markerId
  const poi = allPois.value.find(p => p.id === markerId)
  if (!poi) return
  uni.showLoading({ title: '加载详情...' })
  try {
    const detail = await getPoiDetail(poi.id)
    if (detail) {
      selectPoi({
        ...poi, description: detail.description || '', phone: detail.phone || '', openTime: detail.openTime || '',
        bestSeason: detail.bestSeason || '', suitableFor: detail.suitableFor || '', visitMinutes: detail.visitMinutes,
        tips: detail.tips || [], gallery: detail.gallery?.length ? detail.gallery : poi.images,
        isFavorite: detail.isFavorite ?? poi.isFavorite ?? false
      })
    } else { selectPoi(poi) }
  } catch { selectPoi(poi) }
  finally { uni.hideLoading() }
}

const selectPoi = (poi: POI) => { selectedPoi.value = poi; showPoiPopup.value = true; isFavorited.value = !!poi.isFavorite }
const closePoiPopup = () => { showPoiPopup.value = false; selectedPoi.value = null }
const previewImage = (index: number) => { if (selectedPoi.value?.gallery?.length) uni.previewImage({ urls: selectedPoi.value.gallery, current: index }) }

const requireLogin = (): boolean => {
  try { const t = uni.getStorageSync('token'); if (!t) { uni.showToast({ title: '请先登录', icon: 'none' }); setTimeout(() => uni.navigateTo({ url: '/pages/user/login' }), 500); return false } } catch { return false }
  return true
}

const addToItinerary = () => { if (!requireLogin()) return; uni.showToast({ title: '已添加到行程', icon: 'success' }); closePoiPopup() }

const toggleFavorite = async () => {
  if (!selectedPoi.value || !requireLogin()) return
  if (favoriting.value) return
  favoriting.value = true
  try {
    let ok = false
    if (isFavorited.value) { ok = await unfavoritePoi(selectedPoi.value.id); if (ok) { isFavorited.value = false; uni.showToast({ title: '已取消收藏', icon: 'success' }) } }
    else { ok = await favoritePoi(selectedPoi.value.id); if (ok) { isFavorited.value = true; uni.showToast({ title: '已收藏', icon: 'success' }) } }
    if (!ok) uni.showToast({ title: '操作失败', icon: 'none' })
  } catch { uni.showToast({ title: '网络异常', icon: 'none' }) }
  finally { favoriting.value = false }
}

const checkIn = async () => {
  if (!selectedPoi.value || !requireLogin()) return
  try {
    const ok = await checkinPoi(selectedPoi.value.id)
    if (ok) { uni.showToast({ title: '打卡成功！', icon: 'success' }); closePoiPopup() }
    else uni.showToast({ title: '打卡失败', icon: 'none' })
  } catch { uni.showToast({ title: '网络异常', icon: 'none' }) }
}

const navigateToSpot = () => {
  if (!selectedPoi.value) return
  uni.openLocation({ latitude: selectedPoi.value.latitude, longitude: selectedPoi.value.longitude, name: selectedPoi.value.name, address: selectedPoi.value.address || '', scale: 18, fail: () => uni.showToast({ title: '打开导航失败', icon: 'none' }) })
}

const ensureLocationAuth = async (): Promise<boolean> => {
  return new Promise(resolve => {
    uni.getSetting({ success: (s) => { if (s.authSetting?.['scope.userLocation']) { resolve(true); return }
      uni.authorize({ scope: 'scope.userLocation', success: () => resolve(true), fail: () => { uni.showModal({ title: '需要定位权限', content: '请开启定位权限以查看附近', success: (r) => { if (r.confirm) uni.openSetting({ success: (o) => resolve(!!o.authSetting?.['scope.userLocation']), fail: () => resolve(false) }); else resolve(false) } }) } })
    }, fail: () => resolve(false) })
  })
}

const locateMe = async () => {
  const ok = await ensureLocationAuth(); if (!ok) return
  uni.showLoading({ title: '定位中...' })
  uni.getLocation({ type: 'gcj02', success: (res) => { mapCenter.value = { latitude: res.latitude, longitude: res.longitude }; reverseGeocode(res.latitude, res.longitude); loadPois(); uni.hideLoading() }, fail: () => { uni.hideLoading(); uni.showToast({ title: '获取位置失败', icon: 'none' }) } })
}

const refreshPois = () => loadPois()
const reverseGeocode = (lat: number, lng: number) => {
  for (const [city, coords] of Object.entries(cityCoords)) {
    const d = Math.sqrt((lat - coords.lat) ** 2 + (lng - coords.lng) ** 2)
    if (d < 0.5) { currentCityName.value = city; return }
  }
}

const showCityPicker = () => { selectedCityTemp.value = currentCityName.value; showCityPopup.value = true }
const selectCityTemp = (city: string) => { selectedCityTemp.value = city }
const selectCity = (city: string) => { currentCityName.value = city.includes('市') ? city : city + '市'; showCityPopup.value = false; const coords = cityCoords[currentCityName.value]; if (coords) mapCenter.value = { latitude: coords.lat, longitude: coords.lng }; loadPois() }
const confirmCitySelection = () => { if (selectedCityTemp.value) selectCity(selectedCityTemp.value); showCityPopup.value = false }

const goToHome = () => uni.navigateBack({ delta: 1, fail: () => uni.redirectTo({ url: '/pages/index/index' }) })
const handleAddLocation = () => { showAddMenuPopup.value = false; uni.navigateTo({ url: '/pages/collect/edit' }) }
const handleRecordTrip = () => { showAddMenuPopup.value = false; uni.navigateTo({ url: '/pages/record/index' }) }
const handleTakePhoto = () => { showAddMenuPopup.value = false; uni.chooseImage({ count: 1, sourceType: ['camera'], success: () => uni.navigateTo({ url: '/pages/collect/camera' }) }) }

onMounted(() => {
  const sys = uni.getSystemInfoSync(); statusBarHeight.value = sys.statusBarHeight || 20; windowWidth.value = sys.windowWidth || 375
  // #ifdef MP-WEIXIN
  try { const mb = uni.getMenuButtonBoundingClientRect(); if (mb) { menuButtonLeft.value = mb.left; menuButtonTop.value = mb.top; menuButtonHeight.value = mb.height; menuButtonSpace.value = windowWidth.value - mb.left + 10; navBarHeight.value = (mb.top - statusBarHeight.value) * 2 + mb.height + statusBarHeight.value } } catch {}
  // #endif
  generateMarkerIcons(); loadCategories(); locateMe()
})

onShow(() => {
  loadPois()
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F5F5F7; position: relative; }
.top-bar { position: fixed; top: 0; left: 0; right: 0; z-index: 100; display: flex; justify-content: space-between; align-items: center; padding: 0 32rpx; padding-bottom: 16rpx; background: rgba(255,255,255,0.95); backdrop-filter: blur(20px); box-sizing: border-box; }
.city-info { display: flex; align-items: center; gap: 4rpx; }
.city-name { font-size: 32rpx; font-weight: 600; color: #1D1D1F; }
.city-arrow { font-size: 20rpx; color: #86868B; margin-left: 4rpx; }
.weather-info { margin-left: 12rpx; }
.weather-text { font-size: 24rpx; color: #86868B; }
.top-right { display: flex; align-items: center; gap: 16rpx; }
.top-mp-right { position: absolute; display: flex; align-items: center; justify-content: center; z-index: 101; }
.avatar-wrapper, .avatar-btn { position: relative; width: 64rpx; height: 64rpx; border-radius: 50%; overflow: hidden; background: #F5F5F7; display: flex; align-items: center; justify-content: center; }
.header-avatar { width: 100%; height: 100%; }
.icon-btn { width: 64rpx; height: 64rpx; display: flex; align-items: center; justify-content: center; background: #F5F5F7; border-radius: 50%; }
.map-container { position: fixed; left: 0; right: 0; bottom: 160rpx; }
.nearby-map { width: 100%; height: 100%; background: linear-gradient(135deg, #E8F5F3 0%, #D4EDE9 100%); }
.map-controls { position: absolute; right: 24rpx; bottom: 200rpx; display: flex; flex-direction: column; gap: 20rpx; z-index: 10; }
.control-btn { width: 88rpx; height: 88rpx; background: rgba(255, 255, 255, 0.92); backdrop-filter: blur(20px); border-radius: 44rpx; box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.12), 0 1rpx 4rpx rgba(0, 0, 0, 0.06); display: flex; align-items: center; justify-content: center; border: 1rpx solid rgba(0, 0, 0, 0.06); transition: all 0.2s ease; }
.control-icon { width: 44rpx; height: 44rpx; }
.loading-overlay { position: absolute; top: 50%; left: 50%; transform: translate(-50%,-50%); display: flex; flex-direction: column; align-items: center; gap: 16rpx; background: rgba(255,255,255,0.9); padding: 32rpx 48rpx; border-radius: 24rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.1); }
.loading-spinner { width: 48rpx; height: 48rpx; border: 4rpx solid #E5E5EA; border-top-color: #007AFF; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-text { font-size: 24rpx; color: #86868B; }
.empty-overlay { position: absolute; top: 50%; left: 50%; transform: translate(-50%,-50%); display: flex; flex-direction: column; align-items: center; gap: 12rpx; }
.empty-icon { font-size: 64rpx; }
.empty-text { font-size: 30rpx; color: #1D1D1F; font-weight: 500; }
.empty-hint { font-size: 24rpx; color: #86868B; }
.filter-bar { position: fixed; left: 0; right: 0; z-index: 50; background: #FFF; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05); }
.filter-scroll { width: 100%; white-space: nowrap; }
.filter-wrapper { display: flex; padding: 16rpx 24rpx; gap: 16rpx; }
.filter-item { display: flex; flex-direction: row; align-items: center; gap: 8rpx; padding: 12rpx 24rpx; border-radius: 32rpx; background: #F5F5F7; flex-shrink: 0; &.active { background: rgba(0,122,255,0.1); .filter-name { color: #007AFF; font-weight: 600; } } }
.filter-emoji { font-size: 28rpx; }
.filter-name { font-size: 26rpx; color: #666; white-space: nowrap; }
.tabbar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; justify-content: space-around; align-items: center; height: 160rpx; background: rgba(255,255,255,0.95); backdrop-filter: blur(20px); padding-bottom: env(safe-area-inset-bottom); border-top: 1rpx solid rgba(0,0,0,0.05); z-index: 100; }
.tabbar-item { display: flex; flex-direction: column; align-items: center; gap: 8rpx; padding: 16rpx 48rpx; color: #86868B; &.active { color: #007AFF; .tabbar-text { color: #007AFF; } } }
.tabbar-text { font-size: 22rpx; color: #86868B; font-weight: 500; }
.tabbar-center { margin-top: -60rpx; }
.add-btn { width: 112rpx; height: 112rpx; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); border-radius: 50%; box-shadow: 0 8rpx 32rpx rgba(255,107,107,0.4); }
.poi-popup-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 200; display: flex; align-items: flex-end; background: rgba(0,0,0,0.3); }
.poi-popup { width: 100%; max-height: 80vh; background: #FFF; border-radius: 40rpx 40rpx 0 0; padding: 20rpx 32rpx; padding-bottom: 0; animation: slideUp 0.3s ease-out; display: flex; flex-direction: column; }
.popup-scroll { flex: 1; max-height: calc(80vh - 160rpx); }
@keyframes slideUp { from { transform: translateY(100%); } to { transform: translateY(0); } }
.popup-handle { width: 80rpx; height: 8rpx; background: #E5E5E5; border-radius: 4rpx; margin: 0 auto 24rpx; }
.popup-header { margin-bottom: 24rpx; }
.popup-title-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12rpx; }
.popup-title { flex: 1; font-size: 36rpx; font-weight: 700; color: #1D1D1F; line-height: 1.3; }
.popup-subtitle { margin-bottom: 12rpx; }
.subtitle-text { font-size: 26rpx; color: #86868B; }
.popup-close { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; background: #F5F5F7; border-radius: 50%; }
.popup-meta { display: flex; align-items: center; gap: 16rpx; flex-wrap: wrap; }
.meta-distance { font-size: 24rpx; color: #666; padding: 6rpx 16rpx; background: #F5F5F7; border-radius: 8rpx; }
.meta-rating { display: flex; align-items: center; gap: 4rpx; padding: 6rpx 16rpx; background: #FFF8E7; border-radius: 8rpx; }
.rating-score { font-size: 24rpx; color: #FF9500; font-weight: 600; }
.meta-category { font-size: 24rpx; color: #666; padding: 6rpx 16rpx; background: #F5F5F7; border-radius: 8rpx; }
.popup-gallery { margin: 0 -32rpx; padding: 0 32rpx; margin-bottom: 24rpx; }
.gallery-list { display: flex; gap: 16rpx; padding-right: 32rpx; }
.gallery-image { width: 480rpx; height: 360rpx; border-radius: 24rpx; flex-shrink: 0; }
.popup-description { margin-bottom: 24rpx; }
.desc-title { display: block; font-size: 28rpx; font-weight: 600; color: #1D1D1F; margin-bottom: 12rpx; }
.desc-text { display: block; font-size: 26rpx; color: #666; line-height: 1.6; }
.popup-info { margin-bottom: 16rpx; }
.info-item { display: flex; align-items: flex-start; gap: 12rpx; margin-bottom: 16rpx; }
.info-text { flex: 1; font-size: 26rpx; color: #666; line-height: 1.5; }
.popup-tips { margin-bottom: 16rpx; }
.tip-item { display: flex; align-items: flex-start; gap: 12rpx; margin-bottom: 16rpx; padding: 16rpx; background: #F5F5F7; border-radius: 16rpx; }
.tip-icon { font-size: 32rpx; flex-shrink: 0; }
.tip-content { flex: 1; }
.tip-title { display: block; font-size: 26rpx; font-weight: 600; color: #1D1D1F; margin-bottom: 4rpx; }
.tip-desc { display: block; font-size: 24rpx; color: #666; line-height: 1.5; }
.popup-actions { display: flex; justify-content: space-around; align-items: center; padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); border-top: 1rpx solid #F0F0F0; background: #FFF; }
.action-btn { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.action-text { font-size: 22rpx; color: #666; }
.popup-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 998; }
.city-popup { position: fixed; left: 0; right: 0; bottom: 0; height: 70vh; background: #FFF; border-radius: 40rpx 40rpx 0 0; z-index: 999; display: flex; flex-direction: column; transform: translateY(100%); transition: transform 0.35s cubic-bezier(0.32,0.72,0,1); &.popup-show { transform: translateY(0); } }
.city-popup-header { display: flex; justify-content: space-between; align-items: center; padding: 40rpx 40rpx 24rpx; }
.city-popup-title { font-size: 36rpx; font-weight: 600; color: #1D1D1F; }
.city-popup-close { width: 56rpx; height: 56rpx; display: flex; align-items: center; justify-content: center; background: #F5F5F7; border-radius: 50%; }
.close-icon { font-size: 40rpx; color: #86868B; line-height: 1; }
.popup-search { display: flex; align-items: center; gap: 16rpx; margin: 0 40rpx 24rpx; padding: 24rpx 32rpx; background: #F5F5F7; border-radius: 20rpx; }
.popup-search-icon { font-size: 32rpx; color: #86868B; }
.popup-search-input { flex: 1; font-size: 30rpx; color: #1D1D1F; background: transparent; }
.current-location-section { padding: 0 40rpx 24rpx; }
.section-label { font-size: 26rpx; color: #86868B; display: block; margin-bottom: 16rpx; }
.location-city { display: inline-flex; align-items: center; gap: 8rpx; padding: 16rpx 24rpx; background: rgba(0,122,255,0.1); border-radius: 16rpx; border: 2rpx solid #007AFF; }
.location-city-text { font-size: 28rpx; color: #007AFF; font-weight: 500; }
.hot-cities-section { padding: 0 40rpx 24rpx; }
.city-tags { display: flex; flex-wrap: wrap; gap: 20rpx; }
.city-tag { padding: 20rpx 36rpx; background: #F5F5F7; border-radius: 16rpx; border: 2rpx solid transparent; &.selected { background: rgba(0,200,83,0.1); border-color: #00C853; } }
.city-tag-text { font-size: 28rpx; color: #1D1D1F; }
.city-tag.selected .city-tag-text { color: #00C853; font-weight: 500; }
.popup-footer { display: flex; align-items: center; gap: 24rpx; padding: 24rpx 40rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); background: #FFF; border-top: 1rpx solid #E5E5EA; }
.footer-cancel { padding: 24rpx 40rpx; background: #F5F5F7; border-radius: 100rpx; }
.cancel-text { font-size: 28rpx; color: #1D1D1F; }
.footer-confirm { flex: 1; display: flex; align-items: center; justify-content: center; padding: 28rpx 40rpx; background: #00C853; border-radius: 100rpx; }
.confirm-text { font-size: 30rpx; color: #FFF; font-weight: 600; }
.add-menu-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0); z-index: 998; pointer-events: none; transition: all 0.4s; &.show { background: rgba(0,0,0,0.3); backdrop-filter: blur(30px); pointer-events: auto; } }
.add-menu-popup { position: fixed; left: 0; right: 0; bottom: 0; z-index: 999; padding: 0 48rpx; padding-bottom: calc(60rpx + env(safe-area-inset-bottom)); pointer-events: none; &.show { pointer-events: auto; } }
.menu-options { display: flex; flex-direction: column; gap: 20rpx; }
.animate-item { opacity: 0; transform: translateY(80rpx) scale(0.9); transition: all 0.5s cubic-bezier(0.34,1.56,0.64,1); &.show { opacity: 1; transform: translateY(0) scale(1); } &.item-1 { transition-delay: 0s; } &.item-2 { transition-delay: 0.05s; } &.item-3 { transition-delay: 0.1s; } &.item-4 { transition-delay: 0.15s; } }
.menu-option { display: flex; align-items: center; justify-content: space-between; padding: 36rpx 40rpx; border-radius: 28rpx; &:active { transform: scale(0.98) !important; } &.dark { background: rgba(30,30,30,0.95); } &.light { background: rgba(255,255,255,0.95); } }
.option-content { flex: 1; display: flex; flex-direction: column; gap: 8rpx; }
.option-title { font-size: 32rpx; font-weight: 600; color: #1D1D1F; }
.menu-option.dark .option-title { color: #FFF; }
.option-desc { font-size: 24rpx; color: #86868B; }
.option-icon-wrapper { width: 56rpx; height: 56rpx; display: flex; align-items: center; justify-content: center; &.light { width: 48rpx; height: 48rpx; } }
.menu-close { width: 96rpx; height: 96rpx; margin: 48rpx auto 0; display: flex; align-items: center; justify-content: center; background: rgba(128,128,128,0.6); border-radius: 50%; }
.marker-canvas { position: fixed; left: -9999px; top: -9999px; }

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
  background: #FFF;
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
  color: #1D1D1F;
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
  color: #666;
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
  background: #F5F5F7;
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
  color: #1D1D1F;
}

.style-desc {
  display: block;
  font-size: 22rpx;
  color: #86868B;
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
  color: #1D1D1F;
}
</style>
