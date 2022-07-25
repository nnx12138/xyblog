package com.lx.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.entity.CommentEntity;
import com.lx.blog.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author 2108
 * @email 2108@gmail.com
 * @date 2021-12-12 14:50:37
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {

    BlogPage<CommentVo> selectCommentListTid(@Param("page") BlogPage<CommentVo> page, @Param("tid") Integer id, @Param("type") Integer type);

    Long getCommentCountByTopicId(@Param("type") Integer type, @Param("id") Integer id);

    //后台
    List<CommentEntity> findBlogCommentList(Map map);

    int getTotalBlogComments(Map map);

    int deleteBatch(Integer[] ids);
}
