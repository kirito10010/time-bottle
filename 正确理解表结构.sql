-- 注册表
CREATE TABLE `users` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID，自增主键',
    `username` VARCHAR(50) NOT NULL COMMENT '登录账号，唯一',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值（使用bcrypt/Argon2等强哈希算法加密）',
    `email` VARCHAR(100) NOT NULL COMMENT '电子邮箱，唯一，可用于登录、找回密码、通知',
    `role` ENUM('0', '1', '2') NOT NULL DEFAULT '0' COMMENT '用户角色：0-普通用户，1-VIP用户，2-管理员',
    `points` INT NOT NULL DEFAULT '0' COMMENT '用户积分，可累计或消费',
    `status` ENUM('0', '1') NOT NULL DEFAULT '1' COMMENT '账号状态：0-禁用，1-正常',
    `avatar` VARCHAR(255) DEFAULT 'default-avatar.svg' COMMENT '头像文件名，存储仅文件名，完整路径由应用配置拼接',
    `nickname` VARCHAR(50) DEFAULT '未命名' COMMENT '昵称，显示用，默认“未命名”',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间，可用于统计活跃度',
    `created_at` DATETIME NOT NULL COMMENT '注册时间',
    `updated_at` DATETIME NOT NULL COMMENT '用户信息最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引',
    UNIQUE KEY `uniq_email` (`email`) COMMENT '邮箱唯一索引',
    KEY `idx_status` (`status`) COMMENT '状态索引',
    KEY `idx_role` (`role`) COMMENT '角色索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拾光瓶网站用户基本信息、权限及积分表';



-- 积分记录表
CREATE TABLE `points_log` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `change` INT NOT NULL COMMENT '积分变动值（正数为获得，负数为消耗）',
    `type` VARCHAR(20) NOT NULL COMMENT '变动类型：sign_in-签到奖励，task-任务奖励，accounting-记账奖励，draw-抽卡扣除；可扩展：exchange-兑换消耗，expire-积分过期，admin-管理员调整，invite-邀请奖励，activity-活动奖励等',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注，可记录具体任务ID、签到天数、关联订单等',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';



-- 密码重置验证码表
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    token VARCHAR(10) NOT NULL COMMENT '验证码',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已使用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_email (email) COMMENT '邮箱索引',
    KEY idx_token (token) COMMENT '验证码索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密码重置验证码表';



-- 收支分类表（bill_categories）
CREATE TABLE `bill_categories` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '所属用户ID（关联users表，0=系统默认分类）',
    `name` VARCHAR(30) NOT NULL COMMENT '分类名称（如餐饮、工资、房租）',
    `type` ENUM('0', '1') NOT NULL COMMENT '分类类型：0=支出，1=收入',
    `is_default` ENUM('0', '1') DEFAULT '0' COMMENT '是否系统默认：0=用户自定义，1=系统默认',
    `sort` TINYINT UNSIGNED DEFAULT 0 COMMENT '排序值（数字越小越靠前）',
    `is_deleted` ENUM('0', '1') DEFAULT '0' COMMENT '软删除：0=正常，1=删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_type` (`user_id`, `type`) COMMENT '用户+类型索引，快速查用户的收入/支出分类'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账-收支分类表（固定分类）';

-- 账单记录表（bills）
CREATE TABLE `bills` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '账单ID（BIGINT避免数据溢出）',
    `user_id` INT UNSIGNED NOT NULL COMMENT '所属用户ID（关联users表）',
    `category_id` INT UNSIGNED NOT NULL COMMENT '分类ID（关联bill_categories表）',
    `type` ENUM('0', '1') NOT NULL COMMENT '收支类型：0=支出，1=收入',
    `account` ENUM('现金','微信','支付宝','银行卡','其他') NOT NULL DEFAULT '现金' COMMENT '支付/收款账户（枚举固定选项）',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额（保留2位小数）',
    `remark` VARCHAR(150) DEFAULT '' COMMENT '账单备注（如“午餐-肯德基”）',
    `bill_date` DATE NOT NULL COMMENT '账单发生日期（核心筛选字段）',
    `bill_time` TIME DEFAULT NULL COMMENT '可选：账单发生时间（精确到时分）',
    `is_deleted` ENUM('0', '1') DEFAULT '0' COMMENT '软删除：0=正常，1=删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_date` (`user_id`, `bill_date`) COMMENT '用户+日期索引，快速按时间查账单',
    KEY `idx_category_id` (`category_id`) COMMENT '分类索引，快速按分类统计',
    KEY `idx_account` (`account`) COMMENT '账户索引，快速按账户统计'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账-账单记录表';



