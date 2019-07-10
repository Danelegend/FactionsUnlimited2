package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdRuleRemove extends FCommand {

	public CmdRuleRemove() {
		this.aliases.add("removerule");

		this.requiredArgs.add("rule number");
		this.errorOnToManyArgs = false;

		this.permission = Permission.RULE.node;
		this.disableOnLock = false;

		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RULES_DESCRIPTION;
	}

	@Override
	public void perform() {
		int rule = argAsInt(0, 1);
		myFaction.getRules().remove(rule - 1);
		myFaction.sendMessage(ChatColor.GREEN + me.getName() + " has removed rule " + rule + "!");
	}
}