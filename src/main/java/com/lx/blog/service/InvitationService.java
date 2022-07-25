package com.lx.blog.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.common.dto.BlogPage;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.entity.InvitationEntity;
import com.lx.blog.vo.IndexDataVo;

import java.util.List;

/**
 * 
 *
 * @author Java2108
 * @email java2108@qf.com
 * @date 2021-12-09 15:47:47
 */
public interface InvitationService extends IService<InvitationEntity> {
    Page<IndexDataVo> indexPage(BlogPage<IndexDataVo> page);
//后台,首页分页信息展示
    PageResult getBlogsPage(PageQueryUtil pageUtil);
//获取总条数
    List<InvitationEntity> getAllCategories();

    /**
     * 根据id获取详情
     *
     * @param blogId
     * @return
     */
    InvitationEntity getBlogById(Long blogId);

    boolean deleteBatch(Integer[] ids);
}

