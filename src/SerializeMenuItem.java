import burp.IHttpRequestResponse;
import burp.IMenuItemHandler;

public class SerializeMenuItem implements IMenuItemHandler {

	@Override
	public void menuItemClicked(String menuItemCaption, IHttpRequestResponse[] messageInfo) {
		try {
			for (int i = 0; i < messageInfo.length; i++) {
				IHttpRequestResponse iHttpRequestResponse = messageInfo[i];
				Utilities.findAndSerializeProxyFile(iHttpRequestResponse.getResponse());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
