package cn.edu.wynu.service;

import cn.edu.wynu.model.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {
    UploadFile uploadFile(MultipartFile file);
    UploadFile getFileById(Long id);
    List<UploadFile> getMyFiles();
    boolean deleteFile(Long id);
}
