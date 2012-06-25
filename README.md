BurpJDSer
=========

BurpJDSer is a Burp plugin that will deserialze/serialize Java request and response to and from XML with the use of Xtream library (http://xstream.codehaus.org/)

========= Usage =========
#1 Find client *.jar files
- Burp: view HTML response for embedded jar files
- Alternatively, look through browser's cache

#2(Optional) Search for sensitive information
- Use JD-GUI to open jar file. File --> Save all sources to a location
- What to search: hardcoded password, SQL string, SSN, Credit card, etc.

#3 Start Burp plugin
java -Djava.io.tmpdir=[tmp_folder] -classpath burp.jar;burpjdser.jar;xstream-1.4.2.jar;[client_jar] burp.StartBurp 

#4 Inspect proxy history for serialized Java traffic
- Right-click on proxy item --> deserialize Java
- Click on proxy item again, request/response will be deserialized to xml. Burp will also send deserialized request to Repeater and Intruder
- Fuzz the request using Repeater/Intruder. Request will be automatically serialized back to binary format and response will be deserialized in XML format

*** Notes: Sometimes the client rely on server for authorization check. In case you may want to modify the serialized response to bypass it:
- Inspect proxy response for possible authorization check
- Right-click and deserialize suspected proxy item
- Watch the console for the latest entry:
*** DESERMENU *** Found serialized response file: x . Deserializing...
- Go to the <tmp_folder>, find the response corresponding to the proxy item x
- Edit XML response (turn 'false' to 'true')
- Right-click on the proxy item again --> serialize java
- In <tmp_folder> there should be a file call x.ser
- Intercept response again. Right-click --> paste from file --> x.ser
