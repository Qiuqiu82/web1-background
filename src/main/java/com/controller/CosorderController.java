package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

// cos订单
@RestController
@RequestMapping("/cosorder")
public class CosorderController {
    private static final String PAY_UNPAID = "未支付";
    private static final String PAY_PAID = "已支付";
    private static final String ORDER_PENDING_CONFIRM = "待确认";
    private static final String DESIGNER_WAIT_CLAIM = "待接单";
    private static final String DESIGNER_CLAIMED = "已认领";
    private static final String MSG_DESIGNER_ONLY = "仅设计师可访问该接口";
    private static final String MSG_ORDER_ID_REQUIRED = "orderId不能为空";
    private static final String MSG_CLAIM_SUCCESS = "认领成功";
    private static final String MSG_CLAIM_SUCCESS_SELF = "该订单已由你认领";
    private static final String MSG_START_SUCCESS = "开始制作成功";
    private static final String MSG_SHIP_SUCCESS = "发货成功";
    private static final String MSG_ORDER_NOT_FOUND = "订单不存在";
    private static final String MSG_ORDER_NOT_PAID_FOR_CLAIM = "仅已支付订单可认领";
    private static final String MSG_ORDER_ALREADY_CLAIMED_OTHER = "该订单已被其他设计师认领";
    private static final String MSG_ORDER_NOT_WAIT_CLAIM = "当前订单状态不可认领";
    private static final String MSG_ORDER_STATUS_CHANGED = "订单状态已变更，请刷新后重试";
    private static final String MSG_ADMIN_ONLY = "仅管理员可访问该接口";
    private static final String MSG_ORDER_FORBIDDEN = "无权访问该订单";
    private static final String MSG_CART_IDS_REQUIRED = "cartIds不能为空且必须为数组";
    private static final String MSG_CART_ID_INVALID = "cartIds包含非法值";
    private static final String MSG_CART_NOT_FOUND = "购物车商品不存在";
    private static final String MSG_CART_FORBIDDEN = "购物车商品不属于当前用户";
    private static final String MSG_SIZE_CODE_INVALID = "尺码仅支持 S/M/L/XL";
    private static final String MSG_PAY_SUCCESS = "支付成功";
    private static final String MSG_CANCEL_SUCCESS = "订单取消成功";
    private static final String MSG_CONFIRM_RECEIPT_SUCCESS = "确认收货成功";
    private static final String MSG_ADMIN_TRANSITION_SUCCESS = "订单状态流转成功";
    private static final String MSG_COMM_FORBIDDEN = "仅用户或设计师可访问该接口";
    private static final String MSG_COMM_CONTENT_REQUIRED = "消息内容不能为空";
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
    private boolean isUser(HttpServletRequest req){ return CosRoleUtil.USER.equals(roleCode(req)); }

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
        if(o==null) return R.error(MSG_ORDER_NOT_FOUND);
        if(!uid(request).equals(o.getUserId())) return R.error(403, MSG_ORDER_FORBIDDEN);
        return R.ok().put("data", o);
    }
    @PostMapping("/submit")
    public R submit(@RequestBody Map<String,Object> body, HttpServletRequest request){
        Object cartIdsObj = body.get("cartIds");
        if(!(cartIdsObj instanceof List)) return R.error(MSG_CART_IDS_REQUIRED);

        @SuppressWarnings("unchecked")
        List<Object> ids = (List<Object>) cartIdsObj;
        if(ids.isEmpty()) return R.error(MSG_CART_IDS_REQUIRED);

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
                return R.error(MSG_CART_ID_INVALID);
            }
        }
        if(cartIds.isEmpty()) return R.error(MSG_CART_IDS_REQUIRED);

        List<CoscartEntity> carts = coscartService.selectBatchIds(cartIds);
        if(carts==null || carts.isEmpty()) return R.error(MSG_CART_NOT_FOUND);

        BigDecimal total = BigDecimal.ZERO;
        for(CoscartEntity c: carts){
            if(!uid(request).equals(c.getUserId())) return R.error(403, MSG_CART_FORBIDDEN);
            total = total.add(c.getAmount()==null?BigDecimal.ZERO:c.getAmount());
        }

        Long addressId = parseLong(body.get("addressId"));
        Long bodyProfileId = parseLong(body.get("bodyProfileId"));

        Map<String,Object> addressSnapshot = normalizeAddressSnapshot(asMap(body.get("addressSnapshot")));
        if(addressSnapshot.isEmpty() && addressId != null){
            addressSnapshot = queryAddressSnapshot(addressId, uid(request), utable(request));
        }
        if(addressSnapshot.isEmpty()){
            addressSnapshot = buildLegacyAddressSnapshot(
                    str(body.get("contactName")),
                    str(body.get("contactPhone")),
                    str(body.get("address"))
            );
        }

        String fallbackSizeCode = firstCartSizeCode(carts);
        if(StringUtils.isNotBlank(fallbackSizeCode) && !isAllowedSizeCode(fallbackSizeCode)){
            return R.error(400, MSG_SIZE_CODE_INVALID);
        }
        Map<String,Object> bodySnapshot = normalizeBodyProfileSnapshot(asMap(body.get("bodyProfileSnapshot")), fallbackSizeCode);
        if(bodySnapshot.isEmpty() && bodyProfileId != null){
            bodySnapshot = queryBodySnapshot(bodyProfileId, uid(request), utable(request), fallbackSizeCode);
        }
        if(bodySnapshot.isEmpty()){
            bodySnapshot = buildBodySnapshotFromCarts(carts, fallbackSizeCode);
        }

        String contactName = firstNonBlank(str(body.get("contactName")), str(addressSnapshot.get("receiverName")));
        String contactPhone = firstNonBlank(str(body.get("contactPhone")), str(addressSnapshot.get("receiverPhone")));
        String addressText = firstNonBlank(str(body.get("address")), composeAddressText(addressSnapshot));

        CosorderEntity o = new CosorderEntity();
        o.setId(new Date().getTime() + (long)(Math.random()*1000));
        o.setOrderNo("COS" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        o.setUserId(uid(request));
        o.setUserTable(utable(request));
        o.setTotalAmount(total);
        o.setPayStatus(PAY_UNPAID);
        o.setOrderStatus(ORDER_PENDING_CONFIRM);
        o.setPayType(StringUtils.isBlank(str(body.get("payType"))) ? "ALIPAY" : str(body.get("payType")));
        o.setContactName(contactName);
        o.setContactPhone(contactPhone);
        o.setAddress(addressText);
        o.setRemark(body.get("remark")==null?null:body.get("remark").toString());
        o.setItemsJson(JSON.toJSONString(carts));
        o.setAddressId(addressId);
        o.setBodyProfileId(bodyProfileId);
        o.setAddressSnapshotJson(addressSnapshot.isEmpty() ? null : JSON.toJSONString(addressSnapshot));
        o.setBodyProfileSnapshotJson(bodySnapshot.isEmpty() ? null : JSON.toJSONString(bodySnapshot));

        cosorderService.insert(o);
        cosOrderFlowService.recordOrderCreated(o, uid(request), roleCode(request), "鐢ㄦ埛鎻愪氦璁㈠崟");
        coscartService.deleteBatchIds(cartIds);

        return R.ok().put("data", o);
    }
    @PostMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, @RequestBody(required = false) Map<String,Object> body, HttpServletRequest request){
        CosorderEntity o = cosorderService.selectById(id);
        if(o==null) return R.error(MSG_ORDER_NOT_FOUND);
        if(!uid(request).equals(o.getUserId())) return R.error(403, MSG_ORDER_FORBIDDEN);

        String err = cosOrderFlowService.markPaySuccessByOrderNo(
                o.getOrderNo(),
                null,
                "LEGACY" + System.currentTimeMillis(),
                uid(request),
                roleCode(request),
                "鐢ㄦ埛鍙戣捣鏀粯"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        if(body!=null && body.get("payType")!=null) {
            o.setPayType(body.get("payType").toString());
            cosorderService.updateById(o);
        }
        return R.ok(MSG_PAY_SUCCESS);
    }
    @PostMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id, HttpServletRequest request){
        String err = cosOrderFlowService.userCancel(
                id,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                "鐢ㄦ埛鍙栨秷璁㈠崟"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }
        return R.ok(MSG_CANCEL_SUCCESS);
    }
    @PostMapping("/confirm-receipt/{id}")
    public R confirmReceipt(@PathVariable("id") Long id, HttpServletRequest request){
        String err = cosOrderFlowService.userConfirmReceipt(
                id,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                "鐢ㄦ埛纭鏀惰揣"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }
        return R.ok(MSG_CONFIRM_RECEIPT_SUCCESS);
    }
    @GetMapping("/admin/page")
    public R adminPage(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, MSG_ADMIN_ONLY);
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
            return R.error(403, MSG_ADMIN_ONLY);
        }

        Map<String, Object> order = cosOrderFlowService.queryOrderById(id);
        if(order == null) {
            return R.error(MSG_ORDER_NOT_FOUND);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("statusLogs", cosOrderFlowService.listStatusLogs(id));
        return R.ok().put("data", data);
    }
    @PostMapping("/admin/transition")
    public R adminTransition(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, MSG_ADMIN_ONLY);
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

        return R.ok(MSG_ADMIN_TRANSITION_SUCCESS);
    }
    @GetMapping("/admin/status-log/{orderId}")
    public R adminStatusLog(@PathVariable("orderId") Long orderId, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, MSG_ADMIN_ONLY);
        }
        return R.ok().put("data", cosOrderFlowService.listStatusLogs(orderId));
    }
    @GetMapping("/designer/pool")
    public R designerPool(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, MSG_DESIGNER_ONLY);
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where pay_status=? and (designer_status is null or designer_status='' or designer_status=?) and (designer_id is null or designer_id=0) ");
        List<Object> args = new ArrayList<>();
        args.add(PAY_PAID);
        args.add(DESIGNER_WAIT_CLAIM);

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
            return R.error(403, MSG_DESIGNER_ONLY);
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, MSG_ORDER_ID_REQUIRED);
        }

        Long designerId = uid(request);
        String designerTable = normalizeDesignerTable(utable(request));

        int updated = jdbcTemplate.update(
                "update cosorder set designer_id=?, designer_table=?, designer_status=?, designer_take_time=now() " +
                        "where id=? and pay_status=? and (designer_status is null or designer_status='' or designer_status=?) " +
                        "and (designer_id is null or designer_id=0)",
                designerId,
                designerTable,
                DESIGNER_CLAIMED,
                orderId,
                PAY_PAID,
                DESIGNER_WAIT_CLAIM
        );

        if(updated > 0) {
            return R.ok(MSG_CLAIM_SUCCESS);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,pay_status,designer_id,designer_table,designer_status from cosorder where id=? limit 1",
                orderId
        );
        if(rows.isEmpty()) {
            return R.error(404, MSG_ORDER_NOT_FOUND);
        }

        Map<String, Object> current = rows.get(0);
        String payStatus = str(current.get("pay_status"));
        if(!PAY_PAID.equals(payStatus)) {
            return R.error(400, MSG_ORDER_NOT_PAID_FOR_CLAIM);
        }

        Long currentDesignerId = parseLong(current.get("designer_id"));
        String currentDesignerTable = str(current.get("designer_table"));
        String currentDesignerStatus = str(current.get("designer_status"));

        if(currentDesignerId != null && currentDesignerId > 0) {
            if(designerId.equals(currentDesignerId) && designerTableMatches(designerTable, currentDesignerTable)) {
                return R.ok(MSG_CLAIM_SUCCESS_SELF);
            }
            return R.error(409, MSG_ORDER_ALREADY_CLAIMED_OTHER);
        }

        if(StringUtils.isNotBlank(currentDesignerStatus) && !DESIGNER_WAIT_CLAIM.equals(currentDesignerStatus)) {
            return R.error(409, MSG_ORDER_NOT_WAIT_CLAIM);
        }

        return R.error(409, MSG_ORDER_STATUS_CHANGED);
    }
    @PostMapping("/designer/start")
    public R designerStart(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, MSG_DESIGNER_ONLY);
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, MSG_ORDER_ID_REQUIRED);
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

        return R.ok(MSG_START_SUCCESS);
    }
    @PostMapping("/designer/ship")
    public R designerShip(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, MSG_DESIGNER_ONLY);
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, MSG_ORDER_ID_REQUIRED);
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

        appendDeliveryRecordForShip(orderId, request, remark);
        return R.ok(MSG_SHIP_SUCCESS);
    }
    @GetMapping("/designer/mine")
    public R designerMine(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, MSG_DESIGNER_ONLY);
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        Long designerId = uid(request);
        String designerTable = normalizeDesignerTable(utable(request));

        StringBuilder where = new StringBuilder(" where designer_id=? and (designer_table is null or designer_table='' or designer_table=?) ");
        List<Object> args = new ArrayList<>();
        args.add(designerId);
        args.add(designerTable);
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
    @GetMapping("/comm/session/page")
    public R commSessionPage(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request) && !isUser(request)) {
            return R.error(403, MSG_COMM_FORBIDDEN);
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        String keyword = str(params.get("keyword"));
        StringBuilder where = new StringBuilder(" where 1=1 ");
        List<Object> args = new ArrayList<>();

        if(isDesigner(request)) {
            where.append(" and o.designer_id=? ");
            args.add(uid(request));
        } else {
            where.append(" and o.user_id=? and o.user_table=? ");
            args.add(uid(request));
            args.add(utable(request));
        }

        if(StringUtils.isNotBlank(keyword)) {
            where.append(" and o.order_no like ? ");
            args.add("%" + keyword.trim() + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select o.id as orderId,o.order_no as orderNo,o.user_id as userId,o.user_table as userTable," +
                        "o.designer_id as designerId,o.designer_table as designerTable,o.pay_status as payStatus," +
                        "o.order_status as orderStatus,o.designer_status as designerStatus,o.addtime as orderTime," +
                        "o.designer_take_time as designerTakeTime,m.content as lastMessage,m.message_type as lastMessageType," +
                        "m.sender_role as lastSenderRole,m.sender_name as lastSenderName,m.addtime as lastMessageTime " +
                        "from cosorder o left join cos_order_message m on m.id=(" +
                        "select cm.id from cos_order_message cm where cm.order_id=o.id and cm.deleted=0 order by cm.id desc limit 1" +
                        ") " + where + " order by ifnull(m.addtime, ifnull(o.designer_take_time, o.addtime)) desc, o.id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder o " + where,
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

    @GetMapping("/comm/message/page")
    public R commMessagePage(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request) && !isUser(request)) {
            return R.error(403, MSG_COMM_FORBIDDEN);
        }

        Long orderId = parseLong(params.get("orderId"));
        if(orderId == null) {
            return R.error(400, MSG_ORDER_ID_REQUIRED);
        }

        Map<String, Object> order = queryOrderForComm(orderId);
        if(order == null) {
            return R.error(404, MSG_ORDER_NOT_FOUND);
        }
        if(!canAccessOrderComm(order, request)) {
            return R.error(403, MSG_ORDER_FORBIDDEN);
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 30);
        if(page < 1) page = 1;
        if(limit < 1) limit = 30;
        if(limit > 200) limit = 200;

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,addtime,order_id as orderId,order_no as orderNo,user_id as userId,user_table as userTable," +
                        "designer_id as designerId,designer_table as designerTable,sender_role as senderRole," +
                        "sender_id as senderId,sender_name as senderName,message_type as messageType,content " +
                        "from cos_order_message where order_id=? and deleted=0 order by id asc limit ? offset ?",
                orderId,
                limit,
                (page - 1) * limit
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cos_order_message where order_id=? and deleted=0",
                new Object[]{orderId},
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);
        data.put("order", order);
        return R.ok().put("data", data);
    }

    @PostMapping("/comm/send")
    public R commSend(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request) && !isUser(request)) {
            return R.error(403, MSG_COMM_FORBIDDEN);
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, MSG_ORDER_ID_REQUIRED);
        }

        String content = str(body.get("content"));
        if(StringUtils.isBlank(content)) {
            return R.error(400, MSG_COMM_CONTENT_REQUIRED);
        }

        Map<String, Object> order = queryOrderForComm(orderId);
        if(order == null) {
            return R.error(404, MSG_ORDER_NOT_FOUND);
        }
        if(!canAccessOrderComm(order, request)) {
            return R.error(403, MSG_ORDER_FORBIDDEN);
        }

        String senderRole = roleCode(request);
        insertOrderMessage(
                order,
                uid(request),
                senderRole,
                sessionDisplayName(request, senderRole),
                normalizeMessageType(str(body.get("messageType"))),
                content.trim()
        );

        return R.ok("发送成功");
    }

    @GetMapping("/comm/delivery")
    public R commDelivery(@RequestParam("orderId") Long orderId, HttpServletRequest request){
        if(!isDesigner(request) && !isUser(request)) {
            return R.error(403, MSG_COMM_FORBIDDEN);
        }
        if(orderId == null) {
            return R.error(400, MSG_ORDER_ID_REQUIRED);
        }

        Map<String, Object> order = queryOrderForComm(orderId);
        if(order == null) {
            return R.error(404, MSG_ORDER_NOT_FOUND);
        }
        if(!canAccessOrderComm(order, request)) {
            return R.error(403, MSG_ORDER_FORBIDDEN);
        }

        List<Map<String, Object>> timeline = new ArrayList<>();
        List<Map<String, Object>> logs = cosOrderFlowService.listStatusLogs(orderId);
        for(Map<String, Object> log : logs) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("time", firstNonBlank(str(log.get("addtime")), str(log.get("addTime"))));
            row.put("type", "STATUS");
            row.put("title", buildDeliveryTitle(log));
            row.put("content", firstNonBlank(str(log.get("remark")), "-"));
            row.put("operatorRole", firstNonBlank(str(log.get("operator_role")), str(log.get("operatorRole")), "-"));
            row.put("operatorName", firstNonBlank(str(log.get("operator_id")), str(log.get("operatorId")), "-"));
            timeline.add(row);
        }

        List<Map<String, Object>> deliveryRows = jdbcTemplate.queryForList(
                "select id,addtime,sender_role as senderRole,sender_name as senderName,content " +
                        "from cos_order_message where order_id=? and deleted=0 and message_type='DELIVERY' order by id asc",
                orderId
        );
        for(Map<String, Object> msg : deliveryRows) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("time", firstNonBlank(str(msg.get("addtime")), str(msg.get("addTime"))));
            row.put("type", "DELIVERY");
            row.put("title", "浜や粯璁板綍");
            row.put("content", firstNonBlank(str(msg.get("content")), "-"));
            row.put("operatorRole", firstNonBlank(str(msg.get("senderRole")), "-"));
            row.put("operatorName", firstNonBlank(str(msg.get("senderName")), "-"));
            timeline.add(row);
        }

        timeline.sort(Comparator.comparing(o -> StringUtils.defaultString(str(o.get("time")))));
        return R.ok().put("data", timeline);
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

    private static Map<String,Object> asMap(Object raw){
        if(raw == null){
            return new LinkedHashMap<>();
        }
        if(raw instanceof Map){
            return new LinkedHashMap<>((Map<String, Object>) raw);
        }
        if(raw instanceof JSONObject){
            return new LinkedHashMap<>((JSONObject) raw);
        }
        if(raw instanceof String){
            String txt = String.valueOf(raw).trim();
            if(StringUtils.isBlank(txt)){
                return new LinkedHashMap<>();
            }
            try {
                JSONObject json = JSON.parseObject(txt);
                return new LinkedHashMap<>(json);
            } catch (Exception ignore){
                return new LinkedHashMap<>();
            }
        }
        return new LinkedHashMap<>();
    }

    private Map<String,Object> queryAddressSnapshot(Long addressId, Long userId, String userTable){
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(
                "select receiver_name as receiverName,receiver_phone as receiverPhone,province,city,district,detail_address as detailAddress " +
                        "from cos_user_address where id=? and user_id=? and user_table=? and deleted=0 limit 1",
                addressId, userId, userTable
        );
        if(rows.isEmpty()){
            return new LinkedHashMap<>();
        }
        return normalizeAddressSnapshot(rows.get(0));
    }

    private Map<String,Object> queryBodySnapshot(Long profileId, Long userId, String userTable, String fallbackSizeCode){
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(
                "select profile_name as profileName,height_cm as heightCm,weight_kg as weightKg,waist_cm as waistCm,bust_cm as bustCm,hip_cm as hipCm,shoulder_cm as shoulderCm " +
                        "from cos_user_body_profile where id=? and user_id=? and user_table=? and deleted=0 limit 1",
                profileId, userId, userTable
        );
        if(rows.isEmpty()){
            return new LinkedHashMap<>();
        }
        return normalizeBodyProfileSnapshot(rows.get(0), fallbackSizeCode);
    }

    private static Map<String,Object> normalizeAddressSnapshot(Map<String,Object> raw){
        Map<String,Object> snapshot = new LinkedHashMap<>();
        if(raw == null || raw.isEmpty()){
            return snapshot;
        }
        putIfNotBlank(snapshot, "receiverName", pick(raw, "receiverName", "receiver_name", "contactName", "contact_name"));
        putIfNotBlank(snapshot, "receiverPhone", pick(raw, "receiverPhone", "receiver_phone", "contactPhone", "contact_phone"));
        putIfNotBlank(snapshot, "province", pick(raw, "province"));
        putIfNotBlank(snapshot, "city", pick(raw, "city"));
        putIfNotBlank(snapshot, "district", pick(raw, "district"));
        putIfNotBlank(snapshot, "detailAddress", pick(raw, "detailAddress", "detail_address", "address"));
        return snapshot;
    }

    private static Map<String,Object> normalizeBodyProfileSnapshot(Map<String,Object> raw, String fallbackSizeCode){
        Map<String,Object> snapshot = new LinkedHashMap<>();
        if(raw == null || raw.isEmpty()){
            return snapshot;
        }
        putIfNotBlank(snapshot, "profileName", pick(raw, "profileName", "profile_name"));
        putIfNotBlank(snapshot, "heightCm", pick(raw, "heightCm", "height_cm"));
        putIfNotBlank(snapshot, "weightKg", pick(raw, "weightKg", "weight_kg"));
        putIfNotBlank(snapshot, "waistCm", pick(raw, "waistCm", "waist_cm"));
        putIfNotBlank(snapshot, "bustCm", pick(raw, "bustCm", "bust_cm"));
        putIfNotBlank(snapshot, "hipCm", pick(raw, "hipCm", "hip_cm"));
        putIfNotBlank(snapshot, "shoulderCm", pick(raw, "shoulderCm", "shoulder_cm"));
        String sizeCode = firstNonBlank(str(pick(raw, "sizeCode", "size_code")), fallbackSizeCode);
        if(StringUtils.isNotBlank(sizeCode) && !isAllowedSizeCode(sizeCode)){
            sizeCode = null;
        }
        if(StringUtils.isNotBlank(sizeCode)){
            snapshot.put("sizeCode", sizeCode);
        }
        return snapshot;
    }

    private Map<String,Object> buildBodySnapshotFromCarts(List<CoscartEntity> carts, String fallbackSizeCode){
        if(carts == null || carts.isEmpty()){
            return new LinkedHashMap<>();
        }
        for(CoscartEntity cart : carts){
            if(cart == null || StringUtils.isBlank(cart.getCustomSnapshotJson())){
                continue;
            }
            try {
                JSONObject root = JSON.parseObject(cart.getCustomSnapshotJson());
                Map<String,Object> nested = asMap(root.get("bodyProfileSnapshot"));
                Map<String,Object> normalized = normalizeBodyProfileSnapshot(nested, firstNonBlank(str(root.get("sizeCode")), fallbackSizeCode));
                if(!normalized.isEmpty()){
                    return normalized;
                }
                normalized = normalizeBodyProfileSnapshot(asMap(root), firstNonBlank(str(root.get("sizeCode")), fallbackSizeCode));
                if(!normalized.isEmpty()){
                    return normalized;
                }
            } catch (Exception ignore){
                // ignore parse error
            }
        }
        return new LinkedHashMap<>();
    }

    private static Map<String,Object> buildLegacyAddressSnapshot(String contactName, String contactPhone, String address){
        Map<String,Object> snapshot = new LinkedHashMap<>();
        if(StringUtils.isNotBlank(contactName)){
            snapshot.put("receiverName", contactName.trim());
        }
        if(StringUtils.isNotBlank(contactPhone)){
            snapshot.put("receiverPhone", contactPhone.trim());
        }
        if(StringUtils.isNotBlank(address)){
            snapshot.put("detailAddress", address.trim());
        }
        return snapshot;
    }

    private static String composeAddressText(Map<String,Object> addressSnapshot){
        if(addressSnapshot == null || addressSnapshot.isEmpty()){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        appendAddressPart(sb, str(addressSnapshot.get("province")));
        appendAddressPart(sb, str(addressSnapshot.get("city")));
        appendAddressPart(sb, str(addressSnapshot.get("district")));
        appendAddressPart(sb, str(addressSnapshot.get("detailAddress")));
        return sb.length() == 0 ? null : sb.toString();
    }

    private static void appendAddressPart(StringBuilder sb, String value){
        if(StringUtils.isBlank(value)){
            return;
        }
        sb.append(value.trim());
    }

    private static String firstCartSizeCode(List<CoscartEntity> carts){
        if(carts == null || carts.isEmpty()){
            return null;
        }
        for(CoscartEntity cart : carts){
            if(cart == null || StringUtils.isBlank(cart.getCustomSnapshotJson())){
                continue;
            }
            try {
                JSONObject root = JSON.parseObject(cart.getCustomSnapshotJson());
                String sizeCode = str(root.get("sizeCode"));
                if(StringUtils.isNotBlank(sizeCode)){
                    return sizeCode;
                }
            } catch (Exception ignore){
                // ignore parse error
            }
        }
        return null;
    }

    private static Object pick(Map<String,Object> source, String... keys){
        if(source == null || keys == null){
            return null;
        }
        for(String key : keys){
            if(source.containsKey(key)){
                return source.get(key);
            }
        }
        return null;
    }

    private static void putIfNotBlank(Map<String,Object> target, String key, Object value){
        String text = str(value);
        if(StringUtils.isNotBlank(text)){
            target.put(key, text.trim());
        }
    }

    private static boolean isAllowedSizeCode(String sizeCode){
        if(StringUtils.isBlank(sizeCode)){
            return false;
        }
        String safe = sizeCode.trim().toUpperCase(Locale.ROOT);
        return "S".equals(safe) || "M".equals(safe) || "L".equals(safe) || "XL".equals(safe);
    }

    private static String firstNonBlank(String... values){
        if(values == null){
            return null;
        }
        for(String value : values){
            if(StringUtils.isNotBlank(value)){
                return value.trim();
            }
        }
        return null;
    }
    private void appendDeliveryRecordForShip(Long orderId, HttpServletRequest request, String remark) {
        Map<String, Object> order = queryOrderForComm(orderId);
        if(order == null) {
            return;
        }
        insertOrderMessage(
                order,
                uid(request),
                roleCode(request),
                sessionDisplayName(request, roleCode(request)),
                "DELIVERY",
                StringUtils.defaultIfBlank(remark, "设计师发货")
        );
    }

    private Map<String, Object> queryOrderForComm(Long orderId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,order_no,user_id,user_table,designer_id,designer_table,pay_status,order_status,designer_status,addtime " +
                        "from cosorder where id=? limit 1",
                orderId
        );
        if(rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    private boolean canAccessOrderComm(Map<String, Object> order, HttpServletRequest request) {
        if(order == null) {
            return false;
        }
        if(isDesigner(request)) {
            Long currentDesignerId = uid(request);
            Long orderDesignerId = parseLong(order.get("designer_id"));
            return orderDesignerId != null && currentDesignerId.equals(orderDesignerId);
        }
        if(isUser(request)) {
            Long currentUserId = uid(request);
            String currentTable = utable(request);
            Long orderUserId = parseLong(order.get("user_id"));
            String orderUserTable = str(order.get("user_table"));
            return orderUserId != null
                    && currentUserId.equals(orderUserId)
                    && StringUtils.equalsIgnoreCase(StringUtils.defaultString(currentTable).trim(), StringUtils.defaultString(orderUserTable).trim());
        }
        return false;
    }

    private void insertOrderMessage(Map<String, Object> order,
                                    Long senderId,
                                    String senderRole,
                                    String senderName,
                                    String messageType,
                                    String content) {
        if(order == null || StringUtils.isBlank(content)) {
            return;
        }

        jdbcTemplate.update(
                "insert into cos_order_message(" +
                        "id,addtime,order_id,order_no,user_id,user_table,designer_id,designer_table," +
                        "sender_role,sender_id,sender_name,message_type,content,deleted) " +
                        "values(?,now(),?,?,?,?,?,?,?,?,?,?,?,0)",
                nextMessageId(),
                parseLong(order.get("id")),
                str(order.get("order_no")),
                parseLong(order.get("user_id")),
                str(order.get("user_table")),
                parseLong(order.get("designer_id")),
                normalizeDesignerTable(str(order.get("designer_table"))),
                StringUtils.defaultString(senderRole),
                senderId,
                StringUtils.defaultIfBlank(senderName, "-"),
                normalizeMessageType(messageType),
                content
        );
    }

    private static String normalizeMessageType(String messageType) {
        String safe = StringUtils.defaultString(messageType).trim().toUpperCase(Locale.ROOT);
        if("DELIVERY".equals(safe)) {
            return "DELIVERY";
        }
        return "TEXT";
    }

    private static String normalizeDesignerTable(String rawTable) {
        String safe = StringUtils.defaultString(rawTable).trim();
        if(StringUtils.isBlank(safe)) {
            return "shejishi";
        }
        return safe.toLowerCase(Locale.ROOT);
    }

    private static boolean designerTableMatches(String left, String right) {
        String l = normalizeDesignerTable(left);
        String r = normalizeDesignerTable(right);
        if(StringUtils.equalsIgnoreCase(l, r)) {
            return true;
        }
        return "shejishi".equalsIgnoreCase(l) || "shejishi".equalsIgnoreCase(r);
    }

    private static String sessionDisplayName(HttpServletRequest request, String senderRole) {
        String username = str(request.getSession().getAttribute("username"));
        if(StringUtils.isNotBlank(username)) {
            return username;
        }
        Long userId = parseLong(request.getSession().getAttribute("userId"));
        if(userId == null) {
            return StringUtils.defaultString(senderRole);
        }
        if(CosRoleUtil.DESIGNER.equals(senderRole)) {
            return "设计师#" + userId;
        }
        if(CosRoleUtil.USER.equals(senderRole)) {
            return "用户#" + userId;
        }
        return senderRole + "#" + userId;
    }

    private static Long nextMessageId() {
        long now = System.currentTimeMillis() * 1000;
        int random = new Random().nextInt(1000);
        return now + random;
    }

    private static String buildDeliveryTitle(Map<String, Object> log) {
        String fromOrder = firstNonBlank(str(log.get("from_order_status")), str(log.get("fromOrderStatus")));
        String toOrder = firstNonBlank(str(log.get("to_order_status")), str(log.get("toOrderStatus")));
        if(StringUtils.isNotBlank(fromOrder) || StringUtils.isNotBlank(toOrder)) {
            return "状态变更：" + StringUtils.defaultIfBlank(fromOrder, "-") + " -> " + StringUtils.defaultIfBlank(toOrder, "-");
        }
        String fromPay = firstNonBlank(str(log.get("from_pay_status")), str(log.get("fromPayStatus")));
        String toPay = firstNonBlank(str(log.get("to_pay_status")), str(log.get("toPayStatus")));
        return "支付变更：" + StringUtils.defaultIfBlank(fromPay, "-") + " -> " + StringUtils.defaultIfBlank(toPay, "-");
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