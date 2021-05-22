package com.codingandshare.dbbk.test.utils

import com.codingandshare.dbbk.utils.SqlUtility
import spock.lang.Specification

import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Unit test for {@link SqlUtility}
 */
class SqlUtilitySpec extends Specification {

  def 'Verify sql string'() {
    when: 'call sql string'
    String value = SqlUtility.sqlString(input)

    then: 'Result as expect'
    noExceptionThrown()
    value == expectResult

    where:
    input    | expectResult
    null     | SqlUtility.SQL_VALUE_NULL
    "'null'" | "'''null'''"
    "text"   | "'text'"
  }


  def 'Verify format date'() {
    given: 'setup data'
    Date date = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss').parse('2010-09-28 16:02:43')

    when: 'call sql format date'
    String value = SqlUtility.sqlFormatDate(date, format)

    then: 'Result as expect'
    noExceptionThrown()
    value == "'$valueExpect'"

    where:
    format                | valueExpect
    'dd-MM-yyyy'          | '28-09-2010'
    'yyyy-MM-dd HH:mm:ss' | '2010-09-28 16:02:43'
  }

  def 'Verify sql date'() {
    when: 'call sql format sql date'
    String value = SqlUtility.sqlDate(dateInput, 'yyyy-MM-dd')

    then: 'Result as expect'
    noExceptionThrown()
    value == valueExpect

    where:
    dateInput                               | valueExpect
    new java.sql.Date(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        .parse('2010-09-28 16:02:43').time) | "'2010-09-28'"
    null                                    | SqlUtility.SQL_VALUE_NULL
  }

  def 'Verify sql format time'() {
    when: 'call sql format time'
    String value = SqlUtility.sqlTime(dateInput, 'HH:mm:ss')

    then: 'Result as expect'
    noExceptionThrown()
    value == valueExpect

    where:
    dateInput                               | valueExpect
    new Time(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        .parse('2010-09-28 16:02:43').time) | "'16:02:43'"
    null                                    | SqlUtility.SQL_VALUE_NULL
  }

  def 'Verify sql format timestamp'() {
    when: 'call sql format timestamp'
    String value = SqlUtility.sqlTimestamp(input, 'yyyy-MM-dd HH:mm:ss')

    then: 'Result as expect'
    noExceptionThrown()
    value == valueExcept

    where:
    input                                   | valueExcept
    new Timestamp(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        .parse('2010-09-28 16:02:43').time) | "'2010-09-28 16:02:43'"
    null                                    | SqlUtility.SQL_VALUE_NULL
  }

  def 'Verify sql number'() {
    when: 'call sql format number'
    String value = SqlUtility.sqlNumber(input)

    then: 'Result as expect'
    noExceptionThrown()
    value == valueExpect

    where:
    input | valueExpect
    10    | '10'
    null  | SqlUtility.SQL_VALUE_NULL
  }

  def 'Verify sql array bytes'() {
    when: 'call sql bytes'
    String value = SqlUtility.sqlBytes(input)

    then: 'Result as expect'
    noExceptionThrown()
    value == valueExpect

    where:
    input        | valueExpect
    'test'.bytes | '74657374'
    ''.bytes     | SqlUtility.SQL_VALUE_NULL
    null         | SqlUtility.SQL_VALUE_NULL
  }

}
