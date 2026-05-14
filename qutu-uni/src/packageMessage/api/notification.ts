import { request } from '@/api/request'

export interface NotificationItem {
  id: number
  title: string
  content: string
  type: string
  targetUserId: number | null
  linkUrl: string | null
  coverUrl: string | null
  priority: number
  isRead: number
  publishStatus: number
  publishAt: string | null
  createdAt: string
}

export function getNotificationList(params?: { current?: number; size?: number; userId?: number }) {
  return request<{ records: NotificationItem[]; total: number }>({
    url: '/admin/notification/list',
    method: 'GET',
    data: params,
    useMock: false,
  })
}

export function getUnreadCount(userId: number) {
  return request<number>({
    url: '/admin/notification/unread-count',
    method: 'GET',
    data: { userId },
    useMock: false,
  })
}

export function markNotificationRead(id: number, userId: number) {
  return request<boolean>({
    url: `/admin/notification/${id}/read?userId=${userId}`,
    method: 'PUT',
    useMock: false,
  })
}

export function markAllNotificationRead(userId: number) {
  return request<boolean>({
    url: `/admin/notification/read-all?userId=${userId}`,
    method: 'PUT',
    useMock: false,
  })
}
