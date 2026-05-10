<template>
  <view class="favorites-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">收藏景点</text>
      <view class="nav-right">
        <text class="count-badge" v-if="total > 0">{{ total }}</text>
      </view>
    </view>

    <scroll-view
      class="favorites-list"
      scroll-y
      :show-scrollbar="false"
      :style="{ paddingTop: navBarHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view class="list-container">
      <view class="favorite-card" v-for="item in favorites" :key="item.id" @click="viewDetail(item)">
        <image
          class="favorite-image"
          :src="resolveCoverUrl(item.coverUrl)"
          mode="aspectFill"
        />
        <view class="favorite-info">
          <view class="favorite-header">
            <text class="favorite-name">{{ item.name }}</text>
            <view class="favorite-rating" v-if="item.rating > 0">
              <text class="rating-star">★</text>
              <text class="rating-value">{{ item.rating.toFixed(1) }}</text>
            </view>
          </view>
          <text class="favorite-address" v-if="item.address || item.cityName">
            {{ item.cityName ? item.cityName + ' · ' : '' }}{{ item.address || '' }}
          </text>
          <view class="favorite-tags" v-if="item.tags && item.tags.length > 0">
            <text class="tag-item" v-for="tag in item.tags.slice(0, 3)" :key="tag">{{ tag }}</text>
          </view>
          <view class="favorite-meta">
            <text class="meta-item" v-if="item.priceText">
              <text class="meta-icon">💰</text>{{ item.priceText }}
            </text>
            <text class="meta-item" v-if="item.favoriteCount">
              <text class="meta-icon">❤️</text>{{ item.favoriteCount }}
            </text>
          </view>
          <view class="favorite-actions">
            <view class="action-btn ghost" @click.stop="viewDetail(item)">查看详情</view>
            <view class="action-btn danger" @click.stop="removeFavorite(item)">取消收藏</view>
          </view>
        </view>
      </view>

      <view class="loading-more" v-if="loading">
        <text class="loading-text">加载中...</text>
      </view>

      <view class="no-more" v-if="!hasMore && favorites.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>

      <view class="empty" v-if="!loading && favorites.length === 0">
        <view class="empty-icon">
          <text class="icon-text">♡</text>
        </view>
        <text class="empty-text">还没有收藏景点</text>
        <text class="empty-desc">去发现感兴趣的景点吧</text>
      </view>

      <view class="bottom-space"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getUserFavoriteSpots, unfavoriteSpot } from '@/api'
import { resolveFileUrl } from '@/api/modules/user'
import type { FavoriteSpotItem } from '@/api'

const statusBarHeight = ref(0)
const navBarHeight = ref(0)
const favorites = ref<FavoriteSpotItem[]>([])
const loading = ref(false)
const current = ref(1)
const size = 20
const total = ref(0)
const hasMore = ref(true)

const resolveCoverUrl = (url: string) => {
  return resolveFileUrl(url || '')
}

const loadFavorites = async (reset = false) => {
  if (loading.value) return
  if (reset) {
    current.value = 1
    favorites.value = []
    hasMore.value = true
  }
  loading.value = true
  try {
    console.log('加载收藏景点, current:', current.value, 'size:', size)
    const res: any = await getUserFavoriteSpots({ current: current.value, size })
    console.log('收藏景点接口返回:', res)
    const records = res?.records || res?.list || (Array.isArray(res) ? res : [])
    const resTotal = res?.total ?? records.length
    console.log('解析后的记录数:', records.length, '总数:', resTotal)
    total.value = resTotal

    const mapped: FavoriteSpotItem[] = records.map((item: any) => ({
      id: item.id,
      name: item.name || '',
      subtitle: item.subtitle || '',
      coverUrl: item.coverUrl || '',
      rating: item.rating ?? 0,
      ratingCount: item.ratingCount ?? 0,
      priceText: item.priceText || '',
      distance: item.distance || '',
      address: item.address || '',
      cityName: item.cityName || '',
      tags: item.tags || [],
      favoriteCount: item.favoriteCount ?? 0,
      commentCount: item.commentCount ?? 0,
      viewCount: item.viewCount ?? 0,
      favoriteTime: item.favoriteTime || item.createdAt || ''
    }))
    console.log('映射后的收藏数据:', mapped)

    if (reset) {
      favorites.value = mapped
    } else {
      favorites.value.push(...mapped)
    }
    hasMore.value = favorites.value.length < resTotal
  } catch (error) {
    console.error('加载收藏景点失败:', error)
    if (reset) {
      favorites.value = []
    }
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!hasMore.value || loading.value) return
  current.value++
  loadFavorites()
}

