package me.danelegend.core.grace;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.P;

import me.danelegend.core.util.time.DurationFormatter;

public class GraceListener implements Listener {

	private final P plugin;

	public GraceListener(P plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosionPrime(ExplosionPrimeEvent e) {
		if (plugin.getGraceTimer().getGraceRunnable() != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (plugin.getGraceTimer().getGraceRunnable() != null) {
			Player p = e.getPlayer();

			new BukkitRunnable() {
				public void run() {
					p.sendMessage(translate("&cGrace Timer &7(all explosions disabled) &cis active for another &e"
							+ DurationFormatter
									.graceRemaining(plugin.getGraceTimer().getGraceRunnable().getRemaining())));
				}
			}.runTaskLater(P.p, 3L);
		}
	}

	private String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}
}