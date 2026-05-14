<template>
  <view class="frequent-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">常用地点</text>
      <view class="nav-right" @click="openAddPopup">
        <text class="nav-action">新增</text>
      </view>
    </view>

    <scroll-view class="location-list" scroll-y :show-scrollbar="false" @refresherrefresh="onRefresh" :refresher-triggered="refreshing" refresher-enabled>
      <view class="location-card" v-for="item in locations" :key="item.id" @click="openEditPopup(item)">
        <view class="location-icon-wrap" :class="item.type || 'other'">
          <text class="location-icon-text">{{ typeIcon(item.type) }}</text>
        </view>
        <view class="location-info">
          <view class="location-name">
            <text class="name-text">{{ item.name }}</text>
            <text class="type-tag" :class="item.type || 'other'">{{ typeLabel(item.type) }}</text>
          </view>
          <text class="address-text">{{ item.address || '未设置地址' }}</text>
          <text class="coord-text" v-if="item.latitude && item.longitude">
            {{ item.latitude.toFixed(4) }}, {{ item.longitude.toFixed(4) }}
          </text>
        </view>
        <view class="location-actions">
          <view class="nav-btn" @click.stop="navigateTo(item)" v-if="item.latitude && item.longitude">
            <text class="nav-btn-text">导航</text>
          </view>
          <view class="delete-btn" @click.stop="confirmDelete(item)">删除</view>
        </view>
      </view>
      <view class="empty" v-if="!loading && !locations.length">
        <text class="empty-icon-text">📍</text>
        <text class="empty-text">还没有常用地点</text>
        <text class="empty-desc">添加家、公司等常去地点，出行更便捷</text>
        <view class="empty-action" @click="openAddPopup">
          <text class="empty-action-text">添加地点</text>
        </view>
      </view>
      <view class="bottom-space"></view>
    </scroll-view>

    <!-- 新增/编辑弹窗 -->
    <view class="popup-mask" v-if="showFormPopup" @click="showFormPopup = false">
      <view class="popup-card" @click.stop>
        <text class="popup-title">{{ editingId ? '编辑地点' : '新增常用地点' }}</text>

        <view class="form-group">
          <text class="form-label">地点名称 *</text>
          <input class="form-input" v-model="form.name" placeholder="如：家、公司" />
        </view>

        <view class="form-group">
          <text class="form-label">地点类型</text>
          <view class="type-selector">
            <view
              class="type-option"
              v-for="option in typeOptions"
              :key="option.value"
              :class="{ active: form.type === option.value }"
              @click="form.type = option.value"
            >
              <text class="type-option-icon">{{ option.icon }}</text>
              <text class="type-option-label">{{ option.label }}</text>
            </view>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">地图选点</text>
          <view class="map-picker" @click="chooseLocation">
            <map
              v-if="form.latitude && form.longitude"
              class="mini-map"
              :latitude="form.latitude"
              :longitude="form.longitude"
              :markers="formMarkers"
              :scale="15"
              :show-location="false"
            />
            <view v-else class="map-placeholder">
              <text class="map-placeholder-icon">📍</text>
              <text class="map-placeholder-text">点击选择位置</text>
            </view>
            <view class="map-picker-overlay">
              <text class="map-picker-hint">点击选择位置</text>
            </view>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">详细地址</text>
          <input class="form-input" v-model="form.address" placeholder="详细地址（自动填充或手动输入）" />
        </view>

        <view class="popup-actions">
          <view class="popup-btn ghost" @click="showFormPopup = false">取消</view>
          <view class="popup-btn primary" @click="saveLocation">{{ editingId ? '保存' : '添加' }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getFrequentLocations, addFrequentLocation, deleteFrequentLocation } from '@/api/modules/user'
import type { Location } from '@/api/modules/user'

const statusBarHeight = ref(0)
const locations = ref<Location[]>([])
const loading = ref(false)
const refreshing = ref(false)
const showFormPopup = ref(false)
const editingId = ref<number | null>(null)

const form = ref({
  name: '',
  address: '',
  latitude: 0,
  longitude: 0,
  type: 'home'
})

const typeOptions = [
  { label: '家', value: 'home', icon: '🏠' },
  { label: '公司', value: 'work', icon: '🏢' },
  { label: '常去', value: 'other', icon: '📍' }
]

const typeLabel = (type?: string) => {
  const match = typeOptions.find(option => option.value === type)
  return match?.label || '常去'
}

const typeIcon = (type?: string) => {
  const match = typeOptions.find(option => option.value === type)
  return match?.icon || '📍'
}

const formMarkers = computed(() => {
  if (!form.value.latitude || !form.value.longitude) return []
  return [{
    id: 1,
    latitude: form.value.latitude,
    longitude: form.value.longitude,
    iconPath: '/static/marker-start.png',
    width: 30,
    height: 30,
    callout: { content: form.value.name || '选择的位置', display: 'ALWAYS' }
  }]
})

const loadLocations = async () => {
  loading.value = true
  try {
    const res = await getFrequentLocations()
    locations.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('加载常用地点失败:', error)
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    await loadLocations()
  } finally {
    refreshing.value = false
  }
}

const openAddPopup = () => {
  editingId.value = null
  form.value = { name: '', address: '', latitude: 0, longitude: 0, type: 'home' }
  showFormPopup.value = true
}

const openEditPopup = (item: Location) => {
  editingId.value = item.id
  form.value = {
    name: item.name,
    address: item.address,
    latitude: item.latitude || 0,
    longitude: item.longitude || 0,
    type: item.type || 'other'
  }
  showFormPopup.value = true
}

