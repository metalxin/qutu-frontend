<template>
  <view class="settings-page">
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">个人设置</text>
      <view class="nav-right" :style="navRightStyle"></view>
    </view>

    <view class="section-card">
      <text class="section-title">消息提醒</text>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">行程提醒</text>
          <text class="setting-desc">出发前一天提醒</text>
        </view>
        <switch class="setting-switch" :checked="settings.notifications.tripReminder" @change="toggleNotification('tripReminder', ($event as any).detail.value)" color="#007AFF" />
      </view>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">新攻略推荐</text>
          <text class="setting-desc">适合你的攻略提醒</text>
        </view>
        <switch class="setting-switch" :checked="settings.notifications.newGuides" @change="toggleNotification('newGuides', ($event as any).detail.value)" color="#007AFF" />
      </view>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">系统消息</text>
          <text class="setting-desc">更新与功能公告</text>
        </view>
        <switch class="setting-switch" :checked="settings.notifications.systemMessages" @change="toggleNotification('systemMessages', ($event as any).detail.value)" color="#007AFF" />
      </view>
    </view>

    <view class="section-card">
      <text class="section-title">隐私设置</text>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">展示足迹</text>
          <text class="setting-desc">允许他人查看你的足迹</text>
        </view>
        <switch class="setting-switch" :checked="settings.privacy.showFootprints" @change="togglePrivacy('showFootprints', ($event as any).detail.value)" color="#007AFF" />
      </view>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">展示行程</text>
          <text class="setting-desc">允许他人查看你的行程</text>
        </view>
        <switch class="setting-switch" :checked="settings.privacy.showTrips" @change="togglePrivacy('showTrips', ($event as any).detail.value)" color="#007AFF" />
      </view>
    </view>

    <view class="section-card">
      <text class="section-title">地图设置</text>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">默认缩放</text>
          <text class="setting-desc">当前 {{ settings.mapSettings.defaultZoom }} 级</text>
        </view>
        <slider class="setting-slider" :value="settings.mapSettings.defaultZoom" min="10" max="18" step="1" activeColor="#007AFF" @change="changeZoom" />
      </view>
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title-text">显示路况</text>
          <text class="setting-desc">显示实时交通</text>
        </view>
        <switch class="setting-switch" :checked="settings.mapSettings.showTraffic" @change="toggleMapSetting('showTraffic', ($event as any).detail.value)" color="#007AFF" />
      </view>
      <view class="setting-item" @click="showMapStylePicker = true">
        <view class="setting-info">
          <text class="setting-title-text">地图样式</text>
          <text class="setting-desc">{{ currentMapStyleLabel }}</text>
        </view>
        <text class="arrow-icon">›</text>
      </view>
    </view>

    <view class="section-card">
      <text class="section-title">帮助与反馈</text>
      <view class="setting-item clickable" @click="goToService">
        <view class="setting-info">
          <text class="setting-title-text">AI 客服</text>
          <text class="setting-desc">智能问答，转人工客服</text>
        </view>
        <text class="arrow-icon">›</text>
      </view>
    </view>

    <view class="popup-mask" v-if="showMapStylePicker" @click="showMapStylePicker = false">
      <view class="popup-card" @click.stop>
        <view class="popup-header">
          <text class="popup-title">选择地图样式</text>
          <text class="popup-close" @click="showMapStylePicker = false">×</text>
        </view>
        <view class="popup-list">
          <view class="popup-item" v-for="style in mapStyleOptions" :key="style.value" :class="{ active: settings.mapSettings.mapStyle === style.value }" @click="selectMapStyle(style.value)">
            <text class="popup-text">{{ style.label }}</text>
            <text class="popup-check" v-if="settings.mapSettings.mapStyle === style.value">✓</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getUserSettings, updateUserSettings } from '@/api/modules/user'
import type { UserSettings } from '@/api/modules/user'

const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const showMapStylePicker = ref(false)

const settings = ref<UserSettings>({
  language: 'zh-CN',
  notifications: {
    tripReminder: true,
    newGuides: true,
    systemMessages: true
  },
  privacy: {
    showFootprints: true,
    showTrips: false
  },
  mapSettings: {
    defaultZoom: 14,
    showTraffic: false,
    mapStyle: 'normal'
  }
})

const mapStyleOptions = [
  { label: '标准地图', value: 'normal' },
  { label: '清新浅色', value: 'light' },
  { label: '夜间模式', value: 'dark' }
]

const currentMapStyleLabel = computed(() => {
  const match = mapStyleOptions.find(item => item.value === settings.value.mapSettings.mapStyle)
  return match?.label || '标准地图'
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
    return { width: menuButtonSpace.value + 'px' }
  }
  return {}
})

const loadSettings = async () => {
  const res = await getUserSettings()
  settings.value = res
}

const toggleNotification = (key: 'tripReminder' | 'newGuides' | 'systemMessages', value: boolean) => {
  settings.value.notifications = { ...settings.value.notifications, [key]: value }
  updateUserSettings({ notifications: settings.value.notifications })
}

const togglePrivacy = (key: 'showFootprints' | 'showTrips', value: boolean) => {
  settings.value.privacy = { ...settings.value.privacy, [key]: value }
  updateUserSettings({ privacy: settings.value.privacy })
}

const toggleMapSetting = (key: 'showTraffic', value: boolean) => {
  settings.value.mapSettings = { ...settings.value.mapSettings, [key]: value }
  updateUserSettings({ mapSettings: settings.value.mapSettings })
}

const changeZoom = (event: any) => {
  settings.value.mapSettings = { ...settings.value.mapSettings, defaultZoom: event.detail.value }
  updateUserSettings({ mapSettings: settings.value.mapSettings })
}

const selectMapStyle = (value: string) => {
  settings.value.mapSettings = { ...settings.value.mapSettings, mapStyle: value }
  updateUserSettings({ mapSettings: settings.value.mapSettings })
  showMapStylePicker.value = false
}

const goBack = () => {
  uni.navigateBack()
}

const goToService = () => {
  uni.navigateTo({ url: '/pages/service/index' })
}

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
  loadSettings()
})
</script>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  background: #F5F5F7;
}

.nav-bar {
  padding: 24rpx 24rpx 16rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
}

.nav-left,
.nav-right {
  width: 120rpx;
  display: flex;
  align-items: center;
}

.nav-right {
  justify-content: flex-end;
}

.back-icon {
  font-size: 40rpx;
  color: #1D1D1F;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.section-card {
  margin: 24rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.setting-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  flex: 1;
}

.setting-title-text {
  font-size: 26rpx;
  color: #1D1D1F;
}

.setting-desc {
  font-size: 22rpx;
  color: #86868B;
}

.setting-switch {
  transform: scale(0.8);
}

.setting-slider {
  flex: 1;
}

.setting-item.clickable {
  cursor: pointer;
  opacity: 0.9;
}

.arrow-icon {
  font-size: 32rpx;
  color: #C7C7CC;
}

.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
}

.popup-card {
  width: 100%;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  padding: 24rpx;
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16rpx;
}

.popup-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.popup-close {
  font-size: 32rpx;
  color: #86868B;
}

.popup-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.popup-item {
  padding: 20rpx;
  border-radius: 16rpx;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.popup-item.active {
  background: #E8F5FF;
}

.popup-text {
  font-size: 26rpx;
  color: #1D1D1F;
}

.popup-check {
  font-size: 26rpx;
  color: #007AFF;
}
</style>
