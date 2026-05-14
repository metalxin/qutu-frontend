<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar" :style="navBarStyle">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
      <text class="nav-title">旅行清单</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-card" :style="{ paddingTop: navBarTotalHeight + 'px' }">
      <view class="stats-row">
        <text class="stats-label">已定制清单：</text>
        <text class="stats-value">{{ checklistData.length }}</text>
      </view>
      <view class="stats-row">
        <text class="stats-label">已完成清单：</text>
        <text class="stats-value">{{ completedCount }}</text>
      </view>
    </view>

    <!-- 记录清单标题 -->
    <view class="section-title">
      <text class="title-text">记录清单</text>
    </view>

    <!-- 清单网格 -->
    <view class="checklist-grid">
      <!-- 添加清单卡片 -->
      <view class="checklist-card add-card" @click="openAddMenu">
        <view class="add-icon-wrapper">
          <text class="add-icon">+</text>
        </view>
        <text class="add-text">添加记录清单</text>
      </view>

      <!-- 已有清单 -->
      <view 
        class="checklist-card" 
        v-for="item in checklistData" 
        :key="item.id"
        @click="goToDetail(item)"
      >
        <view class="card-content">
          <view class="card-title-row">
            <text class="card-title">{{ item.title }}</text>
            <view class="card-status" :class="'status-' + item.status">
              <text class="status-text">{{ getStatusLabel(item.status) }}</text>
            </view>
          </view>
          <text class="card-destination" v-if="item.destination">📍 {{ item.destination }}</text>
          <view class="card-dates">
            <text class="date-row">始：{{ (item.departDate || '').replace(/-/g, '/') }}</text>
            <text class="date-row">末：{{ (item.returnDate || '').replace(/-/g, '/') }}</text>
          </view>
          <view class="card-progress" v-if="item.itemsCount > 0">
            <text class="progress-text">{{ item.checkedCount }}/{{ item.itemsCount }}</text>
          </view>
        </view>
        <view class="card-edit" @click.stop="editChecklist(item)">
          <text class="edit-icon">✏️</text>
        </view>
        <view class="card-delete" @click.stop="confirmDelete(item)">
          <text class="delete-icon">×</text>
        </view>
      </view>
    </view>

    <!-- 添加清单弹窗 -->
    <view class="popup-mask" v-if="showAddPopup" @click="closeAddPopup"></view>
    <view class="add-popup" :class="{ 'popup-show': showAddPopup }">
      <!-- 返回按钮 -->
      <view class="popup-back" @click="closeAddPopup">
        <text class="popup-back-icon">‹</text>
      </view>

      <!-- 顶部图片 -->
      <view class="popup-banner">
        <image class="banner-image" src="https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800&q=80" mode="aspectFill" />
      </view>

      <!-- 表单 -->
      <view class="popup-form">
        <view class="form-group">
          <text class="form-label">旅游名称</text>
          <view class="form-input-wrapper">
            <input 
              class="form-input" 
              v-model="formData.name"
              placeholder="为旅行起一个名字吧"
              :maxlength="12"
            />
            <text class="input-count">{{ formData.name.length }}/12</text>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">目的地</text>
          <view class="form-input-wrapper">
            <input 
              class="form-input" 
              v-model="formData.destination"
              placeholder="请输入目的地（选填）"
              :maxlength="20"
            />
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">开始时间</text>
          <picker mode="date" :value="formData.startDate" @change="onStartDateChange">
            <view class="form-input-wrapper picker">
              <text class="picker-text" :class="{ placeholder: !formData.startDate }">
                {{ formData.startDate ? formData.startDate.replace(/-/g, '/') : '请选择旅行开始时间' }}
              </text>
              <text class="picker-icon">📅</text>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">结束时间</text>
          <picker mode="date" :value="formData.endDate" @change="onEndDateChange">
            <view class="form-input-wrapper picker">
              <text class="picker-text" :class="{ placeholder: !formData.endDate }">
                {{ formData.endDate ? formData.endDate.replace(/-/g, '/') : '请选择旅行结束时间' }}
              </text>
              <text class="picker-icon">📅</text>
            </view>
          </picker>
        </view>

        <view class="form-submit" @click="submitChecklist">
          <text class="submit-text">确定</text>
        </view>
      </view>
    </view>

    <!-- 添加方式选择弹窗 -->
    <view class="popup-mask" v-if="showAddMenu" @click="closeAddMenu"></view>
    <view class="add-menu-popup" :class="{ 'popup-show': showAddMenu }">
      <view class="menu-header">
        <text class="menu-title">添加清单</text>
        <view class="menu-close" @click="closeAddMenu">
          <text class="close-icon">×</text>
        </view>
      </view>
      <view class="menu-options">
        <view class="menu-option" @click="onMenuManual">
          <view class="option-icon-wrap" style="background: rgba(0,184,169,0.1);">
            <text class="option-icon-text" style="color: #00B8A9;">✍</text>
          </view>
          <view class="option-info">
            <text class="option-title">手动创建</text>
            <text class="option-desc">填写旅行信息手动创建清单</text>
          </view>
          <text class="option-arrow">›</text>
        </view>
        <view class="menu-option" @click="onMenuSmartImport">
          <view class="option-icon-wrap" style="background: rgba(88,86,214,0.1);">
            <text class="option-icon-text" style="color: #5856D6;">🧠</text>
          </view>
          <view class="option-info">
            <text class="option-title">智能导入</text>
            <text class="option-desc">输入文字自动识别目的地和物品</text>
          </view>
          <text class="option-arrow">›</text>
        </view>
        <view class="menu-option" @click="onMenuUseCode">
          <view class="option-icon-wrap" style="background: rgba(255,149,0,0.1);">
            <text class="option-icon-text" style="color: #FF9500;">🔑</text>
          </view>
          <view class="option-info">
            <text class="option-title">使用口令</text>
            <text class="option-desc">输入口令导入他人分享的清单</text>
          </view>
          <text class="option-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 智能导入弹窗 -->
    <view class="popup-mask" v-if="showSmartImport" @click="closeSmartImport"></view>
    <view class="smart-import-popup" :class="{ 'popup-show': showSmartImport }">
      <view class="si-header" :style="navBarStyle">
        <view class="si-back" @click="closeSmartImport">
          <text class="popup-back-icon">‹</text>
        </view>
        <text class="si-title">智能导入</text>
        <view class="si-placeholder"></view>
      </view>
      <scroll-view scroll-y class="si-body">
        <view class="si-tip">
          <text class="si-tip-text">💡 输入你的旅行计划，系统将自动识别目的地、日期和需要携带的物品</text>
        </view>
        <view class="si-input-area">
          <textarea 
            class="si-textarea" 
            v-model="smartImportText"
            placeholder="例如：我要去杭州3天，带身份证、充电器、换洗衣物"
            :maxlength="500"
            auto-height
            :style="{ minHeight: '200rpx' }"
          />
          <text class="si-text-count">{{ smartImportText.length }}/500</text>
        </view>
        <view class="si-examples">
          <text class="si-examples-title">试试这样说：</text>
          <view class="si-example-tag" v-for="(ex, i) in smartExamples" :key="i" @click="smartImportText = ex">
            <text class="si-example-text">{{ ex }}</text>
          </view>
        </view>
        <view class="si-submit" :class="{ disabled: !smartImportText.trim() || smartImportLoading }" @click="doSmartImport">
          <text class="si-submit-text">{{ smartImportLoading ? '识别中...' : '开始识别' }}</text>
        </view>

        <!-- 识别结果 -->
        <view class="si-result" v-if="smartImportResult">
          <view class="si-result-header">
            <text class="si-result-title">识别结果</text>
            <text class="si-result-edit" @click="editSmartResult = !editSmartResult">{{ editSmartResult ? '完成' : '编辑' }}</text>
          </view>
          <view class="si-result-field">
            <text class="si-result-label">标题</text>
            <input v-if="editSmartResult" class="si-result-input" v-model="smartImportResult.title" />
            <text v-else class="si-result-value">{{ smartImportResult.title }}</text>
          </view>
          <view class="si-result-field">
            <text class="si-result-label">目的地</text>
            <input v-if="editSmartResult" class="si-result-input" v-model="smartImportResult.destination" />
            <text v-else class="si-result-value">{{ smartImportResult.destination || '未识别' }}</text>
          </view>
          <view class="si-result-field">
            <text class="si-result-label">出发</text>
            <picker v-if="editSmartResult" mode="date" :value="smartImportResult!.departDate" @change="(e:any) => smartImportResult!.departDate = e.detail.value">
              <text class="si-result-value picker-val">{{ smartImportResult!.departDate || '未设置' }}</text>
            </picker>
            <text v-else class="si-result-value">{{ smartImportResult!.departDate || '未识别' }}</text>
          </view>
          <view class="si-result-field">
            <text class="si-result-label">返回</text>
            <picker v-if="editSmartResult" mode="date" :value="smartImportResult!.returnDate" @change="(e:any) => smartImportResult!.returnDate = e.detail.value">
              <text class="si-result-value picker-val">{{ smartImportResult!.returnDate || '未设置' }}</text>
            </picker>
            <text v-else class="si-result-value">{{ smartImportResult!.returnDate || '未识别' }}</text>
          </view>
          <view class="si-result-items" v-if="smartImportResult.items.length > 0">
            <text class="si-result-label">物品清单（{{ smartImportResult.items.length }}项）</text>
            <view class="si-item-group" v-for="group in smartItemGroups" :key="group.category">
              <text class="si-group-name">{{ group.name }}</text>
              <view class="si-item-list">
                <view class="si-item-chip" v-for="(item, idx) in group.items" :key="idx">
                  <text class="si-item-text">{{ item.content }}</text>
                  <view class="si-item-remove" v-if="editSmartResult" @click="removeSmartItem(group.category, idx)">
                    <text class="si-item-remove-text">×</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <view class="si-confirm" @click="confirmSmartImport">
            <text class="si-confirm-text">确认创建清单</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 使用口令弹窗 -->
    <view class="popup-mask" v-if="showUseCode" @click="closeUseCode"></view>
    <view class="use-code-popup" :class="{ 'popup-show': showUseCode }">
      <view class="uc-header" :style="navBarStyle">
        <view class="uc-back" @click="closeUseCode">
          <text class="popup-back-icon">‹</text>
        </view>
        <text class="uc-title">使用口令</text>
        <view class="uc-placeholder"></view>
      </view>
      <scroll-view scroll-y class="uc-body">
        <view class="uc-tip">
          <text class="uc-tip-text">🔑 输入好友分享的口令，即可快速复制清单</text>
        </view>
        <view class="uc-input-area">
          <input 
            class="uc-input" 
            v-model="shareCodeInput"
            placeholder="请输入6位口令"
            :maxlength="8"
          />
        </view>
        <view class="uc-submit" :class="{ disabled: !shareCodeInput.trim() || useCodeLoading }" @click="doUseCode">
          <text class="uc-submit-text">{{ useCodeLoading ? '导入中...' : '导入清单' }}</text>
        </view>

        <!-- 导入结果 -->
        <view class="uc-result" v-if="useCodeResult">
          <view class="uc-result-header">
            <text class="uc-result-title">清单预览</text>
          </view>
          <view class="uc-result-field">
            <text class="uc-result-label">标题</text>
            <text class="uc-result-value">{{ useCodeResult.title }}</text>
          </view>
          <view class="uc-result-field" v-if="useCodeResult.destination">
            <text class="uc-result-label">目的地</text>
            <text class="uc-result-value">{{ useCodeResult.destination }}</text>
          </view>
          <view class="uc-result-field" v-if="useCodeResult.remark">
            <text class="uc-result-label">备注</text>
            <text class="uc-result-value">{{ useCodeResult.remark }}</text>
          </view>
          <view class="uc-result-items" v-if="useCodeResult.items.length > 0">
            <text class="uc-result-label">物品清单（{{ useCodeResult.items.length }}项）</text>
            <view class="uc-item-group" v-for="group in useCodeItemGroups" :key="group.category">
              <text class="uc-group-name">{{ group.name }}</text>
              <view class="uc-item-list">
                <view class="uc-item-chip" v-for="(item, idx) in group.items" :key="idx">
                  <text class="uc-item-text">{{ item.content }}</text>
                </view>
              </view>
            </view>
          </view>
          <view class="uc-confirm" @click="confirmUseCode">
            <text class="uc-confirm-text">确认创建清单</text>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'

