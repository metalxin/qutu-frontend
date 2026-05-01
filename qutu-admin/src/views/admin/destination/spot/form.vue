<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑景点' : '新增景点'" width="900" draggable v-model="visible">
		<!-- 步骤指示器 -->
		<div :class="{ 'steps-wrapper': true, 'steps-clickable': form.id }" style="margin-bottom: 24px">
			<el-steps 
				:active="currentStep" 
				finish-status="success" 
				align-center 
			>
				<el-step 
					v-for="(step, index) in stepsConfig" 
					:key="index"
					:title="step.title" 
					:description="step.description" 
				/>
			</el-steps>
			<!-- 编辑模式下的可点击遮罩层 -->
			<div v-if="form.id" class="steps-click-mask">
				<div 
					v-for="(step, index) in stepsConfig" 
					:key="index"
					class="step-click-area"
					@click="goToStep(index)"
				/>
			</div>
		</div>

		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="110px" v-loading="loading" class="spot-form" :class="{ 'form-with-height': form.id }">
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
								<el-form-item label="景点名称" prop="name">
									<el-input v-model="form.name" placeholder="请输入景点名称" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="副标题" prop="subtitle">
									<el-input v-model="form.subtitle" placeholder="请输入副标题" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="所属城市" prop="cityId">
									<el-select v-model="form.cityId" placeholder="请选择城市" filterable style="width: 100%">
										<el-option v-for="city in cityOptions" :key="city.id" :label="city.name" :value="city.id" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="景点分类" prop="categoryId">
									<el-tree-select
										v-model="form.categoryId"
										:data="categoryTree"
										:props="{ label: 'name', value: 'id', children: 'children' }"
										placeholder="请选择分类"
										clearable
										check-strictly
										style="width: 100%"
									/>
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="封面图" prop="coverUrl">
							<ImageUpload v-model="form.coverUrl" :limit="1" />
						</el-form-item>
						<el-form-item label="地址" prop="address">
							<el-input v-model="form.address" placeholder="请输入地址" />
						</el-form-item>
						<el-form-item label="坐标定位" prop="lat">
							<div style="display: flex; align-items: center; gap: 8px; width: 100%">
								<el-input v-model="spotLocationDisplay" readonly placeholder="点击右侧按钮在地图上选择位置" style="flex: 1" />
								<el-button type="primary" @click="openSpotMapPicker">
									<el-icon><Location /></el-icon>
									地图选点
								</el-button>
							</div>
							<div v-if="form.lat !== undefined && form.lng !== undefined" style="margin-top: 6px; display: flex; gap: 12px">
								<el-tag type="info" size="small">纬度: {{ form.lat }}</el-tag>
								<el-tag type="info" size="small">经度: {{ form.lng }}</el-tag>
							</div>
						</el-form-item>
						<el-form-item label="状态" prop="status">
							<el-radio-group v-model="form.status">
								<el-radio :value="1">启用</el-radio>
								<el-radio :value="0">禁用</el-radio>
							</el-radio-group>
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
						<el-form-item label="简介" prop="description">
							<el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入景点简介" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="8">
								<el-form-item label="评分" prop="rating">
									<el-input-number v-model="form.rating" :min="0" :max="5" :precision="1" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="最低价" prop="priceMin">
									<el-input-number v-model="form.priceMin" :min="0" :precision="2" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="最高价" prop="priceMax">
									<el-input-number v-model="form.priceMax" :min="0" :precision="2" style="width: 100%" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="价格描述" prop="priceText">
							<el-input v-model="form.priceText" placeholder="例如：免费 / ¥50起" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="8">
								<el-form-item label="游玩时长(分)" prop="visitDurationMinutes">
									<el-input-number v-model="form.visitDurationMinutes" :min="0" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="最佳季节" prop="bestSeason">
									<el-input v-model="form.bestSeason" placeholder="例如：春秋" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
									<el-form-item label="适合人群" prop="suitableFor">
										<el-input v-model="form.suitableFor" placeholder="例如：亲子/情侣" />
									</el-form-item>
								</el-col>
							</el-row>
						</el-card>
					</div>
			</template>

			<!-- ========== 编辑模式：4步 ========== -->
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
								<el-form-item label="景点名称" prop="name">
									<el-input v-model="form.name" placeholder="请输入景点名称" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="副标题" prop="subtitle">
									<el-input v-model="form.subtitle" placeholder="请输入副标题" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="所属城市" prop="cityId">
									<el-select v-model="form.cityId" placeholder="请选择城市" filterable style="width: 100%" disabled>
										<el-option v-for="city in cityOptions" :key="city.id" :label="city.name" :value="city.id" />
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="景点分类" prop="categoryId">
									<el-tree-select
										v-model="form.categoryId"
										:data="categoryTree"
										:props="{ label: 'name', value: 'id', children: 'children' }"
										placeholder="请选择分类"
										clearable
										check-strictly
										style="width: 100%"
									/>
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="封面图" prop="coverUrl">
							<ImageUpload v-model="form.coverUrl" :limit="1" />
						</el-form-item>
						<el-form-item label="地址" prop="address">
							<el-input v-model="form.address" placeholder="请输入地址" />
						</el-form-item>
						<el-form-item label="坐标定位" prop="lat">
							<div style="display: flex; align-items: center; gap: 8px; width: 100%">
								<el-input v-model="spotLocationDisplay" readonly placeholder="点击右侧按钮在地图上选择位置" style="flex: 1" />
								<el-button type="primary" @click="openSpotMapPicker">
									<el-icon><Location /></el-icon>
									地图选点
								</el-button>
							</div>
							<div v-if="form.lat !== undefined && form.lng !== undefined" style="margin-top: 6px; display: flex; gap: 12px">
								<el-tag type="info" size="small">纬度: {{ form.lat }}</el-tag>
								<el-tag type="info" size="small">经度: {{ form.lng }}</el-tag>
							</div>
						</el-form-item>
						<el-form-item label="状态" prop="status">
							<el-radio-group v-model="form.status">
								<el-radio :value="1">启用</el-radio>
								<el-radio :value="0">禁用</el-radio>
							</el-radio-group>
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
						<el-form-item label="简介" prop="description">
							<el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入景点简介" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="8">
								<el-form-item label="评分" prop="rating">
									<el-input-number v-model="form.rating" :min="0" :max="5" :precision="1" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="最低价" prop="priceMin">
									<el-input-number v-model="form.priceMin" :min="0" :precision="2" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="最高价" prop="priceMax">
									<el-input-number v-model="form.priceMax" :min="0" :precision="2" style="width: 100%" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="价格描述" prop="priceText">
							<el-input v-model="form.priceText" placeholder="例如：免费 / ¥50起" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="8">
								<el-form-item label="游玩时长(分)" prop="visitDurationMinutes">
									<el-input-number v-model="form.visitDurationMinutes" :min="0" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="最佳季节" prop="bestSeason">
									<el-input v-model="form.bestSeason" placeholder="例如：春秋" />
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="适合人群" prop="suitableFor">
									<el-input v-model="form.suitableFor" placeholder="例如：亲子/情侣" />
								</el-form-item>
							</el-col>
						</el-row>
					</el-card>
				</div>

				<!-- 步骤3: 旅行贴士 -->
				<div v-show="currentStep === 2">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>旅行贴士</span>
							</div>
						</template>
						<el-form-item label="旅行贴士">
							<div class="tips-editor">
								<div v-for="(tip, index) in form.tips" :key="index" class="tip-row">
									<el-input v-model="tip.icon" placeholder="图标" style="width: 80px" />
									<el-input v-model="tip.title" placeholder="标题" style="flex: 1" />
									<el-input v-model="tip.description" placeholder="说明" style="flex: 2" />
									<el-button type="danger" @click="removeTip(index)" :icon="Delete" circle />
								</div>
								<el-button type="primary" plain @click="addTip" style="margin-top: 8px">
									<el-icon><Plus /></el-icon> 添加贴士
								</el-button>
							</div>
						</el-form-item>
					</el-card>
				</div>

				<!-- 步骤4: 图库设置 -->
				<div v-show="currentStep === 3">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>图库设置</span>
							</div>
						</template>
						<el-form-item label="景点图集" prop="galleryStr">
							<ImageUpload v-model="form.galleryStr" :limit="9" />
						</el-form-item>
						<el-alert title="支持拖拽排序，最多上传9张图片" type="info" :closable="false" show-icon />
					</el-card>
				</div>

				<!-- 步骤5: 关联攻略 -->
				<div v-show="currentStep === 4">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>关联攻略</span>
							</div>
						</template>
						<el-form-item label="已关联攻略">
							<el-transfer
								v-model="selectedGuideIds"
								:data="guideTransferData"
								:titles="['可选攻略', '已关联']"
								filterable
								filter-placeholder="搜索攻略"
								style="width: 100%"
							/>
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
					<el-button v-if="currentStep < 4" type="primary" @click="nextStep">下一步</el-button>
					<el-button v-if="currentStep === 4" type="primary" @click="onSubmit" :disabled="loading">完成</el-button>
				</template>
			</div>
		</template>
	</el-dialog>
	<MapPicker ref="spotMapPickerRef" @confirm="onSpotMapConfirm" />
