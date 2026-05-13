<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button v-auth="'vip_package_add'" @click="dialogRef.openDialog()" class="ml10" icon="folder-add" type="primary">新增套餐</el-button>
					<right-toolbar @queryTable="getDataList" class="ml10" style="float: right; margin-right: 20px" />
				</div>
			</el-row>
			<el-table :data="state.dataList" style="width: 100%" v-loading="state.loading" border :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle">
				<el-table-column label="ID" prop="id" width="60" />
				<el-table-column label="套餐名称" prop="name" min-width="120" />
				<el-table-column label="月数" prop="months" width="70" />
				<el-table-column label="原价(分)" prop="originalPrice" width="100" />
				<el-table-column label="现价(分)" prop="currentPrice" width="100" />
				<el-table-column label="折扣标签" prop="discountLabel" width="100" />
				<el-table-column label="热门" width="70">
					<template #default="scope">
						<el-tag :type="scope.row.isHot === 1 ? 'danger' : 'info'" size="small">{{ scope.row.isHot === 1 ? '是' : '否' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="排序" prop="sortOrder" width="70" />
				<el-table-column label="状态" width="80">
					<template #default="scope">
						<el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '上架' : '下架' }}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button icon="edit-pen" @click="dialogRef.openDialog(scope.row.id)" text type="primary" v-auth="'vip_package_edit'">编辑</el-button>
						<el-button icon="delete" @click="handleDelete(scope.row.id)" text type="primary" v-auth="'vip_package_del'">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle" v-bind="state.pagination" />
		</div>
		<vip-package-dialog @refresh="getDataList()" ref="dialogRef" />
	</div>
</template>

<script lang="ts" name="vipPackageManage" setup>
import { BasicTableProps, useTable } from '/@/hooks/table';
import { delVipPackage, fetchVipPackagePage } from '/@/api/admin/member';
import { useMessage, useMessageBox } from '/@/hooks/message';

const VipPackageDialog = defineAsyncComponent(() => import('./package-form.vue'));
const dialogRef = ref();

const state: BasicTableProps = reactive<BasicTableProps>({ queryForm: {}, pageList: fetchVipPackagePage });
const { getDataList, currentChangeHandle, sizeChangeHandle, tableStyle } = useTable(state);

const handleDelete = async (id: string) => {
	try { await useMessageBox().confirm('确认删除此套餐吗？'); } catch { return; }
	try {
		await delVipPackage(id);
		getDataList(false);
		useMessage().success('删除成功');
	} catch (err: any) { useMessage().error(err.msg || '删除失败'); }
};
</script>
