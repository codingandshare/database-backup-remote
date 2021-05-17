package com.codingandshare.dbbk.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.TransientDataAccessResourceException
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Integration test for TableMetaDataRepository
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class TableMetaDataRepositoryMariaDBSpec extends Specification {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository

  private static final String DATABASE_NAME = 'test'

  private static final String PREFIX_TABLE_META = 'test_prefix'

  void setupSpec() {
    System.setProperty('PREFIX_TABLE_META', PREFIX_TABLE_META)
  }

  def 'Verify get all tables name'() {
    when: 'get all tables'
    List<String> tables = this.tableMetaDataRepository.getAllTables(DATABASE_NAME)

    then: 'Result list tables as expect'
    noExceptionThrown()
    tables == ['role', 'user', 'user_role']
  }

  def 'Verify get all views'() {
    when: 'get all views'
    List<String> views = this.tableMetaDataRepository.getAllViews(DATABASE_NAME)

    then: 'Result list views as expect'
    noExceptionThrown()
    views == ['user_view']
  }

  def 'Verify get all triggers'() {
    when: 'get all triggers'
    List<String> triggers = this.tableMetaDataRepository.getAllTriggers(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    triggers == ['before_role_delete']
  }

  def 'Verify get database name'() {
    when: 'get database name'
    String name = this.tableMetaDataRepository.databaseName

    then: 'Result database name as expect'
    noExceptionThrown()
    name == DATABASE_NAME
  }

  def 'Verify get all functions'() {
    when: 'get all functions'
    List<String> functions = this.tableMetaDataRepository.getAllSqlFunctions(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    functions == ['getUserName_Func']
  }

  def 'Verify get all procedures'() {
    when: 'get all procedures'
    List<String> procedures = this.tableMetaDataRepository.getAllProcedures(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    procedures == ['GetUserName']
  }

  def 'Verify generate script create table'() {
    when: 'generate script create table'
    String sqlCreateTable = this.tableMetaDataRepository.generateScriptCreateTable('user')

    then: 'Result as expect'
    noExceptionThrown()
    sqlCreateTable == '''-- Script create table user
DROP TABLE IF EXISTS `user`
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;\n'''
  }

  def 'Verify generate script create table when table not existing'() {
    when: 'generate script create table'
    this.tableMetaDataRepository.generateScriptCreateTable('table_not_found')

    then: 'Result as expect'
    thrown(BadSqlGrammarException)
  }

  def 'Verify generate script create view when view not existing'() {
    when: 'generate script create view'
    this.tableMetaDataRepository.generateScriptCreateView('user')

    then: 'Result as expect'
    thrown(TransientDataAccessResourceException)
  }

  def 'Verify generate script create view successfully'() {
    when: 'generate script create view'
    String sqlCreateView = this.tableMetaDataRepository.generateScriptCreateView('user_view')

    then: 'Result as expect'
    noExceptionThrown()
    sqlCreateView == '''CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `user_view` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`password` AS `password`,`u`.`first_name` AS `first_name`,`u`.`last_name` AS `last_name`,`u`.`email` AS `email`,`u`.`gender` AS `gender`,`u`.`status` AS `status`,`r`.`role_name` AS `role_name` from ((`user` `u` join `user_role` `u_role` on(`u`.`id` = `u_role`.`user_id`)) join `role` `r` on(`r`.`id` = `u_role`.`role_id`))'''
  }

  def 'Verify generate script create trigger when trigger not existing'() {
    when: 'generate script create trigger'
    this.tableMetaDataRepository.generateScriptCreateTrigger('trigger_a')

    then: 'Result as expect'
    thrown(TransientDataAccessResourceException)
  }

  def 'Verify generate script create trigger successfully'(){
    when: 'generate script create trigger'
    String scriptTrigger = this.tableMetaDataRepository.generateScriptCreateTrigger('before_role_delete')

    then: 'Result as expect'
    noExceptionThrown()
    scriptTrigger == 'CREATE DEFINER=`root`@`%` TRIGGER before_role_delete BEFORE DELETE ON role FOR EACH ROW DELETE FROM user_role WHERE role_id = OLD.id'
  }

  def 'Verify generate script create procedure when procedure not existing'() {
    when: 'Generate script create procedure'
    this.tableMetaDataRepository.generateScriptCreateProcedure('procedure_not_found')

    then: 'Result as expect'
    thrown(BadSqlGrammarException)
  }

  def 'Verify generate script create procedure successfully'() {
    when: 'Generate script create procedure'
    String scriptProcedure = this.tableMetaDataRepository.generateScriptCreateProcedure('GetUserName')

    then: 'Result as expect'
    noExceptionThrown()
    scriptProcedure == '''CREATE DEFINER=`root`@`%` PROCEDURE `GetUserName`( OUT userName VARCHAR(20) )
BEGIN
    SET userName = 'Nhan Dinh';
END'''
  }

  def 'Verify generate script create function when function name not existing'() {
    when: 'Generate script create function'
    this.tableMetaDataRepository.generateScriptCreateFunction('function_not_found')

    then: 'Result as expect'
    thrown(BadSqlGrammarException)
  }

  def 'Verify generate script create function successfully'() {
    when: 'Generate script create function'
    String scriptFunction = this.tableMetaDataRepository.generateScriptCreateFunction('getUserName_Func')

    then: 'Result as expect'
    noExceptionThrown()
    scriptFunction == '''CREATE DEFINER=`root`@`%` FUNCTION `getUserName_Func`() RETURNS varchar(20) CHARSET latin1
    DETERMINISTIC
BEGIN
    DECLARE userName VARCHAR(20);
    SET userName = 'Nhan Dinh';
    RETURN (userName);
END'''
  }
}
