package me.danelegend.core.timer.factionevent;

import javax.annotation.Nullable;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.google.common.base.Optional;
import com.massivecraft.factions.Faction;

import me.danelegend.core.timer.Timer;

/**
 * Event called when a {@link Timer} is extended.
 */
public class FactionTimerExtendEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final Optional<Faction> faction;
	private final Optional<String> factionId;
	private final Timer timer;
	private final long previousDuration;
	private boolean cancelled;
	private long newDuration;

	public FactionTimerExtendEvent(Timer timer, long previousDuration, long newDuration) {
		this.faction = Optional.absent();
		this.factionId = Optional.absent();
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}

	public FactionTimerExtendEvent(@Nullable Faction faction, String factionId, Timer timer, long previousDuration, long newDuration) {
		this.faction = Optional.fromNullable(faction);
		this.factionId = Optional.fromNullable(factionId);
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Optional<Faction> getFaction() {
		return faction;
	}

	/**
	 * Gets the optional UUID of the user this was removed for.
	 * <p>
	 * <p>This may return absent if the timer is not of a player type
	 *
	 * @return the removed user UUID or {@link Optional#absent()}
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

	public long getPreviousDuration() {
		return previousDuration;
	}

	public long getNewDuration() {
		return newDuration;
	}

	public void setNewDuration(long newDuration) {
		this.newDuration = newDuration;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
