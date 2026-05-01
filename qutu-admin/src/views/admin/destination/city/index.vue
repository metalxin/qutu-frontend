<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="城市名称" prop="name">
						<el-input v-model="state.queryForm.name" placeholder="请输入城市名称" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="省份" prop="province">
						<el-input v-model="state.queryForm.province" placeholder="请输入省份" style="max-width: 160px" clearable />
					</el-form-item>
					<el-form-item label="是否热门" prop="isHot">
						<el-select v-model="state.queryForm.isHot" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="热门" :value="1" />
							<el-option label="非热门" :value="0" />
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
					<el-button v-auth="'destination_city_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增城市
					</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'destination_city_del'">
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
				<el-table-column label="封面" width="110">
					<template #default="scope">
						<el-image :src="scope.row.image || scope.row.coverUrl" fit="cover" style="width: 72px; height: 48px; border-radius: 8px" />
					</template>
				</el-table-column>
				<el-table-column label="城市名称" prop="name" min-width="120" show-overflow-tooltip />
				<el-table-column label="省份" prop="province" min-width="100" show-overflow-tooltip />
				<el-table-column label="副标题" prop="subtitle" min-width="150" show-overflow-tooltip />
				<el-table-column label="景点数" prop="spots" width="90" />
				<el-table-column label="评分" prop="rating" width="90" />
				<el-table-column label="热门" width="80">
					<template #default="scope">
						<el-tag :type="scope.row.isHot === 1 ? 'danger' : 'info'" size="small">{{ scope.row.isHot === 1 ? '热门' : '普通' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="浏览量" prop="viewCount" width="90" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'destination_city_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'destination_city_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<city-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="destinationCity" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delDestination, fetchDestinationPage } from '/@/api/admin/destination';
import { useMessage, useMessageBox } from '/@/hooks/message';

const CityDialog = defineAsyncComponent(() => import('./form.vue'));

const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	pageList: fetchDestinationPage,
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
		await useMessageBox().confirm('确认删除选中的城市吗？');
	} catch {
		return;
	}

	try {
		await Promise.all(ids.map((id) => delDestination(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};
</script>
