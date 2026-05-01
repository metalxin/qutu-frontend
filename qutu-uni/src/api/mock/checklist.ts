/**
 * Mock数据 - 清单模块
 */

// 清单列表
export const mockChecklists = [
  {
    id: 1,
    name: '北京之旅',
    startDate: '2026/02/10',
    endDate: '2026/02/14',
    items: [
      { id: 1, name: '身份证', category: '个人证件', checked: true },
      { id: 2, name: '护照', category: '个人证件', checked: false },
      { id: 3, name: '充电宝', category: '电子设备', checked: true },
      { id: 4, name: '相机', category: '电子设备', checked: false },
      { id: 5, name: '换洗衣物', category: '衣物', checked: false },
      { id: 6, name: '洗漱用品', category: '日用品', checked: true },
      { id: 7, name: '常用药品', category: '医药', checked: false }
    ],
    expenses: [
      { id: 1, name: '机票', amount: 1200, category: '交通' },
      { id: 2, name: '酒店', amount: 800, category: '住宿' },
      { id: 3, name: '景点门票', amount: 300, category: '门票' }
    ]
  },
  {
    id: 2,
    name: '上海周末游',
    startDate: '2026/02/20',
    endDate: '2026/02/22',
    items: [
      { id: 1, name: '身份证', category: '个人证件', checked: true },
      { id: 2, name: '手机', category: '电子设备', checked: true },
      { id: 3, name: '雨伞', category: '日用品', checked: false }
    ],
    expenses: [
      { id: 1, name: '高铁票', amount: 550, category: '交通' },
      { id: 2, name: '迪士尼门票', amount: 475, category: '门票' }
    ]
  }
]

// 清单物品分类
export const mockItemCategories = [
  { id: 1, name: '个人证件', icon: '🪪' },
  { id: 2, name: '电子设备', icon: '📱' },
  { id: 3, name: '衣物', icon: '👕' },
  { id: 4, name: '日用品', icon: '🧴' },
  { id: 5, name: '医药', icon: '💊' },
  { id: 6, name: '食品', icon: '🍪' },
  { id: 7, name: '其他', icon: '📦' }
]

// 推荐物品模板
export const mockRecommendedItems = {
  domestic: [
    { name: '身份证', category: '个人证件' },
    { name: '手机', category: '电子设备' },
    { name: '充电器', category: '电子设备' },
    { name: '充电宝', category: '电子设备' },
    { name: '换洗衣物', category: '衣物' },
    { name: '洗漱用品', category: '日用品' },
    { name: '常用药品', category: '医药' },
    { name: '雨伞', category: '日用品' },
    { name: '防晒霜', category: '日用品' }
  ],
  international: [
    { name: '护照', category: '个人证件' },
    { name: '签证', category: '个人证件' },
    { name: '身份证', category: '个人证件' },
    { name: '转换插头', category: '电子设备' },
    { name: '手机', category: '电子设备' },
    { name: '充电器', category: '电子设备' },
    { name: '换洗衣物', category: '衣物' },
    { name: '洗漱用品', category: '日用品' },
    { name: '常用药品', category: '医药' },
    { name: '外币现金', category: '其他' }
  ]
}

// 费用分类
export const mockExpenseCategories = [
  { id: 1, name: '交通', icon: '🚗' },
  { id: 2, name: '住宿', icon: '🏨' },
  { id: 3, name: '餐饮', icon: '🍽️' },
  { id: 4, name: '门票', icon: '🎫' },
  { id: 5, name: '购物', icon: '🛍️' },
  { id: 6, name: '其他', icon: '💰' }
]
