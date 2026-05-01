<template>
  <view class="backup-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">数据备份</text>
      <view class="nav-right"></view>
    </view>

    <view class="summary-card">
      <text class="summary-title">云端备份</text>
      <text class="summary-desc">保障你的行程与足迹数据安全</text>
      <text class="summary-info">最近备份：{{ latestBackup?.time || '暂无备份' }}</text>
      <text class="summary-info" v-if="latestBackup?.url">文件：{{ latestBackup?.url }}</text>
      <view class="summary-actions">
        <view class="action-btn primary" @click="createBackup">立即备份</view>
      </view>
    </view>

    <view class="restore-card">
      <text class="section-title">恢复数据</text>
      <input class="restore-input" v-model="restoreUrl" placeholder="请输入备份链接" />
      <view class="action-btn ghost" @click="restoreBackup">开始恢复</view>
    </view>

    <view class="history-card">
      <text class="section-title">备份记录</text>
      <view class="history-item" v-for="item in backupHistory" :key="item.id">
        <view class="history-info">
          <text class="history-time">{{ item.time }}</text>
          <text class="history-url">{{ item.url }}</text>
        </view>
        <view class="history-action" @click="copyUrl(item.url)">复制</view>
      </view>
      <view class="empty" v-if="!backupHistory.length">
        <text class="empty-text">暂无备份记录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { backupData, restoreData } from '@/api/modules/user'

const statusBarHeight = ref(0)
const restoreUrl = ref('')
const backupHistory = ref<Array<{ id: number; time: string; url: string }>>([
  { id: 1, time: '2026-02-08 18:20', url: 'https://backup.qutuyunji.com/user_1_backup_0208.zip' },
  { id: 2, time: '2026-01-30 09:12', url: 'https://backup.qutuyunji.com/user_1_backup_0130.zip' }
])

const latestBackup = computed(() => backupHistory.value[0])

const createBackup = async () => {
  const res = await backupData()
  if (res.success) {
    const time = new Date().toISOString().slice(0, 16).replace('T', ' ')
    backupHistory.value.unshift({ id: Date.now(), time, url: res.backupUrl })
    uni.showToast({ title: '备份完成', icon: 'success' })
  }
}

const restoreBackup = async () => {
  if (!restoreUrl.value) {
    uni.showToast({ title: '请输入备份链接', icon: 'none' })
    return
  }
  const res = await restoreData(restoreUrl.value)
  if (res.success) {
    uni.showToast({ title: '恢复成功', icon: 'success' })
  }
}

const copyUrl = (url: string) => {
  uni.setClipboardData({
    data: url,
    success: () => {
      uni.showToast({ title: '已复制', icon: 'none' })
    }
  })
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
})
</script>

<style lang="scss" scoped>
.backup-page {
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

.summary-card,
.restore-card,
.history-card {
  margin: 24rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.summary-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.summary-desc {
  font-size: 24rpx;
  color: #86868B;
}

.summary-info {
  font-size: 22rpx;
  color: #515154;
}

.summary-actions {
  display: flex;
  gap: 12rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.restore-input {
  background: #F5F5F7;
  border-radius: 12rpx;
  padding: 16rpx;
  font-size: 24rpx;
}

.action-btn {
  height: 72rpx;
  border-radius: 16rpx;
  background: #1D1D1F;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
}

.action-btn.ghost {
  background: #F5F5F7;
  color: #1D1D1F;
}

.action-btn.primary {
  background: #1D1D1F;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  padding: 16rpx;
  border-radius: 16rpx;
  background: #F7F7FA;
}

.history-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  flex: 1;
}

.history-time {
  font-size: 24rpx;
  color: #1D1D1F;
}

.history-url {
  font-size: 20rpx;
  color: #86868B;
}

.history-action {
  font-size: 22rpx;
  color: #007AFF;
}

.empty {
  padding: 80rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 24rpx;
  color: #86868B;
}
</style>
