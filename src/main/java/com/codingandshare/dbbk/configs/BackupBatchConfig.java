package com.codingandshare.dbbk.configs;

import com.codingandshare.dbbk.batch.TableProcessor;
import com.codingandshare.dbbk.batch.TableReader;
import com.codingandshare.dbbk.batch.TableWriter;
import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import com.codingandshare.dbbk.services.ReadMetaTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
  private TableMetaDataRepository tableMetaDataRepository;

  /**
   * Create job to backup database.
   * This job will contain steps:
   * - {@link #readMetaDataStep()}
   *
   * @return Job
   */
  @Bean("backupJob")
  protected Job backupJob() {
    return jobs
        .get("backupJob")
        .incrementer(new RunIdIncrementer())
        .start(readMetaDataStep())
        .next(backupDataTable())
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
        .tasklet(new ReadMetaTasklet(this.tableMetaDataRepository))
        .build();
  }

  /**
   * Create {@link Bean} step handle read all data table and convert sql insert and store it to file.
   *
   * @return Step {@link #backupDataTable}
   */
  @Bean
  protected Step backupDataTable() {
    return this.steps
        .get("backupDataTable")
        .<String, List<String>>chunk(2)
        .reader(tableReader())
        .processor(tableProcessor())
        .writer(tableWriter())
        .build();
  }

  /**
   * Create {@link Bean} step handle {@link ItemReader}.
   *
   * @return {@link ItemReader}
   */
  @Bean
  protected ItemReader<String> tableReader() {
    return new TableReader();
  }

  /**
   * Create {@link Bean} step handle {@link ItemProcessor}.
   *
   * @return {@link ItemProcessor}
   */
  @Bean
  protected ItemProcessor<String, List<String>> tableProcessor() {
    return new TableProcessor();
  }

  /**
   * Create {@link Bean} step handle {@link ItemWriter}.ReadMetaTasklet.
   *
   * @return {@link ItemWriter}
   */
  @Bean
  protected ItemWriter<List<String>> tableWriter() {
    return new TableWriter();
  }
}
