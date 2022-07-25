package com.lx.blog.vo;

import lombok.Data;

@Data
public class LikeVo {

    private Integer entityType;

    private Integer entityId;

    private Integer entityUserId;

    private Integer userId;
}
