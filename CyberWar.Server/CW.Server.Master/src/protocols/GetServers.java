package protocols;


import database.Database;
import lowentry.ue4.library.LowEntry;
import lowentry.ue4.libs.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by root on 31.03.2018.
 */
public class GetServers
{
	public static HashMap<String,Object> Handle(JsonNode InputData)
	{
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("success", true);
		data.put("ecode", 0);
		Object[] arrayServers = new Object[0];
		int dataType = 0;
		int CountServers = -1;

		JsonNode dataTypeNode = InputData.get("type");

		if(dataTypeNode != null)
			dataType = dataTypeNode.intValue();

		ResultSet rs = Database.SendSelectQuery("SELECT count(*) FROM servers WHERE type=" + dataType);


		try
		{
			if(rs.next())
			{
				CountServers = rs.getInt(1);
				arrayServers = new Object[CountServers];
			}

			if(dataType > 0)
				rs = Database.SendSelectQuery("SELECT * FROM servers WHERE type=" + dataType);

			if(rs.next())
			{
				int i = 1;

				HashMap<String,Object> metadata;

				if(rs.first())
				{
					metadata = new HashMap<String,Object>();

					metadata.put("id", rs.getInt("id"));
					metadata.put("name", rs.getString("name"));
					metadata.put("type", rs.getInt("type"));
					metadata.put("ip", rs.getString("ip"));
					metadata.put("port", rs.getInt("port"));
					metadata.put("max_players", rs.getInt("max_players"));
					metadata.put("current_players", rs.getInt("current_players"));
					metadata.put("mode", rs.getString("game_mode"));

					arrayServers[0] = metadata;

					while(rs.next())
					{
						metadata = new HashMap<String,Object>();

						metadata.put("id", rs.getInt("id"));
						metadata.put("name", rs.getString("name"));
						metadata.put("type", rs.getInt("type"));
						metadata.put("ip", rs.getString("ip"));
						metadata.put("port", rs.getInt("port"));
						metadata.put("max_players", rs.getInt("max_players"));
						metadata.put("current_players", rs.getInt("current_players"));
						metadata.put("mode", rs.getString("game_mode"));

						arrayServers[i] = metadata;

						i++;
					}
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			data.replace("success", false);
		}

		data.put("count", CountServers);
		data.put("server_list", arrayServers);
		return data;
	}
}
