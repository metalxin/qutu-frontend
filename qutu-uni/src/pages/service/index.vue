<template>
  <view class="service-page">
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <view class="nav-center">
        <view class="nav-avatar">
          <text class="nav-avatar-emoji">🌴</text>
        </view>
        <view class="nav-info">
          <text class="nav-title">小途助手</text>
          <view class="nav-status-row">
            <view class="status-dot" :class="{ online: !isThinking }"></view>
            <text class="nav-status">{{ isThinking ? '思考中...' : '在线' }}</text>
          </view>
        </view>
      </view>
      <view class="nav-right" :style="navRightStyle"></view>
    </view>

    <scroll-view
      class="chat-list"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
      :show-scrollbar="false"
    >
      <view class="hero-card">
        <view class="hero-content">
          <text class="hero-greeting">你好呀！</text>
          <text class="hero-title">我是小途助手🌴</text>
          <text class="hero-desc">你的专属旅行搭子，有什么旅行问题尽管问我吧～</text>
          <view class="hero-tags">
            <view class="hero-tag green">
              <text class="hero-tag-icon">🗺️</text>
              <text class="hero-tag-text">路线规划</text>
            </view>
            <view class="hero-tag blue">
              <text class="hero-tag-icon">📍</text>
              <text class="hero-tag-text">景点推荐</text>
            </view>
            <view class="hero-tag orange">
              <text class="hero-tag-icon">🏨</text>
              <text class="hero-tag-text">住宿建议</text>
            </view>
          </view>
        </view>
        <view class="hero-illustration">
          <view class="palm-tree">
            <view class="palm-leaves">
              <view class="leaf leaf-1"></view>
              <view class="leaf leaf-2"></view>
              <view class="leaf leaf-3"></view>
              <view class="leaf leaf-4"></view>
              <view class="leaf leaf-5"></view>
            </view>
            <view class="palm-trunk"></view>
            <view class="palm-face">
              <view class="eye eye-left"></view>
              <view class="eye eye-right"></view>
              <view class="mouth"></view>
            </view>
          </view>
          <view class="island"></view>
          <view class="cloud cloud-1"></view>
          <view class="cloud cloud-2"></view>
        </view>
      </view>

      <view class="quick-section-title">
        <text class="quick-leaf">🌿</text>
        <text class="quick-title-text">你可以这样问我</text>
        <text class="quick-leaf">🌿</text>
      </view>

      <view class="quick-list">
        <view
          class="quick-item"
          v-for="(item, i) in quickQuestions"
          :key="i"
          @click="sendQuickQuestion(item.question)"
        >
          <view class="quick-item-icon" :style="{ background: item.bgColor }">
            <text class="quick-item-emoji">{{ item.icon }}</text>
          </view>
          <view class="quick-item-content">
            <text class="quick-item-title">{{ item.question }}</text>
            <text class="quick-item-desc">{{ item.desc }}</text>
          </view>
          <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
        </view>
      </view>

      <view class="chat-messages" v-if="chatMessages.length > 0">
        <view
          class="chat-item"
          :class="msg.role"
          v-for="(msg, index) in chatMessages"
          :key="index"
        >
          <view class="avatar" :class="msg.role === 'bot' ? 'bot-avatar' : 'user-avatar'">
            <image v-if="msg.role === 'user' && userAvatar" class="avatar-image" :src="userAvatar" mode="aspectFill" />
            <text v-else-if="msg.role === 'bot'" class="avatar-emoji">🌴</text>
            <text v-else class="avatar-emoji">😊</text>
          </view>
          <view class="bubble-wrapper">
            <view class="bubble" :class="msg.role === 'bot' ? 'bot-bubble' : 'user-bubble'">
              <text class="bubble-text" :class="{ 'user-text': msg.role === 'user' }">{{ msg.content }}</text>
            </view>
            <text class="bubble-time" :class="{ 'time-right': msg.role === 'user' }">{{ msg.time }}</text>
          </view>
        </view>

        <view class="chat-item bot" v-if="isThinking">
          <view class="avatar bot-avatar">
            <text class="avatar-emoji">🌴</text>
          </view>
          <view class="bubble bot-bubble thinking-bubble">
            <view class="typing-dots">
              <view class="dot"></view>
              <view class="dot"></view>
              <view class="dot"></view>
            </view>
          </view>
        </view>

        <view class="chat-item bot" v-if="isStreaming">
          <view class="avatar bot-avatar">
            <text class="avatar-emoji">🌴</text>
          </view>
          <view class="bubble bot-bubble">
            <text class="bubble-text">{{ streamingText }}<text class="cursor-blink">|</text></text>
          </view>
        </view>
      </view>

      <view class="chat-bottom-spacer"></view>
    </scroll-view>

    <view class="bottom-area">
      <view class="input-bar" :class="{ focus: isInputFocus }">
        <view class="input-wrapper">
          <input
            class="chat-input"
            v-model="inputText"
            placeholder="输入你的旅行问题..."
            placeholder-style="color:#C7C7CC;"
            confirm-type="send"
            :adjust-position="false"
            @confirm="sendMessage"
            @focus="isInputFocus = true"
            @blur="isInputFocus = false"
          />
        </view>
        <view class="send-btn" :class="{ active: inputText.trim() }" @click="sendMessage">
          <SFIcon name="send" :size="32" color="#FFFFFF" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { chatWithAI, getOrCreateConversation, getConversationMessages } from '@/api'
