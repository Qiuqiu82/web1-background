package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.ShejishiEntity;
import com.entity.view.ShejishiView;
import com.entity.vo.ShejishiVO;
import com.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShejishiService extends IService<ShejishiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ShejishiVO> selectListVO(Wrapper<ShejishiEntity> wrapper);

    ShejishiVO selectVO(@Param("ew") Wrapper<ShejishiEntity> wrapper);

    List<ShejishiView> selectListView(Wrapper<ShejishiEntity> wrapper);

    ShejishiView selectView(@Param("ew") Wrapper<ShejishiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ShejishiEntity> wrapper);
}
