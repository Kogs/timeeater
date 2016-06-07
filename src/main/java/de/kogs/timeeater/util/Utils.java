package de.kogs.timeeater.util;

import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class Utils {

	public static String millisToString(long millis) {
		return String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));
	}

	public static String timeToString(long millis) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis(millis);

		return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE));
	}
	
	public static String millisToHours(long millis) {
		if (millis == 0) {
			return "";
		}
		double seconds = millis / 1000;
		double hours = seconds / 3600;
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(hours);
	}

	public static boolean isSameDay(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
						.get(Calendar.DAY_OF_YEAR);
	}

	public final static StringConverter<Number> longToMillisConverter = new StringConverter<Number>() {

		@Override
		public String toString(Number millis) {
			return millisToString(millis.longValue());
		}

		@Override
		public Number fromString(String string) {
			// Not needed so far
			return null;
		}
	};

	public static boolean isInRange(Date d1, Date startDate, Date endDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(startDate);
		cal3.setTime(endDate);

//		return d1.after(startDate) && d1.before(endDate);
		
		return ((cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR) && cal1
				.get(Calendar.YEAR) <= cal3.get(Calendar.YEAR)) && (cal1
				.get(Calendar.DAY_OF_YEAR) >= cal2.get(Calendar.DAY_OF_YEAR) && cal1
				.get(Calendar.DAY_OF_YEAR) <= cal3.get(Calendar.DAY_OF_YEAR)));
	}
}
