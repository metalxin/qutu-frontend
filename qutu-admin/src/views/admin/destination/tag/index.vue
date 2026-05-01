<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button v-auth="'destination_tag_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增标签
					</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" />
				</div>
			</el-row>
			<el-table
				:data="dataList"
				style="width: 100%"
				v-loading="loading"
				border
				:cell-style="tableStyle.cellStyle"
				:header-cell-style="tableStyle.headerCellStyle"
			>
				<el-table-column label="标签ID" prop="id" width="200" />
				<el-table-column label="标签名称" prop="name" min-width="200" show-overflow-tooltip />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id, scope.row)" text type="primary" v-auth="'destination_tag_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'destination_tag_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<tag-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="destinationTag" setup>
import { delTag, fetchTagList } from '/@/api/admin/destination';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { TableStyle } from '/@/hooks/table';

const TagDialog = defineAsyncComponent(() => import('./form.vue'));

const dialogRef = ref();
const dataList = ref<any[]>([]);
const loading = ref(false);

const tableStyle = reactive<TableStyle>({
	rowStyle: {},
	cellStyle: { padding: '6px' },
	headerCellStyle: { background: 'var(--el-table-header-bg-color)', color: 'var(--el-text-color-primary)' },
});

const getDataList = async () => {
	loading.value = true;
	try {
		const res = await fetchTagList();
		dataList.value = res.data || [];
	} catch (err: any) {
		useMessage().error(err.msg || '获取数据失败');
	} finally {
		loading.value = false;
	}
};

const handleDelete = async (id: string) => {
	try {
		await useMessageBox().confirm('确认删除该标签吗？');
	} catch {
		return;
	}
	try {
		await delTag(id);
		getDataList();
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};

onMounted(() => {
	getDataList();
});
</script>
