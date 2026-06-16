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
-- Table structure for table `cos_designer_portfolio_order_rel`
--

DROP TABLE IF EXISTS `cos_designer_portfolio_order_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cos_designer_portfolio_order_rel` (
  `id` bigint(20) NOT NULL,
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `portfolio_id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `order_no` varchar(64) NOT NULL,
  `designer_id` bigint(20) NOT NULL,
  `designer_table` varchar(64) NOT NULL DEFAULT 'shejishi',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_portfolio_order` (`portfolio_id`,`order_id`),
  KEY `idx_rel_portfolio` (`portfolio_id`),
  KEY `idx_rel_order` (`order_id`),
  KEY `idx_rel_designer` (`designer_id`,`designer_table`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cos_designer_portfolio_order_rel`
--

LOCK TABLES `cos_designer_portfolio_order_rel` WRITE;
/*!40000 ALTER TABLE `cos_designer_portfolio_order_rel` DISABLE KEYS */;
INSERT INTO `cos_designer_portfolio_order_rel` VALUES (3556928140817,'2026-05-15 12:37:06',1778848594720,1778079514325,'COS20260506225833',1778079749479,'shejishi');
/*!40000 ALTER TABLE `cos_designer_portfolio_order_rel` ENABLE KEYS */;
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
