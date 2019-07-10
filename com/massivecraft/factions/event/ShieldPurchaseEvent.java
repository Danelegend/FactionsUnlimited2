package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction purchases a shield
 */
public class ShieldPurchaseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Faction faction;
	private long duration;

	public ShieldPurchaseEvent(Faction faction, int time) {
		this.faction = faction;
		this.duration = TimeUnit.HOURS.toMillis(time);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Faction getFaction() {
		return this.faction;
	}

	public long getDuration() {
		return this.duration;
	}
}