package com.wecc
import org.mongodb.scala._
import com.github.nscala_time.time.Imports._
object Hello {
  def main(args: Array[String]): Unit = {
    val mongoClient: MongoClient = MongoClient("mongodb://localhost")
    val database: MongoDatabase = mongoClient.getDatabase("logger");
    val collection = database.getCollection("sec_data")
    import org.mongodb.scala.model.Filters._
    import org.mongodb.scala.model.Updates
    import org.mongodb.scala.bson._
    import org.mongodb.scala.model.UpdateOptions

    try {
      val (excelFilePath, pkg, wb) = ExcelUtility.openExcel("test.xlsx")

      val sheet = wb.getSheetAt(0)
      var rowN = 1
      while (true) {
        val row = sheet.getRow(rowN)
        val timeStr = row.getCell(0).getStringCellValue
        val date = DateTime.parse(timeStr, DateTimeFormat.forPattern("YYYY/MM/dd hh:mm:ss"))
        val so2 = row.getCell(1).getNumericCellValue
        System.console().printf(date.toString() + " " + so2 + "\n")
        val bdt: BsonDateTime = new BsonDateTime(date.getMillis)

        val f = collection.updateOne(equal("_id", bdt), Updates.set("so2.v", so2)).toFuture()
        rowN += 1
      }
    } catch {
      case ex: Throwable =>
        System.console().printf(ex.toString())
    } finally {

      //mongoClient.close()
    }
  }
}
