package view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ServerGUI implements ActionListener{

	private JFrame frame;
	private String userName,userPassword;
	private JMenu User;
	private PropertyChangeSupport pcs;
	public static final String SignIn = "SignIn";
	public static final String SignUp = "SignUp";
	public static final String TurnOn = "Turn server on";
	public static final String TurnOff = "Turn server off";
	public static final String noUser = "No User";
	public static final String disconnect = "Disconnect";
	public static final String Options = "Options";
	public static final String Restart = "Restart";
	public static final String User_Access = "User Access";
	public static final String Activity_Monitor = "Activity_Monitor";
	public static final String Show_Users = "Show All Users";
	public static final String Connections = "Connection Numbers";
	public ServerGUI() {
		frame = new JFrame("Server Controller");
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//frame.setUndecorated(true);
		
		pcs = new PropertyChangeSupport(this);
		User = new JMenu("No User");
		User.setBackground(Color.RED);
		JMenuItem disconnect = new JMenuItem("Disconnect");
		User.add(disconnect);
		JMenuBar menuBar = new JMenuBar();
		JMenu Menu_file = new JMenu("File");
		JMenu Menu_user = new JMenu("User");
		JMenu Menu_server = new JMenu("Server");
		JMenuItem MenuItem_showUsers = new JMenuItem("Get All Users");
		JMenuItem MenuItem_displayErrors = new JMenuItem("Display Errors");
		JMenuItem MenuItem_exit = new JMenuItem("exit");
		JMenuItem MenuItem_activityMonitor = new JMenuItem("Activity Monitor");
		JMenuItem MenuItem_SingIn = new JMenuItem("SignIn");
		JMenuItem MenuItem_SignUp = new JMenuItem("SignUp");
		JMenuItem MenuItem_Options = new JMenuItem("Options");
		JMenuItem MenuItem_Restart = new JMenuItem("Restart Server");
		JMenuItem MenuItem_serverStatusChange = new JMenuItem("Turn server on");
		MenuItem_serverStatusChange.setBackground(Color.GREEN);
		JMenuItem MenuItem_access = new JMenuItem("User Access");
		Menu_user.add(MenuItem_access);
		Menu_server.add(MenuItem_serverStatusChange);
		Menu_server.add(MenuItem_activityMonitor);
		Menu_server.add(MenuItem_displayErrors);
		Menu_server.add(MenuItem_showUsers);
		Menu_server.add(MenuItem_Restart);
		JMenu Menu_ServerSupport = new JMenu("IT");
		Menu_ServerSupport.add(MenuItem_SingIn);
		Menu_ServerSupport.add(MenuItem_SignUp);
		Menu_file.add(Menu_ServerSupport);
		Menu_file.add(MenuItem_Options);
		Menu_file.add(MenuItem_exit);
		menuBar.add(Menu_file);
		menuBar.add(Menu_user);
		menuBar.add(Menu_server);
		menuBar.add(User);
		MenuItem_showUsers.addActionListener(this);
		MenuItem_displayErrors.addActionListener(this);
		MenuItem_exit.addActionListener(this);
		MenuItem_activityMonitor.addActionListener(this);
		MenuItem_SingIn.addActionListener(this);
		MenuItem_SignUp.addActionListener(this);
		MenuItem_serverStatusChange.addActionListener(this);
		disconnect.addActionListener(this);
		frame.setSize(400, 400);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		switch (actionEvent.getActionCommand()) {
		case "All Users":
			
			break;
		case "Display Errors":
		
			break;
		case "exit":
			System.exit(0);
			break;
		case "Activity Monitor":
			
			break;
		case "SignIn":{
			JPanel panel = new JPanel(new GridLayout(3, 1,10,50));
			JLabel user_label = new JLabel("User name");
			JTextField user_TXT = new JTextField();
			JLabel password_label = new JLabel("Password");
			JPasswordField password_TXT = new JPasswordField(); 
			JButton submit = new JButton("SignIn");
			panel.add(user_label);
			panel.add(user_TXT);
			panel.add(password_label);
			panel.add(password_TXT);
			panel.add(new JLabel());
			panel.add(submit);
			submit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					userName = user_TXT.getText();
					userPassword = password_TXT.getPassword().toString();
					pcs.firePropertyChange("SignIn", false, true);
					frame.remove(panel);
					frame.revalidate();
					frame.repaint();
					//checks with data base if the user exists
				}
			});
			frame.add(panel,BorderLayout.CENTER);
			frame.revalidate();
			break;
			}
		case "SignUp":{
			JPanel panel = new JPanel(new GridLayout(3, 1,10,50));
			JLabel user_label = new JLabel("User name");
			JTextField user_TXT = new JTextField();
			JLabel password_label = new JLabel("Password");
			JPasswordField password_TXT = new JPasswordField(); 
			JButton submit = new JButton("SignUp");
			panel.add(user_label);
			panel.add(user_TXT);
			panel.add(password_label);
			panel.add(password_TXT);
			panel.add(new JLabel());
			panel.add(submit);
			submit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					userName = user_TXT.getText();
					userPassword = password_TXT.getPassword().toString();
					pcs.firePropertyChange("SignUp", false, true);
					frame.remove(panel);
					frame.revalidate();
					frame.repaint();
					//adds new user to database
				}
			});
			frame.add(panel,BorderLayout.CENTER);
			frame.revalidate();
			break;
		}
		case "Turn server on":
		{
			JMenuItem serverStatus = (JMenuItem)actionEvent.getSource();
			serverStatus.setText("Turn server off");
			serverStatus.setBackground(Color.RED);
			pcs.firePropertyChange("Turn server on", false, true);
			System.out.println("Server is on");
			break;
		}
		case "Turn server off":
		{
			JMenuItem serverStatus = (JMenuItem)actionEvent.getSource();
			pcs.firePropertyChange("Turn server off", true, false);
			serverStatus.setBackground(Color.GREEN);
			serverStatus.setText("Turn server on");
			System.out.println("Server is off");
			break;	
		}
		case "disconnect":
		{
			break;
		}
		case Show_Users:{
			pcs.firePropertyChange(Show_Users,false,true);
			break;
		}
		default:
			
			break;
		}
		
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}
	public String getUserName()
	{
		return userName;
	}
	public String getUserPassword()
	{
		return userPassword;
	}
	//sets the user name in the menuBar if they exist or No User if they don't
	public void setUser(String userName)
	{
		User.setText(userName);
		User.setBackground(Color.GREEN);
	}
	public void Error(String error)
	{
		JPanel panel = new JPanel();
		JLabel label = new JLabel(error);
		JButton ok_btn = new JButton("Ok");
		label.setBounds(20, 50, 200, 50);
		ok_btn.setBounds(180, 150, 40, 50);
		panel.add(label);
		panel.add(ok_btn);
		JFrame frame = new JFrame("Error Frame");
		frame.add(panel);
		frame.revalidate();
		frame.repaint();
		ok_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				frame.dispose();
			}
		});
	}
	
	public void setNumberOfActiveUsers(int number)
	{
		//for the UI
	}
}
