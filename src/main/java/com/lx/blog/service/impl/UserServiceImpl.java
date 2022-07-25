package com.lx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.common.constants.UserConstants;
import com.lx.blog.common.exception.BlogException;
import com.lx.blog.common.exception.EmailException;
import com.lx.blog.common.executor.ExecutorUtils;
import com.lx.blog.common.status.BlogStatus;
import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.common.utils.EmailUtils;
import com.lx.blog.common.utils.PasswordUtils;
import com.lx.blog.common.utils.UUIDUtils;
import com.lx.blog.dao.UserDao;
import com.lx.blog.dto.Email;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.service.UserService;
import com.lx.blog.vo.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired // key/value
    private RedisTemplate redisTemplate;


    @Override
    public void register(UserEntity userEntity) {

        // 1.判断邮箱是否被注册
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", userEntity.getEmail());
        UserEntity dbUserEntity = baseMapper.selectOne(queryWrapper);
        if (dbUserEntity != null) {
            // 抛出一个邮箱异常
            throw new EmailException("该邮箱以被注册");
        }

        // 设置账号的状态未激活
        userEntity.setStatu(BlogStatus.USER_UN_ACTIVATE.getCode());
        userEntity.setActivateCode(UUIDUtils.getUUid()); // 每个人的激活码都不一样
        userEntity.setCreateTime(new Date()); // 用户注册时间
        userEntity.setPwd(PasswordUtils.encode(userEntity.getPwd())); // 密码进行加密

        // 注册操作
        baseMapper.insert(userEntity); // 主键回填，MP已经实现主键回填


        // 把用户的激活码和用户名id绑定  blog:user:activate:00001-->10
        stringRedisTemplate.opsForValue().set(String.format(UserConstants.ACTIVATEKEY, userEntity.getActivateCode()), userEntity.getId().toString(), 1, TimeUnit.DAYS);


        // 把激活的连接发送到用户的邮箱
        Email email = new Email();
        email.setTitle("这是Blog网站的用户激活邮件");
        email.setContent("<a href ='http://127.0.0.1:8001/user/activateUser/" + userEntity.getActivateCode() + "'>点击</a>这里激活");
        email.setToUser(userEntity.getEmail());

        log.debug("{}", email);
        // 使用异步的方式发送邮件
        ExecutorUtils.getExecutor().submit(() -> {
            emailUtils.sendEmail(email);
        });
    }

    @Override
    public void activateUser(String code) {

        // 1.从redis中根据用户激活码查询用户的ID
        String userId = stringRedisTemplate.opsForValue().get(String.format(UserConstants.ACTIVATEKEY, code));

        if (ObjectUtils.isEmpty(userId)) {
            throw new BlogException("激活码有误");
        }

        // 2.激活账号
        Integer integer = baseMapper.updateUserStatus(BlogStatus.USER_ACTIVATE.getCode(), userId, code);
        if (integer <= 0) {
            log.error("账号激活失败,code:{},uid:{}", code, userId.toString());
            throw new BlogException("账号激活失败");
        }

        // 3.删除redis中的激活码
        stringRedisTemplate.delete(String.format(UserConstants.ACTIVATEKEY, code));
    }

    @Override
    public UserToken login(String username, String pwd) {

        // 1.先查询用户的用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        UserEntity userEntity = baseMapper.selectOne(queryWrapper);

        if (userEntity == null) {
            throw new BlogException("用户名不存在");
        }

        // 2.密码的比对
        if (!PasswordUtils.checkpw(pwd, userEntity.getPwd())) {
            throw new BlogException("用户名获密码错误");
        }

        // 3.查询用户的状态是否是已激活
        if (BlogStatus.USER_ACTIVATE.getCode() != userEntity.getStatu()) {
            throw new BlogException("账户的状态有误");
        }

        // 查询这个账号(UID)是否在其他设备是登录，直接redis

        // 4.认证成功了,生成一个登陆用户的凭证(Token)
        UserToken userToken = new UserToken();
        userToken.setCreatTime(new Date());
        userToken.setToken(UUIDUtils.getUUid()); // UUID来做令牌
        userToken.setUid(userEntity.getId());
        userToken.setUname(userEntity.getUsername());
        userToken.setHeaderUrl(userEntity.getHeadUrl());

        long currentTime = System.currentTimeMillis();// 获取当前登录的时间,long类型，是时间使用毫秒表示的
        long userDefaultTimeOut = UserConstants.USER_DEFAULE_TIMEOUT * 1000; // 用户默认的超时时间，乘以1000是转成毫秒
        long userTtl = currentTime + userDefaultTimeOut; // 用户超时时间，long类型
        Date ttl = new Date(userTtl); // 把long类型时间转成Date类型
        userToken.setTtl(ttl);

        // 5.在服务端保存凭证--》Redis
        String key = String.format(UserConstants.USER_LOGIN_TOKEN, userToken.getToken());
        redisTemplate.opsForValue().set(key, userToken, userDefaultTimeOut, TimeUnit.MILLISECONDS); // rediskey的超时时间是10天

        return userToken;
    }

    @Override
    public void logout(String uuid) {
        String key = String.format(UserConstants.USER_LOGIN_TOKEN, uuid);
        redisTemplate.delete(key);

    }

    // 这个还是有问题的？
    // 缓存穿透:缓存中没有，数据库也没有，比如-1。使用默认值解决
    // 缓存击穿：缓存中没有，数据库有，比如同时请求同一个数据，使用锁解决。
    @Override
    public UserEntity findUserById(Integer uid) {
        // 1.先查询缓存
        String key = String.format(UserConstants.USER_INFO_KEY, uid);
        UserEntity userEntity = (UserEntity) redisTemplate.opsForValue().get(key);
        if (userEntity == null) {

            // 1、获取一个对象
            Lock lock = new ReentrantLock();

            // 2、加锁
//            lock.lock(); // 如果拿不到锁就会一直阻塞
            try {
//                boolean b = lock.tryLock(3, TimeUnit.SECONDS);
                boolean b = lock.tryLock(); // 拿不到锁返回false
                if (b) {
                    // 3、处理义务(可能会出现一行)
                    userEntity = getById(uid); // 查询数据库
                    if (userEntity == null) {
                        // 这里是解决缓存穿透
                        redisTemplate.opsForValue().set(key, null, 1, TimeUnit.DAYS);
                    } else {
                        redisTemplate.opsForValue().set(key, userEntity, 1, TimeUnit.DAYS);
                    }
                } else {
                    Thread.sleep(500);
                    return findUserById(uid); // 自旋
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
        return userEntity;
    }


    //管理员模块
    @Override
    public UserEntity adminlogin(String username, String pwd) {
        // 1.先查询用户的用户是否存在
        UserEntity userEntity = baseMapper.selectById(1);
        if (userEntity == null) {
            throw new BlogException("用户名不存在");
        }
        // 2.密码的比对
//        if ( userEntity.getPwd()!=pwd) {
//            throw new BlogException("用户名获密码错误");
//        }

        // 3.查询用户的状态是否是已激活
        if (2 != userEntity.getStatu()) {
            throw new BlogException("账户的状态有误");
        }
        return userEntity;
    }

    //后台用户管理
    @Override
    public PageResult getBlogCategoryPage(PageQueryUtil pageUtil) {
        List<UserEntity> categoryList = userDao.findCategoryList(pageUtil);
        int total = userDao.getTotalCategories(pageUtil);
        PageResult pageResult = new PageResult(categoryList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public Boolean updateCategory(Integer categoryId, String categoryName, String categoryIcon) {
        UserEntity userEntity = userDao.selectById(categoryId);
        if (userEntity != null) {
            userEntity.setHeadUrl(categoryIcon);
            userEntity.setUsername(categoryName);
            //修改用户实体
            int i = userDao.updateById(userEntity);
            return i > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return userDao.deleteBatch(ids) > 0;
    }
}