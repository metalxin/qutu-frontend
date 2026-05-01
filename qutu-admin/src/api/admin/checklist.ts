import request from '/@/utils/request';

// ========== 行前清单管理 ==========
// 后台管理接口路径: /checklist-admin (AdminChecklistController)

export function fetchChecklistPage(query?: Object) {
	return request({
		url: '/admin/checklist-admin/page',
		method: 'get',
		params: query,
	});
}

export function getChecklistObj(id?: string | number) {
	return request({
		url: '/admin/checklist-admin/' + id,
		method: 'get',
	});
}

export function addChecklist(obj?: Object) {
	return request({
		url: '/admin/checklist-admin',
		method: 'post',
		data: obj,
	});
}

export function putChecklist(obj?: Object) {
	return request({
		url: '/admin/checklist-admin',
		method: 'put',
		data: obj,
	});
}

export function delChecklist(id?: string | number) {
	return request({
		url: '/admin/checklist-admin/' + id,
		method: 'delete',
	});
}
