package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.RemaicosfuEntity;
import com.entity.view.RemaicosfuView;
import com.entity.vo.RemaicosfuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 热卖西服
 * 
 * @author 
 * @email 
 * @date 2023-03-07 22:24:10
 */
public interface RemaicosfuDao extends BaseMapper<RemaicosfuEntity> {
	
	List<RemaicosfuVO> selectListVO(@Param("ew") Wrapper<RemaicosfuEntity> wrapper);
	
	RemaicosfuVO selectVO(@Param("ew") Wrapper<RemaicosfuEntity> wrapper);
	
	List<RemaicosfuView> selectListView(@Param("ew") Wrapper<RemaicosfuEntity> wrapper);

	List<RemaicosfuView> selectListView(Pagination page,@Param("ew") Wrapper<RemaicosfuEntity> wrapper);
	
	RemaicosfuView selectView(@Param("ew") Wrapper<RemaicosfuEntity> wrapper);
	

}
