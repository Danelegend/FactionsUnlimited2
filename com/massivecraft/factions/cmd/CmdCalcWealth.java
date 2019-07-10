package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCalcWealth extends FCommand {

	public CmdCalcWealth() {
		super();
		this.aliases.add("calcwealth");
		this.aliases.add("calculatewealth");

		this.permission = Permission.CALCWEALTH.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// Get all Factions and remove non player ones.
		ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
		factionList.remove(Factions.getInstance().getWilderness());
		factionList.remove(Factions.getInstance().getSafeZone());
		factionList.remove(Factions.getInstance().getWarZone());

		for (Faction faction : factionList) {
			faction.setLastMcMMO(getMCMMOAdded(faction));
		}

		me.sendMessage(ChatColor.GOLD + "Successfully recalculated all mcMMO faction points!");
	}

	private int getMCMMOAdded(Faction fac) {
		int totalLevels = 0;
		for (FPlayer fp : fac.getFPlayers())
			for (String skillType : SkillAPI.getSkills())
				totalLevels += ExperienceAPI.getLevelOffline(UUID.fromString(fp.getId()), skillType);
		return (int) Math.ceil(totalLevels * 0.15);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_CALCWEALTH_DESCRIPTION;
	}
}