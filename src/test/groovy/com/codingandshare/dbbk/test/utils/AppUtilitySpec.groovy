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

  def 'Verify clean up folder successfully'() {
    given: 'Setup data '
    File folder = new File('/tmp/nhanabc')
    folder.mkdirs()
    new File('/tmp/nhanabc/nhan1.txt').createNewFile()
    new File('/tmp/nhanabc/nhan2.txt').createNewFile()

    when: 'clean up folder'
    AppUtility.cleanDirectory('/tmp/nhanabc')

    then: 'Result as expect'
    noExceptionThrown()

    and: 'All files in folder cleanup'
    folder.list().length == 0

    cleanup:
    folder.deleteDir()
  }

  def 'Verify clean up folder path folder is file'() {
    given: 'Setup data'
    File file = new File('/tmp/nhan')
    file.createNewFile()

    when: 'clean up folder'
    AppUtility.cleanDirectory('/tmp/nhan')

    then: 'No throw exception'
    noExceptionThrown()

    cleanup:
    file.delete()
  }

  def 'Verify clean up folder path folder is invalid'() {
    when: 'clean up folder'
    AppUtility.cleanDirectory('/tmp/nhan1')

    then: 'No throw exception'
    noExceptionThrown()
  }

  def 'Verify is empty array'() {
    when: 'check empty array'
    String[] array = new String[arrayInput ? arrayInput.size() : 0]
    if (arrayInput) {
      arrayInput.toArray(array)
    }
    boolean result = AppUtility.isEmpty(array)

    then: 'Result as expect'
    noExceptionThrown()
    result == resultExpect

    where:
    arrayInput | resultExpect
    ['ab']     | false
    null       | true
    []         | true
  }
}
