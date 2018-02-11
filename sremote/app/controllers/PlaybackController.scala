package controllers

import upnp.UpnpDeviceSubscriber
import org.teleal.cling.model.meta.RemoteDevice
import upnp.UpnpServiceComponent
import org.teleal.cling.model.types.ServiceId
import org.teleal.cling.model.meta.RemoteService
import play.api.mvc.Controller
import play.api.mvc.Action
import javax.inject.Inject
import org.teleal.cling.UpnpService
import com.google.inject.Singleton
import services.AVTransportService
import com.google.common.eventbus.Subscribe
import com.google.common.eventbus.EventBus
import scala.collection.mutable.ListBuffer
import services.XAutService

case class TransitioningEvent()

@Singleton
class PlaybackController @Inject() (val av: AVTransportService, repo: VideoRepository,
  audioRepo: AudioRepository, bus: EventBus, xaut: XAutService,
  playerStateService: PlayerStateService, xbmcController: XbmcController) extends Controller {

  bus.register(this)
  var currentPlaying: String = "";
  var randomPlaying = true
  var nextSong: AudioEntry = _;
  var nextPlaying: String = "";
  var mediaQueue = List[AudioEntry]()
  var currentVideo: Option[VideoId] = None

  @Subscribe def playerTransitioning(e: TransitioningEvent) = { // called when a STOP event was received by the player
    println(s"randomPlaying: $randomPlaying")
    if (randomPlaying) {
      mediaQueue match {
        case head :: tail =>
          nextSong = head
          mediaQueue = tail
        case Nil =>
      }
      println("invoking next:" + nextSong, this)
      playNextSong()
    }
    currentVideo match {
      case Some(v) => // mark as watched
        println(s"marking as watched $v")
        repo.markWatched(v)
        currentVideo = None
        currentPlaying = ""
      case None =>
    }
  }

  def getCurrentPlaying() = Action {
    Ok("{ \"title\": \"" + currentPlaying + "\" }")
  }

  /**
   * Stops playback for twitch and youtube
   * dlna player checks for the state and stops it if needed
   */
  private def stopPlaybackIfNeeded() = {
    // check for current title
    playerStateService.currentPlayer match {
      case Twitch() => xbmcController.sendHomeCommand // we need to go to home so we don't instantly get started again
      case _ =>
    }
  }

  def invokeLoadAndPlay(containerId: String, videoId: String) = Action {
    stopPlaybackIfNeeded()
    playerStateService.activatePlayer(XBMC())
    randomPlaying = false;
    val v = repo.getVideoEntry(containerId, videoId)
    currentVideo = Some(VideoId(containerId, videoId));
    println(s"playing $v.url")
    av.setUrlAndPlay(v.url, v.title)
    currentPlaying = v.title;
    Ok("")
  }

  def invokeAudioLoadAndPlay(containerId: String, videoId: String) = Action {
    val c = audioRepo.getAudioEntry(containerId, videoId)
    println(s"playing audio $c.url")
    randomPlaying match {
      case true =>
        nextSong = c
        av.stop() // stop will trigger next song
      case false => av.setAudioPlaylistAndPlay(Array(c.url), Array(c.title), Array(c.artist))
    }
    currentPlaying = c.title + " - " + c.artist
    Ok("")
  }

  def queueAudio(containerId: String, audioId: String) = Action {
    val c = audioRepo.getAudioEntry(containerId, audioId)
    println(s"queueuing audio $c.url")
    mediaQueue = mediaQueue.:+(c)
    NoContent
  }

  def playAllAudio() = Action {
    randomPlaying = true
    val c = audioRepo.getRandomEntry()
    currentPlaying = c.title + " - " + c.artist
    av.setAudioPlaylistAndPlay(Array(c.url), Array(c.title), Array(c.artist))
    val n = audioRepo.getRandomEntry()
    nextSong = n
    //av.setNextUrl(n.url, n.title, n.artist)
    nextPlaying = n.title + " - " + n.artist
    NoContent
  }

  def invokePlay() = Action {
    playerStateService.activatePlayer(XBMC())
    av.play()
    NoContent
  }

  def invokePause() = Action {
    av.pause()
    NoContent
  }

  def invokeStop() = Action {
    randomPlaying = false
    currentPlaying = ""
    currentVideo = None
    playerStateService.currentPlayer match {
      case XBMC() => av.stop()
      case Twitch() =>
        xbmcController.sendHomeCommand
        av.stop()
      case Firefox() => xaut.firefoxLoadUrl("192.168.0.190:9000/ui/dashboard.html{Return}") // stop whatever was in firefox
    }
    Ok("")
  }

  private def playNextSong() = {
    currentPlaying = nextSong.title + " - " + nextSong.artist
    av.setAudioPlaylistAndPlay(Array(nextSong.url), Array(nextSong.title), Array(nextSong.artist))
    val n = audioRepo.getRandomEntry()
    nextSong = n
    //av.setNextUrl(n.url, n.title, n.artist)
    nextPlaying = n.title + " - " + n.artist
  }

  def playYoutube(videoId: String) = Action {
    stopPlaybackIfNeeded()
    playerStateService.activatePlayer(Firefox())
    currentPlaying = videoId
    val url = s"www.youtube.com/tv#/watch?v=$videoId{Return}"
    xaut.firefoxLoadUrl(url)
    NoContent
  }

  def skipAdvert() = Action {
    xaut.firefoxSkipAdvert
    NoContent
  }

  def activateFirefox() = Action {
    NoContent
  }

  def invokeNext() = Action {
    // kinda silly but if I am in continous playback invoking next is done by stopping playback
    av.stop()
    NoContent
  }
}