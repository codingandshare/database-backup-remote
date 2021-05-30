package com.codingandshare.dbbk.test.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.codingandshare.dbbk.exceptions.DBBackupException
import com.codingandshare.dbbk.services.StorageService
import com.codingandshare.dbbk.services.impl.LocalStorageService
import com.codingandshare.dbbk.test.utils.BaseSpecification
import com.codingandshare.dbbk.utils.AppUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Stepwise

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.FileTime

/**
 * Unit test for {@link LocalStorageService}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
@Stepwise
class LocalStorageServiceMariaDBSpec extends BaseSpecification {

  @Autowired
  @Qualifier('localStorage')
  private StorageService storageService

  def 'Verify the local storage name'() {
    when: 'Get storage name'
    String name = this.storageService.getBackupStorageName()

    then: 'Result as expect'
    noExceptionThrown()
    name == 'Local storage'
  }

  def 'Verify the local storage store failed'() {
    given: 'Clean folder data_backup'
    new File('/tmp/data_backup').deleteDir()
    ListAppender<ILoggingEvent> logs = setupLogger(LocalStorageService)

    when: 'Store file'
    this.storageService.store()

    then: 'Throw expect as expect'
    DBBackupException e = thrown(DBBackupException)
    e.message == 'Store local file failed'
    e.cause instanceof IOException

    and: 'Logs as expect'
    logs.list.size() == 1
    logs.list.first().level == Level.ERROR
    logs.list.first().message == 'Store local file failed'

    cleanup:
    new File('/tmp/data_backup').mkdirs()
  }

  def 'Verify the local storage service store file successfully'() {
    given: 'Setup data'
    String fileNameBackup = '/tmp/test.sql'
    File backupFile = new File(fileNameBackup)
    backupFile.write('Sql test')

    when: 'store file backup'
    this.storageService.store()

    then: 'Result as expect'
    noExceptionThrown()

    and: 'Check file backup copy to local storage'
    String fileNameExpect = "/tmp/data_backup/test.${AppUtility.formatDate(new Date(), 'yyyy-MM-dd')}.sql"
    File fileExpect = new File(fileNameExpect)
    fileExpect.exists()
    fileExpect.isFile()
    fileExpect.text == backupFile.text

    cleanup:
    backupFile.delete()
    AppUtility.cleanDirectory('/tmp/data_backup')
  }

  def 'Verify local storage cleanup file with retention days'() {
    given: 'Setup files'
    String fileNameBackup = '/tmp/test.sql'
    File backupFile = new File(fileNameBackup)
    backupFile.write('Sql test')
    for (int i = 0; i < 7; i++) {
      Date date = new Date() - i
      String fileNameStore = "/tmp/data_backup/test.${AppUtility.formatDate(date, 'yyyy-MM-dd')}.sql"
      File file = new File(fileNameStore)
      file.write('sql test')
      setLastModifyFile(file.toPath(), date)
    }
    new File('/tmp/data_backup/nhan.txt').createNewFile()
    ListAppender<ILoggingEvent> localStorageLogs = setupLogger(LocalStorageService)


    when: 'Local storage clean up file retention'
    this.storageService.store()

    then: 'Result as expect'
    noExceptionThrown()

    and: 'Check file backup copy to local storage'
    String fileNameExpect = "/tmp/data_backup/test.${AppUtility.formatDate(new Date(), 'yyyy-MM-dd')}.sql"
    File fileExpect = new File(fileNameExpect)
    fileExpect.exists()
    fileExpect.isFile()
    fileExpect.text == backupFile.text

    and: 'Check file after cleanup files'
    File fileFolderBackup = new File('/tmp/data_backup')
    fileFolderBackup.exists()
    fileFolderBackup.isDirectory()
    String[] fileNames = fileFolderBackup.list()
    fileNames.length == 4
    String[] files = fileNames.sort().reverse().findAll { it != 'nhan.txt' }
    for (int i = 0; i < 3; i++) {
      Date date = new Date() - i
      String fileNameStore = "test.${AppUtility.formatDate(date, 'yyyy-MM-dd')}.sql"
      assert files[i] == fileNameStore
    }

    and: 'Check message log as expect'
    localStorageLogs.list.size() == 4
    List<ILoggingEvent> logs = localStorageLogs.list.sort { it.message }
    Date d = new Date() - 6
    for (int i = 0; i < 4; i++) {
      Date date = d + i
      String fileNameStore = "test.${AppUtility.formatDate(date, 'yyyy-MM-dd')}.sql"
      assert logs[i].getLevel() == Level.DEBUG
      assert logs[i].message == "File $fileNameStore is deleted: true"
    }
    cleanup:
    backupFile.delete()
    AppUtility.cleanDirectory('/tmp/data_backup')
  }

  private static void setLastModifyFile(Path path, Date date) {
    BasicFileAttributeView attributes = Files.getFileAttributeView(path, BasicFileAttributeView)
    FileTime fileTime = FileTime.fromMillis(date.time)
    attributes.setTimes(fileTime, fileTime, fileTime)
  }
}
