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
-- Table structure for table `fuzhuangkuanshi`
--

DROP TABLE IF EXISTS `fuzhuangkuanshi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fuzhuangkuanshi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangkuanshi` varchar(200) DEFAULT NULL COMMENT '服装款式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8 COMMENT='服装款式';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fuzhuangkuanshi`
--

LOCK TABLES `fuzhuangkuanshi` WRITE;
/*!40000 ALTER TABLE `fuzhuangkuanshi` DISABLE KEYS */;
INSERT INTO `fuzhuangkuanshi` VALUES (21,'2023-03-07 14:24:32','服装款式1'),(22,'2023-03-07 14:24:32','服装款式2'),(23,'2023-03-07 14:24:32','服装款式3'),(24,'2023-03-07 14:24:32','服装款式4'),(25,'2023-03-07 14:24:32','服装款式5'),(26,'2023-03-07 14:24:32','服装款式6'),(27,'2023-03-07 14:24:32','服装款式7'),(28,'2023-03-07 14:24:32','服装款式8'),(101,'2026-03-08 14:35:53','战斗服'),(102,'2026-03-08 14:35:53','和服'),(103,'2026-03-08 14:35:53','礼装'),(104,'2026-03-08 14:35:53','学院制服'),(105,'2026-03-08 14:35:53','冒险服'),(106,'2026-03-08 14:35:53','偶像打歌服'),(107,'2026-03-08 14:35:53','女仆装'),(108,'2026-03-08 14:35:53','披风斗篷'),(109,'2026-03-08 14:35:53','洛丽塔');
/*!40000 ALTER TABLE `fuzhuangkuanshi` ENABLE KEYS */;
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
