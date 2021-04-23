package com.codingandshare.dbbk.repositories;

import java.util.List;

/**
 * The interface will get meta data info of database.
 *
 * @author Nhan Dinh
 * @since 4/23/21
 **/
public interface TableMetaDataRepository {

  /**
   * The method to get all tables.
   *
   * @return List tables of a database.
   */
  List<String> getAllTables();
}
