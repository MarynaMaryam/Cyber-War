package other;


import lowentry.ue4.classes.sockets.SocketClient;
import lowentry.ue4.library.LowEntry;

import java.util.HashMap;


/**
 * Created by root on 30.03.2018.
 */
public class Networking
{
	public static void SendPackage(SocketClient client, String Action, HashMap<String,Object> Data)
	{
		HashMap<String,Object> response = new HashMap<String,Object>();
		response.put("action", Action);
		response.put("data", Data);
		client.sendMessage(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(response, true)));

		System.out.println("Client - " + client + ", Send:" + LowEntry.toJsonString(response, true));
	}
}
