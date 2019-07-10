package com.massivecraft.factions.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;

import mkremins.fanciful.FancyMessage;
import subside.plugins.koth.captureentities.CappingFactionUUID;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.gamemodes.RunningKoth.EndReason;

public class FactionsPlayerListener implements Listener {

	private ArrayList<UUID> playersFlying = new ArrayList<UUID>();
	private ArrayList<UUID> noFallDamagePlayers = new ArrayList<UUID>();
	public static ArrayList<UUID> stealthPlayer = new ArrayList<UUID>();

	public FactionsPlayerListener() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			initPlayer(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());

		new BukkitRunnable() {
			public void run() {
				Faction factionAt = Board.getInstance().getFactionAt(new FLocation(event.getPlayer()));
				FPlayer fp = FPlayers.getInstance().getByPlayer(event.getPlayer());

				if (!(factionAt.isWilderness() || factionAt.isWarZone() || factionAt.isSafeZone())) {
					if (!factionAt.getPermissions().hasPermission(fp, factionAt, "logout")) {
						if (System.currentTimeMillis() - fp.getLastLoginTime() > 300000) {
							Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
									"spawn " + fp.getPlayer().getName());
							fp.sendMessage(ChatColor.RED + "You were sent to spawn!");
						}
					}
				}
				flightListener(event.getPlayer().getLocation(), factionAt, fp);
			}
		}.runTaskLater(P.p, 5L);
	}

	private void initPlayer(Player player) {
		// Make sure that all online players do have a fplayer.
		final FPlayer me = FPlayers.getInstance().getByPlayer(player);
		((MemoryFPlayer) me).setName(player.getName());

		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());

		// Store player's current FLocation and notify them where they are
		me.setLastStoodAt(new FLocation(player.getLocation()));

		me.login(); // set kills / deaths

		// Check for Faction announcements. Let's delay this so they actually
		// see it.
		Bukkit.getScheduler().runTaskLater(P.p, new Runnable() {
			@Override
			public void run() {
				if (me.isOnline()) {
					me.getFaction().sendUnreadAnnouncements(me);
				}
			}
		}, 33L); // Don't ask me why.

		Faction myFaction = me.getFaction();

		if (!myFaction.isWilderness()) {
			for (FPlayer other : myFaction.getFPlayersWhereOnline(true)) {
				if (other != me && other.isMonitoringJoins()) {
					other.msg(TL.FACTION_LOGIN, me.getName());
				}
			}
		}

		if (me.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.node)) {
			me.setSpyingChat(false);
			P.p.log(Level.INFO, "Found %s spying chat without permission on login. Disabled their chat spying.",
					player.getName());
		}

		if (me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
			me.setIsAdminBypassing(false);
			P.p.log(Level.INFO, "Found %s on admin Bypass without permission on login. Disabled it for them.",
					player.getName());
		}

		// If they have the permission, don't let them autoleave. Bad inverted
		// setter :\
		me.setAutoLeave(!player.hasPermission(Permission.AUTO_LEAVE_BYPASS.node));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

		// Make sure player's power is up to date when they log off.
		me.getPower();
		// and update their last login time to point to when the logged off, for
		// auto-remove routine
		me.setLastLoginTime(System.currentTimeMillis());

		me.logout(); // cache kills / deaths

		if (playersFlying.contains(event.getPlayer().getUniqueId())) {
			playersFlying.remove(event.getPlayer().getUniqueId());
		}

		if (noFallDamagePlayers.contains(event.getPlayer().getUniqueId())) {
			noFallDamagePlayers.remove(event.getPlayer().getUniqueId());
		}

		// if player is waiting for fstuck teleport but leaves, remove
		if (P.p.getStuckMap().containsKey(me.getPlayer().getUniqueId())) {
			FPlayers.getInstance().getByPlayer(me.getPlayer()).msg(TL.COMMAND_STUCK_CANCELLED);
			P.p.getStuckMap().remove(me.getPlayer().getUniqueId());
			P.p.getTimers().remove(me.getPlayer().getUniqueId());
		}

		Faction myFaction = me.getFaction();
		if (!myFaction.isWilderness()) {
			myFaction.memberLoggedOff();
		}

		if (!myFaction.isWilderness()) {
			for (FPlayer player : myFaction.getFPlayersWhereOnline(true)) {
				if (player != me && player.isMonitoringJoins()) {
					player.msg(TL.FACTION_LOGOUT, me.getName());
				}
			}
		}
	}

	// Holds the next time a player can have a map shown.
	private HashMap<UUID, Long> showTimes = new HashMap<UUID, Long>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		FPlayer me = FPlayers.getInstance().getByPlayer(player);

		// clear visualization
		if (event.getFrom().getBlockX() != event.getTo().getBlockX()
				|| event.getFrom().getBlockY() != event.getTo().getBlockY()
				|| event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
			VisualizeUtil.clear(event.getPlayer());
			if (me.isWarmingUp()) {
				me.clearWarmup();
				me.msg(TL.WARMUPS_CANCELLED);
			}
		}

		// quick check to make sure player is moving between chunks; good
		// performance boost
		if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4
				&& event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4
				&& event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}

		Location locTo = event.getTo();
		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(locTo);

		if (from.equals(to)) {
			return;
		}

		// Yes we did change coord (:

		me.setLastStoodAt(to);

		// Did we change "host"(faction)?
		Faction factionFrom = Board.getInstance().getFactionAt(from);
		Faction factionTo = Board.getInstance().getFactionAt(to);
		boolean changedFaction = (factionFrom != factionTo);

		if (me.isMapAutoUpdating()) {
			if (showTimes.containsKey(player.getUniqueId())
					&& (showTimes.get(player.getUniqueId()) > System.currentTimeMillis())) {
				if (P.p.getConfig().getBoolean("findfactionsexploit.log", false)) {
					P.p.log(Level.WARNING, "%s tried to show a faction map too soon and triggered exploit blocker.",
							player.getName());
				}
			} else {
				List<FancyMessage> message = Board.getInstance().getMap(me.getFaction(), to,
						player.getLocation().getYaw());
				for (FancyMessage msg : message) {
					msg.send(me.getPlayer());
				}
				showTimes.put(player.getUniqueId(),
						System.currentTimeMillis() + P.p.getConfig().getLong("findfactionsexploit.cooldown", 2000));
			}
		} else {
			Faction myFaction = me.getFaction();
			String ownersTo = myFaction.getOwnerListString(to);

			if (changedFaction) {
				me.sendFactionHereMessage(factionFrom);
				if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo
						&& !ownersTo.isEmpty()) {
					me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
				}
			} else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && myFaction == factionTo
					&& !myFaction.isWilderness()) {
				String ownersFrom = myFaction.getOwnerListString(from);
				if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
					if (!ownersTo.isEmpty()) {
						me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
					} else if (!TL.GENERIC_PUBLICLAND.toString().isEmpty()) {
						me.sendMessage(TL.GENERIC_PUBLICLAND.toString());
					}
				}
			}
		}

		flightListener(locTo, factionTo, me);

		if (me.getAutoClaimFor() != null) {
			me.attemptClaim(me.getAutoClaimFor(), event.getTo(), true);
		} else if (me.isAutoSafeClaimEnabled()) {
			if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
				me.setIsAutoSafeClaimEnabled(false);
			} else {
				if (!Board.getInstance().getFactionAt(to).isSafeZone()) {
					Board.getInstance().setFactionAt(Factions.getInstance().getSafeZone(), to);
					me.msg(TL.PLAYER_SAFEAUTO);
				}
			}
		} else if (me.isAutoWarClaimEnabled()) {
			if (!Permission.MANAGE_WAR_ZONE.has(player)) {
				me.setIsAutoWarClaimEnabled(false);
			} else {
				if (!Board.getInstance().getFactionAt(to).isWarZone()) {
					Board.getInstance().setFactionAt(Factions.getInstance().getWarZone(), to);
					me.msg(TL.PLAYER_WARAUTO);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// only need to check right-clicks and physical as of MC 1.4+; good
		// performance boost
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) {
			return; // clicked in air, apparently
		}

		if (!canPlayerUseBlock(player, block, false)) {
			event.setCancelled(true);
			if (Conf.handleExploitInteractionSpam) {
				String name = player.getName();
				InteractAttemptSpam attempt = interactSpammers.get(name);
				if (attempt == null) {
					attempt = new InteractAttemptSpam();
					interactSpammers.put(name, attempt);
				}
				int count = attempt.increment();
				if (count >= 10) {
					FPlayer me = FPlayers.getInstance().getByPlayer(player);
					me.msg(TL.PLAYER_OUCH);
					player.damage(NumberConversions.floor((double) count / 10));
				}
			}
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return; // only interested on right-clicks for below
		}

		if (!playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
			event.setCancelled(true);
		}
	}

	// for handling people who repeatedly spam attempts to open a door (or
	// similar) in another faction's territory
	private Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();

	private static class InteractAttemptSpam {
		private int attempts = 0;
		private long lastAttempt = System.currentTimeMillis();

		// returns the current attempt count
		public int increment() {
			long Now = System.currentTimeMillis();
			if (Now > lastAttempt + 2000) {
				attempts = 1;
			} else {
				attempts++;
			}
			lastAttempt = Now;
			return attempts;
		}
	}

	public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck) {
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name)) {
			return true;
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		if (P.p.getConfig().getBoolean("hcf.raidable", false)
				&& otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		if (otherFaction.hasPlayersOnline()) {
			if (!Conf.territoryDenyUseageMaterials.contains(material)) {
				return true; // Item isn't one we're preventing for online
								// factions.
			}
		} else {
			if (!Conf.territoryDenyUseageMaterialsWhenOffline.contains(material)) {
				return true; // Item isn't one we're preventing for offline
								// factions.
			}
		}

		if (otherFaction.isWilderness()) {
			if (!Conf.wildernessDenyUseage
					|| Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you
								// like here.
			}

			if (!justCheck) {
				me.msg(TL.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.msg(TL.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.msg(TL.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
			}

			return false;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage()) {
			if (!justCheck) {
				me.msg(TL.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		return true;
	}

	public static boolean playerHasPermission(FPlayer fplayer, FLocation location, String action) {

		Faction otherFaction = Board.getInstance().getFactionAt(location);

		return otherFaction.getPermissions().hasPermission(fplayer, otherFaction, action);
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {
		if (Conf.playersWhoBypassAllProtection.contains(player.getName())) {
			return true;
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		Material material = block.getType();
		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		if (playerHasPermission(me, loc, "interact")) {
			return true;
		}

		// no door/chest/whatever protection in wilderness, war zones, or safe
		// zones
		if (!otherFaction.isNormal()) {
			return true;
		}

		if (P.p.getConfig().getBoolean("hcf.raidable", false)
				&& otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		// Dupe fix.
		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		if (!rel.isMember() || !otherFaction.playerHasOwnershipRights(me, loc) && player.getItemInHand() != null) {
			switch (player.getItemInHand().getType()) {
			case CHEST:
			case SIGN_POST:
			case TRAPPED_CHEST:
			case SIGN:
			case WOOD_DOOR:
			case IRON_DOOR:
				return false;
			default:
				break;
			}
		}

		// We only care about some material types.
		if (otherFaction.hasPlayersOnline()) {
			if (!Conf.territoryProtectedMaterials.contains(material)) {
				return true;
			}
		} else {
			if (!Conf.territoryProtectedMaterialsWhenOffline.contains(material)) {
				return true;
			}
		}

		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isEnemy() && Conf.territoryEnemyProtectMaterials)
				|| (rel.isAlly() && Conf.territoryAllyProtectMaterials)
				|| (rel.isTruce() && Conf.territoryTruceProtectMaterials)) {
			if (!justCheck) {
				me.msg(TL.PLAYER_USE_TERRITORY,
						(material == Material.SOIL ? "trample " : "use ") + TextUtil.getMaterialName(material),
						otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials
				&& !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

		me.getPower(); // update power, so they won't have gained any while dead

		Location home = me.getFaction().getHome();
		if (Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null && (Conf.homesRespawnFromNoPowerLossWorlds
				|| !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
			event.setRespawnLocation(home);
		}
	}

	// For some reason onPlayerInteract() sometimes misses bucket events
	// depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}
	}

	public static boolean preventCommand(String fullCmd, Player player) {
		if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty()
				&& Conf.permanentFactionMemberDenyCommands.isEmpty() && Conf.warzoneDenyCommands.isEmpty())) {
			return false;
		}

		fullCmd = fullCmd.toLowerCase();

		FPlayer me = FPlayers.getInstance().getByPlayer(player);

		String shortCmd; // command without the slash at the beginning
		if (fullCmd.startsWith("/")) {
			shortCmd = fullCmd.substring(1);
		} else {
			shortCmd = fullCmd;
			fullCmd = "/" + fullCmd;
		}

		if (me.hasFaction() && !me.isAdminBypassing() && !Conf.permanentFactionMemberDenyCommands.isEmpty()
				&& me.getFaction().isPermanent()
				&& isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_PERMANENT, fullCmd);
			return true;
		}

		Faction at = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
		if (at.isWilderness() && !Conf.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.wildernessDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_WILDERNESS, fullCmd);
			return true;
		}

		Relation rel = at.getRelationTo(me);
		if (at.isNormal() && rel.isAlly() && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_ALLY, fullCmd);
			return false;
		}

		if (at.isNormal() && rel.isNeutral() && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_NEUTRAL, fullCmd);
			return true;
		}

		if (at.isNormal() && rel.isEnemy() && !Conf.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_ENEMY, fullCmd);
			return true;
		}

		if (at.isWarZone() && !Conf.warzoneDenyCommands.isEmpty() && !me.isAdminBypassing()
				&& isCommandInList(fullCmd, shortCmd, Conf.warzoneDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_WARZONE, fullCmd);
			return true;
		}

		return false;
	}

	private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
		String cmdCheck;
		while (iter.hasNext()) {
			cmdCheck = iter.next();
			if (cmdCheck == null) {
				iter.remove();
				continue;
			}

			cmdCheck = cmdCheck.toLowerCase();
			if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		FPlayer badGuy = FPlayers.getInstance().getByPlayer(event.getPlayer());
		if (badGuy == null) {
			return;
		}

		// if player was banned (not just kicked), get rid of their stored info
		if (Conf.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin.")) {
			if (badGuy.getRole() == Role.ADMIN) {
				badGuy.getFaction().promoteNewLeader();
			}

			badGuy.leave(false);
			badGuy.remove();
		}
	}

	@EventHandler
	public void onLandClaim(LandClaimEvent e) {
		Player player = e.getfPlayer().getPlayer();
		new BukkitRunnable() {
			public void run() {
				flightListener(player.getLocation(), Board.getInstance().getFactionAt(new FLocation(player)),
						e.getfPlayer());
			}
		}.runTaskLater(P.p, 3L);
	}

	@EventHandler
	public void onLandUnclaim(LandUnclaimEvent e) {
		Player player = e.getfPlayer().getPlayer();
		new BukkitRunnable() {
			public void run() {
				flightListener(player.getLocation(), Board.getInstance().getFactionAt(new FLocation(player)),
						e.getfPlayer());
			}
		}.runTaskLater(P.p, 3L);
	}

	@EventHandler
	public void onFactionJoin(FPlayerJoinEvent e) {
		Player player = e.getfPlayer().getPlayer();
		new BukkitRunnable() {
			public void run() {
				flightListener(player.getLocation(), Board.getInstance().getFactionAt(new FLocation(player)),
						e.getfPlayer());
			}
		}.runTaskLater(P.p, 3L);
	}

	@EventHandler
	public void onFactionLeave(FPlayerLeaveEvent e) {
		Player player = e.getfPlayer().getPlayer();
		if (player == null)
			return;
		new BukkitRunnable() {
			public void run() {
				flightListener(player.getLocation(), Board.getInstance().getFactionAt(new FLocation(player)),
						e.getfPlayer());
			}
		}.runTaskLater(P.p, 3L);
	}

	@EventHandler
	public void onFactionDisband(FactionDisbandEvent e) {
		for (FPlayer player : e.getFaction().getFPlayers()) {
			new BukkitRunnable() {
				public void run() {
					flightListener(player.getPlayer().getLocation(),
							Board.getInstance().getFactionAt(new FLocation(player)), player);
				}
			}.runTaskLater(P.p, 3L);
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
		flightListener(player.getLocation(), Board.getInstance().getFactionAt(new FLocation(e.getTo())), fplayer);
	}

	public void flightListener(Location loc, Faction fac, FPlayer fp) {
		Player p = fp.getPlayer();
		Faction faction = fp.getFaction();

		if (p.hasPermission("factions.fly.bypass")) {
			return;
		}

		if (!p.hasPermission("factions.fly") && p.getAllowFlight()) {
			takeAwayFly(p);
			return;
		}

		if (!p.hasPermission("factions.fly") && (playersFlying.contains(p.getUniqueId()) || p.getAllowFlight())) {
			takeAwayFly(p);
			return;
		}

		int enemyX = 30;
		int enemyY = 255;
		int enemyZ = 30;

		if (!canGoInTerritory(fp, fac) && playersFlying.contains(p.getUniqueId())) {
			if (!p.hasPermission("factions.fly.bypassland"))
				takeAwayFly(p);
			return;
		}

		if (p.getLocation().getY() > 260 && playersFlying.contains(p.getUniqueId())) {
			takeAwayFly(p);
			return;
		}

		if (faction.isWilderness()) {
			if (playersFlying.contains(p.getUniqueId())) {
				takeAwayFly(p);
			}
			return;
		}

		Collection<Entity> closeEntities = loc.getWorld().getNearbyEntities(loc, enemyX, enemyY, enemyZ);

		boolean closeEnemy = false;
		for (Entity entity : closeEntities) {
			if (!(entity instanceof Player))
				continue;
			Player player = (Player) entity;

			if (player.getName().toLowerCase().startsWith("pvplogger"))
				continue;

			Faction opFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
			Relation relation = opFaction.getRelationTo(faction);
			if (relation.isAtLeast(Relation.TRUCE) || player.hasPermission("factions.fly.bypass")) {
				continue;
			} else {
				closeEnemy = true;
			}

			if (playersFlying.contains(player.getUniqueId()) && !p.hasPermission("factions.fly.bypass")
					&& !stealthPlayer.contains(p.getUniqueId()) && player.getGameMode() != GameMode.CREATIVE) {
				takeAwayFly(player);
				return;
			}

			if (playersFlying.contains(p.getUniqueId()) && !player.hasPermission("factions.fly.bypass")
					&& !stealthPlayer.contains(player.getUniqueId()) && p.getGameMode() != GameMode.CREATIVE) {
				takeAwayFly(p);
				return;
			}
		}
		if (!closeEnemy && p.hasPermission("factions.fly") && (canGoInTerritory(fp, fac))
				&& !playersFlying.contains(p.getUniqueId()) && p.getGameMode() != GameMode.CREATIVE) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour flight has been &aenabled!"));
			p.setAllowFlight(true);
			playersFlying.add(p.getUniqueId());
			noFallDamagePlayers.add(p.getUniqueId());
			return;
		}

		if (playersFlying.contains(p.getUniqueId())) {
			if (!p.getAllowFlight())
				p.setAllowFlight(true);
		}
	}

	public void takeAwayFly(final Player p) {
		if (playersFlying.contains(p.getUniqueId())) {
			playersFlying.remove(p.getUniqueId());
		}

		if (p.getAllowFlight())
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour flight has been &cdisabled!"));
		p.setAllowFlight(false);
		new BukkitRunnable() {

			@Override
			public void run() {
				if (noFallDamagePlayers.contains(p.getUniqueId()))
					noFallDamagePlayers.remove(p.getUniqueId());
			}
		}.runTaskLater(P.p, 20 * 10);
	}

	@EventHandler
	public void fallDamageHandler(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (e.getCause() == DamageCause.FALL && noFallDamagePlayers.contains(p.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

	public boolean canGoInTerritory(FPlayer fPlayer, Faction factionAt) {
		if (fPlayer.getPlayer().hasPermission("factions.fly.bypassland"))
			return true;
		if (factionAt.isWilderness() || factionAt.isWarZone() || factionAt.isSafeZone())
			return false;

		return factionAt.getPermissions().hasPermission(fPlayer, factionAt, "fly");
	}

	@EventHandler
	public void onPistonPush(BlockPistonExtendEvent e) {
		for (Block block : e.getBlocks()) {
			if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL
					|| block.getType() == Material.ANVIL) {
				Location loc = block.getLocation();
				for (int i = 0; i < 15; i++) {
					Location locPlus = loc.add(0, 1, 0);
					if (locPlus.getBlock().getType() == Material.SAND
							|| locPlus.getBlock().getType() == Material.GRAVEL) {
						if (i == 15 - 1) {
							e.setCancelled(true);
							return;
						}
					} else
						break;
				}
			}
		}
	}

	@EventHandler
	public void onPistonRet(BlockPistonRetractEvent e) {
		for (Block block : e.getBlocks()) {
			if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL
					|| block.getType() == Material.ANVIL) {
				Location loc = block.getLocation();
				for (int i = 0; i < 15; i++) {
					Location locPlus = loc.add(0, 1, 0);
					if (locPlus.getBlock().getType() == Material.SAND
							|| locPlus.getBlock().getType() == Material.GRAVEL) {
						if (i == 15 - 1) {
							e.setCancelled(true);
							return;
						}
					} else
						break;
				}
			}
		}
		Location loc = e.getBlock().getLocation();
		Location reLoc = e.getBlock().getLocation();
		switch (e.getDirection()) {
		case EAST:
			loc.add(1, 0, 0);
			reLoc.subtract(2, 0, 0);
			break;
		case NORTH:
			loc.subtract(0, 0, 1);
			reLoc.add(0, 0, 2);
			break;
		case SOUTH:
			loc.add(0, 0, 1);
			reLoc.subtract(0, 0, 2);
			break;
		case WEST:
			loc.subtract(1, 0, 0);
			reLoc.add(2, 0, 0);
			break;
		default:
			break;
		}
		loc.add(0, 1, 0);
		Block block = loc.getBlock();
		if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL
				|| block.getType() == Material.ANVIL) {
			for (int i = 0; i < 40; i++) {
				Location locPlus = loc.add(0, 1, 0);
				Block blockPlus = locPlus.getBlock();
				if (blockPlus.getType() == Material.SAND || blockPlus.getType() == Material.GRAVEL
						|| blockPlus.getType() == Material.ANVIL) {
					if (i == 40 - 1) {
						e.setCancelled(true);
						return;
					}
				} else
					break;
			}
		}
		reLoc.add(0, 1, 0);
		if (reLoc.getBlock().getType() == Material.SAND || reLoc.getBlock().getType() == Material.GRAVEL
				|| reLoc.getBlock().getType() == Material.ANVIL) {
			for (int i = 0; i < 40; i++) {
				Location locPlus = reLoc.add(0, 1, 0);
				Block blockPlus = locPlus.getBlock();
				if (blockPlus.getType() == Material.SAND || blockPlus.getType() == Material.GRAVEL
						|| blockPlus.getType() == Material.ANVIL) {
					if (i == 40 - 1) {
						e.setCancelled(true);
						return;
					}
				} else
					break;
			}
		}
	}

	@EventHandler
	public void onHome(PlayerCommandPreprocessEvent e) {
		FPlayer fp = FPlayers.getInstance().getByPlayer(e.getPlayer());

		if (fp.isAdminBypassing())
			return;

		String msg = e.getMessage().toLowerCase();

		if (msg.startsWith("/createhome") || msg.startsWith("/sethome") || msg.startsWith("/essentials:createhome")
				|| msg.startsWith("/essentials:sethome") || msg.startsWith("/ecreatehome")
				|| msg.startsWith("/esethome") || msg.startsWith("/essentials:ecreatehome")
				|| msg.startsWith("/essentials:esethome")) {
			Faction faction = Board.getInstance().getFactionAt(new FLocation(fp));
			if (!faction.getPermissions().hasPermission(fp, faction, "homes")) {
				e.setCancelled(true);
				fp.sendMessage(ChatColor.RED + "You are not permitted to set a home in this territory.");
				return;
			}
		}

		String[] args = e.getMessage().toLowerCase().split(" ");

		if (msg.startsWith("/home") || msg.startsWith("/homes") || msg.startsWith("/essentials:home")
				|| msg.startsWith("/essentials:homes") || msg.startsWith("/ehome") || msg.startsWith("/ehomes")
				|| msg.startsWith("/essentials:ehome") || msg.startsWith("/essentials:ehomes")) {
			File f = new File("plugins/Essentials/userdata/" + e.getPlayer().getUniqueId() + ".yml");
			if (!f.exists())
				return;
			YamlConfiguration playerData = YamlConfiguration.loadConfiguration(f);
			if (args.length == 1) {
				if (playerData.getConfigurationSection("homes") == null)
					return;
				if (playerData.getConfigurationSection("homes").getKeys(false).size() == 1) {
					for (String home : playerData.getConfigurationSection("homes").getKeys(false)) {
						double x = playerData.getDouble("homes." + home + ".x");
						double y = playerData.getDouble("homes." + home + ".y");
						double z = playerData.getDouble("homes." + home + ".z");
						World world = Bukkit.getWorld(playerData.getString("homes." + home + ".world"));
						Location loc = new Location(world, x, y, z);
						Faction fac = Board.getInstance().getFactionAt(new FLocation(loc));
						if (fac.getPermissions().hasPermission(fp, fac, "homes"))
							return;
						if (fac.isWilderness() || fac.isWarZone() || fac.isSafeZone())
							return;
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED
								+ "Another faction has claimed over your home! You will not be able to go to this home!");

					}
				} else
					return;
			} else if (args.length == 2) {
				String homeName = args[1].toLowerCase();
				if (playerData.getConfigurationSection("homes") == null)
					return;
				for (String home : playerData.getConfigurationSection("homes").getKeys(false)) {
					if (!homeName.equalsIgnoreCase(home))
						continue;
					double x = playerData.getDouble("homes." + home + ".x");
					double y = playerData.getDouble("homes." + home + ".y");
					double z = playerData.getDouble("homes." + home + ".z");
					World world = Bukkit.getWorld(playerData.getString("homes." + home + ".world"));
					Location loc = new Location(world, x, y, z);
					Faction fac = Board.getInstance().getFactionAt(new FLocation(loc));
					if (fac.getPermissions().hasPermission(fp, fac, "homes"))
						return;
					if (fac.isWilderness() || fac.isWarZone() || fac.isSafeZone())
						return;
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED
							+ "Another faction has claimed over your home! You will not be able to go to this home!");
				}
			}
		}
	}

	@EventHandler
	public void onTpOthers(PlayerCommandPreprocessEvent e) {
		String msg = e.getMessage().toLowerCase();

		if (msg.startsWith("/tpyes") || msg.startsWith("/tpaccept") || msg.startsWith("/tpahere")) {
			FPlayer fp = FPlayers.getInstance().getByPlayer(e.getPlayer());
			Faction fac = Board.getInstance().getFactionAt(new FLocation(fp));
			if (fac.isWilderness() || fac.isWarZone() || fac.isSafeZone())
				return;
			if (!fac.getPermissions().hasPermission(fp, fac, "tpothers")) {
				e.setCancelled(true);
				e.getPlayer()
						.sendMessage(ChatColor.RED + "You do not have permission to teleport others in this land!!");
			}
		}
	}

	@EventHandler
	public void onKothCapture(KothEndEvent e) {
		if (e.getReason() == EndReason.WON) {
			CappingFactionUUID faction = (CappingFactionUUID) e.getWinner();
			Faction fac = faction.getObject();
			fac.setEventPoints(fac.getEventPoints() + 2000);
			fac.setTokens(fac.getTokens() + 100);
		}
	}
}