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
-- Table structure for table `yonghu`
--

DROP TABLE IF EXISTS `yonghu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yonghu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yonghuzhanghao` varchar(200) DEFAULT NULL COMMENT '用户账号',
  `mima` varchar(200) NOT NULL COMMENT '密码',
  `yonghuxingming` varchar(200) DEFAULT NULL COMMENT '用户姓名',
  `touxiang` longtext COMMENT '头像',
  `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
  `lianxifangshi` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `shentishuju` longtext COMMENT '身体数据',
  PRIMARY KEY (`id`),
  UNIQUE KEY `yonghuzhanghao` (`yonghuzhanghao`)
) ENGINE=InnoDB AUTO_INCREMENT=1778866984590 DEFAULT CHARSET=utf8 COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yonghu`
--

LOCK TABLES `yonghu` WRITE;
/*!40000 ALTER TABLE `yonghu` DISABLE KEYS */;
INSERT INTO `yonghu` VALUES (11,'2023-03-07 14:24:32','用户账号1','123456','用户姓名1','upload/yonghu_touxiang1.jpg','男','13823888881','身体数据1'),(12,'2023-03-07 14:24:32','用户账号2','123456','用户姓名2','upload/yonghu_touxiang2.jpg','男','13823888882','身体数据2'),(13,'2023-03-07 14:24:32','用户账号3','123456','用户姓名3','upload/yonghu_touxiang3.jpg','男','13823888883','身体数据3'),(14,'2023-03-07 14:24:32','用户账号4','123456','用户姓名4','upload/yonghu_touxiang4.jpg','男','13823888884','身体数据4'),(15,'2023-03-07 14:24:32','用户账号5','123456','用户姓名5','upload/yonghu_touxiang5.jpg','男','13823888885','身体数据5'),(16,'2023-03-07 14:24:32','用户账号6','123456','用户姓名6','upload/yonghu_touxiang6.jpg','男','13823888886','身体数据6'),(17,'2023-03-07 14:24:32','用户账号7','123456','用户姓名7','upload/yonghu_touxiang7.jpg','男','13823888887','身体数据7'),(18,'2023-03-07 14:24:32','用户账号8','123456','用户姓名8','upload/yonghu_touxiang8.jpg','男','13823888888','身体数据8'),(1772331312076,'2026-03-01 02:15:12','test1','test1','qiu2','upload/1778865639374.ico','保密','',''),(1772339924353,'2026-03-01 04:38:44','test2','test2','yi','','','',NULL),(1772456543851,'2026-03-02 13:02:23','u0302210223','123456','TestUser',NULL,'?','13800138000','170/90/70'),(1772456730677,'2026-03-02 13:05:30','u72456730','123456','测试用户6730',NULL,'保密','13440749538','身高170cm，体重60kg'),(1772456809953,'2026-03-02 13:06:49','u72456809','123456','测试用户6809',NULL,'保密','13786993655','身高170cm，体重60kg'),(1772456876386,'2026-03-02 13:07:56','u72456876','123456','测试用户6876',NULL,'保密','13665421812','身高170cm，体重60kg'),(1772456927954,'2026-03-02 13:08:47','u72456927','123456','测试用户6927',NULL,'保密','13932233338','身高170cm，体重60kg'),(1772457010784,'2026-03-02 13:10:10','u57010746','123456','??0746',NULL,'??','13262713390','??170cm???60kg | smoke-profile'),(1773168089576,'2026-03-10 18:41:29','aiuser_1773168089532','123456','????1773168089532','','?','13800000000',NULL),(1778866676357,'2026-05-15 17:37:56','12334567890','123456','一一一','','女','12345453322',NULL),(1778866984589,'2026-05-15 17:43:04','145515','123456','五五','upload/1778866975474.jpg','男','17817282728',NULL);
/*!40000 ALTER TABLE `yonghu` ENABLE KEYS */;
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
