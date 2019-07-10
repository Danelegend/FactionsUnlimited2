package me.danelegend.core.timer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.massivecraft.factions.Faction;

import me.danelegend.core.timer.factionevent.FactionTimerClearEvent;
import me.danelegend.core.timer.factionevent.FactionTimerExpireEvent;
import me.danelegend.core.timer.factionevent.FactionTimerExtendEvent;
import me.danelegend.core.timer.factionevent.FactionTimerPauseEvent;
import me.danelegend.core.timer.factionevent.FactionTimerStartEvent;
import me.danelegend.core.util.Config;

/**
 * Represents a {@link Player} {@link Timer} countdown.
 */
public abstract class FactionTimer extends Timer {

	private static final String COOLDOWN_PATH = "timer-cooldowns";
	private static final String PAUSE_PATH = "timer-pauses";
	protected final boolean persistable;
	protected final Map<String, FactionTimerCooldown> cooldowns = new ConcurrentHashMap<>();

	public FactionTimer(String name, long defaultCooldown) {
		this(name, defaultCooldown, true);
	}

	public FactionTimer(String name, long defaultCooldown, boolean persistable) {
		super(name, defaultCooldown);
		this.persistable = persistable;
	}

	public void onExpire(String factionId) {
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerExpireLoadReduce(FactionTimerExpireEvent event) {
		if (event.getTimer() == this) {
			Optional<String> optionalFactionId = event.getFactionId();
			if (optionalFactionId.isPresent()) {
				String factionId = optionalFactionId.get();
				onExpire(factionId);
				clearCooldown(factionId);
			}
		}
	}

	public void clearCooldown(Faction faction) {
		this.clearCooldown(faction.getId());
	}

	public FactionTimerCooldown clearCooldown(String factionId) {
		FactionTimerCooldown runnable = this.cooldowns.remove(factionId);
		if (runnable != null) {
			runnable.cancel();
			Bukkit.getPluginManager().callEvent(new FactionTimerClearEvent(factionId, this));
			return runnable;
		}

		return null;
	}

	public boolean isPaused(Faction faction) {
		return this.isPaused(faction.getId());
	}

	public boolean isPaused(String factionId) {
		FactionTimerCooldown runnable = cooldowns.get(factionId);
		return runnable != null && runnable.isPaused();
	}

	public void setPaused(String factionId, boolean paused) {
		FactionTimerCooldown runnable = this.cooldowns.get(factionId);
		if (runnable != null && runnable.isPaused() != paused) {
			FactionTimerPauseEvent event = new FactionTimerPauseEvent(factionId, this, paused);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				runnable.setPaused(paused);
			}
		}
	}

	public long getRemaining(Faction faction) {
		return this.getRemaining(faction.getId());
	}

	public long getRemaining(String factionId) {
		FactionTimerCooldown runnable = this.cooldowns.get(factionId);
		return runnable == null ? 0L : runnable.getRemaining();
	}

	public boolean setCooldown(@Nullable Faction faction, String factionId) {
		return this.setCooldown(faction, factionId, this.defaultCooldown, false);
	}

	public boolean setCooldown(@Nullable Faction faction, String factionId, long duration, boolean overwrite) {
		return this.setCooldown(faction, factionId, duration, overwrite, null);
	}

	/**
	 * @return true if cooldown was set or changed
	 */
	public boolean setCooldown(@Nullable Faction faction, String factionId, long duration, boolean overwrite,
			@Nullable Predicate<Long> currentCooldownPredicate) {
		FactionTimerCooldown runnable = duration > 0L ? this.cooldowns.get(factionId) : this.clearCooldown(factionId);
		if (runnable != null) {
			long remaining = runnable.getRemaining();
			if (!overwrite && remaining > 0L && duration <= remaining) {
				return false;
			}

			FactionTimerExtendEvent event = new FactionTimerExtendEvent(faction, factionId, this, remaining, duration);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}

			boolean flag = true;
			if (currentCooldownPredicate != null) {
				flag = currentCooldownPredicate.apply(remaining);
			}

			if (flag) {
				runnable.setRemaining(duration);
			}

			return flag;
		} else {
			Bukkit.getPluginManager().callEvent(new FactionTimerStartEvent(faction, factionId, this, duration));
			runnable = new FactionTimerCooldown(this, factionId, duration);
		}

		this.cooldowns.put(factionId, runnable);
		return true;
	}

	@Override
	public void load(Config config) {
		if (!persistable) {
			return;
		}

		String path = COOLDOWN_PATH + '.' + name;
		Object object = config.get(path);
		if (object instanceof MemorySection) {
			MemorySection section = (MemorySection) object;
			long millis = System.currentTimeMillis();
			for (String id : section.getKeys(false)) {
				long remaining = config.getLong(section.getCurrentPath() + '.' + id) - millis;
				if (remaining > 0L) {
					setCooldown(null, id, remaining, true, null);
				}
			}
		}

		path = PAUSE_PATH + '.' + name;
		if ((object = config.get(path)) instanceof MemorySection) {
			MemorySection section = (MemorySection) object;
			for (String id : section.getKeys(false)) {
				FactionTimerCooldown timerCooldown = cooldowns.get(id);
				if (timerCooldown == null)
					continue;

				timerCooldown.setPauseMillis(config.getLong(path + '.' + id));
			}
		}
	}

	@Override
	public void onDisable(Config config) {
		if (this.persistable) {
			Set<Map.Entry<String, FactionTimerCooldown>> entrySet = this.cooldowns.entrySet();
			Map<String, Long> pauseSavemap = new LinkedHashMap<>(entrySet.size());
			Map<String, Long> cooldownSavemap = new LinkedHashMap<>(entrySet.size());
			for (Map.Entry<String, FactionTimerCooldown> entry : entrySet) {
				String id = entry.getKey();
				FactionTimerCooldown runnable = entry.getValue();
				pauseSavemap.put(id, runnable.getPauseMillis());
				cooldownSavemap.put(id, runnable.getExpiryMillis());
			}

			config.set("timer-pauses." + this.name, pauseSavemap);
			config.set("timer-cooldowns." + this.name, cooldownSavemap);
		}
	}
}