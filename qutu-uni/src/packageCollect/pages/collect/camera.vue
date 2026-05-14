<template>
  <view class="camera-page">
    <!-- 相机预览区域 -->
    <view class="camera-container">
      <!-- 微信小程序相机组件 -->
      <camera
        v-if="cameraReady && useWxCamera"
        class="camera-view"
        :device-position="devicePosition"
        :flash="flashMode"
        :key="devicePosition"
        @error="onCameraError"
      />
      
      <!-- 无相机权限时的占位 -->
      <view class="camera-placeholder" v-if="!cameraReady || !useWxCamera">
        <text class="placeholder-icon">📷</text>
        <text class="placeholder-text">{{ placeholderText }}</text>
        <view class="placeholder-btn" @click="openSystemCamera">
          <text class="btn-text">使用系统相机拍照</text>
        </view>
        <view class="placeholder-btn secondary" @click="chooseFromAlbum">
          <text class="btn-text">从相册选择</text>
        </view>
      </view>
      
      <!-- 取景框 -->
      <view class="viewfinder" v-if="cameraReady && useWxCamera">
        <view class="corner top-left"></view>
        <view class="corner top-right"></view>
        <view class="corner bottom-left"></view>
        <view class="corner bottom-right"></view>
      </view>
    </view>

    <!-- 顶部操作栏 -->
    <view class="top-bar" :style="topBarStyle">
      <view class="close-btn" @click="goBack">
        <text class="close-icon">×</text>
      </view>
      <view class="top-tools" v-if="cameraReady && useWxCamera">
        <view class="tool-btn" :class="{ active: flashMode !== 'off' }" @click="toggleFlash">
          <view class="icon-flash">
            <view class="flash-bolt"></view>
            <view class="flash-slash" v-if="flashMode === 'off'"></view>
          </view>
        </view>
        <view class="tool-btn" @click="toggleCamera">
          <view class="icon-flip"></view>
        </view>
      </view>
      <view class="settings-btn" @click="goToSettings">
        <view class="icon-gear"></view>
      </view>
    </view>

    <!-- 位置信息 -->
    <view class="location-bar" v-if="currentLocation">
      <view class="location-tag">
        <text class="location-icon">◉</text>
        <text class="location-text">{{ currentLocation }}</text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <!-- 相册入口 -->
      <view class="album-btn" @click="chooseFromAlbum">
        <image v-if="lastPhoto" class="album-preview" :src="lastPhoto" mode="aspectFill" />
      </view>

      <!-- 拍照按钮 -->
      <view class="capture-btn" @click="takePhoto">
        <view class="capture-outer">
          <view class="capture-inner"></view>
        </view>
      </view>

      <!-- 翻转相机 -->
      <view class="flip-btn" @click="toggleCamera">
        <view class="icon-flip"></view>
      </view>
    </view>
    
    <!-- 拍照方式选择弹窗 -->
    <view class="action-sheet-mask" :class="{ show: showCameraChoice }" @click="showCameraChoice = false">
      <view class="action-sheet" @click.stop>
        <view class="action-item" @click="useWxCameraMode">
          <text class="action-text">使用微信相机</text>
        </view>
        <view class="action-item" @click="openSystemCamera">
          <text class="action-text">使用系统相机</text>
        </view>
        <view class="action-item" @click="chooseFromAlbum">
          <text class="action-text">从相册选择</text>
        </view>
        <view class="action-cancel" @click="showCameraChoice = false">
          <text class="cancel-text">取消</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

// 状态栏高度
const statusBarHeight = ref(0)

// 胶囊按钮信息
const menuButtonLeft = ref(0)
const menuButtonTop = ref(0)
const menuButtonHeight = ref(32)
const windowWidth = ref(375)

const topBarStyle = computed(() => {
  // #ifdef MP-WEIXIN
  if (menuButtonTop.value > 0) {
    return {
      paddingTop: menuButtonTop.value + 'px',
      height: (menuButtonTop.value + menuButtonHeight.value + 8) + 'px',
      paddingRight: (windowWidth.value - menuButtonLeft.value + 10) + 'px'
    }
  }
  // #endif
  return {
    paddingTop: statusBarHeight.value + 'px'
  }
})

