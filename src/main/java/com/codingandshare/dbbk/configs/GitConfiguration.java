package com.codingandshare.dbbk.configs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
   * If the folder git is exists then open git with JGit.
   * If the folder git don't exists then clone repository.
   * Checkout the branch with user config.
   * Pull new commit from remote branch.
   *
   * @return {@link Git}
   * @throws RuntimeException - When clone repository failed.
   *                          - Authentication token git failed.
   *                          - The git folder is not repository.
   *                          - The branch user config is invalid.
   */
  @Bean
  Git gitConfig() {
    try {
      File file = new File(this.gitProperties.getGitDir());
      Git git;
      if (file.exists()) {
        git = Git.open(file);
      } else {
        git = Git.cloneRepository()
            .setDirectory(file)
            .setURI(this.gitProperties.getGitRemoteUrl())
            .setCredentialsProvider(this.usernamePasswordCredentialsProvider())
            .call();
      }
      git.checkout().setName(this.gitProperties.getGitBranch()).call();
      git.pull().setCredentialsProvider(this.usernamePasswordCredentialsProvider()).call();
      return git;
    } catch (GitAPIException | IOException e) {
      throw new RuntimeException("Git clone failed", e);
    }
  }
}
