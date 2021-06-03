package com.codingandshare.dbbk.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.codingandshare.dbbk.utils.SqlUtility.*;

/**
 * The abstraction class for <code>TableMetaDataRepository</code>.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
public abstract class TableMetaDataAbstract {

  protected static final String DB_NAME_EXPRESSION = "${DB_NAME}";

  /**
   * The prefix table meta will get from application properties.
   * The use can define it and can set value for <pre>PREFIX_TABLE_META</pre> environment.
   * By default is <pre>CAS_BATCH_</pre>.
   */
  @Value("${spring.batch.table-prefix}")
  protected String prefixTableMeta;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  /**
   * get database name from connection.
   *
   * @return data name
   */
  public String getDatabaseName() {
    return this.jdbcTemplate.execute(Connection::getCatalog);
  }

  /**
   * Using the {@link JdbcTemplate} execute query.
   * The method common for execute query get list string.
   *
   * @param query query need to execute
   * @param column column name need fetch after executed
   * @return List result as string
   */
  protected List<String> queryAsListString(String query, String column) {
    return this.jdbcTemplate.query(
        query,
        (rs, rowNum) -> rs.getString(column)
    );
  }

  /**
   * Using the {@link JdbcTemplate} execute query.
   * The method common for execute query get an string.
   *
   * @param query query need to execute
   * @param column column name need fetch after executed
   * @return List result as string
   */
  protected String queryAsString(String query, String column) {
    return this.jdbcTemplate.queryForObject(
        query,
        (rs, rowNum) -> rs.getString(column)
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
   * Currently only support mariadb/mysql so this version unsupport Types.ROWID for Oracle.
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
        value = sqlNumber(resultSet.getLong(columnPosition));
        break;
      case Types.DECIMAL:
        value = sqlNumber(resultSet.getBigDecimal(columnPosition));
        break;
      case Types.DOUBLE:
        value = sqlNumber(resultSet.getDouble(columnPosition));
        break;
      case Types.REAL:
      case Types.FLOAT:
        float floatValue = resultSet.getFloat(columnPosition);
        value = sqlNumber(resultSet.wasNull() ? null : floatValue);
        break;
      case Types.BLOB:
      case Types.BINARY:
      case Types.VARBINARY:
        value = sqlBytes(resultSet.getBytes(columnPosition));
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
}
