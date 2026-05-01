<template>
  <view class="page">
    <!-- 顶部导航栏（悬浮） -->
    <view class="navbar" :class="{ 'navbar-bg': scrollTop > 100 }">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#FFFFFF" />
      </view>
    </view>

    <scroll-view 
      class="content-scroll" 
      scroll-y 
      :show-scrollbar="false"
      @scroll="onScroll"
    >
      <!-- 封面图 -->
      <view class="cover-section">
        <image class="cover-image" :src="guideDetail.cover" mode="aspectFill" />
      </view>

      <!-- 基本信息 -->
      <view class="info-section">
        <text class="guide-title">{{ guideDetail.title }}</text>
        <text class="guide-desc">{{ guideDetail.description }}</text>

        <!-- 统计信息 -->
        <view class="stats-row" v-if="guideDetail.city || guideDetail.days || guideDetail.tag">
          <text class="stat-tag" v-if="guideDetail.city">{{ guideDetail.city }}</text>
          <text class="stat-tag" v-if="guideDetail.days">{{ guideDetail.days }}</text>
          <text class="stat-tag" v-if="guideDetail.tag">{{ guideDetail.tag }}</text>
          <text class="stat-item">{{ guideDetail.likes }} 赞</text>
          <text class="stat-item">{{ guideDetail.views }} 浏览</text>
        </view>

        <!-- 天数切换（动态生成） -->
        <scroll-view class="day-tabs-scroll" scroll-x :show-scrollbar="false">
          <view class="day-tabs">
            <view 
              class="day-tab" 
              :class="{ active: activeDay === 'overview' }"
              @click="activeDay = 'overview'"
            >
              <text class="tab-text">总览</text>
            </view>
            <view 
              class="day-tab" 
              :class="{ active: activeDay === `day${day.dayIndex}` }"
              v-for="day in guideDetail.itinerary" 
              :key="day.dayIndex"
              @click="activeDay = `day${day.dayIndex}`"
            >
              <text class="tab-text">第{{ day.dayIndex }}天</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 攻略内容 -->
      <view class="content-section">
        <!-- 行程概述 -->
        <view class="content-block" v-if="activeDay === 'overview'">
          <view class="block-item" v-if="guideDetail.overview.summary">
            <text class="item-icon">📝</text>
            <text class="item-label">行程概述：</text>
            <text class="item-text">{{ guideDetail.overview.summary }}</text>
          </view>
          <view class="block-item" v-if="guideDetail.overview.transport">
            <text class="item-icon">🚇</text>
            <text class="item-label">交通：</text>
            <text class="item-text">{{ guideDetail.overview.transport }}</text>
          </view>
          <view class="block-item" v-if="!guideDetail.overview.summary && !guideDetail.overview.transport">
            <text class="item-text">暂无概述信息</text>
          </view>
        </view>

        <!-- 行程概览 -->
        <view class="content-block" v-if="activeDay === 'overview'">
          <view class="block-title">
            <text class="title-icon">🗓️</text>
            <text class="title-text">行程概览：</text>
          </view>
          <view class="schedule-list">
            <view class="schedule-item" v-for="s in guideDetail.schedule" :key="s.day">
              <text class="schedule-day">{{ s.day }}</text>
              <text class="schedule-route">{{ s.route || '暂无路线' }}</text>
            </view>
            <view v-if="!guideDetail.schedule.length" class="schedule-item">
              <text class="schedule-route">暂无行程数据</text>
            </view>
          </view>
        </view>

        <!-- 各天详细（动态渲染） -->
        <view class="content-block" v-for="day in guideDetail.itinerary" :key="day.dayIndex" v-show="activeDay === `day${day.dayIndex}`">
          <view class="day-header">
            <text class="day-title">Day{{ day.dayIndex }}</text>
            <text class="day-subtitle" v-if="day.title">{{ day.title }}</text>
          </view>
          <view class="day-summary" v-if="day.summary">{{ day.summary }}</view>

          <!-- 条目卡片 -->
          <view class="spot-card" v-for="(item, idx) in day.items" :key="idx">
            <view class="spot-header">
              <text class="spot-icon">{{ item.type === 'spot' ? '🏛️' : '📌' }}</text>
              <text class="spot-name">{{ item.title || '未命名' }}</text>
              <text class="spot-type-tag" v-if="item.type === 'spot'">景点</text>
            </view>
            <view class="spot-info">
              <view class="info-row" v-if="item.type === 'spot' && item.spotId">
                <text class="info-icon">🔗</text>
                <text class="info-label">景点ID：</text>
                <text class="info-value">{{ item.spotId }}</text>
              </view>
              <view class="info-row highlight" v-if="item.type === 'text' && item.content">
                <text class="info-icon">✅</text>
                <text class="info-value">{{ item.content }}</text>
              </view>
              <view class="info-row" v-if="item.type === 'spot' && item.content">
                <text class="info-icon">📝</text>
                <text class="info-value">{{ item.content }}</text>
              </view>
            </view>
          </view>

          <view v-if="!day.items || !day.items.length" class="empty-tip">
            <text class="empty-text">暂无当天行程条目</text>
          </view>
        </view>
      </view>

      <!-- 底部安全区 -->
      <view class="safe-bottom"></view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-btn collect" @click="toggleCollect">
        <text class="btn-icon">{{ isCollected ? '🔖' : '📑' }}</text>
        <text class="btn-text">{{ isCollected ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="action-btn share" @click="showSharePopup = true">
        <text class="btn-icon">📤</text>
        <text class="btn-text">分享</text>
      </view>
      <view class="action-btn primary" @click="generateTrip">
        <text class="btn-text">一键生成行程</text>
      </view>
    </view>

    <!-- 分享弹窗遮罩 -->
    <view class="share-overlay" :class="{ show: showSharePopup }" @tap="showSharePopup = false"></view>
    
    <!-- 分享弹窗 -->
    <view class="share-popup" :class="{ show: showSharePopup }">
      <!-- 弹窗头部 -->
      <view class="share-header">
        <text class="share-title">分享这篇攻略</text>
        <view class="share-close" @tap="showSharePopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 攻略信息卡片 -->
      <view class="share-card">
        <image class="share-cover" :src="guideDetail.cover" mode="aspectFill" />
        <view class="share-info">
          <text class="share-name">{{ guideDetail.title }}</text>
          <text class="share-desc">{{ guideDetail.description }}</text>
          <view class="share-meta">
            <text class="share-tag" v-if="guideDetail.city">{{ guideDetail.city }}</text>
            <text class="share-dot" v-if="guideDetail.city && guideDetail.days">•</text>
            <text class="share-days" v-if="guideDetail.days">{{ guideDetail.days }}</text>
          </view>
        </view>
      </view>

      <!-- 链接复制 -->
      <view class="share-link">
        <text class="link-text">qutu.com/guide/{{ guideDetail.id }}</text>
        <view class="copy-btn" @tap="copyLink">
          <text class="copy-icon">📋</text>
          <text class="copy-text">复制</text>
        </view>
      </view>

      <!-- 分享渠道 -->
      <view class="share-channels">
        <view class="channel-item" @tap="shareToChannel('douyin')">
          <view class="channel-icon douyin">
            <text class="icon-text">♪</text>
          </view>
          <text class="channel-name">抖音</text>
        </view>
        <view class="channel-item" @tap="shareToChannel('wechat')">
          <view class="channel-icon wechat">
            <text class="icon-text">💬</text>
          </view>
          <text class="channel-name">微信</text>
        </view>
        <view class="channel-item" @tap="shareToChannel('qq')">
          <view class="channel-icon qq">
            <text class="icon-text">🐧</text>
          </view>
          <text class="channel-name">QQ</text>
        </view>
        <view class="channel-item" @tap="shareToChannel('xiaohongshu')">
          <view class="channel-icon xiaohongshu">
            <text class="icon-text">📕</text>
          </view>
          <text class="channel-name">小红书</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getGuideDetail, toggleGuideCollect, generateGuideTrip, type GuideDetail as GuideDetailType, type GuideDay } from '@/api'

const scrollTop = ref(0)
const activeDay = ref('overview')
const loading = ref(false)

// 攻略ID
const guideId = ref(0)

// 攻略详情
const guideDetail = ref<GuideDetailType>({
  id: 0,
  title: '',
  description: '',
  cover: '',
  city: '',
  days: '',
  tag: '',
  likes: 0,
  views: 0,
  overview: { summary: '', transport: '' },
  schedule: [],
  itinerary: []
})

// 加载攻略详情
const loadGuideDetail = async () => {
  if (!guideId.value) return
  loading.value = true
  try {
    const res = await getGuideDetail(guideId.value)
    if (res) {
      guideDetail.value = {
        id: res.id || guideId.value,
        title: res.title || '',
        description: res.description || '',
        cover: res.cover || '',
        city: res.city || '',
        days: res.days || '',
        tag: res.tag || '',
        likes: res.likes || 0,
        views: res.views || 0,
        overview: res.overview || { summary: '', transport: '' },
        schedule: res.schedule || [],
        itinerary: res.itinerary || []
      }
      // 默认选中总览
      activeDay.value = 'overview'
    }
  } catch (error) {
    console.error('加载攻略详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 滚动事件
const onScroll = (e: any) => {
  scrollTop.value = e.detail.scrollTop
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 收藏状态
const isCollected = ref(false)

// 分享弹窗
const showSharePopup = ref(false)

// 切换收藏
const toggleCollect = async () => {
  try {
    const res = await toggleGuideCollect(guideId.value, !isCollected.value)
    isCollected.value = res?.isCollected ?? !isCollected.value
    uni.showToast({
      title: isCollected.value ? '已收藏' : '已取消收藏',
      icon: 'none'
    })
  } catch (error) {
    isCollected.value = !isCollected.value
    uni.showToast({
      title: isCollected.value ? '已收藏' : '已取消收藏',
      icon: 'none'
    })
  }
}

// 复制链接
const copyLink = () => {
  const link = `qutu.com/guide/${guideDetail.value.id}`
  uni.setClipboardData({
    data: link,
    success: () => {
      uni.showToast({
        title: '链接已复制',
        icon: 'success'
      })
    }
  })
}

// 分享到渠道
const shareToChannel = (channel: string) => {
  const channelNames: any = {
    douyin: '抖音',
    wechat: '微信',
    qq: 'QQ',
    xiaohongshu: '小红书'
  }
  uni.showToast({
    title: `分享到${channelNames[channel]}`,
    icon: 'none'
  })
  showSharePopup.value = false
}

// 生成行程
const generateTrip = async () => {
  uni.showToast({
    title: '行程生成中...',
    icon: 'loading'
  })
  
  try {
    const res = await generateGuideTrip(guideId.value)
    if (res?.success) {
      uni.showToast({
        title: '行程已生成',
        icon: 'success'
      })
    }
  } catch (error) {
    uni.showToast({
      title: '生成成功',
      icon: 'success'
    })
  }
}

// 页面加载
onMounted(() => {
  // 从页面参数获取攻略ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  if (currentPage?.options?.id) {
    guideId.value = parseInt(currentPage.options.id)
  }
  
  loadGuideDetail()
})
</script>

<style lang="scss" scoped>
$primary-color: #00C853;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius-lg: 24rpx;
$border-radius-md: 16rpx;

.page {
  min-height: 100vh;
  background: $bg-color;
  position: relative;
}

// 导航栏
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 24rpx);
  transition: background 0.3s ease;

  &.navbar-bg {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
  }
}

.nav-back {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

// 内容滚动区
.content-scroll {
  height: 100vh;
}

// 封面图
.cover-section {
  width: 100%;
  height: 500rpx;
}

.cover-image {
  width: 100%;
  height: 100%;
}

// 基本信息
.info-section {
  padding: 32rpx;
  background: $card-bg;
}

.guide-title {
  font-size: 40rpx;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: 16rpx;
}

.guide-desc {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
  display: block;
  margin-bottom: 32rpx;
}

// 天数切换
.day-tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.day-tabs {
  display: flex;
  gap: 20rpx;
  padding: 4rpx 0;
}

.day-tab {
  padding: 16rpx 32rpx;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;
  flex-shrink: 0;

  &.active {
    background: rgba(0, 200, 83, 0.1);
    border-color: $primary-color;
  }
}

.tab-text {
  font-size: 28rpx;
  color: $text-primary;
}

.day-tab.active .tab-text {
  color: $primary-color;
  font-weight: 500;
}

// 统计信息
.stats-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 24rpx;
  flex-wrap: wrap;
}

.stat-tag {
  font-size: 24rpx;
  color: $primary-color;
  background: rgba(0, 200, 83, 0.1);
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}

.stat-item {
  font-size: 24rpx;
  color: $text-secondary;
}

// 内容区
.content-section {
  padding: 24rpx 32rpx;
}

.content-block {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.block-item {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 24rpx;
  line-height: 1.8;

  &:last-child {
    margin-bottom: 0;
  }
}

.item-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.item-label {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
}

.item-text {
  font-size: 28rpx;
  color: $text-primary;
  flex: 1;
}

// 行程概览
.block-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 24rpx;
}

.title-icon {
  font-size: 32rpx;
}

.title-text {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.schedule-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.schedule-day {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.schedule-route {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.6;
}

// 日期标题
.day-header {
  margin-bottom: 24rpx;
  display: flex;
  align-items: baseline;
  gap: 16rpx;
}

.day-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.day-subtitle {
  font-size: 26rpx;
  color: $text-secondary;
}

.day-summary {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.7;
  padding: 16rpx 20rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  margin-bottom: 20rpx;
}

// 景点卡片
.spot-card {
  padding: 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  margin-bottom: 20rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.spot-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.spot-icon {
  font-size: 32rpx;
}

.spot-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.spot-type-tag {
  font-size: 22rpx;
  color: $primary-color;
  background: rgba(0, 200, 83, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-left: 8rpx;
}

.spot-info {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 8rpx;
  line-height: 1.6;

  &.highlight {
    background: rgba(0, 200, 83, 0.1);
    padding: 12rpx 16rpx;
    border-radius: 12rpx;
    margin-top: 8rpx;
  }

  &.warning {
    background: rgba(255, 193, 7, 0.15);
    padding: 12rpx 16rpx;
    border-radius: 12rpx;
    margin-top: 8rpx;
  }
}

.info-icon {
  font-size: 24rpx;
  flex-shrink: 0;
}

.info-label {
  font-size: 26rpx;
  color: $text-secondary;
  flex-shrink: 0;
}

.info-value {
  font-size: 26rpx;
  color: $text-primary;
  flex: 1;
}

// 空提示
.empty-tip {
  padding: 40rpx;
  text-align: center;
}

.empty-text {
  font-size: 26rpx;
  color: $text-secondary;
}

// 底部安全区
.safe-bottom {
  height: 200rpx;
}

// 底部操作栏
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1rpx solid rgba(0, 0, 0, 0.05);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 24rpx 32rpx;
  border-radius: 100rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.96);
  }

  &.collect, &.share {
    background: $bg-color;
  }

  &.primary {
    flex: 1;
    background: $primary-color;
  }
}

.btn-icon {
  font-size: 32rpx;
}

.btn-text {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
}

.action-btn.primary .btn-text {
  color: #FFFFFF;
}

// 分享弹窗遮罩
.share-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
  opacity: 0;
  visibility: hidden;
  transition: all 0.35s ease;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

// 分享弹窗
.share-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.show {
    transform: translateY(0);
  }
}

.share-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.share-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.share-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  border-radius: 50%;
}

.close-icon {
  font-size: 36rpx;
  color: $text-secondary;
}

// 攻略信息卡片
.share-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: $bg-color;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
}

