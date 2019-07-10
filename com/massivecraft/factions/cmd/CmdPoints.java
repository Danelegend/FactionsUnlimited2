package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.NumberUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPoints extends FCommand {

	public CmdPoints() {
		super();
		this.aliases.add("points");

		this.permission = Permission.POINTS.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		me.sendMessage(
				ChatColor.GOLD.toString() + NumberUtil.format(myFaction.getPoints()) + ChatColor.YELLOW + " points");
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_POINTS_DESCRIPTION;
	}
}