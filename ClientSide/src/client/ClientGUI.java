package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.desktop.ScreenSleepEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.YearMonth;

import org.json.simple.parser.JSONObject;




public class ClientGUI implements ActionListener,MouseListener{

	private JFrame frame;
	
	public static final String File = "File";
	public static final String User = "User";
	public static final String Help = "Help";
	public static final String Project = "Project";
	public static final String SignIn = "SignIn";
	public static final String SignUp = "SignUp";
	public static final String Exit = "Exit";
	public static final String GetData = "Get Data";
	public static final String About = "About";
	public static final String Profile = "Profile";
	public static final String Name = "Name";
	public static final String Password = "Password";
	public static final String Email = "Email";
	public static final String SaveAll = "Save All";
	public static final String SaveSelected = "Save Selected";
	public static final String CreateNewTable = "Create New Table";
	public static final String DeleteTable = "Delete Table";
	public static final String SenderStatus = "senderStatus";
	public static final String Network = "Network";
	public static final String Appoitement = "Appoitment";
	public static final String Command = "command";
	public static final String Settings = "Settings";
	public static final String TableName = "TableName";
	public static final String ConnectedUser = "No User";
	
	private JSONObject mouseData;
	private JSONObject sendData;
	private JSONObject localData;
	private PropertyChangeSupport pcs;
	private String userStatus = "User";
	private String rememberName,rememberPassword,rememberEmail;

	private String currentTable = "table1";

	private JComboBox<String>tables;
	
	private Vector<String>tableNames;
	private JTable table;
	private JPopupMenu popupMenu;
	
	private ArrayList<String>data;
	private ArrayList<String>columnNames;
	private String newTableName;
	private String tableToRemove;
	
	private KeyEventDispatcher key = null;
	
	private JMenu ConnectedAs;

	private ArrayList<Integer>drawCoordinates;
	private boolean keyLogger;
	
	private boolean inDrawPanel;
	private boolean inCalendar;
	private boolean inProfile;
	private boolean inTables = true;
	
	private boolean runThread = false;
	private boolean Connected;

	private boolean autoConnect = true;
	private boolean autoRequest = true;
	private boolean autoForget = false;
	private boolean autoSaveOnExit = true;
	private boolean autoSaveOnTabChange = true;
	private boolean autoLogInOnSave = false;
	private boolean saveAll = false;
	
