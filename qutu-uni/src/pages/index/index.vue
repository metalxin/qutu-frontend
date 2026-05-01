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
            <view class="red-dot" v-if="hasMessage"></view>
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
          <view class="red-dot" v-if="hasMessage"></view>
        </view>
      </view>
      <!-- #endif -->
    </view>

    <!-- 搜索框 -->
    <view class="search-container" :style="{ marginTop: headerHeight + 'px' }">
      <view class="search-box">
        <SFIcon name="search" :size="32" color="#86868B" />
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="按目的地探索"
          confirm-type="search"
          @confirm="applySearch"
        />
        <view class="search-clear" v-if="searchKeyword" @click="clearSearch">
          <SFIcon name="close" :size="24" color="#8E8E93" />
        </view>
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
          <text class="banner-desc">记录和收藏你走过的每一处风景</text>
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
        <view class="section-more" @click="toggleDestinations">
          <text class="more-text">{{ showAllDestinations ? '收起' : '查看全部' }} ›</text>
        </view>
      </view>
    </view>

    <!-- 目的地卡片 -->
    <scroll-view class="destination-scroll" scroll-y :show-scrollbar="false">
      <view class="destination-grid">
        <view 
          class="destination-card" 
          v-for="dest in destinations" 
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
            <view class="card-favorite" @click.stop="toggleFavorite(dest)">
              <SFIcon name="heart" :size="28" :color="dest.isFavorite ? '#FF2D55' : '#FFFFFF'" :filled="dest.isFavorite" />
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
      <view class="search-empty" v-if="searchKeyword && destinations.length === 0">
        <text class="empty-text">未找到相关目的地</text>
      </view>
      
      <!-- 底部显示更多按钮 -->
      <view class="load-more-wrapper" v-if="!showAllDestinations">
        <view class="load-more-btn" @click="toggleDestinations">
          <text class="load-more-text">查看更多目的地</text>
          <text class="load-more-arrow">↓</text>
        </view>
      </view>
      <view class="collapse-wrapper" v-else>
        <view class="collapse-btn" @click="toggleDestinations">
          <text class="collapse-text">收起</text>
          <text class="collapse-arrow">↑</text>
        </view>
      </view>
    </scroll-view>

    <!-- 灵感推荐 -->
    <view class="inspiration-section">
      <view class="section-header">
        <view class="section-title-row">
          <text class="section-title">灵感推荐</text>
          <view class="section-more" @click="goToStoryList">
            <text class="more-text">更多灵感 ›</text>
          </view>
        </view>
      </view>
      <scroll-view class="inspiration-scroll" scroll-x :show-scrollbar="false">
        <view class="inspiration-list">
          <view class="inspiration-card" v-for="item in inspirationList" :key="item.id" @tap="goToInspirationDetail(item)">
            <image class="inspiration-cover" :src="item.cover" mode="aspectFill" />
            <view class="inspiration-info">
              <text class="inspiration-title">{{ item.title }}</text>
              <text class="inspiration-location">{{ item.location }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 底部TabBar -->
    <view class="tabbar">
      <view class="tabbar-item active">
        <SFIcon name="home" :size="44" />
        <text class="tabbar-text">主页</text>
      </view>
      <view class="tabbar-center">
        <view class="add-btn" @click="showAddMenu = true">
          <SFIcon name="plus" :size="48" color="#FFFFFF" />
        </view>
      </view>
      <view class="tabbar-item" @click="goToNearby">
        <SFIcon name="location" :size="44" />
        <text class="tabbar-text">附近</text>
      </view>
    </view>

    <!-- 添加菜单弹窗 -->
    <view class="add-menu-mask" :class="{ show: showAddMenu }" @click="showAddMenu = false"></view>
    <view class="add-menu-popup" :class="{ show: showAddMenu }">
      <!-- 左上角使用口令按钮 -->
      <view class="code-btn" :class="{ show: showAddMenu }" @click="showCodePopup = true; showAddMenu = false">
        <SFIcon name="key" :size="28" color="#007AFF" />
        <text class="code-btn-text">使用口令</text>
      </view>

      <!-- 菜单选项 -->
      <view class="menu-options">
        <view class="menu-option dark animate-item item-1" :class="{ show: showAddMenu }" @click="handleCreateTrip">
          <text class="option-title">创建新的行程</text>
          <view class="option-icon-wrapper">
            <SFIcon name="plus" :size="40" color="#FFFFFF" />
          </view>
        </view>

        <view class="menu-option light animate-item item-2" :class="{ show: showAddMenu }" @click="showImportPopup = true; showAddMenu = false">
          <view class="option-content">
            <text class="option-title">智能导入地点/行程</text>
            <text class="option-desc">粘贴笔记链接、行程文本，或上传图片进行识别</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="import" :size="40" color="#34C759" />
          </view>
        </view>

        <view class="menu-option light animate-item item-3" :class="{ show: showAddMenu }" @click="showRecordPopup = true; showAddMenu = false">
          <view class="option-content">
            <text class="option-title">记录行程路线</text>
            <text class="option-desc">选择运动方式开始记录路线</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="route" :size="40" color="#FF9500" />
          </view>
        </view>

        <view class="menu-option light animate-item item-4" :class="{ show: showAddMenu }" @click="handleCollect">
          <view class="option-content">
            <text class="option-title">「采集」识别</text>
            <text class="option-desc">识别同时收藏你的生活</text>
          </view>
          <view class="option-icon-wrapper light">
            <SFIcon name="camera" :size="40" color="#AF52DE" />
          </view>
        </view>
      </view>

      <!-- 关闭按钮 -->
      <view class="menu-close animate-item item-5" :class="{ show: showAddMenu }" @click="showAddMenu = false">
        <SFIcon name="close" :size="40" color="#FFFFFF" />
      </view>
    </view>

    <!-- 记录行程路线弹窗 -->
    <view class="popup-mask" v-if="showRecordPopup" @click="showRecordPopup = false"></view>
    <view class="record-popup" :class="{ 'popup-show': showRecordPopup }">
      <view class="record-popup-header">
        <view class="record-header-left">
          <text class="record-icon">📍</text>
          <text class="record-title">记录</text>
        </view>
        <view class="record-header-right" @click="showActivityPicker = !showActivityPicker">
          <text class="activity-icon">🚴</text>
          <text class="activity-name">{{ selectedActivity }}</text>
          <text class="activity-arrow">▼</text>
        </view>
      </view>
      <text class="record-subtitle">选择一种运动开始记录路线</text>

      <!-- 活动类型选择器 -->
      <view class="activity-picker" :class="{ show: showActivityPicker }">
        <view 
          class="activity-option" 
          v-for="(activity, index) in activityTypes" 
          :key="activity.name"
          :class="{ show: showActivityPicker, active: selectedActivity === activity.name }"
          :style="{ transitionDelay: showActivityPicker ? (index * 0.05) + 's' : '0s' }"
          @click="selectActivity(activity)"
        >
          <text class="activity-option-icon">{{ activity.icon }}</text>
          <text class="activity-option-name">{{ activity.name }}</text>
        </view>
      </view>

      <!-- 开始记录按钮 -->
      <view class="record-btn-wrapper">
        <view 
          class="record-start-btn"
          @longpress="startRecording"
          @touchstart="onRecordBtnTouchStart"
          @touchend="onRecordBtnTouchEnd"
        >
          <text class="record-btn-icon">🚴</text>
          <text class="record-btn-text">开始记录</text>
        </view>
        <text class="record-btn-hint">长按开始</text>
      </view>
    </view>

    <!-- 使用口令弹窗 -->
    <view class="popup-mask" v-if="showCodePopup" @click="showCodePopup = false"></view>
    <view class="code-popup" :class="{ 'popup-show': showCodePopup }">
      <!-- 取消按钮 -->
      <view class="code-cancel" @click="showCodePopup = false">
        <text class="cancel-text">取消</text>
      </view>

      <!-- 票据卡片 -->
      <view class="ticket-card">
        <!-- 顶部撕裂边缘 -->
        <view class="ticket-tear top"></view>
        
        <!-- 复制行程区域 -->
        <view class="ticket-header">
          <view class="arrow-line">
            <view class="arrow-body"></view>
            <view class="arrow-head"></view>
          </view>
          <text class="header-text">复制行程</text>
        </view>

        <!-- 主体内容 -->
        <view class="ticket-body">
          <view class="ticket-content">
            <view class="content-left">
              <text class="itinerary-label">ITINERARY</text>
              <text class="itinerary-title">输入行程口令</text>
            </view>
            <view class="content-right">
              <text class="date-day">{{ currentDay }}</text>
              <view class="date-divider"></view>
              <text class="date-month">{{ currentMonth }}月</text>
            </view>
          </view>

          <!-- 口令输入框 -->
          <view class="code-inputs">
            <view class="code-input-box" v-for="(digit, index) in codeDigits" :key="index">
              <input 
                class="code-input"
                type="text"
                :maxlength="1"
                v-model="codeDigits[index]"
                @input="onCodeInput(index)"
                @keydown="onCodeKeydown($event, index)"
                :ref="el => codeInputRefs[index] = el"
              />
            </view>
          </view>

          <!-- 虚线分割 -->
          <view class="ticket-dashed"></view>
        </view>

        <!-- 底部条码 -->
        <view class="ticket-barcode">
          <view class="barcode-lines">
            <view class="barcode-line" v-for="i in 40" :key="i" :style="{ width: Math.random() * 4 + 2 + 'rpx' }"></view>
          </view>
        </view>

        <!-- 底部撕裂边缘 -->
        <view class="ticket-tear bottom"></view>
      </view>
    </view>

    <!-- 智能导入弹窗 -->
    <view class="popup-mask" v-if="showImportPopup" @click="showImportPopup = false"></view>
    <view class="import-popup" :class="{ 'popup-show': showImportPopup }">
      <!-- 拖拽条 -->
      <view class="drag-bar"></view>

      <!-- 文本或链接识别 -->
      <view class="import-section main">
        <view class="section-header">
          <text class="section-icon">🔄</text>
          <text class="section-title">文本或链接识别</text>
        </view>
        <view class="input-area">
          <textarea 
            class="import-input"
            v-model="importText"
            placeholder="粘贴站外攻略链接到这里，即可获取相关行程。现已支持小红书图文/视频笔记、微信公众号、携程酒店景点、大众点评店铺、飞书文档（需公开文档权限）等。"
          />
          <view class="start-btn" :class="{ active: importText }" @click="startRecognize">
            <text class="start-btn-text">开始识别</text>
          </view>
        </view>
      </view>

      <!-- 截图识别 -->
      <view class="import-section" @click="handleScreenshot">
        <view class="section-header">
          <text class="section-icon">📷</text>
          <text class="section-title">截图识别</text>
        </view>
        <text class="section-desc">选择含有地点信息的页面截图</text>
      </view>
    </view>

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

    <!-- 用户侧边栏遮罩 -->
    <view class="sidebar-mask" :class="{ show: showUserSidebar }" @click="showUserSidebar = false"></view>
    
    <!-- 用户侧边栏 -->
    <view class="user-sidebar" :class="{ show: showUserSidebar }">
      <!-- 顶部用户信息 - 与胶囊同行 -->
      <view class="sidebar-top" :style="sidebarTopStyle">
        <view class="sidebar-header">
          <view class="sidebar-avatar">
            <view class="avatar-circle">
            <image v-if="userInfo.avatar" class="avatar-image" :src="userInfo.avatar" mode="aspectFill" />
            <SFIcon v-else name="user" :size="40" color="#86868B" />
            </view>
          </view>
        <view class="sidebar-user-info" @click="goToProfile">
          <text class="sidebar-username">{{ isLoggedIn ? (userInfo.nickname || '旅行者') : '未登录' }}</text>
          <view class="sidebar-bindwx" v-if="!isLoggedIn" @click.stop="goToLogin">
              <text class="bindwx-text">账号登录</text>
            </view>
          <view class="sidebar-bindwx" v-else-if="!userInfo.wechatBound" @click.stop="bindWechat">
              <text class="bindwx-text">微信登录</text>
            </view>
          <view class="sidebar-bound" v-else>
            <text class="bound-text">已绑定微信</text>
          </view>
          </view>
          <view class="sidebar-actions">
            <view class="action-btn" @click="goToSettings">
              <SFIcon name="settings" :size="36" color="#86868B" />
            </view>

          </view>
        </view>
      </view>

      <!-- 会员卡片 -->
      <view class="sidebar-vip-card" @click="goToVipCenter">
        <view class="vip-left">
          <view class="vip-icon">👑</view>
          <view class="vip-info">
            <text class="vip-title">会员中心</text>
            <text class="vip-desc">开通会员，尊享会员权益</text>
          </view>
        </view>
        <view class="vip-action">
          <text class="vip-action-text">去看看</text>
          <SFIcon name="chevron-right" :size="28" color="#5D4E37" />
        </view>
      </view>

      <!-- 菜单列表 -->
      <scroll-view class="sidebar-menu" scroll-y :show-scrollbar="false">
        <view class="menu-group">
          <view class="menu-item" @click="handleMenuClick('frequent')">
            <view class="menu-icon" style="background: #FFF3E0;">
              <SFIcon name="location" :size="36" color="#FF9500" />
            </view>
            <text class="menu-text">常在地点</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
          <view class="menu-item" @click="handleMenuClick('favorites')">
            <view class="menu-icon" style="background: #FFF8E1;">
              <SFIcon name="star" :size="36" color="#FFB800" />
            </view>
            <text class="menu-text">收藏地点管理</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
        </view>
        
        <view class="menu-group">
          <view class="menu-item" @click="handleMenuClick('backup')">
            <view class="menu-icon" style="background: #E3F2FD;">
              <SFIcon name="import" :size="36" color="#007AFF" />
            </view>
            <text class="menu-text">数据备份</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
          <view class="menu-item" @click="handleMenuClick('messages')">
            <view class="menu-icon" style="background: #FCE4EC;">
              <SFIcon name="bell" :size="36" color="#FF2D55" />
            </view>
            <text class="menu-text">消息中心</text>
            <SFIcon name="chevron-right" :size="28" color="#C7C7CC" />
          </view>
        </view>
        <view class="menu-group" v-if="isLoggedIn">
          <view class="menu-item" @click="doLogout">
            <view class="menu-icon" style="background: #FDECEA;">
              <SFIcon name="power" :size="36" color="#FF3B30" />
            </view>
            <text class="menu-text" style="color:#FF3B30;">退出登录</text>
            <SFIcon name="chevron-right" :size="28" color="#F5B1AD" />
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import SFIcon from '@/components/SFIcon/SFIcon.vue'
import { getDestinations, getRegionGroups, getHotCities, getDestinationSpots } from '@/api'
import { getUserInfo, bindWechat as bindWechatApi, logout as logoutApi } from '@/api/modules/user'
import type { Destination, SpotListItem } from '@/api/modules/destination'
import type { UserInfo } from '@/api/modules/user'

// 加载状态
const loading = ref(false)
const logoSrc = ref('/static/logo.png')

// 功能入口数据
const features = ref([
  { id: 1, name: '时光采集', iconName: 'collection', iconColor: '#007AFF', bgColor: '#E3F2FD', path: '/pages/collect/camera' },
  { id: 2, name: '精选攻略', iconName: 'map', iconColor: '#34C759', bgColor: '#E8F5E9', path: '/pages/guide/list' },
  { id: 3, name: '智能规划', iconName: 'route', iconColor: '#34C759', bgColor: '#E8F5E9', path: '/pages/planning/index' },
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

// 智能导入弹窗
const showImportPopup = ref(false)
const importText = ref('')

// 记录行程路线弹窗
const showRecordPopup = ref(false)
const showActivityPicker = ref(false)
const selectedActivity = ref('山地自行车')
const activityTypes = ref([
  { name: '山地自行车', icon: '🚴' },
  { name: '公路自行车', icon: '🚲' },
  { name: '跑步', icon: '🏃' },
  { name: '徒步', icon: '🥾' },
  { name: '驾车', icon: '🚗' },
  { name: '摩托车', icon: '🏍️' }
])

const selectActivity = (activity: any) => {
  selectedActivity.value = activity.name
  showActivityPicker.value = false
}

const onRecordBtnTouchStart = () => {
  // 按下效果
}

const onRecordBtnTouchEnd = () => {
  // 松开效果
}

const startRecording = () => {
  showRecordPopup.value = false
  uni.navigateTo({
    url: '/pages/record/index'
  })
}

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

// 侧边栏顶部样式 - 与胶囊同行
const sidebarTopStyle = computed(() => {
  // #ifdef MP-WEIXIN
  // 计算胶囊按钮右侧到屏幕边缘的距离
  const capsuleRight = windowWidth.value - menuButtonLeft.value - 87 // 87是胶囊按钮的宽度
  return {
    paddingTop: menuButtonTop.value + 'px',
    minHeight: (menuButtonHeight.value + 16) + 'px',
    paddingRight: (capsuleRight + 97) + 'px' // 留出胶囊按钮宽度+间距
  }
  // #endif
  // #ifndef MP-WEIXIN
  return {
    paddingTop: (statusBarHeight.value + 10) + 'px',
    minHeight: '56px'
  }
  // #endif
})

// 显示通知
const showNotification = () => {
  uni.navigateTo({
    url: '/pages/message/index'
  })
}

// 使用口令弹窗
const showCodePopup = ref(false)
const codeDigits = ref(['', '', '', ''])
const codeInputRefs = ref<any[]>([])
const currentDay = ref('31')
const currentMonth = ref('1')
const hasMessage = ref(true) // 模拟有消息

const displayAvatar = computed(() => {
  if (userInfo.value.avatar) return userInfo.value.avatar
  // 尝试从缓存获取
  try {
    const cachedUser = uni.getStorageSync('userInfo')
    if (cachedUser && cachedUser.avatar) return cachedUser.avatar
  } catch (e) {}
  return '' // 或者返回默认头像路径
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
  
  // 初始化日期
  const now = new Date()
  currentDay.value = String(now.getDate())
  currentMonth.value = String(now.getMonth() + 1)

  // 加载数据
  await Promise.all([
    loadDestinations(),
    loadRegionGroups(),
    loadUserInfo()
  ])
})

onShow(() => {
  loadUserInfo()
})

// 口令输入处理
const onCodeInput = (index: number) => {
  if (codeDigits.value[index] && index < 3) {
    // 自动跳转到下一个输入框
    const nextInput = codeInputRefs.value[index + 1]
    if (nextInput) {
      nextInput.focus()
    }
  }
  // 检查是否输入完成
  if (codeDigits.value.every(d => d)) {
    submitCode()
  }
}

const onCodeKeydown = (e: any, index: number) => {
  if (e.key === 'Backspace' && !codeDigits.value[index] && index > 0) {
    // 删除时跳转到上一个输入框
    const prevInput = codeInputRefs.value[index - 1]
    if (prevInput) {
      prevInput.focus()
    }
  }
}

const submitCode = () => {
  const code = codeDigits.value.join('')
  uni.showLoading({ title: '验证中...' })
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({
      title: '口令验证功能开发中',
      icon: 'none'
    })
  }, 1000)
}

// 创建新行程
const handleCreateTrip = () => {
  showAddMenu.value = false
  uni.navigateTo({
    url: '/pages/planning/index'
  })
}

// 采集识别
const handleCollect = () => {
  showAddMenu.value = false
  uni.navigateTo({
    url: '/pages/collect/camera'
  })
}

// 开始识别
const startRecognize = () => {
  if (!importText.value) {
    uni.showToast({
      title: '请输入链接或文本',
      icon: 'none'
    })
    return
  }
  uni.showToast({
    title: '正在识别...',
    icon: 'loading'
  })
}

// 截图识别
const handleScreenshot = () => {
  uni.chooseImage({
    count: 1,
    sourceType: ['album'],
    success: () => {
      uni.showToast({
        title: '正在识别图片...',
        icon: 'loading'
      })
    }
  })
}

// 目的地数据
const showAllDestinations = ref(false)
const allDestinations = ref<Destination[]>([])
const searchKeyword = ref('')
const showSpotListPopup = ref(false)
const currentDestination = ref<Destination | null>(null)
const spotList = ref<SpotListItem[]>([])
const spotListLoading = ref(false)

const currentDestinationName = computed(() => currentDestination.value?.name || '景点列表')

const filteredDestinations = computed(() => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    return allDestinations.value
  }
  return allDestinations.value.filter(dest => {
    const name = dest.name || ''
    const subtitle = dest.subtitle || ''
    const tag = dest.tag || ''
    return name.includes(keyword) || subtitle.includes(keyword) || tag.includes(keyword)
  })
})

