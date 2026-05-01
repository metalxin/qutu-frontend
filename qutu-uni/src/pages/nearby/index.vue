<template>
  <view class="page">
    <!-- 顶部信息栏 -->
    <view class="top-bar" :style="topBarStyle">
      <view class="city-info" @click="showCityPicker">
        <text class="city-name">{{ currentCity }}</text>
        <text class="city-arrow">▼</text>
        <view class="weather-info">
          <text class="weather-text">{{ weather }}</text>
        </view>
      </view>
      
      <!-- 微信小程序头像靠近右上角胶囊按钮 -->
      <!-- #ifdef MP-WEIXIN -->
      <view
        class="top-mp-right"
        :style="{
          height: menuButtonHeight + 'px',
          top: menuButtonTop + 'px',
          right: (windowWidth - menuButtonLeft + 8) + 'px'
        }"
      >
        <view class="avatar-btn" @click="goToHome">
          <image v-if="displayAvatar" :src="displayAvatar" class="header-avatar" mode="aspectFill" />
          <SFIcon v-else name="user" :size="36" color="#1D1D1F" />
          <view class="red-dot" v-if="hasMessage"></view>
        </view>
      </view>
      <!-- #endif -->

      <view class="top-right" :style="{ marginRight: menuButtonSpace + 'px' }">
        <!-- 非微信小程序显示头像在右侧 -->
        <!-- #ifndef MP-WEIXIN -->
        <view class="icon-btn" @click="goToHome">
          <view class="avatar-wrapper">
            <image v-if="displayAvatar" :src="displayAvatar" class="header-avatar" mode="aspectFill" />
            <SFIcon v-else name="user" :size="36" color="#1D1D1F" />
            <view class="red-dot" v-if="hasMessage"></view>
          </view>
        </view>
        <!-- #endif -->
      </view>
    </view>

    <!-- 分类筛选 -->
    <view class="filter-bar" :style="{ top: navBarHeight + 'px' }">
      <scroll-view class="filter-scroll" scroll-x :show-scrollbar="false">
        <view class="filter-wrapper">
          <view 
            class="filter-item" 
            v-for="cat in categories" 
            :key="cat.id"
            :class="{ active: currentCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >
            <text class="filter-emoji">{{ cat.emoji }}</text>
            <text class="filter-name">{{ cat.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 地图区域 -->
    <view class="map-container" :style="{ top: (navBarHeight + 80) + 'px' }">
      <map
        id="nearbyMap"
        class="nearby-map"
        :latitude="mapCenter.latitude"
        :longitude="mapCenter.longitude"
        :scale="mapScale"
        :markers="mapMarkers"
        :show-location="true"
        @markertap="onMarkerTap"
        @regionchange="onRegionChange"
      />
      
      <!-- 定位按钮 -->
      <view class="map-controls">
        <view class="control-btn" @click="locateMe">
          <SFIcon name="crosshair" :size="40" color="#1D1D1F" />
        </view>
      </view>
      
      <!-- POI 气泡标记 -->
      <view 
        class="poi-bubble" 
        v-for="poi in visiblePois" 
        :key="poi.id"
        :style="{ left: poi.screenX + 'px', top: poi.screenY + 'px' }"
        @click="selectPoi(poi)"
      >
        <image v-if="poi.image" class="bubble-image" :src="poi.image" mode="aspectFill" />
        <view class="bubble-content">
          <text class="bubble-title">{{ poi.title }}</text>
          <text class="bubble-subtitle" v-if="poi.subtitle">{{ poi.subtitle }}</text>
        </view>
      </view>
    </view>

    <!-- POI详情弹窗 -->
    <view class="poi-popup-overlay" v-if="showPoiPopup" @click="closePoiPopup">
      <view class="poi-popup" @click.stop>
        <view class="popup-handle"></view>
        
        <scroll-view class="popup-scroll" scroll-y :show-scrollbar="false">
          <view class="popup-header">
            <view class="popup-title-row">
              <text class="popup-title">{{ selectedPoi?.name }}</text>
              <view class="popup-close" @click="closePoiPopup">
                <SFIcon name="close" :size="36" color="#666" />
              </view>
            </view>
            <view class="popup-meta">
              <text class="meta-distance">距我直线 {{ selectedPoi?.distance }}</text>
              <view class="meta-rating">
                <text class="rating-score">{{ selectedPoi?.rating }}分</text>
                <SFIcon name="star" :size="24" color="#FFB800" filled />
              </view>
              <text class="meta-category">{{ selectedPoi?.category }}</text>
            </view>
          </view>
          
          <scroll-view class="popup-gallery" scroll-x :show-scrollbar="false">
            <view class="gallery-list">
              <image 
                class="gallery-image" 
                v-for="(img, idx) in selectedPoi?.images" 
                :key="idx"
                :src="img"
                mode="aspectFill"
                @click="previewImage(idx)"
              />
            </view>
          </scroll-view>

          <view class="popup-description">
            <text class="desc-title">地点介绍</text>
            <text class="desc-text">{{ selectedPoi?.description || '暂无介绍' }}</text>
          </view>

          <view class="popup-info">
            <view class="info-item" v-if="selectedPoi?.address">
              <SFIcon name="location" :size="28" color="#86868B" />
              <text class="info-text">{{ selectedPoi?.address }}</text>
            </view>
            <view class="info-item" v-if="selectedPoi?.openTime">
              <SFIcon name="clock" :size="28" color="#86868B" />
              <text class="info-text">{{ selectedPoi?.openTime }}</text>
            </view>
            <view class="info-item" v-if="selectedPoi?.phone">
              <SFIcon name="phone" :size="28" color="#86868B" />
              <text class="info-text">{{ selectedPoi?.phone }}</text>
            </view>
          </view>
        </scroll-view>

        <view class="popup-actions">
          <view class="action-btn" @click="addToItinerary">
            <SFIcon name="plus" :size="32" color="#007AFF" />
            <text class="action-text">添加行程</text>
          </view>
          <view class="action-btn" @click="toggleFavorite">
            <SFIcon name="heart" :size="32" :color="isFavorited ? '#FF2D55' : '#1D1D1F'" :filled="isFavorited" />
            <text class="action-text">收藏</text>
          </view>
          <view class="action-btn" @click="checkIn">
            <SFIcon name="checkmark" :size="32" color="#34C759" />
            <text class="action-text">打卡</text>
          </view>
          <view class="action-btn" @click="navigateTo">
            <SFIcon name="navigation" :size="32" color="#007AFF" />
            <text class="action-text">导航</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部导航栏 -->
    <view class="tabbar">
      <view class="tabbar-item" @click="goToHome">
        <SFIcon name="home" :size="44" />
        <text class="tabbar-text">主页</text>
      </view>
      <view class="tabbar-center">
        <view class="add-btn" @click="showAddMenuPopup = true">
          <SFIcon name="plus" :size="48" color="#FFFFFF" />
        </view>
      </view>
      <view class="tabbar-item active" @click="goToNearby">
        <SFIcon name="location" :size="44" />
        <text class="tabbar-text">附近</text>
      </view>
    </view>

    <!-- 添加菜单弹窗 -->
    <view class="add-menu-mask" :class="{ show: showAddMenuPopup }" @click="showAddMenuPopup = false"></view>
    <view class="add-menu-popup" :class="{ show: showAddMenuPopup }">
      <!-- 菜单选项 -->
      <view class="menu-options">
        <view class="menu-option dark animate-item item-1" :class="{ show: showAddMenuPopup }" @click="handleAddLocation">
          <text class="option-title">添加地点</text>
          <view class="option-icon-wrapper">
            <SFIcon name="location" :size="40" color="#FFFFFF" />
          </view>
        </view>

        <view class="menu-option light animate-item item-2" :class="{ show: showAddMenuPopup }" @click="handleRecordTrip">
          <view class="option-content">
            <text class="option-title">记录行程</text>
            <text class="option-desc">记录你的出行路线和足迹</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="route" :size="40" color="#FF9500" />
          </view>
        </view>

        <view class="menu-option light animate-item item-3" :class="{ show: showAddMenuPopup }" @click="handleTakePhoto">
          <view class="option-content">
            <text class="option-title">拍照打卡</text>
            <text class="option-desc">拍照记录当前位置</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="camera" :size="40" color="#AF52DE" />
          </view>
        </view>
      </view>

      <!-- 关闭按钮 -->
      <view class="menu-close animate-item item-4" :class="{ show: showAddMenuPopup }" @click="showAddMenuPopup = false">
        <SFIcon name="close" :size="40" color="#FFFFFF" />
      </view>
    </view>

    <!-- 城市选择弹窗 -->
    <view class="popup-mask" v-if="showCityPopup" @click="showCityPopup = false"></view>
    <view class="city-popup" :class="{ 'popup-show': showCityPopup }">
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <text class="popup-title">选择城市</text>
        <view class="popup-close" @click="showCityPopup = false">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 搜索框 -->
      <view class="popup-search">
        <text class="popup-search-icon">🔍</text>
        <input class="popup-search-input" placeholder="搜索城市" v-model="citySearchText" />
      </view>

      <!-- 当前定位城市 -->
      <view class="current-location-section">
        <text class="section-label">当前定位</text>
        <view class="location-city" @click="selectCity(currentCity)">
          <SFIcon name="location" :size="28" color="#007AFF" />
          <text class="location-city-text">{{ currentCity }}</text>
        </view>
      </view>

      <!-- 热门城市 -->
      <view class="hot-cities-section">
        <text class="section-label">热门城市</text>
        <view class="city-tags">
          <view 
            class="city-tag" 
            v-for="city in hotCities" 
            :key="city"
            :class="{ selected: selectedCityTemp === city }"
            @click="selectCityTemp(city)"
          >
            <text class="city-tag-text">{{ city }}</text>
          </view>
        </view>
      </view>

      <!-- 城市列表 -->
      <scroll-view class="city-scroll" scroll-y>
        <view class="city-group" v-for="group in filteredCityGroups" :key="group.letter">
          <text class="city-group-title">{{ group.letter }}</text>
          <view class="city-list">
            <view 
              class="city-item" 
              v-for="city in group.cities" 
              :key="city"
              :class="{ selected: selectedCityTemp === city }"
              @click="selectCityTemp(city)"
            >
              <text class="city-item-text">{{ city }}</text>
            </view>
          </view>
        </view>
      </scroll-view>

      <!-- 底部操作栏 -->
      <view class="popup-footer">
        <view class="footer-cancel" @click="showCityPopup = false">
          <text class="cancel-text">取消</text>
        </view>
        <view class="footer-confirm" @click="confirmCitySelection">
          <text class="confirm-text">确认选择</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getNearbyCategories, getNearbyPois, getCityList, getPoiDetail } from '@/api'

interface POI {
  id: number
  name: string
  title: string
  subtitle?: string
  latitude: number
  longitude: number
  category: string
  categoryType: string
  rating: number
  distance: string
  image?: string
  images: string[]
  screenX?: number
  screenY?: number
  description?: string
  address?: string
  openTime?: string
  phone?: string
}

interface Category {
  id: string
  name: string
  emoji: string
}

interface CityGroup {
  letter: string
  cities: string[]
}

// 当前城市和天气
const currentCity = ref('合肥市')
const weather = ref('多云2°- 5°☁')
const loading = ref(false)

// 地图配置
const mapCenter = ref({
  latitude: 31.8206,
  longitude: 117.2272
})
const mapScale = ref(13)

// 分类
const categories = ref<Category[]>([
  { id: 'all', name: '全部', emoji: '🔥' },
  { id: 'food', name: '私藏美食', emoji: '🍴' },
  { id: 'place', name: '宝藏去处', emoji: '🌳' }
])
const currentCategory = ref('all')

// POI数据
const allPois = ref<POI[]>([])

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getNearbyCategories()
    if (res && res.length > 0) {
      categories.value = res
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载POI
const loadPois = async () => {
  loading.value = true
  try {
    const res = await getNearbyPois({
      categoryId: currentCategory.value,
      latitude: mapCenter.value.latitude,
      longitude: mapCenter.value.longitude
    })
    if (res && res.length > 0) {
      allPois.value = res as POI[]
    }
  } catch (error) {
    console.error('加载POI失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载城市列表
const loadCityList = async () => {
  try {
    const res = await getCityList()
    if (res) {
      if (res.hotCities && res.hotCities.length > 0) {
        hotCities.value = res.hotCities
      }
      if (res.cityGroups && res.cityGroups.length > 0) {
        cityGroups.value = res.cityGroups
      }
    }
  } catch (error) {
    console.error('加载城市列表失败:', error)
  }
}

// 根据分类筛选POI
const filteredPois = computed(() => {
  if (currentCategory.value === 'all') {
    return allPois.value
  }
  return allPois.value.filter(poi => poi.categoryType === currentCategory.value)
})

// 地图标记点
const mapMarkers = computed(() => {
  return filteredPois.value.map(poi => ({
    id: poi.id,
    latitude: poi.latitude,
    longitude: poi.longitude,
    iconPath: poi.categoryType === 'food' ? '/static/icons/marker-food.png' : '/static/icons/marker-place.png',
    width: 40,
    height: 40,
    callout: {
      content: poi.title,
      display: 'ALWAYS',
      padding: 8,
      borderRadius: 8,
      bgColor: '#FFFFFF',
      fontSize: 12,
      color: '#1D1D1F'
    }
  }))
})

// 可见的POI（简化处理，实际应根据地图视野计算）
const visiblePois = computed((): POI[] => {
  return [] // 暂时不使用自定义气泡，使用地图原生callout
})

// 选中的POI
const selectedPoi = ref<POI | null>(null)
const showPoiPopup = ref(false)
const isFavorited = ref(false)

// 选择分类
const selectCategory = (id: string) => {
  currentCategory.value = id
  loadPois()
}

// 点击标记
const onMarkerTap = async (e: any) => {
  const markerId = e.detail?.markerId || e.markerId
  const poi = allPois.value.find(p => p.id === markerId)
  if (poi) {
    // 获取POI详情
    try {
      const detail = await getPoiDetail(poi.id)
      if (detail) {
        selectPoi({ ...poi, ...detail })
      } else {
        selectPoi(poi)
      }
    } catch (error) {
      selectPoi(poi)
    }
  }
}

// 选择POI
const selectPoi = (poi: POI) => {
  selectedPoi.value = poi
  showPoiPopup.value = true
  isFavorited.value = false // 默认未收藏，实际应从接口获取
}

// 关闭弹窗
const closePoiPopup = () => {
  showPoiPopup.value = false
  selectedPoi.value = null
}

// 预览图片
const previewImage = (index: number) => {
  if (selectedPoi.value?.images?.length) {
    uni.previewImage({
      urls: selectedPoi.value.images,
      current: index
    })
  }
}

// 添加行程
const addToItinerary = () => {
  uni.showToast({ title: '已添加到行程', icon: 'success' })
  closePoiPopup()
}

// 收藏/取消收藏
const toggleFavorite = () => {
  isFavorited.value = !isFavorited.value
  uni.showToast({ 
    title: isFavorited.value ? '已收藏' : '已取消收藏', 
    icon: 'success' 
  })
}

// 打卡
const checkIn = () => {
  uni.showToast({ title: '打卡成功', icon: 'success' })
  closePoiPopup()
}

// 导航
const navigateTo = () => {
  if (selectedPoi.value) {
    uni.openLocation({
      latitude: selectedPoi.value.latitude,
      longitude: selectedPoi.value.longitude,
      name: selectedPoi.value.name,
      address: selectedPoi.value.address || '',
      scale: 18
    })
  }
}

const ensureLocationAuth = async () => {
  return await new Promise<boolean>((resolve) => {
    uni.getSetting({
      success: (settingRes) => {
        const hasAuth = !!settingRes.authSetting?.['scope.userLocation']
        if (hasAuth) {
          resolve(true)
          return
        }
        uni.authorize({
          scope: 'scope.userLocation',
          success: () => resolve(true),
          fail: () => {
            uni.showModal({
              title: '需要定位权限',
              content: '请开启定位权限以查看附近',
              success: (res) => {
                if (res.confirm) {
                  uni.openSetting({
                    success: (openRes) => {
                      resolve(!!openRes.authSetting?.['scope.userLocation'])
                    },
                    fail: () => resolve(false)
                  })
                } else {
                  resolve(false)
                }
              }
            })
          }
        })
      },
      fail: () => resolve(false)
    })
  })
}

// 定位到当前位置
const locateMe = async () => {
  const ok = await ensureLocationAuth()
  if (!ok) return
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      mapCenter.value = {
        latitude: res.latitude,
        longitude: res.longitude
      }
      loadPois()
    },
    fail: () => {
      uni.showToast({
        title: '获取位置失败',
        icon: 'none'
      })
    }
  })
}

// 城市选择弹窗
const showCityPopup = ref(false)
const citySearchText = ref('')
const selectedCityTemp = ref('')

// 热门城市
const hotCities = ref<string[]>(['北京', '上海', '广州', '深圳', '杭州', '成都', '重庆', '西安', '南京', '武汉', '苏州', '天津'])

// 城市分组数据
const cityGroups = ref<CityGroup[]>([
  { letter: 'A', cities: ['安庆', '安阳', '鞍山'] },
  { letter: 'B', cities: ['北京', '保定', '包头', '蚌埠', '滨州'] },
  { letter: 'C', cities: ['成都', '重庆', '长沙', '长春', '常州', '沧州'] },
  { letter: 'D', cities: ['大连', '东莞', '大同', '德州'] },
  { letter: 'F', cities: ['福州', '佛山', '阜阳'] },
  { letter: 'G', cities: ['广州', '贵阳', '桂林', '赣州'] },
  { letter: 'H', cities: ['杭州', '合肥', '哈尔滨', '海口', '惠州', '湖州', '呼和浩特'] },
  { letter: 'J', cities: ['济南', '嘉兴', '金华', '江门', '吉林'] },
  { letter: 'K', cities: ['昆明', '开封'] },
  { letter: 'L', cities: ['兰州', '洛阳', '临沂', '柳州', '连云港'] },
  { letter: 'M', cities: ['绵阳', '马鞍山', '茂名'] },
  { letter: 'N', cities: ['南京', '宁波', '南昌', '南宁', '南通'] },
  { letter: 'Q', cities: ['青岛', '泉州', '秦皇岛', '衢州'] },
  { letter: 'S', cities: ['上海', '深圳', '苏州', '沈阳', '石家庄', '绍兴', '汕头', '三亚'] },
  { letter: 'T', cities: ['天津', '太原', '台州', '唐山', '泰州'] },
  { letter: 'W', cities: ['武汉', '无锡', '温州', '乌鲁木齐', '芜湖', '潍坊'] },
  { letter: 'X', cities: ['西安', '厦门', '徐州', '西宁', '襄阳', '新乡'] },
  { letter: 'Y', cities: ['烟台', '扬州', '宜昌', '银川', '盐城', '岳阳'] },
  { letter: 'Z', cities: ['郑州', '珠海', '中山', '镇江', '漳州', '淄博', '株洲'] }
])

// 过滤后的城市分组
const filteredCityGroups = computed(() => {
  if (!citySearchText.value) {
    return cityGroups.value
  }
  const search = citySearchText.value.toLowerCase()
  return cityGroups.value
    .map(group => ({
      letter: group.letter,
      cities: group.cities.filter(city => city.includes(search))
    }))
    .filter(group => group.cities.length > 0)
})

// 显示城市选择
const showCityPicker = () => {
  selectedCityTemp.value = currentCity.value
  showCityPopup.value = true
}

// 临时选择城市
const selectCityTemp = (city: string) => {
  selectedCityTemp.value = city
}

// 直接选择城市（点击当前定位）
const selectCity = (city: string) => {
  currentCity.value = city
  showCityPopup.value = false
}

// 确认城市选择
const confirmCitySelection = () => {
  if (selectedCityTemp.value) {
    currentCity.value = selectedCityTemp.value
  }
  showCityPopup.value = false
}

// 地图区域变化
const onRegionChange = (e: any) => {
  // 可以在这里更新可见的POI
}

// 跳转到行程
const goToTrip = () => {
  uni.navigateTo({
    url: '/pages/checklist/index'
  })
}

// 添加菜单弹窗
const showAddMenuPopup = ref(false)
const hasMessage = ref(true)

const displayAvatar = computed(() => {
  try {
    const cachedUser = uni.getStorageSync('userInfo')
    if (cachedUser && cachedUser.avatar) return cachedUser.avatar
  } catch (e) {}
  return ''
})

// 添加地点
const handleAddLocation = () => {
  showAddMenuPopup.value = false
  uni.showModal({
    title: '添加地点',
    content: '是否要在当前位置添加新地点？',
    success: (res) => {
      if (res.confirm) {
        uni.navigateTo({ url: '/pages/collect/edit' })
      }
    }
  })
}

// 记录行程
const handleRecordTrip = () => {
  showAddMenuPopup.value = false
  uni.navigateTo({ url: '/pages/record/index' })
}

// 拍照打卡
const handleTakePhoto = () => {
  showAddMenuPopup.value = false
  uni.chooseImage({
    count: 1,
    sourceType: ['camera'],
    success: () => {
      uni.navigateTo({ url: '/pages/collect/camera' })
    }
  })
}

// 跳转到主页
const goToHome = () => {
  uni.navigateBack({
    delta: 1,
    fail: () => {
      // 如果没有上一页，则直接跳转
      uni.redirectTo({
        url: '/pages/index/index'
      })
    }
  })
}

// 当前页面（附近）
const goToNearby = () => {
  // 已在当前页面
}

// 胶囊按钮信息
const statusBarHeight = ref(20)
const menuButtonSpace = ref(0)
const menuButtonLeft = ref(0)
const menuButtonTop = ref(0)
const menuButtonHeight = ref(32)
const windowWidth = ref(375)
const navBarHeight = ref(88)

// 计算顶部栏样式
const topBarStyle = computed(() => {
  return {
    paddingTop: statusBarHeight.value + 'px',
    height: navBarHeight.value + 'px'
  }
})

// 页面加载
onMounted(() => {
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
      menuButtonSpace.value = windowWidth.value - menuButton.left + 10
      // 计算导航栏高度
      navBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height + statusBarHeight.value
    }
  } catch (e) {
    console.log('获取胶囊按钮位置失败', e)
  }
  // #endif
  
  loadCategories()
  loadCityList()
  loadPois()
  
  // 尝试获取当前位置
  locateMe()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F7;
  position: relative;
}

// 顶部信息栏
.top-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32rpx;
  padding-bottom: 16rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-sizing: border-box;
}

