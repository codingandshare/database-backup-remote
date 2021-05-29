package com.codingandshare.dbbk.test.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
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

  def 'Verify the local storage service store file successfully'() {
    given: 'Setup data'
    String fileNameBackup = '/tmp/test.sql'
    File backupFile = new File(fileNameBackup)
    backupFile.write('Sql test')
    File file = new File('/tmp/data_backup')
    if(!file.exists()) {
      file.mkdirs()
    }

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
    new File('/tmp/data_backup').deleteDir()
  }

  def 'Verify local storage cleanup file with retention days'() {
    given: 'Setup files'
    String fileNameBackup = '/tmp/test.sql'
    File backupFile = new File(fileNameBackup)
    backupFile.write('Sql test')
    for (int i = 0; i < 7; i++) {
      Date date = new Date() - i
      new File('/tmp/data_backup').mkdirs()
      String fileNameStore = "/tmp/data_backup/test.${AppUtility.formatDate(date, 'yyyy-MM-dd')}.sql"
      File file = new File(fileNameStore)
      file.write('sql test')
      Files.setAttribute(file.toPath(), 'lastModifiedTime', FileTime.fromMillis(date.time))
    }
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
    fileNames.length == 3
    String[] files = fileNames.sort().reverse()
    for (int i = 0; i < 3; i++) {
      Date date = new Date() - i
      String fileNameStore = "test.${AppUtility.formatDate(date, 'yyyy-MM-dd')}.sql"
      assert files[i] == fileNameStore
    }

    and: 'Check message log as expect'
    localStorageLogs.list.size() == 4
    Date d = new Date() - 6
    for (int i = 0; i < 4; i++) {
      Date date = d + i
      String fileNameStore = "test.${AppUtility.formatDate(date, 'yyyy-MM-dd')}.sql"
      assert localStorageLogs.list[i].getLevel() == Level.DEBUG
      assert localStorageLogs.list[i].message == "File $fileNameStore is deleted: true"
    }

    cleanup:
    backupFile.delete()
    new File('/tmp/data_backup').deleteDir()
  }
}