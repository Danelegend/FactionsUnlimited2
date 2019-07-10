package com.massivecraft.factions.zcore.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Upgrade;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.NumberUtil;
import com.massivecraft.factions.util.UpgradeUtil;

public class CoreUtil {

	public static ItemStack getCoreItem() {
		ItemStack core = new ItemBuilder(Material.NETHER_STAR).setName(translate("&aFaction Core"))
				.setLore(translate("&7Place this core down in any of"), translate("&7your claimed territories"))
				.toItemStack();

		return core;
	}

	public static Inventory coreGUI(Faction fac) {
		Inventory coreGUI = Bukkit.createInventory(null, 18, ChatColor.AQUA + "Faction Core");

		ItemStack factionInfo = new ItemBuilder(Material.BOOK).setName(ChatColor.GREEN + "Faction Information").setLore(
				translate(" &7* &6Name: &a" + fac.getTag()),
				translate(" &7* &6Land / Power / Maxpower: &a" + fac.getLandRounded() + " &6/ &a"
						+ fac.getPowerRounded() + " &6/ &a" + (int) fac.getPowerMax()),
				translate(" &7* &6Points: &a" + fac.getPoints()), translate(" &7* &6Tokens: &a" + fac.getTokens()),
				translate(" &7* &6TNT Storage: &a" + fac.getTNT() + " &6/ &a" + fac.getMaxTNT()),
				translate(" &7* &6Balance: &a$" + Econ.getBalance(fac.getAccountId())),
				translate(" &7* &6Shield: " + (fac.hasShield() ? "&atrue" : "&cfalse")),
				translate(" &7* &6Experience: &a" + fac.getTotalXP()),
				translate(" &7* &6Upgrades unlocked: &a" + fac.getUpgrades().getTotalAmount() + " &6/ &a"
						+ fac.getUpgrades().getMaxAmount()),
				translate(" &7* &6Core Durability: &a" + fac.getDurability() + " &6/ &a" + fac.getMaxDurability()))
				.toItemStack();
		/*ItemStack shop = new ItemBuilder(Material.GOLD_INGOT).setName(ChatColor.BLUE + "Faction Shop")
				.setLore(translate("&7Click here to view your faction shop"), "",
						translate("&6Items in this shop can only be bought with"), translate("&6with faction tokens"),
						"", translate("&7(&bTokens can be obtained through faction"),
						translate("&bupgrades and global events&7)"))
				.toItemStack();*/
		ItemStack remove = new ItemBuilder(Material.SKULL_ITEM).setName(ChatColor.DARK_RED + "Remove Core")
				.setLore(translate("&7Click here to remove your faction core"), "",
						translate("&6&lWhen you remove your core..."),
						translate(" &e* &cSpawners will not be functional in any of your claims"),
						translate(" &e* &cCrops will not grow in any of your claims"),
						translate(" &e* &cYou will be unable to access your faction core features"), "",
						translate("&7(&bFaction vault and experience vault will"),
						translate("&bnot be erased on removal of your core&7)"))
				.toItemStack();
		ItemStack upgrade = new ItemBuilder(Material.DIAMOND_BLOCK).setName(ChatColor.DARK_GREEN + "Faction Upgrades")
				.toItemStack();
		ItemMeta meta = upgrade.getItemMeta();
		meta.setLore(getUpgradeLore(fac));
		upgrade.setItemMeta(meta);

		ItemStack shield = new ItemBuilder(Material.TNT).setName(ChatColor.YELLOW + "Faction Shields")
				.setLore(translate("&7Click here to view the different shields"),
						translate("&7you can purchase to protect your claims"))
				.toItemStack();
		ItemStack vault = new ItemBuilder(Material.CHEST).setName(ChatColor.LIGHT_PURPLE + "Faction Vault")
				.setLore(translate("&7Click here to access your faction vault (&citems&7)")).toItemStack();
		ItemStack xpvault = new ItemBuilder(Material.ENDER_CHEST).setName(ChatColor.AQUA + "Faction Experience Vault")
				.setLore(translate("&7Click here to access your faction vault (&aexperience&7)")).toItemStack();
		/*
		 * upgradeMeta.setDisplayName(fac.getLevel() < 5 ? ChatColor.GREEN +
		 * "Upgrade to level " + (fac.getLevel() + 1) : ChatColor.GREEN + "Max level");
		 */

		coreGUI.setItem(0, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
		coreGUI.setItem(1, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11));
		coreGUI.setItem(7, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11));
		coreGUI.setItem(8, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
		coreGUI.setItem(9, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
		coreGUI.setItem(10, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11));
		coreGUI.setItem(16, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11));
		coreGUI.setItem(17, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
		coreGUI.setItem(2, factionInfo);
		coreGUI.setItem(11, xpvault);
		/*coreGUI.setItem(3, shop);*/
		coreGUI.setItem(3, upgrade);
		coreGUI.setItem(4, shield);
		coreGUI.setItem(15, vault);
		coreGUI.setItem(5, remove);
		return coreGUI;
	}

