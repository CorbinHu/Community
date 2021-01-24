package cn.corbinhu.community.Controller;

import cn.corbinhu.community.Controller.annotation.LoginRequired;
import cn.corbinhu.community.entity.User;
import cn.corbinhu.community.service.FollowService;
import cn.corbinhu.community.service.LikeService;
import cn.corbinhu.community.service.UserService;
import cn.corbinhu.community.utils.CommunityConstant;
import cn.corbinhu.community.utils.CommunityUtil;
import cn.corbinhu.community.utils.HostHolder;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: CorbinHu
 * @date: 2021/1/20 17:43
 * @description:
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping(path = "/setting")
    public String getSettingPage() {
        return "site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("uploadMsg", "您还没选择对象");
            return "site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        // 验证文件格式
        List<String> imageType = Arrays.asList(".jpg", ".jpeg", ".png");
        if (StringUtils.isBlank(suffix) || !imageType.contains(suffix)) {
            model.addAttribute("uploadMsg", "文件格式不对");
            return "site/setting";
        }
        // 随机生成文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放位置
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！", e);
        }
        // 更新当前用户头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        int res = userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放图片路径
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (OutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("读取图像失败!" + e.getMessage());
            throw new RuntimeException("读取文件失败！服务器出现错误！", e);
        }

    }

    // 修改密码
    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(Model model, String oldPassword, String newPassword) {
        Map<String, Object> map = userService.updatePassword(oldPassword, newPassword);
        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);
        if (map != null && !map.isEmpty()) {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "site/setting";
        } else {
            return "redirect:/index";
        }
    }

    // 个人主页 -- 获得多少个赞
    @LoginRequired
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("没有该用户！");
        }
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("user", user);

        // 关注实体数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "site/profile";
    }

}
