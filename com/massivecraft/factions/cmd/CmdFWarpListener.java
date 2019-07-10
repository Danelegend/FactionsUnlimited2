package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFWarpListener implements Listener {

	private FPlayer fme;

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

		if (e.getInventory().getTitle().equals(ChatColor.DARK_GRAY + "Faction Warps")) {

			Player p = (Player) e.getWhoClicked();
			fme = FPlayers.getInstance().getByPlayer(p);

			if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
				return;
			}

			String warpName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
			if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
				if (fme.getRole().isAtLeast(Role.MODERATOR)) {
					openGUI(p, warpName);
					e.setCancelled(true);
				} else {
					p.sendMessage(ChatColor.RED + "You must be a moderator or higher to edit the warp icon.");
					e.setCancelled(true);
				}
			} else {
				this.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warpName, new Runnable() {
					@Override
					public void run() {
						p.teleport(fme.getFaction().getWarp(warpName).getLocation());
						fme.msg(TL.COMMAND_FWARP_WARPED, warpName);
					}
				}, P.p.getConfig().getLong("warmups.f-warp", 0));
				e.setCancelled(true);
			}
		} else if (e.getInventory().getTitle().contains(ChatColor.DARK_GRAY + "Select Icon")) {

			if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
				return;
			}

			Player p = (Player) e.getWhoClicked();
			fme = FPlayers.getInstance().getByPlayer(p);

			String name = e.getInventory().getTitle().substring(18, e.getInventory().getTitle().length());

			fme.getFaction().setWarpIcon(name, e.getCurrentItem());

			fme.sendMessage(ChatColor.GREEN + "Successfully changed icon for " + name);

			e.setCancelled(true);

			p.closeInventory();

			p.chat("/f warp");
		}
	}

	public void doWarmUp(WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
		this.doWarmUp(this.fme, warmup, translationKey, action, runnable, delay);
	}

	public void doWarmUp(FPlayer player, WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable,
			long delay) {
		WarmUpUtil.process(player, warmup, translationKey, action, runnable, delay);
	}

	public void openGUI(Player p, String warpName) {
		Inventory inv = Bukkit.createInventory(null, 18, ChatColor.DARK_GRAY + "Select Icon for " + warpName);

		inv.setItem(0, new ItemStack(Material.STONE));
		inv.setItem(1, new ItemStack(Material.COBBLESTONE));
		inv.setItem(2, new ItemStack(Material.NETHERRACK));
		inv.setItem(3, new ItemStack(Material.SAND));
		inv.setItem(4, new ItemStack(Material.TNT));
		inv.setItem(5, new ItemStack(Material.WATER_BUCKET));
		inv.setItem(6, new ItemStack(Material.DIAMOND_BLOCK));
		inv.setItem(7, new ItemStack(Material.EMERALD_BLOCK));
		inv.setItem(8, new ItemStack(Material.IRON_BLOCK));
		inv.setItem(9, new ItemStack(Material.GOLD_BLOCK));
		inv.setItem(10, new ItemStack(Material.DIAMOND_SWORD));
		inv.setItem(11, new ItemStack(Material.DIAMOND_PICKAXE));
		inv.setItem(12, new ItemStack(Material.DIAMOND_SPADE));
		inv.setItem(13, new ItemStack(Material.SUGAR_CANE));
		inv.setItem(14, new ItemStack(Material.CACTUS));
		inv.setItem(15, new ItemStack(Material.POTION, 1, (short) 16421));
		inv.setItem(16, new ItemStack(Material.LOG));
		inv.setItem(17, new ItemStack(Material.MOB_SPAWNER));

		p.openInventory(inv);
	}
}