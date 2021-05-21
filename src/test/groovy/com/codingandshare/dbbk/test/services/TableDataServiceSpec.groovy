package com.codingandshare.dbbk.test.services


import com.codingandshare.dbbk.exceptions.DBBackupException
import com.codingandshare.dbbk.services.impl.TableDataServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Integration test for {@link TableDataServiceImpl}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class TableDataServiceSpec extends Specification {
  private static final String DB_NAME = 'test'

  @Autowired
  private TableDataServiceImpl tableDataService

  def 'Verify clean up data file backup'() {
    given: 'Setup data file existing content'
    new File('/tmp/test.sql').write('this is content')

    when: 'clean up data file backup'
    FileWriter fileWriter = this.tableDataService.setupFileBackup(DB_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    fileWriter
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.getText().isEmpty()

    cleanup:
    file.delete()
  }

  def 'Verify clean data file backup failed'() {
    given: 'Setup folder not found'
    this.tableDataService.storageFolder = '/nhan'

    when: 'Clean up data file backup'
    this.tableDataService.setupFileBackup(DB_NAME)

    then: 'throw exception as expcet'
    DBBackupException e = thrown(DBBackupException)
    e.message == "Clean up file /nhan/${DB_NAME}.sql failed"

    cleanup:
    this.tableDataService.storageFolder = '/tmp'
  }

  def 'Verify write script data backup successfully'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'write data backup to file'
    this.tableDataService.writeScriptDataBackup('user', fileWriter)
    fileWriter.close()

    then: 'result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.isFile()
    file.text == '''LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO user (id,username,password,first_name,last_name,email,gender,status) VALUES (1,'huunhancit','password','Dinh','Nhan','huunhancit@gmail.com',1,1),\n(2,'dhnhan','password','Dinh','Nhan','dhnhan@gmail.com',1,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
'''
    cleanup:
    file.delete()
  }

  def 'Verify write script data backup throw IOException'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')
    fileWriter.close()

    when: 'write data backup to file'
    this.tableDataService.writeScriptDataBackup('user', fileWriter)

    then: 'Throw IOException as expect'
    thrown(IOException)

    cleanup:
    new File('/tmp/test.sql').delete()
  }

  def 'Verify write script create table to file successfully'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'Write script create table to file'
    this.tableDataService.writeScriptCreateTable('user', fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.isFile()
    file.text == '''-- Script create table user
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
'''

    cleanup:
    file.delete()
  }

  def 'Verify write script header to file'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'Write script header to file'
    this.tableDataService.writeScriptBackupHeader('test', fileWriter)

    then: 'result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.isFile()
    file.text.contains('''-- Database: test
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
-- ------------------------------------------------------''')

    cleanup:
    file.delete()
  }
}
