<template>
  <view class="edit-page">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">编辑采集</text>
      <view class="nav-right" :style="navRightStyle" @click="saveCollection">
        <text class="save-text">保存</text>
      </view>
    </view>

    <scroll-view class="edit-content" scroll-y>
      <!-- 图片预览 -->
      <view class="image-section">
        <view class="stamp-preview">
          <view class="stamp-border">
            <view class="stamp-content">
              <input 
                class="title-input" 
                v-model="form.title" 
                placeholder="输入标题..."
                maxlength="50"
              />
              <image class="preview-image" :src="imagePath" mode="aspectFill" />
              <view class="stamp-footer">
                <text class="stamp-date">{{ currentDate }}</text>
                <text class="stamp-year">{{ currentYear }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 位置信息 -->
      <view class="form-section">
        <view class="section-header">
          <text class="section-title">位置</text>
        </view>
        <view class="location-picker" @click="selectLocation">
          <view class="location-icon">📍</view>
          <text class="location-text">{{ form.location || '添加位置' }}</text>
          <text class="location-arrow">›</text>
        </view>
      </view>

      <!-- 分组选择 -->
      <view class="form-section">
        <view class="section-header">
          <text class="section-title">分组</text>
        </view>
        <view class="group-selector">
          <view 
            class="group-tag" 
            v-for="group in groups" 
            :key="group.id"
            :class="{ active: form.groupId === group.id }"
            @click="form.groupId = group.id"
          >
            <text class="group-text">{{ group.name }}</text>
          </view>
          <view class="group-tag add" @click="showAddGroup = true">
            <text class="group-text">+ 新建</text>
          </view>
        </view>
      </view>

      <!-- 描述 -->
      <view class="form-section">
        <view class="section-header">
          <text class="section-title">描述</text>
          <view class="ai-btn" @click="generateDescription">
            <text class="ai-text">✨ AI生成</text>
          </view>
        </view>
        <textarea 
          class="desc-input" 
          v-model="form.description" 
          placeholder="添加描述..."
          maxlength="500"
          :auto-height="true"
        />
      </view>

      <!-- 标签 -->
      <view class="form-section">
        <view class="section-header">
          <text class="section-title">标签</text>
        </view>
        <view class="tags-container">
          <view 
            class="tag-item" 
            v-for="(tag, index) in form.tags" 
            :key="index"
          >
            <text class="tag-text"># {{ tag }}</text>
            <text class="tag-remove" @click="removeTag(index)">×</text>
          </view>
          <view class="tag-add" @click="showAddTag = true">
            <text class="add-text">+ 添加标签</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 添加分组弹窗 -->
    <view class="popup-mask" :class="{ show: showAddGroup }" @click="showAddGroup = false">
      <view class="popup-content small" @click.stop>
        <view class="popup-header">
          <text class="popup-title">新建分组</text>
        </view>
        <input 
          class="popup-input" 
          v-model="newGroupName" 
          placeholder="分组名称"
          maxlength="20"
        />
        <view class="popup-actions">
          <view class="popup-btn cancel" @click="showAddGroup = false">取消</view>
          <view class="popup-btn confirm" @click="addGroup">确定</view>
        </view>
      </view>
    </view>

    <!-- 添加标签弹窗 -->
    <view class="popup-mask" :class="{ show: showAddTag }" @click="showAddTag = false">
      <view class="popup-content small" @click.stop>
        <view class="popup-header">
          <text class="popup-title">添加标签</text>
        </view>
        <input 
          class="popup-input" 
          v-model="newTag" 
          placeholder="标签名称"
          maxlength="20"
        />
        <view class="popup-actions">
          <view class="popup-btn cancel" @click="showAddTag = false">取消</view>
          <view class="popup-btn confirm" @click="addTag">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { createCollection, getCollectionGroups, createCollectionGroup, recognizeImage } from '@/api'

// 状态栏高度
const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)

// 图片路径
const imagePath = ref('')

