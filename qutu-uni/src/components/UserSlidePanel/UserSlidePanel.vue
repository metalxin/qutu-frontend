<template>
  <view v-if="everVisible">
    <view class="sidebar-mask" :class="{ show: visible }" @click="close"></view>
    <view class="user-sidebar" :class="{ show: visible }">
      <view class="sidebar-top" :style="sidebarTopStyle">
        <view class="sidebar-header">
          <view class="sidebar-avatar">
            <view class="avatar-circle">
              <image class="avatar-image" :src="displayAvatar" mode="aspectFill" />
            </view>
          </view>
          <view class="sidebar-user-info" @click="goToProfile">
            <text class="sidebar-username">{{ isLoggedIn ? (userInfo.nickname || '旅行者') : '未登录' }}</text>
            <view class="sidebar-bindwx" v-if="!isLoggedIn" @click.stop="goToLogin">
              <text class="bindwx-text">账号登录</text>
            </view>
            <view class="sidebar-bindwx" v-else-if="!userInfo.wechatBound" @click.stop="bindWechat">
              <text class="bindwx-text">微信登录</text>
            </view>
            <view class="sidebar-bound" v-else>
              <text class="bound-text">已绑定微信</text>
            </view>
          </view>
          <view class="sidebar-actions">
            <view class="action-btn" @click="goToSettings">
              <SFIcon name="settings" :size="36" color="#86868B" />
            </view>
          </view>
        </view>
      </view>

      <view class="sidebar-vip-card" @click="goToVipCenter">
        <view class="vip-left">
          <view class="vip-icon">👑</view>
          <view class="vip-info">
            <text class="vip-title">会员中心</text>
            <text class="vip-desc">开通会员，尊享会员权益</text>
          </view>
        </view>
        <view class="vip-action">
          <text class="vip-action-text">去看看</text>
          <SFIcon name="chevron-right" :size="28" color="#5D4E37" />
        </view>
      </view>

      <scroll-view class="sidebar-menu" scroll-y :show-scrollbar="false">
        <view class="menu-group">
          <view class="menu-item" @click="handleMenuClick('routes')">
            <view class="menu-icon" style="background: #E8F5E9;">
              <SFIcon name="route" :size="36" color="#34C759" />
            </view>
            <text class="menu-text">我的路线</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
          <view class="menu-item" @click="handleMenuClick('frequent')">
            <view class="menu-icon" style="background: #FFF3E0;">
              <SFIcon name="location" :size="36" color="#FF9500" />
            </view>
            <text class="menu-text">常在地点</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
          <view class="menu-item" @click="handleMenuClick('favorites')">
            <view class="menu-icon" style="background: #FFF8E1;">
              <SFIcon name="star" :size="36" color="#FFB800" />
            </view>
            <text class="menu-text">收藏景点</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
          <view class="menu-item" @click="handleMenuClick('favoriteGuides')">
            <view class="menu-icon" style="background: #E3F2FD;">
              <SFIcon name="bookmark" :size="36" color="#007AFF" />
            </view>
            <text class="menu-text">收藏攻略</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
        </view>

        <view class="menu-group">
          <view class="menu-item" @click="handleMenuClick('messages')">
            <view class="menu-icon" style="background: #FCE4EC;">
              <SFIcon name="bell" :size="36" color="#FF2D55" />
            </view>
            <text class="menu-text">消息中心</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
        </view>
        <view class="menu-group" v-if="isLoggedIn">
          <view class="menu-item" @click="doLogout">
            <view class="menu-icon" style="background: #FDECEA;">
              <SFIcon name="power" :size="36" color="#FF3B30" />
            </view>
            <text class="menu-text" style="color:#FF3B30;">退出登录</text>
            <SFIcon name="chevron-right" :size="28" color="#F5B1AD" />
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getUserInfo, bindWechat as bindWechatApi, logout as logoutApi } from '@/api/modules/user'
import { notificationWs } from '@/api/modules/notification-ws'
import type { UserInfo } from '@/api/modules/user'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const everVisible = ref(false)

