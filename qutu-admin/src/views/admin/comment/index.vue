<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="景点名称" prop="spotName">
						<el-input v-model="state.queryForm.spotName" placeholder="景点名称" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="评论内容" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item>
						<el-button @click="getDataList" icon="search" type="primary">查询</el-button>
						<el-button @click="resetQuery" icon="Refresh">重置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-row>
				<div class="mb8" style="width: 100%">
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column label="景点" prop="spotName" min-width="120" show-overflow-tooltip />
				<el-table-column label="用户名" prop="userName" min-width="100" show-overflow-tooltip />
				<el-table-column label="评论内容" prop="content" min-width="250" show-overflow-tooltip />
				<el-table-column label="类型" width="90">
					<template #default="scope">
						<el-tag v-if="scope.row.parentId" type="warning" size="small">回复</el-tag>
						<el-tag v-else type="success" size="small">主评论</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="点赞" prop="likeCount" width="80" />
				<el-table-column label="回复" prop="replyCount" width="80" />
				<el-table-column label="时间" width="170">
					<template #default="scope">
						{{ formatDate(scope.row.createdAt) }}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="100" fixed="right">
					<template #default="scope">
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'spot_comment_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
	</div>
</template>

<script lang="ts" name="commentManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delComment, fetchCommentPage } from '/@/api/admin/comment';
import { useMessage, useMessageBox } from '/@/hooks/message';

const queryRef = ref();
const showSearch = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchCommentPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); getDataList(); };

const formatDate = (val: string) => {
	if (!val) return '';
	const d = new Date(val);
	if (isNaN(d.getTime())) return val;
	const pad = (n: number) => String(n).padStart(2, '0');
	return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const handleDelete = async (id: string) => {
	try { await useMessageBox().confirm('确认删除此评论吗？'); } catch { return; }
	try {
		await delComment(id);
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
