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
          <text class="user-name">{{ userInfo.nickname || '旅行者' }}</text>
        </view>

        <!-- 分割线 -->
        <view class="card-divider dashed"></view>

        <!-- 日记内容 -->
        <view class="story-wrapper">
          <view class="story-card">
            <view class="story-content">
              <text class="story-title">{{ story.title || '旅行日记' }}</text>
              <image 
                v-if="story.image" 
                class="story-image" 
                :src="story.image" 
                mode="aspectFill" 
              />
              <view class="story-placeholder" v-else>
                <text class="placeholder-icon">📝</text>
              </view>
              <view class="story-footer">
                <text class="story-date">{{ story.day }}</text>
                <text class="story-weekday">{{ story.weekday }}</text>
              </view>
            </view>
            <!-- 位置标签 -->
            <view class="story-location" v-if="story.location">
              <text class="location-text">📍 {{ story.location }}</text>
            </view>
          </view>
        </view>

        <!-- 日记内容 -->
        <view class="card-description" v-if="story.content">
          <text class="desc-first">{{ story.content.charAt(0) }}</text>
          <text class="desc-text">{{ story.content.slice(1, 80) }}{{ story.content.length > 80 ? '...' : '' }}</text>
        </view>

        <!-- 分割线 -->
        <view class="card-divider dashed"></view>

        <!-- 底部信息 -->
        <view class="card-footer">
          <view class="footer-left">
            <view class="flag-icon">📖</view>
            <text class="app-name">趣途云迹</text>
            <text class="divider-line">丨</text>
            <text class="story-type">旅行故事</text>
          </view>
          <view class="footer-right">
            <text class="slogan-small">记录</text>
            <text class="slogan-large">每一段旅途的美好</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 背景色选择 -->
    <view class="color-picker">
      <text class="picker-title">选择卡片风格</text>
      <scroll-view scroll-x class="color-list">
        <view 
          class="color-item" 
          v-for="(color, index) in cardColors" 
          :key="index"
          :style="{ background: color.value }"
          :class="{ active: cardBackground === color.value }"
          @click="selectColor(color.value)"
        >
          <text class="color-check" v-if="cardBackground === color.value">✓</text>
        </view>
      </scroll-view>
    </view>

    <!-- 分享操作栏 -->
    <view class="share-actions">
      <view class="action-item" @click="saveImage">
        <view class="action-icon save">
          <view class="icon-save">
            <view class="save-arrow"></view>
            <view class="save-line"></view>
          </view>
        </view>
        <text class="action-label">保存图片</text>
      </view>
      <view class="action-item">
        <button class="share-button" open-type="share">
          <view class="action-icon wechat">
            <view class="icon-wechat"></view>
          </view>
        </button>
        <text class="action-label">微信好友</text>
      </view>
      <view class="action-item" @click="shareToMoments">
        <view class="action-icon moments">
          <view class="icon-moments">
            <view class="moments-inner"></view>
          </view>
        </view>
        <text class="action-label">朋友圈</text>
      </view>
      <view class="action-item" @click="shareMore">
        <view class="action-icon more">
          <view class="icon-more">
            <view class="more-dot"></view>
            <view class="more-dot"></view>
            <view class="more-dot"></view>
          </view>
        </view>
        <text class="action-label">更多</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShareAppMessage } from '@dcloudio/uni-app'

interface Story {
  id: number
  title?: string
  day: string
  weekday: string
  date: string
  year?: string
  image?: string
  content: string
  location?: string
}

// 状态栏高度
const statusBarHeight = ref(0)

// 卡片背景色列表
const cardColors = [
  { name: '暖阳', value: 'linear-gradient(180deg, #FFD89E 0%, #FFCB7C 100%)' },
  { name: '晴空', value: 'linear-gradient(180deg, #87CEEB 0%, #4A90D9 100%)' },
  { name: '薄暮', value: 'linear-gradient(180deg, #FFB5C5 0%, #FF8FA3 100%)' },
  { name: '森林', value: 'linear-gradient(180deg, #98D8AA 0%, #4CAF50 100%)' },
  { name: '星夜', value: 'linear-gradient(180deg, #667eea 0%, #764ba2 100%)' },
  { name: '简约', value: 'linear-gradient(180deg, #F5F5F5 0%, #E0E0E0 100%)' }
]

// 卡片背景色
const cardBackground = ref('linear-gradient(180deg, #FFD89E 0%, #FFCB7C 100%)')

// 用户信息
const userInfo = ref({
  avatar: '',
  nickname: '旅行者'
})

// 故事信息
const story = ref<Story>({
  id: 0,
  title: '旅行日记',
  day: '',
  weekday: '',
  date: '',
  content: '',
  location: ''
})

// 获取系统信息和参数
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  
  // 尝试从本地存储获取故事数据
  const storyData = uni.getStorageSync('shareStory')
  if (storyData) {
    story.value = JSON.parse(storyData)
    uni.removeStorageSync('shareStory')
  }
  
  // 获取用户信息
  loadUserInfo()
})

