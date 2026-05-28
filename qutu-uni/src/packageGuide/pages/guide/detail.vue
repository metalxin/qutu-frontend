<template>
  <view class="page">
    <!-- 顶部导航栏（悬浮） -->
    <view class="navbar" :class="{ 'navbar-bg': scrollTop > 100 }">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#333333" />
      </view>
    </view>

    <scroll-view 
      class="content-scroll" 
      scroll-y 
      :show-scrollbar="false"
      @scroll="onScroll"
    >
      <!-- 封面图 -->
      <view class="cover-section">
        <image class="cover-image" :src="guideDetail.cover" mode="aspectFill" />
      </view>

      <!-- 基本信息 -->
      <view class="info-section">
        <text class="guide-title">{{ guideDetail.title }}</text>
        <text class="guide-desc">{{ guideDetail.description }}</text>

        <!-- 统计信息 -->
        <view class="stats-row" v-if="guideDetail.city || guideDetail.days || guideDetail.tag">
          <text class="stat-tag" v-if="guideDetail.city">{{ guideDetail.city }}</text>
          <text class="stat-tag" v-if="guideDetail.days">{{ guideDetail.days }}</text>
          <text class="stat-tag" v-if="guideDetail.tag">{{ guideDetail.tag }}</text>
          <text class="stat-item">{{ guideDetail.likes }} 赞</text>
          <text class="stat-item">{{ guideDetail.views }} 浏览</text>
        </view>

        <!-- 天数切换（动态生成） -->
        <scroll-view class="day-tabs-scroll" scroll-x :show-scrollbar="false">
          <view class="day-tabs">
            <view 
              class="day-tab" 
              :class="{ active: activeDay === 'overview' }"
              @click="activeDay = 'overview'"
            >
              <text class="tab-text">总览</text>
            </view>
            <view 
              class="day-tab" 
              :class="{ active: activeDay === `day${day.dayIndex}` }"
              v-for="day in guideDetail.itinerary" 
              :key="day.dayIndex"
              @click="activeDay = `day${day.dayIndex}`"
            >
              <text class="tab-text">第{{ day.dayIndex }}天</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 攻略内容 -->
      <view class="content-section">
        <!-- 行程概述 -->
        <view class="content-block" v-if="activeDay === 'overview'">
          <view class="block-item" v-if="guideDetail.overview.summary">
            <SFIcon name="doc" :size="32" color="#86868B" />
            <text class="item-label">行程概述：</text>
            <text class="item-text">{{ guideDetail.overview.summary }}</text>
          </view>
          <view class="block-item" v-if="guideDetail.overview.transport">
            <SFIcon name="route" :size="32" color="#86868B" />
            <text class="item-label">交通：</text>
            <text class="item-text">{{ guideDetail.overview.transport }}</text>
          </view>
          <view class="block-item" v-if="!guideDetail.overview.summary && !guideDetail.overview.transport">
            <text class="item-text">暂无概述信息</text>
          </view>
        </view>

        <!-- 行程概览 -->
        <view class="content-block" v-if="activeDay === 'overview'">
          <view class="block-title">
            <SFIcon name="calendar" :size="32" color="#86868B" />
            <text class="title-text">行程概览：</text>
          </view>
          <view class="schedule-list">
            <view class="schedule-item" v-for="s in guideDetail.schedule" :key="s.day">
              <text class="schedule-day">{{ s.day }}</text>
              <text class="schedule-route">{{ s.route || '暂无路线' }}</text>
            </view>
            <view v-if="!guideDetail.schedule.length" class="schedule-item">
              <text class="schedule-route">暂无行程数据</text>
            </view>
          </view>
        </view>

        <!-- 各天详细（动态渲染） -->
        <view class="content-block" v-for="day in guideDetail.itinerary" :key="day.dayIndex" v-show="activeDay === `day${day.dayIndex}`">
          <view class="day-header">
            <text class="day-title">Day{{ day.dayIndex }}</text>
            <text class="day-subtitle" v-if="day.title">{{ day.title }}</text>
          </view>
          <view class="day-summary" v-if="day.summary">{{ day.summary }}</view>

          <!-- 条目卡片 -->
          <view class="spot-card" v-for="(item, idx) in day.items" :key="idx">
            <view class="spot-header">
              <SFIcon name="location" :size="28" color="#86868B" />
              <text class="spot-name">{{ item.title || '未命名' }}</text>
              <text class="spot-type-tag" v-if="item.type === 'spot'">景点</text>
            </view>
            <view class="spot-info">
              <view class="info-row" v-if="item.type === 'spot' && item.spotId">
                <SFIcon name="link" :size="24" color="#86868B" />
                <text class="info-label">景点ID：</text>
                <text class="info-value">{{ item.spotId }}</text>
              </view>
              <view class="info-row highlight" v-if="item.type === 'text' && item.content">
                <SFIcon name="check-circle" :size="24" color="#34C759" />
                <text class="info-value">{{ item.content }}</text>
              </view>
              <view class="info-row" v-if="item.type === 'spot' && item.content">
                <SFIcon name="doc" :size="24" color="#86868B" />
                <text class="info-value">{{ item.content }}</text>
              </view>
            </view>
          </view>

          <view v-if="!day.items || !day.items.length" class="empty-tip">
            <text class="empty-text">暂无当天行程条目</text>
          </view>
        </view>
      </view>

      <!-- 底部安全区 -->
      <view class="safe-bottom"></view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-btn collect" @click="toggleCollect">
        <SFIcon name="bookmark" :size="32" :color="isCollected ? '#34C759' : '#86868B'" />
        <text class="btn-text">{{ isCollected ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="action-btn share" @click="showSharePopup = true">
        <SFIcon name="share" :size="32" color="#86868B" />
        <text class="btn-text">分享</text>
      </view>
      <view class="action-btn share-code" @click="openShareCodePopup">
        <text class="btn-icon">🔗</text>
        <text class="btn-text">口令</text>
      </view>
      <view class="action-btn primary" @click="showTripSheet = true">
        <text class="btn-text">一键生成行程</text>
      </view>
    </view>

    <!-- 行程配置弹窗遮罩 -->
    <view class="trip-overlay" :class="{ show: showTripSheet }" @tap="showTripSheet = false"></view>

    <!-- 行程配置弹窗 -->
    <view class="trip-sheet" :class="{ show: showTripSheet }">
      <view class="trip-sheet-content" @tap.stop>
        <!-- 拖拽指示条 -->
        <view class="sheet-handle">
          <view class="handle-bar"></view>
        </view>

        <!-- 标题区域 -->
        <view class="sheet-header">
          <view class="header-info">
            <text class="header-title">一键生成行程</text>
            <text class="header-subtitle">基于攻略: {{ guideDetail.title }}</text>
          </view>
          <view class="header-close" @tap="showTripSheet = false">
            <text class="close-icon">×</text>
          </view>
        </view>

        <!-- 天数选择 -->
        <view class="config-section">
          <view class="section-label">
            <text class="label-icon">📅</text>
            <text class="label-text">行程天数</text>
            <text class="label-value">{{ tripDays }}天</text>
          </view>
          <view class="days-grid">
            <view 
              v-for="day in 7" 
              :key="day"
              class="day-item"
              :class="{ active: tripDays === day }"
              @tap="tripDays = day"
            >
              <text class="day-text">{{ day }}</text>
            </view>
          </view>
        </view>

        <!-- 偏好选择 -->
        <view class="config-section">
          <view class="section-label">
            <text class="label-icon">🎯</text>
            <text class="label-text">规划偏好</text>
          </view>
          <view class="preference-list">
            <view 
              v-for="pref in preferenceOptions" 
              :key="pref.id"
              class="preference-item"
              :class="{ active: tripPreference === pref.id }"
              @tap="tripPreference = pref.id"
            >
              <text class="pref-name">{{ pref.name }}</text>
              <text class="pref-desc">{{ pref.desc }}</text>
            </view>
          </view>
        </view>

        <!-- 交通方式 -->
        <view class="config-section">
          <view class="section-label">
            <text class="label-icon">🚗</text>
            <text class="label-text">交通方式</text>
          </view>
          <view class="transport-list">
            <view 
              v-for="transport in transportModes" 
              :key="transport.id"
              class="transport-item"
              :class="{ active: tripTransport === transport.id }"
              @tap="tripTransport = transport.id"
            >
              <text class="transport-icon">{{ transport.icon }}</text>
              <text class="transport-name">{{ transport.name }}</text>
            </view>
          </view>
        </view>

        <!-- 生成按钮 -->
        <view class="generate-area">
          <view class="generate-btn" :class="{ loading: isGenerating }" @tap="generateTrip">
            <text class="generate-icon" v-if="!isGenerating">✨</text>
            <view class="loading-spinner" v-else></view>
            <text class="generate-text">{{ isGenerating ? 'AI正在规划中...' : `AI智能规划 ${tripDays}天行程` }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 分享弹窗遮罩 -->
    <view class="share-overlay" :class="{ show: showSharePopup }" @tap="showSharePopup = false"></view>
    
    <!-- 分享弹窗 -->
    <view class="share-popup" :class="{ show: showSharePopup }">
      <!-- 弹窗头部 -->
      <view class="share-header">
        <text class="share-title">分享这篇攻略</text>
        <view class="share-close" @tap="showSharePopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 攻略信息卡片 -->
      <view class="share-card">
        <image class="share-cover" :src="guideDetail.cover" mode="aspectFill" />
        <view class="share-info">
          <text class="share-name">{{ guideDetail.title }}</text>
          <text class="share-desc">{{ guideDetail.description }}</text>
          <view class="share-meta">
            <text class="share-tag" v-if="guideDetail.city">{{ guideDetail.city }}</text>
            <text class="share-dot" v-if="guideDetail.city && guideDetail.days">•</text>
            <text class="share-days" v-if="guideDetail.days">{{ guideDetail.days }}</text>
          </view>
        </view>
      </view>

      <!-- 分享渠道 -->
      <view class="share-channels">
        <view class="channel-item">
          <button class="channel-btn" open-type="share">
            <view class="channel-icon wechat">
              <SFIcon name="wechat" :size="36" color="#FFFFFF" />
            </view>
          </button>
          <text class="channel-name">微信好友</text>
        </view>
        <view class="channel-item" @tap="savePoster">
          <view class="channel-icon poster">
            <SFIcon name="image" :size="36" color="#FFFFFF" />
          </view>
          <text class="channel-name">生成海报</text>
        </view>
        <view class="channel-item" @tap="copyLink">
          <view class="channel-icon link">
            <SFIcon name="copy" :size="36" color="#FFFFFF" />
          </view>
          <text class="channel-name">复制链接</text>
        </view>
        <view class="channel-item" @tap="shareToMoments">
          <view class="channel-icon moments">
            <SFIcon name="share" :size="36" color="#FFFFFF" />
          </view>
          <text class="channel-name">朋友圈</text>
        </view>
      </view>
    </view>

    <!-- 海报预览弹窗 -->
    <view class="poster-overlay" :class="{ show: showPosterPreview }" @tap="showPosterPreview = false"></view>
    <view class="poster-popup" :class="{ show: showPosterPreview }">
      <view class="poster-header" :style="posterHeaderStyle">
        <text class="poster-title">分享海报</text>
        <view class="poster-close" @tap="showPosterPreview = false">
          <text class="close-icon">×</text>
        </view>
      </view>
      <scroll-view scroll-y class="poster-scroll">
        <view class="poster-canvas-wrapper">
          <image v-if="posterImagePath" class="poster-preview-img" :src="posterImagePath" mode="widthFix" />
        </view>
      </scroll-view>
      <view class="poster-actions">
        <view class="poster-save-btn" @tap="savePosterToAlbum">
          <text class="poster-save-text">保存到相册</text>
        </view>
        <view class="poster-share-btn" @tap="sharePosterToMoments">
          <text class="poster-share-text">分享到朋友圈</text>
        </view>
      </view>
    </view>

    <!-- Canvas用于海报生成，生成时显示 -->
    <canvas
      v-if="isGeneratingPoster"
      id="guidePosterCanvas"
      type="2d"
      :style="{ position: 'fixed', left: '0', top: '0', width: '600px', height: '960px', opacity: '0', zIndex: -1, pointerEvents: 'none' }"
    ></canvas>

    <!-- 攻略口令分享弹窗 -->
    <view class="sc-overlay" :class="{ show: showShareCodePopup }" @tap="closeShareCodePopup"></view>
    <view class="sc-popup" :class="{ show: showShareCodePopup }">
      <view class="sc-header">
        <text class="sc-title">分享攻略口令</text>
        <view class="sc-close" @tap="closeShareCodePopup">
          <text class="close-icon">×</text>
        </view>
      </view>
      <view class="sc-body" v-if="!shareCodeValue">
        <text class="sc-desc">生成口令后，好友在首页点击「使用口令」输入即可查看你的攻略</text>
        <view class="sc-generate" @tap="doGenerateShareCode">
          <text class="sc-generate-text">{{ generatingCode ? '生成中...' : '生成口令' }}</text>
        </view>
      </view>
      <view class="sc-body" v-else>
        <text class="sc-desc">将以下口令分享给好友</text>
        <view class="sc-code-display">
          <text class="sc-code-text">{{ shareCodeValue }}</text>
        </view>
        <view class="sc-copy" @tap="copyShareCode">
          <text class="sc-copy-text">复制口令</text>
        </view>
        <text class="sc-hint">好友在首页「+ → 使用口令」中输入即可查看攻略</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, getCurrentInstance, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getGuideDetail, toggleGuideCollect, checkGuideFavorite, generateGuideShareCode, type GuideDetail as GuideDetailType, type GuideDay } from '@/api'
import { generateAIRoute, getPreferenceOptions, getTransportModes, type PreferenceOption, type TransportMode } from '../../api/planning'
import { generateGuidePoster, saveImageToAlbum, copyShareLink, trackShare } from '../../utils/share'

const instance = getCurrentInstance()

const menuButtonBottom = ref(0)

const posterHeaderStyle = computed(() => {
  const style: Record<string, string> = {}
  if (menuButtonBottom.value > 0) {
    style.paddingTop = (menuButtonBottom.value + 8) + 'px'
  } else {
    style.paddingTop = '32rpx'
  }
  return style
})

const scrollTop = ref(0)
const activeDay = ref('overview')
const loading = ref(false)

// 攻略ID
const guideId = ref(0)

// 攻略详情
const guideDetail = ref<GuideDetailType>({
  id: 0,
  title: '',
  description: '',
  cover: '',
  city: '',
  days: '',
  tag: '',
  likes: 0,
  views: 0,
  overview: { summary: '', transport: '' },
  schedule: [],
  itinerary: []
})

// 加载攻略详情
const loadGuideDetail = async () => {
  if (!guideId.value) return
  loading.value = true
  try {
    const res = await getGuideDetail(guideId.value)
    if (res) {
      guideDetail.value = {
        id: res.id || guideId.value,
        title: res.title || '',
        description: res.description || '',
        cover: res.cover || '',
        city: res.city || '',
        days: res.days || '',
        tag: res.tag || '',
        likes: res.likes || 0,
        views: res.views || 0,
        overview: res.overview || { summary: '', transport: '' },
        schedule: res.schedule || [],
        itinerary: res.itinerary || []
      }
      // 默认选中总览
      activeDay.value = 'overview'
    }
    // 查询收藏状态
    try {
      const collected = await checkGuideFavorite(guideId.value)
      isCollected.value = !!collected
    } catch {
      // 未登录或查询失败，默认未收藏
      isCollected.value = false
    }
  } catch (error) {
    console.error('加载攻略详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 滚动事件
const onScroll = (e: any) => {
  scrollTop.value = e.detail.scrollTop
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 收藏状态
const isCollected = ref(false)

// 口令分享
const showShareCodePopup = ref(false)
const shareCodeValue = ref('')
const generatingCode = ref(false)

const openShareCodePopup = () => {
  showShareCodePopup.value = true
}

const closeShareCodePopup = () => {
  showShareCodePopup.value = false
}

const doGenerateShareCode = async () => {
  if (generatingCode.value) return
  generatingCode.value = true
  try {
    const code = await generateGuideShareCode(guideId.value)
    shareCodeValue.value = code || ''
  } catch (error: any) {
    uni.showToast({ title: error.message || '生成失败', icon: 'none' })
  } finally {
    generatingCode.value = false
  }
}

const copyShareCode = () => {
  uni.setClipboardData({
    data: shareCodeValue.value,
    success: () => {
      uni.showToast({ title: '口令已复制', icon: 'success' })
    }
  })
}

// 分享弹窗
const showSharePopup = ref(false)

// 切换收藏
const toggleCollect = async () => {
  try {
    const res = await toggleGuideCollect(guideId.value, !isCollected.value)
    isCollected.value = res?.isCollected ?? !isCollected.value
    uni.showToast({
      title: isCollected.value ? '已收藏' : '已取消收藏',
      icon: 'none'
    })
  } catch (error) {
    uni.showToast({
      title: '操作失败，请重试',
      icon: 'none'
    })
  }
}

// 复制链接
const copyLink = () => {
  const path = `/packageGuide/pages/guide/detail?id=${guideDetail.value.id}`
  copyShareLink(path).then(() => {
    uni.showToast({ title: '链接已复制', icon: 'success' })
  }).catch(() => {
    uni.setClipboardData({
      data: path,
      success: () => uni.showToast({ title: '链接已复制', icon: 'success' })
    })
  })
  trackShare('copy_link', path)
}

// 海报相关状态
const posterImagePath = ref('')
const showPosterPreview = ref(false)
const isGeneratingPoster = ref(false)

// 生成海报
const savePoster = async () => {
  if (isGeneratingPoster.value) return
  isGeneratingPoster.value = true
  showSharePopup.value = false

  try {
    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 300))

    const path = await generateGuidePoster('guidePosterCanvas', {
      coverUrl: guideDetail.value.cover,
      title: guideDetail.value.title,
      description: guideDetail.value.description,
      city: guideDetail.value.city,
      days: guideDetail.value.days,
      tag: guideDetail.value.tag,
      likes: guideDetail.value.likes,
      views: guideDetail.value.views,
      guideId: guideDetail.value.id
    }, instance?.proxy)
    if (path) {
      posterImagePath.value = path
      showPosterPreview.value = true
    }
  } catch (e: any) {
    console.error('生成海报失败:', e)
    uni.showToast({ title: e?.message || '海报生成失败，请重试', icon: 'none', duration: 2000 })
  } finally {
    isGeneratingPoster.value = false
  }
}

// 保存海报到相册
const savePosterToAlbum = async () => {
  if (!posterImagePath.value) {
    uni.showToast({ title: '请先生成海报', icon: 'none' })
    return
  }
  try {
    await saveImageToAlbum(posterImagePath.value)
    uni.showToast({ title: '已保存到相册', icon: 'success' })
    trackShare('save_poster', `/packageGuide/pages/guide/detail?id=${guideDetail.value.id}`)
  } catch (e) {
    console.error('保存海报失败:', e)
  }
}

// 分享海报到朋友圈
const sharePosterToMoments = async () => {
  if (!posterImagePath.value) {
    uni.showToast({ title: '请先生成海报', icon: 'none' })
    return
  }
  try {
    await saveImageToAlbum(posterImagePath.value)
    uni.showModal({
      title: '分享到朋友圈',
      content: '海报已保存到相册，请打开微信朋友圈，选择该图片发布即可',
      showCancel: false,
      confirmText: '我知道了'
    })
  } catch (e) {
    console.error('保存失败:', e)
  }
}

// 分享到朋友圈（引导生成海报）
const shareToMoments = () => {
  savePoster()
}

// 微信分享配置
onShareAppMessage(() => {
  trackShare('wechat_friend', `/packageGuide/pages/guide/detail?id=${guideDetail.value.id}`)
  return {
    title: `${guideDetail.value.title} - 趣途云迹`,
    path: `/packageGuide/pages/guide/detail?id=${guideDetail.value.id}&from=share`,
    imageUrl: guideDetail.value.cover || ''
  }
})

// 朋友圈分享配置
onShareTimeline(() => {
  trackShare('moments', `/packageGuide/pages/guide/detail?id=${guideDetail.value.id}`)
  return {
    title: `${guideDetail.value.title} - 趣途云迹`,
    path: `/packageGuide/pages/guide/detail?id=${guideDetail.value.id}&from=share`,
    imageUrl: guideDetail.value.cover || ''
  }
})

// 生成行程（行程配置弹窗）
const showTripSheet = ref(false)
const tripDays = ref(3)
const tripPreference = ref('auto')
const tripTransport = ref('driving')
const isGenerating = ref(false)

const preferenceOptions = ref<PreferenceOption[]>([
  { id: 'auto', name: '自动推荐', desc: 'AI智能规划最优路线' },
  { id: 'spots', name: '只规划景点', desc: '不含餐饮住宿推荐' },
  { id: 'relax', name: '休闲模式', desc: '每天2-3个景点，节奏慢' },
  { id: 'intensive', name: '特种兵模式', desc: '紧凑安排，玩遍更多' }
])

const transportModes = ref<TransportMode[]>([
  { id: 'driving', name: '自驾', icon: '🚗' },
  { id: 'public', name: '公共交通', icon: '🚇' },
  { id: 'walking', name: '步行', icon: '🚶' },
  { id: 'cycling', name: '骑行', icon: '🚲' }
])

const loadPlanningOptions = async () => {
  try {
    const [prefs, transports] = await Promise.all([
      getPreferenceOptions(),
      getTransportModes()
    ])
    if (prefs && prefs.length > 0) preferenceOptions.value = prefs
    if (transports && transports.length > 0) transportModes.value = transports
  } catch (e) {
    console.warn('加载规划选项失败，使用默认值', e)
  }
}

// 弹窗打开时加载选项
import { watch } from 'vue'
watch(showTripSheet, (val) => {
  if (val) loadPlanningOptions()
})

const generateTrip = async () => {
  if (isGenerating.value) return
  isGenerating.value = true

  const cityName = guideDetail.value.city || ''

  try {
    const route = await generateAIRoute({
      startCity: cityName,
      endCity: cityName,
      days: tripDays.value,
      preference: tripPreference.value,
      transport: tripTransport.value
    })

    isGenerating.value = false
    showTripSheet.value = false

    uni.setStorageSync('currentRoute', JSON.stringify(route))
    uni.navigateTo({ url: '/packageTrip/pages/planning/detail' })
  } catch (error) {
    isGenerating.value = false
    uni.showToast({ title: '规划生成失败，请重试', icon: 'none' })
  }
}

// 页面加载
onMounted(() => {
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonBottom.value = menuButton.top + menuButton.height
    }
  } catch (e) {}
  // #endif

  // 从页面参数获取攻略ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  if (currentPage?.options?.id) {
    guideId.value = parseInt(currentPage.options.id)
  }
  
  loadGuideDetail()
})
</script>

<style lang="scss" scoped>
$primary-color: #00C853;
$primary-light: rgba(0, 200, 83, 0.12);
$bg-color: #F5F5F7;
$card-bg: rgba(255, 255, 255, 0.72);
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius-lg: 24rpx;
$border-radius-md: 16rpx;

.page {
  min-height: 100vh;
  background: $bg-color;
  position: relative;
}

// 导航栏
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 24rpx);
  transition: background 0.3s ease;

  &.navbar-bg {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
  }
}

