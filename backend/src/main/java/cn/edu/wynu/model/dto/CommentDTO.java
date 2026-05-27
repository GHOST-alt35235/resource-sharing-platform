package cn.edu.wynu.model.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long resourceId;

    private Long parentId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
