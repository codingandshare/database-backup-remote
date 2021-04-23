package com.codingandshare.dbbk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
    SpringApplication.run(DatabaseBackupApplication.class, args);
  }

}
