package com.codingandshare.dbbk.services.impl;

import com.codingandshare.dbbk.repositories.TableMetaDataRepository;
import com.codingandshare.dbbk.services.DatabaseMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * The class implement by the interface {@link DatabaseMetaService}.
 *
 * @author Nhan Dinh
 * @since 5/23/21
 **/
@Service
public class DatabaseMetaServiceImpl implements DatabaseMetaService {

  @Autowired
  private TableMetaDataRepository tableMetaDataRepository;

  /**
   * Generate the script create foreach procedure and write it to file.
   *
   * @param fileWriter {@link FileWriter}
   * @param procedures list procedures need to backup.
   * @throws IOException write script to file failed
   */
  @Override
  public void writeScriptCreateProcedure(List<String> procedures, FileWriter fileWriter) throws IOException {
    if (!procedures.isEmpty()) {
      fileWriter.write("-- Script create procedure\n\n");
    }
    for (String procedure : procedures) {
      String scriptDropProcedure = this.tableMetaDataRepository.generateSqlDropIfExistsProcedure(procedure);
      fileWriter.write(scriptDropProcedure);
      fileWriter.write("\n");
      String scriptCreateProcedure = this.tableMetaDataRepository.generateScriptCreateProcedure(procedure);
      fileWriter.write(scriptCreateProcedure);
      fileWriter.write(";\n");
    }
    if (!procedures.isEmpty()) {
      fileWriter.write("\n\n");
    }
    fileWriter.flush();
  }
}
