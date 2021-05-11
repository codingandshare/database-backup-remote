package com.codingandshare.dbbk.services;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * The service to handle relate store file.
 *
 * @author Nhan Dinh
 * @since 5/9/21
 **/
@Service
@Slf4j
public class FileService {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  @Value("${app.storageFolder}")
  private String storageFolder;

  private static final String FOLDER_NAME_BACKUP = "cas_backup";

  /**
   * The method handle init data for {@link #storageFolder}.
   * If the folder not config by user then get tmp of OS.
   */
  @PostConstruct
  private void loadStorageFolder() {
    if (this.storageFolder == null || this.storageFolder.isEmpty()) {
      this.storageFolder = System.getProperty("java.io.tmpdir");
    }
  }

  /**
   * The method help to build full absolute path file name to store.
   * The format example: /tmp/cas_backup/user.sql.
   *
   * @param tableName
   * @return String full path file name.
   */
  private String buildFileNameTable(String tableName) {
    String folder = String.format("%s%s%s", this.storageFolder, File.separator, FOLDER_NAME_BACKUP);
    File folderFile = new File(folder);
    if (!folderFile.exists()) {
      folderFile.mkdir();
    }
    return String.format("%s%s%s.sql",
        folder, File.separator, tableName);
  }

  /**
   * The method to handle store content of a file by once table.
   * Content will contain script create table, script sql inserts.
   *
   * @param tableName
   * @param sqlInserts
   */
  public void storeDataFileTable(String tableName, List<String> sqlInserts) {
    String fileName = this.buildFileNameTable(tableName);
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(this.buildSqlScriptCreateTable(tableName));
      if (sqlInserts != null && !sqlInserts.isEmpty()) {
        fileWriter.write("-- Script insert data\n");
        for (String sql : sqlInserts) {
          fileWriter.write(String.format("%s;\n", sql));
        }
      }
      log.info(String.format("Table %s stored", tableName));
    } catch (IOException e) {
      log.error(String.format("store file for table %s", tableName), e);
    }
  }

  /**
   * The method help to build script create table sql.
   *
   * @param tableName
   * @return sql script create table.
   */
  private String buildSqlScriptCreateTable(String tableName) {
    return String.format("-- Script create table %s\n", tableName)
        + this.tableMetaDataRepository.generateScriptCreateTable(tableName)
        + ";\n";
  }
}
