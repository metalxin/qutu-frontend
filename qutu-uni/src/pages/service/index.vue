<template>
  <view class="ai-service-page">
    <view class="nav-bar" :style="navBarStyle">
      <view class="nav-left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">AI 客服</text>
      <view class="nav-right" :style="navRightStyle"></view>
    </view>

    <!-- 聊天消息列表 -->
    <scroll-view class="chat-list" scroll-y :scroll-top="scrollTop" scroll-with-animation>
      <view class="chat-item bot" v-for="(msg, index) in messages" :key="index">
        <view class="avatar bot-avatar">
          <text class="avatar-icon">🤖</text>
        </view>
        <view class="bubble bot-bubble">
          <text class="bubble-text">{{ msg.content }}</text>
        </view>
      </view>
      <view class="chat-item user" v-for="(msg, index) in userMessages" :key="'u' + index">
        <view class="bubble user-bubble">
          <text class="bubble-text">{{ msg.content }}</text>
        </view>
        <view class="avatar user-avatar">
          <text class="avatar-icon">😊</text>
        </view>
      </view>
    </scroll-view>

    <!-- 快捷问题 -->
    <view class="quick-questions" v-if="showQuickQuestions">
      <view class="quick-title">常见问题</view>
      <view class="quick-list">
        <view class="quick-item" v-for="(q, i) in quickQuestions" :key="i" @click="sendQuickQuestion(q)">
          <text class="quick-text">{{ q }}</text>
        </view>
      </view>
    </view>

    <!-- 底部输入区 -->
    <view class="input-bar">
      <input class="chat-input" v-model="inputText" placeholder="输入你的问题..." confirm-type="send" @confirm="sendMessage" />
      <view class="send-btn" :class="{ disabled: !inputText.trim() }" @click="sendMessage">
        <text class="send-text">发送</text>
      </view>
    </view>

    <!-- 转人工按钮 -->
    <view class="transfer-btn" @click="transferToHuman">
      <SFIcon name="headset" :size="32" color="#fff" />
      <text class="transfer-text">转人工客服</text>
    </view>

    <!-- 转人工弹窗 -->
    <view class="modal-mask" v-if="showTransferModal" @click="showTransferModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-icon">
          <SFIcon name="headset" :size="64" color="#00C853" />
        </view>
        <text class="modal-title">转人工客服</text>
        <text class="modal-desc">请添加客服 QQ 获取人工服务</text>
        <view class="qq-info">
          <text class="qq-label">客服 QQ：</text>
          <text class="qq-number">{{ qqNumber }}</text>
          <view class="copy-btn" @click="copyQQ">
            <text class="copy-text">复制</text>
          </view>
        </view>
        <view class="modal-btns">
          <view class="modal-btn cancel" @click="showTransferModal = false">取消</view>
          <view class="modal-btn confirm" @click="openQQ">
            <text class="confirm-text">打开 QQ</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'

const statusBarHeight = ref(0)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const scrollTop = ref(0)
const inputText = ref('')
const showTransferModal = ref(false)
const showQuickQuestions = ref(true)
const qqNumber = '2770496921'

interface Message {
  content: string
  type: 'bot' | 'user'
}

const messages = ref<Message[]>([
  { content: '你好！我是 Qutu AI 客服，有什么可以帮助你的吗？', type: 'bot' }
])

const userMessages = ref<Message[]>([])

const quickQuestions = [
  '如何规划行程？',
  '如何添加目的地？',
  'VIP 会员有什么权益？',
  '如何联系客服？',
  '如何备份数据？',
  '如何修改个人资料？'
]

const botReplies: Record<string, string> = {
  '如何规划行程？': '你可以在首页点击「智能规划」，选择目的地和出行日期，AI 会自动为你生成行程方案。',
  '如何添加目的地？': '在「目的地」页面搜索或浏览你感兴趣的地点，点击「添加」即可加入行程。',
  'VIP 会员有什么权益？': 'VIP 会员享有无限次 AI 规划、专属客服、云端备份、无广告等权益。',
  '如何联系客服？': '你可以在当前页面与 AI 客服对话，或点击底部「转人工客服」添加 QQ 联系。',
  '如何备份数据？': '在「设置」页面点击「云端备份」，数据会自动同步到你的账户。',
  '如何修改个人资料？': '在「我的」页面点击「个人资料」，可以修改昵称、头像、手机号等信息。'
}

const navBarStyle = computed(() => {
  return { paddingTop: statusBarHeight.value + 'px', height: navBarHeight.value + 'px' }
})

const navRightStyle = computed(() => {
  return menuButtonSpace.value > 0 ? { width: menuButtonSpace.value + 'px' } : {}
})

const goBack = () => { uni.navigateBack() }

const scrollToBottom = () => {
  nextTick(() => {
    scrollTop.value = 999999
  })
}

const sendMessage = () => {
  const text = inputText.value.trim()
  if (!text) return

  userMessages.value.push({ content: text, type: 'user' })
  inputText.value = ''
  showQuickQuestions.value = false
  scrollToBottom()

  setTimeout(() => {
    const reply = botReplies[text] || '感谢你的提问，这个问题我需要进一步了解。如需人工帮助，请点击底部「转人工客服」。'
    messages.value.push({ content: reply, type: 'bot' })
    scrollToBottom()
  }, 800)
}

