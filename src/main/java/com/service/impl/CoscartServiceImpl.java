package com.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.CoscartDao;
import com.entity.CoscartEntity;
import com.service.CoscartService;
import org.springframework.stereotype.Service;

@Service("coscartService")
public class CoscartServiceImpl extends ServiceImpl<CoscartDao, CoscartEntity> implements CoscartService {}
