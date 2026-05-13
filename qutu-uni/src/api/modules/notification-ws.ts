/**
 * 通知 WebSocket 连接管理
 * 连接地址: ws://host:port/admin/ws/notification?userId=xxx
 * 
 * 使用方式：
 * import { notificationWs } from '/@/api/modules/notification-ws'
 * 
 * // 登录后连接
 * notificationWs.connect(userId)
 * 
 * // 监听通知
 * notificationWs.onNotification((data) => { ... })
 * 
 * // 退出时断开
 * notificationWs.disconnect()
 */

import { BASE_URL } from '../request'

type NotificationCallback = (data: any) => void

// WebSocket 消息格式
interface WsMessage {
  type: string
  data: any
}

class NotificationWebSocket {
  private socketTask: UniApp.SocketTask | null = null
  private userId: number | null = null
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null
  private heartbeatTimer: ReturnType<typeof setInterval> | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 10
  private reconnectInterval = 5000
  private heartbeatInterval = 30000
  private listeners: NotificationCallback[] = []
  private connected = false

  /**
   * 建立 WebSocket 连接
   */
  connect(userId: number) {
    if (this.connected && this.userId === userId) return
    this.userId = userId
    this.reconnectAttempts = 0
    this.doConnect()
  }

  private doConnect() {
    if (!this.userId) return

    // 构建 WebSocket URL
    let wsUrl: string
    if (BASE_URL && BASE_URL.startsWith('http')) {
      wsUrl = BASE_URL.replace(/^http/, 'ws') + `/admin/ws/notification?userId=${this.userId}`
    } else {
      // 默认使用当前页面 host
      const protocol = typeof window !== 'undefined' && window.location?.protocol === 'https:' ? 'wss:' : 'ws:'
      const host = typeof window !== 'undefined' ? window.location.host : 'localhost:9999'
      wsUrl = `${protocol}//${host}/admin/ws/notification?userId=${this.userId}`
    }

    console.log('[NotificationWS] 连接:', wsUrl)

    this.socketTask = uni.connectSocket({
      url: wsUrl,
      complete: () => {}
    })

    this.socketTask.onOpen(() => {
      console.log('[NotificationWS] 连接成功')
      this.connected = true
      this.reconnectAttempts = 0
      this.startHeartbeat()
    })

    this.socketTask.onMessage((res) => {
      try {
        const message: WsMessage = JSON.parse(res.data as string)
        if (message.type === 'NOTIFICATION') {
          this.notifyListeners(message.data)
        } else if (message.type === 'PONG') {
          // 心跳响应，忽略
        }
      } catch (e) {
        console.warn('[NotificationWS] 消息解析失败:', res.data)
      }
    })

    this.socketTask.onClose((res) => {
      console.log('[NotificationWS] 连接关闭:', res.code, res.reason)
      this.connected = false
      this.stopHeartbeat()
      // 非主动关闭时自动重连
      if (this.userId && res.code !== 1000) {
        this.scheduleReconnect()
      }
    })

    this.socketTask.onError((err) => {
      console.error('[NotificationWS] 连接错误:', err)
      this.connected = false
    })
  }

  /**
   * 断开连接
   */
  disconnect() {
    this.userId = null
    this.connected = false
    this.stopHeartbeat()
    this.clearReconnectTimer()
    if (this.socketTask) {
      this.socketTask.close({ code: 1000, reason: '主动断开' })
      this.socketTask = null
    }
  }

  /**
   * 注册通知回调
   */
  onNotification(callback: NotificationCallback) {
    this.listeners.push(callback)
    return () => {
      this.listeners = this.listeners.filter(l => l !== callback)
    }
  }

  /**
   * 是否已连接
   */
  isConnected() {
    return this.connected
  }

  private notifyListeners(data: any) {
    this.listeners.forEach(cb => {
      try { cb(data) } catch (e) { console.error('[NotificationWS] 回调执行失败:', e) }
    })
  }

  private startHeartbeat() {
    this.stopHeartbeat()
    this.heartbeatTimer = setInterval(() => {
      if (this.socketTask && this.connected) {
        this.socketTask.send({
          data: JSON.stringify({ type: 'PING' }),
          fail: () => { this.connected = false }
        })
      }
    }, this.heartbeatInterval)
  }

  private stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  private scheduleReconnect() {
    this.clearReconnectTimer()
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.warn('[NotificationWS] 超过最大重连次数，停止重连')
      return
    }
    this.reconnectAttempts++
    const delay = this.reconnectInterval * Math.min(this.reconnectAttempts, 5)
    console.log(`[NotificationWS] ${delay}ms 后重连 (第${this.reconnectAttempts}次)`)
    this.reconnectTimer = setTimeout(() => {
      if (this.userId) this.doConnect()
    }, delay)
  }

  private clearReconnectTimer() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }
}

// 单例导出
export const notificationWs = new NotificationWebSocket()