// 胶囊按钮适配
const statusBarHeight = ref(0)
const menuButtonTop = ref(0)
const menuButtonHeight = ref(32)
const menuButtonLeft = ref(0)
const windowWidth = ref(375)

const navBarHeight = ref(44)

const navBarTotalHeight = computed(() => {
  // #ifdef MP-WEIXIN
  if (menuButtonTop.value > 0) {
    const contentBottom = menuButtonTop.value + menuButtonHeight.value
    return contentBottom + (menuButtonTop.value - statusBarHeight.value)
  }
  // #endif
  return navBarHeight.value + statusBarHeight.value
})

const navBarStyle = computed(() => {
  // #ifdef MP-WEIXIN
  if (menuButtonTop.value > 0) {
    // 导航栏内容区域与胶囊按钮垂直居中对齐
    const contentTop = menuButtonTop.value
    const contentBottom = menuButtonTop.value + menuButtonHeight.value
    const totalHeight = contentBottom + (menuButtonTop.value - statusBarHeight.value)
    return {
      paddingTop: contentTop + 'px',
      height: totalHeight + 'px',
      paddingRight: (windowWidth.value - menuButtonLeft.value + 10) + 'px'
    }
  }
  // #endif
  return {
    paddingTop: statusBarHeight.value + 'px',
    height: navBarHeight.value + 'px'
  }
})
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getChecklists, createChecklist, updateChecklist, deleteChecklist, smartImport, importByShareCode } from '@/api'
import type { Checklist } from '@/api/modules/checklist'
import type { SmartImportResult, ShareCodeImportResult } from '@/api/modules/checklist'