// 相机状态
const cameraReady = ref(false)
const useWxCamera = ref(true) // 是否使用微信相机组件

// 相机设置
const devicePosition = ref<'front' | 'back'>('back')
const flashMode = ref<'auto' | 'on' | 'off' | 'torch'>('auto')

// 弹窗状态
const showCameraChoice = ref(false)

// 位置信息
const currentLocation = ref('')

// 最近照片
const lastPhoto = ref('')
const lastCollection = ref('')

// 占位文字
const placeholderText = computed(() => {
  if (!cameraReady.value) {
    return '相机权限未授权'
  }
  return '点击下方按钮拍照'
})

// 闪光灯图标
// 获取系统信息
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  windowWidth.value = systemInfo.windowWidth || 375

  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonLeft.value = menuButton.left
      menuButtonTop.value = menuButton.top
      menuButtonHeight.value = menuButton.height
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  // 检查相机权限
  checkCameraPermission()
  
  // 获取位置
  getLocation()
  
  // 获取最近照片
  getLastPhoto()
})

// 检查相机权限
const checkCameraPermission = () => {
  // #ifdef MP-WEIXIN
  uni.authorize({
    scope: 'scope.camera',
    success: () => {
      cameraReady.value = true
      useWxCamera.value = true
    },
    fail: () => {
      cameraReady.value = false
      useWxCamera.value = false
      // 相机权限被拒绝，可以使用系统相机
      uni.showModal({
        title: '相机权限',
        content: '未获得相机权限，您可以使用系统相机拍照或从相册选择',
        showCancel: true,
        cancelText: '去授权',
        confirmText: '使用系统相机',
        success: (res) => {
          if (res.confirm) {
            openSystemCamera()
          } else if (res.cancel) {
            uni.openSetting({
              success: (settingRes) => {
                if (settingRes.authSetting['scope.camera']) {
                  cameraReady.value = true
                  useWxCamera.value = true
                }
              }
            })
          }
        }
      })
    }
  })
  // #endif
  
  // #ifndef MP-WEIXIN
  // 非微信环境直接使用系统相机
  cameraReady.value = false
  useWxCamera.value = false
  // #endif
}

// 切换闪光灯
const toggleFlash = () => {
  const modes: Array<'auto' | 'on' | 'off' | 'torch'> = ['auto', 'on', 'off', 'torch']
  const currentIndex = modes.indexOf(flashMode.value)
  flashMode.value = modes[(currentIndex + 1) % modes.length]
  
  if (cameraReady.value && useWxCamera.value) {
    const ctx: any = uni.createCameraContext()
    ctx.setFlashMode({
      flash: flashMode.value,
      success: () => {
        const tips: Record<string, string> = {
          'auto': '自动闪光',
          'on': '开启闪光',
          'off': '关闭闪光',
          'torch': '常亮'
        }
        uni.showToast({ title: tips[flashMode.value], icon: 'none' })
      },
      fail: (err: any) => {
        console.error('设置闪光灯失败', err)
        uni.showToast({ title: '闪光灯设置失败', icon: 'none' })
      }
    })
  }
}

const toggleCamera = () => {
  devicePosition.value = devicePosition.value === 'back' ? 'front' : 'back'
  uni.showToast({ 
    title: devicePosition.value === 'back' ? '后置摄像头' : '前置摄像头', 
    icon: 'none' 
  })
}

// 使用微信相机模式
const useWxCameraMode = () => {
  showCameraChoice.value = false
  checkCameraPermission()
}

// 打开系统相机
const openSystemCamera = () => {
  showCameraChoice.value = false
  uni.chooseImage({
    count: 1,
    sourceType: ['camera'],
    sizeType: ['original', 'compressed'],
    success: (res) => {
      const tempPath = res.tempFilePaths[0]
      // 保存到最近照片
      saveRecentPhoto(tempPath)
      // 跳转到编辑页面
      uni.navigateTo({
        url: `/pages/collect/edit?image=${encodeURIComponent(tempPath)}`
      })
    },
    fail: (err) => {
      console.log('取消拍照或失败', err)
    }
  })
}

