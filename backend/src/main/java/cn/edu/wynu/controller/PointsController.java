package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.PointsRecord;
import cn.edu.wynu.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/balance")
    public AjaxResult getMyPoints() {
        Integer points = pointsService.getMyPoints();
        return AjaxResult.success(points);
    }

    @GetMapping("/records")
    public AjaxResult getMyPointsRecords() {
        List<PointsRecord> records = pointsService.getMyPointsRecords();
        return AjaxResult.success(records);
    }

    @GetMapping("/recent")
    public AjaxResult getRecentRecords(@RequestParam(required = false) Integer limit) {
        List<PointsRecord> records = pointsService.getRecentRecords(limit);
        return AjaxResult.success(records);
    }
}
