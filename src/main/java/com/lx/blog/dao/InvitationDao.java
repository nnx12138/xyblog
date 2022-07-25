package com.lx.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.entity.InvitationEntity;
import com.lx.blog.vo.IndexDataVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 
 * @author Java2108
 * @email java2108@qf.com
 * @date 2021-12-09 15:47:47
 */
@Mapper
public interface InvitationDao extends BaseMapper<InvitationEntity> {
    Page<IndexDataVo> selectIndexData(Page<IndexDataVo> page);

    List<InvitationEntity> findBlogList(PageQueryUtil pageUtil);

    int getTotalBlogs(PageQueryUtil pageUtil);

    boolean deleteBatch(Integer[] ids);
}
