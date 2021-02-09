package controller;

import java.beans.PropertyChangeEvent;


import model.DataBase;
import server.Server;
import view.ServerGUI;
//Local
public class ServerController implements ServerContoller{


	private ServerGUI serverGUI;
	private DataBase DataBase; 
	private Server serverDefenition;
	public ServerController() {
		
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch(evt.getPropertyName()) {
		case ServerGUI.SignIn:{
			/*if(DataBase.checkSignIn(serverGUI.getUserName(),serverGUI.getUserPassword()))
				serverGUI.setUser(DataBase.getCurrentUserName());
			else
				serverGUI.setUser(ServerGUI.noUser);
			System.out.println("aaaaaaa");*/
			break;
		}
		case ServerGUI.SignUp:{
			/*if(!DataBase.signUpUser(serverGUI.getUserName(), serverGUI.getUserPassword()))
				serverGUI.Error("an error has accured while attempting to create a user, try again later");
			System.out.println("bbbbbb");*/
			break;
		}
		case ServerGUI.TurnOn:{
			serverDefenition.TurnServerOnOrOff(true);
			System.out.println("cccccccccccc");
			break;
		}
		case ServerGUI.TurnOff:{
			serverDefenition.TurnServerOnOrOff(false);
			System.out.println("dddddddddddd");
			break;
		}
		case ServerGUI.disconnect:{
			break;
		}
		case ServerGUI.Activity_Monitor:{
			break;
		}
		case ServerGUI.Options:{
			break;
		}
		case ServerGUI.User_Access:{
			break;
		}
		case ServerGUI.Show_Users:{
			serverGUI.setNumberOfActiveUsers(serverDefenition.getNumberOfConnections());
			break;
		}
		default:{
			break;
		}
		}
		
	}
	
	public void setServerGUI(ServerGUI serverGUI)
	{
		this.serverGUI = serverGUI;
	}
	
	public void setServerDataBase(DataBase database)
	{
		this.DataBase = database;
	}

	public void setServerDefenition(Server serverDefinition)
	{
		this.serverDefenition = serverDefinition;
	}
	
}
