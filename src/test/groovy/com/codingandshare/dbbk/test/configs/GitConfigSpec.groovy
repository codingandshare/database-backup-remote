package com.codingandshare.dbbk.test.configs

import com.codingandshare.dbbk.configs.GitConfiguration
import com.codingandshare.dbbk.configs.GitProperties
import com.codingandshare.dbbk.exceptions.DBBackupException
import com.codingandshare.dbbk.test.utils.BaseSpecification
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

/**
 * Unit test for GitConfiguration
 */
class GitConfigSpec extends BaseSpecification {

  private GitConfiguration gitConfiguration
  private GitProperties gitProperties

  def setup() {
    this.gitConfiguration = new GitConfiguration()
    this.gitProperties = new GitProperties()
    this.gitConfiguration.gitProperties = this.gitProperties
  }

  def 'Verify config git authenticate'() {
    given: 'Mock data'
    this.gitProperties.setToken('token')

    when: 'get config authenticate'
    UsernamePasswordCredentialsProvider user = this.gitConfiguration.usernamePasswordCredentialsProvider()

    then: 'Result as exepect'
    noExceptionThrown()
    user.username == GitConfiguration.OAUTH_GIT_USER
    user.password == 'token'.toCharArray()
  }

  def 'Verify git config when gitDir not exists'() {
    given: 'Setup data'
    this.gitProperties.setGitDir('/invalid_folder')

    when: 'get git config'
    this.gitConfiguration.gitConfig()

    then: 'throw DBBackupException exception as expect'
    DBBackupException ex = thrown(DBBackupException)
    ex.message == 'Git dir invalid: /invalid_folder'
  }

  def 'Verify git config when folder is not git repo'() {
    given: 'Setup data'
    new File('/tmp/git-data').mkdirs()
    this.gitProperties.setGitDir('/tmp/git-data')

    when: 'get git config'
    this.gitConfiguration.gitConfig()

    then: 'throw DBBackupException exception as expect'
    DBBackupException ex = thrown(DBBackupException)
    ex.message == 'Git open folder failed'

    cleanup:
    new File('/tmp/git-data').deleteDir()
  }

  def 'Verify git open successfully'() {
    given: 'Setup data'
    File file = new File('/tmp/git-test')
    file.mkdirs()
    Git.init().setDirectory(file).call()
    this.gitProperties.setGitDir('/tmp/git-test')

    when: 'get git config'
    Git git = this.gitConfiguration.gitConfig()

    then: 'Result as expect'
    noExceptionThrown()
    git

    cleanup:
    file.delete()
  }
}
