package com.lx.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.entity.CommentEntity;
import com.lx.blog.vo.CommentVo;


public interface CommentService extends IService<CommentEntity> {
    BlogPage<CommentVo> getCommentListByTid(BlogPage<CommentVo> page, Integer id);

    Long getCommentCountByTopicId(Integer type, Integer id);

    /**
     * 后台管理系统中评论分页功能
     *
     * @param pageUtil
     * @return
     */
    PageResult getCommentsPage(PageQueryUtil pageUtil);

    Boolean deleteBatch(Integer[] ids);
}

