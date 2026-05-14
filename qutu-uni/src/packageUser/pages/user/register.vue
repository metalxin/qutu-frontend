<template>
  <view class="register-page">
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

    <!-- 步骤1：输入邮箱 -->
    <view v-if="step === 1">
      <view class="title-area">
        <text class="title">注册账号</text>
        <text class="subtitle">请输入您的邮箱地址</text>
      </view>

      <view class="form-area">
        <view class="input-row">
          <text class="input-icon icon-mail"></text>
          <input
            class="input"
            v-model="email"
            placeholder="请输入邮箱"
            type="text"
          />
        </view>
      </view>

      <view class="login-btn" :class="{ disabled: !canSendCode || sendingCode }" @click="onSendCode">
        <text class="login-text">{{ sendingCode ? '发送中...' : '获取验证码' }}</text>
      </view>
    </view>

    <!-- 步骤2：验证码输入 -->
    <view v-if="step === 2">
      <view class="title-area">
        <text class="title">验证你的邮箱</text>
        <text class="subtitle">验证码已发送至</text>
        <text class="email-text">{{ email || 'example@email.com' }}</text>
      </view>

      <!-- 验证码输入 -->
      <view class="code-input-wrap">
        <input
          class="real-input"
          type="number"
          maxlength="6"
          :value="codeValue"
          @input="onCodeInput"
          @focus="onCodeFocus"
          @blur="onCodeBlur"
        />
        <view class="code-boxes">
          <view
            v-for="(item, index) in 6"
            :key="index"
            class="code-box"
            :class="{ active: codeIndex === index, filled: codeDigits[index] }"
          >
            <text class="code-digit">{{ codeDigits[index] || '' }}</text>
            <view v-if="codeIndex === index && !codeDigits[index]" class="cursor" />
          </view>
        </view>
      </view>

      <!-- 重新发送 -->
      <view class="resend-row">
        <text v-if="countdown > 0" class="resend-text">{{ countdown }}s 后重新发送</text>
        <text v-else class="resend-link" @click="onSendCode">重新发送</text>
      </view>

      <!-- 下一步按钮 -->
      <view class="login-btn" :class="{ disabled: !canSubmit || submitting }" @click="onVerify">
        <text class="login-text">{{ submitting ? '验证中...' : '下一步' }}</text>
      </view>
    </view>

    <!-- 步骤3：设置密码 -->
    <view v-if="step === 3">
      <view class="title-area">
        <text class="title">设置密码</text>
        <text class="subtitle">请设置您的登录密码</text>
      </view>

      <view class="form-area">
        <view class="input-row">
          <text class="input-icon icon-lock"></text>
          <input
            class="input"
            :password="!showPwd1"
            v-model="password"
            placeholder="请输入密码（至少6位）"
          />
          <view class="toggle-pwd" @click="showPwd1 = !showPwd1">
            <SFIcon
              :name="showPwd1 ? 'eye' : 'eye-off'"
              :size="36"
              color="#86868B"
            />
          </view>
        </view>
        <view class="input-row">
          <text class="input-icon icon-lock"></text>
          <input
            class="input"
            :password="!showPwd2"
            v-model="confirmPassword"
            placeholder="请再次输入密码"
          />
          <view class="toggle-pwd" @click="showPwd2 = !showPwd2">
            <SFIcon
              :name="showPwd2 ? 'eye' : 'eye-off'"
              :size="36"
              color="#86868B"
            />
          </view>
        </view>
      </view>

      <view class="login-btn" :class="{ disabled: !canSetPassword || submitting }" @click="onRegister">
        <text class="login-text">{{ submitting ? '注册中...' : '完成注册' }}</text>
      </view>
    </view>

    <!-- 步骤4：注册成功 -->
    <view class="success-area" v-if="step === 4">
      <view class="success-icon">✓</view>
      <text class="success-title">注册成功</text>
      <text class="success-desc">欢迎加入趣途</text>
      <view class="auto-login-btn" :class="{ loading: autoLoggingIn }" @click="onAutoLogin">
        <text class="auto-login-text">{{ autoLoggingIn ? '登录中...' : '一键登录' }}</text>
      </view>
      <view class="manual-login-row">
        <text class="manual-login-link" @click="goLogin">使用其他方式登录</text>
      </view>
    </view>

    <!-- 底部登录入口 -->
    <view class="bottom-row" v-if="step < 4">
      <text class="bottom-text">已有账号？</text>
      <text class="bottom-link" @click="goLogin">去登录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { registerUser, sendEmailCode, loginByEmailCode, getUserInfo } from '@/api/modules/user'

const statusBarHeight = ref(0)
const step = ref(1)
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const codeValue = ref('')
const codeDigits = ref<string[]>(new Array(6).fill(''))
const codeIndex = ref(0)
const submitting = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)
const showPwd1 = ref(false)
const showPwd2 = ref(false)
const autoLoggingIn = ref(false)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const canSendCode = computed(() => {
  return email.value.trim().length > 0 && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())
})

const canSubmit = computed(() => {
  return codeValue.value.length === 6
})

