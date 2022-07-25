package com.lx.blog.service.impl;

import com.lx.blog.common.constants.FollowerConstants;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.service.IFollowerService;
import com.lx.blog.service.UserService;
import com.lx.blog.vo.FollowerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowerServiceImpl implements IFollowerService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * @param fid 被关注的用户ID
     * @param uid 关注的用户ID
     */
    @Override
    public void click(Integer fid, Integer uid) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                // 1.准备key
                String followerKey = String.format(FollowerConstants.FOLLOWER_KEY, uid);
                String fansKey = String.format(FollowerConstants.FANS_KEY, fid);

                // 2.先查询uid是否关注fid
                Double time = redisTemplate.opsForZSet().score(followerKey, fid);

                operations.multi();

                if (time == null) {
                    // 没有关注,使用zset的目的是为了记录关注的时间，可以按照时间排序
                    redisTemplate.opsForZSet().add(followerKey, fid, System.currentTimeMillis());
                    redisTemplate.opsForZSet().add(fansKey, uid, System.currentTimeMillis());
                } else {
                    // 已经关注了,取消关注
                    redisTemplate.opsForZSet().remove(followerKey, fid);
                    redisTemplate.opsForZSet().remove(fansKey, uid);
                }

                operations.exec();
                return null;
            }
        });
    }

    @Override
    public Boolean findUserFollowerStatus(Integer userId, Integer fid) {
        // 1.准备key
        String followerKey = String.format(FollowerConstants.FOLLOWER_KEY, userId);

        // 2.先查询uid是否关注fid
        Double time = redisTemplate.opsForZSet().score(followerKey, fid);

        return time != null ? true : false;
    }

    @Override
    public Long findUserFollowerCount(Integer userId) {
        String followerKey = String.format(FollowerConstants.FOLLOWER_KEY, userId);
        return redisTemplate.opsForZSet().size(followerKey);
    }

    @Override
    public Long findUserFansCount(Integer userId) {
        String fansKey = String.format(FollowerConstants.FANS_KEY, userId);
        return redisTemplate.opsForZSet().size(fansKey);
    }

    @Override
    public List<FollowerVo> findFollowerListByUserId(Integer userId, Integer type) {

        List<FollowerVo> list = new ArrayList<>();

        // 1.查询userId关注的用户id集合(时间)
        String key = null;
        if (type == 1) {
            key = String.format(FollowerConstants.FOLLOWER_KEY, userId);
        } else if (type == 2) {
            key = String.format(FollowerConstants.FANS_KEY, userId);
        }

        Set set = redisTemplate.opsForZSet().range(key, 0, -1);
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            // 关注的用户ID
            Integer uid = (Integer) iterator.next();
            // 关注的时间
            Double time = redisTemplate.opsForZSet().score(key, uid);

            // 查询用户的信息
            UserEntity userEntity = userService.findUserById(uid);

            // 创建一个关注的VO
            FollowerVo vo = new FollowerVo();
            vo.setUserId(uid);
            vo.setCreateTime(new Date(time.longValue()));
            vo.setUsername(userEntity.getUsername());
            vo.setHeadUrl(userEntity.getHeadUrl());

            list.add(vo);
        }
        return list;
    }
}
