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
 * The {@link DatabaseMetaTasklet} is Step help backup creation script for procedures, functions, triggers.
 *
 * @author Nhan Dinh
 * @since 5/23/21
 **/
@Slf4j
@Component
public class DatabaseMetaTasklet implements Tasklet, StepExecutionListener {

  @Autowired
  private DatabaseMetaService databaseMetaService;

  private List<String> procedures;
  private List<String> functions;
  private FileWriter fileWriter;

  /**
   * Setup data for tasklet executing.
   * Get meta data table for help execute tasklet.
   * Get list procedures from {@link ReadMetaTasklet} step.
   * Get list functions from {@link ReadMetaTasklet} step.
   * Get {@link FileWriter} instance from {@link BackupTableDataBackupTasklet} step.
   *
   * @param stepExecution
   */
  @Override
  public void beforeStep(StepExecution stepExecution) {
    ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
    this.procedures = (List<String>) executionContext.get(DBBackupConst.KEY_PROCEDURES);
    this.functions = (List<String>) executionContext.get(DBBackupConst.KEY_FUNCTIONS);
    this.fileWriter = (FileWriter) executionContext.get(DBBackupConst.KEY_FILE);
  }

  /**
   * After execute tasklet done then will closing {@link FileWriter} resource file.
   *
   * @param stepExecution
   * @return {@link ExitStatus}
   */
  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    if (this.fileWriter != null) {
      try {
        this.fileWriter.close();
        log.info("FileWriter is closed.");
      } catch (IOException ignored) {
      }
    }
    return ExitStatus.COMPLETED;
  }

  /**
   * The method execute tasklet.
   * - Backup script create for procedures.
   * - Backup script create for functions.
   *
   * @param contribution
   * @param chunkContext
   * @return {@link RepeatStatus}
   * @throws Exception
   */
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    log.info("Tasklet database meta is executing...");
    log.debug("Backup script create procedure starting...");
    this.databaseMetaService.writeScriptCreateProcedures(this.procedures, this.fileWriter);
    log.debug("Backup script create procedure done.");
    log.debug("Backup script create function starting...");
    this.databaseMetaService.writeScriptCreateFunctions(this.functions, this.fileWriter);
    log.debug("Backup script create function done.");
    log.info("Tasklet database meta is done.");
    return RepeatStatus.FINISHED;
  }
}
