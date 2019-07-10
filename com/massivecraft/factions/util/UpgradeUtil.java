package com.massivecraft.factions.util;

public class UpgradeUtil {

	public static int[] rewards(int cost) {

		int points = cost / 200;

		int tokens = cost / 500;

		int[] rewards = new int[] { points, tokens };

		return rewards;
	}

	public static String[] durabilityCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 1500000, 150000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 3000000, 300000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 6000000, 600000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] kitCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 150000, 15000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 450000, 45000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 1100000, 110000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] xpBoost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 1200000, 120000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1700000, 170000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 2600000, 260000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] cropsCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 400000, 40000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1000000, 100000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 1500000, 150000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] mcMMOCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 800000, 80000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1200000, 120000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 2000000, 200000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] mobCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 1500000, 150000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 2000000, 200000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 3500000, 350000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] sizeCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 700000, 70000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1250000, 125000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 2000000, 200000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] powerCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 1250000, 125000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1750000, 175000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 2250000, 225000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] reducedCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 2000000, 200000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 4000000, 400000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 6000000, 600000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] increasedCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 2000000, 200000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 4000000, 400000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 6000000, 600000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] abilityCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 600000, 60000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1000000, 100000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 1250000, 125000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] tntCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 400000, 40000 });
		} else if (level == 1) {
			return NumberUtil.numberFormat(new int[] { 1000000, 100000 });
		} else if (level == 2) {
			return NumberUtil.numberFormat(new int[] { 1500000, 150000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}

	public static String[] vaultCost(int level) {
		if (level == 0) {
			return NumberUtil.numberFormat(new int[] { 1000000, 100000 });
		} else {
			return NumberUtil.numberFormat(new int[] { 0, 0 });
		}
	}
}