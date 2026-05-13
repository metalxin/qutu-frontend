/**
 * AI 客服模块 API
 */

import { request } from '../request'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

export interface AiConversation {
  id: number
  userId: number
  title: string
  messageCount: number
  lastMessage: string | null
  type: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface AiMessage {
  id: number
  conversationId: number
  role: string
  content: string
  createdAt: string
}

export interface ChatResponse {
  reply: string
  conversationId: number
}

/**
 * 发送消息给 AI 客服（自动保存对话记录）
 */
export function chatWithAI(message: string, userId?: number, conversationId?: number) {
  return request<ChatResponse>({
    url: '/admin/planning/chat',
    method: 'POST',
    data: { message, userId, conversationId },
    timeout: 60000,
  })
}

/**
 * 发送消息给 AI 客服（带历史记录）
 */
export function chatWithHistory(message: string, history: { role: string; content: string }[], userId?: number, conversationId?: number) {
  return request<ChatResponse>({
    url: '/admin/planning/chat/history',
    method: 'POST',
    data: { message, history, userId, conversationId },
    timeout: 60000,
  })
}

/**
 * 获取或创建用户最近的对话
 */
export function getOrCreateConversation(userId: number, conversationId?: number) {
  return request<AiConversation>({
    url: '/admin/ai-chat/conversation',
    method: 'GET',
    data: { userId, conversationId },
    useMock: false,
  })
}

/**
 * 获取用户对话列表
 */
export function getUserConversations(userId: number) {
  return request<AiConversation[]>({
    url: '/admin/ai-chat/conversations',
    method: 'GET',
    data: { userId },
    useMock: false,
  })
}

/**
 * 获取对话消息列表
 */
export function getConversationMessages(conversationId: number) {
  return request<AiMessage[]>({
    url: '/admin/ai-chat/messages',
    method: 'GET',
    data: { conversationId },
    useMock: false,
  })
}
