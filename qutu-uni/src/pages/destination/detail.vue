<template>
  <view class="page">
    <!-- 顶部大图 -->
    <view class="cover-section">
      <image class="cover-image" :src="spotInfo.cover" mode="aspectFill" />
      
      <!-- 顶部操作栏 -->
      <view class="cover-toolbar" :style="toolbarStyle">
        <view class="toolbar-btn back" @click="goBack">
          <SFIcon name="back" :size="36" color="#1D1D1F" />
        </view>
        <view class="toolbar-right">
          <view class="toolbar-btn" :class="{ active: isFavorite }" @click="toggleFavorite">
            <SFIcon name="heart" :size="36" :color="isFavorite ? '#FF2D55' : '#FFFFFF'" :filled="isFavorite" />
          </view>
          <view class="toolbar-btn" @click="shareSpot">
            <SFIcon name="share" :size="36" color="#FFFFFF" />
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
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <view class="start-btn" @click="startJourney">
        <text class="start-text">开始旅程</text>
      </view>
    </view>

    <!-- 分享弹窗 -->
    <view class="share-overlay" :class="{ show: showSharePopup }" @tap="showSharePopup = false"></view>
    <view class="share-popup" :class="{ show: showSharePopup }">
      <!-- 弹窗头部 -->
      <view class="share-header">
        <text class="share-title">分享这个目的地</text>
        <view class="share-close" @tap="showSharePopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 景点信息卡片 -->
      <view class="share-card">
        <image class="share-cover" :src="spotInfo.cover" mode="aspectFill" />
        <view class="share-info">
          <text class="share-name">{{ spotInfo.name }}</text>
          <text class="share-subtitle">（{{ spotInfo.subtitle }}）</text>
          <view class="share-meta">
            <text class="share-location">📍 {{ spotInfo.locationTitle }}</text>
            <text class="share-dot">•</text>
            <text class="share-rating">⭐ {{ spotInfo.rating }}</text>
            <text class="share-reviews">({{ spotInfo.reviewCount || 323 }})</text>
          </view>
        </view>
      </view>

      <!-- 链接复制 -->
      <view class="share-link">
        <text class="link-text">plango.com/detail/{{ spotInfo.id }}</text>
        <view class="copy-btn" @tap="copyLink">
          <text class="copy-icon">📋</text>
          <text class="copy-text">复制</text>
        </view>
      </view>

      <!-- 分享渠道 -->
      <view class="share-channels">
        <view class="channel-item" @tap="shareToChannel('douyin')">
          <view class="channel-icon douyin">
            <text class="icon-text">♪</text>
          </view>
          <text class="channel-name">抖音</text>
        </view>
        <view class="channel-item" @tap="shareToChannel('wechat')">
          <view class="channel-icon wechat">
            <text class="icon-text">💬</text>
          </view>
          <text class="channel-name">微信</text>
        </view>
        <view class="channel-item" @tap="shareToChannel('qq')">
          <view class="channel-icon qq">
            <text class="icon-text">🐧</text>
          </view>
          <text class="channel-name">QQ</text>
        </view>
        <view class="channel-item" @tap="shareToChannel('xiaohongshu')">
          <view class="channel-icon xiaohongshu">
            <text class="icon-text">📕</text>
          </view>
          <text class="channel-name">小红书</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getSpotDetail, getSpotComments, postComment, replyComment, likeComment as likeCommentApi, favoriteSpot, unfavoriteSpot } from '@/api'
import { getRelatedGuides } from '@/api/modules/guide'
import type { SpotDetail, Comment, Reply } from '@/api/modules/destination'
import type { RelatedGuide } from '@/api/modules/guide'

// 加载状态
const loading = ref(false)

// 状态栏高度
const statusBarHeight = ref(44)
const menuButtonSpace = ref(0)

// 收藏状态
const isFavorite = ref(false)
const token = ref('')
const isLoggedIn = computed(() => !!token.value)

// 分享弹窗状态
const showSharePopup = ref(false)

const toolbarStyle = computed(() => {
  const style: Record<string, string> = {
    paddingTop: statusBarHeight.value + 'px'
  }
  if (menuButtonSpace.value > 0) {
    style.paddingRight = menuButtonSpace.value + 'px'
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
  tips: []
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
    await loadSpotInfo(options.id)
  }
})