// 显示的目的地列表
const destinations = computed(() => {
  if (searchKeyword.value.trim()) {
    return filteredDestinations.value
  }
  if (showAllDestinations.value) {
    return filteredDestinations.value
  }
  return filteredDestinations.value.slice(0, 4)
})

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
const toggleDestinations = () => {
  showAllDestinations.value = !showAllDestinations.value
}

const applySearch = () => {
  if (searchKeyword.value.trim()) {
    showAllDestinations.value = true
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  showAllDestinations.value = false
}

// 图片加载失败处理
const handleImageError = (dest: any) => {
  dest.image = ''
}

// 跳转到点亮足迹页面
const goToFootprint = () => {
  uni.navigateTo({
    url: '/pages/footprint/index'
  })
}

// 跳转到附近页面
const goToNearby = () => {
  uni.navigateTo({
    url: '/pages/nearby/index'
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
    url: `/pages/destination/detail?id=${spot.id}&name=${encodeURIComponent(spot.name)}`,
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
  openSpotList(dest)
}

// 切换收藏状态
const toggleFavorite = (dest: any) => {
  if (!isLoggedIn.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/user/login' })
    }, 1000)
    return
  }
  dest.isFavorite = !dest.isFavorite
  uni.showToast({
    title: dest.isFavorite ? '已收藏' : '已取消收藏',
    icon: 'none'
  })
}

// 灵感推荐数据
const inspirationList = ref([
  {
    id: 1,
    title: '杭州西湖一日游攻略',
    location: '杭州 · 西湖',
    cover: 'https://images.unsplash.com/photo-1528164344705-47542687000d?w=400&q=80'
  },
  {
    id: 2,
    title: '黄山日出观景指南',
    location: '安徽 · 黄山',
    cover: 'https://images.unsplash.com/photo-1599579182416-c3a0b5c2c55f?w=400&q=80'
  },
  {
    id: 3,
    title: '成都美食探店之旅',
    location: '四川 · 成都',
    cover: 'https://images.unsplash.com/photo-1569163139394-de4798aa62b6?w=400&q=80'
  },
  {
    id: 4,
    title: '厦门鼓浪屿漫步',
    location: '福建 · 厦门',
    cover: 'https://images.unsplash.com/photo-1580655453156-6e2f136b0225?w=400&q=80'
  }
])

// 跳转到故事列表
const goToStoryList = () => {
  uni.navigateTo({
    url: '/pages/story/index'
  })
}

// 跳转到灵感详情
const goToInspirationDetail = (item: any) => {
  uni.navigateTo({
    url: `/pages/story/detail?id=${item.id}`
  })
}

// 目的地弹窗相关
const showDestinationPopup = ref(false)
const currentLevel = ref(2) // 0: 全球, 1: 亚洲, 2: 中国大陆
const breadcrumb = ref(['亚洲', '中国大陆'])
const currentRegionName = ref('中国大陆')
const selectedProvince = ref('')
const selectedRegion = ref('')

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
  showAllDestinations.value = false
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

// 绑定微信
const bindWechat = async () => {
  if (userInfo.value.wechatBound) {
    uni.showToast({ title: '已绑定微信', icon: 'none' })
    return
  }
  // #ifdef MP-WEIXIN
  try {
    const { wechatMpLogin } = await import('@/api/modules/user')
    await wechatMpLogin()
    userInfo.value.wechatBound = true
    uni.showToast({ title: '绑定成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '绑定失败', icon: 'none' })
  }
  // #endif
  // #ifndef MP-WEIXIN
  uni.navigateTo({ url: '/pages/user/login' })
  // #endif
}

// 跳转设置
const goToSettings = () => {
  showUserSidebar.value = false
  uni.navigateTo({
    url: '/pages/user/settings'
  })
}

// 跳转会员中心
const goToVipCenter = () => {
  showUserSidebar.value = false
  uni.navigateTo({
    url: '/pages/user/vip'
  })
}

// 处理菜单点击
const handleMenuClick = (menu: string) => {
  showUserSidebar.value = false
  const menuRoutes: Record<string, string> = {
    'backup': '/pages/user/backup',
    'frequent': '/pages/user/frequent',
    'favorites': '/pages/user/favorites',
    'messages': '/pages/message/index'
  }
  const target = menuRoutes[menu]
  if (target) {
    uni.navigateTo({ url: target })
    return
  }
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

const goToProfile = () => {
  showUserSidebar.value = false
  if (!isLoggedIn.value) {
    uni.navigateTo({ url: '/pages/user/login' })
    return
  }
  uni.navigateTo({
    url: '/pages/user/profile'
  })
}

const goToLogin = () => {
  showUserSidebar.value = false
  uni.navigateTo({ url: '/pages/user/login' })
}

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

const doLogout = async () => {
  try {
    await logoutApi()
  } catch (e) {}
  try {
    uni.removeStorageSync('token')
    uni.removeStorageSync('refresh_token')
    uni.removeStorageSync('userInfo')
  } catch (e) {}
  token.value = ''
  userInfo.value = {
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
  }
  showUserSidebar.value = false
  uni.showToast({ title: '已退出登录', icon: 'success' })
}
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

.banner-title {
  font-size: 38rpx;
  font-weight: 700;
  color: #FFFFFF;
  letter-spacing: 4rpx;
}

.banner-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
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
  height: auto;
  max-height: calc(100vh - 680rpx);
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

.card-favorite {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 50%;
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
  padding: 0 32rpx 28rpx;
}

.inspiration-scroll {
  white-space: nowrap;
  margin-top: 20rpx;
}

.inspiration-list {
  display: inline-flex;
  gap: 20rpx;
  padding-right: 32rpx;
}

.inspiration-card {
  width: 260rpx;
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
  height: 300rpx;
  display: block;
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

// 底部TabBar
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 110rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding-bottom: env(safe-area-inset-bottom);
  border-top: 1rpx solid rgba(0, 0, 0, 0.05);
}

.tabbar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 12rpx 48rpx;

  &.active {
    .tabbar-icon,
    .tabbar-text {
      color: $primary-color;
    }
  }
}

.tabbar-icon {
  font-size: 40rpx;
  color: $text-secondary;
}

.tabbar-text {
  font-size: 22rpx;
  color: $text-secondary;
  font-weight: 500;
}

.tabbar-center {
  margin-top: -44rpx;
}

.add-btn {
  width: 96rpx;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: 50%;
  box-shadow: 0 8rpx 32rpx rgba(255, 107, 107, 0.4);
}

.add-icon {
  font-size: 56rpx;
  color: #FFFFFF;
  font-weight: 300;
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
  z-index: 999;
  display: flex;
  flex-direction: column;
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
  padding: 0 40rpx 40rpx;
  box-sizing: border-box;
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

.code-btn {
  position: absolute;
  top: -120rpx;
  left: 48rpx;
  padding: 20rpx 32rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 100rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
  opacity: 0;
  transform: translateY(60rpx);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  transition-delay: 0.25s;

  &.show {
    opacity: 1;
    transform: translateY(0);
  }
}

.code-btn-text {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
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
  &.item-5 {
    transition-delay: 0.2s;
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
  color: $text-primary;
}

.menu-option.dark .option-title {
  color: #FFFFFF;
}

.option-desc {
  font-size: 24rpx;
  color: $text-secondary;
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

.option-icon {
  font-size: 36rpx;
  color: #FFFFFF;
}

.menu-option.light .option-icon {
  color: $text-primary;
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

  .close-icon {
    font-size: 48rpx;
    color: #FFFFFF;
  }
}

// 智能导入弹窗
.import-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $card-bg;
  border-radius: 32rpx 32rpx 0 0;
  z-index: 999;
  padding: 20rpx 32rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);

  &.popup-show {
    transform: translateY(0);
  }
}

.drag-bar {
  width: 80rpx;
  height: 8rpx;
  background: #E5E5EA;
  border-radius: 4rpx;
  margin: 0 auto 32rpx;
}

.import-section {
  background: $bg-color;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;

  &.main {
    border: 2rpx solid $text-primary;
    background: $card-bg;
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.section-icon {
  font-size: 32rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.section-desc {
  font-size: 26rpx;
  color: $text-secondary;
}

.input-area {
  position: relative;
}

.import-input {
  width: 100%;
  height: 200rpx;
  font-size: 26rpx;
  color: $text-primary;
  line-height: 1.6;
  background: transparent;
}

.start-btn {
  position: absolute;
  right: 0;
  bottom: 0;
  padding: 16rpx 28rpx;
  background: #E5E5EA;
  border-radius: 12rpx;
  transition: all 0.2s ease;

  &.active {
    background: $primary-color;
  }
}

.start-btn-text {
  font-size: 26rpx;
  color: $text-secondary;
}

.start-btn.active .start-btn-text {
  color: #FFFFFF;
}

// 使用口令弹窗
.code-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(80, 80, 80, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s ease;

  &.popup-show {
    opacity: 1;
    pointer-events: auto;
  }
}

.code-cancel {
  position: absolute;
  top: calc(var(--status-bar-height, 44px) + 20rpx);
  left: 32rpx;
  padding: 16rpx 24rpx;
}

.cancel-text {
  font-size: 32rpx;
  color: #FFFFFF;
}

// 票据卡片
.ticket-card {
  width: 580rpx;
  background: #FFFFFF;
  position: relative;
  transform: scale(0.9) translateY(40rpx);
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.code-popup.popup-show .ticket-card {
  transform: scale(1) translateY(0);
}

// 撕裂边缘
.ticket-tear {
  width: 100%;
  height: 24rpx;
  background: #FFFFFF;
  position: relative;

  &.top {
    background: linear-gradient(135deg, transparent 8rpx, #FFFFFF 8rpx),
                linear-gradient(-135deg, transparent 8rpx, #FFFFFF 8rpx);
    background-size: 24rpx 24rpx;
    background-position: left top;
  }

  &.bottom {
    background: linear-gradient(45deg, transparent 8rpx, #FFFFFF 8rpx),
                linear-gradient(-45deg, transparent 8rpx, #FFFFFF 8rpx);
    background-size: 24rpx 24rpx;
    background-position: left bottom;
  }
}

.ticket-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 24rpx 40rpx;
  gap: 16rpx;
  border-bottom: 2rpx solid #E5E5EA;
}

.arrow-line {
  display: flex;
  align-items: center;
  flex: 1;
}

.arrow-body {
  flex: 1;
  height: 3rpx;
  background: $text-primary;
}

.arrow-head {
  width: 0;
  height: 0;
  border-left: 16rpx solid $text-primary;
  border-top: 8rpx solid transparent;
  border-bottom: 8rpx solid transparent;
}

.header-text {
  font-size: 28rpx;
  color: $text-primary;
}

.ticket-body {
  padding: 32rpx 40rpx;
}

.ticket-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 40rpx;
  border-bottom: 2rpx solid #E5E5EA;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.itinerary-label {
  font-size: 24rpx;
  color: $text-secondary;
  font-style: italic;
  letter-spacing: 4rpx;
}

.itinerary-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $text-primary;
}

.content-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 24rpx;
  border: 2rpx solid $text-primary;
  border-radius: 8rpx;
}

.date-day {
  font-size: 48rpx;
  font-weight: 700;
  color: $text-primary;
  line-height: 1;
}

.date-divider {
  width: 2rpx;
  height: 48rpx;
  background: $text-primary;
  transform: rotate(20deg);
}

.date-month {
  font-size: 28rpx;
  color: $text-primary;
}

// 口令输入框
.code-inputs {
  display: flex;
  justify-content: center;
  gap: 24rpx;
  padding: 48rpx 0;
}

.code-input-box {
  width: 100rpx;
  height: 120rpx;
  border: 3rpx solid $text-primary;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-input {
  width: 100%;
  height: 100%;
  text-align: center;
  font-size: 48rpx;
  font-weight: 600;
  color: $text-primary;
}

.ticket-dashed {
  border-bottom: 3rpx dashed #CCCCCC;
  margin-bottom: 24rpx;
}

// 条码
.ticket-barcode {
  padding: 16rpx 40rpx 24rpx;
}

.barcode-lines {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 3rpx;
  height: 60rpx;
}

.barcode-line {
  height: 100%;
  background: $text-primary;
}

// 记录行程路线弹窗
.record-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  z-index: 999;
  padding: 32rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  box-shadow: 0 -12rpx 40rpx rgba(0, 0, 0, 0.12);
  border-top: 1rpx solid #EFEFF4;

  &.popup-show {
    transform: translateY(0);
  }
}

.record-popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.record-header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.record-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  background: #F2F2F7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
}

.record-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
}

.record-header-right {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 18rpx;
  background: #F2F2F7;
  border: 1rpx solid #E5E5EA;
  border-radius: 100rpx;
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.06);
}

.activity-icon {
  font-size: 26rpx;
}

.activity-name {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 600;
}

.activity-arrow {
  font-size: 20rpx;
  color: #AEAEB2;
}

.record-subtitle {
  font-size: 26rpx;
  color: #8E8E93;
  display: block;
  margin-bottom: 48rpx;
}

// 活动类型选择器
.activity-picker {
  position: absolute;
  top: 100rpx;
  right: 32rpx;
  background: #FFFFFF;
  border-radius: 18rpx;
  border: 1rpx solid #E5E5EA;
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 10;
  opacity: 0;
  transform: scale(0.8) translateY(-20rpx);
  transform-origin: top right;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.show {
    opacity: 1;
    transform: scale(1) translateY(0);
    pointer-events: auto;
  }
}

.activity-option {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #F2F2F7;
  opacity: 0;
  transform: translateX(20rpx);
  transition: all 0.25s ease-out;

  &.show {
    opacity: 1;
    transform: translateX(0);
  }

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F2F2F7;
  }

  &.active {
    background: #EAF2FF;
  }
}

.activity-option-icon {
  font-size: 30rpx;
}

.activity-option-name {
  font-size: 28rpx;
  color: $text-primary;
}

// 开始记录按钮
.record-btn-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.record-start-btn {
  width: 260rpx;
  height: 260rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(0, 122, 255, 0.2);
  background: radial-gradient(circle at 30% 30%, #FFFFFF 0%, #F0F6FF 45%, #E3EEFF 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  transition: all 0.2s ease;
  box-shadow: 0 18rpx 40rpx rgba(0, 122, 255, 0.18);

  &:active {
    transform: scale(0.95);
    background: radial-gradient(circle at 30% 30%, #FFFFFF 0%, #E8F0FF 45%, #D6E7FF 100%);
  }
}

.record-btn-icon {
  font-size: 44rpx;
}

.record-btn-text {
  font-size: 30rpx;
  color: #007AFF;
  font-weight: 600;
}

.record-btn-hint {
  font-size: 24rpx;
  color: #8E8E93;
}

// 用户侧边栏遮罩
.sidebar-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  z-index: 1000;
  pointer-events: none;
  transition: background 0.35s ease;

  &.show {
    background: rgba(0, 0, 0, 0.3);
    pointer-events: auto;
  }
}

// 用户侧边栏
.user-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 72%;
  background: $bg-color;
  z-index: 1001;
  transform: translateX(100%);
  transition: transform 0.35s cubic-bezier(0.32, 0.72, 0, 1);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;

  &.show {
    transform: translateX(0);
  }
}

// 侧边栏顶部区域
.sidebar-top {
  background: $card-bg;
  display: flex;
  align-items: flex-end;
  box-sizing: border-box;
  flex-shrink: 0;
}

// 侧边栏头部
.sidebar-header {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  padding-bottom: 20rpx;
  width: 100%;
  box-sizing: border-box;
  /* #ifdef MP-WEIXIN */
  padding-right: 16rpx; /* 微信小程序右侧padding由sidebarTopStyle控制 */
  /* #endif */
}

.sidebar-avatar {
  margin-right: 16rpx;
}

.avatar-circle {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: $bg-color;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #E5E5EA;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.sidebar-user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  min-width: 0;
}

.sidebar-username {
  font-size: 26rpx;
  color: $text-primary;
  font-weight: 600;
}

.sidebar-bindwx {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 14rpx;
  background: #07C160;
  border-radius: 16rpx;
  align-self: flex-start;
}

.sidebar-bound {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 14rpx;
  background: #E8F5E9;
  border-radius: 16rpx;
  align-self: flex-start;
}

.bindwx-text {
  font-size: 18rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.bound-text {
  font-size: 18rpx;
  color: #34C759;
  font-weight: 500;
}

.sidebar-actions {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.action-btn {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  
  &.close-btn {
    background: $bg-color;
  }
}

// 会员卡片
.sidebar-vip-card {
  margin: 24rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, #FFE5B4 0%, #FFD89B 100%);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4rpx 16rpx rgba(255, 216, 155, 0.4);
}

.vip-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.vip-icon {
  font-size: 40rpx;
}

.vip-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.vip-title {
  font-size: 28rpx;
  color: #5D4E37;
  font-weight: 600;
}

.vip-desc {
  font-size: 20rpx;
  color: #8B7355;
}

.vip-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 12rpx 20rpx;
  background: rgba(255, 255, 255, 0.85);
  border-radius: 24rpx;
}

.vip-action-text {
  font-size: 22rpx;
  color: #5D4E37;
  font-weight: 500;
}

// 菜单列表
.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  padding-bottom: env(safe-area-inset-bottom);
}

.menu-group {
  margin: 16rpx 24rpx;
  margin-right: 32rpx;
  background: $card-bg;
  border-radius: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 20rpx;
  padding-right: 32rpx;
  background: $card-bg;
  border-bottom: 1rpx solid #F0F0F0;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: $bg-color;
  }
}

.menu-icon {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
}

.menu-text {
  flex: 1;
  font-size: 26rpx;
  color: $text-primary;
  font-weight: 400;
}
</style>
