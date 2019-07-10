package me.danelegend.core.timer.type;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionDisbandEvent;

import me.danelegend.core.timer.FactionTimer;
import me.danelegend.core.util.Config;

/**
 * Timer that handles TNT countdown
 */
public class TNTTimer extends FactionTimer implements Listener {

	public TNTTimer() {
		super("TNT", TimeUnit.MINUTES.toMillis(30L));
	}

	@Override
	public void onDisable(Config config) {
		super.onDisable(config);
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.AQUA + ChatColor.BOLD.toString();
	}

	@Override
	public void onExpire(String factionId) {
		Faction fac = Factions.getInstance().getFactionById(factionId);

		fac.setDurability(fac.getMaxDurability());
		fac.sendMessage("");
		fac.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"  &e* &cYou are now able to mine spawners and purchase faction shields"));
		fac.sendMessage("");
	}

	@EventHandler
	public void onFactionDisband(FactionDisbandEvent e) {
		if (this.getRemaining(e.getFaction()) > 0) {
			this.clearCooldown(e.getFaction());
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if (e.getEntity().getType() == EntityType.PRIMED_TNT) {
			Faction factionAt = Board.getInstance().getFactionAt(new FLocation(e.getEntity().getLocation()));

			if (!factionAt.isNormal()) {
				return;
			}

			TNTPrimed tnt = (TNTPrimed) e.getEntity();
			Faction target = Board.getInstance().getFactionAt(new FLocation(tnt.getSourceLoc()));

			if (target == factionAt) {
				return;
			}

			if (factionAt.hasShield()) {
				return;
			}

			if (!isWithinX(tnt.getSourceLoc().getChunk(), factionAt.getCoreLocation().getLocation().getChunk(), 5))

				if (this.getRemaining(factionAt) > 0) {

					factionAt.sendMessage("");
					factionAt.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e** &c&lATTENTION &e**"));
					factionAt.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&aAn explosion has gone off in your claim!"));
					factionAt.sendMessage("");

					target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour faction has activated &c"
							+ factionAt.getTag()
							+ "'s &eTNT timer and now are unable to pick up their faction core and mine their spawners!"));

					for (Player p : factionAt.getOnlinePlayers()) {
						p.getWorld().playSound(p.getLocation(), Sound.NOTE_PLING, 5F, 5F);
					}
				}

			setCooldown(factionAt, factionAt.getId(), defaultCooldown, true);
		} else if (e.getEntity().getType() == EntityType.CREEPER) {
			Faction factionAt = Board.getInstance().getFactionAt(new FLocation(e.getEntity().getLocation()));

			if (!factionAt.isNormal()) {
				return;
			}

			if (this.getRemaining(factionAt) > 0) {

				factionAt.sendMessage("");
				factionAt.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e** &c&lATTENTION &e**"));
				factionAt.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&aAn explosion has gone off in your claim!"));
				factionAt.sendMessage("");

				for (Player p : factionAt.getOnlinePlayers()) {
					p.getWorld().playSound(p.getLocation(), Sound.NOTE_PLING, 5F, 5F);
				}
			}

			setCooldown(factionAt, factionAt.getId(), defaultCooldown, true);
		}
	}

	private boolean isWithinX(Chunk one, Chunk two, int within) {
		if (one.getWorld().getName() != two.getWorld().getName())
			return false;
		int first = one.getX() - two.getX();
		int second = one.getZ() - two.getZ();
		if (Math.abs(first) <= within || Math.abs(second) <= within)
			return true;
		return false;
	}
}
