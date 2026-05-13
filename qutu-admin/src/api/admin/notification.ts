import request from '/@/utils/request';

// ========== 消息通知管理 ==========
// 后台管理接口路径: /message-admin (AdminNotificationController)

export function fetchNotificationPage(query?: Object) {
	return request({
		url: '/admin/message-admin/notifications/page',
		method: 'get',
		params: query,
	});
}

export function getNotificationObj(id?: string | number) {
	return request({
		url: '/admin/message-admin/notifications/' + id,
		method: 'get',
	});
}

export function addNotification(obj?: Object) {
	return request({
		url: '/admin/message-admin/notifications',
		method: 'post',
		data: obj,
	});
}

export function putNotification(obj?: Object) {
	return request({
		url: '/admin/message-admin/notifications',
		method: 'put',
		data: obj,
	});
}

export function delNotification(id?: string | number) {
	return request({
		url: '/admin/message-admin/notifications/' + id,
		method: 'delete',
	});
}

export function publishNotification(id?: string | number) {
	return request({
		url: '/admin/message-admin/notifications/' + id + '/publish',
		method: 'put',
	});
}

export function unpublishNotification(id?: string | number) {
	return request({
		url: '/admin/message-admin/notifications/' + id + '/unpublish',
		method: 'put',
	});
}
