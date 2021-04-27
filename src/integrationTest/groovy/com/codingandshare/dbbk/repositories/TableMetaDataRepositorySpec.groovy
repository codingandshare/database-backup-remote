package com.codingandshare.dbbk.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Integration test for TableMetaDataRepository
 */
@SpringBootTest
@ActiveProfiles('mariadb-test')
class TableMetaDataRepositorySpec extends Specification {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository

  private static final String DATABASE_NAME = 'test'

  void 'Verify get all tables name'() {
    when: 'get all tables'
    List<String> tables = this.tableMetaDataRepository.getAllTables(DATABASE_NAME)

    then: 'Result list tables as expect'
    noExceptionThrown()
    tables == ['role', 'user', 'user_role']
  }

  void 'Verify get all views'() {
    when: 'get all views'
    List<String> views = this.tableMetaDataRepository.getAllViews(DATABASE_NAME)

    then: 'Result list views as expect'
    noExceptionThrown()
    views == ['user_view']
  }

  void 'Verify get all triggers'() {
    when: 'get all triggers'
    List<String> triggers = this.tableMetaDataRepository.getAllTriggers(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    triggers == ['before_role_delete']
  }

  void 'Verify get database name'() {
    when: 'get database name'
    String name = this.tableMetaDataRepository.databaseName

    then: 'Result database name as expect'
    noExceptionThrown()
    name == DATABASE_NAME
  }

  void 'Verify get all functions'() {
    when: 'get all functions'
    List<String> functions = this.tableMetaDataRepository.getAllSqlFunctions(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    functions == ['getUserName_Func']
  }

  void 'Verify get all procedures'() {
    when: 'get all procedures'
    List<String> procedures = this.tableMetaDataRepository.getAllProcedures(DATABASE_NAME)

    then: 'Result as expect'
    noExceptionThrown()
    procedures == ['GetUserName']
  }
}
