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
-- Table structure for table `cos_designer_portfolio`
--

DROP TABLE IF EXISTS `cos_designer_portfolio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cos_designer_portfolio` (
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
  `sort_order` int(11) NOT NULL DEFAULT '0',
  `deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_portfolio_designer` (`designer_id`,`designer_table`,`deleted`),
  KEY `idx_portfolio_status` (`status`,`deleted`),
  KEY `idx_portfolio_sort` (`sort_order`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cos_designer_portfolio`
--

LOCK TABLES `cos_designer_portfolio` WRITE;
/*!40000 ALTER TABLE `cos_designer_portfolio` DISABLE KEYS */;
INSERT INTO `cos_designer_portfolio` VALUES (1772957767153,'2026-03-08 08:16:07',1772735930268,'shejishi','4分娃衣','upload/1773192053853.jpg','[\"upload/1772957784035.jpg\"]','女仆','','启用',0,0),(1773197810035,'2026-03-11 02:56:50',1772735930268,'shejishi','蓝白女仆','http://localhost:8080/springboot0le12/upload/1773197788357.jpg','[]','lolita','女仆装','启用',0,0),(1778848594720,'2026-05-15 12:36:34',1778079749479,'shejishi','娃衣','upload/1778848562648.jpg','[\"upload/1778848574344.jpg\"]','lolita','娃衣','启用',0,0),(1778915140012,'2026-05-16 07:05:40',1778079749479,'shejishi','cos服','upload/1778915131032.jpg','[\"upload/1778915134716.jpg\"]','公主风','','启用',0,0),(1778916091161,'2026-05-16 07:21:31',1778079749479,'shejishi','小香风','upload/1778916078559.jpg','[\"upload/1778916082570.jpg\"]','黑白,暗黑','','启用',0,0);
/*!40000 ALTER TABLE `cos_designer_portfolio` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 14:08:57
