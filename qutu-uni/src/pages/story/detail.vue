<template>
  <view class="detail-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">故事详情</text>
      <view class="nav-actions" :style="navActionsStyle">
        <view class="action-btn" @click="shareStory">
          <text class="action-icon">↗</text>
        </view>
        <view class="action-btn" @click="showMoreMenu = true">
          <view class="more-dots">
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>
    </view>

    <scroll-view class="detail-content" scroll-y>
      <!-- 日记卡片 -->
      <view class="stamp-section">
        <view class="stamp-card">
          <view class="stamp-border">
            <view class="stamp-content">
              <text class="stamp-title">{{ story.title || '旅行日记' }}</text>
              <image 
                v-if="story.image"
                class="stamp-image" 
                :src="story.image" 
                mode="aspectFill" 
                @click="previewImage"
              />
              <view class="stamp-placeholder" v-else>
                <text class="placeholder-icon">📝</text>
                <text class="placeholder-text">文字日记</text>
              </view>
              <view class="stamp-footer">
                <text class="stamp-date">{{ story.day }}</text>
                <text class="stamp-year">{{ story.year }}</text>
              </view>
            </view>
          </view>
          
          <!-- 位置标签 -->
          <view class="stamp-location" v-if="story.location">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ story.location }}</text>
          </view>
        </view>
      </view>

      <!-- 日记内容 -->
      <view class="desc-section" v-if="story.content">
        <text class="desc-text">{{ story.content }}</text>
      </view>

      <!-- 标签 -->
      <view class="tags-section" v-if="story.tags && story.tags.length > 0">
        <view class="tag-item" v-for="(tag, index) in story.tags" :key="index">
          <text class="tag-text"># {{ tag }}</text>
        </view>
      </view>

      <!-- 天气和心情 -->
      <view class="mood-section" v-if="story.weather || story.mood">
        <view class="mood-item" v-if="story.weather">
          <text class="mood-icon">{{ story.weatherIcon || '☀️' }}</text>
          <text class="mood-text">{{ story.weather }}</text>
        </view>
        <view class="mood-item" v-if="story.mood">
          <text class="mood-icon">{{ story.moodIcon || '😊' }}</text>
          <text class="mood-text">{{ story.mood }}</text>
        </view>
      </view>

      <!-- 信息详情 -->
      <view class="info-section">
        <view class="info-item">
          <text class="info-label">记录日期</text>
          <text class="info-value">{{ story.date }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">星期</text>
          <text class="info-value">{{ story.weekday }}</text>
        </view>
        <view class="info-item" v-if="story.createdAt">
          <text class="info-label">创建时间</text>
          <text class="info-value">{{ story.createdAt }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bottom-btn" @click="editStory">
        <text class="btn-icon">✏️</text>
        <text class="btn-text">编辑</text>
      </view>
      <view class="bottom-btn primary" @click="shareStory">
        <text class="btn-icon">↗</text>
        <text class="btn-text">分享</text>
      </view>
    </view>

    <!-- 更多菜单 -->
    <view class="menu-mask" :class="{ show: showMoreMenu }" @click="showMoreMenu = false">
      <view class="menu-content" @click.stop>
        <view class="menu-item" @click="editStory">
          <text class="menu-icon">✏️</text>
          <text class="menu-text">编辑</text>
        </view>
        <view class="menu-item" @click="copyContent">
          <text class="menu-icon">📋</text>
          <text class="menu-text">复制内容</text>
        </view>
        <view class="menu-item" @click="saveImage" v-if="story.image">
          <text class="menu-icon">💾</text>
          <text class="menu-text">保存图片</text>
        </view>
        <view class="menu-item danger" @click="deleteConfirm">
          <text class="menu-icon">🗑️</text>
          <text class="menu-text">删除</text>
        </view>
        <view class="menu-cancel" @click="showMoreMenu = false">
          <text class="cancel-text">取消</text>
        </view>
      </view>
    </view>

    <!-- 编辑弹窗 -->
    <view class="popup-mask" v-if="showEditPopup" @click="showEditPopup = false"></view>
    <view class="edit-popup" :class="{ 'popup-show': showEditPopup }">
      <view class="popup-header">
        <view class="header-close" @click="showEditPopup = false">
          <text class="close-icon">×</text>
        </view>
        <text class="popup-title">编辑日记</text>
        <view class="header-confirm" @click="saveEdit">
          <text class="confirm-icon">✓</text>
        </view>
      </view>

      <scroll-view class="popup-content" scroll-y>
        <!-- 照片选择 -->
        <view class="photo-picker" @click="chooseImage">
          <image v-if="editForm.image" class="preview-image" :src="editForm.image" mode="aspectFill" />
          <view v-else class="picker-placeholder">
            <text class="picker-icon">+</text>
            <text class="picker-text">选择照片</text>
          </view>
        </view>

        <!-- 标题输入 -->
        <view class="input-group">
          <text class="input-label">标题</text>
          <input 
            class="title-input" 
            v-model="editForm.title"
            placeholder="给日记起个标题"
            maxlength="30"
          />
        </view>

        <!-- 内容输入 -->
        <view class="input-group">
          <text class="input-label">内容</text>
          <textarea 
            class="content-input" 
            v-model="editForm.content"
            placeholder="记录旅行中的美好瞬间"
            maxlength="500"
          />
        </view>

        <!-- 位置输入 -->
        <view class="input-group">
          <text class="input-label">位置</text>
          <view class="location-input" @click="chooseLocation">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ editForm.location || '选择位置' }}</text>
          </view>
        </view>

        <!-- 心情选择 -->
        <view class="input-group">
          <text class="input-label">心情</text>
          <view class="mood-picker">
            <view 
              v-for="(mood, index) in moodOptions" 
              :key="index"
              class="mood-option"
              :class="{ active: editForm.mood === mood.value }"
              @click="editForm.mood = mood.value; editForm.moodIcon = mood.icon"
            >
              <text class="mood-emoji">{{ mood.icon }}</text>
            </view>
          </view>
        </view>

        <!-- 天气选择 -->
        <view class="input-group">
          <text class="input-label">天气</text>
          <view class="weather-picker">
            <view 
              v-for="(weather, index) in weatherOptions" 
              :key="index"
              class="weather-option"
              :class="{ active: editForm.weather === weather.value }"
              @click="editForm.weather = weather.value; editForm.weatherIcon = weather.icon"
            >
              <text class="weather-emoji">{{ weather.icon }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { deleteDiary, updateDiary, getDiaryDetail, uploadDiaryImage } from '@/api'

interface Story {
  id: number
  title?: string
  day: string
  weekday: string
  date: string
  year: string
  image?: string
  content: string
  location?: string
  weather?: string
  weatherIcon?: string
  mood?: string
  moodIcon?: string
  tags?: string[]
  createdAt?: string
}

// 状态栏高度
const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)

