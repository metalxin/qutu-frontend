<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar" :style="navBarStyle">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <view class="nav-right" :style="navRightStyle" @click="handleShare">
        <SFIcon name="share" :size="36" color="#1D1D1F" />
      </view>
    </view>

    <scroll-view class="content" scroll-y :show-scrollbar="false">
      <!-- 收藏机主视觉 -->
      <view class="hero-section">
        <view class="machine-wrapper">
          <!-- 复古电视机样式 -->
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
                  <view class="screen-bottom">
                    <text class="bottom-text">LOADING...</text>
                    <text class="bottom-text">LOADING...</text>
                    <text class="bottom-text">THE_DISCOVERY.EXE</text>
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

      <!-- 我的点亮记录入口 -->
      <view class="records-entry" @click="goToRecords">
        <text class="records-text">我的点亮记录 · {{ recordCount }}</text>
        <SFIcon name="chevron-right" :size="28" color="#FFFFFF" />
      </view>

      <!-- 活动卡片列表 -->
      <view class="activity-list">
        <!-- 春节玩乐地图 -->
        <view class="activity-card spring-card" @click="goToMap('spring')">
          <view class="card-header red-header">
            <view class="header-content">
              <text class="card-title">春节玩乐地图</text>
              <text class="card-subtitle">年味在路上，点亮你的新春足迹！</text>
              <text class="card-count">3.05万人已点亮</text>
            </view>
            <view class="header-decoration">
              <view class="deco-icons">
                <text class="deco-emoji">🎒</text>
                <text class="deco-emoji">💕</text>
              </view>
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
              <view class="map-item">
                <text class="item-emoji">🧧</text>
                <text class="item-text">新春玩乐地图</text>
              </view>
            </view>
            <view class="card-action red-action">
              <text class="action-btn-text">去点亮</text>
            </view>
          </view>
        </view>

        <!-- 中国漫游者指南 -->
        <view class="activity-card china-card" @click="goToMap('china')">
          <view class="card-simple purple-card">
            <view class="simple-content">
              <text class="simple-title">中国漫游者指南</text>
              <text class="simple-subtitle">点亮你去过的省/市/自治区/特别行政区</text>
              <view class="simple-divider"></view>
              <text class="simple-count">28万人已点亮</text>
            </view>
            <view class="card-action dark-action">
              <text class="action-btn-text">去点亮</text>
            </view>
            <view class="simple-icon">
              <view class="china-icon">
                <view class="icon-line"></view>
                <view class="icon-line"></view>
                <view class="icon-line"></view>
              </view>
            </view>
          </view>
        </view>

        <!-- 世界探险家手册 -->
        <view class="activity-card world-card" @click="goToMap('world')">
          <view class="card-simple blue-card">
            <view class="simple-content">
              <text class="simple-title">世界探险家手册</text>
              <text class="simple-subtitle">点亮你在世界上去过的国家/地区</text>
              <view class="simple-divider"></view>
              <text class="simple-count">13.02万人已点亮</text>
            </view>
            <view class="card-action dark-action">
              <text class="action-btn-text">去点亮</text>
            </view>
            <view class="simple-icon">
              <view class="globe-icon">
                <SFIcon name="route" :size="80" color="rgba(0,150,200,0.3)" />
              </view>
            </view>
          </view>
        </view>

        <!-- 晒一晒活动 -->
        <view class="activity-card share-card">
          <view class="share-content">
            <text class="share-title">晒一晒过年吃喝玩乐好去处</text>
            <view class="share-info">
              <text class="share-label">创建春节玩乐地图</text>
              <view class="share-item">
                <view class="dot blue"></view>
                <text class="share-text">发布至社交平台：</text>
              </view>
              <text class="share-platforms">小红书/抖音/微信视频号</text>
              <view class="share-item">
                <view class="dot orange"></view>
                <text class="share-text">带话题 #人生地点收藏机 #趣途云迹</text>
              </view>
              <view class="share-item">
                <view class="dot yellow"></view>
                <text class="share-text highlight">2月14日官方抽18位分享用户送</text>
              </view>
              <text class="share-prize">【趣途云迹蛇年贺卡+富士一次性胶片机】</text>
            </view>
          </view>
          <view class="share-decoration">
            <view class="camera-icon">📷</view>
          </view>
        </view>
      </view>

      <!-- 底部间距 -->
      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getFootprintRecords, getFootprintActivities } from '@/api'

interface Activity {
  id: string
  type: string
  title: string
  subtitle: string
  count: string
  bgType: string
}

// 状态栏高度
const statusBarHeight = ref(44)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)

// 点亮记录数
const recordCount = ref(0)

// 活动列表
const activities = ref<Activity[]>([])
const loading = ref(false)

