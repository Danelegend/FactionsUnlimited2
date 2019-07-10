package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdTokens extends FCommand {

	public CmdTokens() {
		super();
		this.aliases.add("tokens");

		// TODO Change
		this.permission = Permission.TOKENS.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		me.sendMessage(ChatColor.GOLD.toString() + myFaction.getTokens() + ChatColor.YELLOW + " tokens");
	}

	@Override
	public TL getUsageTranslation() {
		// TODO Change
		return TL.COMMAND_TOKENS_DESCRIPTION;
	}
}