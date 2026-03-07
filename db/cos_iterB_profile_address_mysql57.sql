-- 文件名：cos_iterB_profile_address_mysql57.sql
-- 适用版本：MySQL 5.7
-- 目标：迭代B补充（个人中心地址簿 + 身材档案 + 订单快照字段）
-- 说明：幂等执行，可重复运行
SET NAMES utf8;
SET @db := DATABASE();

/* =========================================================
   1) 用户地址簿表
   ========================================================= */
CREATE TABLE IF NOT EXISTS `cos_user_address` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_table` varchar(100) NOT NULL COMMENT '用户表',
  `receiver_name` varchar(60) NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) NOT NULL COMMENT '手机号',
  `province` varchar(60) DEFAULT NULL COMMENT '省',
  `city` varchar(60) DEFAULT NULL COMMENT '市',
  `district` varchar(60) DEFAULT NULL COMMENT '区',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否默认(1是0否)',
  `status` varchar(32) DEFAULT '启用' COMMENT '状态',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户地址簿';

SET @sql_add_idx_addr_user := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_user_address' AND index_name='idx_cos_user_addr_user') = 0,
  'ALTER TABLE `cos_user_address` ADD INDEX `idx_cos_user_addr_user` (`user_id`,`user_table`,`deleted`,`is_default`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_addr_user FROM @sql_add_idx_addr_user;
EXECUTE stmt_add_idx_addr_user;
DEALLOCATE PREPARE stmt_add_idx_addr_user;

/* =========================================================
   2) 用户身材档案表
   ========================================================= */
CREATE TABLE IF NOT EXISTS `cos_user_body_profile` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_table` varchar(100) NOT NULL COMMENT '用户表',
  `profile_name` varchar(60) NOT NULL COMMENT '档案名称',
  `height_cm` decimal(6,2) DEFAULT NULL COMMENT '身高cm',
  `weight_kg` decimal(6,2) DEFAULT NULL COMMENT '体重kg',
  `waist_cm` decimal(6,2) DEFAULT NULL COMMENT '腰围cm',
  `bust_cm` decimal(6,2) DEFAULT NULL COMMENT '胸围cm',
  `hip_cm` decimal(6,2) DEFAULT NULL COMMENT '臀围cm',
  `shoulder_cm` decimal(6,2) DEFAULT NULL COMMENT '肩宽cm',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否默认(1是0否)',
  `status` varchar(32) DEFAULT '启用' COMMENT '状态',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户身材档案';

SET @sql_add_idx_body_user := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_user_body_profile' AND index_name='idx_cos_user_body_user') = 0,
  'ALTER TABLE `cos_user_body_profile` ADD INDEX `idx_cos_user_body_user` (`user_id`,`user_table`,`deleted`,`is_default`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_body_user FROM @sql_add_idx_body_user;
EXECUTE stmt_add_idx_body_user;
DEALLOCATE PREPARE stmt_add_idx_body_user;

/* =========================================================
   3) 订单表补充快照字段
   ========================================================= */
SET @sql_add_col_order_addr_snapshot := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='address_snapshot_json') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `address_snapshot_json` longtext COMMENT ''地址快照JSON''',
  'SELECT 1'
);
PREPARE stmt_add_col_order_addr_snapshot FROM @sql_add_col_order_addr_snapshot;
EXECUTE stmt_add_col_order_addr_snapshot;
DEALLOCATE PREPARE stmt_add_col_order_addr_snapshot;

SET @sql_add_col_order_body_snapshot := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='body_profile_snapshot_json') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `body_profile_snapshot_json` longtext COMMENT ''身材快照JSON''',
  'SELECT 1'
);
PREPARE stmt_add_col_order_body_snapshot FROM @sql_add_col_order_body_snapshot;
EXECUTE stmt_add_col_order_body_snapshot;
DEALLOCATE PREPARE stmt_add_col_order_body_snapshot;

SET @sql_add_col_order_address_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='address_id') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `address_id` bigint(20) DEFAULT NULL COMMENT ''地址簿ID''',
  'SELECT 1'
);
PREPARE stmt_add_col_order_address_id FROM @sql_add_col_order_address_id;
EXECUTE stmt_add_col_order_address_id;
DEALLOCATE PREPARE stmt_add_col_order_address_id;

SET @sql_add_col_order_body_profile_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='body_profile_id') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `body_profile_id` bigint(20) DEFAULT NULL COMMENT ''身材档案ID''',
  'SELECT 1'
);
PREPARE stmt_add_col_order_body_profile_id FROM @sql_add_col_order_body_profile_id;
EXECUTE stmt_add_col_order_body_profile_id;
DEALLOCATE PREPARE stmt_add_col_order_body_profile_id;

SET @sql_add_idx_order_address_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cosorder' AND index_name='idx_cosorder_address_id') = 0,
  'ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_address_id` (`address_id`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_order_address_id FROM @sql_add_idx_order_address_id;
EXECUTE stmt_add_idx_order_address_id;
DEALLOCATE PREPARE stmt_add_idx_order_address_id;

SET @sql_add_idx_order_body_profile_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cosorder' AND index_name='idx_cosorder_body_profile_id') = 0,
  'ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_body_profile_id` (`body_profile_id`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_order_body_profile_id FROM @sql_add_idx_order_body_profile_id;
EXECUTE stmt_add_idx_order_body_profile_id;
DEALLOCATE PREPARE stmt_add_idx_order_body_profile_id;