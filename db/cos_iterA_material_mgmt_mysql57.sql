-- 文件名：cos_iterA_material_mgmt_mysql57.sql
-- 适用版本：MySQL 5.7
-- 目标：迭代A（服装与素材管理）
-- 功能：素材资产管理 + 款式面料约束 + 历史数据回填

SET @db := DATABASE();

/* 1) 素材资产表 */
CREATE TABLE IF NOT EXISTS `cos_material_asset` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `material_code` varchar(64) DEFAULT NULL COMMENT '素材编码',
  `material_name` varchar(200) NOT NULL COMMENT '素材名称',
  `category_name` varchar(200) DEFAULT NULL COMMENT '面料类别',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图',
  `asset_urls` longtext COMMENT '素材资源URL(逗号分隔)',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '参考单价',
  `enable_status` varchar(20) DEFAULT '启用' COMMENT '启停状态(启用/停用)',
  `audit_status` varchar(20) DEFAULT '待审核' COMMENT '审核状态(待审核/审核通过/审核驳回)',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
  `version_no` int(11) DEFAULT '1' COMMENT '版本号',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除(0否1是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS素材资产表';

SET @sql_add_idx_asset_code := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_material_asset' AND index_name='idx_cos_material_asset_code') = 0,
  'ALTER TABLE `cos_material_asset` ADD INDEX `idx_cos_material_asset_code` (`material_code`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_asset_code FROM @sql_add_idx_asset_code;
EXECUTE stmt_add_idx_asset_code;
DEALLOCATE PREPARE stmt_add_idx_asset_code;

SET @sql_add_idx_asset_status := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_material_asset' AND index_name='idx_cos_material_asset_status') = 0,
  'ALTER TABLE `cos_material_asset` ADD INDEX `idx_cos_material_asset_status` (`enable_status`,`audit_status`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_asset_status FROM @sql_add_idx_asset_status;
EXECUTE stmt_add_idx_asset_status;
DEALLOCATE PREPARE stmt_add_idx_asset_status;

SET @sql_add_idx_asset_category := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_material_asset' AND index_name='idx_cos_material_asset_category') = 0,
  'ALTER TABLE `cos_material_asset` ADD INDEX `idx_cos_material_asset_category` (`category_name`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_asset_category FROM @sql_add_idx_asset_category;
EXECUTE stmt_add_idx_asset_category;
DEALLOCATE PREPARE stmt_add_idx_asset_category;

SET @sql_add_uk_asset_code := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_material_asset' AND index_name='uk_cos_material_asset_code') = 0,
  'ALTER TABLE `cos_material_asset` ADD UNIQUE KEY `uk_cos_material_asset_code` (`material_code`)',
  'SELECT 1'
);
PREPARE stmt_add_uk_asset_code FROM @sql_add_uk_asset_code;
EXECUTE stmt_add_uk_asset_code;
DEALLOCATE PREPARE stmt_add_uk_asset_code;

/* 2) 款式-面料约束表 */
CREATE TABLE IF NOT EXISTS `cos_style_material_rule` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `style_name` varchar(200) NOT NULL COMMENT '款式名称',
  `material_id` bigint(20) NOT NULL COMMENT '素材ID',
  `material_name` varchar(200) DEFAULT NULL COMMENT '素材名称快照',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否默认(0否1是)',
  `status` varchar(20) DEFAULT '启用' COMMENT '规则状态(启用/停用)',
  `priority` int(11) DEFAULT '100' COMMENT '优先级(越小越优先)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除(0否1是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='款式面料约束规则表';

SET @sql_add_idx_rule_style := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_style_material_rule' AND index_name='idx_cos_style_material_rule_style') = 0,
  'ALTER TABLE `cos_style_material_rule` ADD INDEX `idx_cos_style_material_rule_style` (`style_name`,`status`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_rule_style FROM @sql_add_idx_rule_style;
EXECUTE stmt_add_idx_rule_style;
DEALLOCATE PREPARE stmt_add_idx_rule_style;

