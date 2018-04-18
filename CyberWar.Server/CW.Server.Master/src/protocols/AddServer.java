package protocols;


import database.Database;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Encode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by root on 31.03.2018.
 */
public class AddServer
{
	public static HashMap<String, Object> Handle(JsonNode dataNode)
	{
		HashMap<String,Object> data = new HashMap<String,Object>();

		data.put("success", false);
		data.put("ecode", 0);

		JsonNode dataTypeNode = dataNode.get("type");
		JsonNode dataIpNode = dataNode.get("ip");
		JsonNode dataPortNode = dataNode.get("port");
		JsonNode dataMaxPlayersNode = dataNode.get("max_players");
		JsonNode dataGameModeNode = dataNode.get("mode");
		JsonNode dataNameNode = dataNode.get("name");

		if(dataTypeNode != null && dataIpNode != null && dataPortNode != null && dataMaxPlayersNode != null)
		{
			int dataType = dataTypeNode.intValue();
			String dataIp = dataIpNode.textValue();
			int dataPort = dataPortNode.intValue();
			int dataMaxPlayers = dataMaxPlayersNode.intValue();
			String dataGameMode = "Lobby";
			String dataName = "None";

			if(dataGameModeNode != null && dataType == 2 && dataNameNode != null)
			{
				dataGameMode = dataGameModeNode.textValue();
				dataName = dataNameNode.textValue();
			}

			try
			{
				if(!Database.SendSelectQuery("SELECT * FROM servers WHERE ip='" + dataIp + "' AND port='" + dataPort + "'").next())
				{
					Database.SendUpdateQuery("INSERT INTO servers (name, type, ip, port, max_players, game_mode) VALUES ('" + dataName + "', '" + dataType + "', '" + dataIp + "', '" + dataPort + "', '" + dataMaxPlayers + "', '" + dataGameMode + "')");
					System.out.println("Add Server: Type: " + dataType + ", Address: " + dataIp + ":" + dataPort);
					ResultSet rs = Database.SendSelectQuery("SELECT id FROM servers WHERE ip='" + dataIp + "' AND port='" + dataPort + "'");
					if(rs.next())
					{
						data.put("id", rs.getInt("id"));
						data.replace("success", true);
					}
				}
				else
					data.replace("ecode", 2);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}

		Database.Closed();
		return data;
	}
}
