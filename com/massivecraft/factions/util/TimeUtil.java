package com.massivecraft.factions.util;

import java.util.concurrent.*;

public class TimeUtil {

	public static String formatTime(final long timePeriod) {
		String output = "";
		final long days = TimeUnit.MILLISECONDS.toDays(timePeriod);
		long millis = timePeriod - TimeUnit.DAYS.toMillis(days);
		final long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		if (days > 1L) {
			output = output + days + " days ";
		} else if (days == 1L) {
			output = output + days + " day ";
		}
		if (hours > 1L) {
			output = output + hours + " hours ";
		} else if (hours == 1L) {
			output = output + hours + " hour ";
		}
		if (minutes > 1L) {
			output = output + minutes + " minutes ";
		} else if (minutes == 1L) {
			output = output + minutes + " minute ";
		}
		if (seconds > 1L) {
			output = output + seconds + " seconds ";
		} else if (seconds == 1L) {
			output = output + seconds + " second ";
		}
		if (output.isEmpty()) {
			return "just now";
		}
		return output;
	}

	public static String formatTime(final int seconds) {
		final int days = seconds / 86400;
		final int hours = seconds % 86400 / 3600;
		final int minutes = seconds % 86400 % 3600 / 60;
		final StringBuilder sb = new StringBuilder();
		if (days != 0) {
			if (days > 1) {
				sb.append(days + " days ");
			} else if (days == 1) {
				sb.append("1 day ");
			}
		}
		if (hours != 0) {
			if (hours > 1) {
				sb.append(hours + " hours ");
			} else if (hours == 1) {
				sb.append("1 hour ");
			}
		}
		if (minutes != 0) {
			if (minutes > 1) {
				sb.append(minutes + " minutes ");
			} else if (minutes == 1) {
				sb.append("1 minute ");
			}
		}
		if (sb.toString().isEmpty()) {
			return "just now";
		}
		return sb.toString();
	}
}
