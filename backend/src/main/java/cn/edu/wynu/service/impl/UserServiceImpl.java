package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.dto.LoginDTO;
import cn.edu.wynu.model.dto.RegisterDTO;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Value("${points.register-gift:100}")
    private Integer registerGift;

    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }

    private void clearPassword(User user) {
        if (user != null) {
            user.setPassword(null);
        }
    }

    @Override
    public User login(LoginDTO loginDTO) {
        String password = encryptPassword(loginDTO.getPassword());
        User user = userMapper.login(loginDTO.getUsername(), password);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("该账号已被禁用");
        }
        StpUtil.login(user.getId());
        clearPassword(user);
        return user;
    }

    @Override
    public User register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }
        User existUser = userMapper.findByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        User existPhone = userMapper.findByPhone(registerDTO.getPhone());
        if (existPhone != null) {
            throw new RuntimeException("手机号已被注册");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptPassword(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setPhone(registerDTO.getPhone());
        user.setPoints(registerGift);
        user.setStatus(1);
        user.setRole("user");
        userMapper.insert(user);
        clearPassword(user);
        return user;
    }

    @Override
    public User getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);
        clearPassword(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        clearPassword(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAll();
        users.forEach(this::clearPassword);
        return users;
    }

    @Override
    public Map<String, Object> getAllUsers(Integer pageNum, Integer pageSize) {
        Integer count = userMapper.countAll();
        Integer startIndex = (pageNum - 1) * pageSize;
        List<User> users = userMapper.findAllWithPage(startIndex, pageSize);
        users.forEach(this::clearPassword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", count);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) count / pageSize));
        
        return result;
    }

    @Override
    public Map<String, Object> searchUsers(String keyword, Integer pageNum, Integer pageSize) {
        Integer count = userMapper.countSearchUsers(keyword);
        Integer startIndex = (pageNum - 1) * pageSize;
        List<User> users = userMapper.searchUsers(keyword, startIndex, pageSize);
        users.forEach(this::clearPassword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", count);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) count / pageSize));
        result.put("keyword", keyword);
        
        return result;
    }

    @Override
    public Integer getUserCount() {
        return userMapper.countAll();
    }

    @Override
    public boolean updateInfo(User user) {
        User currentUser = getCurrentUser();
        user.setId(currentUser.getId());
        return userMapper.updateInfo(user) > 0;
    }

    @Override
    public boolean updateAvatar(String avatar) {
        Long userId = StpUtil.getLoginIdAsLong();
        return userMapper.updateAvatarById(userId, avatar) > 0;
    }

    @Override
    public boolean updatePassword(String oldPassword, String newPassword) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);
        String oldPwd = encryptPassword(oldPassword);
        if (!oldPwd.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        String newPwd = encryptPassword(newPassword);
        return userMapper.updatePasswordById(userId, newPwd) > 0;
    }

    @Override
    public boolean disableUser(Long userId) {
        return userMapper.updateStatusById(userId, 0) > 0;
    }

    @Override
    public boolean enableUser(Long userId) {
        return userMapper.updateStatusById(userId, 1) > 0;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        String password = encryptPassword(newPassword);
        return userMapper.updatePasswordById(userId, password) > 0;
    }
}
