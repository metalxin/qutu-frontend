<template>
	<el-dialog
		:close-on-click-modal="false"
		title="地图选点"
		width="900px"
		draggable
		v-model="visible"
		:destroy-on-close="true"
		class="map-picker-dialog"
		@opened="onDialogOpened"
	>
		<div class="map-picker">
			<!-- 搜索栏 -->
			<div class="map-picker__search">
				<el-input
					v-model="searchKeyword"
					placeholder="搜索地点、地址..."
					clearable
					:prefix-icon="Search"
					@keyup.enter="onSearch"
					@clear="onSearch"
				>
					<template #append>
						<el-button @click="onSearch" :loading="searchLoading">
							<el-icon><Search /></el-icon>
							搜索
						</el-button>
					</template>
				</el-input>
			</div>

			<div class="map-picker__body">
				<!-- 搜索结果列表 -->
				<div class="map-picker__results" v-if="searchResults.length > 0">
					<div class="map-picker__results-header">
						<span>搜索结果</span>
						<el-button link type="primary" @click="searchResults = []">
							<el-icon><Close /></el-icon>
							收起
						</el-button>
					</div>
					<div
						v-for="(item, index) in searchResults"
						:key="index"
						class="map-picker__result-item"
						:class="{ 'is-active': selectedPoi?.name === item.name && selectedPoi?.address === item.address }"
						@click="onSelectPoi(item)"
					>
						<div class="map-picker__result-index">{{ index + 1 }}</div>
						<div class="map-picker__result-info">
							<div class="map-picker__result-name">{{ item.name }}</div>
							<div class="map-picker__result-address">{{ item.address || item.district }}</div>
						</div>
					</div>
				</div>

				<!-- 地图容器 -->
				<div class="map-picker__map" id="mapPickerContainer"></div>
			</div>

			<!-- 底部信息栏 -->
			<div class="map-picker__info">
				<div class="map-picker__info-left">
					<el-icon class="map-picker__info-icon"><Location /></el-icon>
					<span v-if="selectedPoi" class="map-picker__info-text">
						{{ selectedPoi.name }}
						<span class="map-picker__info-addr" v-if="selectedPoi.address">（{{ selectedPoi.address }}）</span>
					</span>
					<span v-else class="map-picker__info-text map-picker__info-placeholder">
						点击地图或搜索选择位置
					</span>
				</div>
				<div class="map-picker__info-right" v-if="currentLng !== undefined">
					<el-tag type="info" size="small">经度: {{ currentLng }}</el-tag>
					<el-tag type="info" size="small">纬度: {{ currentLat }}</el-tag>
				</div>
			</div>
		</div>

		<template #footer>
			<span class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button type="primary" @click="onConfirm" :disabled="!selectedPoi">确定选择</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="MapPicker" setup>
import { Search, Close, Location } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

interface PoiItem {
	name: string;
	address: string;
	district?: string;
	lng: number;
	lat: number;
}

const emit = defineEmits(['confirm']);

const visible = ref(false);
const searchKeyword = ref('');
const searchLoading = ref(false);
const searchResults = ref<PoiItem[]>([]);
const selectedPoi = ref<PoiItem | null>(null);
const currentLng = ref<number | undefined>();
const currentLat = ref<number | undefined>();

let mapInstance: any = null;
let markerInstance: any = null;
let placeSearchInstance: any = null;
let geocoderInstance: any = null;

// 高德地图 Key
const AMAP_KEY = import.meta.env.VITE_AMAP_KEY || '';
const AMAP_SECURITY_CODE = import.meta.env.VITE_AMAP_SECURITY_CODE || '';

// 等待高德地图 SDK 加载完成
const waitForAMap = (): Promise<void> => {
	return new Promise((resolve, reject) => {
		if (window.AMap) {
			resolve();
			return;
		}
		let retries = 0;
		const maxRetries = 50; // 最多等待 5 秒
		const timer = setInterval(() => {
			retries++;
			if (window.AMap) {
				clearInterval(timer);
				resolve();
			} else if (retries >= maxRetries) {
				clearInterval(timer);
				reject(new Error('高德地图 SDK 加载超时'));
			}
		}, 100);
	});
};

