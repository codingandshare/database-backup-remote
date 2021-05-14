package com.codingandshare.dbbk.repositories;

import com.codingandshare.dbbk.utils.DBBackupConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The abstraction class for <code>TableMetaDataRepository</code>.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
public abstract class TableMetaDataAbstract {

  private static final String DB_NAME_EXPRESSION = "${DB_NAME}";

  /**
   * The prefix table meta will get from application properties.
   * The use can define it and can set value for <pre>PREFIX_TABLE_META</pre> environment.
   * By default is <pre>CAS_BATCH_</pre>.
   */
  @Value("${spring.batch.table-prefix}")
  private String prefixTableMeta;

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
   * The method to get all tables in database.
   * Exclude all table meta of this service.
   *
   * @param databaseName database name.
   * @return List tables of a database.
   */
  public List<String> getAllTables(String databaseName) {
    String sql = this.sqlGetAllTables().replace(DB_NAME_EXPRESSION, databaseName);
    List<String> tables = this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("table_name")
    );
    List<String> excludeTables = Arrays.stream(DBBackupConst.META_TABLES)
        .map(it -> String.format("%s%s", this.prefixTableMeta, it)).collect(Collectors.toList());
    tables.removeIf(excludeTables::contains);
    return tables;
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

  /**
   * Get all trigger names.
   *
   * @param databaseName
   * @return List trigger names
   */
  public List<String> getAllTriggers(String databaseName) {
    String sql = this.sqlGetAllTriggers().replace(DB_NAME_EXPRESSION, databaseName);
    return this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("trigger")
    );
  }

  /**
   * get all procedure names.
   *
   * @param databaseName
   * @return List procedure names
   */
  public List<String> getAllProcedures(String databaseName) {
    String sql = this.sqlGetAllProcedures().replace(DB_NAME_EXPRESSION, databaseName);
    return this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("Name")
    );
  }

  /**
   * get all functions.
   *
   * @param databaseName
   * @return list function
   */
  public List<String> getAllSqlFunctions(String databaseName) {
    String sql = this.sqlGetAllFunctions().replace(DB_NAME_EXPRESSION, databaseName);
    return this.jdbcTemplate.query(
        sql,
        (rs, rowNum) -> rs.getString("Name")
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
   * @return sql get all triggers
   */
  protected abstract String sqlGetAllTriggers();

  /**
   * Abstract get sql select all functions from database.
   *
   * @return sql get all functions
   */
  protected abstract String sqlGetAllFunctions();

  /**
   * Abstract get sql select all procedures from database.
   *
   * @return sql get all procedures
   */
  protected abstract String sqlGetAllProcedures();

  /**
   * Expose JdbcTemplate to parent class for using.
   *
   * @return JdbcTemplate
   */
  protected JdbcTemplate getJdbcTemplate() {
    return this.jdbcTemplate;
  }
}