</template>

<script lang="ts" name="DestinationSpotDialog" setup>
import { addSpot, getSpotObj, putSpot, fetchDestinationPage, fetchCategoryTree, fetchSpotGuides, updateSpotGuides, fetchSpotImages, updateSpotImages, fetchSpotTips, updateSpotTips } from '/@/api/admin/destination';
import { fetchGuidePage } from '/@/api/admin/guide';
import { useMessage } from '/@/hooks/message';
import { Location, Plus, Delete } from '@element-plus/icons-vue';

type TransferKey = string | number;

const ImageUpload = defineAsyncComponent(() => import('/@/components/Upload/Image.vue'));
const MapPicker = defineAsyncComponent(() => import('/@/components/MapPicker/index.vue'));

const emit = defineEmits(['refresh']);

const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const currentStep = ref(0);
const spotMapPickerRef = ref();

// 步骤配置
const stepsConfig = computed(() => {
	if (form.id) {
		return [
			{ title: '基础信息', description: '景点基本信息' },
			{ title: '详细信息', description: '评分、价格等' },
			{ title: '旅行贴士', description: '编辑旅行贴士' },
			{ title: '图库设置', description: '上传景点图片' },
			{ title: '关联攻略', description: '关联相关攻略' }
		];
	}
	return [
		{ title: '基础信息', description: '景点基本信息' },
		{ title: '详细信息', description: '评分、价格等' }
	];
});

