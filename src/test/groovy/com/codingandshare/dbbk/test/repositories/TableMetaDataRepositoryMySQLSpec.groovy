package com.codingandshare.dbbk.test.repositories

import com.codingandshare.dbbk.repositories.TableMetaDataRepository
import com.codingandshare.dbbk.utils.AppUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.TransientDataAccessResourceException
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowCallbackHandler
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.sql.ResultSet
import java.sql.SQLException

/**
 * Integration test for TableMetaDataRepository
 */
@SpringBootTest
@ActiveProfiles('mysql-test')
class TableMetaDataRepositoryMySQLSpec extends Specification {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository

  @Autowired
  private JdbcTemplate jdbcTemplate

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
    tables == ['role', 'test_data_binary', 'test_data_number', 'test_table', 'user', 'user_role']
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
    thrown(UncategorizedSQLException)
  }

  def 'Verify generate script create view successfully'() {
    when: 'generate script create view'
    String sqlCreateView = this.tableMetaDataRepository.generateScriptCreateView('user_view')

    then: 'Result as expect'
    noExceptionThrown()
    sqlCreateView == '''CREATE ALGORITHM=UNDEFINED DEFINER=`test`@`%` SQL SECURITY DEFINER VIEW `user_view` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`password` AS `password`,`u`.`first_name` AS `first_name`,`u`.`last_name` AS `last_name`,`u`.`email` AS `email`,`u`.`gender` AS `gender`,`u`.`status` AS `status`,`r`.`role_name` AS `role_name` from ((`user` `u` join `user_role` `u_role` on((`u`.`id` = `u_role`.`user_id`))) join `role` `r` on((`r`.`id` = `u_role`.`role_id`)))'''
  }

  def 'Verify generate script create trigger when trigger not existing'() {
    when: 'generate script create trigger'
    this.tableMetaDataRepository.generateScriptCreateTrigger('trigger_a')

    then: 'Result as expect'
    thrown(UncategorizedSQLException)
  }

  def 'Verify generate script create trigger successfully'() {
    when: 'generate script create trigger'
    String scriptTrigger = this.tableMetaDataRepository.generateScriptCreateTrigger('before_role_delete')

    then: 'Result as expect'
    noExceptionThrown()
    scriptTrigger == 'CREATE DEFINER=`test`@`%` TRIGGER before_role_delete BEFORE DELETE ON role FOR EACH ROW DELETE FROM user_role WHERE role_id = OLD.id'
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
    scriptProcedure == '''CREATE DEFINER=`test`@`%` PROCEDURE `GetUserName`( OUT userName VARCHAR (20) )
BEGIN
    SET
userName = 'Nhan Dinh';
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
    scriptFunction == '''CREATE DEFINER=`test`@`%` FUNCTION `getUserName_Func`() RETURNS varchar(20) CHARSET latin1
    DETERMINISTIC
BEGIN
    DECLARE
userName VARCHAR(20);
    SET
userName = 'Nhan Dinh';
RETURN (userName);
END'''
  }

  def 'Verify get list values for insert success'() {
    when: 'get list values insert'
    List<List<String>> values = new ArrayList<>();
    this.jdbcTemplate.query("SELECT * FROM `user`", new RowCallbackHandler() {
      @Override
      void processRow(ResultSet rs) throws SQLException {
        values.add(tableMetaDataRepository.getValueInsertFromResultSet(rs))
      }
    })

    then: 'Result as expect'
    noExceptionThrown()
    values
    values.size() == 2
    String.join(',', values.first()) == "1,'huunhancit','password','Dinh','Nhan','huunhancit@gmail.com',1,1"
    String.join(',', values[1]) == "2,'dhnhan','password','Dinh','Nhan','dhnhan@gmail.com',1,1"
  }

  def 'Verify get list values for insert with date, datetime, time'() {
    when: 'get list values insert'
    List<List<String>> values = new ArrayList<>();
    this.jdbcTemplate.query("SELECT * FROM `test_table`", new RowCallbackHandler() {
      @Override
      void processRow(ResultSet rs) throws SQLException {
        values.add(tableMetaDataRepository.getValueInsertFromResultSet(rs))
      }
    })

    then: 'Result as expect'
    noExceptionThrown()
    values
    values.size() == 1
    String.join(',', values.first()) == "'2020-10-19 03:10:00','03:10:00','2020-10-19'"
  }

  def 'Verify generate script drop table'() {
    when: 'generate script drop table'
    String scriptDropTable = this.tableMetaDataRepository.generateSqlDropTable('user')

    then: 'result as expect'
    noExceptionThrown()
    scriptDropTable == "DROP TABLE IF EXISTS `user`"
  }

  def 'Verify generate script header for backup'() {
    when: 'generate script header'
    String header = this.tableMetaDataRepository.generateScriptBackupHeader('test')

    then: 'Result as expect'
    noExceptionThrown()
    header.contains('''-- Database: test
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
-- ------------------------------------------------------\n\n''')
  }

  def 'Verify generate script unlock table'() {
    when: 'generate script unlock table'
    String scriptUnlockTable = this.tableMetaDataRepository.generateScriptUnLockTable('user')

    then: 'Result as expect'
    noExceptionThrown()
    scriptUnlockTable == 'UNLOCK TABLES;'
  }

  def 'Verify generate script lock table'() {
    when: 'Generate script lock table'
    String scriptLockTable = this.tableMetaDataRepository.generateScriptLockTable('nhan_table')

    then: 'Result as expect'
    noExceptionThrown()
    scriptLockTable == 'LOCK TABLES `nhan_table` WRITE;'
  }

  def 'Verify generate script disable FK'() {
    when: 'Generate script disable FK'
    String scriptDisableFK = this.tableMetaDataRepository.generateSqlDisableFkKey('users')

    then: 'Result as expect'
    noExceptionThrown()
    scriptDisableFK == '/*!40000 ALTER TABLE `users` DISABLE KEYS */;'
  }

  def 'Verify generate script enable FK'() {
    when: 'Generate script enable FK'
    String scriptEnableFk = this.tableMetaDataRepository.generateSqlEnableFkKey('roles')

    then: 'Result as expect'
    noExceptionThrown()
    scriptEnableFk == '/*!40000 ALTER TABLE `roles` ENABLE KEYS */;'
  }

  def 'Verify generate get list values for insert when table contain column number'() {
    when: 'get list values for insert'
    List<List<String>> values = new ArrayList<>();
    this.jdbcTemplate.query("SELECT * FROM `test_data_number`", new RowCallbackHandler() {
      @Override
      void processRow(ResultSet rs) throws SQLException {
        values.add(tableMetaDataRepository.getValueInsertFromResultSet(rs))
      }
    })

    then: 'Result as expect'
    noExceptionThrown()
    values
    values.size() == 3
    String.join(',', values.first()) == '1.0,1.00,NULL,1.0,1'
    String.join(',', values[1]) == "NULL,1.00,'Nhan Dinh',1.0,1"
    String.join(',', values[2]) == '1.0,NULL,NULL,1.0,1'
  }

  def 'Verify gender list values insert when table contain column BLOB'() {
    when: 'get list values for insert'
    List<List<String>> values = new ArrayList<>();
    this.jdbcTemplate.query("SELECT * FROM `test_data_binary`", new RowCallbackHandler() {
      @Override
      void processRow(ResultSet rs) throws SQLException {
        values.add(tableMetaDataRepository.getValueInsertFromResultSet(rs))
      }
    })

    then: 'Result as expect'
    noExceptionThrown()
    values
    values.size() == 2
    String.join(',', values.first()) == 'NULL'
    String.join(',', values.last()) == "'STRING'"
  }

  def 'Verify get all columns from ResultSet'() {
    when: 'Get all columns'
    List<String> columns = new ArrayList<>();
    this.jdbcTemplate.query("SELECT * FROM `test_data_number`", new RowCallbackHandler() {
      @Override
      void processRow(ResultSet rs) throws SQLException {
        if (columns.isEmpty()) {
          columns = tableMetaDataRepository.getAllColumnFromResultSet(rs)
        }
      }
    })

    then: 'Result as expect'
    noExceptionThrown()
    !columns.isEmpty()
    columns.size() == 5
    columns == ['c1', 'c2', 'c3', 'c4', 'c5']
  }

  def 'Verify generate script drop if exists for procedure'() {
    when: 'Generate script drop procedure'
    String scriptDropProcedure = this.tableMetaDataRepository.generateSqlDropIfExistsProcedure('test')

    then: 'Result as expect'
    noExceptionThrown()
    scriptDropProcedure == 'DROP PROCEDURE IF EXISTS `test`;'
  }

  def 'Verify generate script drop if exists for function'() {
    when: 'Generate script drop function'
    String scriptDropFunction = this.tableMetaDataRepository.generateSqlDropIfExistsFunction('getUserName_Func')

    then: 'Result as expect'
    noExceptionThrown()
    scriptDropFunction == 'DROP FUNCTION IF EXISTS `getUserName_Func`;'
  }

  def 'Verify generate script drop exists for trigger'() {
    when: 'Generate script drop trigger'
    String scriptDropTrigger = this.tableMetaDataRepository.generateSqlDropIfExistsTrigger('before_role_delete')

    then: 'Result as expect'
    noExceptionThrown()
    scriptDropTrigger == 'DROP TRIGGER IF EXISTS `before_role_delete`;'
  }

  def 'Verify generate script drop exists for view'() {
    when: 'Generate script drop view'
    String scriptDropView = this.tableMetaDataRepository.generateSqlDropIfExistsView('test')

    then: 'Result as expect'
    noExceptionThrown()
    scriptDropView == 'DROP VIEW IF EXISTS `test`;'
  }

  def 'Verify generate footer for backup script'() {
    when: 'Generate script footer'
    String footer = this.tableMetaDataRepository.generateScriptBackupFooter()

    then: 'Result as expect'
    noExceptionThrown()
    String formatDate = AppUtility.formatDate(new Date(), 'yyyy-MM-dd')
    footer == """
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
-- Backup completed: $formatDate"""
  }

  def 'Verify generate sql select table'() {
    when: 'Generate script select table'
    String select = this.tableMetaDataRepository.generateSelectTable('test')

    then: 'Result as expect'
    noExceptionThrown()
    select == 'SELECT * FROM `test`'
  }

  def 'Verify generate sql insert table'() {
    when: 'Generate script insert table'
    String insertSql = this.tableMetaDataRepository.generateInsertTable('test', ['c1', 'c2'])

    then: 'Result as expect'
    noExceptionThrown()
    insertSql == 'INSERT INTO `test` (`c1`,`c2`) VALUES '
  }
}
