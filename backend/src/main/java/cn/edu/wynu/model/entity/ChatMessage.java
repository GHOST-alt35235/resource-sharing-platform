package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String senderNickname;
    private Long receiverId;
    private String receiverNickname;
    private String content;
    private Long fileId;
    private Integer type;
    private Integer status;
    private LocalDateTime createTime;
}
