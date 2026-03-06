package com.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CoscartEntity;
import com.entity.CosorderEntity;
import com.service.CoscartService;
import com.service.CosOrderFlowService;
import com.service.CosorderService;
import com.utils.CosRoleUtil;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

//cos服装定制订单
@RestController
@RequestMapping("/cosorder")
public class CosorderController {
    @Autowired private CosorderService cosorderService;
    @Autowired private CoscartService coscartService;
    @Autowired private CosOrderFlowService cosOrderFlowService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Long uid(HttpServletRequest req){ return Long.valueOf(req.getSession().getAttribute("userId").toString()); }
    private String utable(HttpServletRequest req){ return req.getSession().getAttribute("tableName").toString(); }
    private String role(HttpServletRequest req){ return req.getSession().getAttribute("role").toString(); }
    private String roleCode(HttpServletRequest req){ return CosRoleUtil.normalize(role(req), utable(req)); }
    private boolean isAdmin(HttpServletRequest req){ return CosRoleUtil.isAdmin(role(req), utable(req)); }
    private boolean isDesigner(HttpServletRequest req){ return CosRoleUtil.DESIGNER.equals(roleCode(req)); }

    @RequestMapping("/page")
    public R page(HttpServletRequest request){
        List<CosorderEntity> list = cosorderService.selectList(
                new EntityWrapper<CosorderEntity>()
                        .eq("user_id", uid(request))
                        .eq("user_table", utable(request))
                        .orderBy("id", false)
        );
        return R.ok().put("data", list);
    }

    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        CosorderEntity o = cosorderService.selectById(id);
        if(o==null) return R.error("订单不存在");
        if(!uid(request).equals(o.getUserId())) return R.error(403,"无权限");
        return R.ok().put("data", o);
    }

    @PostMapping("/submit")
    public R submit(@RequestBody Map<String,Object> body, HttpServletRequest request){
        Object cartIdsObj = body.get("cartIds");
        if(!(cartIdsObj instanceof List)) return R.error("cartIds参数错误");

        @SuppressWarnings("unchecked")
        List<Object> ids = (List<Object>) cartIdsObj;
        if(ids.isEmpty()) return R.error("请选择购物车商品");

        List<Long> cartIds = new ArrayList<>();
        for(Object idObj : ids){
            if(idObj == null) continue;
            try{
                if(idObj instanceof Number){
                    cartIds.add(((Number) idObj).longValue());
                }else{
                    cartIds.add(Long.valueOf(idObj.toString()));
                }
            }catch (Exception ignore){
                return R.error("cartIds存在非法值");
            }
        }
        if(cartIds.isEmpty()) return R.error("请选择购物车商品");

        List<CoscartEntity> carts = coscartService.selectBatchIds(cartIds);
        if(carts==null || carts.isEmpty()) return R.error("购物车为空");

        BigDecimal total = BigDecimal.ZERO;
        for(CoscartEntity c: carts){
            if(!uid(request).equals(c.getUserId())) return R.error(403,"购物车数据非法");
            total = total.add(c.getAmount()==null?BigDecimal.ZERO:c.getAmount());
        }

        CosorderEntity o = new CosorderEntity();
        o.setId(new Date().getTime() + (long)(Math.random()*1000));
        o.setOrderNo("COS" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        o.setUserId(uid(request));
        o.setUserTable(utable(request));
        o.setTotalAmount(total);
        o.setPayStatus("未支付");
        o.setOrderStatus("待确认");
        o.setPayType(body.get("payType")==null?"模拟支付":body.get("payType").toString());
        o.setContactName(body.get("contactName")==null?null:body.get("contactName").toString());
        o.setContactPhone(body.get("contactPhone")==null?null:body.get("contactPhone").toString());
        o.setAddress(body.get("address")==null?null:body.get("address").toString());
        o.setRemark(body.get("remark")==null?null:body.get("remark").toString());
        o.setItemsJson(JSON.toJSONString(carts));

        cosorderService.insert(o);
        cosOrderFlowService.recordOrderCreated(o, uid(request), roleCode(request), "用户下单创建订单");
        coscartService.deleteBatchIds(cartIds);

        return R.ok().put("data", o);
    }

    @PostMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, @RequestBody(required = false) Map<String,Object> body, HttpServletRequest request){
        CosorderEntity o = cosorderService.selectById(id);
        if(o==null) return R.error("订单不存在");
        if(!uid(request).equals(o.getUserId())) return R.error(403,"无权限");

        String err = cosOrderFlowService.markPaySuccessByOrderNo(
                o.getOrderNo(),
                null,
                "LEGACY" + System.currentTimeMillis(),
                uid(request),
                roleCode(request),
                "旧接口模拟支付"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        if(body!=null && body.get("payType")!=null) {
            o.setPayType(body.get("payType").toString());
            cosorderService.updateById(o);
        }
        return R.ok("支付成功(模拟)");
    }

    @PostMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id, HttpServletRequest request){
        String err = cosOrderFlowService.userCancel(
                id,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                "用户主动取消"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }
        return R.ok("订单已取消");
    }

    @GetMapping("/admin/page")
    public R adminPage(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where 1=1 ");
        List<Object> args = new ArrayList<>();

        String orderNo = str(params.get("orderNo"));
        if(StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo.trim() + "%");
        }

        String payStatus = str(params.get("payStatus"));
        if(StringUtils.isNotBlank(payStatus)) {
            where.append(" and pay_status = ? ");
            args.add(payStatus.trim());
        }

        String orderStatus = str(params.get("orderStatus"));
        if(StringUtils.isNotBlank(orderStatus)) {
            where.append(" and order_status = ? ");
            args.add(orderStatus.trim());
        }

        Long userId = parseLong(params.get("userId"));
        if(userId != null) {
            where.append(" and user_id = ? ");
            args.add(userId);
        }

        String dateFrom = normalizeDateBoundary(str(params.get("dateFrom")), false);
        if(StringUtils.isNotBlank(dateFrom)) {
            where.append(" and addtime >= ? ");
            args.add(dateFrom);
        }

        String dateTo = normalizeDateBoundary(str(params.get("dateTo")), true);
        if(StringUtils.isNotBlank(dateTo)) {
            where.append(" and addtime <= ? ");
            args.add(dateTo);
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder " + where + " order by id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
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
    public R adminDetail(@PathVariable("id") Long id, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        Map<String, Object> order = cosOrderFlowService.queryOrderById(id);
        if(order == null) {
            return R.error("订单不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("statusLogs", cosOrderFlowService.listStatusLogs(id));
        return R.ok().put("data", data);
    }

    @PostMapping("/admin/transition")
    public R adminTransition(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "无权限");
        }

        Long orderId = parseLong(body.get("orderId"));
        String toOrderStatus = str(body.get("toOrderStatus"));
        String remark = str(body.get("remark"));

        String err = cosOrderFlowService.adminTransition(
                orderId,
                toOrderStatus,
                uid(request),
                roleCode(request),
                remark
        );

        if(StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        return R.ok("状态流转成功");
    }

    @GetMapping("/admin/status-log/{orderId}")
    public R adminStatusLog(@PathVariable("orderId") Long orderId, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "无权限");
        }
        return R.ok().put("data", cosOrderFlowService.listStatusLogs(orderId));
    }


    @GetMapping("/designer/pool")
    public R designerPool(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "无权限");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where pay_status=? and (designer_status is null or designer_status='' or designer_status=?) and (designer_id is null or designer_id=0) ");
        List<Object> args = new ArrayList<>();
        args.add("已支付");
        args.add("待接单");

        String orderNo = str(params.get("orderNo"));
        if(StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo.trim() + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder " + where + " order by id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
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

    @PostMapping("/designer/claim")
    public R designerClaim(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "无权限");
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, "orderId不能为空");
        }

        Long designerId = uid(request);
        String designerTable = utable(request);

        int updated = jdbcTemplate.update(
                "update cosorder set designer_id=?, designer_table=?, designer_status=?, designer_take_time=now() " +
                        "where id=? and pay_status=? and (designer_status is null or designer_status='' or designer_status=?) " +
                        "and (designer_id is null or designer_id=0)",
                designerId,
                designerTable,
                "已认领",
                orderId,
                "已支付",
                "待接单"
        );

        if(updated > 0) {
            return R.ok("认领成功");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,pay_status,designer_id,designer_table,designer_status from cosorder where id=? limit 1",
                orderId
        );
        if(rows.isEmpty()) {
            return R.error(404, "订单不存在");
        }

        Map<String, Object> current = rows.get(0);
        String payStatus = str(current.get("pay_status"));
        if(!"已支付".equals(payStatus)) {
            return R.error(400, "仅已支付订单可认领");
        }

        Long currentDesignerId = parseLong(current.get("designer_id"));
        String currentDesignerTable = str(current.get("designer_table"));
        String currentDesignerStatus = str(current.get("designer_status"));

        if(currentDesignerId != null && currentDesignerId > 0) {
            if(designerId.equals(currentDesignerId) && StringUtils.equalsIgnoreCase(designerTable, currentDesignerTable)) {
                return R.ok("该订单已在我的订单中");
            }
            return R.error(409, "订单已被其他设计师认领");
        }

        if(StringUtils.isNotBlank(currentDesignerStatus) && !"待接单".equals(currentDesignerStatus)) {
            return R.error(409, "订单状态不可认领");
        }

        return R.error(409, "订单已被认领，请刷新后重试");
    }


    @PostMapping("/designer/start")
    public R designerStart(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "无权限");
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, "orderId不能为空");
        }

        String remark = str(body.get("remark"));
        String err = cosOrderFlowService.designerStartProduction(
                orderId,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                remark
        );

        if(StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        return R.ok("开始制作成功");
    }

    @PostMapping("/designer/ship")
    public R designerShip(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "无权限");
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, "orderId不能为空");
        }

        String remark = str(body.get("remark"));
        String err = cosOrderFlowService.designerShip(
                orderId,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                remark
        );

        if(StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        return R.ok("发货成功");
    }
    @GetMapping("/designer/mine")
    public R designerMine(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "无权限");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where designer_id=? and designer_table=? ");
        List<Object> args = new ArrayList<>();
        args.add(uid(request));
        args.add(utable(request));

        String orderNo = str(params.get("orderNo"));
        if(StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo.trim() + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder " + where + " order by designer_take_time desc, id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
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
    private static String normalizeDateBoundary(String raw, boolean endOfDay) {
        if(StringUtils.isBlank(raw)) {
            return null;
        }
        String safe = raw.trim();
        if(safe.length() == 10) {
            return safe + (endOfDay ? " 23:59:59" : " 00:00:00");
        }
        return safe;
    }

    private static Long parseLong(Object value){
        if(value == null) return null;
        if(value instanceof Number) return ((Number)value).longValue();
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return null;
        }
    }

    private static int parseInt(Object value, int defaultValue){
        if(value == null) return defaultValue;
        if(value instanceof Number) return ((Number)value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private static String str(Object value){
        return value == null ? null : String.valueOf(value);
    }
}