// 加载景点信息
const loadSpotInfo = async (id: string) => {
  loading.value = true
  try {
    // 并行加载景点信息、评论和相关攻略
    const [spotRes, commentsRes, guidesRes] = await Promise.all([
      getSpotDetail(Number(id)),
      getSpotComments(Number(id)),
      getRelatedGuides(Number(id), 3)
    ])
    
    spotInfo.value = spotRes
    comments.value = commentsRes
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
      console.log('调用取消收藏接口')
      await unfavoriteSpot(spotInfo.value.id)
    } else {
      console.log('调用收藏接口')
      await favoriteSpot(spotInfo.value.id)
    }
    isFavorite.value = !isFavorite.value
    uni.showToast({
      title: isFavorite.value ? '已收藏' : '已取消收藏',
      icon: 'none'
    })
  } catch (error) {
    console.error('收藏操作失败:', error)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

// 分享
const shareSpot = () => {
  showSharePopup.value = true
}

// 复制链接
const copyLink = () => {
  const link = `plango.com/detail/${spotInfo.value.id}`
  uni.setClipboardData({
    data: link,
    success: () => {
      uni.showToast({
        title: '链接已复制',
        icon: 'success'
      })
    }
  })
}

// 分享到渠道
const shareToChannel = (channel: string) => {
  const channelNames: any = {
    douyin: '抖音',
    wechat: '微信',
    qq: 'QQ',
    xiaohongshu: '小红书'
  }
  uni.showToast({
    title: `分享到${channelNames[channel]}`,
    icon: 'none'
  })
  showSharePopup.value = false
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
    url: `/pages/guide/detail?id=${id}`
  })
}

// 开始旅程
const startJourney = () => {
  uni.showToast({
    title: '已添加到行程',
    icon: 'success'
  })
}

// 评论相关
const isCommentFocus = ref(false)
const commentText = ref('')
const commentPlaceholder = ref('写下你的评论...')
const replyTarget = ref<any>(null)
const hasMoreComments = ref(true)

// 评论数据
const comments = ref<Comment[]>([])

// 给评论点赞
const likeComment = async (comment: any) => {
  try {
    await likeCommentApi(comment.id)
    comment.isLiked = !comment.isLiked
    comment.likes += comment.isLiked ? 1 : -1
  } catch (error) {
    // 乐观更新，即使失败也更新UI
    comment.isLiked = !comment.isLiked
    comment.likes += comment.isLiked ? 1 : -1
  }
}

// 给回复点赞
const likeReply = async (comment: any, reply: any) => {
  try {
    await likeCommentApi(reply.id)
    reply.isLiked = !reply.isLiked
    reply.likes += reply.isLiked ? 1 : -1
  } catch (error) {
    reply.isLiked = !reply.isLiked
    reply.likes += reply.isLiked ? 1 : -1
  }
}

// 回复评论
const replyToComment = (comment: any) => {
  replyTarget.value = { type: 'comment', comment }
  commentPlaceholder.value = `回复 @${comment.userName}`
  isCommentFocus.value = true
}

// 回复回复
const replyToReply = (comment: any, reply: any) => {
  replyTarget.value = { type: 'reply', comment, reply }
  commentPlaceholder.value = `回复 @${reply.userName}`
  isCommentFocus.value = true
}

// 输入框失焦
const onCommentBlur = () => {
  setTimeout(() => {
    if (!commentText.value) {
      isCommentFocus.value = false
      replyTarget.value = null
      commentPlaceholder.value = '写下你的评论...'
    }
  }, 100)
}

// 发送评论
const sendComment = async () => {
  if (!commentText.value.trim()) {
    return
  }
  
  try {
    if (replyTarget.value) {
      // 回复评论或回复
      const targetComment = replyTarget.value.comment
      const replyToUser = replyTarget.value.type === 'reply' 
        ? replyTarget.value.reply.userName 
        : targetComment.userName
      
      const res = await replyComment(targetComment.id, {
        content: commentText.value,
        replyTo: replyToUser
      })
      
      targetComment.replies.push(res)
    } else {
      // 新评论
      const res = await postComment(spotInfo.value.id, {
        content: commentText.value
      })
      
      comments.value.unshift(res)
    }
    
    commentText.value = ''
    replyTarget.value = null
    commentPlaceholder.value = '写下你的评论...'
    isCommentFocus.value = false
    
    uni.showToast({
      title: '评论成功',
      icon: 'success'
    })
  } catch (error) {
    uni.showToast({ title: '发送失败', icon: 'none' })
  }
}

