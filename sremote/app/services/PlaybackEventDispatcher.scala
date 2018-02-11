package services

import upnp.UpnpDeviceSubscriber
import org.teleal.cling.model.meta.RemoteDevice
import org.teleal.cling.model.meta.RemoteService
import org.teleal.cling.UpnpService
import org.teleal.cling.model.types.ServiceId
import javax.inject.Singleton
import extensions.PlaybackService
import javax.inject.Inject
import com.google.common.eventbus.EventBus
import controllers.TransitioningEvent

@Singleton
class PlaybackEventDispatcher @Inject() (av: AVTransportService, bus: EventBus) extends UpnpDeviceSubscriber {

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
    val player = device.findService(new ServiceId("upnp-org", "AVTransport"))
    if (player != null) {
      service = Some(player)
      // create the subcribtion
      av.subscribeToLastChange({ map =>
        map("LastChange") match {
          case s if s.toString.contains("STOPPED") && !s.toString.contains("AVTransportURI") => 
            println(s)
            bus.post(TransitioningEvent())
          case s => 
        }
      })
    }
  }

}