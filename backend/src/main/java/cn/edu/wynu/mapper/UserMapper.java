package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User login(@Param("username") String username, @Param("password") String password);

    @Insert("INSERT INTO user(username, password, nickname, phone, points, status, role, create_time) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{points}, #{status}, #{role}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET nickname = #{nickname}, introduction = #{introduction}, update_time = NOW() WHERE id = #{id}")
    int updateInfo(User user);

    @Update("UPDATE user SET avatar = #{avatar}, update_time = NOW() WHERE id = #{userId}")
    int updateAvatarById(@Param("userId") Long userId, @Param("avatar") String avatar);

    @Update("UPDATE user SET points = #{points}, update_time = NOW() WHERE id = #{userId}")
    int updatePointsById(@Param("userId") Long userId, @Param("points") Integer points);

    @Update("UPDATE user SET status = #{status}, update_time = NOW() WHERE id = #{userId}")
    int updateStatusById(@Param("userId") Long userId, @Param("status") Integer status);

    @Update("UPDATE user SET password = #{password}, update_time = NOW() WHERE id = #{userId}")
    int updatePasswordById(@Param("userId") Long userId, @Param("password") String password);

    @Select("SELECT * FROM user WHERE status = 1")
    List<User> findAllActive();

    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user LIMIT #{startIndex}, #{pageSize}")
    List<User> findAllWithPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM user")
    int countAll();

    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%') LIMIT #{startIndex}, #{pageSize}")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')")
    int countSearchUsers(@Param("keyword") String keyword);
}
