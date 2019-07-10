package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCore extends FCommand {

	public CmdCore() {
		super();
		this.aliases.add("core");

		this.permission = Permission.CORE.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (myFaction.isPlaced()) {
			LazyLocation coreLoc = myFaction.getCoreLocation();
			me.sendMessage(ChatColor.GOLD.toString() + coreLoc.toString());
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_CORE_DESCRIPTION;
	}
}