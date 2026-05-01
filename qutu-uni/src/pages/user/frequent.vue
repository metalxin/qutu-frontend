<template>
  <view class="frequent-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">常用地点</text>
      <view class="nav-right" @click="showAddPopup = true">
        <text class="nav-action">新增</text>
      </view>
    </view>

    <scroll-view class="location-list" scroll-y :show-scrollbar="false">
      <view class="location-card" v-for="item in locations" :key="item.id">
        <view class="location-info">
          <view class="location-name">
            <text class="name-text">{{ item.name }}</text>
            <text class="type-tag" v-if="item.type">{{ typeLabel(item.type) }}</text>
          </view>
          <text class="address-text">{{ item.address }}</text>
        </view>
        <view class="location-actions">
          <view class="delete-btn" @click="removeLocation(item.id)">删除</view>
        </view>
      </view>
      <view class="empty" v-if="!locations.length">
        <text class="empty-text">还没有常用地点</text>
      </view>
    </scroll-view>

    <view class="popup-mask" v-if="showAddPopup" @click="showAddPopup = false">
      <view class="popup-card" @click.stop>
        <text class="popup-title">新增常用地点</text>
        <input class="popup-input" v-model="form.name" placeholder="地点名称" />
        <input class="popup-input" v-model="form.address" placeholder="详细地址" />
        <view class="popup-picker" @click="showTypePicker = true">
          <text class="picker-text">{{ typeLabel(form.type) }}</text>
          <text class="picker-arrow">›</text>
        </view>
        <view class="popup-actions">
          <view class="popup-btn ghost" @click="showAddPopup = false">取消</view>
          <view class="popup-btn" @click="addLocation">保存</view>
        </view>
      </view>
    </view>

    <view class="popup-mask" v-if="showTypePicker" @click="showTypePicker = false">
      <view class="picker-card" @click.stop>
        <view class="picker-item" v-for="option in typeOptions" :key="option.value" @click="selectType(option.value)">
          <text class="picker-item-text">{{ option.label }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getFrequentLocations, addFrequentLocation, deleteFrequentLocation } from '@/api/modules/user'
import type { Location } from '@/api/modules/user'

const statusBarHeight = ref(0)
const locations = ref<Location[]>([])
const showAddPopup = ref(false)
const showTypePicker = ref(false)

const form = ref({
  name: '',
  address: '',
  type: 'home'
})

const typeOptions = [
  { label: '家', value: 'home' },
  { label: '公司', value: 'work' },
  { label: '常去', value: 'other' }
]

const typeLabel = (type?: string) => {
  const match = typeOptions.find(option => option.value === type)
  return match?.label || '常去'
}

const loadLocations = async () => {
  const res = await getFrequentLocations()
  locations.value = res
}

const addLocation = async () => {
  if (!form.value.name || !form.value.address) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  const newItem = await addFrequentLocation({
    name: form.value.name,
    address: form.value.address,
    latitude: 0,
    longitude: 0,
    type: form.value.type
  })
  locations.value.unshift(newItem)
  showAddPopup.value = false
  form.value = { name: '', address: '', type: 'home' }
  uni.showToast({ title: '已添加', icon: 'success' })
}

const removeLocation = async (id: number) => {
  await deleteFrequentLocation(id)
  locations.value = locations.value.filter(item => item.id !== id)
  uni.showToast({ title: '已删除', icon: 'none' })
}

const selectType = (type: string) => {
  form.value.type = type
  showTypePicker.value = false
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  loadLocations()
})
</script>

<style lang="scss" scoped>
.frequent-page {
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

.nav-action {
  font-size: 26rpx;
  color: #007AFF;
}

.location-list {
  padding: 24rpx;
}

.location-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.location-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
}

.location-name {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.name-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.type-tag {
  font-size: 20rpx;
  color: #007AFF;
  background: #E8F5FF;
  padding: 2rpx 10rpx;
  border-radius: 12rpx;
}

.address-text {
  font-size: 24rpx;
  color: #86868B;
}

.location-actions {
  display: flex;
  align-items: center;
}

.delete-btn {
  font-size: 22rpx;
  color: #FF3B30;
  padding: 6rpx 12rpx;
}

.empty {
  padding: 160rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 26rpx;
  color: #86868B;
}

.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.popup-card {
  width: 80%;
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.popup-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.popup-input {
  padding: 16rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  font-size: 24rpx;
}

.popup-picker {
  padding: 16rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.picker-text {
  font-size: 24rpx;
  color: #1D1D1F;
}

.picker-arrow {
  font-size: 28rpx;
  color: #C7C7CC;
}

.popup-actions {
  display: flex;
  gap: 12rpx;
}

.popup-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 16rpx;
  background: #1D1D1F;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
}

.popup-btn.ghost {
  background: #F5F5F7;
  color: #1D1D1F;
}

.picker-card {
  width: 70%;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.picker-item {
  padding: 16rpx;
  border-radius: 12rpx;
  background: #F5F5F7;
}

.picker-item-text {
  font-size: 24rpx;
  color: #1D1D1F;
}
</style>
