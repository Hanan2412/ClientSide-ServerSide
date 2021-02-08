package client;

import org.json.simple.parser.JSONObject;

public class Client implements ClientDefenition{

	private String name,password;
	private String id;
	public Client(String name,String password)
	{
		this.name = name;
		this.password = password;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getClientInfo() {
		JSONObject clientInfoObject = new JSONObject();
		clientInfoObject.put("name", name);
		clientInfoObject.put("password", password);
		clientInfoObject.put("id", id);
		return clientInfoObject;
	}

	@Override
	public void setClientInfo(JSONObject clientInfo) {
		
	}

	@Override
	public void setUpNewClient(String id) {
		this.id = id;
	}

	public String getClientId() {
		return id;
	}
	public String getName()
	{
		return name;
	}
	public String getPassword()
	{
		return password;
	}
	
}
