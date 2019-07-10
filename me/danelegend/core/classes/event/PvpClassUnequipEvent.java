package me.danelegend.core.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import me.danelegend.core.classes.PvpClass;

public class PvpClassUnequipEvent extends PlayerEvent {
	private static final HandlerList handlers;
	private final PvpClass pvpClass;

	static {
		handlers = new HandlerList();
	}

	public PvpClassUnequipEvent(final Player player, final PvpClass pvpClass) {
		super(player);
		this.pvpClass = pvpClass;
	}

	public PvpClass getPvpClass() {
		return this.pvpClass;
	}

	public static HandlerList getHandlerList() {
		return PvpClassUnequipEvent.handlers;
	}

	public HandlerList getHandlers() {
		return PvpClassUnequipEvent.handlers;
	}
}