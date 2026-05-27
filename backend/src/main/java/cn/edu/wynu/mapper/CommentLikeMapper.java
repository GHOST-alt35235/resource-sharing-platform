package cn.edu.wynu.mapper;

import org.apache.ibatis.annotations.*;
import cn.edu.wynu.model.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentLikeMapper {

    @Select("SELECT * FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    CommentLike findByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Insert("INSERT INTO comment_like(comment_id, user_id, create_time) VALUES(#{commentId}, #{userId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CommentLike commentLike);

    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM comment_like WHERE comment_id = #{commentId}")
    int countByCommentId(Long commentId);
}
