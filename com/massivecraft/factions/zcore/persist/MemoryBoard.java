package com.massivecraft.factions.zcore.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.util.LazyLocation;

import mkremins.fanciful.FancyMessage;

public abstract class MemoryBoard extends Board {

	public class MemoryBoardMap extends HashMap<FLocation, String> {
		private static final long serialVersionUID = -6689617828610585368L;
		Multimap<String, FLocation> factionToLandMap = HashMultimap.create();

		@Override
		public String put(FLocation floc, String factionId) {
			String previousValue = super.put(floc, factionId);
			if (previousValue != null) {
				factionToLandMap.remove(previousValue, floc);
			}

			factionToLandMap.put(factionId, floc);
			return previousValue;
		}

		@Override
		public String remove(Object key) {
			String result = super.remove(key);
			if (result != null) {
				FLocation floc = (FLocation) key;
				factionToLandMap.remove(result, floc);
			}

			return result;
		}

		@Override
		public void clear() {
			super.clear();
			factionToLandMap.clear();
		}

		public int getOwnedLandCount(String factionId) {
			return factionToLandMap.get(factionId).size();
		}

		public void removeFaction(String factionId) {
			Collection<FLocation> flocations = factionToLandMap.removeAll(factionId);
			for (FLocation floc : flocations) {
				super.remove(floc);
			}
		}
	}

	public MemoryBoardMap flocationIds = new MemoryBoardMap();
	public Map<FLocation, TerritoryAccess> map = new HashMap<FLocation, TerritoryAccess>();

	// ----------------------------------------------//
	// Get and Set
	// ----------------------------------------------//
	public String getIdAt(FLocation flocation) {
		if (!flocationIds.containsKey(flocation)) {
			return "0";
		}

		return flocationIds.get(flocation);
	}

	public Faction getFactionAt(FLocation flocation) {
		return Factions.getInstance().getFactionById(getIdAt(flocation));
	}

	public void setIdAt(String id, FLocation flocation) {
		clearOwnershipAt(flocation);

		if (id.equals("0")) {
			removeAt(flocation);
		}

		flocationIds.put(flocation, id);
	}

	@Override
	public Map<FLocation, TerritoryAccess> getAccessMap() {
		return this.map;
	}

	public void setFactionAt(Faction faction, FLocation flocation) {
		setIdAt(faction.getId(), flocation);
	}

	@Override
	public TerritoryAccess getTerritoryAccessAt(FLocation fLoc) {
		if (fLoc == null)
			return null;
		TerritoryAccess ret = this.map.get(fLoc);
		if (ret == null || ret.getHostFaction() == null)
			ret = TerritoryAccess.valueOf(getFactionAt(fLoc).getId());
		return ret;
	}

	@Override
	public void setTerritoryAccessAt(FLocation fLoc, TerritoryAccess territoryAccess) {

		if (this.map.get(fLoc) != null) {
			this.map.remove(fLoc);
			this.map.put(fLoc, territoryAccess);
		} else {
			this.map.put(fLoc, territoryAccess);
		}
	}

	@Override
	public void removeTerritoryAccess(FLocation fLoc) {
		if (this.map.containsKey(fLoc)) {
			this.map.remove(fLoc);
		}
	}

	public void removeAt(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		Iterator<LazyLocation> it = faction.getWarps().values().iterator();
		while (it.hasNext()) {
			if (flocation.isInChunk(it.next().getLocation())) {
				it.remove();
			}
		}
		clearOwnershipAt(flocation);
		flocationIds.remove(flocation);
	}

