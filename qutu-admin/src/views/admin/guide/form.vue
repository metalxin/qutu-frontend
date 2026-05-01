<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑攻略' : '新增攻略'" width="900" draggable v-model="visible">
		<!-- 步骤指示器 -->
		<div :class="{ 'steps-wrapper': true, 'steps-clickable': form.id }" style="margin-bottom: 24px">
			<el-steps :active="currentStep" finish-status="success" align-center>
				<el-step v-for="(step, index) in stepsConfig" :key="index" :title="step.title" :description="step.description" />
			</el-steps>
			<div v-if="form.id" class="steps-click-mask">
				<div v-for="(step, index) in stepsConfig" :key="index" class="step-click-area" @click="goToStep(index)" />
			</div>
		</div>

		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="110px" v-loading="loading" class="guide-form" :class="{ 'form-with-height': form.id }">
			<!-- 步骤1: 基础信息 -->
			<div v-show="currentStep === 0">
				<el-card shadow="never" class="step-card">
					<template #header>
						<div class="card-header"><span>基础信息</span></div>
					</template>
					<el-row :gutter="12">
						<el-col :span="12">
							<el-form-item label="标题" prop="title">
								<el-input v-model="form.title" placeholder="请输入攻略标题" />
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="标签" prop="tag">
								<el-input v-model="form.tag" placeholder="例如：Citywalk / 亲子" />
							</el-form-item>
						</el-col>
					</el-row>
					<el-row :gutter="12">
						<el-col :span="12">
							<el-form-item label="所属城市" prop="cityId">
								<el-select v-model="form.cityId" placeholder="请选择城市" filterable style="width: 100%" @change="onCityChange">
									<el-option v-for="city in cityOptions" :key="city.id" :label="city.name" :value="city.id" />
								</el-select>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="城市名" prop="cityName">
								<el-input v-model="form.cityName" placeholder="选择城市后自动填充" readonly />
							</el-form-item>
						</el-col>
					</el-row>
					<el-form-item label="封面图" prop="coverUrl">
						<ImageUpload v-model="form.coverUrl" :limit="1" />
					</el-form-item>
					<el-form-item label="摘要" prop="summary">
						<el-input v-model="form.summary" type="textarea" :rows="4" placeholder="请输入攻略摘要" />
					</el-form-item>
					<el-row :gutter="12">
						<el-col :span="8">
							<el-form-item label="天数" prop="days">
								<el-input-number v-model="form.days" :min="1" style="width: 100%" />
							</el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item label="晚数" prop="nights">
								<el-input-number v-model="form.nights" :min="0" style="width: 100%" />
							</el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item label="状态" prop="status">
								<el-radio-group v-model="form.status">
									<el-radio :value="1">上架</el-radio>
									<el-radio :value="0">下架</el-radio>
								</el-radio-group>
							</el-form-item>
						</el-col>
					</el-row>
					<el-row :gutter="12">
						<el-col :span="12">
							<el-form-item label="点赞数" prop="likeCount">
								<el-input-number v-model="form.likeCount" :min="0" style="width: 100%" />
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="浏览量" prop="viewCount">
								<el-input-number v-model="form.viewCount" :min="0" style="width: 100%" />
							</el-form-item>
						</el-col>
					</el-row>
				</el-card>
			</div>

			<!-- 步骤2: 行程设置 -->
			<div v-show="currentStep === 1">
				<el-card shadow="never" class="step-card">
					<template #header>
						<div class="card-header">
							<span>行程设置</span>
							<el-button type="primary" icon="folder-add" size="small" @click="addDay">新增一天</el-button>
						</div>
					</template>
					<div v-for="(day, dayIndex) in form.itinerary" :key="day.id || dayIndex" class="day-card">
						<div class="day-card-header">
							<div>
								<strong>Day {{ dayIndex + 1 }}</strong>
								<span class="day-card-subtitle">{{ day.title || '未设置标题' }}</span>
							</div>
							<div>
								<el-button size="small" @click="addItem(dayIndex)">新增条目</el-button>
								<el-button size="small" type="danger" plain @click="removeDay(dayIndex)">删除当天</el-button>
							</div>
						</div>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item :label="`Day ${dayIndex + 1} 标题`">
									<el-input v-model="day.title" placeholder="请输入当天标题" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item :label="`Day ${dayIndex + 1} 摘要`">
									<el-input v-model="day.summary" placeholder="请输入当天摘要" />
								</el-form-item>
							</el-col>
						</el-row>
						<div v-for="(item, itemIndex) in day.items" :key="item.id || `${dayIndex}-${itemIndex}`" class="item-card">
							<div class="item-card-header">
								<strong>条目 {{ itemIndex + 1 }}</strong>
								<el-button size="small" type="danger" plain @click="removeItem(dayIndex, itemIndex)">删除条目</el-button>
							</div>
							<el-row :gutter="12">
								<el-col :span="8">
									<el-form-item label="类型">
										<el-select v-model="item.type" placeholder="请选择类型" class="w100">
											<el-option label="文本" value="text" />
											<el-option label="景点" value="spot" />
										</el-select>
									</el-form-item>
								</el-col>
								<el-col :span="8">
									<el-form-item label="标题">
										<el-input v-model="item.title" placeholder="请输入条目标题" />
									</el-form-item>
								</el-col>
								<el-col :span="8" v-if="item.type === 'spot'">
									<el-form-item label="关联景点">
										<el-select v-model="item.spotId" placeholder="请选择景点" filterable style="width: 100%" @change="(val: any) => onSpotSelect(val, dayIndex, itemIndex)">
											<el-option v-for="spot in spotOptions" :key="spot.id" :label="spot.name" :value="spot.id" />
										</el-select>
									</el-form-item>
								</el-col>
							</el-row>
							<el-form-item label="内容" v-if="item.type === 'text'">
								<el-input v-model="item.content" type="textarea" :rows="3" placeholder="请输入条目内容" />
							</el-form-item>
							<el-form-item label="备注" v-else>
								<el-input v-model="item.content" placeholder="可填写补充说明" />
							</el-form-item>
						</div>
					</div>
				</el-card>
			</div>
		</el-form>

		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
				<el-button v-if="currentStep < 1" type="primary" @click="nextStep">下一步</el-button>
				<el-button v-if="currentStep === 1" type="primary" @click="onSubmit" :disabled="loading">完成</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="GuideDialog" setup>
