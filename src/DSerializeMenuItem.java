import java.util.Arrays;

import burp.IHttpRequestResponse;
import burp.IMenuItemHandler;

public class DSerializeMenuItem implements IMenuItemHandler {

	@Override
	public void menuItemClicked(String menuItemCaption, IHttpRequestResponse[] messageInfo) {
		for (int i = 0; i < messageInfo.length; i++) {
				IHttpRequestResponse iHttpRequestResponse = messageInfo[i];
				try {
					String deserializedRequest = Utilities.findAndDeserializeProxyFile(iHttpRequestResponse);
					BurpExtender.mCallbacks.sendToRepeater(iHttpRequestResponse.getHost(), iHttpRequestResponse.getPort(), iHttpRequestResponse
							.getProtocol().equals("https") ? true : false, deserializedRequest.getBytes(), "jdser");
					BurpExtender.mCallbacks.sendToIntruder(iHttpRequestResponse.getHost(), iHttpRequestResponse.getPort(), iHttpRequestResponse
							.getProtocol().equals("https") ? true : false, deserializedRequest.getBytes());
		
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
}
