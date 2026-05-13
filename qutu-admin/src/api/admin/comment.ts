import request from '/@/utils/request';

// ========== 评论管理 ==========
// 后台管理接口路径: /comment-admin (AdminCommentController in qutu-destination)

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
