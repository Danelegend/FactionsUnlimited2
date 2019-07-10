package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRaidEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.util.CoreUtil;

public class FactionsRaidListener implements Listener {

	private String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Faction faction = FPlayers.getInstance().getByPlayer(p).getFaction();

		if (!faction.isWilderness() && !faction.isPlaced()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					p.sendMessage("");
					p.sendMessage(translate("&c&lWARNING..."));
					p.sendMessage(translate(" &e* &6Spawners will not be functional in any of your claims"));
					p.sendMessage(translate(" &e* &6Crops will not grow in any of your claims"));
					p.sendMessage(translate(" &e* &6You will be unable to access your faction core features"));
					p.sendMessage(translate("&cPlace down your core to &aactivate&c the above features!"));
				}
			}.runTaskLater(P.p, 2L);
		}
	}

	@EventHandler
	public void onCropGrowth(BlockGrowEvent event) {
		Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

		if (faction.isWilderness()) {
			return;
		}

		if (!faction.isPlaced()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {

		if (event.getSpawnReason() != SpawnReason.SPAWNER) {
			return;
		}

		Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getEntity().getLocation()));

		if (faction.isWilderness()) {
			return;
		}

		if (!faction.isPlaced()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSpawnerBreak(BlockBreakEvent event) {
		if (event.getBlock() == null || event.getBlock().getType() != Material.MOB_SPAWNER) {
			return;
		}

		Faction fac = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

		if (fac.isWilderness())
			return;

		if (P.p.getTimerManager().getTNTTimer().getRemaining(fac) > 0
				&& event.getBlock().getType() == Material.MOB_SPAWNER) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can not mine spawners while you are being raided!");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCrystalPrime(ExplosionPrimeEvent e) {
		if (e.getEntityType() == EntityType.ENDER_CRYSTAL) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCrystalDamage(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
			if (event.getDamager().getType() == EntityType.PRIMED_TNT) {
				Faction raidedFaction = Board.getInstance()
						.getFactionAt(new FLocation(event.getEntity().getLocation()));
				TNTPrimed tnt = (TNTPrimed) event.getDamager();
				Faction whoRaided = Board.getInstance().getFactionAt(new FLocation(tnt.getSourceLoc()));

				if (whoRaided == raidedFaction) {
					event.setCancelled(true);
					return;
				}

				raidedFaction.setDurability(raidedFaction.getDurability() - 1);
				String damaged = "&7(&4!&7) &aYour &nfaction core&a has been damaged and is now at &c"
						+ raidedFaction.getDurability() + "&c/" + "&c" + raidedFaction.getMaxDurability();
				if (raidedFaction.getDurability() > 0) {
					event.setCancelled(true);
					whoRaided.sendMessage("");
					whoRaided.sendMessage(
							translate("&aYou have successfully damaged the enemy faction core, their core is now at "
									+ raidedFaction.getDurability() + " durability!"));
					whoRaided.sendMessage("");
					raidedFaction.sendMessage("");
					raidedFaction.sendMessage(translate("&e** &4&lYOU ARE BEING RAIDED &e**"));
					raidedFaction.sendMessage(translate(damaged));
					raidedFaction.sendMessage("");
				} else {
					Bukkit.getPluginManager().callEvent(
							new FactionRaidEvent(whoRaided, raidedFaction, ((int) (raidedFaction.getPoints() * 0.30))));
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFactionRaid(FactionRaidEvent event) {
		event.getWhoRaided().setRaidingPoints(event.getWhoRaided().getRaidingPoints() + event.getPoints());
		event.getWhoRaided().setTokens(event.getWhoRaided().getTokens() + (event.getPoints() * 5));

		for (Entity entity : Bukkit.getWorld(event.getRaidedFaction().getCoreLocation().getWorldName())
				.getNearbyEntities(event.getRaidedFaction().getCoreLocation().getLocation(), 5, 5, 5)) {
			if (entity.getType() == EntityType.ENDER_CRYSTAL) {
				entity.remove();
			}
		}

		event.getRaidedFaction().setPlaced(false);
		event.getRaidedFaction().setRaidable(true);
		event.getRaidedFaction().setDurability(event.getRaidedFaction().getMaxDurability());

		event.getRaidedFaction().setLastRaider(event.getWhoRaided().getTag());
		
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(translate("&e** &d&lA FACTION HAS BEEN RAIDED &e**"));
		Bukkit.broadcastMessage(translate("&7(&4!&7) &c") + event.getRaidedFaction().getTag()
				+ translate(" &7has been raided by &b" + event.getWhoRaided().getTag()));
		Bukkit.broadcastMessage(ChatColor.AQUA + event.getWhoRaided().getTag()
				+ translate(" &7has received &a" + event.getPoints() + " &7points and &6" + event.getPoints() * 5)
				+ translate(" &7tokens for &n&ccompleting the raid"));
		Bukkit.broadcastMessage("");

		for (Player player : event.getRaidedFaction().getOnlinePlayers()) {
			player.getWorld().playSound(player.getLocation(), Sound.WITHER_DEATH, 15F, 15F);
		}
		
		Bukkit.getScheduler().runTaskLater(P.p, () -> {
			event.getRaidedFaction().setLastRaider("");
			event.getWhoRaided().sendMessage(ChatColor.RED + "You may no longer mine " + event.getRaidedFaction().getTag() + "'s spawners!");
		}, 6000L);
	}

	@EventHandler
	public void onCrystalInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().getType() == EntityType.ENDER_CRYSTAL) {
			Faction fac = Board.getInstance().getFactionAt(new FLocation(e.getRightClicked().getLocation()));

			if (fac == FPlayers.getInstance().getByPlayer(e.getPlayer()).getFaction()) {
				e.getPlayer().openInventory(
						CoreUtil.coreGUI(FPlayers.getInstance().getByPlayer(e.getPlayer()).getFaction()));
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "This core does not seem familiar...");
			}
		}
	}

	@EventHandler
	public void onFactionDisband(FactionDisbandEvent e) {
		if (e.getFaction().isPlaced()) {
			Faction fac = e.getFaction();
			for (Entity entity : Bukkit.getWorld(fac.getCoreLocation().getWorldName())
					.getNearbyEntities(fac.getCoreLocation().getLocation(), 5, 5, 5)) {
				if (entity.getType() == EntityType.ENDER_CRYSTAL) {
					entity.remove();
					e.getFaction().setPlaced(false);
					e.getFaction().setCoreLocation(null);
				}
			}
		}
	}

	@EventHandler
	public void onUnclaim(LandUnclaimEvent e) {
		if (e.getFaction().isPlaced()) {
			Faction fac = e.getFaction();
			if (e.getLocation().isInChunk(fac.getCoreLocation().getLocation()))
				for (Entity entity : Bukkit.getWorld(fac.getCoreLocation().getWorldName())
						.getNearbyEntities(fac.getCoreLocation().getLocation(), 5, 5, 5)) {
					if (entity.getType() == EntityType.ENDER_CRYSTAL) {
						entity.remove();
						e.getFaction().setPlaced(false);
						e.getFaction().setCoreLocation(null);
					}
				}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) {
			return;
		}

		if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()
				|| !event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Faction Core")) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();

		if (faction.isPlaced()) {
			player.sendMessage(ChatColor.RED + "Your core is already placed.");
			return;
		}

		if (Board.getInstance().getFactionAt(new FLocation(event.getClickedBlock())) != faction) {
			player.sendMessage(ChatColor.RED + "You can only place a core in your own land.");
			return;
		}

		if (block.getY() < 40 || block.getY() > 200) {
			player.sendMessage(ChatColor.RED + "You can only place a core between y level 40 and 200.");
			return;
		}

		faction.setPlaced(true);
		faction.setCoreLocation(new LazyLocation(block.getLocation().add(0.5, 1, 0.5)));
		player.getInventory().remove(CoreUtil.getCoreItem());
		block.getWorld().spawnEntity(block.getLocation().add(0.5, 1, 0.5), EntityType.ENDER_CRYSTAL);
	}
}
