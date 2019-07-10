package me.danelegend.core.grace;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.google.common.collect.ImmutableList;
import com.massivecraft.factions.P;

import me.danelegend.core.util.BukkitUtils;
import me.danelegend.core.util.JavaUtils;

public class GraceCommand implements CommandExecutor, TabCompleter {

	private static final List<String> COMPLETIONS = ImmutableList.of("start", "end");

	private final P plugin;

	public GraceCommand(P plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.hasPermission("era.staff")) {
			return false;
		}

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("start")) {
				if (args.length < 2) {
					sender.sendMessage(
							ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <duration>");
					return true;
				}

				long duration = JavaUtils.parse(args[1]);

				if (duration == -1L) {
					sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
					return true;
				}

				if (duration < 1000L) {
					sender.sendMessage(ChatColor.RED + "Grace time must last for at least 20 ticks.");
					return true;
				}

				GraceTimer.GraceRunnable graceRunnable = plugin.getGraceTimer().getGraceRunnable();

				if (graceRunnable != null) {
					sender.sendMessage(
							ChatColor.RED + "Grace is already enabled, use /" + label + " cancel to end it.");
					return true;
				}

				plugin.getGraceTimer().start(duration);
				sender.sendMessage(ChatColor.RED + "Started Grace for "
						+ DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
				return true;
			}

			if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) {
				if (plugin.getGraceTimer().cancel()) {
					Bukkit.broadcastMessage(
							ChatColor.translateAlternateColorCodes('&', "&7&m---------------------------------------"));
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
							"&eThe &6&lCannons are now &eENABLED. &6&lGood luck&e!"));
					Bukkit.broadcastMessage(
							ChatColor.translateAlternateColorCodes('&', "&7&m---------------------------------------"));
					return true;
				}

				sender.sendMessage(ChatColor.RED + "Grace is not active.");
				return true;
			}
		}

		sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <start|end>");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
	}
}