// 故事ID
const storyId = ref(0)

// 故事详情
const story = ref<Story>({
  id: 0,
  title: '',
  day: '',
  weekday: '',
  date: '',
  year: '',
  content: ''
})

// 菜单状态
const showMoreMenu = ref(false)

// 编辑弹窗状态
const showEditPopup = ref(false)

const navBarStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return {
      paddingTop: statusBarHeight.value + 'px',
      height: navBarHeight.value + 'px',
      paddingLeft: '24rpx',
      paddingRight: '24rpx',
      paddingBottom: '0px'
    }
  }
  return {
    paddingTop: statusBarHeight.value + 'px',
    minHeight: navBarHeight.value + 'px'
  }
})

const navActionsStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingRight: menuButtonSpace.value + 'px' }
  }
  return {}
})

// 编辑表单
const editForm = ref({
  title: '',
  content: '',
  image: '',
  location: '',
  mood: '',
  moodIcon: '',
  weather: '',
  weatherIcon: ''
})

// 心情选项
const moodOptions = [
  { icon: '😊', value: '开心' },
  { icon: '😄', value: '愉快' },
  { icon: '🥰', value: '幸福' },
  { icon: '😌', value: '平静' },
  { icon: '🤔', value: '思考' },
  { icon: '😢', value: '难过' }
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

// 获取系统信息和参数
onMounted(async () => {
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
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  
  if (options.id) {
    storyId.value = parseInt(options.id)
  }
  
  // 尝试从本地存储获取
  const storyData = uni.getStorageSync('currentStory')
  if (storyData) {
    try {
      story.value = JSON.parse(storyData)
    } catch {}
    uni.removeStorageSync('currentStory')
  }
  
  // 从后端API加载详情
  if (storyId.value) {
    try {
      const detail = await getDiaryDetail(storyId.value)
      if (detail) {
        story.value = {
          ...story.value,
          id: detail.id,
          title: detail.location ? `${detail.location}之旅` : '旅行日记',
          day: detail.day || '',
          weekday: detail.weekday || '',
          date: detail.date || '',
          year: detail.date ? detail.date.split('-')[0] : '',
          image: detail.image || '',
          content: detail.content || '',
          location: detail.location || '',
          weather: detail.weather || '',
          weatherIcon: detail.weatherIcon || '',
          mood: detail.mood || '',
          moodIcon: detail.moodIcon || '',
          tags: [],
          createdAt: detail.date || '',
        }
      }
    } catch (e) {
      console.error('加载故事详情失败:', e)
    }
  }
})

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 预览图片
const previewImage = () => {
  if (story.value.image) {
    uni.previewImage({
      urls: [story.value.image],
      current: story.value.image
    })
  }
}

// 分享
const shareStory = () => {
  showMoreMenu.value = false
  // 保存故事数据供分享页使用
  uni.setStorageSync('shareStory', JSON.stringify(story.value))
  uni.navigateTo({
    url: `/pages/story/share?id=${storyId.value}`
  })
}

// 编辑
const editStory = () => {
  showMoreMenu.value = false
  // 初始化编辑表单
  editForm.value = {
    title: story.value.title || '',
    content: story.value.content || '',
    image: story.value.image || '',
    location: story.value.location || '',
    mood: story.value.mood || '',
    moodIcon: story.value.moodIcon || '',
    weather: story.value.weather || '',
    weatherIcon: story.value.weatherIcon || ''
  }
  showEditPopup.value = true
}

// 选择图片
const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempPath = res.tempFilePaths[0]
      try {
        const uploadRes = await uploadDiaryImage(tempPath)
        editForm.value.image = uploadRes.url
      } catch (e) {
        console.error('上传图片失败:', e)
        // 上传失败时使用临时路径（可能无法持久化）
        editForm.value.image = tempPath
        uni.showToast({ title: '图片上传失败', icon: 'none' })
      }
    }
  })
}

