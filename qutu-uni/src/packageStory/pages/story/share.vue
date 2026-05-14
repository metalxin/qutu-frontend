<template>
  <view class="share-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="navBarStyle">
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
            <image :src="userInfo.avatar || 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSI1MCIgcj0iNTAiIGZpbGw9IiNFNUU1RUEiLz48cGF0aCBkPSJNNTAgMzVjLTggMC0xNCA2LTE0IDE0czYgMTQgMTQgMTQgMTQtNiAxNC0xNC02LTE0LTE0LTE0em0wIDM0Yy0xMiAwLTIyIDYtMjIgMTJ2NGg0NHYtNGMwLTYtMTAtMTItMjItMTJ6IiBmaWxsPSIjODg4ODhCIi8+PC9zdmc+'" mode="aspectFill" />
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

        <!-- 心情天气 -->
        <view class="card-mood" v-if="story.moodIcon || story.weatherIcon">
          <text class="mood-item" v-if="story.moodIcon">{{ story.moodIcon }} {{ story.mood }}</text>
          <text class="mood-item" v-if="story.weatherIcon">{{ story.weatherIcon }} {{ story.weather }}</text>
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
          :class="{ active: selectedColorIndex === index }"
          @click="selectColor(index)"
        >
          <text class="color-check" v-if="selectedColorIndex === index">✓</text>
        </view>
      </scroll-view>
    </view>

    <!-- 分享操作栏 -->
    <view class="share-actions">
      <view class="action-item" @click="savePoster">
        <view class="action-icon save">
          <text class="icon-text">↓</text>
        </view>
        <text class="action-label">生成海报</text>
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
      <view class="action-item" @click="copyStoryLink">
        <view class="action-icon link">
          <text class="icon-text">⎘</text>
        </view>
        <text class="action-label">复制链接</text>
      </view>
    </view>

    <!-- 海报预览 -->
    <view class="poster-preview" v-if="posterImagePath">
      <view class="poster-mask" @tap="posterImagePath = ''"></view>
      <view class="poster-content">
        <image class="poster-image" :src="posterImagePath" mode="widthFix" />
        <view class="poster-actions">
          <view class="poster-btn" @tap="savePosterToAlbum">
            <text class="poster-btn-text">保存到相册</text>
          </view>
          <view class="poster-btn secondary" @tap="posterImagePath = ''">
            <text class="poster-btn-text">关闭</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 海报 Canvas（隐藏） -->
    <canvas id="storyPosterCanvas" type="2d" style="position:fixed;left:-9999px;top:-9999px;width:600px;height:900px;"></canvas>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import { generateStoryPoster, saveImageToAlbum, copyShareLink, trackShare } from '../../utils/share'

const instance = getCurrentInstance()

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
  mood?: string
  moodIcon?: string
  weather?: string
  weatherIcon?: string
}

// 状态栏高度
const statusBarHeight = ref(0)
const navBarHeight = ref(44)

const navBarStyle = computed(() => {
  const style: Record<string, string> = {
    paddingTop: statusBarHeight.value + 'px'
  }
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      const navHeight = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
      navBarHeight.value = navHeight
    }
  } catch (e) {}
  // #endif
  return style
})

// 卡片背景色列表
const cardColors = [
  { name: '暖阳', value: 'linear-gradient(180deg, #FFD89E 0%, #FFCB7C 100%)' },
  { name: '晴空', value: 'linear-gradient(180deg, #87CEEB 0%, #4A90D9 100%)' },
  { name: '薄暮', value: 'linear-gradient(180deg, #FFB5C5 0%, #FF8FA3 100%)' },
  { name: '森林', value: 'linear-gradient(180deg, #98D8AA 0%, #4CAF50 100%)' },
  { name: '星夜', value: 'linear-gradient(180deg, #667eea 0%, #764ba2 100%)' },
  { name: '简约', value: 'linear-gradient(180deg, #F5F5F5 0%, #E0E0E0 100%)' }
]

// 选中色索引
const selectedColorIndex = ref(0)

// 卡片背景色
const cardBackground = ref(cardColors[0].value)

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

// 海报图片路径
const posterImagePath = ref('')

// 获取系统信息和参数
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44

  // 尝试从本地存储获取故事数据
  const storyData = uni.getStorageSync('shareStory')
  if (storyData) {
    try {
      story.value = JSON.parse(storyData)
    } catch {}
    uni.removeStorageSync('shareStory')
  }

  // 获取用户信息
  loadUserInfo()

  // 获取页面参数中的ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  if (currentPage?.options?.id) {
    story.value.id = parseInt(currentPage.options.id)
  }
})

