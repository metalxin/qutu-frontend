# 关键数据库表说明（论文用）

本文档摘录项目中用于论文的关键数据库表结构、字段说明、主外键与索引关系，便于论文中展示数据模型与说明。

---

## 表：t_destination_city（目的地城市表）
说明：存放目的地城市的基本信息与统计字段。

字段说明：

- id : bigint NOT NULL AUTO_INCREMENT — 主键ID
- name : varchar(100) NOT NULL — 城市名称，唯一（uk_city_name）
- province : varchar(100) — 省份
- country : varchar(100) DEFAULT '中国' — 国家
- subtitle : varchar(255) — 副标题
- intro : text — 城市简介
- cover_url : varchar(1000) — 封面图 URL
- banner_url : varchar(1000) — 横幅图 URL
- center_lat : decimal(10,7) — 中心纬度
- center_lng : decimal(10,7) — 中心经度
- spots_count : int DEFAULT 0 — 景点数量统计
- avg_rating : decimal(3,2) DEFAULT 0.00 — 平均评分
- view_count : bigint DEFAULT 0 — 浏览量
- hot_score : decimal(12,2) DEFAULT 0.00 — 热度值
- is_hot : tinyint DEFAULT 0 — 是否热门（布尔）
- created_at / updated_at — 创建/更新时间
- deleted_flag : tinyint DEFAULT 0 — 逻辑删除标记

约束与索引：
- 主键：`id`
- 唯一索引：`uk_city_name` (name)
- 普通索引：`idx_city_hot` (is_hot)

关系：
- 被 `t_spot.city_id` 外键引用（一个城市有多景点）。

---

## 表：t_spot（景点表）
说明：存放景点核心信息、地理位置与统计数据。

字段说明：

- id : bigint NOT NULL AUTO_INCREMENT — 景点ID（主键）
- city_id : bigint NOT NULL — 所属城市（外键，引用 `t_destination_city.id`）
- category_id : bigint — 分类ID（引用 `t_spot_category.id`）
- name : varchar(200) NOT NULL — 景点名称
- subtitle : varchar(255) — 副标题
- cover_url : varchar(1000) — 封面图
- rating : decimal(3,2) — 评分
- rating_count : int — 评分人数
- price_min / price_max / price_text — 价格信息
- visit_duration_minutes — 预计游玩时长（分钟）
- best_season / suitable_for — 建议季节、适合人群
- lat / lng : decimal(10,7) — 纬度/经度
- geo : point GENERATED ALWAYS AS (...) STORED — 空间坐标（用于空间索引）
- address / description — 地址与详情描述
- view_count / favorite_count / comment_count — 统计数
- hot_score : decimal — 热度值（排序依据之一）
- status : tinyint — 状态（例如是否上架）
- created_at / updated_at / deleted_flag

约束与索引：
- 主键：`id`
- 普通索引：`idx_spot_city` (city_id)、`idx_spot_hot` (hot_score)
- 空间索引：`idx_spot_geo` (geo)
- 全文索引：`ft_spot_text` (name, description)
- 外键：`fk_spot_city` (city_id) -> `t_destination_city(id)`，ON DELETE/UPDATE RESTRICT

关系：
- 一对多：`t_destination_city(id)` -> `t_spot(city_id)`
- 一对多：`t_spot(id)` -> `t_spot_image(spot_id)`
- 多对多（通过 `t_spot_tag_map`）：`t_spot` <-> `t_spot_tag`

---

## 表：t_spot_image（景点图片表）
说明：存放景点图片元数据。

字段说明：
- id : bigint AUTO_INCREMENT — 图片ID（主键）
- spot_id : bigint NOT NULL — 所属景点（外键 -> t_spot.id）
- url : varchar(1000) NOT NULL — 图片 URL
- width / height : int — 图片尺寸
- order_index : int — 排序
- created_at : datetime — 上传时间

约束与索引：
- 主键：`id`
- 索引：`idx_image_spot` (spot_id)
- 外键：`fk_image_spot` (spot_id) -> `t_spot(id)`

