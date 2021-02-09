package server;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{//this class implements runnable because the main GUI thread freezes once this class is invoked
	
	private ServerSocket serverSocket;
	private Socket socket;
	private boolean runnable = false;
	private PropertyChangeSupport pcs;
	private int numberOfConnections;
	public static Vector<ServerHandler>ar = new Vector<>();
	private ExecutorService threadPool;
	private final int numberOfAllowedThreads = 5;
	public Server(int port)
	{
		numberOfConnections = 0;
		try {
			serverSocket = new ServerSocket(port);
			pcs = new PropertyChangeSupport(this);
			threadPool = Executors.newFixedThreadPool(numberOfAllowedThreads);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void TurnServerOnOrOff(boolean on)
	{
		if(on) {
			runnable = true;
			
			  Thread runServer = new Thread(this);
			  runServer.setName("runServer");
			  runServer.start();
		}
		else
			runnable = false;
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public int getNumberOfConnections()
	{
		return numberOfConnections;
	}

	@Override
	public void run() {
		while(runnable = true) {
		try {
			socket = serverSocket.accept();
			numberOfConnections++;
			ServerHandler handler = new ServerHandler(socket,numberOfConnections);
			threadPool.execute(handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
	}
}
