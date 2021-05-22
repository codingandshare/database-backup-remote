package com.codingandshare.dbbk.test

import com.codingandshare.dbbk.DatabaseBackupApplication
import com.codingandshare.dbbk.exceptions.ValidateException
import spock.lang.Specification

/**
 * Unit test for DataBaseBackupApplication
 */
class DataBaseBackupApplicationSpec extends Specification {

  void 'Verify validateDBType function when throw ValidateException'() {
    when: 'validate dbType'
    DatabaseBackupApplication.validateDBType(dbType)

    then: 'Throw ValidateException as expect'
    ValidateException e = thrown(ValidateException)
    e.message == messageExpect

    where:
    dbType | messageExpect
    null   | 'Required environment DB_TYPE'
    ''     | 'Required environment DB_TYPE'
    'abc'  | 'Database type abc is invalid'
  }

  def 'Verify validateDBType function successfully'() {
    when: 'validate dbType'
    DatabaseBackupApplication.validateDBType(dbType)

    then: 'No throw exception'
    noExceptionThrown()

    where:
    dbType    | _
    'mariadb' | _
  }
}