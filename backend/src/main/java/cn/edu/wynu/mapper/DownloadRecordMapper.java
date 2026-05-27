package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.DownloadRecord;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DownloadRecordMapper {

    @Select("SELECT * FROM download_record WHERE resource_id = #{resourceId} AND user_id = #{userId} LIMIT 1")
    DownloadRecord findByResourceIdAndUserId(@Param("resourceId") Long resourceId, @Param("userId") Long userId);

    @Insert("INSERT INTO download_record(resource_id, user_id, username, points, is_first_download, create_time) " +
            "VALUES(#{resourceId}, #{userId}, #{username}, #{points}, #{isFirstDownload}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DownloadRecord record);

    @Select("SELECT COUNT(*) FROM download_record")
    int countAll();
}
