<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <text class="nav-title">写攻略</text>
      <view class="nav-action" @click="submitGuide">
        <text class="nav-action-text" :class="{ disabled: submitting }">{{ submitting ? '发布中...' : '发布' }}</text>
      </view>
    </view>

    <scroll-view class="form-scroll" scroll-y :show-scrollbar="false">
      <!-- 封面图 -->
      <view class="form-section">
        <view class="cover-upload" @click="chooseCover">
          <image v-if="form.coverUrl" class="cover-preview" :src="form.coverUrl" mode="aspectFill" />
          <view v-else class="cover-placeholder">
            <SFIcon name="image" :size="48" color="#86868B" />
            <text class="cover-hint">添加封面图</text>
          </view>
        </view>
      </view>

      <!-- 基本信息 -->
      <view class="form-section">
        <view class="form-group">
          <text class="form-label">标题</text>
          <input class="form-input" v-model="form.title" placeholder="给你的攻略起个标题" maxlength="50" />
        </view>

        <view class="form-group">
          <text class="form-label">摘要</text>
          <textarea class="form-textarea" v-model="form.summary" placeholder="简单描述一下这篇攻略..." maxlength="200" :auto-height="true" />
        </view>

        <view class="form-row">
          <view class="form-group half">
            <text class="form-label">天数</text>
            <input class="form-input" v-model.number="form.days" placeholder="1" type="number" />
          </view>
          <view class="form-group half">
            <text class="form-label">晚数</text>
            <input class="form-input" v-model.number="form.nights" placeholder="0" type="number" />
          </view>
        </view>

        <view class="form-row">
          <view class="form-group half">
            <text class="form-label">城市</text>
            <input class="form-input" v-model="form.cityName" placeholder="如：杭州" />
          </view>
          <view class="form-group half">
            <text class="form-label">标签</text>
            <input class="form-input" v-model="form.tag" placeholder="如：Citywalk" />
          </view>
        </view>
      </view>

      <!-- 行程安排 -->
      <view class="form-section">
        <view class="section-header">
          <text class="section-title">行程安排</text>
          <view class="add-day-btn" @click="addDay">
            <text class="add-icon">+</text>
            <text class="add-text">添加一天</text>
          </view>
        </view>

        <view class="day-list">
          <view class="day-card" v-for="(day, dayIdx) in form.itinerary" :key="dayIdx">
            <view class="day-header">
              <text class="day-label">Day {{ dayIdx + 1 }}</text>
              <view class="day-actions">
                <view class="day-action" @click="removeDay(dayIdx)" v-if="form.itinerary.length > 1">
                  <text class="action-text delete">删除</text>
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label small">当天标题</text>
              <input class="form-input" v-model="day.title" placeholder="如：西湖漫步" />
            </view>

            <view class="form-group">
              <text class="form-label small">当天摘要</text>
              <textarea class="form-textarea small" v-model="day.summary" placeholder="简述当天行程..." maxlength="200" :auto-height="true" />
            </view>

            <!-- 条目列表 -->
            <view class="item-list">
              <view class="item-card" v-for="(item, itemIdx) in day.items" :key="itemIdx">
                <view class="item-header">
                  <view class="item-type-tabs">
                    <view class="type-tab" :class="{ active: item.type === 'spot' }" @click="item.type = 'spot'">
                      <text class="type-text">景点</text>
                    </view>
                    <view class="type-tab" :class="{ active: item.type === 'text' }" @click="item.type = 'text'">
                      <text class="type-text">文字</text>
                    </view>
                  </view>
                  <view class="item-remove" @click="removeItem(dayIdx, itemIdx)">
                    <text class="remove-icon">×</text>
                  </view>
                </view>

                <input class="form-input" v-model="item.title" placeholder="条目标题" />
                <textarea v-if="item.type === 'text'" class="form-textarea small" v-model="item.content" placeholder="详细内容..." :auto-height="true" />
              </view>
            </view>

            <view class="add-item-btn" @click="addItem(dayIdx)">
              <text class="add-icon">+</text>
              <text class="add-text">添加条目</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部安全区 -->
      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { createUserGuide, type GuideDayCreateDTO, type GuideDayItemCreateDTO } from '@/api'

const statusBarHeight = ref(44)
const submitting = ref(false)

const form = reactive({
  title: '',
  coverUrl: '',
  summary: '',
  days: 1,
  nights: 0,
  tag: '',
  cityId: undefined as number | undefined,
  cityName: '',
  itinerary: [
    {
      dayIndex: 1,
      title: '',
      summary: '',
      items: [] as GuideDayItemCreateDTO[]
    }
  ] as GuideDayCreateDTO[]
})

onMounted(() => {
  try {
    const sysInfo = uni.getSystemInfoSync()
    statusBarHeight.value = sysInfo.statusBarHeight || 44
  } catch {}
})

const goBack = () => {
  uni.navigateBack()
}

const chooseCover = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0]
      uploadCover(tempFilePath)
    }
  })
}

const uploadCover = (filePath: string) => {
  const token = uni.getStorageSync('token')
  uni.uploadFile({
    url: `${import.meta.env.VITE_WX_API_BASE_URL || ''}/admin/file/upload`,
    filePath,
    name: 'file',
    header: {
      'Authorization': token ? `Bearer ${token}` : ''
    },
    success: (res) => {
      try {
        const data = JSON.parse(res.data)
        if (data.code === 0 && data.data) {
          form.coverUrl = data.data
        } else {
          uni.showToast({ title: '上传失败', icon: 'none' })
        }
      } catch {
        uni.showToast({ title: '上传失败', icon: 'none' })
      }
    },
    fail: () => {
      uni.showToast({ title: '上传失败', icon: 'none' })
    }
  })
}

