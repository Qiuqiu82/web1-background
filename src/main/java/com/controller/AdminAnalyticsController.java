package com.controller;

import com.utils.CosRoleUtil;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    private static final String PAY_PAID = "已支付";
    private static final String PAY_PAID_EN = "PAID";
    private static final String PAY_UNPAID = "未支付";
    private static final String ORDER_PENDING_CONFIRM = "待确认";
    private static final String ORDER_PENDING_PRODUCE = "待生产";
    private static final String ORDER_PRODUCING = "生产中";
    private static final String ORDER_SHIPPED = "已发货";
    private static final String ORDER_FINISHED = "已完成";
    private static final String ORDER_CANCELED = "已取消";
    private static final String ORDER_CANCELED_EN = "CANCELLED";
    private static final String ORDER_PAID = "已支付";
    private static final String ORDER_PAID_EN = "PAID";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/overview")
    public R overview(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "仅管理员可访问统计接口");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userTotal", queryLong("select count(1) from yonghu"));
        data.put("designerTotal", queryLong("select count(1) from shejishi"));
        data.put("orderTotal", queryLong("select count(1) from cosorder"));
        data.put("paidOrderCount", queryLong(
                "select count(1) from cosorder where pay_status in (?,?)",
                PAY_PAID,
                PAY_PAID_EN
        ));
        data.put("todayOrderCount", queryLong("select count(1) from cosorder where date(addtime)=curdate()"));
        data.put("totalRevenue", queryDecimal(
                "select coalesce(sum(total_amount),0) from cosorder where pay_status in (?,?) and (order_status not in (?,?) or order_status is null or order_status='')",
                PAY_PAID,
                PAY_PAID_EN,
                ORDER_CANCELED,
                ORDER_CANCELED_EN
        ));
        data.put("recentOrders", safeQueryList(
                "select id,order_no as orderNo,total_amount as totalAmount,pay_status as payStatus,order_status as orderStatus,addtime from cosorder order by id desc limit 6"
        ));
        data.put("categoryTop", safeQueryList(
                "select ifnull(category_name,'未分类') as name,count(1) as count from cos_material_asset where deleted=0 group by ifnull(category_name,'未分类') order by count(1) desc limit 6"
        ));
        return R.ok().put("data", data);
    }

    @GetMapping("/order-funnel")
    public R orderFunnel(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "仅管理员可访问统计接口");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("unpaidCount", queryLong(
                "select count(1) from cosorder where (pay_status is null or pay_status='' or pay_status=?) and (order_status not in (?,?) or order_status is null or order_status='')",
                PAY_UNPAID,
                ORDER_CANCELED,
                ORDER_CANCELED_EN
        ));
        data.put("paidCount", queryLong(
                "select count(1) from cosorder where pay_status in (?,?) and (order_status is null or order_status='' or order_status in (?,?))",
                PAY_PAID,
                PAY_PAID_EN,
                ORDER_PAID,
                ORDER_PAID_EN
        ));
        data.put("pendingProduceCount", queryLong(
                "select count(1) from cosorder where pay_status in (?,?) and order_status in (?,?)",
                PAY_PAID,
                PAY_PAID_EN,
                ORDER_PENDING_CONFIRM,
                ORDER_PENDING_PRODUCE
        ));
        data.put("producingCount", queryLong(
                "select count(1) from cosorder where order_status=?",
                ORDER_PRODUCING
        ));
        data.put("shippedCount", queryLong(
                "select count(1) from cosorder where order_status=?",
                ORDER_SHIPPED
        ));
        data.put("finishedCount", queryLong(
                "select count(1) from cosorder where order_status=?",
                ORDER_FINISHED
        ));
        data.put("canceledCount", queryLong(
                "select count(1) from cosorder where order_status in (?,?)",
                ORDER_CANCELED,
                ORDER_CANCELED_EN
        ));
        return R.ok().put("data", data);
    }

    @GetMapping("/order-trend")
    public R orderTrend(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "仅管理员可访问统计接口");
        }
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(29);
        String startTime = start.format(DateTimeFormatter.ISO_DATE) + " 00:00:00";
        String endTime = today.format(DateTimeFormatter.ISO_DATE) + " 23:59:59";

        List<Map<String, Object>> rows = safeQueryList(
                "select date_format(addtime,'%Y-%m-%d') as statDate, count(1) as orderCount, " +
                        "coalesce(sum(case when pay_status in (?,?) and (order_status not in (?,?) or order_status is null or order_status='') then total_amount else 0 end),0) as paidAmount, " +
                        "sum(case when order_status=? then 1 else 0 end) as finishedCount " +
                        "from cosorder where addtime>=? and addtime<=? group by date_format(addtime,'%Y-%m-%d') order by statDate asc",
                PAY_PAID,
                PAY_PAID_EN,
                ORDER_CANCELED,
                ORDER_CANCELED_EN,
                ORDER_FINISHED,
                startTime,
                endTime
        );

        Map<String, Map<String, Object>> rowMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            rowMap.put(str(row.get("statDate")), row);
        }

        List<Map<String, Object>> days30 = buildTrendRows(start, today, rowMap);
        List<Map<String, Object>> days7 = days30.subList(Math.max(days30.size() - 7, 0), days30.size());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("days7", days7);
        data.put("days30", days30);
        return R.ok().put("data", data);
    }

    @GetMapping("/order-stay-duration")
    public R orderStayDuration(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "仅管理员可访问统计接口");
        }
        List<Map<String, Object>> rows = safeQueryList(
                "select stageLabel as label,count(1) as count,round(avg(stageMinutes),1) as avgMinutes,round(avg(stageMinutes)/60,2) as avgHours from (" +
                        "select case " +
                        "when o.order_status in ('已取消','CANCELLED') then '已取消' " +
                        "when (o.pay_status is null or o.pay_status='' or o.pay_status='未支付') then '未支付' " +
                        "when o.order_status in ('待确认','待生产') then '待生产' " +
                        "when o.order_status='生产中' then '生产中' " +
                        "when o.order_status='已发货' then '已发货' " +
                        "when o.order_status='已完成' then '已完成' " +
                        "else '已支付' end as stageLabel, " +
                        "timestampdiff(minute, case " +
                        "when o.order_status in ('已取消','CANCELLED') then ifnull((select max(l.addtime) from cosorder_status_log l where l.order_id=o.id and l.to_order_status=o.order_status), o.addtime) " +
                        "when (o.pay_status is null or o.pay_status='' or o.pay_status='未支付') then o.addtime " +
                        "when o.order_status in ('待确认','待生产','生产中','已发货','已完成') then ifnull((select max(l.addtime) from cosorder_status_log l where l.order_id=o.id and l.to_order_status=o.order_status), ifnull(o.pay_time, o.addtime)) " +
                        "else ifnull(o.pay_time, o.addtime) end, now()) as stageMinutes " +
                        "from cosorder o) t group by stageLabel"
        );
        return R.ok().put("data", sortStageRows(rows));
    }

    @GetMapping("/designer-efficiency")
    public R designerEfficiency(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.error(403, "仅管理员可访问统计接口");
        }
        List<Map<String, Object>> list = safeQueryList(
                "select s.id,s.shejishixingming as designerName,s.shejishizhanghao as designerAccount, " +
                        "count(case when o.designer_id is not null then 1 end) as claimCount, " +
                        "sum(case when o.order_status in ('待生产','生产中') then 1 else 0 end) as producingCount, " +
                        "sum(case when o.order_status in ('已发货','已完成') then 1 else 0 end) as deliveredCount, " +
                        "coalesce(sum(case when o.order_status='已完成' and o.pay_status in (?,?) then o.total_amount else 0 end),0) as finishedAmount " +
                        "from shejishi s left join cosorder o on o.designer_id=s.id and (o.designer_table='shejishi' or o.designer_table is null or o.designer_table='') " +
                        "group by s.id,s.shejishixingming,s.shejishizhanghao order by finishedAmount desc, claimCount desc, s.id desc",
                PAY_PAID,
                PAY_PAID_EN
        );
        return R.ok().put("data", list);
    }

    private List<Map<String, Object>> buildTrendRows(LocalDate start,
                                                     LocalDate end,
                                                     Map<String, Map<String, Object>> rowMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            String date = cursor.format(DateTimeFormatter.ISO_DATE);
            Map<String, Object> raw = rowMap.get(date);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", date);
            row.put("label", cursor.getMonthValue() + "/" + cursor.getDayOfMonth());
            row.put("orderCount", longValue(raw == null ? null : raw.get("orderCount")));
            row.put("paidAmount", decimalValue(raw == null ? null : raw.get("paidAmount")));
            row.put("finishedCount", longValue(raw == null ? null : raw.get("finishedCount")));
            result.add(row);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    private List<Map<String, Object>> sortStageRows(List<Map<String, Object>> rows) {
        List<String> order = new ArrayList<>();
        order.add("未支付");
        order.add("已支付");
        order.add("待生产");
        order.add("生产中");
        order.add("已发货");
        order.add("已完成");
        order.add("已取消");
        List<Map<String, Object>> result = new ArrayList<>();
        for (String label : order) {
            for (Map<String, Object> row : rows) {
                if (label.equals(str(row.get("label")))) {
                    result.add(row);
                    break;
                }
            }
        }
        return result;
    }

    private boolean isAdmin(HttpServletRequest request) {
        return CosRoleUtil.isAdmin(sessionStr(request, "role"), sessionStr(request, "tableName"));
    }

    private String sessionStr(HttpServletRequest request, String key) {
        Object value = request.getSession().getAttribute(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ignore) {
            return 0L;
        }
    }

    private BigDecimal decimalValue(Object value) {
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


    private List<Map<String, Object>> safeQueryList(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForList(sql, args);
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
    }
    private long queryLong(String sql, Object... args) {
        try {
            Long value = jdbcTemplate.queryForObject(sql, args, Long.class);
            return value == null ? 0L : value;
        } catch (Exception ignore) {
            return 0L;
        }
    }

    private BigDecimal queryDecimal(String sql, Object... args) {
        try {
            BigDecimal value = jdbcTemplate.queryForObject(sql, args, BigDecimal.class);
            return value == null ? BigDecimal.ZERO : value;
        } catch (Exception ignore) {
            return BigDecimal.ZERO;
        }
    }
}