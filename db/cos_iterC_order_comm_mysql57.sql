-- 文件名：cos_iterC_order_comm_mysql57.sql
-- 适用版本：MySQL 5.7
-- 说明：迭代C订单会话沟通与交付记录数据结构（幂等）
SET NAMES utf8;
SET @db := DATABASE();

CREATE TABLE IF NOT EXISTS `cos_order_message` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号快照',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `user_table` varchar(100) DEFAULT NULL COMMENT '用户表名',
  `designer_id` bigint(20) DEFAULT NULL COMMENT '设计师ID',
  `designer_table` varchar(100) DEFAULT NULL COMMENT '设计师表名',
  `sender_role` varchar(50) NOT NULL COMMENT '发送人角色(USER/DESIGNER)',
  `sender_id` bigint(20) NOT NULL COMMENT '发送人ID',
  `sender_name` varchar(200) DEFAULT NULL COMMENT '发送人名称快照',
  `message_type` varchar(30) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型(TEXT/DELIVERY)',
  `content` varchar(1000) NOT NULL COMMENT '消息内容',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS订单会话消息';

SET @sql_add_idx_order_time := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_order_message' AND index_name='idx_cos_order_message_order_time') = 0,
  'ALTER TABLE `cos_order_message` ADD INDEX `idx_cos_order_message_order_time` (`order_id`,`addtime`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_order_time FROM @sql_add_idx_order_time;
EXECUTE stmt_add_idx_order_time;
DEALLOCATE PREPARE stmt_add_idx_order_time;

SET @sql_add_idx_user := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_order_message' AND index_name='idx_cos_order_message_user') = 0,
  'ALTER TABLE `cos_order_message` ADD INDEX `idx_cos_order_message_user` (`user_id`,`user_table`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_user FROM @sql_add_idx_user;
EXECUTE stmt_add_idx_user;
DEALLOCATE PREPARE stmt_add_idx_user;

SET @sql_add_idx_designer := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_order_message' AND index_name='idx_cos_order_message_designer') = 0,
  'ALTER TABLE `cos_order_message` ADD INDEX `idx_cos_order_message_designer` (`designer_id`,`designer_table`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_designer FROM @sql_add_idx_designer;
EXECUTE stmt_add_idx_designer;
DEALLOCATE PREPARE stmt_add_idx_designer;

SET @sql_add_idx_type := IF(
  (SELECT COUNT(1)
   FROM information_schema.statistics
   WHERE table_schema=@db AND table_name='cos_order_message' AND index_name='idx_cos_order_message_type') = 0,
  'ALTER TABLE `cos_order_message` ADD INDEX `idx_cos_order_message_type` (`message_type`,`deleted`)',
  'SELECT 1'
);
PREPARE stmt_add_idx_type FROM @sql_add_idx_type;
EXECUTE stmt_add_idx_type;
DEALLOCATE PREPARE stmt_add_idx_type;