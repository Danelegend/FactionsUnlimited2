package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.LazyLocation;

public interface Faction extends EconomyParticipator {
    
    public boolean isSystemFaction();
	
	public HashMap<String, List<String>> getAnnouncements();

	public ConcurrentHashMap<String, LazyLocation> getWarps();

	public LazyLocation getWarp(String name);

	public void setWarp(String name, LazyLocation loc);

	public boolean isWarp(String name);

	public boolean removeWarp(String name);

	public void clearWarps();

	public ItemStack getWarpIcon(String warpName);

	public void setWarpIcon(String name, ItemStack itemstack);

	public List<String> getRules();

	public int getDurability();

	public void setDurability(int amount);

	public int getMaxDurability();

	public void setMaxDurability(int amount);

	public boolean isRaidable();

	public void setRaidable(boolean raidable);
	
	public /*Faction - Rather use string instead of faction object to prevent memory issues*/String getLastRaider(); 
	
	public void setLastRaider(/*Faction - Rather use string instead of faction object to prevent memory issues*/String raiderName);

	public abstract int getPoints();

	public abstract boolean isPlaced();

	public abstract void setPlaced(boolean placed);

	public abstract int getTokens();

	public abstract void setTokens(int tokens);

	public abstract int getTNT();

	public abstract void setTNT(int tnt);

	public abstract int getMaxTNT();

	public abstract void setMaxTNT(int tnt);

	public abstract LazyLocation getCoreLocation();

	public abstract void setCoreLocation(LazyLocation loc);

	public abstract boolean hasShield();

	public abstract void setShield(boolean shield);

	public abstract int getTotalXP();

	public abstract void setTotalXP(int xp);

	public abstract int getWarzoneClaims();

	public abstract void setWarzoneClaims(int claims);

	public abstract int getLastMcMMO();

	public abstract void setLastMcMMO(int mcmmo);

	public abstract int getEventPoints();

	public abstract void setEventPoints(int points);

	public abstract int getRaidingPoints();

	public abstract void setRaidingPoints(int points);
	
	public abstract int getUpgradePoints();

	public abstract void setUpgradePoints(int points);

	public abstract Upgrade getUpgrades();

	public abstract Perm getPermissions();

	public int getMaxVaults();

	public void setMaxVaults(int value);

	public void addAnnouncement(FPlayer fPlayer, String msg);

	public void sendUnreadAnnouncements(FPlayer fPlayer);

	public void removeAnnouncements(FPlayer fPlayer);

	public Set<String> getInvites();

	public String getId();

	public void invite(FPlayer fplayer);

	public void deinvite(FPlayer fplayer);

	public boolean isInvited(FPlayer fplayer);

	public boolean getOpen();

	public void setOpen(boolean isOpen);

	public boolean isPeaceful();

	public void setPeaceful(boolean isPeaceful);

	public void setPeacefulExplosionsEnabled(boolean val);

	public boolean getPeacefulExplosionsEnabled();

	public boolean noExplosionsInTerritory();

	public boolean isPermanent();

	public void setPermanent(boolean isPermanent);

	public String getTag();

	public String getTag(String prefix);

	public String getTag(Faction otherFaction);

	public String getTag(FPlayer otherFplayer);

	public void setTag(String str);

	public String getComparisonTag();

	public String getDescription();

	public void setDescription(String value);

	public void setHome(Location home);

	public boolean hasHome();

	public Location getHome();

	public long getFoundedDate();

	public void setFoundedDate(long newDate);

	public void confirmValidHome();

	public String getAccountId();

	public Integer getPermanentPower();

	public void setPermanentPower(Integer permanentPower);

	public boolean hasPermanentPower();

	public double getPowerBoost();

	public void setPowerBoost(double powerBoost);

	public boolean noPvPInTerritory();

	public boolean noMonstersInTerritory();

	public boolean isNormal();

	@Deprecated
	public boolean isNone();

	public boolean isWilderness();

	public boolean isSafeZone();

	public boolean isWarZone();

	public boolean isPlayerFreeType();

	public boolean isPowerFrozen();

	public void setLastDeath(long time);

	public int getKills();

	public int getDeaths();
	
	public double getFactionWealth();

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst);

	@Override
	public String describeTo(RelationParticipator that);

	@Override
	public Relation getRelationTo(RelationParticipator rp);

	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

	@Override
	public ChatColor getColorTo(RelationParticipator rp);

	public Relation getRelationWish(Faction otherFaction);

	public void setRelationWish(Faction otherFaction, Relation relation);

	public int getRelationCount(Relation relation);

	// ----------------------------------------------//
	// Power
	// ----------------------------------------------//
	public double getPower();

	public double getPowerMax();

	public int getPowerRounded();

	public int getPowerMaxRounded();

	public int getLandRounded();

	public int getLandRoundedInWorld(String worldName);

	public boolean hasLandInflation();

	// -------------------------------
	// FPlayers
	// -------------------------------

	// maintain the reference list of FPlayers in this faction
	public void refreshFPlayers();

	public boolean addFPlayer(FPlayer fplayer);

	public boolean removeFPlayer(FPlayer fplayer);

	public int getSize();

	public Set<FPlayer> getFPlayers();

	public Set<FPlayer> getFPlayersWhereOnline(boolean online);

	public FPlayer getFPlayerAdmin();

	public ArrayList<FPlayer> getFPlayersWhereRole(Role role);

	public ArrayList<Player> getOnlinePlayers();

	// slightly faster check than getOnlinePlayers() if you just want to see if
	// there are any players online
	public boolean hasPlayersOnline();

	public void memberLoggedOff();

	// used when current leader is about to be removed from the faction;
	// promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader();

	// ----------------------------------------------//
	// Messages
	// ----------------------------------------------//
	public void msg(String message, Object... args);

	public void sendMessage(String message);

	public void sendMessage(List<String> messages);

	// ----------------------------------------------//
	// Ownership of specific claims
	// ----------------------------------------------//

	public Map<FLocation, Set<String>> getClaimOwnership();

	public void clearAllClaimOwnership();

	public void clearClaimOwnership(FLocation loc);

	public void clearClaimOwnership(FPlayer player);

	public int getCountOfClaimsWithOwners();

	public boolean doesLocationHaveOwnersSet(FLocation loc);

	public boolean isPlayerInOwnerList(FPlayer player, FLocation loc);

	public void setPlayerAsOwner(FPlayer player, FLocation loc);

	public void removePlayerAsOwner(FPlayer player, FLocation loc);

	public Set<String> getOwnerList(FLocation loc);

	public String getOwnerListString(FLocation loc);

	public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc);

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	public void remove();

	public Set<FLocation> getAllClaims();

	public void setId(String id);
}
