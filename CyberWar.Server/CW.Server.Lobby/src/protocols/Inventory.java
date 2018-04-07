package protocols;


import Models.Items;
import Models.Session;
import database.Database;
import sun.plugin2.gluegen.runtime.StructAccessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by root on 05.04.2018.
 */
public class Inventory
{
	public static HashMap<String, Object> Get(Session session)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("success", true);
		data.put("ecode", 0);
		Object Items[] = new Object[0];
		int count;

		ResultSet rs = Database.SendSelectQuery("SELECT count(*) FROM inventory WHERE p_id=" + session.PlayerId);

		try
		{
			if(rs.first())
			{
				count = rs.getInt(1);
				Items = new Object[count];
			}

			rs = Database.SendSelectQuery("SELECT * FROM inventory WHERE p_id=" + session.PlayerId);

			int i = 0;

			while(rs.next())
			{
				HashMap<String, Object> Item = new HashMap<>();
				Item.put("id", rs.getInt(1));
				Items[i] = Item;
				i++;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			data.replace("success", false);
			data.replace("ecode", 1);
		}

		data.put("items", Items);
		return data;
	}

	public static void Add(int PlayerId, int ItemId)
	{
		Database.SendUpdateQuery("INSERT INTO inventory (p_id, item_id) VALUES (" + PlayerId + ", " + ItemId + ")");
	}
}
