<template>
  <view class="route-list-page">
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <view class="nav-title">
        <text class="title-text">我的路线</text>
      </view>
      <view class="nav-action" @click="goToRecord">
        <SFIcon name="plus" :size="36" color="#007AFF" />
      </view>
    </view>

    <scroll-view
      class="content"
      scroll-y
      :show-scrollbar="false"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      :style="{ paddingTop: navBarHeight + 'px' }"
    >
      <view class="stats-bar" v-if="records.length > 0">
        <view class="stat-item">
          <text class="stat-value">{{ totalCount }}</text>
          <text class="stat-label">条路线</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ totalDistance }}</text>
          <text class="stat-label">总距离</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ totalDuration }}</text>
          <text class="stat-label">总时长</text>
        </view>
      </view>

      <view class="route-list" v-if="records.length > 0">
        <view
          class="route-card"
          v-for="record in records"
          :key="record.id"
          @click="viewDetail(record)"
        >
          <view class="route-cover">
            <image
              v-if="record.coverPhoto"
              :src="resolveUrl(record.coverPhoto)"
              mode="aspectFill"
              class="cover-image"
            />
            <view v-else class="cover-placeholder" :style="placeholderStyle(record.activityType)">
              <text class="cover-icon">{{ record.activityIcon || '🚶' }}</text>
            </view>
            <view class="cover-badge">
              <text class="badge-text">{{ record.activityIcon || '🚶' }} {{ record.activityType || '徒步' }}</text>
            </view>
          </view>
          <view class="route-info">
            <text class="route-title">{{ record.title || '未命名路线' }}</text>
            <text class="route-date">{{ formatDate(record.startTime) }}</text>
            <view class="route-stats">
              <view class="route-stat">
                <SFIcon name="clock" :size="24" color="#86868B" />
                <text class="route-stat-text">{{ formatDuration(record.duration) }}</text>
              </view>
              <view class="route-stat">
                <SFIcon name="route" :size="24" color="#86868B" />
                <text class="route-stat-text">{{ formatDistance(record.distance) }}</text>
              </view>
              <view class="route-stat" v-if="record.avgSpeed > 0">
                <SFIcon name="speed" :size="24" color="#86868B" />
                <text class="route-stat-text">{{ (record.avgSpeed * 3.6).toFixed(1) }}km/h</text>
              </view>
              <view class="route-stat" v-if="record.photoCount > 0">
                <SFIcon name="camera" :size="24" color="#86868B" />
                <text class="route-stat-text">{{ record.photoCount }}</text>
              </view>
            </view>
          </view>
          <view class="route-actions">
            <view class="action-delete" @click.stop="confirmDelete(record)">
              <SFIcon name="close" :size="24" color="#FF3B30" />
            </view>
          </view>
        </view>
      </view>

      <view class="empty-state" v-else-if="!loading && !refreshing">
        <view class="empty-icon">
          <SFIcon name="route" :size="80" color="#C7C7CC" />
        </view>
        <text class="empty-text">还没有路线记录</text>
        <text class="empty-desc">去记录你的第一条路线吧</text>
        <view class="empty-action" @click="goToRecord">
          <text class="action-text">开始记录</text>
        </view>
      </view>

      <view class="loading-more" v-if="loading && records.length > 0">
        <text class="loading-text">加载中...</text>
      </view>

      <view class="no-more" v-if="!hasMore && records.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getRouteRecords, deleteRouteRecord, type RouteRecord } from '../../api/route'
import { resolveFileUrl } from '@/api/modules/user'

const records = ref<RouteRecord[]>([])
const loading = ref(false)
const refreshing = ref(false)
const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const totalCount = ref(0)

const statusBarHeight = ref(0)
const navBarHeight = computed(() => (statusBarHeight.value || 44) + 44)

const totalDistance = computed(() => {
  const total = records.value.reduce((sum, r) => sum + (r.distance || 0), 0)
  if (total < 1000) return `${Math.round(total)}m`
  return `${(total / 1000).toFixed(1)}km`
})

const totalDuration = computed(() => {
  const total = records.value.reduce((sum, r) => sum + (r.duration || 0), 0)
  const h = Math.floor(total / 3600)
  const m = Math.floor((total % 3600) / 60)
  if (h > 0) return `${h}h${m}m`
  return `${m}min`
})

const resolveUrl = (url: string) => {
  return resolveFileUrl(url || '')
}

