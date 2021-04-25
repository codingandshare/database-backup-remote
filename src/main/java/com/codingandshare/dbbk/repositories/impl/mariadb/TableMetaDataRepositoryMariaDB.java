package com.codingandshare.dbbk.repositories.impl.mariadb;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * The class implement <code>TableMetaDataRepository</code> interface handle MariaDB.
 *
 * @author Nhan Dinh
 * @since 4/23/21
 **/
@Repository
@Profile("mariadb")
public class TableMetaDataRepositoryMariaDB implements TableMetaDataRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  /**
   * The method to get all table of a database mariadb.
   *
   * @return List tables.
   */
  @Override
  public List<String> getAllTables() {
    return new ArrayList<>();
  }

}
