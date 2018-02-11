package upnp

import org.teleal.cling.model.meta.RemoteDevice
import org.teleal.cling.UpnpService

trait UpnpDeviceSubscriber {
  
  def deviceConnected(upnpService: UpnpService, device: RemoteDevice)

}