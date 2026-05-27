package cn.edu.wynu.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UploadFile {
    private Long id;
    private String fileName;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Long uploaderId;
    private String uploaderNickname;
    private Integer status;
    private LocalDateTime createTime;
}
