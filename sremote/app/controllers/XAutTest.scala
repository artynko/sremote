package controllers
import scala.sys.process._

object XAutTest extends App {
  println(Seq("python", "activatewindow.py", "Firefox").!!)

}