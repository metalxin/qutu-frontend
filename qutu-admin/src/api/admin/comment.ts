import request from '/@/utils/request';

// ========== 评论管理 ==========

export function fetchCommentPage(query?: Object) {
	return request({
		url: '/admin/comment-admin/comments/page',
		method: 'get',
		params: query,
	});
}

export function delComment(id?: string | number) {
	return request({
		url: '/admin/comment-admin/comments/' + id,
		method: 'delete',
	});
}

export function auditComment(id: string | number, status: number) {
	return request({
		url: `/admin/comment-admin/comments/${id}/audit`,
		method: 'put',
		params: { status },
	});
}

export function batchAuditComment(ids: number[], status: number) {
	return request({
		url: '/admin/comment-admin/comments/batch-audit',
		method: 'put',
		data: ids,
		params: { status },
	});
}

// ========== 收藏管理 ==========

export function fetchFavoritePage(query?: Object) {
	return request({
		url: '/admin/comment-admin/favorites/page',
		method: 'get',
		params: query,
	});
}

export function delFavorite(spotId?: string | number, userId?: string | number) {
	return request({
		url: '/admin/comment-admin/favorites/' + spotId + '/' + userId,
		method: 'delete',
	});
}
