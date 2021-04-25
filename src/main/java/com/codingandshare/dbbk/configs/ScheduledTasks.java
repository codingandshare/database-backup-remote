package com.codingandshare.dbbk.configs;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The class handle schedule for tasks.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
@Component
@EnableScheduling
public class ScheduledTasks {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  /**
   * The method execute task backup database with cron express.
   */
  @Scheduled(cron = "${app.scheduleBackup}")
  public void scheduleBackup() {
    System.out.println("Run....");
    String dbName = this.tableMetaDataRepository.getDatabaseName();
    System.out.println(this.tableMetaDataRepository.getAllTables(dbName));
    System.out.println(this.tableMetaDataRepository.getAllViews(dbName));
  }
}