import type { AiMessage } from '@/api'

const statusBarHeight = ref(44)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const scrollTop = ref(0)
const inputText = ref('')
const showQuickQuestions = ref(true)
const isThinking = ref(false)
const isInputFocus = ref(false)
const userAvatar = ref('')
const currentConversationId = ref<number | null>(null)

const isStreaming = ref(false)
const streamingText = ref('')
let timer: ReturnType<typeof setInterval> | null = null

interface ChatMessage {
  content: string
  role: 'bot' | 'user'
  time: string
}

const chatMessages = ref<ChatMessage[]>([])

const quickQuestions = [
  { icon: '️', question: '如何规划行程？', desc: '路线安排 · 天数规划 · 交通建议', bgColor: '#E8F5E9' },
  { icon: '📷', question: '推荐几个景点', desc: '热门景点 · 小众打卡 · 特色体验', bgColor: '#E8EAF6' },
  { icon: '👑', question: 'VIP 有什么权益？', desc: '专属特权 · 会员福利 · 积分说明', bgColor: '#FFF8E1' },
  { icon: '💬', question: '怎么联系客服', desc: '在线客服 · 电话联系 · 服务时间', bgColor: '#E8F5E9' },
  { icon: '☁️', question: '如何备份数据？', desc: '数据同步 · 备份恢复 · 账号安全', bgColor: '#E3F2FD' },
  { icon: '✏️', question: '怎么修改资料？', desc: '个人信息 · 账号设置 · 修改密码', bgColor: '#FFF3E0' }
]

