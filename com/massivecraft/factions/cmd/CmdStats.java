package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.NumberUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStats extends FCommand {

	public CmdStats() {
		this.aliases.add("stats");

		this.optionalArgs.put("player", "");

		this.permission = Permission.STATS.node;
		this.disableOnLock = false;

		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_STATS_DESCRIPTION;
	}

	@Override
	public void perform() {
		if (args.size() == 0) {
			me.sendMessage(translate("&8&m---- &r ") + fme.getNameAndTitle() + translate(" &8&m----"));
			me.sendMessage(translate("&cKills: &a") + NumberUtil.format(fme.getKills()));
			me.sendMessage(translate("&cDeaths: &a") + NumberUtil.format(fme.getDeaths()));
			me.sendMessage(translate("&cFish Caught: &a") + fme.getPlayer().getStatistic(Statistic.FISH_CAUGHT));
			me.sendMessage(translate("&cPower: &a") + fme.getPowerRounded() + "/" + fme.getPowerMaxRounded());
		} else {
			FPlayer fp = argAsBestFPlayerMatch(0);
			me.sendMessage(translate("&8&m---- &r ") + fp.getNameAndTitle() + translate(" &8&m----"));
			me.sendMessage(translate("&cKills: &a") + NumberUtil.format(fp.getKills()));
			me.sendMessage(translate("&cDeaths: &a") + NumberUtil.format(fp.getDeaths()));
			me.sendMessage(translate("&cFish Caught: &a") + fp.getPlayer().getStatistic(Statistic.FISH_CAUGHT));
			me.sendMessage(translate("&cPower: &a") + fp.getPowerRounded() + "/" + fp.getPowerMaxRounded());
		}
	}

	private String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}
}