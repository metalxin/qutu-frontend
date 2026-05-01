/**
 * API模块统一导出
 */

// 请求工具
export * from './request'

// 各模块API
export * as destinationApi from './modules/destination'
export * as guideApi from './modules/guide'
export * as nearbyApi from './modules/nearby'
export * as footprintApi from './modules/footprint'
export * as checklistApi from './modules/checklist'
export * as planningApi from './modules/planning'
export * as storyApi from './modules/story'
export * as userApi from './modules/user'

// 也可以直接导入具体方法
export {
  getDestinations,
  getSpotDetail,
  getDestinationSpots,
  getSpotComments,
  postComment,
  replyComment,
  likeComment,
  favoriteSpot,
  unfavoriteSpot,
  getRegionGroups,
  getHotCities
} from './modules/destination'

export {
  getGuideCategories,
  getGuideTags,
  getGuideList,
  getGuidesPage,
  getGuideDetail,
  getRelatedGuides,
  likeGuide,
  collectGuide,
  toggleGuideCollect,
  generateGuideTrip
} from './modules/guide'

export type {
  GuideDetail,
  GuideDay,
  GuideDayItem,
  GuideListItem,
  GuideTag,
  RelatedGuide
} from './modules/guide'

export {
  getNearbyCategories,
  getNearbyPois,
  getPoiDetail,
  getCityList,
  getMapMarkers
} from './modules/nearby'

export {
  getFootprintRecords,
  getFootprintStats,
  getFootprintActivities,
  getProvinces,
  getCountries,
  lightUpProvince,
  lightUpCity
} from './modules/footprint'

export {
  getChecklists,
  getChecklistDetail,
  createChecklist,
  updateChecklist,
  deleteChecklist,
  toggleChecklistItem,
  addChecklistItem,
  deleteChecklistItem,
  getItemCategories,
  getRecommendedItems,
  getExpenseCategories,
  categoryLabel,
  CATEGORY_MAP,
  getExpenses,
  addExpense,
  updateExpense,
  deleteExpense,
  getTotalExpense,
  updateBudget,
  EXPENSE_CATEGORY_MAP,
  EXPENSE_CATEGORY_REVERSE_MAP
} from './modules/checklist'

export {
  getPlanningSettings,
  generatePlanningRoute,
  getPlanningCities,
  generateAIRoute,
  getSavedRoutes,
  saveRoute,
  getPreferenceOptions,
  getTransportModes,
  getSpotInfo,
  getCitySpots
} from './modules/planning'

// 导出规划模块类型
export type {
  SpotInfo,
  RouteSpot,
  DaySchedule,
  AIRoute,
  PreferenceOption,
  TransportMode
} from './modules/planning'

export {
  getDiaryList,
  getDiaryDetail,
  createDiary,
  updateDiary,
  deleteDiary,
  getStoryTemplates,
  getStoryTags,
  uploadDiaryImage,
  getDiaryByDate,
  getDiaryStats
} from './modules/story'

export {
  getUserInfo,
  updateUserInfo,
  login,
  logout,
  getVipBenefits,
  getVipPackages,
  getUserSettings
} from './modules/user'

export {
  getCollections,
  getCollectionGroups,
  getCollectionDetail,
  getCollectionStats,
  createCollection,
  updateCollection,
  deleteCollection,
  createCollectionGroup,
  deleteCollectionGroup,
  recognizeImage
} from './modules/collect'

export * as collectApi from './modules/collect'
