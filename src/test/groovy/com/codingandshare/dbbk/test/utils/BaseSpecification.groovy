package com.codingandshare.dbbk.test.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import spock.lang.Specification

/**
 * The base class help to test.
 */
class BaseSpecification extends Specification {

  /**
   * The function help to test message log in application.
   * @param clazz
   * @return {@link org.apache.logging.log4j.spi.LoggerAdapter}
   */
  ListAppender<ILoggingEvent> setupLogger(Class<?> clazz) {
    Logger logger = LoggerFactory.getLogger(clazz) as Logger
    logger.setLevel(Level.ALL)
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>()
    listAppender.start()
    logger.addAppender(listAppender)
    listAppender
  }
}
