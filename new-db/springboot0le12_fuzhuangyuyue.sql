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
-- Table structure for table `fuzhuangyuyue`
--

DROP TABLE IF EXISTS `fuzhuangyuyue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fuzhuangyuyue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangbianhao` varchar(200) DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) DEFAULT NULL COMMENT '服装名称',
  `fuzhuangkuanshi` varchar(200) DEFAULT NULL COMMENT '服装款式',
  `mianliaoleibie` varchar(200) DEFAULT NULL COMMENT '面料类别',
  `fuwujiage` int(11) DEFAULT NULL COMMENT '服务价格',
  `shentishuju` longtext COMMENT '身体数据',
  `yuyueshijian` datetime DEFAULT NULL COMMENT '预约时间',
  `yonghuzhanghao` varchar(200) DEFAULT NULL COMMENT '用户账号',
  `yonghuxingming` varchar(200) DEFAULT NULL COMMENT '用户姓名',
  `lianxifangshi` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `sfsh` varchar(200) DEFAULT '待审核' COMMENT '是否审核',
  `shhf` longtext COMMENT '审核回复',
  `ispay` varchar(200) DEFAULT '未支付' COMMENT '是否支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1772587310040 DEFAULT CHARSET=utf8 COMMENT='服装预约';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fuzhuangyuyue`
--

LOCK TABLES `fuzhuangyuyue` WRITE;
/*!40000 ALTER TABLE `fuzhuangyuyue` DISABLE KEYS */;
INSERT INTO `fuzhuangyuyue` VALUES (51,'2023-03-07 14:24:32','服装编号1','服装名称1','服装款式1','面料类别1',1,'身体数据1','2023-03-07 22:24:32','用户账号1','用户姓名1','联系方式1','是','','未支付'),(52,'2023-03-07 14:24:32','服装编号2','服装名称2','服装款式2','面料类别2',2,'身体数据2','2023-03-07 22:24:32','用户账号2','用户姓名2','联系方式2','是','','未支付'),(53,'2023-03-07 14:24:32','服装编号3','服装名称3','服装款式3','面料类别3',3,'身体数据3','2023-03-07 22:24:32','用户账号3','用户姓名3','联系方式3','是','','未支付'),(54,'2023-03-07 14:24:32','服装编号4','服装名称4','服装款式4','面料类别4',4,'身体数据4','2023-03-07 22:24:32','用户账号4','用户姓名4','联系方式4','是','','未支付'),(55,'2023-03-07 14:24:32','服装编号5','服装名称5','服装款式5','面料类别5',5,'身体数据5','2023-03-07 22:24:32','用户账号5','用户姓名5','联系方式5','是','','未支付'),(56,'2023-03-07 14:24:32','服装编号6','服装名称6','服装款式6','面料类别6',6,'身体数据6','2023-03-07 22:24:32','用户账号6','用户姓名6','联系方式6','是','','未支付'),(57,'2023-03-07 14:24:32','服装编号7','服装名称7','服装款式7','面料类别7',7,'身体数据7','2023-03-07 22:24:32','用户账号7','用户姓名7','联系方式7','是','','未支付'),(58,'2023-03-07 14:24:32','服装编号8','服装名称8','服装款式8','面料类别8',8,'身体数据8','2023-03-07 22:24:32','用户账号8','用户姓名8','联系方式8','是','','未支付'),(1772455912260,'2026-03-02 12:51:51','1111111111','服装名称1','服装款式1','面料类别1',1,'160\n定制备注：xda','2026-03-02 00:00:00','test1','qiu','待补充','待审核','等待设计师确认','未支付'),(1772456928210,'2026-03-02 13:08:48','1111111111','SMK-1772456927','Ã¦ÂÂÃ¨Â£ÂÃ¦Â¬Â¾Ã¥Â¼Â1','Ã©ÂÂ¢Ã¦ÂÂÃ§Â±Â»Ã¥ÂÂ«1',1,'èº«é«170cmï¼ä½é60kgï¼åä¿®èº«','2026-03-05 21:08:48','u72456927','Ã¦ÂµÂÃ¨Â¯ÂÃ§ÂÂ¨Ã¦ÂÂ·6927','13932233338','å¾å®¡æ ¸','ç­å¾å¤ç','已支付'),(1772457011068,'2026-03-02 13:10:10','1111111111','SMK-1772457010746','服装款式1','面料类别1',1,'??170cm???60kg????','2026-03-05 13:10:10','u57010746','??0746','13262713390','???','????','???'),(1772587310039,'2026-03-04 01:21:49','8888888888','服装名称8','服装款式8','面料类别8',8,'11\n定制备注：11','2026-03-02 00:00:00','test1','qiu','待补充','待审核','等待设计师确认','未支付');
/*!40000 ALTER TABLE `fuzhuangyuyue` ENABLE KEYS */;
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
