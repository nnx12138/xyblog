package com.lx.blog.controller.web;

import com.lx.blog.common.help.UserHelp;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.common.exception.BlogException;
import com.lx.blog.service.IFollowerService;
import com.lx.blog.service.ILikeService;
import com.lx.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private IFollowerService followerService;

    @Autowired
    private UserHelp userHelp;

    @GetMapping("/info/{userId}")
    public String info(@PathVariable Integer userId, ModelMap modelMap) {

        if (ObjectUtils.isEmpty(userId)) {
            throw new BlogException("用户Id不能为空");
        }

        // 根据用户Id查询用户的信息
        UserEntity userEntity = userService.getById(userId);
        userEntity.setPwd(null);

        // 查询用户的点赞数量
        Long userLikeCount = likeService.getUserLikeCount(userId);

        // 查询当前登录用户是否关注了该用户
        Boolean hasFollowed = false;
        if (userHelp.get() != null) {
            hasFollowed = followerService.findUserFollowerStatus(userHelp.get().getUid(), userId);
        }

        // 查询当前用户关注的数量
        Long followerCount = followerService.findUserFollowerCount(userId);
        // 查询当前用户粉丝的数量
        Long fansCount = followerService.findUserFansCount(userId);

        modelMap.put("user", userEntity);
        modelMap.put("userLikeCount", userLikeCount);
        modelMap.put("hasFollowed", hasFollowed);
        modelMap.put("followerCount", followerCount);
        modelMap.put("fansCount", fansCount);


        return "site/profile";
    }

    @RequestMapping ("/register")
    public String register(){
        return "site/register";
    }

    @RequestMapping ("/login")
    public String login(){
        return "site/login";
    }

    @RequestMapping ("/publish")
    public String publish(){
        return "site/discuss-publish";
    }
    @RequestMapping("/setting")
    public String setting(){
        return "site/setting";
    }
}
