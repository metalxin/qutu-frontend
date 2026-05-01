<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#1D1D1F" />
      </view>
    </view>

    <!-- 清单信息 -->
    <view class="checklist-header">
      <text class="checklist-name">{{ checklistInfo.title }}</text>
      <text class="checklist-date">{{ (checklistInfo.departDate || '').replace(/-/g, '/') }}-{{ (checklistInfo.returnDate || '').replace(/-/g, '/') }}</text>
    </view>

    <!-- Tab切换 -->
    <view class="tab-bar">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'luggage' }"
        @click="activeTab = 'luggage'"
      >
        <text class="tab-text">行李清单</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'expense' }"
        @click="activeTab = 'expense'"
      >
        <text class="tab-text">旅行账单</text>
      </view>
    </view>

    <!-- 行李清单内容 -->
    <view class="tab-content" v-if="activeTab === 'luggage'">
      <view class="progress-info">
        <text class="progress-text">已整理 </text>
        <text class="progress-count">{{ checkedCount }}/{{ luggageItems.length }}</text>
        <text class="progress-text"> 件物品</text>
      </view>

      <!-- 分类列表 -->
      <view class="category-list">
        <view 
          class="category-section" 
          v-for="category in luggageCategories" 
          :key="category.key"
        >
          <view class="category-header" @click="toggleCategory(category.key)">
            <text class="category-name">{{ category.name }}</text>
            <text class="category-arrow" :class="{ expanded: expandedCategories.includes(category.key) }">›</text>
          </view>
          <view class="category-items" v-if="expandedCategories.includes(category.key)">
            <view 
              class="luggage-item" 
              v-for="item in getCategoryItems(category.key)" 
              :key="item.id"
              @click="toggleItem(item)"
            >
              <view class="item-checkbox" :class="{ checked: item.checked === 1 }">
                <text class="check-icon" v-if="item.checked === 1">✓</text>
              </view>
              <text class="item-name" :class="{ checked: item.checked === 1 }">{{ item.content }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 添加物品按钮 -->
      <view class="fab-btn" @click="openAddItemPopup">
        <text class="fab-icon">+</text>
      </view>
    </view>

    <!-- 旅行账单内容 -->
    <view class="tab-content" v-if="activeTab === 'expense'">
      <view class="expense-header">
        <view class="budget-btn" @click="showBudgetPopup = true">
          <text class="budget-text">设置预算</text>
          <text class="budget-arrow">›</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="expenseList.length === 0">
        <text class="empty-icon">📋</text>
        <text class="empty-text">记录旅行消费</text>
        <view class="empty-btn" @click="openAddExpensePopup">
          <text class="empty-btn-text">开始记账</text>
        </view>
      </view>

      <!-- 账单列表 -->
      <view class="expense-list" v-else>
        <view class="expense-summary">
          <text class="summary-label">总支出：</text>
          <text class="summary-value">¥{{ totalExpense.toFixed(2) }}</text>
        </view>
        <view class="expense-item" v-for="expense in expenseList" :key="expense.id" @click="confirmDeleteExpense(expense)">
          <view class="expense-icon-wrapper" :style="{ background: getExpenseColor(expense.category) }">
            <text class="expense-icon">{{ getExpenseIcon(expense.category) }}</text>
          </view>
          <view class="expense-info">
            <text class="expense-category">{{ getExpenseCategoryName(expense.category) }}</text>
            <text class="expense-note">{{ expense.note || getExpenseCategoryName(expense.category) }}</text>
          </view>
          <text class="expense-amount">-¥{{ (expense.amount || 0).toFixed(2) }}</text>
        </view>
        <view class="add-expense-btn" @click="openAddExpensePopup">
          <text class="add-expense-text">+ 添加消费</text>
        </view>
      </view>
    </view>

    <!-- 添加物品弹窗 -->
    <view class="popup-mask" v-if="showAddItem" @click="showAddItem = false"></view>
    <view class="item-popup" :class="{ 'popup-show': showAddItem }">
      <view class="popup-nav">
        <view class="popup-back" @click="showAddItem = false">
          <text class="popup-back-icon">‹</text>
        </view>
      </view>

      <view class="popup-title-section">
        <text class="popup-main-title">行李清单</text>
      </view>

      <!-- 手动添加 -->
      <view class="manual-add">
        <input 
          class="manual-input" 
          v-model="newItemName"
          placeholder="请输入物品名称手动添加"
          @confirm="addCustomItem"
        />
      </view>

      <!-- 常用物品 -->
      <view class="common-items">
        <text class="common-title">常用物品</text>
        
        <!-- 分类标签 -->
        <scroll-view class="category-tabs" scroll-x :show-scrollbar="false">
          <view class="tabs-inner">
            <view 
              class="category-tab" 
              :class="{ active: activeItemCategory === cat.key }"
              v-for="cat in itemCategories" 
              :key="cat.key"
              @click="activeItemCategory = cat.key"
            >
              <text class="tab-label">{{ cat.name }}</text>
            </view>
          </view>
        </scroll-view>

        <!-- 物品列表 -->
        <view class="items-list">
          <view 
            class="common-item" 
            v-for="item in currentCategoryItems" 
            :key="item"
            @click="toggleCommonItem(item)"
          >
            <text class="common-item-name">{{ item }}</text>
            <view 
              class="common-item-add" 
              :class="{ added: isItemAdded(item) }"
              @click.stop="toggleCommonItem(item)"
            >
              <text class="common-item-status" v-if="isItemAdded(item)">已添加</text>
              <text class="common-item-icon" v-if="!isItemAdded(item)">⊕</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 添加消费弹窗 -->
    <view class="popup-mask" v-if="showAddExpense" @click="showAddExpense = false"></view>
    <view class="expense-popup" :class="{ 'popup-show': showAddExpense }">
      <view class="popup-nav">
        <view class="popup-back" @click="showAddExpense = false">
          <text class="popup-back-icon">‹</text>
        </view>
      </view>

      <!-- 消费类型 -->
      <view class="expense-types">
        <view 
          class="expense-type" 
          :class="{ active: selectedExpenseType === type.key }"
          v-for="type in expenseTypes" 
          :key="type.key"
          @click="selectedExpenseType = type.key"
        >
          <view class="type-icon-wrapper" :class="{ active: selectedExpenseType === type.key }">
            <text class="type-icon">{{ type.icon }}</text>
          </view>
          <text class="type-name">{{ type.name }}</text>
        </view>
      </view>

      <!-- 金额输入区 -->
      <view class="amount-section">
        <view class="amount-input-row">
          <input 
            class="note-input" 
            v-model="expenseForm.note"
            placeholder="请输入备注"
          />
          <text class="amount-display">¥{{ parseFloat(expenseForm.amount) ? parseFloat(expenseForm.amount).toFixed(2) : '0.00' }}</text>
        </view>
      </view>

      <!-- 数字键盘 -->
      <view class="keyboard">
        <view class="keyboard-row">
          <view class="key" @click="inputNumber('7')"><text class="key-text">7</text></view>
          <view class="key" @click="inputNumber('8')"><text class="key-text">8</text></view>
          <view class="key" @click="inputNumber('9')"><text class="key-text">9</text></view>
          <view class="key date-key">
            <text class="key-icon">📅</text>
            <text class="key-text">今天</text>
          </view>
        </view>
        <view class="keyboard-row">
          <view class="key" @click="inputNumber('4')"><text class="key-text">4</text></view>
          <view class="key" @click="inputNumber('5')"><text class="key-text">5</text></view>
          <view class="key" @click="inputNumber('6')"><text class="key-text">6</text></view>
          <view class="key" @click="inputNumber('+')"><text class="key-text">+</text></view>
        </view>
        <view class="keyboard-row">
          <view class="key" @click="inputNumber('1')"><text class="key-text">1</text></view>
          <view class="key" @click="inputNumber('2')"><text class="key-text">2</text></view>
          <view class="key" @click="inputNumber('3')"><text class="key-text">3</text></view>
          <view class="key" @click="inputNumber('-')"><text class="key-text">-</text></view>
        </view>
        <view class="keyboard-row">
          <view class="key" @click="inputNumber('.')"><text class="key-text">.</text></view>
          <view class="key" @click="inputNumber('0')"><text class="key-text">0</text></view>
          <view class="key" @click="deleteNumber"><text class="key-icon">⌫</text></view>
          <view class="key confirm-key" @click="submitExpense"><text class="key-text">完成</text></view>
        </view>
      </view>
    </view>

    <!-- 设置预算弹窗 -->
    <view class="popup-mask" v-if="showBudgetPopup" @click="showBudgetPopup = false"></view>
    <view class="budget-popup" :class="{ 'popup-show': showBudgetPopup }">
      <view class="budget-header">
        <text class="budget-title">设置预算</text>
        <view class="budget-close" @click="showBudgetPopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>
      <view class="budget-content">
        <input 
          class="budget-input" 
          type="number"
          v-model="budgetAmount"
          placeholder="请输入预算金额"
        />
      </view>
      <view class="budget-submit" @click="saveBudget">
        <text class="submit-text">确定</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getChecklistDetail, toggleChecklistItem, addChecklistItem, deleteChecklistItem, updateChecklist, getExpenses, addExpense, deleteExpense, updateBudget as apiUpdateBudget, getTotalExpense } from '@/api'
