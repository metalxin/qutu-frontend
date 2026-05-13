<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { notificationWs } from "./api/modules/notification-ws";

const LOGIN_PAGE = '/pages/user/login'
const PROTECTED_ROUTES = new Set([
  '/pages/checklist/index',
  '/pages/checklist/detail',
  '/pages/planning/index',
  '/pages/planning/detail',
  '/pages/route/list',
  '/pages/route/detail',
  '/pages/record/index',
  '/pages/message/index',
  '/pages/footprint/index',
  '/pages/footprint/records',
  '/pages/footprint/map',
  '/pages/collect/index',
  '/pages/collect/camera',
  '/pages/collect/settings',
  '/pages/collect/edit',
  '/pages/collect/detail',
  '/pages/collect/share',
    '/pages/story/index',
    '/pages/story/detail',
  '/pages/user/profile',
  '/pages/user/settings',
  '/pages/user/vip',
  '/pages/user/frequent',
  '/pages/user/favorites',
  '/pages/user/backup',
  '/pages/service/index'
])

const getCurrentRoute = () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  return currentPage?.route ? `/${currentPage.route}` : ''
}

const hasLoginToken = () => {
  try {
    return !!uni.getStorageSync('token')
  } catch (error) {
    return false
  }
}

const redirectToLogin = () => {
  const route = getCurrentRoute()
  if (!route || route === LOGIN_PAGE) {
    return
  }
  uni.showToast({ title: '请先登录', icon: 'none' })
  setTimeout(() => {
    uni.redirectTo({ url: LOGIN_PAGE })
  }, 300)
}

const isProtectedRoute = (url?: string) => {
  if (!url) return false
  const route = String(url).split('?')[0]
  return PROTECTED_ROUTES.has(route)
}

const guardRoute = (url?: string) => {
  if (!isProtectedRoute(url)) return true
  if (hasLoginToken()) return true
  redirectToLogin()
  return false
}

uni.addInterceptor('navigateTo', {
  invoke(args: { url?: string }) {
    return guardRoute(args?.url)
  }
})

uni.addInterceptor('redirectTo', {
  invoke(args: { url?: string }) {
    return guardRoute(args?.url)
  }
})

uni.addInterceptor('reLaunch', {
  invoke(args: { url?: string }) {
    return guardRoute(args?.url)
  }
})

uni.addInterceptor('switchTab', {
  invoke(args: { url?: string }) {
    return guardRoute(args?.url)
  }
})

const checkCurrentRouteAuth = () => {
  const route = getCurrentRoute()
  if (!route || route === LOGIN_PAGE) {
    return
  }
  if (!PROTECTED_ROUTES.has(route)) {
    return
  }
  if (!hasLoginToken()) {
    redirectToLogin()
  }
}

onLaunch(() => {
  console.log("App Launch");
  setTimeout(checkCurrentRouteAuth, 0)
  // 登录状态下自动连接通知 WebSocket
  if (hasLoginToken()) {
    const userId = uni.getStorageSync('userId')
    if (userId) notificationWs.connect(Number(userId))
  }
});
onShow(() => {
  console.log("App Show");
  setTimeout(checkCurrentRouteAuth, 0)
  // 恢复前台时重连 WebSocket
  if (hasLoginToken() && !notificationWs.isConnected()) {
    const userId = uni.getStorageSync('userId')
    if (userId) notificationWs.connect(Number(userId))
  }
});
onHide(() => {
  console.log("App Hide");
});
</script>
<style></style>