function getTimeStr(): string {
  const now = new Date()
  const h = String(now.getHours()).padStart(2, '0')
  const m = String(now.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

function formatTimeStr(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

const navBarStyle = computed(() => ({
  paddingTop: statusBarHeight.value + 'px',
  height: navBarHeight.value + 'px'
}))

const navRightStyle = computed(() =>
  menuButtonSpace.value > 0 ? { width: menuButtonSpace.value + 'px' } : {}
)

const goBack = () => { uni.navigateBack() }

const scrollToBottom = () => {
  nextTick(() => {
    scrollTop.value = scrollTop.value === 0 ? 1 : 0
    nextTick(() => {
      scrollTop.value = 999999
    })
  })
}

const startStreaming = (fullText: string) => {
  stopStreaming()
  isStreaming.value = true
  streamingText.value = ''
  let index = 0

  timer = setInterval(() => {
    if (index < fullText.length) {
      streamingText.value += fullText[index]
      index++
      scrollToBottom()
    } else {
      stopStreaming()
      chatMessages.value.push({ content: fullText, role: 'bot', time: getTimeStr() })
      scrollToBottom()
    }
  }, 40)
}

const stopStreaming = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  isStreaming.value = false
  streamingText.value = ''
}

const getUserId = (): number | null => {
  const id = uni.getStorageSync('userId')
  return id ? Number(id) : null
}

const loadHistory = async () => {
  const userId = getUserId()
  if (!userId) return

  try {
    const conversation = await getOrCreateConversation(userId)
    if (conversation && conversation.id) {
      currentConversationId.value = conversation.id
      const messages = await getConversationMessages(conversation.id)
      if (messages && messages.length > 0) {
        chatMessages.value = messages.map((m: AiMessage) => ({
          content: m.content,
          role: m.role === 'user' ? 'user' as const : 'bot' as const,
          time: formatTimeStr(m.createdAt)
        }))
        showQuickQuestions.value = false
        scrollToBottom()
      }
    }
  } catch (e) {
    console.warn('[小途助手] 加载历史对话失败:', e)
  }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || isThinking.value || isStreaming.value) return

  chatMessages.value.push({ content: text, role: 'user', time: getTimeStr() })
  inputText.value = ''
  showQuickQuestions.value = false
  isThinking.value = true
  scrollToBottom()

  const userId = getUserId()

  try {
    const res = await chatWithAI(text, userId || undefined, currentConversationId.value || undefined)
    isThinking.value = false
    // 更新当前对话ID
    if (res && res.conversationId) {
      currentConversationId.value = res.conversationId
    }
    const reply = res?.reply || res as any
    if (reply) {
      startStreaming(reply)
    } else {
      chatMessages.value.push({ content: '抱歉，小途暂时无法回答这个问题，请稍后再试～', role: 'bot', time: getTimeStr() })
      scrollToBottom()
    }
  } catch (error: any) {
    isThinking.value = false
    const errMsg = error?.msg || '网络开小差了，请稍后再试～'
    chatMessages.value.push({ content: errMsg, role: 'bot', time: getTimeStr() })
    scrollToBottom()
  }
}

const sendQuickQuestion = (q: string) => {
  inputText.value = q
  sendMessage()
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    menuButtonSpace.value = systemInfo.windowWidth - menuButton.left + 10
    navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
  } catch (e) {}
  // #endif

  try {
    const cached = uni.getStorageSync('userInfo')
    if (cached && cached.avatar) {
      userAvatar.value = cached.avatar
    }
  } catch (e) {}

  // 加载历史对话
  loadHistory()
})

onUnmounted(() => {
  stopStreaming()
})
</script>

<style lang="scss" scoped>
$text: #1D1D1F;
$sub: #8E8E93;
$bg: #F0F7F4;
$card: #FFFFFF;
$border: #E5E5EA;
$bot-bubble-bg: #FFFFFF;
$shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

.service-page {
  min-height: 100vh;
  background: $bg;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.nav-back {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-center {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding-left: 8rpx;
}

.nav-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-avatar-emoji {
  font-size: 32rpx;
}

.nav-info {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 700;
  color: $text;
}

.nav-status-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #C7C7CC;
  transition: background 0.3s ease;

  &.online {
    background: #34C759;
    box-shadow: 0 0 8rpx rgba(52, 199, 89, 0.4);
  }
}

.nav-status {
  font-size: 20rpx;
  color: $sub;
}

.nav-right {
  width: 64rpx;
  flex-shrink: 0;
}

.chat-list {
  flex: 1;
  padding: 0 24rpx;
  overflow-y: auto;
  box-sizing: border-box;
}

.hero-card {
  margin: 20rpx 0 28rpx;
  background: linear-gradient(180deg, #E8F5E9 0%, #C8E6C9 40%, #A5D6A7 100%);
  border-radius: 24rpx;
  padding: 36rpx 28rpx;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  min-height: 280rpx;
}

.hero-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(ellipse at 20% 80%, rgba(255,255,255,0.3) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(255,255,255,0.2) 0%, transparent 40%);
  pointer-events: none;
}

.hero-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  position: relative;
  z-index: 1;
}