// 保存最近照片
const saveRecentPhoto = (path: string) => {
  let photos = uni.getStorageSync('recentPhotos') || []
  photos.unshift(path)
  photos = photos.slice(0, 10) // 只保留最近10张
  uni.setStorageSync('recentPhotos', photos)
  lastPhoto.value = path
}

// 获取位置
const getLocation = () => {
  uni.authorize({
    scope: 'scope.userLocation',
    success: () => {
      // 使用逆地理编码获取位置名称
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          // 这里可以调用逆地理编码API获取地址
          currentLocation.value = '君尚·金融街...'
        }
      })
    }
  })
}

// 获取最近照片
const getLastPhoto = () => {
  // 从本地存储获取最近的照片和采集
  const photos = uni.getStorageSync('recentPhotos') || []
  const collections = uni.getStorageSync('recentCollections') || []
  
  if (photos.length > 0) {
    lastPhoto.value = photos[0]
  }
  if (collections.length > 0) {
    lastCollection.value = collections[0]
  }
}

// 拍照
const takePhoto = () => {
  // 如果使用微信相机组件
  if (cameraReady.value && useWxCamera.value) {
    const ctx = uni.createCameraContext()
    ctx.takePhoto({
      quality: 'high',
      success: (res) => {
        const tempPath = res.tempImagePath
        saveRecentPhoto(tempPath)
        uni.navigateTo({
          url: `/pages/collect/edit?image=${encodeURIComponent(tempPath)}`
        })
      },
      fail: (err) => {
        console.error('拍照失败', err)
        // 微信相机失败，尝试系统相机
        uni.showModal({
          title: '拍照失败',
          content: '是否使用系统相机重试？',
          success: (res) => {
            if (res.confirm) {
              openSystemCamera()
            }
          }
        })
      }
    })
  } else {
    // 没有微信相机权限，使用系统相机
    openSystemCamera()
  }
}

// 从相册选择
const chooseFromAlbum = () => {
  showCameraChoice.value = false
  uni.chooseImage({
    count: 1,
    sourceType: ['album'],
    sizeType: ['original', 'compressed'],
    success: (res) => {
      const tempPath = res.tempFilePaths[0]
      saveRecentPhoto(tempPath)
      uni.navigateTo({
        url: `/pages/collect/edit?image=${encodeURIComponent(tempPath)}`
      })
    }
  })
}

// 相机错误
const onCameraError = (e: any) => {
  console.error('相机错误', e)
  cameraReady.value = false
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 前往设置
const goToSettings = () => {
  uni.navigateTo({
    url: '/pages/collect/settings'
  })
}
</script>

<style lang="scss" scoped>
.camera-page {
  width: 100vw;
  height: 100vh;
  background: #1D1D1F;
  position: relative;
}

.camera-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.camera-view {
  width: 100%;
  height: 100%;
}

// 取景框
.viewfinder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 500rpx;
  height: 500rpx;
}

.corner {
  position: absolute;
  width: 60rpx;
  height: 60rpx;
  border-color: rgba(255, 255, 255, 0.8);
  border-style: solid;
  border-width: 0;
}

.top-left {
  top: 0;
  left: 0;
  border-top-width: 6rpx;
  border-left-width: 6rpx;
  border-radius: 16rpx 0 0 0;
}

.top-right {
  top: 0;
  right: 0;
  border-top-width: 6rpx;
  border-right-width: 6rpx;
  border-radius: 0 16rpx 0 0;
}

.bottom-left {
  bottom: 0;
  left: 0;
  border-bottom-width: 6rpx;
  border-left-width: 6rpx;
  border-radius: 0 0 0 16rpx;
}

.bottom-right {
  bottom: 0;
  right: 0;
  border-bottom-width: 6rpx;
  border-right-width: 6rpx;
  border-radius: 0 0 16rpx 0;
}

// 顶部操作栏
.top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  z-index: 10;
}

.close-btn,
.settings-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 48rpx;
  color: #FFFFFF;
  font-weight: 300;
}

.settings-icon {
  font-size: 40rpx;
  color: #FFFFFF;
}

.icon-gear {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid #FFFFFF;
  border-radius: 50%;
  position: relative;
}