	public Set<FLocation> getAllClaims(String factionId) {
		Set<FLocation> locs = new HashSet<FLocation>();
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId)) {
				locs.add(entry.getKey());
			}
		}
		return locs;
	}

	public Set<FLocation> getAllClaims(Faction faction) {
		return getAllClaims(faction.getId());
	}

	// not to be confused with claims, ownership referring to further
	// member-specific ownership of a claim
	public void clearOwnershipAt(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		if (faction != null && faction.isNormal()) {
			faction.clearClaimOwnership(flocation);
		}
	}

	public void unclaimAll(String factionId) {
		Faction faction = Factions.getInstance().getFactionById(factionId);
		if (faction != null && faction.isNormal()) {
			faction.clearAllClaimOwnership();
			faction.clearWarps();
		}
		clean(factionId);
	}

	public void clean(String factionId) {
		flocationIds.removeFaction(factionId);
	}

	// Is this coord NOT completely surrounded by coords claimed by the same
	// faction?
	// Simpler: Is there any nearby coord with a faction other than the faction
	// here?
	public boolean isBorderLocation(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c)
				|| faction != getFactionAt(d);
	}

	// Is this coord connected to any coord claimed by the specified faction?
	public boolean isConnectedLocation(FLocation flocation, Faction faction) {
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c)
				|| faction == getFactionAt(d);
	}

	/**
	 * Checks if there is another faction within a given radius other than
	 * Wilderness. Used for HCF feature that requires a 'buffer' between factions.
	 *
	 * @param flocation
	 *            - center location.
	 * @param faction
	 *            - faction checking for.
	 * @param radius
	 *            - chunk radius to check.
	 *
	 * @return true if another Faction is within the radius, otherwise false.
	 */
	public boolean hasFactionWithin(FLocation flocation, Faction faction, int radius) {
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (x == 0 && z == 0) {
					continue;
				}

				FLocation relative = flocation.getRelative(x, z);
				Faction other = getFactionAt(relative);

				if (other.isNormal() && other != faction) {
					return true;
				}
			}
		}
		return false;
	}

	// ----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	// ----------------------------------------------//

	public void clean() {
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (!Factions.getInstance().isValidFactionId(entry.getValue())) {
				P.p.log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
				iter.remove();
			}
		}
	}

	// ----------------------------------------------//
	// Coord count
	// ----------------------------------------------//

	public int getFactionCoordCount(String factionId) {
		return flocationIds.getOwnedLandCount(factionId);
	}

	public int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.getId());
	}

	public int getFactionCoordCountInWorld(Faction faction, String worldName) {
		String factionId = faction.getId();
		int ret = 0;
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId) && entry.getKey().getWorldName().equals(worldName)) {
				ret += 1;
			}
		}
		return ret;
	}

	// ----------------------------------------------//
	// Map generation
	// ----------------------------------------------//

	/**
	 * The map is relative to a coord and a faction north is in the direction of
	 * decreasing x east is in the direction of decreasing z
	 */
	public ArrayList<FancyMessage> getMap(Faction faction, FLocation flocation, double inDegrees) {
		ArrayList<FancyMessage> ret = new ArrayList<FancyMessage>();
		Faction factionLoc = getFactionAt(flocation);
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, P.p.txt.parse("<a>"));

		FancyMessage tx = new FancyMessage(
				P.p.txt.titleize("(" + flocation.getCoordString() + ") " + factionLoc.getTag(faction)));

		ret.add(tx);

		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		FLocation topLeft = flocation.getRelative(-halfWidth, -halfHeight);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;

		if (Conf.showMapFactionKey) {
			height--;
		}

		Map<Faction, Character> fList = new HashMap<Faction, Character>();
		int chrIdx = 0;

		// For each row
		boolean newLine;
		for (int dz = 0; dz < height; dz++) {
			newLine = true;
			boolean compassLine = false;

			int i = 0;
			FancyMessage rowJSON = new FancyMessage();
			for (int dx = 0; dx < width; dx++) {
				FLocation herePs = topLeft.getRelative(dx, dz);
				if ((dx == halfWidth) && (dz == halfHeight)) {
					rowJSON.then(ChatColor.AQUA + "+");
				} else {
					Faction hereFaction = getFactionAt(herePs);
					if ((hereFaction == null) || (hereFaction.isWilderness())) {
						if (newLine) {
							if (ret.size() == 1) {
								compassLine = true;
								i++;
								rowJSON.text((String) asciiCompass.get(0));
							} else if (ret.size() == 2) {
								compassLine = true;
								i++;
								rowJSON.text((String) asciiCompass.get(1));
							} else if (ret.size() == 3) {
								compassLine = true;
								i++;
								rowJSON.text((String) asciiCompass.get(2));
							} else {
								rowJSON.text(ChatColor.GRAY + "-").tooltip("Click to claim")
										.command("/f claimchunk " + herePs.getX() + " " + herePs.getZ());
							}
						} else if (!compassLine) {
							rowJSON.then(ChatColor.GRAY + "-").tooltip("Click to claim")
									.command("/f claimchunk " + herePs.getX() + " " + herePs.getZ());
						} else if ((compassLine) && (i > 2)) {
							i++;

							rowJSON.then(ChatColor.GRAY + "-").tooltip("Click to claim")
									.command("/f claimchunk " + herePs.getX() + " " + herePs.getZ());
						} else {
							i++;
						}
					} else {
						if (!fList.containsKey(hereFaction)) {
							fList.put(hereFaction, Character.valueOf(Conf.mapKeyChrs[(chrIdx++)]));
						}
						char fchar = ((Character) fList.get(hereFaction)).charValue();

						if (newLine) {
							if (ret.size() == 1) {
								compassLine = true;
								i++;
								rowJSON.text((String) asciiCompass.get(0));
							} else if (ret.size() == 2) {
								compassLine = true;
								i++;
								rowJSON.text((String) asciiCompass.get(1));
							} else if (ret.size() == 3) {
								compassLine = true;
								i++;
								rowJSON.text((String) asciiCompass.get(2));
							} else {
								if (!hereFaction.hasShield()) {
									if (hereFaction == faction) {
										rowJSON.text(hereFaction.getColorTo(faction) + "" + fchar)
												.tooltip("Click to unclaim")
												.command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
									} else {
										rowJSON.text(hereFaction.getColorTo(faction) + "" + fchar);
									}
								} else {
									if (hereFaction == faction) {
										rowJSON.text(ChatColor.BLUE + "" + fchar).tooltip("Click to unclaim")
												.command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
									} else {
										rowJSON.text(ChatColor.BLUE + "" + fchar);
									}
								}
							}
						} else if (!compassLine) {
							if (!hereFaction.hasShield()) {
								if (hereFaction == faction) {
									rowJSON.then(hereFaction.getColorTo(faction) + "" + fchar)
											.tooltip("Click to unclaim")
											.command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
								} else {
									rowJSON.then(hereFaction.getColorTo(faction) + "" + fchar);
								}
							} else {
								if (hereFaction == faction) {
									rowJSON.then(ChatColor.BLUE + "" + fchar).tooltip("Click to unclaim")
											.command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
								} else {
									rowJSON.then(ChatColor.BLUE + "" + fchar);
								}
							}
						} else if ((compassLine) && (i > 2)) {
							i++;

							if (!hereFaction.hasShield()) {
								if (hereFaction == faction) {
									rowJSON.then(hereFaction.getColorTo(faction) + "" + fchar)
											.tooltip("Click to unclaim")
											.command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
								} else {
									rowJSON.then(hereFaction.getColorTo(faction) + "" + fchar);
								}
							} else {
								if (hereFaction == faction) {
									rowJSON.then(ChatColor.BLUE + "" + fchar).tooltip("Click to unclaim")
											.command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
								} else {
									rowJSON.then(ChatColor.BLUE + "" + fchar);
								}
							}
						} else {
							i++;
						}
					}
					newLine = false;
				}
			}
			ret.add(rowJSON);
		}
		String fRow = "";
		for (Faction keyfaction : fList.keySet()) {
			if (!keyfaction.hasShield()) {
				fRow = fRow + "" + keyfaction.getColorTo(faction) + fList.get(keyfaction) + ": " + keyfaction.getTag()
						+ " ";
			} else {
				fRow = fRow + "" + ChatColor.BLUE + fList.get(keyfaction) + ": " + keyfaction.getTag() + " ";
			}
		}
		fRow = fRow.trim();
		ret.add(new FancyMessage(fRow));

		return ret;
	}

	/**
	 * if (!hereFaction.hasShield()) { if (hereFaction == faction) {
	 * rowJSON.then(hereFaction.getColorTo(faction) + "" + fchar) .tooltip("Click to
	 * unclaim") .command("/f unclaimchunk " + herePs.getX() + " " + herePs.getZ());
	 * } else { rowJSON.then(hereFaction.getColorTo(faction) + "" + fchar); } } else
	 * { if (hereFaction == faction) { rowJSON.then(ChatColor.BLUE + "" +
	 * fchar).tooltip("Click to unclaim") .command("/f unclaimchunk " +
	 * herePs.getX() + " " + herePs.getZ()); } else { rowJSON.then(ChatColor.BLUE +
	 * "" + fchar); } }
	 * 
	 * @param old
	 */

	public abstract void convertFrom(MemoryBoard old);
}