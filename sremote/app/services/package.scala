import extensions.UpnpCallback
import scala.collection.JavaConversions._
package object services {

  def callback(call: Map[String, Any] => Unit): UpnpCallback = {
    return new UpnpCallback {
      def resultReceived(map: java.util.Map[String, java.lang.Object]): Unit = {
    		  call(map.toMap)
      }
    }
  }

}