// 跳转到指定步骤（编辑模式用）
const goToStep = (index: number) => {
	if (!form.id) return;
	currentStep.value = index;
};

// 步骤点击回调
const handleStepClick = (index: number) => {
	if (!form.id) return;
	currentStep.value = index;
};

// 城市列表和分类树
const cityOptions = ref<any[]>([]);
const categoryTree = ref<any[]>([]);

// 关联攻略
const selectedGuideIds = ref<TransferKey[]>([]);
const guideTransferData = ref<Array<{ key: TransferKey; label: string; disabled: boolean }>>([]);

// 坐标显示文本
const spotLocationDisplay = computed(() => {
	if (form.lat !== undefined && form.lng !== undefined) {
		return `${form.lat}, ${form.lng}`;
	}
	return '';
});

// 打开地图选点
const openSpotMapPicker = () => {
	spotMapPickerRef.value?.openDialog(form.lng, form.lat);
};

// 地图选点回调
const onSpotMapConfirm = (data: { lng: number; lat: number; name: string; address: string }) => {
	form.lng = data.lng;
	form.lat = data.lat;
	if (!form.address && data.address) {
		form.address = data.address;
	}
};

const form = reactive({
	id: '' as any,
	cityId: undefined as TransferKey | undefined,
	categoryId: undefined as TransferKey | undefined,
	name: '',
	subtitle: '',
	coverUrl: '',
	address: '',
	lat: undefined as number | undefined,
	lng: undefined as number | undefined,
	description: '',
	rating: undefined as number | undefined,
	ratingCount: undefined as number | undefined,
	priceMin: undefined as number | undefined,
	priceMax: undefined as number | undefined,
	priceText: '',
	visitDurationMinutes: undefined as number | undefined,
	bestSeason: '',
	suitableFor: '',
	status: 1,
	gallery: [] as string[],
	galleryStr: '',
	tips: [] as Array<{ id?: number; icon: string; title: string; description: string }>,
});

const dataRules = ref({
	name: [{ required: true, message: '景点名称不能为空', trigger: 'blur' }],
	cityId: [{ required: true, message: '请选择城市', trigger: 'change' }],
	lat: [{ required: true, message: '请选择坐标位置', trigger: 'change' }],
	lng: [{ required: true, message: '请选择坐标位置', trigger: 'change' }],
});

// galleryStr (逗号分隔) ↔ gallery (数组) 双向转换
watch(
	() => form.galleryStr,
	(val: string) => {
		if (val) {
			form.gallery = val.split(',').filter(Boolean);
		} else {
			form.gallery = [];
		}
	}
);