const navBarStyle = computed(() => {
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
const getSystemInfo = () => {
  try {
    const info = uni.getSystemInfoSync()
    statusBarHeight.value = info.statusBarHeight || 44
    const windowWidth = info.windowWidth || 375
    // #ifdef MP-WEIXIN
    try {
      const menuButton = uni.getMenuButtonBoundingClientRect()
      if (menuButton) {
        menuButtonSpace.value = windowWidth - menuButton.left + 10
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
// 加载足迹记录数
const loadRecordCount = async () => {
  try {
    const res = await getFootprintRecords()
    if (res && res.total !== undefined) {
      recordCount.value = res.total
    }
  } catch (error) {
    console.error('加载足迹记录失败:', error)
  }
}

// 加载活动列表
const loadActivities = async () => {
  loading.value = true
  try {
    const res = await getFootprintActivities()
    if (res && res.length > 0) {
      activities.value = res
    }
  } catch (error) {
    console.error('加载活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 分享
const handleShare = () => {
  uni.showToast({
    title: '分享功能开发中',
    icon: 'none'
  })
}

// 活动说明
const showActivityInfo = () => {
  uni.showModal({
    title: '活动说明',
    content: '人生地点收藏机是一个记录你去过的地方的工具，点亮你的足迹，收藏你的回忆！',
    showCancel: false
  })
}

// 跳转到点亮记录
const goToRecords = () => {
  uni.navigateTo({
    url: '/pages/footprint/records'
  })
}

// 跳转到地图详情
const goToMap = (type: string) => {
  uni.navigateTo({
    url: `/pages/footprint/map?type=${type}`
  })
}

// 页面加载
onMounted(() => {
  getSystemInfo()
  loadRecordCount()
  loadActivities()
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
  box-sizing: border-box; /* Ensure padding is included in height */
}

.nav-back, .nav-right {
  width: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content {
  height: 100vh;
  padding-top: 120rpx;
}

// 收藏机主视觉
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

.screen-bottom {
  margin-top: 40rpx;
}

.bottom-text {
  display: block;
  font-size: 18rpx;
  opacity: 0.5;
  margin-bottom: 4rpx;
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

// 点亮记录入口
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

// 活动卡片列表
.activity-list {
  padding: 0 32rpx;
}

.activity-card {
  margin-bottom: 24rpx;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

// 春节玩乐地图卡片
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
  margin-bottom: 8rpx;
}

.card-count {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

.header-decoration {
  display: flex;
  align-items: center;
}

.deco-icons {
  display: flex;
  gap: -10rpx;
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

.card-simple .card-action {
  margin: 0 20rpx;
  flex-shrink: 0;
  align-self: center;
}

.card-action {
  padding: 16rpx 32rpx;
  border-radius: 30rpx;
  min-width: 140rpx;
  text-align: center;
  
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

// 简单卡片样式
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
  display: flex;
  flex-direction: column;
}

.simple-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: #1D1D1F;
  margin-bottom: 8rpx;
  line-height: 1.3;
}

.simple-subtitle {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 1.4;
}

.simple-divider {
  height: 2rpx;
  background: rgba(0, 0, 0, 0.1);
  margin: 24rpx 0;
  border-style: dashed;
}

.simple-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16rpx;
}

.simple-count {
  font-size: 24rpx;
  color: #666;
  flex: 1;
  min-width: 0;
}

.simple-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120rpx;
  flex-shrink: 0;
}

.china-icon {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  opacity: 0.3;
}

.icon-line {
  height: 8rpx;
  background: #8B7BA8;
  border-radius: 4rpx;
  
  &:nth-child(1) { width: 80rpx; }
  &:nth-child(2) { width: 60rpx; margin-left: 10rpx; }
  &:nth-child(3) { width: 70rpx; margin-left: 5rpx; }
}

.globe-icon {
  opacity: 0.5;
}

// 晒一晒卡片
.share-card {
  background: #FFFFFF;
  padding: 32rpx;
  display: flex;
  justify-content: space-between;
}

.share-content {
  flex: 1;
}

.share-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #1D1D1F;
  margin-bottom: 24rpx;
}

.share-info {
  font-size: 24rpx;
  color: #666;
}

.share-label {
  display: block;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 12rpx;
}

.share-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 8rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  
  &.blue { background: #007AFF; }
  &.orange { background: #FF9500; }
  &.yellow { background: #FFCC00; }
}

.share-text {
  font-size: 22rpx;
  color: #666;
  
  &.highlight {
    color: #FF6B6B;
    font-weight: 500;
  }
}

.share-platforms {
  display: block;
  font-size: 22rpx;
  color: #999;
  margin-left: 20rpx;
  margin-bottom: 8rpx;
}

.share-prize {
  display: block;
  font-size: 22rpx;
  color: #666;
  margin-left: 20rpx;
}

.share-decoration {
  display: flex;
  align-items: center;
}

.camera-icon {
  font-size: 80rpx;
}

.bottom-space {
  height: 60rpx;
}
</style>
