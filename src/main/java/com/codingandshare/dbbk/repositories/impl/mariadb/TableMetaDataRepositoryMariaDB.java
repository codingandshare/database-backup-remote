package com.codingandshare.dbbk.repositories.impl.mariadb;

import com.codingandshare.dbbk.repositories.TableMetaDataAbstract;
import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import com.codingandshare.dbbk.utils.AppUtility;
import com.codingandshare.dbbk.utils.DBBackupConst;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class implement {@link TableMetaDataRepository} interface handle MariaDB.
 *
 * @author Nhan Dinh
 * @since 4/23/21
 **/
@Repository
@Profile({"mariadb", "mariadb-test"})
public class TableMetaDataRepositoryMariaDB extends TableMetaDataAbstract implements TableMetaDataRepository {

  /**
   * The method to get all tables in database.
   * Exclude all table meta of this service.
   *
   * @param databaseName database name.
   * @return List tables of a database.
   */
  public List<String> getAllTables(String databaseName) {
    String sql = GET_ALL_TABLES.replace(DB_NAME_EXPRESSION, databaseName);
    List<String> tables = this.queryAsListString(sql, "table_name");
    List<String> excludeTables = Arrays.stream(DBBackupConst.META_TABLES)
        .map(it -> String.format("%s%s", this.prefixTableMeta, it)).collect(Collectors.toList());
    tables.removeIf(excludeTables::contains);
    return tables;
  }

  /**
   * The method to get all views for backup.
   *
   * @param databaseName database name.
   * @return List views of a database.
   */
  public List<String> getAllViews(String databaseName) {
    String sql = SQL_GET_VIEWS.replace(DB_NAME_EXPRESSION, databaseName);
    return this.queryAsListString(sql, "table_name");
  }

  /**
   * Get all trigger names.
   *
   * @param databaseName
   * @return List trigger names
   */
  public List<String> getAllTriggers(String databaseName) {
    String sql = SQL_GET_TRIGGER.replace(DB_NAME_EXPRESSION, databaseName);
    return this.queryAsListString(sql, "trigger");
  }

  /**
   * get all procedure names.
   *
   * @param databaseName
   * @return List procedure names
   */
  public List<String> getAllProcedures(String databaseName) {
    String sql = SQL_GET_PROCEDURES.replace(DB_NAME_EXPRESSION, databaseName);
    return this.queryAsListString(sql, "Name");
  }


  /**
   * get all functions.
   *
   * @param databaseName
   * @return list function
   */
  public List<String> getAllSqlFunctions(String databaseName) {
    String sql = SQL_GET_FUNCTIONS.replace(DB_NAME_EXPRESSION, databaseName);
    return this.queryAsListString(sql, "Name");
  }

  /**
   * Default format date on MariaDB.
   *
   * @return format date
   */
  @Override
  protected String getDateFormat() {
    return "yyyy-MM-dd";
  }

  /**
   * Default format time on MariaDB.
   *
   * @return format time
   */
  @Override
  protected String getTimeFormat() {
    return "HH:mm:ss";
  }

  /**
   * Default format date time on MariaDB.
   *
   * @return format date time
   */
  @Override
  protected String getDateTimeFormat() {
    return "yyyy-MM-dd HH:mm:ss";
  }

  /**
   * Generate sql script for create table.
   *
   * @param tableName
   * @return sql script create table
   * @throws {@link org.springframework.dao.TransientDataAccessResourceException} when the view don't exists
   */
  public String generateScriptCreateTable(String tableName) {
    String sql = String.format("SHOW CREATE TABLE %s", tableName);
    String scriptCreateTable = this.queryAsString(sql, "Create Table");
    StringBuilder script = new StringBuilder();
    script.append(String.format("-- Script create table %s\n", tableName));
    script.append(this.generateSqlDropTable(tableName));
    script.append(";\n");
    script.append("/*!40101 SET @saved_cs_client     = @@character_set_client */;\n");
    script.append("/*!40101 SET character_set_client = utf8 */;\n");
    script.append(scriptCreateTable);
    script.append(';');
    script.append("\n/*!40101 SET character_set_client = @saved_cs_client */;\n");
    return script.toString();
  }

