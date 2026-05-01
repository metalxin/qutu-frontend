<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="景点名称" prop="name">
						<el-input v-model="state.queryForm.name" placeholder="请输入景点名称" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="城市" prop="cityId">
						<el-select v-model="state.queryForm.cityId" clearable placeholder="请选择城市" style="max-width: 160px">
							<el-option v-for="city in cityOptions" :key="city.id" :label="city.name" :value="city.id" />
						</el-select>
					</el-form-item>
					<el-form-item label="状态" prop="status">
						<el-select v-model="state.queryForm.status" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="启用" :value="1" />
							<el-option label="禁用" :value="0" />
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
					<el-button v-auth="'destination_spot_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增景点
					</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'destination_spot_del'">
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
						<el-image :src="scope.row.coverUrl" fit="cover" style="width: 72px; height: 48px; border-radius: 8px" />
					</template>
				</el-table-column>
				<el-table-column label="景点名称" prop="name" min-width="160" show-overflow-tooltip />
				<el-table-column label="副标题" prop="subtitle" min-width="120" show-overflow-tooltip />
				<el-table-column label="评分" prop="rating" width="80" />
				<el-table-column label="价格" prop="priceText" width="120" show-overflow-tooltip />
				<el-table-column label="地址" prop="address" min-width="150" show-overflow-tooltip />
				<el-table-column label="状态" width="80">
					<template #default="scope">
						<el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="浏览量" prop="viewCount" width="90" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'destination_spot_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'destination_spot_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<spot-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="destinationSpot" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delSpot, fetchDestinationPage, fetchSpotPage } from '/@/api/admin/destination';
import { useMessage, useMessageBox } from '/@/hooks/message';

const SpotDialog = defineAsyncComponent(() => import('./form.vue'));

const cityOptions = ref<any[]>([]);

const loadCityOptions = async () => {
	const res: any = await fetchDestinationPage({ page: 1, size: 200 });
	cityOptions.value = res.data?.records || res.data || [];
};

onMounted(() => {
	loadCityOptions();
});

const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	pageList: fetchSpotPage,
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
		await useMessageBox().confirm('确认删除选中的景点吗？');
	} catch {
		return;
	}

	try {
		await Promise.all(ids.map((id) => delSpot(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};
</script>
