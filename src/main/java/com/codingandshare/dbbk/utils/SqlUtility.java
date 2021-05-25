package com.codingandshare.dbbk.utils;

import javax.xml.bind.DatatypeConverter;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * The class Utility help to handle value relate sql syntax.
 *
 * @author Nhan Dinh
 * @since 5/15/21
 **/
public final class SqlUtility {

  public static final String SQL_VALUE_NULL = "NULL";

  /**
   * Private constructor prevent new instance for this object.
   */
  private SqlUtility() {
  }

  /**
   * escape value of sql insert contain single quote.
   * if value is null return {@link #SQL_VALUE_NULL}
   *
   * @param value
   * @return sql string escaped
   */
  public static String sqlString(String value) {
    if (value == null) {
      return SQL_VALUE_NULL;
    }
    return String.format("'%s'", value.replaceAll("'", "''"));
  }

  /**
   * Handle convert {@link java.util.Date} to String with format pattern.
   *
   * @param date
   * @param format pattern format date
   * @return string with format date
   */
  public static String sqlFormatDate(Date date, String format) {
    return String.format("'%s'", AppUtility.formatDate(date, format));
  }

  /**
   * Convert object {@link Date} to string date format on database.
   *
   * @param date
   * @param format
   * @return sql formatted with pattern date
   */
  public static String sqlDate(java.sql.Date date, String format) {
    if (date == null) {
      return SQL_VALUE_NULL;
    }
    return sqlFormatDate(date, format);
  }

  /**
   * Convert object {@link Time} to string time format.
   *
   * @param time
   * @param format
   * @return string time formatted
   */
  public static String sqlTime(Time time, String format) {
    if (time == null) {
      return SQL_VALUE_NULL;
    }
    return sqlFormatDate(time, format);
  }

  /**
   * Convert object {@link Timestamp} to string date time.
   *
   * @param timestamp
   * @param format
   * @return sql string date time
   */
  public static String sqlTimestamp(Timestamp timestamp, String format) {
    if (timestamp == null) {
      return SQL_VALUE_NULL;
    }
    return sqlFormatDate(timestamp, format);
  }

  /**
   * if the value is {@link Number} have value is null will build NULL value in database.
   *
   * @param value
   * @return string NULL
   */
  public static String sqlNumber(Number value) {
    if (value == null) {
      return SQL_VALUE_NULL;
    }
    return String.valueOf(value);
  }

  /**
   * Convert bytes array to string hex binary.
   *
   * @param bytes
   * @return String hex binary
   */
  public static String sqlBytes(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return SQL_VALUE_NULL;
    }
    return DatatypeConverter.printHexBinary(bytes);
  }
}