const initMap = async () => {
	try {
		await waitForAMap();
	} catch {
		ElMessage.error('地图组件加载失败，请检查网络或配置');
		return;
	}

	mapInstance = new window.AMap.Map('mapPickerContainer', {
		zoom: 12,
		center: [116.397428, 39.90923], // 默认北京天安门
		mapStyle: 'amap://styles/fresh', // 清新风格
		viewMode: '2D',
	});

	// 添加工具条
	window.AMap.plugin(['AMap.ToolBar', 'AMap.Scale', 'AMap.Geolocation'], () => {
		mapInstance.addControl(new window.AMap.ToolBar({ position: 'RT' }));
		mapInstance.addControl(new window.AMap.Scale());
	});

	// 点击地图选点
	mapInstance.on('click', (e: any) => {
		const lng = parseFloat(e.lnglat.getLng().toFixed(6));
		const lat = parseFloat(e.lnglat.getLat().toFixed(6));
		placeMarker(lng, lat);
		reverseGeocode(lng, lat);
	});

	// 如果有初始坐标，定位过去
	if (currentLng.value !== undefined && currentLat.value !== undefined) {
		mapInstance.setCenter([currentLng.value, currentLat.value]);
		placeMarker(currentLng.value, currentLat.value);
	}
};

const placeMarker = (lng: number, lat: number) => {
	currentLng.value = lng;
	currentLat.value = lat;

	if (markerInstance) {
		markerInstance.setPosition([lng, lat]);
	} else {
		// 自定义标记样式
		markerInstance = new window.AMap.Marker({
			position: [lng, lat],
			draggable: true,
			animation: 'AMAP_ANIMATION_DROP',
			// 自定义图标
			content: `<div style="
				width: 32px; height: 42px; position: relative;
			">
				<svg viewBox="0 0 32 42" width="32" height="42">
					<path d="M16 0C7.16 0 0 7.16 0 16c0 12 16 26 16 26s16-14 16-26C32 7.16 24.84 0 16 0z" fill="#409EFF"/>
					<circle cx="16" cy="16" r="7" fill="white"/>
				</svg>
			</div>`,
			offset: new window.AMap.Pixel(-16, -42),
		});
		markerInstance.setMap(mapInstance);

		// 标记拖拽结束
		markerInstance.on('dragend', (e: any) => {
			const lng = parseFloat(e.lnglat.getLng().toFixed(6));
			const lat = parseFloat(e.lnglat.getLat().toFixed(6));
			currentLng.value = lng;
			currentLat.value = lat;
			reverseGeocode(lng, lat);
		});
	}

	mapInstance.setCenter([lng, lat]);
};

// 逆地理编码
const reverseGeocode = (lng: number, lat: number) => {
	window.AMap.plugin('AMap.Geocoder', () => {
		if (!geocoderInstance) {
			geocoderInstance = new window.AMap.Geocoder({
				radius: 1000,
				extensions: 'all',
			});
		}
		geocoderInstance.getAddress([lng, lat], (status: string, result: any) => {
			if (status === 'complete' && result.info === 'OK') {
				const regeocode = result.regeocode;
				const poi = regeocode.pois?.[0];
				selectedPoi.value = {
					name: poi?.name || regeocode.formattedAddress,
					address: regeocode.formattedAddress,
					district: regeocode.addressComponent?.district,
					lng,
					lat,
				};
			} else {
				selectedPoi.value = {
					name: `坐标点 (${lng}, ${lat})`,
					address: '',
					lng,
					lat,
				};
			}
		});
	});
};

// 搜索
const onSearch = () => {
	if (!searchKeyword.value.trim()) {
		searchResults.value = [];
		return;
	}

	searchLoading.value = true;
	window.AMap.plugin('AMap.PlaceSearch', () => {
		if (!placeSearchInstance) {
			placeSearchInstance = new window.AMap.PlaceSearch({
				pageSize: 20,
				pageIndex: 1,
				extensions: 'all',
			});
		}
		placeSearchInstance.search(searchKeyword.value, (status: string, result: any) => {
			searchLoading.value = false;
			if (status === 'complete' && result.poiList) {
				searchResults.value = result.poiList.pois.map((poi: any) => ({
					name: poi.name,
					address: poi.address,
					district: poi.pname + poi.cityname + poi.adname,
					lng: parseFloat(poi.location.getLng().toFixed(6)),
					lat: parseFloat(poi.location.getLat().toFixed(6)),
				}));
			} else {
				searchResults.value = [];
				ElMessage.warning('未找到相关地点');
			}
		});
	});
};

