-- 文件名：cos_iterA_material_seed_cosplay_mysql57.sql
-- 适用版本：MySQL 5.7
-- 目的：迭代A补充常见 COS 服装面料素材与款式-面料规则
-- 特性：幂等插入（重复执行不会产生重复记录）

SET NAMES utf8;
SET @db := DATABASE();

/* =========================================================
   1) 素材资产种子数据（常见 COS 面料）
   ========================================================= */
SET @seed_material := (
  SELECT GREATEST(IFNULL(MAX(id), 0), UNIX_TIMESTAMP(NOW()) * 1000000) + 1000
  FROM `cos_material_asset`
);

INSERT INTO `cos_material_asset`
(`id`,`addtime`,`material_code`,`material_name`,`category_name`,`cover_url`,`asset_urls`,`tags`,`unit_price`,`enable_status`,`audit_status`,`audit_remark`,`version_no`,`created_by`,`updated_by`,`updated_at`,`deleted`)
SELECT
  (@seed_material := @seed_material + 1) AS id,
  NOW(),
  d.material_code,
  d.material_name,
  d.category_name,
  NULL AS cover_url,
  NULL AS asset_urls,
  d.tags,
  d.unit_price,
  d.enable_status,
  d.audit_status,
  '迭代A常见COS面料初始化' AS audit_remark,
  1 AS version_no,
  NULL AS created_by,
  NULL AS updated_by,
  NOW(),
  0 AS deleted
FROM (
  SELECT 'MAT-COS-001' AS material_code, '纯棉府绸' AS material_name, '棉布' AS category_name, '透气,亲肤,校服,衬衫' AS tags, 45.00 AS unit_price, '启用' AS enable_status, '审核通过' AS audit_status
  UNION ALL SELECT 'MAT-COS-002', '全棉斜纹', '棉布', '耐磨,挺括,日常COS', 52.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-003', '涤棉混纺', '混纺', '抗皱,易打理,性价比', 38.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-004', 'TC制服呢', '制服呢', 'JK,水手服,学院风', 68.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-005', 'TR西装料', '西装面料', '执事,西装,垂感', 88.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-006', '哑光色丁', '缎面', '礼服,女仆装,光泽', 42.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-007', '高密雪纺', '雪纺', '轻薄,飘逸,层次', 36.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-008', '欧根纱', '网纱', '蓬度,蝴蝶结,裙撑', 32.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-009', '针织罗纹', '针织', '弹性,收口,领口袖口', 28.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-010', '弹力莱卡', '莱卡', '高弹,贴身,战斗服', 58.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-011', '人造皮革', '皮革', '盔甲风,腰封,硬挺', 76.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-012', 'PU亮面皮革', '皮革', '机甲风,反光,舞台', 89.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-013', '天鹅绒', '绒布', '哥特,礼服,复古', 64.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-014', '灯芯绒', '绒布', '秋冬,复古,学院', 55.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-015', '仿羊毛呢', '毛呢', '斗篷,外套,巫师袍', 98.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-016', '双宫缎', '缎面', '旗袍,汉服,礼服', 72.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-017', '锦纶塔丝隆', '功能面料', '防风,轻量,外层', 83.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-018', '锦棉提花', '提花', '和风,汉服,纹样', 108.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-019', '细斜纹衬衫布', '衬衫布', '制服衬衫,挺括,日常', 48.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-020', '轻薄斗篷布', '披风面料', '披风,斗篷,垂坠', 66.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-021', '软纱里衬', '里料', '内衬,防透,舒适', 22.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-022', '厚磅制服呢', '制服呢', '冬季JK,学院外套,挺括', 92.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-023', '金属感涂层布', '特效面料', '机甲,舞台,未来感', 128.00, '启用', '审核通过'
  UNION ALL SELECT 'MAT-COS-024', '泡泡纱棉麻', '棉麻', '夏季,清爽,轻复古', 57.00, '启用', '审核通过'
) d
WHERE NOT EXISTS (
  SELECT 1
  FROM `cos_material_asset` a
  WHERE a.`deleted` = 0
    AND a.`material_code` = d.`material_code`
);

