package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFWarp extends FCommand {

	public CmdFWarp() {
		super();
		this.aliases.add("warp");
		this.aliases.add("warps");

		this.permission = Permission.WARP.node;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
	}

	@Override
	public void perform() {
		// TODO: check if in combat.
		if (args.size() == 0) {

			Inventory warpInv = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Faction Warps");

			int count = 0;
			for (String s : myFaction.getWarps().keySet()) {

				List<String> lore = new ArrayList<String>();
				ItemStack warp;

				try {
					warp = myFaction.getWarpIcon(s);
				} catch (NullPointerException e) {
					warp = new ItemStack(Material.GRASS);
				}

				ItemMeta warpMeta = warp.getItemMeta();

				warpMeta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.AQUA + s);

				lore.add(ChatColor.GRAY + "Left click to teleport to desired destination...");
				lore.add(ChatColor.GRAY + "Right click to edit warp icon...");

				warpMeta.setLore(lore);
				warp.setItemMeta(warpMeta);

				warpInv.setItem(count, warp);
				count++;
			}

			me.openInventory(warpInv);
			return;
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_FWARP_DESCRIPTION;
	}
}