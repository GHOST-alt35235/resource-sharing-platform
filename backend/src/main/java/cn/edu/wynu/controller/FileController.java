package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.UploadFile;
import cn.edu.wynu.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            UploadFile uploadFile = fileService.uploadFile(file);
            return AjaxResult.success("文件上传成功", uploadFile);
        } catch (Exception e) {
            return AjaxResult.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public AjaxResult getFileById(@PathVariable Long id) {
        UploadFile file = fileService.getFileById(id);
        return AjaxResult.success(file);
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) {
        try {
            UploadFile file = fileService.getFileById(id);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            File f = new File(file.getFilePath());
            if (!f.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = getContentType(file.getFileType());
            FileSystemResource resource = new FileSystemResource(f);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getContentType(String fileType) {
        if (fileType == null) return "application/octet-stream";
        String type = fileType.toLowerCase();
        switch (type) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            case ".pdf":
                return "application/pdf";
            case ".zip":
                return "application/zip";
            case ".rar":
                return "application/x-rar-compressed";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".mp4":
                return "video/mp4";
            case ".mp3":
                return "audio/mpeg";
            default:
                return "application/octet-stream";
        }
    }

    @GetMapping("/my")
    public AjaxResult getMyFiles() {
        List<UploadFile> files = fileService.getMyFiles();
        return AjaxResult.success(files);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            UploadFile file = fileService.getFileById(id);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            File f = new File(file.getFilePath());
            if (!f.exists()) {
                return ResponseEntity.notFound().build();
            }

            FileSystemResource resource = new FileSystemResource(f);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteFile(@PathVariable Long id) {
        try {
            boolean result = fileService.deleteFile(id);
            return result ? AjaxResult.success("文件删除成功") : AjaxResult.error("文件删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
