package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdRuleClear extends FCommand {

	public CmdRuleClear() {
		this.aliases.add("clearrules");

		this.permission = Permission.RULE.node;
		this.disableOnLock = false;

		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RULESCLEAR_DESCRIPTION;
	}

	@Override
	public void perform() {
		myFaction.getRules().clear();
		myFaction.sendMessage(ChatColor.GREEN + me.getName() + " has cleared all the faction's rules!");
	}
}