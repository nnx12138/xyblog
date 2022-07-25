package com.lx.blog.controller.web;

import com.lx.blog.common.exception.BlogException;
import com.lx.blog.common.help.UserHelp;
import com.lx.blog.common.utils.R;
import com.lx.blog.service.ILikeService;
import com.lx.blog.vo.LikeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private ILikeService likeService;

    @Autowired
    private UserHelp userHelp;

    @PostMapping("/clickLike")
    public R clickLike(@RequestBody LikeVo likeVo) {

        if (userHelp.get() == null) {
            throw new BlogException("请先登录");
        }

        likeVo.setUserId(userHelp.get().getUid());

        // 1.给实体点赞或者取消点赞
        likeService.clickLike(likeVo);

        // 2.查询实体点赞的数量
        Long likeCount = likeService.likeCount(likeVo);

        // 3.查询实体点赞的状态
        Boolean likeStatus = likeService.likeStatus(likeVo);

        return R.ok().put("likeStatus", likeStatus ? 1 : 0).put("likeCount", likeCount);
    }
}
