<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="标题/内容" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="类型" prop="type">
						<el-select v-model="state.queryForm.type" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="系统" value="system" />
							<el-option label="活动" value="activity" />
							<el-option label="更新" value="update" />
							<el-option label="推广" value="promotion" />
						</el-select>
					</el-form-item>
					<el-form-item label="状态" prop="publishStatus">
						<el-select v-model="state.queryForm.publishStatus" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="草稿" :value="0" />
							<el-option label="已发布" :value="1" />
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
					<el-button v-auth="'notification_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">新增通知</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
				<el-table-column label="类型" width="80">
					<template #default="scope">
						<el-tag size="small" effect="plain">{{ ({ system: '系统', activity: '活动', update: '更新', promotion: '推广' })[scope.row.type] || scope.row.type }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="优先级" width="80">
					<template #default="scope">
						<el-tag :type="({ 0: 'info', 1: 'warning', 2: 'danger' })[scope.row.priority] || 'info'" size="small">{{ ({ 0: '普通', 1: '重要', 2: '紧急' })[scope.row.priority] || '普通' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="状态" width="80">
					<template #default="scope">
						<el-tag :type="scope.row.publishStatus === 1 ? 'success' : 'info'" size="small">{{ scope.row.publishStatus === 1 ? '已发布' : '草稿' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="目标用户" width="100">
					<template #default="scope">
						<el-tag v-if="scope.row.targetUserId" size="small" type="warning">{{ scope.row.targetUserId }}</el-tag>
						<span v-else style="color: #67c23a">全员</span>
					</template>
				</el-table-column>
				<el-table-column label="发布时间" prop="publishAt" width="170" />
				<el-table-column label="操作" width="220" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'notification_edit'">编辑</el-button>
						<el-button v-if="scope.row.publishStatus === 0" icon="Promotion" @click="handlePublish(scope.row.id)" text type="success" v-auth="'notification_edit'">发布</el-button>
						<el-button v-else icon="CircleClose" @click="handleUnpublish(scope.row.id)" text type="warning" v-auth="'notification_edit'">取消发布</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'notification_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<notification-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="notificationManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delNotification, fetchNotificationPage, publishNotification, unpublishNotification } from '/@/api/admin/notification';
import { useMessage, useMessageBox } from '/@/hooks/message';

const NotificationDialog = defineAsyncComponent(() => import('./form.vue'));
const dialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchNotificationPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); getDataList(); };

const handlePublish = async (id: string) => {
	try {
		await useMessageBox().confirm('确认发布此通知吗？');
		await publishNotification(id);
		useMessage().success('发布成功');
		getDataList(false);
	} catch (err: any) { if (err !== 'cancel') useMessage().error(err.msg || '发布失败'); }
};

const handleUnpublish = async (id: string) => {
	try {
		await useMessageBox().confirm('确认取消发布吗？');
		await unpublishNotification(id);
		useMessage().success('取消发布成功');
		getDataList(false);
	} catch (err: any) { if (err !== 'cancel') useMessage().error(err.msg || '操作失败'); }
};

const handleDelete = async (id: string) => {
	try {
		await useMessageBox().confirm('确认删除此通知吗？');
		await delNotification(id);
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { if (err !== 'cancel') useMessage().error(err.msg || '删除失败'); }
};
</script>
