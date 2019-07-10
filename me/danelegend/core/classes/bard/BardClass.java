package me.danelegend.core.classes.bard;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.danelegend.core.classes.PvpClass;
import me.danelegend.core.util.time.DurationFormatter;

@SuppressWarnings("unused")
public class BardClass extends PvpClass implements Listener {

	public static final int HELD_EFFECT_DURATION_TICKS = 100;
	private static final long BUFF_COOLDOWN_MILLIS;
	private static final int TEAMMATE_NEARBY_RADIUS = 25;
	private static final long HELD_REAPPLY_TICKS = 20L;
	private final Map<UUID, BardData> bardDataMap;
	private final Map<Material, BardEffect> bardEffects;
	private final P plugin;
	private final TObjectLongMap<UUID> msgCooldowns;
	private static final String MARK;

	static {
		BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(10L);
		MARK = ChatColor.STRIKETHROUGH + "-------";
	}

	public BardClass(final P plugin) {
		super("Bard", TimeUnit.SECONDS.toMillis(5L));
		this.bardDataMap = new HashMap<UUID, BardData>();
		this.bardEffects = new EnumMap<Material, BardEffect>(Material.class);
		this.msgCooldowns = new TObjectLongHashMap<UUID>();
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		this.bardEffects.put(Material.SUGAR, new BardEffect(25, new PotionEffect(PotionEffectType.SPEED, 120, 2),
				new PotionEffect(PotionEffectType.SPEED, 120, 1)));
		this.bardEffects.put(Material.BLAZE_POWDER,
				new BardEffect(45, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 2),
						new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1)));
		this.bardEffects.put(Material.IRON_INGOT,
				new BardEffect(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 1),
						new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0)));
		this.bardEffects.put(Material.GHAST_TEAR,
				new BardEffect(30, new PotionEffect(PotionEffectType.REGENERATION, 60, 2),
						new PotionEffect(PotionEffectType.REGENERATION, 120, 0)));
		this.bardEffects.put(Material.FEATHER, new BardEffect(35, new PotionEffect(PotionEffectType.JUMP, 120, 5),
				new PotionEffect(PotionEffectType.JUMP, 120, 1)));
		this.bardEffects.put(Material.SPIDER_EYE,
				new BardEffect(45, new PotionEffect(PotionEffectType.WITHER, 120, 1), null));
		this.bardEffects.put(Material.MAGMA_CREAM,
				new BardEffect(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 1),
						new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
	}

	@Override
	public boolean onEquip(final Player player) {

		if (!super.onEquip(player)) {
			return false;
		}
		final BardData bardData = new BardData();
		this.bardDataMap.put(player.getUniqueId(), bardData);
		bardData.startEnergyTracking();

		bardData.heldTask = new BukkitRunnable() {
			int lastEnergy;

			public void run() {

				if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
				}

				if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
				}

				if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
				}

				final ItemStack held = player.getItemInHand();
				if (held != null) {
					final BardEffect bardEffect = BardClass.this.bardEffects.get(held.getType());
					if (bardEffect == null || bardEffect.heldable == null) {
						return;
					}
					if (bardEffect.heldable.getType() == PotionEffectType.JUMP) {
						plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.heldable);
					}
					if (!Board.getInstance().getFactionAt(new FLocation(player)).isSafeZone()) {
						final Faction playerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
						if (playerFaction != null) {
							final Collection<Entity> nearbyEntities = (Collection<Entity>) player
									.getNearbyEntities(25.0, 25.0, 25.0);
							for (final Entity nearby : nearbyEntities) {
								if (nearby instanceof Player && !player.equals(nearby)) {
									final Player target = (Player) nearby;
									if (!playerFaction.getOnlinePlayers().contains(target)) {
										continue;
									}
									plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.heldable);
								}
							}
						}
					}
				}
				final int energy = (int) BardClass.this.getEnergy(player);
				if (energy != 0 && energy != this.lastEnergy
						&& (energy % 10 == 0 || this.lastEnergy - energy - 1 > 0 || energy == 100.0)) {
					this.lastEnergy = energy;
					player.sendMessage(ChatColor.GOLD + BardClass.this.name + ChatColor.YELLOW + " energy is now at "
							+ ChatColor.RED + energy + ChatColor.YELLOW + '.');
				}
			}
		}.runTaskTimer(this.plugin, 0L, 20L);
		return true;
	}

	@Override
	public void onUnequip(final Player player) {
		super.onUnequip(player);
		this.clearBardData(player.getUniqueId());
	}

	private void clearBardData(final UUID uuid) {
		final BardData bardData = this.bardDataMap.remove(uuid);
		if (bardData != null && bardData.getHeldTask() != null) {
			bardData.getHeldTask().cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		this.clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(final PlayerKickEvent event) {
		this.clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onItemHeld(final PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();
		final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
		if (equipped == null || !equipped.equals(this)) {
			return;
		}
		final UUID uuid = player.getUniqueId();
		final long lastMessage = this.msgCooldowns.get(uuid);
		final long millis = System.currentTimeMillis();
		if (lastMessage != this.msgCooldowns.getNoEntryValue() && lastMessage - millis > 0L) {
			return;
		}
		final ItemStack newStack = player.getInventory().getItem(event.getNewSlot());
		if (newStack != null) {
			final BardEffect bardEffect = this.bardEffects.get(newStack.getType());
			if (bardEffect != null) {
				this.msgCooldowns.put(uuid, millis + 1500L);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		Action action = event.getAction();
		if ((action == Action.RIGHT_CLICK_AIR) || ((!event.isCancelled()) && (action == Action.RIGHT_CLICK_BLOCK))) {
			ItemStack stack = event.getItem();
			final BardEffect bardEffect = (BardEffect) this.bardEffects.get(stack.getType());
			if ((bardEffect == null) || (bardEffect.clickable == null)) {
				return;
			}
			event.setUseItemInHand(Event.Result.DENY);
			final Player player = event.getPlayer();
			BardData bardData = (BardData) this.bardDataMap.get(player.getUniqueId());
			if (bardData != null) {
				if (!canUseBardEffect(player, bardData, bardEffect, true)) {
					return;
				}
				if (stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() - 1);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}
				if (bardEffect != null) {
					Faction playerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
					if ((playerFaction != null) && (!bardEffect.clickable.getType().equals(PotionEffectType.WITHER))) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
						for (Entity nearby : nearbyEntities) {
							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;
								if (playerFaction.getOnlinePlayers().contains(target)) {
									this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
								}
							}
						}
					} else if ((playerFaction != null)
							&& (bardEffect.clickable.getType().equals(PotionEffectType.WITHER))) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
						for (Entity nearby : nearbyEntities) {
							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;
								if (!playerFaction.getOnlinePlayers().contains(target)) {
									this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
								}
							}
						}
					} else if (bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
						for (Entity nearby : nearbyEntities) {
							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;
								this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
							}
						}
					}
				}
				if (bardEffect.clickable.getType() == PotionEffectType.INCREASE_DAMAGE
						|| bardEffect.clickable.getType() == PotionEffectType.SPEED
						|| bardEffect.clickable.getType() == PotionEffectType.JUMP) {
					this.plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);

					Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
						public void run() {
							PotionEffect previous = (PotionEffect) BardClass.this.plugin.getEffectRestorer().restores
									.remove(player.getUniqueId(), bardEffect.clickable.getType());
							if (previous != null) {
								player.addPotionEffect(previous, true);
							}
						}
					}, 121L);
				} else if (bardEffect.clickable.getType() == PotionEffectType.REGENERATION
						|| bardEffect.clickable.getType() == PotionEffectType.DAMAGE_RESISTANCE) {
					this.plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);

					Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
						public void run() {
							PotionEffect previous = (PotionEffect) BardClass.this.plugin.getEffectRestorer().restores
									.remove(player.getUniqueId(), bardEffect.clickable.getType());
							if (previous != null) {
								player.addPotionEffect(previous, true);
							}
						}
					}, 61L);
				} else if (bardEffect.clickable.getType() == PotionEffectType.FIRE_RESISTANCE) {
					this.plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);

					Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
						public void run() {
							PotionEffect previous = (PotionEffect) BardClass.this.plugin.getEffectRestorer().restores
									.remove(player.getUniqueId(), bardEffect.clickable.getType());
							if (previous != null) {
								player.addPotionEffect(previous, true);
							}
						}
					}, 901L);
				} else {
					this.plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);
				}
				bardData.setBuffCooldown(BUFF_COOLDOWN_MILLIS);
				double newEnergy = setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
				player.sendMessage(ChatColor.YELLOW + "You have just used " + this.name + " buff " + ChatColor.RED
						+ fromPotionEffectType(bardEffect.clickable.getType()) + ' '
						+ (bardEffect.clickable.getAmplifier() + 1) + ChatColor.YELLOW + " costing you "
						+ ChatColor.GOLD + bardEffect.energyCost + ChatColor.YELLOW + " energy. "
						+ "Your Energy is now " + ChatColor.GREEN + newEnergy * 10.0D / 10.0D + ChatColor.YELLOW + '.');
			}
		}
	}

	private boolean canUseBardEffect(final Player player, final BardData bardData, final BardEffect bardEffect,
			final boolean sendFeedback) {
		String errorFeedback = null;
		final double currentEnergy = bardData.getEnergy();
		if (bardEffect.energyCost > currentEnergy) {
			errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + bardEffect.energyCost
					+ ChatColor.RED + " energy to use this Bard buff, whilst you only have " + ChatColor.BOLD
					+ currentEnergy + ChatColor.RED + '.';
		}
		final long remaining = bardData.getRemainingBuffDelay();
		if (remaining > 0L) {
			errorFeedback = ChatColor.RED + "You cannot use this bard buff for another " + ChatColor.BOLD
					+ DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + '.';
		}
		final Faction factionAt = Board.getInstance().getFactionAt(new FLocation(player));
		if (factionAt.isSafeZone()) {
			errorFeedback = ChatColor.RED + "You may not use bard buffs in safe-zones.";
		}
		if (sendFeedback && errorFeedback != null) {
			player.sendMessage(errorFeedback);
		}
		return errorFeedback == null;
	}

	@Override
	public boolean isApplicableFor(final Player player) {
		final ItemStack helmet = player.getInventory().getHelmet();
		if (helmet == null || helmet.getType() != Material.GOLD_HELMET) {
			return false;
		}
		final ItemStack chestplate = player.getInventory().getChestplate();
		if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) {
			return false;
		}
		final ItemStack leggings = player.getInventory().getLeggings();
		if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) {
			return false;
		}
		final ItemStack boots = player.getInventory().getBoots();
		return boots != null && boots.getType() == Material.GOLD_BOOTS;
	}

	public long getRemainingBuffDelay(final Player player) {
		synchronized (this.bardDataMap) {
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			// monitorexit(this.bardDataMap)
			return (bardData == null) ? 0L : bardData.getRemainingBuffDelay();
		}
	}

	public double getEnergy(final Player player) {
		synchronized (this.bardDataMap) {
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			// monitorexit(this.bardDataMap)
			return (bardData == null) ? 0.0 : bardData.getEnergy();
		}
	}

	public long getEnergyMillis(final Player player) {
		synchronized (this.bardDataMap) {
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			// monitorexit(this.bardDataMap)
			return (bardData == null) ? 0L : bardData.getEnergyMillis();
		}
	}

	public double setEnergy(final Player player, final double energy) {
		final BardData bardData = this.bardDataMap.get(player.getUniqueId());
		if (bardData == null) {
			return 0.0;
		}
		bardData.setEnergy(energy);
		return bardData.getEnergy();
	}

	public static String fromPotionEffectType(PotionEffectType effectType) {
		switch (effectType.getName()) {

		case "INCREASE_DAMAGE":
			return "Strength";
		case "DAMAGE_RESISTANCE":
			return "Resistance";
		case "FIRE_RESISTANCE":
			return "Fire Resistance";
		case "JUMP":
			return "Jump Boost";
		case "REGENERATION":
			return "Regeneration";
		case "SATURATION":
			return "Saturation";
		case "SPEED":
			return "Speed";
		case "WITHER":
			return "Wither";
		}
		return null;
	}
}