import type { ChecklistDetail, ChecklistItem, ChecklistExpense, ChecklistId } from '@/api/modules/checklist'
import { getItemCategories, categoryLabel, EXPENSE_CATEGORY_MAP, EXPENSE_CATEGORY_REVERSE_MAP } from '@/api/modules/checklist'

// 清单ID
const checklistId = ref<ChecklistId>('')

const getErrorMessage = (error: any, fallback = '操作失败') => {
  return error?.response?.msg || error?.response?.message || error?.message || fallback
}

const ensureChecklistReady = () => {
  if (!checklistId.value) {
    uni.showToast({ title: '清单信息未加载', icon: 'none' })
    return false
  }
  return true
}

// 清单信息
const checklistInfo = ref<ChecklistDetail>({
  id: '',
  title: '',
  destination: '',
  departDate: '',
  returnDate: '',
  status: 0,
  itemsCount: 0,
  checkedCount: 0,
  budget: null,
  createdAt: '',
  remark: '',
  updatedAt: '',
  items: []
})

// 当前Tab
const activeTab = ref('luggage')

// 行李物品
const luggageItems = computed(() => checklistInfo.value.items || [])

// 行李分类 - 从 items 动态提取，key 为英文，显示为中文
const luggageCategories = computed(() => {
  const cats = new Map<string, { key: string; name: string }>()
  luggageItems.value.forEach(item => {
    if (!cats.has(item.category)) {
      cats.set(item.category, { key: item.category, name: categoryLabel(item.category) })
    }
  })
  return Array.from(cats.values())
})

