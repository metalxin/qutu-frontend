import { defineStore } from 'pinia';

/**
 * 修改配置时：
 * 1、需要每次都清理 `window.localStorage` 浏览器永久缓存
 * 2、或者点击布局配置最底部 `一键恢复默认` 按钮即可看到效果
 */
export const useThemeConfig = defineStore('themeConfig', {
	state: (): ThemeConfigState => ({
		themeConfig: { "isDrawer": false, "primary": "#0c8145", "isIsDark": false, "topBar": "#0c650d", "topBarColor": "#ffffff", "isTopBarColorGradual": true, "menuBar": "#ffffff", "menuBarColor": "#000000", "menuBarActiveColor": "rgba(242, 243, 245, 1)", "isMenuBarColorGradual": true, "columnsMenuBar": "#116909", "columnsMenuBarColor": "#ffffff", "isColumnsMenuBarColorGradual": true, "isColumnsMenuHoverPreload": true, "isCollapse": false, "isUniqueOpened": true, "isFixedHeader": true, "isFixedHeaderChange": false, "isClassicSplitMenu": false, "isLockScreen": false, "lockScreenTime": 30, "isShowLogo": true, "isShowLogoChange": false, "isBreadcrumb": true, "isTagsview": true, "isBreadcrumbIcon": true, "isTagsviewIcon": true, "isCacheTagsView": true, "isSortableTagsView": true, "isShareTagsView": true, "isFooter": true, "isGrayscale": false, "isInvert": false, "isWartermark": true, "wartermarkText": "Pig", "tagsStyle": "tags-style-four", "animation": "slide-right", "columnsAsideStyle": "columns-round", "columnsAsideLayout": "columns-vertical", "layout": "defaults", "isRequestRoutes": true, "globalTitle": "趣途后台管理系统", "globalViceTitle": "趣途后台管理系统", "globalViceTitleMsg": "专注、免费、开源、维护、解疑", "globalI18n": "zh-cn", "globalComponentSize": "large", "footerAuthor": "©2025 qutu.com All Rights Reserved" }
	}),
	actions: {
		setThemeConfig(data: ThemeConfigState) {
			this.themeConfig = data.themeConfig;
		},
	},
});
