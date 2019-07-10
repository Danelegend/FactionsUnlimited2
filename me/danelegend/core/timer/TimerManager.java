package me.danelegend.core.timer;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.P;

import me.danelegend.core.timer.type.AppleTimer;
import me.danelegend.core.timer.type.ArcherTimer;
import me.danelegend.core.timer.type.ClassLoad;
import me.danelegend.core.timer.type.EnderPearlTimer;
import me.danelegend.core.timer.type.GappleTimer;
import me.danelegend.core.timer.type.RaidableTimer;
import me.danelegend.core.timer.type.ShieldTimer;
import me.danelegend.core.timer.type.TNTTimer;
import me.danelegend.core.util.Config;

public class TimerManager implements Listener {
	private final EnderPearlTimer enderPearlTimer;
	private final AppleTimer appleTimer;
	private final GappleTimer gappleTimer;
	private final ClassLoad pvpClassWarmupTimer;
	private final ArcherTimer archerTimer;
	private final TNTTimer tntTimer;
	private final ShieldTimer shieldTimer;
	private final RaidableTimer raidTimer;
	private final Set<Timer> timers;
	private final JavaPlugin plugin;
	private Config config;

	public TimerManager(final P plugin) {
		this.timers = new LinkedHashSet<Timer>();
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) plugin);
		this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
		this.registerTimer(this.gappleTimer = new GappleTimer(plugin));
		this.registerTimer(this.appleTimer = new AppleTimer(plugin));
		this.registerTimer(this.pvpClassWarmupTimer = new ClassLoad(plugin));
		this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
		this.registerTimer(this.tntTimer = new TNTTimer());
		this.registerTimer(this.shieldTimer = new ShieldTimer());
		this.registerTimer(this.raidTimer = new RaidableTimer());
		this.reloadTimerData();
	}

	public void registerTimer(final Timer timer) {
		this.timers.add(timer);
		if (timer instanceof Listener) {
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, (Plugin) this.plugin);
		}
	}

	public void unregisterTimer(final Timer timer) {
		this.timers.remove(timer);
	}

	public void reloadTimerData() {
		this.config = new Config(P.p, "timers");
		for (final Timer timer : this.timers) {
			timer.load(this.config);
		}
	}

	public void saveTimerData() {
		for (final Timer timer : this.timers) {
			timer.onDisable(this.config);
		}
		this.config.save();
	}

	public EnderPearlTimer getEnderPearlTimer() {
		return this.enderPearlTimer;
	}

	public GappleTimer getGappleTimer() {
		return this.gappleTimer;
	}

	public AppleTimer getAppleTimer() {
		return this.appleTimer;
	}

	public ClassLoad getPvpClassWarmupTimer() {
		return this.pvpClassWarmupTimer;
	}

	public ArcherTimer getArcherTimer() {
		return this.archerTimer;
	}

	public TNTTimer getTNTTimer() {
		return this.tntTimer;
	}

	public ShieldTimer getShieldTimer() {
		return this.shieldTimer;
	}

	public RaidableTimer getRaidableTimer() {
		return this.raidTimer;
	}

	public Set<Timer> getTimers() {
		return this.timers;
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public Config getConfig() {
		return this.config;
	}
}