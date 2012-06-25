import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import burp.IHttpRequestResponse;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Utilities {
	static XStream xstream = new XStream(new DomDriver());
	static String linebreak = "\\r\\n\\r\\n";
	static String lineSeperator = System.getProperty("line.separator");

	public static String findAndDeserializeProxyFile(IHttpRequestResponse item) {
		File burpFolder = Utilities.getBurpFolder();
		List<String> historyItems = new ArrayList<String>();
		Collections.addAll(historyItems, burpFolder.list());
		Collections.sort(historyItems);
		try {

			for (String historyItem : historyItems) {
				if (historyItem.contains(".ser"))
					continue;
				String reqFile = burpFolder + File.separator + historyItem;
				String respFile = burpFolder + File.separator + (Integer.valueOf(historyItem) + 1);

				if (Arrays.equals(item.getRequest(), readFile(reqFile)) && Arrays.equals(item.getResponse(), readFile(respFile))) {
					System.out.println("*** DESERMENU *** Found serialized request file: " + historyItem + " . Deserializing...");
					System.out.println("*** DESERMENU *** Found serialized response file: " + (Integer.valueOf(historyItem) + 1) + " . Deserializing...");
					byte[] strMessage = Utilities.readFile(reqFile);
					String reqXml = Utilities.deserializeProxyItem(strMessage);
					Utilities.writeToFile(reqFile, new String(reqXml));
					strMessage = Utilities.readFile(respFile);
					String respXml = Utilities.deserializeProxyItem(strMessage);
					Utilities.writeToFile(respFile, new String(respXml));
					return reqXml;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "sth went wrong";
	}

	public static void findAndSerializeProxyFile(IHttpRequestResponse item) {
		File burpFolder = Utilities.getBurpFolder();
		List<String> historyItems = new ArrayList<String>();
		Collections.addAll(historyItems, burpFolder.list());
		Collections.sort(historyItems);
		try {
			for (String historyItem : historyItems) {
				String respFile = burpFolder + File.separator + historyItem;
				if (Arrays.equals(item.getResponse(), readFile(respFile))) {
					System.out.println("*** SERMENU *** Found deserialized response file: " + historyItem + " . Serializing...");
					byte[] strMessage = Utilities.readFile(respFile);
					Object xml = Utilities.serializeFromXml(strMessage);
					Utilities.writeToFile(respFile + ".ser", xml);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File getBurpFolder() {
		File tmp = new File(System.getProperty("java.io.tmpdir"));
		List<String> candidates = new ArrayList<String>();
		for (String candidate : tmp.list())
			if (candidate.contains("burp"))
				candidates.add(candidate);
		Collections.sort(candidates);
		return new File(tmp + File.separator + candidates.get(candidates.size() - 1));
	}

	public static void writeToFile(String filePath, String content) throws IOException {
		FileWriter fstream = new FileWriter(filePath);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(content);
		out.close();
	}

	public static void writeToFile(String filePath, Object content) throws IOException {
		FileOutputStream f = new FileOutputStream(filePath);
		ObjectOutput s = new ObjectOutputStream(f);
		s.writeObject(content);
		s.flush();
	}

	static byte[] readFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileInputStream fin = new FileInputStream(file);
		byte[] ret = new byte[(int) file.length()];
		fin.read(ret);
		return ret;
	}

	public static String deserializeProxyItem(byte[] message) {
		try {
			String testStr = new String(message);
			String[] strHeadersAndContent = testStr.split(linebreak);
			byte[] byteOrigMessageContent = Arrays.copyOfRange(message, strHeadersAndContent[0].length() + 4, message.length);
			ByteArrayInputStream in = new ByteArrayInputStream(byteOrigMessageContent);
			ObjectInputStream i;
			i = new ObjectInputStream(in);
			Object obj = i.readObject();
			String xml = xstream.toXML(obj);
			return (strHeadersAndContent[0] + lineSeperator + lineSeperator + xml);
		} catch (Exception e) {
			e.printStackTrace();
			return "sth went wrong " + e.getMessage();
		}
	}

	public static void print(String str) {
		System.out.println(str);
	}

	public static void print(List<String> str) {
		for (String s : str) {
			System.out.println(s);
		}
	}

	public static void print(String[] str) {
		for (int i = 0; i < str.length; i++)
			System.out.println(str[i]);
	}

	public static byte[] serializeProxyItem(byte[] message) {
		try {
			String strMessage = new String(message);
			String[] strHeadersAndContent = strMessage.split(linebreak);
			Object xml = xstream.fromXML(strHeadersAndContent[1]);
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream oStream = new ObjectOutputStream(bStream);
			oStream.writeObject(xml);
			byte[] content = bStream.toByteArray();
			byte[] header = (strHeadersAndContent[0] + lineSeperator + lineSeperator).getBytes();
			byte[] retArray = new byte[header.length + content.length];
			System.arraycopy(header, 0, retArray, 0, header.length);
			System.arraycopy(content, 0, retArray, header.length, content.length);
			return retArray;
		} catch (Exception e) {
			e.printStackTrace();
			return "sth went wrong".getBytes();
		}

	}

	public static Object serializeFromXml(byte[] message) {
		try {
			String strMessage = new String(message);
			String[] strHeadersAndContent = strMessage.split(linebreak);
			Object xml = xstream.fromXML(strHeadersAndContent[1]);
			return xml;
		} catch (Exception e) {
			e.printStackTrace();
			return "sth went wrong".getBytes();
		}

	}
}
