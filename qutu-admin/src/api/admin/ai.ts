import request from '/@/utils/request';

// ========== AI对话管理 ==========
// 后台管理接口路径: /ai-admin (AdminAiController)

export function fetchAiConversationPage(query?: Object) {
	return request({
		url: '/admin/ai-admin/conversations/page',
		method: 'get',
		params: query,
	});
}

export function getAiConversationObj(id?: string | number) {
	return request({
		url: '/admin/ai-admin/conversations/' + id,
		method: 'get',
	});
}

export function delAiConversation(id?: string | number) {
	return request({
		url: '/admin/ai-admin/conversations/' + id,
		method: 'delete',
	});
}

export function fetchAiMessagePage(query?: Object) {
	return request({
		url: '/admin/ai-admin/messages/page',
		method: 'get',
		params: query,
	});
}

export function delAiMessage(id?: string | number) {
	return request({
		url: '/admin/ai-admin/messages/' + id,
		method: 'delete',
	});
}
