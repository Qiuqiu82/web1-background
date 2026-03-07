-- 文件名：cos_iterB_custom_design_mvp_mysql57.sql
-- 适用版本：MySQL 5.7
-- 目标：迭代B（定制编辑核心MVP）数据结构
-- 说明：幂等执行；用于“草稿保存 + 下单快照 + 订单回看”

SET NAMES utf8;
SET @db := DATABASE();

/* =========================================================
   1) 定制草稿表
   ========================================================= */
CREATE TABLE IF NOT EXISTS `cos_custom_design` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_table` varchar(100) NOT NULL COMMENT '用户表',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) DEFAULT NULL COMMENT '商品名称快照',
  `style_name` varchar(200) DEFAULT NULL COMMENT '款式名称快照',
  `design_summary` varchar(500) DEFAULT NULL COMMENT '定制摘要',
  `design_json` longtext COMMENT '结构化定制参数JSON',
  `size_code` varchar(50) DEFAULT NULL COMMENT '尺码',
  `material_name` varchar(200) DEFAULT NULL COMMENT '面料',
  `color_theme` varchar(200) DEFAULT NULL COMMENT '色系',
  `fit_type` varchar(100) DEFAULT NULL COMMENT '松量/版型偏好',
  `silhouette` varchar(100) DEFAULT NULL COMMENT '廓形',
  `craft_tags` varchar(500) DEFAULT NULL COMMENT '工艺标签(逗号分隔)',
  `accessory_tags` varchar(500) DEFAULT NULL COMMENT '配件标签(逗号分隔)',
  `reference_images` longtext COMMENT '参考图URL(逗号分隔)',
  `custom_note` varchar(1000) DEFAULT NULL COMMENT '补充说明',
  `status` varchar(50) DEFAULT '草稿' COMMENT '状态',
  `version_no` int(11) DEFAULT '1' COMMENT '草稿版本号',
  `last_used_at` datetime DEFAULT NULL COMMENT '最近用于下单时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS定制草稿';

SET @sql_add_idx_custom_user_product := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_custom_design' AND index_name='idx_cos_custom_user_product') = 0,
  'ALTER TABLE `cos_custom_design` ADD INDEX `idx_cos_custom_user_product` (`user_id`,`user_table`,`product_id`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_custom_user_product FROM @sql_add_idx_custom_user_product;
EXECUTE stmt_add_idx_custom_user_product;
DEALLOCATE PREPARE stmt_add_idx_custom_user_product;

SET @sql_add_idx_custom_updated := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_custom_design' AND index_name='idx_cos_custom_updated') = 0,
  'ALTER TABLE `cos_custom_design` ADD INDEX `idx_cos_custom_updated` (`updated_at`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_custom_updated FROM @sql_add_idx_custom_updated;
EXECUTE stmt_add_idx_custom_updated;
DEALLOCATE PREPARE stmt_add_idx_custom_updated;

/* =========================================================
   2) 购物车补充定制快照字段
   ========================================================= */
SET @sql_add_col_cart_custom_draft_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='coscart' AND column_name='custom_draft_id') = 0,
  'ALTER TABLE `coscart` ADD COLUMN `custom_draft_id` bigint(20) DEFAULT NULL COMMENT ''定制草稿ID''',
  'SELECT 1'
);
PREPARE stmt_add_col_cart_custom_draft_id FROM @sql_add_col_cart_custom_draft_id;
EXECUTE stmt_add_col_cart_custom_draft_id;
DEALLOCATE PREPARE stmt_add_col_cart_custom_draft_id;

SET @sql_add_col_cart_custom_summary := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='coscart' AND column_name='custom_summary') = 0,
  'ALTER TABLE `coscart` ADD COLUMN `custom_summary` varchar(500) DEFAULT NULL COMMENT ''定制摘要''',
  'SELECT 1'
);
PREPARE stmt_add_col_cart_custom_summary FROM @sql_add_col_cart_custom_summary;
EXECUTE stmt_add_col_cart_custom_summary;
DEALLOCATE PREPARE stmt_add_col_cart_custom_summary;

SET @sql_add_col_cart_custom_snapshot_json := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='coscart' AND column_name='custom_snapshot_json') = 0,
  'ALTER TABLE `coscart` ADD COLUMN `custom_snapshot_json` longtext COMMENT ''定制快照JSON''',
  'SELECT 1'
);
PREPARE stmt_add_col_cart_custom_snapshot_json FROM @sql_add_col_cart_custom_snapshot_json;
EXECUTE stmt_add_col_cart_custom_snapshot_json;
DEALLOCATE PREPARE stmt_add_col_cart_custom_snapshot_json;

SET @sql_add_col_cart_custom_snapshot_version := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='coscart' AND column_name='custom_snapshot_version') = 0,
  'ALTER TABLE `coscart` ADD COLUMN `custom_snapshot_version` int(11) DEFAULT 1 COMMENT ''定制快照版本''',
  'SELECT 1'
);
PREPARE stmt_add_col_cart_custom_snapshot_version FROM @sql_add_col_cart_custom_snapshot_version;
EXECUTE stmt_add_col_cart_custom_snapshot_version;
DEALLOCATE PREPARE stmt_add_col_cart_custom_snapshot_version;

SET @sql_add_idx_cart_custom_draft := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='coscart' AND index_name='idx_coscart_custom_draft_id') = 0,
  'ALTER TABLE `coscart` ADD INDEX `idx_coscart_custom_draft_id` (`custom_draft_id`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_cart_custom_draft FROM @sql_add_idx_cart_custom_draft;
EXECUTE stmt_add_idx_cart_custom_draft;
DEALLOCATE PREPARE stmt_add_idx_cart_custom_draft;

/* =========================================================
   3) 历史数据回填（可选）
   ========================================================= */
UPDATE `coscart`
SET `custom_summary` = `specs`
WHERE (`custom_summary` IS NULL OR `custom_summary` = '')
  AND `specs` IS NOT NULL
  AND `specs` <> '';