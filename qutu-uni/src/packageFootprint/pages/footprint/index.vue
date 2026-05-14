<template>
  <view class="page">
    <view class="navbar" :style="navBarStyle">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <view class="nav-placeholder"></view>
    </view>

    <scroll-view class="content" scroll-y :show-scrollbar="false">
      <view class="hero-section">
        <view class="machine-wrapper">
          <view class="retro-machine">
            <view class="machine-top">
              <view class="antenna-left"></view>
              <view class="machine-label">趣途云迹</view>
              <view class="antenna-right">
                <view class="antenna-ball"></view>
              </view>
            </view>
            <view class="machine-body">
              <view class="screen">
                <view class="screen-content">
                  <text class="loading-text">LOADING...</text>
                  <text class="loading-text">YUAN_ZHOU_LV_3.EXE</text>
                  <view class="screen-title">
                    <text class="title-icon">📍</text>
                    <text class="title-text">人生地点</text>
                  </view>
                  <text class="title-main">收藏机.</text>
                  <view class="screen-stats">
                    <view class="stat-item">
                      <text class="stat-value">{{ stats.totalFootprints || 0 }}</text>
                      <text class="stat-label">足迹</text>
                    </view>
                    <view class="stat-divider"></view>
                    <view class="stat-item">
                      <text class="stat-value">{{ stats.provinceCount || 0 }}</text>
                      <text class="stat-label">省份</text>
                    </view>
                    <view class="stat-divider"></view>
                    <view class="stat-item">
                      <text class="stat-value">{{ stats.countryCount || 0 }}</text>
                      <text class="stat-label">国家</text>
                    </view>
                  </view>
                  <view class="action-link" @click="showActivityInfo">
                    <text class="action-text">活动说明 »</text>
                  </view>
                </view>
                <view class="screen-indicator">
                  <view class="indicator-dot green"></view>
                  <view class="indicator-dot orange"></view>
                </view>
              </view>
              <view class="machine-controls">
                <view class="control-dial"></view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="records-entry" @click="goToRecords">
        <text class="records-text">我的点亮记录 · {{ stats.totalFootprints || 0 }}</text>
        <SFIcon name="chevron-right" :size="28" color="#FFFFFF" />
      </view>

      <view class="stats-section">
        <view class="stats-card">
          <view class="stats-row">
            <view class="stats-item" @click="goToMap('china')">
              <text class="stats-number">{{ stats.provinceCount || 0 }}</text>
              <text class="stats-desc">已点亮省份</text>
              <view class="stats-action">
                <text class="stats-action-text">去点亮 ›</text>
              </view>
            </view>
            <view class="stats-divider"></view>
            <view class="stats-item" @click="goToMap('world')">
              <text class="stats-number">{{ stats.countryCount || 0 }}</text>
              <text class="stats-desc">已点亮国家</text>
              <view class="stats-action">
                <text class="stats-action-text">去点亮 ›</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="recent-section">
        <view class="section-header">
          <text class="section-title">最近足迹</text>
          <view class="section-more" @click="goToRecords">
            <text class="more-text">查看全部 ›</text>
          </view>
        </view>
        <view class="recent-list" v-if="recentRecords.length > 0">
          <view class="recent-item" v-for="record in recentRecords" :key="record.id" @click="viewSpotDetail(record)">
            <view class="recent-image-wrap">
              <image v-if="record.imageUrl" class="recent-image" :src="resolveImageUrl(record.imageUrl)" mode="aspectFill" />
              <view v-else class="recent-image-placeholder">
                <text class="placeholder-emoji">📍</text>
              </view>
            </view>
            <view class="recent-info">
              <text class="recent-name">{{ record.spotName }}</text>
              <text class="recent-location" v-if="record.cityName || record.provinceName">
                {{ record.provinceName ? record.provinceName + ' · ' : '' }}{{ record.cityName || '' }}
              </text>
              <text class="recent-date" v-if="record.createdAt">{{ formatDate(record.createdAt) }}</text>
            </view>
            <view class="recent-tag" :class="record.type === 2 ? 'overseas' : 'domestic'">
              <text class="tag-text">{{ record.type === 2 ? '国外' : '国内' }}</text>
            </view>
          </view>
        </view>
        <view class="recent-empty" v-else>
          <text class="empty-emoji">🗺️</text>
          <text class="empty-text">还没有足迹记录</text>
          <text class="empty-desc">去探索景点，点亮你的足迹吧！</text>
        </view>
      </view>

      <view class="activity-list">
        <view class="activity-card spring-card" @click="goToMap('spring')">
          <view class="card-header red-header">
            <view class="header-content">
              <text class="card-title">春节玩乐地图</text>
              <text class="card-subtitle">年味在路上，点亮你的新春足迹！</text>
            </view>
            <view class="header-decoration">
              <text class="deco-emoji">🎒</text>
            </view>
          </view>
          <view class="card-body cream-body">
            <view class="map-items">
              <view class="map-item">
                <text class="item-emoji">🐲</text>
                <text class="item-text">新春看神仙地图</text>
              </view>
              <view class="map-item">
                <text class="item-emoji">😋</text>
                <text class="item-text">新春贪吃地图</text>
              </view>
            </view>
            <view class="card-action red-action">
              <text class="action-btn-text">去点亮</text>
            </view>
          </view>
        </view>

        <view class="activity-card china-card" @click="goToMap('china')">
          <view class="card-simple purple-card">
            <view class="simple-content">
              <text class="simple-title">中国漫游者指南</text>
              <text class="simple-subtitle">点亮你去过的省/市/自治区/特别行政区</text>
              <view class="simple-divider"></view>
              <text class="simple-count">已点亮 {{ stats.provinceCount || 0 }} 个省份</text>
            </view>
            <view class="card-action dark-action">
              <text class="action-btn-text">去点亮</text>
            </view>
          </view>
        </view>

        <view class="activity-card world-card" @click="goToMap('world')">
          <view class="card-simple blue-card">
            <view class="simple-content">
              <text class="simple-title">世界探险家手册</text>
              <text class="simple-subtitle">点亮你在世界上去过的国家/地区</text>
              <view class="simple-divider"></view>
              <text class="simple-count">已点亮 {{ stats.countryCount || 0 }} 个国家</text>
            </view>
            <view class="card-action dark-action">
              <text class="action-btn-text">去点亮</text>
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getFootprintStats, getFootprintRecords } from '@/api'
import type { FootprintStats, FootprintRecord } from '@/api'
import { resolveFileUrl } from '@/api/modules/user'

