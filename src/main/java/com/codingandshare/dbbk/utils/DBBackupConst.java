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

  public static final String KEY_TABLE = "tables";
  public static final String KEY_DATABASE = "database";

  /**
   * The list tables meta of this service.
   * List table will exclude that when backup database.
   */
  public static final String[] META_TABLES = new String[]{
      "DATABASECHANGELOGLOCK",
      "DATABASECHANGELOG",
      "STEP_EXECUTION_SEQ",
      "JOB_EXECUTION_SEQ",
      "JOB_SEQ",
      "JOB_INSTANCE",
      "JOB_EXECUTION_PARAMS",
      "JOB_EXECUTION",
      "STEP_EXECUTION",
      "JOB_EXECUTION_CONTEXT",
      "STEP_EXECUTION_CONTEXT",
  };

  /**
   * Constant databse type for mariadb.
   */
  public static final String MARIADB = "mariadb";

  /**
   * The list database type support for current version.
   */
  public static final String[] DB_TYPE_SUPPORT = new String[]{MARIADB};

  /**
   * The value is default for fetch number records into memory.
   */
  public static final int FETCH_SIZE_ROWS = 1000;
}
