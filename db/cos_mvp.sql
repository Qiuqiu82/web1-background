DROP TABLE IF EXISTS `coscart`;
CREATE TABLE `coscart` (
                           `id` bigint(20) NOT NULL,
                           `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
                           `user_table` varchar(100) DEFAULT NULL COMMENT '用户表',
                           `product_id` bigint(20) DEFAULT NULL COMMENT '商品ID(remaicosfu.id)',
                           `product_name` varchar(200) DEFAULT NULL COMMENT '商品名',
                           `product_cover` varchar(500) DEFAULT NULL COMMENT '封面图',
                           `specs` varchar(200) DEFAULT NULL COMMENT '规格(尺码等)',
                           `quantity` int(11) DEFAULT '1' COMMENT '数量',
                           `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
                           `amount` decimal(10,2) DEFAULT NULL COMMENT '小计',
                           `checked` int(11) DEFAULT '1' COMMENT '是否勾选',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS购物车';

DROP TABLE IF EXISTS `cosorder`;
CREATE TABLE `cosorder` (
                            `id` bigint(20) NOT NULL,
                            `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
                            `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
                            `user_table` varchar(100) DEFAULT NULL COMMENT '用户表',
                            `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总额',
                            `pay_status` varchar(50) DEFAULT '未支付' COMMENT '支付状态',
                            `order_status` varchar(50) DEFAULT '待确认' COMMENT '订单状态',
                            `pay_type` varchar(100) DEFAULT NULL COMMENT '支付方式',
                            `contact_name` varchar(100) DEFAULT NULL COMMENT '联系人',
                            `contact_phone` varchar(100) DEFAULT NULL COMMENT '联系电话',
                            `address` varchar(255) DEFAULT NULL COMMENT '地址',
                            `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                            `items_json` longtext COMMENT '订单商品快照JSON',
                            PRIMARY KEY (`id`),
                            KEY `idx_cosorder_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS订单';

DROP TABLE IF EXISTS `remaicosfu`;
CREATE TABLE `remaicosfu` (
                              `id` bigint(20) NOT NULL,
                              `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `fuzhuangbianhao` varchar(200) DEFAULT NULL COMMENT '服装编号',
                              `fuzhuangmingcheng` varchar(200) DEFAULT NULL COMMENT '服装名称',
                              `huawentuan` varchar(200) DEFAULT NULL COMMENT '花纹图案',
                              `fuzhuangkuanshi` varchar(200) DEFAULT NULL COMMENT '服装款式',
                              `mianliaoleibie` varchar(200) DEFAULT NULL COMMENT '面料类别',
                              `yimenjin` varchar(200) DEFAULT NULL COMMENT '衣门襟',
                              `chima` varchar(200) DEFAULT NULL COMMENT '尺码',
                              `fuzhuangjiage` int(11) DEFAULT NULL COMMENT '服装价格',
                              `fuzhuangxiangqing` longtext COMMENT '服装详情',
                              `thumbsupnum` int(11) DEFAULT '0' COMMENT '赞',
                              `crazilynum` int(11) DEFAULT '0' COMMENT '踩',
                              `clicktime` datetime DEFAULT NULL COMMENT '最近点击时间',
                              `clicknum` int(11) DEFAULT '0' COMMENT '点击次数',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='热卖cos服';

INSERT INTO `remaicosfu` VALUES
                             (9001,NOW(),'COS001','初音未来套装','upload/cos1.jpg','洛丽塔','雪纺','双排扣','M',399,'详情1',0,0,NOW(),88),
                             (9002,NOW(),'COS002','鬼灭之刃套装','upload/cos2.jpg','和风','棉麻','立领','L',459,'详情2',0,0,NOW(),120),
                             (9003,NOW(),'COS003','原神角色服','upload/cos3.jpg','游戏角色','涤纶','开襟','S',499,'详情3',0,0,NOW(),66);