const sendQuickQuestion = (q: string) => {
  inputText.value = q
  sendMessage()
}

const transferToHuman = () => {
  showTransferModal.value = true
}

const copyQQ = () => {
  uni.setClipboardData({
    data: qqNumber,
    success: () => {
      uni.showToast({ title: 'QQ 号已复制', icon: 'success' })
    }
  })
}

const openQQ = () => {
  const qqUrl = `mqqwpa://im/chat?chat_type=wpa&uin=${qqNumber}&version=1&src_type=web&web_src=oicqzone.com`
  // #ifdef APP-PLUS
  plus.runtime.openURL(qqUrl, () => {
    uni.showModal({
      title: '提示',
      content: '未检测到 QQ，请手动添加 QQ：' + qqNumber,
      showCancel: false
    })
  })
  // #endif
  // #ifdef H5
  window.open(qqUrl)
  // #endif
  // #ifdef MP-WEIXIN
  uni.setClipboardData({
    data: qqNumber,
    success: () => {
      uni.showModal({
        title: '提示',
        content: '小程序无法直接跳转 QQ，已复制 QQ 号，请打开 QQ 添加好友。',
        showCancel: false
      })
    }
  })
  // #endif

  showTransferModal.value = false
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
})
</script>

<style lang="scss" scoped>
$primary: #00C853;
$text: #1D1D1F;
$sub: #86868B;
$bg: #F5F5F7;
$card: #FFFFFF;
$botBubble: #F0F0F0;
$userBubble: #00C853;

.ai-service-page {
  min-height: 100vh;
  background: $bg;
  display: flex;
  flex-direction: column;
  padding-bottom: 160rpx;
}

.nav-bar {
  padding: 24rpx 24rpx 16rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: $card;
}

.nav-left, .nav-right {
  width: 120rpx;
  display: flex;
  align-items: center;
}

.nav-right {
  justify-content: flex-end;
}

.back-icon {
  font-size: 40rpx;
  color: $text;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text;
}

.chat-list {
  flex: 1;
  padding: 24rpx;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 24rpx;
  gap: 16rpx;
}

.chat-item.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bot-avatar {
  background: #E8F5E9;
}

.user-avatar {
  background: #E3F2FD;
}

.avatar-icon {
  font-size: 32rpx;
}

.bubble {
  max-width: 480rpx;
  padding: 20rpx 24rpx;
  border-radius: 24rpx;
}

.bot-bubble {
  background: $botBubble;
  border-top-left-radius: 4rpx;
}

.user-bubble {
  background: $userBubble;
  border-top-right-radius: 4rpx;
}

.bubble-text {
  font-size: 28rpx;
  line-height: 1.6;
  color: $text;
}

.user-bubble .bubble-text {
  color: #fff;
}

.quick-questions {
  padding: 0 24rpx 24rpx;
}

.quick-title {
  font-size: 24rpx;
  color: $sub;
  margin-bottom: 16rpx;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.quick-item {
  padding: 12rpx 24rpx;
  background: $card;
  border-radius: 32rpx;
  border: 1rpx solid $border;
}

.quick-text {
  font-size: 24rpx;
  color: $primary;
}

.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 24rpx;
  background: $card;
  display: flex;
  align-items: center;
  gap: 16rpx;
  border-top: 1rpx solid #E5E5EA;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.chat-input {
  flex: 1;
  height: 72rpx;
  background: $bg;
  border-radius: 36rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: $text;
}

.send-btn {
  padding: 0 32rpx;
  height: 72rpx;
  background: $primary;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn.disabled {
  background: #C7C7CC;
}

.send-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}

.transfer-btn {
  position: fixed;
  bottom: 140rpx;
  left: 50%;
  transform: translateX(-50%);
  padding: 16rpx 32rpx;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  z-index: 10;
}

.transfer-text {
  font-size: 24rpx;
  color: #fff;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-content {
  width: 560rpx;
  background: $card;
  border-radius: 32rpx;
  padding: 48rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.modal-icon {
  margin-bottom: 24rpx;
}

.modal-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $text;
  margin-bottom: 12rpx;
}

.modal-desc {
  font-size: 26rpx;
  color: $sub;
  margin-bottom: 32rpx;
}

.qq-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 20rpx 24rpx;
  background: $bg;
  border-radius: 16rpx;
  margin-bottom: 32rpx;
}

.qq-label {
  font-size: 26rpx;
  color: $sub;
}

.qq-number {
  font-size: 32rpx;
  font-weight: 600;
  color: $primary;
}

.copy-btn {
  padding: 8rpx 20rpx;
  background: $primary;
  border-radius: 12rpx;
}

.copy-text {
  font-size: 22rpx;
  color: #fff;
}

.modal-btns {
  display: flex;
  gap: 20rpx;
  width: 100%;
}

.modal-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.modal-btn.cancel {
  background: $bg;
  color: $sub;
}

.modal-btn.confirm {
  background: $primary;
}

.confirm-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}
</style>
