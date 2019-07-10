package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction is raided.
 */
public class FactionRaidEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Faction faction;
	private Faction target;
	private int points;

	public FactionRaidEvent(Faction faction, Faction target, int points) {
		this.faction = faction;
		this.target = target;
		this.points = points;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Faction getWhoRaided() {
		return this.faction;
	}

	public Faction getRaidedFaction() {
		return this.target;
	}

	public int getPoints() {
		return this.points;
	}
}