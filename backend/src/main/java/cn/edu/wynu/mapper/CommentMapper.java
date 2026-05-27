package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment findById(Long id);

    @Select("SELECT * FROM comment WHERE resource_id = #{resourceId} AND status = 1 ORDER BY like_count DESC, create_time DESC")
    List<Comment> findByResourceId(Long resourceId);

    @Select("SELECT * FROM comment WHERE parent_id = #{parentId} AND status = 1 ORDER BY create_time ASC")
    List<Comment> findByParentId(Long parentId);

    @Select("SELECT * FROM comment WHERE resource_id = #{resourceId} AND parent_id IS NULL AND status = 1 ORDER BY like_count DESC, create_time DESC")
    List<Comment> findRootComments(Long resourceId);

    @Insert("INSERT INTO comment(resource_id, user_id, username, nickname, parent_id, content, like_count, status, create_time) " +
            "VALUES(#{resourceId}, #{userId}, #{username}, #{nickname}, #{parentId}, #{content}, 0, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE comment SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    int decrementLikeCount(Long id);

    @Update("UPDATE comment SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int delete(Long id);
}
