<template>
  <view class="page">
    <view class="navbar" :style="navBarStyle">
      <view class="nav-back" @click="goBack">
        <SFIcon name="back" :size="40" color="#FFFFFF" />
      </view>
      <view class="nav-title">
        <text class="title-text">{{ mapInfo.title }}</text>
      </view>
      <view class="nav-right" :style="navRightStyle">
        <text class="lit-count">{{ litCount }}/{{ totalCount }}</text>
      </view>
    </view>

    <view class="hero-section" :class="mapType">
      <view class="hero-content">
        <view class="hero-title-wrapper">
          <text class="hero-title">{{ mapInfo.title }}</text>
        </view>
        <view class="hero-subtitle">
          <text class="subtitle-text">「{{ mapInfo.slogan }}」</text>
        </view>
        <view class="hero-progress">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
          <text class="progress-text">{{ litCount }}/{{ totalCount }} 已点亮</text>
        </view>
      </view>
    </view>

    <scroll-view class="content" scroll-y :show-scrollbar="false">
      <view class="region-section">
        <view class="section-header">
          <text class="section-title">{{ mapType === 'world' ? '按地区选择' : '按区域选择' }}</text>
        </view>

        <view class="region-group" v-for="group in regionGroups" :key="group.name">
          <view class="group-header">
            <text class="group-name">{{ group.name }}</text>
            <text class="group-count">{{ group.litCount }}/{{ group.items.length }}</text>
          </view>
          <view class="group-items">
            <view
              class="region-item"
              v-for="item in group.items"
              :key="item.code"
              :class="{ lit: item.lit }"
              @click="toggleRegion(item)"
            >
              <view class="item-indicator" :class="{ active: item.lit }">
                <text class="indicator-icon">{{ item.lit ? '✓' : '+' }}</text>
              </view>
              <text class="item-name">{{ item.name }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getUserProvinces, lightUpProvince, getUserCountries, lightUpCountry, createFootprintRecord } from '@/api'
import type { FootprintProvince, FootprintCountry } from '@/api'

interface RegionItem {
  code: string
  name: string
  lit: boolean
  checkinCount?: number
}

interface RegionGroup {
  name: string
  items: RegionItem[]
  litCount: number
}

const statusBarHeight = ref(44)
const menuButtonSpace = ref(0)
const navBarHeight = ref(88)
const mapType = ref('china')
const litProvinces = ref<FootprintProvince[]>([])
const litCountries = ref<FootprintCountry[]>([])
const loading = ref(false)

const CHINA_PROVINCES = [
  { group: '华北', items: [
    { code: '110000', name: '北京' }, { code: '120000', name: '天津' },
    { code: '130000', name: '河北' }, { code: '140000', name: '山西' },
    { code: '150000', name: '内蒙古' }
  ]},
  { group: '东北', items: [
    { code: '210000', name: '辽宁' }, { code: '220000', name: '吉林' },
    { code: '230000', name: '黑龙江' }
  ]},
  { group: '华东', items: [
    { code: '310000', name: '上海' }, { code: '320000', name: '江苏' },
    { code: '330000', name: '浙江' }, { code: '340000', name: '安徽' },
    { code: '350000', name: '福建' }, { code: '360000', name: '江西' },
    { code: '370000', name: '山东' }
  ]},
  { group: '华中', items: [
    { code: '410000', name: '河南' }, { code: '420000', name: '湖北' },
    { code: '430000', name: '湖南' }
  ]},
  { group: '华南', items: [
    { code: '440000', name: '广东' }, { code: '450000', name: '广西' },
    { code: '460000', name: '海南' }
  ]},
  { group: '西南', items: [
    { code: '500000', name: '重庆' }, { code: '510000', name: '四川' },
    { code: '520000', name: '贵州' }, { code: '530000', name: '云南' },
    { code: '540000', name: '西藏' }
  ]},
  { group: '西北', items: [
    { code: '610000', name: '陕西' }, { code: '620000', name: '甘肃' },
    { code: '630000', name: '青海' }, { code: '640000', name: '宁夏' },
    { code: '650000', name: '新疆' }
  ]},
  { group: '特别行政区', items: [
    { code: '710000', name: '台湾' }, { code: '810000', name: '香港' },
    { code: '820000', name: '澳门' }
  ]}
]