const placeholderStyle = (type?: string) => {
  const styles: Record<string, string> = {
    '山地自行车': 'background: linear-gradient(135deg, #E8F5E9, #C8E6C9)',
    '公路自行车': 'background: linear-gradient(135deg, #E3F2FD, #BBDEFB)',
    '跑步': 'background: linear-gradient(135deg, #FFF3E0, #FFE0B2)',
    '徒步': 'background: linear-gradient(135deg, #F3E5F5, #E1BEE7)',
    '驾车': 'background: linear-gradient(135deg, #E0F7FA, #B2EBF2)',
    '摩托车': 'background: linear-gradient(135deg, #FCE4EC, #F8BBD0)'
  }
  return styles[type || ''] || 'background: linear-gradient(135deg, #F5F5F5, #E0E0E0)'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const month = d.getMonth() + 1
  const day = d.getDate()
  const hours = d.getHours().toString().padStart(2, '0')
  const mins = d.getMinutes().toString().padStart(2, '0')
  return `${month}月${day}日 ${hours}:${mins}`
}

const formatDuration = (seconds: number) => {
  if (!seconds && seconds !== 0) return '0min'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) return `${h}h${m}m`
  return `${m}min`
}

const formatDistance = (meters: number) => {
  if (!meters && meters !== 0) return '0m'
  if (meters < 1000) return `${Math.round(meters)}m`
  return `${(meters / 1000).toFixed(1)}km`
}

const loadRecords = async (reset = false) => {
  if (loading.value) return
  if (reset) {
    page.value = 1
    hasMore.value = true
  }
  loading.value = true
  try {
    const res = await getRouteRecords({ page: page.value, pageSize })
    if (res?.list) {
      totalCount.value = res.total || 0
      if (page.value === 1) {
        records.value = res.list
      } else {
        records.value.push(...res.list)
      }
      hasMore.value = records.value.length < res.total
    }
  } catch (error) {
    console.error('加载路线记录失败:', error)
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    await loadRecords(true)
  } finally {
    refreshing.value = false
  }
}

const loadMore = () => {
  if (!hasMore.value || loading.value) return
  page.value++
  loadRecords()
}

const viewDetail = (record: RouteRecord) => {
  uni.navigateTo({ url: `/packageTrip/pages/route/detail?id=${record.id}` })
}

const confirmDelete = (record: RouteRecord) => {
  uni.showModal({
    title: '删除路线',
    content: `确定删除「${record.title || '未命名路线'}」？此操作不可恢复。`,
    confirmColor: '#FF3B30',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteRouteRecord(record.id)
          records.value = records.value.filter(r => r.id !== record.id)
          totalCount.value = Math.max(0, totalCount.value - 1)
          uni.showToast({ title: '已删除', icon: 'success' })
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

const goToRecord = () => {
  uni.navigateTo({ url: '/packageTrip/pages/record/index' })
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  loadRecords()
})
</script>

<style lang="scss" scoped>
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;

.route-list-page {
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
  align-items: center;
  justify-content: space-between;
  padding-bottom: 20rpx;
  background: $card-bg;
  box-shadow: 0 1rpx 0 rgba(0, 0, 0, 0.05);
}

.nav-back, .nav-action {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  flex: 1;
  text-align: center;
}

.title-text {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.content {
  height: 100vh;
  box-sizing: border-box;
}

.stats-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32rpx;
  padding: 32rpx;
  margin: 24rpx;
  background: $card-bg;
  border-radius: 20rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  flex: 1;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
}

.stat-label {
  font-size: 22rpx;
  color: $text-secondary;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: #E5E5EA;
}

.route-list {
  padding: 0 24rpx;
}

.route-card {
  display: flex;
  gap: 24rpx;
  padding: 24rpx;
  background: $card-bg;
  border-radius: 20rpx;
  margin-bottom: 16rpx;
  position: relative;
}

.route-cover {
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-icon {
  font-size: 60rpx;
}

.cover-badge {
  position: absolute;
  bottom: 8rpx;
  left: 8rpx;
  padding: 4rpx 12rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 8rpx;
}

.badge-text {
  font-size: 20rpx;
  color: #FFFFFF;
}

.route-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8rpx;
  min-width: 0;
}

.route-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-date {
  font-size: 24rpx;
  color: $text-secondary;
}

.route-stats {
  display: flex;
  gap: 16rpx;
  margin-top: 8rpx;
  flex-wrap: wrap;
}

.route-stat {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.route-stat-text {
  font-size: 22rpx;
  color: $text-secondary;
}

.route-actions {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
}

.action-delete {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #FFEBEE;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.empty-icon {
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: $text-secondary;
  margin-bottom: 40rpx;
}

.empty-action {
  padding: 20rpx 48rpx;
  background: #007AFF;
  border-radius: 40rpx;
}

.action-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.loading-more {
  padding: 32rpx;
  text-align: center;
}

.loading-text {
  font-size: 26rpx;
  color: $text-secondary;
}

.no-more {
  padding: 32rpx;
  text-align: center;
}

.no-more-text {
  font-size: 24rpx;
  color: #C7C7CC;
}

.bottom-space {
  height: 60rpx;
}
</style>
