package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdUnclaimChunk extends FCommand {

	public static final BlockFace[] axis = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST };

	public CmdUnclaimChunk() {
		super();
		this.aliases.add("unclaimchunk");

		this.requiredArgs.add("x");
		this.requiredArgs.add("z");

		this.permission = Permission.UNCLAIM_CHUNK.node;
		this.disableOnLock = true;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// Args
		Integer x = this.argAsInt(0); // Default to 1
		Integer z = this.argAsInt(1);

		Integer x2 = this.me.getLocation().getChunk().getX();
		Integer z2 = this.me.getLocation().getChunk().getZ();

		Integer distance = (int) Math.sqrt(Math.pow((x - x2), 2) + Math.pow((z - z2), 2));

		if (distance > Conf.chunkClaimDistance) {
			fme.msg(TL.COMMAND_UNCLAIMCHUNK_ABOVEMAX, Conf.chunkClaimDistance);
			return;
		}

		Location claimAt = new Location(this.me.getWorld(), x * 16, 0.0D, z * 16);

		unClaim(new FLocation(claimAt));
		me.chat("/f map");
	}

	private boolean unClaim(FLocation target) {
		Faction targetFaction = Board.getInstance().getFactionAt(target);
		if (targetFaction.isSafeZone()) {
			if (Permission.MANAGE_SAFE_ZONE.has(sender)) {
				Board.getInstance().removeAt(target);
				msg(TL.COMMAND_UNCLAIM_SAFEZONE_SUCCESS);

				if (Conf.logLandUnclaims) {
					P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(),
							targetFaction.getTag()));
				}
				return true;
			} else {
				msg(TL.COMMAND_UNCLAIM_SAFEZONE_NOPERM);
				return false;
			}
		} else if (targetFaction.isWarZone()) {
			if (Permission.MANAGE_WAR_ZONE.has(sender)) {
				Board.getInstance().removeAt(target);
				msg(TL.COMMAND_UNCLAIM_WARZONE_SUCCESS);

				if (Conf.logLandUnclaims) {
					P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(),
							targetFaction.getTag()));
				}
				return true;
			} else {
				msg(TL.COMMAND_UNCLAIM_WARZONE_NOPERM);
				return false;
			}
		}

		if (fme.isAdminBypassing()) {
			Board.getInstance().removeAt(target);

			targetFaction.msg(TL.COMMAND_UNCLAIM_UNCLAIMED, fme.describeTo(targetFaction, true));
			msg(TL.COMMAND_UNCLAIM_UNCLAIMS);

			if (Conf.logLandUnclaims) {
				P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
			}

			return true;
		}

		if (!assertHasFaction()) {
			return false;
		}

		if (!assertMinRole(Role.MODERATOR)) {
			return false;
		}

		if (myFaction != targetFaction) {
			msg(TL.COMMAND_UNCLAIM_WRONGFACTION);
			return false;
		}

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(target, targetFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		if (unclaimEvent.isCancelled()) {
			return false;
		}

		if (Econ.shouldBeUsed()) {
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());

			if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
				if (!Econ.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(),
						TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
					return false;
				}
			} else {
				if (!Econ.modifyMoney(fme, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(),
						TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
					return false;
				}
			}
		}

		Board.getInstance().removeAt(target);
		myFaction.msg(TL.COMMAND_UNCLAIM_FACTIONUNCLAIMED, fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims) {
			P.p.log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
		}

		return true;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_UNCLAIMCHUNK_DESCRIPTION;
	}
}