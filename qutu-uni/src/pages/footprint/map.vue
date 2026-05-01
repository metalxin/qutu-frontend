<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#FFFFFF" />
      </view>
    </view>

    <!-- 主视觉区域 -->
    <view class="hero-section" :class="mapType">
      <view class="hero-content">
        <view class="hero-title-wrapper">
          <text class="hero-title">{{ mapInfo.title }}</text>
          <view class="firework-icon" v-if="mapType === 'spring'">🎆</view>
        </view>
        <view class="hero-subtitle">
          <text class="subtitle-icon">💬</text>
          <text class="subtitle-text">「{{ mapInfo.slogan }}」</text>
        </view>
      </view>
      <view class="hero-decoration">
        <text class="deco-icon">👆</text>
      </view>
    </view>

    <scroll-view class="content" scroll-y :show-scrollbar="false">
      <!-- 精选示例 -->
      <view class="examples-section">
        <view class="section-title">
          <text class="title-arrow">›</text>
          <text class="title-text">精选示例</text>
        </view>

        <scroll-view class="examples-scroll" scroll-x :show-scrollbar="false">
          <view class="examples-list">
            <view 
              class="example-card" 
              v-for="example in examples" 
              :key="example.id"
              @click="viewExample(example)"
            >
              <view class="example-map" :style="{ background: example.bgColor }">
                <view class="map-china">
                  <!-- 简化的中国地图轮廓 -->
                  <view class="china-shape"></view>
                  <view class="map-markers">
                    <view 
                      class="marker" 
                      v-for="(marker, idx) in example.markers" 
                      :key="idx"
                      :style="{ left: marker.x + '%', top: marker.y + '%' }"
                    >
                      <text class="marker-emoji">{{ marker.emoji }}</text>
                    </view>
                  </view>
                </view>
                <view class="map-count">
                  <text class="count-number">{{ example.count }}</text>
                  <text class="count-label">地点</text>
                </view>
                <view class="map-mascot">
                  <text class="mascot-emoji">🐲</text>
                </view>
              </view>
              <view class="example-info">
                <text class="example-title">{{ example.title }}</text>
                <text class="example-desc">{{ example.desc }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 底部间距 -->
      <view class="bottom-space"></view>
    </scroll-view>

    <!-- 底部创建按钮 -->
    <view class="bottom-action">
      <view class="create-btn" @click="showThemePopup = true">
        <SFIcon name="plus" :size="32" color="#FFFFFF" />
        <text class="btn-text">创建我的点亮地图</text>
      </view>
    </view>

    <!-- 自定义主题弹窗 -->
    <view class="popup-overlay" v-if="showThemePopup" @click="showThemePopup = false">
      <view class="theme-popup" @click.stop>
        <view class="popup-header">
          <text class="popup-title">自定义主题</text>
          <view class="popup-close" @click="showThemePopup = false">
            <SFIcon name="close" :size="36" color="#1D1D1F" />
          </view>
        </view>

        <view class="popup-input">
          <input 
            class="theme-input" 
            v-model="customTheme" 
            placeholder="输入自定义主题（9字内）"
            maxlength="9"
          />
          <view class="input-btn" :class="{ active: customTheme }" @click="confirmCustomTheme">
            <text class="btn-text">确定</text>
          </view>
        </view>

        <view class="theme-recommend">
          <text class="recommend-title">热门推荐</text>
          <view class="theme-list">
            <view 
              class="theme-item" 
              v-for="theme in recommendThemes" 
              :key="theme.id"
              @click="selectTheme(theme)"
            >
              <text class="theme-emoji">{{ theme.emoji }}</text>
              <view class="theme-info">
                <text class="theme-name">{{ theme.name }}</text>
                <text class="theme-desc">{{ theme.desc }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

interface Example {
  id: number
  title: string
  desc: string
  count: number
  bgColor: string
  markers: { x: number; y: number; emoji: string }[]
}

interface Theme {
  id: number
  name: string
  desc: string
  emoji: string
}

// 地图类型
const mapType = ref('spring')

// 地图信息
const mapInfo = computed(() => {
  const infoMap: Record<string, { title: string; slogan: string }> = {
    spring: {
      title: '春节玩乐地图',
      slogan: '年味在路上 点亮你的 新春足迹！'
    },
    china: {
      title: '中国漫游者指南',
      slogan: '点亮你去过的省市自治区'
    },
    world: {
      title: '世界探险家手册',
      slogan: '点亮你在世界上去过的国家地区'
    }
  }
  return infoMap[mapType.value] || infoMap.spring
})

// 精选示例
const examples = ref<Example[]>([
  {
    id: 1,
    title: '新春看神仙地图',
    desc: '这个春节，你去过哪些庙会和游神祈福活动？把他们标记下来吧！🐲',
    count: 33,
    bgColor: 'linear-gradient(180deg, #FFE4E4 0%, #FFCECE 100%)',
    markers: [
      { x: 70, y: 35, emoji: '🎭' },
      { x: 75, y: 40, emoji: '🏮' },
      { x: 80, y: 45, emoji: '🐲' },
      { x: 72, y: 50, emoji: '🎊' }
    ]
  },
  {
    id: 2,
    title: '新春贪吃地图',
    desc: '过年啦，你都吃了哪些美食？标记下来吧！😋',
    count: 52,
    bgColor: 'linear-gradient(180deg, #FFE4E4 0%, #FFCECE 100%)',
    markers: [
      { x: 65, y: 45, emoji: '🍜' },
      { x: 70, y: 50, emoji: '🥟' },
      { x: 75, y: 55, emoji: '🍲' }
    ]
  },
  {
    id: 3,
    title: '新春玩乐地图',
    desc: '春节假期都去哪里玩了？记录下来！🧧',
    count: 28,
    bgColor: 'linear-gradient(180deg, #FFE4E4 0%, #FFCECE 100%)',
    markers: [
      { x: 60, y: 40, emoji: '🎡' },
      { x: 68, y: 48, emoji: '🏔️' }
    ]
  }
])

// 主题弹窗
const showThemePopup = ref(false)
const customTheme = ref('')

// 热门推荐主题
const recommendThemes = ref<Theme[]>([
  { id: 1, name: '新春看神仙地图', desc: '记录春节各地的喜庆民俗活动', emoji: '🐲' },
  { id: 2, name: '新春贪吃地图', desc: '记录过年旅行时吃到的美食', emoji: '😋' },
  { id: 3, name: '新春玩乐地图', desc: '记录过年吃喝玩乐的足迹', emoji: '🧧' },
  { id: 4, name: '新春求福气地图', desc: '求财求缘求健康，去哪个寺庙最灵验？', emoji: '🎋' },
  { id: 5, name: '新春庙会地图', desc: '现在的庙会，审美越来越在线了', emoji: '🎭' },
  { id: 6, name: '新春旅行地图', desc: '每个假期都要出去走走', emoji: '🚗' },
  { id: 7, name: '新春三件套地图', desc: '美甲美睫美发店，通通都要做一遍！', emoji: '💅' }
])

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 查看示例
const viewExample = (example: Example) => {
  uni.showToast({
    title: '查看示例：' + example.title,
    icon: 'none'
  })
}

// 选择主题
const selectTheme = (theme: Theme) => {
  showThemePopup.value = false
  uni.navigateTo({
    url: `/pages/footprint/create?theme=${encodeURIComponent(theme.name)}`
  })
}

// 确认自定义主题
const confirmCustomTheme = () => {
  if (!customTheme.value) return
  showThemePopup.value = false
  uni.navigateTo({
    url: `/pages/footprint/create?theme=${encodeURIComponent(customTheme.value)}`
  })
}

// 页面加载获取参数
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  if (currentPage?.options?.type) {
    mapType.value = currentPage.options.type
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #FFF8E7;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 60rpx 32rpx 20rpx;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 主视觉区域
.hero-section {
  padding: 160rpx 32rpx 60rpx;
  position: relative;
  overflow: hidden;
  
  &.spring {
    background: linear-gradient(180deg, #FF6B6B 0%, #FF8E53 100%);
  }
  
  &.china {
    background: linear-gradient(180deg, #8B7BA8 0%, #A890C8 100%);
  }
  
  &.world {
    background: linear-gradient(180deg, #4ECDC4 0%, #44A08D 100%);
  }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.hero-title {
  font-size: 72rpx;
  font-weight: 800;
  color: #FFEB3B;
  text-shadow: 4rpx 4rpx 0 rgba(0, 0, 0, 0.1);
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

.firework-icon {
  font-size: 48rpx;
  animation: sparkle 1s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

.hero-subtitle {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 20rpx;
}

.subtitle-icon {
  font-size: 28rpx;
}

.subtitle-text {
  font-size: 28rpx;
  color: #FFFFFF;
}

.hero-decoration {
  position: absolute;
  right: 40rpx;
  top: 120rpx;
}

.deco-icon {
  font-size: 60rpx;
  animation: float 2s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10rpx); }
}

.content {
  height: calc(100vh - 400rpx);
}

// 精选示例
.examples-section {
  padding: 40rpx 0 40rpx 32rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 24rpx;
}

.title-arrow {
  font-size: 32rpx;
  color: #FF6B6B;
  font-weight: 700;
}

.title-text {
  font-size: 32rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.examples-scroll {
  white-space: nowrap;
}

.examples-list {
  display: inline-flex;
  gap: 20rpx;
  padding-right: 32rpx;
}

.example-card {
  width: 400rpx;
  display: inline-block;
  vertical-align: top;
  white-space: normal;
}

.example-map {
  height: 360rpx;
  border-radius: 24rpx;
  border: 4rpx solid #E8D89A;
  position: relative;
  overflow: hidden;
}

.map-china {
  width: 100%;
  height: 100%;
  position: relative;
}

.china-shape {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 70%;
  background: rgba(255, 100, 100, 0.3);
  border-radius: 40% 60% 50% 50% / 50% 50% 60% 40%;
}

.map-markers {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.marker {
  position: absolute;
  transform: translate(-50%, -50%);
}

.marker-emoji {
  font-size: 32rpx;
}

.map-count {
  position: absolute;
  left: 20rpx;
  bottom: 20rpx;
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.count-number {
  font-size: 48rpx;
  font-weight: 800;
  color: #1D1D1F;
}

.count-label {
  font-size: 24rpx;
  color: #666;
}

.map-mascot {
  position: absolute;
  right: 20rpx;
  bottom: 20rpx;
}

.mascot-emoji {
  font-size: 48rpx;
}

.example-info {
  padding: 16rpx 0;
}

.example-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 8rpx;
}

.example-desc {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 1.4;
}

.bottom-space {
  height: 160rpx;
}

// 底部创建按钮
.bottom-action {
  position: fixed;
  bottom: 60rpx;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 48rpx;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 30rpx rgba(255, 107, 107, 0.4);
  border: 4rpx solid #FFFFFF;
}

.btn-text {
  font-size: 30rpx;
  color: #FFFFFF;
  font-weight: 600;
}

// 主题弹窗
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.theme-popup {
  width: 100%;
  max-height: 80vh;
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  padding: 40rpx 32rpx;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.popup-close {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.popup-input {
  display: flex;
  gap: 16rpx;
  margin-bottom: 40rpx;
}

.theme-input {
  flex: 1;
  height: 88rpx;
  background: #F5F5F7;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.input-btn {
  padding: 0 40rpx;
  height: 88rpx;
  background: #E5E5E5;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.active {
    background: #1D1D1F;
    
    .btn-text {
      color: #FFFFFF;
    }
  }
  
  .btn-text {
    font-size: 28rpx;
    color: #999;
    font-weight: 500;
  }
}

.theme-recommend {
  max-height: 50vh;
  overflow-y: auto;
}

.recommend-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #666;
  margin-bottom: 24rpx;
}

.theme-list {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.theme-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.theme-emoji {
  font-size: 48rpx;
}

.theme-info {
  flex: 1;
}

.theme-name {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 4rpx;
}

.theme-desc {
  display: block;
  font-size: 24rpx;
  color: #666;
}
</style>
