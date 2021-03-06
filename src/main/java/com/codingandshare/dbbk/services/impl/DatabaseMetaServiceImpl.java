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
  public void writeScriptCreateProcedures(List<String> procedures, FileWriter fileWriter) throws IOException {
    if (!procedures.isEmpty()) {
      fileWriter.write("-- Script create procedure\n\n");
      for (String procedure : procedures) {
        String scriptDropProcedure = this.tableMetaDataRepository.generateSqlDropIfExistsProcedure(procedure);
        fileWriter.write(scriptDropProcedure);
        fileWriter.write("\n");
        String scriptCreateProcedure = this.tableMetaDataRepository.generateScriptCreateProcedure(procedure);
        fileWriter.write(scriptCreateProcedure);
        fileWriter.write(";\n");
      }
      fileWriter.write("\n");
      fileWriter.flush();
    }
  }

  /**
   * Generate the script create foreach function and write it to file.
   *
   * @param functions  list functions need to backup.
   * @param fileWriter {@link FileWriter}
   * @throws IOException write script to file failed
   */
  @Override
  public void writeScriptCreateFunctions(List<String> functions, FileWriter fileWriter) throws IOException {
    if (!functions.isEmpty()) {
      fileWriter.write("-- Script create functions\n\n");
      for (String function : functions) {
        String scriptDropFunction = this.tableMetaDataRepository.generateSqlDropIfExistsFunction(function);
        fileWriter.write(scriptDropFunction);
        fileWriter.write("\n");
        String scriptCreateFunction = this.tableMetaDataRepository.generateScriptCreateFunction(function);
        fileWriter.write(scriptCreateFunction);
        fileWriter.write(";\n");
      }
      fileWriter.write("\n");
      fileWriter.flush();
    }
  }

  /**
   * Generate the script create foreach trigger and write it to file.
   *
   * @param triggers   list triggers need to backup
   * @param fileWriter {@link FileWriter}
   * @throws IOException write script to file failed
   */
  @Override
  public void writeScriptCreateTriggers(List<String> triggers, FileWriter fileWriter) throws IOException {
    if (!triggers.isEmpty()) {
      fileWriter.write("-- Script create triggers\n\n");
      for (String trigger : triggers) {
        String scriptDropTrigger = this.tableMetaDataRepository.generateSqlDropIfExistsTrigger(trigger);
        fileWriter.write(scriptDropTrigger);
        fileWriter.write("\n");
        String scriptCreateTrigger = this.tableMetaDataRepository.generateScriptCreateTrigger(trigger);
        fileWriter.write(scriptCreateTrigger);
        fileWriter.write(";\n");
      }
      fileWriter.write("\n");
      fileWriter.flush();
    }
  }

  /**
   * Generate the script create foreach view and write it to file.
   *
   * @param views      list views need to backup
   * @param fileWriter {@link FileWriter}
   * @throws IOException write script to file failed
   */
  @Override
  public void writeScriptCreateViews(List<String> views, FileWriter fileWriter) throws IOException {
    if (!views.isEmpty()) {
      fileWriter.write("-- Script create views\n\n");
      for (String view : views) {
        String scriptDropView = this.tableMetaDataRepository.generateSqlDropIfExistsView(view);
        fileWriter.write(scriptDropView);
        fileWriter.write("\n");
        String scriptCreateView = this.tableMetaDataRepository.generateScriptCreateView(view);
        fileWriter.write(scriptCreateView);
        fileWriter.write(";\n");
      }
      fileWriter.write("\n");
      fileWriter.flush();
    }
  }

  /**
   * Write the footer for script backup.
   *
   * @param fileWriter {@link FileWriter}
   * @throws IOException write to file failed
   */
  @Override
  public void writeScriptBackupFooter(FileWriter fileWriter) throws IOException {
    fileWriter.write(this.tableMetaDataRepository.generateScriptBackupFooter());
    fileWriter.flush();
  }
}
