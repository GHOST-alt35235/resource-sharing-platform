package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.PointsRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointsMapper {

    @Insert("INSERT INTO points_record(user_id, username, points, type, description, related_id, create_time) " +
            "VALUES(#{userId}, #{username}, #{points}, #{type}, #{description}, #{relatedId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointsRecord record);

    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PointsRecord> findByUserId(Long userId);

    @Select("SELECT * FROM points_record ORDER BY create_time DESC LIMIT #{limit}")
    List<PointsRecord> findRecent(@Param("limit") Integer limit);

    }
