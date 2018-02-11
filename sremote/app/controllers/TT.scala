package controllers

import redis.RedisClient
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.Success
import scala.util.Failure

object TT extends App {
  implicit val akkaSystem = akka.actor.ActorSystem()
  
  val client = RedisClient();
  client.set("key", 123)
  client.get("key") map {
    case Some(x) => println(x.utf8String)
    case None =>
  }
  client.hmset("m", Map[String, String]("id" -> "123", "title" -> "this is the title!!"))
  client.hgetall("mm") map { m =>
    println(m("title").utf8String)
  }
  client.del("a_favorites")
  client.sadd("a_favorites", "S_1048", "S_1513")
  client.smembers("a_favorites") map { m => m foreach ( x => println(x.utf8String))
  val l = client.smembers("a_favorites").map( ids => ids)
  println(l)
  }
  
  

}