<template>
  <view class="page">
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <text class="nav-title">{{ currentSpotId ? '相关攻略' : '精选攻略' }}</text>
      <view class="nav-placeholder"></view>
    </view>

    <view
      class="fixed-header"
      v-if="!currentSpotId"
      :style="{ top: navBarHeight + 'px' }"
      id="guideHeader"
    >
      <view class="search-container">
        <view class="search-box">
          <SFIcon name="search" :size="32" color="#86868B" />
          <input class="search-input" placeholder="搜索目的地/关键词" v-model="searchKeyword" @confirm="onSearchConfirm" />
        </view>
      </view>

      <scroll-view class="category-scroll" scroll-x :show-scrollbar="false">
        <view class="category-list">
          <view
            class="category-item"
            :class="{ active: activeCategory === item.id }"
            v-for="item in categories"
            :key="item.id"
            @click="activeCategory = item.id"
          >
            <text class="category-text">{{ item.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <scroll-view
      class="guide-scroll"
      scroll-y
      :show-scrollbar="false"
      @scrolltolower="loadMore"
      :style="{ paddingTop: scrollPaddingTop + 'px' }"
    >
      <view class="guide-list">
        <view
          class="guide-card"
          v-for="guide in guideList"
          :key="guide.id"
          @click="goToDetail(guide.id)"
        >
          <view class="card-cover">
            <image
              v-if="guide.cover && !guide.coverError"
              class="cover-image"
              :src="guide.cover"
              mode="aspectFill"
              @error="onCoverError(guide)"
            />
            <view v-else class="cover-placeholder">
              <SFIcon name="image" :size="48" color="rgba(255,255,255,0.6)" />
            </view>
          </view>
          <view class="card-content">
            <text class="card-title">{{ guide.title }}</text>
            <text class="card-desc" v-if="guide.description">{{ guide.description }}</text>
            <view class="card-meta" v-if="guide.days || guide.likes || guide.views">
              <text class="meta-tag" v-if="guide.days">{{ guide.days }}</text>
              <text class="meta-item" v-if="guide.likes">
                <SFIcon name="heart" :size="24" color="#86868B" :filled="false" /> {{ guide.likes }}
              </text>
              <text class="meta-item" v-if="guide.views">
                <SFIcon name="eye" :size="24" color="#86868B" /> {{ guide.views }}
              </text>
            </view>
          </view>
        </view>

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
import { ref, onMounted, watch, nextTick } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getGuideTags, getGuidesPage, getRelatedGuides } from '@/api'
import type { GuideCategory } from '@/api/modules/guide'

const loading = ref(false)
const categories = ref<GuideCategory[]>([])
const activeCategory = ref(1)
const guideList = ref<any[]>([])
const searchKeyword = ref('')
const currentSpotId = ref<number | null>(null)
const hasMore = ref(true)
const current = ref(1)
const pageSize = ref(10)

const statusBarHeight = ref(44)
const navBarHeight = ref(88)
const scrollPaddingTop = ref(88)

const getSpotIdFromParams = (): number | null => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  if (options.spotId) {
    return Number(options.spotId)
  }
  return null
}

const initSystemInfo = () => {
  try {
    const sysInfo = uni.getSystemInfoSync()
    statusBarHeight.value = sysInfo.statusBarHeight || 44
    navBarHeight.value = (sysInfo.statusBarHeight || 44) + 44
    scrollPaddingTop.value = navBarHeight.value
  } catch {
    statusBarHeight.value = 44
    navBarHeight.value = 88
    scrollPaddingTop.value = 88
  }
}

const updateScrollPadding = () => {
  if (currentSpotId.value) {
    scrollPaddingTop.value = navBarHeight.value
  } else {
    const query = uni.createSelectorQuery()
    query.select('#guideHeader').boundingClientRect((rect: any) => {
      if (rect && rect.height > 0) {
        scrollPaddingTop.value = rect.top + rect.height
      } else {
        scrollPaddingTop.value = navBarHeight.value + 88
      }
    }).exec()
  }
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
    const guides = await getRelatedGuides(spotId, pageSize.value)
    if (loadMore) {
      guideList.value.push(...(guides as any))
      if (guides.length < pageSize.value) {
        hasMore.value = false
      }
    } else {
        guideList.value = (guides as any).map((r: any) => ({ ...r, isCollected: !!r.isCollected }))
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
      // ensure each guide has isCollected flag
      guideList.value = (page.records || []).map(r => ({ ...r, isCollected: !!r.isCollected }))
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

const onSearchConfirm = () => {
  loadGuideList()
}

watch(activeCategory, () => {
  if (!currentSpotId.value) {
    loadGuideList()
  }
})

onMounted(async () => {
  initSystemInfo()

  const spotId = getSpotIdFromParams()

  if (spotId) {
    currentSpotId.value = spotId
    scrollPaddingTop.value = navBarHeight.value
    await loadSpotGuides(spotId)
  } else {
    await loadCategories()
    await loadGuideList()
    nextTick(() => {
      updateScrollPadding()
    })
  }
})

const goBack = () => {
  uni.navigateBack()
}

const goToDetail = (id: number) => {
  uni.navigateTo({
    url: `/packageGuide/pages/guide/detail?id=${id}`
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

const onCoverError = (guide: any) => {
  guide.coverError = true
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

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32rpx 24rpx;
  background: $card-bg;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 60rpx;
}

.fixed-header {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 99;
  background: $card-bg;
}

.search-container {
  padding: 16rpx 32rpx;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 28rpx;
  background: $bg-color;
  border-radius: 20rpx;
  border: 2rpx solid #E5E5EA;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.category-scroll {
  padding: 0 32rpx 20rpx;
  white-space: nowrap;
}

.category-list {
  display: inline-flex;
  gap: 16rpx;
}

.category-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: #F2F2F7;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
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

.guide-scroll {
  height: 100vh;
  box-sizing: border-box;
}

.guide-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 24rpx 32rpx 48rpx;
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

  &:active {
    transform: scale(0.98);
  }
}

.card-cover {
  width: 100%;
  height: 360rpx;
  overflow: hidden;
  position: relative;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 80rpx;
}

.card-content {
  padding: 24rpx 28rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 10rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12rpx;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex-wrap: wrap;
}

.meta-tag {
  font-size: 22rpx;
  color: $primary-color;
  background: rgba(0, 200, 83, 0.1);
  padding: 4rpx 14rpx;
  border-radius: 6rpx;
}

.meta-item {
  font-size: 22rpx;
  color: $text-secondary;
  display: inline-flex;
  align-items: center;
  gap: 4rpx;
}
</style>
