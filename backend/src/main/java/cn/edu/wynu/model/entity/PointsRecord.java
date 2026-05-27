package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private String username;
    private Integer points;
    private Integer type;
    private String description;
    private Long relatedId;
    private LocalDateTime createTime;
}
