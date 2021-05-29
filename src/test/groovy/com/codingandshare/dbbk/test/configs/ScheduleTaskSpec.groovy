package com.codingandshare.dbbk.test.configs

import com.codingandshare.dbbk.configs.ScheduledTasks
import com.codingandshare.dbbk.services.impl.TableDataServiceImpl
import com.codingandshare.dbbk.test.utils.BaseSpecification
import com.codingandshare.dbbk.utils.AppUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
/**
 * Unit test for {@link ScheduledTasks}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class ScheduleTaskSpec extends BaseSpecification {

  @Autowired
  private ScheduledTasks scheduledTasks

  @Autowired
  private TableDataServiceImpl tableDataService

  def 'Schedule job backup database'() {
    when: 'Call schedule job'
    this.scheduledTasks.scheduleBackup()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.isFile()
    file.exists()
    file.name == 'test.sql'
    List<String> lines = file.readLines()
    List<String> expectLines = getClass().getResource('/output/result_test_mariadb.sql').readLines()
    lines.eachWithIndex { String entry, int i ->
      if (i != 0) {
        if (expectLines[i].contains('DATE_BACKUP')) {
          assert expectLines[i].replaceAll('DATE_BACKUP', AppUtility.formatDate(new Date(), 'yyyy-MM-dd')) == entry
        } else {
          assert entry == expectLines[i]
        }
      }
    }

    and: 'Check folder backup storage local'
    File folderBackup = new File('/tmp/data_backup')
    folderBackup.exists()
    folderBackup.isDirectory()
    String[] files = folderBackup.list()
    files.length == 1
    String fileNameStore = "test.${AppUtility.formatDate(new Date(), 'yyyy-MM-dd')}.sql"
    String fileNameBackup = files[0]
    fileNameBackup == fileNameStore
    File fileBackupStorage = new File("/tmp/data_backup/$fileNameStore")
    fileBackupStorage.exists()
    fileBackupStorage.isFile()
    fileBackupStorage.text == file.text

    cleanup:
    file.delete()
    AppUtility.cleanDirectory('/tmp/data_backup')
  }
}
