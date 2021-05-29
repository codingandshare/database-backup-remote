package com.codingandshare.dbbk.services.impl;

import com.codingandshare.dbbk.exceptions.DBBackupException;
import com.codingandshare.dbbk.services.AbstractStorageService;
import com.codingandshare.dbbk.services.StorageService;
import com.codingandshare.dbbk.utils.AppUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * The class help to handle store file backup data to local server.
 * The file with pattern databaseName.${yyyy-MM-dd}.sql.
 * The class always init {@link Service} bean.
 *
 * @author Nhan Dinh
 * @since 5/29/21
 **/
@Slf4j
@Order(0)
@Service(value = "localStorage")
public class LocalStorageService extends AbstractStorageService implements StorageService {

  /**
   * The method help to copy backup file sql to ${STORAGE_FOLDER}/data_backup/databaseName.${yyyy-MM-dd}.sql.
   * The file with pattern databaseName.${yyyy-MM-dd}.sql.
   * After store backup file successfully then will be clean up backup file with retention days.
   * The steps handle this method:
   * 1. Copy file backup data into ${STORAGE_FOLDER}/data_backup/databaseName.${yyyy-MM-dd}.sql
   * 2. Clean up file backup data old with retention date.
   *
   * @throws DBBackupException when store file backup failed
   */
  @Override
  public void store() {
    try {
      String fileNameBackup = String.format(
          "%s.%s.sql",
          this.dataBaseName,
          AppUtility.formatDate(new Date(), "yyyy-MM-dd")
      );
      File fileBackupDate = new File(
          String.format("%s%s%s", this.backupFolder, File.separator, fileNameBackup)
      );
      File fileSqlBackup = new File(this.fileBackup);
      Files.copy(fileSqlBackup.toPath(), fileBackupDate.toPath(), StandardCopyOption.REPLACE_EXISTING);
      this.cleanupBackupFile();
    } catch (IOException e) {
      log.error("Store local file failed", e);
      throw new DBBackupException("Store local file failed", e);
    }
  }

  /**
   * The method get name store service of {@link LocalStorageService}.
   *
   * @return name local service
   */
  @Override
  public String getBackupStorageName() {
    return "Local storage";
  }

  /**
   * The method will run after finished job backup data.
   * Handle clean up backup file on local machine with retention days.
   * The retention days will config by users and by default is 7 days.
   * The variable env for user config: <pre>RETENTION_FILE_BACKUP</pre>.
   * The method list all file need to delete in {@link #backupFolder} folder with pattern databaseName*.
   * And last modify date before date retention.
   */
  private void cleanupBackupFile() {
    File backupFolder = new File(this.backupFolder);
    LocalDate retentionDate = LocalDate.now().minusDays(this.retentionBackupFile - 1);
    String[] listFileNameNeedDelete = backupFolder.list((dir, name) -> {
      String fileName = String.format("%s%s%s", dir, File.separator, name);
      LocalDate fileCreatedDate = Instant
          .ofEpochMilli(new File(fileName).lastModified())
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
      return name.startsWith(dataBaseName) && fileCreatedDate.isBefore(retentionDate);
    });
    if (!AppUtility.isEmpty(listFileNameNeedDelete)) {
      for (String fileNameDeleting : listFileNameNeedDelete) {
        String fileNameFullPath = String.format("%s%s%s", this.backupFolder, File.separator, fileNameDeleting);
        boolean isDeleteSuccess = new File(fileNameFullPath).delete();
        log.debug(String.format("File %s is deleted: %s", fileNameDeleting, isDeleteSuccess));
      }
    }
  }
}
