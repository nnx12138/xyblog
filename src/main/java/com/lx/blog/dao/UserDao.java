package com.lx.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author Java2108
 * @email java2108@qf.com
 * @date 2021-12-09 15:47:47
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
    Integer updateUserStatus(@Param("status") Integer status, @Param("uid") String userId, @Param("code") String code);
//后台管理
    List<UserEntity> findCategoryList(PageQueryUtil pageUtil);
    int getTotalCategories(PageQueryUtil pageUtil);


    int deleteBatch(Integer[] ids);
}
