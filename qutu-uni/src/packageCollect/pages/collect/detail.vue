<template>
  <view class="detail-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <view class="nav-actions" :style="navActionsStyle">
        <view class="action-btn" @click="shareCollection">
          <text class="action-icon">↗</text>
        </view>
        <view class="action-btn" @click="showMoreMenu = true">
          <text class="action-icon">···</text>
        </view>
      </view>
    </view>

    <scroll-view class="detail-content" scroll-y>
      <!-- 邮票卡片 -->
      <view class="stamp-section">
        <view class="stamp-card">
          <view class="stamp-border">
            <view class="stamp-content">
              <text class="stamp-title">{{ collection.title }}</text>
              <image 
                class="stamp-image" 
                :src="collection.image" 
                mode="aspectFill" 
                @click="previewImage"
              />
              <view class="stamp-footer">
                <text class="stamp-date">{{ collection.date }}</text>
                <text class="stamp-year">{{ collection.year }}</text>
              </view>
            </view>
          </view>
          
          <!-- 位置标签 -->
          <view class="stamp-location" v-if="collection.location">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ collection.location }}</text>
          </view>
        </view>
      </view>

      <!-- 描述 -->
      <view class="desc-section" v-if="collection.description">
        <text class="desc-text">{{ collection.description }}</text>
        <text class="desc-note" v-if="collection.aiGenerated">*信息由AI生成</text>
      </view>

      <!-- 标签 -->
      <view class="tags-section" v-if="collection.tags && collection.tags.length > 0">
        <view class="tag-item" v-for="(tag, index) in collection.tags" :key="index">
          <text class="tag-text"># {{ tag }}</text>
        </view>
      </view>

      <!-- 信息详情 -->
      <view class="info-section">
        <view class="info-item">
          <text class="info-label">分组</text>
          <text class="info-value">{{ collection.groupName || '默认分组' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">采集时间</text>
          <text class="info-value">{{ collection.createdAt }}</text>
        </view>
        <view class="info-item" v-if="collection.latitude && collection.longitude">
          <text class="info-label">坐标</text>
          <text class="info-value">{{ collection.latitude.toFixed(4) }}, {{ collection.longitude.toFixed(4) }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bottom-btn" @click="editCollection">
        <text class="btn-icon">✏️</text>
        <text class="btn-text">编辑</text>
      </view>
      <view class="bottom-btn primary" @click="shareCollection">
        <text class="btn-icon">↗</text>
        <text class="btn-text">分享</text>
      </view>
    </view>

    <!-- 更多菜单 -->
    <view class="menu-mask" :class="{ show: showMoreMenu }" @click="showMoreMenu = false">
      <view class="menu-content" @click.stop>
        <view class="menu-item" @click="editCollection">
          <text class="menu-icon">✏️</text>
          <text class="menu-text">编辑</text>
        </view>
        <view class="menu-item" @click="moveToGroup">
          <text class="menu-icon">📁</text>
          <text class="menu-text">移动到分组</text>
        </view>
        <view class="menu-item" @click="saveOriginal">
          <text class="menu-icon">💾</text>
          <text class="menu-text">保存原图</text>
        </view>
        <view class="menu-item danger" @click="deleteConfirm">
          <text class="menu-icon">🗑️</text>
          <text class="menu-text">删除</text>
        </view>
        <view class="menu-cancel" @click="showMoreMenu = false">
          <text class="cancel-text">取消</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getCollectionDetail, deleteCollection } from '@/api'
import type { CollectionDetail } from '@/api/modules/collect'

// 状态栏高度
const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)

// 采集ID
const collectionId = ref(0)

// 采集详情
const collection = ref<CollectionDetail>({
  id: 0,
  title: '',
  image: '',
  date: '',
  year: ''
})

// 菜单状态
const showMoreMenu = ref(false)

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

const navActionsStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingRight: menuButtonSpace.value + 'px' }
  }
  return {}
})

// 获取系统信息和参数
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
  
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  
  if (options.id) {
    collectionId.value = parseInt(options.id)
    loadDetail()
  }
})

// 加载详情
const loadDetail = async () => {
  try {
    const res = await getCollectionDetail(collectionId.value)
    if (res) {
      collection.value = res
    }
  } catch (e) {
    console.error('加载采集详情失败', e)
  }
}

// 预览图片
const previewImage = () => {
  uni.previewImage({
    urls: [collection.value.image],
    current: collection.value.image
  })
}

// 分享
const shareCollection = () => {
  showMoreMenu.value = false
  uni.navigateTo({
    url: `/pages/collect/share?id=${collectionId.value}`
  })
}

