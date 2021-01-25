package cn.corbinhu.community.service;

import cn.corbinhu.community.entity.Comment;
import cn.corbinhu.community.mapper.CommentMapper;
import cn.corbinhu.community.utils.CommunityConstant;
import cn.corbinhu.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author: CorbinHu
 * @date: 2021/1/21 18:28
 * @description:
 */
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 查询某个类型的帖子
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public int findCommentCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentCountByEntity(entityType, entityId);
    }

    /**
     * 添加评论（回帖）
     *
     * @param comment
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 转义标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子的回帖数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCommentCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updatePostCommentCount(comment.getEntityId(), count);
        }
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
