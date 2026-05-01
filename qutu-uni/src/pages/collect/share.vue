<template>
  <view class="share-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">分享</text>
      <view class="nav-right"></view>
    </view>

    <!-- 分享卡片预览 -->
    <view class="card-container">
      <view class="share-card" :style="{ background: cardBackground }">
        <!-- 用户信息 -->
        <view class="card-header">
          <view class="user-avatar">
            <image v-if="userInfo.avatar" :src="userInfo.avatar" mode="aspectFill" />
            <text class="avatar-placeholder" v-else>👤</text>
          </view>
          <text class="user-name">{{ userInfo.nickname || '芒果派' }}</text>
        </view>

        <!-- 分割线 -->
        <view class="card-divider dashed"></view>

        <!-- 邮票内容 -->
        <view class="stamp-wrapper">
          <view class="stamp-card">
            <view class="stamp-content">
              <text class="stamp-title">{{ collection.title }}</text>
              <image class="stamp-image" :src="collection.image" mode="aspectFill" />
              <view class="stamp-footer">
                <text class="stamp-date">{{ collection.date }}</text>
                <text class="stamp-year">{{ collection.year }}</text>
                <view class="play-btn" v-if="collection.hasVideo">
                  <text class="play-icon">▷</text>
                </view>
              </view>
            </view>
            <!-- 位置标签 -->
            <view class="stamp-location" v-if="collection.location">
              <text class="location-text">{{ collection.location }}</text>
            </view>
          </view>
        </view>

        <!-- 描述 -->
        <view class="card-description" v-if="collection.description">
          <text class="desc-first">{{ collection.description.charAt(0) }}</text>
          <text class="desc-text">{{ collection.description.slice(1) }}</text>
          <text class="desc-note" v-if="collection.aiGenerated">*信息由AI生成</text>
        </view>

        <!-- 分割线 -->
        <view class="card-divider dashed"></view>

        <!-- 底部信息 -->
        <view class="card-footer">
          <view class="footer-left">
            <view class="flag-icon">🚩</view>
            <text class="app-name">圆周旅迹</text>
            <text class="divider-line">丨</text>
            <text class="collect-type">采集</text>
          </view>
          <view class="footer-right">
            <text class="slogan-small">每个人</text>
            <text class="slogan-large">都是生活的收藏家</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 分享操作栏 -->
    <view class="share-actions">
      <view class="action-item" @click="saveImage">
        <view class="action-icon">
          <text class="icon-text">↓</text>
        </view>
        <text class="action-label">保存图片</text>
      </view>
      <view class="action-item">
        <button class="share-button" open-type="share">
          <view class="action-icon wechat">
            <text class="icon-text">👥</text>
          </view>
        </button>
        <text class="action-label">微信好友</text>
      </view>
      <view class="action-item" @click="shareToMoments">
        <view class="action-icon moments">
          <text class="icon-text">◎</text>
        </view>
        <text class="action-label">朋友圈</text>
      </view>
      <view class="action-item" @click="shareToXiaohongshu">
        <view class="action-icon xiaohongshu">
          <text class="icon-text">小红书</text>
        </view>
        <text class="action-label">小红书</text>
      </view>
      <view class="action-item" @click="shareMore">
        <view class="action-icon more">
          <text class="icon-text">···</text>
        </view>
        <text class="action-label">更多</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShareAppMessage } from '@dcloudio/uni-app'

// 状态栏高度
const statusBarHeight = ref(0)

// 卡片背景色
const cardBackground = ref('linear-gradient(180deg, #FFD89E 0%, #FFCB7C 100%)')

// 用户信息
const userInfo = ref({
  avatar: '',
  nickname: '芒果派'
})

// 采集信息
const collection = ref({
  id: 1,
  title: '代码编辑界面',
  image: '/static/temp/code.jpg',
  date: '11/30',
  year: '2025',
  location: '神秘的采集地',
  description: '屏幕上显示着结构化的代码文本，可能用于软件开发或网页设计。',
  aiGenerated: true,
  hasVideo: false
})

// 获取系统信息和参数
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  
  if (options.id) {
    loadCollection(options.id)
  }
  
  // 获取用户信息
  loadUserInfo()
})

// 加载采集信息
const loadCollection = (id: string) => {
  // TODO: 从API加载采集信息
}

// 加载用户信息
const loadUserInfo = () => {
  const saved = uni.getStorageSync('userInfo')
  if (saved) {
    userInfo.value = saved
  }
}

