package com.lx.blog.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CommentInfoVo {

    private Integer id;
    private Integer uid;
    private Integer entityType;
    private Integer entityId; // 帖子id/评论ID
    private Integer targetId;
    private String targetName;//增加一个回复人名字
    private String content;
    private Integer statu;
    private Date createTime;

}