	private static List<String> getUpgradeLore(Faction fac) {
		List<String> lore = new ArrayList<String>();
		Upgrade upgrades = fac.getUpgrades();
		lore.add(translate("&7Click here to view all your available upgrades"));
		lore.add(translate("&b(&cMoney and experience cost will be removed from"));
		lore.add(translate("&cfaction balance/experience vault upon upgrading&b)"));
		lore.add("");
		lore.add(translate("&e&lCurrent Upgrades..."));
		if (upgrades.getDurability() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getDurability()))
					+ translate("&c Durability"));
		if (upgrades.getXPLevel() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getXPLevel()))
					+ translate("&c Experience Boost"));
		if (upgrades.getCropsLevel() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getCropsLevel()))
					+ translate("&c Crop Drops"));
		if (upgrades.getmcMMOLevel() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getmcMMOLevel()))
					+ translate("&c mcMMO Experience Boost"));
		if (upgrades.getMobDrops() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getMobDrops()))
					+ translate("&c Mob Drops"));
		if (upgrades.getFactionSize() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getFactionSize()))
					+ translate("&c Faction Size"));
		if (upgrades.getPower() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getPower()))
					+ translate("&c Faction Power Boost"));
		if (upgrades.getReducedDamage() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getReducedDamage()))
					+ translate("&c Reduced Damage"));
		if (upgrades.getIncreasedDamage() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getIncreasedDamage()))
					+ translate("&c Increased Damage"));
		if (upgrades.getShorterCooldowns() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getShorterCooldowns()))
					+ translate("&c mcMMO Ability Cooldown Length"));
		if (upgrades.getTNTStorage() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getTNTStorage()))
					+ translate("&c Virtual TNT Storage"));
		if (upgrades.getFactionVault() > 0)
			lore.add(translate("&8[&a+&8] &6&nTier " + NumberUtil.integerToRomanNumeral(upgrades.getFactionVault()))
					+ translate("&c Faction Vaults"));
		lore.add("");
		lore.add(translate("&7(&e*&7) &eYou unlocked &b") + upgrades.getTotalAmount()
				+ translate("&e upgrades out of &a") + translate("36 &etotal faction upgrades"));
		return lore;
	}

	public static Inventory experienceVault(Faction fac, Player p) {
		Inventory experienceVault = Bukkit.createInventory(null, 18, ChatColor.AQUA + "Experience Vault");
		ItemStack vault = new ItemBuilder(Material.EXP_BOTTLE).setName(translate("&aTotal Faction Experience"))
				.setLore(translate("&7Insert experience by using one of"), translate("&7the icons below"), "",
						translate("&6&lSTORED &c" + fac.getTotalXP() + " &cexperience"),
						translate("&eYour current xp: &a" + SetExpFix.getTotalExperience(p)))
				.toItemStack();

		ItemStack add100 = new ItemBuilder(new ItemStack(Material.WOOL, 1, (short) 14))
				.setName(translate("&c+ 1000 XP"))
				.setLore(translate("&7Use this to store experience levels"), translate("&7into your experience vault"))
				.toItemStack();
		ItemStack add1000 = new ItemBuilder(new ItemStack(Material.WOOL, 1, (short) 4))
				.setName(translate("&e+ 5000 XP"))
				.setLore(translate("&7Use this to store experience levels"), translate("&7into your experience vault"))
				.toItemStack();
		ItemStack add10000 = new ItemBuilder(new ItemStack(Material.WOOL, 1, (short) 5))
				.setName(translate("&a+ 10000 XP"))
				.setLore(translate("&7Use this to store experience levels"), translate("&7into your experience vault"))
				.toItemStack();
		experienceVault.setItem(0, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(1, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(2, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(3, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(5, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(6, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(7, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(8, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
		experienceVault.setItem(9, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
		experienceVault.setItem(10, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
		experienceVault.setItem(11, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
		experienceVault.setItem(15, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
		experienceVault.setItem(16, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
		experienceVault.setItem(17, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
		experienceVault.setItem(12, add100);
		experienceVault.setItem(13, add1000);
		experienceVault.setItem(14, add10000);
		experienceVault.setItem(4, vault);
		return experienceVault;
	}

	public static Inventory upgradeGUI(Faction fac) {
		Inventory upgradeGUI = Bukkit.createInventory(null, 18, ChatColor.DARK_GREEN + "Faction Upgrades");
		Upgrade upgrade = fac.getUpgrades();

		ItemStack durability = new ItemBuilder(upgrade.getDurability() > 0 ? new ItemStack(Material.DIAMOND_CHESTPLATE)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Core Durability"
								+ (upgrade.getDurability() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getDurability())))
						.setLore(translate("&7This upgrade allows you to increase the"),
								translate("&7durability of your faction core"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c+ 10 Durability"),
								translate("  &b&lTier II &c+ 20 Durability"),
								translate("  &b&lTier III &c+ 30 Durability"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.durabilityCost(upgrade.getDurability())[0]),
								translate("  &b* &6" + UpgradeUtil.durabilityCost(upgrade.getDurability())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(getParsedCost(
												UpgradeUtil.durabilityCost(upgrade.getDurability())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil.rewards(
										getParsedCost(UpgradeUtil.durabilityCost(upgrade.getDurability())[1]))[1])
										+ " tokens")
						.toItemStack(); // diamond chestplate
		ItemStack xp = new ItemBuilder(upgrade.getXPLevel() > 0 ? new ItemStack(Material.EXP_BOTTLE)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Experience Boosts"
								+ (upgrade.getXPLevel() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getXPLevel())))
						.setLore(translate("&7This upgrade allows you to increase your"),
								translate("&7experience multiplier"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c1.25x experience"),
								translate("  &b&lTier II &c1.50x experience"),
								translate("  &b&lTier III &c2x experience"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.xpBoost(upgrade.getXPLevel())[0]),
								translate("  &b* &6" + UpgradeUtil.xpBoost(upgrade.getXPLevel())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil
												.rewards(getParsedCost(UpgradeUtil.xpBoost(upgrade.getXPLevel())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.xpBoost(upgrade.getXPLevel())[1]))[1])
										+ " tokens")
						.toItemStack(); // exp bottle
		ItemStack crops = new ItemBuilder(upgrade.getCropsLevel() > 0 ? new ItemStack(Material.SEEDS)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Crop Drops"
								+ (upgrade.getCropsLevel() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getCropsLevel())))
						.setLore(translate("&7This upgrade allows you to increase the chance"),
								translate("&7of doubling crop drops (excludes harvester hoe)"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c50% chance to double"),
								translate("  &b&lTier II &c75% chance to double"),
								translate("  &b&lTier III &c100% chance to double"), "",
								translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.cropsCost(upgrade.getCropsLevel())[0]),
								translate("  &b* &6" + UpgradeUtil.cropsCost(upgrade.getCropsLevel())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(
												getParsedCost(UpgradeUtil.cropsCost(upgrade.getCropsLevel())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.cropsCost(upgrade.getCropsLevel())[1]))[1])
										+ " tokens")
						.toItemStack(); // seeds
		ItemStack mcmmo = new ItemBuilder(upgrade.getmcMMOLevel() > 0 ? new ItemStack(Material.DIAMOND_SWORD)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(
								ChatColor.GOLD + "mcMMO XP"
										+ (upgrade.getmcMMOLevel() == 0 ? ""
												: " " + NumberUtil.integerToRomanNumeral(upgrade.getmcMMOLevel())))
						.setLore(translate("&7This upgrade allows you to increase your"),
								translate("&7mcMMO experience multiplier"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c1.25x experience"),
								translate("  &b&lTier II &c1.50x experience"),
								translate("  &b&lTier III &c2x experience"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel())[0]),
								translate("  &b* &6" + UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(
												getParsedCost(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.mcMMOCost(upgrade.getmcMMOLevel())[1]))[1])
										+ " tokens")
						.toItemStack(); // diamond sword
		ItemStack mobDrops = new ItemBuilder(upgrade.getMobDrops() > 0 ? new ItemStack(Material.ROTTEN_FLESH)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Mob Drops"
								+ (upgrade.getMobDrops() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getMobDrops())))
						.setLore(translate("&7This upgrade allows you to increase the chance"),
								translate("&7of doubling monster drops (affects any monster that dies by lava or"),
								translate("&7player in your territory)"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c50% chance to double"),
								translate("  &b&lTier II &c75% chance to double"),
								translate("  &b&lTier III &c100% chance to double"), "",
								translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.mobCost(upgrade.getMobDrops())[0]),
								translate("  &b* &6" + UpgradeUtil.mobCost(upgrade.getMobDrops())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(
												getParsedCost(UpgradeUtil.mobCost(upgrade.getMobDrops())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.mobCost(upgrade.getMobDrops())[1]))[1])
										+ " tokens")
						.toItemStack(); // rotten flesh
		ItemStack factionSize = new ItemBuilder(upgrade.getFactionSize() > 0 ? new ItemStack(Material.BOOK)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Faction Size"
								+ (upgrade.getFactionSize() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getFactionSize())))
						.setLore(translate("&7This upgrade allows you to increase the amount"),
								translate("&7of players allowed in your faction"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c+ 2 faction limit"),
								translate("  &b&lTier II &c+ 3 faction limit"),
								translate("  &b&lTier III &c+ 5 faction limit"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.sizeCost(upgrade.getFactionSize())[0]),
								translate("  &b* &6" + UpgradeUtil.sizeCost(upgrade.getFactionSize())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(
												getParsedCost(UpgradeUtil.sizeCost(upgrade.getFactionSize())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.sizeCost(upgrade.getFactionSize())[1]))[1])
										+ " tokens")
						.toItemStack(); // book
		ItemStack power = new ItemBuilder(upgrade.getPower() > 0 ? new ItemStack(Material.DIAMOND)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Faction Power Boost"
								+ (upgrade.getPower() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getPower())))
						.setLore(translate("&7This upgrade allows you to increase the total"),
								translate("&7amount of power for your faction"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c+ 10 faction power"),
								translate("  &b&lTier II &c+ 20 faction power"),
								translate("  &b&lTier III &c+ 30 faction power"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.powerCost(upgrade.getPower())[0]),
								translate("  &b* &6" + UpgradeUtil.powerCost(upgrade.getPower())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil
												.rewards(getParsedCost(UpgradeUtil.powerCost(upgrade.getPower())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.powerCost(upgrade.getPower())[1]))[1])
										+ " tokens")
						.toItemStack(); // diamond
		ItemStack reduceDamage = new ItemBuilder(upgrade.getReducedDamage() > 0 ? new ItemStack(Material.GOLD_SWORD)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Reduced Damage"
								+ (upgrade.getReducedDamage() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getReducedDamage())))
						.setLore(translate("&7This upgrade reduces the damage you"),
								translate("&7take in any of your faction claims"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c5% damage reduction"),
								translate("  &b&lTier II &c10% damage reduction"),
								translate("  &b&lTier III &c20% damage reduction"), "",
								translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.reducedCost(upgrade.getReducedDamage())[0]),
								translate("  &b* &6" + UpgradeUtil.reducedCost(upgrade.getReducedDamage())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(getParsedCost(
												UpgradeUtil.reducedCost(upgrade.getReducedDamage())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil.rewards(
										getParsedCost(UpgradeUtil.reducedCost(upgrade.getReducedDamage())[1]))[1])
										+ " tokens")
						.toItemStack(); // gold sword
		ItemStack increasedDamage = new ItemBuilder(
				upgrade.getIncreasedDamage() > 0 ? new ItemStack(Material.BLAZE_POWDER)
						: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
								.setName(ChatColor.GOLD + "Increased Damage"
										+ (upgrade.getIncreasedDamage() == 0 ? ""
												: " " + NumberUtil.integerToRomanNumeral(upgrade.getIncreasedDamage())))
								.setLore(
										translate("&7This upgrade increases the damage you"), translate(
												"&7give to enemies in your faction claim"),
										"", translate("&eThe perks of upgrading this feature..."), translate(
												"  &b&lTier I &c5% damage increase"),
										translate("  &b&lTier II &c10% damage increase"), translate(
												"  &b&lTier III &c20% damage increase"),
										"", translate("&eCost to upgrade..."), translate("  &b* &a$" + UpgradeUtil
												.increasedCost(upgrade.getIncreasedDamage())[0]),
										translate("  &b* &6"
												+ UpgradeUtil.increasedCost(upgrade.getIncreasedDamage())[1])
												+ translate(" &6experience"),
										translate("&7(Money and experience will be removed from"),
										translate("&7faction vaults/balance)"), "",
										translate("&eRewards for upgrading..."),
										translate("  &b* &c"
												+ UpgradeUtil.rewards(getParsedCost(
														UpgradeUtil.increasedCost(upgrade.getIncreasedDamage())[0]))[0]
												+ " points"),
										translate("  &b* &9" + UpgradeUtil.rewards(getParsedCost(
												UpgradeUtil.increasedCost(upgrade.getIncreasedDamage())[1]))[1])
												+ " tokens")
								.toItemStack(); // blaze powder
		ItemStack shorterCooldowns = new ItemBuilder(upgrade.getShorterCooldowns() > 0
				? new ItemStack(Material.REDSTONE)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "mcMMO Ability Cooldown Length"
								+ (upgrade.getShorterCooldowns() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getShorterCooldowns())))
						.setLore(translate("&7This upgrade decreases the mcMMO ability"),
								translate("&7cooldown length for faction members (tiers do not stack)"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c- 4 seconds"), translate("  &b&lTier II &c- 8 seconds"),
								translate("  &b&lTier III &c- 12 seconds"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.abilityCost(upgrade.getShorterCooldowns())[0]),
								translate("  &b* &6" + UpgradeUtil.abilityCost(upgrade.getShorterCooldowns())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(getParsedCost(
												UpgradeUtil.abilityCost(upgrade.getShorterCooldowns())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil.rewards(
										getParsedCost(UpgradeUtil.abilityCost(upgrade.getShorterCooldowns())[1]))[1])
										+ " tokens")
						.toItemStack(); // redstone dust
		ItemStack potionEffect = new ItemBuilder(upgrade.getTNTStorage() > 0 ? new ItemStack(Material.TNT)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Virtual TNT Storage"
								+ (upgrade.getTNTStorage() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getTNTStorage())))
						.setLore(translate("&7This upgrade increases the size of"),
								translate("&7your virtual TNT storage unit"),
								translate(" &7- &c/f tnt &8(shows amount of TNT stored)"),
								translate(" &7- &c/f withdrawtnt &e<amount> &8(withdraw TNT into your inventory)"),
								translate("&7(purchase the TNT storage chest from faction shop)"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &c+ 35,000 TNT"), translate("  &b&lTier II &c+ 60,000 TNT"),
								translate("  &b&lTier III &c+ 100,000 TNT"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.tntCost(upgrade.getTNTStorage())[0]),
								translate("  &b* &6" + UpgradeUtil.tntCost(upgrade.getTNTStorage())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(
												getParsedCost(UpgradeUtil.tntCost(upgrade.getTNTStorage())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.tntCost(upgrade.getTNTStorage())[1]))[1])
										+ " tokens")
						.toItemStack(); // tnt
		ItemStack factionVault = new ItemBuilder(upgrade.getFactionVault() > 0 ? new ItemStack(Material.ENDER_CHEST)
				: new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14))
						.setName(ChatColor.GOLD + "Faction Vault"
								+ (upgrade.getFactionVault() == 0 ? ""
										: " " + NumberUtil.integerToRomanNumeral(upgrade.getFactionVault())))
						.setLore(translate("&7This upgrade unlocks your faction vault"), "",
								translate("&eThe perks of upgrading this feature..."),
								translate("  &b&lTier I &cUnlock Faction Vault"), "", translate("&eCost to upgrade..."),
								translate("  &b* &a$" + UpgradeUtil.vaultCost(upgrade.getFactionVault())[0]),
								translate("  &b* &6" + UpgradeUtil.vaultCost(upgrade.getFactionVault())[1])
										+ translate(" &6experience"),
								translate("&7(Money and experience will be removed from"),
								translate("&7faction vaults/balance)"), "", translate("&eRewards for upgrading..."),
								translate("  &b* &c"
										+ UpgradeUtil.rewards(
												getParsedCost(UpgradeUtil.vaultCost(upgrade.getFactionVault())[0]))[0]
										+ " points"),
								translate("  &b* &9" + UpgradeUtil
										.rewards(getParsedCost(UpgradeUtil.vaultCost(upgrade.getFactionVault())[1]))[1])
										+ " tokens")
						.toItemStack(); // ender chest

		upgradeGUI.setItem(0, durability);
		upgradeGUI.setItem(1, xp);
		upgradeGUI.setItem(2, crops);
		upgradeGUI.setItem(3, mcmmo);
		upgradeGUI.setItem(4, mobDrops);
		upgradeGUI.setItem(5, factionSize);
		upgradeGUI.setItem(6, power);
		upgradeGUI.setItem(7, reduceDamage);
		upgradeGUI.setItem(8, increasedDamage);
		upgradeGUI.setItem(9, shorterCooldowns);
		upgradeGUI.setItem(10, potionEffect);
		upgradeGUI.setItem(11, factionVault);

		return upgradeGUI;
	}

	/*public static Inventory factionShop(Faction fac) {
		Inventory factionShop = Bukkit.createInventory(null, 18, ChatColor.LIGHT_PURPLE + "Faction Shop");
		ItemStack shock = new ItemBuilder(Material.DIAMOND_PICKAXE).setName(ChatColor.RED + "Shockwave")

				.setLore(ChatColor.RED + "Shockwave I", "", translate("&3100 Faction Tokens")).toItemStack();
		ItemStack infusion = new ItemBuilder(Material.DIAMOND_PICKAXE).setName(ChatColor.RED + "Infusion")

				.setLore(ChatColor.RED + "Infusion I", "", translate("&3200 Faction Tokens")).toItemStack();
		ItemStack excavator = new ItemBuilder(Material.DIAMOND_SPADE).setName(ChatColor.RED + "Excavator")

				.setLore(ChatColor.RED + "Shockwave I", "", translate("&350 Faction Tokens")).toItemStack();
		ItemStack harvester = new ItemBuilder(Material.DIAMOND_HOE).setName(ChatColor.RED + "Harvester")
				.setLore(ChatColor.RED + "Harvester I", "", translate("&3100 Faction Tokens")).toItemStack();
		ItemStack creeperEgg = new ItemBuilder(new ItemStack(Material.MONSTER_EGG, 1, (short) 50))
				.setName(ChatColor.AQUA + "Creeper Egg").setLore(translate("&310 Faction Tokens")).toItemStack();
		ItemStack sellWand = new ItemBuilder(Material.DIAMOND_HOE).setName(ChatColor.GOLD + "Sell Wand")
				.setLore(translate("&7Left click on a chest to sell the contents"),
						translate("&7within the storage container"),
						translate(" &c* &eItems will be sold less for what they are worth"),
						translate("&ewhen using this item"), "", translate("&3125 Faction Tokens"))
				.toItemStack();
		ItemStack voidChest = new ItemBuilder(Material.ENDER_CHEST).setName(ChatColor.DARK_PURPLE + "Void Chest")
				.setLore(translate("&7Items stored in this chest will be"), translate("&7instantly sold to the shop"),
						translate(" &c* &eItems will be sold less for what they are worth"),
						translate("&ewhen using this item"), "", translate("&3300 Faction Tokens"))
				.toItemStack();
		ItemStack tntChest = new ItemBuilder(Material.CHEST).setName(ChatColor.DARK_GREEN + "TNT Chest")
				.setLore(translate("&7Place this down and store TNT into"),
						translate("&7your faction's virtual storage system"), "", translate("&3175 Faction Tokens"))
				.toItemStack();
		ItemStack beacon = new ItemBuilder(Material.BEACON).setLore(translate("&350 Faction Tokens")).toItemStack();
		ItemStack mystery = new ItemBuilder(Material.MOB_SPAWNER).setName(translate("&6Mystery Monster Spawner"))
				.setLore(translate("&7Upon use, you will be granted one of the following"),
						translate("&7spawners at a random chance"), "", translate("&eSpawners available..."),
						translate("  &b* &dIron Golem Spawner &7(x1)"), translate("  &b* &dBlaze Spawner &7(x1)"),
						translate("  &b* &dSlime Spawner &7(x1)"), translate("  &b* &dCreeper Spawner &7(x1)"),
						translate("  &b* &dWitch Spawner &7(x1)"), translate("  &b* &dMagma Spawner &7(x1)"),
						translate("  &b* &dGhast Spawner &7(x1)"), translate("  &b* &dCow Spawner &7(x5)"), "",
						translate("&375 Faction Tokens"))
				.toItemStack();

		factionShop.setItem(0, shock);
		factionShop.setItem(1, infusion);
		factionShop.setItem(2, excavator);
		factionShop.setItem(3, harvester);
		factionShop.setItem(4, creeperEgg);
		factionShop.setItem(5, sellWand);
		factionShop.setItem(6, voidChest);
		factionShop.setItem(7, tntChest);
		factionShop.setItem(8, beacon);
		factionShop.setItem(9, mystery);

		return factionShop;
	} */

	public static Inventory shieldShop(Faction fac) {
		Inventory shields = Bukkit.createInventory(null, 9, ChatColor.RED + "Faction Shields");

		ItemStack six = new ItemBuilder(Material.IRON_BLOCK).setName(ChatColor.GRAY + "6 Hour Shield")
				.setLore(translate("&340 Faction Tokens")).toItemStack();
		ItemStack twelve = new ItemBuilder(Material.GOLD_BLOCK).setName(ChatColor.GOLD + "12 Hour Shield")
				.setLore(translate("&360 Faction Tokens")).toItemStack();
		ItemStack eighteen = new ItemBuilder(Material.DIAMOND_BLOCK).setName(ChatColor.AQUA + "18 Hour Shield")
				.setLore(translate("&335 Faction Tokens")).toItemStack();
		ItemStack twentyFour = new ItemBuilder(Material.EMERALD_BLOCK).setName(ChatColor.GREEN + "24 Hour Shield")
				.setLore(translate("&360 Faction Tokens")).toItemStack();

		shields.setItem(0, six);
		shields.setItem(1, twelve);
		shields.setItem(2, eighteen);
		shields.setItem(3, twentyFour);

		return shields;
	}

	private static int getParsedCost(String formatted) {
		String first = formatted.replaceAll(",", "");

		int firstInt = Integer.parseInt(first);

		return firstInt;
	}
 
	private static String translate(String translate) {
		return ChatColor.translateAlternateColorCodes('&', translate);
	}
}