const toNumberOrUndefined = (val: any): number | undefined => {
	if (val === null || val === undefined || val === '') return undefined;
	const num = Number(val);
	return Number.isFinite(num) ? num : undefined;
};

const normalizeImageUrls = (raw: any): string[] => {
	if (!Array.isArray(raw)) return [];
	return raw
		.map((item: any) => {
			if (typeof item === 'string') return item;
			if (item && typeof item.url === 'string') return item.url;
			return '';
		})
		.filter(Boolean);
};

// 添加贴士
const addTip = () => {
	if (!form.tips) form.tips = [];
	form.tips.push({
		icon: '📌',
		title: '',
		description: '',
	});
};

// 删除贴士
const removeTip = (index: number) => {
	if (form.tips && form.tips.length > index) {
		form.tips.splice(index, 1);
	}
};

const normalizeCategoryTree = (raw: any[]): any[] => {
	if (!Array.isArray(raw)) return [];
	return raw.map((node: any) => {
		const normalizedId = node?.id === null || node?.id === undefined ? node?.id : String(node.id);
		const normalizedParentId =
			node?.parentId === null || node?.parentId === undefined ? node?.parentId : String(node.parentId);
		return {
			...node,
			id: normalizedId ?? node?.id,
			parentId: normalizedParentId ?? node?.parentId,
			children: normalizeCategoryTree(node?.children || []),
		};
	});
};

const existsCategoryInTree = (tree: any[], categoryId: TransferKey | undefined): boolean => {
	if (!categoryId && categoryId !== 0) return false;
	const target = String(categoryId);
	for (const node of tree) {
		if (String(node?.id) === target) return true;
		if (Array.isArray(node?.children) && node.children.length) {
			if (existsCategoryInTree(node.children, categoryId)) return true;
		}
	}
	return false;
};

const openDialog = async (id?: string | number) => {
	visible.value = true;
	currentStep.value = 0;
	const isEditMode = id !== undefined && id !== null && id !== '';
	form.id = isEditMode ? (id as any) : '';
	
	// 先清空表单
	Object.assign(form, {
		id: isEditMode ? (id as any) : '',
		cityId: undefined,
		categoryId: undefined,
		name: '',
		subtitle: '',
		coverUrl: '',
		address: '',
		lat: undefined,
		lng: undefined,
		description: '',
		rating: undefined,
		ratingCount: undefined,
		priceMin: undefined,
		priceMax: undefined,
		priceText: '',
		visitDurationMinutes: undefined,
		bestSeason: '',
		suitableFor: '',
		status: 1,
		gallery: [],
		galleryStr: '',
		tips: [],
	});
	selectedGuideIds.value = [];

	// 加载城市列表和分类树
	try {
		const [cityRes, catRes] = await Promise.all([
			fetchDestinationPage({ current: 1, size: 500 }),
			fetchCategoryTree(),
		]);
		cityOptions.value = cityRes.data?.records || cityRes.data || [];
		categoryTree.value = normalizeCategoryTree(catRes.data || []);
	} catch {
		cityOptions.value = [];
		categoryTree.value = [];
	}

	// 加载攻略列表供穿梭框使用
	await loadGuideTransferData();

	if (isEditMode) {
		try {
			loading.value = true;
			// 编辑必须以详情接口为准，避免出现空白回显
			const spotRes = await getSpotObj(id);
			const spotData = spotRes.data || {};
			if (!spotData || !spotData.id) {
				throw new Error('未获取到景点详情');
			}

			let guideIds: TransferKey[] = [];
			let imageUrls: string[] = [];
			let tipsData: Array<{ id?: number; icon: string; title: string; description: string }> = [];

			try {
				const guidesRes = await fetchSpotGuides(id);
				guideIds = (guidesRes.data || []).map((gid: any) => String(gid));
			} catch (e: any) {
				console.error('获取关联攻略失败:', e);
			}

			try {
				const imagesRes = await fetchSpotImages(id);
				imageUrls = normalizeImageUrls(imagesRes.data);
			} catch (e: any) {
				console.error('获取景点图集失败:', e);
			}

			try {
				const tipsRes = await fetchSpotTips(id);
				tipsData = (tipsRes.data || []).map((t: any) => ({
					id: t.id,
					icon: t.icon || '📌',
					title: t.title || '',
					description: t.description || t.desc || '',
				}));
			} catch (e: any) {
				console.error('获取景点贴士失败:', e);
			}

			// 回显数据
			Object.assign(form, {
				id: spotData.id ?? id,
				cityId: toNumberOrUndefined(spotData.cityId),
				categoryId: spotData.categoryId === null || spotData.categoryId === undefined ? undefined : String(spotData.categoryId),
				name: spotData.name || '',
				subtitle: spotData.subtitle || '',
				coverUrl: spotData.coverUrl || spotData.cover || '',
				address: spotData.address || spotData.locationDetail || '',
				lat: toNumberOrUndefined(spotData.lat),
				lng: toNumberOrUndefined(spotData.lng),
				description: spotData.description || '',
				rating: toNumberOrUndefined(spotData.rating),
				ratingCount: toNumberOrUndefined(spotData.ratingCount),
				priceMin: toNumberOrUndefined(spotData.priceMin),
				priceMax: toNumberOrUndefined(spotData.priceMax),
				priceText: spotData.priceText || spotData.price || '',
				visitDurationMinutes: toNumberOrUndefined(spotData.visitDurationMinutes),
				bestSeason: spotData.bestSeason || '',
				suitableFor: spotData.suitableFor || '',
				status: toNumberOrUndefined(spotData.status) ?? 1,
				gallery: imageUrls,
				galleryStr: imageUrls.join(','),
				tips: tipsData,
			});

			if ((form.categoryId || form.categoryId === 0) && !existsCategoryInTree(categoryTree.value, form.categoryId)) {
				const fallbackName = spotData.categoryName || (form.categoryId ? `分类#${form.categoryId}` : '未分类');
				categoryTree.value = categoryTree.value.concat([
					{
						id: String(form.categoryId),
						name: fallbackName,
						children: [],
					},
				]);
			}

			selectedGuideIds.value = guideIds;
		} catch (err: any) {
			useMessage().error(err.msg || '获取数据失败');
		} finally {
			loading.value = false;
		}
	}
};

