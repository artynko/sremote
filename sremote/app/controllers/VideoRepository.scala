package controllers

import domain.Video
import scala.collection.JavaConversions._
import javax.inject.Inject
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.Success
import services.ContentDirectoryService
import services.RedisClientProvider
import sun.text.normalizer.UTF16

case class VideoEntry(url: String, title: String, thumbnailUrl: String)
case class VideoId(containerId: String, videoId: String)

class VideoRepository @Inject() (val cds: ContentDirectoryService, val rs: RedisClientProvider) {
  val WATCHED_SET_KEY = "s_watched"
  val LA_SET_KEY = "s_la"

  val entries = collection.mutable.Map[VideoId, VideoEntry]()

  def getVideoEntry(containerId: String, videoId: String): VideoEntry = {
    entries(VideoId(containerId, videoId))
  }

  private def listContainerById(containerId: String): Future[List[Video]] = {
    val list = collection.mutable.ListBuffer[Video]()
    val promise = scala.concurrent.Promise[List[Video]]()
    cds.browse(containerId, { content =>
      content.getItems().toList.foreach { i =>
        val id = i.getId().replace(containerId + "^", "");
        val turl = i.getResources().get(1).getValue()
        list += Video(containerId, id, i.getTitle(), turl) // this is the result !
        entries += VideoId(containerId, id) -> VideoEntry(i.getResources().get(0).getValue(), i.getTitle(), turl) // this is a cache
      }
      promise.complete(Success(list.toList))
    })
    promise.future
  }

  def listMovies(): Future[List[Video]] = {
    listContainerById("V_M")
  }

  def markWatched(v: VideoId) = {
    rs().sadd(WATCHED_SET_KEY, v.videoId)
  }

  def listNew(): Future[List[Video]] = { // retrieve all that is new
    listContainerById("V_LA") // retrieve what is in the DLNA container
  }

  def listUnwatched(): Future[List[Video]] = { // retrieve all that is new and unwatched
    val l = listContainerById("V_LA") // retrieve what is in the DLNA container
    val newUpdated = l flatMap { res =>
      val newVideosIds = res.map { v =>
        val sadd = rs().sadd(LA_SET_KEY, v.videoId) // add all of it into the redis key
        val video = entries(VideoId(v.containerId, v.videoId))
        // I know that videoId created by serviio is unique
        // store the info for every single video into redis, when they are retrieved the stored data is used
        sadd flatMap (res => rs().hmset(v.videoId, Map("containerId" -> v.containerId, "videoId" -> v.videoId, "title" -> video.title, "turl" -> video.thumbnailUrl, "url" -> video.url)))
      }
      Future.sequence(newVideosIds)
    }
    // do a diff with watched stuff (the redis key contains everything that was ever in new)
    // TODO: a cleanup job that does diff new vs watched and deletes all from watched that isn't in new
    val unwatchedNewVideos = newUpdated flatMap { _ =>
      val unwatchedNewStuff = rs().sdiff(LA_SET_KEY, WATCHED_SET_KEY)
      unwatchedNewStuff flatMap { unwatchedIds =>
        val videos = unwatchedIds map (id => rs().hgetall(id.utf8String))
        Future.sequence(videos)
      }
    }
    unwatchedNewVideos map { list =>
      list.map { videoHash =>
        // we store them again into the cache
        // which is redundant but who cares
        entries += VideoId(videoHash("containerId").utf8String, videoHash("videoId").utf8String) -> VideoEntry(videoHash("url").utf8String, videoHash("title").utf8String, videoHash("turl").utf8String) // this is a cache
        Video(videoHash("containerId").utf8String, videoHash("videoId").utf8String, videoHash("title").utf8String, videoHash("turl").utf8String)
      }.toList
    }
  }

  def list(): Future[List[Video]] = {
    val list = collection.mutable.ListBuffer[Video]()
    val promise = scala.concurrent.Promise[List[Video]]()
    cds.browse("0", { content =>
      println(content.getItems())
      println(content.getDescMetadata())
      println(content.getContainers().toList.foreach(c => println(c.getId(), c.getTitle())))
      cds.browse("V", { content =>
        println(content.getItems())
        println(content.getDescMetadata())
        println(content.getContainers().toList.foreach(c => println(c.getId(), c.getTitle())))
        cds.browse("V_LA", { content =>
          val list = content.getItems().toList.map { i =>
            println(i.getTitle(), i.getDescMetadata(), i.getId())
            println()
            val id = i.getId().replaceAll("V_LA.", "");
            val turl = i.getResources().get(1).getValue()
            entries += VideoId("V_LA", id) -> VideoEntry(i.getResources().get(0).getValue(), i.getTitle(), turl)
            Video("V_LA", id, i.getTitle(), turl) // this is the result !
          }
          println(content.getDescMetadata())
          println(content.getContainers().toList.foreach(c => println(c.getId(), c.getTitle())))
          promise.complete(Success(list))
        })
      })
    })
    promise.future
  }

}