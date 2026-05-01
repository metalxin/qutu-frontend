-- 行前清单后台菜单与按钮权限
-- 菜单号段：98000

DELETE FROM `sys_menu` WHERE `menu_id` IN (
  98000, 98011, 98012, 98013
);

INSERT INTO `sys_menu` (`menu_id`, `name`, `en_name`, `permission`, `path`, `parent_id`, `icon`, `visible`, `sort_order`, `keep_alive`, `embedded`, `menu_type`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES
(98000, '清单管理', 'checklist', NULL, '/admin/checklist/index', -1, 'iconfont icon-rizhi', '1', 13, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(98011, '清单新增', NULL, 'checklist_add', NULL, 98000, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(98012, '清单修改', NULL, 'checklist_edit', NULL, 98000, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(98013, '清单删除', NULL, 'checklist_del', NULL, 98000, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0');