// 下一步
const nextStep = async () => {
	// 第一步验证必填项
	if (currentStep.value === 0) {
		// 验证基本字段
		if (!form.name || !form.cityId) {
			useMessage().warning('请填写景点名称和选择城市');
			return;
		}
		// 验证坐标
		if (form.lat === undefined || form.lng === undefined) {
			useMessage().warning('请选择地图坐标');
			return;
		}
	}
	currentStep.value++;
};

// 上一步
const prevStep = () => {
	currentStep.value--;
};

// 加载攻略穿梭框数据
const loadGuideTransferData = async () => {
	try {
		const res = await fetchGuidePage({ current: 1, size: 500 });
		const records = res.data?.records || res.data || [];
		guideTransferData.value = records.map((g: any) => ({
			key: String(g.id),
			label: g.title || `攻略#${g.id}`,
			disabled: false,
		}));
	} catch {
		guideTransferData.value = [];
	}
};

const onSubmit = async () => {
	const isEdit = form.id !== undefined && form.id !== null && form.id !== '';
	try {
		loading.value = true;
		const payload = { ...form };
		delete (payload as any).gallery;
		delete (payload as any).galleryStr;
		if (!payload.id) delete payload.id;
		const res = isEdit ? await putSpot(payload) : await addSpot(payload);
		// 保存图集
		const spotId = isEdit ? form.id : (res.data?.id || res.data);
		if (spotId) {
			await updateSpotImages(spotId, form.gallery);
			if (isEdit) {
				await updateSpotGuides(spotId, selectedGuideIds.value);
				// 保存贴士
				await updateSpotTips(spotId, form.tips);
			}
		}
		useMessage().success(isEdit ? '编辑成功' : '新增成功');
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
.spot-form {
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

// 编辑模式固定高度
.spot-form.form-with-height {
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

// 步骤容器
.steps-wrapper {
	position: relative;
}

// 编辑模式下步骤可点击
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

// 遮罩层
.steps-click-mask {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: flex;
}

// 每个步骤的可点击区域
.step-click-area {
	flex: 1;
	cursor: pointer;
	&:hover {
		background: rgba(0, 0, 0, 0.02);
	}
}

// 贴士编辑
.tips-editor {
	width: 100%;

	.tip-row {
		display: flex;
		align-items: center;
		gap: 8px;
		margin-bottom: 8px;

		&:last-child {
			margin-bottom: 0;
		}
	}
}
</style>