/* =========================================================
   2) 常见款式-面料规则（明确款式名，含默认）
   ========================================================= */
SET @seed_rule := (
  SELECT GREATEST(IFNULL(MAX(id), 0), UNIX_TIMESTAMP(NOW()) * 1000000 + 500000) + 1000
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
  '迭代A常见款式规则初始化' AS remark,
  NULL AS created_by,
  NULL AS updated_by,
  NOW(),
  0 AS deleted
FROM (
  SELECT 'JK制服' AS style_name, 'MAT-COS-004' AS material_code, 1 AS is_default, 10 AS priority
  UNION ALL SELECT 'JK制服', 'MAT-COS-022', 0, 20
  UNION ALL SELECT 'JK制服', 'MAT-COS-019', 0, 30

  UNION ALL SELECT '水手服', 'MAT-COS-004', 1, 10
  UNION ALL SELECT '水手服', 'MAT-COS-002', 0, 20
  UNION ALL SELECT '水手服', 'MAT-COS-019', 0, 30

  UNION ALL SELECT '洛丽塔', 'MAT-COS-006', 1, 10
  UNION ALL SELECT '洛丽塔', 'MAT-COS-008', 0, 20
  UNION ALL SELECT '洛丽塔', 'MAT-COS-013', 0, 30

  UNION ALL SELECT '女仆装', 'MAT-COS-006', 1, 10
  UNION ALL SELECT '女仆装', 'MAT-COS-004', 0, 20
  UNION ALL SELECT '女仆装', 'MAT-COS-021', 0, 30

  UNION ALL SELECT '汉服', 'MAT-COS-001', 1, 10
  UNION ALL SELECT '汉服', 'MAT-COS-016', 0, 20
  UNION ALL SELECT '汉服', 'MAT-COS-018', 0, 30

  UNION ALL SELECT '旗袍', 'MAT-COS-016', 1, 10
  UNION ALL SELECT '旗袍', 'MAT-COS-018', 0, 20
  UNION ALL SELECT '旗袍', 'MAT-COS-006', 0, 30

  UNION ALL SELECT '和服', 'MAT-COS-018', 1, 10
  UNION ALL SELECT '和服', 'MAT-COS-013', 0, 20
  UNION ALL SELECT '和服', 'MAT-COS-016', 0, 30

  UNION ALL SELECT '巫师袍', 'MAT-COS-015', 1, 10
  UNION ALL SELECT '巫师袍', 'MAT-COS-013', 0, 20
  UNION ALL SELECT '巫师袍', 'MAT-COS-020', 0, 30

  UNION ALL SELECT '执事装', 'MAT-COS-005', 1, 10
  UNION ALL SELECT '执事装', 'MAT-COS-019', 0, 20
  UNION ALL SELECT '执事装', 'MAT-COS-015', 0, 30

  UNION ALL SELECT '战斗服', 'MAT-COS-010', 1, 10
  UNION ALL SELECT '战斗服', 'MAT-COS-017', 0, 20
  UNION ALL SELECT '战斗服', 'MAT-COS-011', 0, 30

  UNION ALL SELECT '机甲风', 'MAT-COS-023', 1, 10
  UNION ALL SELECT '机甲风', 'MAT-COS-012', 0, 20
  UNION ALL SELECT '机甲风', 'MAT-COS-017', 0, 30

  UNION ALL SELECT '哥特礼服', 'MAT-COS-013', 1, 10
  UNION ALL SELECT '哥特礼服', 'MAT-COS-015', 0, 20
  UNION ALL SELECT '哥特礼服', 'MAT-COS-006', 0, 30

  UNION ALL SELECT '斗篷披风', 'MAT-COS-020', 1, 10
  UNION ALL SELECT '斗篷披风', 'MAT-COS-015', 0, 20
  UNION ALL SELECT '斗篷披风', 'MAT-COS-013', 0, 30

  UNION ALL SELECT '西装', 'MAT-COS-005', 1, 10
  UNION ALL SELECT '西装', 'MAT-COS-015', 0, 20
  UNION ALL SELECT '西装', 'MAT-COS-004', 0, 30

  UNION ALL SELECT '衬衫', 'MAT-COS-019', 1, 10
  UNION ALL SELECT '衬衫', 'MAT-COS-001', 0, 20
  UNION ALL SELECT '衬衫', 'MAT-COS-003', 0, 30
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
   3) 对现有商品款式做关键字补充规则（仅补充，不设默认）
   说明：用于提升你现有 remaicosfu 款式命中率
   ========================================================= */
SET @seed_rule2 := (
  SELECT GREATEST(IFNULL(MAX(id), 0), UNIX_TIMESTAMP(NOW()) * 1000000 + 800000) + 1000
  FROM `cos_style_material_rule`
);

INSERT INTO `cos_style_material_rule`
(`id`,`addtime`,`style_name`,`material_id`,`material_name`,`is_default`,`status`,`priority`,`remark`,`created_by`,`updated_by`,`updated_at`,`deleted`)
SELECT
  (@seed_rule2 := @seed_rule2 + 1) AS id,
  NOW(),
  s.style_name,
  m.id AS material_id,
  m.material_name,
  0 AS is_default,
  '启用' AS status,
  k.priority,
  CONCAT('迭代A关键字补充:', k.keyword) AS remark,
  NULL AS created_by,
  NULL AS updated_by,
  NOW(),
  0 AS deleted
FROM (
  SELECT DISTINCT TRIM(`fuzhuangkuanshi`) AS style_name
  FROM `remaicosfu`
  WHERE `fuzhuangkuanshi` IS NOT NULL
    AND TRIM(`fuzhuangkuanshi`) <> ''
) s
INNER JOIN (
  SELECT 'JK' AS keyword, 'MAT-COS-004' AS material_code, 60 AS priority
  UNION ALL SELECT '水手', 'MAT-COS-004', 60
  UNION ALL SELECT '学院', 'MAT-COS-022', 65
  UNION ALL SELECT 'Lolita', 'MAT-COS-006', 60
  UNION ALL SELECT '洛丽塔', 'MAT-COS-006', 60
  UNION ALL SELECT '汉服', 'MAT-COS-001', 60
  UNION ALL SELECT '旗袍', 'MAT-COS-016', 60
  UNION ALL SELECT '和服', 'MAT-COS-018', 60
  UNION ALL SELECT '女仆', 'MAT-COS-006', 60
  UNION ALL SELECT '执事', 'MAT-COS-005', 60
  UNION ALL SELECT '巫师', 'MAT-COS-015', 60
  UNION ALL SELECT '盔甲', 'MAT-COS-011', 60
  UNION ALL SELECT '战斗', 'MAT-COS-010', 60
  UNION ALL SELECT '机甲', 'MAT-COS-023', 60
  UNION ALL SELECT '礼服', 'MAT-COS-013', 60
  UNION ALL SELECT '斗篷', 'MAT-COS-020', 60
  UNION ALL SELECT '披风', 'MAT-COS-020', 60
  UNION ALL SELECT '衬衫', 'MAT-COS-019', 60
  UNION ALL SELECT '西装', 'MAT-COS-005', 60
) k
  ON s.style_name LIKE CONCAT('%', k.keyword, '%')
INNER JOIN `cos_material_asset` m
  ON m.`deleted` = 0
 AND m.`material_code` = k.`material_code`
WHERE NOT EXISTS (
  SELECT 1
  FROM `cos_style_material_rule` r
  WHERE r.`deleted` = 0
    AND r.`style_name` = s.`style_name`
    AND r.`material_id` = m.`id`
);

/* =========================================================
   4) 执行后可选检查
   =========================================================
SELECT COUNT(1) AS material_count FROM cos_material_asset WHERE deleted=0;
SELECT COUNT(1) AS rule_count FROM cos_style_material_rule WHERE deleted=0;
========================================================= */