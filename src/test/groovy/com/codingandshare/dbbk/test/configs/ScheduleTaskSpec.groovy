package com.codingandshare.dbbk.test.configs

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.codingandshare.dbbk.configs.ScheduledTasks
import com.codingandshare.dbbk.services.impl.TableDataServiceImpl
import com.codingandshare.dbbk.test.utils.BaseSpecification
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
        assert entry == expectLines[i]
      }
    }

    cleanup:
    file.delete()
  }
}
