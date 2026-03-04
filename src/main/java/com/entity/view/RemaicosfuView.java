package com.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.entity.RemaicosfuEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 热卖西服
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2023-03-07 22:24:10
 */
@TableName("remaicosfu")
public class RemaicosfuView extends RemaicosfuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public RemaicosfuView(){
	}
 
 	public RemaicosfuView(RemaicosfuEntity remaicosfuEntity){
 	try {
			BeanUtils.copyProperties(this, remaicosfuEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}
