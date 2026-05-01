<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑清单' : '新增清单'" width="900" draggable v-model="visible">
		<div :class="{ 'steps-wrapper': true, 'steps-clickable': form.id }" style="margin-bottom: 24px">
			<el-steps :active="currentStep" finish-status="success" align-center>
				<el-step v-for="(step, index) in stepsConfig" :key="index" :title="step.title" :description="step.description" />
			</el-steps>
			<div v-if="form.id" class="steps-click-mask">
				<div v-for="(step, index) in stepsConfig" :key="index" class="step-click-area" @click="goToStep(index)" />
			</div>
		</div>

		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="110px" v-loading="loading" class="checklist-form" :class="{ 'form-with-height': form.id }">
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
						<el-form-item label="清单标题" prop="title">
							<el-input v-model="form.title" placeholder="请输入清单标题" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="目的地" prop="destination">
									<el-input v-model="form.destination" placeholder="请输入目的地" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="状态" prop="status">
									<el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
										<el-option label="未开始" :value="0" />
										<el-option label="进行中" :value="1" />
										<el-option label="已完成" :value="2" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-row>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="出发日期" prop="departDate">
									<el-date-picker v-model="form.departDate" type="date" value-format="YYYY-MM-DD" placeholder="选择出发日期" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="返回日期" prop="returnDate">
									<el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD" placeholder="选择返回日期" style="width: 100%" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="备注" prop="remark">
							<el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
						</el-form-item>
					</el-card>
				</div>

				<!-- 步骤2: 清单项目 -->
				<div v-show="currentStep === 1">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>清单项目</span>
								<el-button type="primary" size="small" @click="addItem" style="margin-left: 12px">
									<el-icon><Plus /></el-icon> 添加项目
								</el-button>
							</div>
						</template>
						<el-table :data="form.items" border size="small" style="width: 100%">
							<el-table-column label="分类" width="140">
								<template #default="scope">
									<el-select v-model="scope.row.category" placeholder="分类" size="small">
										<el-option label="出行准备" value="prepare" />
										<el-option label="证件文件" value="document" />
										<el-option label="电子设备" value="electronics" />
										<el-option label="衣物" value="clothing" />
										<el-option label="生活用品" value="daily" />
										<el-option label="药品" value="medicine" />
										<el-option label="食品饮料" value="food" />
										<el-option label="其他" value="other" />
									</el-select>
								</template>
							</el-table-column>
							<el-table-column label="内容" min-width="200">
								<template #default="scope">
									<el-input v-model="scope.row.content" placeholder="项目内容" size="small" />
								</template>
							</el-table-column>
							<el-table-column label="已勾选" width="70" align="center">
								<template #default="scope">
									<el-checkbox v-model="scope.row.checked" :true-value="1" :false-value="0" />
								</template>
							</el-table-column>
							<el-table-column label="排序" width="80">
								<template #default="scope">
									<el-input-number v-model="scope.row.orderNum" :min="0" size="small" controls-position="right" style="width: 100%" />
								</template>
							</el-table-column>
							<el-table-column label="操作" width="60" align="center">
								<template #default="scope">
									<el-button type="danger" link size="small" @click="removeItem(scope.$index)">删除</el-button>
								</template>
							</el-table-column>
						</el-table>
					</el-card>
				</div>
			</template>

			<!-- ========== 编辑模式：2步 ========== -->
			<template v-else>
				<!-- 步骤1: 基础信息 -->
				<div v-show="currentStep === 0">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>基础信息</span>
							</div>
						</template>
						<el-form-item label="清单标题" prop="title">
							<el-input v-model="form.title" placeholder="请输入清单标题" />
						</el-form-item>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="目的地" prop="destination">
									<el-input v-model="form.destination" placeholder="请输入目的地" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="状态" prop="status">
									<el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
										<el-option label="未开始" :value="0" />
										<el-option label="进行中" :value="1" />
										<el-option label="已完成" :value="2" />
									</el-select>
								</el-form-item>
							</el-col>
						</el-row>
						<el-row :gutter="12">
							<el-col :span="12">
								<el-form-item label="出发日期" prop="departDate">
									<el-date-picker v-model="form.departDate" type="date" value-format="YYYY-MM-DD" placeholder="选择出发日期" style="width: 100%" />
								</el-form-item>
							</el-col>
							<el-col :span="12">
								<el-form-item label="返回日期" prop="returnDate">
									<el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD" placeholder="选择返回日期" style="width: 100%" />
								</el-form-item>
							</el-col>
						</el-row>
						<el-form-item label="备注" prop="remark">
							<el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
						</el-form-item>
					</el-card>
				</div>

				<!-- 步骤2: 清单项目 -->
				<div v-show="currentStep === 1">
					<el-card shadow="never" class="step-card">
						<template #header>
							<div class="card-header">
								<span>清单项目</span>
								<el-button type="primary" size="small" @click="addItem" style="margin-left: 12px">
									<el-icon><Plus /></el-icon> 添加项目
								</el-button>
							</div>
						</template>
						<el-table :data="form.items" border size="small" style="width: 100%">
							<el-table-column label="分类" width="140">
								<template #default="scope">
									<el-select v-model="scope.row.category" placeholder="分类" size="small">
										<el-option label="出行准备" value="prepare" />
										<el-option label="证件文件" value="document" />
										<el-option label="电子设备" value="electronics" />
										<el-option label="衣物" value="clothing" />
										<el-option label="生活用品" value="daily" />
										<el-option label="药品" value="medicine" />
										<el-option label="食品饮料" value="food" />
										<el-option label="其他" value="other" />
									</el-select>
								</template>
							</el-table-column>
							<el-table-column label="内容" min-width="200">
								<template #default="scope">
									<el-input v-model="scope.row.content" placeholder="项目内容" size="small" />
								</template>
							</el-table-column>
							<el-table-column label="已勾选" width="70" align="center">
								<template #default="scope">
									<el-checkbox v-model="scope.row.checked" :true-value="1" :false-value="0" />
								</template>
							</el-table-column>
							<el-table-column label="排序" width="80">
								<template #default="scope">
									<el-input-number v-model="scope.row.orderNum" :min="0" size="small" controls-position="right" style="width: 100%" />
								</template>
							</el-table-column>
							<el-table-column label="操作" width="60" align="center">
								<template #default="scope">
									<el-button type="danger" link size="small" @click="removeItem(scope.$index)">删除</el-button>
								</template>
							</el-table-column>
						</el-table>
					</el-card>
				</div>
			</template>
		</el-form>

		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
				<el-button v-if="currentStep < totalSteps - 1" type="primary" @click="nextStep">下一步</el-button>
				<el-button v-if="currentStep === totalSteps - 1" type="primary" @click="onSubmit" :disabled="loading">完成</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="ChecklistDialog" setup>
