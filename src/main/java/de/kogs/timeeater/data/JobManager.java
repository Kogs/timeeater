/**
 *
 */
package de.kogs.timeeater.data;

import de.kogs.timeeater.controller.DialogController;
import de.kogs.timeeater.data.hooks.HookManager;
import de.kogs.timeeater.data.hooks.ScheduledJob;
import javafx.application.Platform;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	public static HookManager hookInstance() {
		return HookManager.instance(instance);
	}
	
	private List<ManagerListener> listeners = new ArrayList<ManagerListener>();

	private Job activeJob;

	private ScheduledExecutorService scheduledJobs = Executors.newScheduledThreadPool(1, (r) -> {
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.setName("ScheduledJobs Check Thread");
		return t;
	});
	private Job jobActiveBeforScheduled;
	
	private Map<String, Job> kownJobs = new HashMap<>();

	/**
	 * 
	 */
	public JobManager() {
		load();
		scheduledJobs.schedule(() -> {
			checkScheduledJobs();
		} , 1, TimeUnit.MINUTES);
	}


	public Job getActiveJob() {
		return activeJob;
	}

	public void startWorkOnJob(String jobName) {
		if (kownJobs.containsKey(jobName)) {
			startWorkOnJob(kownJobs.get(jobName));
		} else {
			startWorkOnJob(createJob(jobName));
		}
	}
	
	private Job createJob(String name) {
		Job newJob = new Job();
		newJob.setName(name);
		kownJobs.put(name, newJob);
		hookInstance().applyDefaults(newJob);
		return newJob;
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
			save();
		}
	}

	private void checkScheduledJobs() {
		HookManager hookManager = hookInstance();
		System.out.println("Check Scheduled Jobs");
		List<ScheduledJob> scheduledHooks = hookManager.getHooks(ScheduledJob.class);
		for (ScheduledJob scheduledJob : scheduledHooks) {
			if (scheduledJob.action()) {
				jobActiveBeforScheduled = activeJob;
				// Todo ask
				stopWork();
				startWorkOnJob(scheduledJob.getJob());
			} else {
				if (scheduledJob.getJob().equals(activeJob)) {
					stopWork();
					if (jobActiveBeforScheduled != null) {
						startWorkOnJob(jobActiveBeforScheduled);
					}
				}
			}
			
		}
	}
	
	public Collection<Job> getKownJobs() {
		return kownJobs.values();
	}

	public Job getJob(String name) {
		if (kownJobs.containsKey(name)) {
			return kownJobs.get(name);
		}
		return null;
	}
	
	private void activeJobEvent() {
		for (ManagerListener listener : listeners) {
			listener.activeJobChanged(activeJob);
		}
	}

	public void addListener(ManagerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ManagerListener listener) {
		listeners.remove(listener);
	}
	
	public void save() {
		try (XMLEncoder e = new XMLEncoder(new BufferedOutputStream(
				new FileOutputStream(getSaveFile())))) {

			List<Job> jobs = new ArrayList<>(getKownJobs());

			e.writeObject(jobs);

			Platform.runLater(()->new DialogController("Speichern", "Daten wurden gespeichert"));
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		hookInstance().save();

	}

	public void load() {
		
		try (XMLDecoder d = new XMLDecoder(new BufferedInputStream(
				new FileInputStream(getSaveFile())))) {

			List<Job> jobs = (List<Job>) d.readObject();
			kownJobs = jobs.stream().collect(
					Collectors.toMap(Job::getName, Function.identity()));

			HookManager hookManager = hookInstance();
			for (Job job : jobs) {
				hookManager.applyDefaults(job);
			}
			hookInstance().save();
			
		} catch (Exception e) {
			Platform.runLater(() -> new DialogController("Fehler", "Daten konnten nicht geladen werden"));
			e.printStackTrace();
		}
		
	}

	private File getSaveFile() {
		File folder = new File(System.getProperty("user.dir") + "\\conf\\");
		folder.mkdirs();
		File file = new File(folder, "save.xml");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File: " + file);
		return file;
	}

	public void removeJob(Job j) {
		kownJobs.remove(j.getName());
		save();

	}

}
