package com.massivecraft.factions.zcore.persist.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.TerritoryAccess;

public class JSONTerritoryAccess {
	private static transient File file = new File(P.p.getDataFolder(), "territoryaccess.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

	private Map<FLocation, TerritoryAccess> map = Board.getInstance().getAccessMap();

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //

	public void saveAll() {
		String serialized;
		String info;
		for (FLocation floc : map.keySet()) {
			String worldName = floc.getWorldName();
			long x = floc.getX();
			long z = floc.getZ();

			String factionId = map.get(floc).getHostFactionId();
			boolean isAllowed = map.get(floc).isHostFactionAllowed();
			Set<String> players = map.get(floc).getPlayerIds();
			Set<String> factions = map.get(floc).getFactionIds();

			List<String> playerIds = new ArrayList<String>();
			List<String> factionIds = new ArrayList<String>();

			for (String s : players)
				playerIds.add(s);

			for (String s : factions)
				factionIds.add(s);

			serialized = worldName + "," + x + "," + z + "," + factionId;
			info = worldName + "," + x + "," + z + "," + isAllowed + "," + factionId;

			config.set("TerritoryAccess." + serialized + ".info", info);
			config.set("TerritoryAccess." + serialized + ".players", playerIds);
			config.set("TerritoryAccess." + serialized + ".factions", factionIds);
		}
		save();
	}

	public void loadAll() {
		for (String title : config.getConfigurationSection("TerritoryAccess").getKeys(false)) {

			String info = config.getString("TerritoryAccess." + title + ".info");
			String[] parts = info.split(",");
			String worldName = parts[0];
			int x = Integer.valueOf(parts[1]);
			int z = Integer.valueOf(parts[2]);

			boolean isAllowed = Boolean.valueOf(parts[3]);

			String factionId = parts[4];

			List<String> playerIds = config.getStringList("TerritoryAccess." + title + ".players");
			List<String> factionIds = config.getStringList("TerritoryAccess." + title + ".factions");

			Board.getInstance().setTerritoryAccessAt(new FLocation(worldName, x, z),
					new TerritoryAccess(factionId, isAllowed, factionIds, playerIds));
		}
		file.delete();
	}

	public static void create() {
		if (!P.p.getDataFolder().exists())
			P.p.getDataFolder().mkdir();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error creating " + file.getName() + "!");
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	public static void save() {
		try {
			config.save(file);
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error saving " + file.getName() + "!");
		}
	}
}