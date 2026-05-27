#!/bin/bash

# ============================================
# 资源共享与积分交易平台 - 部署脚本
# 适用于 Ubuntu/Debian 服务器
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 打印带颜色的消息
print_msg() {
    echo -e "${GREEN}[部署]${NC} $1"
}

print_error() {
    echo -e "${RED}[错误]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[警告]${NC} $1"
}

# 检查是否为 root 用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
       print_error "请使用 root 用户运行此脚本"
       exit 1
    fi
}

# 安装依赖
install_dependencies() {
    print_msg "开始安装系统依赖..."

    apt update
    apt install -y openjdk-17-jdk mysql-server nginx certbot python3-certbot-nginx

    print_msg "系统依赖安装完成"
}

# 配置 MySQL
setup_mysql() {
    print_msg "开始配置 MySQL..."

    # 启动 MySQL
    systemctl start mysql
    systemctl enable mysql

    # 安全配置
    mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '${DB_PASSWORD}';"
    mysql -u root -p${DB_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS resource_sharing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    mysql -u root -p${DB_PASSWORD} -e "CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';"
    mysql -u root -p${DB_PASSWORD} -e "GRANT ALL PRIVILEGES ON resource_sharing.* TO 'appuser'@'localhost';"
    mysql -u root -p${DB_PASSWORD} -e "FLUSH PRIVILEGES;"

    print_msg "MySQL 配置完成"
}

# 创建目录结构
create_directories() {
    print_msg "创建目录结构..."

    mkdir -p /var/www/html
    mkdir -p /var/www/uploads
    mkdir -p /var/log/resource-sharing
    mkdir -p /opt/resource-sharing/backend
    mkdir -p /opt/resource-sharing/frontend

    # 设置权限
    chown -R www-data:www-data /var/www/html
    chown -R www-data:www-data /var/www/uploads
    chown -R www-data:www-data /var/log/resource-sharing

    print_msg "目录结构创建完成"
}

# 部署后端
deploy_backend() {
    print_msg "开始部署后端..."

    # 复制 JAR 文件
    cp target/resource-sharing-1.0.0.jar /opt/resource-sharing/backend/

    # 创建 systemd 服务文件
    cat > /etc/systemd/system/resource-sharing.service << EOF
[Unit]
Description=Resource Sharing Backend
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/resource-sharing/backend
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/resource-sharing/backend/resource-sharing-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=append:/var/log/resource-sharing/application.log
StandardError=append:/var/log/resource-sharing/application.log

[Install]
WantedBy=multi-user.target
EOF

    # 重载 systemd 并启动服务
    systemctl daemon-reload
    systemctl enable resource-sharing
    systemctl restart resource-sharing

    # 检查服务状态
    sleep 5
    if systemctl is-active --quiet resource-sharing; then
        print_msg "后端部署成功！"
    else
        print_error "后端部署失败，请检查日志："
        journalctl -u resource-sharing -n 50
        exit 1
    fi
}

# 部署前端
deploy_frontend() {
    print_msg "开始部署前端..."

    # 复制前端构建文件
    rm -rf /var/www/html/*
    cp -r dist/* /var/www/html/

    # 设置权限
    chown -R www-data:www-data /var/www/html

    # 复制 Nginx 配置
    cp deploy/nginx/frontend.conf /etc/nginx/sites-available/resource-sharing
    ln -sf /etc/nginx/sites-available/resource-sharing /etc/nginx/sites-enabled/

    # 测试 Nginx 配置
    nginx -t

    # 重载 Nginx
    systemctl reload nginx

    print_msg "前端部署成功！"
}

# 配置 SSL（可选）
setup_ssl() {
    if [[ -z "${DOMAIN}" ]]; then
        print_warning "未设置域名，跳过 SSL 配置"
        return
    fi

    print_msg "开始配置 SSL..."

    certbot --nginx -d ${DOMAIN} --non-interactive --agree-tos --email ${EMAIL} --redirect

    # 自动续期
    systemctl enable certbot.timer
    systemctl start certbot.timer

    print_msg "SSL 配置完成！"
}

# 显示部署信息
show_info() {
    echo ""
    echo "========================================"
    echo "         部署完成！"
    echo "========================================"
    echo ""
    echo "服务状态："
    echo "  后端服务: $(systemctl is-active resource-sharing)"
    echo "  Nginx: $(systemctl is-active nginx)"
    echo "  MySQL: $(systemctl is-active mysql)"
    echo ""
    echo "访问地址："
    if [[ -n "${DOMAIN}" ]]; then
        echo "  https://${DOMAIN}"
    else
        echo "  http://你的服务器IP"
    fi
    echo ""
    echo "常用命令："
    echo "  查看后端日志: journalctl -u resource-sharing -f"
    echo "  重启后端: systemctl restart resource-sharing"
    echo "  查看 Nginx 日志: tail -f /var/log/nginx/error.log"
    echo ""
}

# 主函数
main() {
    echo "========================================"
    echo "  资源共享与积分交易平台 - 一键部署"
    echo "========================================"
    echo ""

    check_root

    # 读取配置
    read -p "请输入数据库密码: " DB_PASSWORD
    read -p "请输入域名（可选，回车跳过）: " DOMAIN
    read -p "请输入邮箱（用于 SSL 证书）: " EMAIL

    # 执行部署步骤
    install_dependencies
    setup_mysql
    create_directories
    deploy_backend
    deploy_frontend

    if [[ -n "${DOMAIN}" ]]; then
        setup_ssl
    fi

    show_info
}

# 执行主函数
main "$@"
