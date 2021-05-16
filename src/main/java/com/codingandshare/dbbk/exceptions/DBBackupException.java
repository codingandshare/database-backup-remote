package com.codingandshare.dbbk.exceptions;

/**
 * Capture exception when service error.
 *
 * @author Nhan Dinh
 * @since 5/15/21
 **/
public class DBBackupException extends RuntimeException {

  /**
   * Constructor to new instance for <code>DBBackupException</code>.
   *
   * @param message
   */
  public DBBackupException(String message) {
    super(message);
  }
}
