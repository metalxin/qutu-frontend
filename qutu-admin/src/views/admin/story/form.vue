<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑故事' : '新增故事'" width="900" draggable v-model="visible">
		<div :class="{ 'steps-wrapper': true, 'steps-clickable': form.id }" style="margin-bottom: 24px">
			<el-steps :active="currentStep" finish-status="success" align-center>
				<el-step v-for="(step, index) in stepsConfig" :key="index" :title="step.title" :description="step.description" />
			</el-steps>
			<div v-if="form.id" class="steps-click-mask">
				<div v-for="(step, index) in stepsConfig" :key="index" class="step-click-area" @click="goToStep(index)" />
			</div>
		</div>

		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="110px" v-loading="loading" class="story-form" :class="{ 'form-with-height': form.id }">
			<!-- ========== 新增模式：2步 ========== -->
			<template v-if="!form.id">
				<!-- 步骤1: 基础信息 -->
				<div v-show="currentStep === 0">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>基础信息</span>
							</div>
						</template>
						<el-form-item label="故事内容" prop="content">
							<el-input v-model="form.content" type="textarea" :rows="4" placeholder="记录旅途中的故事..." />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="地点名称" prop="locationName">
									<el-input v-model="form.locationName" placeholder="请输入地点名称" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="记录日期" prop="recordDate">
									<el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-row :gutter="12">
							<el-col :span="8">
								<el-form-item label="心情" prop="mood">
									<el-select v-model="form.mood" clearable placeholder="选择心情" style="width: 100%">
										<el-option label="开心" value="开心" />
										<el-option label="平静" value="平静" />
										<el-option label="感动" value="感动" />
										<el-option label="兴奋" value="兴奋" />
										<el-option label="伤感" value="伤感" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="天气" prop="weather">
									<el-select v-model="form.weather" clearable placeholder="选择天气" style="width: 100%">
										<el-option label="晴" value="晴" />
										<el-option label="阴" value="阴" />
										<el-option label="雨" value="雨" />
										<el-option label="雪" value="雪" />
										<el-option label="多云" value="多云" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="可见性" prop="visibility">
									<el-select v-model="form.visibility" placeholder="选择可见性" style="width: 100%">
										<el-option label="仅自己" :value="1" />
										<el-option label="好友可见" :value="2" />
										<el-option label="公开" :value="3" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-row>
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
					</el-card>
				</div>

				<!-- 步骤2: 图库设置 -->
				<div v-show="currentStep === 1">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>图库设置</span>
							</div>
						</template>
						<el-form-item label="故事图片" prop="images">
							<ImageUpload v-model="form.imagesStr" :limit="9" />
						</el-form-item>
						<el-alert title="最多上传9张图片" type="info" :closable="false" show-icon />
					</el-card>
				</div>
			</template>

			<!-- ========== 编辑模式：3步 ========== -->
			<template v-else>
				<!-- 步骤1: 基础信息 -->
				<div v-show="currentStep === 0">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>基础信息</span>
							</div>
						</template>
						<el-form-item label="故事内容" prop="content">
							<el-input v-model="form.content" type="textarea" :rows="4" placeholder="记录旅途中的故事..." />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="地点名称" prop="locationName">
									<el-input v-model="form.locationName" placeholder="请输入地点名称" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="记录日期" prop="recordDate">
									<el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-row :gutter="12">
							<el-col :span="8">
								<el-form-item label="心情" prop="mood">
									<el-select v-model="form.mood" clearable placeholder="选择心情" style="width: 100%">
										<el-option label="开心" value="开心" />
										<el-option label="平静" value="平静" />
										<el-option label="感动" value="感动" />
										<el-option label="兴奋" value="兴奋" />
										<el-option label="伤感" value="伤感" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="天气" prop="weather">
									<el-select v-model="form.weather" clearable placeholder="选择天气" style="width: 100%">
										<el-option label="晴" value="晴" />
										<el-option label="阴" value="阴" />
										<el-option label="雨" value="雨" />
										<el-option label="雪" value="雪" />
										<el-option label="多云" value="多云" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="可见性" prop="visibility">
									<el-select v-model="form.visibility" placeholder="选择可见性" style="width: 100%">
										<el-option label="仅自己" :value="1" />
										<el-option label="好友可见" :value="2" />
										<el-option label="公开" :value="3" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-row>
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
					</el-card>
				</div>

				<!-- 步骤2: 图库设置 -->
				<div v-show="currentStep === 1">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>图库设置</span>
							</div>
						</template>
						<el-form-item label="故事图片" prop="images">
							<ImageUpload v-model="form.imagesStr" :limit="9" />
						</el-form-item>
						<el-alert title="最多上传9张图片" type="info" :closable="false" show-icon />
					</el-card>
				</div>

				<!-- 步骤3: 关联景点 -->
				<div v-show="currentStep === 2">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>关联景点</span>
							</div>
						</template>
						<el-form-item label="关联景点" prop="spotId">
							<el-select v-model="form.spotId" clearable filterable placeholder="请选择关联景点" style="width: 100%">
								<el-option v-for="spot in spotOptions" :key="spot.id" :label="spot.name" :value="spot.id" />
							</el-select>
						</el-form-item>
					</el-card>
				</div>
			</template>
		</el-form>

		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
				<template v-if="!form.id">
					<el-button v-if="currentStep < 1" type="primary" @click="nextStep">下一步</el-button>
					<el-button v-if="currentStep === 1" type="primary" @click="onSubmit" :disabled="loading">完成</el-button>
				</template>
				<template v-else>
					<el-button v-if="currentStep < 2" type="primary" @click="nextStep">下一步</el-button>
					<el-button v-if="currentStep === 2" type="primary" @click="onSubmit" :disabled="loading">完成</el-button>
				</template>
			</div>
		</template>
	</el-dialog>
	<MapPicker ref="mapPickerRef" @confirm="onMapConfirm" />
