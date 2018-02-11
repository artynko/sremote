package controllers

import json.Ok
import play.api.mvc.Action
import play.api.mvc.Controller
import javax.inject.Singleton
import javax.inject.Inject
import play.api.mvc.Result
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.AVTransportService
import domain.Audio
import scala.util.Success
import services.XAutService

@Singleton
class MediaBrowserController @Inject() (repo: VideoRepository, audioRepo: AudioRepository, p: AVTransportService, xaut: XAutService, playerStateService: PlayerStateService) extends Controller {

  def listNew = Action.async {
    repo.listNew.map(result => json.Ok(result))
  }

  def listUnwatched = Action.async {
    repo.listUnwatched.map(result => json.Ok(result))
  }
  
  def addToFavorites(containerId: String, audioId: String) = Action.async {
    audioRepo.addToFavorites(containerId, audioId).map(r => NoContent)
  }
  
  def options(cid: String, vid: String) = Action {
    Ok.withHeaders(("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS"),
          ("Access-Control-Allow-Origin", "*"))
  }

  def removeFromFavorites(containerId: String, audioId: String) = Action.async {
    audioRepo.removeFromFavorites(containerId, audioId).map(r => NoContent)
  }

  def listMovies = Action.async { request =>
    println(request.uri)
    println(request.path)
    println(request.domain)
    repo.listMovies.map(result => json.Ok(result))
  }

  def listAllAudio = Action.async {
    audioRepo.listPlaylist.map(result => json.Ok(result))
    /*val toPlay = List("A_305", "A_306") map (n => audioRepo.getAudioEntry("A_PL^PL", n.toString)) 
    p.setAudioPlaylistAndPlay(Array(toPlay(0).url, toPlay(1).url), Array(toPlay(0).title, toPlay(1).title), Array(toPlay(0).artist , toPlay(1).artist ))*/
  }

  def listFavorites = Action.async {
    audioRepo.listPlaylist.flatMap { s =>
      audioRepo.getFavorites.map {
        a => json.Ok(a.map(i => Audio(i.id.containerId, i.id.audioId, i.title, i.artist, i.url)))
      }
    }
  }
}