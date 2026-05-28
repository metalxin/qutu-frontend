<template>
  <view>
    <!-- 添加菜单弹窗 -->
    <view class="add-menu-mask" :class="{ show: modelValue }" @click="closeMenu"></view>
    <view class="add-menu-popup" :class="{ show: modelValue }">
      <!-- 左上角使用口令按钮 -->
      <view class="code-btn" :class="{ show: modelValue }" @click="openCodePopup">
        <SFIcon name="key" :size="28" color="#007AFF" />
        <text class="code-btn-text">使用口令</text>
      </view>

      <!-- 菜单选项 -->
      <view class="menu-options">
        <view class="menu-option dark animate-item item-1" :class="{ show: modelValue }" @click="handleCreateTrip">
          <text class="option-title">创建新的行程</text>
          <view class="option-icon-wrapper">
            <SFIcon name="plus" :size="40" color="#FFFFFF" />
          </view>
        </view>

        <view class="menu-option light animate-item item-2" :class="{ show: modelValue }" @click="openImportPopup">
          <view class="option-content">
            <text class="option-title">智能导入地点/行程</text>
            <text class="option-desc">粘贴笔记链接、行程文本，或上传图片进行识别</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="import" :size="40" color="#34C759" />
          </view>
        </view>

        <view class="menu-option light animate-item item-3" :class="{ show: modelValue }" @click="openRecordPopup">
          <view class="option-content">
            <text class="option-title">记录行程路线</text>
            <text class="option-desc">选择运动方式开始记录路线</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="route" :size="40" color="#FF9500" />
          </view>
        </view>

        <view class="menu-option light animate-item item-4" :class="{ show: modelValue }" @click="handleCollect">
          <view class="option-content">
            <text class="option-title">「采集」识别</text>
            <text class="option-desc">识别同时收藏你的生活</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="camera" :size="40" color="#AF52DE" />
          </view>
        </view>
      </view>

      <!-- 关闭按钮 -->
      <view class="menu-close animate-item item-5" :class="{ show: modelValue }" @click="closeMenu">
        <SFIcon name="close" :size="40" color="#FFFFFF" />
      </view>
    </view>

    <!-- 使用口令弹窗 -->
    <view class="popup-mask" v-if="showCodePopup" @click="showCodePopup = false"></view>
    <view class="code-popup" :class="{ 'popup-show': showCodePopup }">
      <view class="code-cancel" @click="showCodePopup = false">
        <text class="cancel-text">取消</text>
      </view>
      <view class="ticket-card">
        <view class="ticket-tear top"></view>
        <view class="ticket-header">
          <view class="arrow-line">
            <view class="arrow-body"></view>
            <view class="arrow-head"></view>
          </view>
          <text class="header-text">复制行程</text>
        </view>
        <view class="ticket-body">
          <view class="ticket-content">
            <view class="content-left">
              <text class="itinerary-label">ITINERARY</text>
              <text class="itinerary-title">输入行程口令</text>
            </view>
            <view class="content-right">
              <text class="date-day">{{ currentDay }}</text>
              <view class="date-divider"></view>
              <text class="date-month">{{ currentMonth }}月</text>
            </view>
          </view>
          <view class="code-inputs">
            <view class="code-input-box" v-for="(digit, index) in codeDigits" :key="index">
              <input
                class="code-input"
                type="text"
                :maxlength="1"
                v-model="codeDigits[index]"
                @input="onCodeInput(index)"
                :ref="el => codeInputRefs[index] = el"
                :disabled="codeLoading"
              />
            </view>
          </view>
          <view class="code-loading" v-if="codeLoading">
            <text class="code-loading-text">验证中...</text>
          </view>
          <view class="ticket-dashed"></view>
        </view>
        <view class="ticket-barcode">
          <view class="barcode-lines">
            <view class="barcode-line" v-for="i in 40" :key="i" :style="{ width: Math.random() * 4 + 2 + 'rpx' }"></view>
          </view>
        </view>
        <view class="ticket-tear bottom"></view>
      </view>
    </view>

    <!-- 智能导入弹窗 -->
    <view class="popup-mask" v-if="showImportPopup" @click="showImportPopup = false"></view>
    <view class="import-popup" :class="{ 'popup-show': showImportPopup }">
      <view class="drag-bar"></view>
      <view class="import-section main">
        <view class="section-header">
          <text class="section-icon">🔄</text>
          <text class="section-title">文本或链接识别</text>
        </view>
        <view class="input-area">
          <textarea
            class="import-input"
            v-model="importText"
            placeholder="粘贴站外攻略链接到这里，即可获取相关行程。现已支持小红书图文/视频笔记、微信公众号、携程酒店景点、大众点评店铺、飞书文档（需公开文档权限）等。"
          />
          <view class="start-btn" :class="{ active: importText }" @click="startRecognize">
            <text class="start-btn-text">开始识别</text>
          </view>
        </view>
      </view>
      <view class="import-section" @click="handleScreenshot">
        <view class="section-header">
          <text class="section-icon">📷</text>
          <text class="section-title">截图识别</text>
        </view>
        <text class="section-desc">选择含有地点信息的页面截图</text>
      </view>
    </view>

    <!-- 记录行程路线弹窗 -->
    <view class="popup-mask" v-if="showRecordPopup" @click="showRecordPopup = false"></view>
    <view class="record-popup" :class="{ 'popup-show': showRecordPopup }">
      <view class="record-popup-header">
        <view class="record-header-left">
          <text class="record-icon">📍</text>
          <text class="record-title">记录</text>
        </view>
        <view class="record-header-right" @click="showActivityPicker = !showActivityPicker">
          <text class="activity-icon">🚴</text>
          <text class="activity-name">{{ selectedActivity }}</text>
          <text class="activity-arrow">▼</text>
        </view>
      </view>
      <text class="record-subtitle">选择一种运动开始记录路线</text>
      <view class="activity-picker" :class="{ show: showActivityPicker }">
        <view
          class="activity-option"
          v-for="(activity, index) in activityTypes"
          :key="activity.name"
          :class="{ show: showActivityPicker, active: selectedActivity === activity.name }"
          :style="{ transitionDelay: showActivityPicker ? (index * 0.05) + 's' : '0s' }"
          @click="selectActivity(activity)"
        >
          <text class="activity-option-icon">{{ activity.icon }}</text>
          <text class="activity-option-name">{{ activity.name }}</text>
        </view>
      </view>
      <view class="record-btn-wrapper">
        <view
          class="record-start-btn"
          @longpress="startRecording"
          @touchstart="onRecordBtnTouchStart"
          @touchend="onRecordBtnTouchEnd"
        >
          <text class="record-btn-icon">🚴</text>
          <text class="record-btn-text">开始记录</text>
        </view>
        <text class="record-btn-hint">长按开始</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { importRouteByShareCode, importByShareCode, importGuideByShareCode } from '@/api'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const closeMenu = () => {
  emit('update:modelValue', false)
}

