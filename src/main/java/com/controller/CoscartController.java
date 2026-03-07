package com.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CoscartEntity;
import com.entity.RemaicosfuEntity;
import com.service.CoscartService;
import com.service.RemaicosfuService;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

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
        Integer quantity = body.get("quantity")==null ? 1 : Integer.valueOf(body.get("quantity").toString());
        String specs = body.get("specs")==null ? "" : body.get("specs").toString();

        RemaicosfuEntity product = remaicosfuService.selectById(productId);
        if(product==null) return R.error("商品不存在");

        Long customDraftId = parseLong(body.get("customDraftId"));
        String customSummary = str(body.get("customSummary"));
        String customSnapshotJson = str(body.get("customSnapshotJson"));
        Integer customSnapshotVersion = parseInt(body.get("customSnapshotVersion"), 1);

        if(StringUtils.isBlank(customSummary)) {
            customSummary = specs;
        }

        CoscartEntity cart = new CoscartEntity();
        cart.setId(new Date().getTime() + (long)(Math.random()*1000));
        cart.setUserId(uid(request));
        cart.setUserTable(utable(request));
        cart.setProductId(productId);
        cart.setProductName(product.getFuzhuangmingcheng());
        cart.setProductCover(product.getHuawentuan()==null?"":product.getHuawentuan().split(",")[0]);
        cart.setSpecs(specs);
        cart.setQuantity(quantity);
        BigDecimal price = new BigDecimal(product.getFuzhuangjiage()==null?0:product.getFuzhuangjiage());
        cart.setPrice(price);
        cart.setAmount(price.multiply(new BigDecimal(quantity)));
        cart.setChecked(1);
        cart.setCustomDraftId(customDraftId);
        cart.setCustomSummary(customSummary);
        cart.setCustomSnapshotJson(customSnapshotJson);
        cart.setCustomSnapshotVersion(customSnapshotVersion);

        coscartService.insert(cart);
        return R.ok("加入购物车成功");
    }

    @RequestMapping("/update")
    public R update(@RequestBody CoscartEntity form, HttpServletRequest request){
        CoscartEntity db = coscartService.selectById(form.getId());
        if(db==null) return R.error("购物车记录不存在");
        if(!uid(request).equals(db.getUserId())) return R.error(403,"无权限");

        if(form.getQuantity()!=null && form.getQuantity()>0) db.setQuantity(form.getQuantity());
        if(form.getChecked()!=null) db.setChecked(form.getChecked());
        if(form.getSpecs()!=null) db.setSpecs(form.getSpecs());
        if(form.getCustomDraftId()!=null) db.setCustomDraftId(form.getCustomDraftId());
        if(form.getCustomSummary()!=null) db.setCustomSummary(form.getCustomSummary());
        if(form.getCustomSnapshotJson()!=null) db.setCustomSnapshotJson(form.getCustomSnapshotJson());
        if(form.getCustomSnapshotVersion()!=null) db.setCustomSnapshotVersion(form.getCustomSnapshotVersion());

        db.setAmount(db.getPrice().multiply(new BigDecimal(db.getQuantity())));
        coscartService.updateById(db);
        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        coscartService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    private static String str(Object value){
        return value == null ? null : String.valueOf(value);
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

    private static Integer parseInt(Object value, int defaultValue){
        if(value == null) return defaultValue;
        if(value instanceof Number) return ((Number)value).intValue();
        try {
            return Integer.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return defaultValue;
        }
    }
}