watch(() => props.visible, (val) => {
  if (val) everVisible.value = true
})

const statusBarHeight = ref(20)
const menuButtonTop = ref(0)
const menuButtonHeight = ref(32)
const menuButtonLeft = ref(0)
const windowWidth = ref(375)

const userInfo = ref<UserInfo>({
  id: 0,
  username: '',
  nickname: '',
  name: '',
  avatar: '',
  phone: '',
  email: '',
  wechatBound: false,
  vipLevel: 0,
  vipExpireDate: '',
  createTime: '',
  permissions: [],
  roles: [],
  stats: { trips: 0, spots: 0, guides: 0, footprints: 0 }
})

const token = ref('')
const isLoggedIn = computed(() => !!token.value || !!userInfo.value.id)

const displayAvatar = computed(() => {
  if (userInfo.value.avatar) return userInfo.value.avatar
  try {
    const cachedUser = uni.getStorageSync('userInfo')
    if (cachedUser?.avatar) return cachedUser.avatar
  } catch {}
  return ''
})

const sidebarTopStyle = computed(() => {
  // #ifdef MP-WEIXIN
  const capsuleRight = windowWidth.value - menuButtonLeft.value - 87
  return {
    paddingTop: menuButtonTop.value + 'px',
    minHeight: (menuButtonHeight.value + 16) + 'px',
    paddingRight: (capsuleRight + 97) + 'px'
  }
  // #endif
  // #ifndef MP-WEIXIN
  return {
    paddingTop: (statusBarHeight.value + 10) + 'px',
    minHeight: '56px'
  }
  // #endif
})

const close = () => {
  emit('update:visible', false)
}

const syncAuthCache = () => {
  try { token.value = uni.getStorageSync('token') || '' } catch { token.value = '' }
  try {
    const cachedUser = uni.getStorageSync('userInfo')
    if (cachedUser) userInfo.value = cachedUser
  } catch {}
}

const loadUserInfo = async () => {
  syncAuthCache()
  if (!token.value) return
  try {
    const res = await getUserInfo()
    userInfo.value = res
    uni.setStorageSync('userInfo', res)
  } catch {}
}

const bindWechat = async () => {
  if (userInfo.value.wechatBound) {
    uni.showToast({ title: '已绑定微信', icon: 'none' })
    return
  }
  // #ifdef MP-WEIXIN
  try {
    const { wechatMpLogin } = await import('@/api/modules/user')
    await wechatMpLogin()
    userInfo.value.wechatBound = true
    uni.showToast({ title: '绑定成功', icon: 'success' })
  } catch {
    uni.showToast({ title: '绑定失败', icon: 'none' })
  }
  // #endif
  // #ifndef MP-WEIXIN
  uni.navigateTo({ url: '/pages/user/login' })
  // #endif
}

const goToSettings = () => {
  close()
  uni.navigateTo({ url: '/packageUser/pages/user/settings' })
}

const goToVipCenter = () => {
  close()
  uni.navigateTo({ url: '/packageUser/pages/user/vip' })
}

