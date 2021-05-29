package com.codingandshare.dbbk.configs;

import com.codingandshare.dbbk.services.BackupTableDataBackupTasklet;
import com.codingandshare.dbbk.services.DatabaseMetaTasklet;
import com.codingandshare.dbbk.services.ReadMetaTasklet;
import com.codingandshare.dbbk.services.StorageTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The class config job to backup database.
 *
 * @author Nhan Dinh
 * @since 5/8/21
 **/
@Configuration
@EnableBatchProcessing
public class BackupBatchConfig {

  @Autowired
  private JobBuilderFactory jobs;

  @Autowired
  private StepBuilderFactory steps;

  @Autowired
  private BackupTableDataBackupTasklet backupTableDataBackupTasklet;

  @Autowired
  private ReadMetaTasklet readMetaTasklet;

  @Autowired
  private DatabaseMetaTasklet databaseMetaTasklet;

  @Autowired
  private StorageTasklet storageTasklet;

  /**
   * Create job to backup database.
   * This job will be executed by {@link ScheduledTasks}.
   * This job will contain steps:
   * - {@link #readMetaDataStep()} read meta data for next step to backup.
   * - {@link #backupDataTableStep()} task handle backup script create table and data insert foreach tables.
   * - {@link #backupDatabaseMetaStep()} task handle backup script create procedures, functions, triggers.
   * - {@link #storageStep()} task handle store backup file sql to some storages service.
   *
   * @return Job
   */
  @Bean("backupJob")
  protected Job backupJob() {
    return jobs
        .get("backupJob")
        .incrementer(new RunIdIncrementer())
        .start(readMetaDataStep())
        .next(backupDataTableStep())
        .next(backupDatabaseMetaStep())
        .next(storageStep())
        .build();
  }

  /**
   * Create {@link Bean} step handle to read meta data.
   * This step execute by {@link ReadMetaTasklet}.
   *
   * @return Step {@link #readMetaDataStep}
   */
  @Bean
  protected Step readMetaDataStep() {
    return this.steps
        .get("readMetaDataStep")
        .tasklet(this.readMetaTasklet)
        .build();
  }

  /**
   * Create {@link Bean} step handle to backup script create and data foreach table.
   *
   * @return {@link Step}
   */
  @Bean
  protected Step backupDataTableStep() {
    return this.steps
        .get("backupDataTableStep")
        .tasklet(this.backupTableDataBackupTasklet)
        .build();
  }

  /**
   * Create {@link Bean} the step handle to backup script create produces, functions, triggers.
   *
   * @return {@link Step}
   */
  @Bean
  protected Step backupDatabaseMetaStep() {
    return this.steps
        .get("backupDatabaseMetaStep")
        .tasklet(this.databaseMetaTasklet)
        .build();
  }

  /**
   * Create an {@link Bean} the step handle store backup file sql to some storages service.
   *
   * @return {@link Step}
   */
  @Bean
  protected Step storageStep() {
    return this.steps
        .get("storageStep")
        .tasklet(this.storageTasklet)
        .build();
  }
}
