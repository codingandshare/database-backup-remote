package com.codingandshare.dbbk.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The class will handle task relate to storage backup file.
 * Version 1.0.RELEASE only support local store and git storage.
 * The {@link StorageTasklet} will be called by Backup job.
 * Handle multiple storages file.
 * If the user config add some storages: git, drive,... the class will be handled all storages here.
 * Always local storage execute by default if the user don't have config another storage.
 *
 * @author Nhan Dinh
 * @since 5/29/21
 **/
@Component
@Slf4j
public class StorageTasklet implements Tasklet {

  /**
   * Get list storage service api.
   */
  @Autowired
  private List<BackupStorageService> backupStorageServices;

  /**
   * The method execute all storage service and that method will be executed by backup job.
   * Always {@link com.codingandshare.dbbk.services.impl.LocalBackupStorageService} is executing.
   * Some storages can config by user.
   *
   * @param contribution
   * @param chunkContext
   * @return {@link RepeatStatus}
   * @throws Exception store backup file failed.
   */
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    if (this.backupStorageServices != null && !this.backupStorageServices.isEmpty()) {
      for (BackupStorageService backupStorageService : this.backupStorageServices) {
        log.info(String.format("%s is starting...", backupStorageService.getBackupStorageName()));
        backupStorageService.store();
        log.info(String.format("%s is done.", backupStorageService.getBackupStorageName()));
      }
    }
    return RepeatStatus.FINISHED;
  }
}
