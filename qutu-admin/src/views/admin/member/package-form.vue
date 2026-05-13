<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑套餐' : '新增套餐'" width="550" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="100px" v-loading="loading">
			<el-form-item label="套餐名称" prop="name">
				<el-input v-model="form.name" placeholder="如：月度会员" />
			</el-form-item>
			<el-row :gutter="12">
				<el-col :span="8">
					<el-form-item label="月数" prop="months">
						<el-input-number v-model="form.months" :min="1" :controls="false" style="width: 100%" />
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="原价(分)" prop="originalPrice">
						<el-input-number v-model="form.originalPrice" :min="0" :controls="false" style="width: 100%" />
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="现价(分)" prop="currentPrice">
						<el-input-number v-model="form.currentPrice" :min="0" :controls="false" style="width: 100%" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="12">
				<el-col :span="8">
					<el-form-item label="折扣标签" prop="discountLabel">
						<el-input v-model="form.discountLabel" placeholder="如8折" />
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="是否热门" prop="isHot">
						<el-select v-model="form.isHot" style="width: 100%">
							<el-option label="否" :value="0" />
							<el-option label="是" :value="1" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="排序" prop="sortOrder">
						<el-input-number v-model="form.sortOrder" :controls="false" style="width: 100%" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="描述" prop="description">
				<el-input v-model="form.description" type="textarea" :rows="3" placeholder="套餐描述" />
			</el-form-item>
			<el-form-item label="状态" prop="status">
				<el-select v-model="form.status" style="width: 100%">
					<el-option label="上架" :value="1" />
					<el-option label="下架" :value="0" />
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="onSubmit" :disabled="loading">确定</el-button>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="VipPackageDialog" setup>
import { addVipPackage, getVipPackageObj, putVipPackage } from '/@/api/admin/member';
import { useMessage } from '/@/hooks/message';

const emit = defineEmits(['refresh']);
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);

const form = reactive({
	id: '' as any, name: '', description: '', months: 1, originalPrice: 0, currentPrice: 0,
	discountLabel: '', isHot: 0, sortOrder: 0, status: 1,
});

const dataRules = ref({
	name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
	months: [{ required: true, message: '月数不能为空', trigger: 'change' }],
	currentPrice: [{ required: true, message: '现价不能为空', trigger: 'change' }],
});

const openDialog = async (id?: string) => {
	visible.value = true;
	Object.assign(form, { id: id || '', name: '', description: '', months: 1, originalPrice: 0, currentPrice: 0, discountLabel: '', isHot: 0, sortOrder: 0, status: 1 });
	if (id) {
		try {
			loading.value = true;
			const res = await getVipPackageObj(id);
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
		form.id ? await putVipPackage(payload) : await addVipPackage(payload);
		useMessage().success(form.id ? '编辑成功' : '新增成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) { useMessage().error(err.msg || '保存失败'); }
	finally { loading.value = false; }
};

defineExpose({ openDialog });
</script>
