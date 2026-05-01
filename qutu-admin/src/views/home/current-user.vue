<template>
	<el-card class="h-full shadow-sm hover:shadow-md transition-shadow">
		<div class="flex items-center justify-between">
			<!-- 用户信息 -->
			<div class="flex items-center gap-4">
				<el-avatar 
					:size="56" 
					shape="circle" 
					:src="avatarUrl"
					class="ring-1 ring-gray-100" 
				/>
				<div>
					<h3 class="text-lg font-semibold text-gray-800 mb-2">{{ userData.name }}</h3>
					<div class="flex items-center gap-2 text-sm">
						<span class="px-3 py-1 bg-blue-50 text-blue-600 rounded-full">{{ userData?.dept?.name }}</span>
						<span v-if="userData.postName" class="px-3 py-1 bg-green-50 text-green-600 rounded-full">{{ userData.postName }}</span>
					</div>
				</div>
			</div>

			<!-- 时间 -->
			<div class="flex items-center gap-2 text-sm text-gray-500">
				<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
				</svg>
				<span>{{ parseTime(date) }}</span>
			</div>
		</div>
	</el-card>
</template>

<script setup lang="ts" name="currentUser">
import { useUserInfo } from '/@/stores/userInfo';
import { getObj } from '/@/api/admin/user';

const baseURL = import.meta.env.VITE_API_URL || '';
const date = ref(new Date());

// 智能处理头像 URL：如果已包含完整路径则不重复拼接
const avatarUrl = computed(() => {
	const avatar = userData.value.avatar;
	if (!avatar) return '';
	if (avatar.startsWith('http') || avatar.startsWith('/api') || avatar.startsWith(baseURL)) return avatar;
	return baseURL + avatar;
});

const userData = ref({
	postName: '',
	name: '',
	username: '',
	userId: '',
	avatar: '',
	deptName: '',
	dept: null as any,
} as any);
const loading = ref(false);

setInterval(() => {
	date.value = new Date();
}, 1000);

onMounted(() => {
	const userStore = useUserInfo();
	// 兼容后端返回的两种数据结构：嵌套（含 sysUser）和扁平
	const getUserData = (): any => {
		const user = userStore.userInfos?.user;
		if (!user) return null;
		if (user.sysUser) return user.sysUser;
		return user;
	};

	const loadUserInfo = () => {
		const data = getUserData();
		const userId = data?.userId;
		if (userId !== undefined && userId !== null && userId !== '') {
			initUserInfo(userId);
		}
	};

	loadUserInfo();

	if (!getUserData()?.userId) {
		userStore.setUserInfos().then(() => {
			loadUserInfo();
		});
	}
});

/**
 * 根据用户 ID 初始化用户信息。
 * @param {any} userId - 要查询的用户 ID。
 * @returns {Promise<void>} - 初始化用户信息的 Promise 实例。
 */
const initUserInfo = async (userId: any): Promise<void> => {
	if (userId === undefined || userId === null || userId === '') {
		return;
	}

	try {
		loading.value = true;

		const res = await getObj(userId);
		userData.value = res.data;
		userData.value.postName = res.data?.postList?.map((item: any) => item.postName).join(',') || '';
	} finally {
		loading.value = false;
	}
};
</script>

<style scoped>
.el-card {
	border: 1px solid #e5e7eb;
	border-radius: 12px;
	transition: all 0.2s ease;
}

.el-card :deep(.el-card__body) {
	padding: 24px;
}

/* 头像轻微悬停效果 */
.el-avatar {
	transition: transform 0.2s ease;
}

.el-avatar:hover {
	transform: scale(1.05);
}

/* 标签悬停效果 */
.px-3.py-1 {
	transition: all 0.2s ease;
	font-weight: 500;
}

.px-3.py-1:hover {
	transform: translateY(-1px);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式适配 */
@media (max-width: 640px) {
	.flex.items-center.justify-between {
		flex-direction: column;
		align-items: flex-start;
		gap: 16px;
	}
	
	.el-card :deep(.el-card__body) {
		padding: 16px;
	}
}
</style>

