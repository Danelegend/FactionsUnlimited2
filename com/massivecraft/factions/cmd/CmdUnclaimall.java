package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdUnclaimall extends FCommand {

	public CmdUnclaimall() {
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");

		// this.requiredArgs.add("");
		// this.optionalArgs.put("", "");

		this.permission = Permission.UNCLAIM_ALL.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
	}

	@Override
	public void perform() {

		if (!assertMinRole(Role.ADMIN)) {
			return;
		}

		if (Econ.shouldBeUsed()) {
			double refund = Econ.calculateTotalLandRefund(myFaction.getLandRounded());
			if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
				if (!Econ.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(),
						TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
					return;
				}
			} else {
				if (!Econ.modifyMoney(fme, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(),
						TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
					return;
				}
			}
		}

		LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(myFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
		if (unclaimAllEvent.isCancelled()) {
			return;
		}

		for(FLocation loc : Board.getInstance().getAllClaims(myFaction)) {
			LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(loc, myFaction, fme);
			Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		}
		
		Board.getInstance().unclaimAll(myFaction.getId());
		myFaction.msg(TL.COMMAND_UNCLAIMALL_UNCLAIMED, fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims) {
			P.p.log(TL.COMMAND_UNCLAIMALL_LOG.format(fme.getName(), myFaction.getTag()));
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_UNCLAIMALL_DESCRIPTION;
	}

}
