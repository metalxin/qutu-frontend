import request from '/@/utils/request';

// ========== 足迹管理 ==========
// 后台管理接口路径: /footprint-admin (AdminFootprintController)

export function fetchFootprintPage(query?: Object) {
	return request({
		url: '/admin/footprint-admin/footprints/page',
		method: 'get',
		params: query,
	});
}

export function getFootprintObj(id?: string | number) {
	return request({
		url: '/admin/footprint-admin/footprints/' + id,
		method: 'get',
	});
}

export function addFootprint(obj?: Object) {
	return request({
		url: '/admin/footprint-admin/footprints',
		method: 'post',
		data: obj,
	});
}

export function putFootprint(obj?: Object) {
	return request({
		url: '/admin/footprint-admin/footprints',
		method: 'put',
		data: obj,
	});
}

export function delFootprint(id?: string | number) {
	return request({
		url: '/admin/footprint-admin/footprints/' + id,
		method: 'delete',
	});
}

export function fetchFootprintStats(userId?: number) {
	return request({
		url: '/admin/footprint-admin/stats',
		method: 'get',
		params: { userId },
	});
}

// ========== 采集管理 ==========

export function fetchCollectPage(query?: Object) {
	return request({
		url: '/admin/footprint-admin/collects/page',
		method: 'get',
		params: query,
	});
}

export function getCollectObj(id?: string | number) {
	return request({
		url: '/admin/footprint-admin/collects/' + id,
		method: 'get',
	});
}

export function addCollect(obj?: Object) {
	return request({
		url: '/admin/footprint-admin/collects',
		method: 'post',
		data: obj,
	});
}

export function putCollect(obj?: Object) {
	return request({
		url: '/admin/footprint-admin/collects',
		method: 'put',
		data: obj,
	});
}

export function delCollect(id?: string | number) {
	return request({
		url: '/admin/footprint-admin/collects/' + id,
		method: 'delete',
	});
}
