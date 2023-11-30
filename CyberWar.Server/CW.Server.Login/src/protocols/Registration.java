package protocols;


import database.Database;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Encode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class Registration
{
	public static HashMap<String, Object> Handle(JsonNode dataNode)
	{
		HashMap<String,Object> data = new HashMap<String,Object>();

		data.put("success", false);
		data.put("ecode", 0);

		int Points = 1000000;
		int DefaultsItems[] = new int[5];
		DefaultsItems[0] = 1000;
		DefaultsItems[1] = 2000;
		DefaultsItems[2] = 10001;
		DefaultsItems[3] = 20001;
		DefaultsItems[4] = 30001;
		int AccountId = 0;

		JsonNode dataLoginNode = dataNode.get("login");
		JsonNode dataPasswordNode = dataNode.get("password");
		JsonNode dataNameNode = dataNode.get("name");

		if(dataLoginNode != null && dataPasswordNode != null)
		{
			String dataLogin = dataLoginNode.textValue();
			String dataPassword = dataPasswordNode.textValue();
			String dataName = dataNameNode.textValue();

			if(dataLogin.length() >= 4 && dataPassword.length() >= 4 && dataName.length() >= 4)
			{
				try
				{
					if(!Database.SendSelectQuery("SELECT login FROM accounts WHERE login='" + dataLogin + "'").next())
					{
						String HashPassword = Encode.HashString(dataPassword);
						Database.SendUpdateQuery("INSERT INTO accounts (login, password) VALUES ('" + dataLogin + "', '" + HashPassword + "')");
					}
					else
						data.replace("ecode", 4);

					ResultSet rs = Database.SendSelectQuery("SELECT id FROM accounts WHERE login='" + dataLogin + "'");

					if(rs.next())
						AccountId = rs.getInt(1);

					if(dataName.length() > 3 && dataName.length() < 15)
					{
						rs = Database.SendSelectQuery("SELECT count(*) FROM players WHERE name='" + dataName + "'");

						try
						{
							if(rs.next())
							{
								if(rs.getInt(1) == 0)
								{
									Database.SendUpdateQuery("INSERT INTO players (a_id, name, points, red_character, blue_character, weapon_primary, weapon_secondary, weapon_melee) VALUES (" + AccountId + ", '" + dataName + "', " + Points + ", " + DefaultsItems[0] + ", " + DefaultsItems[1] + ", " + DefaultsItems[2] + ", " + DefaultsItems[3] + ", " + DefaultsItems[4] + ")");
									System.out.println("Add User: Login - " + dataLogin + ", Password - " + dataPassword + ", Name - " + dataName);
								}
								else
									data.replace("ecode", 7);
							}

							rs = Database.SendSelectQuery("SELECT id FROM players WHERE name='" + dataName + "'");

							if(rs.next())
							{
								int i = 0;

								while(i < DefaultsItems.length)
								{
									Database.SendUpdateQuery("INSERT INTO inventory (p_id, item_id) VALUES (" + rs.getInt(1) + ", " + DefaultsItems[i] + ")");

									i++;
								}

								data.replace("success", true);
							}
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
					data.replace("ecode", 1);
				}
			}
			else
				data.replace("ecode", 5);
		}
		else
			data.replace("ecode", 1);

		Database.Closed();
		return data;
	}
}
