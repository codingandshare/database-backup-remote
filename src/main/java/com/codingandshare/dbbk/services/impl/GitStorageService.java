package com.codingandshare.dbbk.services.impl;

import com.codingandshare.dbbk.configs.GitProperties;
import com.codingandshare.dbbk.services.AbstractStorageService;
import com.codingandshare.dbbk.services.StorageService;
import com.codingandshare.dbbk.utils.AppUtility;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

/**
 * The class is implement for {@link StorageService} with Git.
 * Handle business push file backup sql to git repository.
 * Condition for init bean {@link Service} when app.gitStorage is true or GIT_STORAGE env set true
 *
 * @author Nhan Dinh
 * @since 5/29/21
 **/
@Slf4j
@Order(1)
@Service(value = "gitStorage")
@ConditionalOnProperty(havingValue = "true", value = "app.gitStorage")
public class GitStorageService extends AbstractStorageService implements StorageService {

  /**
   * Get instance {@link Git} from config class.
   */
  @Autowired
  private Git git;

  /**
   * Get config git repository.
   */
  @Autowired
  private GitProperties gitProperties;

  /**
   * Get Credential for authenticate for git repository.
   */
  @Autowired
  private UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider;

  /**
   * The method handle get file backup push to git repository.
   * The branch git config by user.
   * The user must set environments:
   * - GIT_TOKEN: token personal generate on git repository.
   * - GIT_REMOTE_URL: git remote url to push file.
   * - GIT_BRANCH: branch user want to push file.
   * - GIT_DIR: the folder store git repository on local server.
   * The steps push file to git server:
   * 1. Fetch all branch to local.
   * 2. checkout branch user config.
   * 3. pull latest commit from remote branch.
   * 4. Copy the file database backup sql to git repo.
   * 5. Git add databaseName.sql file.
   * 6. Git commit with message "Backup completed: yyyy-MM-ddd"
   * 7. Git push commit to git repo.
   *
   * @throws GitAPIException git access repo failed.
   */
  @Override
  public void store() {
    try {
      this.git.fetch().setCredentialsProvider(this.usernamePasswordCredentialsProvider).call();
      this.git.checkout().setName(this.gitProperties.getGitBranch()).call();
      this.git.push().setCredentialsProvider(this.usernamePasswordCredentialsProvider).call();
      String backupFileGit = String.format(
          "%s%s%s.sql",
          this.gitProperties.getGitDir(),
          File.separator,
          this.dataBaseName
      );
      Files.copy(
          new File(this.fileBackup).toPath(),
          new File(backupFileGit).toPath(),
          StandardCopyOption.REPLACE_EXISTING
      );
      String fileNameGitAdding = String.format("%s.sql", this.dataBaseName);
      this.git.add().addFilepattern(fileNameGitAdding).call();
      String commitMessage = String.format(
          "Backup completed: %s",
          AppUtility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")
      );
      CommitCommand commitCommand = this.git
          .commit()
          .setMessage(commitMessage)
          .setAll(true);
      commitCommand.setCredentialsProvider(this.usernamePasswordCredentialsProvider);
      commitCommand.call();
      this.git.push().setCredentialsProvider(this.usernamePasswordCredentialsProvider).call();
    } catch (GitAPIException e) {
      log.error("Git store failed", e);
    } catch (IOException e) {
      log.error("Can't copy file to git repo", e);
    }
  }

  /**
   * The storage name for git storage.
   *
   * @return
   */
  @Override
  public String getBackupStorageName() {
    return "Git storage";
  }
}
