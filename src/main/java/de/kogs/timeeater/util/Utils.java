package de.kogs.timeeater.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javafx.util.StringConverter;

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
	
	
	public static boolean isSameDay(Date d1, Date d2){
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(d1);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		Date d1Day = c.getTime();
		
		c.setTime(d2);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MILLISECOND, 0);
		return d1Day.equals(c.getTime());
	}

}
