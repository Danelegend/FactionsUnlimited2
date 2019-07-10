package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.Upgrade;
import com.massivecraft.factions.event.ShieldPurchaseEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.NumberUtil;
import com.massivecraft.factions.util.UpgradeUtil;
import com.massivecraft.factions.zcore.util.CoreUtil;

public class FactionsGUIListener implements Listener {

	private String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null
				|| !e.getClickedInventory().getTitle().equals(ChatColor.AQUA + "Faction Core")) {
			return;
		}

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		Player p = (Player) e.getWhoClicked();
		Faction fac = FPlayers.getInstance().getByPlayer(p).getFaction();

		Material type = e.getCurrentItem().getType();

		if (type == Material.ENDER_CHEST) {
			p.closeInventory();
			p.openInventory(CoreUtil.experienceVault(fac, p));
		}

		/*if (type == Material.GOLD_INGOT) {
			if (!FPlayers.getInstance().getByPlayer(p).getRole().isAtLeast(Role.MODERATOR)) {
				p.sendMessage(ChatColor.RED + "You must be a moderator to open the faction shop");
				return;
			}
			p.closeInventory();
			p.openInventory(CoreUtil.factionShop(fac));
		} */

		if (type == Material.CHEST) {
			if (fac.getUpgrades().getFactionVault() == 1) {
				p.chat("/f vault 1");
			} else {
				p.sendMessage(ChatColor.RED + "You must upgrade your faction vault to Tier I to use this");
			}
		}

		if (type == Material.DIAMOND_BLOCK) {
			p.closeInventory();
			p.openInventory(CoreUtil.upgradeGUI(fac));
		}

		if (type == Material.TNT) {
			p.closeInventory();
			p.openInventory(CoreUtil.shieldShop(fac));
		}

		if (type == Material.SKULL_ITEM) {

			p.closeInventory();

			if ((P.p.getTimerManager().getTNTTimer().getRemaining(fac) > 0)) {
				p.sendMessage(ChatColor.RED + "You cannot remove your core while your TNT timer is active.");
				return;
			}

			for (Entity entity : Bukkit.getWorld(fac.getCoreLocation().getWorldName())
					.getNearbyEntities(fac.getCoreLocation().getLocation(), 5, 5, 5)) {
				if (entity.getType() == EntityType.ENDER_CRYSTAL) {
					entity.remove();
					p.sendMessage("");
					p.sendMessage(translate("&c&lWARNING..."));
					p.sendMessage(translate(" &e* &6Spawners will not be functional in any of your claims"));
					p.sendMessage(translate(" &e* &6Crops will not grow in any of your claims"));
					p.sendMessage(translate(" &e* &6You will be unable to access your faction core features"));
					p.sendMessage(translate("&cPlace down your core to &aactivate&c the above features!"));
					fac.setPlaced(false);
					fac.setCoreLocation(null);
				}
			}
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClickShields(InventoryClickEvent e) {
		if (e.getClickedInventory() == null
				|| !e.getClickedInventory().getTitle().equals(ChatColor.RED + "Faction Shields")) {
			return;
		}

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		Player p = (Player) e.getWhoClicked();
		Faction fac = FPlayers.getInstance().getByPlayer(p).getFaction();

		if (P.p.getTimerManager().getTNTTimer().getRemaining(fac) > 0 || fac.hasShield()) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You are unable to buy a shield right now. Please try again later.");
			return;
		}

		ItemStack stack = e.getCurrentItem();
		Material type = stack.getType();

		if (type == Material.IRON_BLOCK) {
			if (fac.getTokens() >= 500)
				Bukkit.getPluginManager().callEvent(new ShieldPurchaseEvent(fac, 6));
		} else if (type == Material.GOLD_BLOCK) {
			if (fac.getTokens() >= 1000)
				Bukkit.getPluginManager().callEvent(new ShieldPurchaseEvent(fac, 12));
		} else if (type == Material.DIAMOND_BLOCK) {
			if (fac.getTokens() >= 1500)
				Bukkit.getPluginManager().callEvent(new ShieldPurchaseEvent(fac, 18));
		} else if (type == Material.EMERALD_BLOCK) {
			if (fac.getTokens() >= 2000)
				Bukkit.getPluginManager().callEvent(new ShieldPurchaseEvent(fac, 24));
		}

		e.setCancelled(true);
	}

	/* @EventHandler
	public void onInventoryClickShop(InventoryClickEvent e) {
		if (e.getClickedInventory() == null
				|| !e.getClickedInventory().getTitle().equals(ChatColor.LIGHT_PURPLE + "Faction Shop")) {
			return;
		}

		Player p = (Player) e.getWhoClicked();

		ItemStack stack = e.getCurrentItem();
		Material type = stack.getType();

		Faction fac = FPlayers.getInstance().getByPlayer(p).getFaction();

		if (type == Material.DIAMOND_PICKAXE
				&& stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "Shockwave")) {
			if (fac.getTokens() >= 100) {
				fac.setTokens(fac.getTokens() - 100);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givetool DIAMOND_PICKAXE shockwave " + p.getName());
			}
		} else if (type == Material.DIAMOND_PICKAXE
				&& stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "Infusion")) {
			if (fac.getTokens() >= 200) {
				fac.setTokens(fac.getTokens() - 200);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givetool DIAMOND_PICKAXE infusion " + p.getName());
			}
		} else if (type == Material.DIAMOND_SPADE
				&& stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "Excavator")) {
			if (fac.getTokens() >= 50) {
				fac.setTokens(fac.getTokens() - 50);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givetool DIAMOND_SPADE infusion " + p.getName());
			}
		} else if (type == Material.DIAMOND_HOE
				&& stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "Harvester")) {
			if (fac.getTokens() >= 100) {
				fac.setTokens(fac.getTokens() - 100);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givetool DIAMOND_HOE harvester " + p.getName());
			}
		} else if (type == Material.MONSTER_EGG) {
			if (fac.getTokens() >= 10) {
				fac.setTokens(fac.getTokens() - 10);
				p.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
			}
		} else if (type == Material.BEACON) {
			if (fac.getTokens() >= 50) {
				fac.setTokens(fac.getTokens() - 50);
				p.getInventory().addItem(new ItemStack(Material.BEACON));
			}
		} else if (type == Material.MOB_SPAWNER) {
			if (fac.getTokens() >= 75) {
				fac.setTokens(fac.getTokens() - 75);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mysteryspawner " + p.getName());
			}
		} else if (type == Material.DIAMOND_HOE
				&& stack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Sell Wand")) {
			if (fac.getTokens() >= 125) {
				fac.setTokens(fac.getTokens() - 125);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sellwands give " + p.getName() + " 100");
			}
		} else if (type == Material.ENDER_CHEST) {
			if (fac.getTokens() >= 300) {
				fac.setTokens(fac.getTokens() - 300);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "voidchest " + p.getName());
			}
		} else if (type == Material.CHEST) {
			if (fac.getTokens() >= 175) {
				fac.setTokens(fac.getTokens() - 175);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "f tntchest " + p.getName());
			}
		} else if (type == Material.GOLD_CHESTPLATE) {
			if (fac.getTokens() >= 5) {
				fac.setTokens(fac.getTokens() - 5);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "voucher give Bard 1 " + p.getName());
			}
		} else if (type == Material.CHAINMAIL_CHESTPLATE) {
			if (fac.getTokens() >= 10) {
				fac.setTokens(fac.getTokens() - 10);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "voucher give Rogue 1 " + p.getName());
			}
		} else if (type == Material.LEATHER_CHESTPLATE) {
			if (fac.getTokens() >= 5) {
				fac.setTokens(fac.getTokens() - 5);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "voucher give Archer 1 " + p.getName());
			}
		}
		e.setCancelled(true);
	} */

	@EventHandler
	public void onInventoryClickExperience(InventoryClickEvent e) {
		if (e.getClickedInventory() == null
				|| !e.getClickedInventory().getTitle().equals(ChatColor.AQUA + "Experience Vault")) {
			return;
		}

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		Player p = (Player) e.getWhoClicked();
		Faction fac = FPlayers.getInstance().getByPlayer(p).getFaction();

		ItemStack stack = e.getCurrentItem();
		Material type = stack.getType();
		if (type == Material.WOOL) {
			if (stack.getDurability() == 14) {
				if (SetExpFix.getTotalExperience(p) >= 1000) {
					SetExpFix.setTotalExperience(p, SetExpFix.getTotalExperience(p) - 1000);
					fac.setTotalXP(fac.getTotalXP() + 1000);
					p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
					fac.sendMessage(ChatColor.GREEN + p.getName()
							+ translate(" &7has stored 1000 experience into the faction vault"));
				}
			} else if (stack.getDurability() == 4) {
				if (SetExpFix.getTotalExperience(p) >= 5000) {
					SetExpFix.setTotalExperience(p, SetExpFix.getTotalExperience(p) - 5000);
					fac.setTotalXP(fac.getTotalXP() + 5000);
					p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
					fac.sendMessage(ChatColor.GREEN + p.getName()
							+ translate(" &7has stored 5000 experience into the faction vault"));
				}
			} else if (stack.getDurability() == 5) {
				if (SetExpFix.getTotalExperience(p) >= 10000) {
					SetExpFix.setTotalExperience(p, SetExpFix.getTotalExperience(p) - 10000);
					fac.setTotalXP(fac.getTotalXP() + 10000);
					p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
					fac.sendMessage(ChatColor.GREEN + p.getName()
							+ translate(" &7has stored 10000 experience into the faction vault"));
				}
			}
			ItemStack vault = new ItemBuilder(Material.EXP_BOTTLE).setName(translate("&aTotal Faction Experience"))
					.setLore(translate("&7Insert experience by using one of"), translate("&7the icons below"), "",
							translate("&6&lSTORED &c" + fac.getTotalXP() + " &cexperience"),
							translate("&eYour current xp: &a" + SetExpFix.getTotalExperience(p)))
					.toItemStack();
			e.getInventory().setItem(4, vault);
		}

		e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClickUpgrade(InventoryClickEvent e) {
		if (e.getClickedInventory() == null
				|| !e.getClickedInventory().getTitle().equals(ChatColor.DARK_GREEN + "Faction Upgrades")) {
			return;
		}

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		Player p = (Player) e.getWhoClicked();
		Faction fac = FPlayers.getInstance().getByPlayer(p).getFaction();

		ItemStack item = e.getCurrentItem();

		Upgrade upgrade = fac.getUpgrades();

		if (e.getSlot() == 0) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getDurability() < 3) {
				if (getInfo(UpgradeUtil.durabilityCost(upgrade.getDurability()))[0] <= Econ
						.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.durabilityCost(upgrade.getDurability()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.durabilityCost(upgrade.getDurability()))[0],
							getParsedCosts(UpgradeUtil.durabilityCost(upgrade.getDurability()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.durabilityCost(upgrade.getDurability())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.durabilityCost(upgrade.getDurability())[1]))[1]));
					upgrade.setDurability(upgrade.getDurability() + 1);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nCore Durability&7 in the faction core!"));
					if (upgrade.getDurability() == 1) {
						fac.setDurability(fac.getDurability() + 10);
					} else if (upgrade.getDurability() == 2) {
						fac.setDurability(fac.getDurability() + 20);
					} else if (upgrade.getDurability() == 3) {
						fac.setDurability(fac.getDurability() + 30);
					}
					meta.setDisplayName(ChatColor.GOLD + "Core Durability "
							+ NumberUtil.integerToRomanNumeral(upgrade.getDurability()));
					item.setItemMeta(meta);
					item.setType(Material.DIAMOND_CHESTPLATE);
					item.setDurability((short) 0);
					e.getInventory().setItem(0, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.durabilityCost(upgrade.getDurability()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.durabilityCost(upgrade.getDurability()))[1] + " experience");
				}
			}

		} else if (e.getSlot() == 1) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getXPLevel() < 3) {
				if (getInfo(UpgradeUtil.xpBoost(upgrade.getXPLevel()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.xpBoost(upgrade.getXPLevel()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.xpBoost(upgrade.getXPLevel()))[0],
							getParsedCosts(UpgradeUtil.xpBoost(upgrade.getXPLevel()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.xpBoost(upgrade.getXPLevel())[0]))[0]);
					fac.setTokens((fac.getTokens()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.xpBoost(upgrade.getXPLevel())[1]))[1]));
					upgrade.setXPLevel(upgrade.getXPLevel() + 1);
					item.setType(Material.EXP_BOTTLE);
					meta.setDisplayName(ChatColor.GOLD + "Experience Boosts "
							+ NumberUtil.integerToRomanNumeral(upgrade.getXPLevel()));
					item.setItemMeta(meta);
					e.getInventory().setItem(2, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nExperience Multiplier&7 in the faction core!"));
				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.xpBoost(upgrade.getXPLevel()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.xpBoost(upgrade.getXPLevel()))[1] + " experience");
				}
			}

		} else if (e.getSlot() == 2) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getCropsLevel() < 3) {
				if (getInfo(UpgradeUtil.cropsCost(upgrade.getCropsLevel()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.cropsCost(upgrade.getCropsLevel()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.cropsCost(upgrade.getCropsLevel()))[0],
							getParsedCosts(UpgradeUtil.cropsCost(upgrade.getCropsLevel()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.cropsCost(upgrade.getCropsLevel())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.cropsCost(upgrade.getCropsLevel())[1]))[1]));
					upgrade.setCropsLevel(upgrade.getCropsLevel() + 1);
					item.setType(Material.SEEDS);
					meta.setDisplayName(
							ChatColor.GOLD + "Crop Drops " + NumberUtil.integerToRomanNumeral(upgrade.getCropsLevel()));
					item.setItemMeta(meta);
					e.getInventory().setItem(3, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nDouble Crop Drops&7 in the faction core!"));
				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.cropsCost(upgrade.getCropsLevel()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.cropsCost(upgrade.getCropsLevel()))[1] + " experience");
				}
			}

		} else if (e.getSlot() == 3) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getmcMMOLevel() < 3) {
				if (getInfo(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel()))[0],
							getParsedCosts(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel())[1]))[1]));
					upgrade.setmcMMOLevel(upgrade.getmcMMOLevel() + 1);
					item.setType(Material.DIAMOND_SWORD);
					item.setDurability((short) 0);
					meta.setDisplayName(
							ChatColor.GOLD + "mcMMO XP " + NumberUtil.integerToRomanNumeral(upgrade.getmcMMOLevel()));
					item.setItemMeta(meta);
					e.getInventory().setItem(4, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nmcMMO Multiplier&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel()))[1] + " experience");
				}
			}
		} else if (e.getSlot() == 4) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getMobDrops() < 3) {
				if (getInfo(UpgradeUtil.mobCost(upgrade.getMobDrops()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.mobCost(upgrade.getMobDrops()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.mobCost(upgrade.getMobDrops()))[0],
							getParsedCosts(UpgradeUtil.mobCost(upgrade.getMobDrops()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.mobCost(upgrade.getMobDrops())[0]))[0]);
					fac.setTokens((fac.getTokens()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.mobCost(upgrade.getMobDrops())[1]))[1]));
					upgrade.setMobDrops(upgrade.getMobDrops() + 1);
					item.setType(Material.ROTTEN_FLESH);
					meta.setDisplayName(
							ChatColor.GOLD + "Mob Drops " + NumberUtil.integerToRomanNumeral(upgrade.getMobDrops()));
					item.setItemMeta(meta);
					e.getInventory().setItem(5, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nDouble Mob Drops&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.mobCost(upgrade.getMobDrops()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.mobCost(upgrade.getMobDrops()))[1] + " experience");
				}
			}
		} else if (e.getSlot() == 5) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getFactionSize() < 3) {
				if (getInfo(UpgradeUtil.sizeCost(upgrade.getFactionSize()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.sizeCost(upgrade.getFactionSize()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.sizeCost(upgrade.getFactionSize()))[0],
							getParsedCosts(UpgradeUtil.sizeCost(upgrade.getFactionSize()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.sizeCost(upgrade.getFactionSize())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.sizeCost(upgrade.getFactionSize())[1]))[1]));
					upgrade.setFactionSize(upgrade.getFactionSize() + 1);
					item.setType(Material.BOOK);
					meta.setDisplayName(ChatColor.GOLD + "Faction Size "
							+ NumberUtil.integerToRomanNumeral(upgrade.getFactionSize()));
					item.setItemMeta(meta);
					e.getInventory().setItem(6, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nFaction Size&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.sizeCost(upgrade.getFactionSize()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.sizeCost(upgrade.getFactionSize()))[1] + " experience");
				}
			}
		} else if (e.getSlot() == 6) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getPower() < 3) {
				if (getInfo(UpgradeUtil.powerCost(upgrade.getPower()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.powerCost(upgrade.getPower()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.powerCost(upgrade.getPower()))[0],
							getParsedCosts(UpgradeUtil.powerCost(upgrade.getPower()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.powerCost(upgrade.getPower())[0]))[0]);
					fac.setTokens((fac.getTokens()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.powerCost(upgrade.getPower())[1]))[1]));
					upgrade.setPower(upgrade.getPower() + 1);
					item.setType(Material.DIAMOND);
					if (upgrade.getPower() == 1) {
						fac.setPowerBoost(10);
					} else if (upgrade.getPower() == 2) {
						fac.setPowerBoost(20);
					} else if (upgrade.getPower() == 3) {
						fac.setPowerBoost(30);
					}
					meta.setDisplayName(ChatColor.GOLD + "Faction Power Boost "
							+ NumberUtil.integerToRomanNumeral(upgrade.getPower()));
					item.setItemMeta(meta);
					e.getInventory().setItem(7, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nFaction Power Boost&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.powerCost(upgrade.getPower()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.powerCost(upgrade.getPower()))[1] + " experience");
				}
			}
		} else if (e.getSlot() == 7) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getReducedDamage() < 3) {
				if (getInfo(UpgradeUtil.reducedCost(upgrade.getReducedDamage()))[0] <= Econ
						.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.reducedCost(upgrade.getReducedDamage()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.reducedCost(upgrade.getReducedDamage()))[0],
							getParsedCosts(UpgradeUtil.reducedCost(upgrade.getReducedDamage()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.reducedCost(upgrade.getReducedDamage())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.reducedCost(upgrade.getReducedDamage())[1]))[1]));
					upgrade.setReducedDamage(upgrade.getReducedDamage() + 1);
					item.setType(Material.GOLD_SWORD);
					item.setDurability((short) 0);
					meta.setDisplayName(ChatColor.GOLD + "Reduced Damage "
							+ NumberUtil.integerToRomanNumeral(upgrade.getReducedDamage()));
					item.setItemMeta(meta);
					e.getInventory().setItem(8, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nReduced Damage&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.reducedCost(upgrade.getReducedDamage()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.reducedCost(upgrade.getReducedDamage()))[1] + " experience");
				}
			}
		} else if (e.getSlot() == 8) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getIncreasedDamage() < 3) {
				if (getInfo(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage()))[0] <= Econ
						.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage()))[0],
							getParsedCosts(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage())[1]))[1]));
					upgrade.setIncreasedDamage(upgrade.getIncreasedDamage() + 1);
					item.setType(Material.BLAZE_POWDER);
					meta.setDisplayName(ChatColor.GOLD + "Increased Damage "
							+ NumberUtil.integerToRomanNumeral(upgrade.getIncreasedDamage()));
					item.setItemMeta(meta);
					e.getInventory().setItem(9, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nIncreased Damage&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.increasedCost(upgrade.getIncreasedDamage()))[1]
							+ " experience");
				}
			}
		} else if (e.getSlot() == 9) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getShorterCooldowns() < 3) {
				if (getInfo(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns()))[0] <= Econ
						.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns()))[0],
							getParsedCosts(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns())[1]))[1]));
					upgrade.setShorterCooldowns(upgrade.getShorterCooldowns() + 1);
					item.setType(Material.REDSTONE);
					meta.setDisplayName(ChatColor.GOLD + "mcMMO Ability Cooldown Length "
							+ NumberUtil.integerToRomanNumeral(upgrade.getShorterCooldowns()));
					item.setItemMeta(meta);
					e.getInventory().setItem(10, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nmcMMO Ability Cooldown Length&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns()))[1]
							+ " experience");
				}
			}
		} else if (e.getSlot() == 10) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getTNTStorage() < 3) {
				if (getInfo(UpgradeUtil.tntCost(upgrade.getTNTStorage()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.tntCost(upgrade.getTNTStorage()))[0] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.tntCost(upgrade.getTNTStorage()))[0],
							getParsedCosts(UpgradeUtil.tntCost(upgrade.getTNTStorage()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.tntCost(upgrade.getTNTStorage())[0]))[0]);
					fac.setTokens((fac.getTokens()
							+ UpgradeUtil.rewards(getParsedCost(UpgradeUtil.tntCost(upgrade.getTNTStorage())[1]))[1]));
					upgrade.setTNTStorage(upgrade.getTNTStorage() + 1);
					if (upgrade.getTNTStorage() == 1) {
						fac.setMaxTNT(fac.getMaxTNT() + 35000);
					} else if (upgrade.getTNTStorage() == 2) {
						fac.setMaxTNT(fac.getMaxTNT() + 60000);
					} else if (upgrade.getTNTStorage() == 3) {
						fac.setMaxTNT(fac.getMaxTNT() + 100000);
					}
					item.setType(Material.TNT);
					meta.setDisplayName(ChatColor.GOLD + "Virtual TNT Storage "
							+ NumberUtil.integerToRomanNumeral(upgrade.getTNTStorage()));
					item.setItemMeta(meta);
					e.getInventory().setItem(11, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nVirtual TNT Storage&7 in the faction core!"));

				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.tntCost(upgrade.getTNTStorage()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.tntCost(upgrade.getTNTStorage()))[1] + " experience");
				}
			}
		} else if (e.getSlot() == 11) {
			ItemMeta meta = item.getItemMeta();
			if (upgrade.getFactionVault() == 0) {
				if (getInfo(UpgradeUtil.vaultCost(upgrade.getFactionVault()))[0] <= Econ.getBalance(fac.getAccountId())
						&& getInfo(UpgradeUtil.vaultCost(upgrade.getFactionVault()))[1] <= fac.getTotalXP()) {
					this.removeCost(fac, getParsedCosts(UpgradeUtil.vaultCost(upgrade.getFactionVault()))[0],
							getParsedCosts(UpgradeUtil.vaultCost(upgrade.getFactionVault()))[1]);
					fac.setUpgradePoints(fac.getUpgradePoints() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.vaultCost(upgrade.getFactionVault())[0]))[0]);
					fac.setTokens((fac.getTokens() + UpgradeUtil
							.rewards(getParsedCost(UpgradeUtil.vaultCost(upgrade.getFactionVault())[1]))[1]));
					upgrade.setFactionVault(upgrade.getFactionVault() + 1);
					fac.setMaxVaults(1);
					item.setType(Material.ENDER_CHEST);
					meta.setDisplayName(ChatColor.GOLD + "Faction Vault Size "
							+ NumberUtil.integerToRomanNumeral(upgrade.getFactionVault()));
					item.setItemMeta(meta);
					e.getInventory().setItem(12, item);
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
					fac.sendMessage(ChatColor.GOLD + "" + p.getName()
							+ translate("&7 has unlocked &6&nFaction Vault&7 in the faction core!"));
				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough to purchase this upgrade!");
					p.sendMessage(ChatColor.RED + "You only have $" + Econ.getBalance(fac.getAccountId()) + " and "
							+ fac.getTotalXP() + " experience!");
					p.sendMessage(ChatColor.RED + "You need $"
							+ getParsedCosts(UpgradeUtil.vaultCost(upgrade.getFactionVault()))[0] + " and "
							+ getParsedCosts(UpgradeUtil.vaultCost(upgrade.getFactionVault()))[1] + " experience");
				}
			}
		}
		e.setCancelled(true);
	}

	private int[] getParsedCosts(String[] formatted) {
		String first = formatted[0].replaceAll(",", "");
		String second = formatted[1].replaceAll(",", "");

		int firstInt = Integer.parseInt(first);
		int secondInt = Integer.parseInt(second);

		return new int[] { firstInt, secondInt };
	}

	private void removeCost(Faction fac, int money, int xp) {
		Econ.setBalance(fac.getAccountId(), Econ.getBalance(fac.getAccountId()) - money);
		fac.setTotalXP(fac.getTotalXP() - xp);
	}

	private static int getParsedCost(String formatted) {
		String first = formatted.replaceAll(",", "");

		int firstInt = Integer.parseInt(first);

		return firstInt;
	}

	public int[] getInfo(String[] s) {
		String money = s[0];
		String xp = s[1];

		money = money.replaceAll(",", "");
		xp = xp.replaceAll(",", "");

		int moneyInt = Integer.parseInt(money);
		int xpInt = Integer.parseInt(xp);

		return new int[] { moneyInt, xpInt };
	}
}