// 编辑
const editCollection = () => {
  showMoreMenu.value = false
  uni.navigateTo({
    url: `/pages/collect/edit?id=${collectionId.value}&image=${encodeURIComponent(collection.value.image)}`
  })
}

// 移动到分组
const moveToGroup = () => {
  showMoreMenu.value = false
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

// 保存原图
const saveOriginal = async () => {
  showMoreMenu.value = false
  const imagePath = collection.value.originalImage || collection.value.image
  
  try {
    await uni.saveImageToPhotosAlbum({
      filePath: imagePath
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

// 删除确认
const deleteConfirm = () => {
  showMoreMenu.value = false
  uni.showModal({
    title: '删除采集',
    content: '确定要删除这个采集吗？删除后无法恢复。',
    confirmColor: '#FF3B30',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteCollection(collectionId.value)
          uni.showToast({ title: '删除成功', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
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
.detail-page {
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

.nav-left {
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

.nav-actions {
  display: flex;
  gap: 24rpx;
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon {
  font-size: 36rpx;
  color: #1D1D1F;
}

.detail-content {
  height: calc(100vh - 200rpx);
  padding-bottom: 120rpx;
}

// 邮票卡片
.stamp-section {
  padding: 32rpx;
}

.stamp-card {
  max-width: 600rpx;
  margin: 0 auto;
}

.stamp-border {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 4rpx;
  background-image: 
    radial-gradient(circle at 0 8rpx, transparent 8rpx, #FFFFFF 8rpx),
    radial-gradient(circle at 100% 8rpx, transparent 8rpx, #FFFFFF 8rpx);
  background-size: 24rpx 16rpx;
  background-position: left top, right top;
  background-repeat: repeat-y;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.stamp-content {
  padding: 32rpx;
}

.stamp-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1D1D1F;
  margin-bottom: 24rpx;
  display: block;
}

.stamp-image {
  width: 100%;
  height: 400rpx;
  border-radius: 12rpx;
  background: #F0F0F0;
}

.stamp-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 24rpx;
}

.stamp-date {
  font-size: 56rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.stamp-year {
  font-size: 28rpx;
  color: #86868B;
  writing-mode: vertical-rl;
}

.stamp-location {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  margin-top: 16rpx;
}

.location-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.location-text {
  font-size: 26rpx;
  color: #86868B;
}

// 描述
.desc-section {
  padding: 0 32rpx 24rpx;
}

.desc-text {
  font-size: 30rpx;
  color: #1D1D1F;
  line-height: 1.6;
}

.desc-note {
  display: block;
  font-size: 24rpx;
  color: #86868B;
  margin-top: 12rpx;
}

// 标签
.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 0 32rpx 24rpx;
}

.tag-item {
  padding: 8rpx 20rpx;
  background: #E3F2FD;
  border-radius: 100rpx;
}

.tag-text {
  font-size: 26rpx;
  color: #007AFF;
}

// 信息详情
.info-section {
  background: #FFFFFF;
  margin: 0 32rpx;
  border-radius: 16rpx;
  padding: 16rpx 24rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F0F0F0;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  font-size: 28rpx;
  color: #86868B;
}

.info-value {
  font-size: 28rpx;
  color: #1D1D1F;
}

// 底部操作栏
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 24rpx;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.bottom-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 24rpx;
  background: #F5F5F7;
  border-radius: 16rpx;

  &.primary {
    background: #007AFF;

    .btn-icon,
    .btn-text {
      color: #FFFFFF;
    }
  }
}

.btn-icon {
  font-size: 32rpx;
}

.btn-text {
  font-size: 30rpx;
  font-weight: 500;
  color: #1D1D1F;
}

// 更多菜单
.menu-mask {
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

.menu-content {
  width: 100%;
  background: #F5F5F7;
  border-radius: 24rpx 24rpx 0 0;
  padding: 16rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.3s ease;

  .menu-mask.show & {
    transform: translateY(0);
  }
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: #FFFFFF;
  margin-bottom: 2rpx;

  &:first-child {
    border-radius: 16rpx 16rpx 0 0;
  }

  &:nth-last-child(2) {
    border-radius: 0 0 16rpx 16rpx;
    margin-bottom: 16rpx;
  }

  &.danger {
    .menu-text {
      color: #FF3B30;
    }
  }
}

.menu-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.menu-text {
  font-size: 32rpx;
  color: #1D1D1F;
}

.menu-cancel {
  padding: 32rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  text-align: center;
}

.cancel-text {
  font-size: 32rpx;
  color: #007AFF;
  font-weight: 500;
}
</style>
