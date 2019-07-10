package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPerm extends FCommand {

	public CmdPerm() {
		super();
		this.aliases.add("perm");

		this.optionalArgs.put("set", "");
		this.optionalArgs.put("perm", "");
		this.optionalArgs.put("relation", "");
		this.optionalArgs.put("yes/no", "");

		// TODO change
		this.permission = Permission.PERM.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
	}

	@Override
	public void perform() {
		if (args.size() == 0) {
			myFaction.getPermissions().sendPermissionsList(me);
			return;
		}

		if (args.size() == 4) {
			if (args.get(0).equalsIgnoreCase("set")) {
				String permission = "";
				String relation = "";
				boolean allow = false;
				try {
					permission = argAsString(1);
					relation = argAsString(2);
					if (argAsString(3).equalsIgnoreCase("yes"))
						allow = true;

					myFaction.getPermissions().setPermission(permission, relation, allow);
					me.chat("/f perm");
				} catch (NullPointerException e) {
					return;
				}
			}
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_PERM_DESCRIPTION;
	}
}