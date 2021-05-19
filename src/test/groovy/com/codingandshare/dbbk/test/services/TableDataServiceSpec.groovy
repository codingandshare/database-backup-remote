package com.codingandshare.dbbk.test.services


import com.codingandshare.dbbk.exceptions.DBBackupException
import com.codingandshare.dbbk.services.impl.TableDataServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Integration test for {@link TableDataServiceImpl}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class TableDataServiceSpec extends Specification {
  private static final String DB_NAME = 'test'

  @Autowired
  private TableDataServiceImpl tableDataService

  def 'Verify clean up data file backup'() {
    given: 'Setup data file existing content'
    new File('/tmp/test.sql').write('this is content')

    when: 'clean up data file backup'
    FileWriter fileWriter = this.tableDataService.setupFileBackup(DB_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    fileWriter
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.getText().isEmpty()

    cleanup:
    file.delete()
  }

  def 'Verify clean data file backup failed'() {
    given: 'Setup folder not found'
    this.tableDataService.storageFolder = '/nhan'

    when: 'Clean up data file backup'
    this.tableDataService.setupFileBackup(DB_NAME)

    then: 'throw exception as expcet'
    DBBackupException e = thrown(DBBackupException)
    e.message == "Clean up file /nhan/${DB_NAME}.sql failed"
  }
}
