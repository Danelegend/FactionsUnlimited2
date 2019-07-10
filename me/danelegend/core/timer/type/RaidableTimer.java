package me.danelegend.core.timer.type;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRaidEvent;

import me.danelegend.core.timer.FactionTimer;
import me.danelegend.core.util.Config;

/**
 * Timer that handles Raided countdown
 */
public class RaidableTimer extends FactionTimer implements Listener {

	public RaidableTimer() {
		super("Raidable", TimeUnit.MINUTES.toMillis(10L));
	}

	@Override
	public void onDisable(Config config) {
		super.onDisable(config);
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.DARK_RED + ChatColor.BOLD.toString();
	}

	@Override
	public void onExpire(String factionId) {
		Faction fac = Factions.getInstance().getFactionById(factionId);

		fac.sendMessage("");
		fac.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &e* &bYou are no longer raidable!"));
		fac.sendMessage("");
		fac.setRaidable(false);
	}

	@EventHandler
	public void onFactionDisband(FactionDisbandEvent e) {
		if (this.getRemaining(e.getFaction()) > 0) {
			this.clearCooldown(e.getFaction());
		}
	}

	@EventHandler
	public void onFactionRaid(FactionRaidEvent e) {
		setCooldown(e.getRaidedFaction(), e.getRaidedFaction().getId(), this.defaultCooldown, true);
	}
}
