package protocols;


import Models.Items;
import Models.Session;
import database.Database;
import lowentry.ue4.libs.jackson.databind.JsonNode;
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
				Item.put("id", rs.getInt(2));
				Item.put("count", rs.getInt(3));
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

	public static HashMap<String, Object> Add(Session session, JsonNode InputData)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("success", false);
		data.put("ecode", 0);
		JsonNode ItemIdNode = InputData.get("item_id");
		if(ItemIdNode != null)
		{
			int ItemId = ItemIdNode.intValue();
			Database.SendUpdateQuery("INSERT INTO inventory (p_id, item_id) VALUES (" + session.PlayerId + ", " + ItemId + ")");
		}
		else
			data.replace("ecode", 1);
		return data;
	}

	public static HashMap<String, Object> Selected(Session session, JsonNode InputData)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("success", false);
		data.put("ecode", 0);

		String UpdateSet = null;

		JsonNode ItemIdNode = InputData.get("item_id");
		JsonNode SlotNode = InputData.get("slot");
		JsonNode SubSlotNode = InputData.get("sub_slot");
		JsonNode TeamNode = InputData.get("team");

		if(ItemIdNode != null && SlotNode != null && SubSlotNode != null && TeamNode != null)
		{
			int ItemId = ItemIdNode.intValue();
			int Slot = SlotNode.intValue();
			int SubSlot = SubSlotNode.intValue();
			int Team = TeamNode.intValue();

			switch(Slot)
			{
				case 0:
					switch(Team)
					{
						case 1:
							UpdateSet = "red_character";
							break;

						case 2:
							UpdateSet = "blue_character";
							break;
					}
					break;

				case 1:
					switch(SubSlot)
					{
						case 1:
							UpdateSet = "weapon_primary";
							break;
						case 2:
							UpdateSet = "weapon_secondary";
							break;
						case 3:
							UpdateSet = "weapon_melee";
							break;
						case 4:
							UpdateSet = "weapon_throw";
							break;
						case 5:
							UpdateSet = "weapon_special";
							break;

					}
					break;
			}
			if(UpdateSet != null)
			{
				ResultSet rs = Database.SendSelectQuery("SELECT count(*) FROM inventory WHERE p_id=" + session.PlayerId + " AND item_id =" + ItemId);

				try
				{
					if(rs.next())
					{
						Database.SendUpdateQuery("UPDATE players SET " + UpdateSet + "=" + ItemId + " WHERE id=" + session.PlayerId);
						data.replace("success", true);
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
					data.replace("ecode", 1);
				}
			}
			else
				data.replace("ecode", 8);
		}
		else
			data.replace("ecode", 1);

		data.put("player_state", Player.Get(session));
		return data;
	}
}
