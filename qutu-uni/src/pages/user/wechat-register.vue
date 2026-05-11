<template>
  <view class="wechat-register-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
    </view>

    <view class="logo-area">
      <image class="logo-img" src="/static/logo.png" mode="aspectFit" />
    </view>

    <view class="title-area">
      <text class="title">完善账号信息</text>
      <text class="subtitle">绑定手机号或邮箱，即可完成注册</text>
    </view>

    <!-- 注册方式选择 -->
    <view class="method-tabs">
      <view
        class="method-tab"
        :class="{ active: registerMethod === 'phone' }"
        @click="registerMethod = 'phone'"
      >
        <SFIcon name="phone" :size="32" :color="registerMethod === 'phone' ? '#07C160' : '#86868B'" />
        <text class="method-text" :class="{ active: registerMethod === 'phone' }">手机号快捷注册</text>
      </view>
      <view
        class="method-tab"
        :class="{ active: registerMethod === 'email' }"
        @click="registerMethod = 'email'"
      >
        <SFIcon name="mail" :size="32" :color="registerMethod === 'email' ? '#4A90E2' : '#86868B'" />
        <text class="method-text" :class="{ active: registerMethod === 'email' }">邮箱注册</text>
      </view>
    </view>

    <!-- 手机号快捷注册 -->
    <view v-if="registerMethod === 'phone'" class="form-area">
      <view class="phone-hint">
        <SFIcon name="shield" :size="28" color="#07C160" />
        <text class="phone-hint-text">使用微信绑定的手机号快速注册，安全便捷</text>
      </view>

      <view class="wechat-phone-btn-area">
        <!-- #ifdef MP-WEIXIN -->
        <button
          class="wechat-phone-btn"
          open-type="getPhoneNumber"
          @getphonenumber="onGetPhoneNumber"
          :loading="registering"
          :disabled="registering"
        >
          <SFIcon name="wechat" :size="36" color="#fff" />
          <text class="wechat-phone-btn-text">{{ registering ? '注册中...' : '微信手机号快捷注册' }}</text>
        </button>
        <!-- #endif -->
        <!-- #ifndef MP-WEIXIN -->
        <view class="wechat-phone-btn disabled">
          <text class="wechat-phone-btn-text">仅微信小程序可用</text>
        </view>
        <!-- #endif -->
      </view>

      <view class="divider">
        <view class="divider-line" />
        <text class="divider-text">或手动输入手机号</text>
        <view class="divider-line" />
      </view>

      <view class="input-row">
        <text class="input-prefix">+86</text>
        <input
          class="input"
          v-model="phone"
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
        />
      </view>
      <view class="input-row">
        <input
          class="input code-input"
          v-model="smsCode"
          placeholder="请输入验证码"
          type="number"
          maxlength="6"
        />
        <view
          class="send-code-btn"
          :class="{ disabled: smsCooldown > 0 || !canSendSms }"
          @click="onSendSms"
        >
          <text class="send-code-text">{{ smsCooldown > 0 ? `${smsCooldown}s` : '获取验证码' }}</text>
        </view>
      </view>
      <view class="login-btn" :class="{ disabled: !canPhoneRegister || registering }" @click="onPhoneRegister">
        <text class="login-text">{{ registering ? '注册中...' : '注册并绑定微信' }}</text>
      </view>
    </view>

    <!-- 邮箱注册 -->
    <view v-if="registerMethod === 'email'" class="form-area">
      <view class="input-row">
        <text class="input-icon-text">@</text>
        <input
          class="input"
          v-model="email"
          placeholder="请输入邮箱"
          type="text"
        />
      </view>
      <view class="input-row">
        <input
          class="input code-input"
          v-model="emailCode"
          placeholder="请输入邮箱验证码"
          type="text"
        />
        <view
          class="send-code-btn"
          :class="{ disabled: emailCooldown > 0 || !canSendEmail }"
          @click="onSendEmailCode"
        >
          <text class="send-code-text">{{ emailCooldown > 0 ? `${emailCooldown}s` : '获取验证码' }}</text>
        </view>
      </view>
      <view class="input-row">
        <text class="input-icon-text">🔒</text>
        <input
          class="input"
          :password="!showPwd"
          v-model="password"
          placeholder="请设置密码（至少6位）"
        />
        <view class="toggle-pwd" @click="showPwd = !showPwd">
          <SFIcon :name="showPwd ? 'eye' : 'eye-off'" :size="32" color="#86868B" />
        </view>
      </view>
      <view class="input-row">
        <text class="input-icon-text">🔒</text>
        <input
          class="input"
          :password="!showPwd2"
          v-model="confirmPassword"
          placeholder="请再次输入密码"
        />
        <view class="toggle-pwd" @click="showPwd2 = !showPwd2">
          <SFIcon :name="showPwd2 ? 'eye' : 'eye-off'" :size="32" color="#86868B" />
        </view>
      </view>
      <view class="login-btn" :class="{ disabled: !canEmailRegister || registering }" @click="onEmailRegister">
        <text class="login-text">{{ registering ? '注册中...' : '注册并绑定微信' }}</text>
      </view>
    </view>

    <!-- 协议 -->
    <view class="agreement-row">
      <view class="checkbox" :class="{ checked: agreed }" @click="agreed = !agreed">
        <text v-if="agreed" class="check-icon">✓</text>
      </view>
      <text class="agreement-text">
        注册即代表同意
        <text class="agreement-link" @click="openAgreement('service')">《用户服务协议》</text>
        和
        <text class="agreement-link" @click="openAgreement('privacy')">《隐私政策》</text>
      </text>
    </view>

    <!-- 其他登录方式 -->
    <view class="other-login-row">
      <text class="other-text">已有账号？</text>
      <text class="other-link" @click="goLogin">去登录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  wechatGetOpenid,
  getWechatPhoneNumber,
  wechatQuickRegisterAndLogin,
  registerUser,
  sendEmailVerifyCode,
  sendVerifyCode,
  getUserInfo,
  wechatMpLogin,
} from '@/api/modules/user'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

