package com.codingandshare.dbbk.batch;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Write all sql insert to file with a table.
 *
 * @author Nhan Dinh
 * @since 5/8/21
 **/
public class TableWriter implements ItemWriter<List<String>> {

  /**
   * The method write all sql insert to file.
   *
   * @param items
   * @throws Exception
   */
  @Override
  public void write(List<? extends List<String>> items) throws Exception {
    System.out.println(items);
  }
}
