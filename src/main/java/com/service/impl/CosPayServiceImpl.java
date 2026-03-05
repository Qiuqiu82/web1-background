package com.service.impl;

import com.entity.dto.CosPayCreateReq;
import com.entity.dto.CosPayCreateResp;
import com.service.CosOrderFlowService;
import com.service.CosPayService;
import com.utils.CosRoleUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
 * @Description:
 * @Author: Qiuyan
 * @Date: 2026-03-04 14:20
 * @Version：1.0
 * ceshi
 **/

@Service
public class CosPayServiceImpl implements CosPayService {

    private final JdbcTemplate jdbcTemplate;
    private final CosOrderFlowService cosOrderFlowService;
    private static final DateTimeFormatter ID_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public CosPayServiceImpl(JdbcTemplate jdbcTemplate, CosOrderFlowService cosOrderFlowService) {
        this.jdbcTemplate = jdbcTemplate;
        this.cosOrderFlowService = cosOrderFlowService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CosPayCreateResp createPay(CosPayCreateReq req) {
        if (req.getOrderNo() == null || req.getUserId() == null || req.getUserTable() == null) {
            throw new RuntimeException("参数缺失");
        }
        String payChannel = (req.getPayChannel() == null || req.getPayChannel().trim().isEmpty()) ? "mock" : req.getPayChannel();

        List<Map<String, Object>> orderList = jdbcTemplate.queryForList(
                "select order_no,user_id,user_table,total_amount,pay_status from cosorder where order_no=? and user_id=? and user_table=? limit 1",
                req.getOrderNo(), req.getUserId(), req.getUserTable()
        );
        if (orderList.isEmpty()) throw new RuntimeException("订单不存在");
        Map<String, Object> order = orderList.get(0);
        String orderPayStatus = String.valueOf(order.get("pay_status"));
        if ("已支付".equals(orderPayStatus)) throw new RuntimeException("订单已支付，无需重复支付");

        List<Map<String, Object>> waitPayList = jdbcTemplate.queryForList(
                "select pay_order_no,pay_url,expire_time,pay_status from cospay_order " +
                        "where order_no=? and pay_status='待支付' and expire_time > now() order by id desc limit 1",
                req.getOrderNo()
        );
        if (!waitPayList.isEmpty()) {
            Map<String, Object> p = waitPayList.get(0);
            CosPayCreateResp resp = new CosPayCreateResp();
            resp.setPayOrderNo(String.valueOf(p.get("pay_order_no")));
            resp.setPayUrl(String.valueOf(p.get("pay_url")));
            resp.setPayStatus(String.valueOf(p.get("pay_status")));
            resp.setExpireTime(String.valueOf(p.get("expire_time")));
            return resp;
        }

        String payOrderNo = "PAY" + LocalDateTime.now().format(NO_FMT) + randomNum(6);
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(15);
        String payUrl = "/#/cospay/mock?payOrderNo=" + payOrderNo;

        Long id = nextId();
        BigDecimal amount = new BigDecimal(String.valueOf(order.get("total_amount")));

        jdbcTemplate.update(
                "insert into cospay_order(id,addtime,pay_order_no,order_no,user_id,user_table,amount,pay_channel,pay_status,pay_url,expire_time) " +
                        "values(?,now(),?,?,?,?,?,?, '待支付', ?, ?)",
                id, payOrderNo, req.getOrderNo(), req.getUserId(), req.getUserTable(), amount, payChannel, payUrl, Timestamp.valueOf(expireAt)
        );

        jdbcTemplate.update(
                "update cosorder set pay_order_no=?, pay_type=?, pay_status='未支付' where order_no=?",
                payOrderNo, payChannel, req.getOrderNo()
        );

        CosPayCreateResp resp = new CosPayCreateResp();
        resp.setPayOrderNo(payOrderNo);
        resp.setPayUrl(payUrl);
        resp.setPayStatus("待支付");
        resp.setExpireTime(expireAt.toString());
        return resp;
    }

    @Override
    public Map<String, Object> queryPayStatus(String orderNo, Long userId, String userTable) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select order_no,pay_status,order_status,pay_order_no,pay_time,total_amount from cosorder " +
                        "where order_no=? and user_id=? and user_table=? limit 1",
                orderNo, userId, userTable
        );
        if (list.isEmpty()) throw new RuntimeException("订单不存在");
        return list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mockPaySuccess(String payOrderNo) {
        List<Map<String, Object>> payList = jdbcTemplate.queryForList(
                "select pay_order_no,order_no,pay_status,pay_channel from cospay_order where pay_order_no=? limit 1",
                payOrderNo
        );
        if (payList.isEmpty()) throw new RuntimeException("支付单不存在");
        Map<String, Object> pay = payList.get(0);
        String payStatus = String.valueOf(pay.get("pay_status"));
        if ("已支付".equals(payStatus)) return;

        String channelTradeNo = "MOCK" + LocalDateTime.now().format(ID_FMT);

        int updated = jdbcTemplate.update(
                "update cospay_order set pay_status='已支付', pay_time=now(), channel_trade_no=? " +
                        "where pay_order_no=? and pay_status='待支付'",
                channelTradeNo, payOrderNo
        );
        if (updated == 0) return;

        String orderNo = String.valueOf(pay.get("order_no"));
        String flowErr = cosOrderFlowService.markPaySuccessByOrderNo(
                orderNo,
                payOrderNo,
                channelTradeNo,
                0L,
                CosRoleUtil.ADMIN,
                "模拟支付回调"
        );
        if (flowErr != null && !flowErr.trim().isEmpty()) {
            throw new RuntimeException(flowErr);
        }

        jdbcTemplate.update(
                "insert into cospay_notify_log(id,addtime,notify_id,pay_order_no,order_no,channel,verify_ok,payload,process_status) " +
                        "values(?,now(),?,?,?,?,1,?, '成功')",
                nextId(), UUID.randomUUID().toString(), payOrderNo, orderNo, String.valueOf(pay.get("pay_channel")),
                "{\"mock\":true,\"payOrderNo\":\"" + payOrderNo + "\"}"
        );
    }

    private static String randomNum(int len) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private static Long nextId() {
        String s = LocalDateTime.now().format(ID_FMT) + randomNum(2);
        return Long.parseLong(s);
    }
}
