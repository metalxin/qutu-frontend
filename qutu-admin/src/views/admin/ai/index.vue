<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="标题/消息" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="类型" prop="type">
						<el-select v-model="state.queryForm.type" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="聊天" value="chat" />
							<el-option label="规划" value="planning" />
							<el-option label="攻略" value="guide" />
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
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'ai_conversation_del'">批量删除</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" @selection-change="handleSelectionChange" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column align="center" type="selection" width="40" />
				<el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
				<el-table-column label="用户" prop="username" width="100" />
				<el-table-column label="消息数" prop="messageCount" width="80" />
				<el-table-column label="最后消息" prop="lastMessage" min-width="200" show-overflow-tooltip />
				<el-table-column label="类型" width="80">
					<template #default="scope">
						<el-tag size="small" effect="plain">{{ ({ chat: '聊天', planning: '规划', guide: '攻略' })[scope.row.type] || scope.row.type }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="状态" width="80">
					<template #default="scope">
						<el-tag :type="scope.row.status === 0 ? 'success' : 'info'" size="small">{{ scope.row.status === 0 ? '进行中' : '已结束' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="更新时间" prop="updatedAt" width="170" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="View" @click="viewDetail(scope.row.id)" text type="primary">查看</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'ai_conversation_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>

		<!-- 消息详情弹窗 -->
		<el-dialog title="对话详情" width="800" v-model="detailVisible">
			<div v-if="detailData">
				<el-descriptions :column="3" border class="mb15">
					<el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
					<el-descriptions-item label="用户">{{ detailData.username }}</el-descriptions-item>
					<el-descriptions-item label="类型">{{ detailData.type }}</el-descriptions-item>
				</el-descriptions>
				<el-timeline>
					<el-timeline-item v-for="msg in detailData.messages" :key="msg.id" :timestamp="msg.createdAt" placement="top" :type="msg.role === 'user' ? 'primary' : 'success'">
						<el-card shadow="never">
							<template #header><el-tag size="small" :type="msg.role === 'user' ? '' : 'success'">{{ msg.role === 'user' ? '用户' : 'AI助手' }}</el-tag></template>
							<div style="white-space: pre-wrap; max-height: 200px; overflow-y: auto">{{ msg.content }}</div>
						</el-card>
					</el-timeline-item>
				</el-timeline>
			</div>
		</el-dialog>
	</div>
</template>

<script lang="ts" name="aiConversationManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delAiConversation, fetchAiConversationPage, getAiConversationObj } from '/@/api/admin/ai';
import { useMessage, useMessageBox } from '/@/hooks/message';

const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);
const detailVisible = ref(false);
const detailData = ref<any>(null);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchAiConversationPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); selectObjs.value = []; getDataList(); };
const handleSelectionChange = (objs: { id: string }[]) => { selectObjs.value = objs.map(({ id }) => id); multiple.value = !objs.length; };

const viewDetail = async (id: string) => {
	try {
		const res = await getAiConversationObj(id);
		detailData.value = res.data;
		detailVisible.value = true;
	} catch (err: any) { useMessage().error(err.msg || '获取详情失败'); }
};

const handleDelete = async (ids: string[]) => {
	if (!ids.length) return;
	try { await useMessageBox().confirm('确认删除选中的对话吗？删除后消息也会一并删除！'); } catch { return; }
	try {
		await Promise.all(ids.map((id) => delAiConversation(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
