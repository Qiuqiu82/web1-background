package com.entity.dto;

/**
 * @Description:
 * @Author: Qiuyan
 * @Date: 2026-03-04 14:19
 * @Version： 1.0
 **/

public class CosPayCreateResp {
    private String payOrderNo;
    private String payUrl;
    private String payStatus;
    private String expireTime;

    public String getPayOrderNo() { return payOrderNo; }
    public void setPayOrderNo(String payOrderNo) { this.payOrderNo = payOrderNo; }

    public String getPayUrl() { return payUrl; }
    public void setPayUrl(String payUrl) { this.payUrl = payUrl; }

    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }

    public String getExpireTime() { return expireTime; }
    public void setExpireTime(String expireTime) { this.expireTime = expireTime; }
}
