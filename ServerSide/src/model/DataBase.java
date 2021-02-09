package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;;
public class DataBase{

	private PropertyChangeSupport pcs;

	private static final String JDBC_URL = "jdbc:derby:SAPPDB;create=true";
	private Connection connection;
	private Statement statement;
	private ResultSet qwerty;
	private ArrayList<String>Columns;
	private ArrayList<String>Rows;
	private final String UserTable =  "Users";
	private final String DataTable = "DataTable";
	private final String TableOfTables = "TableOfTables";
	public DataBase()
	{
		pcs = new PropertyChangeSupport(this);
		connection = null;
		statement = null;
		try {
			//connecting to database
			connection = DriverManager.getConnection(JDBC_URL);
			System.out.println("Database connected");
			statement = connection.createStatement();
			//delete table is for testing purposes only, should be deleted once go live
			DeleteTable(UserTable);
			//creating default table
			if(!isExist(UserTable)) {
				statement.execute("create table Users ( NAME VARCHAR (40), PASSWORD VARCHAR (255), EMAIL VARCHAR (255), TABLES VARCHAR (255))");
				statement.execute("insert into Users(NAME,PASSWORD,EMAIL,TABLES) values ('ExampleName','ExamplePassword','ExampleEmail','ExampleTable')");
				qwerty = statement.executeQuery("SELECT * FROM Users");
			}
		}catch(SQLException e)
		{
			System.out.println("Database failed to connect - DataBase Constructor");
			e.printStackTrace();
		}
		//creates the first instance of the database
	}

	//checks if the table exist
	public boolean isExist(String TableName)
	{
		DatabaseMetaData metaData;
		try {
			metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getTables(null, null, TableName.toUpperCase(), null);
			while(resultSet.next())
			{
				System.out.println(resultSet.getString(3));
				if(resultSet.getString(3).equals(TableName.toUpperCase()))
					return true;
			}
			return false;
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("error in isExist database");
			return false;
		}
	}

	public void DeleteTable(String TableName)
	{
		try {
			if(isExist(TableName)) {
				statement.execute("DROP TABLE " + TableName);
			}
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Error in dropping table");
		}
	}

	public void ShutDown()
	{
		try {
			if(statement!=null) 
				statement.close();
			if(connection!=null) {
				DriverManager.getConnection(JDBC_URL + ";shutdown=true");
				connection.close();
			}
			else System.out.println("cant close due to lacking connection");
		}catch(SQLException e) { //an exception acquires when the database is shutdown
			if(e.getSQLState().equals("08006"))
				System.out.println("DataBase was shutdown successfuly");
			else
				System.out.println("Derby wasn't shut down");
		}
	}
	
	public ArrayList<String> getRows(String tableName)
	{
		if(Rows == null || Rows.isEmpty())
			getAllData(tableName);
		return Rows;
	}
	
