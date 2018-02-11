package controllers

import scala.collection.JavaConversions._
import org.teleal.cling.UpnpServiceImpl
import org.teleal.cling.model.message.header.STAllHeader
import org.teleal.cling.model.meta.RemoteDevice
import org.teleal.cling.model.meta.RemoteService
import org.teleal.cling.model.meta.Service
import org.teleal.cling.model.types.ServiceId
import org.teleal.cling.registry.DefaultRegistryListener
import org.teleal.cling.registry.Registry
import org.teleal.cling.support.model.DIDLContent
import org.teleal.cling.UpnpService
import org.teleal.cling.model.action.ActionInvocation
import org.teleal.cling.controlpoint.ActionCallback
import services.AVTransportService
import services.ContentDirectoryService
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import scala.concurrent._
import extensions.PlaybackService

object UpnpController extends App {
  val upnpService = new UpnpServiceImpl();

  def getService() = upnpService

  // Add a listener for device registration events
  upnpService.getRegistry().addListener(
    createRegistryListener(upnpService));

  invokeSearch()

  def invokeSearch() {
    // Broadcast a search message for all devices
    upnpService.getControlPoint().search(
      new STAllHeader());
  }
      val playerService = new AVTransportService()
  

      Thread.sleep(10000)
  playerService.setUrlAndPlay("http://192.168.0.190:8895/resource/1000000010001947/MEDIA_ITEM/AVC_TS_MP_HD_AC3_ISO-0/ORIGINAL", "DOTO")
  def createRegistryListener(upnpService: UpnpService): DefaultRegistryListener = {
    return new DefaultRegistryListener() {

      val serviceId = new ServiceId("upnp-org", "ContentDirectory");
      val contentDirectoryService = new ContentDirectoryService()

      override def remoteDeviceAdded(registry: Registry, device: RemoteDevice) {
        contentDirectoryService.deviceConnected(upnpService, device)
        playerService.deviceConnected(upnpService, device)
        println(device.getServices().size, device.getDetails().getFriendlyName())
        device.getServices().foreach(s => println("\t", s.getServiceId(), s.getServiceType()))
        val service = device.findService(serviceId)
        println(service)
        if (service != null) {
          contentDirectoryService.browse("V_OF^FOL_FD1", { content =>
            println(content.getContainers().toList.foreach(c => println(c.getId(), c.getTitle())))
            content.getItems().toList.foreach { i =>
              println(i.getTitle(), i.getDescMetadata(), i.getId())
              println(i.getResources().get(0).getValue())

            }
            println(content.getDescMetadata())
          })
          println(service.getClass())

          /*
          println("---------" + service.getActions().size)
          service.getActions().foreach { a =>
            println(a.getName())
            a.getOutputArguments().foreach { arg =>
              println("OUT-" + arg.getName() + ", " + arg.getDatatype())
            }
            a.getInputArguments().foreach { arg =>
              println("IN-" + arg.getName() + ", " + arg.getDatatype())
            }
          }
          */
        }
        val player = device.findService(new ServiceId("upnp-org", "AVTransport"))
        println(player)
        if (player != null) {
          println("-----------:")
          //println(playerService.getPlaybackStatus)
          println("after playing")
        }

      }

    };
  }

}