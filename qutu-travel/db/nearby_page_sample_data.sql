-- =========================
-- 附近页面初始数据
-- =========================
SET NAMES utf8mb4;

-- 1. 插入城市数据（合肥市）
INSERT INTO t_destination_city (id, name, province, country, subtitle, center_lat, center_lng, hot_score, is_hot)
VALUES 
(1, '合肥市', '安徽省', '中国', '安徽省省会', 31.8206, 117.2272, 85.5, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 2. 插入分类数据
INSERT INTO t_spot_category (id, parent_id, name, icon, sort_order)
VALUES 
(1, NULL, '美食', '🍴', 1),
(2, NULL, '景点', '🏞️', 2),
(3, NULL, '购物', '🛍️', 3),
(4, NULL, '娱乐', '🎮', 4),
(5, NULL, '住宿', '🏨', 5)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 3. 插入景点/地点数据
INSERT INTO t_spot (id, city_id, category_id, name, subtitle, cover_url, rating, rating_count, 
  lat, lng, address, description, price_text, status, hot_score)
VALUES 
-- 美食类
(1, 1, 1, '庐州烤鸭店', '合肥老字号烤鸭', 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400&q=80', 
 4.6, 1280, 31.8612, 117.2830, '合肥市庐阳区淮河路步行街128号', 
 '庐州烤鸭店是合肥知名的老字号餐厅，以传统工艺烤制的鸭子外酥里嫩、香气四溢而闻名。店内装修古色古香，提供正宗的徽菜和烤鸭系列，是品尝合肥地道美食的必去之处。', 
 '人均¥88', 1, 92.5),

(2, 1, 1, '卡旺卡奶茶(总店)', '合肥本土奶茶品牌', 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&q=80',
 4.5, 2560, 31.8580, 117.2765, '合肥市蜀山区长江中路369号',
 '卡旺卡是源自合肥的本土奶茶品牌，以新鲜水果茶和奶盖茶著称。总店位于繁华的长江中路，店内环境舒适，是年轻人休闲聚会的好去处。',
 '人均¥25', 1, 88.0),

-- 景点类
(3, 1, 2, '逍遥津公园', '三国古战场遗址', 'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=400&q=80',
 4.7, 3520, 31.8689, 117.2945, '合肥市庐阳区寿春路16号',
 '逍遥津公园是合肥最著名的历史名胜之一，因三国时期著名的逍遥津之战而闻名。公园内绿树成荫，湖水清澈，有逍遥阁、张辽衣冠冢等历史遗迹，是了解三国文化和休闲散步的理想场所。',
 '免费', 1, 95.0),

(4, 1, 2, '包公园', '纪念包拯的园林', 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=400&q=80',
 4.8, 4200, 31.8567, 117.2987, '合肥市包河区芜湖路72号',
 '包公园是为纪念北宋名臣包拯而建的大型园林，内有包公祠、包公墓、清风阁等景点。园内建筑古朴典雅，湖水碧波荡漾，展现了包拯清正廉洁的高尚品格，是合肥最具文化底蕴的景点之一。',
 '¥50', 1, 96.5),

(5, 1, 2, '安徽博物院', '国家一级博物馆', 'https://images.unsplash.com/photo-1559628233-100c798642d4?w=400&q=80',
 4.9, 5680, 31.8523, 117.2456, '合肥市蜀山区怀宁路268号',
 '安徽博物院是安徽省最大的综合性博物馆，馆藏文物丰富，尤以徽州古建筑构件、青铜器、文房四宝等最具特色。新馆建筑现代大气，展览内容丰富，是了解安徽历史文化的最佳窗口。',
 '免费', 1, 97.0),

-- 购物类
(6, 1, 3, '合肥银泰中心', '高端购物中心', 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400&q=80',
 4.4, 1890, 31.8601, 117.2789, '合肥市庐阳区长江中路98号',
 '合肥银泰中心是合肥市区内最高端的购物中心之一，汇聚了众多国际知名品牌和餐饮美食。商场环境优雅，服务周到，是购物休闲的理想去处。',
 '', 1, 85.0),

-- 娱乐类
(7, 1, 4, '合肥融创乐园', '大型主题乐园', 'https://images.unsplash.com/photo-1553954197-bba0d8db0f76?w=400&q=80',
 4.6, 3200, 31.7856, 117.2345, '合肥市包河区庐州大道788号',
 '合肥融创乐园是一座大型主题游乐园，拥有刺激的游乐设施、精彩的演出和丰富的餐饮选择。适合家庭出游和年轻人聚会，是合肥最受欢迎的娱乐场所之一。',
 '¥280', 1, 90.0),

-- 住宿类
(8, 1, 5, '合肥洲际酒店', '五星级豪华酒店', 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&q=80',
 4.8, 2100, 31.8578, 117.2812, '合肥市包河区马鞍山路118号',
 '合肥洲际酒店是国际知名的豪华酒店品牌，位于合肥市中心繁华地段。酒店设施完善，服务一流，拥有舒适的客房、高档餐厅和健身中心，是商务出行和休闲旅游的理想选择。',
 '¥880起', 1, 93.0)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4. 插入景点图片数据
INSERT INTO t_spot_image (spot_id, image_url, sort_order)
VALUES 
-- 庐州烤鸭店
(1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80', 1),
(1, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800&q=80', 2),
-- 卡旺卡奶茶
(2, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800&q=80', 1),
-- 逍遥津公园
(3, 'https://images.unsplash.com/photo-1537531383496-f4749b1d9e70?w=800&q=80', 1),
(3, 'https://images.unsplash.com/photo-1559628233-100c798642d4?w=800&q=80', 2),
(3, 'https://images.unsplash.com/photo-1553954197-bba0d8db0f76?w=800&q=80', 3),
-- 包公园
(4, 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=800&q=80', 1),
(4, 'https://images.unsplash.com/photo-1518623489648-a173ef7824f3?w=800&q=80', 2),
-- 安徽博物院
(5, 'https://images.unsplash.com/photo-1559628233-100c798642d4?w=800&q=80', 1),
-- 合肥银泰中心
(6, 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800&q=80', 1),
-- 合肥融创乐园
(7, 'https://images.unsplash.com/photo-1553954197-bba0d8db0f76?w=800&q=80', 1),
-- 合肥洲际酒店
(8, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800&q=80', 1)
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);
