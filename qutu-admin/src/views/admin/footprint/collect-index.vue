<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="名称/描述/地点" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="类型" prop="type">
						<el-select v-model="state.queryForm.type" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="拍照" value="photo" />
							<el-option label="语音" value="voice" />
							<el-option label="文本" value="text" />
							<el-option label="视频" value="video" />
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
					<el-button v-auth="'collect_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">新增采集</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'collect_del'">批量删除</el-button>
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
				<el-table-column label="名称" prop="name" min-width="150" show-overflow-tooltip />
				<el-table-column label="分组" prop="groupName" width="100" />
				<el-table-column label="类型" width="80">
					<template #default="scope">
						<el-tag size="small" effect="plain">{{ typeLabel(scope.row.type) }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="位置" prop="locationName" width="130" show-overflow-tooltip />
				<el-table-column label="用户" prop="username" width="100" />
				<el-table-column label="创建时间" prop="createdAt" width="170" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'collect_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'collect_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<collect-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="collectManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delCollect, fetchCollectPage } from '/@/api/admin/footprint';
import { useMessage, useMessageBox } from '/@/hooks/message';

const CollectDialog = defineAsyncComponent(() => import('./collect-form.vue'));

const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchCollectPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); selectObjs.value = []; getDataList(); };
const handleSelectionChange = (objs: { id: string }[]) => { selectObjs.value = objs.map(({ id }) => id); multiple.value = !objs.length; };
const typeLabel = (t: string) => ({ photo: '拍照', voice: '语音', text: '文本', video: '视频' }[t] || t);

const handleDelete = async (ids: string[]) => {
	if (!ids.length) return;
	try { await useMessageBox().confirm('确认删除选中的采集记录吗？'); } catch { return; }
	try {
		await Promise.all(ids.map((id) => delCollect(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
