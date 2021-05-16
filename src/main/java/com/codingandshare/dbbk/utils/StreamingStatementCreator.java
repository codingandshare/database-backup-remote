package com.codingandshare.dbbk.utils;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The class help to handle streaming data for {@link org.springframework.jdbc.core.JdbcTemplate}.
 * The fetch size streaming into memory default is 1000 records.
 * The help control out of memory when result data query large.
 *
 * @author Nhan Dinh
 * @since 5/15/21
 **/
public class StreamingStatementCreator implements PreparedStatementCreator {

  /**
   * The value is number records fetch into memory java head.
   */
  private final int fetchSize;

  /**
   * The query will execute statement.
   */
  private final String sql;

  /**
   * The constructor help init value for {@link #sql} and {@link #fetchSize}.
   *
   * @param sql       query will execute
   * @param fetchSize number record fetch into memory.
   */
  public StreamingStatementCreator(String sql, int fetchSize) {
    this.fetchSize = fetchSize;
    this.sql = sql;
  }

  /**
   * The constructor help init value for {@link #sql}.
   * The fetch size default value is 1000 records.
   *
   * @param sql query will execute
   */
  public StreamingStatementCreator(String sql) {
    this(sql, DBBackupConst.FETCH_SIZE_ROWS);
  }

  /**
   * The method implement for {@link PreparedStatementCreator#createPreparedStatement(Connection)}.
   * Help to create statement streaming data with {@link #fetchSize} into memory.
   *
   * @param con
   * @return
   * @throws SQLException
   */
  @Override
  public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    final PreparedStatement statement = con.prepareStatement(
        sql,
        ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_READ_ONLY
    );
    statement.setFetchSize(this.fetchSize);
    return statement;
  }
}