.hero-greeting {
  font-size: 36rpx;
  color: $text;
  font-weight: 500;
}

.hero-title {
  font-size: 36rpx;
  color: $text;
  font-weight: 700;
  line-height: 1.3;
}

.hero-desc {
  font-size: 24rpx;
  color: #4A6741;
  line-height: 1.5;
  margin-top: 4rpx;
}

.hero-tags {
  display: flex;
  gap: 12rpx;
  margin-top: 20rpx;
  flex-wrap: wrap;
}

.hero-tag {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 10rpx 18rpx;
  border-radius: 24rpx;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  &.green {
    background: rgba(76, 175, 80, 0.15);
  }

  &.blue {
    background: rgba(66, 133, 244, 0.15);
  }

  &.orange {
    background: rgba(255, 152, 0, 0.15);
  }
}

.hero-tag-icon {
  font-size: 22rpx;
}

.hero-tag-text {
  font-size: 22rpx;
  color: #2D5A3D;
  font-weight: 500;
}

.hero-illustration {
  position: relative;
  width: 180rpx;
  height: 200rpx;
  flex-shrink: 0;
  z-index: 1;
}

.palm-tree {
  position: absolute;
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
}

.palm-trunk {
  width: 16rpx;
  height: 80rpx;
  background: linear-gradient(180deg, #8D6E63, #6D4C41);
  border-radius: 8rpx;
  margin: 0 auto;
}

.palm-leaves {
  position: relative;
  width: 100rpx;
  height: 60rpx;
  margin: 0 auto -10rpx;
}

.leaf {
  position: absolute;
  width: 50rpx;
  height: 20rpx;
  background: #4CAF50;
  border-radius: 50% 50% 50% 0;
  transform-origin: left center;

  &.leaf-1 {
    top: 10rpx;
    left: 25rpx;
    transform: rotate(-30deg);
    background: #66BB6A;
  }

  &.leaf-2 {
    top: 5rpx;
    left: 30rpx;
    transform: rotate(-60deg);
    background: #4CAF50;
  }

  &.leaf-3 {
    top: 15rpx;
    left: 20rpx;
    transform: rotate(0deg);
    background: #81C784;
  }

  &.leaf-4 {
    top: 20rpx;
    left: 25rpx;
    transform: rotate(30deg);
    background: #4CAF50;
  }

  &.leaf-5 {
    top: 25rpx;
    left: 30rpx;
    transform: rotate(60deg);
    background: #66BB6A;
  }
}

.palm-face {
  position: absolute;
  top: 30rpx;
  left: 50%;
  transform: translateX(-50%);
}

.eye {
  position: absolute;
  width: 6rpx;
  height: 8rpx;
  background: #333;
  border-radius: 50%;

  &.eye-left {
    left: -8rpx;
  }

  &.eye-right {
    right: -8rpx;
  }
}

.mouth {
  width: 12rpx;
  height: 6rpx;
  border-bottom: 3rpx solid #333;
  border-radius: 0 0 50% 50%;
  margin: 12rpx auto 0;
}

.island {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 120rpx;
  height: 30rpx;
  background: linear-gradient(180deg, #FFE082, #FFD54F);
  border-radius: 50%;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50rpx;

  &.cloud-1 {
    top: 20rpx;
    right: 10rpx;
    width: 60rpx;
    height: 24rpx;
  }

  &.cloud-2 {
    top: 50rpx;
    right: 40rpx;
    width: 40rpx;
    height: 16rpx;
  }
}

.quick-section-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.quick-leaf {
  font-size: 24rpx;
}

.quick-title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: $text;
}

.quick-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.quick-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx;
  background: $card;
  border-radius: 20rpx;
  box-shadow: $shadow-sm;
  transition: all 0.2s ease;

  &:active {
    background: #F5F5F5;
    transform: scale(0.98);
  }
}

