package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.PointsMapper;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.entity.PointsRecord;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private PointsMapper pointsMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<PointsRecord> getMyPointsRecords() {
        Long userId = StpUtil.getLoginIdAsLong();
        return pointsMapper.findByUserId(userId);
    }

    @Override
    public List<PointsRecord> getRecentRecords(Integer limit) {
        return pointsMapper.findRecent(limit != null ? limit : 20);
    }

    @Override
    public Integer getMyPoints() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);
        return user != null ? user.getPoints() : 0;
    }
}
