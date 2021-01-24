package cn.corbinhu.community.Controller.interceptor;

import cn.corbinhu.community.entity.LoginTicket;
import cn.corbinhu.community.entity.User;
import cn.corbinhu.community.service.UserService;
import cn.corbinhu.community.utils.CommunityConstant;
import cn.corbinhu.community.utils.CookiesUtil;
import cn.corbinhu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author: CorbinHu
 * @date: 2021/1/20 12:48
 * @description: 判断是否登录，根据是否登录来显示某些组件
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor, CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    // 在Controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookies中获取登录凭证
        String ticket = CookiesUtil.getValue(request, "ticket");
        if (ticket != null) {
            // 检查凭证是否失效
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == TICKET_VALID_STATUS
                    && loginTicket.getExpired().after(new Date())){
                // 有效，根据凭证找用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户（考虑多线程问题，使用ThreadLocal进行线程隔离）
                hostHolder.setUser(user);
            }
        }
        return true;
    }
    // 在Controller之后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    // 在TemplateEngine之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
