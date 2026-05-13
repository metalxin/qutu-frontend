import request from '/@/utils/request';

// ========== VIP套餐管理 ==========
// 后台管理接口路径: /member-admin (AdminMemberController)

export function fetchVipPackagePage(query?: Object) {
	return request({
		url: '/admin/member-admin/packages/page',
		method: 'get',
		params: query,
	});
}

export function getVipPackageObj(id?: string | number) {
	return request({
		url: '/admin/member-admin/packages/' + id,
		method: 'get',
	});
}

export function addVipPackage(obj?: Object) {
	return request({
		url: '/admin/member-admin/packages',
		method: 'post',
		data: obj,
	});
}

export function putVipPackage(obj?: Object) {
	return request({
		url: '/admin/member-admin/packages',
		method: 'put',
		data: obj,
	});
}

export function delVipPackage(id?: string | number) {
	return request({
		url: '/admin/member-admin/packages/' + id,
		method: 'delete',
	});
}

// ========== VIP订单管理 ==========

export function fetchVipOrderPage(query?: Object) {
	return request({
		url: '/admin/member-admin/orders/page',
		method: 'get',
		params: query,
	});
}

export function getVipOrderObj(id?: string | number) {
	return request({
		url: '/admin/member-admin/orders/' + id,
		method: 'get',
	});
}

export function cancelVipOrder(id?: string | number) {
	return request({
		url: '/admin/member-admin/orders/' + id + '/cancel',
		method: 'put',
	});
}

export function refundVipOrder(id?: string | number) {
	return request({
		url: '/admin/member-admin/orders/' + id + '/refund',
		method: 'put',
	});
}

// ========== 会员权益管理 ==========

export function fetchMemberBenefits() {
	return request({
		url: '/admin/member-admin/benefits',
		method: 'get',
	});
}

export function addMemberBenefit(obj?: Object) {
	return request({
		url: '/admin/member-admin/benefits',
		method: 'post',
		data: obj,
	});
}

export function putMemberBenefit(obj?: Object) {
	return request({
		url: '/admin/member-admin/benefits',
		method: 'put',
		data: obj,
	});
}

export function delMemberBenefit(id?: string | number) {
	return request({
		url: '/admin/member-admin/benefits/' + id,
		method: 'delete',
	});
}
