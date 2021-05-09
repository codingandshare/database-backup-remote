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
  public static final String KEY_TABLE_NAME = "TABLE_NAME";
  public static final String[] META_TABLES = new String[]{
      "CAS_DATABASECHANGELOGLOCK",
      "CAS_DATABASECHANGELOG",
      "CAS_BATCH_STEP_EXECUTION_SEQ",
      "CAS_BATCH_JOB_EXECUTION_SEQ",
      "CAS_BATCH_JOB_SEQ",
      "CAS_BATCH_JOB_INSTANCE",
      "CAS_BATCH_JOB_EXECUTION_PARAMS",
      "CAS_BATCH_JOB_EXECUTION",
      "CAS_BATCH_STEP_EXECUTION",
      "CAS_BATCH_JOB_EXECUTION_CONTEXT",
      "CAS_BATCH_STEP_EXECUTION_CONTEXT",
  };
  public static final String[] DB_TYPE_SUPPORT = new String[]{MARIADB};
}
