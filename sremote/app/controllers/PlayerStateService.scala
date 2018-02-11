package controllers

import services.XAutService
import javax.inject.Inject
import javax.inject.Singleton

case class Firefox()
case class XBMC()
case class Twitch()

@Singleton
class PlayerStateService @Inject() (xaut: XAutService, xbmcController: XbmcController) {
  xbmcController.playerStateService = this
  var currentPlayer: Any = null

  def activatePlayer(player: Any) = {
    if (player != currentPlayer) {
      println("activating " + player)
      currentPlayer match {
        case Firefox() =>
          xaut.firefoxLoadUrl("192.168.0.190:9000/ui/dashboard.html{Return}") // stop whatever was in firefox
        case Twitch() => xbmcController.sendHomeCommand() // send xbmc to home, to close the addon
        case _ =>
      }
      currentPlayer = player
      player match {
        case Twitch() => xaut.activateWindow("XBMC") // Twitch player uses XBMC window
        case _ => xaut.activateWindow(player.toString.replace("()", ""))
      }
      Thread.sleep(1000)
    }
  }
}