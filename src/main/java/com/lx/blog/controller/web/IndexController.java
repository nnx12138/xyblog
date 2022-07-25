package com.lx.blog.controller.web;

import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.service.ILikeService;
import com.lx.blog.service.InvitationService;
import com.lx.blog.vo.IndexDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private ILikeService likeService;

    /**
     * 1、帖子信息
     * 2、作者信息
     * 3、帖子数据
     *
     * @return
     */
    @GetMapping("/")
    public String index(BlogPage<IndexDataVo> blogPage, ModelMap modelMap) {
        blogPage.setSize(5);
        // 1.查询帖子数据
        blogPage = (BlogPage) invitationService.indexPage(blogPage);
        blogPage.setPath("/");
        modelMap.put("page", blogPage);
        return "index";
    }
}
