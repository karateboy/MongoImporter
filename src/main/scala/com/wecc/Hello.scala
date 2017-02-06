package com.wecc
import scala.concurrent.ExecutionContext.Implicits.global
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

    if (args.length != 3) {
      Console.println("start end shift")
      return
    }

    val start = DateTime.parse(args(0), DateTimeFormat.forPattern("YYYY/MM/dd hh:mm:ss"))
    val startB: BsonDateTime = new BsonDateTime(start.getMillis)
    val end = DateTime.parse(args(1), DateTimeFormat.forPattern("YYYY/MM/dd hh:mm:ss"))
    val endB: BsonDateTime = new BsonDateTime(end.getMillis)
    val shift = args(2).toInt

    val f = collection.find(and(gte("_id", startB), lt("_id", endB))).toFuture()
    val retListF =
      for (docs <- f) yield {
        docs map {
          doc =>
            (doc("_id").asDateTime().getValue, doc("SO2").asDocument().get("v").asDouble().getValue)
        }
      }
    import scala.concurrent._
    import scala.util._
    
    val updateF =
      for(retList <- retListF) yield{
        retList map {
          ret =>
            val time = ret._1 + shift* 1000
            val so2 = ret._2
            
            collection.updateOne(equal("_id", new BsonDateTime(time)), Updates.set("SO2.v", so2)).toFuture()
        }
      }
    
    import scala.concurrent._
    val ff = updateF.flatMap { x => Future.sequence(x) }
    
    val ret = Await.ready(ff, scala.concurrent.duration.Duration.Inf).value.get
    Console.println("Finished...")
    mongoClient.close()
  }
}
