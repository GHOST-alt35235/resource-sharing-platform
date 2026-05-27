package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatar;
    private String introduction;
    private Integer points;
    private Integer status;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
