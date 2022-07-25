package com.lx.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.dao.InvitationDataDao;
import com.lx.blog.entity.InvitationDataEntity;
import com.lx.blog.service.InvitationDataService;
import org.springframework.stereotype.Service;

@Service("invitationDataService")
public class InvitationDataServiceImpl extends ServiceImpl<InvitationDataDao, InvitationDataEntity> implements InvitationDataService {
}