<template>
  <view class="collect-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <view class="nav-right" :style="navRightStyle" @click="goToSettings">
        <view class="settings-icon">
          <text class="icon-refresh">↻</text>
        </view>
      </view>
    </view>

    <!-- 标签页切换 -->
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'collect' }"
        @click="currentTab = 'collect'"
      >
        <text class="tab-text">采集</text>
        <view class="tab-indicator" v-if="currentTab === 'collect'"></view>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'group' }"
        @click="currentTab = 'group'"
      >
        <text class="tab-text">分组</text>
        <view class="tab-indicator" v-if="currentTab === 'group'"></view>
      </view>
    </view>

    <!-- 采集统计 -->
    <view class="stats-row">
      <text class="stats-count">{{ collections.length }}个采集</text>
      <view class="stats-actions">
        <view class="action-btn" @click="showFilter = !showFilter">
          <text class="filter-icon">▽</text>
        </view>
        <view class="action-btn" @click="showMoreMenu = !showMoreMenu">
          <text class="more-icon">···</text>
        </view>
      </view>
    </view>

    <!-- 采集列表 -->
    <scroll-view 
      class="collect-list" 
      scroll-y 
      :show-scrollbar="false"
      v-if="currentTab === 'collect'"
    >
      <view 
        class="stamp-card" 
        v-for="item in collections" 
        :key="item.id"
        @click="viewCollection(item)"
      >
        <view class="stamp-border">
          <view class="stamp-content">
            <text class="stamp-title">{{ item.title }}</text>
            <image class="stamp-image" :src="item.image" mode="aspectFill" />
            <view class="stamp-footer">
              <text class="stamp-date">{{ item.date }}</text>
              <text class="stamp-year">{{ item.year }}</text>
            </view>
          </view>
        </view>
        <view class="stamp-location" v-if="item.location">
          <text class="location-text">{{ item.location }}</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="collections.length === 0">
        <text class="empty-icon">📷</text>
        <text class="empty-text">还没有采集记录</text>
        <text class="empty-hint">点击右下角相机开始采集</text>
      </view>
    </scroll-view>

    <!-- 分组列表 -->
    <scroll-view 
      class="group-list" 
      scroll-y 
      :show-scrollbar="false"
      v-if="currentTab === 'group'"
    >
      <view 
        class="group-card" 
        v-for="group in groups" 
        :key="group.id"
        @click="viewGroup(group)"
      >
        <view class="group-cover">
          <image v-if="group.cover" :src="group.cover" mode="aspectFill" />
          <view class="group-placeholder" v-else>
            <text class="placeholder-icon">📁</text>
          </view>
        </view>
        <view class="group-info">
          <text class="group-name">{{ group.name }}</text>
          <text class="group-count">{{ group.count }}个采集</text>
        </view>
      </view>
    </scroll-view>

    <!-- 悬浮拍照按钮 -->
    <view class="fab-btn" @click="openCamera">
      <view class="fab-icon">
        <text class="camera-icon">📷</text>
      </view>
    </view>

    <!-- 统计弹窗 -->
    <view class="stats-popup-mask" :class="{ show: showStatsPopup }" @click="showStatsPopup = false">
      <view class="stats-popup" @click.stop>
        <view class="popup-handle"></view>
        
        <view class="stats-grid">
          <view class="stat-item">
            <view class="stat-icon">🎫</view>
            <text class="stat-value">{{ collections.length }}</text>
            <text class="stat-label">采集</text>
          </view>
          <view class="stat-item">
            <view class="stat-icon">🏛️</view>
            <text class="stat-value">{{ cityCount }}</text>
            <text class="stat-label">城市</text>
          </view>
          <view class="stat-item">
            <view class="stat-icon">🌐</view>
            <text class="stat-value">{{ countryCount }}</text>
            <text class="stat-label">国家</text>
          </view>
        </view>

        <view class="location-tags">
          <view class="tag-item global" @click="filterByLocation('global')">
            <text class="tag-text">全球 · {{ collections.length }}</text>
          </view>
        </view>

        <view class="mystery-section">
          <text class="mystery-title">神秘的采集地</text>
          <view class="mystery-tags">
            <view class="tag-item" @click="filterByLocation('all')">
              <text class="tag-text">全部 · {{ collections.length }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getCollections, getCollectionGroups } from '@/api'

// 状态栏高度
const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)

// 当前标签页
const currentTab = ref<'collect' | 'group'>('collect')

// 弹窗状态
const showFilter = ref(false)
const showMoreMenu = ref(false)
const showStatsPopup = ref(false)

// 采集数据
interface Collection {
  id: number
  title: string
  image: string
  date: string
  year: string
  location?: string
  city?: string
  country?: string
}

interface Group {
  id: number
  name: string
  cover?: string
  count: number
}

const collections = ref<Collection[]>([])
const groups = ref<Group[]>([])

// 统计数据
const cityCount = computed(() => {
  const cities = new Set(collections.value.filter(c => c.city).map(c => c.city))
  return cities.size
})

const countryCount = computed(() => {
  const countries = new Set(collections.value.filter(c => c.country).map(c => c.country))
  return countries.size
})

const navBarStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return {
      paddingTop: statusBarHeight.value + 'px',
      height: navBarHeight.value + 'px',
      paddingLeft: '24rpx',
      paddingRight: '24rpx',
      paddingBottom: '0px'
    }
  }
  return {
    paddingTop: statusBarHeight.value + 'px',
    minHeight: navBarHeight.value + 'px'
  }
})

const navRightStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingRight: menuButtonSpace.value + 'px' }
  }
  return {}
})

// 获取系统信息
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  const windowWidth = systemInfo.windowWidth || 375
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonSpace.value = windowWidth - menuButton.left + 10
      navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  loadData()
})

