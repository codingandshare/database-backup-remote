package com.codingandshare.dbbk.test.services

import com.codingandshare.dbbk.services.DatabaseMetaService
import com.codingandshare.dbbk.services.TableDataService
import com.codingandshare.dbbk.test.utils.BaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Unit test for {@link DatabaseMetaService}
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class DatabaseMetaServiceSpec extends BaseSpecification {

  @Autowired
  private DatabaseMetaService databaseMetaService

  @Autowired
  private TableDataService tableDataService

  def 'Verify write script create procedure to file successfully'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'write script to file'
    this.databaseMetaService.writeScriptCreateProcedure(['GetUserName'], fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    println file.text
    file.text == '''-- Script create procedure

DROP PROCEDURE IF EXISTS `GetUserName`;
CREATE DEFINER=`root`@`%` PROCEDURE `GetUserName`( OUT userName VARCHAR (20) )
BEGIN
    SET
userName = 'Nhan Dinh';
END;


'''

    cleanup:
    file.delete()
  }

  def 'Verify write script create procedure empty'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'write script create procedure'
    this.databaseMetaService.writeScriptCreateProcedure([], fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.text.isEmpty()

    cleanup:
    file.delete()
  }
}
