# 部署前检查清单

## 1. 服务器准备

### 服务器要求
- [ ] **操作系统**: Ubuntu 20.04+ / Debian 11+
- [ ] **配置**: 最低 1核2G（推荐 2核4G）
- [ ] **公网IP**: 已分配可访问的公网IP
- [ ] **端口开放**:
  - [ ] 22 (SSH)
  - [ ] 80 (HTTP)
  - [ ] 443 (HTTPS)
  - [ ] 3306 (MySQL，仅内网访问)

### 域名准备（可选但推荐）
- [ ] 域名已注册
- [ ] DNS 已解析到服务器 IP
- [ ] 可使用 ping 验证域名解析

## 2. 本地准备

### 代码准备
- [ ] 最新代码已推送到 Git 仓库
- [ ] 数据库初始化脚本已准备（schema.sql, data.sql）
- [ ] 生产环境配置文件已创建

### 构建产物
- [ ] 后端 JAR 文件已构建: `backend/target/resource-sharing-1.0.0.jar`
- [ ] 前端已构建: `frontend/dist/`

## 3. 云服务器安全组/防火墙配置

### 阿里云/腾讯云
- [ ] 添加安全组规则，开放 80, 443 端口
- [ ] 限制 SSH 访问（仅允许特定 IP）

### 服务器内部防火墙
```bash
# 检查防火墙状态
sudo ufw status

# 开放必要端口
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
```

## 4. 环境变量准备

在部署前请准备好以下信息：

| 配置项 | 说明 | 示例 |
|--------|------|------|
| DB_HOST | 数据库主机地址 | localhost |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名 | resource_sharing |
| DB_USERNAME | 数据库用户名 | appuser |
| DB_PASSWORD | 数据库密码 | ********** |
| DOMAIN | 域名（可选） | example.com |
| EMAIL | 邮箱（SSL证书用） | admin@example.com |

## 5. 部署后验证清单

### 服务状态检查
```bash
# 检查所有服务状态
systemctl status mysql
systemctl status nginx
systemctl status resource-sharing

# 检查端口监听
netstat -tlnp | grep -E '80|443|8080|3306'
```

### 功能测试
- [ ] 访问 http://服务器IP 显示前端页面
- [ ] API 接口可访问（http://服务器IP/api/xxx）
- [ ] 数据库连接正常
- [ ] 文件上传功能正常

### 日志检查
```bash
# 后端日志
journalctl -u resource-sharing -n 100

# Nginx 日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# 应用日志
tail -f /var/log/resource-sharing/application.log
```

## 6. 域名 SSL 证书申请（可选）

如果使用域名，可以通过 Let's Encrypt 免费申请 SSL 证书：

```bash
# 申请证书
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# 自动续期测试
sudo certbot renew --dry-run
```

## 7. 性能优化（可选）

### JVM 调优
编辑 systemd 服务文件，调整 JVM 参数：
```bash
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -XX:+UseG1GC -jar /opt/resource-sharing/backend/resource-sharing-1.0.0.jar
```

### Nginx 调优
编辑 nginx.conf：
```nginx
worker_processes auto;
worker_connections 1024;
keepalive_timeout 65;
```

## 8. 备份策略

### 数据库备份
```bash
# 创建备份脚本
mysqldump -u root -p resource_sharing > backup_$(date +%Y%m%d).sql
```

### 文件备份
```bash
# 备份上传的文件
tar -czf uploads_backup_$(date +%Y%m%d).tar.gz /var/www/uploads
```

---

## 快速部署命令

如果所有检查项都已确认，可以使用以下命令开始部署：

```bash
# 1. 上传代码到服务器
scp -r ./backend ./frontend deploy/ user@your-server:/opt/resource-sharing/

# 2. SSH 登录服务器
ssh user@your-server

# 3. 进入部署目录
cd /opt/resource-sharing

# 4. 给脚本添加执行权限
chmod +x deploy/deploy.sh

# 5. 运行部署脚本
sudo ./deploy/deploy.sh
```