.icon-gear::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12rpx;
  height: 12rpx;
  border: 3rpx solid #FFFFFF;
  border-radius: 50%;
}

// 位置信息
.location-bar {
  position: absolute;
  bottom: 280rpx;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.location-tag {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  border-radius: 100rpx;
  padding: 12rpx 24rpx;
}

.location-icon {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
  margin-right: 8rpx;
}

.location-text {
  font-size: 26rpx;
  color: #FFFFFF;
  max-width: 300rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 底部操作栏
.bottom-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40rpx 80rpx 80rpx;
  z-index: 10;
}

// 相册按钮
.album-btn {
  width: 100rpx;
  height: 100rpx;
  border-radius: 20rpx;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.1);
  border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.album-preview {
  width: 100%;
  height: 100%;
}

// 拍照按钮
.capture-btn {
  width: 160rpx;
  height: 160rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.capture-outer {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  border: 8rpx solid rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.1s ease;

  &:active {
    transform: scale(0.92);
  }
}

.capture-inner {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  background: #FFFFFF;
}

// 翻转相机按钮
.flip-btn {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 相机占位区域
.camera-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.8);
}

.placeholder-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
}

.placeholder-text {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 48rpx;
}

.placeholder-btn {
  padding: 24rpx 64rpx;
  background: #007AFF;
  border-radius: 100rpx;
  margin-bottom: 24rpx;

  &.secondary {
    background: rgba(255, 255, 255, 0.2);
  }

  .btn-text {
    font-size: 30rpx;
    color: #FFFFFF;
  }
}

// 顶部工具栏
.top-tools {
  display: flex;
  gap: 24rpx;
}

.tool-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.tool-icon {
  font-size: 32rpx;
}

.icon-flash {
  position: relative;
  width: 32rpx;
  height: 40rpx;
}

.flash-bolt {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 12rpx solid transparent;
  border-right: 12rpx solid transparent;
  border-top: 20rpx solid #FFFFFF;
}

.flash-bolt::after {
  content: '';
  position: absolute;
  top: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 8rpx solid transparent;
  border-right: 8rpx solid transparent;
  border-top: 18rpx solid #FFFFFF;
}

.flash-slash {
  position: absolute;
  top: -4rpx;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 4rpx;
  height: 48rpx;
  background: #FFFFFF;
  border-radius: 2rpx;
}

.tool-btn.active .flash-bolt {
  border-top-color: #FFD60A;
}

.tool-btn.active .flash-bolt::after {
  border-top-color: #FFD60A;
}

.icon-flip {
  width: 36rpx;
  height: 36rpx;
  border: 4rpx solid #FFFFFF;
  border-radius: 50%;
  position: relative;
}

.icon-flip::after {
  content: '';
  position: absolute;
  top: -8rpx;
  right: -6rpx;
  width: 0;
  height: 0;
  border-left: 8rpx solid transparent;
  border-right: 8rpx solid transparent;
  border-bottom: 10rpx solid #FFFFFF;
  transform: rotate(45deg);
}

.flip-btn .icon-flip {
  width: 48rpx;
  height: 48rpx;
  border-width: 5rpx;
}

.flip-btn .icon-flip::after {
  top: -10rpx;
  right: -8rpx;
  border-left-width: 10rpx;
  border-right-width: 10rpx;
  border-bottom-width: 12rpx;
}

// 拍照方式选择弹窗
.action-sheet-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 1000;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.action-sheet {
  width: 100%;
  background: #F5F5F7;
  border-radius: 24rpx 24rpx 0 0;
  padding: 16rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.3s ease;

  .action-sheet-mask.show & {
    transform: translateY(0);
  }
}

.action-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: #FFFFFF;
  margin-bottom: 2rpx;

  &:first-child {
    border-radius: 16rpx 16rpx 0 0;
  }

  &:nth-child(3) {
    border-radius: 0 0 16rpx 16rpx;
    margin-bottom: 16rpx;
  }
}

.action-text {
  font-size: 32rpx;
  color: #1D1D1F;
}

.action-cancel {
  padding: 32rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  text-align: center;
}

.cancel-text {
  font-size: 32rpx;
  color: #007AFF;
  font-weight: 500;
}
</style>
