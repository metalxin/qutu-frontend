-- 已导入旧版攻略菜单时的快速修复脚本
-- 目标：攻略管理改为最外层菜单（与目的地管理同级），点击直接打开列表页

UPDATE `sys_menu`
SET `parent_id` = -1,
    `path` = '/admin/guide/index',
    `sort_order` = 11,
    `menu_type` = '0'
WHERE `menu_id` = 96000;

UPDATE `sys_menu`
SET `parent_id` = 96000,
    `menu_type` = '1'
WHERE `menu_id` IN (96011, 96012, 96013);

-- 删除旧的中间层“攻略列表”菜单（如果存在）
DELETE FROM `sys_menu` WHERE `menu_id` = 96010;
