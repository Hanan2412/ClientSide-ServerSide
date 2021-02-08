package client;

import org.json.simple.parser.JSONObject;

public interface ClientDefenition {
	public JSONObject getClientInfo();
	public void setClientInfo(JSONObject clientInfo);
	public void setUpNewClient(String id);

}
