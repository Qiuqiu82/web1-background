package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@TableName("shejishi")
public class ShejishiEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public ShejishiEntity() {
    }

    public ShejishiEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @TableId
    private Long id;

    private String shejishizhanghao;

    private String mima;

    private String shejishixingming;

    private String touxiang;

    private String lianxifangshi;

    private String zhuanchang;

    private String jianjie;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShejishizhanghao() {
        return shejishizhanghao;
    }

    public void setShejishizhanghao(String shejishizhanghao) {
        this.shejishizhanghao = shejishizhanghao;
    }

    public String getMima() {
        return mima;
    }

    public void setMima(String mima) {
        this.mima = mima;
    }

    public String getShejishixingming() {
        return shejishixingming;
    }

    public void setShejishixingming(String shejishixingming) {
        this.shejishixingming = shejishixingming;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public String getLianxifangshi() {
        return lianxifangshi;
    }

    public void setLianxifangshi(String lianxifangshi) {
        this.lianxifangshi = lianxifangshi;
    }

    public String getZhuanchang() {
        return zhuanchang;
    }

    public void setZhuanchang(String zhuanchang) {
        this.zhuanchang = zhuanchang;
    }

    public String getJianjie() {
        return jianjie;
    }

    public void setJianjie(String jianjie) {
        this.jianjie = jianjie;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}