</template>

<script lang="ts" name="StoryDialog" setup>
import { addStory, getStoryObj, putStory } from '/@/api/admin/story';
import { fetchSpotPage } from '/@/api/admin/destination';
import { useMessage } from '/@/hooks/message';
import { Location } from '@element-plus/icons-vue';

const ImageUpload = defineAsyncComponent(() => import('/@/components/Upload/Image.vue'));
const MapPicker = defineAsyncComponent(() => import('/@/components/MapPicker/index.vue'));

const emit = defineEmits(['refresh']);

const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const currentStep = ref(0);
const mapPickerRef = ref();

const spotOptions = ref<any[]>([]);

const stepsConfig = computed(() => {
	if (form.id) {
		return [
			{ title: '基础信息', description: '故事内容与设置' },
			{ title: '图库设置', description: '上传故事图片' },
			{ title: '关联景点', description: '关联相关景点' },
		];
	}
	return [
		{ title: '基础信息', description: '故事内容与设置' },
		{ title: '图库设置', description: '上传故事图片' },
	];
});

const goToStep = (index: number) => {
	if (!form.id) return;
	currentStep.value = index;
};

const form = reactive({
	id: '' as any,
	userId: undefined as number | undefined,
	content: '',
	locationName: '',
	lng: undefined as number | undefined,
	lat: undefined as number | undefined,
	recordDate: '',
	mood: '',
	weather: '',
	visibility: 3,
	spotId: undefined as string | undefined,
	images: [] as string[],
	imagesStr: '',
});

const dataRules = ref({
	content: [{ required: true, message: '故事内容不能为空', trigger: 'blur' }],
	recordDate: [{ required: true, message: '记录日期不能为空', trigger: 'change' }],
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
	if (!form.locationName && data.name) {
		form.locationName = data.name;
	}
};

watch(
	() => form.imagesStr,
	(val: string) => {
		if (val) {
			form.images = val.split(',').filter(Boolean);
		} else {
			form.images = [];
		}
	}
);

const nextStep = async () => {
	if (currentStep.value === 0) {
		if (!form.content) {
			useMessage().warning('请填写故事内容');
			return;
		}
	}
	currentStep.value++;
};

const prevStep = () => {
	currentStep.value--;
};

const loadSpotOptions = async () => {
	try {
		const res: any = await fetchSpotPage({ current: 1, size: 500 });
		const records = res.data?.records || res.data || [];
		spotOptions.value = records.map((s: any) => ({ ...s, id: String(s.id) }));
	} catch {
		spotOptions.value = [];
	}
};

const openDialog = async (id?: string) => {
	visible.value = true;
	currentStep.value = 0;
	const isEditMode = id !== undefined && id !== null && id !== '';

	Object.assign(form, {
		id: isEditMode ? (id as any) : '',
		userId: undefined,
		content: '',
		locationName: '',
		lng: undefined,
		lat: undefined,
		recordDate: '',
		mood: '',
		weather: '',
		visibility: 3,
		spotId: undefined,
		images: [],
		imagesStr: '',
	});

	await loadSpotOptions();

	if (isEditMode) {
		try {
			loading.value = true;
			const res = await getStoryObj(id);
			const data = res.data || {};
			Object.assign(form, {
				id: data.id,
				userId: data.userId,
				content: data.content || '',
				locationName: data.locationName || '',
				lng: data.lng,
				lat: data.lat,
				recordDate: data.recordDate || '',
				mood: data.mood || '',
				weather: data.weather || '',
				visibility: data.visibility ?? 3,
				spotId: data.spotId ? String(data.spotId) : undefined,
				images: data.images || [],
				imagesStr: (data.images || []).join(','),
			});
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
		delete payload.imagesStr;
		if (!payload.id) delete payload.id;
		form.id ? await putStory(payload) : await addStory(payload);
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
.story-form {
	.el-form-item {
		margin-bottom: 24px !important;
	}

	.el-form-item:last-child {
		margin-bottom: 0 !important;
	}

	.el-row {
		margin-bottom: 24px;

		&:last-child {
			margin-bottom: 0;
		}
	}
}

.story-form.form-with-height {
	min-height: 420px;

	> div {
		min-height: 420px;
	}
}

.step-card {
	margin-bottom: 20px;

	.card-header {
		font-size: 16px;
		font-weight: 600;
		color: #303133;
	}
}

.dialog-footer {
	display: flex;
	justify-content: flex-end;
	gap: 12px;
}

.steps-wrapper {
	position: relative;
}

.steps-clickable {
	.el-steps {
		.el-step {
			cursor: pointer;

			&:hover {
				opacity: 0.8;
			}
		}
	}
}

.steps-click-mask {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: flex;
}

.step-click-area {
	flex: 1;
	cursor: pointer;

	&:hover {
		background: rgba(0, 0, 0, 0.02);
	}
}
</style>