.city-info {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.city-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.city-arrow {
  font-size: 20rpx;
  color: #86868B;
  margin-left: 4rpx;
}

.weather-info {
  margin-left: 12rpx;
}

.weather-text {
  font-size: 24rpx;
  color: #86868B;
}

.top-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.top-mp-right {
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

.red-dot {
  position: absolute;
  top: 0;
  right: 0;
  width: 16rpx;
  height: 16rpx;
  background: #FF2D55;
  border-radius: 50%;
  border: 2rpx solid #FFFFFF;
}

.icon-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F7;
  border-radius: 50%;
}

// 地图区域
.map-container {
  position: fixed;
  top: 88px;
  left: 0;
  right: 0;
  bottom: 160rpx;
}

.nearby-map {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #E8F5F3 0%, #D4EDE9 100%);
}

.map-controls {
  position: absolute;
  right: 32rpx;
  bottom: 180rpx; /* Above the filter bar */
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

// POI气泡
.poi-bubble {
  position: absolute;
  transform: translate(-50%, -100%);
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 12rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 12rpx;
  max-width: 280rpx;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -12rpx;
    left: 50%;
    transform: translateX(-50%);
    border-left: 12rpx solid transparent;
    border-right: 12rpx solid transparent;
    border-top: 12rpx solid #FFFFFF;
  }
}

.bubble-image {
  width: 60rpx;
  height: 60rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.bubble-content {
  flex: 1;
  min-width: 0;
}

.bubble-title {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #1D1D1F;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bubble-subtitle {
  display: block;
  font-size: 20rpx;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// 分类筛选
.filter-bar {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 50;
  background: #FFFFFF;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.filter-scroll {
  width: 100%;
  white-space: nowrap;
}

.filter-wrapper {
  display: flex;
  padding: 16rpx 24rpx;
  gap: 16rpx;
}

.filter-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  border-radius: 32rpx;
  background: #F5F5F7;
  transition: all 0.2s;
  flex-shrink: 0;
  
  &.active {
    background: rgba(0, 122, 255, 0.1);
    
    .filter-name {
      color: #007AFF;
      font-weight: 600;
    }
  }
}

.filter-emoji {
  font-size: 28rpx;
}

.filter-name {
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
}

// 底部TabBar
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 160rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding-bottom: env(safe-area-inset-bottom);
  border-top: 1rpx solid rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.tabbar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 48rpx;
  color: #86868B;

  &.active {
    color: #007AFF;
    
    .tabbar-text {
      color: #007AFF;
    }
  }
}

.tabbar-text {
  font-size: 22rpx;
  color: #86868B;
  font-weight: 500;
}

.tabbar-center {
  margin-top: -60rpx;
}

.add-btn {
  width: 112rpx;
  height: 112rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: 50%;
  box-shadow: 0 8rpx 32rpx rgba(255, 107, 107, 0.4);
}

// POI详情弹窗
.poi-popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.poi-popup {
  width: 100%;
  max-height: 80vh;
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  padding: 20rpx 32rpx;
  padding-bottom: 0;
  animation: slideUp 0.3s ease-out;
  display: flex;
  flex-direction: column;
}

.popup-scroll {
  flex: 1;
  max-height: calc(80vh - 160rpx);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.popup-handle {
  width: 80rpx;
  height: 8rpx;
  background: #E5E5E5;
  border-radius: 4rpx;
  margin: 0 auto 24rpx;
}

.popup-header {
  margin-bottom: 24rpx;
}

.popup-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.popup-title {
  flex: 1;
  font-size: 36rpx;
  font-weight: 700;
  color: #1D1D1F;
  line-height: 1.3;
}

.popup-close {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F7;
  border-radius: 50%;
}

.popup-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.meta-distance {
  font-size: 24rpx;
  color: #666;
  padding: 6rpx 16rpx;
  background: #F5F5F7;
  border-radius: 8rpx;
}

.meta-rating {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 6rpx 16rpx;
  background: #FFF8E7;
  border-radius: 8rpx;
}

.rating-score {
  font-size: 24rpx;
  color: #FF9500;
  font-weight: 600;
}

.meta-category {
  font-size: 24rpx;
  color: #666;
  padding: 6rpx 16rpx;
  background: #F5F5F7;
  border-radius: 8rpx;
}

// 图片画廊
.popup-gallery {
  margin: 0 -32rpx;
  padding: 0 32rpx;
  margin-bottom: 24rpx;
}

.gallery-list {
  display: flex;
  gap: 16rpx;
  padding-right: 32rpx;
}

.gallery-image {
  width: 480rpx;
  height: 360rpx;
  border-radius: 24rpx;
  flex-shrink: 0;
}

// 地点介绍
.popup-description {
  margin-bottom: 24rpx;
}

.desc-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 12rpx;
}

.desc-text {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}

// 地点信息
.popup-info {
  margin-bottom: 16rpx;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.info-text {
  flex: 1;
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

// 底部操作
.popup-actions {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #F0F0F0;
  background: #FFFFFF;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.action-text {
  font-size: 22rpx;
  color: #666;
}

// 城市选择弹窗
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

.city-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 85vh;
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }

  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 40rpx 40rpx 24rpx;
    margin-bottom: 0;
  }

  .popup-title {
    font-size: 36rpx;
    font-weight: 600;
    color: #1D1D1F;
  }

  .popup-close {
    width: 56rpx;
    height: 56rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #F5F5F7;
    border-radius: 50%;
  }

  .close-icon {
    font-size: 40rpx;
    color: #86868B;
    line-height: 1;
  }
}

