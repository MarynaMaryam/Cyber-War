import Models.Items;
import database.Database;
import lowentry.ue4.classes.sockets.*;
import lowentry.ue4.library.LowEntry;
import lowentry.ue4.libs.jackson.databind.JsonNode;
import other.Encode;
import other.Settings;
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

	public static void main(final String[] args) throws Throwable
	{
		config = Settings.GetConfig("Server");
		System.out.println("Configuration loaded");
		Database.Connection(Settings.GetConfig("Database"));
		int Port = Encode.RandomInt(7784, 9999);
		UpdateItems();

		SocketConnectionListener master = new SocketConnectionListener()
		{
			@Override
			public void connected(final SocketConnection connection)
			{
				// this function is called when the connection opens
				System.out.println("[" + Thread.currentThread().getName() + "] Connected: " + connection);

				HashMap<String, Object> root = new HashMap<String, Object>();
				HashMap<String, Object> data = new HashMap<String, Object>();

				root.put("action", "add");
				data.put("type", 1);
				data.put("ip", "127.0.0.1");
				data.put("port", Port);
				data.put("max_players", 100);
				data.put("game_mode", null);
				root.put("data", data);

				connection.sendLatentFunctionCall(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(root)), new SocketConnection.LatentFunctionCallListener()
				{
					@Override
					public void receivedResponse(final SocketConnection connection, final byte[] bytes)
					{
						String jsonString = LowEntry.bytesToStringUtf8(bytes);
						System.out.println("Server - " + connection + ", Receive Package: " + jsonString);
						JsonNode root = LowEntry.parseJsonString(jsonString);

						if(root == null)
						{
							System.out.println("parsing failed");
						}
						else
						{
							JsonNode resultNode = root.get("success");

							if(resultNode != null)
							{
								if(resultNode.booleanValue())
								{
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
														{
															switch(action)
															{
																case "get_player":
																	response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(Player.Get(session), true)));
																	break;
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

																response.done(LowEntry.stringToBytesUtf8(LowEntry.toJsonString(data, true)));

																if(data.get("success").equals(false))
																	client.disconnect();

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

									SocketServer server = null; // only the TCP port has been given, so the server won't listen to UDP
									try
									{
										server = new SocketServer(false, Port, listener);
										System.out.println("Listening: " + server);

										while(true)
										{
											server.listen();
										}
									}
									catch(Throwable throwable)
									{
										throwable.printStackTrace();
									}
								}
							}
							else
								System.out.println("action failed");
						}
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

			@Override
			public void disconnected(final SocketConnection connection)
			{
				// this function is called after the connection closes
				System.out.println("[" + Thread.currentThread().getName() + "] Disconnected: " + connection);
			}

			@Override
			public void receivedUnreliableMessage(final SocketConnection connection, final byte[] bytes)
			{
				// this function is called when you have received a message packet
				System.out.println("[" + Thread.currentThread().getName() + "] Received Message: \"" + LowEntry.bytesToStringUtf8(bytes) + "\"");
			}

			@Override
			public void receivedMessage(final SocketConnection connection, final byte[] bytes)
			{
				// this function is called when you have received a message packet
				System.out.println("[" + Thread.currentThread().getName() + "] Received Message: \"" + LowEntry.bytesToStringUtf8(bytes) + "\"");
			}
		};

		final SocketConnection connection = new SocketConnection("127.0.0.1", config.MasterPort, master);

		if(!connection.connect())
		{
			System.out.println("Failed to connect master server");
			System.exit(1);
		}

		while(connection.isConnected())
		{
			connection.listen();
		}
	}

	private static void UpdateItems()
	{
		Items.GetItems = new HashMap<Integer,Items.Item>();

		ResultSet rs = Database.SendSelectQuery("SELECT * FROM items");

		try
		{
			int i = 0;

			while(rs.next())
			{
				Models.Items.Item item = new Items.Item();

				item.Team = rs.getInt(2);
				item.Slot = rs.getInt(3);
				item.SubSlot = rs.getInt(4);

				Items.GetItems.put(rs.getInt(1), item);
				i++;
			}

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