	public ArrayList<String>getColumnNames(String tableName)
	{
		if(Columns == null || Columns.isEmpty())
			getAllData(tableName);
		return Columns;
	}
	private void getAllData(String tableName)
	{
		Columns = new ArrayList<>();
		Rows = new ArrayList<>();
		try {
			qwerty = statement.executeQuery("SELECT * FROM " + tableName);
			ResultSetMetaData metadata = qwerty.getMetaData();
			int i = metadata.getColumnCount();
			for(int j = 1;j<=i;j++)
			{
				Columns.add(metadata.getColumnName(j));
			}
			while(qwerty.next())
			{
				for(int j = 1;j<=i;j++)
				{
					Rows.add(qwerty.getString(j));
				}
			}
			System.out.println("this is rows "+Rows);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Could'nt get data");
		}
	}
	/*
	 * findTable finds all the tables associated with the email provided by the user when they signIn
	 * the tables are a combination of the user email and the table name 
	 * so multiple users can have the same name for the table but each of them must have a unique email address
	 */
	public ArrayList<String> findTable(String userEmail)
	{
		ArrayList<String>tables = new ArrayList<String>();
		try {
			qwerty = statement.executeQuery("SELECT TABLES FROM " + UserTable);
			while(qwerty.next())
			{
				if(qwerty.getString(1).contains((userEmail)))
				{
					tables.add(qwerty.getString(1));
				}
			}
			if(tables.isEmpty())
				{
					System.out.println("no data for user: " + userEmail);
					return null;
				}
			else 
				return tables;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * UpdateTable receives the data and the columns names from the user in the clientSide of the program
	 * it updates the database, for the table name given in the declaration
	 * if no such table exist, meaning its a new table or the user saved for the first time the function will create
	 * a new table under the name with the user email address as a unique identifier for that table so multiple users
	 * will be able to have the same name for the table. example: tableName = 'table1', email = example@example.com so the 
	 * table will be saved under the name of: example@example.com_table1;  
	 */
	public void UpdateTable(ArrayList<String>Rows,ArrayList<String>ColumnsNames,String tableName,String email)
	{
		//for testing purposes only
		//DeleteTable(DataTable);
		String []cols = new String[ColumnsNames.size()];
		for(int i = 0;i<ColumnsNames.size();i++)
			cols[i] =  ColumnsNames.get(i);
		try {
			boolean tableExist = false;
			ArrayList<String>tables = findTable(email);
			if(tables!=null) {
				for(int i = 0;i < tables.size();i++)
					if(tables.get(i).contains(tableName))
						tableExist = true;
			}
			if(!tableExist)
				{
					String newTableName =parseEmail(email) + "_" + tableName;
					DeleteTable(newTableName);
					statement.execute("create table " + newTableName + "( " + cols[0] + " VARCHAR(255))");
					addTableToUser(newTableName, email);
					qwerty = statement.executeQuery("SELECT * FROM " + newTableName);
					ResultSetMetaData metaData = qwerty.getMetaData();
					int columnCount = metaData.getColumnCount();
					for(int i = 0;i<columnCount;i++)
						if(i>columnCount)
							statement.execute("ALTER TABLE " + newTableName + " ADD COLUMN " + cols[i] + " VARCHAR(255)");
					int i = 0,rowSize = Rows.size(),columnSize = ColumnsNames.size();
					for(int j = 0;j<rowSize;j++) {
						if(i == columnSize)
							i = 0;
						if(i == 0)
							statement.execute("insert into " + newTableName +" ("+cols[i]+")"+ " values('" + Rows.get(j) + "')");
						else
							statement.execute("update " + DataTable + " set " + cols[i] + " = '" + Rows.get(j) + "' where " + cols[i] + " is " + null);
						i++;
					}
					getAllData(newTableName);
				}
			/*if(!isExist(DataTable))//will create a table if it doesn't already exist
				{
					statement.execute("create table " + DataTable + "( " + cols[0] + " VARCHAR(255))");
				}*/
			
			
			/*
			qwerty = statement.executeQuery("SELECT * FROM " + DataTable);
			ResultSetMetaData metadata = qwerty.getMetaData();
			int w = metadata.getColumnCount();
			for(int i = 1;i<cols.length;i++) {
				String col = cols[i];
				if(i>=w) {
					statement.execute("ALTER TABLE " + DataTable + " ADD COLUMN " + col + " VARCHAR(255)");
				}
			}*/
			/*int colSize = cols.length,rowSize = Rows.size();
			if(colSize == 0)
				System.out.println("Table is empty");
			else
			{
				int i = 0;
				for(int j = 0;j<rowSize;j++)
				{
					if(i == ColumnsNames.size())
						i = 0;
					if(i == 0)
						statement.execute("insert into " + DataTable +" ("+cols[i]+")"+ " values('" + Rows.get(j) + "')");//creates a line in the table with one value of data and the rest is null
					else
					{
						statement.execute("update " + DataTable + " set " + cols[i] + " = '" + Rows.get(j) + "' where " + cols[i] + " is " + null);//fills the null values with the data values - a data value can also be null -- meaning an empty location in the table
					}
					i++;
				}
			}
			*/
		}
		catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Update Table fail - DataBase");
		}
		//getAllData(DataTable);
	}
	
	//checks whether the user exists and returns a boolean that indicates it
	public boolean checkSignIn(String name,String password,String eMail)
	{
		System.out.println("checkSignIn");
		getAllData(UserTable);
		Iterator<String>rowIterator = Rows.iterator();
		while(rowIterator.hasNext())
		{
			String nameInRows = rowIterator.next();
			String passwordInRows = rowIterator.next();
			String emailInRows = rowIterator.next();
			rowIterator.next();
			if(name.equals(nameInRows) && password.equals(passwordInRows) && eMail.equals(emailInRows))
				return true;
		}
		return false;
	}
	public boolean signUpUser(String name,String password,String email)
	{
		//signUp a user by adding it to the database
		//should update error handling if failed to add to database
		System.out.println("SignUpUser");
		try {
			String table1 = "table1";
			statement.execute("insert into " + UserTable+" values('" + name + "','"+password+"','" + email + "','"+table1 +"')");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("couldnt create new user");
		}
		return true;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
	public String getCurrentUserName()
	{
		return "Hanan";
	}

	private String parseEmail(String email)
	{
		StringBuilder builder = new StringBuilder();
		for(int i = 0;i<email.length();i++)
		{
			if(email.charAt(i) == '@')
				builder.append("_at_");
			else
				builder.append(email.charAt(i));
			
		}
		System.out.println("parseEmail: " + builder.toString());
		return builder.toString();
	}
	private void addTableToUser(String tableName,String userEmail)
	{
		try {
			ResultSet qwerty = statement.executeQuery("SELECT EMAIL, TABLES FROM " + UserTable);
			while(qwerty.next())
			{
				System.out.println(qwerty.getString(1) +","+ qwerty.getString(2));
				String email = qwerty.getString(1);
				if(email.equals(userEmail))
				{
					StringBuilder builder = new StringBuilder();
					builder.append(qwerty.getString(2));
					builder.append(",");
					builder.append(tableName);
					System.out.println("update " + UserTable + " set TABLES = " + builder.toString() + " where EMAIL = " + userEmail);
					statement.execute("update " + UserTable + " set TABLES = " + builder.toString() + " where EMAIL = " + userEmail);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
