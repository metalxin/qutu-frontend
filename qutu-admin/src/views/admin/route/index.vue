<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="关键词" prop="keyword">
						<el-input v-model="state.queryForm.keyword" placeholder="路线名称" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="活动类型" prop="activityType">
						<el-select v-model="state.queryForm.activityType" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="徒步" value="hiking" />
							<el-option label="骑行" value="cycling" />
							<el-option label="驾车" value="driving" />
							<el-option label="步行" value="walking" />
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
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" v-model:showSearch="showSearch" />
				</div>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column label="序号" type="index" width="70" />
				<el-table-column label="标题" prop="title" min-width="150" show-overflow-tooltip />
				<el-table-column label="活动类型" width="100">
					<template #default="scope">
						<el-tag size="small" effect="plain">{{ activityTypeMap[scope.row.activityType] || scope.row.activityType }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="距离(米)" prop="distance" width="90" />
				<el-table-column label="时长(秒)" prop="duration" width="90" />
				<el-table-column label="轨迹点数" prop="pointCount" width="90" />
				<el-table-column label="开始时间" width="170">
					<template #default="scope">
						{{ formatDateTime(scope.row.startTime) }}
					</template>
				</el-table-column>
				<el-table-column label="结束时间" width="170">
					<template #default="scope">
						{{ formatDateTime(scope.row.endTime) }}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="160" fixed="right">
					<template #default="scope">
						<el-button icon="View" @click="viewDetail(scope.row.id)" text type="primary">查看</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'route_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>

		<el-drawer title="路线详情" size="50%" v-model="detailVisible" direction="rtl" destroy-on-close>
			<el-descriptions :column="2" border v-if="detailData" class="mb15">
				<el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
				<el-descriptions-item label="活动类型">{{ activityTypeMap[detailData.activityType] || detailData.activityType }}</el-descriptions-item>
				<el-descriptions-item label="距离">{{ detailData.distance }}米</el-descriptions-item>
				<el-descriptions-item label="时长">{{ detailData.duration }}秒</el-descriptions-item>
				<el-descriptions-item label="轨迹点数">{{ detailData.pointCount }}</el-descriptions-item>
				<el-descriptions-item label="平均速度">{{ detailData.avgSpeed }}</el-descriptions-item>
				<el-descriptions-item label="开始时间">{{ formatDateTime(detailData.startTime) }}</el-descriptions-item>
				<el-descriptions-item label="结束时间">{{ formatDateTime(detailData.endTime) }}</el-descriptions-item>
			</el-descriptions>
			<h4 v-if="routePoints.length">轨迹点 ({{ routePoints.length }}个)</h4>
			<el-table :data="routePoints" max-height="400" border size="small" v-if="routePoints.length">
				<el-table-column label="纬度" prop="latitude" width="120" />
				<el-table-column label="经度" prop="longitude" width="120" />
				<el-table-column label="速度" prop="speed" width="100" />
				<el-table-column label="时间" min-width="160">
					<template #default="scope">
						{{ formatDateTime(scope.row.recordTime) }}
					</template>
				</el-table-column>
			</el-table>
		</el-drawer>
	</div>
</template>

<script lang="ts" name="routeRecordManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delRouteRecord, fetchRoutePoints, fetchRouteRecordPage, getRouteRecordObj } from '/@/api/admin/route';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { parseTime } from '/@/utils/formatTime';

const activityTypeMap: Record<string, string> = { hiking: '徒步', cycling: '骑行', driving: '驾车', walking: '步行' };

const formatDateTime = (val: string) => {
	if (!val) return '-';
	return parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}');
};

const queryRef = ref();
const showSearch = ref(true);
const detailVisible = ref(false);
const detailData = ref<any>(null);
const routePoints = ref<any[]>([]);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchRouteRecordPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); getDataList(); };

const viewDetail = async (id: string) => {
	try {
		const [recordRes, pointsRes] = await Promise.all([getRouteRecordObj(id), fetchRoutePoints(id)]);
		detailData.value = recordRes.data;
		routePoints.value = pointsRes.data || [];
		detailVisible.value = true;
	} catch (err: any) { useMessage().error(err.msg || '获取详情失败'); }
};

const handleDelete = async (id: string) => {
	try { await useMessageBox().confirm('确认删除此路线记录吗？'); } catch { return; }
	try {
		await delRouteRecord(id);
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
