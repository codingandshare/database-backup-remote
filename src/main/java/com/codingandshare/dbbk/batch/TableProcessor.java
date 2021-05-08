package com.codingandshare.dbbk.batch;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import com.codingandshare.dbbk.utils.DBBackupConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * The class implement by {@link ItemProcessor}.
 * a table to list sql insert.
 *
 * @author Nhan Dinh
 * @since 5/8/21
 **/
@Slf4j
public class TableProcessor implements ItemProcessor<String, List<String>> {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  private StepExecution stepExecution;

  /**
   * Setup {@link StepExecution} for put tableName variable from processor task.
   *
   * @param stepExecution
   */
  @BeforeStep
  public void saveStepExecution(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  /**
   * The method to convert table to list sql insert.
   *
   * @param item
   * @return List sql insert.
   */
  @Override
  public List<String> process(String item) {
    ExecutionContext executionContext = this.stepExecution.getExecutionContext();
    executionContext.put(DBBackupConst.KEY_TABLE_NAME, item);
    return Arrays.asList("aa", "bb");
  }
}
