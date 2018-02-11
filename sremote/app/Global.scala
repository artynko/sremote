import play.api.GlobalSettings
import com.google.inject.AbstractModule
import com.google.inject.Guice
import controllers.Application
import modules.PersistanceModule
import controllers.UpnpController
import com.google.inject.multibindings.Multibinder
import upnp.UpnpDeviceSubscriber
import controllers.PlaybackController
import upnp.UpnpServiceComponent
import services.AVTransportService
import controllers.MediaBrowserController
import controllers.VideoRepository
import services.ContentDirectoryService
import controllers.AudioRepository
import services.PlaybackEventDispatcher
import com.google.common.eventbus.EventBus

object Global extends GlobalSettings {

  val injector = Guice.createInjector(new AbstractModule {
    // the rest controllers module
    protected def configure() {
      bind(classOf[Application]).asEagerSingleton()
      bind(classOf[UpnpServiceComponent]).asEagerSingleton()
      bind(classOf[MediaBrowserController]).asEagerSingleton()
      bind(classOf[PlaybackController]).asEagerSingleton()
      bind(classOf[VideoRepository]).asEagerSingleton()
      bind(classOf[AudioRepository]).asEagerSingleton()
      bind(classOf[EventBus]).asEagerSingleton()
      val mb = Multibinder.newSetBinder(binder(), classOf[UpnpDeviceSubscriber])
      mb.addBinding().to(classOf[AVTransportService]).asEagerSingleton()
      mb.addBinding().to(classOf[ContentDirectoryService]).asEagerSingleton()
      mb.addBinding().to(classOf[PlaybackEventDispatcher]).asEagerSingleton()
    }
  }, new PersistanceModule())
  
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

}