// 弹窗搜索框
.popup-search {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin: 0 40rpx 24rpx;
  padding: 24rpx 32rpx;
  background: #F5F5F7;
  border-radius: 20rpx;
  border: 2rpx solid #E5E5EA;
}

.popup-search-icon {
  font-size: 32rpx;
  color: #86868B;
}

.popup-search-input {
  flex: 1;
  font-size: 30rpx;
  color: #1D1D1F;
  background: transparent;
}

// 当前定位
.current-location-section {
  padding: 0 40rpx 24rpx;
}

.section-label {
  font-size: 26rpx;
  color: #86868B;
  display: block;
  margin-bottom: 16rpx;
}

.location-city {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 24rpx;
  background: rgba(0, 122, 255, 0.1);
  border-radius: 16rpx;
  border: 2rpx solid #007AFF;
}

.location-city-text {
  font-size: 28rpx;
  color: #007AFF;
  font-weight: 500;
}

// 热门城市
.hot-cities-section {
  padding: 0 40rpx 24rpx;
}

.city-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.city-tag {
  padding: 20rpx 36rpx;
  background: #F5F5F7;
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

.city-tag-text {
  font-size: 28rpx;
  color: #1D1D1F;
}

.city-tag.selected .city-tag-text {
  color: #00C853;
  font-weight: 500;
}

// 城市滚动区域
.city-scroll {
  flex: 1;
  padding: 0 40rpx;
  overflow-y: auto;
}

.city-group {
  margin-bottom: 32rpx;
}

.city-group-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 16rpx;
  display: block;
}

