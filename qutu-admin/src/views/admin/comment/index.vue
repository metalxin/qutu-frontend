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
					<el-form-item label="审核状态" prop="status">
						<el-select v-model="state.queryForm.status" placeholder="全部" style="max-width: 140px" clearable>
							<el-option label="待审核" :value="0" />
							<el-option label="已通过" :value="1" />
							<el-option label="已拒绝" :value="2" />
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
					<el-button type="success" icon="Check" @click="handleBatchAudit(1)" :disabled="selectedIds.length === 0" v-auth="'spot_comment_audit'">批量通过</el-button>
					<el-button type="warning" icon="CloseBold" @click="handleBatchAudit(2)" :disabled="selectedIds.length === 0" v-auth="'spot_comment_audit'">批量拒绝</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle" @selection-change="handleSelectionChange">
				<el-table-column type="selection" width="50" />
				<el-table-column label="序号" type="index" width="60" />
				<el-table-column label="景点" prop="spotName" min-width="120" show-overflow-tooltip />
				<el-table-column label="用户名" prop="userName" min-width="100" show-overflow-tooltip />
				<el-table-column label="评论内容" prop="content" min-width="250" show-overflow-tooltip />
				<el-table-column label="类型" width="90">
					<template #default="scope">
						<el-tag v-if="scope.row.parentId" type="warning" size="small">回复</el-tag>
						<el-tag v-else type="success" size="small">主评论</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="审核状态" width="100">
					<template #default="scope">
						<el-tag v-if="scope.row.status === 0" type="info" size="small">待审核</el-tag>
						<el-tag v-else-if="scope.row.status === 1" type="success" size="small">已通过</el-tag>
						<el-tag v-else-if="scope.row.status === 2" type="danger" size="small">已拒绝</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="点赞" prop="likeCount" width="70" />
				<el-table-column label="回复" prop="replyCount" width="70" />
				<el-table-column label="时间" width="170">
					<template #default="scope">
						{{ formatDate(scope.row.createdAt) }}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="200" fixed="right">
					<template #default="scope">
						<el-button v-if="scope.row.status === 0" icon="Check" @click="handleAudit(scope.row.id, 1)" text type="success" v-auth="'spot_comment_audit'">通过</el-button>
						<el-button v-if="scope.row.status === 0" icon="CloseBold" @click="handleAudit(scope.row.id, 2)" text type="warning" v-auth="'spot_comment_audit'">拒绝</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="danger" v-auth="'spot_comment_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
	</div>
</template>

<script lang="ts" name="commentManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delComment, fetchCommentPage, auditComment, batchAuditComment } from '/@/api/admin/comment';
import { useMessage, useMessageBox } from '/@/hooks/message';

const queryRef = ref();
const showSearch = ref(true);
const selectedIds = ref<number[]>([]);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchCommentPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); getDataList(); };

const handleSelectionChange = (rows: any[]) => {
	selectedIds.value = rows.map((r: any) => r.id);
};

const formatDate = (val: string) => {
	if (!val) return '';
	const d = new Date(val);
	if (isNaN(d.getTime())) return val;
	const pad = (n: number) => String(n).padStart(2, '0');
	return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const handleAudit = async (id: number, status: number) => {
	const label = status === 1 ? '通过' : '拒绝';
	try { await useMessageBox().confirm(`确认${label}此评论吗？`); } catch { return; }
	try {
		await auditComment(id, status);
		getDataList(false);
		useMessage().success(`${label}成功`);
	} catch (err: any) { useMessage().error(err.msg || `${label}失败`); }
};

const handleBatchAudit = async (status: number) => {
	const label = status === 1 ? '通过' : '拒绝';
	try { await useMessageBox().confirm(`确认批量${label}选中的 ${selectedIds.value.length} 条评论吗？`); } catch { return; }
	try {
		await batchAuditComment(selectedIds.value, status);
		getDataList(false);
		selectedIds.value = [];
		useMessage().success(`批量${label}成功`);
	} catch (err: any) { useMessage().error(err.msg || `批量${label}失败`); }
};

const handleDelete = async (id: string) => {
	try { await useMessageBox().confirm('确认删除此评论吗？删除后不可恢复'); } catch { return; }
	try {
		await delComment(id);
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
