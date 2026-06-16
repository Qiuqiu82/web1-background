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
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `tablename` varchar(100) DEFAULT NULL COMMENT '表名',
  `role` varchar(100) DEFAULT NULL COMMENT '角色',
  `token` varchar(200) NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (1,1772331312076,'test1','yonghu','用户','vm9acbr8yotkhxga7gls5xzvg5abhme3','2026-03-01 02:15:19','2026-05-16 09:26:27'),(2,1,'admin','users','管理员','0cah72da4kx9rwrh04e18lfstjqwzp33','2026-03-01 02:16:05','2026-05-16 08:06:01'),(3,1772339924353,'test2','yonghu','用户','ut3jxcqiox22gwd7pvv6hcjbxwb98srr','2026-03-01 04:38:52','2026-03-01 05:38:52'),(4,1772456543851,'u0302210223','yonghu','用户','lp3zcw35unpgo9owkq6s9pb5o5lyxgcy','2026-03-02 13:02:23','2026-03-02 14:02:24'),(5,11,'用户账号1','yonghu','用户','6v80qwqc5ycmmpbawgawwpec67reel6u','2026-03-02 13:03:53','2026-03-02 14:07:11'),(6,1772456730677,'u72456730','yonghu','用户','h2elyxv5u6rnbu4afhlbxp80b5r6midj','2026-03-02 13:05:30','2026-03-02 14:05:31'),(7,1772456809953,'u72456809','yonghu','用户','kznen9g55vi3xny58k6eh7prd9pf8mzv','2026-03-02 13:06:49','2026-03-02 14:06:50'),(8,1772456876386,'u72456876','yonghu','用户','ykqha7j78i2uobgutlo5i4ckd2lxeker','2026-03-02 13:07:56','2026-03-02 14:07:56'),(9,1772456927954,'u72456927','yonghu','用户','tx3zqsyrsmpccrke9d6l4kcgcs0zlxgg','2026-03-02 13:08:47','2026-03-02 14:08:48'),(10,1772457010784,'u57010746','yonghu','用户','e06asdno1ghatuqi0cu1rv5kwpdndxvz','2026-03-02 13:10:10','2026-03-02 14:11:00'),(11,301,'designer01','shejishi','DESIGNER','y5kb65oybx9c737fksof786kjwvasbwx','2026-03-05 13:11:03','2026-03-05 19:07:28'),(12,1772735930268,'de1','shejishi','DESIGNER','940x2bgbja50pi4i5ss2238d0h95yvif','2026-03-05 18:38:57','2026-04-10 08:31:55'),(13,1773168089576,'aiuser_1773168089532','yonghu','用户','p4lzvr1jfayj9r3vem4bfxms3v6tuoid','2026-03-10 18:41:29','2026-03-10 19:41:30'),(14,1778079749479,'18512364930','shejishi','DESIGNER','ffms0n1jbf5lvdqyo1r6opxdqqmsocw3','2026-05-06 15:02:35','2026-05-16 09:30:52'),(15,1778866676357,'12334567890','yonghu','用户','euhp1db6h8kilggl2qlqyjk53h5ki8d0','2026-05-15 17:38:05','2026-05-15 18:38:06'),(16,1778866984589,'145515','yonghu','用户','hxovyocldhiw4pxzlk1f9mq7hr7mm4dn','2026-05-15 17:43:10','2026-05-15 18:43:10');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
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
