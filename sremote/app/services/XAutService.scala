package services
import scala.sys.process._

class XAutService {
  
  def activateWindow(title: String) = Seq("python", "activatewindow.py", title) !!
  def firefoxLoadUrl(url: String) = Seq("python", "firefoxopenurl.py", url.replaceAll("#", "{numbersign}")) !!
  def firefoxSkipAdvert() = Seq("python", "firefoxcloseadd.py") !!

}