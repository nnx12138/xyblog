package com.lx.blog.controller.web;

import com.lx.blog.common.exception.BlogException;
import com.lx.blog.common.help.UserHelp;
import com.lx.blog.entity.InvitationEntity;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.vo.UserToken;
import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.common.utils.R;
import com.lx.blog.service.CommentService;
import com.lx.blog.service.ILikeService;
import com.lx.blog.service.InvitationService;
import com.lx.blog.service.UserService;
import com.lx.blog.vo.CommentVo;
import com.lx.blog.vo.LikeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Controller
@RequestMapping("/topic")
//帖子操作
public class TopicController {

    @Autowired
    private UserHelp userHelp;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ILikeService likeService;

    @PostMapping("/publish")
    @ResponseBody
    public R publish(@RequestBody InvitationEntity invitationEntity) {
        UserToken userToken = userHelp.get();
        if (userToken == null) {
            throw new BlogException("用户没有登录");
        }
        invitationEntity.setStatu(1);
        invitationEntity.setCreateTime(new Date());
        invitationEntity.setUid(userToken.getUid());

        // 把帖子内容保存到数据库中
        invitationService.save(invitationEntity);

        return R.ok();
    }


    /**
     * 1、帖子信息
     * 2、帖子的相关数据(点赞，收藏)
     * 3、帖子评论
     * 4、帖子作者
     *
     * @param id 帖子ID
     * @return
     */
    @GetMapping("/detail/{id}")
    public String detail(BlogPage<CommentVo> page, @PathVariable Integer id, ModelMap modelMap) {

        // 1.帖子信息
        InvitationEntity topic = invitationService.getById(id);

        // 2.查询帖子作者
        UserEntity user = userService.getById(topic.getUid());

        // 3.帖子的数据
        page.setSize(5);

        // 4.帖子的评论(评论信息，评论信息作者，评论的回复，分页)
        page = commentService.getCommentListByTid(page, id); //

        // 5.查询帖子点赞数量
        LikeVo likeVo = new LikeVo();
        likeVo.setEntityType(1);
        likeVo.setEntityId(id);
        if (userHelp.get() != null) {
            likeVo.setUserId(userHelp.get().getUid());
        }
        Long likeCount = likeService.likeCount(likeVo);

        // 6.查询当前登录用户对该帖子的点赞状态
        Boolean likeStatus = likeService.likeStatus(likeVo);


        page.setPath("/topic/detail/" + id);
        // 5、把数据放到ModelMap中
        modelMap.addAttribute("topic", topic);
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("page", page);
        modelMap.addAttribute("likeCount", likeCount);
        modelMap.addAttribute("likeStatus", likeStatus ? 1 : 0);

        log.debug("topic:{}", topic);
        log.debug("user:{}", user);
        log.debug("comment:{}", page.getRecords());

        return "site/discuss-detail";
    }


}
