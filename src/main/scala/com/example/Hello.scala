package com.example
import org.mongodb.scala._

object Hello {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("logger");
  val collection = database.getCollection("sec_data")
  
  def main(args: Array[String]): Unit = {
    println("Hello, world!")
  }
}
