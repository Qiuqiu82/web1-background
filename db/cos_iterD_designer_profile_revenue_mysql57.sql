CREATE TABLE IF NOT EXISTS `cos_designer_portfolio` (
  `id` bigint(20) NOT NULL,
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `designer_id` bigint(20) NOT NULL,
  `designer_table` varchar(64) NOT NULL DEFAULT 'shejishi',
  `title` varchar(120) NOT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `image_list_json` text,
  `style_tags` varchar(255) DEFAULT NULL,
  `intro` text,
  `status` varchar(32) NOT NULL DEFAULT '启用',
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `deleted` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_portfolio_designer` (`designer_id`, `designer_table`, `deleted`),
  KEY `idx_portfolio_status` (`status`, `deleted`),
  KEY `idx_portfolio_sort` (`sort_order`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `cos_designer_portfolio_order_rel` (
  `id` bigint(20) NOT NULL,
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `portfolio_id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `order_no` varchar(64) NOT NULL,
  `designer_id` bigint(20) NOT NULL,
  `designer_table` varchar(64) NOT NULL DEFAULT 'shejishi',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_portfolio_order` (`portfolio_id`, `order_id`),
  KEY `idx_rel_portfolio` (`portfolio_id`),
  KEY `idx_rel_order` (`order_id`),
  KEY `idx_rel_designer` (`designer_id`, `designer_table`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;