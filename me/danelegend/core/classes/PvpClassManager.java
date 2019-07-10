package me.danelegend.core.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.P;

import me.danelegend.core.classes.archer.ArcherClass;
import me.danelegend.core.classes.bard.BardClass;
import me.danelegend.core.classes.event.PvpClassEquipEvent;
import me.danelegend.core.classes.event.PvpClassUnequipEvent;
import me.danelegend.core.classes.type.MinerClass;
import me.danelegend.core.classes.type.RogueClass;

public class PvpClassManager implements Listener {
	private final Map<UUID, PvpClass> equippedClassMap;
	private final List<PvpClass> pvpClasses;

	public PvpClassManager(final P plugin) {
		this.equippedClassMap = new HashMap<UUID, PvpClass>();
		(this.pvpClasses = new ArrayList<PvpClass>()).add(new BardClass(plugin));
		pvpClasses.add(new ArcherClass(plugin));
		pvpClasses.add(new MinerClass(plugin));
		pvpClasses.add(new RogueClass(plugin));
		Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) plugin);
		for (final PvpClass pvpClass : this.pvpClasses) {
			if (!(pvpClass instanceof Listener)) {
				continue;
			}
			plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, (Plugin) plugin);
		}
	}

	public void onDisable() {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			this.setEquippedClass(p, null);
		}
		this.pvpClasses.clear();
		this.equippedClassMap.clear();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		this.setEquippedClass(event.getEntity(), null);
	}

	public Collection<PvpClass> getPvpClasses() {
		return this.pvpClasses;
	}

	public PvpClass getEquippedClass(final Player player) {
		final Map<UUID, PvpClass> map = this.equippedClassMap;
		synchronized (map) {
			// monitorexit(map)
			return this.equippedClassMap.get(player.getUniqueId());
		}
	}

	public boolean hasClassEquipped(final Player player, final PvpClass pvpClass) {
		return this.getEquippedClass(player) == pvpClass;
	}

	public void setEquippedClass(final Player player, @Nullable final PvpClass pvpClass) {
		if (pvpClass == null) {
			final PvpClass equipped = this.equippedClassMap.remove(player.getUniqueId());
			if (equipped != null) {
				equipped.onUnequip(player);
				Bukkit.getPluginManager().callEvent((Event) new PvpClassUnequipEvent(player, equipped));
			}
		} else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
			this.equippedClassMap.put(player.getUniqueId(), pvpClass);
			Bukkit.getPluginManager().callEvent((Event) new PvpClassEquipEvent(player, pvpClass));
		}
	}
}