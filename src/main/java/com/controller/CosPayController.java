package com.controller;

import com.entity.dto.CosPayCreateReq;
import com.entity.dto.CosPayCreateResp;
import com.service.CosPayService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description:
 * @Author: Qiuyan
 * @Date: 2026-03-04 14:21
 * @Version： 1.0
 **/


@RestController
@RequestMapping("/cospay")
public class CosPayController {

    private final CosPayService cosPayService;

    public CosPayController(CosPayService cosPayService) {
        this.cosPayService = cosPayService;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody CosPayCreateReq req) {
        CosPayCreateResp resp = cosPayService.createPay(req);
        return ok(resp);
    }

    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam String orderNo,
                                      @RequestParam Long userId,
                                      @RequestParam String userTable) {
        return ok(cosPayService.queryPayStatus(orderNo, userId, userTable));
    }

    @PostMapping("/mock/success")
    public Map<String, Object> mockSuccess(@RequestParam String payOrderNo) {
        cosPayService.mockPaySuccess(payOrderNo);
        return ok("支付成功");
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("msg", "ok");
        res.put("data", data);
        return res;
    }
}
