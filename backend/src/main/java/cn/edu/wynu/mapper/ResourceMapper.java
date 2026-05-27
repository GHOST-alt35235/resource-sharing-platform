package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.Resource;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ResourceMapper {

    @Select("SELECT r.*, uf.file_type FROM resource r LEFT JOIN upload_file uf ON r.file_id = uf.id WHERE r.id = #{id}")
    Resource findById(Long id);

    @Select("SELECT r.*, uf.file_type FROM resource r LEFT JOIN upload_file uf ON r.file_id = uf.id WHERE r.status = 1 ORDER BY r.create_time DESC")
    List<Resource> findAllApproved();

    @Select("SELECT r.*, uf.file_type FROM resource r LEFT JOIN upload_file uf ON r.file_id = uf.id WHERE r.uploader_id = #{uploaderId} ORDER BY r.create_time DESC")
    List<Resource> findByUploaderId(Long uploaderId);

    @Select("SELECT r.*, uf.file_type FROM resource r LEFT JOIN upload_file uf ON r.file_id = uf.id ORDER BY r.create_time DESC")
    List<Resource> findAll();

    @Select("<script>" +
            "SELECT r.*, uf.file_type FROM resource r LEFT JOIN upload_file uf ON r.file_id = uf.id WHERE r.status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (r.title LIKE CONCAT('%', #{keyword}, '%') OR r.description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    List<Resource> search(@Param("keyword") String keyword);

    @Insert("INSERT INTO resource(title, description, file_id, file_name, file_size, price, uploader_id, uploader_name, status, download_count, create_time) " +
            "VALUES(#{title}, #{description}, #{fileId}, #{fileName}, #{fileSize}, #{price}, #{uploaderId}, #{uploaderName}, #{status}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Resource resource);

    @Update("UPDATE resource SET title = #{title}, description = #{description}, price = #{price}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    int update(Resource resource);

    @Update("UPDATE resource SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE resource SET status = #{status}, reject_reason = #{rejectReason}, update_time = NOW() WHERE id = #{id}")
    int rejectResource(@Param("id") Long id, @Param("status") Integer status, @Param("rejectReason") String rejectReason);

    @Update("UPDATE resource SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(Long id);

    @Delete("DELETE FROM resource WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT COUNT(*) FROM resource WHERE status = 1")
    int countAllApproved();
}
