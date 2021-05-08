package com.codingandshare.dbbk.batch;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
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

  /**
   * The method to convert table to list sql insert.
   *
   * @param item
   * @return List sql insert.
   */
  @Override
  public List<String> process(String item) {
    log.info("Table name " + item);
    return Arrays.asList("aa", "bb");
  }
}
