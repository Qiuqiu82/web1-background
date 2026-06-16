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
-- Table structure for table `remaixifu`
--

DROP TABLE IF EXISTS `remaixifu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remaixifu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangbianhao` varchar(200) DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) DEFAULT NULL COMMENT '服装名称',
  `huawentuan` longtext COMMENT '花纹图案',
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `fuzhuangbianhao` (`fuzhuangbianhao`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8 COMMENT='热卖西服';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remaixifu`
--

LOCK TABLES `remaixifu` WRITE;
/*!40000 ALTER TABLE `remaixifu` DISABLE KEYS */;
INSERT INTO `remaixifu` VALUES (41,'2023-03-07 14:24:32','1111111111','服装名称1','upload/remaixifu_huawentuan1.jpg,upload/remaixifu_huawentuan2.jpg,upload/remaixifu_huawentuan3.jpg','服装款式1','面料类别1','衣门襟1','尺码1',1,'服装详情1',1,1,'2026-03-02 21:10:11',6),(42,'2023-03-07 14:24:32','2222222222','服装名称2','upload/remaixifu_huawentuan2.jpg,upload/remaixifu_huawentuan3.jpg,upload/remaixifu_huawentuan4.jpg','服装款式2','面料类别2','衣门襟2','尺码2',2,'服装详情2',2,2,'2026-03-01 10:15:31',3),(43,'2023-03-07 14:24:32','3333333333','服装名称3','upload/remaixifu_huawentuan3.jpg,upload/remaixifu_huawentuan4.jpg,upload/remaixifu_huawentuan5.jpg','服装款式3','面料类别3','衣门襟3','尺码3',3,'服装详情3',3,3,'2023-03-07 22:24:32',3),(44,'2023-03-07 14:24:32','4444444444','服装名称4','upload/remaixifu_huawentuan4.jpg,upload/remaixifu_huawentuan5.jpg,upload/remaixifu_huawentuan6.jpg','服装款式4','面料类别4','衣门襟4','尺码4',4,'服装详情4',4,4,'2023-03-07 22:24:32',4),(45,'2023-03-07 14:24:32','5555555555','服装名称5','upload/remaixifu_huawentuan5.jpg,upload/remaixifu_huawentuan6.jpg,upload/remaixifu_huawentuan7.jpg','服装款式5','面料类别5','衣门襟5','尺码5',5,'服装详情5',5,5,'2023-03-07 22:24:32',5),(46,'2023-03-07 14:24:32','6666666666','服装名称6','upload/remaixifu_huawentuan6.jpg,upload/remaixifu_huawentuan7.jpg,upload/remaixifu_huawentuan8.jpg','服装款式6','面料类别6','衣门襟6','尺码6',6,'服装详情6',6,6,'2026-03-01 10:25:08',7),(47,'2023-03-07 14:24:32','7777777777','服装名称7','upload/remaixifu_huawentuan7.jpg,upload/remaixifu_huawentuan8.jpg,upload/remaixifu_huawentuan9.jpg','服装款式7','面料类别7','衣门襟7','尺码7',7,'服装详情7',7,7,'2023-03-07 22:24:32',7),(48,'2023-03-07 14:24:32','8888888888','服装名称8','upload/remaixifu_huawentuan8.jpg,upload/remaixifu_huawentuan9.jpg,upload/remaixifu_huawentuan10.jpg','服装款式8','面料类别8','衣门襟8','尺码8',8,'服装详情8',8,8,'2026-03-04 09:21:41',10);
/*!40000 ALTER TABLE `remaixifu` ENABLE KEYS */;
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
