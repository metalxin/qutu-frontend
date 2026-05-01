-- 示例数据（论文用）
-- 为关键表提供示例记录（每表 5 条，简化版）

-- t_spot_category
INSERT INTO `t_spot_category` (`id`,`parent_id`,`name`,`icon`,`sort_order`,`created_at`) VALUES
(1,NULL,'自然风光','icon-nature',1,NOW()),
(2,NULL,'人文历史','icon-culture',2,NOW()),
(3,NULL,'美食探店','icon-food',3,NOW());

-- t_destination_city
INSERT INTO `t_destination_city` (`id`,`name`,`province`,`country`,`subtitle`,`intro`,`cover_url`,`banner_url`,`center_lat`,`center_lng`,`spots_count`,`avg_rating`,`view_count`,`hot_score`,`is_hot`,`created_at`,`updated_at`,`deleted_flag`) VALUES
(1,'北京','北京','中国','首都','北京是中国的首都','/images/beijing.jpg','/banners/beijing.jpg',39.9042,116.4074,2,4.6,10234,98.5,1,NOW(),NOW(),0),
(2,'上海','上海','中国','东方明珠','中国经济中心','/images/shanghai.jpg','/banners/shanghai.jpg',31.2304,121.4737,1,4.5,8567,88.1,1,NOW(),NOW(),0),
(3,'杭州','浙江','中国','西湖之城','杭州以西湖著名','/images/hangzhou.jpg','/banners/hangzhou.jpg',30.2741,120.1551,1,4.7,5321,74.2,1,NOW(),NOW(),0),
(4,'成都','四川','中国','美食与熊猫','成都美食丰富','/images/chengdu.jpg','/banners/chengdu.jpg',30.5728,104.0668,1,4.4,4123,65.0,0,NOW(),NOW(),0),
(5,'西安','陕西','中国','古都长安','西安历史悠久','/images/xian.jpg','/banners/xian.jpg',34.3416,108.9398,0,4.3,3890,60.4,0,NOW(),NOW(),0);

-- t_spot
INSERT INTO `t_spot` (`id`,`city_id`,`category_id`,`name`,`subtitle`,`cover_url`,`rating`,`rating_count`,`price_min`,`price_max`,`price_text`,`visit_duration_minutes`,`best_season`,`suitable_for`,`lat`,`lng`,`address`,`description`,`view_count`,`favorite_count`,`comment_count`,`hot_score`,`status`,`created_at`,`updated_at`,`deleted_flag`) VALUES
(1,1,2,'故宫','紫禁城','/spots/forbidden_city.jpg',4.8,12000,NULL,NULL,'门票',120,'春秋','家庭',39.9163,116.3972,'北京市东城区景山前街4号','明清两代皇宫',8000,2000,500,99.9,1,NOW(),NOW(),0),
(2,1,3,'王府井小吃街','老北京小吃','/spots/wangfujing.jpg',4.3,3000,NULL,NULL,'人均¥50',60,'秋','游客',39.9139,116.4134,'北京市东城区王府井大街','王府井小吃丰富',4000,800,120,78.0,1,NOW(),NOW(),0),
(3,2,2,'外滩','外滩景观带','/spots/bund.jpg',4.5,9000,NULL,NULL,'免费',90,'春','情侣',31.2400,121.4900,'上海市黄浦区中山东一路','上海外滩',6000,1200,300,88.0,1,NOW(),NOW(),0),
(4,3,1,'西湖','西湖风景区','/spots/xihu.jpg',4.7,8000,NULL,NULL,'免费',180,'春','散步',30.2431,120.1411,'杭州市西湖区','西湖风光',5000,900,150,85.5,1,NOW(),NOW(),0),
(5,4,3,'锦里古街','成都美食街','/spots/jinli.jpg',4.2,2000,NULL,NULL,'小吃人均¥40',90,'冬','美食爱好者',30.6574,104.0728,'成都市武侯区','锦里古街',2500,500,70,60.2,1,NOW(),NOW(),0);

