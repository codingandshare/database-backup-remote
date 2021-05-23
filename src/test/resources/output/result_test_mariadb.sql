-- Server version: 10.3.28-MariaDB-1:10.3.28+maria~focal
-- Database: test
-- ------------------------------------------------------
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
-- ------------------------------------------------------

-- Script create table role
DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `role_description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
-- SQL insert data role table 
LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


-- Script create table test_data_binary
DROP TABLE IF EXISTS `test_data_binary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_data_binary` (
  `c1` blob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
-- SQL insert data test_data_binary table 
LOCK TABLES `test_data_binary` WRITE;
/*!40000 ALTER TABLE `test_data_binary` DISABLE KEYS */;
INSERT INTO test_data_binary (c1) VALUES (NULL),
(535452494E47);
/*!40000 ALTER TABLE `test_data_binary` ENABLE KEYS */;
UNLOCK TABLES;


-- Script create table test_data_number
DROP TABLE IF EXISTS `test_data_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_data_number` (
  `c1` float DEFAULT NULL,
  `c2` decimal(10,2) DEFAULT NULL,
  `c3` varchar(100) DEFAULT NULL,
  `c4` double DEFAULT NULL,
  `c5` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
-- SQL insert data test_data_number table 
LOCK TABLES `test_data_number` WRITE;
/*!40000 ALTER TABLE `test_data_number` DISABLE KEYS */;
INSERT INTO test_data_number (c1,c2,c3,c4,c5) VALUES (1.0,1.00,NULL,1.0,1),
(NULL,1.00,'Nhan Dinh',1.0,1),
(1.0,NULL,NULL,1.0,1);
/*!40000 ALTER TABLE `test_data_number` ENABLE KEYS */;
UNLOCK TABLES;


-- Script create table test_table
DROP TABLE IF EXISTS `test_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_table` (
  `updated_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `s_time` time DEFAULT NULL,
  `s_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
-- SQL insert data test_table table 
LOCK TABLES `test_table` WRITE;
/*!40000 ALTER TABLE `test_table` DISABLE KEYS */;
INSERT INTO test_table (updated_time,s_time,s_date) VALUES ('2020-10-19 03:10:00','03:10:00','2020-10-19');
/*!40000 ALTER TABLE `test_table` ENABLE KEYS */;
UNLOCK TABLES;


-- Script create table user
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
-- SQL insert data user table 
LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO user (id,username,password,first_name,last_name,email,gender,status) VALUES (1,'huunhancit','password','Dinh','Nhan','huunhancit@gmail.com',1,1),
(2,'dhnhan','password','Dinh','Nhan','dhnhan@gmail.com',1,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


-- Script create table user_role
DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
-- SQL insert data user_role table 
LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;


-- Script create procedure

DROP PROCEDURE IF EXISTS `GetUserName`;
CREATE DEFINER=`root`@`%` PROCEDURE `GetUserName`( OUT userName VARCHAR (20) )
BEGIN
    SET
userName = 'Nhan Dinh';
END;

-- Script create functions

DROP FUNCTION IF EXISTS `getUserName_Func`;
CREATE DEFINER=`root`@`%` FUNCTION `getUserName_Func`() RETURNS varchar(20) CHARSET latin1
    DETERMINISTIC
BEGIN
    DECLARE
userName VARCHAR(20);
    SET
userName = 'Nhan Dinh';
RETURN (userName);
END;

-- Script create triggers

DROP TRIGGER IF EXISTS `before_role_delete`;
CREATE DEFINER=`root`@`%` TRIGGER before_role_delete BEFORE DELETE ON role FOR EACH ROW DELETE FROM user_role WHERE role_id = OLD.id;

-- Script create views

DROP VIEW IF EXISTS `user_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `user_view` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`password` AS `password`,`u`.`first_name` AS `first_name`,`u`.`last_name` AS `last_name`,`u`.`email` AS `email`,`u`.`gender` AS `gender`,`u`.`status` AS `status`,`r`.`role_name` AS `role_name` from ((`user` `u` join `user_role` `u_role` on(`u`.`id` = `u_role`.`user_id`)) join `role` `r` on(`r`.`id` = `u_role`.`role_id`));

