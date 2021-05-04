package com.codingandshare.dbbk.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.TransientDataAccessResourceException
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Integration test for TableMetaDataRepository
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class TableMetaDataRepositorySpec extends Specification {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository

  private static final String DATABASE_NAME = 'test'

  void 'Verify get all tables name'() {
    when: 'get all tables'
    List<String> tables = this.tableMetaDataRepository.getAllTables(DATABASE_NAME)

    then: 'Result list tables as expect'
    noExceptionThrown()
    tables == ['role', 'user', 'user_role']
  }

  void 'Verify get all views'() {
    when: 'get all views'
    List<String> views = this.tableMetaDataRepository.getAllViews(DATABASE_NAME)

    then: 'Result list views as expect'
    noExceptionThrown()
    views == ['user_view']
  }

  void 'Verify get all triggers'() {
    when: 'get all triggers'
    List<String> triggers = this.tableMetaDataRepository.getAllTriggers(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    triggers == ['before_role_delete']
  }

  void 'Verify get database name'() {
    when: 'get database name'
    String name = this.tableMetaDataRepository.databaseName

    then: 'Result database name as expect'
    noExceptionThrown()
    name == DATABASE_NAME
  }

  void 'Verify get all functions'() {
    when: 'get all functions'
    List<String> functions = this.tableMetaDataRepository.getAllSqlFunctions(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    functions == ['getUserName_Func']
  }

  void 'Verify get all procedures'() {
    when: 'get all procedures'
    List<String> procedures = this.tableMetaDataRepository.getAllProcedures(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    procedures == ['GetUserName']
  }

  void 'Verify generate script create table'() {
    when: 'generate script create table'
    String sqlCreateTable = this.tableMetaDataRepository.generateScriptCreateTable('user')

    then: 'Result as expect'
    noExceptionThrown()
    sqlCreateTable == '''CREATE TABLE `user` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4'''
  }

  void 'Verify generate script create view when view not existing'() {
    when: 'generate script create view'
    this.tableMetaDataRepository.generateScriptCreateView('user')

    then: 'Result as expect'
    thrown(TransientDataAccessResourceException)
  }

  void 'Verify generate script create view successfully'() {
    when: 'generate script create view'
    String sqlCreateView = this.tableMetaDataRepository.generateScriptCreateView('user_view')

    then: 'Result as expect'
    noExceptionThrown()
    sqlCreateView == '''CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `user_view` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`password` AS `password`,`u`.`first_name` AS `first_name`,`u`.`last_name` AS `last_name`,`u`.`email` AS `email`,`u`.`gender` AS `gender`,`u`.`status` AS `status`,`r`.`role_name` AS `role_name` from ((`user` `u` join `user_role` `u_role` on(`u`.`id` = `u_role`.`user_id`)) join `role` `r` on(`r`.`id` = `u_role`.`role_id`))'''
  }

}