const WORLD_COUNTRIES = [
  { group: '亚洲', items: [
    { code: 'JP', name: '日本' }, { code: 'KR', name: '韩国' },
    { code: 'TH', name: '泰国' }, { code: 'VN', name: '越南' },
    { code: 'SG', name: '新加坡' }, { code: 'MY', name: '马来西亚' },
    { code: 'ID', name: '印度尼西亚' }, { code: 'PH', name: '菲律宾' },
    { code: 'IN', name: '印度' }, { code: 'AE', name: '阿联酋' },
    { code: 'TR', name: '土耳其' }, { code: 'IL', name: '以色列' }
  ]},
  { group: '欧洲', items: [
    { code: 'FR', name: '法国' }, { code: 'DE', name: '德国' },
    { code: 'IT', name: '意大利' }, { code: 'ES', name: '西班牙' },
    { code: 'GB', name: '英国' }, { code: 'CH', name: '瑞士' },
    { code: 'AT', name: '奥地利' }, { code: 'NL', name: '荷兰' },
    { code: 'BE', name: '比利时' }, { code: 'PT', name: '葡萄牙' },
    { code: 'GR', name: '希腊' }, { code: 'CZ', name: '捷克' },
    { code: 'HU', name: '匈牙利' }, { code: 'PL', name: '波兰' },
    { code: 'SE', name: '瑞典' }, { code: 'NO', name: '挪威' },
    { code: 'FI', name: '芬兰' }, { code: 'DK', name: '丹麦' },
    { code: 'IE', name: '爱尔兰' }, { code: 'RU', name: '俄罗斯' }
  ]},
  { group: '北美洲', items: [
    { code: 'US', name: '美国' }, { code: 'CA', name: '加拿大' },
    { code: 'MX', name: '墨西哥' }, { code: 'CU', name: '古巴' },
    { code: 'JM', name: '牙买加' }, { code: 'PA', name: '巴拿马' }
  ]},
  { group: '南美洲', items: [
    { code: 'BR', name: '巴西' }, { code: 'AR', name: '阿根廷' },
    { code: 'CL', name: '智利' }, { code: 'PE', name: '秘鲁' },
    { code: 'CO', name: '哥伦比亚' }
  ]},
  { group: '大洋洲', items: [
    { code: 'AU', name: '澳大利亚' }, { code: 'NZ', name: '新西兰' },
    { code: 'FJ', name: '斐济' }
  ]},
  { group: '非洲', items: [
    { code: 'EG', name: '埃及' }, { code: 'ZA', name: '南非' },
    { code: 'KE', name: '肯尼亚' }, { code: 'MA', name: '摩洛哥' },
    { code: 'TZ', name: '坦桑尼亚' }
  ]}
]

const mapInfo = computed(() => {
  const infoMap: Record<string, { title: string; slogan: string }> = {
    spring: { title: '春节玩乐地图', slogan: '年味在路上 点亮你的新春足迹！' },
    china: { title: '中国漫游者指南', slogan: '点亮你去过的省/市/自治区/特别行政区' },
    world: { title: '世界探险家手册', slogan: '点亮你在世界上去过的国家/地区' }
  }
  return infoMap[mapType.value] || infoMap.china
})

const navBarStyle = computed(() => ({
  paddingTop: statusBarHeight.value + 'px',
  minHeight: navBarHeight.value + 'px'
}))

const navRightStyle = computed(() => {
  if (menuButtonSpace.value > 0) {
    return { paddingRight: menuButtonSpace.value + 'px' }
  }
  return {}
})

const regionGroups = computed((): RegionGroup[] => {
  const source = mapType.value === 'world' ? WORLD_COUNTRIES : CHINA_PROVINCES
  const litList: any[] = mapType.value === 'world' ? litCountries.value : litProvinces.value
  const codeField = mapType.value === 'world' ? 'countryCode' : 'provinceCode'

  return source.map(group => {
    const items: RegionItem[] = group.items.map(item => {
      const litItem = litList.find((l: any) => l[codeField] === item.code)
      return {
        code: item.code,
        name: item.name,
        lit: !!litItem,
        checkinCount: litItem?.checkinCount
      }
    })
    return {
      name: group.group,
      items,
      litCount: items.filter(i => i.lit).length
    }
  })
})

const litCount = computed(() => {
  return regionGroups.value.reduce((sum, g) => sum + g.litCount, 0)
})

const totalCount = computed(() => {
  return regionGroups.value.reduce((sum, g) => sum + g.items.length, 0)
})

const progressPercent = computed(() => {
  if (totalCount.value === 0) return 0
  return Math.round((litCount.value / totalCount.value) * 100)
})

