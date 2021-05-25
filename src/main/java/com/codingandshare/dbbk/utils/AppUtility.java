package com.codingandshare.dbbk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class contain all method utility of application.
 *
 * @author Nhan Dinh
 * @since 5/25/21
 **/
public final class AppUtility {

  /**
   * The private constructor help to prevent new instance for this class.
   */
  private AppUtility() {
  }

  /**
   * The method convert {@link Date} to string with pattern date.
   * Example pattern format date: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd.
   *
   * @param date    input date
   * @param pattern pattern format date
   * @return date formatted
   */
  public static String formatDate(Date date, String pattern) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.format(date);
  }
}
