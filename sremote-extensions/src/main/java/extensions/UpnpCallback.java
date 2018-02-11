package extensions;

import java.util.Map;

public interface UpnpCallback {
	void resultReceived(Map<String, Object> outputParameters);

}