.nav-back {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

// 内容滚动区
.content-scroll {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

// 封面图
.cover-section {
  width: 100%;
  height: 500rpx;
}

.cover-image {
  width: 100%;
  height: 100%;
}

// 基本信息
.info-section {
  padding: 32rpx;
  background: $card-bg;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1rpx solid rgba(255, 255, 255, 0.6);
}

.guide-title {
  font-size: 40rpx;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: 16rpx;
}

.guide-desc {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
  display: block;
  margin-bottom: 32rpx;
}

// 天数切换
.day-tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.day-tabs {
  display: flex;
  gap: 20rpx;
  padding: 4rpx 0;
}

.day-tab {
  padding: 16rpx 32rpx;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;
  flex-shrink: 0;

  &.active {
    background: rgba(0, 200, 83, 0.1);
    border-color: $primary-color;
  }
}

.tab-text {
  font-size: 28rpx;
  color: $text-primary;
}

.day-tab.active .tab-text {
  color: $primary-color;
  font-weight: 500;
}

// 统计信息
.stats-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 24rpx;
  flex-wrap: wrap;
}

.stat-tag {
  font-size: 24rpx;
  color: $primary-color;
  background: rgba(0, 200, 83, 0.1);
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}

.stat-item {
  font-size: 24rpx;
  color: $text-secondary;
}

// 内容区
.content-section {
  padding: 24rpx 32rpx;
}

.content-block {
  background: $card-bg;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  border-radius: $border-radius-lg;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.block-item {
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-bottom: 24rpx;
  line-height: 1.8;

  &:last-child {
    margin-bottom: 0;
  }
}

.item-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.item-label {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
}

.item-text {
  font-size: 28rpx;
  color: $text-primary;
  flex: 1;
}

// 行程概览
.block-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 24rpx;
}

.title-icon {
  font-size: 32rpx;
}

.title-text {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.schedule-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.schedule-day {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.schedule-route {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.6;
}

// 日期标题
.day-header {
  margin-bottom: 24rpx;
  display: flex;
  align-items: baseline;
  gap: 16rpx;
}

.day-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.day-subtitle {
  font-size: 26rpx;
  color: $text-secondary;
}

.day-summary {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.7;
  padding: 16rpx 20rpx;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: $border-radius-md;
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  margin-bottom: 20rpx;
}

// 景点卡片
.spot-card {
  padding: 24rpx;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: $border-radius-md;
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  margin-bottom: 20rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.spot-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.spot-icon {
  font-size: 32rpx;
}

.spot-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.spot-type-tag {
  font-size: 22rpx;
  color: $primary-color;
  background: rgba(0, 200, 83, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-left: 8rpx;
}

.spot-info {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 8rpx;
  line-height: 1.6;

  &.highlight {
    background: rgba(0, 200, 83, 0.1);
    padding: 12rpx 16rpx;
    border-radius: 12rpx;
    margin-top: 8rpx;
  }

  &.warning {
    background: rgba(255, 193, 7, 0.15);
    padding: 12rpx 16rpx;
    border-radius: 12rpx;
    margin-top: 8rpx;
  }
}

.info-icon {
  font-size: 24rpx;
  flex-shrink: 0;
}

.info-label {
  font-size: 26rpx;
  color: $text-secondary;
  flex-shrink: 0;
}

.info-value {
  font-size: 26rpx;
  color: $text-primary;
  flex: 1;
}

// 空提示
.empty-tip {
  padding: 40rpx;
  text-align: center;
}

.empty-text {
  font-size: 26rpx;
  color: $text-secondary;
}

// 底部安全区
.safe-bottom {
  height: 200rpx;
}

// 底部操作栏
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1rpx solid rgba(0, 0, 0, 0.06);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 24rpx 32rpx;
  border-radius: 100rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.96);
  }

  &.collect, &.share {
    background: rgba(245, 245, 247, 0.6);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1rpx solid rgba(0, 0, 0, 0.06);
  }

  &.primary {
    flex: 1;
    background: linear-gradient(135deg, #00C853, #00BFA5);
    box-shadow: 0 4rpx 16rpx rgba(0, 200, 83, 0.25);
  }
}

.btn-icon {
  font-size: 32rpx;
}

.btn-text {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
}

.action-btn.primary .btn-text {
  color: #FFFFFF;
}

// 分享弹窗遮罩
.share-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
  opacity: 0;
  visibility: hidden;
  transition: all 0.35s ease;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

// 分享弹窗
.share-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  border-top: 1rpx solid rgba(255, 255, 255, 0.6);

  &.show {
    transform: translateY(0);
  }
}

.share-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.share-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.share-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  border-radius: 50%;
}

.close-icon {
  font-size: 36rpx;
  color: $text-secondary;
}

// 攻略信息卡片
.share-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16rpx;
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;
}

