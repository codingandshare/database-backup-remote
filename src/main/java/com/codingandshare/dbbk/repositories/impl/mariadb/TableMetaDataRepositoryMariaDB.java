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

  /**
   * Implement sqlGetAllFunctions to get sql get all functions of database.
   *
   * @return sql get all functions.
   */
  @Override
  protected String sqlGetAllFunctions() {
    return "SHOW FUNCTION STATUS WHERE DB = '${DB_NAME}'";
  }

  /**
   * Implement sqlGetAllProcedures to get sql get all procedures of database.
   *
   * @return sql get all procedures.
   */
  @Override
  protected String sqlGetAllProcedures() {
    return "SHOW PROCEDURE STATUS WHERE DB = '${DB_NAME}'";
  }

  /**
   * Generate sql script for create table.
   *
   * @param tableName
   * @return sql script create table
   */
  public String generateScriptCreateTable(String tableName) {
    String sql = String.format("SHOW CREATE TABLE %s", tableName);
    return this.getJdbcTemplate().queryForObject(
        sql,
        (rs, rowNum) -> rs.getString("Create Table")
    );
  }

  /**
   * Generate sql script for create view.
   *
   * @param viewName
   * @return sql script create view
   */
  @Override
  public String generateScriptCreateView(String viewName) {
    String sql = String.format("SHOW CREATE VIEW %s", viewName);
    return this.getJdbcTemplate().queryForObject(
        sql,
        (rs, rowNum) -> rs.getString("Create View")
    );
  }
}
