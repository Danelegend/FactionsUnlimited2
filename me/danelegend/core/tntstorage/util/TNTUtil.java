package me.danelegend.core.tntstorage.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class TNTUtil {

	public static void withdraw(Player p, int amount) {
		ItemStack[] tnt = new ItemStack[] { new ItemStack(Material.TNT, amount) };
		p.getInventory().addItem(tnt);

		String message = ChatColor.translateAlternateColorCodes('&', "&cYou withdrew &e%tnt% &cTNT");
		message = message.replaceAll("%tnt%", String.valueOf(amount));
		Faction fac = FPlayers.getInstance().getByPlayer(p).getFaction();
		fac.setTNT(fac.getTNT() - amount);

		p.sendMessage(message);
	}
}