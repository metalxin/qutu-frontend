import request from '/@/utils/request';

// ========== 路线记录管理 ==========
// 后台管理接口路径: /route-admin (AdminRouteRecordController in qutu-planning)

export function fetchRouteRecordPage(query?: Object) {
	return request({
		url: '/admin/route-admin/routes/page',
		method: 'get',
		params: query,
	});
}

export function getRouteRecordObj(id?: string | number) {
	return request({
		url: '/admin/route-admin/routes/' + id,
		method: 'get',
	});
}

export function delRouteRecord(id?: string | number) {
	return request({
		url: '/admin/route-admin/routes/' + id,
		method: 'delete',
	});
}

export function fetchRoutePoints(routeId?: string | number) {
	return request({
		url: '/admin/route-admin/routes/' + routeId + '/points',
		method: 'get',
	});
}
