package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.Resource;
import cn.edu.wynu.model.dto.ResourceDTO;
import cn.edu.wynu.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/{id}")
    public AjaxResult getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id);
        return AjaxResult.success(resource);
    }

    @GetMapping("/list")
    public AjaxResult getAllResources() {
        List<Resource> resources = resourceService.getAllApprovedResources();
        return AjaxResult.success(resources);
    }

    @GetMapping("/search")
    public AjaxResult searchResources(@RequestParam(required = false) String keyword) {
        List<Resource> resources;
        if (keyword != null && !keyword.isEmpty()) {
            resources = resourceService.searchResources(keyword);
        } else {
            resources = resourceService.getAllApprovedResources();
        }
        return AjaxResult.success(resources);
    }

    @GetMapping("/my")
    public AjaxResult getMyResources() {
        List<Resource> resources = resourceService.getMyResources();
        return AjaxResult.success(resources);
    }

    @GetMapping("/admin/all")
    public AjaxResult getAllResourcesForAdmin() {
        List<Resource> resources = resourceService.getAllResources();
        return AjaxResult.success(resources);
    }

    @PostMapping
    public AjaxResult createResource(@Valid @RequestBody ResourceDTO resourceDTO) {
        Resource resource = resourceService.createResource(resourceDTO);
        return AjaxResult.success("资源创建成功", resource);
    }

    @PutMapping("/{id}")
    public AjaxResult updateResource(@PathVariable Long id, @Valid @RequestBody ResourceDTO resourceDTO) {
        boolean result = resourceService.updateResource(id, resourceDTO);
        return result ? AjaxResult.success("资源更新成功") : AjaxResult.error("资源更新失败");
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteResource(@PathVariable Long id) {
        boolean result = resourceService.deleteResource(id);
        return result ? AjaxResult.success("资源删除成功") : AjaxResult.error("资源删除失败");
    }

    @PutMapping("/approve/{id}")
    public AjaxResult approveResource(@PathVariable Long id) {
        boolean result = resourceService.approveResource(id);
        return result ? AjaxResult.success("资源审核通过") : AjaxResult.error("操作失败");
    }

    @PutMapping("/reject/{id}")
    public AjaxResult rejectResource(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        String reason = params != null ? params.get("reason") : null;
        boolean result;
        if (reason != null && !reason.isEmpty()) {
            result = resourceService.rejectResource(id, reason);
        } else {
            result = resourceService.rejectResource(id);
        }
        return result ? AjaxResult.success("资源已驳回") : AjaxResult.error("操作失败");
    }

    @PutMapping("/offline/{id}")
    public AjaxResult offlineResource(@PathVariable Long id) {
        boolean result = resourceService.offlineResource(id);
        return result ? AjaxResult.success("资源已下架") : AjaxResult.error("操作失败");
    }

    @PostMapping("/download/{id}")
    public AjaxResult downloadResource(@PathVariable Long id) {
        boolean result = resourceService.downloadResource(id);
        return result ? AjaxResult.success("下载成功") : AjaxResult.error("下载失败");
    }
}
