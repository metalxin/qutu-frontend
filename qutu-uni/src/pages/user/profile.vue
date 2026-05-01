<template>
  <view class="profile-page">
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">个人资料</text>
      <view class="nav-right" :style="navRightStyle"></view>
    </view>

    <!-- 头像区域 -->
    <view class="profile-header">
      <view class="avatar-wrapper" @click="chooseAvatar">
        <image v-if="userInfo.avatar" class="avatar-image" :src="userInfo.avatar" mode="aspectFill" />
        <view class="avatar-placeholder" v-else>
          <SFIcon name="user" :size="48" color="#86868B" />
        </view>
        <view class="avatar-edit-badge">
          <SFIcon name="camera" :size="24" color="#fff" />
        </view>
      </view>
      <view class="header-info">
        <text class="header-name">{{ form.nickname || '旅行者' }}</text>
        <text class="header-subtitle">加入时间 {{ formatTime(userInfo.createTime) }}</text>
      </view>
    </view>

    <!-- 基本信息编辑 -->
    <view class="section-title">基本信息</view>
    <view class="form-card">
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input class="form-input" v-model="form.nickname" placeholder="请输入昵称" />
      </view>
      <view class="form-item">
        <text class="form-label">姓名</text>
        <input class="form-input" v-model="form.name" placeholder="请输入姓名" />
      </view>
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input class="form-input" v-model="form.phone" placeholder="请输入手机号" type="number" />
      </view>
    </view>

    <!-- 账号绑定 -->
    <view class="section-title">账号绑定</view>
    <view class="form-card">
      <view class="form-item" @click="onEmailClick">
        <text class="form-label">邮箱</text>
        <text class="form-value" :class="{ 'form-placeholder': !form.email }">{{ form.email || '未绑定' }}</text>
        <SFIcon name="chevron-right" :size="32" color="#C7C7CC" />
      </view>
      <view class="form-item" @click="onWechatClick">
        <text class="form-label">微信</text>
        <text class="form-value" :class="{ 'form-bound': userInfo.wechatBound }">{{ userInfo.wechatBound ? '已绑定' : '未绑定' }}</text>
        <SFIcon name="chevron-right" :size="32" color="#C7C7CC" />
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="section-title">旅行数据</view>
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-value">{{ stats.trips }}</text>
        <text class="stat-label">行程</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.spots }}</text>
        <text class="stat-label">地点</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.guides }}</text>
        <text class="stat-label">攻略</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.footprints }}</text>
        <text class="stat-label">足迹</text>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="action-bar">
      <view class="save-btn" @click="saveProfile">保存资料</view>
    </view>

    <!-- 邮箱绑定弹窗 -->
    <view class="modal-mask" v-if="showEmailModal" @click="showEmailModal = false">
      <view class="modal-content" @click.stop>
        <text class="modal-title">{{ form.email ? '更换邮箱' : '绑定邮箱' }}</text>
        <view class="modal-form-item">
          <input class="modal-input" v-model="emailForm.email" placeholder="请输入邮箱地址" />
        </view>
        <view class="modal-form-item code-row">
          <input class="modal-input code-input" v-model="emailForm.code" placeholder="验证码" />
          <view class="send-code-btn" :class="{ disabled: emailCooldown > 0 }" @click="sendEmailCode">
            <text class="send-code-text">{{ emailCooldown > 0 ? `${emailCooldown}s` : '获取验证码' }}</text>
          </view>
        </view>
        <view class="modal-btns">
          <view class="modal-btn cancel" @click="showEmailModal = false">取消</view>
          <view class="modal-btn confirm" @click="confirmBindEmail">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getUserInfo, editUserInfo, sendEmailVerifyCode, bindEmail, resolveFileUrl, getWechatOpenid, bindWechat, unbindWechat } from '@/api/modules/user'
import type { UserInfo } from '@/api/modules/user'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const userInfo = ref<UserInfo>({
  id: 0, username: '', nickname: '', name: '', avatar: '', phone: '', email: '',
  wechatBound: false, vipLevel: 0, vipExpireDate: '', createTime: '',
  permissions: [], roles: [],
  stats: { trips: 0, spots: 0, guides: 0, footprints: 0 }
})

