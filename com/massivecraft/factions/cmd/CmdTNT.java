package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import me.danelegend.core.tntstorage.util.TNTUtil;

public class CmdTNT extends FCommand {

	public CmdTNT() {
		super();
		this.aliases.add("tnt");

		this.optionalArgs.put("withdraw", "fill");
		this.optionalArgs.put("amount", "inventory");
		this.optionalArgs.put("radius", "");
		this.optionalArgs.put("tnt", "");

		this.permission = Permission.TNT.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (args.size() == 0) {
			me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Commands"));
			me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/f tnt balance"));
			me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/f tnt withdraw [amount]"));
			me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/f tnt dispensers [radius]"));
			me.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&e/f tnt fill [inventory | storage] [radius] [amount]"));
		} else if (args.size() == 1) {
			if (args.get(0).equalsIgnoreCase("balance")) {
				me.sendMessage(ChatColor.GOLD + "TNT: " + myFaction.getTNT() + "/" + myFaction.getMaxTNT());
			} else if (args.get(0).equalsIgnoreCase("withdraw")) {
				me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must specify an amount"));
			} else if (args.get(0).equalsIgnoreCase("dispensers")) {
				me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must specify a radius"));
			} else if (args.get(0).equalsIgnoreCase("fill")) {
				me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must specify an inventory"));
			}
		} else if (args.size() == 2) {
			if (args.get(0).equalsIgnoreCase("withdraw")) {
				int amount = this.argAsInt(1, 0);

				if (myFaction.getTNT() < amount) {
					me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough TNT"));
				} else {
					TNTUtil.withdraw(me, amount);
				}
			} else if (args.get(0).equalsIgnoreCase("dispensers")) {
				int radius = this.argAsInt(1);

				if (radius < 1 || radius > 40) {
					me.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cYou must enter a radius between 1 and 40"));
					return;
				}

				int amount = this.getDispensers(me, radius);
				me.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&eThere are &6" + amount + " &edispensers within &6" + radius + " &eblocks of you"));
			} else if (args.get(0).equalsIgnoreCase("fill")) {
				me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must specify a radius"));
			}
		} else if (args.size() == 3) {
			if (args.get(0).equalsIgnoreCase("fill")) {
				me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must specify an amount"));
			}
		} else if (args.size() == 4) {
			if (args.get(0).equalsIgnoreCase("fill")) {
				String inv = this.argAsString(1);

				if (inv.equalsIgnoreCase("inventory") || inv.equalsIgnoreCase("inv")) {
					int radius = this.argAsInt(2);

					if (radius < 1 || radius > 40) {
						me.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&cYou must enter a radius between 1 and 40"));
						return;
					}

					int amount = this.argAsInt(3);

					if (amount < 0 || amount > 576) {
						me.sendMessage(
								ChatColor.translateAlternateColorCodes('&', "&cYou must enter an amount over 0"));
						return;
					}

					fillDispensers(me, radius, amount);

				} else if (inv.equalsIgnoreCase("storage")) {
					int radius = this.argAsInt(2);

					if (radius < 1 || radius > 40) {
						me.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&cYou must enter a radius between 1 and 40"));
						return;
					}

					int amount = this.argAsInt(3);

					if (amount < 0 || amount > 576) {
						me.sendMessage(
								ChatColor.translateAlternateColorCodes('&', "&cYou must enter an amount over 0"));
						return;
					}

					fillDispensersStorage(me, radius, amount);

				} else {
					me.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat is not a valid inventory type"));
					return;
				}
			}
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_TNT_DESCRIPTION;
	}

	private int getDispensers(Player p, int radius) {
		int count = 0;
		Location location = p.getLocation();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					if (location.getWorld().getBlockAt(x, y, z).getType() == Material.DISPENSER)
						count++;
				}
			}
		}
		return count;
	}

	private void fillDispensers(Player p, int radius, int amount) {
		List<Block> blocks = new ArrayList<Block>();
		Location location = p.getLocation();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					if (location.getWorld().getBlockAt(x, y, z).getType() == Material.DISPENSER)
						blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}

		if (blocks.size() <= 0) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere are no dispensers nearby"));
			return;
		}

		int total = amount * blocks.size();

		if (!hasTNT(p, total)) {
			p.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough TNT in your inventory"));
			return;
		}
		ItemStack tnt = new ItemStack(Material.TNT, amount);

		for (Block b : blocks) {
			Dispenser dispenser = (Dispenser) b.getState();
			dispenser.getInventory().addItem(tnt);
			p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.TNT, total) });
		}

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou filled &6" + blocks.size()
				+ " &edispensers with &6" + amount + " &eTNT within &6" + radius + "&e of you"));
		p.updateInventory();
	}

	private void fillDispensersStorage(Player p, int radius, int amount) {
		List<Block> blocks = new ArrayList<Block>();
		Location location = p.getLocation();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					if (location.getWorld().getBlockAt(x, y, z).getType() == Material.DISPENSER)
						blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}

		if (blocks.size() <= 0) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere are no dispensers nearby"));
			return;
		}

		int total = amount * blocks.size();

		if (myFaction.getTNT() < amount) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough TNT in your storage"));
			return;
		}

		ItemStack tnt = new ItemStack(Material.TNT, amount);

		for (Block b : blocks) {
			Dispenser dispenser = (Dispenser) b.getState();
			dispenser.getInventory().addItem(tnt);
			myFaction.setTNT(myFaction.getTNT() - total);
		}

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou filled &6" + blocks.size()
				+ " &edispensers with &6" + amount + " &eTNT within &6" + radius + " &eblocks of you"));
	}

	private boolean hasTNT(Player p, int amount) {
		return p.getInventory().contains(Material.TNT, amount);
	}
}