package com.controller;

import com.annotation.IgnoreAuth;
import com.utils.CosRoleUtil;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/cosmaterial")
public class CosMaterialController {

    private static final String ENABLE_ON = "启用";
    private static final String ENABLE_OFF = "停用";

    private static final String AUDIT_PENDING = "待审核";
    private static final String AUDIT_PASS = "审核通过";
    private static final String AUDIT_REJECT = "审核驳回";

    private static final Set<String> ENABLE_STATUS_SET = new HashSet<>(Arrays.asList(ENABLE_ON, ENABLE_OFF));
    private static final Set<String> AUDIT_STATUS_SET = new HashSet<>(Arrays.asList(AUDIT_PENDING, AUDIT_PASS, AUDIT_REJECT));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/admin/page")
    public R adminPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;
        if (limit > 200) limit = 200;

        StringBuilder where = new StringBuilder(" where deleted=0 ");
        List<Object> args = new ArrayList<>();

        String materialName = trimStr(params.get("materialName"));
        if (StringUtils.isNotBlank(materialName)) {
            where.append(" and material_name like ? ");
            args.add("%" + materialName + "%");
        }

        String categoryName = trimStr(params.get("categoryName"));
        if (StringUtils.isNotBlank(categoryName)) {
            where.append(" and category_name like ? ");
            args.add("%" + categoryName + "%");
        }

        String tags = trimStr(params.get("tags"));
        if (StringUtils.isNotBlank(tags)) {
            where.append(" and tags like ? ");
            args.add("%" + tags + "%");
        }

        String enableStatus = trimStr(params.get("enableStatus"));
        if (StringUtils.isNotBlank(enableStatus)) {
            where.append(" and enable_status = ? ");
            args.add(enableStatus);
        }

