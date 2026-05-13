<template>
  <view class="tabbar">
    <view class="tabbar-item" :class="{ active: current === 'home' }" @click="onTabClick('home')">
      <SFIcon name="home" :size="44" />
      <text class="tabbar-text">主页</text>
    </view>
    <view class="tabbar-center">
      <view class="add-btn" @click="$emit('add')">
        <SFIcon name="plus" :size="48" color="#FFFFFF" />
      </view>
    </view>
    <view class="tabbar-item" :class="{ active: current === 'nearby' }" @click="onTabClick('nearby')">
      <SFIcon name="location" :size="44" />
      <text class="tabbar-text">附近</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import SFIcon from '@/components/SFIcon/SFIcon.vue'

defineProps<{
  current: 'home' | 'nearby'
}>()

const emit = defineEmits<{
  add: []
}>()

const onTabClick = (tab: string) => {
  if (tab === 'home') {
    uni.redirectTo({ url: '/pages/index/index' })
  } else if (tab === 'nearby') {
    uni.redirectTo({ url: '/pages/nearby/index' })
  }
}
</script>

<style lang="scss" scoped>
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 110rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding-bottom: env(safe-area-inset-bottom);
  border-top: 1rpx solid rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.tabbar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 12rpx 48rpx;
  color: #86868B;

  &.active {
    color: #007AFF;

    .tabbar-text {
      color: #007AFF;
    }
  }
}

.tabbar-text {
  font-size: 22rpx;
  color: #86868B;
  font-weight: 500;
}

.tabbar-center {
  margin-top: -44rpx;
}

.add-btn {
  width: 96rpx;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: 50%;
  box-shadow: 0 8rpx 32rpx rgba(255, 107, 107, 0.4);
}
</style>
