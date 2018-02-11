package controllers

import domain.Audio
import scala.collection.JavaConversions._
import javax.inject.Inject
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.Success
import services.ContentDirectoryService
import scala.util.Random
import services.RedisClientProvider
import scala.concurrent.Promise

case class AudioEntry(id: AudioId, url: String, title: String, artist: String, thumbnailUrl: String)
case class AudioId(containerId: String, audioId: String)

class AudioRepository @Inject() (val cds: ContentDirectoryService, val rs: RedisClientProvider) {
  val r = new Random()
  val allPlaylistId = "A_PL^PL_10"
  val entries = collection.mutable.Map[AudioId, AudioEntry]()

  def getRandomEntry(): AudioEntry = {
    entries.toList(r.nextInt(entries.size))._2
  }
  
  def addToFavorites(containerId: String, audioId: String) = {
    println(s"add favorite $audioId")
    rs().sadd("a_favorites", audioId)
  }

  def removeFromFavorites(containerId: String, audioId: String) = {
    println(s"remove favorite $audioId")
    rs().srem("a_favorites", audioId)
  }

  def getFavorites(): Future[List[AudioEntry]] = {
    // retrieve all favorites from redis
    rs().smembers("a_favorites") map { ids =>
      val m = ids map (id => entries(AudioId(allPlaylistId, id.utf8String))) // TODO: if it doesn't exist anymore remove from favs
      m.toList
    } 
  }

  def getAudioEntry(containerId: String, audioId: String): AudioEntry = {
    entries(AudioId(containerId, audioId))
  }

  private def listContainerById(containerId: String): Future[List[Audio]] = {
    val list = collection.mutable.ListBuffer[Audio]()
    val promise = scala.concurrent.Promise[List[Audio]]()
    cds.browse(containerId, { content =>
      println(content.getItems().size())
      val list = content.getItems().toList.map { i =>
        val id = i.getId().replace(containerId + "^", "");
        val turl = ""
        //i.getResources().get(1).getValue()
        entries += AudioId(containerId, id) -> AudioEntry(AudioId(containerId, id), i.getResources().get(0).getValue(), i.getTitle(), i.getCreator(), turl) // this is a cache
        Audio(containerId, id, i.getTitle(), i.getCreator(), turl) // this is the result !
      }
      promise.complete(Success(list))
    })
    promise.future
  }

  def listPlaylist(): Future[List[Audio]] = {
    listContainerById(allPlaylistId)
  }

}