package services

import org.teleal.cling.model.meta.RemoteService
import org.teleal.cling.UpnpService
import extensions.PlaybackService
import upnp.UpnpDeviceSubscriber
import org.teleal.cling.model.meta.RemoteDevice
import org.teleal.cling.model.types.ServiceId
import javax.inject.Singleton
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Success
import scala.concurrent.duration._
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class AVTransportService extends UpnpDeviceSubscriber {
  val playbackService = new PlaybackService() // uses the java classes
  var service: Option[RemoteService] = None
  var upnpService: Option[UpnpService] = None

  def deviceConnected(upnp: UpnpService, device: RemoteDevice) {
    upnpService match {
      case None =>
        println("upnpService set!")
        upnpService = Some(upnp)
        println(upnpService)
      case _ =>
    }
    // retrieve the service I am interested in
    println("device connected: " + device.getDetails().getFriendlyName())
    if (device.getDetails().getFriendlyName().contains("XBMC")) {
      val player = device.findService(new ServiceId("upnp-org", "AVTransport"))
      if (player != null) {
        service = Some(player)
        println("XBMC player set")
      }
    }
  }

  def subscribeToLastChange(f: Map[String, Any] => Unit) = {
    playbackService.createEventSubscribtion(upnpService.get, service.get, callback(f))
  }

  def setAudioPlaylistAndPlay(urls: Array[String], titles: Array[String], creators: Array[String]) = {
    stopPlaybackIfNeeded()
    playbackService.setAudioPlaylistUrls(upnpService.get, service.get, urls, titles, creators, callback(_ => play()))
  }

  private def stopPlaybackIfNeeded(): Unit = {
    getPlaybackStatus match {
      case "PLAYING" =>
        println("stopping"); stop()
      case "PAUSED_PLAYBACK" =>
        println("stopping"); stop()
      case _ =>
    }
  }

  def setNextUrl(url: String, title: String, creator: String) = {
    playbackService.setNextTransportUri(upnpService.get, service.get, url, title, creator, callback(_ => Unit))
  }

  def setUrlAndPlay(url: String, title: String) = {
    stopPlaybackIfNeeded()
    playbackService.setCurrentTransportUri(upnpService.get, service.get, url, title, callback(_ => play()))
  }

  def getPlaybackStatus(): String = {
    val p = Promise[String]
    (upnpService, service) match {
      case (Some(upnp), Some(s)) =>
        playbackService.getTransportInfo(upnp, s, callback(map => (p.complete(Success(map("CurrentTransportState").toString)))))
        val f = p.future
        Await.result(f, 5 seconds)
        f.value match {
          case Some(Success(s)) => s
          case _ => "UNKNOWN"
        }
      case _ => throw new RuntimeException("not initialized properly")
    }
  }

  def play() = {
    (upnpService, service) match {
      case (Some(upnp), Some(s)) => playbackService.play(upnp, s)
      case _ => throw new RuntimeException("not initialized properly")
    }
  }

  def stop() = {
    (upnpService, service) match {
      case (Some(upnp), Some(s)) => playbackService.stop(upnp, s)
      case _ => throw new RuntimeException("not initialized properly")
    }
  }

  def pause() = {
    (upnpService, service) match {
      case (Some(upnp), Some(s)) => playbackService.pause(upnp, s)
      case _ => throw new RuntimeException("not initialized properly")
    }
  }

}