package me.danelegend.core.grace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.P;

import me.danelegend.core.util.Config;

public class GraceTimer {

	private GraceRunnable graceRunnable;
	private Config config;

	public GraceTimer() {
		reloadTimerData();
	}

	public GraceRunnable getGraceRunnable() {
		return this.graceRunnable;
	}

	public boolean cancel() {
		if (this.graceRunnable != null) {
			this.graceRunnable.cancel();
			this.graceRunnable = null;
			return true;
		}

		return false;
	}

	public void start(long millis) {
		if (this.graceRunnable == null) {
			this.graceRunnable = new GraceRunnable(this, millis);
			this.graceRunnable.runTaskLater(P.p, millis / 50L);
		}
	}

	public void reloadTimerData() {
		config = new Config(P.p, "grace");
		this.load(this.config);
	}

	public void saveTimerData() {
		this.save(this.config);
		this.config.save();
	}

	public void load(Config config) {
		long millis = System.currentTimeMillis();
		long remaining = config.getLong("gracetimer") - millis;
		if (remaining > 0L) {
			start(remaining);
		}
	}

	public void save(Config config) {
		if (this.getGraceRunnable() != null) {
			long remaining = this.getGraceRunnable().endMillis;
			if (remaining > 0L) {
				config.set("gracetimer", remaining);
			}
		}
	}

	public static class GraceRunnable extends BukkitRunnable {

		private GraceTimer graceTimer;
		private long startMillis;
		private long endMillis;

		public GraceRunnable(GraceTimer graceTimer, long duration) {
			this.graceTimer = graceTimer;
			this.startMillis = System.currentTimeMillis();
			this.endMillis = this.startMillis + duration;
		}

		public long getRemaining() {
			return endMillis - System.currentTimeMillis();
		}

		@Override
		public void run() {
			Bukkit.broadcastMessage(
					ChatColor.translateAlternateColorCodes('&', "&7&m---------------------------------------"));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
					"&eThe &6&lCannons are now &eENABLED. &6&lGood luck&e!"));
			Bukkit.broadcastMessage(
					ChatColor.translateAlternateColorCodes('&', "&7&m---------------------------------------"));
			this.cancel();
			this.graceTimer.graceRunnable = null;
		}
	}
}