const handleMenuClick = (menu: string) => {
  close()
  const menuRoutes: Record<string, string> = {
    'routes': '/packageTrip/pages/route/list',
    'frequent': '/packageUser/pages/user/frequent',
    'favorites': '/packageUser/pages/user/favorites',
    'favoriteGuides': '/packageUser/pages/user/favorite-guides',
    'messages': '/packageMessage/pages/message/index'
  }
  const target = menuRoutes[menu]
  if (target) {
    uni.navigateTo({ url: target })
    return
  }
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

const goToProfile = () => {
  close()
  if (!isLoggedIn.value) {
    uni.navigateTo({ url: '/pages/user/login' })
    return
  }
  uni.navigateTo({ url: '/packageUser/pages/user/profile' })
}

const goToLogin = () => {
  close()
  uni.navigateTo({ url: '/pages/user/login' })
}

const doLogout = async () => {
  try { await logoutApi() } catch {}
  try {
    uni.removeStorageSync('token')
    uni.removeStorageSync('refresh_token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('userId')
  } catch {}
  // 退出登录时断开 WebSocket
  notificationWs.disconnect()
  token.value = ''
  userInfo.value = {
    id: 0, username: '', nickname: '', name: '', avatar: '',
    phone: '', email: '', wechatBound: false, vipLevel: 0,
    vipExpireDate: '', createTime: '', permissions: [], roles: [],
    stats: { trips: 0, spots: 0, guides: 0, footprints: 0 }
  }
  close()
  uni.showToast({ title: '已退出登录', icon: 'success' })
}

watch(() => props.visible, (val) => {
  if (val) {
    syncAuthCache()
    loadUserInfo()
  }
})

const initLayout = () => {
  const sys = uni.getSystemInfoSync()
  statusBarHeight.value = sys.statusBarHeight || 20
  windowWidth.value = sys.windowWidth || 375
  // #ifdef MP-WEIXIN
  try {
    const mb = uni.getMenuButtonBoundingClientRect()
    if (mb) {
      menuButtonLeft.value = mb.left
      menuButtonTop.value = mb.top
      menuButtonHeight.value = mb.height
    }
  } catch {}
  // #endif
}

initLayout()
</script>

<style lang="scss" scoped>
.sidebar-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  z-index: 1000;
  pointer-events: none;
  transition: background 0.35s ease;

  &.show {
    background: rgba(0, 0, 0, 0.3);
    pointer-events: auto;
  }
}

.user-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 72%;
  background: #F5F5F7;
  z-index: 1001;
  transform: translateX(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;

  &.show {
    transform: translateX(0);
  }
}

.sidebar-top {
  background: #FFFFFF;
  display: flex;
  align-items: flex-end;
  box-sizing: border-box;
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  padding-bottom: 20rpx;
  width: 100%;
  box-sizing: border-box;
  /* #ifdef MP-WEIXIN */
  padding-right: 16rpx;
  /* #endif */
}

.sidebar-avatar {
  margin-right: 16rpx;
}

.avatar-circle {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #E5E5EA;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.sidebar-user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  min-width: 0;
}

.sidebar-username {
  font-size: 26rpx;
  color: #1D1D1F;
  font-weight: 600;
}

.sidebar-bindwx {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 14rpx;
  background: #07C160;
  border-radius: 16rpx;
  align-self: flex-start;
}

.sidebar-bound {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 14rpx;
  background: #E8F5E9;
  border-radius: 16rpx;
  align-self: flex-start;
}

.bindwx-text {
  font-size: 18rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.bound-text {
  font-size: 18rpx;
  color: #34C759;
  font-weight: 500;
}

.sidebar-actions {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.action-btn {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.sidebar-vip-card {
  margin: 24rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, #FFE5B4 0%, #FFD89B 100%);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4rpx 16rpx rgba(255, 216, 155, 0.4);
}

.vip-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.vip-icon {
  font-size: 40rpx;
}

.vip-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.vip-title {
  font-size: 28rpx;
  color: #5D4E37;
  font-weight: 600;
}

.vip-desc {
  font-size: 20rpx;
  color: #8B7355;
}

.vip-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 12rpx 20rpx;
  background: rgba(255, 255, 255, 0.85);
  border-radius: 24rpx;
}

.vip-action-text {
  font-size: 22rpx;
  color: #5D4E37;
  font-weight: 500;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  padding-bottom: env(safe-area-inset-bottom);
}

.menu-group {
  margin: 16rpx 24rpx;
  margin-right: 32rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 20rpx;
  padding-right: 32rpx;
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F5F5F7;
  }
}

.menu-icon {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
}

.menu-text {
  flex: 1;
  font-size: 26rpx;
  color: #1D1D1F;
  font-weight: 400;
}
</style>
