<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑城市' : '新增城市'" width="900" draggable v-model="visible">
		<!-- 步骤指示器 -->
		<div :class="{ 'steps-wrapper': true, 'steps-clickable': form.id }" style="margin-bottom: 24px">
			<el-steps :active="currentStep" finish-status="success" align-center>
				<el-step v-for="(step, index) in stepsConfig" :key="index" :title="step.title" :description="step.description" />
			</el-steps>
			<div v-if="form.id" class="steps-click-mask">
				<div v-for="(step, index) in stepsConfig" :key="index" class="step-click-area" @click="goToStep(index)" />
			</div>
		</div>

		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="110px" v-loading="loading" class="city-form" :class="{ 'form-with-height': form.id }">
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
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="城市名称" prop="name">
									<el-input v-model="form.name" placeholder="请输入城市名称" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="省份" prop="province">
									<el-input v-model="form.province" placeholder="请输入省份" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="副标题" prop="subtitle">
							<el-input v-model="form.subtitle" placeholder="请输入副标题" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="国家" prop="country">
									<el-input v-model="form.country" placeholder="请输入国家" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="是否热门" prop="isHot">
									<el-radio-group v-model="form.isHot">
										<el-radio :value="1">热门</el-radio>
										<el-radio :value="0">普通</el-radio>
									</el-radio-group>
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="坐标定位" prop="centerLat">
							<div style="display: flex; align-items: center; gap: 8px; width: 100%">
								<el-input v-model="locationDisplay" readonly placeholder="点击右侧按钮在地图上选择位置" style="flex: 1" />
								<el-button type="primary" @click="openMapPicker">
									<el-icon><Location /></el-icon>
									地图选点
								</el-button>
							</div>
							<div v-if="form.centerLat !== undefined && form.centerLng !== undefined" style="margin-top: 6px; display: flex; gap: 12px">
								<el-tag type="info" size="small">纬度: {{ form.centerLat }}</el-tag>
								<el-tag type="info" size="small">经度: {{ form.centerLng }}</el-tag>
							</div>
						</el-form-item>
					</el-card>
				</div>

				<!-- 步骤2: 详细信息 -->
				<div v-show="currentStep === 1">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>详细信息</span>
							</div>
						</template>
						<el-form-item label="封面图" prop="coverUrl">
							<ImageUpload v-model="form.coverUrl" :limit="1" />
						</el-form-item>
						<el-form-item label="横幅图" prop="bannerUrl">
							<ImageUpload v-model="form.bannerUrl" :limit="1" />
						</el-form-item>
						<el-form-item label="简介" prop="intro">
							<el-input v-model="form.intro" type="textarea" :rows="4" placeholder="请输入城市简介" />
						</el-form-item>
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
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="城市名称" prop="name">
									<el-input v-model="form.name" placeholder="请输入城市名称" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="省份" prop="province">
									<el-input v-model="form.province" placeholder="请输入省份" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="副标题" prop="subtitle">
							<el-input v-model="form.subtitle" placeholder="请输入副标题" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="国家" prop="country">
									<el-input v-model="form.country" placeholder="请输入国家" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="是否热门" prop="isHot">
									<el-radio-group v-model="form.isHot">
										<el-radio :value="1">热门</el-radio>
										<el-radio :value="0">普通</el-radio>
									</el-radio-group>
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="坐标定位" prop="centerLat">
							<div style="display: flex; align-items: center; gap: 8px; width: 100%">
								<el-input v-model="locationDisplay" readonly placeholder="点击右侧按钮在地图上选择位置" style="flex: 1" />
								<el-button type="primary" @click="openMapPicker">
									<el-icon><Location /></el-icon>
									地图选点
								</el-button>
							</div>
							<div v-if="form.centerLat !== undefined && form.centerLng !== undefined" style="margin-top: 6px; display: flex; gap: 12px">
								<el-tag type="info" size="small">纬度: {{ form.centerLat }}</el-tag>
								<el-tag type="info" size="small">经度: {{ form.centerLng }}</el-tag>
							</div>
						</el-form-item>
					</el-card>
				</div>

				<!-- 步骤2: 详细信息 -->
				<div v-show="currentStep === 1">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>详细信息</span>
							</div>
						</template>
						<el-form-item label="简介" prop="intro">
							<el-input v-model="form.intro" type="textarea" :rows="4" placeholder="请输入城市简介" />
						</el-form-item>
					</el-card>
				</div>

				<!-- 步骤3: 图库设置 -->
				<div v-show="currentStep === 2">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>图库设置</span>
							</div>
						</template>
						<el-form-item label="封面图" prop="coverUrl">
							<ImageUpload v-model="form.coverUrl" :limit="1" />
						</el-form-item>
						<el-form-item label="横幅图" prop="bannerUrl">
							<ImageUpload v-model="form.bannerUrl" :limit="1" />
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

