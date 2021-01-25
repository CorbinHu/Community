package cn.corbinhu.community.utils;

/**
 * @author: CorbinHu
 * @date: 2021/1/19 18:25
 * @description:
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 帐号未激活状态
     */
    int NOT_ACTIVE_STATUS = 0;
    /**
     * 帐号激活状态
     */
    int ACTIVE_STATUS = 1;

    /**
     * 登录凭证有效状态
     */
    int TICKET_VALID_STATUS = 0;

    /**
     * 登录凭证无效状态
     */
    int TICKET_INVALID_STATUS = 1;

    /**
     * 普通用户
     */
    int PUBLIC_USER = 0;

    /**
     * 超级管理员
     */
    int ADMIN_USER = 1;

    /**
     * 版主
     */
    int SECTION_USER = 2;
    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     *  实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     *  实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     *  实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 系统消息ID
     */
    int SYSTEM_USER_ID = 1;
}
