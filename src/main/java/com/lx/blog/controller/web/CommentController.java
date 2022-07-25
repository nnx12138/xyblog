package com.lx.blog.controller.web;

import com.lx.blog.common.exception.BlogException;
import com.lx.blog.common.help.UserHelp;
import com.lx.blog.entity.CommentEntity;
import com.lx.blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Slf4j
@RequestMapping("/comment")
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserHelp userHelp;

    @Value("${blog.host}")
    private String host;

    @PostMapping("/add/{topicId}")
    public String add(@PathVariable Integer topicId, CommentEntity commentEntity) {

        if (userHelp.get() == null) {
            throw new BlogException("用户还没有登录");
        }

        commentEntity.setUid(userHelp.get().getUid());
        commentEntity.setStatu(1);
        commentEntity.setCreateTime(new Date());

        // 入库
        commentService.save(commentEntity);
        log.debug("评论保存成功:{}", commentEntity);
        log.debug("host:{}", host);

        return "redirect:"+host+"topic/detail/"+topicId;
    }
}
