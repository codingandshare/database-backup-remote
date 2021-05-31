package com.codingandshare.dbbk.configs;

import com.codingandshare.dbbk.exceptions.DBBackupException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * The class help config clone git repository.
 * The config help create instance JGit.
 * Config authenticate for git with 'oauth2' protocol.
 * If 'app.gitStorage' is true then the user must set all config environments git:
 * - GIT_TOKEN
 * - GIT_BRANCH
 * - GIT_DIR
 *
 * @author Nhan Dinh
 * @since 5/26/21
 **/
@Configuration
@ConditionalOnProperty(havingValue = "true", value = "app.gitStorage")
public class GitConfiguration {

  private static final String OAUTH_GIT_USER = "oauth2";

  /**
   * Load config from properties file.
   */
  @Autowired
  private GitProperties gitProperties;

  /**
   * The method help to create an {@link Bean} using for git authentication.
   * Only support for using personal token with git authenticate.
   *
   * @return {@link UsernamePasswordCredentialsProvider}
   */
  @Bean
  UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider() {
    return new UsernamePasswordCredentialsProvider(
        OAUTH_GIT_USER,
        this.gitProperties.getToken()
    );
  }

  /**
   * Create an {@link Bean} help to config git repository.
   * Detect folder <pre>app.git.gitDir</pre> is git repository or no
   *
   * @return {@link Git}
   * @throws DBBackupException - Authentication token git failed.
   *                           - The git folder is not repository.
   *                           - The branch user config is invalid.
   */
  @Bean
  Git gitConfig() {
    try {
      File file = new File(this.gitProperties.getGitDir());
      if (file.exists()) {
        File fileRepoGit = new File(
            String.format("%s%s", this.gitProperties.getGitDir(), File.separator),
            ".git"
        );
        return Git.open(fileRepoGit);
      } else {
        throw new DBBackupException(String.format("Git dir invalid: %s", this.gitProperties.getGitDir()));
      }
    } catch (IOException e) {
      throw new DBBackupException("Git open folder failed", e);
    }
  }
}
