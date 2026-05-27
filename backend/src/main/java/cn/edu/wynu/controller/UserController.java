package cn.edu.wynu.controller;

import cn.edu.wynu.annotation.AdminPermission;
import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public AjaxResult getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return AjaxResult.success(user);
    }

    @AdminPermission
    @GetMapping("/list")
    public AjaxResult getAllUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = userService.getAllUsers(pageNum, pageSize);
        return AjaxResult.success(result);
    }

    @AdminPermission
    @GetMapping("/search")
    public AjaxResult searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = userService.searchUsers(keyword, pageNum, pageSize);
        return AjaxResult.success(result);
    }

    @AdminPermission
    @GetMapping("/list/all")
    public AjaxResult getAllUsersWithoutPage() {
        List<User> users = userService.getAllUsers();
        return AjaxResult.success(users);
    }

    @AdminPermission
    @GetMapping("/count")
    public AjaxResult getUserCount() {
        Integer count = userService.getUserCount();
        return AjaxResult.success(count);
    }

    @PutMapping("/info")
    public AjaxResult updateInfo(@RequestBody User user) {
        boolean result = userService.updateInfo(user);
        return result ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
    }

    @PutMapping("/avatar")
    public AjaxResult updateAvatar(@RequestBody Map<String, String> params) {
        String avatar = params.get("avatar");
        boolean result = userService.updateAvatar(avatar);
        return result ? AjaxResult.success("头像更新成功") : AjaxResult.error("头像更新失败");
    }

    @PutMapping("/password")
    public AjaxResult updatePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        boolean result = userService.updatePassword(oldPassword, newPassword);
        return result ? AjaxResult.success("密码修改成功") : AjaxResult.error("密码修改失败");
    }

    @AdminPermission
    @PutMapping("/disable/{id}")
    public AjaxResult disableUser(@PathVariable Long id) {
        boolean result = userService.disableUser(id);
        return result ? AjaxResult.success("用户已禁用") : AjaxResult.error("操作失败");
    }

    @AdminPermission
    @PutMapping("/enable/{id}")
    public AjaxResult enableUser(@PathVariable Long id) {
        boolean result = userService.enableUser(id);
        return result ? AjaxResult.success("用户已启用") : AjaxResult.error("操作失败");
    }

    @AdminPermission
    @PutMapping("/resetPassword/{id}")
    public AjaxResult resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("newPassword");
        boolean result = userService.resetPassword(id, newPassword);
        return result ? AjaxResult.success("密码重置成功") : AjaxResult.error("操作失败");
    }
}
