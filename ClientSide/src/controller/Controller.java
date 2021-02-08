package controller;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.json.simple.parser.JSONObject;

import client.ClientGUI;
import networking.Networking;


public class Controller implements ControllerConnections{

	private Networking networking;
	private ClientGUI gui;
	private JSONObject dataToSend;
	public Controller(Networking networking,ClientGUI gui)
	{
		//networking = new Networking(34567);//server port
		Thread networkingThread = new Thread(networking);
		networkingThread.setName("Networking Thread");
		networkingThread.start();
		//gui = new ClientGUI();
		this.gui = gui;
		this.networking = networking;
		//networking.sendData(gui.getRememberedConnection());
		dataToSend = new JSONObject();
		dataToSend.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		switch(evt.getPropertyName()) {
		case ClientGUI.SignIn:{
			System.out.println("Controller SignIn");
			networking.sendData(gui.getDataToSend());
			break;
		}
			case ClientGUI.SignUp:{
				System.out.println("Controller SignuP");
				networking.sendData(gui.getDataToSend());
			break;
		}
		case ClientGUI.GetData:{
			dataToSend.clear();
			dataToSend.put(ClientGUI.GetData, true);
			networking.sendData(dataToSend);
			break;
		}
		case ClientGUI.Profile:{
			dataToSend.clear();
			dataToSend.put(ClientGUI.Profile, "Profile");
			networking.sendData(dataToSend);
			break;
		}
		case ClientGUI.Exit:{
			networking.TerminateConnection();
			System.exit(0);
			break;
		}
		case ClientGUI.CreateNewTable:{
			dataToSend.clear();
			dataToSend.put(ClientGUI.CreateNewTable, true);
			networking.sendData(dataToSend);
			break;
		}
		case ClientGUI.DeleteTable:{
			dataToSend.clear();
			dataToSend.put(ClientGUI.DeleteTable, gui.getTableToDelete());
			networking.sendData(dataToSend);
			break;
		}
		case ClientGUI.SaveAll:{
			dataToSend.clear();
			JSONObject data = gui.getDataToSend();
			networking.sendData(data);
			break;
		}
		case ClientGUI.SaveSelected:{
			dataToSend.clear();
			//receive selected data from gui and update it only
			break;
		}
		case "Connect":{
			networking.Connect(34567);
			break;
		}
		case Networking.newServerData:{
			JSONObject serverData = networking.getData();
			if(gui.isSaveAll())
				
			gui.setNewData(serverData);
			break;
		}
		case Networking.ConnectionTimeOut:{
			System.out.println("Connection Time Out");
			gui.ReportAnError("Unable to connect to server");
			break;
		}
		case Networking.Connected:{
			gui.Connected();
			//networking.sendData(gui.getRememberedConnection());//auto registers to the server if a previous sign in exists
			break;
		}
		default:{
			System.out.println("Controller default");
			break;
		}
		}
	}

}
