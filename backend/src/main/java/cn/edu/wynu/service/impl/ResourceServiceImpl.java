package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.*;
import cn.edu.wynu.model.dto.ResourceDTO;
import cn.edu.wynu.model.entity.*;
import cn.edu.wynu.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private PointsMapper pointsMapper;

    @Autowired
    private DownloadRecordMapper downloadRecordMapper;

    @Value("${points.upload-reward:10}")
    private Integer uploadReward;

    @Override
    public Resource getResourceById(Long id) {
        return resourceMapper.findById(id);
    }

    @Override
    public List<Resource> getAllApprovedResources() {
        return resourceMapper.findAllApproved();
    }

    @Override
    public List<Resource> searchResources(String keyword) {
        return resourceMapper.search(keyword);
    }

    @Override
    public List<Resource> getMyResources() {
        Long userId = StpUtil.getLoginIdAsLong();
        return resourceMapper.findByUploaderId(userId);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceMapper.findAll();
    }

    @Override
    @Transactional
    public Resource createResource(ResourceDTO resourceDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);

        UploadFile file = fileMapper.findById(resourceDTO.getFileId());
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new Resource();
        resource.setTitle(resourceDTO.getTitle());
        resource.setDescription(resourceDTO.getDescription());
        resource.setFileId(resourceDTO.getFileId());
        resource.setFileName(file.getOriginalName());
        resource.setFileSize(file.getFileSize());
        resource.setPrice(resourceDTO.getPrice() != null ? resourceDTO.getPrice() : 0);
        resource.setUploaderId(userId);
        resource.setUploaderName(user.getNickname());
        resource.setStatus(0);
        resourceMapper.insert(resource);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setUsername(user.getUsername());
        record.setPoints(uploadReward);
        record.setType(1);
        record.setDescription("上传资源奖励");
        record.setRelatedId(resource.getId());
        pointsMapper.insert(record);

        userMapper.updatePointsById(userId, user.getPoints() + uploadReward);

        return resource;
    }

    @Override
    public boolean updateResource(Long id, ResourceDTO resourceDTO) {
        Resource resource = resourceMapper.findById(id);
        Long userId = StpUtil.getLoginIdAsLong();
        if (!resource.getUploaderId().equals(userId)) {
            throw new RuntimeException("无权修改此资源");
        }
        resource.setTitle(resourceDTO.getTitle());
        resource.setDescription(resourceDTO.getDescription());
        if (resourceDTO.getPrice() != null) {
            resource.setPrice(resourceDTO.getPrice());
        }
        return resourceMapper.update(resource) > 0;
    }

    @Override
    public boolean deleteResource(Long id) {
        Resource resource = resourceMapper.findById(id);
        Long userId = StpUtil.getLoginIdAsLong();
        if (!resource.getUploaderId().equals(userId)) {
            throw new RuntimeException("无权删除此资源");
        }
        return resourceMapper.delete(id) > 0;
    }

    @Override
    public boolean approveResource(Long id) {
        return resourceMapper.updateStatus(id, 1) > 0;
    }

    @Override
    public boolean rejectResource(Long id) {
        return resourceMapper.updateStatus(id, -1) > 0;
    }

    @Override
    public boolean rejectResource(Long id, String reason) {
        return resourceMapper.rejectResource(id, -1, reason) > 0;
    }

    @Override
    public boolean offlineResource(Long id) {
        return resourceMapper.updateStatus(id, 2) > 0;
    }

    @Override
    @Transactional
    public boolean downloadResource(Long id) {
        Resource resource = resourceMapper.findById(id);
        if (resource == null || resource.getStatus() != 1) {
            throw new RuntimeException("资源不存在或未审核通过");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);
        
        // 管理员下载免费
        if ("admin".equals(user.getRole())) {
            resourceMapper.incrementDownloadCount(id);
            return true;
        }
        
        // 上传者自己下载免费
        if (resource.getUploaderId().equals(userId)) {
            resourceMapper.incrementDownloadCount(id);
            return true;
        }

        DownloadRecord existRecord = downloadRecordMapper.findByResourceIdAndUserId(id, userId);
        if (existRecord != null) {
            resourceMapper.incrementDownloadCount(id);
            return true;
        }

        int price = resource.getPrice();
        if (price > 0) {
            if (user.getPoints() < price) {
                throw new RuntimeException("积分不足");
            }

            userMapper.updatePointsById(userId, user.getPoints() - price);

            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setUsername(user.getUsername());
            record.setPoints(-price);
            record.setType(2);
            record.setDescription("下载资源: " + resource.getTitle());
            record.setRelatedId(id);
            pointsMapper.insert(record);

            User uploader = userMapper.findById(resource.getUploaderId());
            userMapper.updatePointsById(resource.getUploaderId(), uploader.getPoints() + price);

            PointsRecord rewardRecord = new PointsRecord();
            rewardRecord.setUserId(resource.getUploaderId());
            rewardRecord.setUsername(uploader.getUsername());
            rewardRecord.setPoints(price);
            rewardRecord.setType(1);
            rewardRecord.setDescription("资源被下载奖励: " + resource.getTitle());
            rewardRecord.setRelatedId(id);
            pointsMapper.insert(rewardRecord);
        }

        DownloadRecord record = new DownloadRecord();
        record.setResourceId(id);
        record.setUserId(userId);
        record.setUsername(user.getUsername());
        record.setPoints(price);
        record.setIsFirstDownload(1);
        downloadRecordMapper.insert(record);

        resourceMapper.incrementDownloadCount(id);
        return true;
    }
}
