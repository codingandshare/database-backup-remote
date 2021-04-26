package com.codingandshare.dbbk;

import com.codingandshare.dbbk.exceptions.ValidateException;
import com.codingandshare.dbbk.utils.DBBackupConst;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

/**
 * Main class to start Service.
 *
 * @author Nhan Dinh
 * @since 4/23/21
 **/
@SpringBootApplication
public class DatabaseBackupApplication {

  /**
   * Main function to start service.
   *
   * @param args input parameters from command line.
   */
  public static void main(String[] args) {
    String dbType = System.getenv(DBBackupConst.SPRING_PROFILES_ACTIVE);
    try {
      validateDBType(dbType);
      SpringApplication.run(DatabaseBackupApplication.class, args);
    } catch (ValidateException e) {
      System.err.println("Validate error");
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * validate the dbType from env.
   *
   * @param dbType get from env
   * @throws ValidateException when dbType is invalid.
   */
  static void validateDBType(String dbType) throws ValidateException {
    if (dbType == null || dbType.isEmpty()) {
      throw new ValidateException("Required environment DB_TYPE");
    }
    if (!Arrays.asList(DBBackupConst.DB_TYPE_SUPPORT).contains(dbType)) {
      throw new ValidateException(String.format("Database type %s is invalid", dbType));
    }
  }
}