-- 生产项目配置表（每个用户独立维护）
CREATE TABLE production_project_config (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    uid VARCHAR(50) NOT NULL COMMENT '用户ID（登录用户唯一标识，用于数据隔离）',
    project_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    operation_quota DECIMAL(10,2) NOT NULL COMMENT '作业定额（例如：单位工作量/人天）',
    quality_quota DECIMAL(10,2) NOT NULL COMMENT '质检定额（例如：单位工作量/人天）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_uid (uid) COMMENT '按用户查询项目的索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产项目配置表（用户级）';

-- 每日绩效记录表（记录用户每天的工作绩效）
CREATE TABLE daily_performance (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    uid VARCHAR(50) NOT NULL COMMENT '用户ID（登录用户唯一标识）',
    record_date DATE NOT NULL COMMENT '记录日期（年月日）',
    project_id BIGINT NOT NULL COMMENT '关联生产项目ID（production_project_config.id）',
    process_type ENUM('作业', '质检') NOT NULL COMMENT '工序类型（作业或质检）',
    quota_efficiency DECIMAL(10,2) NOT NULL COMMENT '额定效率（记录当时的定额值，从配置表对应工序类型复制）',
    actual_workload DECIMAL(10,2) NOT NULL COMMENT '实际工作量（单位与定额匹配）',
    performance_man_days DECIMAL(10,5) GENERATED ALWAYS AS (actual_workload / quota_efficiency) STORED COMMENT '绩效人天合计（实际工作量 / 额定效率，存储计算列）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_uid_record_date (uid, record_date) COMMENT '按用户和日期查询的索引',
    KEY idx_project_id (project_id) COMMENT '按项目查询的索引',
    CONSTRAINT fk_daily_performance_project FOREIGN KEY (project_id) REFERENCES production_project_config (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日绩效记录表';

-- 加班记录表（记录用户每日加班情况）
CREATE TABLE overtime_record (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    uid VARCHAR(50) NOT NULL COMMENT '用户ID（登录用户唯一标识）',
    record_date DATE NOT NULL COMMENT '加班日期（年月日）',
    overtime_hours DECIMAL(5,2) NOT NULL COMMENT '加班小时数（支持小数，如2.5表示2.5小时）',
    project_id BIGINT NOT NULL COMMENT '关联生产项目ID（production_project_config.id）',
    description VARCHAR(500) DEFAULT NULL COMMENT '加班描述（可填写具体原因或备注）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_uid_record_date (uid, record_date) COMMENT '按用户和日期查询的索引',
    KEY idx_project_id (project_id) COMMENT '按项目查询的索引',
    CONSTRAINT fk_overtime_record_project FOREIGN KEY (project_id) REFERENCES production_project_config (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='加班记录表';



-- 月度薪资记录表（记录用户每月的薪资明细）
CREATE TABLE monthly_salary_record (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    uid VARCHAR(50) NOT NULL COMMENT '用户ID（登录用户唯一标识）',
    month CHAR(7) NOT NULL COMMENT '月份（格式：YYYY-MM，如2026-03）',
    period_start_date DATE NOT NULL COMMENT '周期开始日期',
    period_end_date DATE NOT NULL COMMENT '周期结束日期',
    attendance_days DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '出勤天数（支持小数，如0.5表示半天）',
    basic_salary DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '基本薪资（保留五位小数）',
    performance_coefficient DECIMAL(7,5) NOT NULL DEFAULT 1.00000 COMMENT '绩效总和（保留五位小数）',
    performance_salary DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '绩效薪资（保留五位小数）',
    position_performance DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '岗位绩效（保留五位小数）',
    meal_allowance DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '餐补（保留五位小数）',
    housing_allowance DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '房补（保留五位小数）',
    full_attendance_bonus DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '全勤奖（保留五位小数）',
    other_bonus DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '其他奖金（保留五位小数）',
    pension_insurance DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '养老保险（个人扣款，保留五位小数）',
    medical_insurance DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '医疗保险（个人扣款，保留五位小数）',
    unemployment_insurance DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '失业保险（个人扣款，保留五位小数）',
    late_deduction DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '迟到扣款（保留五位小数）',
    overtime_hours DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '加班时长（小时）',
    overtime_salary DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '加班薪资（保留五位小数）',
    total_payable DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '应发总额（保留五位小数）',
    total_deduction DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '扣除总额（保留五位小数）',
    net_salary DECIMAL(12,5) NOT NULL DEFAULT 0.00000 COMMENT '实发薪资（保留五位小数）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_uid_month (uid, month) COMMENT '确保每月每人一条记录',
    KEY idx_uid (uid) COMMENT '按用户查询的索引',
    KEY idx_month (month) COMMENT '按月份查询的索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='月度薪资记录表';



-- 图鉴表
CREATE TABLE anime_card (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '卡片ID，主键',
    series_name VARCHAR(100) NOT NULL COMMENT '作品系列名称（如：火影忍者、海贼王等）',
    name VARCHAR(100) NOT NULL COMMENT '卡片名称',
    type VARCHAR(50) NOT NULL COMMENT '卡片类型（如：SSR、SR、R等，可根据需要自定义）',
    rarity_level TINYINT NOT NULL CHECK (rarity_level BETWEEN 1 AND 5) COMMENT '稀有度级别：1-普通，2-稀有，3-超稀有，4-史诗，5-传说；对应抽卡概率：40%、30%、15%、10%、5%',
    image_url VARCHAR(500) NOT NULL COMMENT '卡片图片URL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引：按系列筛选
    INDEX idx_series (series_name),
    -- 索引：按稀有度筛选
    INDEX idx_rarity (rarity_level)
) COMMENT='动漫图鉴卡片总表';

-- 用户图鉴表
CREATE TABLE user_card (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID，主键',
    uid BIGINT NOT NULL COMMENT '用户ID，关联用户表',
    card_id BIGINT NOT NULL COMMENT '卡片ID，关联anime_card表',
    quantity INT NOT NULL DEFAULT 1 COMMENT '拥有数量，默认为1',
    obtained_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次获得时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY uk_uid_card (uid, card_id) COMMENT '同一用户每种卡片仅有一条记录，通过quantity表示数量'
) COMMENT='用户卡片收集表';



-- 寄售表（商城表）
CREATE TABLE `consignments` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '寄售记录ID，主键',
    `seller_id` INT UNSIGNED NOT NULL COMMENT '卖家用户ID，关联users.id',
    `card_id` BIGINT NOT NULL COMMENT '卡片ID，关联anime_card.id',
    `unit_price` INT NOT NULL COMMENT '单价（消耗积分），必须为正整数',
    `quantity` INT UNSIGNED NOT NULL COMMENT '当前可售数量，每次购买后递减，减至0时删除记录',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_seller` (`seller_id`),
    KEY `idx_card` (`card_id`),
    KEY `idx_price` (`unit_price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌寄售商城表';



-- 题目表
CREATE TABLE `questions` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '题目ID，自增主键',
    `question_text` TEXT NOT NULL COMMENT '题目内容，支持长文本',
    `option_a` VARCHAR(255) NOT NULL COMMENT '选项A，不超过255字符',
    `option_b` VARCHAR(255) NOT NULL COMMENT '选项B，不超过255字符',
    `option_c` VARCHAR(255) NOT NULL COMMENT '选项C，不超过255字符',
    `option_d` VARCHAR(255) NOT NULL COMMENT '选项D，不超过255字符',
    `correct_option` CHAR(1) NOT NULL COMMENT '正确答案，取值：A、B、C、D（大写字母）',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，记录题目添加时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_at` (`created_at`) COMMENT '创建时间索引，用于按时间排序或筛选'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选择题题库表，存储题目、四个选项及正确答案';



-- 卡牌赠送记录表
CREATE TABLE `card_gifts` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '赠送记录ID',
    `sender_id` INT UNSIGNED NOT NULL COMMENT '赠送者用户ID',
    `receiver_id` INT UNSIGNED NOT NULL COMMENT '接收者用户ID',
    `card_id` BIGINT NOT NULL COMMENT '卡片ID',
    `quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '赠送数量',
    `status` ENUM('pending', 'completed', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，completed-已完成，rejected-已拒绝',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender` (`sender_id`),
    KEY `idx_receiver` (`receiver_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌赠送记录表';

-- 卡牌交换申请表
CREATE TABLE `card_exchanges` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交换申请ID',
    `sender_id` INT UNSIGNED NOT NULL COMMENT '发起者用户ID',
    `receiver_id` INT UNSIGNED NOT NULL COMMENT '接收者用户ID',
    `sender_card_id` BIGINT NOT NULL COMMENT '发起者提供的卡片ID',
    `sender_card_quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '发起者提供的卡片数量',
    `receiver_card_id` BIGINT NOT NULL COMMENT '接收者提供的卡片ID',
    `receiver_card_quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '接收者提供的卡片数量',
    `status` ENUM('pending', 'completed', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，completed-已完成，rejected-已拒绝',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender` (`sender_id`),
    KEY `idx_receiver` (`receiver_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌交换申请表';