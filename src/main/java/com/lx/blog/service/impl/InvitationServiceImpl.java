package com.lx.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.common.status.BlogStatus;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.dao.InvitationDao;
import com.lx.blog.entity.InvitationDataEntity;
import com.lx.blog.entity.InvitationEntity;
import com.lx.blog.service.CommentService;
import com.lx.blog.service.ILikeService;
import com.lx.blog.service.InvitationService;
import com.lx.blog.vo.IndexDataVo;
import com.lx.blog.vo.LikeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("invitationService")
public class InvitationServiceImpl extends ServiceImpl<InvitationDao, InvitationEntity> implements InvitationService {

    @Autowired
    private ILikeService likeService;

    @Autowired
    private CommentService commentService;
    //后台
    @Autowired
    private InvitationDao invitationDao;

    @Override
    public Page<IndexDataVo> indexPage(BlogPage blogPage) {

        // 1.查询数据(作者信息，帖子的信息(不包含评论))
        Page<IndexDataVo> page = baseMapper.selectIndexData(blogPage);

        // 2.查询帖子的点赞数量和评论数量
        for (IndexDataVo indexDataVo : page.getRecords()) {

            LikeVo likeVo = new LikeVo();
            likeVo.setEntityType(1);
            likeVo.setEntityId(indexDataVo.getInvitationEntity().getId());

            if (indexDataVo.getInvitationDataEntity() == null) {
                indexDataVo.setInvitationDataEntity(new InvitationDataEntity());
            }
            // 查询帖子的点赞数量
            Long likeCount = likeService.likeCount(likeVo);
            indexDataVo.getInvitationDataEntity().setLikes(likeCount);

            // 评论的数量
            Long commentCount = commentService.getCommentCountByTopicId(BlogStatus.TOPIC_COMMENT.getCode(), indexDataVo.getInvitationEntity().getId());
            indexDataVo.getInvitationDataEntity().setComments(commentCount);
        }
        return page;
    }

    @Override
    public PageResult getBlogsPage(PageQueryUtil pageUtil) {
        List<InvitationEntity> blogList = invitationDao.findBlogList(pageUtil);
        int total = invitationDao.getTotalBlogs(pageUtil);
        PageResult pageResult = new PageResult(blogList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<InvitationEntity> getAllCategories() {
        return invitationDao.findBlogList(null);
    }
    @Override
    public InvitationEntity getBlogById(Long blogId) {
        return baseMapper.selectById(blogId);

    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        return invitationDao.deleteBatch(ids);
    }
}