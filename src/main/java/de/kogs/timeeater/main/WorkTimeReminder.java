/**
 *
 */
package de.kogs.timeeater.main;

import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.tray.TimeEaterTray;
import de.kogs.timeeater.util.Utils;

import java.awt.TrayIcon.MessageType;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class WorkTimeReminder {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, (r) -> {
		Thread t = new Thread(r);
		t.setName("Work Reminder Thread");
		t.setDaemon(true);
		return t;
	});
	private TimeEaterTray tray;
	
	private long lastShow = 0;
	
	public WorkTimeReminder (TimeUnit intervalCheckUnit, int intervalCheckValue, TimeEaterTray tray) {
		this.tray = tray;
		scheduler.scheduleAtFixedRate(this::check, 0, intervalCheckValue, intervalCheckUnit);
	}
	
	private void check() {
		JobProvider provider = JobManager.getProvider();
		
		if (provider != null) {
			long timeForToday = TimeUnit.HOURS.toMillis(8) - provider.getTimeForDay(new Date());
			
			long timeForWeek = TimeUnit.HOURS.toMillis(40) - getTimeForWeek(provider);
			
			long todayHoursLeft = TimeUnit.MILLISECONDS.toHours(timeForToday);
			tray.updateTimeLeft(buildMsg(timeForToday, timeForWeek));
			
			if (todayHoursLeft < 4) {
				if (timeForToday <= 0 || System.currentTimeMillis() - lastShow > TimeUnit.MINUTES.toMillis(30)) {
					showTrayInfo(timeForToday, timeForWeek);
					lastShow = System.currentTimeMillis();
				}
			}
		}
	}
	
	private void showTrayInfo(long timeForToday, long timeForWeek) {
		tray.displayMessage("Timeeater Reminder", buildMsg(timeForToday, timeForWeek), MessageType.INFO);
	}
	
	private String buildMsg(long timeForToday, long timeForWeek) {
		boolean todayNegativ = false;
		if (timeForToday < 0) {
			todayNegativ = true;
			timeForToday *= -1;
		}
		boolean weekyNegativ = false;
		if (timeForWeek < 0) {
			weekyNegativ = true;
			timeForWeek *= -1;
		}
		
		
		long leaveTime = System.currentTimeMillis() +timeForToday;
		
		
		String msg = "Today left: " + (todayNegativ ? "-" : "") + Utils.millisToString(timeForToday);
		msg += "\nGo home at: " + Utils.timeToString(leaveTime);
		msg += "\nWeek Left: " + (weekyNegativ ? "-" : "") + Utils.millisToString(timeForWeek);
		return msg;
	}

	
	private long getTimeForWeek(JobProvider provider) {
		long time = 0;
		
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(new Date());
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Date monday = c.getTime();
		time += provider.getTimeForDay(monday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		Date tuesday = c.getTime();
		time += provider.getTimeForDay(tuesday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		Date wednesday = c.getTime();
		time += provider.getTimeForDay(wednesday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		Date thursday = c.getTime();
		time += provider.getTimeForDay(thursday);
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		Date friday = c.getTime();
		time += provider.getTimeForDay(friday);
		
		return time;
		
	}
		
}
