package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.Action
import play.api.mvc.Controller
import scalaj.http.Http
import scalaj.http.HttpOptions

@Singleton
class TwitchController @Inject() (playerStateService: PlayerStateService) extends Controller {
  
  /**
   * Relays the POST to the twitch api to circumvent CORS
   */
  def submit() = Action { request =>
    val body = request.body
    val url = (body.asJson.get \ "url").as[String]
    println(url)
    val result = Http(url).option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(10000)).header("Accept", "application/json").asString
    Ok(result)
  }
}