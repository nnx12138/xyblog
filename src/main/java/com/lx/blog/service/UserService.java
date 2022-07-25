package com.lx.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.vo.UserToken;

import java.util.Map;

/**
 * 
 *
 * @author Java2108
 * @email java2108@qf.com
 * @date 2021-12-09 15:47:47
 */
public interface UserService extends IService<UserEntity> {

    void register(UserEntity userEntity);

    void activateUser(String code);

    UserToken login(String username, String pwd);

    void logout(String token);

    UserEntity findUserById(Integer uid);


    UserEntity adminlogin(String username, String pwd);

    /**
     * 查询用户的分页数据
     *
     * @param pageUtil
     * @return
     */
    PageResult getBlogCategoryPage(PageQueryUtil pageUtil);

    Boolean updateCategory(Integer categoryId, String categoryName, String categoryIcon);

    Boolean deleteBatch(Integer[] ids);
}