// 选择搜索结果
const onSelectPoi = (item: PoiItem) => {
	selectedPoi.value = { ...item };
	currentLng.value = item.lng;
	currentLat.value = item.lat;
	placeMarker(item.lng, item.lat);
	searchResults.value = [];
	searchKeyword.value = item.name;
};

// 弹窗打开后初始化地图
const onDialogOpened = () => {
	nextTick(() => {
		initMap();
		// 延迟 resize 确保地图正确显示
		setTimeout(() => {
			if (mapInstance) {
				mapInstance.setFitView();
				mapInstance.resize();
			}
		}, 200);
	});
};

// 确认选择
const onConfirm = () => {
	if (!selectedPoi.value) return;
	emit('confirm', {
		lng: currentLng.value,
		lat: currentLat.value,
		name: selectedPoi.value.name,
		address: selectedPoi.value.address,
	});
	visible.value = false;
};

// 打开弹窗（供父组件调用）
const openDialog = (lng?: number, lat?: number) => {
	visible.value = true;
	currentLng.value = lng;
	currentLat.value = lat;
	selectedPoi.value = null;
	searchKeyword.value = '';
	searchResults.value = [];

	// 如果地图已存在且有坐标，更新中心点
	if (mapInstance && lng !== undefined && lat !== undefined) {
		mapInstance.setCenter([lng, lat]);
		placeMarker(lng, lat);
	}
};

defineExpose({ openDialog });
</script>

<style lang="scss" scoped>
.map-picker {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.map-picker__search {
	:deep(.el-input-group__append) {
		background-color: var(--el-color-primary);
		color: #fff;
		border-color: var(--el-color-primary);
		.el-button {
			color: #fff;
		}
	}
}

.map-picker__body {
	position: relative;
	border-radius: 8px;
	overflow: hidden;
	border: 1px solid var(--el-border-color-lighter);
}

.map-picker__results {
	position: absolute;
	top: 0;
	left: 0;
	width: 280px;
	height: 100%;
	background: rgba(255, 255, 255, 0.96);
	backdrop-filter: blur(8px);
	z-index: 10;
	overflow-y: auto;
	border-right: 1px solid var(--el-border-color-lighter);
	box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);
}

.map-picker__results-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 14px;
	font-size: 13px;
	font-weight: 600;
	color: var(--el-text-color-primary);
	border-bottom: 1px solid var(--el-border-color-lighter);
	background: var(--el-fill-color-lighter);
}

.map-picker__result-item {
	display: flex;
	align-items: flex-start;
	gap: 10px;
	padding: 10px 14px;
	cursor: pointer;
	transition: all 0.2s;
	border-bottom: 1px solid var(--el-border-color-extra-light);

	&:hover {
		background: var(--el-color-primary-light-9);
	}

	&.is-active {
		background: var(--el-color-primary-light-8);

		.map-picker__result-index {
			background: var(--el-color-primary);
			color: #fff;
		}
	}
}

.map-picker__result-index {
	flex-shrink: 0;
	width: 22px;
	height: 22px;
	border-radius: 50%;
	background: var(--el-fill-color);
	color: var(--el-text-color-secondary);
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 12px;
	font-weight: 600;
}

.map-picker__result-info {
	flex: 1;
	min-width: 0;
}

.map-picker__result-name {
	font-size: 13px;
	font-weight: 500;
	color: var(--el-text-color-primary);
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.map-picker__result-address {
	font-size: 12px;
	color: var(--el-text-color-secondary);
	margin-top: 2px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.map-picker__map {
	width: 100%;
	height: 450px;
}

.map-picker__info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 14px;
	background: var(--el-fill-color-lighter);
	border-radius: 8px;
	border: 1px solid var(--el-border-color-lighter);
}

.map-picker__info-left {
	display: flex;
	align-items: center;
	gap: 8px;
	min-width: 0;
	flex: 1;
}

.map-picker__info-icon {
	color: var(--el-color-primary);
	font-size: 18px;
	flex-shrink: 0;
}

.map-picker__info-text {
	font-size: 13px;
	color: var(--el-text-color-primary);
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.map-picker__info-addr {
	color: var(--el-text-color-secondary);
	font-size: 12px;
}

.map-picker__info-placeholder {
	color: var(--el-text-color-placeholder);
}

.map-picker__info-right {
	display: flex;
	gap: 6px;
	flex-shrink: 0;
	margin-left: 12px;
}
</style>
