package cn.corbinhu.community.Controller;

import cn.corbinhu.community.Controller.annotation.LoginRequired;
import cn.corbinhu.community.entity.Comment;
import cn.corbinhu.community.service.CommentService;
import cn.corbinhu.community.service.DiscussPostService;
import cn.corbinhu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author: CorbinHu
 * @date: 2021/1/22 10:55
 * @description:
 */

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    public CommentService commentService;

    @Autowired
    public DiscussPostService discussPostService;

    @Autowired
    public HostHolder hostHolder;

    @LoginRequired
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        int id = hostHolder.getUser().getId();
        comment.setUserId(id);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;

    }
}
