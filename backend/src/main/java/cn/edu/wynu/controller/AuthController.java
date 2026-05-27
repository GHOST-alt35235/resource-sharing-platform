package cn.edu.wynu.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.LoginDTO;
import cn.edu.wynu.model.dto.RegisterDTO;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AjaxResult login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        return AjaxResult.success("登录成功", Map.of(
                "token", StpUtil.getTokenValue(),
                "user", user
        ));
    }

    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return AjaxResult.success("注册成功", user);
    }

    @PostMapping("/logout")
    public AjaxResult logout() {
        StpUtil.logout();
        return AjaxResult.success("退出成功");
    }

    @GetMapping("/current")
    public AjaxResult getCurrentUser() {
        User user = userService.getCurrentUser();
        return AjaxResult.success(user);
    }
}
