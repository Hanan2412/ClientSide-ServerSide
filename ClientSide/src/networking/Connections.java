package networking;

import org.json.simple.parser.JSONObject;

public interface Connections {

	public void updatePort(int port);
	public void sendData(JSONObject object);
	public JSONObject getData();
}