// 加载更多回复
const loadMoreReplies = (comment: any) => {
  // Mock加载更多回复
  const moreReplies = [
    {
      id: Date.now(),
      username: '游客' + Math.floor(Math.random() * 100),
      avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100',
      content: '同感，这里真的很棒！',
      time: '2天前',
      likes: Math.floor(Math.random() * 20),
      isLiked: false,
      replyTo: ''
    }
  ]
  comment.replies.push(...moreReplies)
  
  if (comment.replies.length >= comment.totalReplies) {
    comment.totalReplies = comment.replies.length
  }
}

// 加载更多评论
const loadMoreComments = () => {
  const newComment = {
    id: Date.now(),
    userId: Date.now(),
    userName: '旅行者' + Math.floor(Math.random() * 100),
    userAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100',
    content: '故宫是北京必去的景点，历史底蕴深厚，建筑也很壮观！',
    images: [],
    createTime: '3天前',
    likes: Math.floor(Math.random() * 100),
    isLiked: false,
    replies: [],
    showAllReplies: false
  }
  comments.value.push(newComment)
  
  if (comments.value.length >= 10) {
    hasMoreComments.value = false
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
  padding-bottom: calc(200rpx + env(safe-area-inset-bottom));
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
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
  z-index: 10;
}

.toolbar-btn {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  transition: all 0.2s ease;

  &.back {
    background: rgba(255, 255, 255, 0.9);
    .btn-icon {
      color: $text-primary;
    }
  }

  &.active {
    background: rgba(255, 255, 255, 0.9);
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

// 底部按钮
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  z-index: 900;
}

.start-btn {
  width: 100%;
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
.share-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: $bg-color;
  border-radius: 12rpx;
  margin-bottom: 32rpx;
}

.link-text {
  font-size: 26rpx;
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

.copy-icon {
  font-size: 24rpx;
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

.channel-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &.douyin {
    background: #000000;
  }

  &.wechat {
    background: #07C160;
  }

  &.qq {
    background: #12B7F5;
  }

  &.xiaohongshu {
    background: #FE2C55;
  }
}

.icon-text {
  font-size: 40rpx;
  color: #FFFFFF;
}

.channel-name {
  font-size: 24rpx;
  color: $text-secondary;
}

// 评论区域
.comments-section {
  margin-bottom: 40rpx;
}

.comment-count {
  font-size: 28rpx;
  color: $text-secondary;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.comment-item {
  background: $card-bg;
}

.comment-main {
  display: flex;
  gap: 20rpx;
}

.comment-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.comment-username {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.comment-time {
  font-size: 24rpx;
  color: $text-secondary;
}

.comment-content {
  font-size: 28rpx;
  color: $text-primary;
  line-height: 1.6;
  display: block;
  margin-bottom: 16rpx;
}

.comment-actions {
  display: flex;
  gap: 32rpx;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 0;

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

// 二级评论/回复
.replies-list {
  margin-top: 20rpx;
  padding-left: 20rpx;
  border-left: 4rpx solid #E5E5EA;
}

.reply-item {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 0;

  &:not(:last-child) {
    border-bottom: 1rpx solid #F0F0F0;
  }
}

.reply-avatar {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 8rpx;
  flex-wrap: wrap;
}

.reply-username {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-primary;
}

.reply-to {
  font-size: 24rpx;
  color: $text-secondary;
}

.reply-to-name {
  font-size: 24rpx;
  color: $primary-color;
}

.reply-time {
  font-size: 22rpx;
  color: $text-secondary;
  margin-left: auto;
}

.reply-content {
  font-size: 26rpx;
  color: $text-primary;
  line-height: 1.5;
  display: block;
  margin-bottom: 12rpx;
}

.reply-actions {
  display: flex;
  gap: 24rpx;
}

// 展开更多回复
.expand-replies {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 0;
  
  &:active {
    opacity: 0.7;
  }
}

.expand-text {
  font-size: 26rpx;
  color: $primary-color;
}

.expand-arrow {
  font-size: 20rpx;
  color: $primary-color;
}

// 加载更多评论
.load-more-comments {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
  margin-top: 16rpx;

  &:active {
    opacity: 0.7;
  }
}

.load-more-text {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 500;
}

// 评论输入栏
.comment-input-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 32rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: $card-bg;
  border-top: 1rpx solid #E5E5EA;
  z-index: 100;
  transition: all 0.3s ease;

  &.focus {
    box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.1);
  }
}

.input-wrapper {
  flex: 1;
  padding: 20rpx 28rpx;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
}

.comment-input {
  width: 100%;
  font-size: 28rpx;
  color: $text-primary;
}

.send-btn {
  padding: 20rpx 32rpx;
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
</style>
