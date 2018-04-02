package protocols;


import lowentry.ue4.libs.jackson.databind.JsonNode;
import database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import other.Encode;


public class Authorization
{
	public static HashMap<String, Object> Handle(JsonNode dataNode)
	{
		HashMap<String,Object> data = new HashMap<String,Object>();

		data.put("success", false);
		data.put("ecode", 0);

		JsonNode dataLoginNode = dataNode.get("login");
		JsonNode dataPasswordNode = dataNode.get("password");

		if(dataLoginNode != null && dataPasswordNode != null)
		{
			String dataLogin = dataLoginNode.textValue();
			String dataPassword = dataPasswordNode.textValue();

			try
			{
				String HashPassword = Encode.HashString(dataPassword);
				ResultSet rs = Database.SendSelectQuery("SELECT id, status FROM accounts WHERE login='" + dataLogin + "' AND password='" + HashPassword + "'");

				if(rs.next() == true)
				{
					if(rs.getInt("status") == 0)
					{
						int UserId = rs.getInt("id");
						String HashSessionKey = Encode.HashString(Encode.RandomInt(0, 9999999));

						Database.SendUpdateQuery("UPDATE accounts SET session_key='" + HashSessionKey + "' WHERE id='" + UserId + "'");

						System.out.println("Auth User: " + dataLogin);

						data.replace("success", true);
						data.put("session_key", HashSessionKey);
						data.put("user_id", UserId);
					}
					else if(rs.getInt("status") == 1)
					{
						data.replace("ecode", 2);
					}

					else if(rs.getInt("status") == 2)
					{
						data.replace("ecode", 3);
					}
				}
				else
				{
					data.replace("ecode", 1);
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				data.replace("ecode", 0);
			}
		}
		else
		{
			data.replace("ecode", 0);
		}

		Database.Closed();
		return data;
	}
}
