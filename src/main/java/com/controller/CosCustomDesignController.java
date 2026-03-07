package com.controller;

import com.alibaba.fastjson.JSON;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/coscustomdesign")
public class CosCustomDesignController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/save")
    public R saveDraft(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = uid(request);
        String userTable = utable(request);
        if (userId == null || StringUtils.isBlank(userTable)) {
            return R.error(401, "请先登录");
        }

        Long productId = parseLong(body.get("productId"));
        if (productId == null) {
            return R.error(400, "productId不能为空");
        }

        Long id = parseLong(body.get("id"));
        String productName = trimStr(body.get("productName"));
        String styleName = trimStr(body.get("styleName"));
        String designSummary = trimStr(body.get("designSummary"));
        String sizeCode = trimStr(body.get("sizeCode"));
        String materialName = trimStr(body.get("materialName"));
        String colorTheme = trimStr(body.get("colorTheme"));
        String fitType = trimStr(body.get("fitType"));
        String silhouette = trimStr(body.get("silhouette"));
        String craftTags = normalizeCsv(body.get("craftTags"));
        String accessoryTags = normalizeCsv(body.get("accessoryTags"));
        String referenceImages = normalizeCsv(body.get("referenceImages"));
        String customNote = trimStr(body.get("customNote"));
        String status = defaultStr(trimStr(body.get("status")), "草稿");

        Object designPayload = body.get("designPayload");
        String designJson = designPayload == null ? "{}" : JSON.toJSONString(designPayload);

        if (StringUtils.isBlank(designSummary)) {
            designSummary = buildSummary(sizeCode, materialName, silhouette, fitType, colorTheme);
        }

        if (id == null) {
            id = nextId();
            jdbcTemplate.update(
                    "insert into cos_custom_design(" +
                            "id,addtime,user_id,user_table,product_id,product_name,style_name,design_summary,design_json,size_code,material_name,color_theme,fit_type,silhouette,craft_tags,accessory_tags,reference_images,custom_note,status,version_no,last_used_at,updated_at,deleted" +
                            ") values(?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,null,now(),0)",
                    id,
                    userId,
                    userTable,
                    productId,
                    productName,
                    styleName,
                    designSummary,
                    designJson,
                    sizeCode,
                    materialName,
                    colorTheme,
                    fitType,
                    silhouette,
                    craftTags,
                    accessoryTags,
                    referenceImages,
                    customNote,
                    status
            );
        } else {
            Map<String, Object> db = queryDraftById(id);
            if (db == null || parseInt(db.get("deleted"), 0) == 1) {
                return R.error(404, "草稿不存在");
            }
            if (!userId.equals(parseLong(db.get("user_id")))) {
                return R.error(403, "无权限");
            }

            int newVersion = parseInt(db.get("version_no"), 1) + 1;
            jdbcTemplate.update(
                    "update cos_custom_design set " +
                            "product_id=?,product_name=?,style_name=?,design_summary=?,design_json=?,size_code=?,material_name=?,color_theme=?,fit_type=?,silhouette=?,craft_tags=?,accessory_tags=?,reference_images=?,custom_note=?,status=?,version_no=?,updated_at=now() " +
                            "where id=? and deleted=0",
                    productId,
                    productName,
                    styleName,
                    designSummary,
                    designJson,
                    sizeCode,
                    materialName,
                    colorTheme,
                    fitType,
                    silhouette,
                    craftTags,
                    accessoryTags,
                    referenceImages,
                    customNote,
                    status,
                    newVersion,
                    id
            );
        }

        Map<String, Object> latest = queryDraftById(id);
        return R.ok("草稿保存成功").put("data", latest);
    }

    @GetMapping("/latest")
    public R latest(@RequestParam("productId") Long productId, HttpServletRequest request) {
        Long userId = uid(request);
        String userTable = utable(request);
        if (userId == null || StringUtils.isBlank(userTable)) {
            return R.error(401, "请先登录");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select * from cos_custom_design where user_id=? and user_table=? and product_id=? and deleted=0 order by updated_at desc, id desc limit 1",
                userId,
                userTable,
                productId
        );
        return R.ok().put("data", rows.isEmpty() ? null : rows.get(0));
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = uid(request);
        String userTable = utable(request);
        if (userId == null || StringUtils.isBlank(userTable)) {
            return R.error(401, "请先登录");
        }

        Map<String, Object> draft = queryDraftById(id);
        if (draft == null || parseInt(draft.get("deleted"), 0) == 1) {
            return R.error(404, "草稿不存在");
        }
        if (!userId.equals(parseLong(draft.get("user_id")))) {
            return R.error(403, "无权限");
        }

        return R.ok().put("data", draft);
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        Long userId = uid(request);
        String userTable = utable(request);
        if (userId == null || StringUtils.isBlank(userTable)) {
            return R.error(401, "请先登录");
        }

        if (ids == null || ids.length == 0) {
            return R.ok("无数据");
        }

        int affected = 0;
        for (Long id : ids) {
            if (id == null) continue;
            affected += jdbcTemplate.update(
                    "update cos_custom_design set deleted=1,updated_at=now() where id=? and user_id=? and user_table=? and deleted=0",
                    id,
                    userId,
                    userTable
            );
        }
        return R.ok().put("data", Collections.singletonMap("affected", affected));
    }

    private Map<String, Object> queryDraftById(Long id) {
        if (id == null) {
            return null;
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select * from cos_custom_design where id=? limit 1",
                id
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Long uid(HttpServletRequest req) {
        if (req == null || req.getSession() == null) {
            return null;
        }
        return parseLong(req.getSession().getAttribute("userId"));
    }

    private String utable(HttpServletRequest req) {
        if (req == null || req.getSession() == null) {
            return null;
        }
        Object value = req.getSession().getAttribute("tableName");
        return value == null ? null : String.valueOf(value);
    }

    private static String trimStr(Object value) {
        if (value == null) return null;
        String safe = String.valueOf(value).trim();
        return safe.isEmpty() ? null : safe;
    }

    private static String defaultStr(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private static String normalizeCsv(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            List<String> vals = new ArrayList<>();
            for (Object item : (Collection<?>) value) {
                String text = trimStr(item);
                if (StringUtils.isNotBlank(text)) {
                    vals.add(text);
                }
            }
            return vals.isEmpty() ? null : String.join(",", vals);
        }
        String raw = trimStr(value);
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String[] arr = raw.split(",");
        List<String> vals = new ArrayList<>();
        for (String item : arr) {
            String text = trimStr(item);
            if (StringUtils.isNotBlank(text)) {
                vals.add(text);
            }
        }
        return vals.isEmpty() ? null : String.join(",", vals);
    }

    private static String buildSummary(String sizeCode, String materialName, String silhouette, String fitType, String colorTheme) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(sizeCode)) parts.add("尺码:" + sizeCode);
        if (StringUtils.isNotBlank(materialName)) parts.add("面料:" + materialName);
        if (StringUtils.isNotBlank(silhouette)) parts.add("廓形:" + silhouette);
        if (StringUtils.isNotBlank(fitType)) parts.add("松量:" + fitType);
        if (StringUtils.isNotBlank(colorTheme)) parts.add("色系:" + colorTheme);
        if (parts.isEmpty()) {
            return "默认定制";
        }
        return String.join(" / ", parts);
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

    private static Long nextId() {
        long now = System.currentTimeMillis() * 1000;
        int random = ThreadLocalRandom.current().nextInt(1000);
        return now + random;
    }
}