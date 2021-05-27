package com.codingandshare.dbbk.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The class help to get config for git storage backup file.
 * If the property <code>app.gitStorage</code> is true then this class will get all config.
 * If the property <code>app.gitStorage</code> is false then this class nothing.
 * The version only support authentication git with personal token.
 *
 * @author Nhan Dinh
 * @since 5/27/21
 **/
@Getter
@Setter
@Configuration
@ConditionalOnProperty(havingValue = "true", value = "app.gitStorage")
@ConfigurationProperties(prefix = "app.git")
public class GitProperties {

  private String token;
  private String gitRemoteUrl;
  private String gitBranch;
  private String gitDir;
}
