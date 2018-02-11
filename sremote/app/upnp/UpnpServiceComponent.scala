package upnp
import scala.collection.JavaConversions._
import org.teleal.cling.UpnpServiceImpl
import org.teleal.cling.model.message.header.STAllHeader
import org.teleal.cling.model.meta.RemoteDevice
import org.teleal.cling.registry.DefaultRegistryListener
import services.ContentDirectoryService
import org.teleal.cling.UpnpService
import services.AVTransportService
import org.teleal.cling.registry.Registry
import org.teleal.cling.model.types.ServiceId
import javax.inject.Inject

class UpnpServiceComponent @Inject()(val listeners: java.util.Set[UpnpDeviceSubscriber]) {
  val upnpService = new UpnpServiceImpl();
  
  def getService() = upnpService 

  // Add a listener for device registration events
  upnpService.getRegistry().addListener(createRegistryListener(upnpService));

  invokeSearch()

  def invokeSearch() {
    // Broadcast a search message for all devices
    upnpService.getControlPoint().search(new STAllHeader());
  }

  def createRegistryListener(upnpService: UpnpService): DefaultRegistryListener = {
    return new DefaultRegistryListener() {

      override def remoteDeviceAdded(registry: Registry, device: RemoteDevice) {
        // notify all listeners that a device connected
        listeners.foreach(_.deviceConnected(upnpService, device))

        println(device.getServices().size, device.getDetails().getFriendlyName())
        device.getServices().foreach(s => println("\t", s.getServiceId(), s.getServiceType()))
      }
    };
  }
}