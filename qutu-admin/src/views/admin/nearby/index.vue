<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="名称/地址" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="分类" prop="categoryId">
						<el-select v-model="state.queryForm.categoryId" clearable placeholder="全部" style="max-width: 140px">
							<el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
						</el-select>
					</el-form-item>
					<el-form-item label="状态" prop="status">
						<el-select v-model="state.queryForm.status" clearable placeholder="全部" style="max-width: 100px">
							<el-option label="上架" :value="1" />
							<el-option label="下架" :value="0" />
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
					<el-button v-auth="'nearby_poi_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">新增POI</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column label="名称" prop="name" min-width="150" show-overflow-tooltip />
				<el-table-column label="城市" prop="cityName" min-width="100" show-overflow-tooltip />
				<el-table-column label="评分" prop="rating" width="70" />
				<el-table-column label="价格" prop="priceText" min-width="100" show-overflow-tooltip />
				<el-table-column label="地址" prop="address" min-width="180" show-overflow-tooltip />
				<el-table-column label="浏览量" prop="viewCount" width="80" />
				<el-table-column label="收藏数" prop="favoriteCount" width="80" />
				<el-table-column label="打卡数" prop="checkinCount" width="80" />
				<el-table-column label="状态" width="90">
					<template #default="scope">
						<el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '上架' : '下架' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'nearby_poi_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'nearby_poi_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<nearby-poi-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="nearbyPoiManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delNearbyPoi, fetchNearbyCategoryList, fetchNearbyPoiPage } from '/@/api/admin/nearby';
import { useMessage, useMessageBox } from '/@/hooks/message';

const NearbyPoiDialog = defineAsyncComponent(() => import('./nearby-form.vue'));
const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const categories = ref<any[]>([]);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchNearbyPoiPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); getDataList(); };

const loadCategories = async () => {
	try { const res = await fetchNearbyCategoryList(); categories.value = res.data || []; }
	catch { categories.value = []; }
};

onMounted(() => { loadCategories(); });

const handleDelete = async (id: string) => {
	try { await useMessageBox().confirm('确认删除此POI吗？关联的图片、提示、打卡记录也会被删除！'); } catch { return; }
	try {
		await delNearbyPoi(id);
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