const statusBarHeight = ref(0)
const registerMethod = ref<'phone' | 'email'>('phone')
const registering = ref(false)
const agreed = ref(false)

const openid = ref('')
const phone = ref('')
const smsCode = ref('')
const smsCooldown = ref(0)
let smsTimer: ReturnType<typeof setInterval> | null = null

const email = ref('')
const emailCode = ref('')
const emailCooldown = ref(0)
let emailTimer: ReturnType<typeof setInterval> | null = null

const password = ref('')
const confirmPassword = ref('')
const showPwd = ref(false)
const showPwd2 = ref(false)

const canSendSms = computed(() => /^1\d{10}$/.test(phone.value))
const canSendEmail = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value))

const canPhoneRegister = computed(() => {
  return phone.value.trim().length > 0 && smsCode.value.trim().length >= 4 && agreed.value
})

const canEmailRegister = computed(() => {
  return email.value.trim().length > 0
    && emailCode.value.trim().length >= 4
    && password.value.length >= 6
    && password.value === confirmPassword.value
    && agreed.value
})

const onGetPhoneNumber = async (e: any) => {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }
  if (registering.value) return

  const phoneCode = e?.detail?.code
  if (!phoneCode) {
    if (e?.detail?.errMsg?.includes('deny') || e?.detail?.errMsg?.includes('cancel')) {
      uni.showToast({ title: '您拒绝了手机号授权', icon: 'none' })
    } else {
      uni.showToast({ title: '获取手机号失败', icon: 'none' })
    }
    return
  }

  registering.value = true
  try {
    if (!openid.value) {
      openid.value = await wechatGetOpenid()
    }

    const phoneInfo = await getWechatPhoneNumber(phoneCode)
    const purePhone = phoneInfo.purePhoneNumber || phoneInfo.phoneNumber?.replace(/^\+?86/, '')

    if (!purePhone) {
      uni.showToast({ title: '获取手机号失败', icon: 'none' })
      return
    }

    await wechatQuickRegisterAndLogin({
      miniOpenid: openid.value,
      phone: purePhone,
    })

    try {
      const info = await getUserInfo()
      uni.setStorageSync('userInfo', info)
    } catch (e) {}

    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 500)
  } catch (e: any) {
    const msg = e?.response?.message || e?.response?.error_description || e?.message || '注册失败'
    uni.showToast({ title: msg, icon: 'none', duration: 2500 })
  } finally {
    registering.value = false
  }
}

const onSendSms = async () => {
  if (smsCooldown.value > 0 || !canSendSms.value) return
  try {
    await sendVerifyCode(phone.value)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    smsCooldown.value = 60
    smsTimer = setInterval(() => {
      smsCooldown.value--
      if (smsCooldown.value <= 0 && smsTimer) {
        clearInterval(smsTimer)
        smsTimer = null
      }
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e?.message || '发送失败', icon: 'none' })
  }
}

const onPhoneRegister = async () => {
  if (!canPhoneRegister.value || registering.value) return
  registering.value = true
  try {
    if (!openid.value) {
      openid.value = await wechatGetOpenid()
    }

    await wechatQuickRegisterAndLogin({
      miniOpenid: openid.value,
      phone: phone.value.trim(),
    })

    try {
      const info = await getUserInfo()
      uni.setStorageSync('userInfo', info)
    } catch (e) {}

    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 500)
  } catch (e: any) {
    const msg = e?.response?.message || e?.response?.error_description || e?.message || '注册失败'
    uni.showToast({ title: msg, icon: 'none', duration: 2500 })
  } finally {
    registering.value = false
  }
}

