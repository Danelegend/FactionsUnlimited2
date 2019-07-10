package me.danelegend.core.tntstorage.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory topInventory = e.getView().getTopInventory();
		Location chestLoc = getLocation(topInventory);
		if (topInventory.getTitle().equals(ChatColor.RED + "TNT Chest")) {

			if (e.getCurrentItem() == null || e.getCurrentItem().getType() == null) {
				return;
			}

			if (e.getCurrentItem().getType() == Material.TNT) {
				Faction factionAt = Board.getInstance().getFactionAt(new FLocation(chestLoc));
				new BukkitRunnable() {

					@Override
					public void run() {
						int amount = calculateAmount(topInventory, factionAt);
						factionAt.setTNT(factionAt.getTNT() + amount);
					}
				}.runTaskLater(P.p, 1L);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void hopperMoveItem(final InventoryMoveItemEvent e) {
		if ((e.getInitiator() == null) || (e.getSource() == null) || (e.getItem() == null)) {
			return;
		}
		if (e.getInitiator().getType() != InventoryType.HOPPER) {
			if ((e.getInitiator().getType() == InventoryType.CHEST)
					&& (e.getInitiator().getTitle().equals(ChatColor.RED + "TNT Chest"))) {
				e.setCancelled(true);
			}
			return;
		}
		if (e.getDestination().getTitle().equals(ChatColor.RED + "TNT Chest")) {

			Location loc = getLocation(e.getDestination());

			Faction fac = Board.getInstance().getFactionAt(new FLocation(loc));

			new BukkitRunnable() {

				@Override
				public void run() {
					int amount = calculateAmount(e.getDestination(), fac);
					fac.setTNT(fac.getTNT() + amount);
				}
			}.runTaskLater(P.p, 1L);
		}
	}

	public int calculateAmount(Inventory inv, Faction faction) {
		int size = inv.getSize();
		int amount = 0;

		for (int i = 0; i < size; i++) {
			if (inv.getItem(i) != null) {
				if (inv.getItem(i).getType() == Material.TNT) {
					amount += inv.getItem(i).getAmount();
				}
			}
		}

		if ((faction.getTNT() + amount) > faction.getMaxTNT()) {
			faction.setTNT(faction.getMaxTNT());
			return 0;
		}

		inv.remove(Material.TNT);

		return amount;
	}

	private Location getLocation(Inventory inventory) {
		InventoryHolder holder = inventory.getHolder();
		if (holder != null) {
			if ((holder instanceof Chest)) {
				return ((Chest) holder).getLocation();
			}
			if ((holder instanceof DoubleChest)) {
				return ((DoubleChest) holder).getLocation();
			}
			if ((holder instanceof BlockState)) {
				return ((BlockState) holder).getLocation();
			}
		}
		return null;
	}
}