const showCodePopup = ref(false)
const showImportPopup = ref(false)
const showRecordPopup = ref(false)
const importText = ref('')
const codeDigits = ref(['', '', '', '', '', ''])
const codeInputRefs = ref<any[]>([])
const codeLoading = ref(false)
const currentDay = ref(String(new Date().getDate()))
const currentMonth = ref(String(new Date().getMonth() + 1))
const showActivityPicker = ref(false)
const selectedActivity = ref('山地自行车')
const activityTypes = ref([
  { name: '山地自行车', icon: '🚴' },
  { name: '公路自行车', icon: '🚲' },
  { name: '跑步', icon: '🏃' },
  { name: '徒步', icon: '🥾' },
  { name: '滑雪', icon: '⛷️' },
  { name: '滑板', icon: '🛹' }
])

const openCodePopup = () => {
  emit('update:modelValue', false)
  showCodePopup.value = true
}

const openImportPopup = () => {
  emit('update:modelValue', false)
  showImportPopup.value = true
}

const openRecordPopup = () => {
  emit('update:modelValue', false)
  showRecordPopup.value = true
}

const handleCreateTrip = () => {
  emit('update:modelValue', false)
  uni.navigateTo({ url: '/packageTrip/pages/planning/index' })
}

const handleCollect = () => {
  emit('update:modelValue', false)
  uni.navigateTo({ url: '/packageCollect/pages/collect/camera' })
}