import { addChecklist, getChecklistObj, putChecklist } from '/@/api/admin/checklist';
import { useMessage } from '/@/hooks/message';
import { Plus } from '@element-plus/icons-vue';

const emit = defineEmits(['refresh']);

const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const currentStep = ref(0);

const stepsConfig = computed(() => {
	return [
		{ title: '基础信息', description: '清单基本信息' },
		{ title: '清单项目', description: '管理清单条目' },
	];
});

const totalSteps = computed(() => stepsConfig.value.length);

const goToStep = (index: number) => {
	if (!form.id) return;
	currentStep.value = index;
};

const form = reactive({
	id: '' as any,
	userId: undefined as number | undefined,
	title: '',
	destination: '',
	departDate: '',
	returnDate: '',
	remark: '',
	status: 0,
	items: [] as { id?: number; category: string; content: string; checked: number; orderNum: number }[],
});

const dataRules = ref({
	title: [{ required: true, message: '清单标题不能为空', trigger: 'blur' }],
});

const addItem = () => {
	form.items.push({ category: 'prepare', content: '', checked: 0, orderNum: form.items.length + 1 });
};

const removeItem = (index: number) => {
	form.items.splice(index, 1);
};

const nextStep = async () => {
	if (currentStep.value === 0) {
		if (!form.title) {
			useMessage().warning('请填写清单标题');
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
		userId: undefined,
		title: '',
		destination: '',
		departDate: '',
		returnDate: '',
		remark: '',
		status: 0,
		items: [],
	});

	if (isEditMode) {
		try {
			loading.value = true;
			const res = await getChecklistObj(id);
			const data = res.data || {};
			Object.assign(form, {
				id: data.id,
				userId: data.userId,
				title: data.title || '',
				destination: data.destination || '',
				departDate: data.departDate || '',
				returnDate: data.returnDate || '',
				remark: data.remark || '',
				status: data.status ?? 0,
				items: (data.items || []).map((item: any) => ({
					id: item.id,
					category: item.category || 'prepare',
					content: item.content || '',
					checked: item.checked ?? 0,
					orderNum: item.orderNum ?? 0,
				})),
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
		if (!payload.id) delete payload.id;
		form.id ? await putChecklist(payload) : await addChecklist(payload);
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
.checklist-form {
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

.checklist-form.form-with-height {
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
		display: flex;
		align-items: center;
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