// 保存图片
const saveImage = async () => {
  uni.showLoading({ title: '生成中...' })
  
  try {
    // 这里应该使用 canvas 绘制分享卡片并保存
    // 简化处理：直接保存原图
    if (collection.value.image) {
      await uni.saveImageToPhotosAlbum({
        filePath: collection.value.image
      })
      uni.showToast({ title: '保存成功', icon: 'success' })
    }
  } catch (e) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

// 分享到朋友圈
const shareToMoments = () => {
  uni.showToast({ title: '请先保存图片后分享', icon: 'none' })
}

// 分享到小红书
const shareToXiaohongshu = () => {
  uni.showToast({ title: '请先保存图片后分享', icon: 'none' })
}

// 更多分享
const shareMore = () => {
  uni.showToast({ title: '请先保存图片后分享', icon: 'none' })
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 配置分享
onShareAppMessage(() => {
  return {
    title: collection.value.title,
    path: `/pages/collect/detail?id=${collection.value.id}`,
    imageUrl: collection.value.image
  }
})
</script>

<style lang="scss" scoped>
.share-page {
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

// 卡片容器
.card-container {
  padding: 32rpx;
}

.share-card {
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

// 用户信息
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.user-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
  margin-right: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar image {
  width: 100%;
  height: 100%;
}

.avatar-placeholder {
  font-size: 32rpx;
}

.user-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #1D1D1F;
}

// 分割线
.card-divider {
  height: 2rpx;
  margin: 24rpx 0;

  &.dashed {
    border-bottom: 2rpx dashed rgba(139, 119, 90, 0.3);
  }
}

// 邮票
.stamp-wrapper {
  padding: 24rpx 0;
}

.stamp-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  // 邮票锯齿边缘
  background-image: 
    radial-gradient(circle at 0 8rpx, transparent 8rpx, #FFFFFF 8rpx),
    radial-gradient(circle at 100% 8rpx, transparent 8rpx, #FFFFFF 8rpx);
  background-size: 20rpx 16rpx;
  background-position: left top, right top;
  background-repeat: repeat-y;
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
  align-items: flex-end;
  margin-top: 24rpx;
  position: relative;
}

.stamp-date {
  font-size: 56rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.stamp-year {
  font-size: 24rpx;
  color: #86868B;
  writing-mode: vertical-rl;
  margin-left: 16rpx;
}

.play-btn {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  font-size: 24rpx;
  color: #86868B;
}

.stamp-location {
  padding: 16rpx 32rpx;
  background: rgba(0, 0, 0, 0.02);
}

.location-text {
  font-size: 24rpx;
  color: #86868B;
  writing-mode: vertical-rl;
  text-align: center;
}

// 描述
.card-description {
  padding: 16rpx 0;
}

.desc-first {
  font-size: 48rpx;
  font-weight: 700;
  color: #5D4E3A;
  float: left;
  margin-right: 8rpx;
  line-height: 1;
}

.desc-text {
  font-size: 28rpx;
  color: #5D4E3A;
  line-height: 1.6;
}

.desc-note {
  display: block;
  font-size: 22rpx;
  color: #A0977D;
  margin-top: 12rpx;
}

// 底部
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.footer-left {
  display: flex;
  align-items: center;
}

.flag-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.app-name {
  font-size: 26rpx;
  font-weight: 500;
  color: #5D4E3A;
}

.divider-line {
  font-size: 24rpx;
  color: #A0977D;
  margin: 0 8rpx;
}

.collect-type {
  font-size: 26rpx;
  color: #5D4E3A;
}

.footer-right {
  text-align: right;
}

.slogan-small {
  font-size: 20rpx;
  color: #A0977D;
  display: block;
}

.slogan-large {
  font-size: 24rpx;
  color: #5D4E3A;
  font-style: italic;
}

// 分享操作栏
.share-actions {
  display: flex;
  justify-content: space-around;
  padding: 48rpx 32rpx;
  background: #FFFFFF;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;

  &.wechat {
    background: #07C160;
    
    .icon-text {
      color: #FFFFFF;
    }
  }

  &.moments {
    background: #F5F5F7;
  }

  &.xiaohongshu {
    background: #FF2442;
    
    .icon-text {
      color: #FFFFFF;
      font-size: 20rpx;
      font-weight: 600;
    }
  }
}

.icon-text {
  font-size: 36rpx;
  color: #1D1D1F;
}

.action-label {
  font-size: 24rpx;
  color: #86868B;
}

.share-button {
  padding: 0;
  margin: 0;
  background: transparent;
  border: none;
  line-height: 1;

  &::after {
    border: none;
  }
}
</style>
