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
import scalaj.http.Http

@Singleton
class XbmcController @Inject() () extends Controller {
  var playerStateService: PlayerStateService = _

  /**
   * Relays the POST to the jsonrpc to circumvent CORS
   */
  def submit() = Action { request =>
    val body = request.body
    val cmd = body.asJson.get.toString()
    println(cmd)
    if (cmd.contains("addon")) // an addon command signalizes that an addon is started and it will start playing twitch stream
      playerStateService.activatePlayer(Twitch())

    val result = sendCommand(body.asJson.get.toString)
    Ok
  }
  
  def sendHomeCommand() = sendCommand("""{"jsonrpc": "2.0", "method": "Input.Home", "id": 1}""")

  def sendCommand(cmd: String) = Http.postData("http://192.168.0.190:8080/jsonrpc", cmd).header("Content-Type", "application/json").asString
  /*
  def options(cid: String, vid: String) = Action {
    Ok.withHeaders(("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS"),
          ("Access-Control-Allow-Origin", "*"))
  }
  * *
  */
}