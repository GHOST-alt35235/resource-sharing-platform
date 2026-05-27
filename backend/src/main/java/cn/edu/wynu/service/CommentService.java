package cn.edu.wynu.service;

import cn.edu.wynu.model.entity.Comment;
import cn.edu.wynu.model.dto.CommentDTO;
import java.util.List;

public interface CommentService {
    Comment getCommentById(Long id);
    List<Comment> getCommentsByResourceId(Long resourceId);
    List<Comment> getReplies(Long parentId);
    Comment createComment(CommentDTO commentDTO);
    boolean likeComment(Long commentId);
    boolean unlikeComment(Long commentId);
    boolean deleteComment(Long commentId);
}
