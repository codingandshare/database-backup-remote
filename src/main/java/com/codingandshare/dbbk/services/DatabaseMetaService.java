package com.codingandshare.dbbk.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * The interface will provide some functions write script create procedure, function, trigger to file.
 * Help to build the script create procedure, function, trigger for backup script.
 * The interface will be executed by {@link DatabaseMetaTasklet} step.
 *
 * @author Nhan Dinh
 * @since 5/23/21
 **/
public interface DatabaseMetaService {

  /**
   * Generate the script create foreach procedure and write it to file.
   *
   * @param fileWriter {@link FileWriter}
   * @param procedures list procedures need to backup.
   * @throws IOException write script to file failed
   */
  void writeScriptCreateProcedures(List<String> procedures, FileWriter fileWriter) throws IOException;

  /**
   * Generate the script create foreach function and write it to file.
   *
   * @param functions  list functions need to backup.
   * @param fileWriter {@link FileWriter}
   * @throws IOException write script to file failed
   */
  void writeScriptCreateFunctions(List<String> functions, FileWriter fileWriter) throws IOException;

  /**
   * Generate the script create foreach trigger and write it to file.
   *
   * @param triggers   list triggers need to backup
   * @param fileWriter {@link FileWriter}
   * @throws IOException write script to file failed
   */
  void writeScriptCreateTriggers(List<String> triggers, FileWriter fileWriter) throws IOException;

  /**
   * Generate the script create foreach view and write it to file.
   *
   * @param views      list views need to backup
   * @param fileWriter {@link FileWriter}
   * @throws IOException write script to file failed
   */
  void writeScriptCreateViews(List<String> views, FileWriter fileWriter) throws IOException;

  /**
   * Write the footer for script backup.
   *
   * @param fileWriter {@link FileWriter}
   * @throws IOException write to file failed
   */
  void writeScriptBackupFooter(FileWriter fileWriter) throws IOException;
}
