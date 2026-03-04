package com.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CoscartEntity;
import com.entity.CosorderEntity;
import com.service.CoscartService;
import com.service.CosorderService;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/cosorder")
public class CosorderController {
    @Autowired private CosorderService cosorderService;
    @Autowired private CoscartService coscartService;

    private Long uid(HttpServletRequest req){ return Long.valueOf(req.getSession().getAttribute("userId").toString()); }
    private String utable(HttpServletRequest req){ return req.getSession().getAttribute("tableName").toString(); }

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
        coscartService.deleteBatchIds(cartIds);

        return R.ok().put("data", o);
    }

    @PostMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, @RequestBody(required = false) Map<String,Object> body, HttpServletRequest request){
        CosorderEntity o = cosorderService.selectById(id);
        if(o==null) return R.error("订单不存在");
        if(!uid(request).equals(o.getUserId())) return R.error(403,"无权限");

        o.setPayStatus("已支付");
        o.setOrderStatus("待生产");
        if(body!=null && body.get("payType")!=null) o.setPayType(body.get("payType").toString());
        cosorderService.updateById(o);
        return R.ok("支付成功(模拟)");
    }
}