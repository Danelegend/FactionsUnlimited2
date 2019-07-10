package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdClaimChunk extends FCommand {

	public static final BlockFace[] axis = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST };

	public CmdClaimChunk() {
		super();
		this.aliases.add("claimchunk");

		this.requiredArgs.add("x");
		this.requiredArgs.add("z");

		this.permission = Permission.CLAIM_CHUNK.node;
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
			fme.msg(TL.COMMAND_CLAIMCHUNK_ABOVEMAX, Conf.chunkClaimDistance);
			return;
		}

		final Faction forFaction = this.argAsFaction(2, myFaction);

		Location claimAt = new Location(this.me.getWorld(), x * 16, 0.0D, z * 16);

		fme.attemptClaim(forFaction, claimAt, true);
		me.chat("/f map");
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_CLAIMCHUNK_DESCRIPTION;
	}
}