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
-- Table structure for table `remaicosfu`
--

DROP TABLE IF EXISTS `remaicosfu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remaicosfu` (
  `id` bigint(20) NOT NULL,
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fuzhuangbianhao` varchar(200) DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) DEFAULT NULL COMMENT '服装名称',
  `huawentuan` varchar(200) DEFAULT NULL COMMENT '花纹图案',
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='热卖cos服';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remaicosfu`
--

LOCK TABLES `remaicosfu` WRITE;
/*!40000 ALTER TABLE `remaicosfu` DISABLE KEYS */;
INSERT INTO `remaicosfu` VALUES (9001,'2026-03-03 14:59:35','COS001','初音未来套装','upload/cos1.jpg','洛丽塔','雪纺','双排扣','M',399,'详情1',0,0,'2026-03-11 09:07:32',101),(9002,'2026-03-03 14:59:35','COS002','鬼灭之刃套装','upload/cos2.jpg','和风','棉麻','立领','L',459,'详情2',0,0,'2026-03-08 22:15:27',147),(9003,'2026-03-03 14:59:35','COS003','原神角色服','upload/cos3.jpg','游戏角色','涤纶','开襟','S',499,'详情3',0,0,'2026-03-11 09:07:51',74),(9101,'2026-03-08 14:35:52','COS-101','鬼灭之刃 - 灶门炭治郎战斗服','upload/COS-101.jpg','战斗服','涤棉混纺','对襟','S/M/L/XL',399,'适用场景：Cosplay、漫展、舞台拍摄；设计特点：黑绿格纹羽织、深色内搭、束带层次明显、少年热血风；推荐人群：喜欢战斗系少年角色、偏好日式热血题材用户；相关角色：灶门炭治郎；相关作品：鬼灭之刃；关键词：和风、格纹、战斗服、少年系、热血。',0,0,'2026-05-16 10:27:01',270),(9102,'2026-03-08 14:35:52','COS-102','鬼灭之刃 - 祢豆子和服','upload/COS-102.jpg','和服','锦棉提花','交领','S/M/L/XL',369,'适用场景：Cosplay、写真拍摄、节日活动；设计特点：粉色麻叶纹和服、黑色外褂、蝴蝶结腰封、可爱少女感；推荐人群：喜欢和风少女角色、偏好柔和色系用户；相关角色：灶门祢豆子；相关作品：鬼灭之刃；关键词：和服、粉色、麻叶纹、少女、日式。',0,0,'2026-03-08 23:04:35',153),(9103,'2026-03-08 14:35:52','COS-103','原神 - 雷电将军战袍','upload/COS-103.jpg','战斗服','弹力莱卡','立领','S/M/L/XL',699,'适用场景：Cosplay、漫展舞台、角色出片；设计特点：紫黑主色、金属装饰、不对称剪裁、战斗气质强烈；推荐人群：喜欢游戏高人气角色、偏好华丽战斗风用户；相关角色：雷电将军；相关作品：原神；关键词：战斗服、紫色、雷元素、华丽、游戏角色。',0,0,'2026-05-16 01:44:20',216),(9104,'2026-03-08 14:35:52','COS-104','原神 - 神里绫华礼装','upload/COS-104.jpg','礼装','双宫缎','开襟','S/M/L/XL',659,'适用场景：Cosplay、舞台表演、主题写真；设计特点：蓝白礼装、裙摆层次丰富、飘带灵动、整体精致优雅；推荐人群：喜欢高雅大小姐角色、偏好轻礼服风格用户；相关角色：神里绫华；相关作品：原神；关键词：礼装、蓝白配色、优雅、游戏角色、舞台。',0,0,'2026-03-09 15:16:32',190),(9105,'2026-03-08 14:35:52','COS-105','咒术回战 - 五条悟制服','upload/COS-105.jpg','学院制服','TC制服呢','立领','S/M/L/XL',429,'适用场景：Cosplay、街拍、现代风写真；设计特点：深色高领制服、线条利落、整体简洁、都市感强；推荐人群：喜欢现代帅气角色、偏好极简制服风用户；相关角色：五条悟；相关作品：咒术回战；关键词：制服、现代、学院、简约、帅气。',0,0,'2026-05-16 00:08:16',178),(9106,'2026-03-08 14:35:52','COS-106','间谍过家家 - 阿尼亚学院制服','upload/COS-106.jpg','学院制服','全棉斜纹','翻领','S/M/L/XL',299,'适用场景：Cosplay、校园主题活动、日常改良穿搭；设计特点：学院感外套、衬衫与领结组合、配色清爽、辨识度高；推荐人群：喜欢校园可爱系角色、偏好轻量穿搭用户；相关角色：阿尼亚；相关作品：间谍过家家；关键词：学院制服、校园、可爱、白绿配色、少女。',0,0,'2026-05-06 23:57:40',196),(9107,'2026-03-08 14:35:52','COS-107','海贼王 - 路飞冒险服','upload/COS-107.jpg','冒险服','涤棉混纺','开襟','S/M/L/XL',259,'适用场景：Cosplay、主题派对、轻户外拍摄；设计特点：红色短上衣、蓝色短裤、黄色束带、整体轻松热血；推荐人群：喜欢冒险热血题材、偏好舒适穿着用户；相关角色：蒙奇·D·路飞；相关作品：海贼王；关键词：冒险服、热血、红蓝配色、经典动漫、轻便。',0,0,'2026-03-08 23:07:37',186),(9108,'2026-03-08 14:35:52','COS-108','初音未来 - 偶像打歌服','upload/COS-108.jpg','偶像打歌服','哑光色丁','双排扣','S/M/L/XL',499,'适用场景：舞台表演、Cosplay、打歌拍摄；设计特点：偶像感短裙、亮面装饰、配色活泼、舞台表现力强；推荐人群：喜欢虚拟偶像、偏好舞台系服装用户；相关角色：初音未来；相关作品：Vocaloid；关键词：偶像、打歌服、舞台、活力、二次元。',0,0,'2026-05-16 15:57:00',217),(9109,'2026-03-08 14:35:52','COS-109','明日方舟 - 阿米娅作战服','upload/COS-109.jpg','战斗服','人造皮革','翻领','S/M/L/XL',569,'适用场景：Cosplay、舞台表演、硬朗风写真；设计特点：作战感剪裁、暗色拼接、层次装备感、角色识别度高；推荐人群：喜欢未来战术风、偏好游戏制服用户；相关角色：阿米娅；相关作品：明日方舟；关键词：战斗服、作战风、未来感、暗色系、游戏。',0,0,'2026-05-06 23:52:45',174),(9110,'2026-03-08 14:35:52','COS-110','Re:从零开始 - 蕾姆女仆装','upload/COS-110.jpg','女仆装','哑光色丁','圆领','S/M/L/XL',389,'适用场景：Cosplay、主题咖啡活动、写真拍摄；设计特点：黑白女仆裙、围裙层次、荷叶边装饰、甜系经典；推荐人群：喜欢经典女仆系角色、偏好甜美二次元风用户；相关角色：蕾姆；相关作品：Re:从零开始的异世界生活；关键词：女仆装、黑白、甜美、围裙、经典。',0,0,'2026-05-16 15:17:17',186),(9111,'2026-03-08 14:35:52','COS-111','哈利波特 - 霍格沃茨披风斗篷','upload/COS-111.jpg','披风斗篷','轻薄斗篷布','披风领','S/M/L/XL',459,'适用场景：主题活动、学院风写真、舞台表演；设计特点：学院披风、徽章元素、内外层反差色、沉浸感强；推荐人群：喜欢魔法学院题材、偏好披风造型用户；相关角色：霍格沃茨学院系角色；相关作品：哈利波特；关键词：披风、斗篷、学院、魔法、主题活动。',0,0,'2026-05-16 01:31:28',156),(9112,'2026-03-08 14:35:52','COS-112','约会大作战 - 时崎狂三洛丽塔','upload/COS-112.jpg','洛丽塔','天鹅绒','立领','S/M/L/XL',539,'适用场景：Cosplay、哥特主题拍摄、舞台走秀；设计特点：多层裙摆、暗色蕾丝、钟表感装饰、哥特甜酷兼具；推荐人群：喜欢哥特系二次元角色、偏好繁复裙装用户；相关角色：时崎狂三；相关作品：约会大作战；关键词：洛丽塔、哥特、蕾丝、暗色系、礼裙。',0,0,'2026-05-16 14:56:18',181);
/*!40000 ALTER TABLE `remaicosfu` ENABLE KEYS */;
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
