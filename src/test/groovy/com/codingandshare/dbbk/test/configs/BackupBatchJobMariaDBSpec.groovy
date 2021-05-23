package com.codingandshare.dbbk.test.configs

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.codingandshare.dbbk.repositories.TableMetaDataRepository
import com.codingandshare.dbbk.services.BackupTableDataBackupTasklet
import com.codingandshare.dbbk.services.ReadMetaTasklet
import com.codingandshare.dbbk.test.utils.BaseSpecification
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Unit test for spring batch backup job.
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class BackupBatchJobMariaDBSpec extends BaseSpecification {

  @Autowired
  private JobLauncher jobLauncher

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository

  @Autowired
  @Qualifier('backupJob')
  private Job backJob

  def 'Launch backup job successfully'() {
    given: 'Setup data'
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("JobID", String.valueOf(System.currentTimeMillis()))
        .toJobParameters()
    ListAppender<ILoggingEvent> logReadMetaTable = this.setupLogger(ReadMetaTasklet)
    ListAppender<ILoggingEvent> logBackupTasklet = this.setupLogger(BackupTableDataBackupTasklet)
    List<String> tables = this.tableMetaDataRepository.getAllTables(this.tableMetaDataRepository.getDatabaseName())

    when: 'launch backup job'
    JobExecution jobExecution = this.jobLauncher.run(this.backJob, jobParameters)
    Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions()
    ExitStatus exitStatus = jobExecution.getExitStatus()

    then: 'Result as expect'
    noExceptionThrown()
    actualStepExecutions.size() == 2
    exitStatus.exitCode == 'COMPLETED'
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

    and: 'Log message for ReadMetaDataTable as expect'
    logReadMetaTable.list.size() == 3
    logReadMetaTable.list.first().level == Level.INFO
    logReadMetaTable.list.first().message == 'Read meta data for backup is starting...'
    logReadMetaTable.list[1].level == Level.INFO
    logReadMetaTable.list[1].message == 'Read meta data for backup executed.'
    logReadMetaTable.list[2].level == Level.INFO
    logReadMetaTable.list[2].message == 'Read meta data for backup finished.'

    and: 'Log message for BackupTableDataBackupTasklet as expect'
    List<ILoggingEvent> logs = logBackupTasklet.list
    logs.size() == 4 + (tables.size() * 2)
    logs.first().level == Level.INFO
    logs.first().message == 'Execute backup data starting...'
    logs[1].level == Level.DEBUG
    logs[1].message == 'Generate header script starting...'
    logs[2].level == Level.DEBUG
    logs[2].message == 'Generate header script done.'
    int j = 3
    for (int i = 0; i < tables.size(); i++) {
      String tableName = tables.get(i)
      assert logs[j].level == Level.DEBUG
      assert logs[j].message == "Backup for table $tableName starting..."
      assert logs[j + 1].level == Level.DEBUG
      assert logs[j + 1].message == "Backup for table $tableName done."
      j = j + 2
    }
    logs[tables.size() * 2 + 3].level == Level.INFO
    logs[tables.size() * 2 + 3].message == 'Execute backup data done.'

    cleanup:
    file.delete()
  }
}
