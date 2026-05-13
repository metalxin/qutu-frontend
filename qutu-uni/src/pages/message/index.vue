<template>
  <view class="message-page">
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">消息中心</text>
      <view class="nav-right" :style="navRightStyle" @click="handleMarkAllRead">
        <text class="nav-action">全部已读</text>
      </view>
    </view>

    <view class="tabbar">
      <view class="tab-item" :class="{ active: activeTab === 'system' }" @click="switchTab('system')">
        <text class="tab-text">系统通知</text>
        <view class="tab-badge" v-if="unreadCount.system">{{ unreadCount.system }}</view>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'interaction' }" @click="switchTab('interaction')">
        <text class="tab-text">互动消息</text>
        <view class="tab-badge" v-if="unreadCount.interaction">{{ unreadCount.interaction }}</view>
      </view>
    </view>

    <scroll-view class="message-list" scroll-y :show-scrollbar="false" @scrolltolower="loadMore">
      <view class="message-item" v-for="item in filteredMessages" :key="item.id" @click="openMessage(item)">
        <view class="message-left">
          <view class="message-icon" :style="{ background: item.color || '#E3F2FD' }">
            <text class="icon-text">{{ item.icon || '🔔' }}</text>
          </view>
        </view>
        <view class="message-body">
          <view class="message-header">
            <text class="message-title">{{ item.title }}</text>
            <text class="message-time">{{ formatTime(item.createTime) }}</text>
          </view>
          <text class="message-content">{{ item.content }}</text>
          <view class="message-tags" v-if="parseTags(item.tags).length">
            <text class="tag" v-for="tag in parseTags(item.tags)" :key="tag">{{ tag }}</text>
          </view>
        </view>
        <view class="message-status" v-if="item.readFlag === 0">
          <view class="unread-dot"></view>
        </view>
      </view>
      <view class="empty" v-if="!loading && filteredMessages.length === 0">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无消息</text>
      </view>
      <view class="loading-more" v-if="loading">
        <text class="loading-text">加载中...</text>
      </view>
    </scroll-view>

    <view class="bottom-actions">
      <view class="action-btn" @click="handleClearRead">清理已读</view>
      <view class="action-btn primary" @click="handleRefresh">刷新</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getMessages, getUnreadCount, markRead, markAllRead, deleteReadMessages } from '@/api/modules/message'
import type { MessageItem } from '@/api/modules/message'

const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const activeTab = ref<'system' | 'interaction'>('system')
const messages = ref<MessageItem[]>([])
const loading = ref(false)
const unreadCount = ref<{ system: number; interaction: number }>({ system: 0, interaction: 0 })

const filteredMessages = computed(() => {
  return messages.value.filter(item => item.type === activeTab.value)
})

const switchTab = (tab: 'system' | 'interaction') => {
  activeTab.value = tab
  loadMessages()
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
}

const parseTags = (tags: string | undefined) => {
  if (!tags) return []
  return tags.split(',').filter(t => t.trim())
}

const loadMessages = async () => {
  loading.value = true
  try {
    const res = await getMessages(activeTab.value)
    messages.value = Array.isArray(res) ? res : []
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    if (res) {
      unreadCount.value = res as { system: number; interaction: number }
    }
  } catch {}
}

