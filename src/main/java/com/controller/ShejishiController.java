package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.ShejishiEntity;
import com.service.ShejishiService;
import com.service.TokenService;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/shejishi")
public class ShejishiController {
    @Autowired
    private ShejishiService shejishiService;

    @Autowired
    private TokenService tokenService;

    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        ShejishiEntity user = shejishiService.selectOne(new EntityWrapper<ShejishiEntity>().eq("shejishizhanghao", username));
        if (user == null || !user.getMima().equals(password)) {
            return R.error("\u8d26\u53f7\u6216\u5bc6\u7801\u4e0d\u6b63\u786e");
        }
        String token = tokenService.generateToken(user.getId(), username, "shejishi", "DESIGNER");
        return R.ok().put("token", token);
    }

    @IgnoreAuth
    @PostMapping("/register")
    public R register(@RequestBody ShejishiEntity shejishi) {
        ShejishiEntity exists = shejishiService.selectOne(
                new EntityWrapper<ShejishiEntity>().eq("shejishizhanghao", shejishi.getShejishizhanghao())
        );
        if (exists != null) {
            return R.error("\u6ce8\u518c\u8bbe\u8ba1\u5e08\u5df2\u5b58\u5728");
        }
        if (shejishi.getId() == null) {
            shejishi.setId(new Date().getTime());
        }
        shejishiService.insert(shejishi);
        return R.ok();
    }

    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute("userId");
        ShejishiEntity user = shejishiService.selectById(id);
        return R.ok().put("data", user);
    }

    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        ShejishiEntity user = shejishiService.selectOne(new EntityWrapper<ShejishiEntity>().eq("shejishizhanghao", username));
        if (user == null) {
            return R.error("\u8d26\u53f7\u4e0d\u5b58\u5728");
        }
        user.setMima("123456");
        shejishiService.updateById(user);
        return R.ok("\u5bc6\u7801\u5df2\u91cd\u7f6e\u4e3a\uff1a123456");
    }

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ShejishiEntity shejishi, HttpServletRequest request) {
        EntityWrapper<ShejishiEntity> ew = new EntityWrapper<ShejishiEntity>();
        PageUtils page = shejishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shejishi), params), params));
        return R.ok().put("data", page);
    }

    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ShejishiEntity shejishi, HttpServletRequest request) {
        EntityWrapper<ShejishiEntity> ew = new EntityWrapper<ShejishiEntity>();
        PageUtils page = shejishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shejishi), params), params));
        return R.ok().put("data", page);
    }

    @RequestMapping("/query")
    public R query(ShejishiEntity shejishi) {
        Wrapper<ShejishiEntity> wrapper = new EntityWrapper<ShejishiEntity>();
        wrapper.allEq(MPUtil.allEQMapPre(shejishi, "shejishi"));
        return R.ok().put("data", shejishiService.selectView(wrapper));
    }
}