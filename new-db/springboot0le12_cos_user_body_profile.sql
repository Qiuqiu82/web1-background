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
-- Table structure for table `cos_user_body_profile`
--

DROP TABLE IF EXISTS `cos_user_body_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cos_user_body_profile` (
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
  PRIMARY KEY (`id`),
  KEY `idx_cos_user_body_user` (`user_id`,`user_table`,`deleted`,`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户身材档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cos_user_body_profile`
--

LOCK TABLES `cos_user_body_profile` WRITE;
/*!40000 ALTER TABLE `cos_user_body_profile` DISABLE KEYS */;
INSERT INTO `cos_user_body_profile` VALUES (1772881323569,'2026-03-07 11:02:02',1772331312076,'yonghu','普',158.00,45.00,64.00,40.00,87.00,38.00,1,'启用','2026-05-15 21:07:17',0),(1778850395323,'2026-05-15 13:06:34',1772331312076,'yonghu','胖了',158.00,45.00,67.00,79.00,86.00,38.00,0,'启用','2026-05-15 21:07:17',0),(1778866599718,'2026-05-15 17:36:38',1772331312076,'yonghu','瘦了',158.00,40.00,62.00,86.00,90.00,38.00,0,'启用','2026-05-16 01:36:38',0),(1778867029469,'2026-05-15 17:43:48',1778866984589,'yonghu','瘦了',160.00,45.00,64.00,87.00,87.00,38.00,1,'启用','2026-05-16 01:43:48',0);
/*!40000 ALTER TABLE `cos_user_body_profile` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 14:08:58
