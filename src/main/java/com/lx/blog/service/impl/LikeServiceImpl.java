package com.lx.blog.service.impl;

import com.lx.blog.common.constants.LikeConstatns;
import com.lx.blog.service.ILikeService;
import com.lx.blog.vo.LikeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * 点赞的业务使用redis实现
 * like:1:帖子id -->[101,102,103]
 * like:2:评论id--->[110,22,33,44,44]
 */
@Service
public class LikeServiceImpl implements ILikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞/取消点赞
    @Override
    public void clickLike(LikeVo likeVo) {
        // 这个地方同时需要更新两个key的值，所以需要考虑到原子性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String key = String.format(LikeConstatns.LIKE_TOPIC_KEY, likeVo.getEntityType(), likeVo.getEntityId());
                String userLikeKey = String.format(LikeConstatns.USER_LIKE_KEY, likeVo.getEntityUserId());

                // 1.先判断这个用户是否对这个实体点赞了
                Boolean member = redisTemplate.opsForSet().isMember(key, likeVo.getUserId());

                // 1.开启事务,查询相关的操作一定要在开启事务之前完成
                operations.multi();
                if (member) {
                    // 已经点赞了，应该取消点赞
                    Long remove = redisTemplate.opsForSet().remove(key, likeVo.getUserId());
                    redisTemplate.opsForValue().decrement(userLikeKey); // 字符串
                } else {
                    // 给主体点赞
                    Long add = redisTemplate.opsForSet().add(key, likeVo.getUserId());
                    // 主体的点赞数量+1
                    redisTemplate.opsForValue().increment(userLikeKey);
                }

                // 2.事务提交
                operations.exec();

                return null;
            }
        });


    }

    @Override
    public Boolean likeStatus(LikeVo likeVo) {
        String key = String.format(LikeConstatns.LIKE_TOPIC_KEY, likeVo.getEntityType(), likeVo.getEntityId());
        return redisTemplate.opsForSet().isMember(key, likeVo.getUserId());
    }

    @Override
    public Long getUserLikeCount(Integer userId) {
        String userLikeKey = String.format(LikeConstatns.USER_LIKE_KEY, userId);
        Object o = redisTemplate.opsForValue().get(userLikeKey);
        if (o == null) {
            return 0L;
        }
        return Long.parseLong(o.toString());
    }

    @Override
    public Long likeCount(LikeVo likeVo) {
        String key = String.format(LikeConstatns.LIKE_TOPIC_KEY, likeVo.getEntityType(), likeVo.getEntityId());
        return redisTemplate.opsForSet().size(key);
    }
}