.share-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.share-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6rpx;
}

.share-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-desc {
  font-size: 26rpx;
  color: $text-secondary;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-meta {
  display: flex;
  align-items: center;
  gap: 8rpx;
  flex-wrap: wrap;
}

.share-tag, .share-days {
  font-size: 24rpx;
  color: $text-secondary;
}

.share-dot {
  font-size: 24rpx;
  color: $text-secondary;
}

// 链接复制 - removed, now in share channels

// 分享渠道
.share-channels {
  display: flex;
  justify-content: space-around;
  padding-top: 8rpx;
}

.channel-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;

  &:active {
    opacity: 0.7;
  }
}

.channel-btn {
  padding: 0;
  margin: 0;
  background: transparent;
  border: none;
  line-height: 1;

  &::after {
    display: none;
  }
}

.channel-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &.wechat {
    background: #07C160;
  }

  &.poster {
    background: linear-gradient(135deg, #FF9500, #FF5E3A);
  }

  &.link {
    background: #5856D6;
  }

  &.moments {
    background: linear-gradient(135deg, #FA9D3B, #FF6B00);
  }
}

.channel-name {
  font-size: 24rpx;
  color: $text-secondary;
}

// 海报预览弹窗
.poster-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.poster-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 1001;
  display: flex;
  flex-direction: column;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.show {
    transform: translateY(0);
  }
}

