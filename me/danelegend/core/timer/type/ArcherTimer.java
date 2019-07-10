package me.danelegend.core.timer.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.massivecraft.factions.P;

import me.danelegend.core.classes.archer.ArcherClass;
import me.danelegend.core.timer.PlayerTimer;
import me.danelegend.core.timer.event.TimerExpireEvent;

public class ArcherTimer extends PlayerTimer implements Listener {
	private final P plugin;
	private final Double ARCHER_DAMAGE;

	public ArcherTimer(P plugin) {
		super("Archer Tag", TimeUnit.SECONDS.toMillis(6L));
		this.plugin = plugin;
		this.ARCHER_DAMAGE = Double.valueOf(0.15D);
	}

	public String getScoreboardPrefix() {
		return ChatColor.GOLD.toString() + ChatColor.BOLD;
	}

	@EventHandler
	public void onExpire(TimerExpireEvent e) {
		if ((e.getUserUUID().isPresent()) && (e.getTimer().equals(this))) {
			UUID userUUID = e.getUserUUID().get();
			Player player = Bukkit.getPlayer(userUUID);
			if (player == null) {
				return;
			}
			ArcherClass.TAGGED.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (ArcherClass.TAGGED.containsKey(e.getPlayer().getUniqueId())) {
			ArcherClass.TAGGED.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		if (ArcherClass.TAGGED.containsKey(e.getPlayer().getUniqueId())) {
			ArcherClass.TAGGED.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))) {
			Player entity = (Player) e.getEntity();

			if (getRemaining(entity) > 0L) {
				Double damage = Double.valueOf(e.getDamage() * this.ARCHER_DAMAGE.doubleValue());
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow))) {
			Player entity = (Player) e.getEntity();
			Arrow arrow = (Arrow) e.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player damager = (Player) arrow.getShooter();

				if (getRemaining(entity) > 0L) {
					if (ArcherClass.TAGGED.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
						setCooldown(entity, entity.getUniqueId());
					}

					Double damage = Double.valueOf(e.getDamage() * this.ARCHER_DAMAGE.doubleValue());
					e.setDamage(e.getDamage() + damage.doubleValue());
				}
			}
		}
	}
}