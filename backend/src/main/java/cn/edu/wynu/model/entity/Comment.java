package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Comment {
    private Long id;
    private Long resourceId;
    private Long userId;
    private String username;
    private String nickname;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Comment> replies;
}
