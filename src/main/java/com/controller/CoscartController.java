package com.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CoscartEntity;
import com.entity.RemaicosfuEntity;
import com.service.CoscartService;
import com.service.RemaicosfuService;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
//cos服装定制用户购物车
@RestController
@RequestMapping("/coscart")
public class CoscartController {
    @Autowired private CoscartService coscartService;
    @Autowired private RemaicosfuService remaicosfuService;

    private Long uid(HttpServletRequest req){ return Long.valueOf(req.getSession().getAttribute("userId").toString()); }
    private String utable(HttpServletRequest req){ return req.getSession().getAttribute("tableName").toString(); }

    @RequestMapping("/list")
    public R list(HttpServletRequest request){
        List<CoscartEntity> list = coscartService.selectList(
                new EntityWrapper<CoscartEntity>()
                        .eq("user_id", uid(request))
                        .eq("user_table", utable(request))
                        .orderBy("id", false)
        );
        return R.ok().put("data", list);
    }

    @RequestMapping("/add")
    public R add(@RequestBody Map<String,Object> body, HttpServletRequest request){
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = body.get("quantity")==null?1:Integer.valueOf(body.get("quantity").toString());
        String specs = body.get("specs")==null?"":body.get("specs").toString();

        RemaicosfuEntity p = remaicosfuService.selectById(productId);
        if(p==null) return R.error("商品不存在");

        CoscartEntity e = new CoscartEntity();
        e.setId(new Date().getTime() + (long)(Math.random()*1000));
        e.setUserId(uid(request));
        e.setUserTable(utable(request));
        e.setProductId(productId);
        e.setProductName(p.getFuzhuangmingcheng());
        e.setProductCover(p.getHuawentuan()==null?"":p.getHuawentuan().split(",")[0]);
        e.setSpecs(specs);
        e.setQuantity(quantity);
        BigDecimal price = new BigDecimal(p.getFuzhuangjiage()==null?0:p.getFuzhuangjiage());
        e.setPrice(price);
        e.setAmount(price.multiply(new BigDecimal(quantity)));
        e.setChecked(1);
        coscartService.insert(e);
        return R.ok("加入购物车成功");
    }

    @RequestMapping("/update")
    public R update(@RequestBody CoscartEntity form, HttpServletRequest request){
        CoscartEntity db = coscartService.selectById(form.getId());
        if(db==null) return R.error("购物车记录不存在");
        if(!uid(request).equals(db.getUserId())) return R.error(403,"无权限");

        if(form.getQuantity()!=null && form.getQuantity()>0) db.setQuantity(form.getQuantity());
        if(form.getChecked()!=null) db.setChecked(form.getChecked());
        db.setAmount(db.getPrice().multiply(new BigDecimal(db.getQuantity())));
        coscartService.updateById(db);
        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        coscartService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
