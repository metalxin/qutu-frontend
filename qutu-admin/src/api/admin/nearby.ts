import request from '/@/utils/request';

// ========== 附近POI管理 ==========
// 后台管理接口路径: /nearby-admin (AdminNearbyController)

export function fetchNearbyCategoryList() {
	return request({
		url: '/admin/nearby-admin/categories',
		method: 'get',
	});
}

export function addNearbyCategory(obj?: Object) {
	return request({
		url: '/admin/nearby-admin/categories',
		method: 'post',
		data: obj,
	});
}

export function putNearbyCategory(obj?: Object) {
	return request({
		url: '/admin/nearby-admin/categories',
		method: 'put',
		data: obj,
	});
}

export function delNearbyCategory(id?: string | number) {
	return request({
		url: '/admin/nearby-admin/categories/' + id,
		method: 'delete',
	});
}

export function fetchNearbyPoiPage(query?: Object) {
	return request({
		url: '/admin/nearby-admin/pois/page',
		method: 'get',
		params: query,
	});
}

export function getNearbyPoiObj(id?: string | number) {
	return request({
		url: '/admin/nearby-admin/pois/' + id,
		method: 'get',
	});
}

export function addNearbyPoi(obj?: Object) {
	return request({
		url: '/admin/nearby-admin/pois',
		method: 'post',
		data: obj,
	});
}

export function putNearbyPoi(obj?: Object) {
	return request({
		url: '/admin/nearby-admin/pois',
		method: 'put',
		data: obj,
	});
}

export function delNearbyPoi(id?: string | number) {
	return request({
		url: '/admin/nearby-admin/pois/' + id,
		method: 'delete',
	});
}
