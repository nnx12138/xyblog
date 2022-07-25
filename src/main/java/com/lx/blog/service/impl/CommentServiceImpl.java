package com.lx.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.common.help.UserHelp;
import com.lx.blog.common.status.BlogStatus;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.dao.CommentDao;
import com.lx.blog.entity.CommentEntity;
import com.lx.blog.service.CommentService;
import com.lx.blog.service.ILikeService;
import com.lx.blog.vo.CommentVo;
import com.lx.blog.vo.LikeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    @Autowired
    private ILikeService likeService;

    @Autowired
    private UserHelp userHelp;
    @Autowired
    private CommentDao commentDao;

    @Override
    public BlogPage<CommentVo> getCommentListByTid(BlogPage<CommentVo> page, Integer id) {
        // 1.查询一级评论
        page = baseMapper.selectCommentListTid(page, id, BlogStatus.TOPIC_COMMENT.getCode());
        BlogPage newReplyPage = new BlogPage();

        LikeVo likeVo = new LikeVo();
        likeVo.setEntityType(2);
        if (userHelp.get() != null) {
            likeVo.setUserId(userHelp.get().getUid());
        }

        // 2.查询当前评论的回复
        List<CommentVo> records = page.getRecords();
        for (CommentVo comment : records) {

            likeVo.setEntityId(comment.getCommentEntity().getId());

            // 查询一级评论的点赞信息
            findCommentLikeInfo(comment, likeVo);

            // 查询某一个评论的所有的回复
            Integer cid = comment.getCommentEntity().getId(); // 查询二级评论，根据id去查
            Page<CommentVo> replyPage = baseMapper.selectCommentListTid(newReplyPage, cid, BlogStatus.TOPIC_REPLY.getCode());

            List<CommentVo> records1 = replyPage.getRecords();

            // 查询你二级评论的点赞数量和点赞状态
            for (CommentVo commentVo : records1) {
                likeVo.setEntityId(commentVo.getCommentEntity().getId());
                findCommentLikeInfo(commentVo, likeVo);
            }

            comment.setReplyList(records1);
        }
        return page;
    }

    @Override
    public Long getCommentCountByTopicId(Integer type,Integer id) {
        return baseMapper.getCommentCountByTopicId(type,id);
    }

    public void findCommentLikeInfo(CommentVo comment, LikeVo likeVo) {
        // 3、查询评论的点赞数量和点赞状态
        comment.setLikeCount(likeService.likeCount(likeVo));
        if (likeVo.getUserId() != null) {
            comment.setLikeStatus(likeService.likeStatus(likeVo) ? 1 : 0);
        }
    }


    @Override
    public PageResult getCommentsPage(PageQueryUtil pageUtil) {
        List<CommentEntity> comments = commentDao.findBlogCommentList(pageUtil);
        System.out.println(comments);
        int total = commentDao.getTotalBlogComments(pageUtil);
        PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return commentDao.deleteBatch(ids) > 0;
    }
}