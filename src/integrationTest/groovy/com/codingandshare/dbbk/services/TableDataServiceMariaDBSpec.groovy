package com.codingandshare.dbbk.services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
/**
 * Integration test for {@link com.codingandshare.dbbk.services.impl.TableDataServiceImpl}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class TableDataServiceMariaDBSpec extends Specification {
  private static final String DB_NAME = 'test'

  @Autowired
  private TableDataService tableDataService

  def 'Verify clean up data file backup'() {
    given: 'Setup data file existing content'
    new File('/tmp/test.sql').write('this is content')

    when: 'clean up data file backup'
    this.tableDataService.setupFileBackup(DB_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.getText().isEmpty()

    cleanup:
    file.delete()
  }
}
