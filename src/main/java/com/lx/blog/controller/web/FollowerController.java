package com.lx.blog.controller.web;

import com.lx.blog.common.exception.BlogException;
import com.lx.blog.common.help.UserHelp;
import com.lx.blog.vo.FollowerVo;
import com.lx.blog.common.utils.R;
import com.lx.blog.service.IFollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/follower")
public class FollowerController {

    @Autowired
    private IFollowerService followerService;

    @Autowired
    private UserHelp userHelp;

    // 关注和取消关注
    @GetMapping("/click/{fid}")
    @ResponseBody
    public R click(@PathVariable Integer fid) {
        if (userHelp.get() == null) {
            throw new BlogException("请先登录");
        }
        // 关注或者取消关注
        followerService.click(fid, userHelp.get().getUid());
        return R.ok();
    }

    // 查询关注列表
    @GetMapping("/findFollowerListByUserId/{userId}")
    public String findFollowerListByUserId(@PathVariable Integer userId, ModelMap modelMap) {

        if (userId == null) {
            throw new BlogException("用户id不能为空");
        }

        // 1.根据用户id查询关注列表|
        List<FollowerVo> followerVoList = followerService.findFollowerListByUserId(userId, 1);

        modelMap.put("followerList", followerVoList);

        return "site/followee";
    }


    // 查询粉丝列表
    @GetMapping("/findFansListByUserId/{userId}")
    public String findFansListByUserId(@PathVariable Integer userId, ModelMap modelMap) {

        if (userId == null) {
            throw new BlogException("用户id不能为空");
        }

        // 1.根据用户id查询关注列表|
        List<FollowerVo> followerVoList = followerService.findFollowerListByUserId(userId, 2);

        modelMap.put("fansList", followerVoList);

        return "site/fans";
    }
}