import { addGuide, getGuideObj, putGuide } from '/@/api/admin/guide';
import { fetchDestinationPage, fetchSpotPage } from '/@/api/admin/destination';
import { useMessage } from '/@/hooks/message';

const ImageUpload = defineAsyncComponent(() => import('/@/components/Upload/Image.vue'));

const emit = defineEmits(['refresh']);

const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const currentStep = ref(0);

// 步骤配置
const stepsConfig = computed(() => {
	return [
		{ title: '基础信息', description: '标题、城市、封面等' },
		{ title: '行程设置', description: '日程与条目编排' },
	];
});

// 跳转到指定步骤（编辑模式用）
const goToStep = (index: number) => {
	if (!form.id) return;
	currentStep.value = index;
};

// 城市列表和景点列表
const cityOptions = ref<any[]>([]);
const spotOptions = ref<any[]>([]);

const form = reactive({
	id: '',
	title: '',
	coverUrl: '',
	summary: '',
	days: 1,
	nights: 0,
	tag: '',
	likeCount: 0,
	viewCount: 0,
	cityId: undefined as number | undefined,
	cityName: '',
	status: 1,
	itinerary: [
		{
			id: '',
			dayIndex: 1,
			title: '',
			summary: '',
			items: [
				{
					id: '',
					type: 'text',
					title: '',
					content: '',
					spotId: undefined,
					orderNum: 1,
				},
			],
		},
	],
});

const dataRules = ref({
	title: [{ required: true, message: '攻略标题不能为空', trigger: 'blur' }],
	cityName: [{ required: true, message: '城市名不能为空', trigger: 'blur' }],
	days: [{ required: true, message: '天数不能为空', trigger: 'blur' }],
});

const createEmptyDay = () => ({
	id: '',
	dayIndex: form.itinerary.length + 1,
	title: '',
	summary: '',
	items: [
		{
			id: '',
			type: 'text',
			title: '',
			content: '',
			spotId: undefined,
			orderNum: 1,
		},
	],
});

// 城市选择变化时自动填充城市名
const onCityChange = (val: number) => {
	const city = cityOptions.value.find((c: any) => c.id === val);
	form.cityName = city ? city.name : '';
};

// 景点选择时自动填充标题
const onSpotSelect = (val: any, dayIndex: number, itemIndex: number) => {
	const spot = spotOptions.value.find((s: any) => s.id === val);
	if (spot) {
		form.itinerary[dayIndex].items[itemIndex].title = spot.name;
	}
};