const addDay = () => {
  form.itinerary.push({
    dayIndex: form.itinerary.length + 1,
    title: '',
    summary: '',
    items: []
  })
}

const removeDay = (idx: number) => {
  form.itinerary.splice(idx, 1)
  form.itinerary.forEach((d, i) => {
    d.dayIndex = i + 1
  })
}

const addItem = (dayIdx: number) => {
  if (!form.itinerary[dayIdx].items) {
    form.itinerary[dayIdx].items = []
  }
  form.itinerary[dayIdx].items!.push({
    type: 'text',
    title: '',
    content: '',
    orderNum: form.itinerary[dayIdx].items!.length + 1
  })
}

const removeItem = (dayIdx: number, itemIdx: number) => {
  form.itinerary[dayIdx].items!.splice(itemIdx, 1)
  form.itinerary[dayIdx].items!.forEach((item, i) => {
    item.orderNum = i + 1
  })
}

const submitGuide = async () => {
  if (!form.title.trim()) {
    uni.showToast({ title: '请输入标题', icon: 'none' })
    return
  }
  if (submitting.value) return
  submitting.value = true

  try {
    const dto = {
      title: form.title,
      coverUrl: form.coverUrl || undefined,
      summary: form.summary || undefined,
      days: form.days || 1,
      nights: form.nights || 0,
      tag: form.tag || undefined,
      cityId: form.cityId,
      cityName: form.cityName || undefined,
      itinerary: form.itinerary.map((day, idx) => ({
        dayIndex: idx + 1,
        title: day.title || undefined,
        summary: day.summary || undefined,
        items: (day.items || []).map((item, itemIdx) => ({
          type: item.type || 'text',
          title: item.title || '',
          content: item.content || '',
          spotId: item.spotId,
          orderNum: itemIdx + 1
        }))
      }))
    }

    const guideId = await createUserGuide(dto)
    uni.showToast({ title: '发布成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({
        url: `/packageGuide/pages/guide/detail?id=${guideId}`
      })
    }, 1000)
  } catch (error: any) {
    uni.showToast({ title: error.message || '发布失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #00C853;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius: 16rpx;

.page {
  min-height: 100vh;
  background: $bg-color;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32rpx 24rpx;
  background: $card-bg;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-action {
  padding: 8rpx 24rpx;
}

.nav-action-text {
  font-size: 30rpx;
  font-weight: 600;
  color: $primary-color;

  &.disabled {
    color: $text-secondary;
  }
}

.form-scroll {
  height: 100vh;
  padding-top: 88rpx;
  box-sizing: border-box;
}

.form-section {
  background: $card-bg;
  margin-bottom: 20rpx;
  padding: 32rpx;
}

.cover-upload {
  width: 100%;
  height: 360rpx;
  border-radius: $border-radius;
  overflow: hidden;
  background: #F2F2F7;
}

.cover-preview {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
}

.cover-hint {
  font-size: 28rpx;
  color: $text-secondary;
}

.form-group {
  margin-bottom: 24rpx;

  &.half {
    flex: 1;
  }
}

.form-row {
  display: flex;
  gap: 24rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: $text-primary;
  margin-bottom: 12rpx;

  &.small {
    font-size: 26rpx;
    color: $text-secondary;
  }
}

.form-input {
  width: 100%;
  padding: 20rpx 24rpx;
  background: #F2F2F7;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: $text-primary;
  box-sizing: border-box;
}

.form-textarea {
  width: 100%;
  padding: 20rpx 24rpx;
  background: #F2F2F7;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: $text-primary;
  min-height: 120rpx;
  box-sizing: border-box;

  &.small {
    min-height: 80rpx;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.add-day-btn, .add-item-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: rgba(0, 200, 83, 0.1);
  border-radius: 100rpx;
}

.add-icon {
  font-size: 32rpx;
  color: $primary-color;
  font-weight: 600;
}

.add-text {
  font-size: 26rpx;
  color: $primary-color;
}

.day-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.day-card {
  background: #F2F2F7;
  border-radius: $border-radius;
  padding: 24rpx;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.day-label {
  font-size: 30rpx;
  font-weight: 600;
  color: $primary-color;
}

.day-actions {
  display: flex;
  gap: 16rpx;
}

.action-text {
  font-size: 26rpx;

  &.delete {
    color: #FF3B30;
  }
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.item-card {
  background: $card-bg;
  border-radius: 12rpx;
  padding: 20rpx;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.item-type-tabs {
  display: flex;
  gap: 8rpx;
}

.type-tab {
  padding: 6rpx 20rpx;
  border-radius: 100rpx;
  background: #F2F2F7;

  &.active {
    background: rgba(0, 200, 83, 0.15);
  }
}

.type-text {
  font-size: 24rpx;
  color: $text-secondary;

  .type-tab.active & {
    color: $primary-color;
    font-weight: 500;
  }
}

.item-remove {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-icon {
  font-size: 36rpx;
  color: #FF3B30;
}

.bottom-safe {
  height: 120rpx;
}
</style>
