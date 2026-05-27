package cn.edu.wynu.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminPermissionAspect {

    @Autowired
    private UserMapper userMapper;

    @Pointcut("@annotation(cn.edu.wynu.annotation.AdminPermission)")
    public void adminPermissionPointcut() {}

    @Around("adminPermissionPointcut()")
    public Object checkAdminPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!"admin".equals(user.getRole())) {
            throw new RuntimeException("没有管理员权限");
        }
        
        return joinPoint.proceed();
    }
}
