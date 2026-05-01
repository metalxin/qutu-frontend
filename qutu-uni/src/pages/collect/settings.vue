<template>
  <view class="settings-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">采集偏好设置</text>
      <view class="nav-right"></view>
    </view>

    <!-- 设置列表 -->
    <view class="settings-list">
      <!-- 优先抠图 -->
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title">采集时优先抠图</text>
          <text class="setting-desc">关闭后将默认使用原图采集</text>
        </view>
        <switch 
          class="setting-switch" 
          :checked="settings.preferCutout"
          @change="toggleCutout"
          color="#007AFF"
        />
      </view>

      <!-- 保存原图 -->
      <view class="setting-item">
        <view class="setting-info">
          <text class="setting-title">保存原图至相册</text>
          <text class="setting-desc">关闭后将不保存原图</text>
        </view>
        <switch 
          class="setting-switch" 
          :checked="settings.saveOriginal"
          @change="toggleSaveOriginal"
          color="#007AFF"
        />
      </view>

      <!-- 分割线 -->
      <view class="setting-divider"></view>

      <!-- 默认分组 -->
      <view class="setting-item" @click="showGroupPicker = true">
        <view class="setting-info">
          <text class="setting-title">默认分组</text>
          <text class="setting-desc">新采集将自动添加到该分组</text>
        </view>
        <view class="setting-value">
          <text class="value-text">{{ settings.defaultGroup || '未设置' }}</text>
          <text class="arrow-icon">›</text>
        </view>
      </view>

      <!-- 水印设置 -->
      <view class="setting-item" @click="showWatermarkSettings">
        <view class="setting-info">
          <text class="setting-title">水印设置</text>
          <text class="setting-desc">自定义分享时的水印样式</text>
        </view>
        <view class="setting-value">
          <text class="arrow-icon">›</text>
        </view>
      </view>

      <!-- 分割线 -->
      <view class="setting-divider"></view>

      <!-- 清除缓存 -->
      <view class="setting-item" @click="clearCache">
        <view class="setting-info">
          <text class="setting-title">清除缓存</text>
          <text class="setting-desc">当前缓存 {{ cacheSize }}</text>
        </view>
        <view class="setting-value">
          <text class="arrow-icon">›</text>
        </view>
      </view>
    </view>

    <!-- 分组选择弹窗 -->
    <view class="popup-mask" :class="{ show: showGroupPicker }" @click="showGroupPicker = false">
      <view class="popup-content" @click.stop>
        <view class="popup-header">
          <text class="popup-title">选择默认分组</text>
          <view class="popup-close" @click="showGroupPicker = false">
            <text class="close-icon">×</text>
          </view>
        </view>
        <scroll-view class="popup-list" scroll-y>
          <view 
            class="popup-item" 
            v-for="group in groups" 
            :key="group.id"
            :class="{ active: settings.defaultGroup === group.name }"
            @click="selectGroup(group)"
          >
            <text class="popup-item-text">{{ group.name }}</text>
            <text class="check-icon" v-if="settings.defaultGroup === group.name">✓</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 状态栏高度
const statusBarHeight = ref(0)

// 设置数据
const settings = ref({
  preferCutout: false,
  saveOriginal: true,
  defaultGroup: '',
  watermarkStyle: 'default'
})

// 分组列表
const groups = ref([
  { id: 1, name: '默认分组' },
  { id: 2, name: '旅行' },
  { id: 3, name: '美食' },
  { id: 4, name: '风景' }
])

// 缓存大小
const cacheSize = ref('0 MB')

// 弹窗状态
const showGroupPicker = ref(false)

// 获取系统信息
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  
  // 加载设置
  loadSettings()
  
  // 计算缓存
  calculateCache()
})

// 加载设置
const loadSettings = () => {
  const saved = uni.getStorageSync('collectSettings')
  if (saved) {
    settings.value = { ...settings.value, ...saved }
  }
}

// 保存设置
const saveSettings = () => {
  uni.setStorageSync('collectSettings', settings.value)
}

// 计算缓存
const calculateCache = () => {
  try {
    const info = uni.getStorageInfoSync()
    const sizeKB = info.currentSize || 0
    if (sizeKB < 1024) {
      cacheSize.value = `${sizeKB} KB`
    } else {
      cacheSize.value = `${(sizeKB / 1024).toFixed(1)} MB`
    }
  } catch (e) {
    cacheSize.value = '0 MB'
  }
}

// 切换优先抠图
const toggleCutout = (e: any) => {
  settings.value.preferCutout = e.detail.value
  saveSettings()
}

// 切换保存原图
const toggleSaveOriginal = (e: any) => {
  settings.value.saveOriginal = e.detail.value
  saveSettings()
}

// 选择分组
const selectGroup = (group: any) => {
  settings.value.defaultGroup = group.name
  showGroupPicker.value = false
  saveSettings()
}

// 水印设置
const showWatermarkSettings = () => {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

// 清除缓存
const clearCache = () => {
  uni.showModal({
    title: '清除缓存',
    content: '确定要清除所有缓存吗？',
    success: (res) => {
      if (res.confirm) {
        try {
          uni.clearStorageSync()
          calculateCache()
          uni.showToast({ title: '清除成功', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '清除失败', icon: 'none' })
        }
      }
    }
  })
}

// 返回
const goBack = () => {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  background: #F5F5F7;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  background: #F5F5F7;
}

.nav-left,
.nav-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: #1D1D1F;
  font-weight: 300;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.settings-list {
  padding: 32rpx;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 0;
  border-bottom: 1rpx solid #E5E5EA;
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #1D1D1F;
  display: block;
}

.setting-desc {
  font-size: 26rpx;
  color: #86868B;
  margin-top: 8rpx;
  display: block;
}

.setting-switch {
  transform: scale(0.85);
}

.setting-value {
  display: flex;
  align-items: center;
}

.value-text {
  font-size: 28rpx;
  color: #86868B;
  margin-right: 8rpx;
}

.arrow-icon {
  font-size: 32rpx;
  color: #C7C7CC;
}

.setting-divider {
  height: 32rpx;
}

// 弹窗
.popup-mask {
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

.popup-content {
  width: 100%;
  max-height: 70vh;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  transform: translateY(100%);
  transition: transform 0.3s ease;

  .popup-mask.show & {
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #E5E5EA;
}

.popup-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.popup-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 40rpx;
  color: #86868B;
}

.popup-list {
  max-height: 50vh;
  padding: 16rpx 0;
}

.popup-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;

  &.active {
    background: #F5F5F7;
  }
}

.popup-item-text {
  font-size: 32rpx;
  color: #1D1D1F;
}

.check-icon {
  font-size: 32rpx;
  color: #007AFF;
}
</style>
