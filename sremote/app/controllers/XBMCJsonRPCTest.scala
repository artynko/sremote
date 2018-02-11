package controllers

import scalaj.http.Http


object XBMCJsonRPCTest extends App {

  val result = Http.postData("http://192.168.0.190:8080/jsonrpc", """{"jsonrpc": "2.0", "method": "Addons.ExecuteAddon", "params": { "addonid": "plugin.video.twitch" }}""").header("Content-Type", "application/json").asString
  println(result)
}