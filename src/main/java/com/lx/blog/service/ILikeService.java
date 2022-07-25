package com.lx.blog.service;

import com.lx.blog.vo.LikeVo;

public interface ILikeService {

    public void clickLike(LikeVo likeVo);

    Long likeCount(LikeVo likeVo);

    Boolean likeStatus(LikeVo likeVo);

    Long getUserLikeCount(Integer userId);
}
