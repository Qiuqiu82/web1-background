package com.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.CosorderDao;
import com.entity.CosorderEntity;
import com.service.CosorderService;
import org.springframework.stereotype.Service;

@Service("cosorderService")
public class CosorderServiceImpl extends ServiceImpl<CosorderDao, CosorderEntity> implements CosorderService {}
