-- 文件名：cos_iterF_product_seed_cosplay_mysql57.sql
-- 适用版本：MySQL 5.7
-- 目的：补充 COS 商品目录数据，并同步补齐款式与款式-面料映射
-- 说明：
-- 1. 商品数据参考常见 COS 服装题材，适配现有 remaicosfu 结构
-- 2. 图片统一复用现有 upload/cos1.jpg ~ upload/cos3.jpg 占位图
-- 3. 款式-面料规则依赖迭代 A 已存在的 cos_material_asset / cos_style_material_rule
-- 4. 幂等执行：重复运行不会产生重复商品、款式和规则

SET NAMES utf8;
SET @db := DATABASE();

/* =========================================================
   1) 补充 12 个 COS 商品到 remaicosfu
   ========================================================= */
SET @seed_product := (
  SELECT GREATEST(IFNULL(MAX(id), 0), 9100)
  FROM `remaicosfu`
);

INSERT INTO `remaicosfu`
(`id`,`addtime`,`fuzhuangbianhao`,`fuzhuangmingcheng`,`huawentuan`,`fuzhuangkuanshi`,`mianliaoleibie`,`yimenjin`,`chima`,`fuzhuangjiage`,`fuzhuangxiangqing`,`thumbsupnum`,`crazilynum`,`clicktime`,`clicknum`)
SELECT
  (@seed_product := @seed_product + 1) AS id,
  NOW(),
  d.product_code,
  d.product_name,
  d.cover_url,
  d.style_name,
  d.material_name,
  d.placket_name,
  d.size_text,
  d.sale_price,
  d.detail_text,
  0 AS thumbsupnum,
  0 AS crazilynum,
  NOW() AS clicktime,
  d.clicknum
