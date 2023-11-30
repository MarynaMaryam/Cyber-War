package protocols;

import Models.Items;
import Models.Session;
import database.Database;
import lowentry.ue4.libs.jackson.databind.JsonNode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 05.04.2018.
 */
public class Inventory {
	public static HashMap<String, Object> getInventory(Session session) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("success", true);
		response.put("ecode", 0);
		List<HashMap<String, Object>> items = new ArrayList<>();

		String query = "SELECT count(*) FROM inventory WHERE p_id=?";
		try (PreparedStatement statement = Database.getConnection().prepareStatement(query)) {
			System.out.println("PlayerId: " + session.PlayerId);
			statement.setInt(1, session.PlayerId);
			try (ResultSet rs = statement.executeQuery()) {
				if (rs.first()) {
					int count = rs.getInt(1);
					System.out.println("Count: " + count);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.replace("success", false);
			response.replace("ecode", 1);
		}

		query = "SELECT * FROM inventory WHERE p_id=?";
		try (PreparedStatement statement = Database.getConnection().prepareStatement(query)) {
			statement.setInt(1, session.PlayerId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					HashMap<String, Object> item = new HashMap<>();
					item.put("id", rs.getInt(2)); // Исправленный индекс столбца
					item.put("count", rs.getInt(1)); // Исправленный индекс столбца
					items.add(item);
					System.out.println("Item: " + item);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.replace("success", false);
			response.replace("ecode", 1);
		}

		response.put("items", items);
		return response;
	}



	public static HashMap<String, Object> addItem(Session session, JsonNode inputData) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("success", false);
		response.put("ecode", 0);
		JsonNode itemIdNode = inputData.get("item_id");
		if (itemIdNode != null) {
			int itemId = itemIdNode.intValue();
			String query = "INSERT INTO inventory (p_id, item_id) VALUES (?, ?)";
			try (PreparedStatement statement = Database.getConnection().prepareStatement(query)) {
				statement.setInt(1, session.PlayerId);
				statement.setInt(2, itemId);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				response.replace("ecode", 1);
			}
		} else
			response.replace("ecode", 1);
		return response;
	}

	public static HashMap<String, Object> selectItem(Session session, JsonNode inputData) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("success", false);
		response.put("ecode", 0);

		String updateSet = null;

		JsonNode itemIdNode = inputData.get("item_id");
		JsonNode slotNode = inputData.get("slot");
		JsonNode subSlotNode = inputData.get("sub_slot");
		JsonNode teamNode = inputData.get("team");

		if (itemIdNode != null && slotNode != null && subSlotNode != null && teamNode != null) {
			int itemId = itemIdNode.intValue();
			int slot = slotNode.intValue();
			int subSlot = subSlotNode.intValue();
			int team = teamNode.intValue();

			switch (slot) {
				case 0:
					switch (team) {
						case 1:
							updateSet = "red_character";
							break;

						case 2:
							updateSet = "blue_character";
							break;
					}
					break;

				case 1:
					switch (subSlot) {
						case 1:
							updateSet = "weapon_primary";
							break;
						case 2:
							updateSet = "weapon_secondary";
							break;
						case 3:
							updateSet = "weapon_melee";
							break;
						case 4:
							updateSet = "weapon_throw";
							break;
						case 5:
							updateSet = "weapon_special";
							break;

					}
					break;
			}
			if (updateSet != null) {
				String query = "SELECT count(*) FROM inventory WHERE p_id=? AND item_id=?";
				try (PreparedStatement statement = Database.getConnection().prepareStatement(query)) {
					statement.setInt(1, session.PlayerId);
					statement.setInt(2, itemId);
					try (ResultSet rs = statement.executeQuery()) {
						if (rs.next()) {
							query = "UPDATE players SET " + updateSet + "=? WHERE id=?";
							try (PreparedStatement updateStatement = Database.getConnection().prepareStatement(query)) {
								updateStatement.setInt(1, itemId);
								updateStatement.setInt(2, session.PlayerId);
								updateStatement.executeUpdate();
								response.replace("success", true);
							}
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					response.replace("ecode", 1);
				}
			} else
				response.replace("ecode", 8);
		} else
			response.replace("ecode", 1);

		response.put("player_state", Player.Get(session));
		return response;
	}
}
