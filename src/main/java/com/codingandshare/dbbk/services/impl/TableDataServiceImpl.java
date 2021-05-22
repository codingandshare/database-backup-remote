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
   * @return {@link FileWriter}
   * @throws DBBackupException setup file backup failed
   */
  @Override
  public FileWriter setupFileBackup(String databaseName) {
    String fileNameBackup = String.format("%s%s%s.sql", this.storageFolder, File.separator, databaseName);
    try {
      return new FileWriter(fileNameBackup, false);
    } catch (IOException e) {
      String msg = String.format("Clean up file %s failed", fileNameBackup);
      log.error(msg, e);
      throw new DBBackupException(msg);
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
    fileWriter.write(this.tableMetaDataRepository.generateScriptLockTable(tableName));
    fileWriter.write("\n");
    fileWriter.write(this.tableMetaDataRepository.generateSqlDisableFkKey(tableName));
    fileWriter.write("\n");
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
        fileWriter.write(String.format("(%s)", String.join(",",
            this.tableMetaDataRepository.getValueInsertFromResultSet(rs))));
        if (rows.getAndIncrement() % DBBackupConst.FETCH_SIZE_ROWS == 0) {
          fileWriter.flush();
        }
      } catch (IOException ignored) {
      }
    });
    if (rows.get() > 0) {
      fileWriter.write(";\n");
    }
    fileWriter.write(this.tableMetaDataRepository.generateSqlEnableFkKey(tableName));
    fileWriter.write("\n");
    fileWriter.write(this.tableMetaDataRepository.generateScriptUnLockTable(tableName));
    fileWriter.write("\n");
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
    fileWriter.write(this.tableMetaDataRepository.generateScriptCreateTable(tableName));
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
    fileWriter.write(this.tableMetaDataRepository.generateScriptBackupHeader(databaseName));
    fileWriter.flush();
  }
}
