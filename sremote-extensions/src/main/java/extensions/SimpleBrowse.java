package extensions;

import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;

public abstract class SimpleBrowse extends Browse {
	public abstract void received(DIDLContent content);
	public abstract void failure(UpnpResponse reponse, String string);

	
	public SimpleBrowse(Service service, String containerId, BrowseFlag flag) {
		super(service, containerId, flag);
	}
	
	@Override
	public void received(ActionInvocation arg0, DIDLContent arg1) {
		received(arg1);
	}

	@Override
	public void updateStatus(Status arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
		failure(arg1, arg2);
		
	}

}