        String auditStatus = trimStr(params.get("auditStatus"));
        if (StringUtils.isNotBlank(auditStatus)) {
            where.append(" and audit_status = ? ");
            args.add(auditStatus);
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cos_material_asset " + where + " order by updated_at desc, id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cos_material_asset " + where,
                args.toArray(),
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);
        return R.ok().put("data", data);
    }

    @GetMapping("/admin/detail/{id}")
    public R adminDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }
        Map<String, Object> row = queryMaterialById(id);
        if (row == null || parseInt(row.get("deleted"), 0) == 1) {
            return R.error(404, "素材不存在");
        }
        return R.ok().put("data", row);
    }

    @PostMapping("/save")
    public R save(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        String materialName = trimStr(body.get("materialName"));
        if (StringUtils.isBlank(materialName)) {
            return R.error(400, "materialName不能为空");
        }

        Long id = nextId();
        String materialCode = trimStr(body.get("materialCode"));
        if (StringUtils.isBlank(materialCode)) {
            materialCode = "MAT" + String.format("%06d", Math.abs(id % 1000000));
        }

        String categoryName = trimStr(body.get("categoryName"));
        String coverUrl = trimStr(body.get("coverUrl"));
        String assetUrls = trimStr(body.get("assetUrls"));
        String tags = trimStr(body.get("tags"));
        BigDecimal unitPrice = parseDecimal(body.get("unitPrice"));
        String enableStatus = normalizeEnableStatus(trimStr(body.get("enableStatus")), ENABLE_ON);
        String auditStatus = normalizeAuditStatus(trimStr(body.get("auditStatus")), AUDIT_PENDING);
        String auditRemark = trimStr(body.get("auditRemark"));

        Long operatorId = uid(request);

        try {
            jdbcTemplate.update(
                    "insert into cos_material_asset(" +
                            "id,addtime,material_code,material_name,category_name,cover_url,asset_urls,tags,unit_price," +
                            "enable_status,audit_status,audit_remark,version_no,created_by,updated_by,updated_at,deleted" +
                            ") values(?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?,now(),0)",
                    id,
                    materialCode,
                    materialName,
                    categoryName,
                    coverUrl,
                    assetUrls,
                    tags,
                    unitPrice,
                    enableStatus,
                    auditStatus,
                    auditRemark,
                    1,
                    operatorId,
                    operatorId
            );
        } catch (Exception e) {
            return R.error(400, "素材保存失败，可能素材编码重复");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("materialCode", materialCode);
        return R.ok("保存成功").put("data", data);
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        Long id = parseLong(body.get("id"));
        if (id == null) {
            return R.error(400, "id不能为空");
        }

        Map<String, Object> current = queryMaterialById(id);
        if (current == null || parseInt(current.get("deleted"), 0) == 1) {
            return R.error(404, "素材不存在");
        }

        String materialCode = defaultStr(trimStr(body.get("materialCode")), str(current.get("material_code")));
        String materialName = defaultStr(trimStr(body.get("materialName")), str(current.get("material_name")));
        if (StringUtils.isBlank(materialName)) {
            return R.error(400, "materialName不能为空");
        }

        String categoryName = defaultStr(trimStr(body.get("categoryName")), str(current.get("category_name")));
        String coverUrl = defaultStr(trimStr(body.get("coverUrl")), str(current.get("cover_url")));
        String assetUrls = defaultStr(trimStr(body.get("assetUrls")), str(current.get("asset_urls")));
        String tags = defaultStr(trimStr(body.get("tags")), str(current.get("tags")));
        BigDecimal unitPrice = body.containsKey("unitPrice") ? parseDecimal(body.get("unitPrice")) : parseDecimal(current.get("unit_price"));

        String enableStatus = body.containsKey("enableStatus")
                ? normalizeEnableStatus(trimStr(body.get("enableStatus")), str(current.get("enable_status")))
                : str(current.get("enable_status"));
        String auditStatus = body.containsKey("auditStatus")
                ? normalizeAuditStatus(trimStr(body.get("auditStatus")), str(current.get("audit_status")))
                : str(current.get("audit_status"));
        String auditRemark = body.containsKey("auditRemark")
                ? trimStr(body.get("auditRemark"))
                : str(current.get("audit_remark"));

        int oldVersion = parseInt(current.get("version_no"), 1);
        boolean coreChanged = !StringUtils.equals(materialName, str(current.get("material_name")))
                || !StringUtils.equals(categoryName, str(current.get("category_name")))
                || !StringUtils.equals(coverUrl, str(current.get("cover_url")))
                || !StringUtils.equals(assetUrls, str(current.get("asset_urls")))
                || !StringUtils.equals(tags, str(current.get("tags")))
                || !decimalEquals(unitPrice, parseDecimal(current.get("unit_price")));

        int newVersion = coreChanged ? oldVersion + 1 : oldVersion;

        try {
            jdbcTemplate.update(
                    "update cos_material_asset set material_code=?,material_name=?,category_name=?,cover_url=?,asset_urls=?,tags=?," +
                            "unit_price=?,enable_status=?,audit_status=?,audit_remark=?,version_no=?,updated_by=?,updated_at=now() " +
                            "where id=? and deleted=0",
                    materialCode,
                    materialName,
                    categoryName,
                    coverUrl,
                    assetUrls,
                    tags,
                    unitPrice,
                    enableStatus,
                    auditStatus,
                    auditRemark,
                    newVersion,
                    uid(request),
                    id
            );
        } catch (Exception e) {
            return R.error(400, "素材更新失败，可能素材编码重复");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("versionNo", newVersion);
        data.put("coreChanged", coreChanged);
        return R.ok("更新成功").put("data", data);
    }

    @PostMapping("/toggle/{id}")
    public R toggle(@PathVariable("id") Long id,
                    @RequestBody(required = false) Map<String, Object> body,
                    HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        Map<String, Object> current = queryMaterialById(id);
        if (current == null || parseInt(current.get("deleted"), 0) == 1) {
            return R.error(404, "素材不存在");
        }

        String targetStatus = body == null ? null : trimStr(body.get("enableStatus"));
        if (StringUtils.isBlank(targetStatus)) {
            targetStatus = ENABLE_ON.equals(str(current.get("enable_status"))) ? ENABLE_OFF : ENABLE_ON;
        }
        targetStatus = normalizeEnableStatus(targetStatus, ENABLE_ON);

        jdbcTemplate.update(
                "update cos_material_asset set enable_status=?,updated_by=?,updated_at=now() where id=? and deleted=0",
                targetStatus,
                uid(request),
                id
        );

        return R.ok("状态更新成功").put("data", Collections.singletonMap("enableStatus", targetStatus));
    }

    @PostMapping("/audit/{id}")
    public R audit(@PathVariable("id") Long id,
                   @RequestBody Map<String, Object> body,
                   HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        Map<String, Object> current = queryMaterialById(id);
        if (current == null || parseInt(current.get("deleted"), 0) == 1) {
            return R.error(404, "素材不存在");
        }

        String auditStatus = normalizeAuditStatus(trimStr(body.get("auditStatus")), null);
        if (StringUtils.isBlank(auditStatus)) {
            return R.error(400, "auditStatus非法");
        }

        String auditRemark = trimStr(body.get("auditRemark"));

        jdbcTemplate.update(
                "update cos_material_asset set audit_status=?,audit_remark=?,updated_by=?,updated_at=now() where id=? and deleted=0",
                auditStatus,
                auditRemark,
                uid(request),
                id
        );

        return R.ok("审核状态更新成功");
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }
        if (ids == null || ids.length == 0) {
            return R.ok("无数据");
        }

        int affected = 0;
        for (Long id : ids) {
            if (id == null) continue;
            affected += jdbcTemplate.update(
                    "update cos_material_asset set deleted=1,updated_by=?,updated_at=now() where id=? and deleted=0",
                    uid(request),
                    id
            );
        }
        return R.ok("删除成功").put("data", Collections.singletonMap("affected", affected));
    }

    @GetMapping("/rule/page")
    public R rulePage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;
        if (limit > 200) limit = 200;

        StringBuilder where = new StringBuilder(" where r.deleted=0 ");
        List<Object> args = new ArrayList<>();

        String styleName = trimStr(params.get("styleName"));
        if (StringUtils.isNotBlank(styleName)) {
            where.append(" and r.style_name like ? ");
            args.add("%" + styleName + "%");
        }

        String ruleStatus = trimStr(params.get("status"));
        if (StringUtils.isNotBlank(ruleStatus)) {
            where.append(" and r.status = ? ");
            args.add(ruleStatus);
        }

        String materialName = trimStr(params.get("materialName"));
        if (StringUtils.isNotBlank(materialName)) {
            where.append(" and r.material_name like ? ");
            args.add("%" + materialName + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select r.*,m.enable_status as material_enable_status,m.audit_status as material_audit_status,m.tags,m.unit_price,m.cover_url " +
                        "from cos_style_material_rule r " +
                        "left join cos_material_asset m on r.material_id=m.id " +
                        where +
                        " order by r.style_name asc, r.is_default desc, r.priority asc, r.id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cos_style_material_rule r " + where,
                args.toArray(),
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);
        return R.ok().put("data", data);
    }

    @PostMapping("/rule/save")
    public R ruleSave(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        String styleName = trimStr(body.get("styleName"));
        Long materialId = parseLong(body.get("materialId"));
        if (StringUtils.isBlank(styleName)) {
            return R.error(400, "styleName不能为空");
        }
        if (materialId == null) {
            return R.error(400, "materialId不能为空");
        }

        Map<String, Object> material = queryMaterialById(materialId);
        if (material == null || parseInt(material.get("deleted"), 0) == 1) {
            return R.error(400, "materialId不存在");
        }

        Long exists = jdbcTemplate.queryForObject(
                "select count(1) from cos_style_material_rule where style_name=? and material_id=? and deleted=0",
                new Object[]{styleName, materialId},
                Long.class
        );
        if (exists != null && exists > 0) {
            return R.error(400, "该款式已绑定该面料");
        }

        Long id = nextId();
        Integer isDefault = parseInt(body.get("isDefault"), 0);
        String status = normalizeEnableStatus(trimStr(body.get("status")), ENABLE_ON);
        Integer priority = parseInt(body.get("priority"), 100);
        String remark = trimStr(body.get("remark"));

        jdbcTemplate.update(
                "insert into cos_style_material_rule(" +
                        "id,addtime,style_name,material_id,material_name,is_default,status,priority,remark,created_by,updated_by,updated_at,deleted" +
                        ") values(?,now(),?,?,?,?,?,?,?,?,?,now(),0)",
                id,
                styleName,
                materialId,
                str(material.get("material_name")),
                isDefault,
                status,
                priority,
                remark,
                uid(request),
                uid(request)
        );

        if (isDefault != null && isDefault == 1) {
            clearStyleDefault(styleName, id);
        }

        return R.ok("规则保存成功").put("data", Collections.singletonMap("id", id));
    }

    @PostMapping("/rule/update")
    public R ruleUpdate(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        Long id = parseLong(body.get("id"));
        if (id == null) {
            return R.error(400, "id不能为空");
        }

        Map<String, Object> current = queryRuleById(id);
        if (current == null || parseInt(current.get("deleted"), 0) == 1) {
            return R.error(404, "规则不存在");
        }

        String styleName = defaultStr(trimStr(body.get("styleName")), str(current.get("style_name")));
        Long materialId = body.containsKey("materialId") ? parseLong(body.get("materialId")) : parseLong(current.get("material_id"));
        if (StringUtils.isBlank(styleName) || materialId == null) {
            return R.error(400, "styleName/materialId不能为空");
        }

        Map<String, Object> material = queryMaterialById(materialId);
        if (material == null || parseInt(material.get("deleted"), 0) == 1) {
            return R.error(400, "materialId不存在");
        }

        Long exists = jdbcTemplate.queryForObject(
                "select count(1) from cos_style_material_rule where style_name=? and material_id=? and deleted=0 and id<>?",
                new Object[]{styleName, materialId, id},
                Long.class
        );
        if (exists != null && exists > 0) {
            return R.error(400, "该款式已绑定该面料");
        }

        Integer isDefault = body.containsKey("isDefault") ? parseInt(body.get("isDefault"), 0) : parseInt(current.get("is_default"), 0);
        String status = body.containsKey("status")
                ? normalizeEnableStatus(trimStr(body.get("status")), str(current.get("status")))
                : str(current.get("status"));
        Integer priority = body.containsKey("priority") ? parseInt(body.get("priority"), 100) : parseInt(current.get("priority"), 100);
        String remark = body.containsKey("remark") ? trimStr(body.get("remark")) : str(current.get("remark"));

        jdbcTemplate.update(
                "update cos_style_material_rule set style_name=?,material_id=?,material_name=?,is_default=?,status=?,priority=?,remark=?,updated_by=?,updated_at=now() where id=? and deleted=0",
                styleName,
                materialId,
                str(material.get("material_name")),
                isDefault,
                status,
                priority,
                remark,
                uid(request),
                id
        );

        if (isDefault != null && isDefault == 1) {
            clearStyleDefault(styleName, id);
        }

        return R.ok("规则更新成功");
    }

    @PostMapping("/rule/delete")
    public R ruleDelete(@RequestBody Long[] ids, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "无权限");
        }
        if (ids == null || ids.length == 0) {
            return R.ok("无数据");
        }

        int affected = 0;
        for (Long id : ids) {
            if (id == null) continue;
            affected += jdbcTemplate.update(
                    "update cos_style_material_rule set deleted=1,updated_by=?,updated_at=now() where id=? and deleted=0",
                    uid(request),
                    id
            );
        }

        return R.ok("删除成功").put("data", Collections.singletonMap("affected", affected));
    }

    @IgnoreAuth
    @GetMapping("/rule/by-style")
    public R ruleByStyle(@RequestParam("styleName") String styleName) {
        if (StringUtils.isBlank(styleName)) {
            return R.ok().put("data", new ArrayList<>());
        }

        List<Map<String, Object>> rows = queryAvailableMaterialsByStyle(styleName);
        return R.ok().put("data", rows);
    }

    @IgnoreAuth
    @GetMapping("/rule/by-style/{styleName}")
    public R ruleByStylePath(@PathVariable("styleName") String styleName) {
        if (StringUtils.isBlank(styleName)) {
            return R.ok().put("data", new ArrayList<>());
        }

        List<Map<String, Object>> rows = queryAvailableMaterialsByStyle(styleName);
        return R.ok().put("data", rows);
    }

    private List<Map<String, Object>> queryAvailableMaterialsByStyle(String styleName) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select r.id as ruleId,r.style_name as styleName,r.is_default as isDefault,r.priority,r.remark as ruleRemark," +
                        "m.id as materialId,m.material_code as materialCode,m.material_name as materialName,m.category_name as categoryName," +
                        "m.cover_url as coverUrl,m.asset_urls as assetUrls,m.tags,m.unit_price as unitPrice " +
                        "from cos_style_material_rule r " +
                        "inner join cos_material_asset m on r.material_id = m.id " +
                        "where r.deleted=0 and r.status=? and r.style_name=? and m.deleted=0 and m.enable_status=? and m.audit_status=? " +
                        "order by r.is_default desc, r.priority asc, r.id desc",
                ENABLE_ON,
                styleName,
                ENABLE_ON,
                AUDIT_PASS
        );

        if (!rows.isEmpty()) {
            return rows;
        }

        List<Map<String, Object>> fallbackRows = jdbcTemplate.queryForList(
                "select null as ruleId, ? as styleName, 0 as isDefault, 999 as priority, null as ruleRemark," +
                        "m.id as materialId,m.material_code as materialCode,m.material_name as materialName,m.category_name as categoryName," +
                        "m.cover_url as coverUrl,m.asset_urls as assetUrls,m.tags,m.unit_price as unitPrice " +
                        "from cos_material_asset m " +
                        "where m.deleted=0 and m.enable_status=? and m.audit_status=? and exists (" +
                        "select 1 from remaicosfu p where p.fuzhuangkuanshi=? and p.mianliaoleibie=m.material_name" +
                        ") order by m.material_name asc",
                styleName,
                ENABLE_ON,
                AUDIT_PASS,
                styleName
        );

        if (!fallbackRows.isEmpty()) {
            return fallbackRows;
        }

        return jdbcTemplate.queryForList(
                "select null as ruleId, ? as styleName, 0 as isDefault, 999 as priority, null as ruleRemark," +
                        "m.id as materialId,m.material_code as materialCode,m.material_name as materialName,m.category_name as categoryName," +
                        "m.cover_url as coverUrl,m.asset_urls as assetUrls,m.tags,m.unit_price as unitPrice " +
                        "from cos_material_asset m where m.deleted=0 and m.enable_status=? and m.audit_status=? " +
                        "order by m.material_name asc limit 30",
                styleName,
                ENABLE_ON,
                AUDIT_PASS
        );
    }

    private void clearStyleDefault(String styleName, Long exceptId) {
        jdbcTemplate.update(
                "update cos_style_material_rule set is_default=0 where deleted=0 and style_name=? and id<>?",
                styleName,
                exceptId
        );
    }

    private Map<String, Object> queryMaterialById(Long id) {
        if (id == null) {
            return null;
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cos_material_asset where id=? limit 1",
                id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    private Map<String, Object> queryRuleById(Long id) {
        if (id == null) {
            return null;
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cos_style_material_rule where id=? limit 1",
                id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    private boolean isAdmin(HttpServletRequest req) {
        return CosRoleUtil.isAdmin(sessionStr(req, "role"), sessionStr(req, "tableName"));
    }

    private Long uid(HttpServletRequest req) {
        return parseLong(sessionObj(req, "userId"));
    }

    private Object sessionObj(HttpServletRequest req, String key) {
        if (req == null || req.getSession() == null) {
            return null;
        }
        return req.getSession().getAttribute(key);
    }

    private String sessionStr(HttpServletRequest req, String key) {
        Object value = sessionObj(req, key);
        return value == null ? null : String.valueOf(value);
    }

    private static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String trimStr(Object value) {
        return StringUtils.trimToNull(str(value));
    }

    private static String defaultStr(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private static Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return null;
        }
    }

    private static int parseInt(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private static BigDecimal parseDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(String.valueOf(value));
        try {
            String safe = String.valueOf(value).trim();
            if (safe.isEmpty()) return null;
            return new BigDecimal(safe);
        } catch (Exception ignore) {
            return null;
        }
    }

    private static boolean decimalEquals(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }

    private static String normalizeEnableStatus(String status, String defaultValue) {
        if (StringUtils.isBlank(status)) {
            return defaultValue;
        }
        return ENABLE_STATUS_SET.contains(status) ? status : defaultValue;
    }

    private static String normalizeAuditStatus(String status, String defaultValue) {
        if (StringUtils.isBlank(status)) {
            return defaultValue;
        }
        return AUDIT_STATUS_SET.contains(status) ? status : defaultValue;
    }

    private static Long nextId() {
        long now = System.currentTimeMillis() * 1000;
        int random = ThreadLocalRandom.current().nextInt(1000);
        return now + random;
    }
}