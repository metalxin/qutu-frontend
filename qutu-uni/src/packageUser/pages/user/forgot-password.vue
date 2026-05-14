<template>
  <view class="forgot-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
    </view>

    <!-- Logo 区域 -->
    <view class="logo-area">
      <image class="logo-img" src="/static/logo.png" mode="aspectFit" />
    </view>

    <!-- 标题区域 -->
    <view class="title-area" v-if="step < 3">
      <text class="title">{{ step === 1 ? '验证你的邮箱' : '设置新密码' }}</text>
      <text class="subtitle" v-if="step === 1">验证码已发送至 {{ email || 'example@email.com' }}</text>
      <text class="subtitle" v-else>请设置您的新密码</text>
    </view>

    <!-- 步骤1：验证码输入 -->
    <view v-if="step === 1">
      <view class="code-input-area">
        <view
          v-for="(item, index) in 6"
          :key="index"
          class="code-box"
          :class="{ active: codeIndex === index, filled: codeDigits[index] }"
          @click="focusCodeInput"
        >
          <text class="code-digit">{{ codeDigits[index] || '' }}</text>
          <view v-if="codeIndex === index && !codeDigits[index]" class="cursor" />
        </view>
      </view>

      <!-- 隐藏的真实输入框 -->
      <input
        ref="codeInputRef"
        class="hidden-input"
        type="number"
        maxlength="6"
        v-model="codeValue"
        @input="onCodeInput"
        @focus="onCodeFocus"
        @blur="onCodeBlur"
      />

      <!-- 重新发送 -->
      <view class="resend-row">
        <text v-if="cooldown > 0" class="resend-text">{{ cooldown }}s 后重新发送</text>
        <text v-else class="resend-link" @click="sendCode">重新发送</text>
      </view>

      <view class="next-btn" :class="{ disabled: !canNext || submitting }" @click="onNext">
        <text class="next-text">下一步</text>
      </view>
    </view>

    <!-- 步骤2：设置新密码 -->
    <view v-if="step === 2">
      <view class="form-area">
        <view class="input-row">
          <text class="input-icon">🔒</text>
          <input
            class="input"
            :password="!showPwd1"
            v-model="newPassword"
            placeholder="请输入新密码（至少6位）"
          />
          <view class="toggle-pwd" @click="showPwd1 = !showPwd1">
            <SFIcon :name="showPwd1 ? 'eye' : 'eye-off'" :size="36" color="#86868B" />
          </view>
        </view>
        <view class="input-row">
          <text class="input-icon">🔒</text>
          <input
            class="input"
            :password="!showPwd2"
            v-model="confirmPassword"
            placeholder="请再次输入新密码"
          />
          <view class="toggle-pwd" @click="showPwd2 = !showPwd2">
            <SFIcon :name="showPwd2 ? 'eye' : 'eye-off'" :size="36" color="#86868B" />
          </view>
        </view>
      </view>

      <view class="next-btn" :class="{ disabled: !canNext || submitting }" @click="onNext">
        <text class="next-text">{{ submitting ? '处理中...' : '重置密码' }}</text>
      </view>
    </view>

    <!-- 步骤3：完成 -->
    <view class="success-area" v-if="step === 3">
      <view class="success-icon">✓</view>
      <text class="success-title">密码重置成功</text>
      <text class="success-desc">请使用新密码登录</text>
      <view class="next-btn" @click="goLogin">
        <text class="next-text">去登录</text>
      </view>
    </view>

    <!-- 底部登录入口 -->
    <view class="bottom-row" v-if="step < 3">
      <text class="bottom-text">想起密码了？</text>
      <text class="bottom-link" @click="goLogin">去登录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { sendEmailVerifyCode, resetPassword } from '@/api/modules/user'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

const statusBarHeight = ref(0)
const step = ref(1)
const submitting = ref(false)

// 步骤1
const email = ref('')
const codeValue = ref('')
const codeDigits = ref<string[]>(new Array(6).fill(''))
const codeIndex = ref(0)
const cooldown = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

// 步骤2
const newPassword = ref('')
const confirmPassword = ref('')
const showPwd1 = ref(false)
const showPwd2 = ref(false)

watch(codeValue, (val) => {
  const digits = val.split('')
  for (let i = 0; i < 6; i++) {
    codeDigits.value[i] = digits[i] || ''
  }
  codeIndex.value = Math.min(val.length, 5)
})

const onCodeInput = () => {
  codeValue.value = codeValue.value.replace(/\D/g, '').slice(0, 6)
}