.poster-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
}

.poster-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.poster-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  border-radius: 50%;
}

.poster-scroll {
  flex: 1;
  overflow: hidden;
}

.poster-canvas-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32rpx;
}

.poster-preview-img {
  width: 600rpx;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
}

.poster-actions {
  display: flex;
  gap: 20rpx;
  padding: 32rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1rpx solid rgba(0, 0, 0, 0.06);
}

.poster-save-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  border-radius: 50rpx;

  &:active {
    opacity: 0.7;
  }
}

.poster-save-text {
  font-size: 30rpx;
  font-weight: 500;
  color: $text-primary;
}

.poster-share-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #07C160;
  border-radius: 50rpx;

  &:active {
    opacity: 0.7;
  }
}

.poster-share-text {
  font-size: 30rpx;
  font-weight: 500;
  color: #FFFFFF;
}

// 行程配置弹窗遮罩
.trip-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;

  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.trip-sheet {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);

  &.show {
    transform: translateY(0);
  }
}

.trip-sheet-content {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 32rpx 32rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
  max-height: 85vh;
  overflow-y: auto;
  border-top: 1rpx solid rgba(255, 255, 255, 0.6);
}

.sheet-handle {
  display: flex;
  justify-content: center;
  padding: 20rpx 0 8rpx;
}

