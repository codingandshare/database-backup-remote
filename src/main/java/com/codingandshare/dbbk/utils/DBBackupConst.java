package com.codingandshare.dbbk.utils;

/**
 * The interface contain all constants of application.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
public final class DBBackupConst {
  /**
   * private constructor prevent new this object.
   */
  private DBBackupConst() {
  }

  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String MARIADB = "mariadb";
  public static final String[] DB_TYPE_SUPPORT = new String[]{MARIADB};
}
