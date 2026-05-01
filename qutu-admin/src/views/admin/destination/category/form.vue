<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑分类' : '新增分类'" width="500" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="90px" v-loading="loading">
			<el-form-item label="分类名称" prop="name">
				<el-input v-model="form.name" placeholder="请输入分类名称" />
			</el-form-item>
			<el-form-item label="父分类" prop="parentId">
				<el-tree-select
					v-model="form.parentId"
					:data="categoryTree"
					:props="{ label: 'name', value: 'id', children: 'children' }"
					placeholder="无（顶级分类）"
					clearable
					check-strictly
					style="width: 100%"
				/>
			</el-form-item>
			<el-form-item label="图标" prop="icon">
				<el-input v-model="form.icon" placeholder="请输入图标标识" />
			</el-form-item>
			<el-form-item label="排序" prop="sortOrder">
				<el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button @click="onSubmit" type="primary" :disabled="loading">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="DestinationCategoryDialog" setup>
import { addCategory, fetchCategoryTree, putCategory } from '/@/api/admin/destination';
import { useMessage } from '/@/hooks/message';

const emit = defineEmits(['refresh']);

const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const categoryTree = ref<any[]>([]);

const form = reactive({
	id: '' as any,
	parentId: undefined as number | undefined,
	name: '',
	icon: '',
	sortOrder: 0,
});

const dataRules = ref({
	name: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }],
});

const loadCategoryTree = async () => {
	try {
		const res = await fetchCategoryTree();
		categoryTree.value = res.data || [];
	} catch {
		categoryTree.value = [];
	}
};

const openDialog = async (id?: string) => {
	visible.value = true;
	form.id = '';
	form.parentId = undefined;
	form.name = '';
	form.icon = '';
	form.sortOrder = 0;
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	await loadCategoryTree();

	if (id) {
		// 从树中查找数据回显
		const findInTree = (tree: any[], targetId: string): any => {
			for (const node of tree) {
				if (String(node.id) === String(id)) return node;
				if (node.children) {
					const found = findInTree(node.children, targetId);
					if (found) return found;
				}
			}
			return null;
		};
		const data = findInTree(categoryTree.value, id);
		if (data) {
			Object.assign(form, {
				id: data.id,
				parentId: data.parentId,
				name: data.name || '',
				icon: data.icon || '',
				sortOrder: data.sortOrder ?? 0,
			});
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
		if (!payload.parentId) payload.parentId = 0;
		form.id ? await putCategory(payload) : await addCategory(payload);
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
