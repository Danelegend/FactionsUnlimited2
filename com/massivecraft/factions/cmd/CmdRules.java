package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdRules extends FCommand {

	public CmdRules() {
		this.aliases.add("rules");

		this.permission = Permission.RULE.node;
		this.disableOnLock = false;

		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RULES_DESCRIPTION;
	}

	@Override
	public void perform() {
		fme.sendMessage(ChatColor.GREEN + "Here are the rules for your Faction:");
		int count = 1;
		for (String s : myFaction.getRules()) {
			msg(ChatColor.AQUA + "Rule " + count + ": " + ChatColor.YELLOW + s);
			count++;
		}
	}
}