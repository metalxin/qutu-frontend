<template>
  <view class="backup-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">数据备份</text>
      <view class="nav-right"></view>
    </view>

    <view class="hero-card">
      <text class="hero-title">保护你的账号数据</text>
      <text class="hero-desc">生成备份文件，或在需要时使用备份链接恢复数据。</text>
    </view>

    <view class="section-card">
      <text class="section-title">创建备份</text>
      <text class="section-desc">点击后会向服务端请求最新备份链接。</text>
      <view class="primary-btn" :class="{ loading: backupLoading }" @click="handleBackup">
        <text class="btn-text">{{ backupLoading ? '备份中...' : '立即备份' }}</text>
      </view>
      <view class="result-box" v-if="backupUrl">
        <text class="result-label">最近一次备份地址</text>
        <text class="result-value">{{ backupUrl }}</text>
      </view>
    </view>

    <view class="section-card">
      <text class="section-title">恢复数据</text>
      <text class="section-desc">请输入备份文件地址后再执行恢复。</text>
      <input
        class="url-input"
        v-model="restoreUrl"
        placeholder="请输入备份文件 URL"
        placeholder-class="url-placeholder"
        confirm-type="done"
      />
      <view class="secondary-btn" :class="{ loading: restoreLoading }" @click="handleRestore">
        <text class="btn-text">{{ restoreLoading ? '恢复中...' : '执行恢复' }}</text>
      </view>
    </view>

    <view class="section-card note-card">
      <text class="section-title">提示</text>
      <text class="note-text">恢复操作会直接使用你提供的备份链接，请确认来源可靠后再继续。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { backupData, restoreData } from '@/api/modules/user'

const statusBarHeight = ref(0)
const backupLoading = ref(false)
const restoreLoading = ref(false)
const backupUrl = ref('')
const restoreUrl = ref('')

const goToLogin = () => {
  uni.redirectTo({ url: '/pages/user/login' })
}

const ensureLoggedIn = () => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => {
      goToLogin()
    }, 300)
    return false
  }
  return true
}

const goBack = () => {
  uni.navigateBack()
}

const handleBackup = async () => {
  if (!ensureLoggedIn()) return
  if (backupLoading.value) return
  backupLoading.value = true
  try {
    const res = await backupData()
    backupUrl.value = res?.backupUrl || ''
    if (backupUrl.value) {
      restoreUrl.value = backupUrl.value
    }
    uni.showToast({ title: '备份成功', icon: 'success' })
  } catch (error) {
    console.error('创建备份失败:', error)
    uni.showToast({ title: '备份失败', icon: 'none' })
  } finally {
    backupLoading.value = false
  }
}

const handleRestore = async () => {
  if (!ensureLoggedIn()) return
  const url = restoreUrl.value.trim()
  if (!url) {
    uni.showToast({ title: '请输入备份 URL', icon: 'none' })
    return
  }
  if (restoreLoading.value) return
  restoreLoading.value = true
  try {
    await restoreData(url)
    uni.showToast({ title: '恢复成功', icon: 'success' })
  } catch (error) {
    console.error('恢复数据失败:', error)
    uni.showToast({ title: '恢复失败', icon: 'none' })
  } finally {
    restoreLoading.value = false
  }
}

onShow(() => {
  ensureLoggedIn()
})

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
})
</script>

<style lang="scss" scoped>
.backup-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F5F5F7 0%, #FFFFFF 100%);
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

.hero-card,
.section-card {
  margin: 24rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: #FFFFFF;
  box-shadow: 0 10rpx 30rpx rgba(17, 24, 39, 0.06);
}

.hero-card {
  background: linear-gradient(135deg, #FFF4E5 0%, #FFE2C2 100%);
}

.hero-title,
.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.hero-desc,
.section-desc,
.note-text {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #6B7280;
}

.section-card + .section-card {
  margin-top: 0;
}

.primary-btn,
.secondary-btn {
  margin-top: 20rpx;
  height: 88rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.primary-btn {
  background: linear-gradient(135deg, #FFB347 0%, #FF8C42 100%);
}

.secondary-btn {
  background: #1D1D1F;
}

.primary-btn.loading,
.secondary-btn.loading {
  opacity: 0.7;
}

.btn-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.result-box {
  margin-top: 20rpx;
  padding: 20rpx;
  border-radius: 16rpx;
  background: #F7F7FA;
}

.result-label {
  display: block;
  font-size: 22rpx;
  color: #6B7280;
}

.result-value {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #111827;
  word-break: break-all;
}

.url-input {
  margin-top: 20rpx;
  height: 88rpx;
  padding: 0 24rpx;
  border-radius: 18rpx;
  background: #F7F7FA;
  color: #111827;
  font-size: 26rpx;
}

.url-placeholder {
  color: #9CA3AF;
}

.note-card {
  margin-bottom: 40rpx;
}
</style>