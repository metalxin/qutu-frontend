/**
 * API模块统一导出
 */

export * from './request'

export * as destinationApi from './modules/destination'
export * as guideApi from './modules/guide'
export * as nearbyApi from './modules/nearby'
export * as footprintApi from './modules/footprint'
export * as checklistApi from './modules/checklist'
export * as storyApi from './modules/story'
export * as userApi from './modules/user'

export {
  getDestinations,
  getSpotDetail,
  getDestinationSpots,
  getSpotComments,
  postComment,
  replyComment,
  likeComment,
  unlikeComment,
  deleteComment,
  favoriteSpot,
  unfavoriteSpot,
  getUserFavoriteSpots,
  getRegionGroups,
  getHotCities,
  searchDestinations
} from './modules/destination'

export type {
  FavoriteSpotItem,
  Comment,
  Reply
} from './modules/destination'

export {
  getGuideCategories,
  getGuideTags,
  getGuideList,
  getGuidesPage,
  getGuideDetail,
  getRelatedGuides,
  getInspirations,
  searchInspirations,
  likeGuide,
  collectGuide,
  uncollectGuide,
  toggleGuideCollect,
  checkGuideFavorite,
  getUserFavoriteGuides,
  generateGuideTrip
} from './modules/guide'

export type {
  GuideDetail,
  GuideDay,
  GuideDayItem,
  GuideListItem,
  GuideTag,
  RelatedGuide,
  InspirationItem,
  FavoriteGuideItem,
  FavoriteGuidePage
} from './modules/guide'

export {
  getNearbyCategories,
  getNearbyPois,
  getPoiDetail,
  favoritePoi,
  unfavoritePoi,
  checkinPoi,
  checkFavorite,
  getCityList,
  getMapMarkers,
  getCityListLegacy
} from './modules/nearby'

export type {
  NearbyCategory,
  NearbyPoi,
  NearbyPoiDetail,
  CityInfo
} from './modules/nearby'

export {
  getFootprintRecords,
  getFootprintStats,
  createFootprintRecord,
  deleteFootprintRecord,
  getUserProvinces,
  lightUpProvince,
  getUserCountries,
  lightUpCountry
} from './modules/footprint'

export type {
  FootprintRecord,
  FootprintRecordPage,
  FootprintStats,
  FootprintProvince,
  FootprintCountry,
  FootprintCreateDTO
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
  EXPENSE_CATEGORY_REVERSE_MAP,
  smartImport,
  generateShareCode,
  importByShareCode
} from './modules/checklist'

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
  chatWithAI,
  chatWithHistory,
  getOrCreateConversation,
  getUserConversations,
  getConversationMessages
} from './modules/service'

export type {
  ChatMessage,
  AiConversation,
  AiMessage,
  ChatResponse
} from './modules/service'
