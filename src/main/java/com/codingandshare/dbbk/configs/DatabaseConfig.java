package com.codingandshare.dbbk.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * The class to config relate to database connection.
 *
 * @author Nhan Dinh
 * @since 4/25/21
 **/
@Configuration
public class DatabaseConfig {

  @Autowired
  private DataSource dataSource;

  /**
   * Create instance <code>JdbcTemplate</code> bean.
   *
   * @return JdbcTemplate
   */
  @Bean
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(this.dataSource);
  }
}
