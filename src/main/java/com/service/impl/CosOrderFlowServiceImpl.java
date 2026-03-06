package com.service.impl;

import com.entity.CosorderEntity;
import com.service.CosOrderFlowService;
import com.utils.CosRoleUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service("cosOrderFlowService")
public class CosOrderFlowServiceImpl implements CosOrderFlowService {
    public static final String PAY_UNPAID = "未支付";
    public static final String PAY_PAID = "已支付";

    public static final String ORDER_PENDING_CONFIRM = "待确认";
    public static final String ORDER_PENDING_PRODUCE = "待生产";
    public static final String ORDER_PRODUCING = "生产中";
    public static final String ORDER_SHIPPED = "已发货";
    public static final String ORDER_FINISHED = "已完成";
    public static final String ORDER_CANCELED = "已取消";

    private final JdbcTemplate jdbcTemplate;

    public CosOrderFlowServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordOrderCreated(CosorderEntity order, Long operatorId, String operatorRole, String remark) {
        if (order == null || order.getId() == null) {
            return;
        }
        insertLog(
                order.getId(),
                order.getOrderNo(),
                null,
                order.getPayStatus(),
                null,
                order.getOrderStatus(),
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "用户下单")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String markPaySuccessByOrderNo(String orderNo,
                                          String payOrderNo,
                                          String channelTradeNo,
                                          Long operatorId,
                                          String operatorRole,
                                          String remark) {
        if (StringUtils.isBlank(orderNo)) {
            return "订单号不能为空";
        }

        Map<String, Object> current = queryOrderByNo(orderNo);
        if (current == null) {
            return "订单不存在";
        }

        String fromPay = str(current.get("pay_status"));
        String fromOrder = str(current.get("order_status"));

        if (ORDER_CANCELED.equals(fromOrder)) {
            return "已取消订单不能支付";
        }

        if (PAY_PAID.equals(fromPay)) {
            return null;
        }

        if (!PAY_UNPAID.equals(fromPay)) {
            return "订单支付状态非法";
        }

        int updated = jdbcTemplate.update(
                "update cosorder set pay_status=?, order_status=?, pay_time=now(), " +
                        "pay_order_no=ifnull(?, pay_order_no), pay_channel_trade_no=ifnull(?, pay_channel_trade_no) " +
                        "where order_no=? and pay_status=?",
                PAY_PAID,
                ORDER_PENDING_PRODUCE,
                payOrderNo,
                channelTradeNo,
                orderNo,
                PAY_UNPAID
        );

        if (updated == 0) {
            Map<String, Object> latest = queryOrderByNo(orderNo);
            if (latest != null && PAY_PAID.equals(str(latest.get("pay_status")))) {
                return null;
            }
            return "订单支付状态已变更，请刷新重试";
        }

        insertLog(
                longVal(current.get("id")),
                str(current.get("order_no")),
                fromPay,
                PAY_PAID,
                fromOrder,
                ORDER_PENDING_PRODUCE,
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "支付成功")
        );

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String adminTransition(Long orderId,
                                  String toOrderStatus,
                                  Long operatorId,
                                  String operatorRole,
                                  String remark) {
        if (orderId == null) {
            return "orderId不能为空";
        }
        if (StringUtils.isBlank(toOrderStatus)) {
            return "目标状态不能为空";
        }

        Map<String, Object> current = queryOrderById(orderId);
        if (current == null) {
            return "订单不存在";
        }

        String fromPay = str(current.get("pay_status"));
        String fromOrder = str(current.get("order_status"));

        if (!PAY_PAID.equals(fromPay)) {
            return "仅已支付订单可进行生产流转";
        }

        if (!allowAdminTransition(fromOrder, toOrderStatus)) {
            return "非法状态流转: " + StringUtils.defaultString(fromOrder) + " -> " + toOrderStatus;
        }

        int updated = jdbcTemplate.update(
                "update cosorder set order_status=? where id=? and pay_status=? and order_status=?",
                toOrderStatus,
                orderId,
                PAY_PAID,
                fromOrder
        );

        if (updated == 0) {
            return "订单状态已变更，请刷新重试";
        }

        insertLog(
                orderId,
                str(current.get("order_no")),
                fromPay,
                fromPay,
                fromOrder,
                toOrderStatus,
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "管理员状态流转")
        );

        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String designerStartProduction(Long orderId,
                                          Long designerId,
                                          String designerTable,
                                          Long operatorId,
                                          String operatorRole,
                                          String remark) {
        if (orderId == null) {
            return "orderId不能为空";
        }
        if (designerId == null || StringUtils.isBlank(designerTable)) {
            return "设计师信息缺失";
        }

        Map<String, Object> current = queryOrderById(orderId);
        if (current == null) {
            return "订单不存在";
        }

        String fromPay = str(current.get("pay_status"));
        String fromOrder = str(current.get("order_status"));
        Long currentDesignerId = longVal(current.get("designer_id"));
        String currentDesignerTable = str(current.get("designer_table"));

        if (!PAY_PAID.equals(fromPay)) {
            return "仅已支付订单可开始制作";
        }
        if (!ORDER_PENDING_PRODUCE.equals(fromOrder)) {
            return "仅待生产订单可开始制作";
        }
        if (currentDesignerId == null || !designerId.equals(currentDesignerId) || !StringUtils.equalsIgnoreCase(designerTable, currentDesignerTable)) {
            return "仅认领该订单的设计师可操作";
        }

        int updated = jdbcTemplate.update(
                "update cosorder set order_status=?, designer_status=? " +
                        "where id=? and pay_status=? and order_status=? and designer_id=? and designer_table=?",
                ORDER_PRODUCING,
                "制作中",
                orderId,
                PAY_PAID,
                ORDER_PENDING_PRODUCE,
                designerId,
                designerTable
        );

        if (updated == 0) {
            return "订单状态已变更，请刷新重试";
        }

        insertLog(
                orderId,
                str(current.get("order_no")),
                fromPay,
                fromPay,
                fromOrder,
                ORDER_PRODUCING,
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "设计师开始制作")
        );

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String designerShip(Long orderId,
                               Long designerId,
                               String designerTable,
                               Long operatorId,
                               String operatorRole,
                               String remark) {
        if (orderId == null) {
            return "orderId不能为空";
        }
        if (designerId == null || StringUtils.isBlank(designerTable)) {
            return "设计师信息缺失";
        }

        Map<String, Object> current = queryOrderById(orderId);
        if (current == null) {
            return "订单不存在";
        }

        String fromPay = str(current.get("pay_status"));
        String fromOrder = str(current.get("order_status"));
        Long currentDesignerId = longVal(current.get("designer_id"));
        String currentDesignerTable = str(current.get("designer_table"));

        if (!PAY_PAID.equals(fromPay)) {
            return "仅已支付订单可发货";
        }
        if (!ORDER_PRODUCING.equals(fromOrder)) {
            return "仅生产中订单可发货";
        }
        if (currentDesignerId == null || !designerId.equals(currentDesignerId) || !StringUtils.equalsIgnoreCase(designerTable, currentDesignerTable)) {
            return "仅认领该订单的设计师可操作";
        }

        int updated = jdbcTemplate.update(
                "update cosorder set order_status=?, designer_status=? " +
                        "where id=? and pay_status=? and order_status=? and designer_id=? and designer_table=?",
                ORDER_SHIPPED,
                "已发货",
                orderId,
                PAY_PAID,
                ORDER_PRODUCING,
                designerId,
                designerTable
        );

        if (updated == 0) {
            return "订单状态已变更，请刷新重试";
        }

        insertLog(
                orderId,
                str(current.get("order_no")),
                fromPay,
                fromPay,
                fromOrder,
                ORDER_SHIPPED,
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "设计师发货")
        );

        return null;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String userCancel(Long orderId,
                             Long userId,
                             String userTable,
                             Long operatorId,
                             String operatorRole,
                             String remark) {
        if (orderId == null) {
            return "orderId不能为空";
        }
        if (userId == null || StringUtils.isBlank(userTable)) {
            return "用户信息缺失";
        }

        Map<String, Object> current = queryOrderByOwner(orderId, userId, userTable);
        if (current == null) {
            return "订单不存在或无权限";
        }

        String fromPay = str(current.get("pay_status"));
        String fromOrder = str(current.get("order_status"));

        if (!PAY_UNPAID.equals(fromPay) || !ORDER_PENDING_CONFIRM.equals(fromOrder)) {
            return "仅待确认且未支付订单可取消";
        }

        int updated = jdbcTemplate.update(
                "update cosorder set order_status=? where id=? and user_id=? and user_table=? and pay_status=? and order_status=?",
                ORDER_CANCELED,
                orderId,
                userId,
                userTable,
                PAY_UNPAID,
                ORDER_PENDING_CONFIRM
        );

        if (updated == 0) {
            return "订单状态已变更，请刷新重试";
        }

        insertLog(
                orderId,
                str(current.get("order_no")),
                fromPay,
                fromPay,
                fromOrder,
                ORDER_CANCELED,
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "用户取消订单")
        );

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String userConfirmReceipt(Long orderId,
                                     Long userId,
                                     String userTable,
                                     Long operatorId,
                                     String operatorRole,
                                     String remark) {
        if (orderId == null) {
            return "orderId不能为空";
        }
        if (userId == null || StringUtils.isBlank(userTable)) {
            return "用户信息缺失";
        }

        Map<String, Object> current = queryOrderByOwner(orderId, userId, userTable);
        if (current == null) {
            return "订单不存在或无权限";
        }

        String fromPay = str(current.get("pay_status"));
        String fromOrder = str(current.get("order_status"));

        if (!PAY_PAID.equals(fromPay)) {
            return "仅已支付订单可确认收货";
        }
        if (!ORDER_SHIPPED.equals(fromOrder)) {
            return "仅已发货订单可确认收货";
        }

        int updated = jdbcTemplate.update(
                "update cosorder set order_status=?, designer_status=? " +
                        "where id=? and user_id=? and user_table=? and pay_status=? and order_status=?",
                ORDER_FINISHED,
                "已完成",
                orderId,
                userId,
                userTable,
                PAY_PAID,
                ORDER_SHIPPED
        );

        if (updated == 0) {
            return "订单状态已变更，请刷新重试";
        }

        insertLog(
                orderId,
                str(current.get("order_no")),
                fromPay,
                fromPay,
                fromOrder,
                ORDER_FINISHED,
                operatorId,
                operatorRole,
                StringUtils.defaultIfBlank(remark, "用户确认收货")
        );

        return null;
    }
    @Override
    public List<Map<String, Object>> listStatusLogs(Long orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        return jdbcTemplate.queryForList(
                "select * from cosorder_status_log where order_id=? order by id asc",
                orderId
        );
    }

    @Override
    public Map<String, Object> queryOrderById(Long orderId) {
        if (orderId == null) {
            return null;
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder where id=? limit 1",
                orderId
        );
        return list.isEmpty() ? null : list.get(0);
    }

    private Map<String, Object> queryOrderByNo(String orderNo) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder where order_no=? limit 1",
                orderNo
        );
        return list.isEmpty() ? null : list.get(0);
    }

    private Map<String, Object> queryOrderByOwner(Long orderId, Long userId, String userTable) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder where id=? and user_id=? and user_table=? limit 1",
                orderId,
                userId,
                userTable
        );
        return list.isEmpty() ? null : list.get(0);
    }

    private boolean allowAdminTransition(String fromOrderStatus, String toOrderStatus) {
        return (ORDER_PENDING_PRODUCE.equals(fromOrderStatus) && ORDER_PRODUCING.equals(toOrderStatus))
                || (ORDER_PRODUCING.equals(fromOrderStatus) && ORDER_SHIPPED.equals(toOrderStatus))
                || (ORDER_SHIPPED.equals(fromOrderStatus) && ORDER_FINISHED.equals(toOrderStatus));
    }

    private void insertLog(Long orderId,
                           String orderNo,
                           String fromPayStatus,
                           String toPayStatus,
                           String fromOrderStatus,
                           String toOrderStatus,
                           Long operatorId,
                           String operatorRole,
                           String remark) {
        if (orderId == null) {
            return;
        }

        jdbcTemplate.update(
                "insert into cosorder_status_log(" +
                        "id,addtime,order_id,order_no,from_pay_status,to_pay_status,from_order_status,to_order_status," +
                        "operator_id,operator_role,remark) values(?,now(),?,?,?,?,?,?,?,?,?)",
                nextId(),
                orderId,
                orderNo,
                StringUtils.trimToNull(fromPayStatus),
                StringUtils.trimToNull(toPayStatus),
                StringUtils.trimToNull(fromOrderStatus),
                StringUtils.trimToNull(toOrderStatus),
                operatorId,
                CosRoleUtil.normalize(operatorRole, null),
                StringUtils.defaultString(remark)
        );
    }

    private static Long nextId() {
        long now = System.currentTimeMillis() * 1000;
        int random = ThreadLocalRandom.current().nextInt(1000);
        return now + random;
    }

    private static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Long longVal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return null;
        }
    }
}
