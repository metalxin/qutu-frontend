-- 旅行故事后台菜单与按钮权限
-- 菜单号段：97000

DELETE FROM `sys_menu` WHERE `menu_id` IN (
  97000, 97011, 97012, 97013
);

INSERT INTO `sys_menu` (`menu_id`, `name`, `en_name`, `permission`, `path`, `parent_id`, `icon`, `visible`, `sort_order`, `keep_alive`, `embedded`, `menu_type`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES
(97000, '故事管理', 'story', NULL, '/admin/story/index', -1, 'iconfont icon-rizhi', '1', 12, '0', '0', '0', 'admin', NOW(), 'admin', NOW(), '0'),
(97011, '故事新增', NULL, 'story_add', NULL, 97000, NULL, '1', 1, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(97012, '故事修改', NULL, 'story_edit', NULL, 97000, NULL, '1', 2, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0'),
(97013, '故事删除', NULL, 'story_del', NULL, 97000, NULL, '1', 3, '0', NULL, '1', 'admin', NOW(), 'admin', NOW(), '0');
