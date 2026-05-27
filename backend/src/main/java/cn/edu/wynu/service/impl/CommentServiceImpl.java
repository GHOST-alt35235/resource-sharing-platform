package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.CommentMapper;
import cn.edu.wynu.mapper.CommentLikeMapper;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.entity.Comment;
import cn.edu.wynu.model.entity.CommentLike;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.model.dto.CommentDTO;
import cn.edu.wynu.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.findById(id);
    }

    @Override
    public List<Comment> getCommentsByResourceId(Long resourceId) {
        List<Comment> rootComments = commentMapper.findRootComments(resourceId);
        for (Comment comment : rootComments) {
            List<Comment> replies = commentMapper.findByParentId(comment.getId());
            comment.setReplies(replies);
        }
        return rootComments;
    }

    @Override
    public List<Comment> getReplies(Long parentId) {
        return commentMapper.findByParentId(parentId);
    }

    @Override
    @Transactional
    public Comment createComment(CommentDTO commentDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);

        Comment comment = new Comment();
        comment.setResourceId(commentDTO.getResourceId());
        comment.setUserId(userId);
        comment.setUsername(user.getUsername());
        comment.setNickname(user.getNickname());
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setStatus(1);

        commentMapper.insert(comment);
        return comment;
    }

    @Override
    @Transactional
    public boolean likeComment(Long commentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        CommentLike exist = commentLikeMapper.findByCommentIdAndUserId(commentId, userId);
        if (exist != null) {
            return false;
        }
        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        commentLikeMapper.insert(like);
        commentMapper.incrementLikeCount(commentId);
        return true;
    }

    @Override
    @Transactional
    public boolean unlikeComment(Long commentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        CommentLike exist = commentLikeMapper.findByCommentIdAndUserId(commentId, userId);
        if (exist == null) {
            return false;
        }
        commentLikeMapper.delete(commentId, userId);
        commentMapper.decrementLikeCount(commentId);
        return true;
    }

    @Override
    public boolean deleteComment(Long commentId) {
        Comment comment = commentMapper.findById(commentId);
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);

        if (comment.getUserId().equals(userId) || "admin".equals(user.getRole())) {
            return commentMapper.delete(commentId) > 0;
        }
        return false;
    }
}