const removeFavorite = async (item: FavoriteSpotItem) => {
  uni.showModal({
    title: '取消收藏',
    content: `确定取消收藏「${item.name}」？`,
    confirmColor: '#FF3B30',
    success: async (res) => {
      if (res.confirm) {
        try {
          await unfavoriteSpot(item.id)
          favorites.value = favorites.value.filter(f => f.id !== item.id)
          total.value = Math.max(0, total.value - 1)
          uni.showToast({ title: '已取消收藏', icon: 'success' })
        } catch (error) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}

const viewDetail = (item: FavoriteSpotItem) => {
  uni.navigateTo({
    url: `/pages/destination/detail?id=${item.id}`
  })
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  nextTick(() => {
    uni
      .createSelectorQuery()
      .select('.nav-bar')
      .boundingClientRect((rect) => {
        navBarHeight.value = rect?.height || 0
      })
      .exec()
  })
})

onShow(() => {
  loadFavorites(true)
})
</script>

<style lang="scss" scoped>
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$accent: #007AFF;

.favorites-page {
  min-height: 100vh;
  background: $bg-color;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: 24rpx 24rpx 16rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: $card-bg;
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
  color: $text-primary;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.count-badge {
  font-size: 24rpx;
  color: $accent;
  font-weight: 600;
}

.favorites-list {
  height: 100vh;
}

.list-container {
  padding: 24rpx;
}

.favorite-card {
  background: $card-bg;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.favorite-image {
  width: 100%;
  height: 280rpx;
}

.favorite-info {
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.favorite-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.favorite-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.favorite-rating {
  display: flex;
  align-items: center;
  gap: 4rpx;
  flex-shrink: 0;
}

.rating-star {
  font-size: 22rpx;
  color: #FFB800;
}

.rating-value {
  font-size: 24rpx;
  font-weight: 600;
  color: #FFB800;
}

.favorite-address {
  font-size: 24rpx;
  color: $text-secondary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.favorite-tags {
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
}

.tag-item {
  font-size: 20rpx;
  color: $accent;
  background: #E3F2FD;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
}

.favorite-meta {
  display: flex;
  gap: 20rpx;
}

.meta-item {
  font-size: 22rpx;
  color: $text-secondary;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.meta-icon {
  font-size: 20rpx;
}

.favorite-actions {
  margin-top: 8rpx;
  display: flex;
  gap: 12rpx;
}

.action-btn {
  flex: 1;
  height: 64rpx;
  border-radius: 16rpx;
  background: $text-primary;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 500;
}

.action-btn.ghost {
  background: $bg-color;
  color: $text-primary;
}

.action-btn.danger {
  background: #FFEBEE;
  color: #FF3B30;
}

.loading-more,
.no-more {
  padding: 32rpx;
  text-align: center;
}

.loading-text,
.no-more-text {
  font-size: 24rpx;
  color: $text-secondary;
}

.empty {
  padding: 160rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  background: #FFF8E1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.icon-text {
  font-size: 56rpx;
  color: #FFB800;
}

.empty-text {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.empty-desc {
  font-size: 24rpx;
  color: $text-secondary;
}

.bottom-space {
  height: 60rpx;
}
</style>
