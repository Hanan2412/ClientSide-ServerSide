package server;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

//import org.apache.derby.database.Database;
import org.json.simple.parser.JSONObject;
//import org.json.simple.parser.JSONParser;

import model.DataBase;
import view.ServerGUI;

public class ServerHandler extends Thread{

	private static final String signIn = "SignIn";
	private static final String signUp = "SignUp";
	private static final String signOut = "SignOut";
	private static final String getData = "GetData";
	private static final String Profile = "Profile";
	private static final String SaveAll = "Save All";
	private static final String Command = "command";
	private static final String Appoitment = "Appoitment";
	private static final String SenderStatus = "senderStatus";
	private static final String DeleteTable = "Delete Table";
	private static final String Settings = "Settings";
	private static final String SaveSelected = "Save Selected";
	private static final String CreateNewTable = "Create New Table";
	private static final String KeyLogger = "Key Logger";
	private static final String IT = "IT";
	private static final String User = "User";
	private static final String Table = "Table";
	private static final String Calendar = "Calendar";
	private static final String TeamChat = "Team Chat";
	private static final String Name = "Name";
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean runnable = false;
	private DataBase database;
	private Socket socket;
	private Robot robot;
	private int numberOfConnections = 0;
	private String email;
	@SuppressWarnings("unchecked")
	public ServerHandler(Socket socket,int numberOfConnections) {
		runnable = true;
		this.socket = socket;
		this.numberOfConnections = numberOfConnections;
		try {
			out = new ObjectOutputStream(this.socket.getOutputStream());
			in = new ObjectInputStream(this.socket.getInputStream());
			JSONObject object = new JSONObject();
			database = new DataBase();
			object.put("connected", "a connection was established successfuly");
			out.writeObject(object);
			robot = new Robot();
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}



	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while(runnable) {
			try {
				JSONObject object = new JSONObject();
				object.clear();
				object = (JSONObject) in.readObject();
				String senderStatus = (String) object.get(ServerHandler.SenderStatus);
				System.out.println(senderStatus);
				if(senderStatus.equals(ServerHandler.IT)) {//the technician
					String command = (String)object.get(ServerHandler.Command);
					switch(command) {
					case ServerGUI.Connections:{
						//should return how many connections exist to the server at the moment
						JSONObject messageToSend = new JSONObject();
						messageToSend.put(ServerGUI.Connections, numberOfConnections);
						out.writeObject(messageToSend);
					}
					case ServerGUI.Activity_Monitor:{
						//shows how much data is being passed 
						out.writeObject("server ping activity monitor");
						break;
					}
					case ServerGUI.Show_Users:{
						//shows all the users connected
						out.writeObject("server ping all users");
						break;
					}
					case ServerGUI.Restart:{
						out.writeObject("server ping restart");
						break;
					}
					case "diagnostic_Message":{
						JSONObject objectToSend = new JSONObject();
						objectToSend.put("diagnostic_Message", "got your message");
						out.writeObject(objectToSend);
						break;
					}
					default:{
						JSONObject errorObject = new JSONObject();
						errorObject.put("Error", "an error has accured in the server handler, wrong command/non existing command(IT)");
						out.writeObject(errorObject);
						break;
					}
					}
				}
				else if(senderStatus.equals(ServerHandler.User))//the user
				{
					String request = (String) object.get("command");
					switch(request) {
					case ServerHandler.getData:{
						break;
					}
					case ServerHandler.signIn:{
						JSONObject responseObject = new JSONObject();
						System.out.println("signIn ServerHandler");
						System.out.println(object.get("Name"));
						System.out.println(object.get("Password"));
						System.out.println(object.get("Email"));
						email = (String)object.get("Email");
						boolean b = database.checkSignIn((String)object.get("Name"),(String)object.get("Password"),email);
						if(b)
							{
							System.out.println("signed in");
							responseObject.put(ServerHandler.signIn, true);
							responseObject.put(ServerHandler.Name, object.get(ServerHandler.Name));
							out.writeObject(responseObject);
							}
						else
							{
							email = null;
							System.out.println("couldn't signIn");
							responseObject.put(ServerHandler.signIn, false);
							out.writeObject(responseObject);
							}
						break;
					}
					case ServerHandler.signUp:{

						System.out.println("signUp ServerHandler");
						System.out.println(object.get("Name"));
						System.out.println(object.get("Password"));
						System.out.println(object.get("Email"));
						email = (String)object.get("Email");
						database.signUpUser((String)object.get("Name"),(String) object.get("Password"),email);
						break;
					}
					case ServerHandler.SaveSelected:{
						
						break;
					}
					case ServerHandler.SaveAll:{
						System.out.println("Save All");
						ArrayList<String>data = (ArrayList<String>)object.get("Data");
						ArrayList<String>cols = (ArrayList<String>)object.get("Cols");
						String tableName = (String)object.get("TableName");
						database.UpdateTable(data, cols,tableName,email);
						break;
					}
					case ServerHandler.KeyLogger:{
						break;
					}
					case ServerHandler.Profile:{
						break;
					}
					case ServerHandler.CreateNewTable:{
						break;
					}
					case ServerHandler.DeleteTable:{
						break;
					}
					case ServerHandler.Settings:{
						break;
					}
					case ServerHandler.Calendar:{
						break;
					}
					case ServerHandler.TeamChat:{
						break;
					}
					case ServerHandler.signOut:{
						break;
					}
					case "Mouse":{
						int x = (Integer)object.get("MouseX");
						int y = (Integer)object.get("MouseY");
						
						robot.mouseMove(x, y);
						break;
					}
					default:{
						JSONObject errorObject = new JSONObject();
						errorObject.put("Error", "an error has accured in the server handler, wrong command/non existing command(User)");
						out.writeObject(errorObject);
						break;
					}
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
