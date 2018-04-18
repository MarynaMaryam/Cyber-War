package Models;


import database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by root on 05.04.2018.
 */
public class Items
{
	public static HashMap<Integer, Item> GetItems;

	public static class Item
	{
		public int Team    = 0;
		public int Slot    = 0;
		public int SubSlot = 0;
	}


	public static void UpdateItems()
	{
		Items.GetItems = new HashMap<Integer,Items.Item>();

		ResultSet rs = Database.SendSelectQuery("SELECT * FROM items");

		try
		{
			int i = 0;

			while(rs.next())
			{
				Models.Items.Item item = new Items.Item();

				item.Team = rs.getInt(2);
				item.Slot = rs.getInt(3);
				item.SubSlot = rs.getInt(4);

				Items.GetItems.put(rs.getInt(1), item);
				i++;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
