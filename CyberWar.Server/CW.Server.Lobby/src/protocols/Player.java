package protocols;


import Models.Items;
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
	public static HashMap<String, Object> Get(Models.Session session)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();

		data.put("success", false);
		data.put("ecode", 0);
		ResultSet rs = Database.SendSelectQuery("SELECT * FROM players WHERE a_id=" + session.AccountID);

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

			rs = Database.SendSelectQuery("SELECT money FROM accounts WHERE id=" + session.AccountID);

			if(rs.first())
				data.put("money", rs.getInt("money"));
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return data;
	}
}
