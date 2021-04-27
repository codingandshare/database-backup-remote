package com.codingandshare.dbbk.repositories.impl.mariadb;

import com.codingandshare.dbbk.repositories.TableMetaDataAbstract;
import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * The class implement <code>TableMetaDataRepository</code> interface handle MariaDB.
 *
 * @author Nhan Dinh
 * @since 4/23/21
 **/
@Repository
@Profile({"mariadb | mariadb-test"})
public class TableMetaDataRepositoryMariaDB extends TableMetaDataAbstract implements TableMetaDataRepository {

  /**
   * Implement sqlGetAllTables to get sql get all tables with table_schema.
   *
   * @return sql get all tables.
   */
  @Override
  protected String sqlGetAllTables() {
    return "SHOW TABLE STATUS FROM ${DB_NAME} WHERE Comment != 'VIEW'";
  }

  /**
   * Implement sqlGetAllViews to get sql get all views with table_schema.
   *
   * @return sql get all tables.
   */
  @Override
  protected String sqlGetAllViews() {
    return "SHOW TABLE STATUS FROM ${DB_NAME} WHERE Comment = 'VIEW'";
  }

  /**
   * Implement sqlGetAllTriggers to get sql get all triggers of database.
   *
   * @return sql get all triggers.
   */
  @Override
  protected String sqlGetAllTriggers() {
    return "SHOW TRIGGERS FROM ${DB_NAME}";
  }
}
