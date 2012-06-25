import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IHttpRequestResponse;
import burp.IScanIssue;

public class BurpExtender implements IBurpExtender {
	public static IBurpExtenderCallbacks mCallbacks;

	public BurpExtender() {
	}

	public byte[] processProxyMessage(int messageReference, boolean messageIsRequest, String remoteHost, int remotePort, boolean serviceIsHttps,
			String httpMethod, String url, String resourceType, String statusCode, String responseContentType, byte[] message, int[] action) {
		return message;
	}

	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		mCallbacks = callbacks;
		mCallbacks.registerMenuItem("deserialize Java", new DSerializeMenuItem());
		mCallbacks.registerMenuItem("serialize Java", new SerializeMenuItem());
	}

	public void setCommandLineArgs(String[] args) {
	}

	public void processHttpMessage(String toolName, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		try {
			if ("repeater".equals(toolName) || "intruder".equals(toolName)) {
				
				if (messageIsRequest) {
//					System.out.println("Before serialized: " + new String(messageInfo.getRequest()));
					byte[] xml = Utilities.serializeProxyItem(messageInfo.getRequest());
//					System.out.println("After serialized: " + new String(xml));
					messageInfo.setRequest(xml);
				} else {
					String xml = Utilities.deserializeProxyItem(messageInfo.getResponse());
					messageInfo.setResponse(xml.getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void newScanIssue(IScanIssue issue) {
	}

	public void applicationClosing() {

	}

}