.quick-item-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.quick-item-emoji {
  font-size: 32rpx;
}

.quick-item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.quick-item-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text;
}

.quick-item-desc {
  font-size: 22rpx;
  color: $sub;
}

.chat-messages {
  margin-top: 24rpx;
}

.chat-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 24rpx;
  gap: 12rpx;
  padding: 0 4rpx;
  box-sizing: border-box;

  &.user {
    flex-direction: row-reverse;
  }
}

.avatar {
  width: 64rpx;
  height: 64rpx;
  min-width: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  margin-top: 4rpx;
}

.bot-avatar {
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.12);
}

.user-avatar {
  background: linear-gradient(135deg, #E8F0FE, #C5DBF8);
  box-shadow: 0 4rpx 12rpx rgba(66, 133, 244, 0.12);
}

.avatar-emoji {
  font-size: 30rpx;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.bubble-wrapper {
  display: flex;
  flex-direction: column;
  flex: 0 1 auto;
  max-width: calc(100% - 80rpx);
  min-width: 0;
}

.user .bubble-wrapper {
  align-items: flex-end;
}

.bubble {
  padding: 18rpx 22rpx;
  border-radius: 20rpx;
  position: relative;
  overflow-wrap: break-word;
  word-break: break-word;
  width: fit-content;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.bot-bubble {
  background: $bot-bubble-bg;
  border-top-left-radius: 6rpx;
  box-shadow: $shadow-sm;
}

.user-bubble {
  background: linear-gradient(135deg, #FF6B3D, #FF8A50);
  border-top-right-radius: 6rpx;
  box-shadow: 0 4rpx 16rpx rgba(255, 107, 61, 0.25);
}

.bubble-text {
  font-size: 28rpx;
  line-height: 1.65;
  color: $text;
  word-break: break-word;
  white-space: pre-wrap;
}

.user-text {
  color: #FFFFFF;
}

.bubble-time {
  font-size: 20rpx;
  color: #C7C7CC;
  margin-top: 6rpx;
  padding: 0 8rpx;

  &.time-right {
    text-align: right;
    color: rgba(142, 142, 147, 0.7);
  }
}

.thinking-bubble {
  padding: 24rpx 32rpx;
  min-width: 100rpx;
}

.typing-dots {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  background: #81C784;
  border-radius: 50%;
  animation: dotBounce 1.4s infinite ease-in-out both;

  &:nth-child(1) { animation-delay: -0.32s; }
  &:nth-child(2) { animation-delay: -0.16s; }
  &:nth-child(3) { animation-delay: 0s; }
}

@keyframes dotBounce {
  0%, 80%, 100% { transform: scale(0.5); opacity: 0.3; }
  40% { transform: scale(1); opacity: 1; }
}

.cursor-blink {
  font-weight: 300;
  color: #4CAF50;
  animation: blink 0.8s step-end infinite;
}

@keyframes blink {
  50% { opacity: 0; }
}

.chat-bottom-spacer {
  height: 140rpx;
}

.bottom-area {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  background: $card;
  border-top: 1rpx solid $border;
}

.input-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: $card;
  transition: all 0.2s ease;

  &.focus {
    box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.06);
  }
}

.input-wrapper {
  flex: 1;
  background: $bg;
  border-radius: 44rpx;
  padding: 0 28rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  transition: all 0.2s ease;
}

.chat-input {
  flex: 1;
  font-size: 28rpx;
  color: $text;
  height: 72rpx;
  line-height: 72rpx;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #C7C7CC;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;

  &.active {
    background: linear-gradient(135deg, #4CAF50, #66BB6A);
    box-shadow: 0 4rpx 16rpx rgba(76, 175, 80, 0.3);
  }

  &:active {
    transform: scale(0.9);
  }
}
</style>