// 展开的分类
const expandedCategories = ref<string[]>([])

// 已勾选数量
const checkedCount = computed(() => {
  return luggageItems.value.filter(item => item.checked === 1).length
})

// 切换分类展开
const toggleCategory = (name: string) => {
  const index = expandedCategories.value.indexOf(name)
  if (index > -1) {
    expandedCategories.value.splice(index, 1)
  } else {
    expandedCategories.value.push(name)
  }
}

// 获取分类物品
const getCategoryItems = (category: string) => {
  return luggageItems.value.filter(item => item.category === category)
}

// 切换物品勾选 - 调用API
const toggleItem = async (item: ChecklistItem) => {
  const newChecked = item.checked === 1 ? 0 : 1
  try {
    const success = await toggleChecklistItem(item.id, newChecked)
    if (!success) {
      throw new Error('勾选失败')
    }
    item.checked = newChecked
    // 更新清单统计
    checklistInfo.value.checkedCount = luggageItems.value.filter(i => i.checked === 1).length
  } catch (error) {
    console.error('勾选失败:', error)
    uni.showToast({ title: getErrorMessage(error, '勾选失败'), icon: 'none' })
  }
}

// 添加物品弹窗
const showAddItem = ref(false)
const newItemName = ref('')
const activeItemCategory = ref('document')

const openAddItemPopup = () => {
  if (!ensureChecklistReady()) return
  showAddItem.value = true
}

// 物品分类数据 - key 与后端 category 一致
const itemCategories = ref(getItemCategories().map(cat => ({
  key: cat.key,
  name: cat.name,
  items: getItemsByCategory(cat.key)
})))

