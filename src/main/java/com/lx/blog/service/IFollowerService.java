package com.lx.blog.service;

import com.lx.blog.vo.FollowerVo;

import java.util.List;

public interface IFollowerService {
    void click(Integer fid, Integer uid);

    Boolean findUserFollowerStatus(Integer userId, Integer fid);

    Long findUserFollowerCount(Integer userId);

    Long findUserFansCount(Integer userId);

    List<FollowerVo> findFollowerListByUserId(Integer userId, Integer type);
}
