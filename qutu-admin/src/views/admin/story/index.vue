<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="内容/地点" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="心情" prop="mood">
						<el-select v-model="state.queryForm.mood" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="开心" value="开心" />
							<el-option label="平静" value="平静" />
							<el-option label="感动" value="感动" />
							<el-option label="兴奋" value="兴奋" />
							<el-option label="伤感" value="伤感" />
						</el-select>
					</el-form-item>
					<el-form-item label="天气" prop="weather">
						<el-select v-model="state.queryForm.weather" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="晴" value="晴" />
							<el-option label="阴" value="阴" />
							<el-option label="雨" value="雨" />
							<el-option label="雪" value="雪" />
							<el-option label="多云" value="多云" />
						</el-select>
					</el-form-item>
					<el-form-item label="可见性" prop="visibility">
						<el-select v-model="state.queryForm.visibility" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="仅自己" :value="1" />
							<el-option label="好友可见" :value="2" />
							<el-option label="公开" :value="3" />
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
					<el-button v-auth="'story_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增故事
					</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'story_del'">
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
						<el-image v-if="scope.row.firstImage" :src="scope.row.firstImage" fit="cover" style="width: 72px; height: 48px; border-radius: 8px" :preview-src-list="[scope.row.firstImage]" preview-teleported />
						<span v-else style="color: #ccc">无图</span>
					</template>
				</el-table-column>
				<el-table-column label="内容" prop="content" min-width="200" show-overflow-tooltip />
				<el-table-column label="地点" prop="locationName" width="130" show-overflow-tooltip />
				<el-table-column label="用户" prop="username" width="100" />
				<el-table-column label="日期" prop="recordDate" width="110" />
				<el-table-column label="心情" width="80">
					<template #default="scope">
						<el-tag v-if="scope.row.mood" size="small" effect="plain">{{ scope.row.mood }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="天气" width="80">
					<template #default="scope">
						<el-tag v-if="scope.row.weather" size="small" effect="plain" type="info">{{ scope.row.weather }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="图片数" prop="imagesCount" width="80" />
				<el-table-column label="可见性" width="90">
					<template #default="scope">
						<el-tag :type="visibilityTagType(scope.row.visibility)" size="small">{{ visibilityLabel(scope.row.visibility) }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'story_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'story_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<story-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="storyManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delStory, fetchStoryPage } from '/@/api/admin/story';
import { useMessage, useMessageBox } from '/@/hooks/message';

const StoryDialog = defineAsyncComponent(() => import('./form.vue'));

const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	pageList: fetchStoryPage,
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
		await useMessageBox().confirm('确认删除选中的故事吗？');
	} catch {
		return;
	}

	try {
		await Promise.all(ids.map((id) => delStory(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};

const visibilityLabel = (v: number) => {
	const map: Record<number, string> = { 1: '仅自己', 2: '好友可见', 3: '公开' };
	return map[v] || '未知';
};

const visibilityTagType = (v: number) => {
	const map: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'success' };
	return map[v] || 'info';
};
</script>
