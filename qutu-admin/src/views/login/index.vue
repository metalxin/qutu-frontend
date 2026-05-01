<template>
	<div class="select-none">
		<div class="login-container">
			<div class="login-box">
				<!-- 左侧 -->
				<div class="login-box-left">
					<div class="login-left-decoration">
						<div class="login-logo-icon">
							<svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
								<rect width="120" height="120" rx="20" fill="url(#grad)"/>
								<path d="M35 75 L60 40 L85 75 L75 75 L60 55 L45 75 Z" fill="white" opacity="0.9"/>
								<path d="M40 85 Q60 70 80 85 Q70 95 60 88 Q50 95 40 85" fill="white" opacity="0.8"/>
								<defs>
									<linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
										<stop offset="0%" style="stop-color:#36b36e;stop-opacity:1" />
										<stop offset="100%" style="stop-color:#1a8f51;stop-opacity:1" />
									</linearGradient>
								</defs>
							</svg>
						</div>
					</div>
					<div class="login-left-title">
						<h2>趣途后台管理系统</h2>
						<p>欢迎登录</p>
					</div>
				</div>
				
				<!-- 右侧表单 -->
				<div class="login-form">
					<!-- 下载二维码 -->
					<div class="login-qr-download">
						<button class="qr-btn">扫码下载APP</button>
						<div class="qr-icon">
							<svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
								<rect x="2" y="2" width="28" height="28" rx="4" stroke="#ddd" stroke-width="1.5"/>
								<rect x="6" y="6" width="20" height="20" rx="2" fill="#36b36e" opacity="0.1"/>
								<rect x="8" y="8" width="6" height="6" fill="#36b36e"/>
								<rect x="18" y="8" width="6" height="6" fill="#36b36e"/>
								<rect x="8" y="18" width="6" height="6" fill="#36b36e"/>
								<rect x="18" y="18" width="6" height="6" fill="#36b36e"/>
							</svg>
						</div>
					</div>
					
					<div class="login-title">欢迎登录</div>
					<el-tabs v-model="tabsActiveName">
						<!-- 用户名密码登录 -->
						<el-tab-pane :label="$t('label.one1')" name="account">
							<Password @signInSuccess="signInSuccess" />
						</el-tab-pane>
						<!-- 手机号登录 -->
						<el-tab-pane :label="$t('label.two2')" name="mobile">
							<Mobile @signInSuccess="signInSuccess" />
						</el-tab-pane>
						<!-- 注册 -->
						<el-tab-pane :label="$t('label.register')" name="register" v-if="registerEnable">
							<Register @afterSuccess="tabsActiveName = 'account'" />
						</el-tab-pane>
					</el-tabs>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts" name="loginIndex">
import { useThemeConfig } from '/@/stores/themeConfig';
import { NextLoading } from '/@/utils/loading';
import illustration from '/@/assets/login/login_bg.svg';
import miniQr from '/@/assets/login/mini_qr.png';
import { useI18n } from 'vue-i18n';
import { formatAxis } from '/@/utils/formatTime';
import { useMessage } from '/@/hooks/message';
import { Session } from '/@/utils/storage';
import { initBackEndControlRoutes } from '/@/router/backEnd';

// 引入组件
const Password = defineAsyncComponent(() => import('./component/password.vue'));
const Mobile = defineAsyncComponent(() => import('./component/mobile.vue'));
const Register = defineAsyncComponent(() => import('./component/register.vue'));

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const { t } = useI18n();
const route = useRoute();
const router = useRouter();

// 是否开启注册
const registerEnable = ref(import.meta.env.VITE_REGISTER_ENABLE === 'true');

// 默认选择账号密码登录方式
const tabsActiveName = ref('account');

// 获取布局配置信息
const getThemeConfig = computed(() => {
	return themeConfig.value;
});

// 登录成功后的跳转处理事件
const signInSuccess = async () => {
	const isNoPower = await initBackEndControlRoutes();
	if (isNoPower) {
		useMessage().warning('抱歉，您没有登录权限');
		Session.clear();
	} else {
		// 初始化登录成功时间问候语
		let currentTimeInfo = formatAxis(new Date());
		if (route.query?.redirect) {
			router.push({
				path: <string>route.query?.redirect,
				query: Object.keys(<string>route.query?.params).length > 0 ? JSON.parse(<string>route.query?.params) : '',
			});
		} else {
			router.push('/');
		}
		// 登录成功提示
		const signInText = t('signInText');
		useMessage().success(`${currentTimeInfo}，${signInText}`);
		// 添加 loading，防止第一次进入界面时出现短暂空白
		NextLoading.start();
	}
};

// 页面加载时
onMounted(() => {
	NextLoading.done();
});
</script>