const onCodeInput = (index: number) => {
  const val = codeDigits.value[index]
  if (val && index < 5) {
    const nextInput = codeInputRefs.value[index + 1]
    if (nextInput) nextInput.focus()
  }
  if (index === 5 && val) {
    submitCode()
  }
}

const submitCode = async () => {
  const code = codeDigits.value.join('')
  if (codeLoading.value) return
  codeLoading.value = true
  try {
    try {
      const routeRes = await importRouteByShareCode(code)
      if (routeRes) {
        uni.showToast({ title: '行程口令验证成功', icon: 'success' })
        setTimeout(() => {
          showCodePopup.value = false
          codeDigits.value = ['', '', '', '', '', '']
          uni.navigateTo({ url: '/packageTrip/pages/planning/detail?shareCode=' + encodeURIComponent(code) })
        }, 800)
        return
      }
    } catch {}
    try {
      const guideRes = await importGuideByShareCode(code)
      if (guideRes && guideRes.guideId) {
        uni.showToast({ title: '攻略口令验证成功', icon: 'success' })
        setTimeout(() => {
          showCodePopup.value = false
          codeDigits.value = ['', '', '', '', '', '']
          uni.navigateTo({ url: `/packageGuide/pages/guide/detail?id=${guideRes.guideId}` })
        }, 800)
        return
      }
    } catch {}
    try {
      await importByShareCode(code)
      uni.showToast({ title: '口令验证成功', icon: 'success' })
      setTimeout(() => {
        showCodePopup.value = false
        codeDigits.value = ['', '', '', '', '', '']
        uni.navigateTo({ url: '/pages/checklist/index?shareCode=' + encodeURIComponent(code) })
      }, 800)
    } catch (error: any) {
      const msg = error?.response?.msg || error?.response?.message || error?.message || '口令无效或已失效'
      uni.showToast({ title: msg, icon: 'none' })
    }
  } finally {
    codeLoading.value = false
  }
}

const startRecognize = () => {
  if (!importText.value.trim()) {
    uni.showToast({ title: '请输入内容', icon: 'none' })
    return
  }
  uni.showToast({ title: '识别中...', icon: 'loading' })
  setTimeout(() => {
    showImportPopup.value = false
    importText.value = ''
    uni.navigateTo({ url: '/packageTrip/pages/planning/index' })
  }, 1500)
}

const handleScreenshot = () => {
  uni.chooseImage({
    count: 1,
    sourceType: ['album'],
    success: () => {
      showImportPopup.value = false
      uni.showToast({ title: '识别中...', icon: 'loading' })
      setTimeout(() => {
        uni.navigateTo({ url: '/packageTrip/pages/planning/index' })
      }, 1500)
    }
  })
}

const selectActivity = (activity: { name: string; icon: string }) => {
  selectedActivity.value = activity.name
  showActivityPicker.value = false
}

const startRecording = () => {
  showRecordPopup.value = false
  uni.navigateTo({ url: '/packageTrip/pages/record/index' })
}

const onRecordBtnTouchStart = () => {}
const onRecordBtnTouchEnd = () => {}
</script>

<style lang="scss" scoped>
.add-menu-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  z-index: 998;
  pointer-events: none;
  transition: all 0.4s;

  &.show {
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(30px);
    -webkit-backdrop-filter: blur(30px);
    pointer-events: auto;
  }
}

.add-menu-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  padding: 0 48rpx;
  padding-bottom: calc(60rpx + env(safe-area-inset-bottom));
  pointer-events: none;

  &.show {
    pointer-events: auto;
  }
}

.code-btn {
  position: absolute;
  top: -80rpx;
  left: 48rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 28rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 100rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
  opacity: 0;
  transform: translateY(20rpx);
  transition: all 0.3s ease;

  &.show {
    opacity: 1;
    transform: translateY(0);
  }
}

.code-btn-text {
  font-size: 26rpx;
  color: #007AFF;
  font-weight: 500;
}

.menu-options {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.menu-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 32rpx;
  border-radius: 24rpx;
  opacity: 0;
  transform: translateY(40rpx);

  &.dark {
    background: #1D1D1F;
  }

  &.light {
    background: #FFFFFF;
    box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.06);
  }
}

