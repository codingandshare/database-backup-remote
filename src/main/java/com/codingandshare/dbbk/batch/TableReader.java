package com.codingandshare.dbbk.batch;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;

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
   * The method read all table need to backup.
   *
   * @return List tables need to backup.
   * @throws Exception
   */
  @Override
  public String read() throws Exception {
    if (this.delegate == null || this.delegate.read() == null) {
      String databaseName = this.tableMetaDataRepository.getDatabaseName();
      this.delegate = new IteratorItemReader<>(this.tableMetaDataRepository.getAllTables(databaseName));
    }
    return this.delegate.read();
  }
}