const form = ref({ nickname: '', phone: '', name: '', email: '' })
const stats = ref({ trips: 0, spots: 0, guides: 0, footprints: 0 })

// 邮箱绑定
const showEmailModal = ref(false)
const emailForm = ref({ email: '', code: '' })
const emailCooldown = ref(0)
let emailTimer: ReturnType<typeof setInterval> | null = null

/** 格式化时间：后端返回 LocalDataTime 如 "2026-04-13T10:28:05" */
const formatTime = (t: string) => {
  if (!t) return ''
  // 兼容多种格式
  const d = t.replace('T', ' ').substring(0, 10)
  return d
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res
    form.value.nickname = res.nickname
    form.value.phone = res.phone
    form.value.name = res.name || ''
    form.value.email = res.email
    stats.value = res.stats || { trips: 0, spots: 0, guides: 0, footprints: 0 }
  } catch (e) {
    // 从缓存尝试
    try {
      const cached = uni.getStorageSync('userInfo')
      if (cached) {
        userInfo.value = cached
        form.value.nickname = cached.nickname
        form.value.phone = cached.phone
        form.value.name = cached.name || ''
        form.value.email = cached.email
        stats.value = cached.stats || { trips: 0, spots: 0, guides: 0, footprints: 0 }
      }
    } catch (e2) {}
  }
}

