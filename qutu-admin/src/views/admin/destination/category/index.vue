<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button v-auth="'destination_category_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增分类
					</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" />
				</div>
			</el-row>
			<el-table
				:data="state.dataList"
				style="width: 100%"
				v-loading="state.loading"
				border
				row-key="id"
				default-expand-all
				:tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
				:cell-style="tableStyle.cellStyle"
				:header-cell-style="tableStyle.headerCellStyle"
			>
				<el-table-column label="分类名称" prop="name" min-width="200" show-overflow-tooltip />
				<el-table-column label="图标" prop="icon" width="100" show-overflow-tooltip />
				<el-table-column label="排序" prop="sortOrder" width="100" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'destination_category_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'destination_category_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<category-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="destinationCategory" setup>
import { delCategory, fetchCategoryTree } from '/@/api/admin/destination';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { TableStyle } from '/@/hooks/table';

const CategoryDialog = defineAsyncComponent(() => import('./form.vue'));

const dialogRef = ref();

const state = reactive({
	dataList: [] as any[],
	loading: false,
});

const tableStyle = reactive<TableStyle>({
	rowStyle: {},
	cellStyle: { padding: '6px' },
	headerCellStyle: { background: 'var(--el-table-header-bg-color)', color: 'var(--el-text-color-primary)' },
});

const getDataList = async () => {
	state.loading = true;
	try {
		const res = await fetchCategoryTree();
		state.dataList = res.data || [];
	} catch (err: any) {
		useMessage().error(err.msg || '获取数据失败');
	} finally {
		state.loading = false;
	}
};

const handleDelete = async (id: string) => {
	try {
		await useMessageBox().confirm('确认删除该分类吗？');
	} catch {
		return;
	}
	try {
		await delCategory(id);
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
