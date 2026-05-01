-- 精选攻略后台菜单与按钮权限
-- 菜单号段：96000

DELETE FROM `sys_menu` WHERE `menu_id` IN (
  96000, 96010, 96011, 96012, 96013
);

INSERT INTO `sys_menu` (`menu_id`, `name`, `en_name`, `permission`, `path`, `parent_id`, `icon`, `visible`, `sort_order`, `keep_alive`, `embedded`, `menu_type`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES
(96000, '攻略管理', 'guide', NULL, '/admin/guide/index', -1, 'iconfont icon-shuxingtu', '1', 11, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(96011, '攻略新增', NULL, 'guide_add', NULL, 96000, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(96012, '攻略修改', NULL, 'guide_edit', NULL, 96000, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(96013, '攻略删除', NULL, 'guide_del', NULL, 96000, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0');
