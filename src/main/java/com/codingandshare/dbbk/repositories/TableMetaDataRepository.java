package com.codingandshare.dbbk.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * The interface will get meta data info of database.
 *
 * @author Nhan Dinh
 * @since 4/23/21
 **/
public interface TableMetaDataRepository {

  /**
   * The method to get all tables.
   *
   * @param databaseName database name.
   * @return List tables of a database.
   */
  List<String> getAllTables(String databaseName);

  /**
   * The method to get all views.
   *
   * @param databaseName database name.
   * @return List views of a database.
   */
  List<String> getAllViews(String databaseName);

  /**
   * Get database name from JDBC connection.
   *
   * @return database name.
   */
  String getDatabaseName();

  /**
   * Get all triggers name.
   *
   * @param databaseName
   * @return List trigger names.
   */
  List<String> getAllTriggers(String databaseName);

  /**
   * get all procedure names.
   *
   * @param databaseName
   * @return List procedure names
   */
  List<String> getAllProcedures(String databaseName);

  /**
   * get all functions.
   *
   * @param databaseName
   * @return list function
   */
  List<String> getAllSqlFunctions(String databaseName);

  /**
   * Generate sql script for create table.
   *
   * @param tableName
   * @return sql script create table
   */
  String generateScriptCreateTable(String tableName);

  /**
   * Generate sql script for create view.
   *
   * @param viewName
   * @return sql script create view
   */
  String generateScriptCreateView(String viewName);

  /**
   * Generate sql script for create trigger.
   *
   * @param triggerName
   * @return sql script create trigger
   */
  String generateScriptCreateTrigger(String triggerName);

  /**
   * Generate sql script for create procedure.
   *
   * @param procedureName
   * @return sql script create procedure
   */
  String generateScriptCreateProcedure(String procedureName);

  /**
   * Generate sql script for create function.
   *
   * @param functionName
   * @return sql script create function
   */
  String generateScriptCreateFunction(String functionName);

  /**
   * Help to build the value for sql insert from {@link ResultSet}.
   *
   * @param resultSet
   * @return List values for insert
   * @throws SQLException when build sql insert failed
   */
  List<String> getValueInsertFromResultSet(ResultSet resultSet) throws SQLException;

  /**
   * Help to get all columns from {@link ResultSet}.
   *
   * @param resultSet
   * @return list columns
   * @throws SQLException
   */
  List<String> getAllColumnFromResultSet(ResultSet resultSet) throws SQLException;

  /**
   * The method help to get database version current info.
   *
   * @return database version
   */
  String getDatabaseVersion();

  /**
   * The script header for backup script.
   * Contain database info, database name.
   * Script set some meta data.
   *
   * @param databaseName
   * @return script backup header
   */
  String generateScriptBackupHeader(String databaseName);

  /**
   * The method help to build script sql lock table when insert data for that table.
   *
   * @param tableName
   * @return script sql lock table
   */
  String generateScriptLockTable(String tableName);

  /**
   * The script help to build script sql unlock table when release insert data for that table.
   *
   * @param tableName
   * @return script sql unlock table
   */
  String generateScriptUnLockTable(String tableName);

  /**
   * generate sql script drop table if exists.
   *
   * @param tableName
   * @return sql script drop table
   */
  String generateSqlDropTable(String tableName);

  /**
   * generate sql script disable foreign key help disable violate constraints when insert data to table.
   *
   * @param tableName
   * @return sql disable foreign key.
   */
  String generateSqlDisableFkKey(String tableName);

  /**
   * generate sql script enable foreign key after insert data for table finished.
   *
   * @param tableName
   * @return sql enable foreign key.
   */
  String generateSqlEnableFkKey(String tableName);

  /**
   * Generate sql script drop if exists procedure.
   *
   * @param procedureName
   * @return sql script drop procedure
   */
  String generateSqlDropIfExistsProcedure(String procedureName);

  /**
   * Generate sql script help to drop if exists function.
   *
   * @param functionName
   * @return sql script drop function.
   */
  String generateSqlDropIfExistsFunction(String functionName);

  /**
   * Generate sql script help to drop if exists trigger.
   *
   * @param triggerName
   * @return sql script drop trigger
   */
  String generateSqlDropIfExistsTrigger(String triggerName);
}