const onSendEmailCode = async () => {
  if (emailCooldown.value > 0 || !canSendEmail.value) return
  try {
    await sendEmailVerifyCode(email.value.trim())
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    emailCooldown.value = 60
    emailTimer = setInterval(() => {
      emailCooldown.value--
      if (emailCooldown.value <= 0 && emailTimer) {
        clearInterval(emailTimer)
        emailTimer = null
      }
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e?.message || '发送失败', icon: 'none' })
  }
}

const onEmailRegister = async () => {
  if (!canEmailRegister.value || registering.value) return
  registering.value = true
  try {
    if (!openid.value) {
      openid.value = await wechatGetOpenid()
    }

    await registerUser({
      username: email.value.trim().split('@')[0],
      password: password.value,
      email: email.value.trim(),
      code: emailCode.value.trim(),
      randomStr: email.value.trim(),
      miniOpenid: openid.value,
    })

    try {
      await wechatMpLogin()
      const info = await getUserInfo()
      uni.setStorageSync('userInfo', info)
    } catch (e) {}

    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 500)
  } catch (e: any) {
    const msg = e?.response?.message || e?.response?.error_description || e?.message || '注册失败'
    uni.showToast({ title: msg, icon: 'none', duration: 2500 })
  } finally {
    registering.value = false
  }
}

const openAgreement = (type: string) => {
  uni.showToast({ title: '协议页面开发中', icon: 'none' })
}

const goLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' })
}

const goBack = () => {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.reLaunch({ url: '/pages/index/index' })
  }
}

onMounted(async () => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20

  try {
    openid.value = await wechatGetOpenid()
  } catch (e) {}
})
</script>

<style lang="scss" scoped>
$primary: #4A90E2;
$wechat: #07C160;
$text: #1D1D1F;
$sub: #86868B;
$bg: #F5F5F7;
$border: #E5E5EA;

.wechat-register-page {
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
  margin-top: 32rpx;
  display: flex;
  justify-content: center;
}

.logo-img {
  width: 240rpx;
  height: 100rpx;
}

.title-area {
  margin-top: 32rpx;
}

.title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text;
  display: block;
}

.subtitle {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $sub;
  display: block;
}

.method-tabs {
  margin-top: 40rpx;
  display: flex;
  gap: 24rpx;
}

.method-tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 28rpx 20rpx;
  border-radius: 20rpx;
  border: 2rpx solid $border;
  background: #fff;
  transition: all 0.2s;
}

.method-tab.active {
  border-color: $wechat;
  background: rgba(7, 193, 96, 0.04);
}

.method-text {
  font-size: 26rpx;
  color: $sub;
}

.method-text.active {
  color: $wechat;
  font-weight: 600;
}

.form-area {
  margin-top: 40rpx;
}

.phone-hint {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 20rpx 24rpx;
  background: rgba(7, 193, 96, 0.06);
  border-radius: 16rpx;
  margin-bottom: 32rpx;
}

.phone-hint-text {
  font-size: 24rpx;
  color: $sub;
  flex: 1;
}

.wechat-phone-btn-area {
  margin-bottom: 32rpx;
}

.wechat-phone-btn {
  width: 100%;
  height: 96rpx;
  background: $wechat;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  border: none;
  padding: 0;

  &::after {
    border: none;
  }
}

.wechat-phone-btn.disabled {
  background: #C7C7CC;
}

.wechat-phone-btn-text {
  font-size: 30rpx;
  color: #fff;
  font-weight: 600;
}

.divider {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 32rpx;
}

.divider-line {
  flex: 1;
  height: 1rpx;
  background: $border;
}

.divider-text {
  font-size: 24rpx;
  color: $sub;
  white-space: nowrap;
}

.input-row {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 2rpx solid #f0f0f0;
}

.input-prefix {
  font-size: 30rpx;
  color: $text;
  font-weight: 500;
  margin-right: 16rpx;
  padding-right: 16rpx;
  border-right: 2rpx solid $border;
}

.input-icon-text {
  font-size: 28rpx;
  margin-right: 16rpx;
  color: $sub;
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

.login-btn {
  margin-top: 48rpx;
  background: $wechat;
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

.agreement-row {
  margin-top: 32rpx;
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  padding: 0 8rpx;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border-radius: 6rpx;
  border: 2rpx solid $border;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 4rpx;
  transition: all 0.2s;
}

.checkbox.checked {
  background: $wechat;
  border-color: $wechat;
}

.check-icon {
  font-size: 20rpx;
  color: #fff;
}

.agreement-text {
  font-size: 22rpx;
  color: $sub;
  line-height: 1.6;
}

.agreement-link {
  color: $primary;
}

.other-login-row {
  margin-top: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.other-text {
  font-size: 26rpx;
  color: $sub;
}

.other-link {
  font-size: 26rpx;
  color: $primary;
  font-weight: 500;
}
</style>
