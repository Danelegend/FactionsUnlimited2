package me.danelegend.core.tntstorage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestItem {

	public static ItemStack tntChest() {
		ItemStack tntChest = new ItemStack(Material.CHEST);
		ItemMeta tntMeta = tntChest.getItemMeta();
		tntMeta.setDisplayName(ChatColor.RED + "TNT Chest");
		tntChest.setItemMeta(tntMeta);
		return tntChest;
	}
}