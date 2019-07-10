package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdToggleModeratorChat extends FCommand {

	public CmdToggleModeratorChat() {
		super();
		this.aliases.add("tmc");
		this.aliases.add("togglemodchat");
		this.aliases.add("modc");

		this.disableOnLock = false;

		this.permission = Permission.TOGGLE_TRUCE_CHAT.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_TOGGLEMODCHAT_DESCRIPTION;
	}

	@Override
	public void perform() {

		if (!assertMinRole(Role.MODERATOR)) {
			return;
		}

		if (!Conf.factionOnlyChat) {
			msg(TL.COMMAND_CHAT_DISABLED.toString());
			return;
		}

		boolean ignoring = fme.isIgnoreModChat();

		msg(ignoring ? TL.COMMAND_TOGGLEMODCHAT_UNIGNORE : TL.COMMAND_TOGGLEMODCHAT_IGNORE);
		fme.setIgnoreModChat(!ignoring);
	}
}
