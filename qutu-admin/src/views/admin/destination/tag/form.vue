<template>
	<el-dialog :close-on-click-modal="false" :title="form.id ? '编辑标签' : '新增标签'" width="400" draggable v-model="visible">
		<el-form :model="form" :rules="dataRules" ref="dataFormRef" label-width="80px" v-loading="loading">
			<el-form-item label="标签名称" prop="name">
				<el-input v-model="form.name" placeholder="请输入标签名称" />
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

<script lang="ts" name="DestinationTagDialog" setup>
import { addTag, putTag } from '/@/api/admin/destination';
import { useMessage } from '/@/hooks/message';

const emit = defineEmits(['refresh']);

const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);

const form = reactive({
	id: '' as any,
	name: '',
});

const dataRules = ref({
	name: [{ required: true, message: '标签名称不能为空', trigger: 'blur' }],
});

const openDialog = (id?: string, row?: any) => {
	visible.value = true;
	form.id = '';
	form.name = '';
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	if (id && row) {
		form.id = row.id;
		form.name = row.name || '';
	}
};

const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		const payload = { ...form };
		if (!payload.id) delete payload.id;
		form.id ? await putTag(payload) : await addTag(payload);
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
