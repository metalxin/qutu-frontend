<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑采集' : '新增采集'" width="600" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="100px" v-loading="loading" class="collect-form">
			<el-form-item label="用户" prop="userId">
				<el-select v-model="form.userId" clearable filterable remote reserve-keyword placeholder="搜索用户名称" :remote-method="searchUsers" :loading="userSearching" style="width: 100%">
					<el-option v-for="u in userOptions" :key="u.userId" :label="u.username + (u.phone ? ' (' + u.phone + ')' : '')" :value="u.userId" />
				</el-select>
			</el-form-item>
			<el-form-item label="名称" prop="name">
				<el-input v-model="form.name" placeholder="采集名称" />
			</el-form-item>
			<el-row :gutter="16">
				<el-col :span="12">
					<el-form-item label="类型" prop="type">
						<el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
							<el-option label="拍照" value="photo" />
							<el-option label="语音" value="voice" />
							<el-option label="文本" value="text" />
							<el-option label="视频" value="video" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="分组" prop="groupName">
						<el-input v-model="form.groupName" placeholder="分组名称" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="描述" prop="description">
				<el-input v-model="form.description" type="textarea" :rows="3" placeholder="描述" />
			</el-form-item>
			<el-form-item label="位置" prop="locationName">
				<el-input v-model="form.locationName" placeholder="位置名称" />
			</el-form-item>
			<el-form-item label="坐标定位" prop="lat">
				<div style="display: flex; align-items: center; gap: 8px; width: 100%">
					<el-input v-model="locationDisplay" readonly placeholder="点击右侧按钮在地图上选择位置" style="flex: 1" />
					<el-button type="primary" @click="openMapPicker">
						<el-icon><Location /></el-icon>
						地图选点
					</el-button>
				</div>
				<div v-if="form.lat !== undefined && form.lng !== undefined" style="margin-top: 6px; display: flex; gap: 12px">
					<el-tag type="info" size="small">纬度: {{ form.lat }}</el-tag>
					<el-tag type="info" size="small">经度: {{ form.lng }}</el-tag>
				</div>
			</el-form-item>
			<el-form-item label="图片" prop="imageUrl">
				<ImageUpload v-model="form.imageUrl" :limit="1" />
			</el-form-item>
			<el-form-item label="标签" prop="tags">
				<el-input v-model="form.tags" placeholder="标签，逗号分隔" />
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="onSubmit" :disabled="loading">确定</el-button>
		</template>
	</el-dialog>
	<MapPicker ref="mapPickerRef" @confirm="onMapConfirm" />
</template>

<script lang="ts" name="CollectDialog" setup>
import { addCollect, getCollectObj, putCollect } from '/@/api/admin/footprint';
import { useMessage } from '/@/hooks/message';
import { Location } from '@element-plus/icons-vue';
import request from '/@/utils/request';

const ImageUpload = defineAsyncComponent(() => import('/@/components/Upload/Image.vue'));
const MapPicker = defineAsyncComponent(() => import('/@/components/MapPicker/index.vue'));

const emit = defineEmits(['refresh']);
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const userSearching = ref(false);
const userOptions = ref<any[]>([]);
const mapPickerRef = ref();

const form = reactive({
	id: '' as any, userId: undefined as any, name: '', description: '', imageUrl: '',
	aiResult: '', lat: undefined as any, lng: undefined as any, locationName: '',
	groupName: '', tags: '', type: 'photo', visibility: 1, sortOrder: 0,
});

const dataRules = ref({
	userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
	name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
});

const locationDisplay = computed(() => {
	if (form.lat !== undefined && form.lng !== undefined) {
		return `${form.lat}, ${form.lng}`;
	}
	return '';
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

const openMapPicker = () => {
	mapPickerRef.value?.openDialog(form.lng, form.lat);
};

const onMapConfirm = (data: { lng: number; lat: number; name: string; address: string }) => {
	form.lng = data.lng;
	form.lat = data.lat;
	if (data.name && !form.locationName) {
		form.locationName = data.name;
	}
};

const openDialog = async (id?: string) => {
	visible.value = true;
	userOptions.value = [];
	Object.assign(form, { id: id || '', userId: undefined, name: '', description: '', imageUrl: '', aiResult: '', lat: undefined, lng: undefined, locationName: '', groupName: '', tags: '', type: 'photo', visibility: 1, sortOrder: 0 });
	if (id) {
		try {
			loading.value = true;
			const res = await getCollectObj(id);
			Object.assign(form, res.data || {});
			if (form.userId) {
				try {
					const uRes = await request({ url: '/admin/user/' + form.userId, method: 'get' });
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
		form.id ? await putCollect(payload) : await addCollect(payload);
		useMessage().success(form.id ? '编辑成功' : '新增成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) { useMessage().error(err.msg || '保存失败'); }
	finally { loading.value = false; }
};

defineExpose({ openDialog });
</script>

<style lang="scss">
.collect-form {
	.el-form-item {
		margin-bottom: 20px !important;
	}

	.el-row {
		margin-bottom: 20px;

		&:last-child {
			margin-bottom: 0;
		}
	}
}
</style>
