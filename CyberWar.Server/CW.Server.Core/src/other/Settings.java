package other;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;



public class Settings
{
	public static class Config
	{
		public int LoginPort;
		public int MasterPort;
		public String URL;
		public String User;
		public String Password;
	}

	public static Config GetConfig(String Section) throws IOException
	{
		Ini ini = new Ini(new File(System.getProperty("user.dir") + "\\Configs\\Server.ini"));
		Config config = new Config();

		switch(Section)
		{
			case "Server":
				config.LoginPort = ini.get(Section, "LoginPort", int.class);
				config.MasterPort = ini.get(Section, "MasterPort", int.class);
				break;

			case "Database":
				config.URL = ini.get(Section, "URL", String.class);
				config.User = ini.get(Section, "User", String.class);
				config.Password = ini.get(Section, "Password", String.class);
				break;
		}
		return config;
	}
}
