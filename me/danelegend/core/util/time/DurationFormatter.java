package me.danelegend.core.util.time;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;

public class DurationFormatter {

	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
	private static final long HOUR = TimeUnit.HOURS.toMillis(1L);

	public static String getRemaining(long millis, boolean milliseconds) {
		return getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
		if (milliseconds && duration < MINUTE) {
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()
					.format(duration * 0.001) + 's';
		} else {
			return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
		}
	}

	public static String graceRemaining(long millis) {
		boolean trail = true;

		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;

		if (days > 0)
			return days + "d " + hours % 24 + "h " + minutes % 60 + "m " + seconds % 60 + "s";
		if (hours > 0)
			return hours % 24 + "h " + minutes % 60 + "m " + seconds % 60 + "s";
		if (minutes > 0)
			return minutes % 60 + "m " + seconds % 60 + "s";
		return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()
				.format(millis * 0.001) + 's';
	}
}