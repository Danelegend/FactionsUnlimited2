package me.danelegend.core.timer.factionevent;

import com.google.common.base.Optional;

import me.danelegend.core.timer.Timer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a {@link Timer} is removed.
 */
public class FactionTimerClearEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Optional<String> factionId;
	private final Timer timer;

	public FactionTimerClearEvent(Timer timer) {
		this.factionId = Optional.absent();
		this.timer = timer;
	}

	public FactionTimerClearEvent(String factionId, Timer timer) {
		this.factionId = Optional.of(factionId);
		this.timer = timer;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the optional UUID of the user this was removed for.
	 * <p>
	 * <p>
	 * This may return absent if the timer is not of a player type
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

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}