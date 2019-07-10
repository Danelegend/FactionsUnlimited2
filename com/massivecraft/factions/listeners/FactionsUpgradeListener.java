package com.massivecraft.factions.listeners;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.util.player.UserManager;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class FactionsUpgradeListener implements Listener {

	private Random r = new Random();

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			Faction factionAt = Board.getInstance().getFactionAt(new FLocation(e.getEntity().getLocation()));

			if (factionAt.getUpgrades().getMobDrops() > 0) {

				if (factionAt.getUpgrades().getMobDrops() == 1) {
					int chance = r.nextInt(99) + 1;
					if (chance <= 50)
						e.getDrops().addAll(e.getDrops());
				}

				if (factionAt.getUpgrades().getMobDrops() == 2) {
					int chance = r.nextInt(99) + 1;
					if (chance <= 75)
						e.getDrops().addAll(e.getDrops());
				}

				if (factionAt.getUpgrades().getMobDrops() == 3) {
					e.getDrops().addAll(e.getDrops());
				}
			}
		}
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		ItemStack item = e.getEntity().getItemStack();

		Faction faction = Board.getInstance().getFactionAt(new FLocation(e.getLocation()));

		if (item.getType() == Material.CACTUS || item.getType() == Material.SUGAR_CANE
				|| item.getType() == Material.PUMPKIN || item.getType() == Material.MELON

				|| item.getType() == Material.COCOA || item.getType() == Material.WHEAT) {

			if (faction.getUpgrades().getCropsLevel() > 0) {

				if (faction.getUpgrades().getMobDrops() == 1) {
					int chance = r.nextInt(99) + 1;
					if (chance <= 50)
						item.setAmount(item.getAmount() * 2);
				}

				if (faction.getUpgrades().getMobDrops() == 2) {
					int chance = r.nextInt(99) + 1;
					if (chance <= 75)
						item.setAmount(item.getAmount() * 2);
				}

				if (faction.getUpgrades().getMobDrops() == 3) {
					item.setAmount(item.getAmount() * 2);
				}
			}
		}
	}

	@EventHandler
	public void onMcMMOXPGain(McMMOPlayerXpGainEvent e) {
		Faction faction = FPlayers.getInstance().getByPlayer(e.getPlayer()).getFaction();

		if (faction.getUpgrades().getmcMMOLevel() > 0) {
			if (faction.getUpgrades().getmcMMOLevel() == 1) {
				e.setRawXpGained((float) (e.getRawXpGained() * 1.25));
			}

			if (faction.getUpgrades().getmcMMOLevel() == 2) {
				e.setRawXpGained((float) (e.getRawXpGained() * 1.50));
			}

			if (faction.getUpgrades().getmcMMOLevel() == 3) {
				e.setRawXpGained(e.getRawXpGained() * 2);
			}
		}
	}

	@EventHandler
	public void onMcMMOAbility(McMMOPlayerAbilityDeactivateEvent e) {
		Player p = e.getPlayer();
		Faction faction = FPlayers.getInstance().getByPlayer(p).getFaction();

		if (faction.getUpgrades().getShorterCooldowns() > 0) {
			if (faction.getUpgrades().getShorterCooldowns() == 1) {
				UserManager.getPlayer(p).setAbilityDATS(e.getAbility(), e.getAbility().getCooldown() - 4);
			}

			if (faction.getUpgrades().getShorterCooldowns() == 2) {
				UserManager.getPlayer(p).setAbilityDATS(e.getAbility(), e.getAbility().getCooldown() - 8);
			}

			if (faction.getUpgrades().getShorterCooldowns() == 3) {
				UserManager.getPlayer(p).setAbilityDATS(e.getAbility(), e.getAbility().getCooldown() - 12);
			}
		}
	}
}