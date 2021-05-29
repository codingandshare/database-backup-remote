package com.codingandshare.dbbk.services;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * The abstraction class help build some common methods used by {@link BackupStorageService}.
 * Handle get config from env for put to storage.
 *
 * @author Nhan Dinh
 * @since 5/29/21
 **/
public abstract class AbstractBackStorageService {

  /**
   * The folder storage file backup on local server.
   */
  private static final String BACKUP_FOLDER = "data_backup";

  /**
   * The file name output after run backup file finished.
   */
  protected String fileBackup;

  /**
   * Get <pre>STORAGE_FOLDER</pre> env.
   */
  @Value("${app.storageFolder}")
  private String storageFolder;

  /**
   * The config get retentaion file to delete backup file.
   * By default is 7 days.
   * The user can config this value.
   */
  @Value("${app.retentionBackupFile}")
  protected Integer retentionBackupFile;

  /**
   * Database name need to backup data.
   */
  protected String dataBaseName;

  /**
   * The backup folder with on local server.
   * Location for folder backup: ${STORAGE_FOLDER}/data_backup.
   */
  protected String backupFolder;

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  /**
   * The method will get config and init for {@link AbstractBackStorageService#}.
   * The method will run after spring context started.
   * Init value for {@link #fileBackup}.
   * Init value for {@link #backupFolder}.
   * Init value for {@link #dataBaseName}.
   * If the folder store backup is doesn't exists then create new folder.
   */
  @PostConstruct
  void setupConfig() {
    this.dataBaseName = this.tableMetaDataRepository.getDatabaseName();
    this.fileBackup = String.format("%s%s%s.sql", this.storageFolder, File.separator, this.dataBaseName);
    this.backupFolder = String.format("%s%s%s", this.storageFolder, File.separator, BACKUP_FOLDER);
    File backupFolderFile = new File(this.backupFolder);
    if (!backupFolderFile.exists()) {
      backupFolderFile.mkdirs();
    }
  }
}
