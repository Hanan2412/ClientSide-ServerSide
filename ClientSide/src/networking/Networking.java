package networking;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


import org.json.simple.parser.JSONObject;

public class Networking extends Thread implements Connections{

	private Socket socket;
	private int port;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean connectionEstablished = true;
	private boolean runnable;
	private JSONObject dataRecieved;
	private JSONObject noNewDataObject;
	private PropertyChangeSupport pcs;
	public static final String newServerData = "newDataFromServer";
	public static final String ConnectionTimeOut = "Connection Time Out";
	public static final String Connected = "Connected";
	@SuppressWarnings("unchecked")
	public Networking(int port) {
		this.port = port;
		InetAddress address;
		pcs = new PropertyChangeSupport(this);
		try {
			address = InetAddress.getByName("192.168.1.18");//current server address
			socket = new Socket(address,port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			runnable = true;
			noNewDataObject = new JSONObject();
			noNewDataObject.put("No Data", true);
			Thread networkingThread = new Thread(Networking.this);
			networkingThread.setName("Networking Thread");
			networkingThread.start();
		} catch (IOException e) {
			e.printStackTrace();
			connectionEstablished = false;
			pcs.firePropertyChange(Networking.ConnectionTimeOut,true,false);
		}

	}
	
	/*
	 * the following 2 methods (Networking(),Connect) are to replace the original Constructor
	 */
	
	
	public Networking() {
		//almost empty constructor so errors can be sent to the gui, because the listener is set after the original constructor is lunched
		pcs = new PropertyChangeSupport(this);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void Connect(int port)
	{
		//Connects the Client side to the Server side
		this.port = port;
		InetAddress address;
		try {
			address = InetAddress.getByName("192.168.1.18");//current server address
			socket = new Socket(address,port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			runnable = true;
			noNewDataObject = new JSONObject();
			noNewDataObject.put("No Data", true);
			Thread networkingThread = new Thread(Networking.this);
			networkingThread.setName("Networking Thread");
			networkingThread.start();
			pcs.firePropertyChange(Networking.Connected,true,false);
		} catch (IOException e) {
			e.printStackTrace();
			connectionEstablished = false;
			pcs.firePropertyChange(Networking.ConnectionTimeOut,true,false);
		}
	}
	
	
	@Override
	public void updatePort(int port) {
		this.port = port;
		try {
			in.close();
			out.close();
			socket.close();
			runnable = false;
			RestartConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void RestartConnection()
	{
		InetAddress address;
		try {
			address = InetAddress.getByName("192.168.1.18");//current server address
			socket = new Socket(address,port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			runnable = true;
		} catch (IOException e) {
			e.printStackTrace();
			connectionEstablished = false;
		}
	}
	
	@Override
	public void sendData(JSONObject object) {
		// TODO Auto-generated method stub
		if(connectionEstablished)
		{
			try {
				out.writeObject(object);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	@Override
	public JSONObject getData() {
		// TODO Auto-generated method stub
		return dataRecieved;	
	}

	@Override
	public void run() {
		super.run();
		while(runnable)
		{
			try {
				synchronized (in) {
					System.out.println("money");
					 dataRecieved = (JSONObject)in.readObject();
					 pcs.firePropertyChange(Networking.newServerData,true,false);
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void TerminateConnection()
	{
		runnable = false;
		try {
			if(in!=null) {
			in.close();
			out.close();
			socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
}
