package cn.corbinhu.community.mapper;

import cn.corbinhu.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: CorbinHu
 * @date: 2021/1/21 18:11
 * @description:
 */
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    // 查询某种类型帖子的数量
    int selectCommentCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