const statusBarHeight = ref(44)
const navBarHeight = ref(88)

const stats = ref<FootprintStats>({
  totalFootprints: 0,
  domesticFootprints: 0,
  overseasFootprints: 0,
  provinceCount: 0,
  countryCount: 0,
  collectCount: 0,
  provinces: [],
  countries: []
})

const recentRecords = ref<FootprintRecord[]>([])
const loading = ref(false)

const navBarStyle = computed(() => {
  return {
    paddingTop: statusBarHeight.value + 'px',
    minHeight: navBarHeight.value + 'px'
  }
})

const resolveImageUrl = (url: string) => {
  return resolveFileUrl(url || '')
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

const getSystemInfo = () => {
  try {
    const info = uni.getSystemInfoSync()
    statusBarHeight.value = info.statusBarHeight || 44
    // #ifdef MP-WEIXIN
    try {
      const menuButton = uni.getMenuButtonBoundingClientRect()
      if (menuButton) {
        navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height + statusBarHeight.value
      }
    } catch (e) {
      console.log('获取胶囊按钮位置失败', e)
    }
    // #endif
  } catch (e) {
    console.error('获取系统信息失败', e)
  }
}

const loadStats = async () => {
  try {
    const res = await getFootprintStats()
    if (res) {
      stats.value = res
    }
  } catch (error) {
    console.error('加载足迹统计失败:', error)
  }
}

const loadRecentRecords = async () => {
  loading.value = true
  try {
    const res = await getFootprintRecords({ current: 1, size: 5 })
    if (res) {
      recentRecords.value = res.records || []
    }
  } catch (error) {
    console.error('加载最近足迹失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  uni.navigateBack()
}

const showActivityInfo = () => {
  uni.showModal({
    title: '活动说明',
    content: '人生地点收藏机是一个记录你去过的地方的工具，点亮你的足迹，收藏你的回忆！',
    showCancel: false
  })
}

const goToRecords = () => {
  uni.navigateTo({ url: '/packageFootprint/pages/footprint/records' })
}

const goToMap = (type: string) => {
  uni.navigateTo({ url: `/packageFootprint/pages/footprint/map?type=${type}` })
}

const viewSpotDetail = (record: FootprintRecord) => {
  if (record.spotId) {
    uni.navigateTo({ url: `/packageTrip/pages/destination/detail?id=${record.spotId}` })
  }
}

onMounted(() => {
  getSystemInfo()
})

onShow(() => {
  loadStats()
  loadRecentRecords()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #E8F4F8 0%, #F5F5F7 100%);
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  background: transparent;
  box-sizing: border-box;
}

.nav-back, .nav-placeholder {
  width: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content {
  height: 100vh;
  padding-top: 120rpx;
}

.hero-section {
  padding: 40rpx 32rpx;
  display: flex;
  justify-content: center;
}

.machine-wrapper {
  width: 100%;
  max-width: 600rpx;
}

.retro-machine {
  position: relative;
}

.machine-top {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 20rpx;
  position: relative;
}

.antenna-left {
  position: absolute;
  left: 80rpx;
  bottom: 30rpx;
  width: 8rpx;
  height: 80rpx;
  background: #666;
  transform: rotate(-15deg);
  border-radius: 4rpx;
}

.machine-label {
  font-size: 24rpx;
  color: #999;
  letter-spacing: 4rpx;
}

.antenna-right {
  position: absolute;
  right: 60rpx;
  bottom: 20rpx;
}

.antenna-ball {
  width: 40rpx;
  height: 40rpx;
  background: #00C9A7;
  border-radius: 50%;
}

.machine-body {
  background: linear-gradient(180deg, #4A4A4A 0%, #2A2A2A 100%);
  border-radius: 40rpx;
  padding: 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.3);
}

.screen {
  background: #1A1A1A;
  border-radius: 20rpx;
  padding: 40rpx;
  position: relative;
  border: 4rpx solid #333;
}

.screen-content {
  color: #00FF88;
  font-family: monospace;
}

.loading-text {
  display: block;
  font-size: 20rpx;
  opacity: 0.6;
  margin-bottom: 8rpx;
}

.screen-title {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.title-icon {
  font-size: 32rpx;
  margin-right: 8rpx;
}

.title-text {
  font-size: 32rpx;
  font-weight: 500;
}

.title-main {
  display: block;
  font-size: 56rpx;
  font-weight: 700;
  margin: 10rpx 0 20rpx;
}

.screen-stats {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-top: 16rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #00FF88;
}

.stat-label {
  font-size: 20rpx;
  color: rgba(0, 255, 136, 0.6);
  margin-top: 4rpx;
}

.stat-divider {
  width: 2rpx;
  height: 40rpx;
  background: rgba(0, 255, 136, 0.3);
}

.action-link {
  position: absolute;
  right: 40rpx;
  bottom: 60rpx;
}

.action-text {
  color: #00C9A7;
  font-size: 24rpx;
}

.screen-indicator {
  position: absolute;
  right: 40rpx;
  bottom: 30rpx;
  display: flex;
  gap: 12rpx;
}

.indicator-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;

  &.green {
    background: #00FF88;
    box-shadow: 0 0 10rpx #00FF88;
  }

  &.orange {
    background: #FF9500;
    box-shadow: 0 0 10rpx #FF9500;
  }
}

.machine-controls {
  display: flex;
  justify-content: flex-end;
  margin-top: 20rpx;
}

.control-dial {
  width: 60rpx;
  height: 60rpx;
  background: #666;
  border-radius: 50%;
  border: 4rpx solid #888;
}

.records-entry {
  margin: 0 auto 40rpx;
  width: fit-content;
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx 40rpx;
  background: #1D1D1F;
  border-radius: 40rpx;
}

.records-text {
  color: #FFFFFF;
  font-size: 28rpx;
  font-weight: 500;
}

.stats-section {
  padding: 0 32rpx;
  margin-bottom: 32rpx;
}

.stats-card {
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.stats-row {
  display: flex;
  align-items: center;
}

.stats-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stats-number {
  font-size: 48rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.stats-desc {
  font-size: 24rpx;
  color: #86868B;
}

.stats-action {
  margin-top: 8rpx;
}

.stats-action-text {
  font-size: 24rpx;
  color: #007AFF;
  font-weight: 500;
}

.stats-divider {
  width: 2rpx;
  height: 80rpx;
  background: #F0F0F0;
}

.recent-section {
  padding: 0 32rpx;
  margin-bottom: 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.section-more {
  padding: 8rpx 0;
}

.more-text {
  font-size: 24rpx;
  color: #007AFF;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.recent-image-wrap {
  width: 100rpx;
  height: 100rpx;
  border-radius: 16rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.recent-image {
  width: 100%;
  height: 100%;
}

.recent-image-placeholder {
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-emoji {
  font-size: 40rpx;
}

.recent-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.recent-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recent-location {
  font-size: 22rpx;
  color: #86868B;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recent-date {
  font-size: 20rpx;
  color: #C7C7CC;
}

.recent-tag {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  flex-shrink: 0;

  &.domestic {
    background: #E3F2FD;
  }

  &.overseas {
    background: #FFF3E0;
  }
}

.tag-text {
  font-size: 20rpx;
  font-weight: 500;

  .domestic & {
    color: #1976D2;
  }

  .overseas & {
    color: #E65100;
  }
}

.recent-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  gap: 12rpx;
}

.empty-emoji {
  font-size: 64rpx;
  margin-bottom: 8rpx;
}

.empty-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.empty-desc {
  font-size: 24rpx;
  color: #86868B;
}

.activity-list {
  padding: 0 32rpx;
}

.activity-card {
  margin-bottom: 24rpx;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.card-header {
  padding: 32rpx;
  display: flex;
  justify-content: space-between;

  &.red-header {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  }
}

.header-content {
  flex: 1;
}

.card-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #FFFFFF;
  margin-bottom: 8rpx;
}

.card-subtitle {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}

.header-decoration {
  display: flex;
  align-items: center;
}

.deco-emoji {
  font-size: 48rpx;
}

.card-body {
  padding: 24rpx 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 176rpx;

  &.cream-body {
    background: #FFF8E7;
  }
}

.map-items {
  flex: 1;
}

.map-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.item-emoji {
  font-size: 32rpx;
}

.item-text {
  font-size: 28rpx;
  color: #1D1D1F;
  font-weight: 500;
}

.card-action {
  padding: 16rpx 32rpx;
  border-radius: 30rpx;
  min-width: 140rpx;
  text-align: center;
  flex-shrink: 0;

  &.red-action {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  }

  &.dark-action {
    background: #1D1D1F;
  }
}

.action-btn-text {
  font-size: 26rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.card-simple {
  padding: 32rpx;
  display: flex;
  align-items: center;
  min-height: 220rpx;

  &.purple-card {
    background: linear-gradient(135deg, #E8E0F0 0%, #F0E8F8 100%);
  }

  &.blue-card {
    background: linear-gradient(135deg, #D0F0F8 0%, #E0F8FF 100%);
  }
}

.simple-content {
  flex: 1;
  min-width: 0;
}

.simple-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: #1D1D1F;
  margin-bottom: 8rpx;
}

.simple-subtitle {
  display: block;
  font-size: 24rpx;
  color: #666;
}

.simple-divider {
  height: 2rpx;
  background: rgba(0, 0, 0, 0.1);
  margin: 24rpx 0;
}

.simple-count {
  font-size: 24rpx;
  color: #666;
}

.bottom-space {
  height: 60rpx;
}
</style>
