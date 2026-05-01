<template>
  <view class="favorites-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">收藏地点</text>
      <view class="nav-right"></view>
    </view>

    <scroll-view class="favorites-list" scroll-y :show-scrollbar="false">
      <view class="favorite-card" v-for="item in favorites" :key="item.id">
        <image class="favorite-image" :src="item.image" mode="aspectFill" />
        <view class="favorite-info">
          <text class="favorite-name">{{ item.name }}</text>
          <text class="favorite-address">{{ item.address }}</text>
          <text class="favorite-time">收藏于 {{ item.addTime }}</text>
          <view class="favorite-actions">
            <view class="action-btn ghost" @click="viewDetail(item)">查看</view>
            <view class="action-btn" @click="removeFavorite(item.id)">移除</view>
          </view>
        </view>
      </view>
      <view class="empty" v-if="!favorites.length">
        <text class="empty-text">暂无收藏地点</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getFavoriteLocations, deleteFavoriteLocation } from '@/api/modules/user'
import type { Location } from '@/api/modules/user'

const statusBarHeight = ref(0)
const favorites = ref<Location[]>([])

const loadFavorites = async () => {
  const res = await getFavoriteLocations()
  favorites.value = res
}

const removeFavorite = async (id: number) => {
  await deleteFavoriteLocation(id)
  favorites.value = favorites.value.filter(item => item.id !== id)
  uni.showToast({ title: '已移除收藏', icon: 'none' })
}

const viewDetail = (item: Location) => {
  uni.navigateTo({
    url: `/pages/destination/detail?id=${item.id}&name=${encodeURIComponent(item.name)}`
  })
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  loadFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-page {
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

.favorites-list {
  padding: 24rpx;
}

.favorite-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.favorite-image {
  width: 100%;
  height: 260rpx;
}

.favorite-info {
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.favorite-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.favorite-address {
  font-size: 24rpx;
  color: #86868B;
}

.favorite-time {
  font-size: 22rpx;
  color: #C7C7CC;
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
  background: #1D1D1F;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
}

.action-btn.ghost {
  background: #F5F5F7;
  color: #1D1D1F;
}

.empty {
  padding: 160rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 26rpx;
  color: #86868B;
}
</style>