FROM (
  SELECT
    'COS-101' AS product_code,
    '鬼灭之刃 - 灶门炭治郎战斗服' AS product_name,
    'upload/cos2.jpg' AS cover_url,
    '战斗服' AS style_name,
    '涤棉混纺' AS material_name,
    '对襟' AS placket_name,
    'S/M/L/XL' AS size_text,
    399 AS sale_price,
    168 AS clicknum,
    CONCAT('适用场景：Cosplay、漫展、舞台拍摄；',
           '设计特点：黑绿格纹羽织、深色内搭、束带层次明显、少年热血风；',
           '推荐人群：喜欢战斗系少年角色、偏好日式热血题材用户；',
           '相关角色：灶门炭治郎；',
           '相关作品：鬼灭之刃；',
           '关键词：和风、格纹、战斗服、少年系、热血。') AS detail_text
  UNION ALL SELECT
    'COS-102','鬼灭之刃 - 祢豆子和服','upload/cos2.jpg','和服','锦棉提花','交领','S/M/L/XL',369,152,
    CONCAT('适用场景：Cosplay、写真拍摄、节日活动；',
           '设计特点：粉色麻叶纹和服、黑色外褂、蝴蝶结腰封、可爱少女感；',
           '推荐人群：喜欢和风少女角色、偏好柔和色系用户；',
           '相关角色：灶门祢豆子；',
           '相关作品：鬼灭之刃；',
           '关键词：和服、粉色、麻叶纹、少女、日式。')
  UNION ALL SELECT
    'COS-103','原神 - 雷电将军战袍','upload/cos3.jpg','战斗服','弹力莱卡','立领','S/M/L/XL',699,210,
    CONCAT('适用场景：Cosplay、漫展舞台、角色出片；',
           '设计特点：紫黑主色、金属装饰、不对称剪裁、战斗气质强烈；',
           '推荐人群：喜欢游戏高人气角色、偏好华丽战斗风用户；',
           '相关角色：雷电将军；',
           '相关作品：原神；',
           '关键词：战斗服、紫色、雷元素、华丽、游戏角色。')
  UNION ALL SELECT
    'COS-104','原神 - 神里绫华礼装','upload/cos3.jpg','礼装','双宫缎','开襟','S/M/L/XL',659,188,
    CONCAT('适用场景：Cosplay、舞台表演、主题写真；',
           '设计特点：蓝白礼装、裙摆层次丰富、飘带灵动、整体精致优雅；',
           '推荐人群：喜欢高雅大小姐角色、偏好轻礼服风格用户；',
           '相关角色：神里绫华；',
           '相关作品：原神；',
           '关键词：礼装、蓝白配色、优雅、游戏角色、舞台。')
  UNION ALL SELECT
    'COS-105','咒术回战 - 五条悟制服','upload/cos1.jpg','学院制服','TC制服呢','立领','S/M/L/XL',429,176,
    CONCAT('适用场景：Cosplay、街拍、现代风写真；',
           '设计特点：深色高领制服、线条利落、整体简洁、都市感强；',
           '推荐人群：喜欢现代帅气角色、偏好极简制服风用户；',
           '相关角色：五条悟；',
           '相关作品：咒术回战；',
           '关键词：制服、现代、学院、简约、帅气。')
  UNION ALL SELECT
    'COS-106','间谍过家家 - 阿尼亚学院制服','upload/cos1.jpg','学院制服','全棉斜纹','翻领','S/M/L/XL',299,194,
    CONCAT('适用场景：Cosplay、校园主题活动、日常改良穿搭；',
           '设计特点：学院感外套、衬衫与领结组合、配色清爽、辨识度高；',
           '推荐人群：喜欢校园可爱系角色、偏好轻量穿搭用户；',
           '相关角色：阿尼亚；',
           '相关作品：间谍过家家；',
           '关键词：学院制服、校园、可爱、白绿配色、少女。')
  UNION ALL SELECT
    'COS-107','海贼王 - 路飞冒险服','upload/cos1.jpg','冒险服','涤棉混纺','开襟','S/M/L/XL',259,185,
    CONCAT('适用场景：Cosplay、主题派对、轻户外拍摄；',
           '设计特点：红色短上衣、蓝色短裤、黄色束带、整体轻松热血；',
           '推荐人群：喜欢冒险热血题材、偏好舒适穿着用户；',
           '相关角色：蒙奇·D·路飞；',
           '相关作品：海贼王；',
           '关键词：冒险服、热血、红蓝配色、经典动漫、轻便。')
  UNION ALL SELECT
    'COS-108','初音未来 - 偶像打歌服','upload/cos1.jpg','偶像打歌服','哑光色丁','双排扣','S/M/L/XL',499,205,
    CONCAT('适用场景：舞台表演、Cosplay、打歌拍摄；',
           '设计特点：偶像感短裙、亮面装饰、配色活泼、舞台表现力强；',
           '推荐人群：喜欢虚拟偶像、偏好舞台系服装用户；',
           '相关角色：初音未来；',
           '相关作品：Vocaloid；',
           '关键词：偶像、打歌服、舞台、活力、二次元。')
  UNION ALL SELECT
    'COS-109','明日方舟 - 阿米娅作战服','upload/cos3.jpg','战斗服','人造皮革','翻领','S/M/L/XL',569,173,
    CONCAT('适用场景：Cosplay、舞台表演、硬朗风写真；',
           '设计特点：作战感剪裁、暗色拼接、层次装备感、角色识别度高；',
           '推荐人群：喜欢未来战术风、偏好游戏制服用户；',
           '相关角色：阿米娅；',
           '相关作品：明日方舟；',
           '关键词：战斗服、作战风、未来感、暗色系、游戏。')
  UNION ALL SELECT
    'COS-110','Re:从零开始 - 蕾姆女仆装','upload/cos2.jpg','女仆装','哑光色丁','圆领','S/M/L/XL',389,166,
    CONCAT('适用场景：Cosplay、主题咖啡活动、写真拍摄；',
           '设计特点：黑白女仆裙、围裙层次、荷叶边装饰、甜系经典；',
           '推荐人群：喜欢经典女仆系角色、偏好甜美二次元风用户；',
           '相关角色：蕾姆；',
           '相关作品：Re:从零开始的异世界生活；',
           '关键词：女仆装、黑白、甜美、围裙、经典。')
  UNION ALL SELECT
    'COS-111','哈利波特 - 霍格沃茨披风斗篷','upload/cos3.jpg','披风斗篷','轻薄斗篷布','披风领','S/M/L/XL',459,149,
    CONCAT('适用场景：主题活动、学院风写真、舞台表演；',
           '设计特点：学院披风、徽章元素、内外层反差色、沉浸感强；',
           '推荐人群：喜欢魔法学院题材、偏好披风造型用户；',
           '相关角色：霍格沃茨学院系角色；',
           '相关作品：哈利波特；',
           '关键词：披风、斗篷、学院、魔法、主题活动。')
  UNION ALL SELECT
    'COS-112','约会大作战 - 时崎狂三洛丽塔','upload/cos2.jpg','洛丽塔','天鹅绒','立领','S/M/L/XL',539,171,
    CONCAT('适用场景：Cosplay、哥特主题拍摄、舞台走秀；',
           '设计特点：多层裙摆、暗色蕾丝、钟表感装饰、哥特甜酷兼具；',
           '推荐人群：喜欢哥特系二次元角色、偏好繁复裙装用户；',
           '相关角色：时崎狂三；',
           '相关作品：约会大作战；',
           '关键词：洛丽塔、哥特、蕾丝、暗色系、礼裙。')
) d
WHERE NOT EXISTS (
  SELECT 1
  FROM `remaicosfu` r
  WHERE r.`fuzhuangbianhao` = d.`product_code`
);

/* =========================================================
   2) 同步补齐款式词到 fuzhuangkuanshi
   ========================================================= */
SET @seed_style := (
  SELECT GREATEST(IFNULL(MAX(id), 0), 100)
  FROM `fuzhuangkuanshi`
);

