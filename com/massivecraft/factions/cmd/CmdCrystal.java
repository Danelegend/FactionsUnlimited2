package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.CoreUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCrystal extends FCommand {

	private static String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}

	public CmdCrystal() {
		super();
		this.aliases.add("crystal");

		this.permission = Permission.CRYSTAL.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
	}

	@Override
	public void perform() {
		if (!myFaction.isPlaced()) {
			me.sendMessage("");
			me.sendMessage(translate("&c&lWARNING..."));
			me.sendMessage(translate(" &e* &6Spawners will not be functional in any of your claims"));
			me.sendMessage(translate(" &e* &6Crops will not grow in any of your claims"));
			me.sendMessage(translate(" &e* &6You will be unable to access your faction core features"));
			me.sendMessage(translate("&cPlace down your core to &aactivate&c the above features!"));
			me.getInventory().addItem(CoreUtil.getCoreItem());
		} else {
			me.sendMessage(ChatColor.RED + "You already have a faction core placed down!");
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_CRYSTAL_DESCRIPTION;
	}
}