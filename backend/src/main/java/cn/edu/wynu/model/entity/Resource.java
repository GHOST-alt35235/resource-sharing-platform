package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resource {
    private Long id;
    private String title;
    private String description;
    private Long fileId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Integer price;
    private Long uploaderId;
    private String uploaderName;
    private Integer status;
    private String rejectReason;
    private Integer downloadCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
