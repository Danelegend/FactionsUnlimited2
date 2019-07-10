package com.massivecraft.factions.util;

import org.bukkit.inventory.*;
import org.bukkit.enchantments.*;

public class Glow extends Enchantment {

	public Glow(final int id) {
		super(id);
	}

	public boolean canEnchantItem(final ItemStack arg0) {
		return true;
	}

	public boolean conflictsWith(final Enchantment arg0) {
		return false;
	}

	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ALL;
	}

	public int getMaxLevel() {
		return 1;
	}

	public String getName() {
		return "Glow";
	}

	public int getStartLevel() {
		return 1;
	}
}
