package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DownloadRecord {
    private Long id;
    private Long resourceId;
    private Long userId;
    private String username;
    private Integer points;
    private Integer isFirstDownload;
    private LocalDateTime createTime;
}
