package protocols;


import database.Database;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Encode;

import java.sql.SQLException;
import java.util.HashMap;


public class Registration
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

			if(dataLogin.length() >= 4 && dataPassword.length() >= 4)
			{
				try
				{
					if(Database.SendSelectQuery("SELECT login FROM accounts WHERE login='" + dataLogin + "'").next() == false)
					{
						String HashPassword = Encode.HashString(dataPassword);
						Database.SendUpdateQuery("INSERT INTO accounts (login, password) VALUES ('" + dataLogin + "', '" + HashPassword + "')");
						System.out.println("Add User: Login - " + dataLogin + ", Password - " + HashPassword);
						data.replace("success", true);
					}
					else
						data.replace("ecode", 4);
				}
				catch(SQLException e)
				{
					e.printStackTrace();
					data.replace("ecode", 0);
				}
			}
			else
				data.replace("ecode", 5);
		}
		else
			data.replace("ecode", 0);

		Database.Closed();
		return data;
	}
}
