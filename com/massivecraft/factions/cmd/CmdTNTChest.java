package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.zcore.util.TL;

public class CmdTNTChest extends FCommand {

	public CmdTNTChest() {
		super();
		this.aliases.add("tntchest");

		this.requiredArgs.add("player");

		this.permission = Permission.TNTCHEST.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (args.size() == 1) {
			Player target = argAsPlayer(0);

			if (target == null) {
				me.sendMessage(ChatColor.RED + "That player does not exist.");
				return;
			}

			target.getInventory()
					.addItem(new ItemBuilder(Material.CHEST).setName(ChatColor.RED + "TNT Chest").toItemStack());
			me.sendMessage(ChatColor.GOLD + "You gave " + target.getName() + " a TNT Chest");
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_TNTCHEST_DESCRIPTION;
	}
}