// 表单数据
const form = ref({
  title: '',
  location: '',
  groupId: 1,
  description: '',
  tags: [] as string[]
})

// 分组列表
const groups = ref([
  { id: 1, name: '默认分组' }
])

// 弹窗状态
const showAddGroup = ref(false)
const showAddTag = ref(false)
const newGroupName = ref('')
const newTag = ref('')

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return `${(now.getMonth() + 1).toString().padStart(2, '0')}/${now.getDate().toString().padStart(2, '0')}`
})

const currentYear = computed(() => {
  return new Date().getFullYear().toString()
})

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

const navRightStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingRight: menuButtonSpace.value + 'px' }
  }
  return {}
})

// 获取系统信息和参数
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
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  
  if (options.image) {
    imagePath.value = decodeURIComponent(options.image)
  }
  
  // 加载分组
  loadGroups()
})

// 加载分组
const loadGroups = async () => {
  try {
    const res = await getCollectionGroups()
    if (res && res.length > 0) {
      groups.value = res
    }
  } catch (e) {
    console.error('加载分组失败', e)
  }
}

// 选择位置
const selectLocation = () => {
  uni.chooseLocation({
    success: (res) => {
      form.value.location = res.name || res.address
    },
    fail: () => {
      // 用户取消或未授权
    }
  })
}

