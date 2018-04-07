package Models;


import java.util.HashMap;


/**
 * Created by root on 05.04.2018.
 */
public class Items
{
	public static HashMap<Integer, Item> GetItems;

	public static class Item
	{
		public int Team    = 0;
		public int Slot    = 0;
		public int SubSlot = 0;
	}
}