function getItemsByCategory(categoryKey: string): string[] {
  const categoryItemsMap: Record<string, string[]> = {
    document: ['身份证', '学生证', '户口本', '护照', '签证', '港澳通行证', '驾驶证'],
    electronics: ['手机', '充电器', '充电宝', '耳机', '相机', '平板', '笔记本电脑'],
    clothing: ['换洗衣物', '睡衣', '外套', '鞋子', '帽子', '围巾', '手套'],
    daily: ['牙刷', '牙膏', '毛巾', '洗面奶', '护肤品', '防晒霜', '纸巾'],
    food: ['零食', '水', '饮料', '水果', '面包', '泡面'],
    medicine: ['感冒药', '创可贴', '止泻药', '晕车药', '驱蚊水'],
    prepare: ['行程单', '酒店预订单', '机票/车票', '地图', '旅行攻略', '笔记本'],
    other: ['雨伞', '钱包', '钥匙', '塑料袋']
  }
  return categoryItemsMap[categoryKey] || []
}

// 当前分类物品
const currentCategoryItems = computed(() => {
  const cat = itemCategories.value.find(c => c.key === activeItemCategory.value)
  return cat ? cat.items : []
})

// 判断物品是否已添加
const isItemAdded = (name: string) => {
  return luggageItems.value.some(item => item.content === name)
}

// 添加/移除常用物品
const toggleCommonItem = async (name: string) => {
  if (!ensureChecklistReady()) return

  if (isItemAdded(name)) {
    // 移除
    const item = luggageItems.value.find(i => i.content === name)
    if (item) {
      try {
        const success = await deleteChecklistItem(item.id)
        if (!success) {
          throw new Error('删除失败')
        }
        // 重新加载详情
        const refreshed = await loadDetail()
        if (!refreshed) {
          throw new Error('刷新清单失败，请稍后重试')
        }
        uni.showToast({ title: '已移除', icon: 'success' })
        showAddItem.value = false
      } catch (error) {
        console.error('删除物品失败:', error)
        uni.showToast({ title: getErrorMessage(error, '移除失败'), icon: 'none' })
      }
    }
  } else {
    // 添加
    try {
      const itemId = await addChecklistItem(checklistId.value, {
        category: activeItemCategory.value,
        content: name,
        checked: 0
      })
      if (!itemId) {
        throw new Error('添加失败或无权限')
      }
      const refreshed = await loadDetail()
      if (!refreshed) {
        throw new Error('刷新清单失败，请稍后重试')
      }
      uni.showToast({ title: '已添加', icon: 'success' })
      showAddItem.value = false
    } catch (error) {
      console.error('添加物品失败:', error)
      uni.showToast({ title: getErrorMessage(error, '添加失败'), icon: 'none' })
    }
  }
}

// 添加自定义物品
const addCustomItem = async () => {
  if (!ensureChecklistReady()) return
  if (!newItemName.value.trim()) return
  try {
    const itemId = await addChecklistItem(checklistId.value, {
      category: activeItemCategory.value,
      content: newItemName.value.trim(),
      checked: 0
    })
    if (!itemId) {
      throw new Error('添加失败或无权限')
    }
    newItemName.value = ''
    const refreshed = await loadDetail()
    if (!refreshed) {
      throw new Error('刷新清单失败，请稍后重试')
    }
    uni.showToast({ title: '添加成功', icon: 'success' })
    showAddItem.value = false
  } catch (error) {
    console.error('添加自定义物品失败:', error)
    uni.showToast({ title: getErrorMessage(error, '添加失败'), icon: 'none' })
  }
}

// 消费相关
const showAddExpense = ref(false)
const expenseList = ref<ChecklistExpense[]>([])
const selectedExpenseType = ref('transport')
const expenseForm = ref({
  amount: '',
  note: ''
})

const openAddExpensePopup = () => {
  if (!ensureChecklistReady()) return
  // 重置表单
  expenseForm.value = { amount: '', note: '' }
  selectedExpenseType.value = 'transport'
  showAddExpense.value = true
}

// 消费类型 - key 与后端 category 一致
const expenseTypes = ref([
  { name: '交通', key: 'transport', icon: '🚌', color: '#00B8A9' },
  { name: '住宿', key: 'accommodation', icon: '🏠', color: '#6C5CE7' },
  { name: '美食', key: 'food', icon: '🍜', color: '#FD79A8' },
  { name: '购物', key: 'shopping', icon: '🛍️', color: '#FDCB6E' },
  { name: '娱乐', key: 'entertainment', icon: '🎮', color: '#74B9FF' },
  { name: '其他', key: 'other', icon: '➕', color: '#B2BEC3' }
])