const normalizeGuide = (data: any) => {
	const itinerary = (data?.itinerary || []).map((day: any, dayIndex: number) => ({
		id: day.id || '',
		dayIndex: day.dayIndex ?? dayIndex + 1,
		title: day.title || '',
		summary: day.summary || '',
		items: (day.items || []).length
			? day.items.map((item: any, itemIndex: number) => ({
				id: item.id || '',
				type: item.type || 'text',
				title: item.title || '',
				content: item.content || '',
				spotId: item.spotId,
				orderNum: item.orderNum ?? itemIndex + 1,
			}))
			: [
				{
					id: '',
					type: 'text',
					title: '',
					content: '',
					spotId: undefined,
					orderNum: 1,
				},
			],
	}));

	return {
		id: data?.id || '',
		title: data?.title || '',
		coverUrl: data?.coverUrl || data?.cover || '',
		summary: data?.summary || '',
		days: data?.days || 1,
		nights: data?.nights || 0,
		tag: data?.tag || '',
		likeCount: data?.likeCount || 0,
		viewCount: data?.viewCount || 0,
		cityId: data?.cityId,
		cityName: data?.cityName || '',
		status: data?.status ?? 1,
		itinerary: itinerary.length ? itinerary : [createEmptyDay()],
	};
};

const openDialog = async (id?: string) => {
	visible.value = true;
	currentStep.value = 0;
	form.id = '';
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 加载城市列表
	try {
		const cityRes = await fetchDestinationPage({ current: 1, size: 500 });
		cityOptions.value = cityRes.data?.records || cityRes.data || [];
	} catch {
		cityOptions.value = [];
	}

	// 加载景点列表
	try {
		const spotRes = await fetchSpotPage({ current: 1, size: 500 });
		spotOptions.value = spotRes.data?.records || spotRes.data || [];
	} catch {
		spotOptions.value = [];
	}

	if (id) {
		try {
			loading.value = true;
			const res = await getGuideObj(id);
			Object.assign(form, normalizeGuide(res.data));
		} catch (err: any) {
			useMessage().error(err.msg || '获取数据失败');
		} finally {
			loading.value = false;
		}
	} else {
		Object.assign(form, normalizeGuide({}));
	}
};

// 下一步
const nextStep = async () => {
	if (currentStep.value === 0) {
		if (!form.title || !form.cityName) {
			useMessage().warning('请填写攻略标题和选择城市');
			return;
		}
	}
	currentStep.value++;
};

// 上一步
const prevStep = () => {
	currentStep.value--;
};

const addDay = () => {
	form.itinerary.push(createEmptyDay());
};

const removeDay = (dayIndex: number) => {
	form.itinerary.splice(dayIndex, 1);
	if (!form.itinerary.length) {
		form.itinerary.push(createEmptyDay());
	}
};

const addItem = (dayIndex: number) => {
	form.itinerary[dayIndex].items.push({
		id: '',
		type: 'text',
		title: '',
		content: '',
		spotId: undefined,
		orderNum: form.itinerary[dayIndex].items.length + 1,
	});
};

const removeItem = (dayIndex: number, itemIndex: number) => {
	form.itinerary[dayIndex].items.splice(itemIndex, 1);
	if (!form.itinerary[dayIndex].items.length) {
		form.itinerary[dayIndex].items.push({
			id: '',
			type: 'text',
			title: '',
			content: '',
			spotId: undefined,
			orderNum: 1,
		});
	}
};

const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		const payload = JSON.parse(JSON.stringify(form));
		payload.itinerary = (payload.itinerary || []).map((day: any, dayIndex: number) => ({
			...day,
			dayIndex: dayIndex + 1,
			items: (day.items || []).map((item: any, itemIndex: number) => ({
				...item,
				orderNum: itemIndex + 1,
			})),
		}));
		if (!payload.id) {
			delete payload.id;
		}
		form.id ? await putGuide(payload) : await addGuide(payload);
		useMessage().success(form.id ? '编辑成功' : '新增成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg || '保存失败');
	} finally {
		loading.value = false;
	}
};

defineExpose({
	openDialog,
});
</script>

<style lang="scss">
.guide-form {
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

.guide-form.form-with-height {
	min-height: 420px;

	> div {
		min-height: 420px;
	}
}

.step-card {
	margin-bottom: 20px;

	.card-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
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

.day-card {
	border: 1px solid #ebeef5;
	border-radius: 14px;
	padding: 14px 16px;
	margin-bottom: 16px;
	background: #fbfcfe;
}

.day-card-header,
.item-card-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 12px;
}

.day-card-subtitle {
	margin-left: 12px;
	color: #8c95a1;
	font-size: 12px;
}

.item-card {
	border: 1px solid #eef1f6;
	background: #fff;
	border-radius: 12px;
	padding: 12px 14px;
	margin-bottom: 12px;
}
</style>
