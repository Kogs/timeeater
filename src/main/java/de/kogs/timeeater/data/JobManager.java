/**
 *
 */
package de.kogs.timeeater.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class JobManager {

	private static JobManager instance;

	public static JobManager instance() {
		if (instance == null) {
			instance = new JobManager();
		}
		return instance;
	}

	private List<ManagerListener> listeners = new ArrayList<ManagerListener>();

	private Job activeJob;

	private Map<String, Job> kownJobs = new HashMap<>();

	/**
	 * 
	 */
	public JobManager() {

	}

	public Job getActiveJob() {
		return activeJob;
	}

	public void startWorkOnJob(String jobName) {
		if (kownJobs.containsKey(jobName)) {
			startWorkOnJob(kownJobs.get(jobName));
		} else {
			Job newJob = new Job();
			newJob.setName(jobName);
			kownJobs.put(jobName, newJob);
			startWorkOnJob(newJob);
		}
	}

	private void startWorkOnJob(Job job) {
		if (activeJob == null || activeJob.getActiveWork() == null) {
			activeJob = job;
			LoggedWork work = new LoggedWork();
			work.setLogDate(new Date());
			work.setLogStart(System.currentTimeMillis());
			activeJob.setActiveWork(work);
			activeJob.getWorks().add(work);
			activeJobEvent();
		}
	}

	public void stopWork() {
		if (activeJob != null && activeJob.getActiveWork() != null) {
			LoggedWork activeWork = activeJob.getActiveWork();
			activeWork.setLogEnd(System.currentTimeMillis());
			System.out.println("Worked on Job: " + activeJob.getName() + " "
					+ (activeWork.getLogEnd() - activeWork.getLogStart()));
			activeJob.setActiveWork(null);
			activeJob = null;
			activeJobEvent();
		}
	}

	public Collection<Job> getKownJobs() {
		return kownJobs.values();
	}

	
	
	private void activeJobEvent(){
		for(ManagerListener listener : listeners){
			listener.activeJobChanged(activeJob);
		}
	}
	public void addListener(ManagerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ManagerListener listener) {
		listeners.remove(listener);
	}

}
