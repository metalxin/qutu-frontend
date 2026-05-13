import { get, put, request } from '../request'

export interface MessageItem {
  id: number
  userId: number
  type: 'system' | 'interaction'
  title: string
  content: string
  icon: string
  color: string
  tags: string
  readFlag: number
  bizId: number | null
  bizType: string | null
  createTime: string
}

export interface UnreadCount {
  system: number
  interaction: number
}

export function getMessages(type?: string) {
  return request<MessageItem[]>({
    url: `/admin/message/list${type ? '?type=' + type : ''}`,
    method: 'GET',
    useMock: false
  })
}

export function getUnreadCount() {
  return request<UnreadCount>({
    url: '/admin/message/unread-count',
    method: 'GET',
    useMock: false
  })
}

export function markRead(messageId: number) {
  return request({
    url: `/admin/message/read/${messageId}`,
    method: 'PUT',
    useMock: false
  })
}

export function markAllRead(type?: string) {
  return request({
    url: `/admin/message/read-all${type ? '?type=' + type : ''}`,
    method: 'PUT',
    useMock: false
  })
}

export function deleteReadMessages(type?: string) {
  return request({
    url: `/admin/message/read${type ? '?type=' + type : ''}`,
    method: 'DELETE',
    useMock: false
  })
}
