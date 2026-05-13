<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row class="ml10" v-show="showSearch">
				<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList" ref="queryRef">
					<el-form-item label="订单号" prop="orderNo">
						<el-input v-model="state.queryForm.orderNo" placeholder="订单号" style="max-width: 180px" clearable />
					</el-form-item>
					<el-form-item label="状态" prop="status">
						<el-select v-model="state.queryForm.status" clearable placeholder="全部" style="max-width: 120px">
							<el-option label="待支付" :value="0" />
							<el-option label="已支付" :value="1" />
							<el-option label="已取消" :value="2" />
							<el-option label="已退款" :value="3" />
						</el-select>
					</el-form-item>
					<el-form-item>
						<el-button @click="getDataList" icon="search" type="primary">查询</el-button>
						<el-button @click="resetQuery" icon="Refresh">重置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column label="订单号" prop="orderNo" min-width="180" show-overflow-tooltip />
				<el-table-column label="用户ID" prop="userId" width="80" />
				<el-table-column label="套餐ID" prop="packageId" width="80" />
				<el-table-column label="支付金额" prop="payAmount" width="100" />
				<el-table-column label="支付方式" prop="payMethod" width="80">
					<template #default="scope">
						<el-tag v-if="scope.row.payMethod" size="small">{{ scope.row.payMethod === 'wechat' ? '微信' : '支付宝' }}</el-tag>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column label="状态" width="80">
					<template #default="scope">
						<el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="VIP有效期" width="200">
					<template #default="scope">
						<span v-if="scope.row.vipStartTime">{{ scope.row.vipStartTime }} ~ {{ scope.row.vipEndTime }}</span>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column label="创建时间" prop="createdAt" width="170" />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button v-if="scope.row.status === 0" icon="CircleClose" @click="handleCancel(scope.row.id)" text type="warning" v-auth="'vip_order_edit'">取消</el-button>
						<el-button v-if="scope.row.status === 1" icon="RefreshLeft" @click="handleRefund(scope.row.id)" text type="danger" v-auth="'vip_order_refund'">退款</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
	</div>
</template>

<script lang="ts" name="vipOrderManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { cancelVipOrder, fetchVipOrderPage, refundVipOrder } from '/@/api/admin/member';
import { useMessage, useMessageBox } from '/@/hooks/message';

const queryRef = ref();
const showSearch = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchVipOrderPage, descs: ['id'] });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const resetQuery = () => { queryRef.value.resetFields(); getDataList(); };
const statusLabel = (s: number) => ({ 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退款' }[s] || '未知');
const statusTagType = (s: number) => ({ 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }[s] || 'info');

const handleCancel = async (id: string) => {
	try { await useMessageBox().confirm('确认取消此订单吗？'); } catch { return; }
	try { await cancelVipOrder(id); getDataList(false); useMessage().success('取消成功'); }
	catch (err: any) { useMessage().error(err.msg || '取消失败'); }
};

const handleRefund = async (id: string) => {
	try { await useMessageBox().confirm('确认退款此订单吗？退款后VIP权益将被收回！'); } catch { return; }
	try { await refundVipOrder(id); getDataList(false); useMessage().success('退款成功'); }
	catch (err: any) { useMessage().error(err.msg || '退款失败'); }
};
</script>