<script lang="ts" name="DestinationCityDialog" setup>
import { addDestination, getDestinationObj, putDestination } from '/@/api/admin/destination';
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

const stepsConfig = computed(() => {
	if (form.id) {
		return [
			{ title: '基础信息', description: '城市基本信息' },
			{ title: '详细信息', description: '城市简介' },
			{ title: '图库设置', description: '上传城市图片' },
		];
	}
	return [
		{ title: '基础信息', description: '城市基本信息' },
		{ title: '详细信息', description: '城市简介' },
	];
});

const goToStep = (index: number) => {
	if (!form.id) return;
	currentStep.value = index;
};

const locationDisplay = computed(() => {
	if (form.centerLat !== undefined && form.centerLng !== undefined) {
		return `${form.centerLat}, ${form.centerLng}`;
	}
	return '';
});

const openMapPicker = () => {
	mapPickerRef.value?.openDialog(form.centerLng, form.centerLat);
};

const onMapConfirm = (data: { lng: number; lat: number; name: string; address: string }) => {
	form.centerLng = data.lng;
	form.centerLat = data.lat;
};

const form = reactive({
	id: '' as any,
	name: '',
	province: '',
	country: '中国',
	subtitle: '',
	intro: '',
	coverUrl: '',
	bannerUrl: '',
	centerLat: undefined as number | undefined,
	centerLng: undefined as number | undefined,
	isHot: 0,
});

const dataRules = ref({
	name: [{ required: true, message: '城市名称不能为空', trigger: 'blur' }],
	province: [{ required: true, message: '省份不能为空', trigger: 'blur' }],
});

const nextStep = async () => {
	if (currentStep.value === 0) {
		if (!form.name || !form.province) {
			useMessage().warning('请填写城市名称和省份');
			return;
		}
	}
	currentStep.value++;
};

const prevStep = () => {
	currentStep.value--;
};

const openDialog = async (id?: string) => {
	visible.value = true;
	currentStep.value = 0;
	const isEditMode = id !== undefined && id !== null && id !== '';

	Object.assign(form, {
		id: isEditMode ? (id as any) : '',
		name: '',
		province: '',
		country: '中国',
		subtitle: '',
		intro: '',
		coverUrl: '',
		bannerUrl: '',
		centerLat: undefined,
		centerLng: undefined,
		isHot: 0,
	});

	if (isEditMode) {
		try {
			loading.value = true;
			const res = await getDestinationObj(id);
			const data = res.data || {};
			Object.assign(form, {
				id: data.id,
				name: data.name || '',
				province: data.province || '',
				country: data.country || '中国',
				subtitle: data.subtitle || '',
				intro: data.intro || '',
				coverUrl: data.coverUrl || data.image || '',
				bannerUrl: data.bannerUrl || '',
				centerLat: data.centerLat,
				centerLng: data.centerLng,
				isHot: data.isHot ?? 0,
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
		const payload = { ...form };
		if (!payload.id) delete payload.id;
		form.id ? await putDestination(payload) : await addDestination(payload);
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
.city-form {
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

.city-form.form-with-height {
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
