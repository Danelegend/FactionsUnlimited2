package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStealth extends FCommand {

	public CmdStealth() {
		super();
		this.aliases.add("stealth");

		this.permission = Permission.STEALTH.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_STEALTH_DESCRIPTION;
	}

	@Override
	public void perform() {

		if (!FactionsPlayerListener.stealthPlayer.contains(me.getUniqueId())) {
			me.sendMessage(ChatColor.YELLOW + "Stealth mode has been " + ChatColor.GREEN + "activated"
					+ ChatColor.YELLOW + "!");
			FactionsPlayerListener.stealthPlayer.add(me.getUniqueId());
		} else {
			me.sendMessage(ChatColor.YELLOW + "Stealth mode has been " + ChatColor.RED + "deactivated"
					+ ChatColor.YELLOW + "!");
			FactionsPlayerListener.stealthPlayer.remove(me.getUniqueId());
		}
	}
}