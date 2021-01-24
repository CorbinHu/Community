package cn.corbinhu.community.mapper;

import cn.corbinhu.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author: CorbinHu
 * @date: 2021/1/19 21:11
 * @description:
 */

@Mapper
@Deprecated
public interface LoginTicketMapper {

    // 插入一条登录信息
    @Insert({"insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    // 根据ticket查询一条记录
    @Select({"select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    // 更新登录信息状态
    @Update({"update login_ticket set status=#{status} where ticket=#{ticket} "})
    int updateStatus(String ticket, int status);
}
