
import database.Database;
import lowentry.ue4.classes.sockets.LatentResponse;
import lowentry.ue4.classes.sockets.SocketClient;
import lowentry.ue4.classes.sockets.SocketServer;
import lowentry.ue4.classes.sockets.SocketServerListener;
import lowentry.ue4.library.LowEntry;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Settings;
import protocols.Authorization;
import protocols.Registration;
import java.nio.ByteBuffer;
import java.util.HashMap;



public class LaunchLogin
{
	public static Settings.Config config;


	public static void main(final String[] args) throws Throwable
	{
		config = Settings.GetConfig("Server");
		System.out.println("Configuration loaded");
		Database.Connection(Settings.GetConfig("Database"));

		SocketServerListener listener = new SocketServerListener()
		{

			@Override public void clientConnected(final SocketServer server, final SocketClient client)
			{
				// initialize the attachment object
				client.setAttachment(new Models.Session());
				System.out.println(client + " connected");
			}

			@Override public void clientDisconnected(final SocketServer server, final SocketClient client)
			{
				// initialize the attachment object
				Models.Session Session = client.getAttachment();
				System.out.println(client + " disconnected");
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
				// only allow 200B and less when the client hasn't authenticated yet

				Models.Session session = client.getAttachment();
				if(!session.IsAuth)
				{
					return (bytes <= 200); // this will only allow packets of 200B and less
				}

				return (bytes <= (10 * 1024)); // this will only allow packets of 10KB and less
			}

			@Override public byte[] receivedFunctionCall(final SocketServer server, final SocketClient client, final byte[] bytes)
			{
				return null;
			}


			@Override public boolean startReceivingLatentFunctionCall(SocketServer server, SocketClient client, int bytes)
			{
				// only allow 200B and less when the client hasn't authenticated yet

				Models.Session session = client.getAttachment();
				if(!session.IsAuth)
				{
					return (bytes <= 200); // this will only allow packets of 200B and less
				}

				return (bytes <= (10 * 1024)); // this will only allow packets of 10KB and less
			}

			@Override public void receivedLatentFunctionCall(SocketServer server, SocketClient client, byte[] bytes, final LatentResponse response)
			{
				Models.Session session = client.getAttachment();
				HashMap<String,Object> data = new HashMap<String,Object>();

				String jsonString = LowEntry.bytesToStringUtf8(bytes);
				System.out.println("Client - " + client + ", Receive Package: " + jsonString);
				JsonNode root = LowEntry.parseJsonString(jsonString);

				if(root == null)
					System.out.println("parsing failed");

				else
				{
					JsonNode actionNode = root.get("action");

					if(actionNode != null)
					{
						String action = actionNode.textValue();
						JsonNode dataNode = root.get("data");

						if(dataNode != null)
						{
							data.put("success", false);
							data.put("ecode", 0);

							if(session.IsAuth)
							{
								switch(action)
								{
									case "Authorization":
										HashMap<String,Object> resultAuth = Authorization.Handle(dataNode);
										HashMap<String,Object> masterServer = new HashMap<String, Object>();

										if(resultAuth.get("success").equals(true))
										{
											masterServer.put("ip", "127.0.0.1");
											masterServer.put("port", config.MasterPort);
											resultAuth.put("master", masterServer);
										}

										response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(resultAuth, true)));
										break;

									case "Registration":
										response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(Registration.Handle(dataNode), true)));
										break;
								}
							}
							else if(action.equals("Authentication"))
							{
								String Version = "1.0.0.0";
								JsonNode dataVersionNode = dataNode.get("version");

								if(dataVersionNode != null)
								{
									if(dataVersionNode.textValue().equals(Version))
									{
										Models.Session Session = client.getAttachment();
										Session.IsAuth = true;
										client.setAttachment(Session);
										data.replace("success", true);
									}
									else
										data.replace("success", false);

									response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(data, true)));

									if(data.get("success").equals(false))
										client.disconnect();

								}
							}
							else
								System.out.println("Action is not defined");
						}
						else
							System.out.println("data failed");
					}
					else
						System.out.println("action failed");
				}
			}
		};

		SocketServer server = new SocketServer(false, config.LoginPort, listener); // only the TCP port has been given, so the server won't listen to UDP
		System.out.println("Listening: " + server);

		while(true)
		{
			server.listen();
		}
	}
}