/** 选择并上传头像 */
const chooseAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempPath = res.tempFilePaths[0]
      uni.showLoading({ title: '上传中...' })
      try {
        const uploadRes: any = await new Promise((resolve, reject) => {
          uni.uploadFile({
            url: `${uni.getStorageSync('BASE_URL') || ''}/admin/sys-file/upload`,
            filePath: tempPath,
            name: 'file',
            header: {
              Authorization: uni.getStorageSync('token') ? uni.getStorageSync('token') : ''
            },
            success: (r) => {
              try { resolve(JSON.parse(r.data)) } catch (e) { reject(e) }
            },
            fail: reject
          })
        })
        if (uploadRes?.code === 0 && uploadRes?.data?.url) {
          const avatarUrl = resolveFileUrl(uploadRes.data.url)
          // 调用后端保存头像
          await editUserInfo({ avatar: uploadRes.data.url })
          userInfo.value.avatar = avatarUrl
          form.value.email = form.value.email // trigger reactivity
          // 更新缓存
          try {
            const cached = uni.getStorageSync('userInfo')
            if (cached) { cached.avatar = avatarUrl; uni.setStorageSync('userInfo', cached) }
          } catch (e) {}
          uni.showToast({ title: '头像已更新', icon: 'success' })
        } else {
          uni.showToast({ title: '上传失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    }
  })
}

/** 保存资料 */
const saveProfile = async () => {
  if (!form.value.nickname?.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  try {
    await editUserInfo({
      nickname: form.value.nickname,
      phone: form.value.phone,
      name: form.value.name
    })
    // 更新本地
    userInfo.value.nickname = form.value.nickname
    userInfo.value.phone = form.value.phone
    userInfo.value.name = form.value.name
    // 更新缓存
    try {
      const cached = uni.getStorageSync('userInfo')
      if (cached) {
        cached.nickname = form.value.nickname
        cached.phone = form.value.phone
        cached.name = form.value.name
        uni.setStorageSync('userInfo', cached)
      }
    } catch (e) {}
    uni.showToast({ title: '资料已保存', icon: 'success' })
  } catch (e: any) {
    const msg = e?.response?.message || e?.message || '保存失败'
    uni.showToast({ title: msg, icon: 'none' })
  }
}

/** 邮箱绑定/解绑 */
const onEmailClick = () => {
  if (form.value.email) {
    uni.showActionSheet({
      itemList: ['更换邮箱', '解绑邮箱'],
      success: (res) => {
        if (res.tapIndex === 0) {
          // 更换邮箱
          emailForm.value.email = ''
          emailForm.value.code = ''
          showEmailModal.value = true
        } else if (res.tapIndex === 1) {
          // 解绑邮箱
          uni.showModal({
            title: '提示',
            content: '确定要解绑邮箱吗？',
            success: async (modalRes) => {
              if (modalRes.confirm) {
                try {
                  await bindEmail({ email: '', code: '' })
                  form.value.email = ''
                  userInfo.value.email = ''
                  uni.showToast({ title: '已解绑', icon: 'success' })
                } catch (e: any) {
                  uni.showToast({ title: e?.message || '解绑失败', icon: 'none' })
                }
              }
            }
          })
        }
      }
    })
  } else {
    emailForm.value.email = ''
    emailForm.value.code = ''
    showEmailModal.value = true
  }
}

/** 发送邮箱验证码 */
const sendEmailCode = async () => {
  if (emailCooldown.value > 0) return
  if (!emailForm.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.value.email)) {
    uni.showToast({ title: '请输入正确的邮箱', icon: 'none' })
    return
  }
  try {
    await sendEmailVerifyCode(emailForm.value.email)
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

/** 确认绑定邮箱 */
const confirmBindEmail = async () => {
  if (!emailForm.value.email) {
    uni.showToast({ title: '请输入邮箱', icon: 'none' }); return
  }
  if (!emailForm.value.code) {
    uni.showToast({ title: '请输入验证码', icon: 'none' }); return
  }
  try {
    await bindEmail({ email: emailForm.value.email, code: emailForm.value.code })
    form.value.email = emailForm.value.email
    userInfo.value.email = emailForm.value.email
    showEmailModal.value = false
    uni.showToast({ title: '绑定成功', icon: 'success' })
  } catch (e: any) {
    uni.showToast({ title: e?.message || '绑定失败', icon: 'none' })
  }
}

/** 微信绑定/解绑 */
const onWechatClick = () => {
  if (userInfo.value.wechatBound) {
    uni.showActionSheet({
      itemList: ['解绑微信'],
      success: async (res) => {
        if (res.tapIndex === 0) {
          uni.showModal({
            title: '提示',
            content: '确定要解绑微信吗？解绑后将无法使用微信登录。',
            success: async (modalRes) => {
              if (modalRes.confirm) {
                try {
                  await unbindWechat()
                  userInfo.value.wechatBound = false
                  // 更新缓存
                  try {
                    const cached = uni.getStorageSync('userInfo')
                    if (cached) { cached.wechatBound = false; uni.setStorageSync('userInfo', cached) }
                  } catch (e) {}
                  uni.showToast({ title: '已解绑', icon: 'success' })
                } catch (e: any) {
                  uni.showToast({ title: e?.message || '解绑失败', icon: 'none' })
                }
              }
            }
          })
        }
      }
    })
  } else {
    // #ifdef MP-WEIXIN
    uni.showModal({
      title: '绑定微信',
      content: '将使用当前微信账号绑定',
      success: async (modalRes) => {
        if (modalRes.confirm) {
          try {
            uni.showLoading({ title: '绑定中...' })
            // 1. 获取微信openid
            const openid = await getWechatOpenid()
            // 2. 调用绑定接口
            await bindWechat(openid)
            userInfo.value.wechatBound = true
            // 更新缓存
            try {
              const cached = uni.getStorageSync('userInfo')
              if (cached) { cached.wechatBound = true; uni.setStorageSync('userInfo', cached) }
            } catch (e) {}
            uni.hideLoading()
            uni.showToast({ title: '绑定成功', icon: 'success' })
          } catch (e: any) {
            uni.hideLoading()
            uni.showToast({ title: e?.message || '绑定失败', icon: 'none' })
          }
        }
      }
    })
    // #endif
    // #ifndef MP-WEIXIN
    uni.showToast({ title: '请在微信小程序中绑定', icon: 'none' })
    // #endif
  }
}

const goBack = () => { uni.navigateBack() }

const navBarStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingTop: statusBarHeight.value + 'px', height: navBarHeight.value + 'px', paddingLeft: '24rpx', paddingRight: '24rpx', paddingBottom: '0px' }
  }
  return { paddingTop: statusBarHeight.value + 'px', minHeight: navBarHeight.value + 'px' }
})