const chooseLocation = () => {
  uni.chooseLocation({
    latitude: form.value.latitude || undefined,
    longitude: form.value.longitude || undefined,
    success: (res: any) => {
      form.value.latitude = res.latitude
      form.value.longitude = res.longitude
      form.value.address = res.address || form.value.address
      if (!form.value.name && res.name) {
        form.value.name = res.name
      }
    },
    fail: (err: any) => {
      // 用户取消选择不提示
      if (err.errMsg && err.errMsg.includes('cancel')) return
      console.warn('选择位置失败:', err)
      // 降级：使用当前定位
      uni.getLocation({
        type: 'gcj02',
        success: (locRes: any) => {
          form.value.latitude = locRes.latitude
          form.value.longitude = locRes.longitude
          uni.showToast({ title: '已使用当前位置', icon: 'none' })
        }
      })
    }
  })
}

const saveLocation = async () => {
  if (!form.value.name) {
    uni.showToast({ title: '请填写地点名称', icon: 'none' })
    return
  }
  try {
    if (editingId.value) {
      // 编辑模式：先删再增（简化实现，后端支持PUT后可优化）
      await deleteFrequentLocation(editingId.value)
      const newItem = await addFrequentLocation({
        name: form.value.name,
        address: form.value.address,
        latitude: form.value.latitude,
        longitude: form.value.longitude,
        type: form.value.type
      })
      // 替换列表中的旧项
      const idx = locations.value.findIndex(l => l.id === editingId.value)
      if (idx !== -1 && newItem) {
        locations.value.splice(idx, 1, newItem)
      } else {
        await loadLocations()
      }
    } else {
      const newItem = await addFrequentLocation({
        name: form.value.name,
        address: form.value.address,
        latitude: form.value.latitude,
        longitude: form.value.longitude,
        type: form.value.type
      })
      if (newItem) {
        locations.value.unshift(newItem)
      } else {
        await loadLocations()
      }
    }
    showFormPopup.value = false
    uni.showToast({ title: editingId.value ? '已保存' : '已添加', icon: 'success' })
  } catch (error) {
    console.error('保存地点失败:', error)
    uni.showToast({ title: '保存失败，请重试', icon: 'none' })
  }
}

const confirmDelete = (item: Location) => {
  uni.showModal({
    title: '删除地点',
    content: `确定删除「${item.name}」？`,
    confirmColor: '#FF3B30',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteFrequentLocation(item.id)
          locations.value = locations.value.filter(l => l.id !== item.id)
          uni.showToast({ title: '已删除', icon: 'none' })
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

const navigateTo = (item: Location) => {
  uni.openLocation({
    latitude: item.latitude,
    longitude: item.longitude,
    name: item.name,
    address: item.address || '',
    fail: () => {
      uni.showToast({ title: '打开导航失败', icon: 'none' })
    }
  })
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
  position: sticky;
  top: 0;
  z-index: 100;
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
  height: calc(100vh - 88rpx);
  padding: 24rpx;
}

.location-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.location-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.home {
    background: #FFF3E0;
  }
  &.work {
    background: #E3F2FD;
  }
  &.other {
    background: #F3E5F5;
  }
}

.location-icon-text {
  font-size: 36rpx;
}

.location-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  flex: 1;
  min-width: 0;
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.type-tag {
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 12rpx;
  flex-shrink: 0;

  &.home {
    color: #E65100;
    background: #FFF3E0;
  }
  &.work {
    color: #1565C0;
    background: #E3F2FD;
  }
  &.other {
    color: #7B1FA2;
    background: #F3E5F5;
  }
}

.address-text {
  font-size: 24rpx;
  color: #86868B;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coord-text {
  font-size: 20rpx;
  color: #C7C7CC;
  font-family: monospace;
}

.location-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12rpx;
  flex-shrink: 0;
}

.nav-btn {
  padding: 8rpx 16rpx;
  background: #007AFF;
  border-radius: 12rpx;
}

.nav-btn-text {
  font-size: 22rpx;
  color: #FFFFFF;
}

.delete-btn {
  font-size: 22rpx;
  color: #FF3B30;
  padding: 6rpx 12rpx;
}

.empty {
  padding: 160rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.empty-icon-text {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 8rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #86868B;
  margin-bottom: 40rpx;
}

.empty-action {
  padding: 20rpx 48rpx;
  background: #007AFF;
  border-radius: 40rpx;
}

.empty-action-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.bottom-space {
  height: 60rpx;
}

/* 弹窗样式 */
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
  width: 85%;
  max-height: 85vh;
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  overflow-y: auto;
}

.popup-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.form-label {
  font-size: 24rpx;
  color: #86868B;
}

.form-input {
  padding: 16rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #1D1D1F;
}

.type-selector {
  display: flex;
  gap: 12rpx;
}

.type-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 16rpx 8rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  border: 3rpx solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: #007AFF;
    background: #E3F2FD;
  }
}

.type-option-icon {
  font-size: 32rpx;
}

.type-option-label {
  font-size: 22rpx;
  color: #1D1D1F;
}

.map-picker {
  width: 100%;
  height: 300rpx;
  border-radius: 16rpx;
  overflow: hidden;
  position: relative;
}

.mini-map {
  width: 100%;
  height: 100%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  background: #F5F5F7;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.map-placeholder-icon {
  font-size: 48rpx;
}

.map-placeholder-text {
  font-size: 24rpx;
  color: #86868B;
}

.map-picker-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.4));
  text-align: center;
}

.map-picker-hint {
  font-size: 22rpx;
  color: #FFFFFF;
}

.popup-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 8rpx;
}

.popup-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 500;

  &.ghost {
    background: #F5F5F7;
    color: #1D1D1F;
  }

  &.primary {
    background: #007AFF;
    color: #FFFFFF;
  }
}
</style>
