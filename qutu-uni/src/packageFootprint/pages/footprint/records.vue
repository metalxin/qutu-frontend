<template>
  <view class="page">
    <view class="navbar" :style="navBarStyle">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <view class="nav-title">
        <text class="title-text">我的点亮记录</text>
      </view>
      <view class="nav-right" @click="goToCreate">
        <SFIcon name="plus" :size="36" color="#1D1D1F" />
      </view>
    </view>

    <scroll-view
      class="content"
      scroll-y
      :show-scrollbar="false"
      @scrolltolower="loadMore"
    >
      <view class="records-list" v-if="records.length > 0">
        <view
          class="record-card"
          v-for="record in records"
          :key="record.id"
          @longpress="confirmDelete(record)"
        >
          <view class="record-image-wrap">
            <image
              v-if="record.imageUrl"
              class="record-image"
              :src="resolveImageUrl(record.imageUrl)"
              mode="aspectFill"
            />
            <view v-else class="record-image-placeholder">
              <text class="placeholder-emoji">📍</text>
            </view>
            <view class="record-type-badge" :class="record.type === 2 ? 'overseas' : 'domestic'">
              <text class="badge-text">{{ record.type === 2 ? '国外' : '国内' }}</text>
            </view>
          </view>
          <view class="record-info">
            <text class="record-name">{{ record.spotName }}</text>
            <text class="record-location" v-if="record.cityName || record.provinceName">
              {{ record.provinceName ? record.provinceName + ' · ' : '' }}{{ record.cityName || '' }}
            </text>
            <text class="record-remark" v-if="record.remark">{{ record.remark }}</text>
            <text class="record-date" v-if="record.createdAt">{{ formatDate(record.createdAt) }}</text>
          </view>
        </view>
      </view>

      <view class="loading-more" v-if="loading">
        <text class="loading-text">加载中...</text>
      </view>
      <view class="no-more" v-if="noMore && records.length > 0">
        <text class="no-more-text">没有更多了</text>
      </view>

      <view class="empty-state" v-if="!loading && records.length === 0">
        <view class="empty-icon">
          <text class="empty-emoji">🗺️</text>
        </view>
        <text class="empty-text">还没有点亮记录哦</text>
        <text class="empty-desc">去探索景点，点亮你的足迹吧！</text>
        <view class="empty-action" @click="goToCreate">
          <text class="action-text">去点亮</text>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getFootprintRecords, deleteFootprintRecord } from '@/api'
import type { FootprintRecord } from '@/api'
import { resolveFileUrl } from '@/api/modules/user'

const statusBarHeight = ref(44)
const navBarHeight = ref(88)

const records = ref<FootprintRecord[]>([])
const loading = ref(false)
const current = ref(1)
const size = ref(20)
const total = ref(0)
const noMore = ref(false)

const navBarStyle = computed(() => {
  return {
    paddingTop: statusBarHeight.value + 'px',
    minHeight: navBarHeight.value + 'px'
  }
})

const resolveImageUrl = (url: string) => {
  return resolveFileUrl(url || '')
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

const getSystemInfo = () => {
  try {
    const info = uni.getSystemInfoSync()
    statusBarHeight.value = info.statusBarHeight || 44
    // #ifdef MP-WEIXIN
    try {
      const menuButton = uni.getMenuButtonBoundingClientRect()
      if (menuButton) {
        navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height + statusBarHeight.value
      }
    } catch (e) {
      console.log('获取胶囊按钮位置失败', e)
    }
    // #endif
  } catch (e) {
    console.error('获取系统信息失败', e)
  }
}

const loadRecords = async (reset = false) => {
  if (loading.value) return
  if (!reset && noMore.value) return

  if (reset) {
    current.value = 1
    records.value = []
    noMore.value = false
  }

  loading.value = true
  try {
    const res = await getFootprintRecords({ current: current.value, size: size.value })
    if (res) {
      if (reset) {
        records.value = res.records || []
      } else {
        records.value = [...records.value, ...(res.records || [])]
      }
      total.value = res.total || 0
      if (records.value.length >= total.value) {
        noMore.value = true
      }
      current.value++
    }
  } catch (error) {
    console.error('加载足迹记录失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!noMore.value && !loading.value) {
    loadRecords()
  }
}

const confirmDelete = (record: FootprintRecord) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除足迹「${record.spotName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteFootprintRecord(record.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          loadRecords(true)
        } catch (error) {
          console.error('删除足迹失败:', error)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

const goBack = () => {
  uni.navigateBack()
}

const goToCreate = () => {
  uni.navigateTo({ url: '/pages/footprint/map?type=china' })
}

onMounted(() => {
  getSystemInfo()
})

onShow(() => {
  loadRecords(true)
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F7;
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
  padding: 0 32rpx;
  background: #FFFFFF;
  box-sizing: border-box;
}

.nav-back, .nav-right {
  width: 60rpx;
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
  color: #1D1D1F;
}

.content {
  height: 100vh;
  padding-top: 140rpx;
}

.records-list {
  padding: 24rpx 32rpx;
}

.record-card {
  display: flex;
  gap: 24rpx;
  padding: 24rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.record-image-wrap {
  width: 160rpx;
  height: 160rpx;
  border-radius: 16rpx;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
}

.record-image {
  width: 100%;
  height: 100%;
}

.record-image-placeholder {
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-emoji {
  font-size: 48rpx;
}

.record-type-badge {
  position: absolute;
  top: 8rpx;
  left: 8rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;

  &.domestic {
    background: rgba(25, 118, 210, 0.85);
  }

  &.overseas {
    background: rgba(230, 81, 0, 0.85);
  }
}

.badge-text {
  font-size: 18rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.record-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8rpx;
}

.record-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-location {
  font-size: 24rpx;
  color: #86868B;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-remark {
  font-size: 22rpx;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-date {
  font-size: 22rpx;
  color: #C7C7CC;
}

.loading-more, .no-more {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
}

.loading-text, .no-more-text {
  font-size: 24rpx;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
  gap: 16rpx;
}

.empty-emoji {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.empty-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.empty-desc {
  font-size: 24rpx;
  color: #86868B;
  margin-bottom: 16rpx;
}

.empty-action {
  padding: 20rpx 48rpx;
  background: #1D1D1F;
  border-radius: 40rpx;
  margin-top: 16rpx;
}

.action-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.bottom-space {
  height: 60rpx;
}
</style>