// 清单数据
const checklistData = ref<Checklist[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 计算已完成清单数
const completedCount = computed(() => {
  return checklistData.value.filter(item => item.status === 2).length
})

// 弹窗控制
const showAddPopup = ref(false)
const showAddMenu = ref(false)
const showSmartImport = ref(false)
const showUseCode = ref(false)
const editingId = ref<string | number | null>(null)

// 表单数据
const formData = ref({
  name: '',
  destination: '',
  startDate: '',
  endDate: ''
})

const getErrorMessage = (error: any, fallback = '操作失败') => {
  return error?.response?.msg || error?.response?.message || error?.message || fallback
}

const getStatusLabel = (status: number) => {
  const map: Record<number, string> = { 0: '未开始', 1: '进行中', 2: '已完成' }
  return map[status] || '未知'
}

const resetForm = () => {
  formData.value = { name: '', destination: '', startDate: '', endDate: '' }
  editingId.value = null
}

const closeAddPopup = () => {
  showAddPopup.value = false
  resetForm()
}

// ========== 添加方式菜单 ==========
const openAddMenu = () => {
  showAddMenu.value = true
}

const closeAddMenu = () => {
  showAddMenu.value = false
}

const onMenuManual = () => {
  closeAddMenu()
  resetForm()
  showAddPopup.value = true
}

const onMenuSmartImport = () => {
  closeAddMenu()
  smartImportText.value = ''
  smartImportResult.value = null
  editSmartResult.value = false
  showSmartImport.value = true
}

const onMenuUseCode = () => {
  closeAddMenu()
  shareCodeInput.value = ''
  useCodeResult.value = null
  showUseCode.value = true
}

// ========== 智能导入 ==========
const smartImportText = ref('')
const smartImportLoading = ref(false)
const smartImportResult = ref<SmartImportResult | null>(null)
const editSmartResult = ref(false)

const smartExamples = [
  '我要去杭州3天，带身份证、充电器、换洗衣物',
  '下周去三亚5天，需要泳衣、防晒霜、墨镜',
  '周末去北京，带充电宝、耳机、雨伞',
  '去日本7天，护照、转换插头、旅行保险'
]

const CATEGORY_NAME_MAP: Record<string, string> = {
  document: '个人证件',
  electronics: '电子设备',
  clothing: '衣物用品',
  daily: '生活用品',
  food: '食品饮料',
  medicine: '药品',
  prepare: '出行准备',
  other: '其他'
}

const smartItemGroups = computed(() => {
  if (!smartImportResult.value) return []
  const map = new Map<string, { category: string; name: string; items: typeof smartImportResult.value.items }>()
  for (const item of smartImportResult.value.items) {
    if (!map.has(item.category)) {
      map.set(item.category, { category: item.category, name: item.categoryName || CATEGORY_NAME_MAP[item.category] || item.category, items: [] })
    }
    map.get(item.category)!.items.push(item)
  }
  return Array.from(map.values())
})

const removeSmartItem = (category: string, idx: number) => {
  if (!smartImportResult.value) return
  const group = smartItemGroups.value.find(g => g.category === category)
  if (!group) return
  const item = group.items[idx]
  const i = smartImportResult.value.items.indexOf(item)
  if (i >= 0) smartImportResult.value.items.splice(i, 1)
}

const closeSmartImport = () => {
  showSmartImport.value = false
}

const doSmartImport = async () => {
  if (!smartImportText.value.trim() || smartImportLoading.value) return
  smartImportLoading.value = true
  try {
    const res = await smartImport(smartImportText.value.trim())
    smartImportResult.value = res
    editSmartResult.value = false
  } catch (error: any) {
    uni.showToast({ title: getErrorMessage(error, '识别失败'), icon: 'none' })
  } finally {
    smartImportLoading.value = false
  }
}

const confirmSmartImport = async () => {
  if (!smartImportResult.value) return
  const r = smartImportResult.value
  if (!r.title) {
    uni.showToast({ title: '请填写标题', icon: 'none' })
    return
  }
  if (!r.departDate || !r.returnDate) {
    uni.showToast({ title: '请设置出发和返回日期', icon: 'none' })
    return
  }
  try {
    await createChecklist({
      title: r.title,
      destination: r.destination || '',
      departDate: r.departDate,
      returnDate: r.returnDate,
      items: r.items.map((item, idx) => ({
        category: item.category,
        content: item.content,
        checked: 0,
        orderNum: idx + 1
      }))
    })
    uni.showToast({ title: '创建成功', icon: 'success' })
    closeSmartImport()
    loadChecklists()
  } catch (error: any) {
    uni.showToast({ title: getErrorMessage(error, '创建失败'), icon: 'none' })
  }
}

// ========== 使用口令 ==========
const shareCodeInput = ref('')
const useCodeLoading = ref(false)
const useCodeResult = ref<ShareCodeImportResult | null>(null)

const useCodeItemGroups = computed(() => {
  if (!useCodeResult.value) return []
  const map = new Map<string, { category: string; name: string; items: typeof useCodeResult.value.items }>()
  for (const item of useCodeResult.value.items) {
    if (!map.has(item.category)) {
      map.set(item.category, { category: item.category, name: CATEGORY_NAME_MAP[item.category] || item.category, items: [] })
    }
    map.get(item.category)!.items.push(item)
  }
  return Array.from(map.values())
})

const closeUseCode = () => {
  showUseCode.value = false
}

const doUseCode = async () => {
  if (!shareCodeInput.value.trim() || useCodeLoading.value) return
  useCodeLoading.value = true
  try {
    const res = await importByShareCode(shareCodeInput.value.trim())
    useCodeResult.value = res
  } catch (error: any) {
    uni.showToast({ title: getErrorMessage(error, '导入失败'), icon: 'none' })
  } finally {
    useCodeLoading.value = false
  }
}

const confirmUseCode = async () => {
  if (!useCodeResult.value) return
  const r = useCodeResult.value
  const today = new Date()
  const departDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  const returnDate = (() => {
    const d = new Date(today)
    d.setDate(d.getDate() + 2)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  })()
  try {
    await createChecklist({
      title: r.title,
      destination: r.destination || '',
      departDate,
      returnDate,
      remark: r.remark || '',
      items: r.items.map((item, idx) => ({
        category: item.category,
        content: item.content,
        checked: 0,
        orderNum: idx + 1
      }))
    })
    uni.showToast({ title: '创建成功', icon: 'success' })
    closeUseCode()
    loadChecklists()
  } catch (error: any) {
    uni.showToast({ title: getErrorMessage(error, '创建失败'), icon: 'none' })
  }
}

// 加载清单数据
const loadChecklists = async () => {
  loading.value = true
  try {
    const res = await getChecklists({ current: currentPage.value, size: pageSize.value })
    if (res && res.records) {
      checklistData.value = res.records
      total.value = res.total
    } else {
      checklistData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载清单失败:', error)
    checklistData.value = []
    total.value = 0
    uni.showToast({ title: getErrorMessage(error, '加载清单失败'), icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 日期选择
const onStartDateChange = (e: any) => {
  formData.value.startDate = e.detail.value
}

const onEndDateChange = (e: any) => {
  formData.value.endDate = e.detail.value
}

// 提交清单
const submitChecklist = async () => {
  if (!formData.value.name) {
    uni.showToast({ title: '请输入旅游名称', icon: 'none' })
    return
  }
  if (!formData.value.startDate) {
    uni.showToast({ title: '请选择开始时间', icon: 'none' })
    return
  }
  if (!formData.value.endDate) {
    uni.showToast({ title: '请选择结束时间', icon: 'none' })
    return
  }
  const departDate = formData.value.startDate
  const returnDate = formData.value.endDate
  if (returnDate < departDate) {
    uni.showToast({ title: '结束日期不能早于开始日期', icon: 'none' })
    return
  }

  try {
    const submitData = {
      title: formData.value.name,
      destination: formData.value.destination.trim(),
      departDate,
      returnDate
    }

    if (editingId.value) {
      // 编辑现有清单
      await updateChecklist(editingId.value, submitData)
      uni.showToast({ title: '更新成功', icon: 'success' })
    } else {
      // 创建新清单
      await createChecklist(submitData)
      uni.showToast({ title: '创建成功', icon: 'success' })
    }
    // 重新加载列表
    await loadChecklists()
    closeAddPopup()
  } catch (error) {
    uni.showToast({ title: getErrorMessage(error, editingId.value ? '更新失败' : '创建失败'), icon: 'none' })
  }
}

// 跳转详情
const goToDetail = (item: Checklist) => {
  uni.navigateTo({
    url: `/packageChecklist/pages/checklist/detail?id=${encodeURIComponent(String(item.id))}`
  })
}

// 编辑清单
const editChecklist = (item: Checklist) => {
  editingId.value = item.id
  formData.value = {
    name: item.title,
    destination: item.destination || '',
    startDate: item.departDate || '',
    endDate: item.returnDate || ''
  }
  showAddPopup.value = true
}

// 删除清单
const confirmDelete = (item: Checklist) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除清单"${item.title}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteChecklist(item.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          loadChecklists()
        } catch (error) {
          uni.showToast({ title: getErrorMessage(error, '删除失败'), icon: 'none' })
          loadChecklists()
        }
      }
    }
  })
}

