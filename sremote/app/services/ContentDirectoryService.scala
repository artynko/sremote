package services

import org.teleal.cling.model.meta.RemoteService
import org.teleal.cling.UpnpService
import org.teleal.cling.support.model.DIDLContent
import org.teleal.cling.support.model.BrowseFlag
import org.teleal.cling.model.message.UpnpResponse
import extensions.SimpleBrowse
import upnp.UpnpDeviceSubscriber
import org.teleal.cling.model.meta.RemoteDevice
import org.teleal.cling.model.types.ServiceId
import javax.inject.Singleton

@Singleton
class ContentDirectoryService extends UpnpDeviceSubscriber {
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
    val friendlyName = device.getDetails().getFriendlyName()
    println("device connected: " + friendlyName)
    val player = device.findService(new ServiceId("upnp-org", "ContentDirectory"))
    if (player != null && friendlyName.contains("Serviio")) {
      println("Found mediaserver")
      service = Some(player)
    }
  }

  def browse(id: String, action: DIDLContent => Unit) {
    (upnpService, service) match {
      case (Some(upnp), Some(s)) =>
        println(upnp, s)
        upnp.getControlPoint().execute(new SimpleBrowse(s, id, BrowseFlag.DIRECT_CHILDREN) {

          override def received(content: DIDLContent) {
            action(content)
          }

          override def failure(response: UpnpResponse, string: String) {}

        });
      case _ => throw new RuntimeException("uninitialized")
    }
  }
}