SET @sql_add_idx_rule_material := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_style_material_rule' AND index_name='idx_cos_style_material_rule_material') = 0,
  'ALTER TABLE `cos_style_material_rule` ADD INDEX `idx_cos_style_material_rule_material` (`material_id`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_rule_material FROM @sql_add_idx_rule_material;
EXECUTE stmt_add_idx_rule_material;
DEALLOCATE PREPARE stmt_add_idx_rule_material;

SET @sql_add_uk_rule_style_material := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_style_material_rule' AND index_name='uk_cos_style_material_rule_style_material') = 0,
  'ALTER TABLE `cos_style_material_rule` ADD UNIQUE KEY `uk_cos_style_material_rule_style_material` (`style_name`,`material_id`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_uk_rule_style_material FROM @sql_add_uk_rule_style_material;
EXECUTE stmt_add_uk_rule_style_material;
DEALLOCATE PREPARE stmt_add_uk_rule_style_material;

/* 3) 历史面料类别 -> 素材资产回填 */
SET @seed_material := UNIX_TIMESTAMP(NOW()) * 1000000;
INSERT INTO `cos_material_asset`
(`id`,`addtime`,`material_code`,`material_name`,`category_name`,`cover_url`,`asset_urls`,`tags`,`unit_price`,`enable_status`,`audit_status`,`audit_remark`,`version_no`,`created_by`,`updated_by`,`updated_at`,`deleted`)
SELECT
  (@seed_material := @seed_material + 1) AS id,
  NOW(),
  CONCAT('MAT', LPAD((@seed_material % 1000000), 6, '0')),
  t.mianliaoleibie,
  t.mianliaoleibie,
  NULL,
  NULL,
  REPLACE(t.mianliaoleibie, '，', ','),
  t.mianliaojiage,
  '启用',
  '审核通过',
  '历史面料类别回填',
  1,
  NULL,
  NULL,
  NOW(),
  0
FROM (
  SELECT TRIM(`mianliaoleibie`) AS mianliaoleibie, CAST(MAX(`mianliaojiage`) AS DECIMAL(10,2)) AS mianliaojiage
  FROM `mianliaoleibie`
  WHERE `mianliaoleibie` IS NOT NULL AND TRIM(`mianliaoleibie`) <> ''
  GROUP BY TRIM(`mianliaoleibie`)
) t
WHERE NOT EXISTS (
  SELECT 1
  FROM `cos_material_asset` a
  WHERE a.`deleted` = 0 AND a.`material_name` = t.`mianliaoleibie`
);

/* 4) 历史款式-面料 -> 约束规则回填 */
SET @seed_rule := UNIX_TIMESTAMP(NOW()) * 1000000 + 500000;
INSERT INTO `cos_style_material_rule`
(`id`,`addtime`,`style_name`,`material_id`,`material_name`,`is_default`,`status`,`priority`,`remark`,`created_by`,`updated_by`,`updated_at`,`deleted`)
SELECT
  (@seed_rule := @seed_rule + 1) AS id,
  NOW(),
  t.fuzhuangkuanshi,
  a.id,
  t.mianliaoleibie,
  1,
  '启用',
  100,
  '历史款式面料回填',
  NULL,
  NULL,
  NOW(),
  0
FROM (
  SELECT DISTINCT TRIM(`fuzhuangkuanshi`) AS fuzhuangkuanshi, TRIM(`mianliaoleibie`) AS mianliaoleibie
  FROM `remaicosfu`
  WHERE `fuzhuangkuanshi` IS NOT NULL AND TRIM(`fuzhuangkuanshi`) <> ''
    AND `mianliaoleibie` IS NOT NULL AND TRIM(`mianliaoleibie`) <> ''
) t
INNER JOIN `cos_material_asset` a
  ON a.`deleted` = 0 AND a.`material_name` = t.`mianliaoleibie`
WHERE NOT EXISTS (
  SELECT 1
  FROM `cos_style_material_rule` r
  WHERE r.`deleted` = 0
    AND r.`style_name` = t.`fuzhuangkuanshi`
    AND r.`material_id` = a.`id`
);