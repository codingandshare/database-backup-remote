package com.codingandshare.dbbk.services;

import java.io.FileWriter;
import java.io.IOException;

/**
 * The interface help to handle business relate to backup data for table.
 *
 * @author Nhan Dinh
 * @since 05/15/2021
 */
public interface TableDataService {

  /**
   * The method help to handle backup file sql.
   * The pattern file name: ${storageFolder}/database_name.sql.
   * The ${storageFolder} user can config it and default is /tmp folder.
   *
   * @param databaseName database name need to backup
   * @return {@link FileWriter}
   */
  FileWriter setupFileBackup(String databaseName);

  /**
   * The method help to generate script data sql insert for an table.
   * Query table streaming data into {@link java.io.FileWriter}.
   *
   * @param tableName table name need to generate script
   * @param fileWriter {@link FileWriter} write sql to file
   * @throws IOException write script to file failed
   */
  void writeScriptDataBackup(String tableName, FileWriter fileWriter) throws IOException;

  /**
   * The method help to generate script data sql create table for an table.
   *
   * @param tableName
   * @param fileWriter
   * @throws IOException write script to file failed.
   */
  void writeScriptCreateTable(String tableName, FileWriter fileWriter) throws IOException;

  /**
   * The method help to build header for backup script.
   * Header contain: Database version, host, database name, disable some check constraints.
   *
   * @param databaseName
   * @param fileWriter help to write to file
   * @throws IOException write to file failed
   */
  void writeScriptBackupHeader(String databaseName, FileWriter fileWriter) throws IOException;
}