// 页面加载
const isFirstLoad = ref(true)
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 44
  windowWidth.value = systemInfo.windowWidth || 375

  // #ifdef MP-WEIXIN
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonTop.value = menuButton.top
      menuButtonHeight.value = menuButton.height
      menuButtonLeft.value = menuButton.left
      // 计算导航栏高度，使内容与胶囊按钮垂直居中对齐
      navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height + statusBarHeight.value
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif

  loadChecklists()
  isFirstLoad.value = false

  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  if (options.shareCode) {
    shareCodeInput.value = decodeURIComponent(options.shareCode)
    showUseCode.value = true
    nextTick(() => {
      doUseCode()
    })
  } else if (options.name || options.destination) {
    formData.value = {
      name: decodeURIComponent(options.name || ''),
      destination: decodeURIComponent(options.destination || ''),
      startDate: options.departDate || '',
      endDate: options.returnDate || ''
    }
    showAddPopup.value = true
  }
})

onShow(() => {
  if (!isFirstLoad.value) {
    loadChecklists()
  }
})
</script>

<style lang="scss" scoped>
$primary-color: #00B8A9;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius-lg: 24rpx;
$border-radius-md: 16rpx;

.page {
  min-height: 100vh;
  background: $bg-color;
}

// 导航栏
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32rpx;
  background: $card-bg;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 60rpx;
}