// AI生成描述
const generateDescription = async () => {
  if (!imagePath.value) return
  
  uni.showLoading({ title: 'AI识别中...' })
  try {
    const res = await recognizeImage(imagePath.value)
    if (res) {
      if (res.title && !form.value.title) {
        form.value.title = res.title
      }
      if (res.description) {
        form.value.description = res.description
      }
      if (res.tags && res.tags.length > 0) {
        form.value.tags = [...new Set([...form.value.tags, ...res.tags])]
      }
    }
    uni.showToast({ title: '识别完成', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '识别失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

// 添加分组
const addGroup = async () => {
  if (!newGroupName.value.trim()) {
    uni.showToast({ title: '请输入分组名称', icon: 'none' })
    return
  }
  
  try {
    const newGroup = await createCollectionGroup(newGroupName.value.trim())
    groups.value.push(newGroup)
    form.value.groupId = newGroup.id
    showAddGroup.value = false
    newGroupName.value = ''
  } catch (e) {
    uni.showToast({ title: '创建失败', icon: 'none' })
  }
}

// 添加标签
const addTag = () => {
  const tag = newTag.value.trim()
  if (!tag) {
    uni.showToast({ title: '请输入标签名称', icon: 'none' })
    return
  }
  if (!form.value.tags.includes(tag)) {
    form.value.tags.push(tag)
  }
  showAddTag.value = false
  newTag.value = ''
}

// 移除标签
const removeTag = (index: number) => {
  form.value.tags.splice(index, 1)
}

// 保存采集
const saveCollection = async () => {
  if (!imagePath.value) {
    uni.showToast({ title: '请选择图片', icon: 'none' })
    return
  }
  
  uni.showLoading({ title: '保存中...' })
  try {
    await createCollection({
      image: imagePath.value,
      title: form.value.title || '未命名采集',
      description: form.value.description,
      location: form.value.location,
      groupId: form.value.groupId
    })
    
    uni.showToast({ title: '保存成功', icon: 'success' })
    
    setTimeout(() => {
      // 返回采集列表
      uni.redirectTo({
        url: '/pages/collect/index'
      })
    }, 1500)
  } catch (e) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

// 返回
const goBack = () => {
  uni.showModal({
    title: '放弃编辑？',
    content: '当前编辑内容将不会保存',
    success: (res) => {
      if (res.confirm) {
        uni.navigateBack()
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.edit-page {
  min-height: 100vh;
  background: #F5F5F7;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  background: #F5F5F7;
}

.nav-left,
.nav-right {
  min-width: 80rpx;
  display: flex;
  align-items: center;
}

.back-icon {
  font-size: 48rpx;
  color: #1D1D1F;
  font-weight: 300;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.save-text {
  font-size: 32rpx;
  color: #007AFF;
  font-weight: 500;
}

.edit-content {
  height: calc(100vh - 100rpx);
  padding-bottom: 64rpx;
}

// 图片预览
.image-section {
  padding: 32rpx;
}

.stamp-preview {
  max-width: 500rpx;
  margin: 0 auto;
}

.stamp-border {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 4rpx;
  background-image: 
    radial-gradient(circle at 0 8rpx, transparent 8rpx, #FFFFFF 8rpx),
    radial-gradient(circle at 100% 8rpx, transparent 8rpx, #FFFFFF 8rpx);
  background-size: 24rpx 16rpx;
  background-position: left top, right top;
  background-repeat: repeat-y;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.stamp-content {
  padding: 24rpx;
}

.title-input {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 16rpx;
  width: 100%;
}

.preview-image {
  width: 100%;
  height: 320rpx;
  border-radius: 12rpx;
  background: #F0F0F0;
}

.stamp-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 16rpx;
}

.stamp-date {
  font-size: 48rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.stamp-year {
  font-size: 24rpx;
  color: #86868B;
  writing-mode: vertical-rl;
}

// 表单区域
.form-section {
  background: #FFFFFF;
  margin: 24rpx 32rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.ai-btn {
  padding: 8rpx 16rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 100rpx;
}

.ai-text {
  font-size: 24rpx;
  color: #FFFFFF;
}

// 位置选择
.location-picker {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
}

.location-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.location-text {
  flex: 1;
  font-size: 30rpx;
  color: #1D1D1F;
}

.location-arrow {
  font-size: 32rpx;
  color: #C7C7CC;
}

// 分组选择
.group-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.group-tag {
  padding: 12rpx 24rpx;
  background: #F5F5F7;
  border-radius: 100rpx;
  border: 2rpx solid transparent;

  &.active {
    background: #E3F2FD;
    border-color: #007AFF;

    .group-text {
      color: #007AFF;
    }
  }

  &.add {
    border: 2rpx dashed #C7C7CC;
    background: transparent;

    .group-text {
      color: #86868B;
    }
  }
}

.group-text {
  font-size: 26rpx;
  color: #1D1D1F;
}

// 描述输入
.desc-input {
  width: 100%;
  font-size: 28rpx;
  color: #1D1D1F;
  line-height: 1.6;
  min-height: 120rpx;
}

// 标签
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 8rpx 20rpx;
  background: #F5F5F7;
  border-radius: 100rpx;
}

.tag-text {
  font-size: 26rpx;
  color: #007AFF;
}

.tag-remove {
  font-size: 28rpx;
  color: #86868B;
  margin-left: 8rpx;
}

.tag-add {
  padding: 8rpx 20rpx;
  border: 2rpx dashed #C7C7CC;
  border-radius: 100rpx;
}

.add-text {
  font-size: 26rpx;
  color: #86868B;
}

// 弹窗
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 1000;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.popup-content {
  width: 600rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 32rpx;
  transform: scale(0.9);
  transition: transform 0.3s ease;

  .popup-mask.show & {
    transform: scale(1);
  }

  &.small {
    width: 500rpx;
  }
}

.popup-header {
  text-align: center;
  margin-bottom: 32rpx;
}

.popup-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.popup-input {
  width: 100%;
  padding: 24rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  font-size: 30rpx;
  margin-bottom: 32rpx;
}

.popup-actions {
  display: flex;
  gap: 24rpx;
}

.popup-btn {
  flex: 1;
  padding: 24rpx;
  text-align: center;
  border-radius: 12rpx;
  font-size: 30rpx;

  &.cancel {
    background: #F5F5F7;
    color: #86868B;
  }

  &.confirm {
    background: #007AFF;
    color: #FFFFFF;
  }
}
</style>
