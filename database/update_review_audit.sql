-- ======================================================
-- 评价审核机制迁移脚本
-- 说明：
--   audit_status: 0=待审核(扣分评价需管理员审核), 1=已通过/直接生效, 2=已拒绝
--   audit_remark: 管理员拒绝时的备注原因
--   images 字段已存在，存储 JSON 数组格式的图片 URL 列表
-- ======================================================

USE es;

-- 新增审核状态字段（默认 1=直接生效，兼容历史数据）
ALTER TABLE reviews
    ADD COLUMN IF NOT EXISTS audit_status  TINYINT  NOT NULL DEFAULT 1
        COMMENT '审核状态: 0=待审核, 1=已通过, 2=已拒绝',
    ADD COLUMN IF NOT EXISTS audit_remark  VARCHAR(255) NULL
        COMMENT '管理员审核备注（拒绝原因）';

-- 为审核状态加索引，管理员查询待审核列表时走索引
CREATE INDEX IF NOT EXISTS idx_reviews_audit_status
    ON reviews (audit_status, created_at);
