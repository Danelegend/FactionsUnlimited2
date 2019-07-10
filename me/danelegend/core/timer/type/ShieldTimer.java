package me.danelegend.core.timer.type;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.ShieldPurchaseEvent;

import me.danelegend.core.timer.FactionTimer;
import me.danelegend.core.util.Config;

/**
 * Timer that handles Shield countdown
 */
public class ShieldTimer extends FactionTimer implements Listener {

	public ShieldTimer() {
		super("Shield", TimeUnit.HOURS.toMillis(8L));
	}

	@Override
	public void onDisable(Config config) {
		super.onDisable(config);
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.AQUA + ChatColor.BOLD.toString();
	}

	@Override
	public void onExpire(String factionId) {
		Faction fac = Factions.getInstance().getFactionById(factionId);

		fac.sendMessage("");
		fac.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"  &e* &cThe faction shield has expired and your claims are now exposed to explosions"));
		fac.sendMessage(
				ChatColor.translateAlternateColorCodes('&', "  &e* &bPurchase another shield at &cyour faction core"));
		fac.sendMessage("");
		fac.setShield(false);
	}

	@EventHandler
	public void onFactionDisband(FactionDisbandEvent e) {
		if (this.getRemaining(e.getFaction()) > 0) {
			this.clearCooldown(e.getFaction());
		}
	}

	@EventHandler
	public void onShieldPurchase(ShieldPurchaseEvent e) {

		Faction faction = e.getFaction();

		faction.sendMessage("");
		faction.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"  &e* &cThe faction shield is now activated and protecting you from explosions &6(except creeper eggs)"));
		faction.sendMessage("");

		faction.setShield(true);

		setCooldown(faction, faction.getId(), e.getDuration(), true);
	}
}