const getSystemInfo = () => {
  try {
    const info = uni.getSystemInfoSync()
    statusBarHeight.value = info.statusBarHeight || 44
    const windowWidth = info.windowWidth || 375
    // #ifdef MP-WEIXIN
    try {
      const menuButton = uni.getMenuButtonBoundingClientRect()
      if (menuButton) {
        menuButtonSpace.value = windowWidth - menuButton.left + 10
        navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height + statusBarHeight.value
      }
    } catch (e) {
      console.log('获取胶囊按钮位置失败', e)
    }
    // #endif
  } catch (e) {
    console.error('获取系统信息失败', e)
  }
}

const loadLitData = async () => {
  loading.value = true
  try {
    if (mapType.value === 'world') {
      const res = await getUserCountries()
      if (res) litCountries.value = res
    } else {
      const res = await getUserProvinces()
      if (res) litProvinces.value = res
    }
  } catch (error) {
    console.error('加载点亮数据失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleRegion = async (item: RegionItem) => {
  if (loading.value) return

  if (item.lit) {
    uni.showToast({ title: '已点亮过该地区', icon: 'none' })
    return
  }

  loading.value = true
  try {
    if (mapType.value === 'world') {
      await lightUpCountry(item.code, item.name)
    } else {
      await lightUpProvince(item.code, item.name)
    }

    await createFootprintRecord({
      spotName: item.name,
      provinceName: mapType.value !== 'world' ? item.name : undefined,
      provinceCode: mapType.value !== 'world' ? item.code : undefined,
      countryName: mapType.value === 'world' ? item.name : '中国',
      countryCode: mapType.value === 'world' ? item.code : 'CN',
      type: mapType.value === 'world' ? 2 : 1
    })

    uni.showToast({ title: '点亮成功！', icon: 'success' })
    await loadLitData()
  } catch (error) {
    console.error('点亮失败:', error)
    uni.showToast({ title: '点亮失败，请重试', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  getSystemInfo()
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  if (currentPage?.options?.type) {
    mapType.value = currentPage.options.type
  }
  loadLitData()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F7;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  background: transparent;
  box-sizing: border-box;
}

.nav-back, .nav-right {
  width: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  flex: 1;
  text-align: center;
}

.title-text {
  font-size: 34rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.lit-count {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.hero-section {
  padding: 140rpx 32rpx 48rpx;
  position: relative;

  &.spring {
    background: linear-gradient(180deg, #FF6B6B 0%, #FF8E53 100%);
  }

  &.china {
    background: linear-gradient(180deg, #8B7BA8 0%, #A890C8 100%);
  }

  &.world {
    background: linear-gradient(180deg, #4ECDC4 0%, #44A08D 100%);
  }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title-wrapper {
  margin-bottom: 16rpx;
}

.hero-title {
  font-size: 56rpx;
  font-weight: 800;
  color: #FFEB3B;
  text-shadow: 2rpx 2rpx 0 rgba(0, 0, 0, 0.1);
}

.hero-subtitle {
  margin-bottom: 24rpx;
}

.subtitle-text {
  font-size: 26rpx;
  color: #FFFFFF;
}

.hero-progress {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.progress-bar {
  flex: 1;
  height: 12rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #FFEB3B;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 24rpx;
  color: #FFFFFF;
  font-weight: 500;
  white-space: nowrap;
}

.content {
  height: calc(100vh - 320rpx);
}

.region-section {
  padding: 24rpx 32rpx;
}

.section-header {
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1D1D1F;
}

.region-group {
  margin-bottom: 32rpx;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
  padding: 0 4rpx;
}

.group-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.group-count {
  font-size: 24rpx;
  color: #86868B;
}

.group-items {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.region-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  background: #FFFFFF;
  border-radius: 12rpx;
  border: 2rpx solid #E5E5EA;

  &.lit {
    background: #E8F5E9;
    border-color: #4CAF50;
  }
}

.item-indicator {
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background: #F0F0F0;
  display: flex;
  align-items: center;
  justify-content: center;

  &.active {
    background: #4CAF50;
  }
}

.indicator-icon {
  font-size: 20rpx;
  color: #999;

  .active & {
    color: #FFFFFF;
  }
}

.item-name {
  font-size: 26rpx;
  color: #1D1D1F;
  font-weight: 500;

  .lit & {
    color: #2E7D32;
  }
}

.bottom-space {
  height: 60rpx;
}
</style>
