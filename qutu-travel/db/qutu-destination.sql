SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 目的地城市表
-- =========================
CREATE TABLE t_destination_city (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(100) NOT NULL COMMENT '城市名称',
  province VARCHAR(100) COMMENT '省份',
  country VARCHAR(100) DEFAULT '中国' COMMENT '国家',

  subtitle VARCHAR(255) COMMENT '副标题',
  intro TEXT COMMENT '简介',

  cover_url VARCHAR(1000) COMMENT '封面图',
  banner_url VARCHAR(1000) COMMENT '横幅图',

  center_lat DECIMAL(10,7) COMMENT '中心纬度',
  center_lng DECIMAL(10,7) COMMENT '中心经度',

  spots_count INT DEFAULT 0 COMMENT '景点数量',
  avg_rating DECIMAL(3,2) DEFAULT 0 COMMENT '平均评分',

  view_count BIGINT DEFAULT 0 COMMENT '浏览量',
  hot_score DECIMAL(12,2) DEFAULT 0 COMMENT '热度值',
  is_hot TINYINT DEFAULT 0 COMMENT '是否热门',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted_flag TINYINT DEFAULT 0 COMMENT '逻辑删除',

  UNIQUE KEY uk_city_name(name),
  INDEX idx_city_hot(is_hot)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目的地城市表';


-- =========================
-- 景点分类表
-- =========================
CREATE TABLE t_spot_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  parent_id BIGINT COMMENT '父分类ID',
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  icon VARCHAR(50) COMMENT '图标',
  sort_order INT DEFAULT 0 COMMENT '排序',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  UNIQUE KEY uk_category_name(name),
  INDEX idx_category_parent(parent_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点分类表';


-- =========================
-- 景点主表（含空间索引）
-- =========================
CREATE TABLE t_spot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '景点ID',

  city_id BIGINT NOT NULL COMMENT '城市ID',
  category_id BIGINT COMMENT '分类ID',

  name VARCHAR(200) NOT NULL COMMENT '景点名称',
  subtitle VARCHAR(255) COMMENT '副标题',
  cover_url VARCHAR(1000) COMMENT '封面图',

  rating DECIMAL(3,2) DEFAULT 0 COMMENT '评分',
  rating_count INT DEFAULT 0 COMMENT '评分人数',

  price_min DECIMAL(10,2) COMMENT '最低价',
  price_max DECIMAL(10,2) COMMENT '最高价',
  price_text VARCHAR(255) COMMENT '价格描述',

  visit_duration_minutes INT COMMENT '游玩时长',

  best_season VARCHAR(100) COMMENT '最佳季节',
  suitable_for VARCHAR(100) COMMENT '适合人群',

  lat DECIMAL(10,7) NOT NULL COMMENT '纬度',
  lng DECIMAL(10,7) NOT NULL COMMENT '经度',

  geo POINT GENERATED ALWAYS AS (
    ST_SRID(POINT(lng, lat), 4326)
  ) STORED NOT NULL COMMENT '空间坐标',

  address VARCHAR(512) COMMENT '地址',
  description TEXT COMMENT '详情',

  view_count BIGINT DEFAULT 0 COMMENT '浏览量',
  favorite_count INT DEFAULT 0 COMMENT '收藏数',
  comment_count INT DEFAULT 0 COMMENT '评论数',
  hot_score DECIMAL(12,2) DEFAULT 0 COMMENT '热度',

  status TINYINT DEFAULT 1 COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted_flag TINYINT DEFAULT 0 COMMENT '逻辑删除',

  FULLTEXT KEY ft_spot_text(name, description),
  INDEX idx_spot_city(city_id),
  INDEX idx_spot_hot(hot_score),
  SPATIAL INDEX idx_spot_geo(geo)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';


-- =========================
-- 景点图片
-- =========================
CREATE TABLE t_spot_image (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
  spot_id BIGINT NOT NULL COMMENT '景点ID',

  url VARCHAR(1000) NOT NULL COMMENT '图片URL',
  width INT COMMENT '宽度',
  height INT COMMENT '高度',

  order_index INT DEFAULT 0 COMMENT '排序',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  INDEX idx_image_spot(spot_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点图片表';


-- =========================
-- 景点提示
-- =========================
CREATE TABLE t_spot_tip (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提示ID',
  spot_id BIGINT NOT NULL COMMENT '景点ID',
  icon VARCHAR(20) COMMENT '图标',
  title VARCHAR(100) COMMENT '标题',
  description VARCHAR(500) COMMENT '说明',
  order_index INT DEFAULT 0 COMMENT '排序',

  INDEX idx_tip_spot(spot_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点提示表';


-- =========================
-- 标签
-- =========================
CREATE TABLE t_spot_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
  name VARCHAR(100) UNIQUE COMMENT '标签名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';


CREATE TABLE t_spot_tag_map (
  spot_id BIGINT COMMENT '景点ID',
  tag_id BIGINT COMMENT '标签ID',
  PRIMARY KEY (spot_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签关联表';


-- =========================
-- 评论
-- =========================
CREATE TABLE t_spot_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  spot_id BIGINT NOT NULL COMMENT '景点ID',

  user_id BIGINT NOT NULL COMMENT '用户ID',
  user_name VARCHAR(100) COMMENT '用户名',
  user_avatar VARCHAR(1000) COMMENT '头像',

  content TEXT NOT NULL COMMENT '评论内容',
  parent_id BIGINT COMMENT '父评论',

  like_count INT DEFAULT 0 COMMENT '点赞数',
  reply_count INT DEFAULT 0 COMMENT '回复数',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  INDEX idx_comment_spot(spot_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';


-- =========================
-- 评论点赞
-- =========================
CREATE TABLE t_comment_like (
  comment_id BIGINT COMMENT '评论ID',
  user_id BIGINT COMMENT '用户ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (comment_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';


-- =========================
-- 收藏
-- =========================
CREATE TABLE t_spot_favorite (
  spot_id BIGINT COMMENT '景点ID',
  user_id BIGINT COMMENT '用户ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (spot_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';


-- =========================
-- 行为日志
-- =========================
CREATE TABLE t_spot_action_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  user_id BIGINT COMMENT '用户ID',
  spot_id BIGINT COMMENT '景点ID',
  action_type TINYINT COMMENT '行为类型',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '时间',

  INDEX idx_action_spot(spot_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行为日志表';


-- =========================
-- 外键
-- =========================
ALTER TABLE t_spot
  ADD CONSTRAINT fk_spot_city FOREIGN KEY (city_id)
  REFERENCES t_destination_city(id);

ALTER TABLE t_spot_image
  ADD CONSTRAINT fk_image_spot FOREIGN KEY (spot_id)
  REFERENCES t_spot(id);

ALTER TABLE t_spot_tip
  ADD CONSTRAINT fk_tip_spot FOREIGN KEY (spot_id)
  REFERENCES t_spot(id);

SET FOREIGN_KEY_CHECKS = 1;