// 总支出
const totalExpense = computed(() => {
  return expenseList.value.reduce((sum, item) => sum + (item.amount || 0), 0)
})

// 输入数字
const inputNumber = (num: string) => {
  if (num === '+' || num === '-') return
  if (num === '.' && expenseForm.value.amount.includes('.')) return
  if (num === '.' && !expenseForm.value.amount) {
    expenseForm.value.amount = '0.'
    return
  }
  expenseForm.value.amount += num
}

// 删除数字
const deleteNumber = () => {
  expenseForm.value.amount = expenseForm.value.amount.slice(0, -1)
}

// 加载费用列表
const loadExpenses = async () => {
  if (!checklistId.value) return false
  try {
    const list = await getExpenses(checklistId.value)
    expenseList.value = list || []
    return true
  } catch (error) {
    console.error('加载费用列表失败:', error)
    return false
  }
}

// 提交消费
const submitExpense = async () => {
  if (!ensureChecklistReady()) return
  const amount = parseFloat(expenseForm.value.amount)
  if (!amount || isNaN(amount)) {
    uni.showToast({ title: '请输入金额', icon: 'none' })
    return
  }

  try {
    const type = expenseTypes.value.find(t => t.key === selectedExpenseType.value)
    const expenseId = await addExpense(checklistId.value, {
      category: type?.key || selectedExpenseType.value || 'other',
      amount,
      note: expenseForm.value.note || type?.name || '其他',
      expenseDate: new Date().toISOString().slice(0, 10)
    })
    if (!expenseId) {
      throw new Error('记账失败或无权限')
    }
    const refreshed = await loadExpenses()
    if (!refreshed) {
      throw new Error('账单刷新失败，请稍后重试')
    }
    expenseForm.value = { amount: '', note: '' }
    showAddExpense.value = false
    uni.showToast({ title: '记录成功', icon: 'success' })
  } catch (error) {
    console.error('添加费用失败:', error)
    uni.showToast({ title: getErrorMessage(error, '记录失败'), icon: 'none' })
  }
}

