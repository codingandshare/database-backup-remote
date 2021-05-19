DROP
DATABASE IF EXISTS test;

CREATE
DATABASE test;

USE
test;

CREATE TABLE `user`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `username`   varchar(100) NOT NULL,
    `password`   varchar(500) NOT NULL,
    `first_name` varchar(100) DEFAULT NULL,
    `last_name`  varchar(100) DEFAULT NULL,
    `email`      varchar(300) NOT NULL,
    `gender`     int(11) NOT NULL,
    `status`     int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;

insert into `user` (username, password, first_name, last_name, email, gender, status)
values ('huunhancit', 'password', 'Dinh', 'Nhan', 'huunhancit@gmail.com', 1, 1);
insert into `user` (username, password, first_name, last_name, email, gender, status)
values ('dhnhan', 'password', 'Dinh', 'Nhan', 'dhnhan@gmail.com', 1, 1);

CREATE TABLE `role`
(
    `id`               int(11) NOT NULL,
    `role_name`        varchar(50)  NOT NULL,
    `role_description` varchar(200) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `test_table`
(
    updated_time TIMESTAMP,
    s_time       TIME,
    s_date       DATE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `test_table` (updated_time, s_time, s_date)
values ('2020-10-19 03:10:00', '03:10:00', '2020-10-19');

CREATE TABLE `user_role`
(
    `user_id` int(11) NOT NULL,
    `role_id` int(11) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    KEY       `fk_user_role_role` (`role_id`),
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP VIEW IF EXISTS `user_view`;

CREATE VIEW `user_view` as
SELECT u.*, r.role_name
FROM `user` u
         JOIN `user_role` u_role ON u.id = u_role.user_id
         JOIN `role` r ON r.id = u_role.role_id;

CREATE TRIGGER before_role_delete
    BEFORE DELETE
    ON role
    FOR EACH ROW DELETE
                 FROM user_role
                 WHERE role_id = OLD.id;

CREATE FUNCTION getUserName_Func()
    RETURNS VARCHAR(20)
    DETERMINISTIC
BEGIN
    DECLARE
userName VARCHAR(20);
    SET
userName = 'Nhan Dinh';
RETURN (userName);
END;

CREATE PROCEDURE GetUserName(
    OUT userName VARCHAR (20)
)
BEGIN
    SET
userName = 'Nhan Dinh';
END;
