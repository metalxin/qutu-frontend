import request from '/@/utils/request';

export function fetchGuidePage(query?: Object) {
	return request({
		url: '/admin/guide-admin/page',
		method: 'get',
		params: query,
	});
}

export function getGuideObj(id?: string | number) {
	return request({
		url: '/admin/guide-admin/' + id,
		method: 'get',
	});
}

export function addGuide(obj?: Object) {
	return request({
		url: '/admin/guide-admin',
		method: 'post',
		data: obj,
	});
}

export function putGuide(obj?: Object) {
	return request({
		url: '/admin/guide-admin',
		method: 'put',
		data: obj,
	});
}

export function delGuide(id?: string | number) {
	return request({
		url: '/admin/guide-admin/' + id,
		method: 'delete',
	});
}
