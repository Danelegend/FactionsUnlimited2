package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetTokens extends FCommand {

	public CmdSetTokens() {
		super();
		this.aliases.add("addtokens");

		this.requiredArgs.add("faction");
		this.requiredArgs.add("amount");

		this.permission = Permission.SETTOKENS.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (args.size() == 2) {
			Faction target = argAsFaction(0);
			int amount = argAsInt(1);

			if (target == null) {
				me.sendMessage(ChatColor.RED + "That faction does not exist.");
				return;
			}

			target.setTokens(target.getTokens() + amount);

			me.sendMessage(
					ChatColor.YELLOW.toString() + "Set " + ChatColor.GREEN + target.getTag() + ChatColor.YELLOW + "'s tokens to " + target.getTokens());
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_SETTOKENS_DESCRIPTION;
	}
}