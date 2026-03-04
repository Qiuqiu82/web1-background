package com.service;

import com.entity.dto.CosPayCreateReq;
import com.entity.dto.CosPayCreateResp;

import java.util.Map;

/**
 * @Description:
 * @Author: Qiuyan
 * @Date: 2026-03-04 14:19
 * @Version： 1.0
 **/

public interface CosPayService {
    CosPayCreateResp createPay(CosPayCreateReq req);
    Map<String, Object> queryPayStatus(String orderNo, Long userId, String userTable);
    void mockPaySuccess(String payOrderNo);
}
