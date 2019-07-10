package me.danelegend.core.classes.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.massivecraft.factions.P;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.danelegend.core.classes.PvpClass;

public class RogueClass extends PvpClass implements Listener {

	private final P plugin;
	public static TObjectLongMap<UUID> rogueSpeedCooldowns = new TObjectLongHashMap<UUID>();
	private TObjectLongMap<UUID> rogueJumpCooldowns = new TObjectLongHashMap<UUID>();
	private static PotionEffect ROGUE_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
	private static PotionEffect ROGUE_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 5);
	private static long ROGUE_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
	private static long ROGUE_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);

	public RogueClass(P plugin) {
		super("Rogue", TimeUnit.SECONDS.toMillis(5L));

		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if (entity instanceof Player && damager instanceof Player) {
			Player attacker = (Player) damager;
			Player player = (Player) entity;
			if (plugin.getPvpClassManager().getEquippedClass(attacker) == this) {
				final Location lo = player.getEyeLocation();
				Location o = attacker.getEyeLocation();
				Vector c = lo.toVector().subtract(o.toVector());
				Vector d = player.getEyeLocation().getDirection();
				double delta = c.dot(d);
				if (delta > 0) {
					ItemStack stack = attacker.getItemInHand();
					if (stack != null && stack.getType() == Material.GOLD_SWORD && stack.getEnchantments().isEmpty()) {
						player.sendMessage(
								ChatColor.RED + attacker.getName() + ChatColor.YELLOW + " has backstabbed you.");
						player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);

						attacker.sendMessage(ChatColor.YELLOW + "You have backstabbed " + ChatColor.RED
								+ player.getName() + ChatColor.YELLOW + '.');
						attacker.setItemInHand(new ItemStack(Material.AIR, 1));
						attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);

						if (player.getHealth() - 6 >= 0) {
							player.setHealth(player.getHealth() - 6);
						} else {
							player.setHealth(0);
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onArcherSpeedClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem())
				&& (event.getItem().getType() == Material.SUGAR)) {
			if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
				return;
			}
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			long timestamp = rogueSpeedCooldowns.get(uuid);
			long millis = System.currentTimeMillis();
			long remaining = timestamp == rogueSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
			if (remaining > 0L) {
				player.sendMessage(ChatColor.RED + "Cannot use Speed Boost for another "
						+ DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
			} else {
				ItemStack stack = player.getItemInHand();
				if (stack.getAmount() == 1) {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				} else {
					stack.setAmount(stack.getAmount() - 1);
				}
				player.sendMessage(ChatColor.GREEN + "Speed 5 activated for 7 seconds.");

				this.plugin.getEffectRestorer().setRestoreEffect(player, ROGUE_SPEED_EFFECT);

				Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
					public void run() {
						PotionEffect previous = (PotionEffect) RogueClass.this.plugin.getEffectRestorer().restores
								.remove(player.getUniqueId(), ROGUE_SPEED_EFFECT.getType());
						if (previous != null) {
							player.addPotionEffect(previous, true);
						}
					}
				}, 121L);
				rogueSpeedCooldowns.put(event.getPlayer().getUniqueId(),
						System.currentTimeMillis() + ROGUE_SPEED_COOLDOWN_DELAY);
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onArcherJumpClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem())
				&& (event.getItem().getType() == Material.FEATHER)) {
			if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
				return;
			}
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			long timestamp = this.rogueJumpCooldowns.get(uuid);
			long millis = System.currentTimeMillis();
			long remaining = timestamp == this.rogueJumpCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
			if (remaining > 0L) {
				player.sendMessage(ChatColor.RED + "Cannot use Jump Boost for another "
						+ DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
			} else {
				ItemStack stack = player.getItemInHand();
				if (stack.getAmount() == 1) {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				} else {
					stack.setAmount(stack.getAmount() - 1);
				}
				player.sendMessage(ChatColor.GREEN + "Jump Boost 4 activated for 7 seconds.");

				this.plugin.getEffectRestorer().setRestoreEffect(player, ROGUE_JUMP_EFFECT);

				Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
					public void run() {
						PotionEffect previous = (PotionEffect) RogueClass.this.plugin.getEffectRestorer().restores
								.remove(player.getUniqueId(), ROGUE_JUMP_EFFECT.getType());
						if (previous != null) {
							player.addPotionEffect(previous, true);
						}
					}
				}, 121L);
				this.rogueJumpCooldowns.put(event.getPlayer().getUniqueId(),
						System.currentTimeMillis() + ROGUE_JUMP_COOLDOWN_DELAY);
			}
		}
	}

	@Override
	public boolean isApplicableFor(Player player) {
		PlayerInventory playerInventory = player.getInventory();

		ItemStack helmet = playerInventory.getHelmet();
		if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
			return false;
		}

		ItemStack chestplate = playerInventory.getChestplate();
		if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
			return false;
		}

		ItemStack leggings = playerInventory.getLeggings();
		if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
			return false;
		}

		ItemStack boots = playerInventory.getBoots();
		return !(boots == null || boots.getType() != Material.CHAINMAIL_BOOTS);
	}
}