  /**
   * Generate sql script for create view.
   *
   * @param viewName
   * @return sql script create view
   * @throws {@link org.springframework.dao.TransientDataAccessResourceException} when the view don't exists
   */
  @Override
  public String generateScriptCreateView(String viewName) {
    String sql = String.format("SHOW CREATE VIEW %s", viewName);
    return this.queryAsString(sql, "Create View");
  }

  /**
   * Generate sql script for create trigger.
   *
   * @param triggerName
   * @return sql script create trigger
   * @throws {@link org.springframework.dao.TransientDataAccessResourceException} when the trigger don't exists
   */
  @Override
  public String generateScriptCreateTrigger(String triggerName) {
    String sql = String.format("SHOW CREATE TRIGGER %s", triggerName);
    return this.queryAsString(sql, "SQL Original Statement");
  }

  /**
   * Generate sql script for create procedure.
   *
   * @param procedureName
   * @return sql script create procedure
   * @throws {@link org.springframework.dao.TransientDataAccessResourceException} when the procedure don't exists
   */
  @Override
  public String generateScriptCreateProcedure(String procedureName) {
    String sql = String.format("SHOW CREATE PROCEDURE %s", procedureName);
    return this.queryAsString(sql, "Create procedure");
  }

  /**
   * Generate sql script for create function.
   *
   * @param functionName
   * @return sql script create function
   * @throws {@link org.springframework.jdbc.BadSqlGrammarException} when the function don't exists
   */
  @Override
  public String generateScriptCreateFunction(String functionName) {
    String sql = String.format("SHOW CREATE FUNCTION %s", functionName);
    return this.queryAsString(sql, "Create Function");
  }

  /**
   * Help get current version of MariaDB database.
   *
   * @return database version
   */
  @Override
  public String getDatabaseVersion() {
    String version = this.jdbcTemplate.queryForObject(
        "SELECT VERSION()",
        (rs, rowNum) -> rs.getString(1)
    );
    return String.format("MariaDB %s", version);
  }

  /**
   * Generate script sql header for backup script on MariaDB.
   * Database info, database name, set sql mode, disable check Foreign key, unique when re-table.
   * Set charset for encoding for backup script.
   *
   * @param databaseName
   * @return sql script backup header
   */
  @Override
  public String generateScriptBackupHeader(String databaseName) {
    StringBuilder script = new StringBuilder();
    script.append(String.format("-- Server version: %s\n", this.getDatabaseVersion()));
    script.append(String.format("-- Database: %s\n", databaseName));
    script.append("-- ------------------------------------------------------\n");
    script.append("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n");
    script.append("/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n");
    script.append("/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n");
    script.append("/*!40101 SET NAMES utf8mb4 */;\n");
    script.append("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n");
    script.append("/*!40103 SET TIME_ZONE='+00:00' */;\n");
    script.append("/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;\n");
    script.append("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n");
    script.append("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n");
    script.append("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n");
    script.append("-- ------------------------------------------------------\n\n");
    return script.toString();
  }

  /**
   * Generate script help to lock table when insert data for table on MariaDB.
   * Prevent the data is integrity.
   *
   * @param tableName
   * @return sql script lock table
   */
  @Override
  public String generateScriptLockTable(String tableName) {
    return String.format("LOCK TABLES `%s` WRITE;", tableName);
  }

  /**
   * Generate script help to unlock table after insert data for table finished.
   *
   * @param tableName
   * @return sql script unlock table
   */
  @Override
  public String generateScriptUnLockTable(String tableName) {
    return "UNLOCK TABLES;";
  }

  /**
   * Generate script help drop if exists.
   * Using when re-create table and this sql will execute before script create table.
   *
   * @param tableName
   * @return sql script drop table if exists
   */
  @Override
  public String generateSqlDropTable(String tableName) {
    return String.format("DROP TABLE IF EXISTS `%s`", tableName);
  }

