import request from '/@/utils/request';

// ========== 旅行故事管理 ==========
// 后台管理接口路径: /story-admin (AdminStoryController)

export function fetchStoryPage(query?: Object) {
	return request({
		url: '/admin/story-admin/page',
		method: 'get',
		params: query,
	});
}

export function getStoryObj(id?: string | number) {
	return request({
		url: '/admin/story-admin/' + id,
		method: 'get',
	});
}

export function addStory(obj?: Object) {
	return request({
		url: '/admin/story-admin',
		method: 'post',
		data: obj,
	});
}

export function putStory(obj?: Object) {
	return request({
		url: '/admin/story-admin',
		method: 'put',
		data: obj,
	});
}

export function delStory(id?: string | number) {
	return request({
		url: '/admin/story-admin/' + id,
		method: 'delete',
	});
}
