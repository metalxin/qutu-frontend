<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="标题/目的地" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="状态" prop="status">
						<el-select v-model="state.queryForm.status" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="未开始" :value="0" />
							<el-option label="进行中" :value="1" />
							<el-option label="已完成" :value="2" />
						</el-select>
					</el-form-item>
					<el-form-item>
						<el-button @click="getDataList" icon="search" type="primary">查询</el-button>
						<el-button @click="resetQuery" icon="Refresh">重置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button v-auth="'checklist_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增清单
					</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'checklist_del'">
						批量删除
					</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table
				:data="state.dataList"
				@selection-change="handleSelectionChange"
				style="width: 100%"
				v-loading="state.loading"
				border
				:cell-style="tableStyle.cellStyle"
				:header-cell-style="tableStyle.headerCellStyle"
			>
				<el-table-column align="center" type="selection" width="40" />
				<el-table-column label="标题" prop="title" min-width="160" show-overflow-tooltip />
				<el-table-column label="目的地" prop="destination" width="130" show-overflow-tooltip />
				<el-table-column label="用户" prop="username" width="100" />
				<el-table-column label="出发日期" prop="departDate" width="110" />
				<el-table-column label="返回日期" prop="returnDate" width="110" />
				<el-table-column label="状态" width="90">
					<template #default="scope">
						<el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="进度" width="120">
					<template #default="scope">
						<el-progress :percentage="scope.row.itemsCount ? Math.round((scope.row.checkedCount / scope.row.itemsCount) * 100) : 0" :stroke-width="8" />
					</template>
				</el-table-column>
				<el-table-column label="完成/总数" width="100">
					<template #default="scope">
						{{ scope.row.checkedCount || 0 }}/{{ scope.row.itemsCount || 0 }}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'checklist_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'checklist_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<checklist-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="checklistManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delChecklist, fetchChecklistPage } from '/@/api/admin/checklist';
import { useMessage, useMessageBox } from '/@/hooks/message';

const ChecklistDialog = defineAsyncComponent(() => import('./form.vue'));

const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	pageList: fetchChecklistPage,
	descs: ['id'],
});

const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => {
	queryRef.value.resetFields();
	selectObjs.value = [];
	getDataList();
};

const handleSelectionChange = (objs: { id: string }[]) => {
	selectObjs.value = objs.map(({ id }) => id);
	multiple.value = !objs.length;
};

const handleDelete = async (ids: string[]) => {
	if (!ids.length) return;
	try {
		await useMessageBox().confirm('确认删除选中的清单吗？');
	} catch {
		return;
	}

	try {
		await Promise.all(ids.map((id) => delChecklist(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};

const statusLabel = (v: number) => {
	const map: Record<number, string> = { 0: '未开始', 1: '进行中', 2: '已完成' };
	return map[v] || '未知';
};

const statusTagType = (v: number) => {
	const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success' };
	return map[v] || 'info';
};
</script>
