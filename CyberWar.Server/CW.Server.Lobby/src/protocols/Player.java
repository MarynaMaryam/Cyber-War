package protocols;


import database.Database;
import lowentry.ue4.libs.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by root on 02.04.2018.
 */
public class Player
{
	public static HashMap<String, Object> Create(Models.Session session, JsonNode dataNode)
	{
		HashMap<String,Object> data = new HashMap<String,Object>();

		int Points = 1000000;

		data.put("success", false);
		data.put("ecode", 0);
		JsonNode dataNameNode = dataNode.get("name");

		if(dataNameNode != null)
		{
			String dataName = dataNameNode.textValue();

			if(dataName.length() > 3 && dataName.length() < 15)
			{
				ResultSet rs = Database.SendSelectQuery("SELECT count(*) FROM players WHERE name='" + dataName + "'");
				try
				{
					if(rs.next())
					{
						if(rs.getInt(1) == 0)
						{
							Database.SendUpdateQuery("INSERT INTO players (a_id, name, points) VALUES (" + session.account.ID + ", '" + dataName + "', " + Points + ")");
							data.replace("success", true);
						}
						else
							data.replace("ecode", 7);
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
			else
				data.replace("ecode", 6);
		}

		return data;
	}

	public static HashMap<String, Object> Get(Models.Session session)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();

		data.put("success", false);
		data.put("ecode", 0);
		ResultSet rs = Database.SendSelectQuery("SELECT * FROM players WHERE a_id=" + session.account.ID);

		try
		{
			if(rs.first())
			{
				data.put("id", rs.getInt(1));
				data.put("name", rs.getString(3));
				data.put("points", rs.getInt(4));
				data.put("red_character", rs.getInt(5));
				data.put("blue_character", rs.getInt(6));
				data.put("weapon_primary", rs.getInt(7));
				data.put("weapon_secondary", rs.getInt(8));
				data.put("weapon_melee", rs.getInt(9));
				data.put("weapon_throw", rs.getInt(10));
				data.put("weapon_special", rs.getInt(11));
				data.put("wins", rs.getInt(12));
				data.put("lose", rs.getInt(13));
				data.put("kills", rs.getInt(14));
				data.put("assists", rs.getInt(15));
				data.put("deaths", rs.getInt(16));
				data.put("exp", rs.getInt(17));
				data.replace("success", true);
			}
			else
				data.replace("ecode", 2);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return data;
	}
}