// 统计卡片
.stats-card {
  margin: 24rpx 32rpx;
  padding: 32rpx;
  background: $card-bg;
  border-radius: $border-radius-lg;
}

.stats-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.stats-label {
  font-size: 30rpx;
  color: $text-primary;
}

.stats-value {
  font-size: 30rpx;
  color: $primary-color;
  font-weight: 600;
}

// 区块标题
.section-title {
  padding: 0 32rpx 20rpx;
}

.title-text {
  font-size: 28rpx;
  color: $text-secondary;
}

// 清单网格
.checklist-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  padding: 0 32rpx 48rpx;
}

.checklist-card {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
  min-height: 280rpx;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &.add-card {
    justify-content: center;
    align-items: center;
    border: 2rpx dashed #E5E5EA;
    background: transparent;
  }
}

.add-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #E5E5EA;
  border-radius: $border-radius-md;
  margin-bottom: 16rpx;
}

.add-icon {
  font-size: 40rpx;
  color: $text-secondary;
}

.add-text {
  font-size: 26rpx;
  color: $text-secondary;
}

.card-content {
  flex: 1;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.card-status {
  padding: 2rpx 12rpx;
  border-radius: 100rpx;
  flex-shrink: 0;

  &.status-0 {
    background: #E5E5EA;
  }
  &.status-1 {
    background: rgba(0, 184, 169, 0.15);
  }
  &.status-2 {
    background: rgba(52, 199, 89, 0.15);
  }
}

.status-text {
  font-size: 20rpx;
  color: $text-secondary;

  .status-1 & {
    color: $primary-color;
  }
  .status-2 & {
    color: #34C759;
  }
}

.card-destination {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 8rpx;
  display: block;
}

.card-dates {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.date-row {
  font-size: 24rpx;
  color: $text-secondary;
}

.card-progress {
  margin-top: 12rpx;
}

.progress-text {
  font-size: 22rpx;
  color: $primary-color;
  font-weight: 500;
}

.card-edit {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-delete {
  position: absolute;
  right: 24rpx;
  top: 24rpx;
  width: 44rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.06);
}

.delete-icon {
  font-size: 28rpx;
  color: #FF3B30;
}

.edit-icon {
  font-size: 28rpx;
}

// 弹窗遮罩
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

// 添加清单弹窗
.add-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 90vh;
  background: $bg-color;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  overflow: hidden;

  &.popup-show {
    transform: translateY(0);
  }
}

.popup-back {
  position: absolute;
  top: 24rpx;
  left: 24rpx;
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  z-index: 10;
}

.popup-back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

.popup-banner {
  width: 100%;
  height: 400rpx;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.popup-form {
  padding: 40rpx;
}

.form-group {
  margin-bottom: 40rpx;
}

.form-label {
  font-size: 30rpx;
  font-weight: 500;
  color: $text-primary;
  margin-bottom: 16rpx;
  display: block;
}

.form-input-wrapper {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  background: $card-bg;
  border-radius: $border-radius-md;

  &.picker {
    justify-content: space-between;
  }
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.input-count {
  font-size: 26rpx;
  color: $text-secondary;
  margin-left: 16rpx;
}

.picker-text {
  font-size: 28rpx;
  color: $text-primary;

  &.placeholder {
    color: $text-secondary;
  }
}

.picker-icon {
  font-size: 32rpx;
}

/* 隐藏 H5 原生 date picker 空值红色警告图标 */
.form-input-wrapper.picker :deep(input) {
  &::-webkit-calendar-picker-indicator {
    display: none;
  }
}

.form-submit {
  margin-top: 24rpx;
  padding: 28rpx 40rpx;
  background: $primary-color;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.submit-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}

// ========== 添加方式选择弹窗 ==========
.add-menu-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  padding-bottom: env(safe-area-inset-bottom);

  &.popup-show {
    transform: translateY(0);
  }
}

.menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 40rpx 24rpx;
}

.menu-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.menu-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 40rpx;
  color: $text-secondary;
}

