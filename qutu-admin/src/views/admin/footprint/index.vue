<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="景点/城市/省份" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="类型" prop="type">
						<el-select v-model="state.queryForm.type" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="国内" :value="1" />
							<el-option label="国外" :value="2" />
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
					<el-button v-auth="'footprint_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">新增足迹</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'footprint_del'">批量删除</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" @selection-change="handleSelectionChange" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column align="center" type="selection" width="40" />
				<el-table-column label="图片" width="90">
					<template #default="scope">
						<el-image v-if="scope.row.imageUrl" :src="scope.row.imageUrl" fit="cover" style="width: 60px; height: 40px; border-radius: 6px" :preview-src-list="[scope.row.imageUrl]" preview-teleported />
						<span v-else style="color: #ccc">无图</span>
					</template>
				</el-table-column>
				<el-table-column label="景点" prop="spotName" min-width="140" show-overflow-tooltip />
				<el-table-column label="城市" prop="cityName" width="100" show-overflow-tooltip />
				<el-table-column label="省份" prop="provinceName" width="90" />
				<el-table-column label="国家" prop="countryName" width="90" />
				<el-table-column label="用户" prop="username" width="100" />
				<el-table-column label="类型" width="80">
					<template #default="scope">
						<el-tag :type="scope.row.type === 1 ? '' : 'warning'" size="small">{{ scope.row.type === 1 ? '国内' : '国外' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="创建时间" prop="createdAt" width="170" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'footprint_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'footprint_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<footprint-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="footprintManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delFootprint, fetchFootprintPage } from '/@/api/admin/footprint';
import { useMessage, useMessageBox } from '/@/hooks/message';

const FootprintDialog = defineAsyncComponent(() => import('./form.vue'));

const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	pageList: fetchFootprintPage,
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
		await useMessageBox().confirm('确认删除选中的足迹记录吗？');
	} catch {
		return;
	}
	try {
		await Promise.all(ids.map((id) => delFootprint(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};
</script>