const canSetPassword = computed(() => {
  return password.value.length >= 6 && password.value === confirmPassword.value
})

watch(codeValue, (val) => {
  const digits = val.split('')
  for (let i = 0; i < 6; i++) {
    codeDigits.value[i] = digits[i] || ''
  }
  codeIndex.value = Math.min(val.length, 5)
})

const onCodeInput = (e: any) => {
  let val = e.detail.value || ''
  val = val.replace(/\D/g, '').slice(0, 6)
  codeValue.value = val
}

const onCodeFocus = () => {
  codeIndex.value = Math.min(codeValue.value.length, 5)
}

const onCodeBlur = () => {
  codeIndex.value = -1
}

const onSendCode = async () => {
  if (!email.value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    uni.showToast({ title: '请输入正确的邮箱', icon: 'none' })
    return
  }
  if (countdown.value > 0 || sendingCode.value) return
  sendingCode.value = true
  try {
    await sendEmailCode(email.value.trim())
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    step.value = 2
    countdown.value = 60
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0 && countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch (e: any) {
    const msg = e?.msg || e?.message || '发送失败'
    uni.showToast({ title: msg, icon: 'none' })
  } finally {
    sendingCode.value = false
  }
}

const onVerify = async () => {
  if (!canSubmit.value || submitting.value) return
  step.value = 3
}

const onRegister = async () => {
  if (!canSetPassword.value || submitting.value) return
  submitting.value = true
  try {
    await registerUser({
      username: email.value.trim().split('@')[0],
      password: password.value,
      email: email.value.trim(),
      code: codeValue.value,
    })
    step.value = 4
  } catch (e: any) {
    const msg = e?.response?.message || e?.response?.error_description || e?.message || '注册失败'
    uni.showToast({ title: msg, icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const goLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' })
}

const onAutoLogin = async () => {
  if (autoLoggingIn.value) return
  autoLoggingIn.value = true
  try {
    await loginByEmailCode(email.value.trim(), codeValue.value)
    const info = await getUserInfo()
    uni.setStorageSync('userInfo', info)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.reLaunch({ url: '/pages/index/index' })
      }
    }, 500)
  } catch (e: any) {
    const resp = e?.response || {}
    const msg = resp.error_description || resp.message || e?.message || '自动登录失败，请手动登录'
    uni.showToast({ title: msg, icon: 'none', duration: 2500 })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/user/login' })
    }, 2000)
  } finally {
    autoLoggingIn.value = false
  }
}

const goBack = () => {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.reLaunch({ url: '/pages/index/index' })
  }
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
})
</script>

<style lang="scss" scoped>
$primary: #4A90E2;
$text: #1D1D1F;
$sub: #86868B;
$bg: #F5F5F7;
$border: #E5E5EA;

.register-page {
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

.email-text {
  margin-top: 8rpx;
  font-size: 26rpx;
  color: $text;
  display: block;
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
  font-size: 28rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
  color: $sub;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40rpx;
  height: 40rpx;
}

/* 简洁黑白图标 */
.icon-mail::before {
  content: '';
  display: block;
  width: 26rpx;
  height: 18rpx;
  border: 2rpx solid $sub;
  border-radius: 4rpx;
  position: relative;
}
.icon-mail::after {
  content: '';
  position: absolute;
  width: 14rpx;
  height: 14rpx;
  border: 2rpx solid $sub;
  border-radius: 2rpx;
  transform: rotate(45deg);
  border-right: none;
  border-bottom: none;
  margin-top: 2rpx;
}

.icon-lock::before {
  content: '';
  display: block;
  width: 24rpx;
  height: 18rpx;
  border: 2rpx solid $sub;
  border-radius: 4rpx;
  position: relative;
  margin-top: 8rpx;
}
.icon-lock::after {
  content: '';
  position: absolute;
  width: 10rpx;
  height: 8rpx;
  border: 2rpx solid $sub;
  border-radius: 50% 50% 0 0;
  border-bottom: none;
  margin-top: -14rpx;
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

/* 验证码输入区域 */
.code-input-wrap {
  margin-top: 48rpx;
  position: relative;
  height: 96rpx;
}

.real-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: 10;
  caret-color: transparent;
}

.code-boxes {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  pointer-events: none;
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
  background: #fff;
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

.login-btn {
  margin-top: 48rpx;
  background: $primary;
  height: 96rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn.disabled {
  background: #C7C7CC;
}

.login-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: 600;
}

.success-area {
  margin-top: 120rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 32rpx;
}

.success-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #34C759;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  color: #fff;
  font-weight: 700;
}

.success-title {
  margin-top: 32rpx;
  font-size: 36rpx;
  font-weight: 700;
  color: $text;
}

.success-desc {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $sub;
}

.auto-login-btn {
  margin-top: 64rpx;
  background: $primary;
  height: 96rpx;
  border-radius: 16rpx;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auto-login-btn.loading {
  opacity: 0.7;
}

.auto-login-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: 600;
}

.manual-login-row {
  margin-top: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.manual-login-link {
  font-size: 26rpx;
  color: $primary;
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
