<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">旅行故事</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 日记卡片 -->
    <view class="content">
      <view class="diary-card">
        <view class="card-header">
          <view class="date-left">
            <text class="date-num">{{ currentDate.day }}</text>
            <text class="date-weekday">{{ currentDate.weekday }}</text>
          </view>
          <view class="date-right">
            <text class="date-full">{{ currentDate.full }}</text>
          </view>
        </view>
        <view class="card-body">
          <view class="welcome-text">
            <text class="text-line">旅行中总会有好看的风景</text>
            <text class="text-line">每个人都是生活的艺术家</text>
            <text class="text-line">在这里记录你的每一次旅行记忆</text>
          </view>
        </view>
        <view class="card-footer">
          <view class="add-diary-btn" @click="showAddDiary = true">
            <text class="btn-icon">+</text>
            <text class="btn-text">记录日记</text>
          </view>
        </view>
      </view>

      <!-- 日记列表 -->
      <view class="diary-list" v-if="diaryList.length > 0">
        <view 
          class="diary-item" 
          v-for="diary in diaryList" 
          :key="diary.id"
          @click="viewDiaryDetail(diary)"
        >
          <view class="item-header">
            <view class="item-date">
              <text class="item-day">{{ diary.day }}</text>
              <text class="item-weekday">{{ diary.weekday }}</text>
            </view>
            <view class="item-meta">
              <text class="item-mood" v-if="diary.moodIcon">{{ diary.moodIcon }}</text>
              <text class="item-weather" v-if="diary.weatherIcon">{{ diary.weatherIcon }}</text>
              <text class="item-full-date">{{ diary.date }}</text>
            </view>
          </view>
          <view class="item-content">
            <image v-if="diary.image" class="item-image" :src="diary.image" mode="aspectFill" />
            <text class="item-text">{{ diary.content }}</text>
            <text class="item-location" v-if="diary.location">📍 {{ diary.location }}</text>
          </view>
          <!-- 分享按钮 -->
          <view class="item-actions">
            <view class="action-btn" @click.stop="shareDiary(diary)">
              <text class="action-icon">↗</text>
              <text class="action-text">分享</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 添加日记弹窗 -->
    <view class="popup-mask" v-if="showAddDiary" @click="showAddDiary = false"></view>
    <view class="add-diary-popup" :class="{ 'popup-show': showAddDiary }">
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <view class="header-close" @click="showAddDiary = false">
          <text class="close-icon">×</text>
        </view>
        <text class="popup-title">记录日记</text>
        <view class="header-confirm" @click="saveDiary">
          <text class="confirm-icon">✓</text>
        </view>
      </view>

      <!-- 表单内容 -->
      <scroll-view class="popup-content" scroll-y>
        <!-- 模板快捷入口 -->
        <view class="template-section">
          <view class="section-title">快捷模板</view>
          <scroll-view scroll-x class="template-list">
            <view 
              class="template-item" 
              v-for="tpl in storyTemplates" 
              :key="tpl.id"
              @click="useTemplate(tpl)"
            >
              <text class="template-icon">{{ tpl.icon }}</text>
              <text class="template-name">{{ tpl.name }}</text>
            </view>
          </scroll-view>
        </view>

        <!-- 照片选择 -->
        <view class="photo-section">
          <view class="section-title">添加照片</view>
          <view class="photo-picker" @click="chooseImage">
            <image v-if="diaryForm.image" class="preview-image" :src="diaryForm.image" mode="aspectFill" />
            <view v-else class="picker-placeholder">
              <text class="picker-icon">📷</text>
              <text class="picker-text">点击添加照片</text>
            </view>
            <view class="remove-photo" v-if="diaryForm.image" @click.stop="diaryForm.image = ''">
              <text class="remove-icon">×</text>
            </view>
          </view>
        </view>

        <!-- 内容输入 -->
        <view class="input-section">
          <view class="section-title">日记内容</view>
          <textarea 
            class="content-input" 
            v-model="diaryForm.content"
            :placeholder="contentPlaceholder"
            :maxlength="500"
          />
          <text class="word-count">{{ diaryForm.content.length }}/500</text>
        </view>

        <!-- 位置选择 -->
        <view class="location-section">
          <view class="section-title">旅行位置</view>
          <view class="location-picker" @click="chooseLocation">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ diaryForm.location || '选择或输入位置' }}</text>
            <text class="location-arrow">›</text>
          </view>
        </view>

        <!-- 日期选择 -->
        <view class="date-section">
          <view class="section-title">记录日期</view>
          <picker mode="date" :value="pickerDate" @change="onDateChange">
            <view class="date-picker">
              <text class="date-icon">📅</text>
              <text class="date-text">{{ diaryForm.date }}</text>
              <text class="date-arrow">›</text>
            </view>
          </picker>
        </view>

        <!-- 心情选择 -->
        <view class="mood-section">
          <view class="section-title">今日心情</view>
          <view class="mood-list">
            <view 
              class="mood-item" 
              v-for="mood in moodOptions" 
              :key="mood.value"
              :class="{ active: diaryForm.mood === mood.value }"
              @click="selectMood(mood)"
            >
              <text class="mood-emoji">{{ mood.icon }}</text>
              <text class="mood-name">{{ mood.value }}</text>
            </view>
          </view>
        </view>

        <!-- 天气选择 -->
        <view class="weather-section">
          <view class="section-title">当日天气</view>
          <view class="weather-list">
            <view 
              class="weather-item" 
              v-for="weather in weatherOptions" 
              :key="weather.value"
              :class="{ active: diaryForm.weather === weather.value }"
              @click="selectWeather(weather)"
            >
              <text class="weather-emoji">{{ weather.icon }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 日期选择器弹窗 -->
    <view class="date-popup" v-if="showDatePopup">
      <view class="date-popup-mask" @click="showDatePopup = false"></view>
      <view class="date-popup-content">
        <picker-view class="date-picker-view" :value="datePickerValue" @change="onPickerChange">
          <picker-view-column>
            <view v-for="year in yearList" :key="year" class="picker-item">{{ year }}年</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="month in 12" :key="month" class="picker-item">{{ month }}月</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="day in 31" :key="day" class="picker-item">{{ day }}日</view>
          </picker-view-column>
        </picker-view>
        <view class="date-popup-btns">
          <view class="date-btn cancel" @click="showDatePopup = false">取消</view>
          <view class="date-btn confirm" @click="confirmDate">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { getDiaryList, createDiary, getStoryTemplates, uploadDiaryImage } from '@/api'

// 状态栏高度
const statusBarHeight = ref(44)

interface Diary {
  id: number
  day: string
  weekday: string
  date: string
  image: string
  content: string
  location: string
  mood?: string
  moodIcon?: string
  weather?: string
  weatherIcon?: string
}

interface StoryTemplate {
  id: number
  name: string
  icon: string
  desc: string
}

// 当前日期
const currentDate = ref({
  day: '01/30',
  weekday: '周五',
  full: '2026-01-30'
})

const loading = ref(false)

// 日记列表
const diaryList = ref<Diary[]>([])

// 故事模板
const storyTemplates = ref<StoryTemplate[]>([
  { id: 1, name: '旅途日记', icon: '📝', desc: '记录每一天的旅行点滴' },
  { id: 2, name: '美食记录', icon: '🍜', desc: '发现旅途中的美味' },
  { id: 3, name: '风景随拍', icon: '📷', desc: '用镜头捕捉美丽瞬间' },
  { id: 4, name: '心情感悟', icon: '💭', desc: '旅行中的所思所想' }
])

// 心情选项
const moodOptions = [
  { icon: '😊', value: '开心' },
  { icon: '😄', value: '愉快' },
  { icon: '🥰', value: '幸福' },
  { icon: '😌', value: '平静' },
  { icon: '🤔', value: '思考' },
  { icon: '😢', value: '感动' }
]

// 天气选项
const weatherOptions = [
  { icon: '☀️', value: '晴' },
  { icon: '⛅', value: '多云' },
  { icon: '☁️', value: '阴' },
  { icon: '🌧️', value: '雨' },
  { icon: '❄️', value: '雪' },
  { icon: '🌪️', value: '大风' }
]

// 内容占位符
const contentPlaceholder = ref('记录旅行中的美好瞬间...')

// 添加日记弹窗
const showAddDiary = ref(false)
const showDatePopup = ref(false)

// 日期选择器
const pickerDate = computed(() => {
  return diaryForm.date.replace(/\//g, '-')
})

const yearList = Array.from({ length: 10 }, (_, i) => 2020 + i)
const datePickerValue = ref([6, 0, 0]) // 默认2026年1月1日

// 日记表单
const diaryForm = reactive({
  image: '',
  content: '',
  location: '',
  date: '2026/01/30',
  mood: '',
  moodIcon: '',
  weather: '',
  weatherIcon: ''
})

// 使用模板
const useTemplate = (tpl: StoryTemplate) => {
  switch (tpl.id) {
    case 1: // 旅途日记
      contentPlaceholder.value = '今天的旅途中，我看到了...'
      break
    case 2: // 美食记录
      contentPlaceholder.value = '今天品尝了...味道真是太棒了！'
      break
    case 3: // 风景随拍
      contentPlaceholder.value = '这里的风景真美，让我想起了...'
      break
    case 4: // 心情感悟
      contentPlaceholder.value = '旅行让我明白了...'
      break
  }
  uni.showToast({ title: `已选择${tpl.name}模板`, icon: 'none' })
}

// 选择位置
const chooseLocation = () => {
  uni.chooseLocation({
    success: (res) => {
      diaryForm.location = res.name || res.address
    },
    fail: () => {
      // 位置选择失败时，显示输入框
      uni.showModal({
        title: '输入位置',
        editable: true,
        placeholderText: '请输入旅行位置',
        success: (res) => {
          if (res.confirm && res.content) {
            diaryForm.location = res.content
          }
        }
      })
    }
  })
}

// 日期变更
const onDateChange = (e: any) => {
  const date = e.detail.value
  diaryForm.date = date.replace(/-/g, '/')
}

// 选择心情
const selectMood = (mood: { icon: string; value: string }) => {
  if (diaryForm.mood === mood.value) {
    diaryForm.mood = ''
    diaryForm.moodIcon = ''
  } else {
    diaryForm.mood = mood.value
    diaryForm.moodIcon = mood.icon
  }
}

// 选择天气
const selectWeather = (weather: { icon: string; value: string }) => {
  if (diaryForm.weather === weather.value) {
    diaryForm.weather = ''
    diaryForm.weatherIcon = ''
  } else {
    diaryForm.weather = weather.value
    diaryForm.weatherIcon = weather.icon
  }
}

// picker-view变化
const onPickerChange = (e: any) => {
  datePickerValue.value = e.detail.value
}

// 确认日期
const confirmDate = () => {
  const year = yearList[datePickerValue.value[0]]
  const month = String(datePickerValue.value[1] + 1).padStart(2, '0')
  const day = String(datePickerValue.value[2] + 1).padStart(2, '0')
  diaryForm.date = `${year}/${month}/${day}`
  showDatePopup.value = false
}

// 加载日记列表
const loadDiaryList = async () => {
  loading.value = true
  try {
    const res = await getDiaryList()
    if (res && res.length > 0) {
      diaryList.value = res
    }
  } catch (error) {
    console.error('加载日记列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载模板
const loadTemplates = async () => {
  try {
    const res = await getStoryTemplates()
    if (res && res.length > 0) {
      storyTemplates.value = res
    }
  } catch (error) {
    console.error('加载模板失败:', error)
  }
}

// 初始化日期
onMounted(() => {
  // 获取状态栏高度
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  
  const now = new Date()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const year = now.getFullYear()
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  
  currentDate.value = {
    day: `${month}/${day}`,
    weekday: weekdays[now.getDay()],
    full: `${year}-${month}-${day}`
  }
  
  diaryForm.date = `${year}/${month}/${day}`
  
  // 设置日期选择器初始值
  datePickerValue.value = [year - 2020, now.getMonth(), now.getDate() - 1]
  
  loadDiaryList()
  loadTemplates()
})

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 选择图片
const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempPath = res.tempFilePaths[0]
      diaryForm.image = tempPath
      // 尝试上传到后端
      try {
        const uploadRes = await uploadDiaryImage(tempPath)
        if (uploadRes.url) {
          diaryForm.image = uploadRes.url
        }
      } catch (e) {
        console.warn('图片上传失败，使用本地临时路径:', e)
      }
    }
  })
}

// 保存日记
const saveDiary = async () => {
  if (!diaryForm.content && !diaryForm.image) {
    uni.showToast({
      title: '请输入内容或选择照片',
      icon: 'none'
    })
    return
  }

  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const dateParts = diaryForm.date.split('/')
  const dateObj = new Date(parseInt(dateParts[0]), parseInt(dateParts[1]) - 1, parseInt(dateParts[2]))
  
  const newDiary: Diary = {
    id: Date.now(),
    day: `${dateParts[1]}/${dateParts[2]}`,
    weekday: weekdays[dateObj.getDay()],
    date: diaryForm.date.replace(/\//g, '-'),
    image: diaryForm.image,
    content: diaryForm.content,
    location: diaryForm.location,
    mood: diaryForm.mood,
    moodIcon: diaryForm.moodIcon,
    weather: diaryForm.weather,
    weatherIcon: diaryForm.weatherIcon
  }

  try {
    const res = await createDiary({
      image: diaryForm.image,
      images: diaryForm.image ? [diaryForm.image] : [],
      content: diaryForm.content,
      location: diaryForm.location,
      date: diaryForm.date.replace(/\//g, '-'),
      mood: diaryForm.mood,
      weather: diaryForm.weather
    })
    
    if (res && res.id) {
      newDiary.id = res.id
    }
  } catch (error) {
    console.error('保存日记失败:', error)
  }

  diaryList.value.unshift(newDiary)

  // 重置表单
  resetDiaryForm()
  showAddDiary.value = false

  uni.showToast({
    title: '记录成功',
    icon: 'success'
  })
}

// 重置表单
const resetDiaryForm = () => {
  diaryForm.image = ''
  diaryForm.content = ''
  diaryForm.location = ''
  diaryForm.mood = ''
  diaryForm.moodIcon = ''
  diaryForm.weather = ''
  diaryForm.weatherIcon = ''
  contentPlaceholder.value = '记录旅行中的美好瞬间...'
}

// 查看日记详情
const viewDiaryDetail = (diary: Diary) => {
  const year = diary.date.split('-')[0]
  const storyData = {
    id: diary.id,
    title: diary.location ? `${diary.location}之旅` : '旅行日记',
    day: diary.day,
    weekday: diary.weekday,
    date: diary.date,
    year: year,
    image: diary.image,
    content: diary.content,
    location: diary.location,
    mood: diary.mood,
    moodIcon: diary.moodIcon,
    weather: diary.weather,
    weatherIcon: diary.weatherIcon,
    createdAt: diary.date
  }
  
  uni.setStorageSync('currentStory', JSON.stringify(storyData))
  uni.navigateTo({
    url: `/pages/story/detail?id=${diary.id}`
  })
}

// 分享日记
const shareDiary = (diary: Diary) => {
  const year = diary.date.split('-')[0]
  const storyData = {
    id: diary.id,
    title: diary.location ? `${diary.location}之旅` : '旅行日记',
    day: diary.day,
    weekday: diary.weekday,
    date: diary.date,
    year: year,
    image: diary.image,
    content: diary.content,
    location: diary.location,
    mood: diary.mood,
    moodIcon: diary.moodIcon,
    weather: diary.weather,
    weatherIcon: diary.weatherIcon
  }
  
  uni.setStorageSync('shareStory', JSON.stringify(storyData))
  uni.navigateTo({
    url: `/pages/story/share?id=${diary.id}`
  })
}

// 监听详情页返回，刷新列表
uni.$on('diaryUpdated', () => {
  loadDiaryList()
})
</script>

<style lang="scss" scoped>
@use 'sass:color';

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
  padding: 16rpx 32rpx;
  background: $card-bg;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
}

.back-icon {
  font-size: 44rpx;
  color: $text-primary;
  font-weight: bold;
  margin-top: -4rpx;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 60rpx;
}

// 内容区
.content {
  padding: 24rpx 32rpx;
}

// 日记卡片
.diary-card {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24rpx;
}

.date-left {
  display: flex;
  align-items: baseline;
  gap: 16rpx;
}

.date-num {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
}

.date-weekday {
  font-size: 32rpx;
  font-weight: 500;
  color: $text-primary;
}

.date-full {
  font-size: 26rpx;
  color: $text-secondary;
}

.card-body {
  margin-bottom: 32rpx;
}

.welcome-text {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.text-line {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.8;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.add-diary-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx 32rpx;
  background: $primary-color;
  border-radius: 12rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.96);
  }
}

.btn-icon {
  font-size: 32rpx;
  color: #FFFFFF;
}

.btn-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

// 日记列表
.diary-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.diary-item {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.item-date {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.item-day {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.item-weekday {
  font-size: 28rpx;
  color: $text-primary;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.item-mood, .item-weather {
  font-size: 28rpx;
}

.item-full-date {
  font-size: 24rpx;
  color: $text-secondary;
}

.item-content {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.item-image {
  width: 100%;
  height: 400rpx;
  border-radius: $border-radius-md;
}

.item-text {
  font-size: 28rpx;
  color: $text-primary;
  line-height: 1.8;
}

.item-location {
  font-size: 24rpx;
  color: $text-secondary;
}

// 日记项操作按钮
.item-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #F0F0F5;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: #F5F5F7;
  border-radius: 30rpx;
  
  .action-icon {
    font-size: 28rpx;
    color: #4A90D9;
  }
  
  .action-text {
    font-size: 26rpx;
    color: #4A90D9;
  }
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
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

// 添加日记弹窗
.add-diary-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  overflow: hidden;

  &.popup-show {
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 40rpx;
  border-bottom: 1rpx solid #E5E5EA;
}

.header-close, .header-confirm {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 48rpx;
  color: $text-secondary;
}

.confirm-icon {
  font-size: 40rpx;
  color: $primary-color;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.popup-content {
  padding: 32rpx 40rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
  max-height: 70vh;
  box-sizing: border-box;
  width: 100%;
}

// 通用section标题
.section-title {
  font-size: 26rpx;
  color: $text-secondary;
  margin-bottom: 16rpx;
}

// 模板区域
.template-section {
  margin-bottom: 32rpx;
}

.template-list {
  white-space: nowrap;
  padding: 8rpx 0;
}

.template-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx 24rpx;
  background: $bg-color;
  border-radius: 16rpx;
  margin-right: 20rpx;
  
  &:active {
    background: color.adjust(#F5F5F7, $lightness: -5%);
  }
}

.template-icon {
  font-size: 40rpx;
}

.template-name {
  font-size: 24rpx;
  color: $text-primary;
}

// 照片区域
.photo-section {
  margin-bottom: 32rpx;
}

// 照片选择
.photo-picker {
  width: 100%;
  height: 300rpx;
  background: $bg-color;
  border-radius: $border-radius-lg;
  overflow: hidden;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.picker-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background: linear-gradient(135deg, #E8F5F3 0%, #D4EDE9 100%);
}

.picker-icon {
  font-size: 56rpx;
}

.picker-text {
  font-size: 28rpx;
  color: $text-secondary;
}

.remove-photo {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  width: 48rpx;
  height: 48rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-icon {
  color: #fff;
  font-size: 32rpx;
}

// 输入区域
.input-section {
  margin-bottom: 32rpx;
  position: relative;
}

.content-input {
  width: 100%;
  height: 200rpx;
  padding: 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  font-size: 28rpx;
  color: $text-primary;
  line-height: 1.6;
  box-sizing: border-box;
}

.word-count {
  position: absolute;
  bottom: 16rpx;
  right: 16rpx;
  font-size: 22rpx;
  color: $text-secondary;
}

// 位置选择
.location-section {
  margin-bottom: 32rpx;
}

.location-picker {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
}

.location-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.location-text {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
}

.location-arrow {
  font-size: 32rpx;
  color: $text-secondary;
}

// 日期选择
.date-section {
  margin-bottom: 32rpx;
}

.date-picker {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
}

.date-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.date-text {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
}

.date-arrow {
  font-size: 32rpx;
  color: $text-secondary;
}

// 心情选择
.mood-section {
  margin-bottom: 32rpx;
}

.mood-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.mood-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 20rpx;
  background: $bg-color;
  border-radius: 16rpx;
  min-width: 100rpx;
  
  &.active {
    background: rgba(0, 184, 169, 0.15);
    border: 2rpx solid $primary-color;
  }
}

.mood-emoji {
  font-size: 36rpx;
}

.mood-name {
  font-size: 22rpx;
  color: $text-secondary;
}

// 天气选择
.weather-section {
  margin-bottom: 32rpx;
}

.weather-list {
  display: flex;
  gap: 20rpx;
}

.weather-item {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  border-radius: 50%;
  
  &.active {
    background: rgba(0, 184, 169, 0.15);
    border: 2rpx solid $primary-color;
  }
}

.weather-emoji {
  font-size: 40rpx;
}

// 日期弹窗
.date-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.date-popup-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.date-popup-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
}

.date-picker-view {
  height: 400rpx;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80rpx;
  font-size: 32rpx;
  color: $text-primary;
}

.date-popup-btns {
  display: flex;
  border-top: 1rpx solid #E5E5EA;
}

.date-btn {
  flex: 1;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  
  &.cancel {
    color: $text-secondary;
  }
  
  &.confirm {
    color: $primary-color;
    font-weight: 600;
  }
}

// 旧样式兼容
.input-area {
  margin-bottom: 24rpx;
}

.input-row {
  padding: 28rpx 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  margin-bottom: 20rpx;
}

.location-input {
  width: 100%;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.date-input {
  font-size: 28rpx;
  color: $text-secondary;
}
</style>
