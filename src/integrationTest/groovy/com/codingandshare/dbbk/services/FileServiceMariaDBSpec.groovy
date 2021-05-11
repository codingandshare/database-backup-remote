package com.codingandshare.dbbk.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Integration test for {@link FileService}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class FileServiceMariaDBSpec extends Specification {

  @Autowired
  private FileService fileService

  def 'Check folder storage as expect'() {
    when:
    String storageFolder = this.fileService.storageFolder

    then: 'Folder storage as expect'
    noExceptionThrown()
    storageFolder == '/tmp'
  }

  def 'Verify build name table function'() {
    given: 'Setup data'
    String tableName = 'user';

    when: 'Build file name table'
    String pathTableName = this.fileService.buildFileNameTable(tableName)

    then: 'Result as expect'
    noExceptionThrown()
    pathTableName == "/tmp/cas_backup/${tableName}.sql"
    new File('/tmp/cas_backup').exists()

    cleanup:
    new File('/tmp/cas_backup').deleteDir()
  }

  def 'Verify build script create table'() {
    given: 'Setup data'
    String tableName = 'user'

    when: 'Build script create table'
    String script = this.fileService.buildSqlScriptCreateTable(tableName)

    then: 'Result as expect'
    noExceptionThrown()
    script == '''-- Script create table user
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
'''

    cleanup:
    new File('/tmp/cas_backup').deleteDir()
  }

  def 'Verify data file for table when sql inserts is null or empty'() {
    given: 'Setup data'
    String tableName = 'user'

    when: 'Store data file table'
    this.fileService.storeDataFileTable(tableName, sqlInserts)

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/cas_backup/user.sql')
    file.exists()
    file.text == '''-- Script create table user
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
'''

    cleanup:
    new File('/tmp/cas_backup').deleteDir()

    where:
    sqlInserts | _
    []         | _
    null       | _
  }

  def 'Verify data file for table when sqlInserts is not empty'() {
    given: 'Setup data'
    String tableName = 'user'
    List<String> sqlInserts = [
        "INSERT INTO user (username, password, first_name,email) VALUES ('ndinh', 'abc123', 'Nhan', 'huunhancit@gmail.com')",
        "INSERT INTO user (username, password, first_name,email) VALUES ('dhnhan', 'abc123', 'Nhan', 'nhan@gmail.com')"
    ]

    when: 'Store data file table'
    this.fileService.storeDataFileTable(tableName, sqlInserts)

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/cas_backup/user.sql')
    file.exists()
    file.text == '''-- Script create table user
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- Script insert data
INSERT INTO user (username, password, first_name,email) VALUES ('ndinh', 'abc123', 'Nhan', 'huunhancit@gmail.com');
INSERT INTO user (username, password, first_name,email) VALUES ('dhnhan', 'abc123', 'Nhan', 'nhan@gmail.com');
'''

    cleanup:
    new File('/tmp/cas_backup').deleteDir()
  }
}