// 选择位置
const chooseLocation = () => {
  uni.chooseLocation({
    success: (res) => {
      editForm.value.location = res.name || res.address
    },
    fail: () => {
      // 选择位置失败时，使用模拟位置
      const mockLocations = ['北京·故宫', '上海·外滩', '杭州·西湖', '成都·宽窄巷子', '西安·大雁塔']
      const randomLocation = mockLocations[Math.floor(Math.random() * mockLocations.length)]
      editForm.value.location = randomLocation
      uni.showToast({ title: '已选择位置', icon: 'none' })
    }
  })
}

// 保存编辑
const saveEdit = async () => {
  if (!editForm.value.content.trim()) {
    uni.showToast({ title: '请输入日记内容', icon: 'none' })
    return
  }
  
  // 更新故事数据
  story.value.title = editForm.value.title || (editForm.value.location ? `${editForm.value.location}之旅` : '旅行日记')
  story.value.content = editForm.value.content
  story.value.image = editForm.value.image
  story.value.location = editForm.value.location
  story.value.mood = editForm.value.mood
  story.value.moodIcon = editForm.value.moodIcon
  story.value.weather = editForm.value.weather
  story.value.weatherIcon = editForm.value.weatherIcon
  
  // 调用更新API
  try {
    const updateData: any = {
      content: story.value.content,
      location: story.value.location,
      mood: story.value.mood || '',
      weather: story.value.weather || '',
    }
    // 只有图片发生变化时才提交图片字段
    if (editForm.value.image) {
      updateData.images = [editForm.value.image]
    } else {
      updateData.images = []
    }
    const success = await updateDiary(storyId.value, updateData)
    if (!success) {
      uni.showToast({ title: '保存失败', icon: 'none' })
      return
    }
  } catch (e) {
    console.error('更新日记失败:', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
    return
  }
  
  // 通知列表页刷新
  uni.$emit('diaryUpdated')
  
  // 关闭弹窗
  showEditPopup.value = false
  
  uni.showToast({ title: '保存成功', icon: 'success' })
}

// 复制内容
const copyContent = () => {
  showMoreMenu.value = false
  uni.setClipboardData({
    data: story.value.content || '',
    success: () => {
      uni.showToast({ title: '复制成功', icon: 'success' })
    }
  })
}

// 保存图片
const saveImage = async () => {
  showMoreMenu.value = false
  if (!story.value.image) return
  
  try {
    await uni.saveImageToPhotosAlbum({
      filePath: story.value.image
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

// 删除确认
const deleteConfirm = () => {
  showMoreMenu.value = false
  uni.showModal({
    title: '删除日记',
    content: '确定要删除这篇日记吗？',
    confirmColor: '#FF3B30',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteDiary(storyId.value)
          uni.showToast({ title: '删除成功', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: #F5F5F7;
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 30rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
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
  color: #333;
  font-weight: bold;
  margin-top: -4rpx;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.nav-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
}

.action-icon {
  font-size: 32rpx;
  color: #333;
}

// 三个点样式
.more-dots {
  display: flex;
  gap: 6rpx;
  
  .dot {
    width: 8rpx;
    height: 8rpx;
    background: #333;
    border-radius: 50%;
  }
}

.detail-content {
  padding: 0 30rpx;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  width: 100%;
}

// 邮票卡片样式
.stamp-section {
  padding: 20rpx 0;
}

.stamp-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.stamp-border {
  border: 4rpx dashed #E0E0E0;
  border-radius: 16rpx;
  padding: 24rpx;
}

.stamp-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stamp-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  text-align: center;
}

.stamp-image {
  width: 100%;
  height: 400rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.stamp-placeholder {
  width: 100%;
  height: 300rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  
  .placeholder-icon {
    font-size: 80rpx;
    margin-bottom: 16rpx;
  }
  
  .placeholder-text {
    font-size: 28rpx;
    color: #fff;
  }
}

.stamp-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
  
  .stamp-date {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
  }
  
  .stamp-year {
    font-size: 28rpx;
    color: #666;
  }
}

.stamp-location {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 20rpx;
  padding: 12rpx 20rpx;
  background: #F0F0F5;
  border-radius: 20rpx;
  
  .location-icon {
    font-size: 28rpx;
  }
  
  .location-text {
    font-size: 26rpx;
    color: #666;
  }
}

// 描述区域
.desc-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-top: 20rpx;
  box-sizing: border-box;
  
  .desc-text {
    font-size: 30rpx;
    color: #333;
    line-height: 1.8;
    word-break: break-all;
    display: block;
  }
}

// 标签区域
.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
  
  .tag-item {
    background: rgba(74, 144, 217, 0.1);
    padding: 12rpx 24rpx;
    border-radius: 30rpx;
    
    .tag-text {
      font-size: 26rpx;
      color: #4A90D9;
    }
  }
}

// 心情天气
.mood-section {
  display: flex;
  gap: 30rpx;
  margin-top: 20rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx 30rpx;
  
  .mood-item {
    display: flex;
    align-items: center;
    gap: 12rpx;
    
    .mood-icon {
      font-size: 36rpx;
    }
    
    .mood-text {
      font-size: 28rpx;
      color: #666;
    }
  }
}

// 信息区域
.info-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 20rpx 30rpx;
  margin-top: 20rpx;
  
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #F0F0F5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      font-size: 28rpx;
      color: #999;
    }
    
    .info-value {
      font-size: 28rpx;
      color: #333;
    }
  }
}

// 底部操作栏
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
  
  .bottom-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10rpx;
    height: 88rpx;
    border-radius: 44rpx;
    background: #F5F5F7;
    
    &.primary {
      background: linear-gradient(135deg, #4A90D9, #67B8DE);
      
      .btn-icon, .btn-text {
        color: #fff;
      }
    }
    
    .btn-icon {
      font-size: 32rpx;
    }
    
    .btn-text {
      font-size: 28rpx;
      color: #333;
    }
  }
}

// 更多菜单
.menu-mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s;
  
  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.menu-content {
  position: absolute;
  left: 30rpx;
  right: 30rpx;
  bottom: calc(30rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 32rpx 40rpx;
  border-bottom: 1rpx solid #F0F0F5;
  
  &.danger {
    .menu-text {
      color: #FF3B30;
    }
  }
  
  .menu-icon {
    font-size: 36rpx;
  }
  
  .menu-text {
    font-size: 30rpx;
    color: #333;
  }
}

.menu-cancel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx;
  background: #F5F5F7;
  
  .cancel-text {
    font-size: 30rpx;
    color: #666;
  }
}

// 编辑弹窗遮罩
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

// 编辑弹窗
.edit-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  max-height: 85vh;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  
  &.popup-show {
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #F0F0F5;
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
  color: #999;
}

.confirm-icon {
  font-size: 40rpx;
  color: #4A90D9;
  font-weight: bold;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.popup-content {
  padding: 32rpx;
  max-height: calc(85vh - 120rpx);
}

// 照片选择
.photo-picker {
  width: 100%;
  height: 360rpx;
  background: #F5F5F7;
  border-radius: 16rpx;
  margin-bottom: 32rpx;
  overflow: hidden;
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
  gap: 16rpx;
}

.picker-icon {
  font-size: 64rpx;
  color: #ccc;
}

.picker-text {
  font-size: 28rpx;
  color: #999;
}

// 输入组
.input-group {
  margin-bottom: 32rpx;
}

.input-label {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.title-input {
  width: 100%;
  height: 80rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.content-input {
  width: 100%;
  height: 200rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.location-input {
  display: flex;
  align-items: center;
  gap: 12rpx;
  height: 80rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  padding: 0 24rpx;
}

.location-icon {
  font-size: 32rpx;
}

.location-text {
  font-size: 28rpx;
  color: #666;
}

// 心情选择
.mood-picker, .weather-picker {
  display: flex;
  gap: 20rpx;
  flex-wrap: wrap;
}

.mood-option, .weather-option {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F7;
  border-radius: 50%;
  transition: all 0.2s ease;
  
  &.active {
    background: #4A90D9;
    transform: scale(1.1);
  }
}

.mood-emoji, .weather-emoji {
  font-size: 40rpx;
}
</style>
