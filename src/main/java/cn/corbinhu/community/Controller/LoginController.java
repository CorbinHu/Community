package cn.corbinhu.community.Controller;

import cn.corbinhu.community.entity.User;
import cn.corbinhu.community.service.UserService;
import cn.corbinhu.community.utils.CommunityConstant;
import cn.corbinhu.community.utils.CommunityUtil;
import cn.corbinhu.community.utils.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: CorbinHu
 * @date: 2021/1/19 15:25
 * @description:
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "site/login";
    }

    // 注册
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.insertUser(user);
        if (map == null || map.isEmpty()) {
            String msg = "注册成功，我们已经向您的邮箱发送了一封激活帐号邮件，请尽快激活";
            model.addAttribute("msg", msg);
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";
        }
    }

    // 激活
    // domain/contextPath/activation/userid/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(@PathVariable int userId,
                             @PathVariable String code, Model model) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            String msg = "激活成功，您可以正常使用该帐号！";
            model.addAttribute("msg", msg);
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            String msg = "该帐号已激活，请不要重复激活，该操作无效！";
            model.addAttribute("msg", msg);
            model.addAttribute("target", "/login");
        } else {
            String msg = "您提供的激活码不对，请检查激活码！";
            model.addAttribute("msg", msg);
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入session
        //session.setAttribute("kaptcha", text);
        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("验证码获取错误！" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username, String password, boolean rememberMe, String code,
            /*HttpSession session,*/ HttpServletResponse response, Model model,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 验证验证码是否正确
        //String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "site/login";
        }

        //验证用户名，密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }

    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }
}
