package cn.corbinhu.community.service;

import cn.corbinhu.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author: CorbinHu
 * @date: 2021/1/23 15:05
 * @description:
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //给帖子点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        //String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        //if (member) {
        //    redisTemplate.opsForSet().remove(entityLikeKey, userId);
        //} else {
        //    redisTemplate.opsForSet().add(entityLikeKey, userId);
        //}
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean member = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();
                if (member) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    //查询某个实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某个实体点赞的状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }

}
