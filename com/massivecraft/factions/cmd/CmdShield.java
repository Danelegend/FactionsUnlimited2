package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShield extends FCommand {

	public CmdShield() {
		super();
		this.aliases.add("shield");

		// TODO CHANGE
		this.permission = Permission.SHIELD.node;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
	}

	@Override
	public void perform() {
		me.sendMessage(ChatColor.YELLOW + "Your shield is active for ");
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_SHIELD_DESCRIPTION;
	}
}