package com.massivecraft.factions;

public class Upgrade {

	private int durability = 0; // 5
	private int xp = 0; // 3
	private int crops = 0;// 2
	private int mcmmo = 0; // 3
	private int mobDrops = 0; // 4
	private int factionSize = 0; // 1
	private int power = 0; // 2
	private int reducedDamage = 0; // 4
	private int increasedDamage = 0; // 5
	private int shorterCooldowns = 0; // 1
	private int tntStorage = 0; // 2
	private int factionVault = 0; // 3

	public int getTotalAmount() {
		return xp + crops + mcmmo + mobDrops + factionSize + power + reducedDamage + increasedDamage
				+ shorterCooldowns + tntStorage + factionVault;
	}

	public int getMaxAmount() {
		return 36;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	public int getXPLevel() {
		return this.xp;
	}

	public void setXPLevel(int level) {
		this.xp = level;
	}

	public int getCropsLevel() {
		return this.crops;
	}

	public void setCropsLevel(int level) {
		this.crops = level;
	}

	public int getmcMMOLevel() {
		return this.mcmmo;
	}

	public void setmcMMOLevel(int level) {
		this.mcmmo = level;
	}

	public int getMobDrops() {
		return mobDrops;
	}

	public void setMobDrops(int mobDrops) {
		this.mobDrops = mobDrops;
	}

	public int getFactionSize() {
		return factionSize;
	}

	public void setFactionSize(int factionSize) {
		this.factionSize = factionSize;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getReducedDamage() {
		return reducedDamage;
	}

	public void setReducedDamage(int reducedDamage) {
		this.reducedDamage = reducedDamage;
	}

	public int getIncreasedDamage() {
		return increasedDamage;
	}

	public void setIncreasedDamage(int increasedDamage) {
		this.increasedDamage = increasedDamage;
	}

	public int getShorterCooldowns() {
		return shorterCooldowns;
	}

	public void setShorterCooldowns(int shorterCooldowns) {
		this.shorterCooldowns = shorterCooldowns;
	}

	public int getTNTStorage() {
		return tntStorage;
	}

	public void setTNTStorage(int tntStorage) {
		this.tntStorage = tntStorage;
	}

	public int getFactionVault() {
		return factionVault;
	}

	public void setFactionVault(int factionVault) {
		this.factionVault = factionVault;
	}
}