/**
 *
 */
package de.kogs.timeeater.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 */
public abstract class JobProvider {
	
	private static JobProvider provider;
	
	public static void initProvider(JobProvider provider) {
		if (JobProvider.provider == null) {
			JobProvider.provider = provider;
		}
	}
	
	public static JobProvider getProvider() {
		return provider;
	}
	
	private List<ManagerListener> listeners = new ArrayList<ManagerListener>();
	
	protected void activeJobEvent() {
		for (ManagerListener listener : listeners) {
			listener.activeJobChanged(getActiveJob());
		}
	}
	
	public void addListener(ManagerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ManagerListener listener) {
		listeners.remove(listener);
	}
	
	public abstract JobVo getActiveJob();
	public abstract void startWorkOnJob(String jobName);

	
	public abstract void stopWork();
	
	public abstract Collection<JobVo> getKownJobs();
	

	public abstract Collection<JobVo> getJobsForDay(Date day);
	
	public abstract long getTimeForDay(Date day);
	
	public abstract List<JobVo> getJobsForRange(Date start, Date end);
	
	public abstract JobVo getJob(String name);
	
	public abstract void removeJob(JobVo j);
	
	public abstract void save();
	
	public abstract void backup();
	
}
