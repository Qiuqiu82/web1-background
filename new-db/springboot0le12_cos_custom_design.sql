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
-- Table structure for table `cos_custom_design`
--

DROP TABLE IF EXISTS `cos_custom_design`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cos_custom_design` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_table` varchar(100) NOT NULL COMMENT '用户表',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) DEFAULT NULL COMMENT '商品名称快照',
  `style_name` varchar(200) DEFAULT NULL COMMENT '款式名称快照',
  `design_summary` varchar(500) DEFAULT NULL COMMENT '定制摘要',
  `design_json` longtext COMMENT '结构化定制参数JSON',
  `size_code` varchar(50) DEFAULT NULL COMMENT '尺码',
  `material_name` varchar(200) DEFAULT NULL COMMENT '面料',
  `color_theme` varchar(200) DEFAULT NULL COMMENT '色系',
  `fit_type` varchar(100) DEFAULT NULL COMMENT '松量/版型偏好',
  `silhouette` varchar(100) DEFAULT NULL COMMENT '廓形',
  `craft_tags` varchar(500) DEFAULT NULL COMMENT '工艺标签(逗号分隔)',
  `accessory_tags` varchar(500) DEFAULT NULL COMMENT '配件标签(逗号分隔)',
  `reference_images` longtext COMMENT '参考图URL(逗号分隔)',
  `custom_note` varchar(1000) DEFAULT NULL COMMENT '补充说明',
  `status` varchar(50) DEFAULT '草稿' COMMENT '状态',
  `version_no` int(11) DEFAULT '1' COMMENT '草稿版本号',
  `last_used_at` datetime DEFAULT NULL COMMENT '最近用于下单时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_cos_custom_user_product` (`user_id`,`user_table`,`product_id`,`deleted`),
  KEY `idx_cos_custom_updated` (`updated_at`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS定制草稿';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cos_custom_design`
--

LOCK TABLES `cos_custom_design` WRITE;
/*!40000 ALTER TABLE `cos_custom_design` DISABLE KEYS */;
INSERT INTO `cos_custom_design` VALUES (1772859931719892,'2026-03-07 05:05:31',1772331312076,'yonghu',9001,'初音未来套装','洛丽塔','尺码:M / 面料:哑光色丁 / 身材:普 / 版型轮廓:合体 / 松量:合体 / 配色:蓝白 / 工艺:包边','{\"productId\":9001,\"productName\":\"初音未来套装\",\"styleName\":\"洛丽塔\",\"sizeCode\":\"M\",\"materialName\":\"哑光色丁\",\"silhouette\":\"合体\",\"fitType\":\"合体\",\"colorTheme\":\"蓝白\",\"craftTags\":[\"包边\"],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:哑光色丁 / 身材:普 / 版型轮廓:合体 / 松量:合体 / 配色:蓝白 / 工艺:包边\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-03-11T01:07:45.302Z\"}','M','哑光色丁','蓝白','合体','合体','包边',NULL,NULL,NULL,'Draft',5,NULL,'2026-03-11 09:07:45',0),(1772881378083277,'2026-03-07 11:02:58',1772331312076,'yonghu',9002,'鬼灭之刃套装','和风','尺码:M / 面料:PU亮面皮革 / 身材:普 / 版型轮廓:常规 / 松量:常规 / 配色:无 / 工艺:包边 / 配件:披风','{\"productId\":9002,\"productName\":\"鬼灭之刃套装\",\"styleName\":\"和风\",\"sizeCode\":\"M\",\"materialName\":\"PU亮面皮革\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"无\",\"craftTags\":[\"包边\"],\"accessoryTags\":[\"披风\"],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:PU亮面皮革 / 身材:普 / 版型轮廓:常规 / 松量:常规 / 配色:无 / 工艺:包边 / 配件:披风\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-03-08T07:56:08.092Z\"}','M','PU亮面皮革','无','常规','常规','包边','披风',NULL,NULL,'Draft',3,NULL,'2026-03-08 15:56:08',0),(1772957480872499,'2026-03-08 08:11:20',1772331312076,'yonghu',9003,'原神角色服','游戏角色','尺码:M / 面料:PU亮面皮革 / 身材:普 / 版型轮廓:常规 / 松量:常规','{\"productId\":9003,\"productName\":\"原神角色服\",\"styleName\":\"游戏角色\",\"sizeCode\":\"M\",\"materialName\":\"PU亮面皮革\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:PU亮面皮革 / 身材:普 / 版型轮廓:常规 / 松量:常规\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-03-11T01:07:53.884Z\"}','M','PU亮面皮革',NULL,'常规','常规',NULL,NULL,NULL,NULL,'Draft',2,NULL,'2026-03-11 09:07:53',0),(1773040563611724,'2026-03-09 07:16:03',1772331312076,'yonghu',9112,'约会大作战 - 时崎狂三洛丽塔','洛丽塔','尺码:M / 面料:天鹅绒 / 身材:普 / 版型轮廓:修身 / 松量:弹力 / 配色:红黑 / 工艺:包边|隐形拉链|拼色 / 配件:蝴蝶结|金属扣|可拆卸|披风','{\"productId\":9112,\"productName\":\"约会大作战 - 时崎狂三洛丽塔\",\"styleName\":\"洛丽塔\",\"sizeCode\":\"M\",\"materialName\":\"天鹅绒\",\"silhouette\":\"修身\",\"fitType\":\"弹力\",\"colorTheme\":\"红黑\",\"craftTags\":[\"包边\",\"隐形拉链\",\"拼色\"],\"accessoryTags\":[\"蝴蝶结\",\"金属扣\",\"可拆卸\",\"披风\"],\"referenceImages\":[],\"customNote\":\"花边需要更加华丽\",\"summary\":\"尺码:M / 面料:天鹅绒 / 身材:普 / 版型轮廓:修身 / 松量:弹力 / 配色:红黑 / 工艺:包边|隐形拉链|拼色 / 配件:蝴蝶结|金属扣|可拆卸|披风\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-16T06:56:29.041Z\"}','M','天鹅绒','红黑','弹力','修身','包边,隐形拉链,拼色','蝴蝶结,金属扣,可拆卸,披风',NULL,'花边需要更加华丽','Draft',7,NULL,'2026-05-16 14:56:29',0),(1773040585086963,'2026-03-09 07:16:25',1772331312076,'yonghu',9108,'初音未来 - 偶像打歌服','偶像打歌服','尺码:M / 面料:欧根纱 / 身材:普 / 版型轮廓:修身 / 松量:常规 / 工艺:暗扣|隐形拉链 / 配件:金属扣','{\"productId\":9108,\"productName\":\"初音未来 - 偶像打歌服\",\"styleName\":\"偶像打歌服\",\"sizeCode\":\"M\",\"materialName\":\"欧根纱\",\"silhouette\":\"修身\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[\"暗扣\",\"隐形拉链\"],\"accessoryTags\":[\"金属扣\"],\"referenceImages\":[],\"customNote\":\"多一些花边\",\"summary\":\"尺码:M / 面料:欧根纱 / 身材:普 / 版型轮廓:修身 / 松量:常规 / 工艺:暗扣|隐形拉链 / 配件:金属扣\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-16T06:59:09.468Z\"}','M','欧根纱',NULL,'常规','修身','暗扣,隐形拉链','金属扣',NULL,'多一些花边','Draft',5,NULL,'2026-05-16 14:59:09',0),(1773040593438961,'2026-03-09 07:16:33',1772331312076,'yonghu',9104,'原神 - 神里绫华礼装','礼装','尺码:M / 面料:哑光色丁 / 身材:普 / 版型轮廓:常规 / 松量:常规','{\"productId\":9104,\"productName\":\"原神 - 神里绫华礼装\",\"styleName\":\"礼装\",\"sizeCode\":\"M\",\"materialName\":\"哑光色丁\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:哑光色丁 / 身材:普 / 版型轮廓:常规 / 松量:常规\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-03-09T07:16:33.407Z\"}','M','哑光色丁',NULL,'常规','常规',NULL,NULL,NULL,NULL,'Draft',1,NULL,'2026-03-09 15:16:33',0),(1778082739568281,'2026-05-06 15:52:19',1772331312076,'yonghu',9111,'哈利波特 - 霍格沃茨披风斗篷','披风斗篷','尺码:M / 面料:轻薄斗篷布 / 身材:普 / 版型轮廓:常规 / 松量:常规 / 配色:经典 / 工艺:滚边 / 配件:披风|腰带','{\"productId\":9111,\"productName\":\"哈利波特 - 霍格沃茨披风斗篷\",\"styleName\":\"披风斗篷\",\"sizeCode\":\"M\",\"materialName\":\"轻薄斗篷布\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"经典\",\"craftTags\":[\"滚边\"],\"accessoryTags\":[\"披风\",\"腰带\"],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:轻薄斗篷布 / 身材:普 / 版型轮廓:常规 / 松量:常规 / 配色:经典 / 工艺:滚边 / 配件:披风|腰带\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-15T16:07:25.144Z\"}','M','轻薄斗篷布','经典','常规','常规','滚边','披风,腰带',NULL,NULL,'Draft',5,NULL,'2026-05-16 00:07:25',0),(1778082766731540,'2026-05-06 15:52:46',1772331312076,'yonghu',9109,'明日方舟 - 阿米娅作战服','战斗服','尺码:M / 面料:弹力莱卡 / 身材:普 / 版型轮廓:常规 / 松量:常规','{\"productId\":9109,\"productName\":\"明日方舟 - 阿米娅作战服\",\"styleName\":\"战斗服\",\"sizeCode\":\"M\",\"materialName\":\"弹力莱卡\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:弹力莱卡 / 身材:普 / 版型轮廓:常规 / 松量:常规\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-06T15:52:46.719Z\"}','M','弹力莱卡',NULL,'常规','常规',NULL,NULL,NULL,NULL,'Draft',1,NULL,'2026-05-06 23:52:46',0),(1778083061335516,'2026-05-06 15:57:41',1772331312076,'yonghu',9106,'间谍过家家 - 阿尼亚学院制服','学院制服','尺码:M / 面料:TC制服呢 / 身材:普 / 版型轮廓:常规 / 松量:常规','{\"productId\":9106,\"productName\":\"间谍过家家 - 阿尼亚学院制服\",\"styleName\":\"学院制服\",\"sizeCode\":\"M\",\"materialName\":\"TC制服呢\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:TC制服呢 / 身材:普 / 版型轮廓:常规 / 松量:常规\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-06T15:57:41.324Z\"}','M','TC制服呢',NULL,'常规','常规',NULL,NULL,NULL,NULL,'Draft',1,NULL,'2026-05-06 23:57:41',0),(1778861297766619,'2026-05-15 16:08:17',1772331312076,'yonghu',9105,'咒术回战 - 五条悟制服','学院制服','尺码:M / 面料:TC制服呢 / 身材:普 / 版型轮廓:常规 / 松量:常规','{\"productId\":9105,\"productName\":\"咒术回战 - 五条悟制服\",\"styleName\":\"学院制服\",\"sizeCode\":\"M\",\"materialName\":\"TC制服呢\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:TC制服呢 / 身材:普 / 版型轮廓:常规 / 松量:常规\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-15T16:08:17.751Z\"}','M','TC制服呢',NULL,'常规','常规',NULL,NULL,NULL,NULL,'Draft',1,NULL,'2026-05-16 00:08:17',0),(1778867066290834,'2026-05-15 17:44:26',1778866984589,'yonghu',9103,'原神 - 雷电将军战袍','战斗服','尺码:M / 面料:弹力莱卡 / 身材:瘦了 / 版型轮廓:常规 / 松量:常规 / 配色:饿 / 工艺:隐形拉链 / 配件:蝴蝶结','{\"productId\":9103,\"productName\":\"原神 - 雷电将军战袍\",\"styleName\":\"战斗服\",\"sizeCode\":\"M\",\"materialName\":\"弹力莱卡\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"饿\",\"craftTags\":[\"隐形拉链\"],\"accessoryTags\":[\"蝴蝶结\"],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:弹力莱卡 / 身材:瘦了 / 版型轮廓:常规 / 松量:常规 / 配色:饿 / 工艺:隐形拉链 / 配件:蝴蝶结\",\"bodyProfileId\":1778867029469,\"bodyProfileSnapshot\":{\"profileName\":\"瘦了\",\"heightCm\":160,\"weightKg\":45,\"waistCm\":64,\"bustCm\":87,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-15T17:44:26.279Z\"}','M','弹力莱卡','饿','常规','常规','隐形拉链','蝴蝶结',NULL,NULL,'Draft',1,NULL,'2026-05-16 01:44:26',0),(1778898474460922,'2026-05-16 02:27:54',1772331312076,'yonghu',9101,'鬼灭之刃 - 灶门炭治郎战斗服','战斗服','尺码:M / 面料:弹力莱卡 / 身材:普 / 版型轮廓:常规 / 松量:常规','{\"productId\":9101,\"productName\":\"鬼灭之刃 - 灶门炭治郎战斗服\",\"styleName\":\"战斗服\",\"sizeCode\":\"M\",\"materialName\":\"弹力莱卡\",\"silhouette\":\"常规\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[],\"accessoryTags\":[],\"referenceImages\":[],\"customNote\":\"\",\"summary\":\"尺码:M / 面料:弹力莱卡 / 身材:普 / 版型轮廓:常规 / 松量:常规\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-16T02:27:54.386Z\"}','M','弹力莱卡',NULL,'常规','常规',NULL,NULL,NULL,NULL,'Draft',1,NULL,'2026-05-16 10:27:54',0),(1778915857136072,'2026-05-16 07:17:37',1772331312076,'yonghu',9110,'Re:从零开始 - 蕾姆女仆装','女仆装','尺码:M / 面料:TC制服呢 / 身材:普 / 版型轮廓:修身 / 松量:常规 / 工艺:滚边|包边 / 配件:腰带','{\"productId\":9110,\"productName\":\"Re:从零开始 - 蕾姆女仆装\",\"styleName\":\"女仆装\",\"sizeCode\":\"M\",\"materialName\":\"TC制服呢\",\"silhouette\":\"修身\",\"fitType\":\"常规\",\"colorTheme\":\"\",\"craftTags\":[\"滚边\",\"包边\"],\"accessoryTags\":[\"腰带\"],\"referenceImages\":[],\"customNote\":\"多加一点花边\",\"summary\":\"尺码:M / 面料:TC制服呢 / 身材:普 / 版型轮廓:修身 / 松量:常规 / 工艺:滚边|包边 / 配件:腰带\",\"bodyProfileId\":1772881323569,\"bodyProfileSnapshot\":{\"profileName\":\"普\",\"heightCm\":158,\"weightKg\":45,\"waistCm\":64,\"bustCm\":40,\"hipCm\":87,\"shoulderCm\":38,\"sizeCode\":\"M\"},\"snapshotAt\":\"2026-05-16T07:17:37.115Z\"}','M','TC制服呢',NULL,'常规','修身','滚边,包边','腰带',NULL,'多加一点花边','Draft',1,NULL,'2026-05-16 15:17:37',0);
/*!40000 ALTER TABLE `cos_custom_design` ENABLE KEYS */;
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