-- t_spot_image
INSERT INTO `t_spot_image` (`id`,`spot_id`,`url`,`width`,`height`,`order_index`,`created_at`) VALUES
(1,1,'/spots/images/forbidden_1.jpg',1200,800,1,NOW()),
(2,1,'/spots/images/forbidden_2.jpg',1200,800,2,NOW()),
(3,3,'/spots/images/bund_1.jpg',1200,800,1,NOW()),
(4,4,'/spots/images/xihu_1.jpg',1200,800,1,NOW()),
(5,5,'/spots/images/jinli_1.jpg',1200,800,1,NOW());

-- t_spot_tag
INSERT INTO `t_spot_tag` (`id`,`name`) VALUES
(1,'热门'),(2,'网红'),(3,'古迹'),(4,'美食'),(5,'公园');

-- t_spot_tag_map
INSERT INTO `t_spot_tag_map` (`spot_id`,`tag_id`) VALUES
(1,3),(1,2),(2,2),(5,4),(4,5);

-- t_spot_comment
INSERT INTO `t_spot_comment` (`id`,`spot_id`,`user_id`,`user_name`,`user_avatar`,`content`,`parent_id`,`like_count`,`reply_count`,`created_at`) VALUES
(1,1,101,'alice','/avatars/alice.jpg','非常值得一游',NULL,10,2,NOW()),
(2,1,102,'bob','/avatars/bob.jpg','历史建筑保存很好',NULL,5,1,NOW()),
(3,3,103,'charlie','/avatars/charlie.jpg','夜景漂亮',NULL,8,0,NOW()),
(4,4,104,'diana','/avatars/diana.jpg','适合散步',NULL,3,0,NOW()),
(5,5,105,'eric','/avatars/eric.jpg','小吃很好吃',NULL,6,1,NOW());

-- sys_user
INSERT INTO `sys_user` (`user_id`,`username`,`password`,`salt`,`phone`,`avatar`,`nickname`,`name`,`email`,`dept_id`,`create_by`,`update_by`,`create_time`,`update_time`,`lock_flag`,`del_flag`) VALUES
(101,'admin','$2a$10$abc','salt','13800000001','/avatars/admin.jpg','管理员','管理员','admin@example.com',1,'admin','admin',NOW(),NOW(),'0','0'),
(102,'alice','$2a$10$abc','salt','13800000002','/avatars/alice.jpg','Alice','Alice','alice@example.com',1,'admin','admin',NOW(),NOW(),'0','0'),
(103,'bob','$2a$10$abc','salt','13800000003','/avatars/bob.jpg','Bob','Bob','bob@example.com',1,'admin','admin',NOW(),NOW(),'0','0');

-- sys_role
INSERT INTO `sys_role` (`role_id`,`role_name`,`role_code`,`role_desc`,`create_by`,`create_time`,`update_by`) VALUES
(1,'管理员','ROLE_ADMIN','系统管理员','admin',NOW(),'admin'),
(2,'内容编辑','ROLE_EDITOR','内容编辑','admin',NOW(),'admin');

-- sys_menu (关键权限点示例)
INSERT INTO `sys_menu` (`menu_id`,`name`,`en_name`,`permission`,`path`,`parent_id`,`icon`,`visible`,`sort_order`,`keep_alive`,`embedded`,`menu_type`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`) VALUES
(95010,'目的地城市','destination-city',NULL,'/admin/destination/city/index',95000,'iconfont icon-dingwei','1',1,'0','0','0','admin',NOW(),'admin',NOW(),'0'),
(95011,'城市新增',NULL,'destination_city_add',NULL,95010,NULL,'1',1,'0',NULL,'1','admin',NOW(),'admin',NOW(),'0'),
(95012,'城市修改',NULL,'destination_city_edit',NULL,95010,NULL,'1',2,'0',NULL,'1','admin',NOW(),'admin',NOW(),'0'),
(95013,'城市删除',NULL,'destination_city_del',NULL,95010,NULL,'1',3,'0',NULL,'1','admin',NOW(),'admin',NOW(),'0');

-- sys_role_menu / sys_user_role
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES
(1,95010),(1,95011),(1,95012),(1,95013),(2,95010),(2,95011);

INSERT INTO `sys_user_role` (`user_id`,`role_id`) VALUES
(101,1),(102,2);
