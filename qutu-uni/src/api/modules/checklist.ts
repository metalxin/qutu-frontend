/**
 * 清单模块 API - 对接后端真实接口
 * 后端 context-path 为 /admin，所有接口需加 /admin 前缀
 */

import { get, post, put, del } from '../request'

export type ChecklistId = string | number

// 类型定义
export interface ChecklistItem {
  id: ChecklistId
  category: string
  content: string
  checked: number  // 0-未勾，1-已勾
  orderNum: number
}

export interface Checklist {
  id: ChecklistId
  title: string
  destination: string
  departDate: string
  returnDate: string
  status: number  // 0-未开始，1-进行中，2-已完成
  itemsCount: number
  checkedCount: number
  budget: number | null
  createdAt: string
}

export interface ChecklistDetail extends Checklist {
  remark: string
  updatedAt: string
  budget: number | null
  items: ChecklistItem[]
}

export interface ItemCategory {
  id: number
  key: string   // 英文key，与后端 category 字段一致
  name: string  // 中文显示名
  icon: string
}

export interface ChecklistExpense {
  id: ChecklistId
  checklistId: ChecklistId
  category: string
  amount: number
  note: string
  expenseDate: string
}

// 后端分页响应结构
interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// API前缀（后端 context-path）
const API_PREFIX = '/admin'

// 分类映射：英文key → 中文名
export const CATEGORY_MAP: Record<string, string> = {
  document: '个人证件',
  electronics: '电子设备',
  clothing: '衣物用品',
  daily: '生活用品',
  medicine: '药品',
  food: '食品饮料',
  prepare: '出行准备',
  other: '其他',
}

// 反向映射：中文名 → 英文key
export const CATEGORY_REVERSE_MAP: Record<string, string> = Object.fromEntries(
  Object.entries(CATEGORY_MAP).map(([k, v]) => [v, k])
)

/** 将后端 category 英文值转为中文显示名 */
export function categoryLabel(key: string): string {
  return CATEGORY_MAP[key] || key
}

// API方法

/**
 * 获取我的清单列表（分页）
 */
export function getChecklists(params?: { current?: number; size?: number }) {
  return get<PageResult<Checklist>>(`${API_PREFIX}/checklists/page`, params)
}

/**
 * 获取清单详情
 */
export function getChecklistDetail(id: ChecklistId) {
  return get<ChecklistDetail>(`${API_PREFIX}/checklists/${id}`)
}

/**
 * 创建清单
 */
export function createChecklist(data: {
  title: string
  destination?: string
  departDate: string
  returnDate: string
  remark?: string
  items?: { category: string; content: string; checked?: number; orderNum?: number }[]
}) {
  return post<number>(`${API_PREFIX}/checklists`, data)
}

/**
 * 更新清单（含清单项）
 */
export function updateChecklist(id: ChecklistId, data: {
  title?: string
  destination?: string
  departDate?: string
  returnDate?: string
  remark?: string
  items?: { category: string; content: string; checked?: number; orderNum?: number }[]
}) {
  return put<boolean>(`${API_PREFIX}/checklists/${id}`, data)
}

/**
 * 删除清单
 */
export function deleteChecklist(id: ChecklistId) {
  return del<boolean>(`${API_PREFIX}/checklists/${id}`)
}

/**
 * 勾选/取消勾选清单项
 */
export function toggleChecklistItem(itemId: ChecklistId, checked: number) {
  return put<boolean>(`${API_PREFIX}/checklists/items/${itemId}/toggle`, { checked })
}

/**
 * 添加清单项
 */
export function addChecklistItem(checklistId: ChecklistId, data: { category: string; content: string; checked?: number; orderNum?: number }) {
  return post<number>(`${API_PREFIX}/checklists/${checklistId}/items`, data)
}

/**
 * 删除清单项
 */
export function deleteChecklistItem(itemId: ChecklistId) {
  return del<boolean>(`${API_PREFIX}/checklists/items/${itemId}`)
}

/**
 * 获取物品分类（前端本地，无后端接口）
 * key 与后端 ChecklistItemDTO.category 一致
 */
export function getItemCategories(): ItemCategory[] {
  return [
    { id: 1, key: 'document', name: '个人证件', icon: '🪪' },
    { id: 2, key: 'electronics', name: '电子设备', icon: '📱' },
    { id: 3, key: 'clothing', name: '衣物用品', icon: '👕' },
    { id: 4, key: 'daily', name: '生活用品', icon: '🧴' },
    { id: 5, key: 'food', name: '食品饮料', icon: '🍜' },
    { id: 6, key: 'medicine', name: '药品', icon: '💊' },
    { id: 7, key: 'prepare', name: '出行准备', icon: '🎒' },
    { id: 8, key: 'other', name: '其他', icon: '📦' }
  ]
}

/**
 * 获取推荐物品（前端本地，无后端接口）
 */
export function getRecommendedItems(type: 'domestic' | 'international') {
  const items: Record<string, string[]> = {
    domestic: ['身份证', '手机', '充电器', '换洗衣物', '洗漱用品'],
    international: ['护照', '签证', '转换插头', '外币', '旅行保险']
  }
  return items[type] || items.domestic
}

/**
 * 获取费用分类（前端本地，无后端接口）
 */
export function getExpenseCategories() {
  return [
    { id: 1, key: 'transport', name: '交通', icon: '🚌' },
    { id: 2, key: 'accommodation', name: '住宿', icon: '🏠' },
    { id: 3, key: 'food', name: '美食', icon: '🍜' },
    { id: 4, key: 'shopping', name: '购物', icon: '🛍️' },
    { id: 5, key: 'entertainment', name: '娱乐', icon: '🎮' },
    { id: 6, key: 'other', name: '其他', icon: '➕' }
  ]
}

// 费用分类映射：英文key → 中文名
export const EXPENSE_CATEGORY_MAP: Record<string, string> = {
  transport: '交通',
  accommodation: '住宿',
  food: '美食',
  shopping: '购物',
  entertainment: '娱乐',
  other: '其他',
}

// 中文名 → 英文key
export const EXPENSE_CATEGORY_REVERSE_MAP: Record<string, string> = Object.fromEntries(
  Object.entries(EXPENSE_CATEGORY_MAP).map(([k, v]) => [v, k])
)

// ========== 费用 API ==========

/**
 * 获取清单费用列表
 */
export function getExpenses(checklistId: ChecklistId) {
  return get<ChecklistExpense[]>(`${API_PREFIX}/checklists/${checklistId}/expenses`)
}

/**
 * 添加费用
 */
export function addExpense(checklistId: ChecklistId, data: {
  category: string
  amount: number
  note?: string
  expenseDate?: string
}) {
  return post<number>(`${API_PREFIX}/checklists/${checklistId}/expenses`, data)
}

/**
 * 更新费用
 */
export function updateExpense(expenseId: ChecklistId, data: {
  category?: string
  amount?: number
  note?: string
  expenseDate?: string
}) {
  return put<boolean>(`${API_PREFIX}/checklists/expenses/${expenseId}`, data)
}

/**
 * 删除费用
 */
export function deleteExpense(expenseId: ChecklistId) {
  return del<boolean>(`${API_PREFIX}/checklists/expenses/${expenseId}`)
}

/**
 * 获取费用总计
 */
export function getTotalExpense(checklistId: ChecklistId) {
  return get<number>(`${API_PREFIX}/checklists/${checklistId}/expenses/total`)
}

/**
 * 设置预算
 */
export function updateBudget(checklistId: ChecklistId, budget: number) {
  return put<boolean>(`${API_PREFIX}/checklists/${checklistId}/budget`, { budget })
}
