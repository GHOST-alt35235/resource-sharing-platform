# 服务器环境准备指南

## 一、购买云服务器

推荐服务商（学生/新手优惠）：

| 服务商 | 特点 | 官网 |
|--------|------|------|
| 阿里云 | 稳定性高，文档完善 | https://www.aliyun.com |
| 腾讯云 | 价格优惠，生态完善 | https://cloud.tencent.com |
| 华为云 | 政务/企业客户多 | https://www.huaweicloud.com |
| AWS | 全球覆盖，免费套餐 | https://aws.amazon.com |

### 推荐配置
- **新手/学习**: 1核2G 最低配
- **正式使用**: 2核4G 起
- **系统选择**: Ubuntu 20.04 LTS 或 Debian 11

## 二、SSH 连接服务器

### Windows 用户
推荐使用 **PowerShell** 或 **Windows Terminal**：

```powershell
ssh root@你的服务器IP
```

或者使用工具：
- Xshell
- MobaXterm
- Windows Terminal (内置 SSH)

### Mac/Linux 用户
```bash
ssh root@你的服务器IP
```

### 首次连接后

```bash
# 更新系统
apt update && apt upgrade -y

# 安装基础工具
apt install -y wget curl git unzip
```

## 三、服务器安全配置

### 1. 创建新用户（可选但推荐）

```bash
# 创建新用户
adduser deploy

# 添加 sudo 权限
usermod -aG sudo deploy

# 切换到新用户
su - deploy
```

### 2. 配置 SSH 密钥登录

```bash
# 在本地生成 SSH 密钥（本地执行）
ssh-keygen -t rsa -b 4096

# 上传公钥到服务器
ssh-copy-id deploy@你的服务器IP

# 之后可以免密登录
ssh deploy@你的服务器IP
```

### 3. 配置防火墙

```bash
# 安装 ufw
sudo apt install -y ufw

# 设置默认规则
sudo ufw default deny incoming
sudo ufw default allow outgoing

# 开放必要端口
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS

# 启用防火墙
sudo ufw enable

# 查看状态
sudo ufw status verbose
```

### 4. 修改 SSH 端口（可选）

```bash
sudo nano /etc/ssh/sshd_config
# 找到 Port 22，改为其他端口（如 2222）
# 重启 SSH 服务
sudo systemctl restart sshd
```

## 四、安装 Java 17

### 方法一：使用 apt（推荐）

```bash
# 安装 OpenJDK 17
sudo apt update
sudo apt install -y openjdk-17-jdk

# 验证安装
java -version

# 设置 JAVA_HOME
echo "JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" | sudo tee -a /etc/environment
source /etc/environment
```

### 方法二：手动安装

```bash
# 下载 JDK
cd /opt
sudo wget https://download.java.net/java/GA/jdk17/0d483333a00580d32a17a13ce8c9f22f/9/GPL/openjdk-17_linux-x64_bin.tar.gz

# 解压
sudo tar -xzf openjdk-17_linux-x64_bin.tar.gz

# 配置环境变量
export JAVA_HOME=/opt/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

## 五、安装 MySQL 8.0

### 安装

```bash
# 安装 MySQL
sudo apt update
sudo apt install -y mysql-server

# 启动并设置开机启动
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置（生产环境必须执行）
sudo mysql_secure_installation
```

### 配置远程访问（如需要）

```bash
# 编辑 MySQL 配置
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf

# 找到 bind-address，改为 0.0.0.0
bind-address = 0.0.0.0

# 重启
sudo systemctl restart mysql
```

### 创建数据库和用户

```bash
sudo mysql -u root -p
```

```sql
-- 创建数据库
CREATE DATABASE resource_sharing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（生产环境请使用强密码）
CREATE USER 'appuser'@'localhost' IDENTIFIED BY '你的强密码';

-- 授权
GRANT ALL PRIVILEGES ON resource_sharing.* TO 'appuser'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

## 六、安装 Nginx

```bash
# 安装
sudo apt update
sudo apt install -y nginx

# 启动并设置开机启动
sudo systemctl start nginx
sudo systemctl enable nginx

# 测试
curl http://localhost
```

## 七、安装 Node.js（用于前端构建）

```bash
# 使用 NodeSource 安装 Node.js 18.x
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# 验证安装
node -v
npm -v
```

## 八、安装 Certbot（SSL 证书工具）

```bash
# 安装 certbot
sudo apt install -y certbot python3-certbot-nginx

# 验证
certbot --version
```

## 九、上传项目代码

### 方法一：使用 Git（推荐）

```bash
# 在服务器上克隆
cd /opt
sudo git clone https://your-repo-url/resource-sharing.git

# 或者在本地打包上传
# 本地执行：
tar -czf resource-sharing.tar.gz backend frontend deploy

# 上传到服务器
scp resource-sharing.tar.gz user@your-server:/opt/

# 服务器上解压
sudo tar -xzf resource-sharing.tar.gz
```

### 方法二：使用 SCP 直接上传

```bash
# 上传整个项目（本地执行）
scp -r ./backend user@your-server:/opt/resource-sharing/
scp -r ./frontend user@your-server:/opt/resource-sharing/
scp -r ./deploy user@your-server:/opt/resource-sharing/
```

## 十、构建项目

### 构建后端

```bash
cd /opt/resource-sharing/backend

# 确保使用生产配置
# 编辑 src/main/resources/application-prod.yml
# 设置正确的数据库连接信息

# 打包（跳过测试）
mvn clean package -DskipTests
```

### 构建前端

```bash
cd /opt/resource-sharing/frontend

# 安装依赖
npm install

# 修改 API 地址（如果需要）
# 编辑 vite.config.js 或 .env.production
# VITE_API_BASE_URL=http://your-domain.com

# 构建生产版本
npm run build
```

## 十一、验证部署

```bash
# 检查服务状态
sudo systemctl status mysql
sudo systemctl status nginx
sudo systemctl status resource-sharing

# 检查端口
sudo netstat -tlnp | grep -E '80|443|8080|3306'

# 测试访问
curl http://localhost
curl http://localhost/api/xxx
```

## 常见问题

### 1. 端口被占用
```bash
# 查找占用端口的进程
sudo lsof -i :8080
sudo fuser -k 8080/tcp
```

### 2. 数据库连接失败
```bash
# 检查 MySQL 日志
sudo tail -f /var/log/mysql/error.log

# 测试连接
mysql -u appuser -p resource_sharing
```

### 3. Nginx 502 错误
```bash
# 检查后端是否启动
curl http://localhost:8080

# 检查 Nginx 日志
sudo tail -f /var/log/nginx/error.log
```

### 4. 权限问题
```bash
# 设置正确权限
sudo chown -R www-data:www-data /var/www/html
sudo chown -R www-data:www-data /var/www/uploads
```

---

## 下一步

完成服务器准备后，请查看 `DEPLOYMENT_CHECKLIST.md` 进行部署！
