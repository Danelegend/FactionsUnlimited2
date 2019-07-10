package me.danelegend.core.timer.type;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Predicate;

import me.danelegend.core.timer.PlayerTimer;
import me.danelegend.core.util.time.DurationFormatter;

/**
 * Timer used to prevent {@link Player}s from using Golden Apples too often.
 */
public class AppleTimer extends PlayerTimer implements Listener {

	// private final ImageMessage goppleArtMessage;

	public AppleTimer(JavaPlugin plugin) {
		super("Apple", TimeUnit.SECONDS.toMillis(5L));
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.YELLOW.toString() + ChatColor.BOLD;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerConsume(PlayerItemConsumeEvent event) {
		ItemStack stack = event.getItem();
		if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 0) {
			Player player = event.getPlayer();
			if (setCooldown(player, player.getUniqueId(), defaultCooldown, false, new Predicate<Long>() {
				@Override
				public boolean apply(@Nullable Long value) {
					return false;
				}
			})) {

			} else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You still have a " + getDisplayName() + ChatColor.RED
						+ " cooldown for another " + ChatColor.BOLD + ChatColor.YELLOW
						+ DurationFormatter.getRemaining(getRemaining(player), true, false) + ChatColor.RED + '.');
			}
		}
	}
}
