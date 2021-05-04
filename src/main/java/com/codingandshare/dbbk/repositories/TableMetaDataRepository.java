package com.codingandshare.dbbk.repositories;

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
}
