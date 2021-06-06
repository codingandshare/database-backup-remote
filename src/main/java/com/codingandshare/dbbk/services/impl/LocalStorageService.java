package com.codingandshare.dbbk.services.impl;

import com.codingandshare.dbbk.exceptions.DBBackupException;
import com.codingandshare.dbbk.services.AbstractStorageService;
import com.codingandshare.dbbk.services.StorageService;
import com.codingandshare.dbbk.utils.AppUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

  private static final int BUFFER_BYTE = 1024;

  /**
   * The method help to copy backup file sql to ${STORAGE_FOLDER}/data_backup/databaseName.${yyyy-MM-dd}.sql.
   * The file with pattern databaseName.${yyyy-MM-dd}.sql.
   * After store backup file successfully then will be clean up backup file with retention days.
   * The steps handle this method:
   * 1. Zip the file backup sql to backup zip
   * 2. Copy file backup data into ${STORAGE_FOLDER}/data_backup/databaseName.${yyyy-MM-dd}.sql
   * 3. Clean up file backup data old with retention date.
   *
   * @throws DBBackupException when store file backup failed
   */
  @Override
  public void store() {
    try {
      File fileSqlBackup = new File(this.fileBackup);
      File backupZipped = this.zipBackupFiles(this.dataBaseName, fileSqlBackup);
      String fileNameBackup = String.format(
          "%s.%s.zip",
          this.dataBaseName,
          AppUtility.formatDate(new Date(), "yyyy-MM-dd")
      );
      File fileBackupDate = new File(
          String.format("%s%s%s", this.backupFolder, File.separator, fileNameBackup)
      );
      Files.copy(backupZipped.toPath(), fileBackupDate.toPath(), StandardCopyOption.REPLACE_EXISTING);
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

  /**
   * The method help zip the files backup out sql.
   * Example file name ${databaseName}.sql --> output file ${databaseName}.zip.
   *
   * @param files        list files need to zip
   * @param databaseName database name
   * @throws IOException when zip file failed
   * @return file output zipped
   */
  private File zipBackupFiles(String databaseName, File... files) throws IOException {
    String outputFileName = String.format("%s.zip", this.fileBackup.replace(".sql", ""));
    try (FileOutputStream fos = new FileOutputStream(outputFileName);
         ZipOutputStream zipOutputStream = new ZipOutputStream(fos)) {
      for (File file : files) {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[BUFFER_BYTE];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
          zipOutputStream.write(bytes, 0, length);
        }
        fis.close();
      }
      return new File(outputFileName);
    }
  }
}
