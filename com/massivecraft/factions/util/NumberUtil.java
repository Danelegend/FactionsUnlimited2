package com.massivecraft.factions.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {

	public static String integerToRomanNumeral(int input) {
		if (input < 1 || input > 3999)
			return "Invalid Roman Number Value";
		String s = "";
		while (input >= 1000) {
			s += "M";
			input -= 1000;
		}
		while (input >= 900) {
			s += "CM";
			input -= 900;
		}
		while (input >= 500) {
			s += "D";
			input -= 500;
		}
		while (input >= 400) {
			s += "CD";
			input -= 400;
		}
		while (input >= 100) {
			s += "C";
			input -= 100;
		}
		while (input >= 90) {
			s += "XC";
			input -= 90;
		}
		while (input >= 50) {
			s += "L";
			input -= 50;
		}
		while (input >= 40) {
			s += "XL";
			input -= 40;
		}
		while (input >= 10) {
			s += "X";
			input -= 10;
		}
		while (input >= 9) {
			s += "IX";
			input -= 9;
		}
		while (input >= 5) {
			s += "V";
			input -= 5;
		}
		while (input >= 4) {
			s += "IV";
			input -= 4;
		}
		while (input >= 1) {
			s += "I";
			input -= 1;
		}
		return s;
	}

	public static String[] numberFormat(int[] costs) {
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		format.setParseIntegerOnly(true);

		String xp = format.format(costs[0]);
		xp = xp.replaceAll("\\.00", "");
		xp = xp.replaceAll("\\$", "");
		String money = format.format(costs[1]);
		money = money.replaceAll("\\$", "");
		money = money.replaceAll("\\.00", "");

		return new String[] { xp, money };
	}

	public static String format(int number) {
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		format.setParseIntegerOnly(true);

		String formatted = format.format(number);
		formatted = formatted.replaceAll("\\.00", "");
		formatted = formatted.replaceAll("\\$", "");

		return formatted;
	}
}