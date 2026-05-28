<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="header" :style="headerStyle">
      <view class="header-left">
        <image class="logo-image" :src="logoSrc" mode="aspectFit" @error="onLogoError" />
      </view>
      <!-- 右侧按钮，使用动态边距避开胶囊按钮 -->
      <view class="header-right" :style="{ marginRight: menuButtonLeft > 0 ? (windowWidth - menuButtonLeft + 10) + 'px' : '0' }">
        <!-- 微信小程序头像居中显示，非微信显示在右侧 -->
        <!-- #ifndef MP-WEIXIN -->
        <view class="icon-btn" @click="showUserSidebar = true">
          <view class="avatar-wrapper">
            <image v-if="displayAvatar" :src="displayAvatar" class="header-avatar" mode="aspectFill" />
            <SFIcon v-else name="user" :size="36" color="#1D1D1F" />
          </view>
        </view>
        <!-- #endif -->
      </view>
      
      <!-- 微信小程序头像放在胶囊按钮左侧 -->
      <!-- #ifdef MP-WEIXIN -->
      <view
        class="header-mp-right"
        :style="{
          height: menuButtonHeight + 'px',
          top: menuButtonTop + 'px',
          right: (windowWidth - menuButtonLeft + 8) + 'px'
        }"
      >
        <view class="avatar-btn" @click="showUserSidebar = true">
          <image v-if="displayAvatar" :src="displayAvatar" class="header-avatar" mode="aspectFill" />
          <SFIcon v-else name="user" :size="36" color="#1D1D1F" />
        </view>
      </view>
      <!-- #endif -->
    </view>

    <!-- 搜索框 -->
    <view class="search-container" :style="{ marginTop: headerHeight + 'px' }">
      <view class="search-box" @click="openDestinationListPopup">
        <SFIcon name="search" :size="32" color="#86868B" />
        <text class="search-placeholder">按目的地探索</text>
      </view>

      <!-- 功能入口 -->
      <view class="feature-grid">
        <view 
          class="feature-item" 
          v-for="item in features" 
          :key="item.id"
          @click="handleFeatureClick(item)"
        >
          <view class="feature-icon-wrapper" :style="{ background: item.bgColor }">
            <SFIcon :name="item.iconName" :size="44" :color="item.iconColor" />
          </view>
          <text class="feature-text">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <!-- 收藏机横幅 -->
    <view class="banner" @click="goToFootprint">
      <image class="banner-bg" src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&q=80" mode="aspectFill" />
      <view class="banner-overlay"></view>
      <view class="banner-content">
        <view class="banner-left">
          <view class="banner-tag">
            <SFIcon name="location" :size="24" color="#FFFFFF" />
            <text class="banner-tag-text">人生地点</text>
          </view>
          <text class="banner-title">收藏机</text>
          <view class="banner-stats">
            <view class="banner-stat">
              <text class="banner-stat-value">{{ footprintStats.totalFootprints || 0 }}</text>
              <text class="banner-stat-label">足迹</text>
            </view>
            <view class="banner-stat-divider"></view>
            <view class="banner-stat">
              <text class="banner-stat-value">{{ footprintStats.provinceCount || 0 }}</text>
              <text class="banner-stat-label">省份</text>
            </view>
            <view class="banner-stat-divider"></view>
            <view class="banner-stat">
              <text class="banner-stat-value">{{ footprintStats.countryCount || 0 }}</text>
              <text class="banner-stat-label">国家</text>
            </view>
          </view>
        </view>
        <view class="banner-right">
          <view class="banner-action-btn">
            <text class="banner-action">点亮你的足迹</text>
            <SFIcon name="chevron-right" :size="28" color="#FFFFFF" />
          </view>
        </view>
      </view>
    </view>

    <!-- 探索附近 -->
    <view class="section-header">
      <view class="section-title-row">
        <view class="section-title-left">
          <text class="section-title">探索附近</text>
          <view class="city-selector" @click="showDestinationPopup = true">
            <SFIcon name="location" :size="24" color="#007AFF" />
            <text class="city-name">{{ selectedRegion || '杭州' }}</text>
            <SFIcon name="chevron-down" :size="20" color="#86868B" />
          </view>
        </view>
        <view class="section-actions">
          <view class="section-more" @click="openDestinationListPopup">
            <text class="more-text">查看全部 ›</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 目的地卡片 -->
    <view class="destination-scroll">
      <view class="destination-grid">
        <view
          class="destination-card"
          v-for="dest in destinations.slice(0, 4)"
          :key="dest.id"
          @tap="handleDestinationClick(dest)"
        >
          <view class="card-image" :style="{ background: dest.gradient || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }">
            <image v-if="dest.image" class="dest-image" :src="dest.image" mode="aspectFill" @error="handleImageError(dest)" />
            <view class="card-placeholder" v-else>
              <text class="placeholder-text">{{ dest.name }}</text>
            </view>
            <view class="card-tag" v-if="dest.tag">
              <text class="tag-text">{{ dest.tag }}</text>
            </view>

            <view class="card-rating">
              <SFIcon name="star" :size="24" color="#FFB800" filled />
              <text class="rating-text">{{ dest.rating }}</text>
            </view>
          </view>
          <view class="card-info">
            <view class="card-header">
              <text class="card-title">{{ dest.name }}</text>
              <text class="card-spots">{{ dest.spots }}个景点</text>
            </view>
            <text class="card-subtitle">{{ dest.subtitle }}</text>
          </view>
        </view>
      </view>
      <view class="search-empty" v-if="destinations.length === 0">
        <text class="empty-text">暂无目的地数据</text>
      </view>
    </view>

    <!-- 灵感推荐 -->
    <view class="inspiration-section">
      <view class="section-header">
        <view class="section-title-row">
          <text class="section-title">灵感推荐</text>
          <view class="section-more" @click="openInspirationListPopup">
            <text class="more-text">更多灵感 ›</text>
          </view>
        </view>
      </view>
      <scroll-view class="inspiration-scroll" scroll-x :show-scrollbar="false">
        <view class="inspiration-list">
          <view class="inspiration-card" v-for="item in inspirationList" :key="item.id" @tap="goToInspirationDetail(item)">
            <image v-if="item.cover && !item.coverError" class="inspiration-cover" :src="item.cover" mode="aspectFill" @error="item.coverError = true" />
            <view v-else class="inspiration-cover inspiration-cover-placeholder">
              <text class="placeholder-emoji">🗺️</text>
            </view>
            <view class="inspiration-info">
              <text class="inspiration-title">{{ item.title }}</text>
              <text class="inspiration-location">{{ item.location }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="popup-mask" v-if="showInspirationListPopup" @click="closeInspirationListPopup"></view>
    <view class="inspiration-list-popup" :class="{ 'popup-show': showInspirationListPopup }">
      <view class="popup-header">
        <text class="popup-title">灵感推荐</text>
        <view class="popup-close" @click="closeInspirationListPopup">
          <text class="close-icon">×</text>
        </view>
      </view>
      <view class="popup-search-bar">
        <SFIcon name="search" :size="28" color="#86868B" />
        <input
          class="popup-search-input"
          v-model="inspirationSearchKeyword"
          placeholder="搜索灵感"
          confirm-type="search"
          @confirm="doInspirationSearch"
          @input="onInspirationSearchInput"
        />
        <view class="popup-search-clear" v-if="inspirationSearchKeyword" @click="clearInspirationSearch">
          <SFIcon name="close" :size="22" color="#8E8E93" />
        </view>
      </view>
      <view class="inspiration-list-subtitle">
        <text class="spotlist-count">{{ displayInspirationList.length }}条灵感</text>
      </view>
      <scroll-view class="inspiration-list-scroll" scroll-y :show-scrollbar="false">
        <view class="inspiration-list-empty" v-if="inspirationSearchLoading">
          <text class="loading-text">搜索中...</text>
        </view>
        <view class="inspiration-list-empty" v-else-if="displayInspirationList.length === 0">
          <text class="empty-text">{{ inspirationSearchKeyword ? '未找到相关灵感' : '暂无灵感内容' }}</text>
        </view>
        <view class="inspiration-list-items" v-else>
          <view
            class="inspiration-list-item"
            v-for="item in displayInspirationList"
            :key="item.id"
            @tap="goToInspirationDetail(item)"
          >
            <view class="inspiration-list-cover-wrap">
              <image v-if="item.cover && !item.coverError" class="inspiration-list-cover" :src="item.cover" mode="aspectFill" @error="item.coverError = true" />
              <view v-else class="inspiration-list-cover inspiration-list-cover-placeholder">
                <text class="placeholder-emoji">🗺️</text>
              </view>
            </view>
            <view class="inspiration-list-info">
              <text class="inspiration-list-title">{{ item.title }}</text>
              <text class="inspiration-list-location">{{ item.location }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 底部TabBar -->
    <AppTabBar current="home" @add="showAddMenu = true" />

    <!-- 悬浮小途助手 -->
    <view class="ai-float-btn" @click="goToAIService">
      <view class="ai-float-icon">
        <SFIcon name="sparkle" :size="44" color="#007AFF" />
      </view>
    </view>

    <!-- 添加菜单弹窗 -->
    <AddMenuPopup v-model="showAddMenu" />

    <!-- 目的地选择弹窗 -->
    <view class="popup-mask" v-if="showDestinationPopup" @click="showDestinationPopup = false"></view>
    <view class="destination-popup" :class="{ 'popup-show': showDestinationPopup }">
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <text class="popup-title">选择目的地</text>
        <view class="popup-close" @click="showDestinationPopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 搜索框 -->
      <view class="popup-search">
        <text class="popup-search-icon">🔍</text>
        <input class="popup-search-input" placeholder="搜索全球目的地" />
      </view>

      <!-- 面包屑导航 -->
      <view class="breadcrumb">
        <text class="breadcrumb-item" :class="{ active: currentLevel === 0 }" @click="navigateToLevel(0)">全球</text>
        <text class="breadcrumb-separator" v-if="breadcrumb.length > 0">|</text>
        <text class="breadcrumb-item" :class="{ active: currentLevel === 1 }" @click="navigateToLevel(1)" v-if="breadcrumb.length > 0">{{ breadcrumb[0] }}</text>
        <text class="breadcrumb-separator" v-if="breadcrumb.length > 1">›</text>
        <text class="breadcrumb-item active" v-if="breadcrumb.length > 1">{{ breadcrumb[1] }}</text>
      </view>

      <!-- 当前选中区域标签 -->
      <view class="selected-tag" v-if="currentRegionName">
        <text class="tag-text">{{ currentRegionName }}</text>
      </view>

      <!-- 地区列表 -->
      <scroll-view class="region-scroll" scroll-y>
        <!-- 北方地区 -->
        <view class="region-group" v-for="group in regionGroups" :key="group.name">
          <text class="region-group-title">{{ group.name }}</text>
          <view class="region-tags">
            <view 
              class="region-tag" 
              :class="{ selected: selectedProvince === province }"
              v-for="province in group.provinces" 
              :key="province"
              @click="selectProvince(province)"
            >
              <text class="region-tag-text">{{ province }}</text>
            </view>
          </view>
        </view>
      </scroll-view>

      <!-- 底部操作栏 -->
      <view class="popup-footer">
        <view class="footer-back" @click="goBack">
          <text class="back-arrow">‹</text>
          <text class="back-text">返回</text>
        </view>
        <view class="footer-confirm" @click="confirmSelection">
          <text class="confirm-text">选择 {{ currentRegionName }}</text>
        </view>
      </view>
    </view>

    <view class="popup-mask" v-if="showSpotListPopup" @click="closeSpotList"></view>
    <view class="spotlist-popup" :class="{ 'popup-show': showSpotListPopup }">
      <view class="popup-header">
        <text class="popup-title">{{ currentDestinationName }}</text>
        <view class="popup-close" @click="closeSpotList">
          <text class="close-icon">×</text>
        </view>
      </view>
      <view class="spotlist-subtitle" v-if="currentDestination">
        <text class="spotlist-count">{{ spotList.length }}个景点</text>
      </view>
      <scroll-view class="spotlist-scroll" scroll-y :show-scrollbar="false">
        <view class="spotlist-loading" v-if="spotListLoading">
          <text class="loading-text">加载中...</text>
        </view>
        <view class="spotlist-empty" v-else-if="spotList.length === 0">
          <text class="empty-text">暂无景点数据</text>
        </view>
        <view class="spotlist-items" v-else>
          <view class="spotlist-card" v-for="spot in spotList" :key="spot.id" @tap="goToSpotDetail(spot)">
            <view class="spotlist-cover">
              <image class="spotlist-image" :src="spot.image" mode="aspectFill" />
            </view>
            <view class="spotlist-info">
              <view class="spotlist-header">
                <text class="spotlist-name">{{ spot.name }}</text>
                <view class="spotlist-rating">
                  <SFIcon name="star" :size="22" color="#FFB800" filled />
                  <text class="spotlist-rating-text">{{ spot.rating }}</text>
                </view>
              </view>
              <view class="spotlist-meta">
                <text class="spotlist-tag">{{ spot.category }}</text>
                <text class="spotlist-price">{{ spot.price }}</text>
                <text class="spotlist-duration">{{ spot.duration }}</text>
              </view>
              <text class="spotlist-address">{{ spot.address }}</text>
              <text class="spotlist-opentime">{{ spot.openTime }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="popup-mask" v-if="showDestinationListPopup" @click="closeDestinationListPopup"></view>
    <view class="destination-list-popup" :class="{ 'popup-show': showDestinationListPopup }">
      <view class="popup-header">
        <text class="popup-title">全部目的地</text>
        <view class="popup-close" @click="closeDestinationListPopup">
          <text class="close-icon">×</text>
        </view>
      </view>
      <view class="popup-search-bar">
        <SFIcon name="search" :size="28" color="#86868B" />
        <input
          class="popup-search-input"
          v-model="destSearchKeyword"
          placeholder="搜索目的地"
          confirm-type="search"
          @confirm="doDestSearch"
          @input="onDestSearchInput"
        />
        <view class="popup-search-clear" v-if="destSearchKeyword" @click="clearDestSearch">
          <SFIcon name="close" :size="22" color="#8E8E93" />
        </view>
      </view>
      <view class="destination-list-subtitle">
        <text class="spotlist-count">{{ drawerDestinations.length }}个目的地</text>
      </view>
      <scroll-view class="destination-list-scroll" scroll-y :show-scrollbar="false">
        <view class="destination-list-empty" v-if="destSearchLoading">
          <text class="loading-text">搜索中...</text>
        </view>
        <view class="destination-list-empty" v-else-if="drawerDestinations.length === 0">
          <text class="empty-text">{{ destSearchKeyword ? '未找到相关目的地' : '暂无目的地数据' }}</text>
        </view>
        <view class="destination-list-items" v-else>
          <view
            class="destination-list-item"
            v-for="dest in drawerDestinations"
            :key="dest.id"
            @tap="handleDestinationClick(dest)"
          >
            <view class="destination-list-cover" :style="{ background: dest.gradient || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }">
              <image v-if="dest.image" class="destination-list-image" :src="dest.image" mode="aspectFill" @error="handleImageError(dest)" />
              <view v-else class="destination-list-placeholder">
                <text class="destination-list-placeholder-text">{{ dest.name }}</text>
              </view>
            </view>
            <view class="destination-list-info">
              <view class="destination-list-header">
                <text class="destination-list-name">{{ dest.name }}</text>
                <text class="destination-list-spots">{{ dest.spots }}个景点</text>
              </view>
              <text class="destination-list-subtitle-text">{{ dest.subtitle }}</text>
              <view class="destination-list-meta">
                <view class="destination-list-tag" v-if="dest.tag">
                  <text class="tag-text">{{ dest.tag }}</text>
                </view>
                <view class="destination-list-rating">
                  <SFIcon name="star" :size="22" color="#FFB800" filled />
                  <text class="destination-list-rating-text">{{ dest.rating }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 用户侧边栏 -->
    <UserSlidePanel v-model:visible="showUserSidebar" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import AddMenuPopup from '@/components/AddMenuPopup/AddMenuPopup.vue'
import AppTabBar from '@/components/AppTabBar/AppTabBar.vue'
import UserSlidePanel from '@/components/UserSlidePanel/UserSlidePanel.vue'
import { getDestinations, getRegionGroups, getHotCities, getDestinationSpots, getInspirations, searchDestinations, searchInspirations, getFootprintStats } from '@/api'
import { DEFAULT_AVATAR, getUserInfo } from '@/api/modules/user'
import type { Destination, SpotListItem } from '@/api/modules/destination'
import type { UserInfo } from '@/api/modules/user'
import type { FootprintStats } from '@/api'

// 加载状态
const loading = ref(false)
const logoSrc = ref('/static/logo.png')

// 功能入口数据
const features = ref([
  { id: 1, name: '时光采集', iconName: 'collection', iconColor: '#007AFF', bgColor: '#E3F2FD', path: '/packageCollect/pages/collect/camera' },
  { id: 2, name: '精选攻略', iconName: 'guide', iconColor: '#34C759', bgColor: '#E8F5E9', path: '/pages/guide/list' },
  { id: 3, name: '智能规划', iconName: 'route', iconColor: '#34C759', bgColor: '#E8F5E9', path: '/packageTrip/pages/planning/index' },
  { id: 4, name: '旅行故事', iconName: 'play', iconColor: '#AF52DE', bgColor: '#F3E5F5', path: '/pages/story/index' },
  { id: 5, name: '旅行清单', iconName: 'luggage', iconColor: '#FF2D55', bgColor: '#FCE4EC', path: '/pages/checklist/index' }
])

// 处理功能入口点击
const handleFeatureClick = (item: any) => {
  if (item.path) {
    uni.navigateTo({
      url: item.path
    })
  } else {
    uni.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  }
}

// 添加菜单弹窗
const showAddMenu = ref(false)

const onLogoError = () => {
  logoSrc.value = '/static/logo.png'
}

// 状态栏高度
const statusBarHeight = ref(44)

// 胶囊按钮信息
const menuButtonLeft = ref(0)
const menuButtonTop = ref(0)
const menuButtonHeight = ref(32)
const windowWidth = ref(375)
const navBarHeight = ref(88)

// 计算导航栏样式
const headerStyle = computed(() => {
  return {
    paddingTop: statusBarHeight.value + 'px',
    height: (menuButtonTop.value - statusBarHeight.value) * 2 + menuButtonHeight.value + statusBarHeight.value + 'px',
    minHeight: '88px'
  }
})

const headerHeight = computed(() => {
  const h = (menuButtonTop.value - statusBarHeight.value) * 2 + menuButtonHeight.value + statusBarHeight.value
  return h > 88 ? h : 88
})

// 显示通知
const showNotification = () => {
  uni.navigateTo({
    url: '/packageMessage/pages/message/index'
  })
}

const displayAvatar = computed(() => {
  // 未登录：不要读取历史缓存头像，避免一直显示之前的真人头像
  if (!isLoggedIn.value) {
    try {
      const cachedUser = uni.getStorageSync('userInfo')
      if (cachedUser?.avatar) {
        cachedUser.avatar = ''
        uni.setStorageSync('userInfo', cachedUser)
      }
    } catch (e) {}
    return DEFAULT_AVATAR
  }

  if (userInfo.value.avatar) return userInfo.value.avatar
  try {
    const cachedUser = uni.getStorageSync('userInfo')
    if (cachedUser && cachedUser.avatar) return cachedUser.avatar
  } catch (e) {}
  return DEFAULT_AVATAR
})

// 初始化
onMounted(async () => {
  // 获取系统信息
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 20
  windowWidth.value = systemInfo.windowWidth || 375
  
  // #ifdef MP-WEIXIN
  // 获取微信胶囊按钮位置
  try {
    const menuButton = uni.getMenuButtonBoundingClientRect()
    if (menuButton) {
      menuButtonLeft.value = menuButton.left
      menuButtonTop.value = menuButton.top
      menuButtonHeight.value = menuButton.height
      // 计算导航栏高度
      navBarHeight.value = menuButton.bottom + 8
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  // 加载数据
  await Promise.all([
    loadDestinations(),
    loadRegionGroups(),
    loadUserInfo(),
    loadInspirations()
  ])
})

onShow(() => {
  loadUserInfo()
  loadFootprintStats()
})

// 目的地数据
const allDestinations = ref<Destination[]>([])
const showDestinationListPopup = ref(false)
const showInspirationListPopup = ref(false)
const showSpotListPopup = ref(false)
const currentDestination = ref<Destination | null>(null)
const spotList = ref<SpotListItem[]>([])
const spotListLoading = ref(false)

const currentDestinationName = computed(() => currentDestination.value?.name || '景点列表')

const drawerDestinations = computed(() => {
  if (destSearchKeyword.value.trim()) {
    return destSearchResults.value
  }
  return allDestinations.value
})

const destinations = computed(() => {
  return allDestinations.value.slice(0, 4)
})

const destSearchKeyword = ref('')
const destSearchLoading = ref(false)
const destSearchResults = ref<Destination[]>([])
let destSearchTimer: ReturnType<typeof setTimeout> | null = null

const doDestSearch = async () => {
  const keyword = destSearchKeyword.value.trim()
  if (!keyword) {
    destSearchResults.value = []
    return
  }
  destSearchLoading.value = true
  try {
    const results = await searchDestinations(keyword, 50)
    destSearchResults.value = results
  } catch (e) {
    console.error('搜索目的地失败:', e)
    destSearchResults.value = []
  } finally {
    destSearchLoading.value = false
  }
}

const onDestSearchInput = () => {
  if (destSearchTimer) clearTimeout(destSearchTimer)
  const keyword = destSearchKeyword.value.trim()
  if (!keyword) {
    destSearchResults.value = []
    return
  }
  destSearchTimer = setTimeout(() => {
    doDestSearch()
  }, 500)
}

const clearDestSearch = () => {
  destSearchKeyword.value = ''
  destSearchResults.value = []
}

const inspirationSearchKeyword = ref('')
const inspirationSearchLoading = ref(false)
const inspirationSearchResults = ref<any[]>([])
let inspirationSearchTimer: ReturnType<typeof setTimeout> | null = null

const displayInspirationList = computed(() => {
  if (inspirationSearchKeyword.value.trim()) {
    return inspirationSearchResults.value
  }
  return inspirationList.value
})

const doInspirationSearch = async () => {
  const keyword = inspirationSearchKeyword.value.trim()
  if (!keyword) {
    inspirationSearchResults.value = []
    return
  }
  inspirationSearchLoading.value = true
  try {
    const results = await searchInspirations(keyword, 50)
    inspirationSearchResults.value = results
  } catch (e) {
    console.error('搜索灵感失败:', e)
    inspirationSearchResults.value = []
  } finally {
    inspirationSearchLoading.value = false
  }
}

const onInspirationSearchInput = () => {
  if (inspirationSearchTimer) clearTimeout(inspirationSearchTimer)
  const keyword = inspirationSearchKeyword.value.trim()
  if (!keyword) {
    inspirationSearchResults.value = []
    return
  }
  inspirationSearchTimer = setTimeout(() => {
    doInspirationSearch()
  }, 500)
}

const clearInspirationSearch = () => {
  inspirationSearchKeyword.value = ''
  inspirationSearchResults.value = []
}

// 加载目的地数据
const loadDestinations = async () => {
  loading.value = true
  try {
    const res = await getDestinations({ region: selectedRegion.value })
    allDestinations.value = res.map((dest: any) => ({
      ...dest,
      isFavorite: false
    }))
  } catch (error) {
    console.error('加载目的地失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 点击更多
const openDestinationListPopup = () => {
  showDestinationListPopup.value = true
}

const closeDestinationListPopup = () => {
  showDestinationListPopup.value = false
}

const openInspirationListPopup = () => {
  showInspirationListPopup.value = true
}

const closeInspirationListPopup = () => {
  showInspirationListPopup.value = false
}

// 图片加载失败处理
const handleImageError = (dest: any) => {
  dest.image = ''
}

// 跳转到 AI 客服
const goToAIService = () => {
  uni.navigateTo({
    url: '/pages/service/index'
  })
}

// 跳转到点亮足迹页面
const goToFootprint = () => {
  uni.navigateTo({
    url: '/packageFootprint/pages/footprint/index'
  })
}

const loadDestinationSpots = async (dest: Destination) => {
  spotListLoading.value = true
  try {
    const res = await getDestinationSpots(dest.id, { limit: 20 })
    spotList.value = res
  } catch (error) {
    console.error('加载景点列表失败:', error)
    spotList.value = []
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    spotListLoading.value = false
  }
}

const openSpotList = async (dest: Destination) => {
  currentDestination.value = dest
  showSpotListPopup.value = true
  await loadDestinationSpots(dest)
}

const closeSpotList = () => {
  showSpotListPopup.value = false
}

const goToSpotDetail = (spot: SpotListItem) => {
  showSpotListPopup.value = false
  uni.navigateTo({
    url: `/packageTrip/pages/destination/detail?id=${spot.id}&name=${encodeURIComponent(spot.name)}`,
    fail: (err) => {
      console.error('跳转失败:', err)
      uni.showToast({
        title: '页面跳转失败',
        icon: 'none'
      })
    }
  })
}

const handleDestinationClick = (dest: Destination) => {
  showDestinationListPopup.value = false
  openSpotList(dest)
}

// 灵感推荐数据
const inspirationList = ref<any[]>([])

const loadInspirations = async () => {
  try {
    const list = await getInspirations(6)
    inspirationList.value = list
  } catch (e) {
    console.error('加载灵感推荐失败:', e)
  }
}

// 跳转到故事列表
const goToStoryList = () => {
  uni.navigateTo({
    url: '/pages/story/index'
  })
}

// 跳转到灵感详情
const goToInspirationDetail = (item: any) => {
  showInspirationListPopup.value = false
  uni.navigateTo({
    url: `/packageGuide/pages/guide/detail?id=${item.id}`
  })
}

// 目的地弹窗相关
const showDestinationPopup = ref(false)
const currentLevel = ref(2) // 0: 全球, 1: 亚洲, 2: 中国大陆
const breadcrumb = ref(['亚洲', '中国大陆'])
const currentRegionName = ref('中国大陆')
const selectedProvince = ref('')
const selectedRegion = ref('')

const footprintStats = ref<FootprintStats>({
  totalFootprints: 0,
  domesticFootprints: 0,
  overseasFootprints: 0,
  provinceCount: 0,
  countryCount: 0,
  collectCount: 0,
  provinces: [],
  countries: []
})

const loadFootprintStats = async () => {
  try {
    const res = await getFootprintStats()
    if (res) footprintStats.value = res
  } catch (error) {
    console.error('加载足迹统计失败:', error)
  }
}

// 地区分组数据
const regionGroups = ref<Array<{ name: string; provinces: string[] }>>([])

// 加载地区数据
const loadRegionGroups = async () => {
  try {
    const res = await getRegionGroups()
    regionGroups.value = res
  } catch (error) {
    console.error('加载地区数据失败:', error)
  }
}

// 选择省份
const selectProvince = (province: string) => {
  selectedProvince.value = province
}

// 确认选择
const confirmSelection = () => {
  if (selectedProvince.value) {
    selectedRegion.value = selectedProvince.value
  } else {
    selectedRegion.value = currentRegionName.value
  }
  showDestinationPopup.value = false
  showDestinationListPopup.value = false
  loadDestinations()
}

// 返回上一级
const goBack = () => {
  if (currentLevel.value > 0) {
    currentLevel.value--
    breadcrumb.value.pop()
    if (breadcrumb.value.length > 0) {
      currentRegionName.value = breadcrumb.value[breadcrumb.value.length - 1]
    } else {
      currentRegionName.value = '全球'
    }
    selectedProvince.value = ''
  }
}

// 导航到指定层级
const navigateToLevel = (level: number) => {
  if (level < currentLevel.value) {
    currentLevel.value = level
    breadcrumb.value = breadcrumb.value.slice(0, level)
    if (level === 0) {
      currentRegionName.value = '全球'
    } else {
      currentRegionName.value = breadcrumb.value[level - 1]
    }
    selectedProvince.value = ''
  }
}

// 用户侧边栏
const showUserSidebar = ref(false)

const userInfo = ref<UserInfo>({
  id: 0,
  username: '',
  nickname: '',
  name: '',
  avatar: '',
  phone: '',
  email: '',
  wechatBound: false,
  vipLevel: 0,
  vipExpireDate: '',
  createTime: '',
  permissions: [],
  roles: [],
  stats: {
    trips: 0,
    spots: 0,
    guides: 0,
    footprints: 0
  }
})

const token = ref('')
const isLoggedIn = computed(() => !!token.value || !!userInfo.value.id)

const syncAuthCache = () => {
  try {
    token.value = uni.getStorageSync('token') || ''
  } catch (e) {
    token.value = ''
  }
  try {
    const cachedUser = uni.getStorageSync('userInfo')
    if (cachedUser) {
      userInfo.value = cachedUser
    }
  } catch (e) {}
}

const loadUserInfo = async () => {
  syncAuthCache()
  if (!token.value) return
  try {
    const res = await getUserInfo()
    userInfo.value = res
    uni.setStorageSync('userInfo', res)
  } catch (e) {}
}

onShow(() => {
  if (selectedRegion.value) {
    loadDestinations()
  }
})
</script>

<style lang="scss" scoped>
// 苹果风格变量
$primary-color: #007AFF;
$bg-color: #F5F5F7;
$card-bg: #FFFFFF;
$text-primary: #1D1D1F;
$text-secondary: #86868B;
$border-radius-lg: 24rpx;
$border-radius-md: 16rpx;
$shadow-light: 0 2rpx 20rpx rgba(0, 0, 0, 0.06);
$shadow-medium: 0 4rpx 30rpx rgba(0, 0, 0, 0.1);

.page {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 140rpx;
}

// 顶部导航栏
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32rpx 12rpx;
  background: $card-bg;
  box-sizing: border-box;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.header-mp-right {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 101;
}

.avatar-wrapper, .avatar-btn {
  position: relative;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  overflow: hidden;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-avatar {
  width: 100%;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.logo-icon {
  font-size: 48rpx;
}

.logo-text {
  font-size: 36rpx;
  font-weight: 600;
  color: $primary-color;
  letter-spacing: 2rpx;
}

.logo-image {
  width: 200rpx;
  height: 80rpx;
}

.brand-text {
  font-size: 36rpx;
  font-weight: 700;
  line-height: 1;
  letter-spacing: 2rpx;
  color: #0DB2C1;
  background: linear-gradient(90deg, #FFA927 0%, #FFD15C 20%, #40C08E 50%, #0DB2C1 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.icon-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  border-radius: 50%;
}

.icon {
  font-size: 36rpx;
}

// 搜索框
.search-container {
  padding: 20rpx 32rpx 0;
  background: $card-bg;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 22rpx 28rpx;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
}

.search-placeholder {
  flex: 1;
  font-size: 28rpx;
  color: #86868B;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.search-clear {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #E5E5EA;
  border-radius: 50%;
}

.popup-search-bar {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin: 0 40rpx 16rpx;
  padding: 18rpx 24rpx;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
}

.popup-search-input {
  flex: 1;
  font-size: 26rpx;
  color: $text-primary;
  background: transparent;
}

.popup-search-clear {
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #E5E5EA;
  border-radius: 50%;
}

// 功能入口
.feature-grid {
  display: flex;
  justify-content: space-between;
  padding: 32rpx 0 24rpx;
  background: $card-bg;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14rpx;
}

.feature-icon-wrapper {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  border-radius: 20rpx;
}

.feature-text {
  font-size: 24rpx;
  color: $text-primary;
  font-weight: 500;
}

// 横幅广告
.banner {
  margin: 0 32rpx 28rpx;
  position: relative;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $shadow-medium;
  height: 240rpx;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.45) 0%, rgba(0, 0, 0, 0.25) 100%);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  height: 100%;
  box-sizing: border-box;
}

.banner-left {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.banner-tag {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.banner-tag-text {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.9);
}

.banner-title {
  font-size: 38rpx;
  font-weight: 700;
  color: #FFFFFF;
  letter-spacing: 4rpx;
}

.banner-stats {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 8rpx;
}

.banner-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.banner-stat-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.banner-stat-label {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.8);
}

.banner-stat-divider {
  width: 2rpx;
  height: 36rpx;
  background: rgba(255, 255, 255, 0.4);
}

.banner-right {
  display: flex;
  align-items: center;
}

.banner-action-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 16rpx 24rpx;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 100rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.banner-action {
  font-size: 26rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.banner-arrow {
  font-size: 32rpx;
  color: #FFFFFF;
}

// 地区筛选
.section-header {
  padding: 28rpx 32rpx 16rpx;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex-shrink: 0;
}

.section-title-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex-shrink: 0;
}

.section-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $text-primary;
}

.city-selector {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 10rpx 20rpx;
  background: $bg-color;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
}

.location-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 10rpx 14rpx;
  background: rgba(0, 122, 255, 0.08);
  border-radius: 100rpx;
  color: $primary-color;
}

.location-action-text {
  font-size: 24rpx;
  color: $primary-color;
  font-weight: 600;
}

.city-name {
  font-size: 26rpx;
  color: $text-secondary;
  font-weight: 500;
}

.section-more {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: auto;
}

.more-text {
  font-size: 28rpx;
  color: $text-secondary;
  font-weight: 500;
}

// 目的地卡片
.destination-scroll {
}

.destination-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 0 32rpx 28rpx;
  box-sizing: border-box;
}

.destination-card {
  background: $card-bg;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $shadow-light;

  &:active {
    transform: scale(0.98);
    box-shadow: $shadow-medium;
  }
}

.card-image {
  width: 100%;
  height: 220rpx;
  position: relative;
  overflow: hidden;
  border-radius: $border-radius-lg $border-radius-lg 0 0;
}

.dest-image {
  width: 100%;
  height: 100%;
}

.card-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-text {
  font-size: 48rpx;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.card-tag {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  padding: 6rpx 16rpx;
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
  border-radius: 8rpx;
}

.tag-text {
  font-size: 20rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.card-rating {
  position: absolute;
  bottom: 16rpx;
  right: 16rpx;
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 6rpx 12rpx;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  border-radius: 8rpx;
}

.rating-icon {
  font-size: 20rpx;
}

.rating-text {
  font-size: 22rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #E8E8ED 0%, #D1D1D6 100%);
}

.placeholder-icon {
  font-size: 80rpx;
  opacity: 0.5;
}

.card-info {
  padding: 18rpx 20rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.card-spots {
  font-size: 22rpx;
  color: $text-secondary;
}

.card-subtitle {
  font-size: 24rpx;
  color: $text-secondary;
  display: block;
}

// 加载更多
.load-more-wrapper,
.collapse-wrapper {
  display: flex;
  justify-content: center;
  padding: 16rpx 0 32rpx;
}

.load-more-btn,
.collapse-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 40rpx;
  background: $card-bg;
  border-radius: 100rpx;
  border: 2rpx solid #E5E5EA;
  transition: all 0.2s ease;

  &:active {
    background: $bg-color;
    transform: scale(0.98);
  }
}

.load-more-text,
.collapse-text {
  font-size: 26rpx;
  color: $primary-color;
  font-weight: 500;
}

.load-more-arrow,
.collapse-arrow {
  font-size: 24rpx;
  color: $primary-color;
}

// 灵感推荐
.inspiration-section {
  padding: 0 0 60rpx;
  margin-top: -8rpx;
}

.inspiration-scroll {
  white-space: nowrap;
  margin-top: 16rpx;
  padding-left: 32rpx;
}

.inspiration-list {
  display: inline-flex;
  gap: 20rpx;
  padding-right: 32rpx;
}

.inspiration-card {
  width: 420rpx;
  flex-shrink: 0;
  border-radius: $border-radius-lg;
  overflow: hidden;
  background: $card-bg;
  box-shadow: $shadow-light;

  &:active {
    transform: scale(0.98);
  }
}

.inspiration-cover {
  width: 100%;
  height: 240rpx;
  display: block;
}

.inspiration-cover-placeholder {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-emoji {
  font-size: 48rpx;
}

.inspiration-info {
  padding: 18rpx;
}

.inspiration-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.inspiration-location {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
}

// 悬浮 AI 客服按钮
.ai-float-btn {
  position: fixed;
  right: 32rpx;
  bottom: 260rpx;
  z-index: 900;
  opacity: 0;
  animation: floatBtnIn 0.5s ease 1s forwards;
}

@keyframes floatBtnIn {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.ai-float-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 44rpx;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.08), 0 0 1rpx rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.92);
  }
}

// 弹窗遮罩层
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

// 目的地选择弹窗
.destination-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 85vh;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.spotlist-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 85vh;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx 40rpx 24rpx;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
}

.popup-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  border-radius: 50%;
}

.spotlist-subtitle {
  padding: 0 40rpx 20rpx;
}

.spotlist-count {
  font-size: 26rpx;
  color: $text-secondary;
}

.spotlist-scroll {
  flex: 1;
  height: 0;
  padding: 0 40rpx 40rpx;
  box-sizing: border-box;
}

// 目的地列表抽屉
.destination-list-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 82vh;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.destination-list-subtitle {
  padding: 0 40rpx 20rpx;
}

.destination-list-scroll {
  flex: 1;
  height: 0;
  padding: 0 40rpx 40rpx;
  box-sizing: border-box;
}

.destination-list-empty {
  min-height: 320rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.destination-list-items {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.destination-list-item {
  display: flex;
  gap: 20rpx;
  padding: 16rpx;
  background: $bg-color;
  border-radius: 28rpx;

  &:active {
    transform: scale(0.99);
  }
}

.destination-list-cover {
  width: 180rpx;
  height: 180rpx;
  border-radius: 22rpx;
  overflow: hidden;
  background: #E5E5EA;
  flex-shrink: 0;
}

.destination-list-image {
  width: 100%;
  height: 100%;
}

.destination-list-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12rpx;
  box-sizing: border-box;
}

.destination-list-placeholder-text {
  font-size: 32rpx;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.92);
  text-align: center;
  line-height: 1.25;
}

.destination-list-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.destination-list-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12rpx;
}

.destination-list-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.2;
}

.destination-list-spots {
  font-size: 22rpx;
  color: $text-secondary;
  flex-shrink: 0;
}

.destination-list-subtitle-text {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.4;
  display: block;
}

.destination-list-meta {
  margin-top: auto;
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.destination-list-tag {
  padding: 6rpx 14rpx;
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
  border-radius: 999rpx;
}

.destination-list-tag .tag-text {
  font-size: 20rpx;
  color: #FFFFFF;
  font-weight: 600;
}

.destination-list-rating {
  display: flex;
  align-items: center;
  gap: 4rpx;
  margin-left: auto;
}

.destination-list-rating-text {
  font-size: 24rpx;
  color: $text-primary;
  font-weight: 600;
}

// 灵感列表抽屉
.inspiration-list-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 82vh;
  background: $card-bg;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.inspiration-list-subtitle {
  padding: 0 40rpx 20rpx;
}

.inspiration-list-scroll {
  flex: 1;
  height: 0;
  padding: 0 40rpx 40rpx;
  box-sizing: border-box;
}

.inspiration-list-empty {
  min-height: 320rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.inspiration-list-items {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.inspiration-list-item {
  display: flex;
  gap: 20rpx;
  padding: 16rpx;
  background: $bg-color;
  border-radius: 28rpx;

  &:active {
    transform: scale(0.99);
  }
}

.inspiration-list-cover-wrap {
  width: 180rpx;
  height: 180rpx;
  border-radius: 22rpx;
  overflow: hidden;
  background: #E5E5EA;
  flex-shrink: 0;
}

.inspiration-list-cover {
  width: 100%;
  height: 100%;
}

.inspiration-list-cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
}

.inspiration-list-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10rpx;
}

.inspiration-list-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.3;
}

.inspiration-list-location {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.4;
  display: block;
}

.spotlist-loading,
.spotlist-empty {
  padding: 60rpx 0;
  display: flex;
  justify-content: center;
}

.spotlist-items {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.spotlist-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: $bg-color;
  border-radius: $border-radius-md;
}

.spotlist-cover {
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx;
  overflow: hidden;
  background: #E5E5EA;
  flex-shrink: 0;
}

.spotlist-image {
  width: 100%;
  height: 100%;
}

.spotlist-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.spotlist-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.spotlist-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.spotlist-rating {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.spotlist-rating-text {
  font-size: 22rpx;
  color: $text-secondary;
}

.spotlist-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex-wrap: wrap;
}

.spotlist-tag {
  font-size: 22rpx;
  color: #00C853;
  background: rgba(0, 200, 83, 0.1);
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
}

.spotlist-price,
.spotlist-duration {
  font-size: 22rpx;
  color: $text-secondary;
}

.spotlist-address,
.spotlist-opentime {
  font-size: 24rpx;
  color: $text-secondary;
}

.close-icon {
  font-size: 40rpx;
  color: $text-secondary;
  line-height: 1;
}

// 弹窗搜索框
.popup-search {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin: 0 40rpx 32rpx;
  padding: 24rpx 32rpx;
  background: $bg-color;
  border-radius: 20rpx;
  border: 2rpx solid #E5E5EA;
}

.popup-search-icon {
  font-size: 32rpx;
  color: $text-secondary;
}

.popup-search-input {
  flex: 1;
  font-size: 30rpx;
  color: $text-primary;
  background: transparent;
}

// 面包屑导航
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 0 40rpx 24rpx;
}

.breadcrumb-item {
  font-size: 28rpx;
  color: $text-secondary;
  
  &.active {
    color: #00C853;
    font-weight: 500;
  }
}

.breadcrumb-separator {
  font-size: 24rpx;
  color: $text-secondary;
}

// 当前选中区域标签
.selected-tag {
  display: inline-flex;
  margin: 0 40rpx 32rpx;
  padding: 16rpx 32rpx;
  background: #00C853;
  border-radius: 12rpx;
  align-self: flex-start;
}

.tag-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

// 地区滚动区域
.region-scroll {
  flex: 1;
  padding: 0 40rpx;
  overflow-y: auto;
}

.region-group {
  margin-bottom: 40rpx;
}

.region-group-title {
  font-size: 26rpx;
  color: $text-secondary;
  margin-bottom: 20rpx;
}

.region-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.region-tag {
  padding: 20rpx 36rpx;
  background: $bg-color;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: rgba(0, 200, 83, 0.1);
    border-color: #00C853;
  }

  &:active {
    transform: scale(0.96);
  }
}

.region-tag-text {
  font-size: 28rpx;
  color: $text-primary;
}

.region-tag.selected .region-tag-text {
  color: #00C853;
  font-weight: 500;
}

// 弹窗底部操作栏
.popup-footer {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx 40rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: $card-bg;
  border-top: 1rpx solid #E5E5EA;
}

.footer-back {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 24rpx 32rpx;
  background: $bg-color;
  border-radius: 100rpx;
}

.back-arrow {
  font-size: 32rpx;
  color: $text-primary;
}

.back-text {
  font-size: 28rpx;
  color: $text-primary;
}

.footer-confirm {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28rpx 40rpx;
  background: #00C853;
  border-radius: 100rpx;
}

.confirm-text {
  font-size: 30rpx;
  color: #FFFFFF;
  font-weight: 600;
}
</style>
