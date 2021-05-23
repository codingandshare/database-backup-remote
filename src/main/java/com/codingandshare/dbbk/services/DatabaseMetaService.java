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
   * @param fileWriter   {@link FileWriter}
   * @param procedures list procedures need to backup.
   * @throws IOException write script to file failed
   */
  void writeScriptCreateProcedure(List<String> procedures, FileWriter fileWriter) throws IOException;
}