.city-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.city-item {
  padding: 16rpx 28rpx;
  background: #F5F5F7;
  border-radius: 12rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: rgba(0, 200, 83, 0.1);
    border-color: #00C853;
  }
}

.city-item-text {
  font-size: 28rpx;
  color: #1D1D1F;
}

.city-item.selected .city-item-text {
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
  background: #FFFFFF;
  border-top: 1rpx solid #E5E5EA;
}

.footer-cancel {
  padding: 24rpx 40rpx;
  background: #F5F5F7;
  border-radius: 100rpx;
}

.cancel-text {
  font-size: 28rpx;
  color: #1D1D1F;
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

// 添加菜单弹窗
.add-menu-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  backdrop-filter: blur(0);
  -webkit-backdrop-filter: blur(0);
  z-index: 998;
  pointer-events: none;
  transition: all 0.4s ease;

  &.show {
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(30px);
    -webkit-backdrop-filter: blur(30px);
    pointer-events: auto;
  }
}

.add-menu-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  padding: 0 48rpx;
  padding-bottom: calc(60rpx + env(safe-area-inset-bottom));
  pointer-events: none;

  &.show {
    pointer-events: auto;
  }
}

.menu-options {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

// 菜单项依次飞入动画
.animate-item {
  opacity: 0;
  transform: translateY(80rpx) scale(0.9);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.show {
    opacity: 1;
    transform: translateY(0) scale(1);
  }

  &.item-1 {
    transition-delay: 0s;
  }
  &.item-2 {
    transition-delay: 0.05s;
  }
  &.item-3 {
    transition-delay: 0.1s;
  }
  &.item-4 {
    transition-delay: 0.15s;
  }
}

.menu-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 40rpx;
  border-radius: 28rpx;
  transition: transform 0.2s ease, opacity 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);

  &:active {
    transform: scale(0.98) !important;
  }

  &.dark {
    background: rgba(30, 30, 30, 0.95);
  }

  &.light {
    background: rgba(255, 255, 255, 0.95);
  }
}

.option-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.option-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1D1D1F;
}

.menu-option.dark .option-title {
  color: #FFFFFF;
}

.option-desc {
  font-size: 24rpx;
  color: #86868B;
  line-height: 1.5;
}

.option-icon-wrapper {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.light {
    width: 48rpx;
    height: 48rpx;
  }
}

.menu-close {
  width: 96rpx;
  height: 96rpx;
  margin: 48rpx auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(128, 128, 128, 0.6);
  border-radius: 50%;

  &:active {
    transform: scale(0.95) !important;
  }
}
</style>
