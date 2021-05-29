package com.codingandshare.dbbk.services;

/**
 * The interface handle store file to some storages.
 * Handle clean up backup files on local file with retention days.
 * The service will called by {@link StorageTasklet}.
 *
 * @author Nhan Dinh
 * @since 05/29/2021
 */
public interface StorageService {

  /**
   * The method to handle store file backup to some storages.
   * After the {@link DatabaseMetaTasklet} tasklet finished then get the output sql file put to storage.
   */
  void store();

  /**
   * The method get name of storage service.
   * @return store service name
   */
  String getBackupStorageName();
}
