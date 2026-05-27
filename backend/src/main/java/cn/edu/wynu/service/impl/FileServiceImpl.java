package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.FileMapper;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.entity.UploadFile;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Override
    public UploadFile uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            Path filePath = Paths.get(uploadPath, fileName);
            Files.write(filePath, file.getBytes());

            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileName(fileName);
            uploadFile.setOriginalName(originalFilename);
            uploadFile.setFilePath(filePath.toString());
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType(fileExtension);
            uploadFile.setUploaderId(userId);
            uploadFile.setUploaderNickname(user.getNickname());
            uploadFile.setStatus(1);

            fileMapper.insert(uploadFile);
            return uploadFile;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public UploadFile getFileById(Long id) {
        return fileMapper.findById(id);
    }

    @Override
    public List<UploadFile> getMyFiles() {
        Long userId = StpUtil.getLoginIdAsLong();
        return fileMapper.findByUploaderId(userId);
    }

    @Override
    public boolean deleteFile(Long id) {
        UploadFile file = fileMapper.findById(id);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        if (!file.getUploaderId().equals(userId)) {
            throw new RuntimeException("无权删除此文件");
        }

        try {
            Path filePath = Paths.get(file.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
        }

        return fileMapper.delete(id) > 0;
    }
}
