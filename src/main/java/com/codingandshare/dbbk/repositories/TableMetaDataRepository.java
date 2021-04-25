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
}
