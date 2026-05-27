package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.Comment;
import cn.edu.wynu.model.dto.CommentDTO;
import cn.edu.wynu.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public AjaxResult getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return AjaxResult.success(comment);
    }

    @GetMapping("/resource/{resourceId}")
    public AjaxResult getCommentsByResourceId(@PathVariable Long resourceId) {
        List<Comment> comments = commentService.getCommentsByResourceId(resourceId);
        return AjaxResult.success(comments);
    }

    @GetMapping("/replies/{parentId}")
    public AjaxResult getReplies(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getReplies(parentId);
        return AjaxResult.success(replies);
    }

    @PostMapping
    public AjaxResult createComment(@Valid @RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.createComment(commentDTO);
        return AjaxResult.success("评论成功", comment);
    }

    @PostMapping("/like/{id}")
    public AjaxResult likeComment(@PathVariable Long id) {
        boolean result = commentService.likeComment(id);
        return result ? AjaxResult.success("点赞成功") : AjaxResult.error("已点赞");
    }

    @DeleteMapping("/like/{id}")
    public AjaxResult unlikeComment(@PathVariable Long id) {
        boolean result = commentService.unlikeComment(id);
        return result ? AjaxResult.success("取消点赞成功") : AjaxResult.error("未点赞");
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteComment(@PathVariable Long id) {
        boolean result = commentService.deleteComment(id);
        return result ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}
