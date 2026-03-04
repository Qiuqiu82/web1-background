package com.entity.dto;

/**
 * @Description:
 * @Author: Qiuyan
 * @Date: 2026-03-04 14:18
 * @Version： 1.0
 **/

public class CosPayCreateReq {
    private String orderNo;
    private Long userId;
    private String userTable;
    private String payChannel; // mock/wechat/alipay

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserTable() { return userTable; }
    public void setUserTable(String userTable) { this.userTable = userTable; }

    public String getPayChannel() { return payChannel; }
    public void setPayChannel(String payChannel) { this.payChannel = payChannel; }
}