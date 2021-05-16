package com.codingandshare.dbbk.services;

import com.codingandshare.dbbk.utils.DBBackupConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * {@link BackupTableDataBackupTasklet} will generate script create table and sql insert data.
 *
 * @author Nhan Dinh
 * @since 5/14/21
 **/
@Slf4j
@Component
public class BackupTableDataBackupTasklet implements Tasklet, StepExecutionListener {

  @Autowired
  private TableDataService tableDataService;

  private List<String> tables;
  private FileWriter fileWriter;
  private String databaseName;

  /**
   * The method to setup for {@link StepExecution}.
   * Get list tables from {@link ReadMetaTasklet}.
   * Get database name from {@link ReadMetaTasklet}.
   * Clean up file old backup with <pre>database_name.sql</pre>.
   *
   * @param stepExecution
   */
  @Override
  public void beforeStep(StepExecution stepExecution) {
    ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
    this.databaseName = (String) executionContext.get(DBBackupConst.KEY_DATABASE);
    this.tables = (List<String>) executionContext.get(DBBackupConst.KEY_TABLE);
    this.fileWriter = this.tableDataService.setupFileBackup(databaseName);
  }

  /**
   * Get all tables for backup from {@link org.springframework.batch.item.ExecutionContext} in spring batch.
   * Generate script create foreach tables.
   * Generate script sql insert data foreach tables.
   * Add all script to file.
   *
   * @param contribution
   * @param chunkContext
   * @return
   * @throws Exception
   */
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    log.info("Execute backup data starting...");
    log.debug("Generate header script starting...");
    this.tableDataService.writeScriptBackupHeader(this.databaseName, fileWriter);
    log.debug("Generate header script done.");
    for (String table : tables) {
      log.debug(String.format("Backup for table %s starting...", table));
      this.tableDataService.writeScriptCreateTable(table, this.fileWriter);
      this.fileWriter.write(String.format("-- SQL insert data %s table \n", table));
      this.tableDataService.writeScriptDataBackup(table, this.fileWriter);
      this.fileWriter.write("\n\n");
      log.debug(String.format("Backup for table %s done.", table));
    }
    log.info("Execute backup data done.");
    return RepeatStatus.FINISHED;
  }

  /**
   * The method will execute after step finished.
   * Closing {@link FileWriter} resource.
   *
   * @param stepExecution
   * @return
   */
  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    if (this.fileWriter != null) {
      try {
        this.fileWriter.close();
      } catch (IOException ignored) {
      }
    }
    return ExitStatus.COMPLETED;
  }
}
