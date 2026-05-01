<template>
  <view class="planning-detail">
    <!-- 自定义导航栏 -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-back" @tap="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">规划详情</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 头部信息 -->
    <view class="route-header">
      <view class="header-bg" :style="{ backgroundImage: `url(${headerImage})` }">
        <view class="header-overlay">
          <view class="route-title">{{ route?.name || '智能规划路线' }}</view>
          <view class="route-meta">
            <text class="meta-item">
              <text class="iconfont icon-location"></text>
              {{ route?.startCity }} → {{ route?.endCity }}
            </text>
            <text class="meta-item">
              <text class="iconfont icon-calendar"></text>
              {{ route?.days }}天行程
            </text>
          </view>
        </view>
      </view>
    </view>

    <!-- 路线概览 -->
    <view class="route-summary" v-if="route?.summary">
      <view class="summary-item">
        <view class="summary-value">{{ route.summary.totalSpots }}</view>
        <view class="summary-label">景点数</view>
      </view>
      <view class="summary-item">
        <view class="summary-value">{{ route.summary.totalDistance }}</view>
        <view class="summary-label">总里程</view>
      </view>
      <view class="summary-item">
        <view class="summary-value">{{ route.summary.estimatedCost }}</view>
        <view class="summary-label">预估花费</view>
      </view>
      <view class="summary-item">
        <view class="summary-value">{{ route.summary.bestSeason }}</view>
        <view class="summary-label">最佳季节</view>
      </view>
    </view>

    <!-- 日程切换标签 -->
    <view class="day-tabs">
      <scroll-view scroll-x class="tabs-scroll">
        <view class="tabs-container">
          <view 
            v-for="day in route?.days" 
            :key="day"
            class="tab-item"
            :class="{ active: currentDay === day }"
            @tap="switchDay(day)"
          >
            <text class="day-num">Day {{ day }}</text>
            <text class="day-date" v-if="route?.schedule?.[day-1]">{{ route.schedule[day-1].date }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 当日行程 -->
    <view class="day-schedule" v-if="currentSchedule">
      <!-- 当日概览 -->
      <view class="day-overview">
        <view class="overview-item">
          <text class="iconfont icon-time"></text>
          <text>预计游玩 {{ currentSchedule.totalDuration }}</text>
        </view>
        <view class="overview-item">
          <text class="iconfont icon-route"></text>
          <text>行程 {{ currentSchedule.totalDistance }}</text>
        </view>
      </view>

      <!-- 景点时间线 -->
      <view class="spots-timeline">
        <view 
          v-for="(spot, index) in currentSchedule.spots" 
          :key="spot.id"
          class="timeline-item"
        >
          <view class="timeline-left">
            <view class="time-start">{{ spot.startTime }}</view>
            <view class="time-end">{{ spot.endTime }}</view>
          </view>
          <view class="timeline-line">
            <view class="dot" :class="{ first: index === 0, last: index === currentSchedule.spots.length - 1 }"></view>
            <view class="line" v-if="index < currentSchedule.spots.length - 1"></view>
          </view>
          <view class="timeline-content" @tap="showSpotDetail(spot)">
            <view class="spot-card">
              <image 
                class="spot-image" 
                :src="spot.spotInfo?.image || '/static/images/default.jpg'"
                mode="aspectFill"
              />
              <view class="spot-info">
                <view class="spot-name">{{ spot.name }}</view>
                <view class="spot-meta">
                  <text class="duration">
                    <text class="iconfont icon-time"></text>
                    {{ spot.duration }}
                  </text>
                  <text class="rating" v-if="spot.spotInfo?.rating">
                    <text class="iconfont icon-star"></text>
                    {{ spot.spotInfo.rating }}
                  </text>
                </view>
                <view class="spot-category" v-if="spot.spotInfo?.category">
                  {{ spot.spotInfo.category }}
                </view>
              </view>
              <view class="spot-arrow">
                <text class="iconfont icon-arrow-right"></text>
              </view>
            </view>
          </view>
        </view>

        <!-- 午餐推荐 -->
        <view class="timeline-item meal-item" v-if="currentSchedule.meals?.lunch">
          <view class="timeline-left">
            <view class="time-start">12:00</view>
            <view class="time-end">13:30</view>
          </view>
          <view class="timeline-line">
            <view class="dot meal-dot"></view>
            <view class="line"></view>
          </view>
          <view class="timeline-content">
            <view class="meal-card">
              <view class="meal-icon">🍜</view>
              <view class="meal-info">
                <view class="meal-type">午餐推荐</view>
                <view class="meal-name">{{ currentSchedule.meals.lunch.name }}</view>
                <view class="meal-address">{{ currentSchedule.meals.lunch.address }}</view>
              </view>
            </view>
          </view>
        </view>

        <!-- 晚餐推荐 -->
        <view class="timeline-item meal-item" v-if="currentSchedule.meals?.dinner">
          <view class="timeline-left">
            <view class="time-start">18:00</view>
            <view class="time-end">19:30</view>
          </view>
          <view class="timeline-line">
            <view class="dot meal-dot"></view>
            <view class="line" v-if="currentSchedule.hotel"></view>
          </view>
          <view class="timeline-content">
            <view class="meal-card">
              <view class="meal-icon">🍲</view>
              <view class="meal-info">
                <view class="meal-type">晚餐推荐</view>
                <view class="meal-name">{{ currentSchedule.meals.dinner.name }}</view>
                <view class="meal-address">{{ currentSchedule.meals.dinner.address }}</view>
              </view>
            </view>
          </view>
        </view>

        <!-- 住宿推荐 -->
        <view class="timeline-item hotel-item" v-if="currentSchedule.hotel">
          <view class="timeline-left">
            <view class="time-start">20:00</view>
          </view>
          <view class="timeline-line">
            <view class="dot hotel-dot"></view>
          </view>
          <view class="timeline-content">
            <view class="hotel-card">
              <view class="hotel-icon">🏨</view>
              <view class="hotel-info">
                <view class="hotel-type">住宿推荐</view>
                <view class="hotel-name">{{ currentSchedule.hotel.name }}</view>
                <view class="hotel-price">{{ currentSchedule.hotel.price }}/晚</view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-btn secondary" @tap="regenerateRoute">
        <text class="iconfont icon-refresh"></text>
        <text>重新生成</text>
      </view>
      <view class="action-btn primary" @tap="saveAndStart">
        <text class="iconfont icon-save"></text>
        <text>保存并开始</text>
      </view>
    </view>

    <!-- 景点详情弹窗 -->
    <view class="spot-popup" v-if="showPopup" @tap="showPopup = false">
      <view class="popup-content" @tap.stop>
        <image 
          class="popup-image" 
          :src="selectedSpot?.spotInfo?.image || '/static/images/default.jpg'"
          mode="aspectFill"
        />
        <view class="popup-info">
          <view class="popup-title">{{ selectedSpot?.name }}</view>
          <view class="popup-rating" v-if="selectedSpot?.spotInfo?.rating">
            <text class="iconfont icon-star"></text>
            <text>{{ selectedSpot.spotInfo.rating }}分</text>
            <text class="category">{{ selectedSpot.spotInfo.category }}</text>
          </view>
          <view class="popup-desc" v-if="selectedSpot?.spotInfo?.description">
            {{ selectedSpot.spotInfo.description }}
          </view>
          <view class="popup-detail">
            <view class="detail-item" v-if="selectedSpot?.spotInfo?.openTime">
              <text class="label">开放时间</text>
              <text class="value">{{ selectedSpot.spotInfo.openTime }}</text>
            </view>
            <view class="detail-item" v-if="selectedSpot?.spotInfo?.address">
              <text class="label">地址</text>
              <text class="value">{{ selectedSpot.spotInfo.address }}</text>
            </view>
            <view class="detail-item" v-if="selectedSpot?.spotInfo?.price">
              <text class="label">门票</text>
              <text class="value">{{ selectedSpot.spotInfo.price }}</text>
            </view>
            <view class="detail-item tips" v-if="selectedSpot?.spotInfo?.tips">
              <text class="label">小贴士</text>
              <text class="value">{{ selectedSpot.spotInfo.tips }}</text>
            </view>
          </view>
          <view class="popup-actions">
            <view class="popup-btn" @tap="navigateToSpot">
              <text class="iconfont icon-navigation"></text>
              <text>导航</text>
            </view>
            <view class="popup-btn" @tap="showPopup = false">
              <text class="iconfont icon-close"></text>
              <text>关闭</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import type { AIRoute, RouteSpot, DaySchedule } from '@/api/modules/planning'
import { saveRoute } from '@/api/modules/planning'

// 状态栏高度
const statusBarHeight = ref(44)

// 路线数据
const route = ref<AIRoute | null>(null)
const currentDay = ref(1)
const showPopup = ref(false)
const selectedSpot = ref<RouteSpot | null>(null)

// 头部背景图
const headerImage = computed(() => {
  const firstSpot = route.value?.spots?.[0]
  return firstSpot?.spotInfo?.image || 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800'
})

// 当前日程
const currentSchedule = computed<DaySchedule | null>(() => {
  if (!route.value?.schedule) return null
  return route.value.schedule[currentDay.value - 1] || null
})

onLoad((options) => {
  // 从页面参数或eventChannel获取路线数据
  const eventChannel = (getCurrentPages().slice(-1)[0] as any).getOpenerEventChannel?.()
  
  if (eventChannel) {
    eventChannel.on('routeData', (data: AIRoute) => {
      route.value = data
    })
  }
  
  // 尝试从本地存储获取
  const routeData = uni.getStorageSync('currentRoute')
  if (routeData) {
    route.value = JSON.parse(routeData)
    uni.removeStorageSync('currentRoute')
  }
})

onMounted(() => {
  // 获取状态栏高度
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  
  // 如果没有数据，模拟一个
  if (!route.value) {
    // 延迟获取，等待eventChannel
    setTimeout(() => {
      if (!route.value) {
        uni.showToast({
          title: '暂无路线数据',
          icon: 'none'
        })
      }
    }, 500)
  }
})

// 返回上一页
function goBack() {
  uni.navigateBack()
}

// 切换日期
function switchDay(day: number) {
  currentDay.value = day
}

// 显示景点详情
function showSpotDetail(spot: RouteSpot) {
  selectedSpot.value = spot
  showPopup.value = true
}

// 导航到景点
function navigateToSpot() {
  if (!selectedSpot.value?.spotInfo) return
  
  const { latitude, longitude, name, address } = selectedSpot.value.spotInfo
  
  uni.openLocation({
    latitude,
    longitude,
    name,
    address,
    fail: () => {
      uni.showToast({
        title: '打开地图失败',
        icon: 'none'
      })
    }
  })
}

// 重新生成路线
function regenerateRoute() {
  uni.showModal({
    title: '提示',
    content: '确定要重新生成路线吗？当前路线将被替换。',
    success: (res) => {
      if (res.confirm) {
        uni.navigateBack()
      }
    }
  })
}

// 保存并开始
async function saveAndStart() {
  if (!route.value) return
  
  uni.showLoading({ title: '保存中...' })
  
  try {
    await saveRoute(route.value)
    uni.hideLoading()
    
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }, 1500)
  } catch (error) {
    uni.hideLoading()
    uni.showToast({
      title: '保存失败',
      icon: 'none'
    })
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #007AFF;
$primary-light: #E3F2FD;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;

.planning-detail {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

// 导航栏
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  background: $bg-color;
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

.route-header {

  .header-bg {
   
    height: 400rpx;
    background-size: cover;
    background-position: center;
    position: relative;
  }
  
  .header-overlay {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    padding: 60rpx 32rpx 80rpx;
  
    background: linear-gradient(transparent, rgba(0,0,0,0.7));
    color: #fff;
  }
  
  .route-title {
    font-size: 44rpx;
    font-weight: bold;
    margin-bottom: 16rpx;
  }
  
  .route-meta {
    display: flex;
    gap: 30rpx;
    
    .meta-item {
      font-size: 26rpx;
      opacity: 0.9;
      
      .iconfont {
        margin-right: 8rpx;
      }
    }
  }
}

.route-summary {
  display: flex;
  background: $card-bg;
  padding: 36rpx 32rpx;
  margin: -32rpx 24rpx 28rpx;
  border-radius: 32rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.12);
  position: relative;
  z-index: 10;
  
  .summary-item {
    flex: 1;
    text-align: center;
    
    &:not(:last-child) {
      border-right: 1rpx solid #F0F0F5;
    }
  }
  
  .summary-value {
    font-size: 36rpx;
    font-weight: bold;
    color: $primary-color;
    margin-bottom: 8rpx;
  }
  
  .summary-label {
    font-size: 24rpx;
    color: $text-secondary;
  }
}

.day-tabs {
  background: $card-bg;
  padding: 24rpx 0;
  margin-bottom: 24rpx;
  
  .tabs-scroll {
    white-space: nowrap;
  }
  
  .tabs-container {
    display: inline-flex;
    padding: 0 24rpx;
    gap: 16rpx;
  }
  
  .tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 32rpx;
    border-radius: 20rpx;
    background: $bg-color;
    transition: all 0.2s ease;
    
    &.active {
      background: linear-gradient(135deg, $primary-color, #5AC8FA);
      box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.25);
      
      .day-num, .day-date {
        color: #fff;
      }
    }
    
    .day-num {
      font-size: 28rpx;
      font-weight: 600;
      color: $text-primary;
    }
    
    .day-date {
      font-size: 22rpx;
      color: $text-secondary;
      margin-top: 4rpx;
    }
  }
}

