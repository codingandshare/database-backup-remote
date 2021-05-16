package com.codingandshare.dbbk.services.impl;

import com.codingandshare.dbbk.exceptions.DBBackupException;
import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import com.codingandshare.dbbk.services.TableDataService;
import com.codingandshare.dbbk.utils.DBBackupConst;
import com.codingandshare.dbbk.utils.StreamingStatementCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The service to handle relate store file.
 *
 * @author Nhan Dinh
 * @since 5/9/21
 **/
@Service
@Slf4j
public class TableDataServiceImpl implements TableDataService {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  @Value("${app.storageFolder}")
  private String storageFolder;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  /**
   * The method help to handle backup file sql.
   * The pattern file name: ${storageFolder}/database_name.sql.
   * The ${storageFolder} user can config it and default is /tmp folder.
   *
   * @param databaseName database name need to backup
   * @throws DBBackupException setup file backup failed
   * @return {@link FileWriter}
   */
  @Override
  public FileWriter setupFileBackup(String databaseName) {
    String fileNameBackup = String.format("%s%s%s.sql", this.storageFolder, File.separator, databaseName);
    try {
      return new FileWriter(fileNameBackup, false);
    } catch (IOException e) {
      log.error(String.format("Clean up file %s", fileNameBackup), e);
      throw new DBBackupException(String.format("Clean up file %s", fileNameBackup));
    }
  }

  /**
   * The method help to generate script data sql insert for an table.
   * Query table streaming data into {@link FileWriter}.
   * Batch size on buffer of {@link FileWriter} default is {@link DBBackupConst#FETCH_SIZE_ROWS}.
   * And will flush buffer to file.
   *
   * @param tableName table name need to generate script
   * @throws RuntimeException write script to file failed
   */
  @Override
  public void writeScriptDataBackup(String tableName, FileWriter fileWriter) throws IOException {
    fileWriter.write("LOCK TABLES `entities` WRITE;\n");
    fileWriter.write("/*!40000 ALTER TABLE `entities` DISABLE KEYS */;\n");
    String sqlSelect = String.format("SELECT * FROM %s", tableName);
    AtomicLong rows = new AtomicLong();
    this.jdbcTemplate.query(new StreamingStatementCreator(sqlSelect), rs -> {
      try {
        if (rows.get() == 0) {
          List<String> columns = this.tableMetaDataRepository.getAllColumnFromResultSet(rs);
          fileWriter.write(String.format("INSERT INTO %s (%s) VALUES ", tableName, String.join(",", columns)));
        } else {
          fileWriter.append(",\n");
        }
        fileWriter.write(this.tableMetaDataRepository.buildValueInsertFromResultSet(rs));
        if (rows.getAndIncrement() % DBBackupConst.FETCH_SIZE_ROWS == 0) {
          fileWriter.flush();
        }
      } catch (IOException e) {
        throw new RuntimeException(String.format("Build query insert for %s table failed", tableName), e);
      }
    });
    if (rows.get() > 0) {
      fileWriter.write(";\n");
    }
    fileWriter.write("/*!40000 ALTER TABLE `entities` ENABLE KEYS */;\n");
    fileWriter.write("UNLOCK TABLES;\n");
    fileWriter.flush();
  }

  /**
   * The method help to generate script data sql create table for an table.
   *
   * @param tableName
   * @param fileWriter
   * @throws IOException write script to file failed.
   */
  @Override
  public void writeScriptCreateTable(String tableName, FileWriter fileWriter) throws IOException {
    fileWriter.write(String.format("-- Script create table %s\n", tableName));
    String dropIfExists = String.format("DROP TABLE IF EXISTS `%s`\n", tableName);
    fileWriter.write(dropIfExists);
    fileWriter.write("/*!40101 SET @saved_cs_client     = @@character_set_client */;\n");
    fileWriter.write("/*!40101 SET character_set_client = utf8 */;\n");
    fileWriter.write(this.tableMetaDataRepository.generateScriptCreateTable(tableName));
    fileWriter.write("\n");
    fileWriter.write("/*!40101 SET character_set_client = @saved_cs_client */;\n");
    fileWriter.flush();
  }

  /**
   * The method help to build header for backup script.
   * Header contain: Database version, host, database name, disable some check constraints.
   *
   * @param databaseName
   * @param fileWriter   help to write to file
   * @throws IOException write to file failed
   */
  @Override
  public void writeScriptBackupHeader(String databaseName, FileWriter fileWriter) throws IOException {
    String serverVersion = String.format("-- Server version: %s\n", this.tableMetaDataRepository.getDatabaseVersion());
    fileWriter.write(serverVersion);
    fileWriter.write(String.format("-- Database: %s\n", databaseName));
    fileWriter.write("-- ------------------------------------------------------\n");
    fileWriter.write("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n");
    fileWriter.write("/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n");
    fileWriter.write("/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n");
    fileWriter.write("/*!40101 SET NAMES utf8mb4 */;\n");
    fileWriter.write("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n");
    fileWriter.write("/*!40103 SET TIME_ZONE='+00:00' */;\n");
    fileWriter.write("/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;\n");
    fileWriter.write("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n");
    fileWriter.write("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n");
    fileWriter.write("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n");
    fileWriter.write("-- ------------------------------------------------------\n\n");
    fileWriter.flush();
  }
}
