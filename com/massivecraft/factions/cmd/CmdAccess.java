package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAccess extends FCommand {

	public FLocation chunk;
	public TerritoryAccess ta;
	public Faction hostFaction;

	public CmdAccess() {
		super();
		this.aliases.add("access");

		this.requiredArgs.add("f|p|view|clear");
		this.optionalArgs.put("playername|factionname", "");
		this.optionalArgs.put("all|clear", "");

		this.permission = Permission.ACCESS.node;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {

		chunk = new FLocation(me.getLocation());
		ta = Board.getInstance().getTerritoryAccessAt(chunk);
		hostFaction = ta.getHostFaction();

		if (args.size() == 0) {
			me.sendMessage(ChatColor.RED + "/f access [f|p|view] [playername/factionname] [all| clear]");
			me.sendMessage(ChatColor.RED + "/f access view");
		}

		if (args.size() > 0) {
			if (args.get(0).equalsIgnoreCase("f")) {

				Faction faction = argAsFaction(1);

				if (faction == null) {
					return;
				}

				boolean newValue = !ta.isFactionIdGranted(faction.getId());

				if (args.size() == 3) {
					if (args.get(2).equalsIgnoreCase("all")) {
						ta = ta.withFactionId(faction.getId(), newValue);

						for (FLocation floc : FPlayers.getInstance().getByPlayer(me).getFaction().getAllClaims()) {
							Board.getInstance().setTerritoryAccessAt(floc, ta);
						}
						me.sendMessage(ChatColor.GREEN + "You have granted " + faction.getTag()
								+ " access to all of your land");
						this.sendAccessInfo(me);
						return;
					} else if (args.get(2).equals("clear")) {
						ta = ta.withFactionId(faction.getId(), false);

						for (FLocation floc : FPlayers.getInstance().getByPlayer(me).getFaction().getAllClaims()) {
							Board.getInstance().setTerritoryAccessAt(floc, ta);
						}
						me.sendMessage(ChatColor.GREEN + "You have removed " + faction.getTag()
								+ "'s access in all of your land");
						this.sendAccessInfo(me);
						return;
					}
				}

				if (Board.getInstance().getFactionAt(chunk) == FPlayers.getInstance().getByPlayer(me).getFaction()) {

					// Apply
					ta = ta.withFactionId(faction.getId(), newValue);
					Board.getInstance().setTerritoryAccessAt(chunk, ta);

					// Inform
					this.sendAccessInfo(me);
				} else {
					me.sendMessage(ChatColor.RED + "You cannot grant access to other faction's land");
				}
			} else if (args.get(0).equalsIgnoreCase("p")) {

				// Args
				FPlayer fplayer = argAsBestFPlayerMatch(1);

				if (fplayer == null) {
					return;
				}

				boolean newValue = !ta.isPlayerIdGranted(fplayer.getId());

				if (args.size() == 3) {
					if (args.get(2).equalsIgnoreCase("all")) {
						ta = ta.withPlayerId(fplayer.getId(), true);

						for (FLocation floc : FPlayers.getInstance().getByPlayer(me).getFaction().getAllClaims()) {
							Board.getInstance().setTerritoryAccessAt(floc, ta);
						}
						me.sendMessage(ChatColor.GREEN + "You have granted " + fplayer.getName()
								+ " access to all of your land");
						this.sendAccessInfo(me);
						return;
					} else if (args.get(2).equals("clear")) {
						ta = ta.withPlayerId(fplayer.getId(), false);

						for (FLocation floc : FPlayers.getInstance().getByPlayer(me).getFaction().getAllClaims()) {
							Board.getInstance().setTerritoryAccessAt(floc, ta);
						}
						me.sendMessage(ChatColor.GREEN + "You have removed " + fplayer.getName()
								+ "'s access in all of your land");
						this.sendAccessInfo(me);
						return;
					}
				}

				if (Board.getInstance().getFactionAt(chunk) == FPlayers.getInstance().getByPlayer(me).getFaction()) {
					// Apply
					ta = ta.withPlayerId(fplayer.getId(), newValue);
					Board.getInstance().setTerritoryAccessAt(chunk, ta);

					// Inform
					this.sendAccessInfo(me);
				} else {
					me.sendMessage(ChatColor.RED + "You cannot grant access to other faction's land");
				}
			} else if (args.get(0).equalsIgnoreCase("view")) {
				if (Board.getInstance().getFactionAt(chunk) == FPlayers.getInstance().getByPlayer(me).getFaction()) {
					this.sendAccessInfo(me);
				} else {
					me.sendMessage(ChatColor.RED + "You cannot view access in other faction's land");
				}
			} else {
				me.sendMessage(ChatColor.RED + "/f access [p/f] [playername/factionname] [all]");
				me.sendMessage(ChatColor.RED + "/f access view");
			}
		}
	}

	public void sendAccessInfo(Player p) {
		String title = ChatColor.LIGHT_PURPLE + "Access at " + chunk.toString();
		FPlayer me = FPlayers.getInstance().getByPlayer(p);
		Faction fme = me.getFaction();
		p.sendMessage(title);

		String bool = ta.isHostFactionAllowed() ? "TRUE" : "FALSE";

		p.sendMessage(ChatColor.YELLOW + "Host Faction: " + ta.getHostFaction().describeTo(fme, true));
		p.sendMessage(ChatColor.YELLOW + "Host Faction Allowed: " + bool);
		p.sendMessage(
				ChatColor.YELLOW + "Granted Players: " + describeRelationParticipators(ta.getGrantedFPlayers(), fme));
		p.sendMessage(
				ChatColor.YELLOW + "Granted Factions: " + describeRelationParticipators(ta.getGrantedFactions(), fme));
	}

	public static String describeRelationParticipators(Collection<? extends RelationParticipator> relationParticipators,
			RelationParticipator observer) {
		if (relationParticipators.size() == 0)
			return "none";
		List<String> descriptions = new ArrayList<>();
		for (RelationParticipator relationParticipator : relationParticipators) {
			descriptions.add(relationParticipator.describeTo(observer));
		}

		String all = "";

		for (String s : descriptions) {
			all = all + " " + s;
		}
		return all;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_ACCESS_DESCRIPTION;
	}
}