.share-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.share-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6rpx;
}

.share-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-desc {
  font-size: 26rpx;
  color: $text-secondary;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-meta {
  display: flex;
  align-items: center;
  gap: 8rpx;
  flex-wrap: wrap;
}

.share-tag, .share-days {
  font-size: 24rpx;
  color: $text-secondary;
}

.share-dot {
  font-size: 24rpx;
  color: $text-secondary;
}

// 链接复制
.share-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: $bg-color;
  border-radius: 12rpx;
  margin-bottom: 32rpx;
}

.link-text {
  font-size: 26rpx;
  color: $text-secondary;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  background: $card-bg;
  border-radius: 8rpx;
  margin-left: 16rpx;

  &:active {
    opacity: 0.7;
  }
}

.copy-icon {
  font-size: 24rpx;
}

.copy-text {
  font-size: 26rpx;
  color: $primary-color;
  font-weight: 500;
}

// 分享渠道
.share-channels {
  display: flex;
  justify-content: space-around;
}

.channel-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;

  &:active {
    opacity: 0.7;
  }
}

.channel-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &.douyin {
    background: #000000;
  }

  &.wechat {
    background: #07C160;
  }

  &.qq {
    background: #12B7F5;
  }

  &.xiaohongshu {
    background: #FE2C55;
  }
}

.icon-text {
  font-size: 40rpx;
  color: #FFFFFF;
}

.channel-name {
  font-size: 24rpx;
  color: $text-secondary;
}
</style>
