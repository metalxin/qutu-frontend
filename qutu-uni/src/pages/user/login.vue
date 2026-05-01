<template>
  <view class="login-page">
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

    <!-- 欢迎文案 -->
    <view class="welcome-area">
      <text class="welcome-title">欢迎回来</text>
      <text class="welcome-subtitle">探索世界，遇见美好</text>
    </view>

    <!-- 登录方式 Tab -->
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ active: loginType === 'account' }"
        @click="loginType = 'account'"
      >
        <text class="tab-text">账号登录</text>
        <view v-if="loginType === 'account'" class="tab-line" />
      </view>
      <view
        class="tab-item"
        :class="{ active: loginType === 'emailCode' }"
        @click="loginType = 'emailCode'"
      >
        <text class="tab-text">邮箱验证码登录</text>
        <view v-if="loginType === 'emailCode'" class="tab-line" />
      </view>
    </view>

    <!-- 账号登录：账号/邮箱 + 密码 + 图片验证码 -->
    <view class="form-area" v-if="loginType === 'account'">
      <view class="input-row">
        <text class="input-icon icon-user"></text>
        <input
          class="input"
          v-model="username"
          placeholder="请输入账号或邮箱"
          type="text"
        />
      </view>
      <view class="input-row">
        <text class="input-icon icon-lock"></text>
        <input
          class="input"
          :password="!showPassword"
          v-model="password"
          placeholder="请输入密码"
        />
        <view class="toggle-pwd" @click="showPassword = !showPassword">
          <SFIcon
            :name="showPassword ? 'eye' : 'eye-off'"
            :size="36"
            color="#86868B"
          />
        </view>
      </view>
      <view class="input-row">
        <text class="input-icon icon-shield"></text>
        <input
          class="input"
          v-model="captchaCode"
          placeholder="请输入图片验证码"
          type="text"
        />
        <image
          class="captcha-img"
          :src="captchaImg"
          @click="refreshCaptcha"
          mode="scaleToFill"
        />
      </view>
    </view>

    <!-- 邮箱验证码登录：邮箱 + 邮箱验证码 -->
    <view class="form-area" v-if="loginType === 'emailCode'">
      <view class="input-row">
        <text class="input-icon icon-mail"></text>
        <input
          class="input"
          v-model="emailForCode"
          placeholder="请输入邮箱"
          type="text"
        />
      </view>
      <view class="input-row">
        <text class="input-icon icon-shield"></text>
        <input
          class="input code-input"
          v-model="emailCode"
          placeholder="请输入邮箱验证码"
          type="text"
        />
        <view
          class="send-code-btn"
          :class="{ disabled: emailCooldown > 0 }"
          @click="sendEmailCode"
        >
          <text class="send-code-text">{{ emailCooldown > 0 ? `${emailCooldown}s` : '获取验证码' }}</text>
        </view>
      </view>
    </view>

    <!-- 记住密码 + 忘记密码 -->
    <view class="remember-row" v-if="loginType !== 'emailCode'">
      <view class="remember-check" @click="rememberMe = !rememberMe">
        <view class="checkbox" :class="{ checked: rememberMe }">
          <text v-if="rememberMe" class="check-icon">✓</text>
        </view>
        <text class="remember-text">记住密码</text>
      </view>
      <text class="forgot-link" @click="goForgotPassword">忘记密码?</text>
    </view>
    <view class="remember-row" v-else>
      <view />
      <text class="forgot-link" @click="goForgotPassword">忘记密码?</text>
    </view>

    <!-- 登录按钮 -->
    <view class="login-btn" :class="{ disabled: !canLogin || logging }" @click="onLogin">
      <text class="login-text">{{ logging ? '登录中...' : '登录' }}</text>
    </view>

    <!-- 其他方式登录（仅微信小程序显示） -->
    <!-- #ifdef MP-WEIXIN -->
    <view class="other-login">
      <text class="other-login-text">或使用其他方式登录</text>
      <view class="wechat-login-btn" @click="onWxLogin">
        <SFIcon name="wechat" :size="36" color="#07C160" />
        <text class="wechat-login-text">微信登录</text>
      </view>
    </view>
    <!-- #endif -->

    <!-- 注册入口 -->
    <view class="register-row">
      <text class="register-text">还没有账号？</text>
      <text class="register-link" @click="goRegister">立即注册</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  loginByPassword,
  loginByEmailCode,
  sendEmailVerifyCode,
  wechatMpLogin,
  getUserInfo,
  getCaptchaImageUrl,
} from '@/api/modules/user'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

