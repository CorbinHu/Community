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
     * 默认有效时长
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 有效时长
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
