package extensions;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

public class PlaybackService {
	
	public void setCurrentTransportUri(final UpnpService upnpService, Service av, String url, String title) {
		setCurrentTransportUri(upnpService, av, url, title);
	}
	
	public void createEventSubscribtion(final UpnpService upnpService, Service av, final UpnpCallback callback) {
		SubscriptionCallback cl = new SubscriptionCallback(av, 30) {

		    @Override
		    public void established(GENASubscription sub) {
		        System.out.println("Established: " + sub.getSubscriptionId());
		    }

		    @Override
		    protected void failed(GENASubscription subscription,
		                          UpnpResponse responseStatus,
		                          Exception exception,
		                          String defaultMsg) {
		        System.err.println(defaultMsg);
		    }

		    @Override
		    public void ended(GENASubscription sub,
		                      CancelReason reason,
		                      UpnpResponse response) {
		        assert reason == null;
		    }

		    public void eventReceived(GENASubscription sub) {
		    	callback.resultReceived(sub.getCurrentValues());
		    }

		    public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
		        System.out.println("Missed events: " + numberOfMissedEvents);
		    }

		};
		upnpService.getControlPoint().execute(cl);
	}
	
	public void setNextTransportUri(final UpnpService upnpService, Service av, String url, String title, String creator, final UpnpCallback callback) {
		Action a = av.getAction("SetNextAVTransportURI");
		ActionArgument arg = a.getInputArgument("InstanceID");
		ActionArgument uri = a.getInputArgument("NextURI");
		ActionArgument metadata = a.getInputArgument("NextURIMetaData");
		String playlist = "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";
			playlist += "<item id=\"fake_id_0\" parentID=\"fake_parentID\" restricted=\"1\">" +
			"<dc:creator>"+creator+"</dc:creator>" +
			"<dc:title>"+title+"</dc:title>" +
			"<dc:artist>"+creator+"</dc:artist>" +
			"<upnp:class>object.item.audioItem.musicTrack</upnp:class>" +
			"<upnp:artist>"+creator+"</upnp:artist><upnp:artist role=\"Performer\">"+creator+"</upnp:artist><upnp:artist role=\"AlbumArtist\">"+creator+"</upnp:artist>" +
			"<upnp:album></upnp:album>" +
			"<res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" size=\"1000\">"+url+"</res>" +
			"</item>";
		playlist += "</DIDL-Lite>";
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, 
				new ActionArgumentValue[]{
					new ActionArgumentValue<>(arg, "0"),
					new ActionArgumentValue<>(uri, url),
					new ActionArgumentValue<>(metadata, playlist)
				})) {

			@Override
			public void failure(ActionInvocation arg0,
					UpnpResponse arg1, String arg2) {
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				if (callback != null)
					callback.resultReceived(arg0.getOutputMap());
			}
			
		});
	}

	public void setAudioPlaylistUrls(final UpnpService upnpService, Service av, String[] url, String title[], String creator[], final UpnpCallback callback) {
		Action a = av.getAction("SetAVTransportURI");
		ActionArgument arg = a.getInputArgument("InstanceID");
		ActionArgument uri = a.getInputArgument("CurrentURI");
		ActionArgument metadata = a.getInputArgument("CurrentURIMetaData");
		String playlist = "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";
		for (int n = 0; n < url.length; n++) {
			playlist += "<item id=\"fake_id_"+n+"\" parentID=\"fake_parentID\" restricted=\"1\">" +
			"<dc:creator>"+creator[n]+"</dc:creator>" +
			"<dc:title>"+title[n]+"</dc:title>" +
			"<dc:artist>"+creator[n]+"</dc:artist>" +
			"<upnp:class>object.item.audioItem.musicTrack</upnp:class>" +
			"<upnp:artist>"+creator[n]+"</upnp:artist><upnp:artist role=\"Performer\">"+creator[n]+"</upnp:artist><upnp:artist role=\"AlbumArtist\">"+creator[n]+"</upnp:artist>" +
			"<upnp:album></upnp:album>" +
			"<res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" size=\"1000\">"+url[n]+"</res>" +
			"</item>";
		}
		playlist += "</DIDL-Lite>";
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, 
				new ActionArgumentValue[]{
					new ActionArgumentValue<>(arg, "0"),
					new ActionArgumentValue<>(uri, url[0]),
					new ActionArgumentValue<>(metadata, playlist)
				})) {

			@Override
			public void failure(ActionInvocation arg0,
					UpnpResponse arg1, String arg2) {
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				if (callback != null)
					callback.resultReceived(arg0.getOutputMap());
				
			}
			
		});
	}

	public void setCurrentTransportUri(final UpnpService upnpService, Service av, String url, String title, final UpnpCallback callback) {
		Action a = av.getAction("SetAVTransportURI");


		ActionArgument arg = a.getInputArgument("InstanceID");
		ActionArgument uri = a.getInputArgument("CurrentURI");
		ActionArgument metadata = a.getInputArgument("CurrentURIMetaData");
		String metadataContent = "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">" +
		"<item id=\"fake_id_29\" parentID=\"fake_parentID\" restricted=\"1\">" +
		"<dc:title>"+title+"</dc:title>" +
		"<upnp:class>object.item.videoItem</upnp:class>" +
		"<res protocolInfo=\"http-get:*:video/mp4:DLNA.ORG_PN=AVC_MP4_MP_SD_AAC_MULT5;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" size=\"1000\">"+url+"</res>" +
		"</item></DIDL-Lite>";
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, 
				new ActionArgumentValue[]{
					new ActionArgumentValue<>(arg, "0"),
					new ActionArgumentValue<>(uri, url),
					new ActionArgumentValue<>(metadata, metadataContent)
				})) {

			@Override
			public void failure(ActionInvocation arg0,
					UpnpResponse arg1, String arg2) {
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				if (callback != null)
					callback.resultReceived(arg0.getOutputMap());
				
			}
			
		});
	}

	public void play(final UpnpService upnpService, Service av) {
		Action a = av.getAction("Play");


		ActionArgument arg = a.getInputArgument("InstanceID");
		ActionArgument speed = a.getInputArgument("Speed");
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, 
				new ActionArgumentValue[]{
					new ActionArgumentValue<>(arg, "0"),
					new ActionArgumentValue<>(speed, "1")
				})) {

			@Override
			public void failure(ActionInvocation arg0,
					UpnpResponse arg1, String arg2) {
				System.out.println(arg2);
				System.out.println(arg1);
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				System.out.println("success!!!");
				
			}
			
		});
	}

	public void pause(final UpnpService upnpService, Service av) {
		Action a = av.getAction("Pause");

		ActionArgument arg = a.getInputArgument("InstanceID");
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, new ActionArgumentValue[]{new ActionArgumentValue<>(arg, "0")})) {

			@Override
			public void failure(ActionInvocation arg0,
					UpnpResponse arg1, String arg2) {
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				System.out.println("success!!!");
				
			}
			
		});
	}

	public void stop(final UpnpService upnpService, Service av) {
		Action a = av.getAction("Stop");

		ActionArgument arg = a.getInputArgument("InstanceID");
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, new ActionArgumentValue[]{new ActionArgumentValue<>(arg, "0")})) {

			@Override
			public void failure(ActionInvocation arg0,
					UpnpResponse arg1, String arg2) {
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				System.out.println("success!!!");
				
			}
			
		});
	}

	public void getTransportInfo(final UpnpService upnpService, Service av, final UpnpCallback callback) {
		Action a = av.getAction("GetTransportInfo");

		ActionArgument arg = a.getInputArgument("InstanceID");
		upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, new ActionArgumentValue[]{new ActionArgumentValue<>(arg, "0")})) {

			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
				
			}

			@Override
			public void success(ActionInvocation arg0) {
				callback.resultReceived(arg0.getOutputMap());
			}
			
		});
	}
	
	public static void main(String[] args) {
		PlaybackService s = new PlaybackService();
		UpnpService upnpService = new UpnpServiceImpl();

		// Add a listener for device registration events
		upnpService.getRegistry().addListener(
				s.createRegistryListener(upnpService));

		// Broadcast a search message for all devices
		upnpService.getControlPoint().search(new STAllHeader());

	}

	RegistryListener createRegistryListener(final UpnpService upnpService) {
		return new DefaultRegistryListener() {

			ServiceId serviceId = new UDAServiceId("SwitchPower");

			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				System.out.println(device.getDetails().getFriendlyName());
				Service av = device.findService(new ServiceId("upnp-org", "AVTransport"));
				if (av != null) {







					/*
Action a = av.getAction("GetMediaInfo");

ActionArgument arg = a.getInputArgument("InstanceID");
upnpService.getControlPoint().execute(new ActionCallback(new ActionInvocation(a, new ActionArgumentValue[]{new ActionArgumentValue<>(arg, "0")})) {

	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
		System.out.println("failure" + arg2 + ", " + arg1.getStatusCode());
		
	}

	@Override
	public void success(ActionInvocation arg0) {
		System.out.println("yay");
	}
	
});
*/
				}

			}

			@Override
			public void remoteDeviceRemoved(Registry registry,
					RemoteDevice device) {
				Service switchPower;
				if ((switchPower = device.findService(serviceId)) != null) {
					System.out.println("Service disappeared: " + switchPower);
				}
			}

		};
	}

}
