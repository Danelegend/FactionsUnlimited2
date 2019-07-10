package me.danelegend.core.timer.factionevent;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.google.common.base.Optional;
import com.massivecraft.factions.Faction;

import me.danelegend.core.timer.Timer;

/**
 * Event called when a {@link Timer} starts.
 */
public class FactionTimerStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Optional<Faction> faction;
	private final Optional<String> factionId;
	private final Timer timer;
	private final long duration;

	public FactionTimerStartEvent(Timer timer, final long duration) {
		this.faction = Optional.absent();
		this.factionId = Optional.absent();
		this.timer = timer;
		this.duration = duration;
	}

	public FactionTimerStartEvent(@Nullable Faction faction, String factionId, Timer timer, long duration) {
		this.faction = Optional.fromNullable(faction);
		this.factionId = Optional.fromNullable(factionId);
		this.timer = timer;
		this.duration = duration;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Optional<Faction> getFaction() {
		return faction;
	}

	/**
	 * Gets the optional UUID of the user this has expired for.
	 * <p>
	 * <p>
	 * This may return absent if the timer is not of a player type
	 *
	 * @return the expiring user UUID or {@link Optional#absent()}
	 */
	public Optional<String> getFactionId() {
		return factionId;
	}

	/**
	 * Gets the {@link Timer} that was expired.
	 *
	 * @return the expiring timer
	 */
	public Timer getTimer() {
		return timer;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}