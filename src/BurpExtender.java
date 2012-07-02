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
		try {
			if (messageIsRequest) {
				byte[] deserReq = Utilities.deserializeProxyItem(message).getBytes();
				return deserReq;
			} else {
				String resp = new String(message);
				if (resp.contains(Utilities.X_BURP_DESERIALIZED)) {
					resp.replace(Utilities.X_BURP_DESERIALIZED, "");
					Utilities.print("*** DESER *** Found serialized response at Burp file: " + Utilities.findBurpFile(message) + " . Deserialzing...");
					return Utilities.serializeProxyItem(message);
				}
				return message;
			}
		} catch (Exception e) {
			return message;
		}
	}

	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		mCallbacks = callbacks;
		// mCallbacks.registerMenuItem("deserialize Java", new
		// DSerializeMenuItem());
		mCallbacks.registerMenuItem("serialize Java", new SerializeMenuItem());
	}

	public void setCommandLineArgs(String[] args) {
	}

	public void processHttpMessage(String toolName, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		try {
			if ("Repeater".equals(toolName) || "Intruder".equals(toolName)) {
				if (messageIsRequest) {
					byte[] xml = Utilities.serializeProxyItem(messageInfo.getRequest());
					messageInfo.setRequest(xml);
				} else {
					String xml = Utilities.deserializeProxyItem(messageInfo.getResponse());
					messageInfo.setResponse(xml.getBytes());
				}
			} else if ("Proxy".equals(toolName)) {
				if (messageIsRequest) {
					byte[] byteReq = messageInfo.getRequest();
					String request = new String(byteReq);
					if (request.contains(Utilities.X_BURP_DESERIALIZED))
						messageInfo.setRequest(Utilities.serializeProxyItem(byteReq));
				} else {
					byte[] byteResp = messageInfo.getResponse();
					messageInfo.setResponse(Utilities.deserializeProxyItem(byteResp).getBytes());
				}

			}
		} catch (Exception e) {
			// e.printStackTrace();

		}

	}

	public void newScanIssue(IScanIssue issue) {
	}

	public void applicationClosing() {

	}

}
