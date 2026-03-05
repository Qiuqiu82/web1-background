-- 文件名建议：cos_week2_order_collab_mysql57.sql
-- 适用版本：MySQL 5.7
-- 说明：请在业务低峰执行；若索引已存在，重复执行 ADD INDEX 语句会报错，可手动跳过。

/* 1) 新增订单状态日志表 */
CREATE TABLE IF NOT EXISTS `cosorder_status_log` (
  `id` bigint(20) NOT NULL,
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `from_pay_status` varchar(50) DEFAULT NULL COMMENT '变更前支付状态',
  `to_pay_status` varchar(50) DEFAULT NULL COMMENT '变更后支付状态',
  `from_order_status` varchar(50) DEFAULT NULL COMMENT '变更前履约状态',
  `to_order_status` varchar(50) DEFAULT NULL COMMENT '变更后履约状态',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_role` varchar(50) DEFAULT NULL COMMENT '操作人角色编码(ADMIN/USER/DESIGNER)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_cosorder_status_log_order` (`order_id`),
  KEY `idx_cosorder_status_log_addtime` (`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS订单状态流转日志';

/* 2) cosorder 增补索引（若不存在则执行） */
ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_no` (`order_no`);
ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_pay_status` (`pay_status`);
ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_order_status` (`order_status`);
ALTER TABLE `cosorder` ADD INDEX `idx_cosorder_addtime` (`addtime`);
