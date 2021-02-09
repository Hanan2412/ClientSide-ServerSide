package server;

import controller.ServerController;
import model.DataBase;
import view.ServerGUI;

public class ServerMain {

	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ServerDefinition serverDefinition = new ServerDefinition(34567);
		ServerGUI serverGUI = new ServerGUI();
		ServerController serverController = new ServerController();
		serverGUI.addPropertyChangeListener(serverController);
		serverController.setServerGUI(serverGUI);
		Server serverDefinition = new Server(34567);
		serverDefinition.addPropertyChangeListener(serverController);
		serverController.setServerDefenition(serverDefinition);
	}*/

	public static void main(String[] args)
	{
		Server server = new Server(34567);
		server.TurnServerOnOrOff(true);
	}
}