.menu-options {
  padding: 0 40rpx 40rpx;
}

.menu-option {
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx;
  background: $bg-color;
  border-radius: $border-radius-lg;
  margin-bottom: 20rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.option-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.option-icon-text {
  font-size: 36rpx;
}

.option-info {
  flex: 1;
  margin-left: 24rpx;
}

.option-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: 4rpx;
}

.option-desc {
  font-size: 24rpx;
  color: $text-secondary;
  display: block;
}

.option-arrow {
  font-size: 36rpx;
  color: $text-secondary;
  margin-left: 12rpx;
}

// ========== 智能导入弹窗 ==========
.smart-import-popup {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: $bg-color;
  z-index: 999;
  transform: translateX(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateX(0);
  }
}

.si-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  background: $card-bg;
}

.si-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.si-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.si-placeholder {
  width: 60rpx;
}

.si-body {
  flex: 1;
  height: calc(100vh - 100rpx - env(safe-area-inset-top));
  padding: 32rpx;
}

.si-tip {
  padding: 24rpx 28rpx;
  background: rgba(88, 86, 214, 0.08);
  border-radius: $border-radius-md;
  margin-bottom: 32rpx;
}

.si-tip-text {
  font-size: 26rpx;
  color: #5856D6;
  line-height: 1.6;
}

