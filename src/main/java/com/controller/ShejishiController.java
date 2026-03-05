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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
        ShejishiEntity u = shejishiService.selectOne(new EntityWrapper<ShejishiEntity>().eq("shejishizhanghao", username));
        if (u == null || !u.getMima().equals(password)) {
            return R.error("账号或密码不正确");
        }
        String token = tokenService.generateToken(u.getId(), username, "shejishi", "DESIGNER");
        return R.ok().put("token", token);
    }

    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute("userId");
        ShejishiEntity u = shejishiService.selectById(id);
        return R.ok().put("data", u);
    }

    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        ShejishiEntity u = shejishiService.selectOne(new EntityWrapper<ShejishiEntity>().eq("shejishizhanghao", username));
        if (u == null) {
            return R.error("账号不存在");
        }
        u.setMima("123456");
        shejishiService.updateById(u);
        return R.ok("密码已重置为：123456");
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
