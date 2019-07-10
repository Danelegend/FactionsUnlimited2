package com.massivecraft.factions.cmd;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdRuleAdd extends FCommand {

	public CmdRuleAdd() {
		this.aliases.add("addrule");

		this.requiredArgs.add("rule");
		this.errorOnToManyArgs = false;

		this.permission = Permission.RULE.node;
		this.disableOnLock = false;

		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RULESADD_DESCRIPTION;
	}

	@Override
	public void perform() {
		if (myFaction.getRules().size() + 1 <= 12) {
			String rule = StringUtils.join(args, " ");
			myFaction.getRules().add(rule);
			myFaction.sendMessage(ChatColor.GREEN + me.getName() + " has added a new rule!");
			myFaction.sendMessage(ChatColor.YELLOW + rule);
		} else {
			me.sendMessage(ChatColor.RED + "You have reached the max amount of rules.");
		}
	}
}