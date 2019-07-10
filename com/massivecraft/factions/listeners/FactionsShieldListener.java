package com.massivecraft.factions.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class FactionsShieldListener implements Listener {

	@EventHandler
	public void onEntityExplode(ExplosionPrimeEvent e) {
		if (e.getEntityType() != EntityType.PRIMED_TNT) {
			return;
		}

		Faction factionAt = Board.getInstance().getFactionAt(new FLocation(e.getEntity().getLocation()));

		if (!factionAt.hasShield()) {
			return;
		}

		TNTPrimed tnt = (TNTPrimed) e.getEntity();
		Faction source = Board.getInstance().getFactionAt(new FLocation(tnt.getSourceLoc()));

		if (source != factionAt) {
			e.setCancelled(true);
		}
	}
}