.si-input-area {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 28rpx;
  margin-bottom: 24rpx;
  position: relative;
}

.si-textarea {
  width: 100%;
  font-size: 28rpx;
  color: $text-primary;
  line-height: 1.6;
  min-height: 200rpx;
}

.si-text-count {
  font-size: 24rpx;
  color: $text-secondary;
  text-align: right;
  display: block;
  margin-top: 12rpx;
}

.si-examples {
  margin-bottom: 32rpx;
}

.si-examples-title {
  font-size: 26rpx;
  color: $text-secondary;
  margin-bottom: 16rpx;
  display: block;
}

.si-example-tag {
  display: inline-block;
  padding: 12rpx 24rpx;
  background: $card-bg;
  border-radius: 100rpx;
  margin-right: 16rpx;
  margin-bottom: 16rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.96);
  }
}

.si-example-text {
  font-size: 24rpx;
  color: $primary-color;
}

.si-submit {
  padding: 28rpx 40rpx;
  background: $primary-color;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &.disabled {
    opacity: 0.5;
    pointer-events: none;
  }
}

.si-submit-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}

.si-result {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
}

.si-result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.si-result-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.si-result-edit {
  font-size: 26rpx;
  color: $primary-color;
  padding: 8rpx 20rpx;
  background: rgba(0, 184, 169, 0.1);
  border-radius: 100rpx;
}