// 加载用户信息
const loadUserInfo = () => {
  const saved = uni.getStorageSync('userInfo')
  if (saved) {
    userInfo.value = saved
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 选择背景色
const selectColor = (color: string) => {
  cardBackground.value = color
}

// 保存图片
const saveImage = async () => {
  uni.showLoading({ title: '生成中...' })
  
  try {
    // 如果有图片，保存图片
    if (story.value.image) {
      await uni.saveImageToPhotosAlbum({
        filePath: story.value.image
      })
      uni.showToast({ title: '保存成功', icon: 'success' })
    } else {
      uni.showToast({ title: '暂无图片可保存', icon: 'none' })
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

// 更多分享
const shareMore = () => {
  uni.showToast({ title: '请先保存图片后分享', icon: 'none' })
}

// 微信分享
onShareAppMessage(() => {
  return {
    title: story.value.title || '我的旅行故事',
    path: `/pages/story/detail?id=${story.value.id}`,
    imageUrl: story.value.image || ''
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
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
}

.back-icon {
  font-size: 44rpx;
  color: #333;
  font-weight: bold;
  margin-top: -4rpx;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
}

.nav-right {
  width: 60rpx;
}

// 卡片容器
.card-container {
  padding: 30rpx;
}

.share-card {
  border-radius: 30rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 30rpx;
}

.user-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  
  image {
    width: 100%;
    height: 100%;
  }
  
  .avatar-placeholder {
    font-size: 32rpx;
  }
}

.user-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.card-divider {
  height: 2rpx;
  margin: 24rpx 0;
  
  &.dashed {
    border-top: 2rpx dashed rgba(0, 0, 0, 0.15);
  }
}

// 故事卡片
.story-wrapper {
  margin: 20rpx 0;
}

.story-card {
  background: rgba(255, 255, 255, 0.6);
  border-radius: 20rpx;
  padding: 24rpx;
}

.story-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.story-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  text-align: center;
}

.story-image {
  width: 100%;
  height: 300rpx;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
}

.story-placeholder {
  width: 100%;
  height: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12rpx;
  margin-bottom: 16rpx;
  
  .placeholder-icon {
    font-size: 60rpx;
  }
}

.story-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
  
  .story-date {
    font-size: 28rpx;
    font-weight: 600;
    color: #333;
  }
  
  .story-weekday {
    font-size: 26rpx;
    color: #666;
  }
}

.story-location {
  margin-top: 16rpx;
  padding: 10rpx 20rpx;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 20rpx;
  
  .location-text {
    font-size: 24rpx;
    color: #666;
  }
}

// 描述
.card-description {
  padding: 20rpx 0;
  
  .desc-first {
    font-size: 48rpx;
    font-weight: bold;
    color: #333;
    float: left;
    line-height: 1;
    margin-right: 8rpx;
  }
  
  .desc-text {
    font-size: 26rpx;
    color: #333;
    line-height: 1.8;
  }
}

// 底部
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 8rpx;
  
  .flag-icon {
    font-size: 32rpx;
  }
  
  .app-name {
    font-size: 24rpx;
    font-weight: 600;
    color: #333;
  }
  
  .divider-line {
    color: rgba(0, 0, 0, 0.3);
  }
  
  .story-type {
    font-size: 24rpx;
    color: #666;
  }
}

.footer-right {
  text-align: right;
  
  .slogan-small {
    display: block;
    font-size: 22rpx;
    color: #666;
  }
  
  .slogan-large {
    font-size: 24rpx;
    font-weight: 600;
    color: #333;
  }
}

// 颜色选择器
.color-picker {
  margin: 0 30rpx 20rpx;
  padding: 24rpx 30rpx;
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  
  .picker-title {
    font-size: 26rpx;
    color: #999;
    margin-bottom: 20rpx;
    display: block;
  }
  
  .color-list {
    white-space: nowrap;
    padding: 8rpx 0;
  }
  
  .color-item {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-right: 24rpx;
    border: 4rpx solid #fff;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
    position: relative;
    vertical-align: middle;
    
    &.active {
      border-color: #333;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.2);
    }
    
    .color-check {
      font-size: 36rpx;
      color: #fff;
      font-weight: bold;
      text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.4);
    }
  }
}

// 分享操作栏
.share-actions {
  display: flex;
  justify-content: center;
  gap: 40rpx;
  padding: 40rpx 30rpx;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  
  .share-button {
    padding: 0;
    margin: 0;
    background: transparent;
    border: none;
    line-height: 1;
    
    &::after {
      display: none;
    }
  }
}

.action-icon {
  width: 100rpx;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #F5F5F7;
  
  &.save {
    background: linear-gradient(135deg, #4A90D9, #67B8DE);
  }
  
  &.wechat {
    background: #07C160;
  }
  
  &.moments {
    background: linear-gradient(135deg, #FA9D3B, #FF6B00);
  }
  
  &.more {
    background: linear-gradient(135deg, #A0A0A5, #8E8E93);
  }
}

// 保存图标
.icon-save {
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .save-arrow {
    width: 0;
    height: 0;
    border-left: 12rpx solid transparent;
    border-right: 12rpx solid transparent;
    border-top: 16rpx solid #fff;
    margin-bottom: 4rpx;
  }
  
  .save-line {
    width: 32rpx;
    height: 4rpx;
    background: #fff;
    border-radius: 2rpx;
  }
}

// 微信图标
.icon-wechat {
  width: 44rpx;
  height: 36rpx;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 28rpx;
    height: 24rpx;
    background: #fff;
    border-radius: 50%;
  }
  
  &::after {
    content: '';
    position: absolute;
    right: 0;
    top: 0;
    width: 24rpx;
    height: 20rpx;
    background: #fff;
    border-radius: 50%;
  }
}

// 朋友圈图标
.icon-moments {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .moments-inner {
    width: 16rpx;
    height: 16rpx;
    background: #fff;
    border-radius: 50%;
  }
}

// 更多图标
.icon-more {
  display: flex;
  gap: 8rpx;
  
  .more-dot {
    width: 10rpx;
    height: 10rpx;
    background: #fff;
    border-radius: 50%;
  }
}

.action-label {
  font-size: 24rpx;
  color: #666;
}
</style>
