package cn.corbinhu.community.Controller;

import cn.corbinhu.community.Controller.annotation.LoginRequired;
import cn.corbinhu.community.entity.Comment;
import cn.corbinhu.community.entity.DiscussPost;
import cn.corbinhu.community.entity.Event;
import cn.corbinhu.community.event.EventProducer;
import cn.corbinhu.community.service.CommentService;
import cn.corbinhu.community.service.DiscussPostService;
import cn.corbinhu.community.utils.CommunityConstant;
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
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        int id = hostHolder.getUser().getId();
        comment.setUserId(id);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(id)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            //System.out.println(comment.getTargetId());
            //System.out.println(target.getUserId());
            //if (comment.getTargetId() != 0 && comment.getTargetId() != target.getUserId()) {
            //    event.setEntityUserId(comment.getTargetId());
            //    eventProducer.fireEvent(event);
            //}
            event.setEntityUserId(target.getUserId());

        }
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;

    }
}