.si-result-field {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F0F0F0;

  &:last-child {
    border-bottom: none;
  }
}

.si-result-label {
  font-size: 26rpx;
  color: $text-secondary;
  width: 100rpx;
  flex-shrink: 0;
}

.si-result-value {
  font-size: 28rpx;
  color: $text-primary;
  flex: 1;

  &.picker-val {
    color: $primary-color;
  }
}

.si-result-input {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
  padding: 8rpx 16rpx;
  background: $bg-color;
  border-radius: 8rpx;
}

.si-result-items {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #F0F0F0;
}

.si-item-group {
  margin-bottom: 20rpx;
}

.si-group-name {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 12rpx;
  display: block;
}

.si-item-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.si-item-chip {
  display: flex;
  align-items: center;
  padding: 10rpx 20rpx;
  background: $bg-color;
  border-radius: 100rpx;
}

.si-item-text {
  font-size: 24rpx;
  color: $text-primary;
}

.si-item-remove {
  margin-left: 8rpx;
  width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.si-item-remove-text {
  font-size: 24rpx;
  color: #FF3B30;
}

.si-confirm {
  margin-top: 32rpx;
  padding: 28rpx 40rpx;
  background: $primary-color;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.si-confirm-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}

// ========== 使用口令弹窗 ==========
.use-code-popup {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: $bg-color;
  z-index: 999;
  transform: translateX(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateX(0);
  }
}

.uc-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  background: $card-bg;
}

.uc-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.uc-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.uc-placeholder {
  width: 60rpx;
}

.uc-body {
  flex: 1;
  height: calc(100vh - 100rpx - env(safe-area-inset-top));
  padding: 32rpx;
}

.uc-tip {
  padding: 24rpx 28rpx;
  background: rgba(255, 149, 0, 0.08);
  border-radius: $border-radius-md;
  margin-bottom: 32rpx;
}

.uc-tip-text {
  font-size: 26rpx;
  color: #FF9500;
  line-height: 1.6;
}

.uc-input-area {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 28rpx 32rpx;
  margin-bottom: 24rpx;
}

.uc-input {
  width: 100%;
  font-size: 36rpx;
  color: $text-primary;
  text-align: center;
  letter-spacing: 12rpx;
  font-weight: 600;
}

.uc-submit {
  padding: 28rpx 40rpx;
  background: #FF9500;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &.disabled {
    opacity: 0.5;
    pointer-events: none;
  }
}

.uc-submit-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}

.uc-result {
  background: $card-bg;
  border-radius: $border-radius-lg;
  padding: 32rpx;
}

.uc-result-header {
  margin-bottom: 24rpx;
}

.uc-result-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.uc-result-field {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F0F0F0;

  &:last-child {
    border-bottom: none;
  }
}

.uc-result-label {
  font-size: 26rpx;
  color: $text-secondary;
  width: 100rpx;
  flex-shrink: 0;
}

.uc-result-value {
  font-size: 28rpx;
  color: $text-primary;
  flex: 1;
}

.uc-result-items {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #F0F0F0;
}

.uc-item-group {
  margin-bottom: 20rpx;
}

.uc-group-name {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 12rpx;
  display: block;
}

.uc-item-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.uc-item-chip {
  display: flex;
  align-items: center;
  padding: 10rpx 20rpx;
  background: $bg-color;
  border-radius: 100rpx;
}

.uc-item-text {
  font-size: 24rpx;
  color: $text-primary;
}

.uc-confirm {
  margin-top: 32rpx;
  padding: 28rpx 40rpx;
  background: #FF9500;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.uc-confirm-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}
</style>