// 删除费用
const confirmDeleteExpense = (expense: ChecklistExpense) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除这笔${EXPENSE_CATEGORY_MAP[expense.category] || expense.category}消费吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteExpense(expense.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          await loadExpenses()
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

// 获取费用显示名
const getExpenseCategoryName = (key: string) => {
  return EXPENSE_CATEGORY_MAP[key] || key
}

// 获取费用图标
const getExpenseIcon = (key: string) => {
  const type = expenseTypes.value.find(t => t.key === key)
  return type?.icon || '💰'
}

// 获取费用颜色
const getExpenseColor = (key: string) => {
  const type = expenseTypes.value.find(t => t.key === key)
  return type?.color || '#00B8A9'
}

// 预算
const showBudgetPopup = ref(false)
const budgetAmount = ref('')

const saveBudget = async () => {
  if (!ensureChecklistReady()) return
  const amount = parseFloat(budgetAmount.value)
  if (isNaN(amount)) {
    uni.showToast({ title: '请输入有效金额', icon: 'none' })
    return
  }
  try {
    const success = await apiUpdateBudget(checklistId.value, amount)
    if (!success) {
      throw new Error('设置失败')
    }
    showBudgetPopup.value = false
    checklistInfo.value.budget = amount
    uni.showToast({ title: '预算设置成功', icon: 'success' })
  } catch (error) {
    console.error('设置预算失败:', error)
    uni.showToast({ title: '设置失败', icon: 'none' })
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 加载详情
const loadDetail = async () => {
  if (!checklistId.value) return false
  try {
    const res = await getChecklistDetail(checklistId.value)
    if (res) {
      checklistInfo.value = res
      // 默认展开所有有物品的分类
      const categories = new Set(res.items.map(item => item.category))
      expandedCategories.value = Array.from(categories)
      // 初始化预算
      budgetAmount.value = res.budget != null ? String(res.budget) : ''
    } else {
      uni.showToast({ title: '清单不存在或无权限', icon: 'none' })
      return false
    }
    await loadExpenses()
    return true
  } catch (error) {
    console.error('加载清单详情失败:', error)
    uni.showToast({ title: getErrorMessage(error, '加载清单失败'), icon: 'none' })
    return false
  }
}

// 加载数据
onLoad((options) => {
  const id = options?.id
  if (!id) {
    uni.showToast({ title: '清单参数缺失', icon: 'none' })
    return
  }
  checklistId.value = String(id)
  if (!String(checklistId.value).trim()) {
    uni.showToast({ title: '清单参数无效', icon: 'none' })
    return
  }
  loadDetail()
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
  background: $card-bg;
}

// 导航栏
.navbar {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 24rpx);
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

// 清单头部
.checklist-header {
  padding: 0 32rpx 32rpx;
}

.checklist-name {
  font-size: 48rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
  margin-bottom: 8rpx;
}

.checklist-date {
  font-size: 28rpx;
  color: $text-secondary;
}

// Tab栏
.tab-bar {
  display: flex;
  gap: 48rpx;
  padding: 0 32rpx;
  border-bottom: 1rpx solid #E5E5EA;
}

.tab-item {
  padding: 24rpx 0;
  position: relative;

  &.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4rpx;
    background: $text-primary;
  }
}

.tab-text {
  font-size: 30rpx;
  color: $text-secondary;
}

.tab-item.active .tab-text {
  color: $text-primary;
  font-weight: 600;
}

// Tab内容
.tab-content {
  padding: 32rpx;
  min-height: calc(100vh - 400rpx);
  position: relative;
}

// 进度信息
.progress-info {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.progress-text {
  font-size: 28rpx;
  color: $text-secondary;
}

.progress-count {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 600;
}

// 分类列表
.category-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.category-section {
  background: $bg-color;
  border-radius: $border-radius-md;
  overflow: hidden;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
}

.category-name {
  font-size: 30rpx;
  font-weight: 500;
  color: $text-primary;
}

.category-arrow {
  font-size: 32rpx;
  color: $text-secondary;
  transition: transform 0.2s ease;

  &.expanded {
    transform: rotate(90deg);
  }
}

.category-items {
  padding: 0 32rpx 20rpx;
}

.luggage-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #E5E5EA;

  &:last-child {
    border-bottom: none;
  }
}

.item-checkbox {
  width: 44rpx;
  height: 44rpx;
  border: 3rpx solid $primary-color;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.checked {
    background: $primary-color;
  }
}

.check-icon {
  font-size: 28rpx;
  color: #FFFFFF;
}

.item-name {
  font-size: 28rpx;
  color: $text-primary;

  &.checked {
    text-decoration: line-through;
    color: $text-secondary;
  }
}

// 浮动按钮
.fab-btn {
  position: fixed;
  right: 48rpx;
  bottom: 120rpx;
  width: 112rpx;
  height: 112rpx;
  background: $primary-color;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 32rpx rgba(0, 184, 169, 0.4);
}

.fab-icon {
  font-size: 56rpx;
  color: #FFFFFF;
}

// 账单头部
.expense-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 32rpx;
}

.budget-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 24rpx;
  border: 2rpx solid $primary-color;
  border-radius: 100rpx;
}

.budget-text {
  font-size: 26rpx;
  color: $primary-color;
}

.budget-arrow {
  font-size: 24rpx;
  color: $primary-color;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 30rpx;
  color: $text-secondary;
  margin-bottom: 48rpx;
}

.empty-btn {
  padding: 28rpx 120rpx;
  background: $primary-color;
  border-radius: 100rpx;
}

.empty-btn-text {
  font-size: 30rpx;
  color: #FFFFFF;
  font-weight: 500;
}

// 账单列表
.expense-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.expense-summary {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  margin-bottom: 16rpx;
}

.summary-label {
  font-size: 28rpx;
  color: $text-secondary;
}

.summary-value {
  font-size: 32rpx;
  color: $text-primary;
  font-weight: 600;
}

.expense-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
}

.expense-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.expense-icon {
  font-size: 36rpx;
}

.expense-info {
  flex: 1;
}

.expense-category {
  font-size: 28rpx;
  font-weight: 500;
  color: $text-primary;
  display: block;
}

.expense-note {
  font-size: 24rpx;
  color: $text-secondary;
}