// 加载数据
const loadData = async () => {
  try {
    const [collectRes, groupRes] = await Promise.all([
      getCollections(),
      getCollectionGroups()
    ])
    collections.value = collectRes || []
    groups.value = groupRes || []
  } catch (e) {
    console.error('加载采集数据失败', e)
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 前往设置
const goToSettings = () => {
  uni.navigateTo({
    url: '/pages/collect/settings'
  })
}

// 打开相机
const openCamera = () => {
  uni.navigateTo({
    url: '/pages/collect/camera'
  })
}

// 查看采集详情
const viewCollection = (item: Collection) => {
  uni.navigateTo({
    url: `/pages/collect/detail?id=${item.id}`
  })
}

// 查看分组
const viewGroup = (group: Group) => {
  uni.navigateTo({
    url: `/pages/collect/group?id=${group.id}`
  })
}

// 按位置筛选
const filterByLocation = (location: string) => {
  showStatsPopup.value = false
  // TODO: 实现位置筛选
}
</script>

<style lang="scss" scoped>
.collect-page {
  min-height: 100vh;
  background: #F5F5F7;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  background: #F5F5F7;
  box-sizing: border-box;
}

.nav-left {
  width: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: #1D1D1F;
  font-weight: 300;
}

.nav-right {
  width: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-refresh {
  font-size: 40rpx;
  color: #1D1D1F;
}

.tabs {
  display: flex;
  padding: 0 32rpx;
  gap: 48rpx;
}

.tab-item {
  position: relative;
  padding: 24rpx 0;
}

.tab-text {
  font-size: 40rpx;
  font-weight: 700;
  color: #86868B;
}

.tab-item.active .tab-text {
  color: #1D1D1F;
}

.tab-indicator {
  position: absolute;
  bottom: 16rpx;
  left: 0;
  width: 48rpx;
  height: 6rpx;
  background: #007AFF;
  border-radius: 3rpx;
}

.stats-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
}

.stats-count {
  font-size: 28rpx;
  color: #86868B;
}

.stats-actions {
  display: flex;
  gap: 24rpx;
}

.action-btn {
  padding: 8rpx;
}

.filter-icon,
.more-icon {
  font-size: 32rpx;
  color: #1D1D1F;
}

.collect-list,
.group-list {
  height: calc(100vh - 320rpx);
  padding: 0 32rpx 120rpx;
}

// 邮票卡片样式
.stamp-card {
  margin-bottom: 32rpx;
}

.stamp-border {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 4rpx;
  // 邮票锯齿边缘效果
  background-image: 
    radial-gradient(circle at 0 8rpx, transparent 8rpx, #FFFFFF 8rpx),
    radial-gradient(circle at 100% 8rpx, transparent 8rpx, #FFFFFF 8rpx);
  background-size: 24rpx 16rpx;
  background-position: left top, right top;
  background-repeat: repeat-y;
}

.stamp-content {
  background: #FFFFFF;
  padding: 24rpx;
  border-radius: 12rpx;
}

.stamp-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 16rpx;
}

.stamp-image {
  width: 100%;
  height: 360rpx;
  border-radius: 8rpx;
  background: #F0F0F0;
}

.stamp-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 16rpx;
}

.stamp-date {
  font-size: 48rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.stamp-year {
  font-size: 28rpx;
  color: #86868B;
  writing-mode: vertical-rl;
}

.stamp-location {
  padding: 12rpx 24rpx;
}

.location-text {
  font-size: 24rpx;
  color: #86868B;
}

// 分组卡片
.group-card {
  display: flex;
  align-items: center;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.group-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #F0F0F0;
  margin-right: 24rpx;
}

.group-cover image {
  width: 100%;
  height: 100%;
}

.group-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48rpx;
}

.group-info {
  flex: 1;
}

.group-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
  display: block;
}

.group-count {
  font-size: 24rpx;
  color: #86868B;
  margin-top: 8rpx;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #1D1D1F;
  margin-bottom: 12rpx;
}

.empty-hint {
  font-size: 26rpx;
  color: #86868B;
}

// 悬浮按钮
.fab-btn {
  position: fixed;
  right: 32rpx;
  bottom: 64rpx;
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.15);
}

.camera-icon {
  font-size: 48rpx;
}

// 统计弹窗
.stats-popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 1000;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.stats-popup {
  width: 100%;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  padding: 24rpx 32rpx 64rpx;
  transform: translateY(100%);
  transition: transform 0.3s ease;

  .stats-popup-mask.show & {
    transform: translateY(0);
  }
}

.popup-handle {
  width: 64rpx;
  height: 8rpx;
  background: #E5E5EA;
  border-radius: 4rpx;
  margin: 0 auto 32rpx;
}

.stats-grid {
  display: flex;
  justify-content: space-around;
  padding: 32rpx 0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-icon {
  font-size: 48rpx;
  margin-bottom: 16rpx;
}

.stat-value {
  font-size: 48rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.stat-label {
  font-size: 24rpx;
  color: #86868B;
  margin-top: 8rpx;
}

.location-tags {
  padding: 24rpx 0;
}

.tag-item {
  display: inline-flex;
  padding: 16rpx 32rpx;
  background: #F5F5F7;
  border-radius: 100rpx;
  margin-right: 16rpx;
  margin-bottom: 16rpx;

  &.global {
    background: #1D1D1F;
    
    .tag-text {
      color: #FFFFFF;
    }
  }
}

.tag-text {
  font-size: 26rpx;
  color: #1D1D1F;
}

.mystery-section {
  padding-top: 24rpx;
}

.mystery-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 16rpx;
}

.mystery-tags {
  display: flex;
  flex-wrap: wrap;
}
</style>
