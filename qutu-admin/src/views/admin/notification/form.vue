<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑通知' : '新增通知'" width="700" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="100px" v-loading="loading" class="notification-form">
			<el-form-item label="标题" prop="title">
				<el-input v-model="form.title" placeholder="通知标题" />
			</el-form-item>
			<el-row :gutter="24">
				<el-col :span="12">
					<el-form-item label="类型" prop="type">
						<el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
							<el-option label="系统" value="system" />
							<el-option label="活动" value="activity" />
							<el-option label="更新" value="update" />
							<el-option label="推广" value="promotion" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="优先级" prop="priority">
						<el-select v-model="form.priority" placeholder="选择优先级" style="width: 100%">
							<el-option label="普通" :value="0" />
							<el-option label="重要" :value="1" />
							<el-option label="紧急" :value="2" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="内容" prop="content">
				<el-input v-model="form.content" type="textarea" :rows="5" placeholder="通知内容" />
			</el-form-item>
			<el-form-item label="目标用户" prop="targetUserId">
				<el-select v-model="form.targetUserId" clearable filterable remote reserve-keyword placeholder="留空则全员通知" :remote-method="searchUsers" :loading="userSearching" style="width: 100%">
					<el-option v-for="u in userOptions" :key="u.userId" :label="u.username + (u.phone ? ' (' + u.phone + ')' : '')" :value="u.userId" />
				</el-select>
			</el-form-item>
			<el-form-item label="跳转链接" prop="linkUrl">
				<el-input v-model="form.linkUrl" placeholder="跳转链接(可选)" />
			</el-form-item>
			<el-form-item label="封面图" prop="coverUrl">
				<ImageUpload v-model="form.coverUrl" :limit="1" />
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="onSubmit" :disabled="loading">确定</el-button>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="NotificationDialog" setup>
import { addNotification, getNotificationObj, putNotification } from '/@/api/admin/notification';
import { useMessage } from '/@/hooks/message';
import request from '/@/utils/request';

const ImageUpload = defineAsyncComponent(() => import('/@/components/Upload/Image.vue'));

const emit = defineEmits(['refresh']);
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const userSearching = ref(false);
const userOptions = ref<any[]>([]);

const form = reactive({
	id: '' as any, title: '', content: '', type: 'system', targetUserId: undefined as any,
	linkUrl: '', coverUrl: '', priority: 0, publishStatus: 0,
});

const dataRules = ref({
	title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
	content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
	type: [{ required: true, message: '请选择类型', trigger: 'change' }],
});

const searchUsers = async (query: string) => {
	if (!query) { userOptions.value = []; return; }
	userSearching.value = true;
	try {
		const res = await request({ url: '/admin/user/page', method: 'get', params: { username: query, size: 20 } });
		userOptions.value = res.data?.records || [];
	} catch { userOptions.value = []; }
	finally { userSearching.value = false; }
};

const openDialog = async (id?: string) => {
	visible.value = true;
	userOptions.value = [];
	Object.assign(form, { id: id || '', title: '', content: '', type: 'system', targetUserId: undefined, linkUrl: '', coverUrl: '', priority: 0, publishStatus: 0 });
	if (id) {
		try {
			loading.value = true;
			const res = await getNotificationObj(id);
			await nextTick();
			Object.assign(form, res.data || {});
			// 如果有目标用户，预加载该用户信息
			if (form.targetUserId) {
				try {
					const uRes = await request({ url: '/admin/user/' + form.targetUserId, method: 'get' });
					if (uRes.data) userOptions.value = [uRes.data];
				} catch {}
			}
		} catch (err: any) { useMessage().error(err.msg || '获取数据失败'); }
		finally { loading.value = false; }
	}
};

const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;
	try {
		loading.value = true;
		const payload: any = { ...form };
		if (!payload.id) delete payload.id;
		form.id ? await putNotification(payload) : await addNotification(payload);
		useMessage().success(form.id ? '编辑成功' : '新增成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) { useMessage().error(err.msg || '保存失败'); }
	finally { loading.value = false; }
};

defineExpose({ openDialog });
</script>

<style lang="scss">
.notification-form {
	.el-form-item {
		margin-bottom: 20px !important;
	}

	.el-form-item:last-child {
		margin-bottom: 0 !important;
	}

	.el-row {
		margin-bottom: 20px;

		&:last-child {
			margin-bottom: 0;
		}
	}
}
</style>
