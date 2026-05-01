<template>
  <view class="vip-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">会员中心</text>
      <view class="nav-right"></view>
    </view>

    <view class="vip-header">
      <image class="vip-avatar" :src="userInfo.avatar" mode="aspectFill" />
      <view class="vip-info">
        <text class="vip-name">{{ userInfo.nickname }}</text>
        <text class="vip-level">{{ vipLevelText }}</text>
        <text class="vip-expire" v-if="userInfo.vipExpireDate">有效期至 {{ userInfo.vipExpireDate }}</text>
      </view>
    </view>

    <view class="benefits-card">
      <text class="section-title">会员权益</text>
      <view class="benefits-grid">
        <view class="benefit-item" v-for="benefit in benefits" :key="benefit.id">
          <text class="benefit-icon">{{ benefit.icon }}</text>
          <text class="benefit-name">{{ benefit.name }}</text>
          <text class="benefit-desc">{{ benefit.desc }}</text>
        </view>
      </view>
    </view>

    <view class="packages-card">
      <text class="section-title">开通会员</text>
      <view class="package-item" v-for="pkg in packages" :key="pkg.id" :class="{ active: selectedPackageId === pkg.id }" @click="selectedPackageId = pkg.id">
        <view class="package-left">
          <text class="package-name">{{ pkg.name }}</text>
          <text class="package-duration">{{ pkg.duration }}天</text>
        </view>
        <view class="package-right">
          <text class="package-price">¥{{ pkg.price }}</text>
          <text class="package-origin">¥{{ pkg.originalPrice }}</text>
          <text class="package-tag" v-if="pkg.tag">{{ pkg.tag }}</text>
        </view>
      </view>
      <view class="buy-btn" @click="purchaseVipPackage">立即开通</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getUserInfo, getVipBenefits, getVipPackages, purchaseVip } from '@/api/modules/user'
import type { UserInfo, VipBenefit, VipPackage } from '@/api/modules/user'

const statusBarHeight = ref(0)
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
  stats: {
    trips: 0,
    spots: 0,
    guides: 0,
    footprints: 0
  }
})

const benefits = ref<VipBenefit[]>([])
const packages = ref<VipPackage[]>([])
const selectedPackageId = ref<number | null>(null)

const vipLevelText = computed(() => {
  if (userInfo.value.vipLevel === 1) return '月度会员'
  if (userInfo.value.vipLevel === 2) return '年度会员'
  return '普通用户'
})

const loadVipData = async () => {
  const [info, benefitList, packageList] = await Promise.all([
    getUserInfo(),
    getVipBenefits(),
    getVipPackages()
  ])
  userInfo.value = info
  benefits.value = benefitList
  packages.value = packageList
  selectedPackageId.value = packageList[0]?.id || null
}

const purchaseVipPackage = async () => {
  if (!selectedPackageId.value) {
    uni.showToast({ title: '请选择套餐', icon: 'none' })
    return
  }
  const res = await purchaseVip(selectedPackageId.value)
  if (res.success) {
    uni.showToast({ title: '开通成功', icon: 'success' })
  }
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  loadVipData()
})
</script>

<style lang="scss" scoped>
.vip-page {
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

.vip-header {
  margin: 24rpx;
  padding: 24rpx;
  background: linear-gradient(135deg, #FFE5B4 0%, #FFD89B 100%);
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.vip-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #FFFFFF;
}

.vip-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.vip-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #5D4E37;
}

.vip-level {
  font-size: 24rpx;
  color: #8B7355;
}

.vip-expire {
  font-size: 22rpx;
  color: #8B7355;
}

.benefits-card,
.packages-card {
  margin: 24rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 24rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 20rpx;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.benefit-item {
  background: #F7F7FA;
  border-radius: 16rpx;
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  text-align: center;
}

.benefit-icon {
  font-size: 32rpx;
}

.benefit-name {
  font-size: 24rpx;
  color: #1D1D1F;
}

.benefit-desc {
  font-size: 20rpx;
  color: #86868B;
}

.package-item {
  padding: 20rpx;
  border-radius: 16rpx;
  border: 2rpx solid #E5E5EA;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.package-item.active {
  border-color: #007AFF;
  background: #F0F7FF;
}

.package-left {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.package-name {
  font-size: 26rpx;
  color: #1D1D1F;
  font-weight: 600;
}

.package-duration {
  font-size: 22rpx;
  color: #86868B;
}

.package-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

.package-price {
  font-size: 28rpx;
  color: #FF2D55;
  font-weight: 600;
}

.package-origin {
  font-size: 20rpx;
  color: #C7C7CC;
  text-decoration: line-through;
}

.package-tag {
  font-size: 20rpx;
  color: #FFFFFF;
  background: #FF9500;
  padding: 2rpx 8rpx;
  border-radius: 8rpx;
}

.buy-btn {
  margin-top: 12rpx;
  height: 80rpx;
  border-radius: 20rpx;
  background: #1D1D1F;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}
</style>
