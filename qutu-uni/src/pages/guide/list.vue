<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <text class="nav-title">{{ currentSpotId ? '相关攻略' : '精选攻略' }}</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 搜索框（非景点攻略模式显示） -->
    <view class="search-container" v-if="!currentSpotId">
      <view class="search-box">
        <SFIcon name="search" :size="32" color="#86868B" />
        <input class="search-input" placeholder="搜索目的地/关键词" v-model="searchKeyword" @confirm="loadGuideList" />
      </view>
    </view>

    <!-- 分类标签（非景点攻略模式显示） -->
    <scroll-view class="category-scroll" scroll-x :show-scrollbar="false" v-if="!currentSpotId">
      <view class="category-list">
        <view 
          class="category-item" 
          :class="{ active: activeCategory === item.id }"
          v-for="item in categories" 
          :key="item.id"
          @click="activeCategory = item.id"
        >
          <text class="category-text">{{ item.name }}</text>
          <text class="category-icon">{{ item.icon }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 攻略列表 -->
    <scroll-view 
      class="guide-scroll" 
      :class="{ 'guide-scroll-full': !!currentSpotId }"
      scroll-y 
      :show-scrollbar="false"
      @scrolltolower="loadMore"
    >
      <view class="guide-list">
        <view 
          class="guide-card" 
          v-for="guide in guideList" 
          :key="guide.id"
          @click="goToDetail(guide.id)"
        >
          <view class="card-cover">
            <image class="cover-image" :src="guide.cover" mode="aspectFill" />
          </view>
          <view class="card-content">
            <text class="card-title">{{ guide.title }}</text>
            <text class="card-desc">{{ guide.description }}</text>
          </view>
        </view>
        
        <!-- 加载状态 -->
        <view class="loading-tip" v-if="loading">
          <text>加载中...</text>
        </view>
        <view class="loading-tip" v-if="!loading && !hasMore && guideList.length > 0">
          <text>没有更多了</text>
        </view>
        <view class="loading-tip" v-if="!loading && guideList.length === 0">
          <text>暂无攻略</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getGuideTags, getGuidesPage, getRelatedGuides } from '@/api'
import type { GuideCategory, GuideListItem } from '@/api/modules/guide'

const loading = ref(false)
const categories = ref<GuideCategory[]>([])
const activeCategory = ref(1)
const guideList = ref<GuideListItem[]>([])
const searchKeyword = ref('')
const currentSpotId = ref<number | null>(null)
const spotName = ref('')
const hasMore = ref(true)
const current = ref(1)
const pageSize = ref(10)

// 获取景点ID
const getSpotIdFromParams = (): number | null => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  if (options.spotId) {
    return Number(options.spotId)
  }
  return null
}

const loadSpotGuides = async (spotId: number, loadMore = false) => {
  if (loadMore) {
    current.value++
  } else {
    current.value = 1
    guideList.value = []
  }
  
  loading.value = true
  try {
    if (loadMore) {
      const moreGuides = await getRelatedGuides(spotId, pageSize.value)
      guideList.value.push(...(moreGuides as any))
      if (moreGuides.length < pageSize.value) {
        hasMore.value = false
      }
    } else {
      const guides = await getRelatedGuides(spotId, pageSize.value)
      guideList.value = guides as any
      if (guides.length < pageSize.value) {
        hasMore.value = false
      }
    }
  } catch (error) {
    console.error('加载景点攻略失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const tags = await getGuideTags(12)
    const iconMap: Record<string, string> = {
      Citywalk: '🚶',
      亲子: '👨‍👩‍👧',
      美食: '🍜',
      海边: '🏖️',
      古镇: '🏯',
      自然风光: '🏔️'
    }
    categories.value = tags.map((t, idx) => ({
      id: idx + 1,
      name: t.name,
      icon: iconMap[t.name] || '🏷️'
    }))
    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].id
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadGuideList = async (loadMore = false) => {
  if (currentSpotId.value) {
    return
  }
  
  loading.value = true
  try {
    if (loadMore) {
      current.value++
    } else {
      current.value = 1
      guideList.value = []
    }
    
    const tag = categories.value.find(c => c.id === activeCategory.value)?.name
    const page = await getGuidesPage({
      current: current.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      tag,
      orderBy: 'viewCount'
    })
    
    if (loadMore) {
      guideList.value.push(...page.records)
    } else {
      guideList.value = page.records
    }
    
    if (page.records.length < pageSize.value) {
      hasMore.value = false
    }
  } catch (error) {
    console.error('加载攻略列表失败:', error)
  } finally {
    loading.value = false
  }
}

watch(activeCategory, () => {
  if (!currentSpotId.value) {
    loadGuideList()
  }
})

onMounted(async () => {
  const spotId = getSpotIdFromParams()
  
  if (spotId) {
    currentSpotId.value = spotId
    await loadSpotGuides(spotId)
  } else {
    await loadCategories()
    await loadGuideList()
  }
})

const goBack = () => {
  uni.navigateBack()
}

const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/pages/guide/detail?id=${id}`
  })
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    if (currentSpotId.value) {
      loadSpotGuides(currentSpotId.value, true)
    } else {
      loadGuideList(true)
    }
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #00C853;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius-lg: 24rpx;
$border-radius-md: 16rpx;
$shadow-light: 0 2rpx 20rpx rgba(0, 0, 0, 0.06);

.page {
  min-height: 100vh;
  background: $bg-color;
}

// 导航栏
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 24rpx);
  background: $card-bg;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 60rpx;
}

// 搜索框
.search-container {
  padding: 24rpx 32rpx;
  background: $card-bg;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  background: $bg-color;
  border-radius: 20rpx;
  border: 2rpx solid #E5E5EA;
}

.search-icon {
  font-size: 32rpx;
  color: $text-secondary;
}

.search-input {
  flex: 1;
  font-size: 30rpx;
  color: $text-primary;
  background: transparent;
}

// 分类标签
.category-scroll {
  background: $card-bg;
  padding: 0 32rpx 24rpx;
  white-space: nowrap;
}

.category-list {
  display: inline-flex;
  gap: 20rpx;
}

.category-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 14rpx 24rpx;
  background: #F2F2F7;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
  transition: all 0.2s ease;
  flex-shrink: 0;

  &.active {
    background: rgba(0, 200, 83, 0.12);
    border-color: $primary-color;
  }
}

.category-text {
  font-size: 26rpx;
  color: $text-primary;
}

.category-item.active .category-text {
  color: $primary-color;
  font-weight: 500;
}

.category-icon {
  font-size: 24rpx;
  color: $text-secondary;
}

// 攻略列表
.guide-scroll {
  height: calc(100vh - 400rpx);
  padding: 24rpx 32rpx;
  
  &.guide-scroll-full {
    height: calc(100vh - 200rpx);
  }
}

.guide-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding-bottom: 48rpx;
}

.loading-tip {
  text-align: center;
  padding: 32rpx;
  color: $text-secondary;
  font-size: 28rpx;
}

.guide-card {
  background: $card-bg;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $shadow-light;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.card-cover {
  width: 100%;
  height: 360rpx;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.card-content {
  padding: 28rpx 32rpx;
}

.card-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12rpx;
  display: block;
}

.card-desc {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
