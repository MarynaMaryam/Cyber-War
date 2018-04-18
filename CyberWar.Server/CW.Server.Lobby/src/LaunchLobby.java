import Models.Items;
import database.Database;
import lowentry.ue4.classes.sockets.*;
import lowentry.ue4.library.LowEntry;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Encode;
import other.Settings;
import protocols.Inventory;
import protocols.Player;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by root on 01.04.2018.
 */
public class LaunchLobby
{
	public static Settings.Config config;
	public static int Port = Encode.RandomInt(7784, 9999);

	public static void main(final String[] args) throws Throwable
	{
		SocketServer server = null;

		config = Settings.GetConfig("Server");
		System.out.println("Configuration loaded");
		Database.Connection(Settings.GetConfig("Database"));
		Items.UpdateItems();

		// Create socket for master server

		SocketConnectionListener master = new SocketConnectionListener()
		{
			@Override public void connected(final SocketConnection connection)
			{
				// this function is called when the connection opens
				System.out.println("[" + Thread.currentThread().getName() + "] Connected: " + connection);

				HashMap<String,Object> root = new HashMap<String,Object>();
				HashMap<String,Object> data = new HashMap<String,Object>();

				root.put("action", "add");
				data.put("type", 1);
				data.put("ip", "127.0.0.1");
				data.put("port", Port);
				data.put("max_players", 100);
				root.put("data", data);

				connection.sendLatentFunctionCall(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(root)), new SocketConnection.LatentFunctionCallListener()
				{
					@Override
					public void receivedResponse(final SocketConnection connection, final byte[] bytes)
					{
						String jsonString = LowEntry.bytesToStringUtf8(bytes);
						System.out.println("Server - " + connection + ", Receive Package: " + jsonString);
						JsonNode root = LowEntry.parseJsonString(jsonString);
					}

					@Override
					public void canceled(final SocketConnection connection)
					{
						// this function is called when your function call has been canceled
						System.out.println("[" + Thread.currentThread().getName() + "] Latent Function Call canceled");
					}

					@Override
					public void failed(final SocketConnection connection)
					{
						// this function is called when your function call fails, by for example timing out or by the connection closing
						System.out.println("[" + Thread.currentThread().getName() + "] Latent Function Call failed");
					}
				});
			}

			@Override public void disconnected(final SocketConnection connection)
			{
				// this function is called after the connection closes
				System.out.println("[" + Thread.currentThread().getName() + "] Disconnected: " + connection);
			}

			@Override public void receivedUnreliableMessage(final SocketConnection connection, final byte[] bytes)
			{
				// this function is called when you have received a message packet
				System.out.println("[" + Thread.currentThread().getName() + "] Received Message: \"" + LowEntry.bytesToStringUtf8(bytes) + "\"");
			}

			@Override public void receivedMessage(final SocketConnection connection, final byte[] bytes)
			{
				// this function is called when you have received a message packet
				System.out.println("[" + Thread.currentThread().getName() + "] Received Message: \"" + LowEntry.bytesToStringUtf8(bytes) + "\"");
			}
		};

		SocketConnection  connection = new SocketConnection("127.0.0.1", config.MasterPort, master);

		// Create socket for clients

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

				if(Session.IsAuth)
				{
					Database.SendUpdateQuery("UPDATE accounts SET status=0 WHERE session_key='" + Session.SessionKey + "'");
				}

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
							if(session.IsAuth)
								switch(action)
								{
									case "get_player":
										response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(Player.Get(session), true)));

									case "get_inventory":
										response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(Inventory.Get(session), true)));

									case "selected_item_inventory":
										response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(Inventory.Selected(session, dataNode), true)));

									case "get_server_list":

										if(connection != null)
										{
											connection.sendLatentFunctionCall(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(root)), new SocketConnection.LatentFunctionCallListener()
											{
												@Override public void receivedResponse(final SocketConnection connection, final byte[] bytes)
												{
													response.done(bytes);
												}

												@Override public void canceled(final SocketConnection connection)
												{

												}

												@Override public void failed(final SocketConnection connection)
												{

												}
											});
										}
										else
										{
											System.out.println("Failed Connection Master Server");
											response.done(LowEntry.stringToBytesUtf8("{\"success\": false, \"ecode\": 9}"));
										}

								}
							else if(action.equals("auth"))
							{
								data.put("success", false);
								data.put("ecode", 0);
								JsonNode dataSessionKeyNode = dataNode.get("session_key");

								if(dataSessionKeyNode != null)
								{
									String SessionKey = dataSessionKeyNode.textValue();

									try
									{
										ResultSet rs = Database.SendSelectQuery("SELECT * FROM accounts WHERE session_key='" + SessionKey + "'");

										if(rs.next())
										{
											session.IsAuth = true;
											session.SessionKey = SessionKey;
											session.AccountID = rs.getInt("id");
										}

										rs = Database.SendSelectQuery("SELECT id FROM players WHERE a_id=" + session.AccountID);

										if(rs.next())
										{
											session.PlayerId = rs.getInt(1);
											client.setAttachment(session);
											data.replace("success", true);
										}
										else
											data.replace("ecode", 2);
									}
									catch(SQLException e)
									{
										e.printStackTrace();
										data.replace("ecode", 1);
									}

									if(data.get("success").equals(false))
										client.disconnect();

									response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(data, true)));

								}
								else
									System.out.println("session_key failed");
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

		server = new SocketServer(false, Port, listener);

		System.out.println("Listening: " + server);
		System.out.println("Connection Master Server: " + server);

		while(true)
		{
			if(connection.isConnected())
			{
				connection.listen();
				server.listen();
			}
			else
			{
				connection.connect();
			}
		}
	}
}
