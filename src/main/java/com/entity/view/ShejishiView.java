package com.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.entity.ShejishiEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

@TableName("shejishi")
public class ShejishiView extends ShejishiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public ShejishiView() {
    }

    public ShejishiView(ShejishiEntity shejishiEntity) {
        try {
            BeanUtils.copyProperties(this, shejishiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
