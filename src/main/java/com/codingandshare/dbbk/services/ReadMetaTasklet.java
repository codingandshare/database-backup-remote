package com.codingandshare.dbbk.services;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * The class handle read all meta for backup tasks.
 * This class will start first on backup job.
 *
 * @author Nhan Dinh
 * @since 5/8/21
 **/
@Slf4j
public class ReadMetaTasklet implements Tasklet, StepExecutionListener {
  private List<String> tables;
  private List<String> triggers;
  private List<String> functions;
  private List<String> procedures;
  private List<String> views;
  private String databaseName;
  private final TableMetaDataRepository tableMetaDataRepository;

  /**
   * The constructor initialize object with {@link TableMetaDataRepository}.
   *
   * @param tableMetaDataRepository
   */
  public ReadMetaTasklet(TableMetaDataRepository tableMetaDataRepository) {
    this.tableMetaDataRepository = tableMetaDataRepository;
  }

  /**
   * Run this method first when {@link ReadMetaTasklet} executed by job backup.
   * Init some lists.
   *
   * @param stepExecution
   */
  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.tables = new ArrayList<>();
    this.triggers = new ArrayList<>();
    this.functions = new ArrayList<>();
    this.procedures = new ArrayList<>();
    this.views = new ArrayList<>();
    log.info("Read meta data for backup is starting...");
  }

  /**
   * The method execute when {@link Tasklet} called.
   * Get database name current of this connection.
   * Get all  table, trigger, function, procedure.
   *
   * @param contribution
   * @param chunkContext
   * @return Status for execute
   * @throws Exception when error collect data from database
   */
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    log.info("Read meta data for backup executed.");
    this.databaseName = this.tableMetaDataRepository.getDatabaseName();
    this.tables = this.tableMetaDataRepository.getAllTables(this.databaseName);
    this.triggers = this.tableMetaDataRepository.getAllTriggers(this.databaseName);
    this.views = this.tableMetaDataRepository.getAllViews(this.databaseName);
    this.functions = this.tableMetaDataRepository.getAllSqlFunctions(this.databaseName);
    this.procedures = this.tableMetaDataRepository.getAllProcedures(this.databaseName);
    return RepeatStatus.FINISHED;
  }

  /**
   * Binding data into {@link ExecutionContext} after collect from database.
   *
   * @param stepExecution
   * @return {@link ExitStatus}
   */
  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    log.info("Read meta data for backup finished.");
    ExecutionContext executionContext = stepExecution
        .getJobExecution()
        .getExecutionContext();
    executionContext.put("views", this.views);
    executionContext.put("tables", this.tables);
    executionContext.put("triggers", this.triggers);
    executionContext.put("functions", this.functions);
    executionContext.put("procedures", this.procedures);
    return ExitStatus.COMPLETED;
  }
}
