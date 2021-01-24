package cn.corbinhu.community.Controller;

import cn.corbinhu.community.Controller.annotation.LoginRequired;
import cn.corbinhu.community.entity.Comment;
import cn.corbinhu.community.entity.DiscussPost;
import cn.corbinhu.community.entity.Page;
import cn.corbinhu.community.entity.User;
import cn.corbinhu.community.service.CommentService;
import cn.corbinhu.community.service.DiscussPostService;
import cn.corbinhu.community.service.LikeService;
import cn.corbinhu.community.service.UserService;
import cn.corbinhu.community.utils.CommunityConstant;
import cn.corbinhu.community.utils.CommunityUtil;
import cn.corbinhu.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author: CorbinHu
 * @date: 2021/1/21 14:33
 * @description:
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录，请登录");
        }
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.getJSONString(1, "标题和内容都不能为空");
        }
        //其他为默认值
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        int res = discussPostService.addDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0, "发贴成功");
    }

    @GetMapping("/detail/{discussPastId}")
    public String findDiscussPostById(@PathVariable("discussPastId") int discussPostId, Model model, Page page) {
        User hostUser = hostHolder.getUser();
        // 帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        // 帖子作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);
        // 点赞的数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        int likeStatus = hostUser == null ? 0 : likeService.findEntityLikeStatus(hostUser.getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);
        // 评论分页信息 Page对象会自动封装到Model中
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());
        // 获取回帖列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        // 回帖VO列表 --- 用于前端展示的数据
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 回帖VO
                Map<String, Object> commentVo = new HashMap<>();
                // 回帖
                commentVo.put("comment", comment);
                // 作者信息
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 点赞的数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                likeStatus = hostUser == null ? 0 : likeService.findEntityLikeStatus(hostUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
                // 评论回复列表，根据评论的id拿到回复列表，不分页
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        // 回复作者信息
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标 -- 如果为0 则为对评论的评论，不为0，则为对评论的回复，需要得到回复的目标user
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        // 点赞的数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        likeStatus = hostUser == null ? 0 : likeService.findEntityLikeStatus(hostUser.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);
                // 回复帖子的数量
                int replyCount = commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "site/discuss-detail";

    }
}
