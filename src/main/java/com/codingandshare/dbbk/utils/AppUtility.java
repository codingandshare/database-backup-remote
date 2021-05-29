package com.codingandshare.dbbk.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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

  /**
   * The method help to cleanup all files of <pre>pathFolder</pre>.
   * If path folder input is not exists or is not directory then do nothing.
   * If path folder input exists and is directory then clean up all files of this folder.
   *
   * @param pathFolder path folder need to clean up files.
   * @throws NullPointerException when list file name is null
   */
  public static void cleanDirectory(String pathFolder) {
    File folder = new File(pathFolder);
    if (folder.exists() && folder.isDirectory()) {
      String[] fileNames = folder.list();
      Objects.requireNonNull(fileNames);
      for (String fileName : fileNames) {
        new File(
            String.format("%s%s%s", pathFolder, File.separator, fileName)
        ).delete();
      }
    }
  }

  /**
   * The function help to check array is null or is empty.
   *
   * @param array
   * @return - true when array is null or array is empty
   * - false when array is not null and not empty
   */
  public static boolean isEmpty(String[] array) {
    return array == null || array.length == 0;
  }
}
