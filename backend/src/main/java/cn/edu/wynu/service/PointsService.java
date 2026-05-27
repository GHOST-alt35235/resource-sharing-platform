package cn.edu.wynu.service;

import cn.edu.wynu.model.entity.PointsRecord;
import java.util.List;

public interface PointsService {
    List<PointsRecord> getMyPointsRecords();
    List<PointsRecord> getRecentRecords(Integer limit);
    Integer getMyPoints();
}
