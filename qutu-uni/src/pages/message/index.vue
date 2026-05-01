<template>
  <view class="message-page">
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">消息中心</text>
      <view class="nav-right" :style="navRightStyle" @click="markAllRead">
        <text class="nav-action">全部已读</text>
      </view>
    </view>

    <view class="tabbar">
      <view class="tab-item" :class="{ active: activeTab === 'system' }" @click="activeTab = 'system'">
        <text class="tab-text">系统通知</text>
        <view class="tab-badge" v-if="unreadCount.system">{{ unreadCount.system }}</view>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'interaction' }" @click="activeTab = 'interaction'">
        <text class="tab-text">互动消息</text>
        <view class="tab-badge" v-if="unreadCount.interaction">{{ unreadCount.interaction }}</view>
      </view>
    </view>

    <scroll-view class="message-list" scroll-y :show-scrollbar="false">
      <view class="message-item" v-for="item in filteredMessages" :key="item.id" @click="openMessage(item)">
        <view class="message-left">
          <view class="message-icon" :style="{ background: item.color }">
            <text class="icon-text">{{ item.icon }}</text>
          </view>
        </view>
        <view class="message-body">
          <view class="message-header">
            <text class="message-title">{{ item.title }}</text>
            <text class="message-time">{{ item.time }}</text>
          </view>
          <text class="message-content">{{ item.content }}</text>
          <view class="message-tags" v-if="item.tags?.length">
            <text class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</text>
          </view>
        </view>
        <view class="message-status" v-if="!item.read">
          <view class="unread-dot"></view>
        </view>
      </view>
      <view class="empty" v-if="filteredMessages.length === 0">
        <text class="empty-text">暂无消息</text>
      </view>
    </scroll-view>

    <view class="bottom-actions">
      <view class="action-btn" @click="clearRead">清理已读</view>
      <view class="action-btn primary" @click="refreshMessages">刷新</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

type MessageType = 'system' | 'interaction'

interface MessageItem {
  id: number
  type: MessageType
  title: string
  content: string
  time: string
  read: boolean
  icon: string
  color: string
  tags?: string[]
}

const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const activeTab = ref<MessageType>('system')
const messages = ref<MessageItem[]>([
  {
    id: 1,
    type: 'system',
    title: '行程提醒',
    content: '你收藏的“北京四天三夜”将于明天开启，记得准备行李清单。',
    time: '08:30',
    read: false,
    icon: '🧳',
    color: '#E3F2FD',
    tags: ['行程', '提醒']
  },
  {
    id: 2,
    type: 'system',
    title: '版本更新',
    content: '新增消息中心与个人中心优化，快来体验新功能。',
    time: '昨天',
    read: true,
    icon: '✨',
    color: '#F3E5F5',
    tags: ['更新']
  },
  {
    id: 3,
    type: 'system',
    title: '备份完成',
    content: '云端备份已完成，最新备份文件已同步到你的账户。',
    time: '02-09',
    read: false,
    icon: '☁️',
    color: '#E8F5E9',
    tags: ['备份']
  },
  {
    id: 4,
    type: 'interaction',
    title: '新的收藏',
    content: '你收藏的“西湖”新增 3 条旅行故事。',
    time: '09:12',
    read: false,
    icon: '⭐',
    color: '#FFF8E1',
    tags: ['收藏']
  },
  {
    id: 5,
    type: 'interaction',
    title: '攻略推荐',
    content: '为你推荐“杭州两天一夜Citywalk”，点击查看详情。',
    time: '昨天',
    read: true,
    icon: '📌',
    color: '#E8F5E9',
    tags: ['推荐']
  }
])

const filteredMessages = computed(() => {
  return messages.value.filter(item => item.type === activeTab.value)
})

const unreadCount = computed(() => {
  const system = messages.value.filter(item => item.type === 'system' && !item.read).length
  const interaction = messages.value.filter(item => item.type === 'interaction' && !item.read).length
  return { system, interaction }
})

const goBack = () => {
  uni.navigateBack()
}

const markAllRead = () => {
  messages.value = messages.value.map(item => ({ ...item, read: true }))
  uni.showToast({ title: '已全部标记为已读', icon: 'none' })
}

const openMessage = (item: MessageItem) => {
  if (!item.read) {
    item.read = true
  }
  uni.showToast({ title: '已查看消息', icon: 'none' })
}

const clearRead = () => {
  messages.value = messages.value.filter(item => item.type !== activeTab.value || !item.read)
  uni.showToast({ title: '已清理已读消息', icon: 'none' })
}

const refreshMessages = () => {
  const newItem: MessageItem = {
    id: Date.now(),
    type: activeTab.value,
    title: activeTab.value === 'system' ? '系统提示' : '互动消息',
    content: activeTab.value === 'system' ? '你的账户安全状态良好，建议开启更多提醒。' : '你的旅行故事获得了新的点赞。',
    time: '刚刚',
    read: false,
    icon: activeTab.value === 'system' ? '🔔' : '💬',
    color: activeTab.value === 'system' ? '#E3F2FD' : '#FCE4EC',
    tags: activeTab.value === 'system' ? ['系统'] : ['互动']
  }
  messages.value.unshift(newItem)
  uni.showToast({ title: '已刷新消息', icon: 'none' })
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
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
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
}

.empty-text {
  font-size: 26rpx;
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
