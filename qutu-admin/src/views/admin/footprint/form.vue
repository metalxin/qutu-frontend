<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑足迹' : '新增足迹'" width="600" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="100px" v-loading="loading" class="footprint-form">
			<el-form-item label="用户" prop="userId">
				<el-select v-model="form.userId" clearable filterable remote reserve-keyword placeholder="搜索用户名称" :remote-method="searchUsers" :loading="userSearching" style="width: 100%">
					<el-option v-for="u in userOptions" :key="u.userId" :label="u.username + (u.phone ? ' (' + u.phone + ')' : '')" :value="u.userId" />
				</el-select>
			</el-form-item>
			<el-form-item label="景点名称" prop="spotName">
				<el-input v-model="form.spotName" placeholder="请输入景点名称" />
			</el-form-item>
			<el-row :gutter="16">
				<el-col :span="12">
					<el-form-item label="城市" prop="cityName">
						<el-input v-model="form.cityName" placeholder="城市名称" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="省份" prop="provinceName">
						<el-input v-model="form.provinceName" placeholder="省份名称" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="16">
				<el-col :span="12">
					<el-form-item label="省份编码" prop="provinceCode">
						<el-input v-model="form.provinceCode" placeholder="如 ZJ" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="国家" prop="countryName">
						<el-input v-model="form.countryName" placeholder="国家名称" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="16">
				<el-col :span="8">
					<el-form-item label="国家编码" prop="countryCode">
						<el-input v-model="form.countryCode" placeholder="如 CN" />
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="纬度" prop="lat">
						<el-input-number v-model="form.lat" :precision="6" :controls="false" placeholder="纬度" style="width: 100%" />
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="经度" prop="lng">
						<el-input-number v-model="form.lng" :precision="6" :controls="false" placeholder="经度" style="width: 100%" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="类型" prop="type">
				<el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
					<el-option label="国内" :value="1" />
					<el-option label="国外" :value="2" />
				</el-select>
			</el-form-item>
			<el-form-item label="足迹图片" prop="imageUrl">
				<ImageUpload v-model="form.imageUrl" :limit="1" />
			</el-form-item>
			<el-form-item label="备注" prop="remark">
				<el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="onSubmit" :disabled="loading">确定</el-button>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="FootprintDialog" setup>
import { addFootprint, getFootprintObj, putFootprint } from '/@/api/admin/footprint';
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
	id: '' as any,
	userId: undefined as number | undefined,
	spotId: undefined as number | undefined,
	spotName: '',
	cityName: '',
	provinceName: '',
	provinceCode: '',
	countryName: '',
	countryCode: '',
	lat: undefined as number | undefined,
	lng: undefined as number | undefined,
	type: 1,
	remark: '',
	imageUrl: '',
});

const dataRules = ref({
	userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
	spotName: [{ required: true, message: '景点名称不能为空', trigger: 'blur' }],
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
	Object.assign(form, {
		id: id || '',
		userId: undefined, spotId: undefined, spotName: '', cityName: '',
		provinceName: '', provinceCode: '', countryName: '', countryCode: '',
		lat: undefined, lng: undefined, type: 1, remark: '', imageUrl: '',
	});
	if (id) {
		try {
			loading.value = true;
			const res = await getFootprintObj(id);
			const data = res.data || {};
			Object.assign(form, data);
			if (form.userId) {
				try {
					const uRes = await request({ url: '/admin/user/' + form.userId, method: 'get' });
					if (uRes.data) userOptions.value = [uRes.data];
				} catch {}
			}
		} catch (err: any) {
			useMessage().error(err.msg || '获取数据失败');
		} finally {
			loading.value = false;
		}
	}
};

const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;
	try {
		loading.value = true;
		const payload: any = { ...form };
		if (!payload.id) delete payload.id;
		form.id ? await putFootprint(payload) : await addFootprint(payload);
		useMessage().success(form.id ? '编辑成功' : '新增成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg || '保存失败');
	} finally {
		loading.value = false;
	}
};

defineExpose({ openDialog });
</script>

<style lang="scss">
.footprint-form {
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
