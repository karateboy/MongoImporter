package com.wecc
import org.apache.poi.openxml4j.opc._
import org.apache.poi.xssf.usermodel._
import com.github.nscala_time.time.Imports._
import java.io._
import java.nio.file.Files
import java.nio.file._
import org.apache.poi.ss.usermodel._

object ExcelUtility {
  def openExcel(reportFilePath: String) = {
    //Open Excel
    val pkg = OPCPackage.open(new FileInputStream(reportFilePath))
    val wb = new XSSFWorkbook(pkg);

    (reportFilePath, pkg, wb)
  }

  def finishExcel(reportFilePath: Path, pkg: OPCPackage, wb: XSSFWorkbook) = {
    val out = new FileOutputStream(reportFilePath.toAbsolutePath().toString());
    wb.write(out);
    out.close();
    pkg.close();

    new File(reportFilePath.toAbsolutePath().toString())
  }  
  
  def openSecData() = {
    val (reportFilePath, pkg, wb) = openExcel("tidyReport.xlsx")
    val evaluator = wb.getCreationHelper().createFormulaEvaluator()
    val format = wb.createDataFormat();
    
    val sheet = wb.getSheetAt(0)
  }

}