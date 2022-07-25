package com.lx.blog.vo;

import com.lx.blog.entity.InvitationDataEntity;
import com.lx.blog.entity.InvitationEntity;
import com.lx.blog.entity.UserEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class IndexDataVo implements Serializable {

    private UserEntity userEntity; // 作者

    private InvitationEntity invitationEntity; // 帖子

    private InvitationDataEntity invitationDataEntity;// 帖子数据
}
