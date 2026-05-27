package cn.edu.wynu.service;

import cn.edu.wynu.model.entity.Resource;
import cn.edu.wynu.model.dto.ResourceDTO;
import java.util.List;

public interface ResourceService {
    Resource getResourceById(Long id);
    List<Resource> getAllApprovedResources();
    List<Resource> searchResources(String keyword);
    List<Resource> getMyResources();
    List<Resource> getAllResources();
    Resource createResource(ResourceDTO resourceDTO);
    boolean updateResource(Long id, ResourceDTO resourceDTO);
    boolean deleteResource(Long id);
    boolean approveResource(Long id);
    boolean rejectResource(Long id);
    boolean rejectResource(Long id, String reason);
    boolean offlineResource(Long id);
    boolean downloadResource(Long id);
}
