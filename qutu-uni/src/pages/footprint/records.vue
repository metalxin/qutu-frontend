<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <view class="nav-title">
        <text class="title-text">我的点亮记录</text>
      </view>
      <view class="nav-placeholder"></view>
    </view>

    <scroll-view class="content" scroll-y :show-scrollbar="false">
      <!-- 有记录时显示列表 -->
      <view class="records-list" v-if="records.length > 0">
        <view 
          class="record-card" 
          v-for="record in records" 
          :key="record.id"
          @click="viewRecord(record)"
        >
          <view class="record-image">
            <image :src="record.image" mode="aspectFill" />
          </view>
          <view class="record-info">
            <text class="record-title">{{ record.title }}</text>
            <text class="record-date">{{ record.date }}</text>
            <text class="record-count">{{ record.count }}个地点</text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-else>
        <view class="empty-icon">
          <view class="frame-icon">
            <view class="frame-border">
              <view class="frame-inner">
                <view class="sun-icon"></view>
                <view class="mountain-icon"></view>
              </view>
            </view>
          </view>
        </view>
        <text class="empty-text">还没有点亮记录哦</text>
        <view class="empty-action" @click="goCreate">
          <text class="action-text">立即创建</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

interface Record {
  id: number
  title: string
  date: string
  count: number
  image: string
}

// 记录列表
const records = ref<Record[]>([])

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 查看记录
const viewRecord = (record: Record) => {
  uni.navigateTo({
    url: `/pages/footprint/detail?id=${record.id}`
  })
}

// 去创建
const goCreate = () => {
  uni.navigateTo({
    url: '/pages/footprint/map?type=spring'
  })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #FFFFFF;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 60rpx 32rpx 20rpx;
  background: #FFFFFF;
}

.nav-back, .nav-placeholder {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  flex: 1;
  text-align: center;
}

.title-text {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.content {
  height: 100vh;
  padding-top: 140rpx;
}

// 记录列表
.records-list {
  padding: 32rpx;
}

.record-card {
  display: flex;
  gap: 24rpx;
  padding: 24rpx;
  background: #F5F5F7;
  border-radius: 24rpx;
  margin-bottom: 20rpx;
}

.record-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 16rpx;
  overflow: hidden;
  
  image {
    width: 100%;
    height: 100%;
  }
}

.record-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.record-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 8rpx;
}

.record-date {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.record-count {
  font-size: 24rpx;
  color: #007AFF;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.empty-icon {
  margin-bottom: 40rpx;
}

.frame-icon {
  width: 200rpx;
  height: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.frame-border {
  width: 160rpx;
  height: 160rpx;
  border: 4rpx solid #1D1D1F;
  border-radius: 8rpx;
  padding: 16rpx;
  position: relative;
  
  &::before,
  &::after {
    content: '';
    position: absolute;
    width: 20rpx;
    height: 20rpx;
    border: 4rpx solid #1D1D1F;
  }
  
  &::before {
    top: -12rpx;
    left: -12rpx;
    border-right: none;
    border-bottom: none;
  }
  
  &::after {
    bottom: -12rpx;
    right: -12rpx;
    border-left: none;
    border-top: none;
  }
}

.frame-inner {
  width: 100%;
  height: 100%;
  border: 2rpx solid #1D1D1F;
  position: relative;
  overflow: hidden;
}

.sun-icon {
  position: absolute;
  top: 20rpx;
  right: 30rpx;
  width: 24rpx;
  height: 24rpx;
  border: 2rpx solid #1D1D1F;
  border-radius: 50%;
}

.mountain-icon {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 40rpx solid transparent;
  border-right: 40rpx solid transparent;
  border-bottom: 50rpx solid transparent;
  border-bottom-color: transparent;
  
  &::before {
    content: '';
    position: absolute;
    left: -30rpx;
    top: 10rpx;
    width: 0;
    height: 0;
    border-left: 30rpx solid transparent;
    border-right: 30rpx solid transparent;
    border-bottom: 40rpx solid #1D1D1F;
  }
  
  &::after {
    content: '';
    position: absolute;
    left: 0;
    top: 20rpx;
    width: 60rpx;
    height: 2rpx;
    background: #1D1D1F;
    transform: rotate(-30deg);
  }
}

.empty-text {
  font-size: 30rpx;
  color: #1D1D1F;
  margin-bottom: 32rpx;
}

.empty-action {
  padding: 20rpx 48rpx;
  background: #1D1D1F;
  border-radius: 40rpx;
}

.action-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}
</style>
