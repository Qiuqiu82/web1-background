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
-- Table structure for table `cos_material_asset`
--

DROP TABLE IF EXISTS `cos_material_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cos_material_asset` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `material_code` varchar(64) DEFAULT NULL COMMENT '素材编码',
  `material_name` varchar(200) NOT NULL COMMENT '素材名称',
  `category_name` varchar(200) DEFAULT NULL COMMENT '面料类别',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图',
  `asset_urls` longtext COMMENT '素材资源URL(逗号分隔)',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '参考单价',
  `enable_status` varchar(20) DEFAULT '启用' COMMENT '启停状态(启用/停用)',
  `audit_status` varchar(20) DEFAULT '待审核' COMMENT '审核状态(待审核/审核通过/审核驳回)',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
  `version_no` int(11) DEFAULT '1' COMMENT '版本号',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除(0否1是)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cos_material_asset_code` (`material_code`),
  KEY `idx_cos_material_asset_code` (`material_code`),
  KEY `idx_cos_material_asset_status` (`enable_status`,`audit_status`,`deleted`),
  KEY `idx_cos_material_asset_category` (`category_name`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='COS素材资产表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cos_material_asset`
--

LOCK TABLES `cos_material_asset` WRITE;
/*!40000 ALTER TABLE `cos_material_asset` DISABLE KEYS */;
INSERT INTO `cos_material_asset` VALUES (1772856804000001,'2026-03-07 04:13:24','MAT000001','面料类别1','面料类别1',NULL,NULL,'面料类别1',1.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:14',1),(1772856804000002,'2026-03-07 04:13:24','MAT000002','面料类别2','面料类别2',NULL,NULL,'面料类别2',2.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:13',1),(1772856804000003,'2026-03-07 04:13:24','MAT000003','面料类别3','面料类别3',NULL,NULL,'面料类别3',3.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:11',1),(1772856804000004,'2026-03-07 04:13:24','MAT000004','面料类别4','面料类别4',NULL,NULL,'面料类别4',4.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:09',1),(1772856804000005,'2026-03-07 04:13:24','MAT000005','面料类别5','面料类别5',NULL,NULL,'面料类别5',5.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:07',1),(1772856804000006,'2026-03-07 04:13:24','MAT000006','面料类别6','面料类别6',NULL,NULL,'面料类别6',6.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:05',1),(1772856804000007,'2026-03-07 04:13:24','MAT000007','面料类别7','面料类别7',NULL,NULL,'面料类别7',7.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:02',1),(1772856804000008,'2026-03-07 04:13:24','MAT000008','面料类别8','面料类别8',NULL,NULL,'面料类别8',8.00,'启用','审核通过','历史面料类别回填',1,NULL,1,'2026-03-07 12:38:00',1),(1772858250001001,'2026-03-07 04:37:30','MAT-COS-001','纯棉府绸','棉布',NULL,NULL,'透气,亲肤,校服,衬衫',45.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001002,'2026-03-07 04:37:30','MAT-COS-002','全棉斜纹','棉布',NULL,NULL,'耐磨,挺括,日常COS',52.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001003,'2026-03-07 04:37:30','MAT-COS-003','涤棉混纺','混纺',NULL,NULL,'抗皱,易打理,性价比',38.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001004,'2026-03-07 04:37:30','MAT-COS-004','TC制服呢','制服呢',NULL,NULL,'JK,水手服,学院风',68.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001005,'2026-03-07 04:37:30','MAT-COS-005','TR西装料','西装面料',NULL,NULL,'执事,西装,垂感',88.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001006,'2026-03-07 04:37:30','MAT-COS-006','哑光色丁','缎面',NULL,NULL,'礼服,女仆装,光泽',42.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001007,'2026-03-07 04:37:30','MAT-COS-007','高密雪纺','雪纺',NULL,NULL,'轻薄,飘逸,层次',36.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001008,'2026-03-07 04:37:30','MAT-COS-008','欧根纱','网纱',NULL,NULL,'蓬度,蝴蝶结,裙撑',32.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001009,'2026-03-07 04:37:30','MAT-COS-009','针织罗纹','针织',NULL,NULL,'弹性,收口,领口袖口',28.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001010,'2026-03-07 04:37:30','MAT-COS-010','弹力莱卡','莱卡',NULL,NULL,'高弹,贴身,战斗服',58.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001011,'2026-03-07 04:37:30','MAT-COS-011','人造皮革','皮革',NULL,NULL,'盔甲风,腰封,硬挺',76.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001012,'2026-03-07 04:37:30','MAT-COS-012','PU亮面皮革','皮革',NULL,NULL,'机甲风,反光,舞台',89.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001013,'2026-03-07 04:37:30','MAT-COS-013','天鹅绒','绒布',NULL,NULL,'哥特,礼服,复古',64.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001014,'2026-03-07 04:37:30','MAT-COS-014','灯芯绒','绒布',NULL,NULL,'秋冬,复古,学院',55.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001015,'2026-03-07 04:37:30','MAT-COS-015','仿羊毛呢','毛呢',NULL,NULL,'斗篷,外套,巫师袍',98.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001016,'2026-03-07 04:37:30','MAT-COS-016','双宫缎','缎面',NULL,NULL,'旗袍,汉服,礼服',72.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001017,'2026-03-07 04:37:30','MAT-COS-017','锦纶塔丝隆','功能面料',NULL,NULL,'防风,轻量,外层',83.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001018,'2026-03-07 04:37:30','MAT-COS-018','锦棉提花','提花',NULL,NULL,'和风,汉服,纹样',108.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001019,'2026-03-07 04:37:30','MAT-COS-019','细斜纹衬衫布','衬衫布',NULL,NULL,'制服衬衫,挺括,日常',48.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001020,'2026-03-07 04:37:30','MAT-COS-020','轻薄斗篷布','披风面料',NULL,NULL,'披风,斗篷,垂坠',66.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001021,'2026-03-07 04:37:30','MAT-COS-021','软纱里衬','里料',NULL,NULL,'内衬,防透,舒适',22.00,'停用','审核通过','迭代A常见COS面料初始化',1,NULL,1,'2026-05-16 01:31:13',0),(1772858250001022,'2026-03-07 04:37:30','MAT-COS-022','厚磅制服呢','制服呢',NULL,NULL,'冬季JK,学院外套,挺括',92.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001023,'2026-03-07 04:37:30','MAT-COS-023','金属感涂层布','特效面料',NULL,NULL,'机甲,舞台,未来感',128.00,'启用','审核通过','迭代A常见COS面料初始化',1,NULL,NULL,'2026-03-07 12:37:30',0),(1772858250001024,'2026-03-07 04:37:30','MAT-COS-024','泡泡纱棉麻','棉麻',NULL,NULL,'夏季,清爽,轻复古',57.00,'停用','审核通过','迭代A常见COS面料初始化',1,NULL,1,'2026-05-16 01:31:08',0),(1778848982851650,'2026-05-15 12:43:02','MAT851650','泡泡棉','针织',NULL,NULL,'亲肤',6.00,'启用','审核通过',NULL,1,1,1,'2026-05-15 20:43:02',0),(1778849175245750,'2026-05-15 12:46:15','府绸','纯棉60支','针织','upload/1778849140678.jpg','upload/1778849146679.jpg,upload/1778849165553.jpg','透气',25.00,'启用','待审核',NULL,1,1,1,'2026-05-15 20:46:15',0),(1778864758600490,'2026-05-15 17:05:58','MAT600490','雪纺','针织','upload/1778864741828.webp','upload/1778864747309.webp','亲肤',8.00,'启用','审核通过',NULL,1,1,1,'2026-05-16 01:06:05',0),(1778916013800945,'2026-05-16 07:20:13','MAT800945','纯棉20支','针织','upload/1778916005291.webp','upload/1778916009325.webp',NULL,18.00,'启用','待审核',NULL,1,1,1,'2026-05-16 15:20:13',0);
/*!40000 ALTER TABLE `cos_material_asset` ENABLE KEYS */;
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