	public ClientGUI()
	{
		tableNames = new Vector<>(3);
		tableNames.add("table1");
		tableNames.add("table2");
		tableNames.add("table3");
		
		drawCoordinates = new ArrayList<>();
		
		sendData = new JSONObject();
		sendData.clear();

		frame = new JFrame("Client");
		pcs = new PropertyChangeSupport(this);
		JMenuBar menuBar = new JMenuBar();
		JMenu File = new JMenu(ClientGUI.File);
		JMenu User = new JMenu(ClientGUI.User);
		JMenu Help = new JMenu(ClientGUI.Help);
		JMenu Project = new JMenu(ClientGUI.Project);
		JMenu File_Network = new JMenu(ClientGUI.Network);
		JMenuItem File_SignIn = new JMenuItem(SignIn);
		JMenuItem File_SignUp = new JMenuItem(SignUp);
		JMenuItem File_Disconnect = new JMenuItem("Disconnect");
		JMenuItem File_exit = new JMenuItem(Exit);
		JMenuItem File_settings = new JMenuItem("Settings");
		File_settings.addActionListener(this);
		JMenuItem File_keyLogger = new JMenuItem("Key Logger");
		File_keyLogger.setToolTipText("a key logger will log every key stroke you press while the program is running");
		JMenuItem Project_getData = new JMenuItem(GetData);
		JMenuItem Help_about = new JMenuItem(About);
		JMenuItem User_profile = new JMenuItem(Profile);
		JMenuItem Project_UpdateAll = new JMenuItem(SaveAll);
		JMenuItem Project_UpdateSelected = new JMenuItem(SaveSelected);
		File_Network.add(File_Disconnect);
		ConnectedAs = new JMenu(ConnectedUser);
		ConnectedAs.setToolTipText("You are connected as: " + ConnectedAs.getText());
		
		////////////////TABLE///////////////

		JMenuItem newTable = new JMenuItem(CreateNewTable);
		JMenuItem deleteTable = new JMenuItem(DeleteTable);
		newTable.addActionListener(this);
		deleteTable.addActionListener(this);
		Project.add(newTable);
		Project.add(deleteTable);
		Project.add(new JSeparator());
		tables = new JComboBox<>(tableNames);
		tables.addActionListener(this);
		DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
		listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		tables.setRenderer(listRenderer);
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		table = new JTable(defaultTableModel);
		defaultTableModel.addColumn("A");
		defaultTableModel.addColumn("B");
		defaultTableModel.addColumn("C");
		defaultTableModel.addRow(new Object[] {"","",""});
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		JScrollPane scrollPane = new JScrollPane(table);
		setTableHeaders();
		setPopUpMenu();
		scrollPane.setComponentPopupMenu(popupMenu);
		////////////END TABLE//////////////
		File.add(File_SignIn);
		File.add(File_SignUp);
		File.add(File_Network);
		File.add(File_keyLogger);
		File.add(File_settings);
		File.add(File_exit);
		Help.add(Help_about);
		User.add(User_profile);
		Project.add(Project_getData);
		//Project.add(Project_UpdateSelected);
		Project.add(Project_UpdateAll);
		menuBar.add(File);
		//menuBar.add(User);
		menuBar.add(Project);
		menuBar.add(Help);
		File_exit.addActionListener(this);
		File_SignIn.addActionListener(this);
		File_SignUp.addActionListener(this);
		File_keyLogger.addActionListener(this);
		Project_getData.addActionListener(this);
		Project_UpdateAll.addActionListener(this);
		Project_UpdateSelected.addActionListener(this);
		Help_about.addActionListener(this);
		User_profile.addActionListener(this);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(tables);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(scrollPane);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel,BorderLayout.CENTER);
		//mainPanel.add(bottomPanel,BorderLayout.CENTER);
		///////////////Tabs//////////////
		JPanel mainPanel1 = new JPanel();
		mainPanel1.setLayout(new BorderLayout());
		mainPanel1.add(topPanel,BorderLayout.PAGE_START);
		mainPanel1.add(bottomPanel,BorderLayout.CENTER);
		JTabbedPane tabbedPane = new JTabbedPane();
		ImageIcon icon = null;
		tabbedPane.addTab("Tables", icon,mainPanel1,"Shows the tables");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		JPanel calendarPanel = new JPanel();
		GridLayout gridLayout = new GridLayout(5, 7);
		calendarPanel.setLayout(gridLayout);
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		Dimension textAreaSize = textArea.getPreferredSize();
		calendarPanel.setPreferredSize(new Dimension((int)textAreaSize.getWidth()*gridLayout.getColumns(),(int)textAreaSize.getHeight()*gridLayout.getRows()));
		for(int i = 1;i<=setCalendar();i++) {
			int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			JTextArea area = new JTextArea(String.valueOf(i));
			area.setText(String.valueOf(i));
			area.setEditable(false);
			area.addMouseListener(ClientGUI.this);
			area.setToolTipText("the " + i + " th day of the month");
			area.setComponentPopupMenu(createPopUp(area));
			JLabel appointment = new JLabel("appointment");
			appointment.setToolTipText(appointment.getText().toString());
			appointment.addMouseListener(ClientGUI.this);
			appointment.setComponentPopupMenu(createPopUp(appointment));
			JPanel calendarPane = new JPanel();
			calendarPane.setLayout(gridLayout);
			calendarPane.add(area);
			calendarPane.add(appointment);
			calendarPane.addMouseListener(ClientGUI.this);
			calendarPane.setComponentPopupMenu(createPopUp(calendarPane));
			if(i == today)
			{
			calendarPane.setBackground(Color.GRAY);	
			area.setText(area.getText().toString() + " today");
			}
			JPanel mainCalendar = new JPanel();
			mainCalendar.setToolTipText("superGayyyyyyy");
			mainCalendar.setLayout(new BoxLayout(mainCalendar, BoxLayout.Y_AXIS));
			mainCalendar.add(calendarPane);
			calendarPanel.add(mainCalendar);
		}
        tabbedPane.addTab("Calendar", icon, calendarPanel,"Shows the Calendar");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        JComponent panel3 = makeTextPanel("Profile");
        
        /*tabbedPane.addTab("Profile", icon, panel3,
                "Shows User profile Data");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);*/
        
        /*JComponent ChatPanel = makeTextPanel("Team Chat");
        tabbedPane.addTab("Group Chat", icon,ChatPanel,"Chat");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_5);*/
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        frame.setContentPane(tabbedPane);
		/////////////END Tabs////////////
		menuBar.add(ConnectedAs);
		frame.setJMenuBar(menuBar);
		/////////Table///////////////////
		/////////////////////////////////
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		

