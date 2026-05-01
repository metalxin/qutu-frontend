<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="标题/摘要/标签" style="max-width: 220px" clearable />
					</el-form-item>
					<el-form-item label="城市" prop="cityName">
						<el-input v-model="state.queryForm.cityName" placeholder="请输入城市名" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="标签" prop="tag">
						<el-input v-model="state.queryForm.tag" placeholder="请输入标签" style="max-width: 160px" clearable />
					</el-form-item>
					<el-form-item label="状态" prop="status">
						<el-select v-model="state.queryForm.status" clearable placeholder="全部" style="max-width: 120px">
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
					<el-button v-auth="'guide_add'" @click="guideDialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">
						新增攻略
					</el-button>
					<el-button plain :disabled="multiple" @click="handleDelete(selectObjs)" class="ml10" icon="Delete" type="primary" v-auth="'guide_del'">
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
				<el-table-column label="标题" prop="title" min-width="220" show-overflow-tooltip />
				<el-table-column label="城市" prop="cityName" min-width="120" show-overflow-tooltip />
				<el-table-column label="标签" prop="tag" min-width="120" show-overflow-tooltip />
				<el-table-column label="行程" width="120">
					<template #default="scope">
						<el-tag type="success">{{ scope.row.days }}天{{ scope.row.nights }}晚</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="点赞" prop="likeCount" width="90" />
				<el-table-column label="浏览" prop="viewCount" width="100" />
				<el-table-column label="状态" width="90">
					<template #default="scope">
						<el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '上架' : '下架' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="250" fixed="right">
					<template #default="scope">
						<el-button icon="View" @click="handlePreview(scope.row.id)" text type="primary">查看</el-button>
						<el-button icon="edit-pen" @click="guideDialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'guide_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete([scope.row.id])" text type="primary" v-auth="'guide_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<guide-dialog @refresh="getDataList()" ref="guideDialogRef" />
		<el-drawer v-model="previewVisible" size="70%" :title="previewTitle" destroy-on-close>
			<div v-if="previewData" class="guide-preview">
				<!-- 封面区域 -->
				<div class="preview-hero" :style="{ backgroundImage: `url(${previewData.coverUrl || ''})` }">
					<div class="preview-hero-mask"></div>
					<div class="preview-hero-meta">
						<h2>{{ previewData.title }}</h2>
						<p>{{ previewData.summary }}</p>
					</div>
				</div>

				<!-- 基础信息卡片 -->
				<el-card shadow="never" class="detail-card">
					<template #header>
						<div class="card-header"><span>基础信息</span></div>
					</template>
					<el-descriptions :column="3" border>
						<el-descriptions-item label="攻略标题" :span="3">{{ previewData.title || '-' }}</el-descriptions-item>
						<el-descriptions-item label="所属城市">{{ previewData.cityName || '-' }}</el-descriptions-item>
						<el-descriptions-item label="标签">{{ previewData.tag || '-' }}</el-descriptions-item>
						<el-descriptions-item label="状态">
							<el-tag :type="previewData.status === 1 ? 'success' : 'info'" size="small">{{ previewData.status === 1 ? '上架' : '下架' }}</el-tag>
						</el-descriptions-item>
						<el-descriptions-item label="行程天数">{{ previewData.days }}天{{ previewData.nights }}晚</el-descriptions-item>
						<el-descriptions-item label="点赞数">{{ previewData.likeCount ?? 0 }}</el-descriptions-item>
						<el-descriptions-item label="浏览量">{{ previewData.viewCount ?? 0 }}</el-descriptions-item>
						<el-descriptions-item label="摘要" :span="3">{{ previewData.summary || '-' }}</el-descriptions-item>
						<el-descriptions-item label="创建时间">{{ previewData.createdAt || '-' }}</el-descriptions-item>
						<el-descriptions-item label="更新时间" :span="2">{{ previewData.updatedAt || '-' }}</el-descriptions-item>
					</el-descriptions>
				</el-card>

				<!-- 行程详情 -->
				<el-card shadow="never" class="detail-card">
					<template #header>
						<div class="card-header"><span>行程详情</span></div>
					</template>
					<el-tabs v-if="(previewData.itinerary || []).length">
						<el-tab-pane v-for="(day, index) in previewData.itinerary || []" :key="day.id || index" :label="day.title || `Day${day.dayIndex}`">
							<div class="day-detail-section">
								<div class="day-detail-summary">{{ day.summary || '暂无摘要' }}</div>
								<div v-for="(item, itemIndex) in day.items || []" :key="item.id || itemIndex" class="guide-item">
									<div class="guide-item-title">
										<el-tag size="small" :type="item.type === 'spot' ? 'success' : 'info'" class="mr8">{{ item.type === 'spot' ? '景点' : '文本' }}</el-tag>
										<strong>{{ item.title || '未命名' }}</strong>
									</div>
									<div v-if="item.type === 'spot'" class="guide-item-line">关联景点ID：{{ item.spotId || '-' }}</div>
									<div v-if="item.content" class="guide-item-line">{{ item.content }}</div>
								</div>
								<el-empty v-if="!(day.items || []).length" description="暂无条目" :image-size="60" />
							</div>
						</el-tab-pane>
					</el-tabs>
					<el-empty v-else description="暂无行程数据" :image-size="80" />
				</el-card>
			</div>
		</el-drawer>
	</div>