.expense-amount {
  font-size: 32rpx;
  font-weight: 600;
  color: #FF3B30;
}

.add-expense-btn {
  padding: 28rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-expense-text {
  font-size: 28rpx;
  color: $primary-color;
}

// 弹窗基础样式
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

// 添加物品弹窗
.item-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 85vh;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  display: flex;
  flex-direction: column;
  pointer-events: none;

  &.popup-show {
    transform: translateY(0);
    pointer-events: auto;
  }
}

.popup-nav {
  padding: 24rpx 32rpx;
}

.popup-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.popup-back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
}

.popup-title-section {
  padding: 0 32rpx 32rpx;
}

.popup-main-title {
  font-size: 48rpx;
  font-weight: 700;
  color: $text-primary;
}

.manual-add {
  margin: 0 32rpx 32rpx;
  padding: 28rpx 32rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
}

.manual-input {
  font-size: 28rpx;
  color: $text-primary;
  width: 100%;
}

.common-items {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.common-title {
  font-size: 28rpx;
  color: $text-secondary;
  padding: 0 32rpx 20rpx;
}

.category-tabs {
  padding: 0 32rpx;
  white-space: nowrap;
  border-bottom: 1rpx solid #E5E5EA;
}

.tabs-inner {
  display: inline-flex;
  gap: 40rpx;
}

.category-tab {
  padding: 20rpx 0;
  position: relative;

  &.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4rpx;
    background: $text-primary;
  }
}

.tab-label {
  font-size: 28rpx;
  color: $text-secondary;
}

.category-tab.active .tab-label {
  color: $text-primary;
  font-weight: 500;
}

.items-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 32rpx;
}

.common-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0;
  border-bottom: 1rpx solid #E5E5EA;
}

.common-item-name {
  font-size: 30rpx;
  color: $text-primary;
}

.common-item-add {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.common-item-status {
  font-size: 26rpx;
  color: $text-secondary;
}

.common-item-icon {
  font-size: 36rpx;
  color: $primary-color;
}

// 消费弹窗
.expense-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  pointer-events: none;

  &.popup-show {
    transform: translateY(0);
    pointer-events: auto;
  }
}

.expense-types {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
  padding: 32rpx;
}

.expense-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.type-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #E5E5EA;
  display: flex;
  align-items: center;
  justify-content: center;

  &.active {
    background: $primary-color;
  }
}

.type-icon {
  font-size: 40rpx;
}

.type-name {
  font-size: 24rpx;
  color: $text-primary;
}

.amount-section {
  padding: 32rpx;
  background: $bg-color;
}

.amount-input-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-input {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.amount-display {
  font-size: 48rpx;
  font-weight: 600;
  color: $text-primary;
}

// 数字键盘
.keyboard {
  background: $card-bg;
  padding-bottom: env(safe-area-inset-bottom);
}

.keyboard-row {
  display: flex;
  border-top: 1rpx solid #E5E5EA;
}

.key {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx 0;
  border-right: 1rpx solid #E5E5EA;

  &:last-child {
    border-right: none;
  }

  &:active {
    background: $bg-color;
  }

  &.date-key {
    flex-direction: column;
    gap: 4rpx;
  }

  &.confirm-key {
    background: $primary-color;
  }
}

.key-text {
  font-size: 36rpx;
  color: $text-primary;
}

.key-icon {
  font-size: 32rpx;
}

.confirm-key .key-text {
  color: #FFFFFF;
  font-weight: 500;
}

.date-key .key-text {
  font-size: 24rpx;
}

// 预算弹窗
.budget-popup {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%) scale(0.9);
  width: 80%;
  background: $card-bg;
  border-radius: $border-radius-lg;
  z-index: 999;
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;

  &.popup-show {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
    pointer-events: auto;
  }
}

.budget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #E5E5EA;
}

.budget-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.budget-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 40rpx;
  color: $text-secondary;
}

.budget-content {
  padding: 32rpx;
}

.budget-input {
  width: 100%;
  padding: 28rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
  font-size: 32rpx;
  color: $text-primary;
}

.budget-submit {
  margin: 0 32rpx 32rpx;
  padding: 28rpx;
  background: $primary-color;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-text {
  font-size: 30rpx;
  color: #FFFFFF;
  font-weight: 600;
}
</style>
