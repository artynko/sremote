package services

import redis.RedisClient

class RedisClientProvider {
  implicit val akkaSystem = akka.actor.ActorSystem()

  val r = new RedisClient()
  
  def apply() = r

}