const handleMarkAllRead = async () => {
  try {
    await markAllRead(activeTab.value)
    messages.value = messages.value.map(item => ({
      ...item,
      readFlag: item.type === activeTab.value ? 1 : item.readFlag
    }))
    unreadCount.value = { ...unreadCount.value, [activeTab.value]: 0 }
    uni.showToast({ title: '已全部标记为已读', icon: 'none' })
  } catch {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const openMessage = async (item: MessageItem) => {
  if (item.readFlag === 0) {
    try {
      await markRead(item.id)
      item.readFlag = 1
      unreadCount.value = {
        ...unreadCount.value,
        [item.type]: Math.max(0, (unreadCount.value[item.type] || 0) - 1)
      }
    } catch {}
  }
  if (item.bizType && item.bizId) {
    const routes: Record<string, string> = {
      'route': `/pages/route/detail?id=${item.bizId}`,
      'guide': `/pages/guide/detail?id=${item.bizId}`,
      'destination': `/pages/destination/detail?id=${item.bizId}`,
      'checklist': `/pages/checklist/detail?id=${item.bizId}`
    }
    const target = routes[item.bizType]
    if (target) {
      uni.navigateTo({ url: target })
    }
  }
}

const handleClearRead = async () => {
  try {
    await deleteReadMessages(activeTab.value)
    messages.value = messages.value.filter(item => item.type !== activeTab.value || item.readFlag === 0)
    uni.showToast({ title: '已清理已读消息', icon: 'none' })
  } catch {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const handleRefresh = async () => {
  await loadMessages()
  await loadUnreadCount()
  uni.showToast({ title: '已刷新', icon: 'none' })
}

const loadMore = () => {}

const goBack = () => {
  uni.navigateBack()
}

const navBarStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return {
      paddingTop: statusBarHeight.value + 'px',
      height: navBarHeight.value + 'px',
      paddingLeft: '24rpx',
      paddingRight: '24rpx',
      paddingBottom: '0px'
    }
  }
  return {
    paddingTop: statusBarHeight.value + 'px',
    minHeight: navBarHeight.value + 'px'
  }
})

const navRightStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingRight: menuButtonSpace.value + 'px' }
  }
  return {}
})

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  const windowWidth = systemInfo.windowWidth || 375
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonSpace.value = windowWidth - menuButton.left + 10
      navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
    }
  } catch (e) {}
  // #endif
  loadMessages()
  loadUnreadCount()
})
</script>

<style lang="scss" scoped>
.message-page {
  min-height: 100vh;
  background: #F5F5F7;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  padding: 24rpx 24rpx 16rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
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

.nav-action {
  font-size: 24rpx;
  color: #007AFF;
}

.tabbar {
  display: flex;
  padding: 16rpx 24rpx;
  gap: 16rpx;
  background: #FFFFFF;
}

.tab-item {
  flex: 1;
  padding: 16rpx;
  background: #F5F5F7;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.tab-item.active {
  background: #1D1D1F;
}

.tab-text {
  font-size: 26rpx;
  color: #1D1D1F;
}

.tab-item.active .tab-text {
  color: #FFFFFF;
}

.tab-badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  border-radius: 16rpx;
  background: #FF3B30;
  font-size: 20rpx;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-list {
  flex: 1;
  padding: 24rpx;
}

.message-item {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 20rpx;
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.message-left {
  display: flex;
  align-items: center;
}

.message-icon {
  width: 68rpx;
  height: 68rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-text {
  font-size: 32rpx;
}

.message-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12rpx;
}

.message-title {
  font-size: 28rpx;
  color: #1D1D1F;
  font-weight: 600;
}

.message-time {
  font-size: 22rpx;
  color: #86868B;
}

.message-content {
  font-size: 24rpx;
  color: #515154;
  line-height: 1.6;
}

.message-tags {
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
}

.tag {
  font-size: 20rpx;
  color: #5E5CE6;
  background: #F2F2FF;
  padding: 4rpx 12rpx;
  border-radius: 16rpx;
}

.message-status {
  display: flex;
  align-items: center;
}

.unread-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: #FF3B30;
}

.empty {
  padding: 120rpx 0;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.empty-icon {
  font-size: 64rpx;
}

.empty-text {
  font-size: 26rpx;
  color: #86868B;
}

.loading-more {
  padding: 24rpx 0;
  text-align: center;
}

.loading-text {
  font-size: 24rpx;
  color: #86868B;
}

.bottom-actions {
  padding: 16rpx 24rpx 32rpx;
  background: #FFFFFF;
  display: flex;
  gap: 16rpx;
}

.action-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 16rpx;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  color: #1D1D1F;
}

.action-btn.primary {
  background: #1D1D1F;
  color: #FFFFFF;
}
</style>
