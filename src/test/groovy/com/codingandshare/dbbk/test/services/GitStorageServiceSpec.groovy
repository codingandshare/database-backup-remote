package com.codingandshare.dbbk.test.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.codingandshare.dbbk.configs.GitProperties
import com.codingandshare.dbbk.services.impl.GitStorageService
import com.codingandshare.dbbk.test.utils.BaseSpecification
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

/**
 * Unit test for GitStorageService
 */
class GitStorageServiceSpec extends BaseSpecification {

  private GitStorageService gitStorageService
  private Git git
  private GitProperties gitProperties
  private CommitCommand commitCommand

  def setup() {
    File file = new File('/tmp/test.sql')
    file.write('This is test')
    File gitFile = new File('/tmp/git/data-test')
    gitFile.mkdirs()
    this.gitStorageService = new GitStorageService()
    this.gitStorageService.dataBaseName = 'test'
    this.gitStorageService.fileBackup = '/tmp/test.sql'
    this.git = Mock(Git)
    this.gitProperties = new GitProperties()
    this.gitProperties.setGitBranch('main')
    this.gitProperties.setGitDir('/tmp/git/data-test')
    this.gitProperties.setToken('token')
    this.gitStorageService.git = this.git
    this.gitStorageService.gitProperties = this.gitProperties
    this.gitStorageService.usernamePasswordCredentialsProvider = Mock(UsernamePasswordCredentialsProvider)
    this.git.fetch() >> Mock(FetchCommand)
    this.git.fetch().setCredentialsProvider(_) >> Mock(FetchCommand)
    this.git.checkout() >> Mock(CheckoutCommand)
    this.git.checkout().setName(_) >> Mock(CheckoutCommand)
    this.git.pull() >> Mock(PullCommand)
    this.git.pull().setCredentialsProvider(_) >> Mock(PullCommand)
    this.git.add() >> Mock(AddCommand)
    this.git.add().addFilepattern(_) >> Mock(AddCommand)
    commitCommand = Mock(CommitCommand)
    this.git.commit() >> commitCommand
    commitCommand.setMessage(_) >> commitCommand
    commitCommand.setAll(_) >> commitCommand
  }

  def 'Verify store git storage throw GitApiException'() {
    given: 'Mock data'
    PushCommand pushCommand = Mock(PushCommand)
    this.git.push() >> pushCommand
    pushCommand.setCredentialsProvider(_) >> pushCommand
    pushCommand.call() >> {
      throw new TransportException('Git push failed')
    }
    ListAppender<ILoggingEvent> log = setupLogger(GitStorageService)

    when: 'git store'
    this.gitStorageService.store()

    then: 'result as expect'
    noExceptionThrown()

    and: 'Log message failed'
    log.list.size() == 1
    log.list.first().level == Level.ERROR
    log.list.first().message == 'Git store failed'

    and: 'File copied to git dir'
    new File('/tmp/test.sql').text == new File('/tmp/git/data-test/test.sql').text
  }

  def 'Verify store git successfully'() {
    given: 'Mock data'
    PushCommand pushCommand = Mock(PushCommand)
    this.git.push() >> pushCommand
    pushCommand.setCredentialsProvider(_) >> pushCommand
    ListAppender<ILoggingEvent> log = setupLogger(GitStorageService)

    when: 'git store'
    this.gitStorageService.store()

    then: 'result as expect'
    noExceptionThrown()

    and: 'Log message not write'
    log.list.size() == 0

    and: 'File copied to git dir'
    new File('/tmp/test.sql').text == new File('/tmp/git/data-test/test.sql').text
  }

  def 'Verify store git throw IOException'() {
    given: 'Mock data'
    PushCommand pushCommand = Mock(PushCommand)
    this.git.push() >> pushCommand
    pushCommand.setCredentialsProvider(_) >> pushCommand
    this.gitProperties.setGitDir('/invalid_folder')
    ListAppender<ILoggingEvent> log = setupLogger(GitStorageService)

    when: 'Store git'
    this.gitStorageService.store()

    then: 'Result no throw exception'
    noExceptionThrown()

    and: 'Log message error'
    log.list.size() == 1
    log.list.first().level == Level.ERROR
    log.list.first().message == 'Can\'t copy file to git repo'
  }

  def 'Verify name git storage'() {
    when: 'get name git store'
    String name = this.gitStorageService.getBackupStorageName()

    then: 'Result as expect'
    noExceptionThrown()
    name == 'Git storage'
  }

  def cleanup() {
    new File('/tmp/test.sql').delete()
    new File('/tmp/git/data-test').deleteDir()
  }
}
