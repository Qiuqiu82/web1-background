-- 文件名：cos_week3_designer_min_mysql57.sql
-- 适用版本：MySQL 5.7
-- 说明：新增设计师角色与最小接单能力（抢单 + 我的订单）

SET @db := DATABASE();

/* 1) 设计师表 */
CREATE TABLE IF NOT EXISTS `shejishi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `shejishizhanghao` varchar(200) DEFAULT NULL COMMENT '设计师账号',
  `mima` varchar(200) NOT NULL COMMENT '密码',
  `shejishixingming` varchar(200) DEFAULT NULL COMMENT '设计师姓名',
  `touxiang` longtext COMMENT '头像',
  `lianxifangshi` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `zhuanchang` varchar(255) DEFAULT NULL COMMENT '擅长风格',
  `jianjie` longtext COMMENT '简介',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设计师';

SET @sql_add_unique_shejishi := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='shejishi' AND index_name='uk_shejishi_zhanghao') = 0,
  'ALTER TABLE `shejishi` ADD UNIQUE KEY `uk_shejishi_zhanghao` (`shejishizhanghao`)',
  'SELECT 1'
);
PREPARE stmt_add_unique_shejishi FROM @sql_add_unique_shejishi;
EXECUTE stmt_add_unique_shejishi;
DEALLOCATE PREPARE stmt_add_unique_shejishi;

INSERT INTO `shejishi`
(`id`,`addtime`,`shejishizhanghao`,`mima`,`shejishixingming`,`touxiang`,`lianxifangshi`,`zhuanchang`,`jianjie`)
VALUES
(301, NOW(), 'designer01', '123456', '设计师A', NULL, '13800138001', '礼服/修身西装', '擅长礼服与修身版型搭配'),
(302, NOW(), 'designer02', '123456', '设计师B', NULL, '13800138002', '商务/通勤西装', '擅长商务通勤与版型优化')
ON DUPLICATE KEY UPDATE
`mima`=VALUES(`mima`),
`shejishixingming`=VALUES(`shejishixingming`),
`lianxifangshi`=VALUES(`lianxifangshi`),
`zhuanchang`=VALUES(`zhuanchang`),
`jianjie`=VALUES(`jianjie`);

/* 2) cosorder 增加设计师接单字段 */
SET @sql_add_col_designer_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='designer_id') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `designer_id` bigint(20) DEFAULT NULL COMMENT ''认领设计师ID''',
  'SELECT 1'
);
PREPARE stmt_add_col_designer_id FROM @sql_add_col_designer_id;
EXECUTE stmt_add_col_designer_id;
DEALLOCATE PREPARE stmt_add_col_designer_id;

SET @sql_add_col_designer_table := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='designer_table') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `designer_table` varchar(100) DEFAULT NULL COMMENT ''设计师表名''',
  'SELECT 1'
);
PREPARE stmt_add_col_designer_table FROM @sql_add_col_designer_table;
EXECUTE stmt_add_col_designer_table;
DEALLOCATE PREPARE stmt_add_col_designer_table;

SET @sql_add_col_designer_status := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='designer_status') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `designer_status` varchar(50) DEFAULT ''待接单'' COMMENT ''设计师接单状态''',
  'SELECT 1'
);
PREPARE stmt_add_col_designer_status FROM @sql_add_col_designer_status;
EXECUTE stmt_add_col_designer_status;
DEALLOCATE PREPARE stmt_add_col_designer_status;

SET @sql_add_col_designer_take_time := IF(
  (SELECT COUNT(1)
   FROM information_schema.columns
   WHERE table_schema=@db AND table_name='cosorder' AND column_name='designer_take_time') = 0,
  'ALTER TABLE `cosorder` ADD COLUMN `designer_take_time` datetime DEFAULT NULL COMMENT ''认领时间''',
  'SELECT 1'
);
PREPARE stmt_add_col_designer_take_time FROM @sql_add_col_designer_take_time;
EXECUTE stmt_add_col_designer_take_time;
DEALLOCATE PREPARE stmt_add_col_designer_take_time;

/* 3) 历史数据回填 */
UPDATE `cosorder`
SET `designer_status` = '待接单'
WHERE `designer_status` IS NULL OR `designer_status` = '';

/* 4) 索引 */
SET @sql_add_idx_designer_status := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cosorder' AND index_name='idx_cosorder_designer_status') = 0,
  'ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_designer_status` (`designer_status`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_designer_status FROM @sql_add_idx_designer_status;
EXECUTE stmt_add_idx_designer_status;
DEALLOCATE PREPARE stmt_add_idx_designer_status;

SET @sql_add_idx_designer_id := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cosorder' AND index_name='idx_cosorder_designer_id') = 0,
  'ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_designer_id` (`designer_id`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_designer_id FROM @sql_add_idx_designer_id;
EXECUTE stmt_add_idx_designer_id;
DEALLOCATE PREPARE stmt_add_idx_designer_id;

SET @sql_add_idx_pay_designer := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cosorder' AND index_name='idx_cosorder_pay_designer') = 0,
  'ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_pay_designer` (`pay_status`, `designer_status`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_pay_designer FROM @sql_add_idx_pay_designer;
EXECUTE stmt_add_idx_pay_designer;
DEALLOCATE PREPARE stmt_add_idx_pay_designer;
