import { createApp } from 'vue';
import pinia from '/@/stores/index';
import App from './App.vue';
import router from './router';
import { directive } from '/@/directive';
import { i18n } from '/@/i18n';
import other from '/@/utils/other';

import ElementPlus from 'element-plus';
import '/@/theme/tailwind.css';
import 'element-plus/dist/index.css';
import '/@/theme/index.scss';

import { ElementIcons, Pagination, RightToolbar, DictTag, UploadExcel, UploadFile, UploadImg, Editor, Tip, DelWrap } from '/@/components/index';
import { parseTime, parseDate, dateTimeStr, dateStr, timeStr } from '/@/utils/formatTime';

// 布局工具
import { Splitpanes, Pane } from 'splitpanes';
import 'splitpanes/dist/splitpanes.css';

// 加载高德地图 JS API
const AMAP_KEY = import.meta.env.VITE_AMAP_KEY || '';
const AMAP_SECURITY_CODE = import.meta.env.VITE_AMAP_SECURITY_CODE || '';
if (AMAP_SECURITY_CODE) {
	(window as any)._AMapSecurityConfig = { securityJsCode: AMAP_SECURITY_CODE };
}
if (AMAP_KEY) {
	const plugins = 'AMap.PlaceSearch,AMap.Geocoder,AMap.ToolBar,AMap.Scale,AMap.Geolocation';
	const src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&plugin=${plugins}`;
	const s = document.createElement('script');
	s.src = src;
	s.async = true;
	document.head.appendChild(s);
}

const app = createApp(App);

// 导入通用自定义组件
app.component('DictTag', DictTag);
app.component('Pagination', Pagination);
app.component('RightToolbar', RightToolbar);
app.component('uploadExcel', UploadExcel);
app.component('UploadFile', UploadFile);
app.component('UploadImg', UploadImg);
app.component('Editor', Editor);
app.component('Tip', Tip);
app.component('DelWrap', DelWrap);
// 导入布局插件
app.component('Splitpanes', Splitpanes);
app.component('Pane', Pane);
// 全局方法挂载
app.config.globalProperties.parseTime = parseTime;
app.config.globalProperties.parseDate = parseDate;
app.config.globalProperties.dateTimeStr = dateTimeStr;
app.config.globalProperties.dateStr = dateStr;
app.config.globalProperties.timeStr = timeStr;
app.config.globalProperties.baseURL = import.meta.env.VITE_API_URL;

directive(app);
other.elSvg(app);
app
	.use(pinia) // pinia 存储
	.use(router) // 路由
	.use(ElementPlus) // ElementPlus 全局引入
	.use(ElementIcons) // elementIcons 图标全局引入
	.use(i18n) // 国际化
	.mount('#app');
