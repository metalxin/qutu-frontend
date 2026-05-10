/**
 * AI 客服模块 API
 */

import { request } from '../request'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

/**
 * 发送消息给 AI 客服
 */
export function chatWithAI(message: string) {
  return request<string>({
    url: '/admin/planning/chat',
    method: 'POST',
    data: { message },
    timeout: 60000,  // AI 回复可能较慢，设置 60 秒超时
  })
}

/**
 * 发送消息给 AI 客服（带历史记录）
 */
export function chatWithHistory(message: string, history: { role: string; content: string }[]) {
  return request<string>({
    url: '/admin/planning/chat/history',
    method: 'POST',
    data: { message, history },
    timeout: 60000,
  })
}
