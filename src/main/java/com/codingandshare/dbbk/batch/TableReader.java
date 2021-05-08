package com.codingandshare.dbbk.batch;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Implement class for interface {@link ItemReader}
 * The class handle read all table to backup database.
 *
 * @author Nhan Dinh
 * @since 5/8/21
 **/
public class TableReader implements ItemReader<String> {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  private ItemReader<String> delegate;

  /**
   * Setup reset reader task.
   */
  @BeforeStep
  public void setupStartReader() {
    this.delegate = null;
  }

  /**
   * The method read all table need to backup.
   *
   * @return List tables need to backup.
   * @throws Exception
   */
  @Override
  public String read() throws Exception {
    if (this.delegate == null) {
      String databaseName = this.tableMetaDataRepository.getDatabaseName();
      List<String> tables = this.tableMetaDataRepository.getAllTables(databaseName);
      this.delegate = new IteratorItemReader<>(tables);
    }
    return this.delegate.read();
  }
}
