-- 为 users 表新增身份证号字段
ALTER TABLE `users`
    ADD COLUMN `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号' AFTER `phone_number`;
