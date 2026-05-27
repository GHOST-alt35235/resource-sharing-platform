package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.UploadFile;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FileMapper {

    @Select("SELECT * FROM upload_file WHERE id = #{id}")
    UploadFile findById(Long id);

    @Select("SELECT * FROM upload_file WHERE uploader_id = #{uploaderId} ORDER BY create_time DESC")
    List<UploadFile> findByUploaderId(Long uploaderId);

    @Insert("INSERT INTO upload_file(file_name, original_name, file_path, file_size, file_type, uploader_id, uploader_nickname, status, create_time) " +
            "VALUES(#{fileName}, #{originalName}, #{filePath}, #{fileSize}, #{fileType}, #{uploaderId}, #{uploaderNickname}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UploadFile file);

    @Update("UPDATE upload_file SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM upload_file WHERE id = #{id}")
    int delete(Long id);
}