INSERT INTO `fuzhuangkuanshi` (`id`,`addtime`,`fuzhuangkuanshi`)
SELECT
  (@seed_style := @seed_style + 1) AS id,
  NOW(),
  d.style_name
FROM (
  SELECT '战斗服' AS style_name
  UNION ALL SELECT '和服'
  UNION ALL SELECT '礼装'
  UNION ALL SELECT '学院制服'
  UNION ALL SELECT '冒险服'
  UNION ALL SELECT '偶像打歌服'
  UNION ALL SELECT '女仆装'
  UNION ALL SELECT '披风斗篷'
  UNION ALL SELECT '洛丽塔'
) d
WHERE NOT EXISTS (
  SELECT 1
  FROM `fuzhuangkuanshi` s
  WHERE s.`fuzhuangkuanshi` = d.`style_name`
);

/* =========================================================
   3) 为新增款式补齐面料映射规则
   依赖：已执行迭代 A 的素材与规则建表/种子 SQL
   ========================================================= */
SET @seed_rule := (
  SELECT GREATEST(IFNULL(MAX(id), 0), UNIX_TIMESTAMP(NOW()) * 1000000 + 900000) + 1000
  FROM `cos_style_material_rule`
);

INSERT INTO `cos_style_material_rule`
(`id`,`addtime`,`style_name`,`material_id`,`material_name`,`is_default`,`status`,`priority`,`remark`,`created_by`,`updated_by`,`updated_at`,`deleted`)
SELECT
  (@seed_rule := @seed_rule + 1) AS id,
  NOW(),
  d.style_name,
  m.id AS material_id,
  m.material_name,
  d.is_default,
  '启用' AS status,
  d.priority,
  '迭代F扩充COS商品目录时补充的款式面料规则' AS remark,
  NULL AS created_by,
  NULL AS updated_by,
  NOW(),
  0 AS deleted
FROM (
  SELECT '战斗服' AS style_name, 'MAT-COS-010' AS material_code, 1 AS is_default, 10 AS priority
  UNION ALL SELECT '战斗服', 'MAT-COS-003', 0, 20
  UNION ALL SELECT '战斗服', 'MAT-COS-011', 0, 30

  UNION ALL SELECT '和服', 'MAT-COS-018', 1, 10
  UNION ALL SELECT '和服', 'MAT-COS-016', 0, 20
  UNION ALL SELECT '和服', 'MAT-COS-007', 0, 30

  UNION ALL SELECT '礼装', 'MAT-COS-006', 1, 10
  UNION ALL SELECT '礼装', 'MAT-COS-013', 0, 20
  UNION ALL SELECT '礼装', 'MAT-COS-008', 0, 30

  UNION ALL SELECT '学院制服', 'MAT-COS-004', 1, 10
  UNION ALL SELECT '学院制服', 'MAT-COS-002', 0, 20
  UNION ALL SELECT '学院制服', 'MAT-COS-019', 0, 30

  UNION ALL SELECT '冒险服', 'MAT-COS-003', 1, 10
  UNION ALL SELECT '冒险服', 'MAT-COS-001', 0, 20
  UNION ALL SELECT '冒险服', 'MAT-COS-017', 0, 30

  UNION ALL SELECT '偶像打歌服', 'MAT-COS-006', 1, 10
  UNION ALL SELECT '偶像打歌服', 'MAT-COS-008', 0, 20
  UNION ALL SELECT '偶像打歌服', 'MAT-COS-023', 0, 30

  UNION ALL SELECT '女仆装', 'MAT-COS-006', 1, 10
  UNION ALL SELECT '女仆装', 'MAT-COS-004', 0, 20
  UNION ALL SELECT '女仆装', 'MAT-COS-021', 0, 30

  UNION ALL SELECT '披风斗篷', 'MAT-COS-020', 1, 10
  UNION ALL SELECT '披风斗篷', 'MAT-COS-015', 0, 20
  UNION ALL SELECT '披风斗篷', 'MAT-COS-013', 0, 30

  UNION ALL SELECT '洛丽塔', 'MAT-COS-006', 1, 10
  UNION ALL SELECT '洛丽塔', 'MAT-COS-008', 0, 20
  UNION ALL SELECT '洛丽塔', 'MAT-COS-013', 0, 30
) d
INNER JOIN `cos_material_asset` m
  ON m.`deleted` = 0
 AND m.`material_code` = d.`material_code`
WHERE NOT EXISTS (
  SELECT 1
  FROM `cos_style_material_rule` r
  WHERE r.`deleted` = 0
    AND r.`style_name` = d.`style_name`
    AND r.`material_id` = m.`id`
);

/* =========================================================
   4) 可选检查
   =========================================================
SELECT COUNT(1) AS product_count FROM remaicosfu;
SELECT COUNT(1) AS style_count FROM fuzhuangkuanshi;
SELECT COUNT(1) AS style_rule_count FROM cos_style_material_rule WHERE deleted=0;
========================================================= */