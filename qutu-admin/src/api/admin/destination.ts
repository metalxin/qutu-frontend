import request from '/@/utils/request';

// ========== 目的地城市 ==========

export function fetchDestinationPage(query?: Object) {
	return request({
		url: '/admin/destinations/page',
		method: 'get',
		params: query,
	});
}

export function getDestinationObj(id?: string | number) {
	return request({
		url: '/admin/destinations/' + id,
		method: 'get',
	});
}

export function addDestination(obj?: Object) {
	return request({
		url: '/admin/destinations',
		method: 'post',
		data: obj,
	});
}

export function putDestination(obj?: Object) {
	return request({
		url: '/admin/destinations',
		method: 'put',
		data: obj,
	});
}

export function delDestination(id?: string | number) {
	return request({
		url: '/admin/destinations/' + id,
		method: 'delete',
	});
}

// ========== 景点分类 ==========

export function fetchCategoryTree() {
	return request({
		url: '/admin/categories/tree',
		method: 'get',
	});
}

export function fetchCategoryRoots() {
	return request({
		url: '/admin/categories/roots',
		method: 'get',
	});
}

export function fetchCategoryChildren(parentId: string | number) {
	return request({
		url: '/admin/categories/' + parentId + '/children',
		method: 'get',
	});
}

export function addCategory(obj?: Object) {
	return request({
		url: '/admin/categories',
		method: 'post',
		data: obj,
	});
}

export function putCategory(obj?: Object) {
	return request({
		url: '/admin/categories',
		method: 'put',
		data: obj,
	});
}

export function delCategory(id?: string | number) {
	return request({
		url: '/admin/categories/' + id,
		method: 'delete',
	});
}

// ========== 景点 ==========

export function fetchSpotPage(query?: Object) {
	return request({
		url: '/admin/spots/page',
		method: 'get',
		params: query,
	});
}

export function getSpotObj(id?: string | number) {
	return request({
		url: '/admin/spots/' + id + '/edit',
		method: 'get',
	});
}

export function addSpot(obj?: Object) {
	return request({
		url: '/admin/spots',
		method: 'post',
		data: obj,
	});
}

export function putSpot(obj?: Object) {
	return request({
		url: '/admin/spots',
		method: 'put',
		data: obj,
	});
}

export function delSpot(id?: string | number) {
	return request({
		url: '/admin/spots/' + id,
		method: 'delete',
	});
}

// ========== 标签 ==========

export function fetchTagList() {
	return request({
		url: '/admin/tags',
		method: 'get',
	});
}

export function addTag(obj?: Object) {
	return request({
		url: '/admin/tags',
		method: 'post',
		data: obj,
	});
}

export function putTag(obj?: Object) {
	return request({
		url: '/admin/tags',
		method: 'put',
		data: obj,
	});
}

export function delTag(id?: string | number) {
	return request({
		url: '/admin/tags/' + id,
		method: 'delete',
	});
}

// ========== 景点关联攻略 ==========

export function fetchSpotGuides(spotId: string | number) {
	return request({
		url: '/admin/spots/' + spotId + '/guide-ids',
		method: 'get',
	});
}

export function updateSpotGuides(spotId: string | number, guideIds: Array<string | number>) {
	return request({
		url: '/admin/spots/' + spotId + '/guides',
		method: 'put',
		data: guideIds,
	});
}

// ========== 景点图集 ==========

export function fetchSpotImages(spotId: string | number) {
	return request({
		url: '/admin/spots/' + spotId + '/images',
		method: 'get',
	});
}

export function updateSpotImages(spotId: string | number, urls: string[]) {
	return request({
		url: '/admin/spots/' + spotId + '/images',
		method: 'put',
		data: urls,
	});
}

// ========== 景点贴士 ==========

export function fetchSpotTips(spotId: string | number) {
	return request({
		url: '/admin/spots/' + spotId + '/tips',
		method: 'get',
	});
}

export function updateSpotTips(spotId: string | number, tips: Array<{ id?: number; icon: string; title: string; description: string }>) {
	const payload = (tips || []).map((t) => ({ icon: t.icon, title: t.title, desc: t.description }));
	return request({
		url: '/admin/spots/' + spotId + '/tips',
		method: 'put',
		data: payload,
	});
}
