<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑POI' : '新增POI'" width="800" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="100px" v-loading="loading" class="poi-form">
			<el-row :gutter="24">
				<el-col :span="12">
					<el-form-item label="名称" prop="name">
						<el-input v-model="form.name" placeholder="POI名称" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="分类" prop="categoryId">
						<el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
							<el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="24">
				<el-col :span="12">
					<el-form-item label="城市" prop="cityName">
						<el-input v-model="form.cityName" placeholder="城市名称" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="评分" prop="rating">
						<el-input-number v-model="form.rating" :precision="1" :min="0" :max="5" :controls="false" style="width: 100%" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="地址" prop="address">
				<el-input v-model="form.address" placeholder="详细地址" />
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
			<el-row :gutter="24">
				<el-col :span="12">
					<el-form-item label="价格" prop="priceText">
						<el-input v-model="form.priceText" placeholder="如：人均50元" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="状态" prop="status">
						<el-select v-model="form.status" style="width: 100%">
							<el-option label="上架" :value="1" />
							<el-option label="下架" :value="0" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="副标题" prop="subtitle">
				<el-input v-model="form.subtitle" placeholder="副标题" />
			</el-form-item>
			<el-form-item label="描述" prop="description">
				<el-input v-model="form.description" type="textarea" :rows="3" placeholder="详细介绍" />
			</el-form-item>
			<el-row :gutter="24">
				<el-col :span="12">
					<el-form-item label="电话" prop="phone">
						<el-input v-model="form.phone" placeholder="联系电话" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="营业时间" prop="openTime">
						<el-input v-model="form.openTime" placeholder="如 09:00-22:00" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="封面图" prop="coverUrl">
				<ImageUpload v-model="form.coverUrl" :limit="1" />
			</el-form-item>
			<el-form-item label="图库" prop="galleryStr">
				<ImageUpload v-model="form.galleryStr" :limit="9" />
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="onSubmit" :disabled="loading">确定</el-button>
		</template>
	</el-dialog>
	<MapPicker ref="mapPickerRef" @confirm="onMapConfirm" />
</template>

<script lang="ts" name="NearbyPoiDialog" setup>
import { addNearbyPoi, getNearbyPoiObj, putNearbyPoi, fetchNearbyCategoryList } from '/@/api/admin/nearby';
import { useMessage } from '/@/hooks/message';
import { Location } from '@element-plus/icons-vue';

const ImageUpload = defineAsyncComponent(() => import('/@/components/Upload/Image.vue'));
const MapPicker = defineAsyncComponent(() => import('/@/components/MapPicker/index.vue'));

const emit = defineEmits(['refresh']);
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const categories = ref<any[]>([]);
const mapPickerRef = ref();

const form = reactive({
	id: '' as any, categoryId: undefined as any, cityName: '', name: '', subtitle: '', coverUrl: '',
	rating: undefined as any, ratingCount: undefined as any, priceText: '', lat: undefined as any, lng: undefined as any,
	address: '', description: '', phone: '', openTime: '', bestSeason: '', suitableFor: '', visitMinutes: undefined as any,
	viewCount: 0, favoriteCount: 0, checkinCount: 0, hotScore: undefined as any, status: 1, galleryStr: '',
});

const locationDisplay = computed(() => {
	if (form.lat !== undefined && form.lng !== undefined) {
		return `${form.lat}, ${form.lng}`;
	}
	return '';
});

const openMapPicker = () => {
	mapPickerRef.value?.openDialog(form.lng, form.lat);
};

const onMapConfirm = (data: { lng: number; lat: number; name: string; address: string }) => {
	form.lng = data.lng;
	form.lat = data.lat;
	if (data.address && !form.address) {
		form.address = data.address;
	}
};

const dataRules = ref({ name: [{ required: true, message: '名称不能为空', trigger: 'blur' }] });

const loadCategories = async () => {
	try { const res = await fetchNearbyCategoryList(); categories.value = res.data || []; }
	catch { categories.value = []; }
};

const openDialog = async (id?: string) => {
	visible.value = true;
	await loadCategories();
	Object.assign(form, { id: id || '', categoryId: undefined, cityName: '', name: '', subtitle: '', coverUrl: '', rating: undefined, ratingCount: undefined, priceText: '', lat: undefined, lng: undefined, address: '', description: '', phone: '', openTime: '', bestSeason: '', suitableFor: '', visitMinutes: undefined, viewCount: 0, favoriteCount: 0, checkinCount: 0, hotScore: undefined, status: 1, galleryStr: '' });
	if (id) {
		try {
			loading.value = true;
			const res = await getNearbyPoiObj(id);
			Object.assign(form, res.data || {});
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
		form.id ? await putNearbyPoi(payload) : await addNearbyPoi(payload);
		useMessage().success(form.id ? '编辑成功' : '新增成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) { useMessage().error(err.msg || '保存失败'); }
	finally { loading.value = false; }
};

defineExpose({ openDialog });
</script>

<style lang="scss">
.poi-form {
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