.handle-bar {
  width: 72rpx;
  height: 8rpx;
  background: #E0E0E0;
  border-radius: 4rpx;
}

.sheet-header {
  display: flex;
  align-items: center;
  padding: 16rpx 32rpx 24rpx;
  gap: 20rpx;
}

.header-icon {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  border-radius: 24rpx;
}

.icon-emoji {
  font-size: 40rpx;
}

.header-info {
  flex: 1;
}

.header-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #1A1A1A;
  display: block;
}

.header-subtitle {
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  border-radius: 50%;
}

.config-section {
  padding: 16rpx 32rpx 24rpx;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 20rpx;
}

.label-icon {
  font-size: 28rpx;
}

.label-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.label-value {
  font-size: 26rpx;
  color: #00C853;
  font-weight: 600;
  margin-left: auto;
}

.days-grid {
  display: flex;
  gap: 16rpx;
}

.day-item {
  flex: 1;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16rpx;
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;

  &.active {
    background: rgba(0, 200, 83, 0.12);
    border-color: $primary-color;
  }
}

.day-text {
  font-size: 28rpx;
  font-weight: 500;
  color: #666;

  .day-item.active & {
    color: $primary-color;
    font-weight: 700;
  }
}

.preference-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.preference-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20rpx;
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;

  &.active {
    background: rgba(0, 200, 83, 0.12);
    border-color: $primary-color;
  }
}