</template>

<script lang="ts" name="adminGuide" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delGuide, fetchGuidePage, getGuideObj } from '/@/api/admin/guide';
import { useMessage, useMessageBox } from '/@/hooks/message';

const GuideDialog = defineAsyncComponent(() => import('./form.vue'));

const guideDialogRef = ref();
const queryRef = ref();
const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);
const previewVisible = ref(false);
const previewTitle = ref('攻略详情');
const previewData = ref<any>(null);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	pageList: fetchGuidePage,
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

const handlePreview = async (id: string) => {
	const res = await getGuideObj(id);
	previewData.value = res.data;
	previewTitle.value = res.data?.title || '攻略详情';
	previewVisible.value = true;
};

const handleDelete = async (ids: string[]) => {
	if (!ids.length) return;
	try {
		await useMessageBox().confirm('确认删除选中的攻略吗？');
	} catch {
		return;
	}

	try {
		await Promise.all(ids.map((id) => delGuide(id)));
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg || '删除失败');
	}
};
</script>

<style scoped>
.guide-preview {
	display: flex;
	flex-direction: column;
	gap: 16px;
}
.preview-hero {
	position: relative;
	min-height: 240px;
	border-radius: 18px;
	overflow: hidden;
	background-size: cover;
	background-position: center;
	background-color: #f4f6f8;
}
.preview-hero-mask {
	position: absolute;
	inset: 0;
	background: linear-gradient(180deg, rgba(18, 24, 32, 0.12), rgba(18, 24, 32, 0.55));
}
.preview-hero-meta {
	position: absolute;
	left: 20px;
	right: 20px;
	bottom: 20px;
	color: #fff;
}
.preview-hero-meta h2 {
	margin: 0 0 8px;
	font-size: 26px;
}
.preview-hero-meta p {
	margin: 0;
	font-size: 14px;
	opacity: 0.95;
}
.detail-card {
	border-radius: 14px;
}
.detail-card .card-header {
	font-size: 16px;
	font-weight: 600;
	color: #303133;
}
.day-detail-section {
	padding: 4px 0;
}
.day-detail-summary {
	color: #5c677d;
	margin-bottom: 16px;
	line-height: 1.7;
	padding: 12px 16px;
	background: #f8fafc;
	border-radius: 10px;
}
.guide-item {
	background: #f8fafc;
	border: 1px solid #edf2f7;
	border-radius: 14px;
	padding: 14px 16px;
	margin-bottom: 12px;
}
.guide-item-title {
	display: flex;
	align-items: center;
	margin-bottom: 8px;
}
.guide-item-line {
	color: #5c677d;
	line-height: 1.7;
	padding-left: 4px;
}
.mr8 {
	margin-right: 8px;
}
</style>
