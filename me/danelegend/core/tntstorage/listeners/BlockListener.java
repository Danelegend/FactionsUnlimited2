package me.danelegend.core.tntstorage.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {

		if (e.getItemInHand() == null || (e.getItemInHand().getType() != Material.CHEST)) {
			return;
		}

		if (!e.getItemInHand().hasItemMeta() || !e.getItemInHand().getItemMeta().hasDisplayName()) {
			return;
		}

		ItemStack inHand = e.getItemInHand();
		ItemMeta inHandMeta = inHand.getItemMeta();

		if (inHandMeta.getDisplayName().equals(ChatColor.RED + "TNT Chest")) {
			Faction factionAt = Board.getInstance().getFactionAt(new FLocation(e.getBlock()));
			if (factionAt.isWilderness()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cYou cannot place this in &2Wilderness."));
			}
		}
	}
}