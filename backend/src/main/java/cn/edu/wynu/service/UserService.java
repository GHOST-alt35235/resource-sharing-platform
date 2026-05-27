package cn.edu.wynu.service;

import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.model.dto.LoginDTO;
import cn.edu.wynu.model.dto.RegisterDTO;
import java.util.List;
import java.util.Map;

public interface UserService {
    User login(LoginDTO loginDTO);
    User register(RegisterDTO registerDTO);
    User getCurrentUser();
    User getUserById(Long id);
    List<User> getAllUsers();
    Map<String, Object> getAllUsers(Integer pageNum, Integer pageSize);
    Map<String, Object> searchUsers(String keyword, Integer pageNum, Integer pageSize);
    Integer getUserCount();
    boolean updateInfo(User user);
    boolean updateAvatar(String avatar);
    boolean updatePassword(String oldPassword, String newPassword);
    boolean disableUser(Long userId);
    boolean enableUser(Long userId);
    boolean resetPassword(Long userId, String newPassword);
}