---

## 表：t_spot_category（景点分类）
说明：景点分类树。

字段说明：
- id : bigint AUTO_INCREMENT — 分类ID（主键）
- parent_id : bigint — 父分类ID
- name : varchar(100) NOT NULL — 分类名称，唯一（uk_category_name）
- icon : varchar(50) — 图标
- sort_order : int — 排序
- created_at : datetime

索引：
- 唯一索引：`uk_category_name` (name)
- 普通索引：`idx_category_parent` (parent_id)

---

## 表：t_spot_tag / t_spot_tag_map（标签与关联）
说明：
- `t_spot_tag` 保存标签元数据（id, name）。
- `t_spot_tag_map` 为标签与景点的多对多关联（主键复合：spot_id, tag_id）。

---

## 表：t_spot_comment（评论表）
说明：用户对景点的评论。

字段要点：
- id, spot_id, user_id, user_name, user_avatar, content, parent_id（回复）、like_count、reply_count、created_at
- 索引：`idx_comment_spot` (spot_id)

---

## 表：sys_user（用户表）
说明：系统用户信息（后台/前端可复用）。

字段要点：
- user_id : bigint 主键
- username / password / salt — 登录凭据
- phone / avatar / nickname / name / email
- dept_id — 部门ID
- lock_flag / del_flag — 账号状态
- wx_openid / mini_openid / qq_openid / gitee_login / osc_id — 第三方登录标识

索引：
- `user_wx_openid`, `user_qq_openid`, `user_idx1_username`

---

## 表：sys_role（角色表）
说明：角色定义。

字段要点：
- role_id, role_name, role_code, role_desc, create_by, create_time, del_flag
- 索引：`role_idx1_role_code` (role_code)

---

## 表：sys_menu（菜单/权限表）
说明：菜单与权限点，`permission` 字段存放权限标识（控制器注解中使用）。

字段要点：
- menu_id 主键
- name / en_name / permission / path / parent_id / icon
- menu_type（目录/菜单/按钮），visible/keep_alive 等前端路由元信息

关系：
- 菜单与角色通过 `sys_role_menu` 关联

---

## 表：sys_role_menu / sys_user_role（权限关联表）
说明：
- `sys_role_menu(role_id, menu_id)`：角色与菜单（权限）关联
- `sys_user_role(user_id, role_id)`：用户与角色关联

---

## 表：t_checklist / t_checklist_item（行前清单）
说明：用户个人出行清单与清单项。

`t_checklist` 主要字段：id, user_id, title, destination, depart_date, return_date, status, items_count, checked_count, budget, created_at

`t_checklist_item` 存放单条清单项（字段：id, checklist_id, content, checked, order_num 等）。

---

### 关系概览（简要）

- 用户模型：`sys_user` —< `sys_user_role` —> `sys_role` —< `sys_role_menu` —> `sys_menu`（RBAC）
- 目的地/景点模型：`t_destination_city` —< `t_spot` —< `t_spot_image`, `t_spot_comment`; `t_spot` 可通过 `t_spot_tag_map` 关联 `t_spot_tag`；`t_spot.category_id` 指向 `t_spot_category`。

---

附注：我已从数据库初始化脚本 `db/qutu.sql` 提取上述字段与约束。

---

## 示例数据（位置）
示例数据 SQL 已生成：[db/DB_SAMPLE_DATA.sql](db/DB_SAMPLE_DATA.sql)

---

## ER 图（Mermaid）
ER 图源文件：[db/ER_DIAGRAM.mmd](db/ER_DIAGRAM.mmd)

下面的 ER 图展示了论文中常用的实体关系（目的地 - 景点 - 图片 - 分类 - 标签；以及 RBAC 表关系）。我也已尝试把该 Mermaid 渲染为图片放在对话中预览。

---

下一步：我将尝试导出本 Markdown 为 Word（.docx）和 PDF，并把导出文件放到 `db/` 目录。
