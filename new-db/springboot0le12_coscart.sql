-- MySQL dump 10.13  Distrib 5.7.39, for Win64 (x86_64)
--
-- Host: localhost    Database: springboot0le12
-- ------------------------------------------------------
-- Server version	5.7.39-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `coscart`
--

DROP TABLE IF EXISTS `coscart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `custom_draft_id` bigint(20) DEFAULT NULL COMMENT '定制草稿ID',
  `custom_summary` varchar(500) DEFAULT NULL COMMENT '定制摘要',
  `custom_snapshot_json` longtext COMMENT '定制快照JSON',
  `custom_snapshot_version` int(11) DEFAULT '1' COMMENT '定制快照版本',
  PRIMARY KEY (`id`),
  KEY `idx_coscart_custom_draft_id` (`custom_draft_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coscart`
--

LOCK TABLES `coscart` WRITE;
/*!40000 ALTER TABLE `coscart` DISABLE KEYS */;
/*!40000 ALTER TABLE `coscart` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 14:08:59