const statusBarHeight = ref(0)
const loginType = ref<'account' | 'emailCode'>('account')

// 账号登录
const username = ref('')
const password = ref('')
const captchaCode = ref('')
const captchaImg = ref('')
const randomStr = ref('')
const showPassword = ref(false)
const rememberMe = ref(false)
const logging = ref(false)

// 邮箱验证码登录
const emailForCode = ref('')
const emailCode = ref('')
const emailCooldown = ref(0)
let emailTimer: ReturnType<typeof setInterval> | null = null

const canLogin = computed(() => {
  if (loginType.value === 'account') {
    return username.value.trim().length > 0 && password.value.length >= 6 && captchaCode.value.trim().length > 0
  } else {
    return emailForCode.value.trim().length > 0 && emailCode.value.trim().length > 0
  }
})

const refreshCaptcha = () => {
  randomStr.value = Date.now().toString()
  captchaImg.value = getCaptchaImageUrl(randomStr.value)
}

const loadRemembered = () => {
  try {
    const saved = uni.getStorageSync('remembered_login')
    if (saved) {
      const parsed = JSON.parse(saved)
      username.value = parsed.username || ''
      password.value = parsed.password || ''
      rememberMe.value = true
    }
  } catch (e) {}
}

const saveRemembered = () => {
  try {
    if (rememberMe.value && loginType.value === 'account') {
      uni.setStorageSync(
        'remembered_login',
        JSON.stringify({
          username: username.value.trim(),
          password: password.value,
        })
      )
    } else {
      uni.removeStorageSync('remembered_login')
    }
  } catch (e) {}
}

/** 发送邮箱验证码 */
const sendEmailCode = async () => {
  if (emailCooldown.value > 0) return
  const targetEmail = emailForCode.value
  if (!targetEmail || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(targetEmail)) {
    uni.showToast({ title: '请输入正确的邮箱', icon: 'none' })
    return
  }
  try {
    await sendEmailVerifyCode(targetEmail)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    emailCooldown.value = 60
    emailTimer = setInterval(() => {
      emailCooldown.value--
      if (emailCooldown.value <= 0) {
        if (emailTimer) clearInterval(emailTimer)
        emailTimer = null
      }
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e?.message || '发送失败', icon: 'none' })
  }
}

const onLogin = async () => {
  if (!canLogin.value || logging.value) return
  logging.value = true
  try {
    if (loginType.value === 'account') {
      await loginByPassword(
        username.value.trim(),
        password.value,
        captchaCode.value.trim(),
        randomStr.value
      )
      saveRemembered()
    } else {
      await loginByEmailCode(
        emailForCode.value.trim(),
        emailCode.value.trim()
      )
    }
    try {
      const info = await getUserInfo()
      uni.setStorageSync('userInfo', info)
    } catch (e) {}
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
    refreshCaptcha()
    captchaCode.value = ''
    const msg = e?.response?.message || e?.response?.error_description || e?.message || '登录失败'
    uni.showToast({ title: msg, icon: 'none' })
  } finally {
    logging.value = false
  }
}

const onAppleLogin = async () => {
  uni.showToast({ title: 'Apple 登录开发中', icon: 'none' })
}

const onWxLogin = async () => {
  if (logging.value) return
  logging.value = true
  try {
    await wechatMpLogin()
    try {
      const info = await getUserInfo()
      uni.setStorageSync('userInfo', info)
    } catch (e) {}
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
    const msg = e?.response?.message || e?.response?.error_description || e?.message || '微信登录失败'
    uni.showToast({ title: msg, icon: 'none' })
  } finally {
    logging.value = false
  }
}

const goRegister = () => {
  uni.navigateTo({ url: '/pages/user/register' })
}

