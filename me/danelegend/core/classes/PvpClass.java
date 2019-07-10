package me.danelegend.core.classes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.bukkit.potion.PotionEffect;
import java.util.Set;

public abstract class PvpClass {
	public static final long DEFAULT_MAX_DURATION;
	protected final Set<PotionEffect> passiveEffects;
	protected final String name;
	protected final long warmupDelay;

	static {
		DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
	}

	public PvpClass(final String name, final long warmupDelay) {
		this.passiveEffects = new HashSet<PotionEffect>();
		this.name = name;
		this.warmupDelay = warmupDelay;
	}

	public String getName() {
		return this.name;
	}

	public long getWarmupDelay() {
		return this.warmupDelay;
	}

	public boolean onEquip(final Player player) {
		for (final PotionEffect effect : this.passiveEffects) {
			player.addPotionEffect(effect, true);
		}
		String translate = "&b" + this.name + " &6has been activated and is now ready for use!";
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', translate));
		return true;
	}

	public void onUnequip(final Player player) {
		for (final PotionEffect effect : this.passiveEffects) {
			for (final PotionEffect active : player.getActivePotionEffects()) {
				if (active.getDuration() > PvpClass.DEFAULT_MAX_DURATION
						&& active.getType().equals((Object) effect.getType())) {
					if (active.getAmplifier() != effect.getAmplifier()) {
						continue;
					}
					player.removePotionEffect(effect.getType());
					break;
				}
			}
		}
		String translate = "  &e* &b" + this.name + " &6has been deactivated!";
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', translate));
	}

	public abstract boolean isApplicableFor(final Player p0);
}