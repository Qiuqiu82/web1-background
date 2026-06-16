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
-- Table structure for table `cos_user_address`
--

DROP TABLE IF EXISTS `cos_user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cos_user_address` (
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
  PRIMARY KEY (`id`),
  KEY `idx_cos_user_addr_user` (`user_id`,`user_table`,`deleted`,`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户地址簿';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cos_user_address`
--

LOCK TABLES `cos_user_address` WRITE;
/*!40000 ALTER TABLE `cos_user_address` DISABLE KEYS */;
INSERT INTO `cos_user_address` VALUES (1772881358510,'2026-03-07 11:02:38',1772331312076,'yonghu','球球球','18512364930','上海市','','浦东新区','上川路999号',0,'启用','2026-05-15 21:05:06',0),(1778850307690,'2026-05-15 13:05:06',1772331312076,'yonghu','一二','18512364930','江苏省','苏州市','平江路','平江路1号276',1,'启用','2026-05-16 00:42:44',0),(1778867053414,'2026-05-15 17:44:12',1778866984589,'yonghu','额','14323453234','上海市','','浦东新区','999',1,'启用','2026-05-16 01:44:12',0);
/*!40000 ALTER TABLE `cos_user_address` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 14:09:00
