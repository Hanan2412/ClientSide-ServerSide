package clientSide;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.parser.JSONObject;





public class Client {

	public static final String senderStatus = "User";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			InetAddress address = InetAddress.getByName("192.168.1.18");
			Socket socket = new Socket(address,34567);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true) {
				JSONObject object = new JSONObject();
				object.put("senderStatus", senderStatus);
				object.put("command", "diagnostic_Message");
				//out.writeObject("this is main pc");
				out.writeObject(object);
				JSONObject receiveObject = (JSONObject)in.readObject();
				System.out.println("this is message from server: " + receiveObject.toString());
				
				if(receiveObject.toString().equals("exit"))
					break;
			}
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}
	}

}
