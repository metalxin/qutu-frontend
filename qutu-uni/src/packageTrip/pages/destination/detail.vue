<template>
  <view class="page">
    <!-- 顶部大图 -->
    <view class="cover-section">
      <image class="cover-image" :src="spotInfo.cover" mode="aspectFill" />
      
      <!-- 顶部操作栏 -->
      <view class="cover-toolbar" :class="{ scrolled: isToolbarScrolled }" :style="toolbarStyle">
        <view class="toolbar-btn back" @click="goBack">
          <SFIcon name="back" :size="36" :color="isToolbarScrolled ? '#1D1D1F' : '#FFFFFF'" />
        </view>
        <view class="toolbar-right">
          <view class="toolbar-btn" :class="{ active: isFavorite }" @click="toggleFavorite">
            <SFIcon name="heart" :size="36" :color="isFavorite ? '#FF2D55' : (isToolbarScrolled ? '#1D1D1F' : '#FFFFFF')" :filled="isFavorite" />
          </view>
          <view class="toolbar-btn" @click="shareSpot">
            <SFIcon name="share" :size="36" :color="isToolbarScrolled ? '#1D1D1F' : '#FFFFFF'" />
          </view>
        </view>
      </view>
    </view>

    <!-- 内容区域 -->
    <view class="content-section">
      <!-- 标题区域 -->
      <view class="title-section">
        <text class="spot-name">{{ spotInfo.name }}</text>
        <text class="spot-subtitle">（{{ spotInfo.subtitle }}）</text>
      </view>

      <!-- 评分和距离 -->
      <view class="meta-section">
        <view class="meta-item">
          <SFIcon name="star" :size="28" color="#FFB800" filled />
          <text class="meta-text">{{ spotInfo.rating }}/5</text>
          <text class="meta-label">({{ spotInfo.reviewSource }})</text>
        </view>
        <text class="meta-divider">•</text>
        <view class="meta-item">
          <SFIcon name="route" :size="28" color="#007AFF" />
          <text class="meta-text">{{ spotInfo.distance }}</text>
        </view>
      </view>

      <!-- 描述 -->
      <view class="desc-section">
        <text class="desc-text" :class="{ expanded: isDescExpanded }">{{ spotInfo.description }}</text>
        <text class="read-more" @click="isDescExpanded = !isDescExpanded">
          {{ isDescExpanded ? '收起' : '阅读更多...' }}
        </text>
      </view>

      <!-- 位置和价格 -->
      <view class="info-cards">
        <view class="info-card">
          <SFIcon name="location" :size="36" color="#FF2D55" />
          <view class="info-content">
            <text class="info-title">{{ spotInfo.locationTitle }}</text>
            <text class="info-detail">{{ spotInfo.locationDetail }}</text>
          </view>
        </view>
        <view class="info-card">
          <SFIcon name="wallet" :size="36" color="#34C759" />
          <view class="info-content">
            <text class="info-title">{{ spotInfo.price }}</text>
            <text class="info-detail">{{ spotInfo.priceNote }}</text>
          </view>
        </view>
      </view>

      <!-- 图库 -->
      <view class="gallery-section">
        <view class="section-header">
          <text class="section-title">图库</text>
          <text class="section-action" @click="viewAllGallery">查看全部</text>
        </view>
        <scroll-view class="gallery-scroll" scroll-x :show-scrollbar="false">
          <view class="gallery-list">
            <view 
              class="gallery-item" 
              v-for="(img, index) in spotInfo.gallery" 
              :key="index"
              @click="previewImage(index)"
            >
              <image class="gallery-image" :src="img" mode="aspectFill" />
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 相关攻略 -->
      <view class="guides-section">
        <view class="section-header">
          <text class="section-title">相关攻略</text>
          <text class="section-action" @click="viewAllGuides">查看更多</text>
        </view>
        <view class="guides-list">
          <view 
            class="guide-card" 
            v-for="guide in relatedGuides" 
            :key="guide.id"
            @click="goToGuideDetail(guide.id)"
          >
            <image class="guide-cover" :src="guide.cover" mode="aspectFill" />
            <view class="guide-info">
              <text class="guide-title">{{ guide.title }}</text>
              <text class="guide-desc">{{ guide.description }}</text>
              <view class="guide-meta">
                <view class="meta-item">
                  <text class="meta-icon">📅</text>
                  <text class="meta-text">{{ guide.days }}</text>
                </view>
                <view class="meta-item">
                  <text class="meta-icon">❤️</text>
                  <text class="meta-text">{{ guide.likes }}</text>
                </view>
                <view class="meta-item">
                  <text class="meta-icon">👁️</text>
                  <text class="meta-text">{{ guide.views }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 更多信息 -->
      <view class="more-section">
        <view class="section-header">
          <text class="section-title">旅行贴士</text>
        </view>
        <view class="tips-list">
          <view class="tip-item" v-for="(tip, index) in spotInfo.tips" :key="index">
            <text class="tip-icon">{{ tip.icon }}</text>
            <view class="tip-content">
              <text class="tip-title">{{ tip.title }}</text>
              <text class="tip-desc">{{ tip.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 评论区域 -->
      <view class="comments-section">
        <view class="section-header" @click="showComments = !showComments">
          <text class="section-title">评论 ({{ commentTotal }})</text>
          <text class="section-toggle">{{ showComments ? '收起' : '展开' }}</text>
        </view>

        <view class="comments-list" v-if="showComments && comments.length > 0">
          <view class="comment-item" v-for="comment in comments" :key="comment.id">
            <image class="comment-avatar" :src="comment.userAvatar || '/static/logo.png'" mode="aspectFill" />
            <view class="comment-body">
              <view class="comment-header">
                <text class="comment-username">{{ comment.userName }}</text>
                <text class="comment-time">{{ comment.createTime }}</text>
              </view>
              <text class="comment-content">{{ comment.content }}</text>
              <view class="comment-actions">
                <view class="action-item" @click="handleLikeComment(comment)">
                  <text class="action-icon">{{ comment.isLiked ? '❤️' : '🤍' }}</text>
                  <text class="action-text">{{ comment.likes || '' }}</text>
                </view>
                <view class="action-item" @click="replyToComment(comment)">
                  <text class="action-icon">💬</text>
                  <text class="action-text">回复</text>
                </view>
              </view>

              <!-- 二级评论/回复 -->
              <view class="replies-section" v-if="comment.replies && comment.replies.length > 0">
                <view class="reply-item" v-for="reply in (comment.showAllReplies ? comment.replies : comment.replies.slice(0, 3))" :key="reply.id">
                  <image class="reply-avatar" :src="reply.userAvatar || '/static/logo.png'" mode="aspectFill" />
                  <view class="reply-body">
                    <view class="reply-header">
                      <text class="reply-username">{{ reply.userName }}</text>
                      <text class="reply-to" v-if="reply.replyTo">回复 <text class="reply-to-name">@{{ reply.replyTo }}</text></text>
                    </view>
                    <text class="reply-content">{{ reply.content }}</text>
                    <view class="reply-actions">
                      <text class="reply-time">{{ reply.createTime }}</text>
                      <view class="action-item" @click="handleLikeReply(comment, reply)">
                        <text class="action-icon-small">{{ reply.isLiked ? '❤️' : '🤍' }}</text>
                        <text class="action-text-small">{{ reply.likes || '' }}</text>
                      </view>
                      <view class="action-item" @click="replyToReply(comment, reply)">
                        <text class="action-text-small">回复</text>
                      </view>
                    </view>
                  </view>
                </view>
                <view class="load-more-replies" v-if="comment.replies.length > 3 && !comment.showAllReplies" @click="comment.showAllReplies = true">
                  <text class="load-more-text">展开全部 {{ comment.replies.length }} 条回复</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view class="no-comments" v-if="showComments && comments.length === 0">
          <text class="no-comments-text">暂无评论，快来抢沙发~</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <view class="bottom-comment-btn" @click="focusCommentInput">
        <text class="bottom-comment-icon">💬</text>
      </view>
      <view class="footprint-btn" :class="{ lit: isFootprintLit }" @click="toggleFootprint">
        <text class="footprint-icon">{{ isFootprintLit ? '✓' : '📍' }}</text>
        <text class="footprint-text">{{ isFootprintLit ? '已点亮' : '点亮足迹' }}</text>
      </view>
      <view class="start-btn" @click="startJourney">
        <text class="start-text">开始旅程</text>
      </view>
    </view>

    <!-- 评论输入弹层 -->
    <view class="comment-input-overlay" v-if="isCommentFocus" @tap="closeCommentInput"></view>
    <view class="comment-input-popup" :class="{ show: isCommentFocus }">
      <view class="input-row">
        <input
          class="comment-input"
          v-model="commentText"
          :placeholder="commentPlaceholder"
          :focus="isCommentFocus"
          :adjust-position="true"
          confirm-type="send"
          @confirm="sendComment"
        />
        <view class="emoji-toggle" @click="showEmojiPicker = !showEmojiPicker">
          <text class="emoji-icon">{{ showEmojiPicker ? '⌨️' : '😊' }}</text>
        </view>
        <view class="send-btn" :class="{ active: commentText.trim() }" @click="sendComment">
          <text class="send-text">发送</text>
        </view>
      </view>
      <view class="emoji-picker" v-if="showEmojiPicker">
        <scroll-view scroll-y class="emoji-scroll">
          <view class="emoji-grid">
            <view
              class="emoji-item"
              v-for="emoji in emojiList"
              :key="emoji"
              @click="insertEmoji(emoji)"
            >
              <text class="emoji-text">{{ emoji }}</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 行程配置弹窗 -->
    <view class="trip-overlay" :class="{ show: showTripSheet }" @tap="showTripSheet = false"></view>
    <view class="trip-sheet" :class="{ show: showTripSheet }">
      <view class="trip-sheet-content" @tap.stop>
        <!-- 拖拽指示条 -->
        <view class="sheet-handle">
          <view class="handle-bar"></view>
        </view>

        <!-- 标题区域 -->
        <view class="sheet-header">
          <view class="header-icon">
            <text class="icon-emoji">🚀</text>
          </view>
          <view class="header-info">
            <text class="header-title">开始旅程</text>
            <text class="header-subtitle">目的地: {{ spotInfo.name }} · {{ spotInfo.locationTitle }}</text>
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
          <view class="checklist-btn" @tap="createChecklistFromDestination">
            <text class="checklist-icon">📋</text>
            <text class="checklist-text">创建旅行清单</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 分享弹窗 -->
    <view class="share-overlay" :class="{ show: showSharePopup }" @tap="showSharePopup = false"></view>
    <view class="share-popup" :class="{ show: showSharePopup }">
      <view class="share-header">
        <text class="share-title">分享给朋友</text>
        <view class="share-close" @tap="showSharePopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <view class="share-card">
        <image class="share-cover" :src="spotInfo.cover" mode="aspectFill" />
        <view class="share-info">
          <text class="share-name">{{ spotInfo.name }}</text>
          <text class="share-subtitle" v-if="spotInfo.subtitle">（{{ spotInfo.subtitle }}）</text>
          <view class="share-meta">
            <text class="share-location">📍 {{ spotInfo.locationTitle }}</text>
            <text class="share-dot">•</text>
            <text class="share-rating">⭐ {{ spotInfo.rating }}</text>
          </view>
        </view>
      </view>

      <view class="share-link-row">
        <text class="link-text">{{ shareLink }}</text>
        <view class="copy-btn" @tap="handleCopyLink">
          <text class="copy-text">复制链接</text>
        </view>
      </view>

      <view class="share-channels">
        <view class="channel-item" @tap="handleShareWechatFriend">
          <button class="channel-btn wechat" open-type="share">
            <text class="channel-icon-text">💬</text>
          </button>
          <text class="channel-name">微信好友</text>
        </view>
        <view class="channel-item" @tap="handleShareMoments">
          <view class="channel-btn moments">
            <text class="channel-icon-text">◎</text>
          </view>
          <text class="channel-name">朋友圈</text>
        </view>
        <view class="channel-item" @tap="handleGeneratePoster">
          <view class="channel-btn poster">
            <text class="channel-icon-text">🖼️</text>
          </view>
          <text class="channel-name">生成海报</text>
        </view>
        <view class="channel-item" @tap="handleCopyLink">
          <view class="channel-btn link">
            <text class="channel-icon-text">🔗</text>
          </view>
          <text class="channel-name">复制链接</text>
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
        <view class="poster-save-btn" @tap="handleSavePoster">
          <text class="poster-save-text">保存到相册</text>
        </view>
        <view class="poster-share-btn" @tap="handleSharePosterToMoments">
          <text class="poster-share-text">分享到朋友圈</text>
        </view>
      </view>
    </view>

    <!-- Canvas用于海报生成，生成时显示 -->
    <canvas
      v-if="isGeneratingPoster"
      id="sharePosterCanvas"
      type="2d"
      :style="{ position: 'fixed', left: '0', top: '0', width: '600px', height: '900px', opacity: '0', zIndex: -1, pointerEvents: 'none' }"
    ></canvas>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, getCurrentInstance, nextTick } from 'vue'
import { onPageScroll, onShow, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getSpotDetail, getSpotComments, getUserFavoriteSpots, postComment, replyComment, likeComment as likeCommentApi, unlikeComment as unlikeCommentApi, deleteComment as deleteCommentApi, favoriteSpot, unfavoriteSpot, createChecklist, createFootprintRecord, getFootprintRecords } from '@/api'
import { getRelatedGuides } from '@/api/modules/guide'
import { generateAIRoute, getPreferenceOptions, getTransportModes } from '../../api/planning'
import type { SpotDetail, Comment, Reply } from '@/api/modules/destination'
import type { RelatedGuide } from '@/api/modules/guide'
import type { AIRoute, PreferenceOption, TransportMode } from '../../api/planning'
import { buildShareConfig, copyShareLink, generateAndSavePoster, saveImageToAlbum, shareToWechatFriend, shareToMoments } from '../../utils/share'
import type { PosterConfig } from '../../utils/share'

// 加载状态
const loading = ref(false)

// 状态栏高度
const statusBarHeight = ref(44)
const menuButtonSpace = ref(0)
const menuButtonBottom = ref(0)

// 收藏状态
const isFavorite = ref(false)
const isFootprintLit = ref(false)
const spotId = ref('')
const isFirstShow = ref(true)
const isToolbarScrolled = ref(false)
const token = ref('')
const isLoggedIn = computed(() => !!token.value)

onPageScroll((e: any) => {
  isToolbarScrolled.value = e.scrollTop > 100
})

// 分享弹窗状态
const showSharePopup = ref(false)
const showPosterPreview = ref(false)
const posterImagePath = ref('')
const isGeneratingPoster = ref(false)
const instance = getCurrentInstance()

const shareLink = computed(() => {
  return `趣途云迹 · ${spotInfo.value.name} - ${spotInfo.value.locationTitle}`
})

const currentShareConfig = computed(() => {
  return buildShareConfig(
    'spot',
    spotInfo.value.id,
    spotInfo.value.name,
    spotInfo.value.cover,
    `${spotInfo.value.name} - ${spotInfo.value.locationTitle} | ⭐${spotInfo.value.rating}`
  )
})

onShareAppMessage(() => {
  shareToWechatFriend(currentShareConfig.value)
  return {
    title: currentShareConfig.value.title,
    path: currentShareConfig.value.path,
    imageUrl: currentShareConfig.value.imageUrl
  }
})

onShareTimeline(() => {
  shareToMoments(currentShareConfig.value)
  return {
    title: currentShareConfig.value.title,
    query: `id=${spotInfo.value.id}&from=share`,
    imageUrl: currentShareConfig.value.imageUrl
  }
})

const toolbarStyle = computed(() => {
  const style: Record<string, string> = {
    paddingTop: statusBarHeight.value + 'px'
  }
  if (menuButtonSpace.value > 0) {
    style.paddingRight = menuButtonSpace.value + 'px'
  }
  return style
})

const posterHeaderStyle = computed(() => {
  const style: Record<string, string> = {}
  if (menuButtonBottom.value > 0) {
    style.paddingTop = (menuButtonBottom.value + 8) + 'px'
  } else {
    style.paddingTop = '32rpx'
  }
  return style
})

// 描述展开状态
const isDescExpanded = ref(false)

// 景点信息
const spotInfo = ref<SpotDetail>({
  id: 1,
  name: '',
  subtitle: '',
  cover: '',
  rating: 0,
  reviewSource: '',
  distance: '',
  description: '',
  locationTitle: '',
  locationDetail: '',
  price: '',
  priceNote: '',
  gallery: [],
  tips: [],
  provinceName: '',
  provinceCode: '',
  countryName: '',
  countryCode: '',
  lat: 0,
  lng: 0
})

// 获取系统信息
onMounted(async () => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  const windowWidth = systemInfo.windowWidth || 375
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonSpace.value = windowWidth - menuButton.left + 10
      menuButtonBottom.value = menuButton.top + menuButton.height
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  token.value = uni.getStorageSync('token') || ''
  
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  
  if (options.id) {
    spotId.value = options.id
    await loadSpotInfo(options.id)
  }
})

onShow(() => {
  if (isFirstShow.value) {
    isFirstShow.value = false
    return
  }
  if (spotId.value) {
    loadSpotInfo(spotId.value)
  }
})

// 加载景点信息
const loadSpotInfo = async (id: string) => {
  loading.value = true
  try {
    const [spotRes, commentsRes, guidesRes] = await Promise.all([
      getSpotDetail(Number(id)),
      getSpotComments(Number(id)),
      getRelatedGuides(Number(id), 3)
    ])
    
    spotInfo.value = spotRes
    isFavorite.value = !!spotRes.isFavorite
    if (!isFavorite.value && isLoggedIn.value) {
      const favoritePage = await getUserFavoriteSpots({ current: 1, size: 200 })
      const favoriteRecords = favoritePage?.records || []
      isFavorite.value = favoriteRecords.some(item => Number(item.id) === Number(id))
    }
    await checkFootprintStatus(Number(id))
    comments.value = commentsRes?.records || []
    commentTotal.value = commentsRes?.total || 0
    relatedGuides.value = guidesRes
  } catch (error) {
    console.error('加载景点信息失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 收藏
const toggleFavorite = async () => {
  if (!isLoggedIn.value) {
    uni.navigateTo({ url: '/pages/user/login' })
    return
  }
  try {
    console.log('开始收藏景点, spotId:', spotInfo.value.id, 'isFavorite:', isFavorite.value)
    if (isFavorite.value) {
      console.log('调用取消收藏接口, URL:', `/admin/spots/${spotInfo.value.id}/unfavorite`)
      const res = await unfavoriteSpot(spotInfo.value.id)
      console.log('取消收藏接口返回:', res)
    } else {
      console.log('调用收藏接口, URL:', `/admin/spots/${spotInfo.value.id}/favorite`)
      const res = await favoriteSpot(spotInfo.value.id)
      console.log('收藏接口返回:', res)
    }
    isFavorite.value = !isFavorite.value
    uni.showToast({
      title: isFavorite.value ? '已收藏' : '已取消收藏',
      icon: 'success'
    })
  } catch (error: any) {
    console.error('收藏操作失败:', error)
    const errorMsg = error?.message || error?.msg || '操作失败'
    uni.showToast({ title: errorMsg, icon: 'none' })
  }
}

const checkFootprintStatus = async (spotId: number) => {
  if (!isLoggedIn.value) return
  try {
    const res = await getFootprintRecords({ current: 1, size: 200 })
    if (res) {
      isFootprintLit.value = (res.records || []).some(r => r.spotId === spotId)
    }
  } catch (error) {
    console.error('检查足迹状态失败:', error)
  }
}

const toggleFootprint = async () => {
  if (!isLoggedIn.value) {
    uni.navigateTo({ url: '/pages/user/login' })
    return
  }
  if (isFootprintLit.value) {
    uni.showToast({ title: '已经点亮过该足迹', icon: 'none' })
    return
  }
  try {
    const spot = spotInfo.value
    await createFootprintRecord({
      spotId: spot.id,
      spotName: spot.name,
      cityName: spot.locationTitle,
      provinceName: spot.provinceName,
      provinceCode: spot.provinceCode,
      countryName: spot.countryName || '中国',
      countryCode: spot.countryCode || 'CN',
      lat: spot.lat,
      lng: spot.lng,
      type: spot.countryCode && spot.countryCode !== 'CN' ? 2 : 1,
      imageUrl: spot.cover
    })
    isFootprintLit.value = true
    uni.showToast({ title: '点亮足迹成功！', icon: 'success' })
  } catch (error) {
    console.error('点亮足迹失败:', error)
    uni.showToast({ title: '点亮失败，请重试', icon: 'none' })
  }
}

const shareSpot = () => {
  showSharePopup.value = true
}

const handleShareWechatFriend = () => {
  showSharePopup.value = false
}

const handleShareMoments = () => {
  showSharePopup.value = false
  handleGeneratePoster()
}

const handleCopyLink = async () => {
  try {
    await copyShareLink(currentShareConfig.value.path)
    uni.showToast({ title: '链接已复制', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '复制失败', icon: 'none' })
  }
}

const handleGeneratePoster = async () => {
  if (isGeneratingPoster.value) return
  isGeneratingPoster.value = true
  showSharePopup.value = false

  try {
    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 300))

    const posterConfig: PosterConfig = {
      coverUrl: spotInfo.value.cover,
      title: spotInfo.value.name,
      subtitle: spotInfo.value.subtitle,
      rating: spotInfo.value.rating,
      location: spotInfo.value.locationTitle,
      price: spotInfo.value.price !== '免费' ? spotInfo.value.price : undefined,
      tags: spotInfo.value.tags
    }

    const filePath = await generateAndSavePoster('sharePosterCanvas', posterConfig, instance?.proxy)
    if (filePath) {
      posterImagePath.value = filePath
      showPosterPreview.value = true
    }
  } catch (e: any) {
    console.error('海报生成失败:', e)
    uni.showToast({ title: e?.message || '海报生成失败，请重试', icon: 'none', duration: 2000 })
  } finally {
    isGeneratingPoster.value = false
  }
}

const handleSavePoster = async () => {
  if (!posterImagePath.value) {
    uni.showToast({ title: '请先生成海报', icon: 'none' })
    return
  }
  try {
    await saveImageToAlbum(posterImagePath.value)
    uni.showToast({ title: '已保存到相册', icon: 'success' })
  } catch (e) {
    console.error('保存失败:', e)
  }
}

const handleSharePosterToMoments = async () => {
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

// 查看全部图库
const viewAllGallery = () => {
  uni.previewImage({
    urls: spotInfo.value.gallery,
    current: 0
  })
}

// 预览图片
const previewImage = (index: number) => {
  uni.previewImage({
    urls: spotInfo.value.gallery,
    current: index
  })
}

// 相关攻略数据
const relatedGuides = ref<RelatedGuide[]>([])

// 查看更多攻略
const viewAllGuides = () => {
  uni.navigateTo({
    url: `/pages/guide/list?spotId=${spotInfo.value.id}`
  })
}

// 跳转攻略详情
const goToGuideDetail = (id: number) => {
  uni.navigateTo({
    url: `/packageGuide/pages/guide/detail?id=${id}`
  })
}

// 开始旅程
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

const startJourney = () => {
  showTripSheet.value = true
  loadPlanningOptions()
}

const generateTrip = async () => {
  if (isGenerating.value) return
  isGenerating.value = true

  const cityName = spotInfo.value.locationTitle || spotInfo.value.locationDetail || ''

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

const createChecklistFromDestination = async () => {
  const today = new Date()
  const endDate = new Date(today)
  endDate.setDate(today.getDate() + tripDays.value - 1)
  const formatDate = (d: Date) => d.toISOString().slice(0, 10)

  try {
    const checklistId = await createChecklist({
      title: spotInfo.value.name + '之旅',
      destination: spotInfo.value.locationTitle || spotInfo.value.name,
      departDate: formatDate(today),
      returnDate: formatDate(endDate)
    })
    showTripSheet.value = false
    uni.showToast({ title: '清单创建成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateTo({ url: `/packageChecklist/pages/checklist/detail?id=${checklistId}` })
    }, 500)
  } catch (error) {
    uni.showToast({ title: '创建清单失败', icon: 'none' })
  }
}

// 评论相关
const isCommentFocus = ref(false)
const commentText = ref('')
const commentPlaceholder = ref('写下你的评论...')
const replyTarget = ref<any>(null)
const showEmojiPicker = ref(false)
const showComments = ref(true)
const commentTotal = ref(0)
const commentPage = ref(1)
const commentPageSize = 20
const hasMoreComments = ref(true)

const emojiList = [
  '😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆',
  '😉', '😊', '😋', '😎', '😍', '😘', '🥰', '😗',
  '😙', '😚', '🙂', '🤗', '🤩', '🤔', '🤨', '😐',
  '😑', '😶', '😏', '😒', '🙄', '😬', '🤥', '😌',
  '😔', '😪', '🤤', '😴', '😷', '🤒', '🤕', '🤢',
  '🤮', '🥵', '🥶', '🥴', '😵', '🤯', '🤠', '🥳',
  '🥸', '😎', '🤓', '🧐', '😕', '😟', '🙁', '😮',
  '😯', '😲', '😳', '🥺', '😦', '😧', '😨', '😰',
  '😥', '😢', '😭', '😱', '😖', '😣', '😞', '😓',
  '😩', '😫', '🥱', '😤', '😡', '😠', '🤬', '😈',
  '👿', '💀', '☠️', '💩', '🤡', '👹', '👺', '👻',
  '👍', '👎', '👏', '🙌', '🤝', '❤️', '🧡', '💛',
  '💚', '💙', '💜', '🖤', '🤍', '💔', '❣️', '💕',
  '💞', '💓', '💗', '💖', '💘', '💝', '💟', '✨',
  '⭐', '🌟', '💫', '🔥', '💯', '🎉', '🎊', '🎈',
  '🏆', '🥇', '🎯', '🎨', '📸', '📷', '🎬', '🎭',
  '🌍', '🌎', '🌏', '🗺️', '🏔️', '⛰️', '🌋', '🏖️',
  '🏕️', '🏠', '🏛️', '🗼', '🏰', '⛩️', '🕌', '🕍',
  '✈️', '🚗', '🚕', '🚌', '🚎', '🚄', '🚅', '🚈',
  '🚢', '🛳️', '⛵', '🚤', '🚲', '🛵', '🏍️', '🚨'
]

// 评论数据
const comments = ref<any[]>([])

const focusCommentInput = () => {
  replyTarget.value = null
  commentPlaceholder.value = '写下你的评论...'
  isCommentFocus.value = true
  showEmojiPicker.value = false
}

const insertEmoji = (emoji: string) => {
  commentText.value += emoji
}

const handleLikeComment = async (comment: any) => {
  try {
    if (comment.isLiked) {
      await unlikeCommentApi(comment.id)
      comment.isLiked = false
      comment.likes = Math.max(0, (comment.likes || 0) - 1)
    } else {
      await likeCommentApi(comment.id)
      comment.isLiked = true
      comment.likes = (comment.likes || 0) + 1
    }
  } catch {
    if (comment.isLiked) {
      comment.isLiked = false
      comment.likes = Math.max(0, (comment.likes || 0) - 1)
    } else {
      comment.isLiked = true
      comment.likes = (comment.likes || 0) + 1
    }
  }
}

const handleLikeReply = async (comment: any, reply: any) => {
  try {
    if (reply.isLiked) {
      await unlikeCommentApi(reply.id)
      reply.isLiked = false
      reply.likes = Math.max(0, (reply.likes || 0) - 1)
    } else {
      await likeCommentApi(reply.id)
      reply.isLiked = true
      reply.likes = (reply.likes || 0) + 1
    }
  } catch {
    if (reply.isLiked) {
      reply.isLiked = false
      reply.likes = Math.max(0, (reply.likes || 0) - 1)
    } else {
      reply.isLiked = true
      reply.likes = (reply.likes || 0) + 1
    }
  }
}

const replyToComment = (comment: any) => {
  replyTarget.value = { type: 'comment', comment }
  commentPlaceholder.value = `回复 @${comment.userName}`
  isCommentFocus.value = true
  showEmojiPicker.value = false
}

const replyToReply = (comment: any, reply: any) => {
  replyTarget.value = { type: 'reply', comment, reply }
  commentPlaceholder.value = `回复 @${reply.userName}`
  isCommentFocus.value = true
  showEmojiPicker.value = false
}

const onCommentBlur = () => {
  setTimeout(() => {
    if (!commentText.value) {
      isCommentFocus.value = false
      replyTarget.value = null
      commentPlaceholder.value = '写下你的评论...'
      showEmojiPicker.value = false
    }
  }, 150)
}

const closeCommentInput = () => {
  isCommentFocus.value = false
  replyTarget.value = null
  commentPlaceholder.value = '写下你的评论...'
  showEmojiPicker.value = false
}

const sendComment = async () => {
  if (!commentText.value.trim()) return

  try {
    if (replyTarget.value) {
      const targetComment = replyTarget.value.comment
      const replyToUser = replyTarget.value.type === 'reply'
        ? replyTarget.value.reply.userName
        : targetComment.userName

      const res = await replyComment(targetComment.id, {
        content: commentText.value,
        replyTo: replyToUser
      })

      if (!targetComment.replies) targetComment.replies = []
      targetComment.replies.push(res)
    } else {
      const res = await postComment(spotInfo.value.id, {
        content: commentText.value
      })

      comments.value.unshift(res)
    }

    commentText.value = ''
    replyTarget.value = null
    commentPlaceholder.value = '写下你的评论...'
    isCommentFocus.value = false
    showEmojiPicker.value = false

    uni.showToast({ title: '评论成功，等待审核', icon: 'none' })
  } catch {
    uni.showToast({ title: '发送失败', icon: 'none' })
  }
}

const loadComments = async () => {
  try {
    const res = await getSpotComments(spotInfo.value.id, {
      current: commentPage.value,
      size: commentPageSize
    })
    if (commentPage.value === 1) {
      comments.value = res.records || []
    } else {
      comments.value.push(...(res.records || []))
    }
    commentTotal.value = res.total || 0
    hasMoreComments.value = comments.value.length < commentTotal.value
  } catch {
    // ignore
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #FF7043;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;

.page {
  min-height: 100vh;
  background: $card-bg;
}

// 封面区域
.cover-section {
  position: relative;
  width: 100%;
  height: 560rpx;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.cover-toolbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
  z-index: 100;
  transition: background 0.3s ease;

  &.scrolled {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
  }
}

.toolbar-btn {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 50%;
  transition: all 0.3s ease;

  &.back {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1rpx solid rgba(255, 255, 255, 0.2);
  }

  &.active {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1rpx solid rgba(255, 255, 255, 0.2);
  }

  .scrolled & {
    background: rgba(0, 0, 0, 0.06);
    backdrop-filter: none;
    -webkit-backdrop-filter: none;
    border: none;

    &.back {
      background: rgba(0, 0, 0, 0.06);
    }

    &.active {
      background: rgba(0, 0, 0, 0.06);
    }
  }

  &:active {
    transform: scale(0.95);
  }
}

.btn-icon {
  font-size: 32rpx;
  color: #FFFFFF;
}

.toolbar-right {
  display: flex;
  gap: 16rpx;
}

// 内容区域
.content-section {
  padding: 32rpx;
  padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
  margin-top: -40rpx;
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  position: relative;
  z-index: 5;
}

// 标题
.title-section {
  margin-bottom: 16rpx;
}

.spot-name {
  font-size: 44rpx;
  font-weight: 700;
  color: $text-primary;
}

.spot-subtitle {
  font-size: 32rpx;
  color: $text-secondary;
  margin-left: 8rpx;
}

// 元信息
.meta-section {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 32rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.meta-icon {
  font-size: 28rpx;

  &.distance {
    color: $primary-color;
  }
}

.meta-text {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
}

.meta-label {
  font-size: 24rpx;
  color: $text-secondary;
}

.meta-divider {
  color: $text-secondary;
}

// 描述
.desc-section {
  margin-bottom: 32rpx;
}

.desc-text {
  font-size: 28rpx;
  line-height: 1.7;
  color: $text-primary;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 4;
  overflow: hidden;

  &.expanded {
    -webkit-line-clamp: unset;
    display: block;
  }
}

.read-more {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 500;
  margin-top: 8rpx;
  display: inline-block;
}

// 信息卡片
.info-cards {
  display: flex;
  gap: 24rpx;
  margin-bottom: 40rpx;
}

.info-card {
  flex: 1;
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
  background: $bg-color;
  border-radius: 16rpx;
}

.info-icon {
  font-size: 36rpx;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  flex: 1;
  min-width: 0;
}

.info-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.info-detail {
  font-size: 22rpx;
  color: $text-secondary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 图库
.gallery-section {
  margin-bottom: 40rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.section-action {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 500;
}

.gallery-scroll {
  margin: 0 -32rpx;
  padding: 0 32rpx;
}

.gallery-list {
  display: flex;
  gap: 16rpx;
  padding-right: 32rpx;
}

.gallery-item {
  flex-shrink: 0;
  width: 200rpx;
  height: 200rpx;
  border-radius: 16rpx;
  overflow: hidden;

  &:active {
    opacity: 0.8;
  }
}

.gallery-image {
  width: 100%;
  height: 100%;
}

// 相关攻略
.guides-section {
  margin-bottom: 40rpx;
}

.guides-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.guide-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: $card-bg;
  border-radius: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.guide-cover {
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx;
  flex-shrink: 0;
}

.guide-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 8rpx 0;
}

.guide-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-desc {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-meta {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.guide-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.guide-meta .meta-icon {
  font-size: 24rpx;
}

.guide-meta .meta-text {
  font-size: 24rpx;
  color: $text-secondary;
}

// 更多信息
.more-section {
  margin-bottom: 32rpx;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.tip-item {
  display: flex;
  gap: 16rpx;
  padding: 20rpx;
  background: $bg-color;
  border-radius: 16rpx;
}

.tip-icon {
  font-size: 36rpx;
}

.tip-content {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.tip-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.tip-desc {
  font-size: 24rpx;
  color: $text-secondary;
}

// 行程配置弹窗
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
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
  max-height: 85vh;
  overflow-y: auto;
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
  background: linear-gradient(135deg, #FFF3E0, #FFE0B2);
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
}

.header-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  border-radius: 50%;
}

.close-icon {
  font-size: 36rpx;
  color: #999;
  line-height: 1;
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
  color: #FF5722;
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
  background: #F5F5F7;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.active {
    background: #FFF3E0;
    border-color: #FF5722;
  }
}

.day-text {
  font-size: 28rpx;
  font-weight: 500;
  color: #666;

  .day-item.active & {
    color: #FF5722;
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
  background: #F5F5F7;
  border-radius: 20rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.active {
    background: #FFF3E0;
    border-color: #FF5722;
  }
}

.pref-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-right: 16rpx;

  .preference-item.active & {
    color: #FF5722;
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
  background: #F5F5F7;
  border-radius: 20rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.active {
    background: #FFF3E0;
    border-color: #FF5722;
  }
}

.transport-icon {
  font-size: 36rpx;
}

.transport-name {
  font-size: 24rpx;
  color: #666;

  .transport-item.active & {
    color: #FF5722;
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
  background: linear-gradient(135deg, #FF7043, #FF5722);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 24rpx rgba(255, 87, 34, 0.3);
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

.checklist-btn {
  width: 100%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background: #FFFFFF;
  border: 2rpx solid #E5E5EA;
  border-radius: 50rpx;
  margin-top: 20rpx;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
    background: #F5F5F7;
  }
}

.checklist-icon {
  font-size: 32rpx;
}

.checklist-text {
  font-size: 28rpx;
  font-weight: 500;
  color: #1D1D1F;
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
  transition: all 0.3s ease;

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
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

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
  background: $bg-color;
  border-radius: 50%;
}

.close-icon {
  font-size: 36rpx;
  color: $text-secondary;
}

// 景点信息卡片
.share-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: $bg-color;
  border-radius: 16rpx;
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
}

.share-subtitle {
  font-size: 26rpx;
  color: $text-secondary;
}

.share-meta {
  display: flex;
  align-items: center;
  gap: 8rpx;
  flex-wrap: wrap;
}

.share-location {
  font-size: 24rpx;
  color: $text-secondary;
}

.share-dot {
  font-size: 24rpx;
  color: $text-secondary;
}

.share-rating {
  font-size: 24rpx;
  color: #FFB800;
}

.share-reviews {
  font-size: 24rpx;
  color: $text-secondary;
}

// 链接复制
.share-link-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: $bg-color;
  border-radius: 12rpx;
  margin-bottom: 32rpx;
}

.link-text {
  font-size: 24rpx;
  color: $text-secondary;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  background: $card-bg;
  border-radius: 8rpx;
  margin-left: 16rpx;

  &:active {
    opacity: 0.7;
  }
}

.copy-text {
  font-size: 26rpx;
  color: $primary-color;
  font-weight: 500;
}

// 分享渠道
.share-channels {
  display: flex;
  justify-content: space-around;
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
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  margin: 0;
  border: none;
  line-height: 1;

  &::after {
    display: none;
  }

  &.wechat {
    background: #07C160;
  }

  &.moments {
    background: linear-gradient(135deg, #FA9D3B, #FF6B00);
  }

  &.poster {
    background: linear-gradient(135deg, #5856D6, #AF52DE);
  }

  &.link {
    background: linear-gradient(135deg, #007AFF, #5AC8FA);
  }
}

.channel-icon-text {
  font-size: 40rpx;
  color: #FFFFFF;
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
  background: $card-bg;
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
  background: $bg-color;
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

.poster-canvas {
  position: absolute;
  left: -9999px;
  top: -9999px;
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
  background: $card-bg;
  border-top: 1rpx solid #E5E5EA;
}

.poster-save-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
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

// 评论区域
.comments-section {
  margin-top: 24rpx;
  padding: 0 32rpx 32rpx;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.comment-item {
  display: flex;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #F0F0F0;

  &:last-child {
    border-bottom: none;
  }
}

.comment-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  flex-shrink: 0;
  background: #F0F0F0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.comment-username {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.comment-time {
  font-size: 22rpx;
  color: $text-secondary;
}

.comment-content {
  font-size: 28rpx;
  color: $text-primary;
  line-height: 1.6;
  display: block;
  margin-bottom: 16rpx;
  word-break: break-all;
}

.comment-actions {
  display: flex;
  gap: 32rpx;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 4rpx 0;

  &:active {
    opacity: 0.7;
  }
}

.action-icon {
  font-size: 28rpx;
}

.action-text {
  font-size: 24rpx;
  color: $text-secondary;
}

.action-icon-small {
  font-size: 24rpx;
}

.action-text-small {
  font-size: 22rpx;
  color: $text-secondary;
}

// 二级评论/回复
.replies-section {
  margin-top: 16rpx;
  padding: 16rpx 20rpx;
  background: #F8F8FA;
  border-radius: 16rpx;
}

.reply-item {
  display: flex;
  gap: 12rpx;
  padding: 12rpx 0;

  &:not(:last-child) {
    border-bottom: 1rpx solid #EEEEF0;
  }
}

.reply-avatar {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  flex-shrink: 0;
  background: #E5E5EA;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 4rpx;
  flex-wrap: wrap;
}

.reply-username {
  font-size: 24rpx;
  font-weight: 600;
  color: $text-primary;
}

.reply-to {
  font-size: 22rpx;
  color: $text-secondary;
}

.reply-to-name {
  color: $primary-color;
}

.reply-content {
  font-size: 26rpx;
  color: $text-primary;
  line-height: 1.5;
  display: block;
  margin-bottom: 8rpx;
  word-break: break-all;
}

.reply-actions {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.reply-time {
  font-size: 20rpx;
  color: $text-secondary;
}

.load-more-replies {
  padding: 12rpx 0 4rpx;

  &:active {
    opacity: 0.7;
  }
}

.load-more-text {
  font-size: 24rpx;
  color: $primary-color;
}

.no-comments {
  padding: 60rpx 0;
  text-align: center;
}

.no-comments-text {
  font-size: 28rpx;
  color: $text-secondary;
}

// 底部按钮
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  z-index: 900;
}

.bottom-comment-btn {
  width: 100rpx;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F7;
  border-radius: 50rpx;
  border: 2rpx solid #E5E5EA;

  &:active {
    transform: scale(0.96);
    opacity: 0.7;
  }
}

.bottom-comment-icon {
  font-size: 40rpx;
}

.footprint-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  min-width: 140rpx;
  padding: 12rpx 24rpx;
  background: #F5F5F7;
  border-radius: 50rpx;
  border: 2rpx solid #E5E5EA;
  transition: all 0.2s ease;

  &.lit {
    background: #E8F5E9;
    border-color: #4CAF50;
  }

  &:active {
    transform: scale(0.96);
  }
}

.footprint-icon {
  font-size: 28rpx;
}

.footprint-text {
  font-size: 20rpx;
  font-weight: 500;
  color: #86868B;

  .lit & {
    color: #2E7D32;
  }
}

.start-btn {
  flex: 1;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF7043, #FF5722);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 24rpx rgba(255, 87, 34, 0.3);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 16rpx rgba(255, 87, 34, 0.3);
  }
}

.start-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

// 评论输入弹层
.comment-input-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 950;
}

.comment-input-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $card-bg;
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
  z-index: 960;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.3s ease;

  &.show {
    transform: translateY(0);
  }
}

.input-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.comment-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: $text-primary;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
}

.emoji-toggle {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    opacity: 0.7;
  }
}

.emoji-icon {
  font-size: 40rpx;
}

.send-btn {
  padding: 16rpx 28rpx;
  background: #E5E5EA;
  border-radius: 100rpx;
  transition: all 0.2s ease;

  &.active {
    background: $primary-color;
  }

  &:active {
    transform: scale(0.96);
  }
}

.send-text {
  font-size: 28rpx;
  color: $text-secondary;
  font-weight: 500;
}

.send-btn.active .send-text {
  color: #FFFFFF;
}

// 表情选择器
.emoji-picker {
  margin-top: 12rpx;
  border-top: 1rpx solid #E5E5EA;
}

.emoji-scroll {
  max-height: 360rpx;
}

.emoji-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx 0;
}

.emoji-item {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    background: $bg-color;
    border-radius: 16rpx;
  }
}

.emoji-text {
  font-size: 40rpx;
}

// 评论区收起/展开
.section-toggle {
  font-size: 24rpx;
  color: $text-secondary;
  margin-left: auto;
}
</style>