  /**
   * Generate sql script disable foreign key using insert data for table.
   * Disable constraint FK, prevent data violate constraint data.
   * The sql will execute before insert data into table.
   *
   * @param tableName
   * @return sql script disable FK
   */
  @Override
  public String generateSqlDisableFkKey(String tableName) {
    return String.format("/*!40000 ALTER TABLE `%s` DISABLE KEYS */;", tableName);
  }

  /**
   * Generate sql script enable foreign key after insert data for table finished.
   *
   * @param tableName
   * @return sql script enable FK
   */
  @Override
  public String generateSqlEnableFkKey(String tableName) {
    return String.format("/*!40000 ALTER TABLE `%s` ENABLE KEYS */;", tableName);
  }

  /**
   * Generate sql script drop if exists procedure.
   *
   * @param procedureName
   * @return sql script drop procedure
   */
  @Override
  public String generateSqlDropIfExistsProcedure(String procedureName) {
    return String.format("DROP PROCEDURE IF EXISTS `%s`;", procedureName);
  }

  /**
   * Generate sql script help to drop if exists function.
   *
   * @param functionName
   * @return sql script drop function.
   */
  @Override
  public String generateSqlDropIfExistsFunction(String functionName) {
    return String.format("DROP FUNCTION IF EXISTS `%s`;", functionName);
  }

  /**
   * Generate sql script help to drop if exists trigger.
   *
   * @param triggerName
   * @return sql script drop trigger
   */
  @Override
  public String generateSqlDropIfExistsTrigger(String triggerName) {
    return String.format("DROP TRIGGER IF EXISTS `%s`;", triggerName);
  }

  /**
   * Generate sql script help to drop if exists view.
   *
   * @param viewName
   * @return sql script drop view
   */
  @Override
  public String generateSqlDropIfExistsView(String viewName) {
    return String.format("DROP VIEW IF EXISTS `%s`;", viewName);
  }

  /**
   * Generate sql backup for footer.
   *
   * @return sql end of file backup
   */
  @Override
  public String generateScriptBackupFooter() {
    StringBuilder sql = new StringBuilder();
    sql.append("\n");
    sql.append("/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;\n");
    sql.append("/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;\n");
    sql.append("/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;\n");
    sql.append("/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;\n");
    sql.append("/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;\n");
    sql.append("/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;\n");
    sql.append(String.format("-- Backup completed: %s",
        AppUtility.formatDate(new Date(), this.getDateFormat())));
    return sql.toString();
  }

  /**
   * Generate sql select for table.
   *
   * @param tableName
   * @return sql select from table
   */
  @Override
  public String generateSelectTable(String tableName) {
    return String.format("SELECT * FROM `%s`", tableName);
  }

  /**
   * Generate insert data for table.
   *
   * @param tableName
   * @param columns
   * @return sql insert table
   */
  @Override
  public String generateInsertTable(String tableName, List<String> columns) {
    List<String> cols = columns.stream()
        .map(it -> String.format("`%s`", it))
        .collect(Collectors.toList());
    return String.format("INSERT INTO `%s` (%s) VALUES ", tableName, String.join(",", cols));
  }

  private static final String SQL_GET_VIEWS = "SHOW TABLE STATUS FROM ${DB_NAME} WHERE Comment = 'VIEW'";
  private static final String SQL_GET_TRIGGER = "SHOW TRIGGERS FROM ${DB_NAME}";
  private static final String SQL_GET_FUNCTIONS = "SHOW FUNCTION STATUS WHERE DB = '${DB_NAME}'";
  private static final String SQL_GET_PROCEDURES = "SHOW PROCEDURE STATUS WHERE DB = '${DB_NAME}'";
  private static final String GET_ALL_TABLES = "SHOW TABLE STATUS FROM ${DB_NAME} WHERE Comment != 'VIEW'";
}
