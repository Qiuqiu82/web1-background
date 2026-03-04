package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.RemaicosfuEntity;
import com.entity.view.RemaicosfuView;
import com.entity.vo.RemaicosfuVO;
import com.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 热卖西服
 *
 * @author 
 * @email 
 * @date 2023-03-07 22:24:10
 */
public interface RemaicosfuService extends IService<RemaicosfuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<RemaicosfuVO> selectListVO(Wrapper<RemaicosfuEntity> wrapper);
   	
   	RemaicosfuVO selectVO(@Param("ew") Wrapper<RemaicosfuEntity> wrapper);
   	
   	List<RemaicosfuView> selectListView(Wrapper<RemaicosfuEntity> wrapper);
   	
   	RemaicosfuView selectView(@Param("ew") Wrapper<RemaicosfuEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<RemaicosfuEntity> wrapper);
   	

}

