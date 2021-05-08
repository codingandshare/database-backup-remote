package com.codingandshare.dbbk.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The class handle schedule for tasks.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
@Slf4j
@Component
@EnableScheduling
@ConditionalOnProperty(name = "app.runSchedule", havingValue = "true")
public class ScheduledTasks {

  @Autowired
  @Qualifier("backupJob")
  private Job backJob;

  @Autowired
  private JobLauncher jobLauncher;

  /**
   * The method execute task backup database with cron express.
   */
  @Scheduled(cron = "${app.scheduleBackup}")
  public void scheduleBackup() {
    try {
      log.info("Run backup job is starting..");
      JobParameters jobParameters = new JobParametersBuilder()
          .addString("JobID", String.valueOf(System.currentTimeMillis()))
          .toJobParameters();
      this.jobLauncher.run(this.backJob, jobParameters);
    } catch (JobExecutionAlreadyRunningException e) {
      log.warn("Job is running.");
    } catch (JobRestartException e) {
      log.warn("Job is restarted.");
    } catch (JobInstanceAlreadyCompleteException e) {
      log.warn("Job is completed.");
    } catch (JobParametersInvalidException e) {
      log.warn("Job parameters is invalid.");
    }
  }
}
