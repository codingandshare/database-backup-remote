package com.codingandshare.dbbk.batch;

import com.codingandshare.dbbk.services.FileService;
import com.codingandshare.dbbk.utils.DBBackupConst;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Write all sql insert to file with a table.
 *
 * @author Nhan Dinh
 * @since 5/8/21
 **/
public class TableWriter implements ItemWriter<List<String>> {

  private StepExecution stepExecution;

  @Autowired
  private FileService fileService;

  /**
   * Setup {@link StepExecution} for get tableName variable from processor task.
   *
   * @param stepExecution
   */
  @BeforeStep
  public void saveStepExecution(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  /**
   * The method write all sql insert to file.
   *
   * @param items
   */
  @Override
  public void write(List<? extends List<String>> items) {
    ExecutionContext executionContext = this.stepExecution.getExecutionContext();
    String tableName = (String) executionContext.get(DBBackupConst.KEY_TABLE_NAME);
    this.fileService.storeDataFileTable(tableName, !items.isEmpty() ? items.get(0) : null);
  }
}