const navRightStyle = computed(() => {
  return menuButtonSpace.value > 0 ? { width: menuButtonSpace.value + 'px' } : {}
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
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
$primary: #00C853;
$text: #1D1D1F;
$sub: #86868B;
$bg: #F5F5F7;
$card: #FFFFFF;
$border: #E5E5EA;

.profile-page { min-height: 100vh; background: $bg; padding-bottom: 120rpx; }
.nav-bar { padding: 24rpx 24rpx 16rpx; display: flex; align-items: center; justify-content: space-between; background: $card; }
.nav-left, .nav-right { width: 120rpx; display: flex; align-items: center; }
.nav-right { justify-content: flex-end; }
.back-icon { font-size: 40rpx; color: $text; }
.nav-title { font-size: 32rpx; font-weight: 600; color: $text; }

.profile-header {
  margin: 24rpx; padding: 24rpx; background: $card; border-radius: 20rpx;
  display: flex; align-items: center; gap: 20rpx;
}
.avatar-wrapper {
  width: 120rpx; height: 120rpx; border-radius: 50%; overflow: hidden;
  background: #F0F0F0; display: flex; align-items: center; justify-content: center;
  position: relative;
}
.avatar-image { width: 100%; height: 100%; }
.avatar-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.avatar-edit-badge {
  position: absolute; bottom: 0; right: 0; width: 40rpx; height: 40rpx;
  background: $primary; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  border: 2rpx solid #fff;
}
.header-info { display: flex; flex-direction: column; gap: 8rpx; }
.header-name { font-size: 32rpx; font-weight: 600; color: $text; }
.header-subtitle { font-size: 24rpx; color: $sub; }

.section-title {
  margin: 32rpx 24rpx 12rpx; font-size: 26rpx; color: $sub; font-weight: 500;
}
.form-card {
  margin: 0 24rpx; background: $card; border-radius: 20rpx; padding: 0 24rpx;
}
.form-item {
  display: flex; align-items: center; gap: 16rpx; padding: 28rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}
.form-item:last-child { border-bottom: 0; }
.form-label { width: 120rpx; font-size: 28rpx; color: $text; flex-shrink: 0; }
.form-input {
  flex: 1; background: $bg; border-radius: 12rpx; padding: 16rpx 20rpx;
  font-size: 26rpx; color: $text;
}
.form-value { flex: 1; font-size: 28rpx; color: $text; text-align: right; }
.form-placeholder { color: #C7C7CC; }
.form-bound { color: $primary; }

.stats-card {
  margin: 0 24rpx; background: $card; border-radius: 20rpx; padding: 24rpx;
  display: flex; justify-content: space-between;
}
.stat-item { flex: 1; text-align: center; }
.stat-value { font-size: 36rpx; font-weight: 700; color: $primary; }
.stat-label { font-size: 22rpx; color: $sub; margin-top: 4rpx; }

.action-bar { padding: 40rpx 24rpx 0; }
.save-btn {
  height: 88rpx; border-radius: 44rpx; background: $primary;
  color: #FFFFFF; display: flex; align-items: center; justify-content: center;
  font-size: 30rpx; font-weight: 600;
}

/* 弹窗样式 */
.modal-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 999;
}
.modal-content {
  width: 600rpx; background: $card; border-radius: 24rpx; padding: 40rpx;
}
.modal-title { font-size: 32rpx; font-weight: 600; color: $text; text-align: center; margin-bottom: 32rpx; }
.modal-form-item { margin-bottom: 20rpx; }
.modal-input {
  width: 100%; height: 80rpx; background: $bg; border-radius: 12rpx;
  padding: 0 20rpx; font-size: 28rpx; color: $text; box-sizing: border-box;
}
.code-row { display: flex; gap: 16rpx; align-items: center; }
.code-input { flex: 1; }
.send-code-btn {
  flex-shrink: 0; height: 80rpx; padding: 0 24rpx; background: $primary;
  border-radius: 12rpx; display: flex; align-items: center; justify-content: center;
}
.send-code-btn.disabled { background: #C7C7CC; }
.send-code-text { font-size: 24rpx; color: #fff; white-space: nowrap; }
.modal-btns { display: flex; gap: 20rpx; margin-top: 32rpx; }
.modal-btn {
  flex: 1; height: 80rpx; border-radius: 40rpx; display: flex;
  align-items: center; justify-content: center; font-size: 28rpx;
}
.modal-btn.cancel { background: $bg; color: $sub; }
.modal-btn.confirm { background: $primary; color: #fff; }
</style>
