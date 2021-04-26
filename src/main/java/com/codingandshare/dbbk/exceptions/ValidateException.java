package com.codingandshare.dbbk.exceptions;

/**
 * The exception to capture error relate to validate.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
public class ValidateException extends RuntimeException {

  /**
   * Constructor to new instance for <code>ValidateException</code>.
   *
   * @param message
   */
  public ValidateException(String message) {
    super(message);
  }
}