.pref-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;

  .preference-item.active & {
    color: $primary-color;
  }
}

.pref-desc {
  font-size: 24rpx;
  color: #999;
  margin-left: auto;
}

.transport-list {
  display: flex;
  gap: 16rpx;
}

.transport-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx 0;
  background: rgba(245, 245, 247, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20rpx;
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;

  &.active {
    background: rgba(0, 200, 83, 0.12);
    border-color: $primary-color;
  }
}

.transport-icon {
  font-size: 36rpx;
}

.transport-name {
  font-size: 24rpx;
  color: #666;

  .transport-item.active & {
    color: $primary-color;
    font-weight: 600;
  }
}

.generate-area {
  padding: 24rpx 32rpx 32rpx;
}

.generate-btn {
  width: 100%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background: linear-gradient(135deg, #00C853, #00BFA5);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 200, 83, 0.3);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &.loading {
    opacity: 0.85;
    pointer-events: none;
  }
}

.generate-icon {
  font-size: 32rpx;
}

.loading-spinner {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
  border-top-color: #FFFFFF;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.generate-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #FFFFFF;
}

// 口令分享弹窗
.sc-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;

  &.show {
    opacity: 1;
    pointer-events: auto;
  }
}

.sc-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx 32rpx;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.3s ease;

  &.show {
    transform: translateY(0);
  }
}

.sc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.sc-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.sc-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sc-body {
  text-align: center;
}

.sc-desc {
  font-size: 28rpx;
  color: #86868B;
  margin-bottom: 32rpx;
  display: block;
}

.sc-generate {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 24rpx 64rpx;
  background: #00C853;
  border-radius: 100rpx;
}

.sc-generate-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.sc-code-display {
  background: #F2F2F7;
  border-radius: 16rpx;
  padding: 40rpx;
  margin-bottom: 24rpx;
}

.sc-code-text {
  font-size: 48rpx;
  font-weight: 700;
  color: #00C853;
  letter-spacing: 8rpx;
}

.sc-copy {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 48rpx;
  background: #00C853;
  border-radius: 100rpx;
  margin-bottom: 24rpx;
}

.sc-copy-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.sc-hint {
  font-size: 24rpx;
  color: #86868B;
  display: block;
}

// 底部操作栏口令按钮
.action-btn.share-code {
  .btn-icon {
    font-size: 28rpx;
  }
}
</style>
