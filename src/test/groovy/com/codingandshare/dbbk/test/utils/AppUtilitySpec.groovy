package com.codingandshare.dbbk.test.utils

import com.codingandshare.dbbk.utils.AppUtility
import spock.lang.Specification

/**
 * Unit test for {@link com.codingandshare.dbbk.utils.AppUtility}
 * @author Nhan Dinh
 * @since 5/25/21
 * */
class AppUtilitySpec extends Specification {

  def 'Verify formatDate successfully'() {
    given: 'Setup data'
    Calendar cal = Calendar.getInstance()
    cal.set(2020, 9, 9)

    when: 'call formatDate'
    String dateFormatted = AppUtility.formatDate(cal.getTime(), pattern)

    then: 'Result as expect'
    noExceptionThrown()
    dateFormatted == dateExpect

    where:
    pattern      | dateExpect
    'yyyy-MM-dd' | '2020-10-09'
    'dd-MM-yyyy' | '09-10-2020'
  }
}
