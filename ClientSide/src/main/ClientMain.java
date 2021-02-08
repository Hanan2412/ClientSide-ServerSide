package main;

import client.ClientGUI;
import controller.Controller;
import controller.ControllerConnections;
import networking.Networking;

public class ClientMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientGUI gui = new ClientGUI();
		//Networking networking = new Networking(34567);
		Networking networking = new Networking();
		ControllerConnections controller = new Controller(networking,gui);
		networking.addPropertyChangeListener(controller);
		gui.addPropertyChangeListener(controller);
		//networking.Connect(34567);
	}

}