const onCodeFocus = () => {
  codeIndex.value = Math.min(codeValue.value.length, 5)
}

const onCodeBlur = () => {
  codeIndex.value = -1
}

const focusCodeInput = () => {
  const inputEl = document.querySelector('.hidden-input') as HTMLInputElement
  if (inputEl) inputEl.focus()
}

const canNext = computed(() => {
  if (step.value === 1) {
    return codeValue.value.length === 6
  }
  if (step.value === 2) {
    return newPassword.value.length >= 6 && confirmPassword.value === newPassword.value
  }
  return false
})

const sendCode = async () => {
  if (cooldown.value > 0) return
  if (!email.value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    uni.showToast({ title: '请输入正确的邮箱', icon: 'none' })
    return
  }
  try {
    await sendEmailVerifyCode(email.value)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    cooldown.value = 60
    timer = setInterval(() => {
      cooldown.value--
      if (cooldown.value <= 0) {
        if (timer) clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e?.message || '发送失败', icon: 'none' })
  }
}

const onNext = async () => {
  if (!canNext.value || submitting.value) return
  submitting.value = true
  try {
    if (step.value === 1) {
      step.value = 2
    } else if (step.value === 2) {
      await resetPassword({
        email: email.value.trim(),
        code: codeValue.value.trim(),
        password: newPassword.value
      })
      step.value = 3
    }
  } catch (e: any) {
    const msg = e?.response?.message || e?.message || '操作失败'
    uni.showToast({ title: msg, icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const goLogin = () => {
  uni.navigateBack()
}

const goBack = () => {
  if (step.value > 1 && step.value < 3) {
    step.value--
  } else {
    uni.navigateBack()
  }
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  // 从页面参数获取邮箱
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  if (currentPage?.$page?.options?.email) {
    email.value = decodeURIComponent(currentPage.$page.options.email)
  }
})
</script>

<style lang="scss" scoped>
$primary: #4A90E2;
$text: #1D1D1F;
$sub: #86868B;
$bg: #F5F5F7;
$border: #E5E5EA;

.forgot-page {
  min-height: 100vh;
  background: #fff;
  padding: 0 48rpx;
}

.nav-bar {
  display: flex;
  align-items: center;
  height: 88rpx;
}

.back-icon {
  font-size: 56rpx;
  color: $text;
}

.logo-area {
  margin-top: 40rpx;
  display: flex;
  justify-content: center;
}

.logo-img {
  width: 240rpx;
  height: 100rpx;
}

.title-area {
  margin-top: 48rpx;
}

.title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text;
  display: block;
}

.subtitle {
  margin-top: 16rpx;
  font-size: 26rpx;
  color: $sub;
  display: block;
}

.code-input-area {
  margin-top: 48rpx;
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.code-box {
  width: 88rpx;
  height: 96rpx;
  border-radius: 16rpx;
  border: 2rpx solid $border;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.2s;
}

.code-box.active {
  border-color: $primary;
}

.code-box.filled {
  border-color: $text;
  background: #f8f8f8;
}

.code-digit {
  font-size: 40rpx;
  font-weight: 600;
  color: $text;
}

.cursor {
  width: 2rpx;
  height: 40rpx;
  background: $primary;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.hidden-input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.resend-row {
  margin-top: 32rpx;
  display: flex;
  justify-content: flex-start;
}

.resend-text {
  font-size: 26rpx;
  color: $sub;
}

.resend-link {
  font-size: 26rpx;
  color: $primary;
}

.form-area {
  margin-top: 48rpx;
}

.input-row {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 2rpx solid #f0f0f0;
}

.input-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.input {
  flex: 1;
  font-size: 30rpx;
  color: $text;
}

.toggle-pwd {
  flex-shrink: 0;
  padding: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.next-btn {
  margin-top: 48rpx;
  background: $primary;
  height: 96rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.next-btn.disabled {
  background: #C7C7CC;
}

.next-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: 600;
}

.success-area {
  margin-top: 80rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.success-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #00C853;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  color: #fff;
  margin-bottom: 32rpx;
}

.success-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $text;
}

.success-desc {
  margin-top: 12rpx;
  font-size: 28rpx;
  color: $sub;
}

.bottom-row {
  margin-top: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.bottom-text {
  font-size: 26rpx;
  color: $sub;
}

.bottom-link {
  font-size: 26rpx;
  color: $primary;
  font-weight: 500;
}
</style>
