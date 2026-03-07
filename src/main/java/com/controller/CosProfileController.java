package com.controller;

import com.utils.CosRoleUtil;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/cosprofile")
public class CosProfileController {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long uid(HttpServletRequest req) {
        return Long.valueOf(req.getSession().getAttribute("userId").toString());
    }

    private String utable(HttpServletRequest req) {
        return req.getSession().getAttribute("tableName").toString();
    }

    private String role(HttpServletRequest req) {
        Object raw = req.getSession().getAttribute("role");
        return raw == null ? "" : String.valueOf(raw);
    }

    private String roleCode(HttpServletRequest req) {
        return CosRoleUtil.normalize(role(req), utable(req));
    }

    private R requireUser(HttpServletRequest request) {
        if (!CosRoleUtil.USER.equals(roleCode(request))) {
            return R.error(403, "user only");
        }
        return null;
    }

    @GetMapping("/address/page")
    public R addressPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;
        if (limit > 100) limit = 100;

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,user_id as userId,user_table as userTable,receiver_name as receiverName,receiver_phone as receiverPhone," +
                        "province,city,district,detail_address as detailAddress,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                        "from cos_user_address where user_id=? and user_table=? and deleted=0 " +
                        "order by is_default desc, updated_at desc, id desc limit ? offset ?",
                uid(request), utable(request), limit, (page - 1) * limit
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cos_user_address where user_id=? and user_table=? and deleted=0",
                new Object[]{uid(request), utable(request)},
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);

        return R.ok().put("data", data);
    }

    @PostMapping("/address/save")
    public R addressSave(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        try {
            Map<String, Object> payload = normalizeAddressPayload(body);
            Long id = genId();
            int isDefault = parseBoolInt(payload.get("isDefault"));

            Long exists = jdbcTemplate.queryForObject(
                    "select count(1) from cos_user_address where user_id=? and user_table=? and deleted=0",
                    new Object[]{uid(request), utable(request)},
                    Long.class
            );
            if ((exists == null || exists == 0) && isDefault == 0) {
                isDefault = 1;
            }

            if (isDefault == 1) {
                clearAddressDefault(request);
            }

            jdbcTemplate.update(
                    "insert into cos_user_address(id,user_id,user_table,receiver_name,receiver_phone,province,city,district,detail_address,is_default,status,updated_at,deleted) " +
                            "values(?,?,?,?,?,?,?,?,?,?,?,now(),0)",
                    id,
                    uid(request),
                    utable(request),
                    payload.get("receiverName"),
                    payload.get("receiverPhone"),
                    payload.get("province"),
                    payload.get("city"),
                    payload.get("district"),
                    payload.get("detailAddress"),
                    isDefault,
                    payload.get("status")
            );

            return R.ok().put("data", queryAddressById(id, request));
        } catch (IllegalArgumentException ex) {
            return R.error(400, ex.getMessage());
        }
    }

    @PostMapping("/address/update")
    public R addressUpdate(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        Long id = parseLong(body.get("id"));
        if (id == null) return R.error(400, "address id required");

        Map<String, Object> db = queryAddressById(id, request);
        if (db == null) return R.error(404, "address not found");

        try {
            Map<String, Object> payload = normalizeAddressPayload(body);
            int isDefault = parseBoolInt(payload.get("isDefault"));
            if (isDefault == 1) {
                clearAddressDefault(request);
            }

            jdbcTemplate.update(
                    "update cos_user_address set receiver_name=?,receiver_phone=?,province=?,city=?,district=?,detail_address=?,is_default=?,status=?,updated_at=now() " +
                            "where id=? and user_id=? and user_table=? and deleted=0",
                    payload.get("receiverName"),
                    payload.get("receiverPhone"),
                    payload.get("province"),
                    payload.get("city"),
                    payload.get("district"),
                    payload.get("detailAddress"),
                    isDefault,
                    payload.get("status"),
                    id,
                    uid(request),
                    utable(request)
            );

            ensureAddressOneDefault(request);
            return R.ok().put("data", queryAddressById(id, request));
        } catch (IllegalArgumentException ex) {
            return R.error(400, ex.getMessage());
        }
    }

    @PostMapping("/address/delete")
    public R addressDelete(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        Long id = parseLong(body.get("id"));
        if (id == null) return R.error(400, "address id required");

        int updated = jdbcTemplate.update(
                "update cos_user_address set deleted=1,is_default=0,updated_at=now() where id=? and user_id=? and user_table=? and deleted=0",
                id,
                uid(request),
                utable(request)
        );

        if (updated <= 0) return R.error(404, "address not found or forbidden");

        ensureAddressOneDefault(request);
        return R.ok("deleted");
    }

    @PostMapping("/address/set-default/{id}")
    public R addressSetDefault(@PathVariable("id") Long id, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        Map<String, Object> target = queryAddressById(id, request);
        if (target == null) return R.error(404, "address not found");

        clearAddressDefault(request);
        jdbcTemplate.update(
                "update cos_user_address set is_default=1,updated_at=now() where id=? and user_id=? and user_table=? and deleted=0",
                id,
                uid(request),
                utable(request)
        );
        return R.ok().put("data", queryAddressById(id, request));
    }

    @GetMapping("/address/default")
    public R addressDefault(HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,user_id as userId,user_table as userTable,receiver_name as receiverName,receiver_phone as receiverPhone," +
                        "province,city,district,detail_address as detailAddress,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                        "from cos_user_address where user_id=? and user_table=? and deleted=0 and is_default=1 limit 1",
                uid(request), utable(request)
        );

        Map<String, Object> result = rows.isEmpty() ? null : rows.get(0);
        if (result == null) {
            List<Map<String, Object>> fallback = jdbcTemplate.queryForList(
                    "select id,user_id as userId,user_table as userTable,receiver_name as receiverName,receiver_phone as receiverPhone," +
                            "province,city,district,detail_address as detailAddress,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                            "from cos_user_address where user_id=? and user_table=? and deleted=0 order by updated_at desc,id desc limit 1",
                    uid(request), utable(request)
            );
            result = fallback.isEmpty() ? null : fallback.get(0);
        }
        return R.ok().put("data", result);
    }

    @GetMapping("/body/page")
    public R bodyPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;
        if (limit > 100) limit = 100;

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,user_id as userId,user_table as userTable,profile_name as profileName,height_cm as heightCm,weight_kg as weightKg," +
                        "waist_cm as waistCm,bust_cm as bustCm,hip_cm as hipCm,shoulder_cm as shoulderCm,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                        "from cos_user_body_profile where user_id=? and user_table=? and deleted=0 " +
                        "order by is_default desc, updated_at desc, id desc limit ? offset ?",
                uid(request), utable(request), limit, (page - 1) * limit
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cos_user_body_profile where user_id=? and user_table=? and deleted=0",
                new Object[]{uid(request), utable(request)},
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);

        return R.ok().put("data", data);
    }

    @PostMapping("/body/save")
    public R bodySave(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        try {
            Map<String, Object> payload = normalizeBodyPayload(body);
            Long id = genId();
            int isDefault = parseBoolInt(payload.get("isDefault"));

            Long exists = jdbcTemplate.queryForObject(
                    "select count(1) from cos_user_body_profile where user_id=? and user_table=? and deleted=0",
                    new Object[]{uid(request), utable(request)},
                    Long.class
            );
            if ((exists == null || exists == 0) && isDefault == 0) {
                isDefault = 1;
            }

            if (isDefault == 1) {
                clearBodyDefault(request);
            }

            jdbcTemplate.update(
                    "insert into cos_user_body_profile(id,user_id,user_table,profile_name,height_cm,weight_kg,waist_cm,bust_cm,hip_cm,shoulder_cm,is_default,status,updated_at,deleted) " +
                            "values(?,?,?,?,?,?,?,?,?,?,?,?,now(),0)",
                    id,
                    uid(request),
                    utable(request),
                    payload.get("profileName"),
                    payload.get("heightCm"),
                    payload.get("weightKg"),
                    payload.get("waistCm"),
                    payload.get("bustCm"),
                    payload.get("hipCm"),
                    payload.get("shoulderCm"),
                    isDefault,
                    payload.get("status")
            );

            return R.ok().put("data", queryBodyById(id, request));
        } catch (IllegalArgumentException ex) {
            return R.error(400, ex.getMessage());
        }
    }

    @PostMapping("/body/update")
    public R bodyUpdate(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        Long id = parseLong(body.get("id"));
        if (id == null) return R.error(400, "body profile id required");

        Map<String, Object> db = queryBodyById(id, request);
        if (db == null) return R.error(404, "body profile not found");

        try {
            Map<String, Object> payload = normalizeBodyPayload(body);
            int isDefault = parseBoolInt(payload.get("isDefault"));
            if (isDefault == 1) {
                clearBodyDefault(request);
            }

            jdbcTemplate.update(
                    "update cos_user_body_profile set profile_name=?,height_cm=?,weight_kg=?,waist_cm=?,bust_cm=?,hip_cm=?,shoulder_cm=?,is_default=?,status=?,updated_at=now() " +
                            "where id=? and user_id=? and user_table=? and deleted=0",
                    payload.get("profileName"),
                    payload.get("heightCm"),
                    payload.get("weightKg"),
                    payload.get("waistCm"),
                    payload.get("bustCm"),
                    payload.get("hipCm"),
                    payload.get("shoulderCm"),
                    isDefault,
                    payload.get("status"),
                    id,
                    uid(request),
                    utable(request)
            );

            ensureBodyOneDefault(request);
            return R.ok().put("data", queryBodyById(id, request));
        } catch (IllegalArgumentException ex) {
            return R.error(400, ex.getMessage());
        }
    }

    @PostMapping("/body/delete")
    public R bodyDelete(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        Long id = parseLong(body.get("id"));
        if (id == null) return R.error(400, "body profile id required");

        int updated = jdbcTemplate.update(
                "update cos_user_body_profile set deleted=1,is_default=0,updated_at=now() where id=? and user_id=? and user_table=? and deleted=0",
                id,
                uid(request),
                utable(request)
        );

        if (updated <= 0) return R.error(404, "body profile not found or forbidden");

        ensureBodyOneDefault(request);
        return R.ok("deleted");
    }

    @PostMapping("/body/set-default/{id}")
    public R bodySetDefault(@PathVariable("id") Long id, HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        Map<String, Object> target = queryBodyById(id, request);
        if (target == null) return R.error(404, "body profile not found");

        clearBodyDefault(request);
        jdbcTemplate.update(
                "update cos_user_body_profile set is_default=1,updated_at=now() where id=? and user_id=? and user_table=? and deleted=0",
                id,
                uid(request),
                utable(request)
        );
        return R.ok().put("data", queryBodyById(id, request));
    }

    @GetMapping("/body/default")
    public R bodyDefault(HttpServletRequest request) {
        R auth = requireUser(request);
        if (auth != null) return auth;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,user_id as userId,user_table as userTable,profile_name as profileName,height_cm as heightCm,weight_kg as weightKg," +
                        "waist_cm as waistCm,bust_cm as bustCm,hip_cm as hipCm,shoulder_cm as shoulderCm,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                        "from cos_user_body_profile where user_id=? and user_table=? and deleted=0 and is_default=1 limit 1",
                uid(request), utable(request)
        );

        Map<String, Object> result = rows.isEmpty() ? null : rows.get(0);
        if (result == null) {
            List<Map<String, Object>> fallback = jdbcTemplate.queryForList(
                    "select id,user_id as userId,user_table as userTable,profile_name as profileName,height_cm as heightCm,weight_kg as weightKg," +
                            "waist_cm as waistCm,bust_cm as bustCm,hip_cm as hipCm,shoulder_cm as shoulderCm,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                            "from cos_user_body_profile where user_id=? and user_table=? and deleted=0 order by updated_at desc,id desc limit 1",
                    uid(request), utable(request)
            );
            result = fallback.isEmpty() ? null : fallback.get(0);
        }
        return R.ok().put("data", result);
    }

    private Map<String, Object> normalizeAddressPayload(Map<String, Object> body) {
        String receiverName = trim(pick(body, "receiverName", "receiver_name"));
        String receiverPhone = trim(pick(body, "receiverPhone", "receiver_phone"));
        String province = trim(pick(body, "province"));
        String city = trim(pick(body, "city"));
        String district = trim(pick(body, "district"));
        String detailAddress = trim(pick(body, "detailAddress", "detail_address"));
        String status = trim(pick(body, "status"));
        if (StringUtils.isBlank(status)) status = "Enabled";

        if (StringUtils.isBlank(receiverName)) throw new IllegalArgumentException("receiver required");
        if (receiverName.length() > 60) throw new IllegalArgumentException("receiver length <= 60");
        if (!PHONE_PATTERN.matcher(StringUtils.defaultString(receiverPhone)).matches()) {
            throw new IllegalArgumentException("invalid phone");
        }
        if (StringUtils.isBlank(detailAddress)) throw new IllegalArgumentException("detail address required");
        if (detailAddress.length() > 255) throw new IllegalArgumentException("detail address length <= 255");

        Map<String, Object> payload = new HashMap<>();
        payload.put("receiverName", receiverName);
        payload.put("receiverPhone", receiverPhone);
        payload.put("province", province);
        payload.put("city", city);
        payload.put("district", district);
        payload.put("detailAddress", detailAddress);
        payload.put("status", status);
        payload.put("isDefault", body.get("isDefault"));
        return payload;
    }

    private Map<String, Object> normalizeBodyPayload(Map<String, Object> body) {
        String profileName = trim(pick(body, "profileName", "profile_name"));
        BigDecimal heightCm = parseBigDecimal(pick(body, "heightCm", "height_cm"));
        BigDecimal weightKg = parseBigDecimal(pick(body, "weightKg", "weight_kg"));
        BigDecimal waistCm = parseBigDecimal(pick(body, "waistCm", "waist_cm"));
        BigDecimal bustCm = parseBigDecimal(pick(body, "bustCm", "bust_cm"));
        BigDecimal hipCm = parseBigDecimal(pick(body, "hipCm", "hip_cm"));
        BigDecimal shoulderCm = parseBigDecimal(pick(body, "shoulderCm", "shoulder_cm"));
        String status = trim(pick(body, "status"));
        if (StringUtils.isBlank(status)) status = "Enabled";

        if (StringUtils.isBlank(profileName)) throw new IllegalArgumentException("profile name required");
        if (!inRange(heightCm, 120, 220)) throw new IllegalArgumentException("height out of range 120-220");
        if (!inRange(weightKg, 30, 180)) throw new IllegalArgumentException("weight out of range 30-180");
        if (!inRange(waistCm, 40, 160)) throw new IllegalArgumentException("waist out of range 40-160");
        if (!inRange(bustCm, 40, 160)) throw new IllegalArgumentException("bust out of range 40-160");
        if (!inRange(hipCm, 40, 160)) throw new IllegalArgumentException("hip out of range 40-160");
        if (!inRange(shoulderCm, 30, 70)) throw new IllegalArgumentException("shoulder out of range 30-70");

        Map<String, Object> payload = new HashMap<>();
        payload.put("profileName", profileName);
        payload.put("heightCm", heightCm);
        payload.put("weightKg", weightKg);
        payload.put("waistCm", waistCm);
        payload.put("bustCm", bustCm);
        payload.put("hipCm", hipCm);
        payload.put("shoulderCm", shoulderCm);
        payload.put("status", status);
        payload.put("isDefault", body.get("isDefault"));
        return payload;
    }

    private void clearAddressDefault(HttpServletRequest request) {
        jdbcTemplate.update(
                "update cos_user_address set is_default=0 where user_id=? and user_table=? and deleted=0",
                uid(request),
                utable(request)
        );
    }

    private void clearBodyDefault(HttpServletRequest request) {
        jdbcTemplate.update(
                "update cos_user_body_profile set is_default=0 where user_id=? and user_table=? and deleted=0",
                uid(request),
                utable(request)
        );
    }

    private void ensureAddressOneDefault(HttpServletRequest request) {
        Long count = jdbcTemplate.queryForObject(
                "select count(1) from cos_user_address where user_id=? and user_table=? and deleted=0 and is_default=1",
                new Object[]{uid(request), utable(request)},
                Long.class
        );
        if (count != null && count > 0) return;

        List<Map<String, Object>> fallback = jdbcTemplate.queryForList(
                "select id from cos_user_address where user_id=? and user_table=? and deleted=0 order by updated_at desc,id desc limit 1",
                uid(request), utable(request)
        );
        if (!fallback.isEmpty()) {
            Long id = parseLong(fallback.get(0).get("id"));
            if (id != null) {
                jdbcTemplate.update(
                        "update cos_user_address set is_default=1 where id=? and user_id=? and user_table=? and deleted=0",
                        id, uid(request), utable(request)
                );
            }
        }
    }

    private void ensureBodyOneDefault(HttpServletRequest request) {
        Long count = jdbcTemplate.queryForObject(
                "select count(1) from cos_user_body_profile where user_id=? and user_table=? and deleted=0 and is_default=1",
                new Object[]{uid(request), utable(request)},
                Long.class
        );
        if (count != null && count > 0) return;

        List<Map<String, Object>> fallback = jdbcTemplate.queryForList(
                "select id from cos_user_body_profile where user_id=? and user_table=? and deleted=0 order by updated_at desc,id desc limit 1",
                uid(request), utable(request)
        );
        if (!fallback.isEmpty()) {
            Long id = parseLong(fallback.get(0).get("id"));
            if (id != null) {
                jdbcTemplate.update(
                        "update cos_user_body_profile set is_default=1 where id=? and user_id=? and user_table=? and deleted=0",
                        id, uid(request), utable(request)
                );
            }
        }
    }

    private Map<String, Object> queryAddressById(Long id, HttpServletRequest request) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,user_id as userId,user_table as userTable,receiver_name as receiverName,receiver_phone as receiverPhone," +
                        "province,city,district,detail_address as detailAddress,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                        "from cos_user_address where id=? and user_id=? and user_table=? and deleted=0 limit 1",
                id,
                uid(request),
                utable(request)
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Map<String, Object> queryBodyById(Long id, HttpServletRequest request) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,user_id as userId,user_table as userTable,profile_name as profileName,height_cm as heightCm,weight_kg as weightKg," +
                        "waist_cm as waistCm,bust_cm as bustCm,hip_cm as hipCm,shoulder_cm as shoulderCm,is_default as isDefault,status,updated_at as updatedAt,addtime " +
                        "from cos_user_body_profile where id=? and user_id=? and user_table=? and deleted=0 limit 1",
                id,
                uid(request),
                utable(request)
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    private static boolean inRange(BigDecimal value, double min, double max) {
        if (value == null) return false;
        return value.compareTo(BigDecimal.valueOf(min)) >= 0 && value.compareTo(BigDecimal.valueOf(max)) <= 0;
    }

    private static BigDecimal parseBigDecimal(Object value) {
        if (value == null) return null;
        String text = String.valueOf(value).trim();
        if (StringUtils.isBlank(text)) return null;
        try {
            return new BigDecimal(text);
        } catch (Exception e) {
            return null;
        }
    }

    private static String trim(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private static Object pick(Map<String, Object> map, String... keys) {
        if (map == null || keys == null) return null;
        for (String key : keys) {
            if (map.containsKey(key)) return map.get(key);
        }
        return null;
    }

    private static int parseBoolInt(Object raw) {
        if (raw == null) return 0;
        if (raw instanceof Number) return ((Number) raw).intValue() > 0 ? 1 : 0;
        String text = String.valueOf(raw).trim();
        if (StringUtils.isBlank(text)) return 0;
        if ("true".equalsIgnoreCase(text) || "yes".equalsIgnoreCase(text) || "y".equalsIgnoreCase(text)) return 1;
        return "1".equals(text) ? 1 : 0;
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

    private static Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return null;
        }
    }

    private static Long genId() {
        return System.currentTimeMillis() + (long) (Math.random() * 1000);
    }
}