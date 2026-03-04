package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.RemaicosfuDao;
import com.entity.RemaicosfuEntity;
import com.entity.view.RemaicosfuView;
import com.entity.vo.RemaicosfuVO;
import com.service.RemaicosfuService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("remaicosfuService")
public class RemaicosfuServiceImpl extends ServiceImpl<RemaicosfuDao, RemaicosfuEntity> implements RemaicosfuService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<RemaicosfuEntity> page = this.selectPage(
                new Query<RemaicosfuEntity>(params).getPage(),
                new EntityWrapper<RemaicosfuEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<RemaicosfuEntity> wrapper) {
		  Page<RemaicosfuView> page =new Query<RemaicosfuView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<RemaicosfuVO> selectListVO(Wrapper<RemaicosfuEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public RemaicosfuVO selectVO(Wrapper<RemaicosfuEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<RemaicosfuView> selectListView(Wrapper<RemaicosfuEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public RemaicosfuView selectView(Wrapper<RemaicosfuEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