		try {
			File file = new File("C:\\Users\\Hanan\\normal_eclipse-workspace\\ClientSide\\Remember me.txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			rememberName = bufferedReader.readLine();
			rememberPassword = bufferedReader.readLine();
			rememberEmail = bufferedReader.readLine();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				
				if(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("Tables"))
				{
					if(!inTables && autoSaveOnTabChange) {
					tabbedPane.setSelectedIndex(1);
					saveCalendar();
					inTables = true;
					tabbedPane.setSelectedIndex(0);
					}
					inCalendar = false;
				}
				else if(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("Calendar"))//calendar tab
				{
					 if(!inCalendar)
					 {
						 loadCalendar();
					 }
					inProfile = false;
					inCalendar = true;
					inDrawPanel = false;
					runThread = false;
					inTables = false;
					//loadCalendar();//sends a request to the server to load existing calendar
					//saveCalendar();
				}
			}
		});
		readSettingsLocally();
		if(autoConnect)
			{
			
			Thread waitThread = new Thread() {
					
					@Override
					public void run() {
						try {
							sleep(3000);
							pcs.firePropertyChange("Connect", true, false);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				waitThread.start();
			}
		if(!autoForget)
		{
			try {
				File file = new File("C:\\Users\\Hanan\\normal_eclipse-workspace\\ClientSide\\Remember me.txt");
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				rememberName = bufferedReader.readLine();
				rememberPassword = bufferedReader.readLine();
				rememberEmail = bufferedReader.readLine();

				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		if(autoRequest)
			pcs.firePropertyChange(ClientGUI.GetData, true, false);
		WindowAdapter adapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				//save all function should be called
				System.out.println("Pressed exit");
				if(autoSaveOnExit) {
				//saveCalendar();
				pcs.firePropertyChange(Exit,false,true);
				}
				saveSettingsLocally();
			}
		};
		frame.addWindowListener(adapter);

	}

	//setting popUpmenu for calendar components
	private JPopupMenu createPopUp(JComponent component)
	{
		if(component instanceof JLabel)
		{
			JLabel label = (JLabel)component;
			JPopupMenu pop = new JPopupMenu("Appoitment Edit");
			JMenuItem item = new JMenuItem("edit appoitment");
			item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					createDialong("Edit appoitment", "Appoitment new Text",label);	
				}
			});
			JMenuItem delete = new JMenuItem("delete appoitment");
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					Container c = label.getParent();
					c.remove(label);
				}
			});
			pop.add(item);
			pop.add(delete);
			return pop;
		}
		else if(component instanceof JPanel)
		{
			JPopupMenu pop = new JPopupMenu("Fill the void");
			JMenuItem addAppoitment = new JMenuItem("Add Appoitment");
			addAppoitment.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					createDialong("Add new appoitment", "your new appoitment", component);
				}
			});
			pop.add(addAppoitment);
			return pop;
		}
		else if(component instanceof JTextArea)
		{
			JPopupMenu pop = new JPopupMenu("Set a Reminder");
			JMenuItem item = new JMenuItem("Set this date as a reminder");
			item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTextArea date = (JTextArea)component;
					date.setBackground(Color.YELLOW);
					
				}
			});
			pop.add(item);
			return pop;
		}
		else return null;
	}
	private JComponent makeTextPanel(String text)
	{
		JPanel panel = new JPanel(false);
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1,1));
		panel.add(label);
		return panel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		switch(e.getActionCommand()) {
		case ClientGUI.SignIn:{
			JFrame signIn = new JFrame("SignIn");
			String[] labels = {"Name: ","Password: ","Email: "};
			int numPairs = labels.length;
			JPanel panel = new JPanel(new SpringLayout());
			for(int i = 0;i<numPairs;i++)
			{
				JLabel label = new JLabel(labels[i],JLabel.TRAILING);
				panel.add(label);
				if(i == 1)
				{
					JPasswordField password = new JPasswordField(10);
					label.setLabelFor(password);
					panel.add(password);
				}
				else {
					JTextField textField = new JTextField(10);
					label.setLabelFor(textField);
					panel.add(textField);
				}
			}
			JCheckBox rememberMe = new JCheckBox();
			JButton ok = new JButton("Ok");
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Component[] components = panel.getComponents();
					JTextField name = (JTextField)components[1];
					JPasswordField password = (JPasswordField)components[3];
					JTextField email = (JTextField)components[5];

					//regular expression pattern to identify a valid email address
					Pattern pattern = Pattern.compile("([A-Za-z0-9]+)(@)([A-Za-z0-9]+)([.])([A-Za-z0-9]+)");
					Matcher match = pattern.matcher(email.getText());
					if(match.find()) {
						sendData = new JSONObject();
						sendData.put(SenderStatus,ClientGUI.User);
						sendData.put(Command, ClientGUI.SignIn);
						sendData.put(ClientGUI.Name, name.getText());
						sendData.put(ClientGUI.Password, String.valueOf(password.getPassword()));
						sendData.put(ClientGUI.Email, email.getText());
						if(rememberMe.isSelected())
						{
							System.out.println("will be remembered");
							try {
								PrintWriter writer = new PrintWriter("Remember me.txt");
								writer.println(ClientGUI.Name + ": " + name.getText());
								writer.println(ClientGUI.Password + ": " + String.valueOf(password.getPassword()));
								writer.println(ClientGUI.Email + ": " + email.getText());
								writer.checkError();
								writer.close();
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
						}
						System.out.println("SignIn send data: " + sendData);
						pcs.firePropertyChange(ClientGUI.SignIn,true,false);
						signIn.dispose();
					}
					else
					{
						email.setBackground(Color.RED);
						email.setToolTipText("Enter correct email form example@example.com");
					}
				}
			});

			JButton cancell = new JButton("Cancel");
			cancell.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signIn.dispose();
				}
			});

			JLabel remember = new JLabel("Remember me");
			remember.setLabelFor(rememberMe);

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(remember);
			buttonPanel.add(rememberMe);
			buttonPanel.add(cancell);
			buttonPanel.add(ok);

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.add(panel);
			mainPanel.add(buttonPanel);


			SpringUtilities.makeCompactGrid(panel,numPairs, 2,6,6,6,6);
			panel.setOpaque(true);

			signIn.setContentPane(mainPanel);
			signIn.pack();
			signIn.setResizable(false);
			signIn.setVisible(true);
			break;
		}
		case ClientGUI.SignUp:{
			Sign("SignUp", false);
			break;
		}
		case ClientGUI.GetData:{
			pcs.firePropertyChange(ClientGUI.GetData,false,true);
			break;
		}
		case ClientGUI.Exit:{
			pcs.firePropertyChange(ClientGUI.Exit, false, true);
			break;
		}
		case ClientGUI.About:{
			JFrame about = new JFrame("About");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
		    JComponent panel1 = makeTextPanel("Client Side Version 0.0.3 " + dateFormat.format(date));
			Container container = about.getContentPane();
			container.add(panel1);
			about.setSize(400, 400);
			about.setResizable(false);
			about.setVisible(true);
			break;
		}
		case ClientGUI.Profile:{
			//gets all the information about the currently logged in user
			pcs.firePropertyChange(ClientGUI.Profile, false, true);
			break;
		}
		case ClientGUI.SaveAll:{
			saveAll = true;
			if(!autoLogInOnSave) {
				if(!ConnectedAs.getText().equals(ClientGUI.ConnectedUser)) {
				readFromTable();
				sendData.put(TableName,tables.getItemAt(tables.getSelectedIndex()));
				sendData.put(SenderStatus,User);
				sendData.put(Command, SaveAll);
				pcs.firePropertyChange(ClientGUI.SaveAll, true, false);
				}
				else {
					ReportAnError("SignUp or SignIn to your account in order to save to remote database");
				}
				break;
			}
			else
			{
				Sign(SignIn,true);
			}
		}
		case ClientGUI.SaveSelected:{
			SelectedData();
			pcs.firePropertyChange(ClientGUI.SaveSelected, true, false);
			break;
		}
		case "comboBoxChanged":{
			if(!currentTable.equals(tables.getItemAt(tables.getSelectedIndex()))) {
				if(!ConnectedAs.getText().equals(ClientGUI.ConnectedUser)) {//saves the table before changing it
					readFromTable();
					sendData.put(TableName,tables.getItemAt(tables.getSelectedIndex()));
					sendData.put(SenderStatus,User);
					sendData.put(Command, SaveAll);
					pcs.firePropertyChange(ClientGUI.SaveAll, true, false);
					}
				currentTable = tables.getItemAt(tables.getSelectedIndex());//creates new empty table and then calls for the data in the server to populate it
				DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
				defaultTableModel.getDataVector().removeAllElements();
				defaultTableModel.fireTableDataChanged();
				defaultTableModel.addRow(new Object[] {"","",""});
				defaultTableModel.fireTableDataChanged();
				pcs.firePropertyChange(ClientGUI.GetData, false, true);
			}
			break;
		}
		case ClientGUI.CreateNewTable:{
			createDialong("New Table", "New Table Name", null);
			break;
		}
		case ClientGUI.DeleteTable:{
			createDialong("Delete Table", "Select the table to delete", null);
			break;
		}
		case "Key Logger":{
			if(!keyLogger) {
				keyLogger = true;
				keyLogger(keyLogger);
				JMenuItem keyLogger = (JMenuItem)e.getSource();
				keyLogger.setBackground(Color.GREEN);
			}
			else {
				keyLogger = false;
				keyLogger(keyLogger);
				JMenuItem keyLogger = (JMenuItem)e.getSource();
				keyLogger.setBackground(null);
			}
				
			break;
		}
		case ClientGUI.Settings:{
			//opens settings page - ideally changes the main panel on screen
			
			JFrame SettingsFrame = new JFrame("Settings");
			JTabbedPane settingsTabs = new JTabbedPane();
			settingsTabs.setTabPlacement(JTabbedPane.LEFT);
			JPanel title = new JPanel();
			JLabel titleLabel = new JLabel("set what the program does on startup");
			title.add(titleLabel);
			JPanel panel = new JPanel();
			panel.setLayout(new SpringLayout());
			JCheckBox checkBox = new JCheckBox();
			checkBox.setEnabled(true);
			checkBox.setSelected(autoConnect);
			checkBox.setToolTipText("checked checkbox means the program will try to connect automaticaly to the server at startup");
			checkBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox c = (JCheckBox) e.getSource();
					autoConnect = c.isSelected();
					System.out.println(autoConnect);
				}
			});
			JLabel label = new JLabel("connect at startup");
			label.setLabelFor(checkBox);
			panel.add(label);
			panel.add(checkBox);
			label = new JLabel("forget me");
			checkBox = new JCheckBox();
			checkBox.setSelected(autoForget);
			checkBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox c = (JCheckBox) e.getSource();
					autoForget = c.isSelected();
					System.out.println(autoForget);
				}
			});
			label.setLabelFor(checkBox);
			checkBox.setToolTipText("check means you'll have to signIn every startup");
			panel.add(label);
			panel.add(checkBox);
			label = new JLabel("Load Data");
			checkBox = new JCheckBox();
			checkBox.setSelected(autoRequest);
			checkBox.setToolTipText("checked means the the program will request the saved data");
			checkBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox c = (JCheckBox) e.getSource();
					autoRequest = c.isSelected();
					System.out.println(autoRequest);
				}
			});
			label.setLabelFor(checkBox);
			panel.add(label);
			panel.add(checkBox);
			panel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
			SpringUtilities.makeCompactGrid(panel,3, 2,6,6,6,6);
			panel.setOpaque(true);
			JPanel mainWindowPanel = new JPanel();
			mainWindowPanel.setLayout(new BoxLayout(mainWindowPanel, BoxLayout.Y_AXIS));
			mainWindowPanel.add(title);
			mainWindowPanel.add(panel);
			settingsTabs.addTab("StartUp", mainWindowPanel);
			
			title = new JPanel(new SpringLayout());
			titleLabel = new JLabel("Project Settings");
			title.add(titleLabel);
			title.add(new JLabel());
			title.setAlignmentX(JPanel.CENTER_ALIGNMENT);
			SpringUtilities.makeCompactGrid(title,1, 2,6,6,6,6);
			panel = new JPanel();
			panel.setLayout(new SpringLayout());
			checkBox = new JCheckBox();
			checkBox.setEnabled(true);
			checkBox.setSelected(autoSaveOnExit);
			checkBox.setToolTipText("auto saves on exit");
			checkBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox c = (JCheckBox) e.getSource();
					autoSaveOnExit = c.isSelected();
					
					System.out.println(autoSaveOnExit);
				}
			});
			label = new JLabel("Auto save on exit");
			label.setLabelFor(checkBox);
			panel.add(label);
			panel.add(checkBox);
			
			checkBox = new JCheckBox();
			checkBox.setSelected(autoSaveOnTabChange);
			checkBox.setToolTipText("saves the data when a tab is switched");
			checkBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox c = (JCheckBox) e.getSource();
					autoSaveOnTabChange = c.isSelected();
					System.out.println(autoSaveOnTabChange);
				}
			});
			label = new JLabel("Auto save on tab switch");
			label.setLabelFor(checkBox);
			panel.add(label);
			panel.add(checkBox);
			
			label = new JLabel("manual saving requiers a password");
			checkBox = new JCheckBox();
			checkBox.setSelected(autoLogInOnSave);
			checkBox.setToolTipText("if checked, a password will be required inorder to save manually. notice that a connection to the server is reqiered");
			checkBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox c = (JCheckBox) e.getSource();
					autoLogInOnSave = c.isSelected();
					System.out.println(autoLogInOnSave);
				}
			});
			label.setLabelFor(checkBox);
			panel.add(label);
			panel.add(checkBox);
			
			panel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
			SpringUtilities.makeCompactGrid(panel,3, 2,6,6,6,6);
			panel.setOpaque(true);
			mainWindowPanel = new JPanel();
			mainWindowPanel.setLayout(new BoxLayout(mainWindowPanel, BoxLayout.Y_AXIS));
			mainWindowPanel.add(title);
			mainWindowPanel.add(panel);
			settingsTabs.addTab("Project Settings", mainWindowPanel);
			
			
			SettingsFrame.add(settingsTabs);
			SettingsFrame.setResizable(false);
			SettingsFrame.pack();
			SettingsFrame.setVisible(true);
			break;
		}
		default:{
			ReportAnError("The thing you clicked on has no function yet");
			System.out.println("An error has accured in actionListener");
			break;
		}
		}
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@SuppressWarnings("unchecked")
	public void setNewData(JSONObject newDataObject)//set table should be called here
	{
		if(newDataObject.containsKey(ClientGUI.SignIn))
		{
			if((Boolean)newDataObject.get(ClientGUI.SignIn))
				{
					ConnectedAs.setText((String) newDataObject.get(ClientGUI.Name));
					ConnectedAs.setToolTipText("You are connected as " + ConnectedAs.getText().toString());
					if(saveAll) {
						readFromTable();
						sendData.put(TableName,tables.getItemAt(tables.getSelectedIndex()));
						sendData.put(SenderStatus,User);
						sendData.put(Command, SaveAll);
						pcs.firePropertyChange(ClientGUI.SaveAll, true, false);
					}
						
				}
			else
				System.out.println("no user under thoes credentials");
		}
		else if(newDataObject.containsKey("Table Data"))
		{
			setTable((ArrayList<String>)newDataObject.get("Rows"),(ArrayList<String>) newDataObject.get("Columns"));
		}
		
		System.out.println(newDataObject.toString());
	}

	public JSONObject getDataToSend()
	{
		return sendData;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getRememberedConnection() {
		if(rememberName != null && rememberEmail != null && rememberPassword != null) {
			JSONObject remember = new JSONObject();
			remember.put(ClientGUI.SenderStatus,userStatus);
			remember.put(Command, ClientGUI.SignIn);
			remember.put(ClientGUI.Name, rememberName);
			remember.put(ClientGUI.Password, rememberPassword);
			remember.put(ClientGUI.Email, rememberEmail);
			return remember;
		}
		return null;
	}
	
	public boolean isSaveAll()
	{
		return saveAll;
	}
	public void ReportAnError(String error)
	{
		JFrame errorFrame = new JFrame("Error");
		JTextPane pane = new JTextPane();
		pane.setEditable(false);
		pane.setText("an error has accured: " + error);
		JScrollPane scrollPane = new JScrollPane(pane);
		Container container = errorFrame.getContentPane();
		container.add(scrollPane,BorderLayout.CENTER);
		errorFrame.setLocation(frame.getLocationOnScreen());;
		errorFrame.setSize(frame.getWidth()/2, frame.getHeight()/2);
		errorFrame.setResizable(false);
		errorFrame.setVisible(true);
		
		if(error.equals("Unable to connect to server"))
		{
			Connected = false;
			Component component = frame.getJMenuBar().getMenu(0).getMenuComponent(1);
			JMenu menu = (JMenu)component;
			JMenuItem disconnect = menu.getItem(0);
			disconnect.setText("Connect");
		}
		errorFrame.pack();
		
	}

	private void setTableHeaders()
	{
		JTableHeader tableHeader = table.getTableHeader();
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		tableHeader.setToolTipText("You can drag a header to a different location in the table");
		tableHeader.setComponentPopupMenu(popupMenu);
	}
	private void setPopUpMenu()
	{
		popupMenu = new JPopupMenu("Edit Table");
		JMenuItem addColumn = new JMenuItem("Add Column");
		JMenuItem removeColumn = new JMenuItem("Remove Column");
		JMenuItem editColumn = new JMenuItem("Edit Column");
		JMenuItem removeLine = new JMenuItem("Remove Row");
		JMenuItem addRow = new JMenuItem("Add Row");
		popupMenu.add(addColumn);
		popupMenu.add(editColumn);
		popupMenu.add(removeColumn);
		popupMenu.add(removeLine);
		popupMenu.add(addRow);
		addColumn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				createDialong("Add New Column", "Enter new column name", null);
			}
		});
		editColumn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(table.getSelectedColumn()!=-1) {
				createDialong("Edit header", "New column name", null);
				JTableHeader tableHeader = table.getTableHeader();
				tableHeader.repaint();
				}
			}
		});

		removeColumn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				JTableHeader tableHeader = table.getTableHeader();
				int selectedColumn = table.getSelectedColumn();
				int columnCount = table.getColumnCount();
				if(table.getSelectedColumn()!=-1)
				{
					TableColumn column = table.getColumnModel().getColumn(selectedColumn);
					table.removeColumn(column);
					table.clearSelection();
					DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
					////////////////
					Vector<Object> columnName = new Vector<>();
					for(int i = 0; i <columnCount; i++)
						columnName.add(defaultTableModel.getColumnName(i));
					columnName.remove(selectedColumn);
					defaultTableModel.setColumnIdentifiers(columnName);
					tableHeader.repaint();

				}
			}
		});

		removeLine.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
				if(table.getSelectedRow() >= 0)
					defaultTableModel.removeRow(table.getSelectedRow());
				else
					System.out.println("Row wasn't selected");
			}
		});

		
		addRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
				defaultTableModel.addRow(new Object[] {});
			}
		});
		//table.add(popupMenu);
		table.setComponentPopupMenu(popupMenu);
	}

	@SuppressWarnings("unchecked")
	private void readFromTable()
	{
		DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
		data = new ArrayList<>();
		columnNames = new ArrayList<>();
		for(int i = 0;i<defaultTableModel.getRowCount();i++)
		{
			for(int j = 0; j<defaultTableModel.getColumnCount();j++) {
				data.add((String) defaultTableModel.getValueAt(i, j));
			}


		}
		for(int i = 0;i<table.getColumnCount();i++)
			columnNames.add(table.getColumnName(i));
		
		sendData = new JSONObject();
		sendData.put("Data", data);
		sendData.put("Cols", columnNames);
	}

	private void setTable(ArrayList<String>rows,ArrayList<String>columns)
	{
		DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
		defaultTableModel.setColumnCount(columns.size());
		defaultTableModel.setRowCount(0);
		Object [] identifires = new Object[columns.size()];
		for(int i = 0;i<columns.size();i++)
		{
			identifires[i] = columns.get(i);
		}
		defaultTableModel.setColumnIdentifiers(identifires);
		for(int i = 0;i<rows.size();i++)
		{
			for(int j = 0;j<columns.size();j++) {
				defaultTableModel.setValueAt(rows.get(i), i, j);
			}
		}
	}
	
	public ArrayList<String>getColsNames()
	{
		return columnNames;
	}
	
	public ArrayList<String>getData(){
		return data;
	}
	public String getTableName()
	{
		return currentTable;
	}
	
	public String getNewTableName()
	{
		return newTableName;
	}
	public String getTableToDelete()
	{
		return tableToRemove;
	}
	
	
	
	
	private HashMap<Dimension,String> SelectedData()
	{
		DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
		data = new ArrayList<>();
		columnNames = new ArrayList<>();
		HashMap<Dimension,String> dataLocation = new HashMap<>();
		int[] selectedRow = table.getSelectedRows();
		int[] selectedColumn = table.getSelectedColumns();
		for(int i = 0;i<selectedRow.length;i++)
		{
			for(int j = 0; j<selectedColumn.length;j++) {
				Dimension index = new Dimension(selectedRow[i], selectedColumn[j]);
				dataLocation.put(index, (String) defaultTableModel.getValueAt(selectedRow[i], selectedColumn[j]));
			}
		}
		System.out.println(dataLocation);
		for(int i = 0;i<table.getColumnCount();i++)
			columnNames.add(table.getColumnName(i));
		
		return dataLocation;
	}
	
	public void Connected()
	{
		System.out.println("Connected function - GUI - connected to server");
		Connected = true;
		Component component = frame.getJMenuBar().getMenu(0).getMenuComponent(2);
		JMenu menu = (JMenu)component;
		JMenuItem disconnect = menu.getItem(0);
		disconnect.setText("Disconnect");
	}
	
	private int setCalendar()
	{
		Calendar calendar = Calendar.getInstance();
		YearMonth yearMonth = YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
		int numberOfDays = yearMonth.lengthOfMonth();
		return numberOfDays;
	}
	
	private void createDialong(String dialogTitle,String labelText,Component component)
	{
		JFrame DialogFrame = new JFrame(dialogTitle);
		JPanel panel = new JPanel(new SpringLayout());
		JLabel label = new JLabel(labelText);
		JTextField NewComponentNameTxt = new JTextField(10);
		JComboBox<String>tableToDelete = new JComboBox<String>(tableNames);
		panel.add(label);
		if(dialogTitle.equals("Delete Table"))
		{
			label.setLabelFor(tableToDelete);
			panel.add(tableToDelete);
		}
		else {
			label.setLabelFor(NewComponentNameTxt);
			panel.add(NewComponentNameTxt);
		}
		
		JButton ok = new JButton("Ok");
		JButton cancel = new JButton("Cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(panel);
		mainPanel.add(buttonPanel);
		SpringUtilities.makeCompactGrid(panel,1, 2,6,6,6,6);
		panel.setOpaque(true);
		DialogFrame.setContentPane(mainPanel);
		DialogFrame.setResizable(false);
		DialogFrame.pack();
		DialogFrame.setVisible(true);
		
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				switch(dialogTitle) {
				
				case "New Table":{
					tableNames.add(NewComponentNameTxt.getText().toString());
					break;
				}
				case "Add new appoitment":{
					JPanel panel = (JPanel)component;
					JLabel label = new JLabel(NewComponentNameTxt.getText().toString());
					label.setToolTipText(label.getText().toString());
					//label.addMouseListener(new mouse());
					label.addMouseListener(ClientGUI.this);
					panel.add(label);
					break;
				}
				case "Edit appoitment":{
					JLabel label = (JLabel)component;
					label.setText(NewComponentNameTxt.getText().toString());
					label.setToolTipText(label.getText().toString());
					break;
				}
				case "Add New Column":{
					TableColumn column = new TableColumn(2);
					column.setHeaderValue(NewComponentNameTxt.getText().toString());
					DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
					defaultTableModel.addColumn(column.getHeaderValue().toString());
					break;
				}
				case "Edit header":{
					DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
					int count = defaultTableModel.getColumnCount();
					Vector<Object> columnName = new Vector<>();
					for(int i = 0; i <count; i++)
						columnName.add(defaultTableModel.getColumnName(i));
					int selected = table.getSelectedColumn();
					String x = (String)columnName.get(selected);
					columnName.remove(selected);
					x = NewComponentNameTxt.getText().toString();
					columnName.add(selected,x);
					defaultTableModel.setColumnIdentifiers(columnName);
				
					break;
				}
				case "Delete Table":{
					
					tableToRemove = tables.getItemAt(tableToDelete.getSelectedIndex());
					tables.removeItemAt(tableToDelete.getSelectedIndex());
					break;
				}
				default:{
					System.out.println("Create dialog  - no dialog was created for the button you pressed");
					break;
				}
				}
				
				DialogFrame.dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DialogFrame.dispose();
			}
		});
	}
	
	@SuppressWarnings("unused")
	private void keyLogger(boolean active)
	{
		KeyboardFocusManager keyboardFocusManager = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();
		if(active)
		{
			 key = new KeyEventDispatcher() {
				
				@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					// TODO Auto-generated method stub
					if(e.getID() == KeyEvent.KEY_RELEASED)
					{
						if(e.getKeyChar() == KeyEvent.VK_SPACE)
						{
							System.out.print(" ");
						}
						else if(e.getKeyChar() == KeyEvent.VK_ENTER)
						{
							System.out.println();
						}
						else
							System.out.print(e.getKeyChar());
					}
					return false;
				}
			};	
			keyboardFocusManager.addKeyEventDispatcher(key);
		}
		else
		{
			if(key!=null) {
			keyboardFocusManager.removeKeyEventDispatcher(key);
			}
		}
	}
	// for game////////////////////////////////////////////////////////////////////
	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(inCalendar) {
			if(e.getSource() instanceof JLabel)
			{
				JLabel label = (JLabel) e.getSource();
				label.setOpaque(true);
				label.setBackground(Color.RED);
			}
			else if(e.getSource() instanceof JTextArea) {
				JTextArea area =(JTextArea) e.getSource();
				if(area.getBackground() != Color.YELLOW)
					area.setBackground(Color.CYAN);
			}
			else if(e.getSource() instanceof JPanel) {
				JPanel panel = (JPanel)e.getSource();
				if(panel.getBackground() != Color.GRAY)
					panel.setBackground(Color.GREEN);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		if(inCalendar) {
			if(e.getSource() instanceof JLabel) {
				JLabel label = (JLabel) e.getSource();
				label.setOpaque(true);
				label.setBackground(null);
			}
			else if(e.getSource() instanceof JTextArea) {
				JTextArea area =(JTextArea) e.getSource();
				if(area.getBackground()!=Color.YELLOW)
					area.setBackground(null);
			}
			else if(e.getSource() instanceof JPanel) {
				JPanel panel = (JPanel)e.getSource();
				if(panel.getBackground() != Color.GRAY)
					panel.setBackground(null);
		}
	
		}
	}
	
	private void Sign(String Title,boolean sign)
	{
		JFrame signIn = new JFrame(Title);
		String[] labels = {"Name: ","Password: ","Email: "};
		int numPairs = labels.length;
		JPanel panel = new JPanel(new SpringLayout());
		for(int i = 0;i<numPairs;i++)
		{
			JLabel label = new JLabel(labels[i],JLabel.TRAILING);
			panel.add(label);
			if(i == 1)
			{
				JPasswordField password = new JPasswordField(10);
				label.setLabelFor(password);
				panel.add(password);
			}
			else {
				JTextField textField = new JTextField(10);
				label.setLabelFor(textField);
				panel.add(textField);
			}
		}
		JCheckBox rememberMe = new JCheckBox();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				Component[] components = panel.getComponents();
				JTextField name = (JTextField)components[1];
				JPasswordField password = (JPasswordField)components[3];
				JTextField email = (JTextField)components[5];

				//regular expression pattern to identify a valid email address
				Pattern pattern = Pattern.compile("([A-Za-z0-9]+)(@)([A-Za-z0-9]+)([.])([A-Za-z0-9]+)");
				Matcher match = pattern.matcher(email.getText());
				if(match.find()) {
					sendData = new JSONObject();
					sendData.put(SenderStatus,ClientGUI.User);
					if(sign)
						sendData.put(Command, ClientGUI.SignIn);
					else
						sendData.put(Command, ClientGUI.SignUp);
					sendData.put(ClientGUI.Name, name.getText());
					sendData.put(ClientGUI.Password, String.valueOf(password.getPassword()));
					sendData.put(ClientGUI.Email, email.getText());
					if(rememberMe.isSelected())
					{
						System.out.println("will be remembered");
						try {
							PrintWriter writer = new PrintWriter("Remember me.txt");
							writer.println(ClientGUI.Name + ": " + name.getText());
							writer.println(ClientGUI.Password + ": " + String.valueOf(password.getPassword()));
							writer.println(ClientGUI.Email + ": " + email.getText());
							writer.checkError();
							writer.close();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
					if(sign) {
					System.out.println("SignIn send data: " + sendData);
					pcs.firePropertyChange(ClientGUI.SignIn,true,false);
					}else
					{
						System.out.println("SignUp send data: " + sendData);
						pcs.firePropertyChange(ClientGUI.SignUp,true,false);
					}
					signIn.dispose();
				}
				else
				{
					email.setBackground(Color.RED);
					email.setToolTipText("Enter correct email form example@example.com");
				}
			}
		});

		JButton cancell = new JButton("Cancel");
		cancell.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				signIn.dispose();
			}
		});

		JLabel remember = new JLabel("Remember me");
		remember.setLabelFor(rememberMe);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(remember);
		buttonPanel.add(rememberMe);
		buttonPanel.add(cancell);
		buttonPanel.add(ok);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(panel);
		mainPanel.add(buttonPanel);


		SpringUtilities.makeCompactGrid(panel,numPairs, 2,6,6,6,6);
		panel.setOpaque(true);

		signIn.setContentPane(mainPanel);
		signIn.pack();
		signIn.setResizable(false);
		signIn.setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	private void saveCalendar()
	{
		sendData = new JSONObject();
		JTabbedPane tabbedPane = (JTabbedPane)frame.getContentPane();
		//JPanel cp = (JPanel)tabbedPane.getTabComponentAt(1);
		JPanel calendarPanel = (JPanel)tabbedPane.getSelectedComponent();
		Component[] components = calendarPanel.getComponents();//each component corresponds to a day in a month
		for(int i = 0;i<components.length;i++)
		{
			sendData.put("day", i);
			JPanel mainPanel = (JPanel)components[i];
			Component[] components1 = mainPanel.getComponents();
			JPanel finalPanel = (JPanel) components1[0];
			Component[] components2 = finalPanel.getComponents();
			for(int j = 0;j<components2.length;j++)
			{
				
				if(components2[j] instanceof JLabel)
				{
					JLabel label = (JLabel)components2[j];
					sendData.put("appoitment" + j, label.getText());
					System.out.println("label " + label.getText());
				}
				else if(components2[j] instanceof JTextArea)
				{
					JTextArea area = (JTextArea)components2[j];
					System.out.println("textarea " + area.getText());
				}
				else System.out.println("wtf");
			}
		}
	}
	/*
	 * sends a request to the server to load this month calendar 
	 */
	@SuppressWarnings("unchecked")
	private void loadCalendar()
	{
		sendData = new JSONObject();
		sendData.put("loadCalendar", true);
	}
	
	private void saveSettingsLocally()
	{
		try {
			PrintWriter writer = new PrintWriter("Settings.txt");
			writer.println("autoConnect: " + autoConnect);
			writer.println("autoRequest: " + autoRequest);
			writer.println("autoForget: " + autoForget);
			writer.println("autoSaveOnExit: " + autoSaveOnExit);
			writer.println("autoSaveOnTabChange: " + autoSaveOnTabChange);
			writer.println("autoLogInOnSave: " + autoLogInOnSave);
			if(writer.checkError())
				ReportAnError("Error in saving local information");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void readSettingsLocally()
	{
		try {
			File file = new File("C:\\Users\\Hanan\\normal_eclipse-workspace\\ClientSide\\Settings.txt");
			if(file.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			boolean[] settingsArray = {autoConnect,autoRequest,autoForget,autoSaveOnExit,autoSaveOnTabChange,autoLogInOnSave};
			String setting;
			int i = 0;
			while(( setting = bufferedReader.readLine()) != null)
			{
				if(setting.contains("true"))
					settingsArray[i] = true;
				else settingsArray[i] = false;
				i++;
			}
			autoConnect = settingsArray[0];
			autoRequest = settingsArray[1];
			autoForget = settingsArray[2];
			autoSaveOnExit = settingsArray[3];
			autoSaveOnTabChange = settingsArray[4];
			autoLogInOnSave = settingsArray[5];
			bufferedReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
