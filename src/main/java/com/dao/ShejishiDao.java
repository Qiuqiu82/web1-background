package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.ShejishiEntity;
import com.entity.view.ShejishiView;
import com.entity.vo.ShejishiVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShejishiDao extends BaseMapper<ShejishiEntity> {

    List<ShejishiVO> selectListVO(@Param("ew") Wrapper<ShejishiEntity> wrapper);

    ShejishiVO selectVO(@Param("ew") Wrapper<ShejishiEntity> wrapper);

    List<ShejishiView> selectListView(@Param("ew") Wrapper<ShejishiEntity> wrapper);

    List<ShejishiView> selectListView(Pagination page, @Param("ew") Wrapper<ShejishiEntity> wrapper);

    ShejishiView selectView(@Param("ew") Wrapper<ShejishiEntity> wrapper);
}
