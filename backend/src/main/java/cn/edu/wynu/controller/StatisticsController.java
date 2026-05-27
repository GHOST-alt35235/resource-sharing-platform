package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.mapper.DownloadRecordMapper;
import cn.edu.wynu.mapper.ResourceMapper;
import cn.edu.wynu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DownloadRecordMapper downloadRecordMapper;

    @GetMapping
    public AjaxResult getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("resources", resourceMapper.countAllApproved());
        stats.put("users", userMapper.countAll());
        stats.put("downloads", downloadRecordMapper.countAll());
        return AjaxResult.success(stats);
    }
}
