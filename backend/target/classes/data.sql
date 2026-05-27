-- 插入默认管理员账号 (密码: admin123)
INSERT IGNORE INTO user (username, password, nickname, phone, points, status, role) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13800138000', 1000, 1, 'admin');

-- 插入测试用户 (密码: 123456)
INSERT IGNORE INTO user (username, password, nickname, phone, points, status, role) VALUES
('test1', 'e10adc3949ba59abbe56e057f20f883e', '测试用户1', '13800138001', 100, 1, 'user'),
('test2', 'e10adc3949ba59abbe56e057f20f883e', '测试用户2', '13800138002', 100, 1, 'user');
