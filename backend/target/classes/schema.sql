-- 资源共享与积分交易平台 数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS resource_sharing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE resource_sharing;

-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    avatar VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    introduction VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    points INT DEFAULT 100 COMMENT '积分余额',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: user-普通用户 admin-管理员 moderator-审核员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 资源表
DROP TABLE IF EXISTS resource;
CREATE TABLE resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
    title VARCHAR(200) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源描述',
    file_id BIGINT COMMENT '文件ID',
    file_name VARCHAR(200) COMMENT '原始文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    price INT DEFAULT 0 COMMENT '下载所需积分',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    uploader_name VARCHAR(50) COMMENT '上传者昵称',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 -1-已驳回 2-已下架',
    reject_reason VARCHAR(500) COMMENT '驳回原因',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_uploader (uploader_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FULLTEXT INDEX ft_title_desc (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';

-- 积分记录表
DROP TABLE IF EXISTS points_record;
CREATE TABLE points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    points INT NOT NULL COMMENT '积分变动(正数-增加 负数-减少)',
    type TINYINT NOT NULL COMMENT '类型: 1-收入 2-支出 3-奖励 4-惩罚',
    description VARCHAR(500) COMMENT '描述',
    related_id BIGINT COMMENT '关联ID(资源ID等)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 评论表
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID(回复)',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-删除 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_resource_id (resource_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_like_count (like_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 评论点赞表
DROP TABLE IF EXISTS comment_like;
CREATE TABLE comment_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    INDEX idx_comment_id (comment_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- 聊天会话表
DROP TABLE IF EXISTS chat_session;
CREATE TABLE chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user1_id BIGINT NOT NULL COMMENT '用户1ID',
    user2_id BIGINT NOT NULL COMMENT '用户2ID',
    user1_nickname VARCHAR(50) COMMENT '用户1昵称',
    user2_nickname VARCHAR(50) COMMENT '用户2昵称',
    last_message TEXT COMMENT '最后一条消息',
    last_time DATETIME COMMENT '最后消息时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user1 (user1_id),
    INDEX idx_user2 (user2_id),
    INDEX idx_last_time (last_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 聊天消息表
DROP TABLE IF EXISTS chat_message;
CREATE TABLE chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    sender_nickname VARCHAR(50) COMMENT '发送者昵称',
    receiver_id BIGINT COMMENT '接收者ID',
    receiver_nickname VARCHAR(50) COMMENT '接收者昵称',
    content TEXT COMMENT '消息内容',
    file_id BIGINT COMMENT '文件ID(文件消息)',
    type TINYINT DEFAULT 1 COMMENT '类型: 1-文本 2-文件',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已读 1-未读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 文件上传表
DROP TABLE IF EXISTS upload_file;
CREATE TABLE upload_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    file_name VARCHAR(200) NOT NULL COMMENT '存储文件名',
    original_name VARCHAR(200) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件类型',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    uploader_nickname VARCHAR(50) COMMENT '上传者昵称',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传表';

-- 下载记录表(用于积分幂等)
DROP TABLE IF EXISTS download_record;
CREATE TABLE download_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    points INT DEFAULT 0 COMMENT '消耗积分',
    is_first_download TINYINT DEFAULT 1 COMMENT '是否首次有效下载',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_resource_user (resource_id, user_id),
    INDEX idx_resource_id (resource_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='下载记录表';

-- 插入默认管理员账号 (密码: admin123)
INSERT IGNORE INTO user (username, password, nickname, phone, points, status, role) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '管理员', '13800138000', 1000, 1, 'admin');

-- 插入测试用户 (密码: 123456)
INSERT IGNORE INTO user (username, password, nickname, phone, points, status, role) VALUES
('test1', 'e10adc3949ba59abbe56e057f20f883e', '测试用户1', '13800138001', 100, 1, 'user'),
('test2', 'e10adc3949ba59abbe56e057f20f883e', '测试用户2', '13800138002', 100, 1, 'user');
