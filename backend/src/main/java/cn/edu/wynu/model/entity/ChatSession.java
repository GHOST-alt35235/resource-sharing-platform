package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSession {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private String user1Nickname;
    private String user2Nickname;
    private String lastMessage;
    private LocalDateTime lastTime;
    private LocalDateTime createTime;
}
