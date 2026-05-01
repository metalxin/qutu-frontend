<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <text class="nav-title">旅行清单</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-card">
      <view class="stats-row">
        <text class="stats-label">已定制清单：</text>
        <text class="stats-value">{{ checklistData.length }}</text>
      </view>
      <view class="stats-row">
        <text class="stats-label">已完成清单：</text>
        <text class="stats-value">{{ completedCount }}</text>
      </view>
    </view>

    <!-- 记录清单标题 -->
    <view class="section-title">
      <text class="title-text">记录清单</text>
    </view>

    <!-- 清单网格 -->
    <view class="checklist-grid">
      <!-- 添加清单卡片 -->
      <view class="checklist-card add-card" @click="showAddPopup = true">
        <view class="add-icon-wrapper">
          <text class="add-icon">+</text>
        </view>
        <text class="add-text">添加记录清单</text>
      </view>

      <!-- 已有清单 -->
      <view 
        class="checklist-card" 
        v-for="item in checklistData" 
        :key="item.id"
        @click="goToDetail(item)"
      >
        <view class="card-content">
          <text class="card-title">{{ item.title }}</text>
          <view class="card-dates">
            <text class="date-row">始：{{ (item.departDate || '').replace(/-/g, '/') }}</text>
            <text class="date-row">末：{{ (item.returnDate || '').replace(/-/g, '/') }}</text>
          </view>
          <view class="card-progress" v-if="item.itemsCount > 0">
            <text class="progress-text">{{ item.checkedCount }}/{{ item.itemsCount }}</text>
          </view>
        </view>
        <view class="card-edit" @click.stop="editChecklist(item)">
          <text class="edit-icon">✏️</text>
        </view>
        <view class="card-delete" @click.stop="confirmDelete(item)">
          <text class="delete-icon">×</text>
        </view>
      </view>
    </view>

    <!-- 添加清单弹窗 -->
    <view class="popup-mask" v-if="showAddPopup" @click="showAddPopup = false"></view>
    <view class="add-popup" :class="{ 'popup-show': showAddPopup }">
      <!-- 返回按钮 -->
      <view class="popup-back" @click="showAddPopup = false">
        <text class="popup-back-icon">‹</text>
      </view>

      <!-- 顶部图片 -->
      <view class="popup-banner">
        <image class="banner-image" src="https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800&q=80" mode="aspectFill" />
      </view>

      <!-- 表单 -->
      <view class="popup-form">
        <view class="form-group">
          <text class="form-label">旅游名称</text>
          <view class="form-input-wrapper">
            <input 
              class="form-input" 
              v-model="formData.name"
              placeholder="为旅行起一个名字吧"
              :maxlength="12"
            />
            <text class="input-count">{{ formData.name.length }}/12</text>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">开始时间</text>
          <picker mode="date" :value="formData.startDate" @change="onStartDateChange">
            <view class="form-input-wrapper picker">
              <text class="picker-text" :class="{ placeholder: !formData.startDate }">
                {{ formData.startDate || '请选择旅行开始时间' }}
              </text>
              <text class="picker-icon">📅</text>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">结束时间</text>
          <picker mode="date" :value="formData.endDate" @change="onEndDateChange">
            <view class="form-input-wrapper picker">
              <text class="picker-text" :class="{ placeholder: !formData.endDate }">
                {{ formData.endDate || '请选择旅行结束时间' }}
              </text>
              <text class="picker-icon">📅</text>
            </view>
          </picker>
        </view>

        <view class="form-submit" @click="submitChecklist">
          <text class="submit-text">确定</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getChecklists, createChecklist, updateChecklist, deleteChecklist } from '@/api'
import type { Checklist } from '@/api/modules/checklist'

