package cn.corbinhu.community.Controller;

import cn.corbinhu.community.entity.User;
import cn.corbinhu.community.service.UserService;
import cn.corbinhu.community.utils.CommunityConstant;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入session
        session.setAttribute("kaptcha", text);
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
                        HttpSession session, HttpServletResponse response, Model model){

        // 验证验证码是否正确
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg", "验证码不正确!");
            return "site/login";
        }

        //验证用户名，密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }

    }
}
