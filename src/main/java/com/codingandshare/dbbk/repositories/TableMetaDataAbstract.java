package com.codingandshare.dbbk.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * The abstraction class for <code>TableMetaDataRepository</code>.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
public abstract class TableMetaDataAbstract {

  private static final String DB_NAME_EXPRESSION = "${DB_NAME}";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  /**
   * get database name from connection.
   *
   * @return data name
   */
  public String getDatabaseName() {
    return this.jdbcTemplate.execute(Connection::getCatalog);
  }

  /**
   * The method to get all tables.
   *
   * @param databaseName database name.
   * @return List tables of a database.
   */
  public List<String> getAllTables(String databaseName) {
    String sql = this.sqlGetAllTables().replace(DB_NAME_EXPRESSION, databaseName);
    return this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("table_name")
    );
  }

  /**
   * The method to get all views.
   *
   * @param databaseName database name.
   * @return List views of a database.
   */
  public List<String> getAllViews(String databaseName) {
    String sql = this.sqlGetAllViews().replace(DB_NAME_EXPRESSION, databaseName);
    return this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("table_name")
    );
  }

  public List<String> getAllTriggers(String databaseName) {
    String sql = this.sqlGetAllTriggers().replace(DB_NAME_EXPRESSION, databaseName);
    return this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("trigger")
    );
  }

  /**
   * Abstract method to get sql select all tables from database.
   *
   * @return sql get all tables.
   */
  protected abstract String sqlGetAllTables();

  /**
   * Abstract method to get sql select all views from database.
   *
   * @return sql get all views from database name.
   */
  protected abstract String sqlGetAllViews();

  /**
   * Abstract get sql select all triggers from database.
   *
   * @return
   */
  protected abstract String sqlGetAllTriggers();
}
