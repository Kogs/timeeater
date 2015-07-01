package de.kogs.timeeater.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javafx.util.StringConverter;

public class Utils {
	
	public static String millisToString(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	
	
	public static String timeToString(long millis){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis(millis);

		return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE));
	}

	public static boolean isSameDay(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}
	
	public final static StringConverter<Number> longToMillisConverter = new StringConverter<Number>() {

		@Override
		public String toString(Number millis) {
			return millisToString(millis.longValue());
		}

		@Override
		public Number fromString(String string) {
			//Not needed so far
			return null;
		}
	}; 
	
	
	

}
