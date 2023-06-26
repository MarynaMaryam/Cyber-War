package database;

import org.ini4j.Ini;
import other.Settings;

import java.sql.*;
import java.util.Set;



public class Database
{
	// JDBC variables for opening and managing connection
	private static Connection con;
	private static Statement  stmt;
	private static ResultSet  rs;

	public static void Connection(Settings.Config config) throws  SQLException
	{
		con = DriverManager.getConnection(config.URL, config.User, config.Password);
		stmt = con.createStatement();
		System.out.println("Connection to database");
	}

	public static ResultSet SendSelectQuery(String Query)
	{
		try
		{
			// executing SELECT query
			rs = stmt.executeQuery(Query);
			return rs;
		}
		catch (SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
		return null;
	}

	public static int SendUpdateQuery(String Query)
	{
		try
		{
			// getting Statement object to execute query
			stmt = con.createStatement();

			return stmt.executeUpdate(Query);

		}
		catch (SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
		return 0;
	}

	public static void Closed()
	{
		try
		{
			rs.close();
		}
		catch(SQLException se)
		{ /*can't do anything */ }
	}
	public static Connection getConnection() {
		return con;
	}

}
