DROP DATABASE IF EXISTS test;

CREATE DATABASE test;

USE test;

CREATE TABLE `user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(100) NOT NULL,
    `password` varchar(500) NOT NULL,
    `first_name` varchar(100) DEFAULT NULL,
    `last_name` varchar(100) DEFAULT NULL,
    `email` varchar(300) NOT NULL,
    `gender` int(11) NOT NULL,
    `status` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
    `id` int(11) NOT NULL,
    `role_name` varchar(50) NOT NULL,
    `role_description` varchar(200) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
     `user_id` int(11) NOT NULL,
     `role_id` int(11) NOT NULL,
     PRIMARY KEY (`user_id`,`role_id`),
     KEY `fk_user_role_role` (`role_id`),
     CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
     CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP VIEW IF EXISTS `user_view`;

CREATE VIEW `user_view` as
SELECT u.*, r.role_name FROM `user` u
 JOIN `user_role` u_role ON u.id = u_role.user_id
 JOIN `role` r  ON r.id = u_role.role_id;
