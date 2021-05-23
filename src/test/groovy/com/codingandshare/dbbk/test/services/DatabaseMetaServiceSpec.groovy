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
    this.databaseMetaService.writeScriptCreateProcedures(['GetUserName'], fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
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
    this.databaseMetaService.writeScriptCreateProcedures([], fileWriter)
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

  def 'Verify write script create function to file successfully'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'Write script create function'
    this.databaseMetaService.writeScriptCreateFunctions(['getUserName_Func'], fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.text == '''-- Script create functions

DROP FUNCTION IF EXISTS `getUserName_Func`;
CREATE DEFINER=`root`@`%` FUNCTION `getUserName_Func`() RETURNS varchar(20) CHARSET latin1
    DETERMINISTIC
BEGIN
    DECLARE
userName VARCHAR(20);
    SET
userName = 'Nhan Dinh';
RETURN (userName);
END;

'''

    cleanup:
    file.delete()
  }

  def 'Verify write script create functions when empty function list'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'write script create functions'
    this.databaseMetaService.writeScriptCreateFunctions([], fileWriter)
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


  def 'Verify write script create trigger successfully'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'Write script create trigger'
    this.databaseMetaService.writeScriptCreateTriggers(['before_role_delete'], fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.text == '''-- Script create triggers

DROP TRIGGER IF EXISTS `before_role_delete`;
CREATE DEFINER=`root`@`%` TRIGGER before_role_delete BEFORE DELETE ON role FOR EACH ROW DELETE FROM user_role WHERE role_id = OLD.id;
'''
    cleanup:
    file.delete()
  }

  def 'Verify write script create trigger when empty triggers'() {
    given: 'Setup data'
    FileWriter fileWriter = this.tableDataService.setupFileBackup('test')

    when: 'Write script create trigger'
    this.databaseMetaService.writeScriptCreateTriggers([], fileWriter)
    fileWriter.close()

    then: 'Result as expect'
    noExceptionThrown()
    File file = new File('/tmp/test.sql')
    file.exists()
    file.isFile()
    file.text.isEmpty()
  }
}