// 清单数据
const checklistData = ref<Checklist[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 计算已完成清单数
const completedCount = computed(() => {
  return checklistData.value.filter(item => item.status === 2).length
})

// 弹窗控制
const showAddPopup = ref(false)
const editingId = ref<string | number | null>(null)

// 表单数据
const formData = ref({
  name: '',
  startDate: '',
  endDate: ''
})

const getErrorMessage = (error: any, fallback = '操作失败') => {
  return error?.response?.msg || error?.response?.message || error?.message || fallback
}

// 加载清单数据
const loadChecklists = async () => {
  loading.value = true
  try {
    const res = await getChecklists({ current: currentPage.value, size: pageSize.value })
    if (res && res.records) {
      checklistData.value = res.records
      total.value = res.total
    } else {
      checklistData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载清单失败:', error)
    checklistData.value = []
    total.value = 0
    uni.showToast({ title: getErrorMessage(error, '加载清单失败'), icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 日期选择
const onStartDateChange = (e: any) => {
  formData.value.startDate = e.detail.value.replace(/-/g, '/')
}

const onEndDateChange = (e: any) => {
  formData.value.endDate = e.detail.value.replace(/-/g, '/')
}

// 提交清单
const submitChecklist = async () => {
  if (!formData.value.name) {
    uni.showToast({ title: '请输入旅游名称', icon: 'none' })
    return
  }
  if (!formData.value.startDate) {
    uni.showToast({ title: '请选择开始时间', icon: 'none' })
    return
  }
  if (!formData.value.endDate) {
    uni.showToast({ title: '请选择结束时间', icon: 'none' })
    return
  }

  try {
    const submitData = {
      title: formData.value.name,
      departDate: formData.value.startDate.replace(/\//g, '-'),
      returnDate: formData.value.endDate.replace(/\//g, '-')
    }

    if (editingId.value) {
      // 编辑现有清单
      await updateChecklist(editingId.value, submitData)
      uni.showToast({ title: '更新成功', icon: 'success' })
    } else {
      // 创建新清单
      await createChecklist(submitData)
      uni.showToast({ title: '创建成功', icon: 'success' })
    }
    // 重新加载列表
    loadChecklists()
  } catch (error) {
    uni.showToast({ title: getErrorMessage(error, editingId.value ? '更新失败' : '创建失败'), icon: 'none' })
  }

  // 重置表单
  formData.value = { name: '', startDate: '', endDate: '' }
  editingId.value = null
  showAddPopup.value = false
}

// 跳转详情
const goToDetail = (item: Checklist) => {
  uni.navigateTo({
    url: `/pages/checklist/detail?id=${encodeURIComponent(String(item.id))}`
  })
}

// 编辑清单
const editChecklist = (item: Checklist) => {
  editingId.value = item.id
  formData.value = {
    name: item.title,
    startDate: (item.departDate || '').replace(/-/g, '/'),
    endDate: (item.returnDate || '').replace(/-/g, '/')
  }
  showAddPopup.value = true
}

// 删除清单
const confirmDelete = (item: Checklist) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除清单"${item.title}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteChecklist(item.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          loadChecklists()
        } catch (error) {
          uni.showToast({ title: getErrorMessage(error, '删除失败'), icon: 'none' })
          loadChecklists()
        }
      }
    }
  })
}

// 页面加载
onMounted(() => {
  loadChecklists()
})
</script>

<style lang="scss" scoped>
$primary-color: #00B8A9;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius-lg: 24rpx;
$border-radius-md: 16rpx;

.page {
  min-height: 100vh;
  background: $bg-color;
}

// 导航栏
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 24rpx);
  background: $card-bg;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 60rpx;
}

// 统计卡片
.stats-card {
  margin: 24rpx 32rpx;
  padding: 32rpx;
  background: $card-bg;
  border-radius: $border-radius-lg;
}

.stats-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.stats-label {
  font-size: 30rpx;
  color: $text-primary;
}

.stats-value {
  font-size: 30rpx;
  color: $primary-color;
  font-weight: 600;
}

// 区块标题
.section-title {
  padding: 0 32rpx 20rpx;
}

.title-text {
  font-size: 28rpx;
  color: $text-secondary;
}

// 清单网格
.checklist-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  padding: 0 32rpx 48rpx;
}

.checklist-card {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
  min-height: 280rpx;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &.add-card {
    justify-content: center;
    align-items: center;
    border: 2rpx dashed #E5E5EA;
    background: transparent;
  }
}

.add-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #E5E5EA;
  border-radius: $border-radius-md;
  margin-bottom: 16rpx;
}

.add-icon {
  font-size: 40rpx;
  color: $text-secondary;
}

.add-text {
  font-size: 26rpx;
  color: $text-secondary;
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 16rpx;
  display: block;
}

.card-dates {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.date-row {
  font-size: 24rpx;
  color: $text-secondary;
}

.card-progress {
  margin-top: 12rpx;
}

.progress-text {
  font-size: 22rpx;
  color: $primary-color;
  font-weight: 500;
}

.card-edit {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-delete {
  position: absolute;
  right: 24rpx;
  top: 24rpx;
  width: 44rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.06);
}

.delete-icon {
  font-size: 28rpx;
  color: #FF3B30;
}

.edit-icon {
  font-size: 28rpx;
}

// 弹窗遮罩
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

// 添加清单弹窗
.add-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 90vh;
  background: $bg-color;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  overflow: hidden;

  &.popup-show {
    transform: translateY(0);
  }
}

.popup-back {
  position: absolute;
  top: 24rpx;
  left: 24rpx;
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  z-index: 10;
}

.popup-back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

.popup-banner {
  width: 100%;
  height: 400rpx;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.popup-form {
  padding: 40rpx;
}

.form-group {
  margin-bottom: 40rpx;
}

.form-label {
  font-size: 30rpx;
  font-weight: 500;
  color: $text-primary;
  margin-bottom: 16rpx;
  display: block;
}

.form-input-wrapper {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  background: $card-bg;
  border-radius: $border-radius-md;

  &.picker {
    justify-content: space-between;
  }
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.input-count {
  font-size: 26rpx;
  color: $text-secondary;
  margin-left: 16rpx;
}

.picker-text {
  font-size: 28rpx;
  color: $text-primary;

  &.placeholder {
    color: $text-secondary;
  }
}

.picker-icon {
  font-size: 32rpx;
}

.form-submit {
  margin-top: 60rpx;
  padding: 32rpx;
  background: $primary-color;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.submit-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}
</style>
