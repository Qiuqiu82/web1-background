package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.utils.CosRoleUtil;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/designer")
public class DesignerCenterController {
    private static final String DESIGNER_TABLE = "shejishi";
    private static final String PAY_STATUS_CN = "\u5df2\u652f\u4ed8";
    private static final String PAY_STATUS_EN = "PAID";
    private static final String ORDER_STATUS_SHIPPED = "\u5df2\u53d1\u8d27";
    private static final String ORDER_STATUS_DONE = "\u5df2\u5b8c\u6210";
    private static final String ORDER_STATUS_CANCEL = "\u5df2\u53d6\u6d88";
    private static final String ORDER_STATUS_SHIPPED_EN = "SHIPPED";
    private static final String ORDER_STATUS_DONE_EN = "DONE";
    private static final String ORDER_STATUS_CANCEL_EN = "CANCELLED";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long uid(HttpServletRequest request) {
        Object value = request.getSession().getAttribute("userId");
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }

    private String sessionStr(HttpServletRequest request, String key) {
        Object value = request.getSession().getAttribute(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private boolean isDesigner(HttpServletRequest request) {
        return CosRoleUtil.DESIGNER.equals(CosRoleUtil.normalize(sessionStr(request, "role"), sessionStr(request, "tableName")));
    }

    private String designerTable(HttpServletRequest request) {
        return DESIGNER_TABLE;
    }

    private String trimValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int parseInt(Object value, int fallback) {
        try {
            return value == null ? fallback : Integer.parseInt(String.valueOf(value));
        } catch (Exception ignore) {
            return fallback;
        }
    }

    private Long parseLong(Object value) {
        try {
            return value == null ? null : Long.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return null;
        }
    }

    private BigDecimal toDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (Exception ignore) {
            return BigDecimal.ZERO;
        }
    }

    private boolean ownPortfolio(Long portfolioId, Long designerId) {
        if (portfolioId == null || designerId == null) {
            return false;
        }
        Long count = jdbcTemplate.queryForObject(
                "select count(1) from cos_designer_portfolio where id=? and designer_id=? and designer_table=? and deleted=0",
                new Object[]{portfolioId, designerId, DESIGNER_TABLE},
                Long.class
        );
        return count != null && count > 0;
    }

    private List<Long> parseIdList(Object listObj, Object singleObj) {
        List<Long> ids = new ArrayList<>();
        if (listObj instanceof Collection) {
            for (Object item : (Collection<?>) listObj) {
                Long id = parseLong(item);
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        if (ids.isEmpty()) {
            Long single = parseLong(singleObj);
            if (single != null) {
                ids.add(single);
            }
        }
        return ids;
    }

    private String normalizeTags(Object value) {
        if (value instanceof Collection) {
            List<String> tags = new ArrayList<>();
            for (Object item : (Collection<?>) value) {
                String text = trimValue(item);
                if (StringUtils.isNotBlank(text)) {
                    tags.add(text);
                }
            }
            return String.join(",", tags);
        }
        return trimValue(value).replace("\uFF0C", ",");
    }

    private String normalizeImageList(Object value) {
        List<String> images = new ArrayList<>();
        if (value instanceof Collection) {
            for (Object item : (Collection<?>) value) {
                String text = trimValue(item);
                if (StringUtils.isNotBlank(text)) {
                    images.add(text);
                }
            }
        } else {
            String text = trimValue(value);
            if (StringUtils.isNotBlank(text)) {
                if (text.startsWith("[")) {
                    try {
                        JSONArray array = JSON.parseArray(text);
                        for (Object item : array) {
                            String image = trimValue(item);
                            if (StringUtils.isNotBlank(image)) {
                                images.add(image);
                            }
                        }
                    } catch (Exception ignore) {
                        images.addAll(Arrays.asList(text.split(",")));
                    }
                } else {
                    images.addAll(Arrays.asList(text.split(",")));
                }
            }
        }
        List<String> cleaned = new ArrayList<>();
        for (String image : images) {
            String item = trimValue(image);
            if (StringUtils.isNotBlank(item)) {
                cleaned.add(item);
            }
        }
        return JSON.toJSONString(cleaned);
    }

    private boolean eligiblePortfolioOrder(Map<String, Object> order) {
        String orderStatus = trimValue(order.get("order_status"));
        return ORDER_STATUS_SHIPPED.equals(orderStatus)
                || ORDER_STATUS_DONE.equals(orderStatus)
                || ORDER_STATUS_SHIPPED_EN.equalsIgnoreCase(orderStatus)
                || ORDER_STATUS_DONE_EN.equalsIgnoreCase(orderStatus);
    }

    private List<Map<String, Object>> queryEligibleOrderRows(Long orderId, Long designerId) {
        return jdbcTemplate.queryForList(
                "select id,order_no,order_status,total_amount,addtime from cosorder where id=? and designer_id=? " +
                        "and (designer_table=? or designer_table is null or designer_table='') " +
                        "and (pay_status=? or pay_status=?)",
                orderId,
                designerId,
                DESIGNER_TABLE,
                PAY_STATUS_CN,
                PAY_STATUS_EN
        );
    }

    @GetMapping("/portfolio/page")
    public R portfolioPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u8bbf\u95ee\u4f5c\u54c1\u96c6\u7ba1\u7406");
        }
        Long designerId = uid(request);
        int page = Math.max(parseInt(params.get("page"), 1), 1);
        int limit = parseInt(params.get("limit"), 10);
        if (limit < 1) {
            limit = 10;
        }
        if (limit > 50) {
            limit = 50;
        }

        StringBuilder where = new StringBuilder(" where p.deleted=0 and p.designer_id=? and p.designer_table=? ");
        List<Object> args = new ArrayList<>();
        args.add(designerId);
        args.add(designerTable(request));

        String keyword = trimValue(params.get("keyword"));
        if (StringUtils.isNotBlank(keyword)) {
            where.append(" and (p.title like ? or p.style_tags like ? or p.intro like ?) ");
            String likeKeyword = "%" + keyword + "%";
            args.add(likeKeyword);
            args.add(likeKeyword);
            args.add(likeKeyword);
        }
        String status = trimValue(params.get("status"));
        if (StringUtils.isNotBlank(status)) {
            where.append(" and p.status=? ");
            args.add(status);
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        String listSql = "select p.*, coalesce(rel.linked_order_count,0) as linked_order_count, coalesce(rel.linked_order_nos,'') as linked_order_nos " +
                "from cos_designer_portfolio p " +
                "left join (select portfolio_id, count(1) as linked_order_count, group_concat(order_no order by id desc separator '\u3001') as linked_order_nos " +
                "from cos_designer_portfolio_order_rel group by portfolio_id) rel on rel.portfolio_id=p.id " +
                where +
                " order by p.sort_order asc, p.id desc limit ? offset ?";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(listSql, listArgs.toArray());
        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cos_designer_portfolio p " + where,
                args.toArray(),
                Long.class
        );
        Long enabledCount = jdbcTemplate.queryForObject(
                "select count(1) from cos_designer_portfolio where deleted=0 and designer_id=? and designer_table=? and status='\u542f\u7528'",
                new Object[]{designerId, designerTable(request)},
                Long.class
        );
        Long orderLinkCount = jdbcTemplate.queryForObject(
                "select count(1) from cos_designer_portfolio_order_rel r inner join cos_designer_portfolio p on p.id=r.portfolio_id " +
                        "where p.deleted=0 and p.designer_id=? and p.designer_table=?",
                new Object[]{designerId, designerTable(request)},
                Long.class
        );

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total == null ? 0 : total);
        stats.put("enabledCount", enabledCount == null ? 0 : enabledCount);
        stats.put("orderLinkCount", orderLinkCount == null ? 0 : orderLinkCount);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);
        data.put("stats", stats);
        return R.ok().put("data", data);
    }

    @PostMapping("/portfolio/save")
    public R portfolioSave(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u65b0\u589e\u4f5c\u54c1");
        }
        Long designerId = uid(request);
        String title = trimValue(body.get("title"));
        if (StringUtils.isBlank(title)) {
            return R.error(400, "\u4f5c\u54c1\u6807\u9898\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Long id = parseLong(body.get("id"));
        if (id == null) {
            id = System.currentTimeMillis();
        }
        String coverImage = trimValue(body.get("coverImage"));
        String imageListJson = normalizeImageList(body.get("imageListJson"));
        String styleTags = normalizeTags(body.get("styleTags"));
        String intro = trimValue(body.get("intro"));
        String status = "\u9690\u85cf".equals(trimValue(body.get("status"))) ? "\u9690\u85cf" : "\u542f\u7528";
        Integer sortOrder = parseInt(body.get("sortOrder"), 0);

        jdbcTemplate.update(
                "insert into cos_designer_portfolio(id,addtime,designer_id,designer_table,title,cover_image,image_list_json,style_tags,intro,status,sort_order,deleted) " +
                        "values(?,now(),?,?,?,?,?,?,?,?,?,0)",
                id,
                designerId,
                designerTable(request),
                title,
                coverImage,
                imageListJson,
                styleTags,
                intro,
                status,
                sortOrder
        );
        return R.ok("\u4f5c\u54c1\u5df2\u4fdd\u5b58").put("data", Collections.singletonMap("id", id));
    }

    @PostMapping("/portfolio/update")
    public R portfolioUpdate(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u7f16\u8f91\u4f5c\u54c1");
        }
        Long designerId = uid(request);
        Long id = parseLong(body.get("id"));
        if (id == null) {
            return R.error(400, "\u4f5c\u54c1ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (!ownPortfolio(id, designerId)) {
            return R.error(403, "\u65e0\u6743\u7f16\u8f91\u8be5\u4f5c\u54c1");
        }
        String title = trimValue(body.get("title"));
        if (StringUtils.isBlank(title)) {
            return R.error(400, "\u4f5c\u54c1\u6807\u9898\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String coverImage = trimValue(body.get("coverImage"));
        String imageListJson = normalizeImageList(body.get("imageListJson"));
        String styleTags = normalizeTags(body.get("styleTags"));
        String intro = trimValue(body.get("intro"));
        String status = "\u9690\u85cf".equals(trimValue(body.get("status"))) ? "\u9690\u85cf" : "\u542f\u7528";
        Integer sortOrder = parseInt(body.get("sortOrder"), 0);

        jdbcTemplate.update(
                "update cos_designer_portfolio set title=?,cover_image=?,image_list_json=?,style_tags=?,intro=?,status=?,sort_order=? " +
                        "where id=? and designer_id=? and designer_table=? and deleted=0",
                title,
                coverImage,
                imageListJson,
                styleTags,
                intro,
                status,
                sortOrder,
                id,
                designerId,
                designerTable(request)
        );
        return R.ok("\u4f5c\u54c1\u5df2\u66f4\u65b0");
    }

    @PostMapping("/portfolio/delete")
    public R portfolioDelete(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u5220\u9664\u4f5c\u54c1");
        }
        Long designerId = uid(request);
        List<Long> ids = parseIdList(body.get("ids"), body.get("id"));
        if (ids.isEmpty()) {
            return R.error(400, "\u8bf7\u9009\u62e9\u8981\u5220\u9664\u7684\u4f5c\u54c1");
        }
        for (Long id : ids) {
            jdbcTemplate.update(
                    "update cos_designer_portfolio set deleted=1 where id=? and designer_id=? and designer_table=? and deleted=0",
                    id,
                    designerId,
                    designerTable(request)
            );
            jdbcTemplate.update("delete from cos_designer_portfolio_order_rel where portfolio_id=? and designer_id=? and designer_table=?", id, designerId, designerTable(request));
        }
        return R.ok("\u4f5c\u54c1\u5df2\u5220\u9664");
    }

    @GetMapping("/portfolio/order-options")
    public R portfolioOrderOptions(@RequestParam(required = false) Long portfolioId, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u67e5\u770b\u53ef\u5173\u8054\u8ba2\u5355");
        }
        Long designerId = uid(request);
        if (portfolioId != null && !ownPortfolio(portfolioId, designerId)) {
            return R.error(403, "\u65e0\u6743\u67e5\u770b\u8be5\u4f5c\u54c1\u7684\u5173\u8054\u8ba2\u5355");
        }
        Long safePortfolioId = portfolioId == null ? -1L : portfolioId;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select o.id,o.order_no,o.order_status,o.total_amount,o.addtime,case when rel.order_id is null then 0 else 1 end as linked " +
                        "from cosorder o left join (select order_id from cos_designer_portfolio_order_rel where portfolio_id=?) rel on rel.order_id=o.id " +
                        "where o.designer_id=? and (o.designer_table=? or o.designer_table is null or o.designer_table='') " +
                        "and (o.pay_status=? or o.pay_status=?) and o.order_status in (?,?,?,?) order by o.id desc limit 200",
                safePortfolioId,
                designerId,
                designerTable(request),
                PAY_STATUS_CN,
                PAY_STATUS_EN,
                ORDER_STATUS_SHIPPED,
                ORDER_STATUS_DONE,
                ORDER_STATUS_SHIPPED_EN,
                ORDER_STATUS_DONE_EN
        );
        return R.ok().put("data", list);
    }

    @PostMapping("/portfolio/link-order")
    public R portfolioLinkOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u5173\u8054\u6848\u4f8b\u8ba2\u5355");
        }
        Long designerId = uid(request);
        Long portfolioId = parseLong(body.get("portfolioId"));
        if (portfolioId == null || !ownPortfolio(portfolioId, designerId)) {
            return R.error(403, "\u65e0\u6743\u64cd\u4f5c\u8be5\u4f5c\u54c1");
        }
        List<Long> orderIds = parseIdList(body.get("orderIds"), body.get("orderId"));
        if (orderIds.isEmpty()) {
            return R.error(400, "\u8bf7\u9009\u62e9\u8981\u5173\u8054\u7684\u8ba2\u5355");
        }

        for (Long orderId : orderIds) {
            List<Map<String, Object>> orderRows = queryEligibleOrderRows(orderId, designerId);
            if (orderRows.isEmpty() || !eligiblePortfolioOrder(orderRows.get(0))) {
                return R.error(400, "\u53ea\u80fd\u5173\u8054\u5f53\u524d\u8bbe\u8ba1\u5e08\u672c\u4eba\u5df2\u652f\u4ed8\u4e14\u5df2\u53d1\u8d27\u6216\u5df2\u5b8c\u6210\u7684\u8ba2\u5355");
            }
            Map<String, Object> order = orderRows.get(0);
            Long count = jdbcTemplate.queryForObject(
                    "select count(1) from cos_designer_portfolio_order_rel where portfolio_id=? and order_id=?",
                    new Object[]{portfolioId, orderId},
                    Long.class
            );
            if (count != null && count > 0) {
                continue;
            }
            jdbcTemplate.update(
                    "insert into cos_designer_portfolio_order_rel(id,addtime,portfolio_id,order_id,order_no,designer_id,designer_table) values(?,now(),?,?,?,?,?)",
                    System.currentTimeMillis() + orderId,
                    portfolioId,
                    orderId,
                    trimValue(order.get("order_no")),
                    designerId,
                    designerTable(request)
            );
        }
        return R.ok("\u6848\u4f8b\u8ba2\u5355\u5df2\u5173\u8054");
    }

    @PostMapping("/portfolio/unlink-order")
    public R portfolioUnlinkOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u53d6\u6d88\u5173\u8054\u6848\u4f8b\u8ba2\u5355");
        }
        Long designerId = uid(request);
        Long portfolioId = parseLong(body.get("portfolioId"));
        if (portfolioId == null || !ownPortfolio(portfolioId, designerId)) {
            return R.error(403, "\u65e0\u6743\u64cd\u4f5c\u8be5\u4f5c\u54c1");
        }
        List<Long> orderIds = parseIdList(body.get("orderIds"), body.get("orderId"));
        if (orderIds.isEmpty()) {
            return R.error(400, "\u8bf7\u9009\u62e9\u8981\u53d6\u6d88\u5173\u8054\u7684\u8ba2\u5355");
        }
        for (Long orderId : orderIds) {
            jdbcTemplate.update(
                    "delete from cos_designer_portfolio_order_rel where portfolio_id=? and order_id=? and designer_id=? and designer_table=?",
                    portfolioId,
                    orderId,
                    designerId,
                    designerTable(request)
            );
        }
        return R.ok("\u6848\u4f8b\u8ba2\u5355\u5df2\u53d6\u6d88\u5173\u8054");
    }

    @GetMapping("/revenue/summary")
    public R revenueSummary(HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u67e5\u770b\u6536\u76ca\u770b\u677f");
        }
        Long designerId = uid(request);
        LocalDate now = LocalDate.now();
        String monthStart = now.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE) + " 00:00:00";
        Map<String, Object> row = jdbcTemplate.queryForMap(
                "select coalesce(sum(total_amount),0) as totalAmount, " +
                        "coalesce(sum(case when order_status in (?,?) then total_amount else 0 end),0) as completedAmount, " +
                        "coalesce(sum(case when order_status is null or order_status='' or order_status not in (?,?) then total_amount else 0 end),0) as inProgressAmount, " +
                        "coalesce(sum(case when addtime>=? then 1 else 0 end),0) as monthOrderCount " +
                        "from cosorder where designer_id=? and (designer_table=? or designer_table is null or designer_table='') " +
                        "and (pay_status=? or pay_status=?) and (order_status is null or order_status not in (?,?))",
                ORDER_STATUS_DONE,
                ORDER_STATUS_DONE_EN,
                ORDER_STATUS_DONE,
                ORDER_STATUS_DONE_EN,
                monthStart,
                designerId,
                designerTable(request),
                PAY_STATUS_CN,
                PAY_STATUS_EN,
                ORDER_STATUS_CANCEL,
                ORDER_STATUS_CANCEL_EN
        );
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalAmount", toDecimal(row.get("totalAmount")));
        data.put("completedAmount", toDecimal(row.get("completedAmount")));
        data.put("inProgressAmount", toDecimal(row.get("inProgressAmount")));
        data.put("monthOrderCount", parseInt(row.get("monthOrderCount"), 0));
        return R.ok().put("data", data);
    }

    @GetMapping("/revenue/trend")
    public R revenueTrend(HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u67e5\u770b\u6536\u76ca\u8d8b\u52bf");
        }
        Long designerId = uid(request);
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        String startTime = start.format(DateTimeFormatter.ISO_DATE) + " 00:00:00";
        String endTime = end.format(DateTimeFormatter.ISO_DATE) + " 23:59:59";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select date_format(addtime,'%Y-%m-%d') as statDate, coalesce(sum(total_amount),0) as totalAmount, count(1) as orderCount " +
                        "from cosorder where designer_id=? and (designer_table=? or designer_table is null or designer_table='') " +
                        "and (pay_status=? or pay_status=?) and (order_status is null or order_status not in (?,?)) " +
                        "and addtime>=? and addtime<=? group by date_format(addtime,'%Y-%m-%d') order by statDate asc",
                designerId,
                designerTable(request),
                PAY_STATUS_CN,
                PAY_STATUS_EN,
                ORDER_STATUS_CANCEL,
                ORDER_STATUS_CANCEL_EN,
                startTime,
                endTime
        );

        Map<String, Map<String, Object>> indexed = new HashMap<>();
        for (Map<String, Object> row : rows) {
            indexed.put(trimValue(row.get("statDate")), row);
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            String date = cursor.format(DateTimeFormatter.ISO_DATE);
            Map<String, Object> raw = indexed.get(date);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date);
            item.put("amount", raw == null ? BigDecimal.ZERO : toDecimal(raw.get("totalAmount")));
            item.put("count", raw == null ? 0 : parseInt(raw.get("orderCount"), 0));
            trend.add(item);
            cursor = cursor.plusDays(1);
        }
        return R.ok().put("data", trend);
    }

    @GetMapping("/revenue/flow/page")
    public R revenueFlowPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (!isDesigner(request)) {
            return R.error(403, "\u4ec5\u8bbe\u8ba1\u5e08\u53ef\u67e5\u770b\u6536\u76ca\u6d41\u6c34");
        }
        Long designerId = uid(request);
        int page = Math.max(parseInt(params.get("page"), 1), 1);
        int limit = parseInt(params.get("limit"), 10);
        if (limit < 1) {
            limit = 10;
        }
        if (limit > 50) {
            limit = 50;
        }

        StringBuilder where = new StringBuilder(" where designer_id=? and (designer_table=? or designer_table is null or designer_table='') " +
                "and (pay_status=? or pay_status=?) and (order_status is null or order_status not in (?,?)) ");
        List<Object> args = new ArrayList<>();
        args.add(designerId);
        args.add(designerTable(request));
        args.add(PAY_STATUS_CN);
        args.add(PAY_STATUS_EN);
        args.add(ORDER_STATUS_CANCEL);
        args.add(ORDER_STATUS_CANCEL_EN);

        String orderNo = trimValue(params.get("orderNo"));
        if (StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,order_no,addtime,order_status,total_amount,designer_status from cosorder " + where +
                        " order by id desc limit ? offset ?",
                listArgs.toArray()
        );
        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
                args.toArray(),
                Long.class
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);
        return R.ok().put("data", data);
    }
}