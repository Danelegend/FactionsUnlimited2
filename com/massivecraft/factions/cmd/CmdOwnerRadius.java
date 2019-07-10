package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.zcore.util.TL;

public class CmdOwnerRadius extends FCommand {
	
    public CmdOwnerRadius() {
        super();
        this.aliases.add("ownerradius");

        this.requiredArgs.add("radius");

        this.permission = Permission.OWNER.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

	@Override
	public TL getUsageTranslation() {
		return null;
	}

	@Override
	public void perform() {

		boolean hasBypass = fme.isAdminBypassing();

		if (!hasBypass && !assertHasFaction()) {
			return;
		}

		if (!Conf.ownedAreasEnabled) {
			fme.msg(TL.COMMAND_OWNER_DISABLED);
			return;
		}

		if (!hasBypass && Conf.ownedAreasLimitPerFaction > 0
				&& myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction) {
			fme.msg(TL.COMMAND_OWNER_LIMIT, Conf.ownedAreasLimitPerFaction);
			return;
		}

		if (!hasBypass && !assertMinRole(Conf.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.COLEADER)) {
			return;
		}

		int radius = argAsInt(0);

		new SpiralTask(new FLocation(me), radius) {

			@Override
			public boolean work() {

				// if economy is enabled, they're not on the bypass list, and
				// this command has a cost set, make 'em pay
				if (!payForCommand(Conf.econCostOwner, TL.COMMAND_OWNER_TOSET, TL.COMMAND_OWNER_FORSET)) {
					return false;
				}

				if (Board.getInstance().getFactionAt(this.currentFLocation()) == myFaction) {
					fme.msg(TL.COMMAND_OWNER_ADDED);
					myFaction.setPlayerAsOwner(fme, this.currentFLocation());
				} else {
					fme.msg(ChatColor.RED + "This claim does not belong to you");
				}

				return true;
			}
		};
	}
}