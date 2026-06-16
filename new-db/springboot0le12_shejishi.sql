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
-- Table structure for table `shejishi`
--

DROP TABLE IF EXISTS `shejishi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shejishi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `shejishizhanghao` varchar(200) DEFAULT NULL COMMENT '设计师账号',
  `mima` varchar(200) NOT NULL COMMENT '密码',
  `shejishixingming` varchar(200) DEFAULT NULL COMMENT '设计师姓名',
  `touxiang` longtext COMMENT '头像',
  `lianxifangshi` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `zhuanchang` varchar(255) DEFAULT NULL COMMENT '擅长风格',
  `jianjie` longtext COMMENT '简介',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shejishi_zhanghao` (`shejishizhanghao`)
) ENGINE=InnoDB AUTO_INCREMENT=1778079749480 DEFAULT CHARSET=utf8 COMMENT='设计师';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shejishi`
--

LOCK TABLES `shejishi` WRITE;
/*!40000 ALTER TABLE `shejishi` DISABLE KEYS */;
INSERT INTO `shejishi` VALUES (301,'2026-03-05 12:41:41','designer01','123456','设计师A',NULL,'13800138001','礼服/修身西装','擅长礼服与修身版型搭配'),(302,'2026-03-05 12:41:41','designer02','123456','设计师B',NULL,'13800138002','商务/通勤西装','擅长商务通勤与版型优化'),(1772735930268,'2026-03-05 18:38:50','de1','123','de1','','qq:3204953276','女仆装','爱好四分娃衣'),(1778079749479,'2026-05-06 15:02:29','18512364930','123456','一二','','18512364930','cos','擅长四分娃衣制作');
/*!40000 ALTER TABLE `shejishi` ENABLE KEYS */;
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
