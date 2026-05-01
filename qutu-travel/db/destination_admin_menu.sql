-- 目的地后台菜单与按钮权限
-- 执行前请确认 menu_id 未冲突（本脚本使用 9500x 号段）

DELETE FROM `sys_menu` WHERE `menu_id` IN (
  95000, 95010, 95011, 95012, 95013,
  95020, 95021, 95022, 95023,
  95030, 95031, 95032, 95033,
  95040, 95041, 95042, 95043
);

INSERT INTO `sys_menu` (`menu_id`, `name`, `en_name`, `permission`, `path`, `parent_id`, `icon`, `visible`, `sort_order`, `keep_alive`, `embedded`, `menu_type`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES
(95000, '目的地管理', 'destination', NULL, '/destination', -1, 'iconfont icon-dingwei', '1', 10, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),

(95010, '目的地城市', 'destination-city', NULL, '/admin/destination/city/index', 95000, 'iconfont icon-dingwei', '1', 1, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(95011, '城市新增', NULL, 'destination_city_add', NULL, 95010, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95012, '城市修改', NULL, 'destination_city_edit', NULL, 95010, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95013, '城市删除', NULL, 'destination_city_del', NULL, 95010, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),

(95020, '景点分类', 'destination-category', NULL, '/admin/destination/category/index', 95000, 'iconfont icon-fenlei', '1', 2, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(95021, '分类新增', NULL, 'destination_category_add', NULL, 95020, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95022, '分类修改', NULL, 'destination_category_edit', NULL, 95020, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95023, '分类删除', NULL, 'destination_category_del', NULL, 95020, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),

(95030, '景点管理', 'destination-spot', NULL, '/admin/destination/spot/index', 95000, 'iconfont icon-fengjing', '1', 3, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(95031, '景点新增', NULL, 'destination_spot_add', NULL, 95030, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95032, '景点修改', NULL, 'destination_spot_edit', NULL, 95030, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95033, '景点删除', NULL, 'destination_spot_del', NULL, 95030, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),

(95040, '标签管理', 'destination-tag', NULL, '/admin/destination/tag/index', 95000, 'iconfont icon-biaoqian', '1', 4, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(95041, '标签新增', NULL, 'destination_tag_add', NULL, 95040, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95042, '标签修改', NULL, 'destination_tag_edit', NULL, 95040, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(95043, '标签删除', NULL, 'destination_tag_del', NULL, 95040, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0');
