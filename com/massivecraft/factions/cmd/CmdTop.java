package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.NumberUtil;
import com.massivecraft.factions.zcore.util.TL;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CmdTop extends FCommand {

	public CmdTop() {
		super();
		this.aliases.add("top");
		this.aliases.add("t");

		this.optionalArgs.put("page", "");

		this.permission = Permission.TOP.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// Can sort by: points
		// Get all Factions and remove non player ones.
		ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
		factionList.remove(Factions.getInstance().getWilderness());
		factionList.remove(Factions.getInstance().getSafeZone());
		factionList.remove(Factions.getInstance().getWarZone());

		// TODO: Better way to sort?
		Collections.sort(factionList, new Comparator<Faction>() {
			@Override
			public int compare(Faction f1, Faction f2) {
				int f1Size = f1.getPoints();
				int f2Size = f2.getPoints();
				if (f1Size < f2Size) {
					return 1;
				} else if (f1Size > f2Size) {
					return -1;
				}
				return 0;
			}
		});

		ArrayList<FancyMessage> lines = new ArrayList<FancyMessage>();

		final int pageheight = 9;
		int pagenumber = this.argAsInt(1, 1);
		int pagecount = (factionList.size() / pageheight) + 1;
		if (pagenumber > pagecount) {
			pagenumber = pagecount;
		} else if (pagenumber < 1) {
			pagenumber = 1;
		}
		int start = (pagenumber - 1) * pageheight;
		int end = start + pageheight;
		if (end > factionList.size()) {
			end = factionList.size();
		}

		// lines.add(TL.COMMAND_TOP_TOP.format("TOP", pagenumber, pagecount));
		lines.add(new FancyMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----- " + ChatColor.BLUE
				+ " Top Factions " + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + " -----"));

		int rank = 1;
		for (Faction faction : factionList.subList(start, end)) {
			// Get the relation color if player is executing this.
			String fac = sender instanceof Player ? faction.getRelationTo(fme).getColor() + faction.getTag()
					: faction.getTag();
			// lines.add(TL.COMMAND_TOP_LINE.format(rank, fac, getValue(faction)));
			lines.add(new FancyMessage(ChatColor.GOLD + String.valueOf(rank) + ". " + ChatColor.RED + fac + " - "
					+ ChatColor.BLUE + getValue(faction)).tooltip(getTooltip(faction)));
			rank++;
		}

		for (FancyMessage message : lines)
			message.send(me);
	}

	private List<String> getTooltip(Faction faction) {
		List<String> toolTip = new ArrayList<String>();

		toolTip.add(0,
				translate("&8&m---- &r ") + myFaction.getColorTo(faction) + faction.getTag() + translate(" &8&m----"));
		toolTip.add(translate("&cRaiding: &a") + NumberUtil.format(faction.getRaidingPoints()));
		toolTip.add(translate("&cGlobal Events: &a") + NumberUtil.format(faction.getEventPoints()));
		toolTip.add(translate("&cFaction Upgrades: &a") + NumberUtil.format(faction.getUpgradePoints()));
		toolTip.add(translate("&cmcMMO Levels: &a") + NumberUtil.format(faction.getLastMcMMO()));
		toolTip.add(translate("&8&m---------------"));
		toolTip.add(translate("&eTotal Points: &3") + NumberUtil.format(faction.getPoints()));
		return toolTip;
	}

	private String getValue(Faction faction) {
		return NumberUtil.format(faction.getPoints());
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_TOP_DESCRIPTION;
	}

	private String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}
}