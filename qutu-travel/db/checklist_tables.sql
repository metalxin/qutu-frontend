-- =========================
-- 行前清单建表 SQL
-- =========================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_checklist
-- ----------------------------
DROP TABLE IF EXISTS `t_checklist`;
CREATE TABLE `t_checklist`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '清单标题',
  `destination` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目的地',
  `depart_date` date NULL DEFAULT NULL COMMENT '出发日期',
  `return_date` date NULL DEFAULT NULL COMMENT '返回日期',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已完成',
  `items_count` int NOT NULL DEFAULT 0 COMMENT '清单项数量',
  `checked_count` int NOT NULL DEFAULT 0 COMMENT '已完成项数量',
  `budget` decimal(10, 2) NULL DEFAULT NULL COMMENT '预算金额',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删，1已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '行前清单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_checklist_item
-- ----------------------------
DROP TABLE IF EXISTS `t_checklist_item`;
CREATE TABLE `t_checklist_item`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `checklist_id` bigint NOT NULL COMMENT '清单ID',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'prepare' COMMENT '分类：prepare|document|clothing|daily|food|medicine|electronics|other',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目内容',
  `checked` tinyint NOT NULL DEFAULT 0 COMMENT '是否已勾选：0-未勾，1-已勾',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_checklist`(`checklist_id` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '清单项' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_checklist_expense
-- ----------------------------
DROP TABLE IF EXISTS `t_checklist_expense`;
CREATE TABLE `t_checklist_expense`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `checklist_id` bigint NOT NULL COMMENT '清单ID',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'other' COMMENT '分类：transport|accommodation|food|shopping|entertainment|other',
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `expense_date` date NULL DEFAULT NULL COMMENT '消费日期',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_checklist`(`checklist_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '清单费用' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