.animate-item {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.item-1 { transition-delay: 0s; }
  &.item-2 { transition-delay: 0.05s; }
  &.item-3 { transition-delay: 0.1s; }
  &.item-4 { transition-delay: 0.15s; }
  &.item-5 { transition-delay: 0.2s; }

  &.show {
    opacity: 1;
    transform: translateY(0);
  }
}

.option-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;

  .light & {
    color: #1D1D1F;
  }
}

.option-desc {
  font-size: 24rpx;
  color: #86868B;
  margin-top: 4rpx;
  display: block;
}

.option-content {
  flex: 1;
}

.option-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;

  &.light {
    background: #F2F2F7;
  }
}

.menu-close {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 24rpx auto 0;
  opacity: 0;
  transform: scale(0.5);
  transition: all 0.3s ease;

  &.show {
    opacity: 1;
    transform: scale(1);
  }
}

.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 997;
}

// 智能导入弹窗
.import-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  z-index: 999;
  padding: 20rpx 32rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.drag-bar {
  width: 80rpx;
  height: 8rpx;
  background: #E5E5EA;
  border-radius: 4rpx;
  margin: 0 auto 32rpx;
}

.import-section {
  background: #F2F2F7;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;

  &.main {
    border: 2rpx solid #1D1D1F;
    background: #FFFFFF;
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.section-icon {
  font-size: 32rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.section-desc {
  font-size: 26rpx;
  color: #86868B;
}

.input-area {
  position: relative;
}

.import-input {
  width: 100%;
  height: 200rpx;
  font-size: 26rpx;
  color: #1D1D1F;
  line-height: 1.6;
  background: transparent;
}

.start-btn {
  position: absolute;
  right: 0;
  bottom: 0;
  padding: 16rpx 28rpx;
  background: #E5E5EA;
  border-radius: 12rpx;
  transition: all 0.2s ease;

  &.active {
    background: #00C853;
  }
}

.start-btn-text {
  font-size: 26rpx;
  color: #86868B;
}

.start-btn.active .start-btn-text {
  color: #FFFFFF;
}

// 使用口令弹窗
.code-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(80, 80, 80, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s ease;

  &.popup-show {
    opacity: 1;
    pointer-events: auto;
  }
}

.code-cancel {
  position: absolute;
  top: calc(var(--status-bar-height, 44px) + 20rpx);
  left: 32rpx;
  padding: 16rpx 24rpx;
}

.cancel-text {
  font-size: 32rpx;
  color: #FFFFFF;
}

.ticket-card {
  width: 580rpx;
  background: #FFFFFF;
  position: relative;
  transform: scale(0.9) translateY(40rpx);
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.code-popup.popup-show .ticket-card {
  transform: scale(1) translateY(0);
}

.ticket-tear {
  width: 100%;
  height: 24rpx;
  background: #FFFFFF;
  position: relative;

  &.top {
    background: linear-gradient(135deg, transparent 8rpx, #FFFFFF 8rpx),
                linear-gradient(-135deg, transparent 8rpx, #FFFFFF 8rpx);
    background-size: 24rpx 24rpx;
    background-position: left top;
  }

  &.bottom {
    background: linear-gradient(45deg, transparent 8rpx, #FFFFFF 8rpx),
                linear-gradient(-45deg, transparent 8rpx, #FFFFFF 8rpx);
    background-size: 24rpx 24rpx;
    background-position: left bottom;
  }
}

.ticket-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 24rpx 40rpx;
  gap: 16rpx;
  border-bottom: 2rpx solid #E5E5EA;
}

.arrow-line {
  display: flex;
  align-items: center;
  flex: 1;
}

.arrow-body {
  flex: 1;
  height: 3rpx;
  background: #1D1D1F;
}

.arrow-head {
  width: 0;
  height: 0;
  border-left: 16rpx solid #1D1D1F;
  border-top: 8rpx solid transparent;
  border-bottom: 8rpx solid transparent;
}

.header-text {
  font-size: 28rpx;
  color: #1D1D1F;
}

.ticket-body {
  padding: 32rpx 40rpx;
}

.ticket-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 40rpx;
  border-bottom: 2rpx solid #E5E5EA;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.itinerary-label {
  font-size: 24rpx;
  color: #86868B;
  font-style: italic;
  letter-spacing: 4rpx;
}

.itinerary-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.content-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 24rpx;
  border: 2rpx solid #1D1D1F;
  border-radius: 8rpx;
}

.date-day {
  font-size: 48rpx;
  font-weight: 700;
  color: #1D1D1F;
  line-height: 1;
}

.date-divider {
  width: 2rpx;
  height: 48rpx;
  background: #1D1D1F;
  transform: rotate(20deg);
}

.date-month {
  font-size: 28rpx;
  color: #1D1D1F;
}

.code-inputs {
  display: flex;
  justify-content: center;
  gap: 16rpx;
  padding: 48rpx 0;
}

.code-input-box {
  width: 80rpx;
  height: 100rpx;
  border: 3rpx solid #1D1D1F;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-input {
  width: 100%;
  height: 100%;
  text-align: center;
  font-size: 44rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.code-loading {
  display: flex;
  justify-content: center;
  padding-bottom: 16rpx;
}

.code-loading-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
}

.ticket-dashed {
  border-bottom: 3rpx dashed #CCCCCC;
  margin-bottom: 24rpx;
}

.ticket-barcode {
  padding: 16rpx 40rpx 24rpx;
}

.barcode-lines {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 3rpx;
  height: 60rpx;
}

.barcode-line {
  height: 100%;
  background: #1D1D1F;
}

// 记录行程路线弹窗
.record-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  z-index: 999;
  padding: 32rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  box-shadow: 0 -12rpx 40rpx rgba(0, 0, 0, 0.12);
  border-top: 1rpx solid #EFEFF4;

  &.popup-show {
    transform: translateY(0);
  }
}

.record-popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.record-header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.record-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  background: #F2F2F7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
}

.record-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.record-header-right {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 18rpx;
  background: #F2F2F7;
  border: 1rpx solid #E5E5EA;
  border-radius: 100rpx;
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.06);
}

.activity-icon {
  font-size: 26rpx;
}

.activity-name {
  font-size: 28rpx;
  color: #1D1D1F;
  font-weight: 600;
}

.activity-arrow {
  font-size: 20rpx;
  color: #AEAEB2;
}

.record-subtitle {
  font-size: 26rpx;
  color: #8E8E93;
  display: block;
  margin-bottom: 48rpx;
}

.activity-picker {
  position: absolute;
  top: 100rpx;
  right: 32rpx;
  background: #FFFFFF;
  border-radius: 18rpx;
  border: 1rpx solid #E5E5EA;
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 10;
  opacity: 0;
  transform: scale(0.8) translateY(-20rpx);
  transform-origin: top right;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.show {
    opacity: 1;
    transform: scale(1) translateY(0);
    pointer-events: auto;
  }
}

.activity-option {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #F2F2F7;
  opacity: 0;
  transform: translateX(20rpx);
  transition: all 0.25s ease-out;

  &.show {
    opacity: 1;
    transform: translateX(0);
  }

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F2F2F7;
  }

  &.active {
    background: #EAF2FF;
  }
}

.activity-option-icon {
  font-size: 30rpx;
}

.activity-option-name {
  font-size: 28rpx;
  color: #1D1D1F;
}

.record-btn-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.record-start-btn {
  width: 260rpx;
  height: 260rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(0, 122, 255, 0.2);
  background: radial-gradient(circle at 30% 30%, #FFFFFF 0%, #F0F6FF 45%, #E3EEFF 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  transition: all 0.2s ease;
  box-shadow: 0 18rpx 40rpx rgba(0, 122, 255, 0.18);

  &:active {
    transform: scale(0.95);
    background: radial-gradient(circle at 30% 30%, #FFFFFF 0%, #E8F0FF 45%, #D6E7FF 100%);
  }
}

.record-btn-icon {
  font-size: 44rpx;
}

.record-btn-text {
  font-size: 30rpx;
  color: #007AFF;
  font-weight: 600;
}

.record-btn-hint {
  font-size: 24rpx;
  color: #8E8E93;
}
</style>
