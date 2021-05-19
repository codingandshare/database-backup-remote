package com.codingandshare.dbbk.repositories;

import com.codingandshare.dbbk.utils.DBBackupConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codingandshare.dbbk.utils.SqlUtility.sqlDate;
import static com.codingandshare.dbbk.utils.SqlUtility.sqlString;
import static com.codingandshare.dbbk.utils.SqlUtility.sqlTime;
import static com.codingandshare.dbbk.utils.SqlUtility.sqlTimestamp;
import static com.codingandshare.dbbk.utils.SqlUtility.SQL_VALUE_NULL;
import static com.codingandshare.dbbk.utils.SqlUtility.sqlBytes;
import static com.codingandshare.dbbk.utils.SqlUtility.sqlNumber;

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
   * Help to build the value for sql insert from {@link ResultSet}.
   *
   * @param resultSet of once record
   * @return sql insert of one record
   * @throws SQLException when build sql insert failed
   */
  public List<String> getValueInsertFromResultSet(ResultSet resultSet) throws SQLException {
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    int columnCount = resultSetMetaData.getColumnCount();
    List<String> values = new ArrayList<>();
    for (int i = 1; i <= columnCount; i++) {
      values.add(this.getValueInsert(resultSet, i));
    }
    return values;
  }

  /**
   * Build value for insert sql depend on <pre>dbType</pre>.
   * Handle for some types: dateTime, string, int, date.
   *
   * @param resultSet      from JDBC
   * @param columnPosition position of column need to build value insert
   * @return value of sql insert
   * @throws SQLException get value insert sql failed
   */
  protected String getValueInsert(ResultSet resultSet, int columnPosition) throws SQLException {
    int dbType = resultSet.getMetaData().getColumnType(columnPosition);
    String value;
    switch (dbType) {
      case Types.BOOLEAN:
      case Types.BIT:
      case Types.DISTINCT:
      case Types.INTEGER:
      case Types.SMALLINT:
      case Types.TINYINT:
        value = sqlNumber(resultSet.getInt(columnPosition));
        break;
      case Types.BIGINT:
      case Types.NUMERIC:
      case Types.ROWID:
        value = sqlNumber(resultSet.getLong(columnPosition));
        break;
      case Types.DECIMAL:
        value = sqlNumber(resultSet.getBigDecimal(columnPosition));
        break;
      case Types.DOUBLE:
        value = sqlNumber(resultSet.getDouble(columnPosition));
        break;
      case Types.FLOAT:
        value = sqlNumber(resultSet.getFloat(columnPosition));
        break;
      case Types.BLOB:
      case Types.BINARY:
        value = sqlBytes(resultSet.getBytes(columnPosition));
        break;
      case Types.NULL:
        value = SQL_VALUE_NULL;
        break;
      case Types.TIME:
      case Types.TIME_WITH_TIMEZONE:
        value = sqlTime(resultSet.getTime(columnPosition), this.getTimeFormat());
        break;
      case Types.TIMESTAMP:
      case Types.TIMESTAMP_WITH_TIMEZONE:
        value = sqlTimestamp(resultSet.getTimestamp(columnPosition), this.getDateTimeFormat());
        break;
      case Types.DATE:
        value = sqlDate(resultSet.getDate(columnPosition), this.getDateFormat());
        break;
      default:
        return sqlString(resultSet.getString(columnPosition));
    }
    return value;
  }

  /**
   * Help to get all columns from {@link ResultSet}.
   *
   * @param resultSet
   * @return list columns
   * @throws SQLException
   */
  public List<String> getAllColumnFromResultSet(ResultSet resultSet) throws SQLException {
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    int columnCount = resultSetMetaData.getColumnCount();
    List<String> columns = new ArrayList<>();
    for (int i = 1; i <= columnCount; i++) {
      columns.add(resultSetMetaData.getColumnName(i));
    }
    return columns;
  }

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
   * Abstract method help to get date format depend on database type.
   *
   * @return date format
   */
  protected abstract String getDateFormat();

  /**
   * Abstract method help to get time format depend on database type.
   *
   * @return time format
   */
  protected abstract String getTimeFormat();

  /**
   * Abstract method help to get date time format depend on database type.
   *
   * @return date time format.
   */
  protected abstract String getDateTimeFormat();

  /**
   * Abstract method to get sql select all tables from database.
   *
   * @return sql get all tables.
   */
  protected abstract String sqlGetAllTables();

  /**
   * Expose JdbcTemplate to parent class for using.
   *
   * @return JdbcTemplate
   */
  protected JdbcTemplate getJdbcTemplate() {
    return this.jdbcTemplate;
  }
}
