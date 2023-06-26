import database.Database;
import lowentry.ue4.classes.sockets.LatentResponse;
import lowentry.ue4.classes.sockets.SocketClient;
import lowentry.ue4.classes.sockets.SocketServer;
import lowentry.ue4.classes.sockets.SocketServerListener;
import lowentry.ue4.library.LowEntry;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Settings;
import protocols.AddServer;
import protocols.GetServers;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LaunchMaster
{
	public static Settings.Config config;

	public static class ClientSession
	{
		public boolean isRegistration = false;
		public int ServerId = 0;
	}

	public static void main(final String[] args) throws Throwable
	{
		config = Settings.GetConfig("Server");
		System.out.println("Configuration loaded");
		Database.Connection(Settings.GetConfig("Database"));

		Database.SendUpdateQuery("DELETE FROM servers");

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		SocketServerListener listener = new SocketServerListener()
		{

			@Override public void clientConnected(final SocketServer server, final SocketClient client)
			{
				System.out.println(client + " connected");
			}

			@Override public void clientDisconnected(final SocketServer server, final SocketClient client)
			{
				if(client.getAttachment() != null)
				{
					LaunchMaster.ClientSession Session = client.getAttachment();
					if(Session.isRegistration)
					{
						try
						{
							PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM servers WHERE id=?");
							ps.setInt(1, Session.ServerId);
							ps.executeUpdate();
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
					}
					System.out.println(client + " server " + Session.ServerId + " deleted");
				}
			}


			@Override public void receivedConnectionValidation(SocketServer server, SocketClient client)
			{

			}


			@Override public boolean startReceivingUnreliableMessage(SocketServer server, SocketClient client, int bytes)
			{
				// don't allow UDP packets
				return false;
			}

			@Override public void receivedUnreliableMessage(SocketServer server, SocketClient client, ByteBuffer bytes)
			{
			}


			@Override public boolean startReceivingMessage(final SocketServer server, final SocketClient client, final int bytes)
			{
				return (bytes <= (10 * 1024)); // this will only allow packets of 10KB and less
			}

			@Override public void receivedMessage(final SocketServer server, final SocketClient client, final byte[] bytes)
			{

			}


			@Override public boolean startReceivingFunctionCall(final SocketServer server, final SocketClient client, final int bytes)
			{
				return (bytes <= (10 * 1024)); // this will only allow packets of 10KB and less
			}

			@Override public byte[] receivedFunctionCall(final SocketServer server, final SocketClient client, final byte[] bytes)
			{
				return null;
			}


			@Override public boolean startReceivingLatentFunctionCall(SocketServer server, SocketClient client, int bytes)
			{
				return (bytes <= (10 * 1024)); // this will only allow packets of 10KB and less
			}

			@Override public void receivedLatentFunctionCall(SocketServer server, SocketClient client, byte[] bytes, final LatentResponse response)
			{
				executor.execute(new Runnable()
				{
					@Override
					public void run()
					{
						String jsonString = LowEntry.bytesToStringUtf8(bytes);
						System.out.println(client + ": Receive Package: " + jsonString);
						JsonNode root = LowEntry.parseJsonString(jsonString);

						if(root == null)
						{
							System.out.println("parsing failed");
						}
						else
						{
							JsonNode actionNode = root.get("action");

							if(actionNode != null)
							{
								String action = actionNode.textValue();
								JsonNode dataNode = root.get("data");

								if(dataNode != null)
								{
									switch(action)
									{
										case "get_server_list":
											response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(GetServers.Handle(dataNode), true)));

										case "add":
											HashMap<String,Object> resultAdd = AddServer.Handle(dataNode);

											if(resultAdd.get("success").equals(true))
											{
												client.setAttachment(new LaunchMaster.ClientSession());
												ClientSession session = client.getAttachment();
												session.isRegistration = true;
												session.ServerId = (int) resultAdd.get("id");
												client.setAttachment(session);
											}

											response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(resultAdd, true)));
									}
								}
								else
									System.out.println("data failed");
							}
							else
								System.out.println("action failed");
						}
					}
				});
			}
		};

		SocketServer server = new SocketServer(false, config.MasterPort, listener); // only the TCP port has been given, so the server won't listen to UDP
		System.out.println("Listening: " + server);

		while(true)
		{
			server.listen();
		}
	}
}
