package com.codingandshare.dbbk.configs;

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

  /**
   * The method execute task backup database with cron express.
   */
  @Scheduled(cron = "${app.scheduleBackup}")
  public void scheduleBackup() {
    System.out.println("Run....");
  }
}
