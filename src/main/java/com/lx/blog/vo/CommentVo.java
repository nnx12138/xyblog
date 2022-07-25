package com.lx.blog.vo;

import com.lx.blog.entity.UserEntity;
import lombok.Data;

import java.util.List;

@Data
//评论下的评论
public class CommentVo {

    private UserEntity user; // 评论的人

    private CommentInfoVo commentEntity; // 评论信息

    private List<CommentVo> replyList; // 评论的回复集合

    private Long likeCount; // 这个评论的点赞数量

    private Integer likeStatus = 0; // 当前登录用户对这个评论的点赞状态
}