// 加载用户信息
const loadUserInfo = () => {
  try {
    const saved = uni.getStorageSync('userInfo')
    if (saved && typeof saved === 'object') {
      userInfo.value = saved
    }
  } catch {}
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 选择背景色
const selectColor = (index: number) => {
  selectedColorIndex.value = index
  cardBackground.value = cardColors[index].value
}

// 生成海报
const savePoster = async () => {
  uni.showLoading({ title: '生成海报中...' })
  try {
    const path = await generateStoryPoster('storyPosterCanvas', {
      title: story.value.title || '旅行日记',
      content: story.value.content || '',
      imageUrl: story.value.image,
      location: story.value.location,
      date: story.value.day,
      weekday: story.value.weekday,
      moodIcon: story.value.moodIcon,
      weatherIcon: story.value.weatherIcon,
      userName: userInfo.value.nickname || '旅行者'
    }, instance?.proxy)
    posterImagePath.value = path
    uni.hideLoading()
  } catch (e) {
    uni.hideLoading()
    console.error('生成海报失败:', e)
    uni.showToast({ title: '海报生成失败', icon: 'none' })
  }
}

// 保存海报到相册
const savePosterToAlbum = async () => {
  if (!posterImagePath.value) return
  try {
    await saveImageToAlbum(posterImagePath.value)
    uni.showToast({ title: '海报已保存到相册', icon: 'success' })
    trackShare('save_poster', `/packageStory/pages/story/share?id=${story.value.id}`)
  } catch (e) {
    console.error('保存海报失败:', e)
  }
}

// 分享到朋友圈（引导保存海报）
const shareToMoments = () => {
  savePoster()
}

// 复制链接
const copyStoryLink = () => {
  const path = `/packageStory/pages/story/detail?id=${story.value.id}`
  copyShareLink(path).then(() => {
    uni.showToast({ title: '链接已复制', icon: 'success' })
  }).catch(() => {
    uni.setClipboardData({
      data: path,
      success: () => uni.showToast({ title: '链接已复制', icon: 'success' })
    })
  })
  trackShare('copy_link', path)
}

// 微信分享
onShareAppMessage(() => {
  trackShare('wechat_friend', `/packageStory/pages/story/detail?id=${story.value.id}`)
  return {
    title: story.value.title || '我的旅行故事',
    path: `/packageStory/pages/story/detail?id=${story.value.id}&from=share`,
    imageUrl: story.value.image || ''
  }
})

// 朋友圈分享
onShareTimeline(() => {
  trackShare('moments', `/packageStory/pages/story/detail?id=${story.value.id}`)
  return {
    title: story.value.title || '我的旅行故事',
    path: `/packageStory/pages/story/detail?id=${story.value.id}&from=share`,
    imageUrl: story.value.image || ''
  }
})
</script>

<style lang="scss" scoped>
.share-page {
  min-height: 100vh;
  background: #F5F5F7;
  padding-bottom: env(safe-area-inset-bottom);
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

// 心情天气
.card-mood {
  display: flex;
  gap: 20rpx;
  padding: 8rpx 0;

  .mood-item {
    font-size: 26rpx;
    color: #666;
    background: rgba(0, 0, 0, 0.05);
    padding: 6rpx 16rpx;
    border-radius: 16rpx;
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
  justify-content: space-around;
  padding: 40rpx 30rpx;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
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

  &.save {
    background: linear-gradient(135deg, #4A90D9, #67B8DE);
  }

  &.wechat {
    background: #07C160;
  }

  &.moments {
    background: linear-gradient(135deg, #FA9D3B, #FF6B00);
  }

  &.link {
    background: #5856D6;
  }
}

.icon-text {
  font-size: 36rpx;
  color: #fff;
}

.action-label {
  font-size: 24rpx;
  color: #666;
}

// 海报预览
.poster-preview {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;
}

.poster-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
}

.poster-content {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
}

.poster-image {
  width: 100%;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.3);
}

.poster-actions {
  display: flex;
  gap: 24rpx;
  width: 100%;
}

.poster-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #4A90D9, #67B8DE);

  &.secondary {
    background: rgba(255, 255, 255, 0.2);
  }
}

.poster-btn-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}
</style>