const goForgotPassword = () => {
  uni.navigateTo({ url: '/pages/user/forgot-password' })
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
  refreshCaptcha()
  loadRemembered()
})
</script>

<style lang="scss" scoped>
$primary: #4A90E2;
$text: #1D1D1F;
$sub: #86868B;
$bg: #F5F5F7;
$border: #E5E5EA;

.login-page {
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

.welcome-area {
  margin-top: 40rpx;
}

.welcome-title {
  font-size: 44rpx;
  font-weight: 700;
  color: $text;
  display: block;
}

.welcome-subtitle {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $sub;
  display: block;
}

.tab-bar {
  margin-top: 48rpx;
  display: flex;
  justify-content: space-around;
  border-bottom: 2rpx solid #f0f0f0;
}

.tab-item {
  position: relative;
  padding: 20rpx 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tab-text {
  font-size: 28rpx;
  color: $sub;
}

.tab-item.active .tab-text {
  color: $text;
  font-weight: 600;
}

.tab-line {
  position: absolute;
  bottom: -2rpx;
  width: 48rpx;
  height: 4rpx;
  background: $primary;
  border-radius: 2rpx;
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
  font-family: 'SF Pro Display', -apple-system, sans-serif;
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
.icon-user::before {
  content: '';
  display: block;
  width: 28rpx;
  height: 28rpx;
  border: 2rpx solid $sub;
  border-radius: 50% 50% 0 0;
  position: relative;
}
.icon-user::after {
  content: '';
  position: absolute;
  width: 16rpx;
  height: 10rpx;
  border: 2rpx solid $sub;
  border-radius: 50% 50% 0 0;
  border-bottom: none;
  margin-top: -6rpx;
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

.icon-shield::before {
  content: '';
  display: block;
  width: 22rpx;
  height: 26rpx;
  border: 2rpx solid $sub;
  border-radius: 0 0 50% 50%;
  position: relative;
}
.icon-shield::after {
  content: '';
  position: absolute;
  width: 22rpx;
  height: 10rpx;
  border: 2rpx solid $sub;
  border-radius: 50% 50% 0 0;
  border-bottom: none;
  margin-top: -12rpx;
}

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

.input {
  flex: 1;
  font-size: 30rpx;
  color: $text;
}

.code-input {
  flex: 1;
  min-width: 0;
}

.captcha-img {
  width: 200rpx;
  height: 80rpx;
  flex-shrink: 0;
  border-radius: 8rpx;
  background: #f0f0f0;
}

.send-code-btn {
  flex-shrink: 0;
  padding: 12rpx 24rpx;
  background: $primary;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-code-btn.disabled {
  background: #C7C7CC;
}

.send-code-text {
  font-size: 24rpx;
  color: #fff;
  white-space: nowrap;
}

.toggle-pwd {
  flex-shrink: 0;
  padding: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remember-row {
  margin-top: 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.remember-check {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border-radius: 8rpx;
  border: 2rpx solid $primary;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.checkbox.checked {
  background: $primary;
  border-color: $primary;
}

.check-icon {
  font-size: 24rpx;
  color: #fff;
}

.remember-text {
  font-size: 26rpx;
  color: $sub;
}

.forgot-link {
  font-size: 26rpx;
  color: $sub;
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

.other-login {
  margin-top: 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.other-login-text {
  font-size: 24rpx;
  color: $sub;
}

.other-login-icons {
  margin-top: 24rpx;
  display: flex;
  gap: 48rpx;
}

.social-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-icon.apple {
  background: #000;
}

.social-icon.wechat {
  background: #07C160;
}

.social-img {
  width: 40rpx;
  height: 40rpx;
}

.wechat-login-btn {
  margin-top: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 20rpx 48rpx;
  background: #fff;
  border: 2rpx solid #07C160;
  border-radius: 48rpx;
}

.wechat-login-text {
  font-size: 28rpx;
  color: #07C160;
  font-weight: 500;
}

.register-row {
  margin-top: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.register-text {
  font-size: 26rpx;
  color: $sub;
}

.register-link {
  font-size: 26rpx;
  color: $primary;
  font-weight: 500;
}
</style>