.day-schedule {
  padding: 0 24rpx;
}

.day-overview {
  display: flex;
  gap: 32rpx;
  padding: 24rpx 32rpx;
  background: $card-bg;
  border-radius: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  
  .overview-item {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 26rpx;
    color: $text-secondary;
    
    .iconfont {
      color: $primary-color;
    }
  }
}

.spots-timeline {
  padding: 24rpx;
  background: $card-bg;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.timeline-item {
  display: flex;
  padding-bottom: 30rpx;
  
  &:last-child {
    padding-bottom: 0;
  }
}

.timeline-left {
  width: 100rpx;
  text-align: right;
  padding-right: 20rpx;
  
  .time-start {
    font-size: 26rpx;
    font-weight: bold;
    color: #333;
  }
  
  .time-end {
    font-size: 22rpx;
    color: #999;
    margin-top: 4rpx;
  }
}

.timeline-line {
  width: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .dot {
    width: 20rpx;
    height: 20rpx;
    border-radius: 50%;
    background: #007AFF;
    border: 4rpx solid #fff;
    box-shadow: 0 0 0 4rpx rgba(0, 122, 255, 0.2);
    flex-shrink: 0;
    
    &.first {
      background: #4CAF50;
      box-shadow: 0 0 0 4rpx rgba(76, 175, 80, 0.2);
    }
    
    &.last {
      background: #FF5722;
      box-shadow: 0 0 0 4rpx rgba(255, 87, 34, 0.2);
    }
    
    &.meal-dot {
      background: #FF9800;
      box-shadow: 0 0 0 4rpx rgba(255, 152, 0, 0.2);
    }
    
    &.hotel-dot {
      background: #9C27B0;
      box-shadow: 0 0 0 4rpx rgba(156, 39, 176, 0.2);
    }
  }
  
  .line {
    flex: 1;
    width: 4rpx;
    background: #e0e0e0;
    margin: 8rpx 0;
  }
}

.timeline-content {
  flex: 1;
  padding-left: 20rpx;
}

.spot-card {
  display: flex;
  background: #f9f9f9;
  border-radius: 16rpx;
  overflow: hidden;
  
  .spot-image {
    width: 160rpx;
    height: 140rpx;
    flex-shrink: 0;
  }
  
  .spot-info {
    flex: 1;
    padding: 16rpx;
    
    .spot-name {
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 8rpx;
    }
    
    .spot-meta {
      display: flex;
      gap: 20rpx;
      font-size: 24rpx;
      color: #666;
      margin-bottom: 8rpx;
      
      .iconfont {
        margin-right: 4rpx;
      }
      
      .rating {
        color: #FF9800;
      }
    }
    
    .spot-category {
      display: inline-block;
      font-size: 22rpx;
      color: #007AFF;
      background: rgba(0, 122, 255, 0.1);
      padding: 4rpx 12rpx;
      border-radius: 8rpx;
    }
  }
  
  .spot-arrow {
    display: flex;
    align-items: center;
    padding: 0 16rpx;
    color: #ccc;
  }
}

.meal-card, .hotel-card {
  display: flex;
  background: #f9f9f9;
  border-radius: 16rpx;
  padding: 20rpx;
  
  .meal-icon, .hotel-icon {
    font-size: 48rpx;
    margin-right: 20rpx;
  }
  
  .meal-info, .hotel-info {
    .meal-type, .hotel-type {
      font-size: 24rpx;
      color: #999;
    }
    
    .meal-name, .hotel-name {
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      margin: 4rpx 0;
    }
    
    .meal-address {
      font-size: 24rpx;
      color: #666;
    }
    
    .hotel-price {
      font-size: 26rpx;
      color: #FF5722;
    }
  }
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: $card-bg;
  box-shadow: 0 -4rpx 24rpx rgba(0, 0, 0, 0.08);
  
  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12rpx;
    height: 96rpx;
    border-radius: 48rpx;
    font-size: 30rpx;
    font-weight: 500;
    transition: all 0.2s ease;
    
    &.secondary {
      background: $bg-color;
      color: $text-primary;
    }
    
    &.primary {
      background: linear-gradient(135deg, $primary-color, #5AC8FA);
      color: #fff;
      box-shadow: 0 8rpx 24rpx rgba(0, 122, 255, 0.3);
    }
    
    &:active {
      transform: scale(0.97);
    }
  }
}

.spot-popup {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
  
  .popup-content {
    width: 100%;
    max-height: 80vh;
    background: #fff;
    border-radius: 30rpx 30rpx 0 0;
    overflow: hidden;
  }
  
  .popup-image {
    width: 100%;
    height: 360rpx;
  }
  
  .popup-info {
    padding: 30rpx;
    
    .popup-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 16rpx;
    }
    
    .popup-rating {
      display: flex;
      align-items: center;
      gap: 10rpx;
      font-size: 28rpx;
      color: #FF9800;
      margin-bottom: 20rpx;
      
      .category {
        color: #999;
        margin-left: 20rpx;
      }
    }
    
    .popup-desc {
      font-size: 28rpx;
      color: #666;
      line-height: 1.6;
      margin-bottom: 20rpx;
    }
    
    .popup-detail {
      .detail-item {
        display: flex;
        padding: 16rpx 0;
        border-bottom: 1rpx solid #eee;
        
        &.tips {
          flex-direction: column;
          
          .value {
            margin-top: 8rpx;
          }
        }
        
        .label {
          width: 160rpx;
          font-size: 26rpx;
          color: #999;
          flex-shrink: 0;
        }
        
        .value {
          flex: 1;
          font-size: 26rpx;
          color: #333;
        }
      }
    }
    
    .popup-actions {
      display: flex;
      gap: 20rpx;
      margin-top: 30rpx;
      
      .popup-btn {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10rpx;
        height: 80rpx;
        border-radius: 40rpx;
        font-size: 28rpx;
        background: #f5f5f5;
        color: #333;
        
        &:first-child {
          background: linear-gradient(135deg, #007AFF, #5AC8FA);
          color: #fff;
        }
      }
    }
  }
}
</style>
